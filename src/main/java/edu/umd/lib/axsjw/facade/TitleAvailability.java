package edu.umd.lib.axsjw.facade;

import static edu.umd.lib.axsjw.facade.utils.FacadeUtils.padSysNum;
import static edu.umd.lib.axsjw.facade.utils.FacadeUtils.parseDueDate;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.client.WebTarget;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.umd.lib.axsjw.jaxb.CircStatus;
import edu.umd.lib.axsjw.jaxb.ReadItem;
import edu.umd.lib.axsjw.net.JaxbAlephOp;
import edu.umd.lib.axsjw.net.impl.JaxbAlephOpImpl;

public class TitleAvailability {
  final Logger logger = LoggerFactory.getLogger(TitleAvailability.class);

  private JAXBContext ctx;

  public TitleAvailability() throws JAXBException {
    this.ctx = JAXBContext.newInstance("edu.umd.lib.axsjw.jaxb");
  }

  /**
   * Returns a TitleAvailabilityResult containing information about the
   * availability of a title with the given barcodes.
   *
   * @param webTarget
   *          the WebTarget describing the network connection to the Aleph
   *          X-Server
   * @param library
   *          the ADM library to search for the title
   * @param subLibrary
   *          the subLibrary location where items must be available. Should not
   *          be null.
   * @param collection
   *          the collection where items must be available. Should not be null.
   * @param barcodes
   *          an array of barcodes to search
   *
   * @return a TitleAvailabilityResult containing information about the
   *         availability of a title with the given barcodes.
   */
  public TitleAvailabilityResult getAvailability(WebTarget webTarget, String library, String subLibrary,
      String collection, String[] barcodes) {
    TitleAvailabilityResult currentResult = new TitleAvailabilityResult();

    for (String barcode : barcodes) {
      ReadItem readItem = getReadItem(webTarget, library, barcode);

      if (readItem == null) {
        continue;
      }

      if (isValidLocation(readItem, subLibrary, collection)) {
        Map<String, String> z30Map = readItem.getZ30Map();
        String sysNum = padSysNum(z30Map.get("z30-doc-number"));

        CircStatus circStatus = getCircStatus(webTarget, library, sysNum);

        if (circStatus == null) {
          continue;
        }
        currentResult = updateTitleAvailabilityResult(circStatus, barcode, currentResult);
      } else {
        logger.debug("Skipping read-item for barcode: {}. Wrong location: subLibrary: {}, collection: {}", barcode,
            subLibrary, collection);
      }
    }
    return currentResult;
  }

  /**
   * Returns true if the "z30-sub-library" and "z30-collection" entries in the
   * given ReadItem match the given subLibary and collection parameters, false
   * otherwise.
   *
   * @param readItem
   *          the ReadItem to validate. Should not be null, and should contain a
   *          non-null z30Map.
   * @param subLibrary
   *          the expected subLibary. Should not be null.
   * @param collection
   *          the expected collection. Should not be null.
   * @return true if the "z30-sub-library" and "z30-collection" entries in the
   *         given ReadItem match the given subLibary and collection parameters,
   *         false otherwise.
   */
  protected boolean isValidLocation(ReadItem readItem, String subLibrary, String collection) {
    if ((readItem == null) || readItem.isError()) {
      return false;
    }
    Map<String, String> z30Map = readItem.getZ30Map();
    String z30SubLibrary = z30Map.get("z30-sub-library");
    String z30Collection = z30Map.get("z30-collection");
    return subLibrary.equals(z30SubLibrary) && collection.equals(z30Collection);
  }

  /**
   * Return a TitleAvailibilityResult update from the given parameters.
   *
   * @param circStatus
   *          the CircStatus to use in updating the result. Should not be null.
   * @param barcode
   *          the bar code indicating the item being checked for availability.
   *          Should not be null.
   * @param currentResult
   *          the current TitleAvailablityResult. Should not be null.
   * @return a TitleAvailibilityResult update from the given parameters.
   */
  protected TitleAvailabilityResult updateTitleAvailabilityResult(
      CircStatus circStatus, String barcode,
      TitleAvailabilityResult currentResult) {
    if (circStatus.isError()) {
      return currentResult;
    }

    int availableCount = currentResult.getNumAvailable();

    Map<String, String>[] itemDataMaps = circStatus.getItemDataMaps();
    for (Map<String, String> itemData : itemDataMaps) {
      String itemBarCode = itemData.get("barcode");
      if (barcode.equals(itemBarCode)) {
        String dueDate = itemData.get("due-date");
        if ("On Shelf".equals(dueDate)) {
          availableCount++;
          currentResult = new TitleAvailabilityResult(availableCount++, null, null);
          continue;
        } else if (availableCount == 0) {
          String dueHour = itemData.get("due-hour");
          Date date = parseDueDate(dueDate, dueHour);
          if (date != null) {
            String currentDueDate = currentResult.getDueDate();
            String currentDueHour = currentResult.getDueHour();
            Date currentDate = parseDueDate(currentDueDate, currentDueHour);
            if ((currentDate == null) || (date.before(currentDate))) {
              currentResult = new TitleAvailabilityResult(availableCount, dueDate, dueHour);
            } else {
              continue;
            }
          }
        }
      }
    }
    return currentResult;
  }

  /**
   * Retrieves a ReadItem object from the Aleph X-Server using the given
   * parameters.
   *
   * @param webTarget
   *          the WebTarget describing the network connection to the Aleph
   *          X-Server
   * @param library
   *          the ADM library of the item requested to be retrieved
   * @param barcode
   *          a unique identifier of a single item within the ADM library.
   * @return a ReadItem object from the Aleph X-Server using the given
   *         parameters, or null if an error occurs.
   */
  protected ReadItem getReadItem(WebTarget webTarget, String library, String barcode) {
    try {
      logger.debug("Calling read-item for library: {}, barcode: {}", library, barcode);
      Map<String, String> queryParams = new HashMap<>();
      queryParams.put("op", "read-item");
      queryParams.put("library", library);
      queryParams.put("item_barcode", barcode);

      Unmarshaller unmarshaller = ctx.createUnmarshaller();
      JaxbAlephOp<ReadItem> readItemOp = new JaxbAlephOpImpl<>();
      ReadItem readItem = readItemOp.request(webTarget, queryParams, unmarshaller);
      if (readItem.isError()) {
        logger.error("Error retrieving read-item, library='{}', barcode='{}', error={}", library, barcode,
            readItem.getError());
        return null;
      }
      return readItem;
    } catch (JAXBException jaxbe) {
      logger.error("JAXBError: Calling read-item, library='" + library + "', barcode='" + barcode + "'", jaxbe);
    }
    return null;
  }

  /**
   * Retrieves a CircStatus object from the Aleph X-Server using the given
   * parameters.
   *
   * @param webTarget
   *          the WebTarget describing the network connection to the Aleph
   *          X-Server
   * @param library
   *          the ADM library of the item requested to be retrieved
   * @param sysNum
   *          document for which the user would like to retrieve circulation
   *          information.
   * @return a CircStatus object from the Aleph X-Server using the given
   *         parameters, or null if an error occurs.
   */
  protected CircStatus getCircStatus(WebTarget webTarget, String library, String sysNum) {
    try {
      logger.debug("Calling circ-status for library: {}, sysNum: {}", library, sysNum);
      Map<String, String> queryParams = new HashMap<>();
      queryParams.put("op", "circ-status");
      queryParams.put("library", library);
      queryParams.put("sys_no", sysNum);

      Unmarshaller unmarshaller = ctx.createUnmarshaller();
      JaxbAlephOp<CircStatus> circStatusOp = new JaxbAlephOpImpl<>();
      CircStatus circStatus = circStatusOp.request(webTarget, queryParams, unmarshaller);
      if (circStatus.isError()) {
        logger.error("Error retrieving circ-status, library='{}', sysNum='{}', error={}", library, sysNum,
            circStatus.getError());
        return null;
      }
      return circStatus;

    } catch (JAXBException jaxbe) {
      logger.error("JAXBError: Calling circ-status, library='" + library + "', sysNum='" + sysNum + "'", jaxbe);
    }
    return null;
  }
}
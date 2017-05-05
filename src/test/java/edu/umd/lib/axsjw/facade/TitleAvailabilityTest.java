package edu.umd.lib.axsjw.facade;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import java.io.File;
import java.net.URL;
import java.util.Map;

import javax.ws.rs.client.WebTarget;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.umd.lib.axsjw.jaxb.CircStatus;
import edu.umd.lib.axsjw.jaxb.ReadItem;

public class TitleAvailabilityTest {
  private Unmarshaller unmarshaller;

  @Before
  public void setUp() throws Exception {
    // Create context
    JAXBContext ctx = JAXBContext.newInstance("edu.umd.lib.axsjw.jaxb");

    // Create unmarshaller
    unmarshaller = ctx.createUnmarshaller();
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void testGetAvailabilityValidParams() throws Exception {
    String barcode = "32055660182979";

    ReadItem validReadItem = getReadItemFromTestFile("test-data/read-item-valid.xml");
    CircStatus validCircStatus = getCircStatusFromTestFile("test-data/circ-status-valid-single-item.xml");

    TitleAvailability titleAvailability = mockTitleAvailability(validReadItem, validCircStatus);
    TitleAvailabilityResult result = titleAvailability.getAvailability(null, "mai50", "mai01", "TU-TU", "JVFIC",
        new String[] { barcode });
    assertEquals(1, result.getNumAvailable());

  }

  @Test
  public void testGetAvailabilityValidParamsDueDate() throws Exception {
    String barcode = "32055660182979";

    ReadItem validReadItem = getReadItemFromTestFile("test-data/read-item-valid.xml");
    CircStatus validCircStatus = getCircStatusFromTestFile("test-data/circ-status-valid-single-item.xml");

    // Modify validCircStatus to have a due date
    validCircStatus.getItemDataMaps()[0].put("due-date", "April-25-2017");
    validCircStatus.getItemDataMaps()[0].put("due-hour", "06:53 PM");

    TitleAvailability titleAvailability = mockTitleAvailability(validReadItem, validCircStatus);
    TitleAvailabilityResult result = titleAvailability.getAvailability(null, "mai50", "mai01", "TU-TU", "JVFIC",
        new String[] { barcode });
    assertEquals(0, result.getNumAvailable());
    assertEquals("April-25-2017", result.getDueDate());
    assertEquals("06:53 PM", result.getDueHour());
  }

  @Test
  public void testGetAvailabilityBadReadItem() throws Exception {
    String barcode = "32055660182979";

    ReadItem badReadItem = getReadItemFromTestFile("test-data/read-item-error-item-not-found-barcode.xml");
    CircStatus validCircStatus = getCircStatusFromTestFile("test-data/circ-status-valid-single-item.xml");

    TitleAvailability titleAvailability = mockTitleAvailability(badReadItem, validCircStatus);
    TitleAvailabilityResult result = titleAvailability.getAvailability(null, "mai50", "mai01", "TU-TU", "JVFIC",
        new String[] { barcode });
    assertEquals(0, result.getNumAvailable());
    assertNull(result.getDueDate());
    assertNull(result.getDueHour());
  }

  @Test
  public void testGetAvailabilityBadCircStatus() throws Exception {
    String barcode = "32055660182979";

    ReadItem badReadItem = getReadItemFromTestFile("test-data/read-item-valid.xml");
    CircStatus validCircStatus = getCircStatusFromTestFile("test-data/circ-status-error-item-not-found.xml");

    TitleAvailability titleAvailability = mockTitleAvailability(badReadItem, validCircStatus);
    TitleAvailabilityResult result = titleAvailability.getAvailability(null, "mai50", "mai01", "TU-TU", "JVFIC",
        new String[] { barcode });
    assertEquals(0, result.getNumAvailable());
    assertNull(result.getDueDate());
    assertNull(result.getDueHour());
  }

  @Test
  public void testIsValidLocation() throws Exception {
    TitleAvailability titleAvailability = new TitleAvailability();

    ReadItem readItem = getReadItemFromTestFile("test-data/read-item-valid.xml");
    assertTrue(titleAvailability.isValidLocation(readItem, "TU-TU", "JVFIC"));
    assertFalse(titleAvailability.isValidLocation(readItem, "TU-TU", "REF"));
    assertFalse(titleAvailability.isValidLocation(readItem, "CPMCK", "JVFIC"));
    assertFalse(titleAvailability.isValidLocation(readItem, "CPMCK", "REF"));

    assertFalse(titleAvailability.isValidLocation(null, "CPMCK", "REF"));

    ReadItem errorReadItem = getReadItemFromTestFile("test-data/read-item-error-item-not-found-barcode.xml");
    assertFalse(titleAvailability.isValidLocation(errorReadItem, "CPMCK", "REF"));
  }

  @Test
  public void testUpdateTitleAvailabilityResult() throws Exception {
    TitleAvailability titleAvailability = new TitleAvailability();
    TitleAvailabilityResult currentResult = new TitleAvailabilityResult();

    CircStatus circStatus = getCircStatusFromTestFile("test-data/circ-status-valid-single-item.xml");
    String barcode = "32055660182979";
    TitleAvailabilityResult result = titleAvailability.updateTitleAvailabilityResult(circStatus, barcode,
        currentResult);
    assertEquals(1, result.getNumAvailable());
  }

  @Test
  public void testUpdateTitleAvailabilityResultDueDate() throws Exception {
    TitleAvailability titleAvailability = new TitleAvailability();
    TitleAvailabilityResult currentResult = new TitleAvailabilityResult();

    String barcode = "32055660182979";

    CircStatus circStatus = getCircStatusFromTestFile("test-data/circ-status-valid-single-item.xml");
    // Modify circStatus item so due date is April 25, 2017,6:53PM
    Map<String, String> itemMap = circStatus.getItemDataMaps()[0];
    itemMap.put("due-date", "April-25-2017");
    itemMap.put("due-hour", "06:53 PM");

    TitleAvailabilityResult result = titleAvailability.updateTitleAvailabilityResult(circStatus, barcode,
        currentResult);
    assertEquals(0, result.getNumAvailable());
    assertEquals("April-25-2017", result.getDueDate());
    assertEquals("06:53 PM", result.getDueHour());
  }

  @Test
  public void testUpdateTitleAvailabilityResultMultipleDueDates() throws Exception {
    TitleAvailability titleAvailability = new TitleAvailability();
    TitleAvailabilityResult currentResult = new TitleAvailabilityResult();

    String barcode = "32055660182979";

    CircStatus circStatus = getCircStatusFromTestFile("test-data/circ-status-valid-multiple-items.xml");
    // Modify circStatus items with different due dates
    Map<String, String> itemMap0 = circStatus.getItemDataMaps()[0];
    itemMap0.put("due-date", "April-25-2017");
    itemMap0.put("due-hour", "06:53 PM");
    itemMap0.put("barcode", barcode);

    Map<String, String> itemMap1 = circStatus.getItemDataMaps()[1];
    itemMap1.put("due-date", "January-4-2017");
    itemMap1.put("due-hour", "12:22 PM");
    itemMap1.put("barcode", barcode);

    TitleAvailabilityResult result = titleAvailability.updateTitleAvailabilityResult(circStatus, barcode,
        currentResult);
    assertEquals(0, result.getNumAvailable());
    assertEquals("January-4-2017", result.getDueDate());
    assertEquals("12:22 PM", result.getDueHour());
  }

  @Test
  public void testUpdateTitleAvailabilityResultMultipleMixedAvailableAndDueDate() throws Exception {
    TitleAvailability titleAvailability = new TitleAvailability();
    TitleAvailabilityResult currentResult = new TitleAvailabilityResult();

    String barcode = "32055660182979";

    CircStatus circStatus = getCircStatusFromTestFile("test-data/circ-status-valid-multiple-items.xml");
    // Modify circStatus items with different due dates
    Map<String, String> itemMap0 = circStatus.getItemDataMaps()[0];
    itemMap0.put("due-date", "April-25-2017");
    itemMap0.put("due-hour", "06:53 PM");
    itemMap0.put("barcode", barcode);

    Map<String, String> itemMap1 = circStatus.getItemDataMaps()[1];
    itemMap1.put("due-date", "On Shelf");
    itemMap1.put("barcode", barcode);

    TitleAvailabilityResult result = titleAvailability.updateTitleAvailabilityResult(circStatus, barcode,
        currentResult);
    assertEquals(1, result.getNumAvailable());
    assertEquals(null, result.getDueDate());
    assertEquals(null, result.getDueHour());
  }

  // Utility methods
  protected TitleAvailability mockTitleAvailability(ReadItem readItem, CircStatus circStatus) throws Exception {
    // Mock TitleAvailability methods except for getAvailability
    TitleAvailability titleAvailability = spy(TitleAvailability.class);
    doReturn(readItem).when(titleAvailability).getReadItem(isNull(WebTarget.class), anyString(), anyString());
    doReturn(circStatus).when(titleAvailability).getCircStatus(isNull(WebTarget.class), anyString(), anyString());
    return titleAvailability;
  }

  protected CircStatus getCircStatusFromTestFile(String filePath) throws Exception {
    URL circStatusTestFileUrl = ClassLoader
        .getSystemResource(filePath);
    File circStatusTestFile = new File(circStatusTestFileUrl.toURI());
    return (CircStatus) unmarshaller.unmarshal(circStatusTestFile);
  }

  protected ReadItem getReadItemFromTestFile(String filePath) throws Exception {
    URL readItemTestFileUrl = ClassLoader
        .getSystemResource(filePath);
    File readItemTestFile = new File(readItemTestFileUrl.toURI());
    return (ReadItem) unmarshaller.unmarshal(readItemTestFile);
  }
}
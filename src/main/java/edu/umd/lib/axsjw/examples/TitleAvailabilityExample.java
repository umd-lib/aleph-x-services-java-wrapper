package edu.umd.lib.axsjw.examples;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.xml.bind.JAXBException;

import org.glassfish.jersey.client.ClientConfig;

import edu.umd.lib.axsjw.facade.TitleAvailability;
import edu.umd.lib.axsjw.facade.TitleAvailabilityResult;

/**
 * Example code demonstrating the TitleAvailability class.
 * <p>
 * Note: Please change the SERVER_URL, LIBRARY, and BARCODES to appropriate
 * values for your Aleph installation before running this example.
 */
public class TitleAvailabilityExample {
  public static final void main(String[] args) {
    // The URL to the Aleph X-Server
    String SERVER_URL = "http://aleph.lib.example.edu/X";

    // The ADM library of the item requested to be retrieved
    String ADM_LIBRARY = "mai50";

    // The BIB library of the item requested to be retrieved
    String BIB_LIBRARY = "mai01";

    // The sublibrary the item must belong to
    String SUB_LIBRARY = "CPMCK";

    // The collection the item must belong to
    String COLLECTION = "RES";

    // The document for which the user would like to retrieve circulation
    // information.
    String[] BARCODES = new String[] { "12345678901234" };

    Client client = ClientBuilder.newClient(new ClientConfig());
    WebTarget webTarget = client.target(SERVER_URL);

    TitleAvailability titleAvailability = null;
    try {
      titleAvailability = new TitleAvailability();
    } catch (JAXBException jaxbe) {
      System.out.println("An error occurred:" + jaxbe);
      return;
    }

    TitleAvailabilityResult result = titleAvailability.getAvailability(
        webTarget, ADM_LIBRARY, BIB_LIBRARY, SUB_LIBRARY, COLLECTION, BARCODES);

    if (result.getNumAvailable() > 0) {
      System.out.println(result.getNumAvailable() + " items available.");
    } else if ((result.getDueDate() != null) && (result.getDueHour() != null)) {
      System.out.println("All copies checked out. Next available at " +
          result.getDueDate() + " " + result.getDueHour());
    } else {
      System.out.println("Could not determine item availability");
    }
  }
}

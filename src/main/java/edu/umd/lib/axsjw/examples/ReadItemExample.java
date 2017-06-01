package edu.umd.lib.axsjw.examples;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.glassfish.jersey.client.ClientConfig;

import edu.umd.lib.axsjw.jaxb.ReadItem;
import edu.umd.lib.axsjw.net.JaxbAlephOp;
import edu.umd.lib.axsjw.net.impl.JaxbAlephOpImpl;

/**
 * Example code demonstrating calling the "read-item" operation on an Aleph
 * X-server.
 * <p>
 * Note: Please change the SERVER_URL, ADM_LIBRARY, and ITEM_BARCODE to
 * appropriate values for your Aleph installation before running this example.
 */
public class ReadItemExample {
  public static final void main(String[] args) {
    // The URL to the Aleph X-Server
    String SERVER_URL = "http://aleph.lib.example.edu/X";

    // The ADM library of the item requested to be retrieved
    String ADM_LIBRARY = "mai50";

    // A unique identifier of a single item within the ADM library.
    String ITEM_BARCODE = "12345678901234";

    Client client = ClientBuilder.newClient(new ClientConfig());
    WebTarget webTarget = client.target(SERVER_URL);

    try {
      // Create context
      JAXBContext ctx = JAXBContext.newInstance("edu.umd.lib.axsjw.jaxb");

      // Create unmarshaller
      Unmarshaller unmarshaller = ctx.createUnmarshaller();

      // Populate query params for operation
      Map<String, String> params = new HashMap<>();
      params.put("op", "read-item");
      params.put("item_barcode", ITEM_BARCODE);
      params.put("library", ADM_LIBRARY);

      // Perform "read-item" operation
      JaxbAlephOp<ReadItem> opReadItem = new JaxbAlephOpImpl<>();
      ReadItem readItem = opReadItem.request(webTarget, params, unmarshaller);

      // Output result
      System.out.println("read-item");
      System.out.println("---------");
      System.out.println("Session id: " + readItem.getSessionId());
      if (!readItem.isError()) {
        System.out.println("z30:");
        Map<String, String> z30Map = readItem.getZ30Map();
        for (String key : z30Map.keySet()) {
          System.out.println("\t" + key + ": '" + z30Map.get(key) + "'");
        }
      } else {
        System.out.println("error: " + readItem.getError());
      }
    } catch (JAXBException e) {
      System.out.println(e);
    }
  }
}

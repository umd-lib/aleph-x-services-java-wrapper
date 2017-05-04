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

import edu.umd.lib.axsjw.jaxb.CircStatus;
import edu.umd.lib.axsjw.net.JaxbAlephOp;
import edu.umd.lib.axsjw.net.impl.JaxbAlephOpImpl;

/**
 * Example code demonstrating calling the "circ-status" operation on an Aleph
 * X-server.
 * <p>
 * Note: Please change the SERVER_URL, LIBRARY, and SYS_NO to appropriate values
 * for your Aleph installation before running this example.
 */
public class CircStatusExample {
  public static final void main(String[] args) {
    // The URL to the Aleph X-Server
    String SERVER_URL = "http://alephdev.lib.umd.edu/X";

    // The ADM library of the item requested to be retrieved
    String LIBRARY = "mai01";

    // The document for which the user would like to retrieve circulation
    // information.
    String SYS_NO = "004572025";

    Client client = ClientBuilder.newClient(new ClientConfig());
    WebTarget webTarget = client.target(SERVER_URL);

    try {
      // Create context
      JAXBContext ctx = JAXBContext.newInstance("edu.umd.lib.axsjw.jaxb");

      // Create unmarshaller
      Unmarshaller unmarshaller = ctx.createUnmarshaller();

      // Populate query params for operation
      Map<String, String> params = new HashMap<>();
      params.put("op", "circ-status");
      params.put("sys_no", SYS_NO);
      params.put("library", LIBRARY);

      // Perform "circ-status" operation
      JaxbAlephOp<CircStatus> opCircStatus = new JaxbAlephOpImpl<>();
      CircStatus circStatus = opCircStatus.request(webTarget, params, unmarshaller);

      // Output result
      System.out.println("circ-status");
      System.out.println("---------");
      System.out.println("Session id: " + circStatus.getSessionId());
      if (!circStatus.isError()) {
        for (Map<String, String> itemDataMap : circStatus.getItemDataMaps()) {
          System.out.println("itemDataMap:");
          for (String key : itemDataMap.keySet()) {
            System.out.println("\t" + key + ": '" + itemDataMap.get(key) + "'");
          }
        }
      } else {
        System.out.println("error: " + circStatus.getError());
      }
    } catch (JAXBException e) {
      System.out.println(e);
    }
  }
}

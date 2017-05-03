package edu.umd.lib.axsjw;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;

import org.glassfish.jersey.client.ClientConfig;

import edu.umd.lib.axsjw.jaxb.MapAdapter;
import edu.umd.lib.axsjw.jaxb.ReadItem;
import edu.umd.lib.axsjw.net.JaxbAlephOp;
import edu.umd.lib.axsjw.net.impl.JaxbAlephOpImpl;

public class App {
  public static final void main(String[] args) {
    Client client = ClientBuilder.newClient(new ClientConfig());

    WebTarget webTarget = client.target("http://alephdev.lib.umd.edu:80/X");

    try {
      // Create context
      JAXBContext ctx = JAXBContext.newInstance("edu.umd.lib.axsjw.jaxb");

      // Create unmarshaller
      Unmarshaller unmarshaller = ctx.createUnmarshaller();

      MapAdapter mapAdapter = new MapAdapter();
      unmarshaller.setAdapter(mapAdapter);

      Map<String, String> params = new HashMap<>();
      params.put("op", "read-item");
      params.put("item_barcode", "32055660182979");
      params.put("library", "mai50");

      JaxbAlephOp<ReadItem> opReadItem = new JaxbAlephOpImpl<>();
      ReadItem readItem = opReadItem.request(webTarget, params, unmarshaller);
      System.out.println("readItem = " + readItem + ", readItem.sessionId = " + readItem.getSessionId());
    } catch (JAXBException | ParserConfigurationException e) {
      System.out.println(e);
    }
  }
}

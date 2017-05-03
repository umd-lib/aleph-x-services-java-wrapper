package edu.umd.lib.axsjw.net;

import java.util.Map;

import javax.ws.rs.client.WebTarget;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

/**
 * Low-level interface for performing an Aleph X-Services operation using JAXB
 * to convert the XML response into a Java object.
 *
 * @param <T>
 *          the class of the object to return.
 */
public interface JaxbAlephOp<T> {
  /**
   * Queries the given WebTarget passing the queryParams Map as GET parameters,
   * passing the resulting XML response to the given JAXB Unmarshaller, and
   * returning the unmarshalled object.
   *
   * @param webTarget
   *          the WebTarget defining the HTTP connection to be made.
   * @param queryParams
   *          the GET query parameters to include in the request.
   * @param unmarshaller
   *          the JAXB unmarshaller to use in handling the XML response
   * @return the unmarshalled object from the XML response from the server.
   * @throws JAXBException
   *           if a JAXB exception occurs
   */
  public T request(WebTarget webTarget, Map<String, String> queryParams, Unmarshaller unmarshaller)
      throws JAXBException;
}
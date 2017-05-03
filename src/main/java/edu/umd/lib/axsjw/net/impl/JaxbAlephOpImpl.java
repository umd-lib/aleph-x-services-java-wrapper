package edu.umd.lib.axsjw.net.impl;

import java.io.StringReader;
import java.util.Map;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import edu.umd.lib.axsjw.net.JaxbAlephOp;

/**
 * Implementation of the JaxbAlpehOp interface
 */
public class JaxbAlephOpImpl<T> implements JaxbAlephOp<T> {
  protected String request(WebTarget webTarget, Map<String, String> queryParams) {
    for (String key : queryParams.keySet()) {
      webTarget = webTarget.queryParam(key, queryParams.get(key));
    }
    String xml = webTarget.request(MediaType.TEXT_XML).get(String.class);
    return xml;
  }

  @Override
  public T request(WebTarget webTarget, Map<String, String> queryParams, Unmarshaller unmarshaller)
      throws JAXBException {
    String xml = request(webTarget, queryParams);
    T entity = (T) unmarshaller.unmarshal(new StringReader(xml));
    return entity;
  }
}
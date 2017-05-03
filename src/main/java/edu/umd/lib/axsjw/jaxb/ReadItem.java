package edu.umd.lib.axsjw.jaxb;

import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * JAXB class for converting XML returned by the "read-item" operation into a
 * Java object.
 */
@XmlRootElement(name = "read-item")
@XmlAccessorType(XmlAccessType.FIELD)
public class ReadItem {
  @XmlElement(name = "session-id")
  private String sessionId;

  @XmlElement(name = "error")
  private String error;

  @XmlElement(name = "z30")
  @XmlJavaTypeAdapter(MapAdapter.class)
  private Map<String, String> z30Map;

  /**
   * @return the session id from the response.
   */
  public String getSessionId() {
    return sessionId;
  }

  /**
   * @return an error description provided by the Aleph X-Server.
   */
  public String getError() {
    return error;
  }

  /**
   * @return an array of Maps representing the "item-data" elements in the
   *         response.
   */
  public Map<String, String> getZ30Map() {
    return z30Map;
  }
}
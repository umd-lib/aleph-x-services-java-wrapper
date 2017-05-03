package edu.umd.lib.axsjw.jaxb;

import javax.xml.bind.annotation.XmlElement;

/**
 * Abstract class that manages common fields in the return values of all Aleph
 * operations
 */
public abstract class AbstractAlephOpResult {
  @XmlElement(name = "session-id")
  private String sessionId;

  @XmlElement(name = "error")
  private String error;

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
   * @return true if an error has occurred, false otherwise.
   */
  public boolean isError() {
    return error != null;
  }
}
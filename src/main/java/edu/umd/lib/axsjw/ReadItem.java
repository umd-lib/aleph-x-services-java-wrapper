package edu.umd.lib.axsjw;

import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

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

  public String getSessionId() {
    return sessionId;
  }

  public void setSessionId(String sessionId) {
    this.sessionId = sessionId;
  }

  public String getError() {
    return error;
  }

  public void setError(String error) {
    this.error = error;
  }

  public Map<String, String> getZ30Map() {
    return z30Map;
  }

  public void setZ30Map(Map<String, String> z30Map) {
    this.z30Map = z30Map;
  }
}
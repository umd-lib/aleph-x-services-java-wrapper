package edu.umd.lib.axsjw;

import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement(name = "circ-status")
@XmlAccessorType(XmlAccessType.FIELD)
public class CircStatus {
  @XmlElement(name = "session-id")
  private String sessionId;

  @XmlElement(name = "error")
  private String error;

  @XmlElement(name = "item-data")
  @XmlJavaTypeAdapter(MapAdapter.class)
  private Map<String, String>[] itemDataMaps;

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

  public Map<String, String>[] getItemDataMaps() {
    return itemDataMaps;
  }

  public void setZ30Map(Map<String, String>[] itemDataMaps) {
    this.itemDataMaps = itemDataMaps;
  }

}
package edu.umd.lib.axsjw.jaxb;

import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * JAXB class for converting XML returned by the "circ-status" operation into a
 * Java object.
 */
@XmlRootElement(name = "circ-status")
@XmlAccessorType(XmlAccessType.FIELD)
public class CircStatus extends AbstractAlephOpResult {
  @XmlElement(name = "item-data")
  @XmlJavaTypeAdapter(MapAdapter.class)
  private Map<String, String>[] itemDataMaps;

  /**
   * @return an array of Maps representing the "item-data" elements in the
   *         response.
   */
  public Map<String, String>[] getItemDataMaps() {
    return itemDataMaps;
  }
}
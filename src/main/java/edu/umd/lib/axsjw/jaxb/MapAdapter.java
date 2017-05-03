package edu.umd.lib.axsjw.jaxb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Converts XML nodes into a Map of with keys derived from the node name and
 * values derived from the text values within the node.
 *
 * Source:
 *   http://stackoverflow.com/questions/27182975/jaxb-unmarshal-elements-of-xml-to-map
 */
public class MapAdapter extends XmlAdapter<MapAdapter.AdaptedMap, Map<String, String>> {

  private DocumentBuilder documentBuilder;

  public MapAdapter() throws ParserConfigurationException {
    documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
  }

  public static class AdaptedMap {
    @XmlAnyElement
    public List<Element> elements = new ArrayList<Element>();
  }

  @Override
  public AdaptedMap marshal(Map<String, String> map) {
    Document document = documentBuilder.newDocument();
    AdaptedMap adaptedMap = new AdaptedMap();
    for (Entry<String, String> entry : map.entrySet()) {
      Element element = document.createElement(entry.getKey());
      element.setTextContent(entry.getValue());
      adaptedMap.elements.add(element);
    }
    return adaptedMap;
  }

  @Override
  public Map<String, String> unmarshal(AdaptedMap adaptedMap) {
    HashMap<String, String> map = new HashMap<String, String>();
    for (Element element : adaptedMap.elements) {
      map.put(element.getLocalName(), element.getTextContent());
    }
    return map;
  }
}
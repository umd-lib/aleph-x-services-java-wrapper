package edu.umd.lib.axsjw.jaxb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URL;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CircStatusTest {
  private Unmarshaller unmarshaller;

  @Before
  public void setUp() throws Exception {
    // Create context
    JAXBContext ctx = JAXBContext.newInstance("edu.umd.lib.axsjw.jaxb");

    // Create unmarshaller
    unmarshaller = ctx.createUnmarshaller();
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void testValidResponseSingleItem() throws Exception {
    URL testFileUrl = ClassLoader.getSystemResource("test-data/circ-status-valid-single-item.xml");
    File testFile = new File(testFileUrl.toURI());

    CircStatus circStatus = (CircStatus) unmarshaller.unmarshal(testFile);

    assertEquals("PDGYYRDCJ6356XT6TLQU5KK48DKVAYVLGB9SS9JFBQEI4M8UCI", circStatus.getSessionId());
    Map<String, String>[] itemDataMaps = circStatus.getItemDataMaps();
    assertEquals(1, itemDataMaps.length);

    Map<String, String> itemDataMap0 = itemDataMaps[0];
    assertEquals("On Shelf", itemDataMap0.get("due-date"));

    assertFalse(circStatus.isError());
    assertNull(circStatus.getError());
  }

  @Test
  public void testValidResponseMultipleItems() throws Exception {
    URL testFileUrl = ClassLoader.getSystemResource("test-data/circ-status-valid-multiple-items.xml");
    File testFile = new File(testFileUrl.toURI());

    CircStatus circStatus = (CircStatus) unmarshaller.unmarshal(testFile);

    assertEquals("V5Y84RLQ6QSSSR2GABI6M7FTMSJ24ESMIDT24XYF2UKMP8QYUI", circStatus.getSessionId());
    Map<String, String>[] itemDataMaps = circStatus.getItemDataMaps();
    assertEquals(2, itemDataMaps.length);

    Map<String, String> itemDataMap0 = itemDataMaps[0];
    assertEquals("On Shelf", itemDataMap0.get("due-date"));

    assertFalse(circStatus.isError());
    assertNull(circStatus.getError());
  }

  @Test
  public void testErrorResponse() throws Exception {
    URL testFileUrl = ClassLoader.getSystemResource("test-data/circ-status-error-item-not-found.xml");
    File testFile = new File(testFileUrl.toURI());

    CircStatus circStatus = (CircStatus) unmarshaller.unmarshal(testFile);

    assertEquals("D4UPE5LUKUFNUSBNH51A8NIYNSRKG6LFSJMN2YH9DQBVHGIKAR", circStatus.getSessionId());

    assertTrue(circStatus.isError());
    assertEquals("Document: 999999999 doesn't exist in library: MAI01. Make sure you insert BIB library.",
        circStatus.getError());

    assertNull(circStatus.getItemDataMaps());
  }
}
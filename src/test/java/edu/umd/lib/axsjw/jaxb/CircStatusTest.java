package edu.umd.lib.axsjw.jaxb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

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
  public void testValidResponsSingleItem() throws Exception {
    URL testFileUrl = ClassLoader.getSystemResource("test-data/circ-status-valid-single-item.xml");
    File testFile = new File(testFileUrl.toURI());

    CircStatus circStatus = (CircStatus) unmarshaller.unmarshal(testFile);

    assertEquals("MJGH1J27U7BG14HCNED7QX9D626UMUHVUHTXPVJN4SBRAJLMD5", circStatus.getSessionId());
    Map<String, String>[] itemDataMaps = circStatus.getItemDataMaps();
    assertEquals(1, itemDataMaps.length);

    Map<String, String> itemDataMap0 = itemDataMaps[0];
    assertEquals("On Shelf", itemDataMap0.get("due-date"));
    assertNull(circStatus.getError());
  }

  @Test
  public void testValidResponseMultipleItems() throws Exception {
    URL testFileUrl = ClassLoader.getSystemResource("test-data/circ-status-valid-multiple-items.xml");
    File testFile = new File(testFileUrl.toURI());

    CircStatus circStatus = (CircStatus) unmarshaller.unmarshal(testFile);

    assertEquals("76G7MA6BPUG18PPLMLPS24P1UX69LRDSU62K1C5MSG3USC7LBN", circStatus.getSessionId());
    Map<String, String>[] itemDataMaps = circStatus.getItemDataMaps();
    assertEquals(2, itemDataMaps.length);

    Map<String, String> itemDataMap0 = itemDataMaps[0];
    assertEquals("On Shelf", itemDataMap0.get("due-date"));
    assertNull(circStatus.getError());
  }

  @Test
  public void testErrorResponse() throws Exception {
    URL testFileUrl = ClassLoader.getSystemResource("test-data/circ-status-error-item-not-found.xml");
    File testFile = new File(testFileUrl.toURI());

    CircStatus circStatus = (CircStatus) unmarshaller.unmarshal(testFile);

    assertEquals("HMUF2ATHT8FUFFULAPRTM3PS1LHSUX5QXVG853PPGXQCQF6F2A", circStatus.getSessionId());
    assertNull(circStatus.getItemDataMaps());
    assertEquals("Document: 999999999 doesn't exist in library: MAI01. Make sure you insert BIB library.",
        circStatus.getError());
  }
}
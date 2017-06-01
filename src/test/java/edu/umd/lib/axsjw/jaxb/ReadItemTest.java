package edu.umd.lib.axsjw.jaxb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ReadItemTest {
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
  public void testValidResponse() throws Exception {
    URL testFileUrl = ClassLoader.getSystemResource("test-data/read-item-valid.xml");
    File testFile = new File(testFileUrl.toURI());

    ReadItem readItem = (ReadItem) unmarshaller.unmarshal(testFile);

    assertEquals("VT5QF4XVQ4QJE9SY166YXELPHJN8AH3MT6RVAVEEKKQ9BFR54F", readItem.getSessionId());
    assertEquals("32055660182979", readItem.getZ30Map().get("z30-barcode"));

    assertFalse(readItem.isError());
    assertNull(readItem.getError());
  }

  @Test
  public void testErrorResponse() throws Exception {
    URL testFileUrl = ClassLoader.getSystemResource("test-data/read-item-error-item-not-found-barcode.xml");
    File testFile = new File(testFileUrl.toURI());

    ReadItem readItem = (ReadItem) unmarshaller.unmarshal(testFile);

    assertEquals("N8A9J13UBK6GJ79UDH35MDGNQUQ7BKPT2H4GPAYIYMTH49631E", readItem.getSessionId());

    assertTrue(readItem.isError());
    assertEquals("Item could not been found based on item barcode: BARCODE_DOES_NOT_EXIST.", readItem.getError());

    assertNull(readItem.getZ30Map());
  }
}
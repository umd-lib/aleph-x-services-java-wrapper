package edu.umd.lib.axsjw.net.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.quality.Strictness;

import edu.umd.lib.axsjw.jaxb.CircStatus;
import edu.umd.lib.axsjw.jaxb.MapAdapter;
import edu.umd.lib.axsjw.jaxb.ReadItem;
import edu.umd.lib.axsjw.net.JaxbAlephOp;

public class JaxbAlephOpImplTest extends JerseyTest {
  @Rule
  public MockitoRule rule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS);

  @Override
  public void setUp() throws Exception {
  }

  @Override
  public void tearDown() throws Exception {
  }

  @Override
  protected Application configure() {
    return new ResourceConfig();
  }

  @Test
  public void testValidReadItem() throws Exception {
    URL testFileUrl = ClassLoader.getSystemResource("test-data/read-item-valid.xml");
    File testFile = new File(testFileUrl.toURI());
    byte[] fileBytes = Files.readAllBytes(testFile.toPath());
    String fileAsString = new String(fileBytes, StandardCharsets.UTF_8);

    WebTarget webTargetMock = mock(WebTarget.class);
    Builder builderMock = mock(Builder.class);
    when(webTargetMock.request(MediaType.TEXT_XML)).thenReturn(builderMock);
    when(builderMock.get(String.class)).thenReturn(fileAsString);

    JaxbAlephOp<ReadItem> alephOp = new JaxbAlephOpImpl<>();
    Map<String, String> queryParams = new HashMap<>();

    // Create context
    JAXBContext ctx = JAXBContext.newInstance("edu.umd.lib.axsjw.jaxb");

    // Create unmarshaller
    Unmarshaller unmarshaller = ctx.createUnmarshaller();

    ReadItem readItem = alephOp.request(webTargetMock, queryParams, unmarshaller);
    assertEquals("HYITNCE7LYPH46E8LXSFVUKVTR3NHIX8T1XCDTDYKUEV6PLNSG", readItem.getSessionId());
  }

  @Test
  public void testInvalidReadItem() throws Exception {
    String responseString = "This response is not valid XML.";

    WebTarget webTargetMock = mock(WebTarget.class);
    Builder builderMock = mock(Builder.class);
    when(webTargetMock.request(MediaType.TEXT_XML)).thenReturn(builderMock);
    when(builderMock.get(String.class)).thenReturn(responseString);

    JaxbAlephOp<ReadItem> alephOp = new JaxbAlephOpImpl<>();
    Map<String, String> queryParams = new HashMap<>();

    // Create context
    JAXBContext ctx = JAXBContext.newInstance(ReadItem.class, CircStatus.class);

    // Create unmarshaller
    Unmarshaller unmarshaller = ctx.createUnmarshaller();

    MapAdapter mapAdapter = new MapAdapter();
    unmarshaller.setAdapter(mapAdapter);

    try {
      ReadItem readItem = alephOp.request(webTargetMock, queryParams, unmarshaller);
      fail("Should have thrown UnmarshalException.");
    } catch (UnmarshalException ue) {
      assertTrue(true);
    }
  }

}

package edu.umd.lib.axsjw.facade.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import javax.xml.bind.Unmarshaller;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class FacadeUtilsTest {
  private Unmarshaller unmarshaller;

  @Before
  public void setUp() throws Exception {
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void testLeftPad() throws Exception {
    assertEquals("0000", FacadeUtils.leftPad(4, '0', ""));
    assertEquals("000A", FacadeUtils.leftPad(4, '0', "A"));
    assertEquals("00AB", FacadeUtils.leftPad(4, '0', "AB"));
    assertEquals("0ABC", FacadeUtils.leftPad(4, '0', "ABC"));
    assertEquals("ABCD", FacadeUtils.leftPad(4, '0', "ABCD"));
    assertEquals("ABCDE", FacadeUtils.leftPad(4, '0', "ABCDE"));

    assertEquals("..AB", FacadeUtils.leftPad(4, '.', "AB"));

    assertEquals("", FacadeUtils.leftPad(0, '.', ""));

    assertEquals("ABC", FacadeUtils.leftPad(-1, '0', "ABC"));

    assertNull(FacadeUtils.leftPad(9, '0', null));
  }

  @Test
  public void testPadSysNum() throws Exception {
    assertEquals("000000000", FacadeUtils.padSysNum(""));
    assertEquals("000000001", FacadeUtils.padSysNum("1"));
    assertEquals("000000012", FacadeUtils.padSysNum("12"));
    assertEquals("000000123", FacadeUtils.padSysNum("123"));
    assertEquals("000001234", FacadeUtils.padSysNum("1234"));
    assertEquals("000012345", FacadeUtils.padSysNum("12345"));
    assertEquals("000123456", FacadeUtils.padSysNum("123456"));
    assertEquals("001234567", FacadeUtils.padSysNum("1234567"));
    assertEquals("012345678", FacadeUtils.padSysNum("12345678"));
    assertEquals("123456789", FacadeUtils.padSysNum("123456789"));
    assertEquals("1234567890", FacadeUtils.padSysNum("1234567890"));

    assertNull(FacadeUtils.padSysNum(null));
  }
}
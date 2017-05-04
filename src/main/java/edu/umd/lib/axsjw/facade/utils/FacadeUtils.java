package edu.umd.lib.axsjw.facade.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Miscellaneous utility methods
 */
public class FacadeUtils {
  private static final Logger logger = LoggerFactory.getLogger(FacadeUtils.class);

  /**
   * Returns the given String, left-padded with the given character so that the
   * total length of the String is the given totalLength.
   * <p>
   * The given String is returned unchanged if the length of the String is equal
   * to or greater than the given totalLength.
   *
   * @param totalLength
   *          the total length of the returned String. Should be greater than
   *          zero.
   * @param padChar
   *          the character to use for padding
   * @param strToPad
   *          the String to pad
   * @return the given String, left-padded with the given character so that the
   *         total length of the String is the given totalLength. Will return
   *         null if the given String is null.
   */
  public static String leftPad(int totalLength, char padChar, String strToPad) {
    if (strToPad == null) {
      return null;
    }

    if (strToPad.length() >= totalLength) {
      return strToPad;
    }

    StringBuilder sb = new StringBuilder(strToPad);
    while (sb.length() < totalLength) {
      sb.insert(0, padChar);
    }
    return sb.toString();
  }

  /**
   * Returns the given System Number, left-padded with zeros to a total length
   * of 9 characters.
   *
   * @param sysNo
   *          the system number to left-pad
   * @return the given String, left-padded with zeros to a total length of 9
   *         characters.
   */
  public static String padSysNum(String sysNum) {
    return leftPad(9, '0', sysNum);
  }

  /**
   * Returns a Data from the given dueData and dueHour, or null if the Strings
   * cannot be parsed.
   *
   * @param dueDate
   *          a String representing the due date, expected to be in
   *          "MMMM-dd-yyyy" format.
   *
   * @param dueHour
   *          a String representing the due date time, expected to be in
   *          "HH:mm aa" format.
   * @return a Date from the given dueData and dueHour, or null if the Strings
   *         cannot be parsed.
   */
  public static Date parseDueDate(String dueDate, String dueHour) {
    if ((dueDate == null) || (dueHour == null)) {
      return null;
    }
    String fullDateString = dueDate + "|" + dueHour;
    SimpleDateFormat sdf = new SimpleDateFormat("MMMM-dd-yyyy|HH:mm aa");
    try {
      Date date = sdf.parse(fullDateString);
      return date;
    } catch (ParseException pe) {
      logger.error("Could not parse dueDate: '" + dueDate + "', dueHour: '" + dueHour + "' as a date.", pe);
    }
    return null;
  }
}
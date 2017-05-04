package edu.umd.lib.axsjw.facade;

/**
 * Value object for returning the result from a TitleAvailability request.
 * <p>
 * Note: The "dueDate" and "dueHour" are the strings returned by the Aleph
 * X-Server "circ-status" operation. They are not converted into a
 * java.util.Date object because Aleph does not provide time zone information.
 */
public class TitleAvailabilityResult {
  private int numAvailable = 0;
  private String dueDate = null;
  private String dueHour = null;

  public TitleAvailabilityResult() {
  }

  /**
   * Constructs a TitleAvailability object using the given parameters.
   *
   * @param numAvailable
   *          the number of items available
   * @param dueDate
   *          the due date (in "MMMM-dd-yyyy" format) of the next available
   *          item. Will be ignored if numAvailabe is greater than zero.
   * @param dueHour
   *          the due hour (in "HH:mm a" format) of the next available item.
   *          Will be ignored if numAvailabe is greater than zero.
   */
  public TitleAvailabilityResult(int numAvailable, String dueDate, String dueHour) {
    this.numAvailable = numAvailable;
    this.dueDate = dueDate;
    this.dueHour = dueHour;
  }

  public int getNumAvailable() {
    return numAvailable;
  }

  /**
   * @return a String representing the date (in "MMMM-dd-yyyy" format) that the
   *         next available item will be available. Will be null if numAvailable
   *         is greater than 0.
   */
  public String getDueDate() {
    if (numAvailable > 0) {
      return null;
    }
    return dueDate;
  }

  /**
   * @return a String representing the time (in "HH:mm a" format) that the next
   *         available item will be available. Will be null if numAvailable is
   *         greater than 0.
   */
  public String getDueHour() {
    if (numAvailable > 0) {
      return null;
    }
    return dueHour;
  }
}
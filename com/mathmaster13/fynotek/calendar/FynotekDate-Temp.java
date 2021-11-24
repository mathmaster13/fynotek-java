package com.mathmaster13.fynotek.calendar;
import java.time.chrono.*;

public final class FynotekDate implements ChronoLocalDate {
  /**
  The proleptic year.
  */
  private final int year;
  /**
  The month-of-year.
  */
  private final byte month;
  /**
  The day-of-month.
  */
  private final byte day;
  
  @Override
  public FynotekChronology getChronology() {
    return FynotekChronology.INSTANCE;
  }

  @Override
  public int lengthOfYear() {
    if (isLeapYear()) return 149;
    if (year % 2 == 0) return 148;
    return 147;
  }

  // the isSupported function, both for fields and for units, is handled by ChronoLocalDate, and is the exact same implementation as the one used by LocalDate.

  
}
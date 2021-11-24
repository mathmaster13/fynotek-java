package com.mathmaster13.fynotek.calendar;
import java.time.chrono.*;
import java.time.temporal.*;
import java.time.*;

public enum FynotekEra implements Era {
  /** The singleton instance for the era before the current one, 'kynsi fynotekñy', which has the numeric value 1. */
  KÑ,
  /** The singleton instance for the current era, 'kynsi fynotek', which has the numeric value 2. */
  KF;
  
  public ValueRange range(TemporalField field) {
    if (field instanceof ChronoField) {
      if (field == ChronoField.ERA) return ValueRange.of(1, 2);
      throw new UnsupportedTemporalTypeException("Unsupported field: " + field);
    }
    return field.rangeRefinedBy(this);
  }
  public int getValue() {
    return (this == KÑ ? 1 : 2);
  }

  public static FynotekEra of(int value) {
    if (value == 1) return KÑ;
    if (value == 2) return KF;
    throw new DateTimeException("Value must be 1 or 2");
  }
}
package com.mathmaster13.fynotek.calendar;

import static java.time.LocalTime.SECONDS_PER_DAY;
import static java.time.temporal.ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH;
import static java.time.temporal.ChronoField.ALIGNED_DAY_OF_WEEK_IN_YEAR;
import static java.time.temporal.ChronoField.ALIGNED_WEEK_OF_MONTH;
import static java.time.temporal.ChronoField.ALIGNED_WEEK_OF_YEAR;
import static java.time.temporal.ChronoField.DAY_OF_MONTH;
import static java.time.temporal.ChronoField.DAY_OF_YEAR;
import static java.time.temporal.ChronoField.EPOCH_DAY;
import static java.time.temporal.ChronoField.ERA;
import static java.time.temporal.ChronoField.MONTH_OF_YEAR;
import static java.time.temporal.ChronoField.PROLEPTIC_MONTH;
import static java.time.temporal.ChronoField.YEAR;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.IsoEra;
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalQueries;
import java.time.temporal.TemporalQuery;
import java.time.temporal.TemporalUnit;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.time.temporal.ValueRange;
import java.time.zone.ZoneOffsetTransition;
import java.time.zone.ZoneRules;
import java.util.Objects;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public final class FynotekDate implements ChronoLocalDate, Serializable {

  // TODO: Generate serialVersionUID
  static final long DAYS_UNIX_EPOCH_TO_FYNOTEK_EPOCH = 18636;
  // The number of days in a 4-year cycle.
  private static final int DAYS_PER_CYCLE = 591;
  
  /** The proleptic year. */
  private final int year;
  /** The month-of-year. */
  private final short month;
  /** The day-of-month. */
  private final short day;

  @Override
  public FynotekChronology getChronology() {return FynotekChronology.INSTANCE;}
  
  //-----------------------------------------------------------------------
  /**
   * Obtains the current date from the system clock in the default time-zone.
   * <p>
   * This will query the {@link Clock#systemDefaultZone() system clock} in the default
   * time-zone to obtain the current date.
   * <p>
   * Using this method will prevent the ability to use an alternate clock for testing
   * because the clock is hard-coded.
   *
   * @return the current date using the system clock and default time-zone, not null
   */
  public static FynotekDate now() {
    return now(Clock.systemDefaultZone());
  }

  /**
   * Obtains the current date from the system clock in the specified time-zone.
   * <p>
   * This will query the {@link Clock#system(ZoneId) system clock} to obtain the current date.
   * Specifying the time-zone avoids dependence on the default time-zone.
   * <p>
   * Using this method will prevent the ability to use an alternate clock for testing
   * because the clock is hard-coded.
   *
   * @param zone  the zone ID to use, not null
   * @return the current date using the system clock, not null
   */
  public static FynotekDate now(ZoneId zone) {
    return now(Clock.system(zone));
  }

  /**
   * Obtains the current date from the specified clock.
   * <p>
   * This will query the specified clock to obtain the current date - today.
   * Using this method allows the use of an alternate clock for testing.
   * The alternate clock may be introduced using {@link Clock dependency injection}.
   *
   * @param clock  the clock to use, not null
   * @return the current date, not null
   */
  public static FynotekDate now(Clock clock) {
    Objects.requireNonNull(clock, "clock");
    final Instant now = clock.instant();  // called once
    return ofInstant(now, clock.getZone());
  }

  //-----------------------------------------------------------------------
  /**
   * Obtains an instance of {@code FynotekDate} from a year, month and day.
   * <p>
   * This returns a {@code FynotekDate} with the specified year, month and day-of-month.
   * The day must be valid for the year and month, otherwise an exception will be thrown.
   *
   * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
   * @param month  the month-of-year to represent, from 1 to 5
   * @param dayOfMonth  the day-of-month to represent, from 1 to 30
   * @return the local date, not null
   * @throws DateTimeException if the value of any field is out of range,
   *  or if the day-of-month is invalid for the month-year
   */
  public static FynotekDate of(int year, int month, int dayOfMonth) {
    YEAR.checkValidValue(year);
    MONTH_OF_YEAR.checkValidValue(month);
    DAY_OF_MONTH.checkValidValue(dayOfMonth);
    return create(year, month, dayOfMonth);
  }

  //-----------------------------------------------------------------------
  /**
   * Obtains an instance of {@code FynotekDate} from a year and day-of-year.
   * <p>
   * This returns a {@code FynotekDate} with the specified year and day-of-year.
   * The day-of-year must be valid for the year, otherwise an exception will be thrown.
   *
   * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
   * @param dayOfYear  the day-of-year to represent, from 1 to 149
   * @return the local date, not null
   * @throws DateTimeException if the value of any field is out of range,
   *  or if the day-of-year is invalid for the year
   */
  public static FynotekDate ofYearDay(int year, int dayOfYear) {
    YEAR.checkValidValue(year);
    DAY_OF_YEAR.checkValidValue(dayOfYear);
    if (dayOfYear > lengthOfYear(year)) throw new DateTimeException("Invalid date 'DayOfYear " + dayOfYear "' as it is out of range for year '" + year + "'");
    int moy = monthFromDay(year, dayOfYear);
    int dom = domFromDOY(year, moy, dayOfYear);
    return new FynotekDate(year, moy, dom);
  }

  private static int monthFromDay(int year, int day) {
    int yearLength = lengthOfYear(year);
    int dayRange = 0;
    for (int i = 1; i <= 4; i++) {
      dayRange += lengthOfMonth(year, i, yearLength);
      if (day <= dayRange) return i;
    }
    return 5;
  }

  private static int domFromDOY(int year, int monthOfYear, int dayOfYear) {
    int dayRange = 0;
    for (int i = 1; i < monthOfYear; i++) dayRange += lengthOfMonth(year, i, lengthOfYear(year));
    return dayOfYear - dayRange;
  }

  static int lengthOfYear(long year) {
    if (year % 4 == 0) return 149;
    if (year % 2 == 0) return 148;
    return 147;
  }

  public int lengthOfYear() {return lengthOfYear(year);}

  static int lengthOfMonth(long year, long month) {
    return lengthOfMonth(year, month, lengthOfYear(year));
  }

  // For performance, so that the function does not have to check the year length repeatedly.
  private static int lengthOfMonth(long year, long month, int lengthOfYear) {
    if (lengthOfYear == 149 && month == 2) return 30;
    boolean oddYear = (yearLength == 147);
    boolean oddMonth = (month % 2 == 1);
    return (!(oddYear ^ oddMonth) ? 29 : 30);
  }

  public int lengthOfMonth() {return lengthOfMonth(year, month);}
  
}
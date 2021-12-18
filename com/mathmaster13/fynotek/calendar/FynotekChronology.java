package com.mathmaster13.fynotek.calendar;

import java.time.chrono.*;
import java.time.format.*;
import static java.time.temporal.ChronoField.*;
import static java.time.temporal.ChronoUnit.*;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.time.Clock;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.Month;
import java.time.Period;
import java.time.Year;
import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.ResolverStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalField;
import java.time.temporal.ValueRange;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public final class FynotekChronology extends AbstractChronology implements Serializable {

  /**
  Singleton instance of the ISO chronology.
  */
  public static final FynotekChronology INSTANCE = new FynotekChronology();

  static final long DAYS_UNIX_EPOCH_TO_FYNOTEK_EPOCH = 18636;

  // The number of days in a 4-year cycle.
  private static final int DAYS_PER_CYCLE = 591;
  
  /**
  Serialization version.
  */
  private static final long serialVersionUID = -1440403870442975015L;
  
  /**
  Restricted constructor.
  */
  private FynotekChronology() {}

  /**
  Gets the ID of the chronology - 'Fynotek'.
  <p>
  The ID uniquely identifies the {@code Chronology}.
  It can be used to look up the {@code Chronology} using {@link Chronology#of(String)}.

  @return the chronology ID - 'Fynotek'
  @see #getCalendarType()
  */
  @Override
  public String getId() {
    return "Fynotek";
  }

  /**
  Gets the calendar type of the underlying calendar system - 'fynotek'.
  <p>
  Since Fynotek is not in Unicode, the calendar type is not defined by the Unicode LDML, and is simply 'fynotek'.
  It can be used to look up the {@code Chronology} using {@link Chronology#of(String)}.

  @return the calendar system type - 'fynotek'
  @see #getId()
  */
  @Override
  public String getCalendarType() {
    return "fynotek";
  }

  //-----------------------------------------------------------------------
  /**
  Obtains a Fynotek date from the era, year-of-era, month-of-year, and day-of-month fields.

  @param era  the Fynotek era, not null
  @param yearOfEra  the Fynotek year-of-era
  @param month  the Fynotek month-of-year
  @param dayOfMonth  the Fynotek day-of-month
  @return the Fynotek date, not null
  @throws DateTimeException if unable to create the date
  @throws ClassCastException if the type of {@code era} is not {@code FynotekEra}
  */
  @Override  // override with covariant return type
  public FynotekDate date(Era era, int yearOfEra, int month, int dayOfMonth) {
    return date(prolepticYear(era, yearOfEra), month, dayOfMonth);
  }

  /**
  Obtains a Fynotek date from the proleptic-year, month-of-year, and day-of-month fields.
  <p>
  This is equivalent to {@link FynotekDate#of(int, int, int)}.

  @param prolepticYear  the Fynotek proleptic-year
  @param month  the Fynotek month-of-year
  @param dayOfMonth  the Fynotek day-of-month
  @return the Fynotek date, not null
  @throws DateTimeException if unable to create the date
  */
  @Override  // override with covariant return type
  public FynotekDate date(int prolepticYear, int month, int dayOfMonth) {
    return FynotekDate.of(prolepticYear, month, dayOfMonth);
  }

  /**
  Obtains a Fynotek date from the era, year-of-era and day-of-year fields.

  @param era  the Fynotek era, not null
  @param yearOfEra  the Fynotek year-of-era
  @param dayOfYear  the Fynotek day-of-year
  @return the Fynotek date, not null
  @throws DateTimeException if unable to create the date
  */
  @Override  // override with covariant return type
  public FynotekDate dateYearDay(Era era, int yearOfEra, int dayOfYear) {
    return dateYearDay(prolepticYear(era, yearOfEra), dayOfYear);
  }

  /**
  Obtains a Fynotek date from the proleptic-year and day-of-year fields.
  <p>
  This is equivalent to {@link FynotekDate#ofYearDay(int, int)}.

  @param prolepticYear  the Fynotek proleptic-year
  @param dayOfYear  the Fynotek day-of-year
  @return the Fynotek date, not null
  @throws DateTimeException if unable to create the date
  */
  @Override  // override with covariant return type
  public FynotekDate dateYearDay(int prolepticYear, int dayOfYear) {
    return FynotekDate.ofYearDay(prolepticYear, dayOfYear);
  }

  /**
  Obtains a Fynotek date from the epoch-day.
  <p>
  This is equivalent to {@link FynotekDate#ofEpochDay(long)}.

  @param epochDay  the epoch day
  @return the Fynotek date, not null
  @throws DateTimeException if unable to create the date
  */
  @Override  // override with covariant return type
  public FynotekDate dateEpochDay(long epochDay) {
    return FynotekDate.ofEpochDay(epochDay);
  }

  //-----------------------------------------------------------------------
  /**
  Obtains a Fynotek date from another date-time object.
  <p>
  This is equivalent to {@link FynotekDate#from(TemporalAccessor)}.

  @param temporal  the date-time object to convert, not null
  @return the Fynotek date, not null
  @throws DateTimeException if unable to create the date
  */
  @Override  // override with covariant return type
  public FynotekDate date(TemporalAccessor temporal) {
    return FynotekDate.from(temporal);
  }

  //-----------------------------------------------------------------------
  /**
  Gets the number of seconds from the epoch of 1970-01-01T00:00:00Z.
  <p>
  The number of seconds is calculated using the year, month, day-of-month, hour, minute, second, and zoneOffset.

  @param prolepticYear  the year
  @param month  the month-of-year, from 1 to 5
  @param dayOfMonth  the day-of-month, from 1 to 29 or 30, depending on the month
  @param hour  the hour-of-day, from 0 to 23
  @param minute  the minute-of-hour, from 0 to 59
  @param second  the second-of-minute, from 0 to 59
  @param zoneOffset the zone offset, not null
  @return the number of seconds relative to 1970-01-01T00:00:00Z, may be negative
  @throws DateTimeException if the value of any argument is out of range, or if the day-of-month is invalid for the month-of-year
  */
  @Override
  public long epochSecond(int prolepticYear, int month, int dayOfMonth, int hour, int minute, int second, ZoneOffset zoneOffset) {
    YEAR.checkValidValue(prolepticYear);
    MONTH_OF_YEAR.checkValidValue(month);
    DAY_OF_MONTH.checkValidValue(dayOfMonth);
    HOUR_OF_DAY.checkValidValue(hour);
    MINUTE_OF_HOUR.checkValidValue(minute);
    SECOND_OF_MINUTE.checkValidValue(second);
    Objects.requireNonNull(zoneOffset, "zoneOffset");
    if (dayOfMonth > 29) {
      int dom = numberOfDaysOfMonth(prolepticYear, month);
      if (dayOfMonth > dom) {
        if (dayOfMonth == 30) throw new DateTimeException("Invalid date 'Day 30 of Month " + month + "' as '" + prolepticYear + "' does not have 30 days");
        else throw new DateTimeException("Invalid date 'Day " + dayOfMonth + " of Month " + month + "'");
      }
    }

    prolepticYear--;
    long cycles = prolepticYear / 4;
    int nonCycleYears = prolepticYear % 4;
    long totalDays = 0;
    int timeinSec = 0;
    totalDays += (cycles * DAYS_PER_CYCLE) + yearsToDays(nonCycleYears) + DAYS_UNIX_EPOCH_TO_FYNOTEK_EPOCH;
    timeinSec = (hour * 60 + minute) * 60 + second;
    return Math.addExact(Math.multiplyExact(totalDays, 86400L), timeinSec - zoneOffset.getTotalSeconds());
  }

  private int yearsToDays(int years) {
    int days = 0;
    switch (years) {
      case 3:
        days += 147;
      case 2:
        days += 148;
      case 1:
        days += 147;
    }
    return days;
  }
  
  /**
  Gets the number of days for the given month in the given year.

  @param year the year to represent
  @param month the month-of-year to represent, from 1 to 5
  @return the number of days for the given month in the given year
  */
  private int numberOfDaysOfMonth(int year, int month) {
    if (year % 2 == 0) return ((month % 2 == 1 || (isLeapYear(year) && month == 2)) ? 30 : 29); // Even-numbered years
    return ((month % 2 == 1) ? 29 : 30); // Odd-numbered years
  }

  private int lengthOfYear(int year) {
    if (isLeapYear(year)) return 149;
    if (year % 2 == 0) return 148;
    return 147;
  }

  //-----------------------------------------------------------------------
  /**
  Obtains the current Fynotek date from the system clock in the default time-zone.
  <p>
  This will query the {@link Clock#systemDefaultZone() system clock} in the default
  time-zone to obtain the current date.
  <p>
  Using this method will prevent the ability to use an alternate clock for testing
  because the clock is hard-coded.

  @return the current Fynotek date using the system clock and default time-zone, not null
  @throws DateTimeException if unable to create the date
  */
  @Override  // override with covariant return type
  public FynotekDate dateNow() {
    return dateNow(Clock.systemDefaultZone());
  }

  /**
  Obtains the current Fynotek date from the system clock in the specified time-zone.
  <p>
  This will query the {@link Clock#system(ZoneId) system clock} to obtain the current date.
  Specifying the time-zone avoids dependence on the default time-zone.
  <p>
  Using this method will prevent the ability to use an alternate clock for testing
  because the clock is hard-coded.

  @return the current Fynotek date using the system clock, not null
  @throws DateTimeException if unable to create the date
  */
  @Override  // override with covariant return type
  public FynotekDate dateNow(ZoneId zone) {
    return dateNow(Clock.system(zone));
  }

  /**
  Obtains the current Fynotek date from the specified clock.
  <p>
  This will query the specified clock to obtain the current date - today.
  Using this method allows the use of an alternate clock for testing.
  The alternate clock may be introduced using {@link Clock dependency injection}.

  @param clock  the clock to use, not null
  @return the current Fynotek date, not null
  @throws DateTimeException if unable to create the date
  */
  @Override  // override with covariant return type
  public FynotekDate dateNow(Clock clock) {
    Objects.requireNonNull(clock, "clock");
    return date(FynotekDate.now(clock));
  }

  //-----------------------------------------------------------------------
  /**
  Checks if the year is a leap year.
  This method applies the current rules for leap years across the whole time-line.
  A year is a leap year if it is divisible by four without remainder.
  The calculation is proleptic - applying the same rules into the far future and far past.

  @param prolepticYear  the Fynotek proleptic year to check
  @return true if the year is leap, false otherwise
  */
  @Override
  public boolean isLeapYear(long prolepticYear) {
    return (prolepticYear % 4 == 0);
  }

  @Override
  public int prolepticYear(Era era, int yearOfEra) {
    if (!(era instanceof FynotekEra)) throw new ClassCastException("Era must be FynotekEra");
    return (era == FynotekEra.KF ? yearOfEra : 1 - yearOfEra);
  }

  @Override
  public FynotekEra eraOf(int eraValue) {
    return FynotekEra.of(eraValue);
  }

  @Override
  public List<Era> eras() {
    return List.of(FynotekEra.values());
  }

  //-----------------------------------------------------------------------
  /**
  Resolves parsed {@code ChronoField} values into a date during parsing.
  <p>
  Most {@code TemporalField} implementations are resolved using the
  resolve method on the field. By contrast, the {@code ChronoField} class
  defines fields that only have meaning relative to the chronology.
  As such, {@code ChronoField} date fields are resolved here in the
  context of a specific chronology.
  <p>
  {@code ChronoField} instances on the Fynotek calendar system are resolved
  as follows.
  <ul>
  <li>{@code EPOCH_DAY} - If present, this is converted to a {@code FynotekDate}
   and all other date fields are then cross-checked against the date.
  <li>{@code PROLEPTIC_MONTH} - If present, then it is split into the
   {@code YEAR} and {@code MONTH_OF_YEAR}. If the mode is strict or smart
   then the field is validated.
  <li>{@code YEAR_OF_ERA} and {@code ERA} - If both are present, then they
   are combined to form a {@code YEAR}. In lenient mode, the {@code YEAR_OF_ERA}
   range is not validated, in smart and strict mode it is. The {@code ERA} is
   validated for range in all three modes. If only the {@code YEAR_OF_ERA} is
   present, and the mode is smart or lenient, then the current era (CE/AD)
   is assumed. In strict mode, no era is assumed and the {@code YEAR_OF_ERA} is
   left untouched. If only the {@code ERA} is present, then it is left untouched.
  <li>{@code YEAR}, {@code MONTH_OF_YEAR} and {@code DAY_OF_MONTH} -
   If all three are present, then they are combined to form a {@code FynotekDate}.
   In all three modes, the {@code YEAR} is validated. If the mode is smart or strict,
   then the month and day are validated, with the day validated from 1 to 31.
   If the mode is lenient, then the date is combined in a manner equivalent to
   creating a date on the first of January in the requested year, then adding
   the difference in months, then the difference in days.
   If the mode is smart, and the day-of-month is greater than the maximum for
   the year-month, then the day-of-month is adjusted to the last day-of-month.
   If the mode is strict, then the three fields must form a valid date.
  <li>{@code YEAR} and {@code DAY_OF_YEAR} -
   If both are present, then they are combined to form a {@code FynotekDate}.
   In all three modes, the {@code YEAR} is validated.
   If the mode is lenient, then the date is combined in a manner equivalent to
   creating a date on the first of January in the requested year, then adding
   the difference in days.
   If the mode is smart or strict, then the two fields must form a valid date.
  <li>{@code YEAR}, {@code MONTH_OF_YEAR}, {@code ALIGNED_WEEK_OF_MONTH} and
   {@code ALIGNED_DAY_OF_WEEK_IN_MONTH} -
   If all four are present, then they are combined to form a {@code FynotekDate}.
   In all three modes, the {@code YEAR} is validated.
   If the mode is lenient, then the date is combined in a manner equivalent to
   creating a date on the first of January in the requested year, then adding
   the difference in months, then the difference in weeks, then in days.
   If the mode is smart or strict, then the all four fields are validated to
   their outer ranges. The date is then combined in a manner equivalent to
   creating a date on the first day of the requested year and month, then adding
   the amount in weeks and days to reach their values. If the mode is strict,
   the date is additionally validated to check that the day and week adjustment
   did not change the month.
  <li>{@code YEAR}, {@code MONTH_OF_YEAR}, {@code ALIGNED_WEEK_OF_MONTH} and
   {@code DAY_OF_WEEK} - If all four are present, then they are combined to
   form a {@code FynotekDate}. The approach is the same as described above for
   years, months and weeks in {@code ALIGNED_DAY_OF_WEEK_IN_MONTH}.
   The day-of-week is adjusted as the next or same matching day-of-week once
   the years, months and weeks have been handled.
  <li>{@code YEAR}, {@code ALIGNED_WEEK_OF_YEAR} and {@code ALIGNED_DAY_OF_WEEK_IN_YEAR} -
   If all three are present, then they are combined to form a {@code FynotekDate}.
   In all three modes, the {@code YEAR} is validated.
   If the mode is lenient, then the date is combined in a manner equivalent to
   creating a date on the first of January in the requested year, then adding
   the difference in weeks, then in days.
   If the mode is smart or strict, then the all three fields are validated to
   their outer ranges. The date is then combined in a manner equivalent to
   creating a date on the first day of the requested year, then adding
   the amount in weeks and days to reach their values. If the mode is strict,
   the date is additionally validated to check that the day and week adjustment
   did not change the year.
  <li>{@code YEAR}, {@code ALIGNED_WEEK_OF_YEAR} and {@code DAY_OF_WEEK} -
   If all three are present, then they are combined to form a {@code FynotekDate}.
   The approach is the same as described above for years and weeks in
   {@code ALIGNED_DAY_OF_WEEK_IN_YEAR}. The day-of-week is adjusted as the
   next or same matching day-of-week once the years and weeks have been handled.
  </ul>

  @param fieldValues  the map of fields to values, which can be updated, not null
  @param resolverStyle  the requested type of resolve, not null
  @return the resolved date, null if insufficient information to create a date
  @throws DateTimeException if the date cannot be resolved, typically
   because of a conflict in the input data
  */
  @Override  // override for performance
  public FynotekDate resolveDate(Map<TemporalField, Long> fieldValues, ResolverStyle resolverStyle) {
    return (FynotekDate) super.resolveDate(fieldValues, resolverStyle);
  }

  void resolveProlepticMonth(Map<TemporalField, Long> fieldValues, ResolverStyle resolverStyle) {
    Long pMonth = fieldValues.remove(PROLEPTIC_MONTH);
    if (pMonth != null) {
      if (resolverStyle != ResolverStyle.LENIENT) {
        PROLEPTIC_MONTH.checkValidValue(pMonth);
      }
      addFieldValue(fieldValues, MONTH_OF_YEAR, Math.floorMod(pMonth, 5) + 1);
      addFieldValue(fieldValues, YEAR, Math.floorDiv(pMonth, 5));
    }
  }

  FynotekDate resolveYearOfEra(Map<TemporalField, Long> fieldValues, ResolverStyle resolverStyle) {
    Long yoeLong = fieldValues.remove(YEAR_OF_ERA);
    if (yoeLong != null) {
      if (resolverStyle != ResolverStyle.LENIENT) {
        YEAR_OF_ERA.checkValidValue(yoeLong);
      }
      Long era = fieldValues.remove(ERA);
      if (era == null) {
        Long year = fieldValues.get(YEAR);
        if (resolverStyle == ResolverStyle.STRICT) {
          // do not invent era if strict, but do cross-check with year
          if (year != null) {
            addFieldValue(fieldValues, YEAR, (year > 0 ? yoeLong : Math.subtractExact(1, yoeLong)));
          } else {
            // reinstate the field removed earlier, no cross-check issues
            fieldValues.put(YEAR_OF_ERA, yoeLong);
          }
        } else {
          // invent era
          addFieldValue(fieldValues, YEAR, (year == null || year > 0 ? yoeLong : Math.subtractExact(1, yoeLong)));
        }
      } else if (era.longValue() == 2L) {
        addFieldValue(fieldValues, YEAR, yoeLong);
      } else if (era.longValue() == 1L) {
        addFieldValue(fieldValues, YEAR, Math.subtractExact(1, yoeLong));
      } else {
        throw new DateTimeException("Invalid value for era: " + era);
      }
    } else if (fieldValues.containsKey(ERA)) {
      ERA.checkValidValue(fieldValues.get(ERA));  // always validated
    }
    return null;
  }

  FynotekDate resolveYMD(Map <TemporalField, Long> fieldValues, ResolverStyle resolverStyle) {
    int y = YEAR.checkValidIntValue(fieldValues.remove(YEAR));
    if (resolverStyle == ResolverStyle.LENIENT) {
      long months = Math.subtractExact(fieldValues.remove(MONTH_OF_YEAR), 1);
      long days = Math.subtractExact(fieldValues.remove(DAY_OF_MONTH), 1);
      return FynotekDate.of(y, 1, 1).plusMonths(months).plusDays(days);
    }
    int moy = MONTH_OF_YEAR.checkValidIntValue(fieldValues.remove(MONTH_OF_YEAR));
    int dom = DAY_OF_MONTH.checkValidIntValue(fieldValues.remove(DAY_OF_MONTH));
    if (resolverStyle == ResolverStyle.SMART) {  // previous valid
      dom = Math.min(dom, numberOfDaysOfMonth(y, moy));
    }
    return FynotekDate.of(y, moy, dom);
  }

  FynotekDate resolveYMAA(Map<TemporalField, Long> fieldValues, ResolverStyle resolverStyle) {
    int y = range(YEAR).checkValidIntValue(fieldValues.remove(YEAR), YEAR);
    if (resolverStyle == ResolverStyle.LENIENT) {
      long months = Math.subtractExact(fieldValues.remove(MONTH_OF_YEAR), 1);
      long weeks = Math.subtractExact(fieldValues.remove(ALIGNED_WEEK_OF_MONTH), 1);
      long days = Math.subtractExact(fieldValues.remove(ALIGNED_DAY_OF_WEEK_IN_MONTH), 1);
      return date(y, 1, 1).plus(months, MONTHS).plus(weeks, WEEKS).plus(days, DAYS);
    }
    int moy = range(MONTH_OF_YEAR).checkValidIntValue(fieldValues.remove(MONTH_OF_YEAR), MONTH_OF_YEAR);
    int aw = range(ALIGNED_WEEK_OF_MONTH).checkValidIntValue(fieldValues.remove(ALIGNED_WEEK_OF_MONTH), ALIGNED_WEEK_OF_MONTH);
    int ad = range(ALIGNED_DAY_OF_WEEK_IN_MONTH).checkValidIntValue(fieldValues.remove(ALIGNED_DAY_OF_WEEK_IN_MONTH), ALIGNED_DAY_OF_WEEK_IN_MONTH);
    FynotekDate date = date(y, moy, 1).plus(daysFromWeeks(y, moy, aw) + (ad - 1), DAYS);
    if (resolverStyle == ResolverStyle.STRICT && date.get(MONTH_OF_YEAR) != moy) throw new DateTimeException("Strict mode rejected resolved date as it is in a different month");
    return date;
  }

  FynotekDate resolveYMAD(Map<TemporalField, Long> fieldValues, ResolverStyle resolverStyle) {
    // The aligned day-of-week is always the same as the day-of-week, so a very similar implementation is used.
    int y = range(YEAR).checkValidIntValue(fieldValues.remove(YEAR), YEAR);
    if (resolverStyle == ResolverStyle.LENIENT) {
      long months = Math.subtractExact(fieldValues.remove(MONTH_OF_YEAR), 1);
      long weeks = Math.subtractExact(fieldValues.remove(ALIGNED_WEEK_OF_MONTH), 1);
      long days = Math.subtractExact(fieldValues.remove(DAY_OF_WEEK), 1);
      return date(y, 1, 1).plus(months, MONTHS).plus(weeks, WEEKS).plus(days, DAYS);
    }
    int moy = range(MONTH_OF_YEAR).checkValidIntValue(fieldValues.remove(MONTH_OF_YEAR), MONTH_OF_YEAR);
    int aw = range(ALIGNED_WEEK_OF_MONTH).checkValidIntValue(fieldValues.remove(ALIGNED_WEEK_OF_MONTH), ALIGNED_WEEK_OF_MONTH);
    int ad = range(DAY_OF_WEEK).checkValidIntValue(fieldValues.remove(DAY_OF_WEEK), DAY_OF_WEEK);
    FynotekDate date = date(y, moy, 1).plus(daysFromWeeks(y, moy, aw) + (ad - 1), DAYS);
    if (resolverStyle == ResolverStyle.STRICT && date.get(MONTH_OF_YEAR) != moy) throw new DateTimeException("Strict mode rejected resolved date as it is in a different month");
    return date;
  }

  FynotekDate resolveYAA(Map<TemporalField, Long> fieldValues, ResolverStyle resolverStyle) {
    int y = range(YEAR).checkValidIntValue(fieldValues.remove(YEAR), YEAR);
    if (resolverStyle == ResolverStyle.LENIENT) {
      long weeks = Math.subtractExact(fieldValues.remove(ALIGNED_WEEK_OF_YEAR), 1);
      long days = Math.subtractExact(fieldValues.remove(ALIGNED_DAY_OF_WEEK_IN_YEAR), 1);
      return dateYearDay(y, 1).plus(weeks, WEEKS).plus(days, DAYS);
    }
    int aw = range(ALIGNED_WEEK_OF_YEAR).checkValidIntValue(fieldValues.remove(ALIGNED_WEEK_OF_YEAR), ALIGNED_WEEK_OF_YEAR);
    int ad = range(ALIGNED_DAY_OF_WEEK_IN_YEAR).checkValidIntValue(fieldValues.remove(ALIGNED_DAY_OF_WEEK_IN_YEAR), ALIGNED_DAY_OF_WEEK_IN_YEAR);
    ChronoLocalDate date = dateYearDay(y, 1).plus(daysFromWeeks(y, aw) + (ad - 1), DAYS);
    if (resolverStyle == ResolverStyle.STRICT && date.get(YEAR) != y) {
      throw new DateTimeException("Strict mode rejected resolved date as it is in a different year");
    }
    return date;
  }

  FynotekDate resolveYAD(Map<TemporalField, Long> fieldValues, ResolverStyle resolverStyle) {
        int y = range(YEAR).checkValidIntValue(fieldValues.remove(YEAR), YEAR);
    if (resolverStyle == ResolverStyle.LENIENT) {
      long weeks = Math.subtractExact(fieldValues.remove(ALIGNED_WEEK_OF_YEAR), 1);
      long days = Math.subtractExact(fieldValues.remove(DAY_OF_WEEK), 1);
      return dateYearDay(y, 1).plus(weeks, WEEKS).plus(days, DAYS);
    }
    int aw = range(ALIGNED_WEEK_OF_YEAR).checkValidIntValue(fieldValues.remove(ALIGNED_WEEK_OF_YEAR), ALIGNED_WEEK_OF_YEAR);
    int ad = range(DAY_OF_WEEK).checkValidIntValue(fieldValues.remove(DAY_OF_WEEK), DAY_OF_WEEK);
    ChronoLocalDate date = dateYearDay(y, 1).plus(daysFromWeeks(y, aw) + (ad - 1), DAYS);
    if (resolverStyle == ResolverStyle.STRICT && date.get(YEAR) != y) {
      throw new DateTimeException("Strict mode rejected resolved date as it is in a different year");
    }
    return date;
  }

  private int daysFromWeeks(int year, int month, int weeks) {
    weeks--; // If it's week 1, add nothing. If week 2, add the number of days in week 1. If it's week 3, add the number of days in weeks 1 and 2, etc.
    int days = 0;
    switch (weeks) {
      case 3:
        days += 7;
      case 2:
        days += (numberOfDaysOfMonth(year, month) == 30 ? 8 : 7);
      case 1:
        days += 7;
    }
    return days;
  }

  private int daysFromWeeks(int year, int weeks) {
    int weeksMod4 = weeks % 4;
    return daysFromWeeks(year, (weeks - 1)/4 + 1, (weeksMod4 == 0 ? 4 : weeksMod4));
  }

  //-----------------------------------------------------------------------
  @Override
  public ValueRange range(ChronoField field) {
    return field.range();
  }

  // package private functions my BELOATHED
  /**
  Adds a field-value pair to the map, checking for conflicts.
  <p>
  If the field is not already present, then the field-value pair is added to the map.
  If the field is already present and it has the same value as that specified, no action occurs.
  If the field is already present and it has a different value to that specified, then an exception is thrown.
  @param field  the field to add, not null
  @param value  the value to add, not null
  @throws java.time.DateTimeException if the field is already present with a different value
  */
  void addFieldValue(Map<TemporalField, Long> fieldValues, ChronoField field, long value) {
    Long old = fieldValues.get(field);  // check first for better error message
    if (old != null && old.longValue() != value) throw new DateTimeException("Conflict found: " + field + " " + old + " differs from " + field + " " + value);
    fieldValues.put(field, value);
  }
  
  //-----------------------------------------------------------------------
  
  // If stuff breaks try re-adding the writeReplace function
  
  /**
  Defend against malicious streams.
  @param s the stream to read
  @throws InvalidObjectException always
  */
  private void readObject(ObjectInputStream s) throws InvalidObjectException {
    throw new InvalidObjectException("Deserialization via serialization delegate");
  }
}

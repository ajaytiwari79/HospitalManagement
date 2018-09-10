package com.kairos.utils;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static java.time.temporal.TemporalAdjusters.previousOrSame;

/**
 * Created by oodles on 1/2/17.
 */
public class DateUtil {
    public static final String ISO_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String MONGODB_QUERY_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final String ONLY_DATE = "yyyy-MM-dd";
    private static final Logger logger = LoggerFactory.getLogger(DateUtil.class);

    public static Date getEndOfDay(Date date) {
        LocalDateTime localDateTime = dateToLocalDateTime(date);
        LocalDateTime endOfDay = localDateTime.with(LocalTime.MAX);
        return localDateTimeToDate(endOfDay);
    }

    public static Date getStartOfDay(Date date) {
        LocalDateTime localDateTime = dateToLocalDateTime(date);
        // LocalDateTime startOfDay = localDateTime.with(LocalTime.MIN);
        LocalDateTime startOfDay = localDateTime.with(LocalTime.MIN);

        return localDateTimeToDate(startOfDay);
    }

    public static Date addTimeInDate(Date date, int hour, int minute, int second) {
        LocalDateTime localDateTime = dateToLocalDateTime(date);
        // LocalDateTime startOfDay = localDateTime.with(LocalTime.MIN);
        LocalDateTime startOfDay = localDateTime.with(LocalTime.of(hour, minute, second));

        return localDateTimeToDate(startOfDay);
    }

    private static Date localDateTimeToDate(LocalDateTime startOfDay) {
        return Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant());
    }

    private static LocalDateTime dateToLocalDateTime(Date date) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(date.getTime()), ZoneId.systemDefault());
    }


    public static Date getSingleCompleteDate(Date date, Date time) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        String dateString = dateFormat.format(date);
        DateFormat timeFormat = new SimpleDateFormat("hh:mm:ss Z");
        String timeString = timeFormat.format(time);
        DateFormat f = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss Z");
        Date completeDate = f.parse(dateString + " " + timeString);
        return completeDate;
    }


    public static LocalDateTime getMondayFromWeek(int week, int year) {
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        LocalDateTime ldt = LocalDateTime.now()
                .withYear(year)
                .with(weekFields.weekOfYear(), week)
                .with(weekFields.dayOfWeek(), 2);
        return ldt;
    }

    public static LocalDateTime getSundayFromWeek(int week, int year) {
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        LocalDateTime ldt = LocalDateTime.now()
                .withYear(year)
                .with(weekFields.weekOfYear(), week + 1)
                .with(weekFields.dayOfWeek(), 1);
        return ldt;
    }

    public static Date getFirstDayOfCurrentWeek() {
        Calendar c = Calendar.getInstance();
        c.setTime(DateUtil.getCurrentDate());
        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return c.getTime();
    }

    public static Date getLastDayOfCurrentWeek() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.WEEK_OF_YEAR, Calendar.FRIDAY);
        return c.getTime();
    }

    public static Date addWeeksInDate(Date date, int weeks) {
        DateTime dateTime = new DateTime(date);
        return dateTime.plusWeeks(weeks).toDate();
    }

    public static String getISODateString(Date date) {
        SimpleDateFormat isoFormat = new SimpleDateFormat(MONGODB_QUERY_DATE_FORMAT);
        isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return isoFormat.format(date);
    }

    public static Date getDeductionInTimeDuration(Date startDate, Date endDate, int dayShiftPercentage, int nightShiftPercentage) {
        int percentage = 4;
        DateTime startTime = new DateTime(startDate);
        int startHour = startTime.getHourOfDay();
        if (startHour < 16) percentage = (dayShiftPercentage != 0) ? dayShiftPercentage : 4;
        else percentage = (nightShiftPercentage != 0) ? nightShiftPercentage : 7;

        long duration = endDate.getTime() - startDate.getTime();
        long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(duration);
        long reducedTime = (diffInMinutes - (diffInMinutes * percentage) / 100);
        Calendar c = Calendar.getInstance();
        c.setTime(startDate);
        c.add(Calendar.MINUTE, (int) (long) reducedTime);

        return c.getTime();
    }


    public static Date convertToOnlyDate(String receivedDate, String dateFormat) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = simpleDateFormat.parse(receivedDate);
        return date;
    }

    public static Date convertToOnlyDateTimeWithDateProvided(String receivedDate, long timestampedDate) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(MONGODB_QUERY_DATE_FORMAT);

        Date date = simpleDateFormat.parse(receivedDate);
        logger.info("selected date is " + date);
        timestampedDate = timestampedDate + (((date.getHours() * 60 * 60) + (date.getMinutes() * 60) + date.getSeconds()) * 1000);
        logger.info("selected date timestamp" + timestampedDate);
        date.setTime(timestampedDate);

        return date;
    }

    public static LocalDate getDateForUpcomingDay(LocalDate today, DayOfWeek dayOfWeek) {

        return today.with(TemporalAdjusters.next(dayOfWeek));

    }

    public static LocalDate getDateForPreviousDay(LocalDate today, DayOfWeek dayOfWeek) {

        return today.with(TemporalAdjusters.previous(dayOfWeek));

    }

    public static DayOfWeek getDayOfWeek(int day) {

        DayOfWeek dayOfWeek = null;
        switch (day) {
            case 1: {
                dayOfWeek = DayOfWeek.MONDAY;
                break;
            }
            case 2: {
                dayOfWeek = DayOfWeek.TUESDAY;
                break;
            }
            case 3: {
                dayOfWeek = DayOfWeek.WEDNESDAY;
                break;
            }
            case 4: {
                dayOfWeek = DayOfWeek.THURSDAY;
                break;
            }
            case 5: {
                dayOfWeek = DayOfWeek.FRIDAY;
                break;
            }
            case 6: {
                dayOfWeek = DayOfWeek.SATURDAY;
                break;
            }
            case 7: {
                dayOfWeek = DayOfWeek.SUNDAY;
                break;
            }
        }
        return dayOfWeek;
    }

    public static Long getIsoDateInLong(String dateReceived) {
        Long date = null;
        if (!StringUtils.isEmpty(dateReceived)) {
            DateFormat isoFormat = new SimpleDateFormat(ONLY_DATE);
            isoFormat.setLenient(false);
            isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            try {
                date = isoFormat.parse(dateReceived).getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return date;
    }


    public static Long getIsoDateWithTimezoneInLong(String dateReceived) throws ParseException {
        DateFormat isoFormat = new SimpleDateFormat(MONGODB_QUERY_DATE_FORMAT);
        isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = isoFormat.parse(dateReceived);
        return date.getTime();
    }


    public static LocalDate asLocalDate(Date date) {
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static LocalTime asLocalTime(Date date) {
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalTime();
    }

    public static Date asDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static Date asDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());

    }


    public static Date getCurrentDate() {
        //TODO this cant be system's date. this gotta be unit;s date. sachin
        return new Date();
    }


    public static LocalDate getCurrentLocalDate() {
        return LocalDate.now();
    }

    public static Long getCurrentDateMillis() {
        DateTime date = new DateTime().withTime(0, 0, 0, 0);
        return date.getMillis();
    }

    public static Date getTimezonedStartOfDay(String timezone, Date date) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone(timezone));
        DateTime dateTime = new DateTime(date).withTime(0, 0, 0, 0);
        calendar.setTime(dateTime.toDate());

        return calendar.getTime();
    }

    public static Date getTimezonedEndOfDay(String timezone, Date date) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone(timezone));
        DateTime dateTime = new DateTime(date).withTime(23, 59, 59, 59);
        calendar.setTime(dateTime.toDate());

        return calendar.getTime();
    }

    public static LocalDate getDateFromEpoch(Long dateLong) {
        LocalDate date = null;
        if (Optional.ofNullable(dateLong).isPresent()) {
            date = Instant.ofEpochMilli(dateLong).atZone(ZoneId.systemDefault()).toLocalDate();
        }
        return date;
    }

    public static Long getDateFromEpoch(LocalDate localDate) {
        return localDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();
    }

    public static LocalDate getTimezonedCurrentDate(String timezone) {
        return Instant.ofEpochMilli(new Date().getTime()).atZone(ZoneId.of(timezone)).toLocalDate();
    }

    public static Long getStartDateOfWeekFromDate(LocalDate date) {
        LocalDate localDate = date.with(previousOrSame(DayOfWeek.MONDAY));
        return localDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();
    }





}

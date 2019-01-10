package com.kairos.commons.utils;

import com.kairos.enums.DurationType;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static java.time.temporal.TemporalAdjusters.firstInMonth;
import static java.time.temporal.TemporalAdjusters.previousOrSame;
import static javax.management.timer.Timer.ONE_HOUR;

/**
 * Created by oodles on 1/2/17.
 */
public  class DateUtils {
    //TODO gotta add logger
    public static final String ISO_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String MONGODB_QUERY_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final String ONLY_DATE = "yyyy-MM-dd";

    public static Date getEndOfDay(Date date) {
        LocalDateTime localDateTime = dateToLocalDateTime(date);
        LocalDateTime endOfDay = localDateTime.with(LocalTime.MAX);
        return localDateTimeToDate(endOfDay);
    }
    public static LocalDateTime getLocalDateTimeFromDate(Date date ){
       return LocalDateTime.ofInstant(Instant.ofEpochMilli(date.getTime()), ZoneId.systemDefault());
    }
    public static long getCurrentMillistByTimeZone(String timeZone){
        return Timestamp.valueOf(LocalDateTime.now(ZoneId.of(timeZone))).getTime();
    }
    public static LocalDate getCurrentLocalDate() {
        return LocalDate.now();

    }

    public static LocalTime getCurrentLocalTime() {
        return LocalTime.now();

    }
    public static LocalDateTime getCurrentLocalDateTime() {
        return LocalDateTime.now().atZone(ZoneId.systemDefault()).toLocalDateTime();

    }

    public static LocalDate getLocalDateFromDate(Date date) {

        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return localDate;
    }

    public static Date convertLocalDateToDate(LocalDate dateToConvert) {
        return Date.from(dateToConvert.atStartOfDay()
                .atZone(ZoneId.systemDefault())
                .toInstant());
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

    public static Date localDateTimeToDate(LocalDateTime startOfDay) {
        return Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static LocalDateTime dateToLocalDateTime(Date date) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(date.getTime()), ZoneId.systemDefault());
    }


    public static LocalDateTime getLocalDateTime(){
        return LocalDateTime.now();
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
        c.setTime(DateUtils.getDate());
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
        DateTime startTime = new DateTime(startDate).toDateTime(DateTimeZone.UTC);
        int startHour = startTime.getHourOfDay();
        DateTime endTime = new DateTime(endDate).toDateTime(DateTimeZone.UTC);
        int endHour = endTime.getHourOfDay();
        if (startHour < 16) percentage = (dayShiftPercentage != 0) ? dayShiftPercentage : 4;
        else percentage = (nightShiftPercentage != 0) ? nightShiftPercentage : 7;

        if (endHour > 19) percentage = (nightShiftPercentage != 0) ? nightShiftPercentage : 7;

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
        timestampedDate = timestampedDate + (((date.getHours() * 60 * 60) + (date.getMinutes() * 60) + date.getSeconds()) * 1000);
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

    public static LocalTime toLocalTime(DateTime dateTime) {
        return LocalTime.of(dateTime.getHourOfDay(), dateTime.getMinuteOfHour());
    }

    public static Long getIsoDateInLong(String dateReceived) throws ParseException {
        DateFormat isoFormat = new SimpleDateFormat(ONLY_DATE);
        isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = isoFormat.parse(dateReceived);
        return date.getTime();
    }


    public static Long getIsoDateWithTimezoneInLong(String dateReceived) throws ParseException {
        DateFormat isoFormat = new SimpleDateFormat(MONGODB_QUERY_DATE_FORMAT);
        isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = isoFormat.parse(dateReceived);
        return date.getTime();
    }

    public static LocalDate calcNextMonday(LocalDate today) {
        return today.with(TemporalAdjusters.next(DayOfWeek.MONDAY));
    }

    public static Long getTimeDuration(Date startDate, Date endDate) {
        long duration = endDate.getTime() - startDate.getTime();
        return TimeUnit.MILLISECONDS.toMinutes(duration);
    }

    public static Date getCurrentDate() {
        //TODO this cant be system's date. this gotta be unit;s date. sachin
        return new Date();
    }

    /**
     * This method receives date to return date of particular DAY on the week number of provided date's month.
     * Example:- to get date of 1st,2nd,3rd or 4th DayOfWeek in a month.
     *
     * @param date
     * @param weekNumber
     * @param dayOfWeek
     * @return
     */

    public static LocalDate getDateOfWeekInMonth(LocalDate date, int weekNumber, DayOfWeek dayOfWeek) {
        return date.with(firstInMonth(dayOfWeek)).plusWeeks(weekNumber - 1);
    }

    /*
    This method receives date to return date of particular DAY on the week number of provided date's month.
     if the calculated date is of past date than it will return next month's date.
     Example if first monday is passed in provided date than date of upcoming month's first monday will be returned.
     */
    public static LocalDate getDateOfWeekInCurrentOrNextMonth(LocalDate date, int weekNumber, DayOfWeek dayOfWeek) {
        LocalDate calculatedDate = getDateOfWeekInMonth(date, weekNumber, dayOfWeek);
        if (calculatedDate.isBefore(date)) {
            calculatedDate = getDateOfWeekInMonth(date.plusMonths(1), weekNumber, dayOfWeek);
        }
        return calculatedDate;
    }

    /**
     * this method is checking whether task start date is equal to date from or
     * task start date lies between date from and date to
     *
     * @param taskStartDate
     * @param dateTimeFrom
     * @param dateTimeTo
     * @return
     */
    public static boolean isTaskOnOrBetweenDates(LocalDateTime taskStartDate, LocalDateTime dateTimeFrom, LocalDateTime dateTimeTo) {
        return (taskStartDate.isEqual(dateTimeFrom) || (taskStartDate.isAfter(dateTimeFrom) && taskStartDate.isBefore(dateTimeTo)));
    }


    public static Date getDateFromLocalDate(LocalDate localDate) {
        Date date;
        date = localDate != null
                ? Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant())
                : Date.from(LocalDate.now().atStartOfDay().toInstant(ZoneOffset.UTC));
        return date;
    }

    public static Date asDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static Date asDate(@NotNull(message = "date can not be null") LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public static LocalDate asLocalDate(Date date) {
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static LocalDate asLocalDate(Long date) {
        return Instant.ofEpochMilli(date).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static LocalDate asLocalDate(String receivedDate) {
        return LocalDate.parse(receivedDate, DateTimeFormatter.ISO_LOCAL_DATE);
    }

    public static LocalDate asLocalDate(DateTime dateTime){
        return asLocalDate(dateTime.toDate());
    }


    public static LocalTime asLocalTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()).toLocalTime();
    }

    public static LocalTime asLocalTime(Long date) {
        return LocalDateTime.ofInstant(new Date(date).toInstant(), ZoneId.systemDefault()).toLocalTime();
    }

    public static Date asDate(LocalTime localTime) {
        Instant instant = localTime.atDate(LocalDate.now()).
                atZone(ZoneId.systemDefault()).toInstant();
        Date time = Date.from(instant);
        return time;
    }

    public static LocalDateTime asLocalDateTime(Date date) {
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public static Date onlyDate(Date date) {
        return getDateByZoneDateTime(asZoneDateTime(date).truncatedTo(ChronoUnit.DAYS));
    }

    public static Date addMinutes(final Date date, final int amount) {
        return add(date, Calendar.MINUTE, amount);
    }

    public static Date addDays(final Date date, final int days) {
        return add(date, Calendar.DAY_OF_MONTH, days);
    }

    public static Date addMonths(final Date date, final int amount) {
        return add(date, Calendar.MONTH, amount);
    }

    private static Date add(final Date date, final int calendarField, final int amount) {
        if (date == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        final Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(calendarField, amount);
        return c.getTime();
    }

    public static boolean isSameDay(final Date date1, final Date date2) {
        if (date1 == null || date2 == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        final Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        final Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return isSameDay(cal1, cal2);
    }

    public static boolean isSameDay(final Calendar cal1, final Calendar cal2) {
        if (cal1 == null || cal2 == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
    }

    public static Date getDate() {
        return new Date();
    }

    public static LocalDate getLocalDate(){
        return LocalDate.now();
    }

    public static Date parseStringDate(String dateString, SimpleDateFormat dateFormat) throws ParseException {
        return dateFormat.parse(dateString);
    }

    public static Date getDate(long millis) {
        return new Date(millis);
    }

    public static LocalDate getLocalDate(long millis) {
        return getLocalDateFromDate(getDate(millis));
    }

    public static LocalDate toLocalDate(DateTime date) {
        return Instant.ofEpochMilli(date.toDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
    }


    public static Long getDifferenceBetweenDatesInMinute(Date startDate, Date endDate) {
        return ((endDate.getTime() - startDate.getTime()) / (1000 * 60));
    }

    public static int getDifferenceBetweenDatesInDays(LocalDate startDate, LocalDate endDate, DurationType durationType) {
        switch (durationType) {
            case DAYS: {
                return Period.between(startDate, endDate).getDays();
            }
            case MINUTES: {
                return (3600 * Period.between(startDate, endDate).getDays());
            }
            case HOURS: {
                return (60 * Period.between(startDate, endDate).getDays());
            }
        }
        return Period.between(startDate, endDate).getDays();

    }

    public static DateTime toJodaDateTime(LocalDate localDate) {
        return new DateTime(localDate.getYear(), localDate.getMonthValue(), localDate.getDayOfMonth(), 0, 0);
    }

    public static String getDateStringByTimeZone(Date date, ZoneId zoneId, String dateFormatString) {
        DateTime dateTime = new DateTime(date).withZone(DateTimeZone.forID(zoneId.getId()));
        org.joda.time.format.DateTimeFormatter formatter = DateTimeFormat.forPattern(dateFormatString);
        return dateTime.toString(formatter);
    }

    public static String getDateString(Date date, String dateFormatString) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormatString);
        return simpleDateFormat.format(date);
    }

    public static String formatLocalDate(LocalDate localDate, String dateFormatString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormatString);
        return localDate.format(formatter);
    }

    public static Date convertUTCTOTimeZone(Date date, TimeZone toTimeZone) {
        TimeZone fromTimeZone = TimeZone.getTimeZone("UTC");
        long fromTimeZoneOffset = getTimeZoneUTCAndDSTOffset(date, fromTimeZone);
        long toTimeZoneOffset = getTimeZoneUTCAndDSTOffset(date, toTimeZone);

        return new Date(date.getTime() - (toTimeZoneOffset - fromTimeZoneOffset));
    }

    private static long getTimeZoneUTCAndDSTOffset(Date date, TimeZone timeZone) {
        long timeZoneDSTOffset = 0;
        if (timeZone.inDaylightTime(date)) {
            timeZoneDSTOffset = timeZone.getDSTSavings();
        }

        return timeZone.getRawOffset() + timeZoneDSTOffset;
    }

    public static Date getDateByLocalDateAndLocalTime(LocalDate localDate, LocalTime localTime) {
        return new DateTime(localDate.getYear(), localDate.getMonthValue(), localDate.getDayOfMonth(), localTime.getHour(), localTime.getMinute()).toDate();
    }

    public static Date getDateByLocalDate(LocalDate localDate) {
        return new DateTime(localDate.getYear(), localDate.getMonthValue(), localDate.getDayOfMonth(), 0, 0).toDate();
    }

    public static String getDateStringWithFormat(Date date, String dateFormat) {

        org.joda.time.format.DateTimeFormatter formatter = DateTimeFormat.forPattern(dateFormat);
        DateTime dateTime = new DateTime(date);
        return dateTime.toString(formatter);
    }

    public static ZonedDateTime asZoneDateTime(Date date) {
        return ZonedDateTime.ofInstant(date.toInstant(),
                ZoneId.systemDefault());
    }

    public static Date asDate(ZonedDateTime dateTime) {
        return Date.from(dateTime.toInstant());
    }

    public static org.joda.time.LocalDate asJodaLocalDate(Date date) {
        return new DateTime(date).toLocalDate();
    }

    /**
     * returns Joda DateTime from {@link Date} and {@link LocalTime}
     */
    public static DateTime getDateTime(Date date, LocalTime time) {
        return new DateTime(date).withMinuteOfHour(time.getMinute()).withHourOfDay(time.getHour());
    }

    public static List<LocalDate> getDates(LocalDate start, LocalDate end) {
        List<LocalDate> dates = new ArrayList<>();
        for (LocalDate ld = start; !ld.isAfter(end); ld = ld.plusDays(1)) {
            dates.add(ld);
        }
        return dates;
    }


    public static Date getDateByZoneDateTime(ZonedDateTime zonedDateTime) {
        return Date.from(zonedDateTime.toInstant());
    }

    public static LocalDate addDurationInLocalDateExcludingLastDate(LocalDate localDate, int duration, DurationType durationType, int recurringNumber) {
        LocalDate endDate = addDurationInLocalDate(localDate, duration, durationType, recurringNumber);
        return endDate.minusDays(1);
    }


    public static Long getDurationBetweenTwoLocalDates(LocalDate startDate, LocalDate endDate, DurationType durationType) {
        switch (durationType) {
            // Add case for Month, Year etc
            case DAYS: {
                return ChronoUnit.DAYS.between(startDate, endDate);
            }
            default:
                return null;
        }
    }

    public static LocalDateTime addDurationInLocalDateTime(LocalDateTime localDateTime, int duration, DurationType durationType, int recurringNumber) {
        switch (durationType) {
            case DAYS: {
                return localDateTime.plusDays(duration * recurringNumber);
            }
            case WEEKS: {
                return localDateTime.plusDays(duration * recurringNumber * 7);
            }
            case MONTHS: {
                return localDateTime.plusMonths(duration * recurringNumber);
            }
            case HOURS: {
                return localDateTime.plusHours(duration * recurringNumber);
            }
            case MINUTES: {
                return localDateTime.plusMinutes(duration * recurringNumber);
            }


        }
        return localDateTime;
    }
    public static LocalDateTime substractDurationInLocalDateTime(LocalDateTime localDateTime, int duration, DurationType durationType) {
        switch (durationType) {
            case DAYS: {
                return localDateTime.minusDays(duration);
            }
            case HOURS: {
                return localDateTime.minusHours(duration );
            }
            case MINUTES: {
                return localDateTime.minusMinutes(duration );
            }


        }
        return localDateTime;
    }

    public static LocalDate addDurationInLocalDate(LocalDate localDate, int duration, DurationType durationType, int recurringNumber) {
        switch (durationType) {
            case DAYS: {
                return localDate.plusDays(duration * recurringNumber);
            }
            case WEEKS: {
                return localDate.plusDays(duration * recurringNumber * 7);
            }
            case MONTHS: {
                return localDate.plusMonths(duration * recurringNumber);
            }
            case YEAR: {
                return localDate.plusYears(duration * recurringNumber);
            }
        }
        return localDate;
    }

    public static String getDurationOfTwoLocalDates(LocalDate startDate, LocalDate endDate) {
        // Get duration of period
        Period period = Period.between(startDate, endDate);
        return (period.getMonths() > 0 ? period.getMonths() + " MONTHS " : "") +
                (period.getDays() >= 7 ? period.getDays() / 7 + " WEEKS " : "") +
                (period.getDays() % 7 > 0 ? period.getDays() % 7 + " DAYS " : "");

    }


    public static Date getISOEndOfWeekDate(LocalDate date) {

        Date endOfWeek = Date.from(ZonedDateTime.ofInstant(date.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant(),
                ZoneId.systemDefault()).with(DayOfWeek.SUNDAY).toInstant());

        return endOfWeek;
    }

    public static Long getISOStartOfWeek(LocalDate date) {

        Date startOfWeek = Date.from(ZonedDateTime.ofInstant(date.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant(),
                ZoneId.systemDefault()).with(DayOfWeek.MONDAY).toInstant());
        return startOfWeek.getTime();
    }


    public static Long getLongFromLocalDate(LocalDate date) {
        return (date == null) ? null : date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public static Long getLongFromLocalDateimeTime(LocalDateTime date) {
        return (date == null) ? null : date.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public static LocalDate getDateFromEpoch(Long dateLong) {
        LocalDate date = null;
        if (Optional.ofNullable(dateLong).isPresent()) {
            date = Instant.ofEpochMilli(dateLong).atZone(ZoneId.systemDefault()).toLocalDate();
        }
        return date;
    }

    public static Date asDateEndOfDay(LocalDate localDate) {
        return Date.from(localDate.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant());
    }

    public static Date asDate(LocalDate localDate, LocalTime localTime) {
        return Date.from(localDate.atTime(localTime).atZone(ZoneId.systemDefault()).toInstant());
    }

    public static Date getStartDateOfWeekFromDate(LocalDate date) {
        return asDate(date.with(previousOrSame(DayOfWeek.MONDAY)));

    }

    public static LocalTime getTimeFromMinuteLong(Long durationInMinute) {
        return LocalTime.MIN.plus(Duration.ofMinutes(durationInMinute));
    }

    public static LocalDate getLocalDateFromLocalDateTime(LocalDateTime localDateTime) {
        return localDateTime.toLocalDate();
    }

    public static LocalTime getLocalTimeFromLocalDateTime(LocalDateTime localDateTime) {
        return localDateTime.toLocalTime();
    }

    public static LocalDateTime getTimezonedCurrentDateTime(String timezone) {
        return Instant.ofEpochMilli(new Date().getTime()).atZone(ZoneId.of(timezone)).toLocalDateTime();
    }

    public static LocalDateTime getTimezonedCurrentDate(LocalDateTime dateTime) {
        return LocalDateTime.of(dateTime.getYear(), dateTime.getMonth(), dateTime.getDayOfMonth(), dateTime.getHour(), dateTime.getMinute());
    }

    public static LocalDateTime getLocalDateTimeFromZoneId(ZoneId unitTimeZone) {
        return LocalDateTime.now(unitTimeZone);
    }

    public static ZonedDateTime getZonedDateTimeFromZoneId(ZoneId unitTimeZone) {
        return ZonedDateTime.now(unitTimeZone);
    }

    public static Long getEndOfDayMillisforUnitFromEpoch(ZoneId zone, Long dateMillis) {
        LocalDate date = Instant.ofEpochMilli(dateMillis).atZone(ZoneId.systemDefault()).toLocalDate();
        ZonedDateTime zdt = ZonedDateTime.of(date, LocalTime.MAX, zone);
        return zdt.toInstant().toEpochMilli();
    }

    public static LocalDateTime getLocalDatetimeFromLong(Long millis) {
        return Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public static Long getMillisFromLocalDateTime(LocalDateTime date) {
        return date == null ? null : date.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public static Long getOneDayBeforeMillis() {
        return LocalDate.now().minusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public static Long getCurrentDayStartMillis() {
        return LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public static Date getCurrentDayStart() {
        return Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public static Long getCurrentMillis() {
        return System.currentTimeMillis();
    }

    public static LocalDateTime getStartOfDayFromLocalDate(LocalDate localDate) {

        return localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public static LocalDateTime getEndOfDayFromLocalDate(LocalDate localDate) {

        return localDate.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public static LocalDateTime getLocalDateTime(LocalDate localDate, int hours, int minutes, int seconds) {
        return LocalDateTime.of(localDate, LocalTime.of(hours, minutes, seconds));
    }

    public static Date getStartOfTheDay(Date date){
        return asDate(asZoneDateTime(date).truncatedTo(ChronoUnit.DAYS));
    }

    public static int getWeekNumberByLocalDate(LocalDate localDate) {
        TemporalField woy = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear();
        return localDate.get(woy);
    }

    public static Date getDateAfterDaysWithTime(short daysAfter, LocalTime duration) {
        return Date.from(DateUtils.getCurrentLocalDate().plusDays(daysAfter).atTime(duration).toInstant(ZoneOffset.UTC));
    }

    public static LocalDate getLocalDateAfterDays(short daysAfter) {
        return DateUtils.getCurrentLocalDate().plusDays(daysAfter);
    }

    public static LocalDate getLocalDateFromString(String receivedDate) {
        SimpleDateFormat format = new SimpleDateFormat(ISO_FORMAT, Locale.US);
        format.setTimeZone(TimeZone.getTimeZone(ZoneId.systemDefault()));
        LocalDate localDate = null;
        try {
            localDate = DateUtils.asLocalDate(format.parse(receivedDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return localDate;

    }

    public static LocalDateTime getLocalDateTimeFromMillis(Long longValue) {
        return (longValue == null) ? null : LocalDateTime.ofInstant(Instant.ofEpochMilli(longValue), ZoneId.systemDefault());
    }

    public static LocalDateTime getEndOfDayFromLocalDateTime(){
        return LocalDateTime.now().toLocalDate().atTime(LocalTime.MAX);
    }

    public static LocalDate getLocalDateFromTimezone(String timeZone){
        return LocalDate.now(ZoneId.of(timeZone));
    }

    public static Long getMillisFromDateAndTime(Date date,LocalTime localTime ) {

        return LocalDateTime.ofInstant(Instant.ofEpochMilli(date.getTime()), ZoneId.systemDefault()).withHour(localTime.getHour()).
                withMinute(localTime.getMinute()).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

    }

    public static int getHourFromDate(Date date) {

        return asZoneDateTime(date).getHour();
    }

    public static int getMinutesFromDate(Date date) {

        return asZoneDateTime(date).getMinute();
    }

    public static LocalDate getStartDateOfWeek(){
        return LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    }

    public static LocalDate getEndDateOfWeek(){
        return LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
    }


    public static Double getHoursFromTotalMilliSeconds(long totalMilliSeconds){
        long seconds, minutes, hours;
        seconds = totalMilliSeconds / 1000;
        minutes = seconds / 60;
        hours = minutes / 60;
        minutes = minutes % 60;
        return new Double(hours+"."+minutes);
    }

    public static Double getHoursByMinutes(double totalMinutes){
        Integer hour  = (int) totalMinutes/(60);
        Integer minutes = (int)totalMinutes % 60;
        return new Double(hour+"."+Math.abs(minutes));
    }



    public static boolean startDateIsEqualsOrBeforeEndDate(LocalDate startdate,LocalDate endDate){
        return startdate.isBefore(endDate) || startdate.equals(endDate);
    }


    public static Date plusDays(Date date,int plusDays){
        return DateUtils.asDate(DateUtils.asZoneDateTime(date).plusDays(plusDays));
    }

    public static Date plusMonths(Date date,int plusMonths){
        return DateUtils.asDate(DateUtils.asZoneDateTime(date).plusMonths(plusMonths));
    }

    public static Date plusWeeks(Date date,int plusWeeks){
        return DateUtils.asDate(DateUtils.asZoneDateTime(date).plusWeeks(plusWeeks));
    }

    public static Date plusHours(Date date,int plusHours){
        return DateUtils.asDate(DateUtils.asZoneDateTime(date).plusHours(plusHours));
    }

    public static Date plusMinutes(Date date,int plusMinutes){
        return DateUtils.asDate(DateUtils.asZoneDateTime(date).plusMinutes(plusMinutes));
    }

    public static Date plusSeconds(Date date,int plusSeconds) {
        return DateUtils.asDate(DateUtils.asZoneDateTime(date).plusSeconds(plusSeconds));
    }

    public static LocalDateTime getLocalDateTimeFromLocalDate(LocalDate localDate){
        return localDate.atStartOfDay();

    }

}

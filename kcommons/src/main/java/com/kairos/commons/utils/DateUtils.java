package com.kairos.commons.utils;

import com.kairos.enums.DurationType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.kairos.enums.DurationType.DAYS;
import static java.time.temporal.TemporalAdjusters.firstInMonth;
import static java.time.temporal.TemporalAdjusters.previousOrSame;

/**
 * Created by oodles on 1/2/17.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public  class DateUtils {
    public static final String ISO_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String MONGODB_QUERY_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final String ONLY_DATE = "yyyy-MM-dd";
    public static final String COMMON_DATE_FORMAT = "dd-MM-yyyy";
    public static final String KPI_DATE_FORMAT = "dd-MMM-yy";
    public static final String COMMON_TIME_FORMAT="HH:mm";
    public static final String THE_DATE_MUST_NOT_BE_NULL = "The date must not be null";
    public static final Logger LOGGER = LoggerFactory.getLogger(DateUtils.class);

    public static Date getEndOfDay(Date date) {
        LocalDateTime localDateTime = dateToLocalDateTime(date);
        LocalDateTime endOfDay = localDateTime.with(LocalTime.MAX);
        return localDateTimeToDate(endOfDay);
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

        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static Date convertLocalDateToDate(LocalDate dateToConvert) {
        return Date.from(dateToConvert.atStartOfDay()
                .atZone(ZoneId.systemDefault())
                .toInstant());
    }


    public static Date getStartOfDay(Date date) {
        LocalDateTime localDateTime = dateToLocalDateTime(date);
        LocalDateTime startOfDay = localDateTime.with(LocalTime.MIN);
        return localDateTimeToDate(startOfDay);
    }

    public static Date getMidNightOfDay(Date date) {
        LocalDateTime localDateTime = dateToLocalDateTime(date).plusDays(1);
        LocalDateTime startOfDay = localDateTime.with(LocalTime.MIDNIGHT);

        return localDateTimeToDate(startOfDay);
    }

    public static Date addTimeInDate(Date date, int hour, int minute, int second) {
        LocalDateTime localDateTime = dateToLocalDateTime(date);
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

    public static LocalDateTime getMondayFromWeek(int week, int year) {
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        return LocalDateTime.now()
                .withYear(year)
                .with(weekFields.weekOfYear(), week)
                .with(weekFields.dayOfWeek(), 2);
    }

    public static LocalDateTime getSundayFromWeek(int week, int year) {
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        return LocalDateTime.now()
                .withYear(year)
                .with(weekFields.weekOfYear(), week + 1l)
                .with(weekFields.dayOfWeek(), 1);
    }

    public static Date getDeductionInTimeDuration(Date startDate, Date endDate, int dayShiftPercentage, int nightShiftPercentage) {
        int percentage;
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
        c.add(Calendar.MINUTE, (int)reducedTime);

        return c.getTime();
    }


    public static Date convertToOnlyDate(String receivedDate, String dateFormat) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return simpleDateFormat.parse(receivedDate);
    }


    public static LocalDate getDateForUpcomingDay(LocalDate today, DayOfWeek dayOfWeek) {

        return today.with(TemporalAdjusters.next(dayOfWeek));

    }

    public static LocalDate getDateForPreviousDay(LocalDate today, DayOfWeek dayOfWeek) {

        return today.with(TemporalAdjusters.previous(dayOfWeek));

    }

    public static LocalTime toLocalTime(DateTime dateTime) {
        return LocalTime.of(dateTime.getHourOfDay(), dateTime.getMinuteOfHour());
    }

    public static LocalDate calcNextMonday(LocalDate today) {
        return today.with(TemporalAdjusters.next(DayOfWeek.MONDAY));
    }

    public static Long getTimeDuration(Date startDate, Date endDate) {
        long duration = endDate.getTime() - startDate.getTime();
        return TimeUnit.MILLISECONDS.toMinutes(duration);
    }

    public static Date getCurrentDate() {
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
        return date.with(firstInMonth(dayOfWeek)).plusWeeks(weekNumber - 1l);
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

    public static Date getDateFromLocalDate(LocalDate localDate) {
        return localDate != null
                ? Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant())
                : Date.from(LocalDate.now().atStartOfDay().toInstant(ZoneOffset.UTC));
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

    public static String asLocalDateString(Date date, String pattern) {
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate().format(DateTimeFormatter.ofPattern(pattern,Locale.ENGLISH));
    }

    public static LocalDate asLocalDate(Long date) {
        return Instant.ofEpochMilli(date).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static LocalDate asLocalDate(String receivedDate) {
        return LocalDate.parse(receivedDate, DateTimeFormatter.ISO_LOCAL_DATE);
    }



    public static int getWeekNoByLocalDate(LocalDate localDate){
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        return localDate.get(weekFields.weekOfWeekBasedYear());
    }
    public static LocalDate asLocalDate(DateTime dateTime){
        return asLocalDate(dateTime.toDate());
    }


    public static LocalTime asLocalTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()).toLocalTime();
    }

    public static Date asDate(LocalTime localTime) {
        Instant instant = localTime.atDate(LocalDate.now()).
                atZone(ZoneId.systemDefault()).toInstant();
        return Date.from(instant);
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

    private static Date add(final Date date, final int calendarField, final int amount) {
        if (date == null) {
            throw new IllegalArgumentException(THE_DATE_MUST_NOT_BE_NULL);
        }
        final Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(calendarField, amount);
        return c.getTime();
    }

    public static boolean isSameDay(final Date date1, final Date date2) {
        if (date1 == null || date2 == null) {
            throw new IllegalArgumentException(THE_DATE_MUST_NOT_BE_NULL);
        }
        final Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        final Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return isSameDay(cal1, cal2);
    }

    public static boolean isSameDay(final Calendar cal1, final Calendar cal2) {
        if (cal1 == null || cal2 == null) {
            throw new IllegalArgumentException(THE_DATE_MUST_NOT_BE_NULL);
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

    public static Date getDate(long millis) {
        return new Date(millis);
    }

    public static LocalDate getLocalDate(long millis) {
        return getLocalDateFromDate(getDate(millis));
    }

    public static LocalDate toLocalDate(DateTime date) {
        return Instant.ofEpochMilli(date.toDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static int getDifferenceBetweenDatesInDays(LocalDate startDate, LocalDate endDate, DurationType durationType) {
        switch (durationType) {
            case DAYS: return Period.between(startDate, endDate).getDays();
            case MINUTES: return (3600 * Period.between(startDate, endDate).getDays());
            case HOURS: return (60 * Period.between(startDate, endDate).getDays());
            default:break;
        }
        return Period.between(startDate, endDate).getDays();
    }

    public static DateTime toJodaDateTime(LocalDate localDate) {
        return new DateTime(localDate.getYear(), localDate.getMonthValue(), localDate.getDayOfMonth(), 0, 0);
    }

    public static String getDateString(Date date, String dateFormatString) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormatString);
        return simpleDateFormat.format(date);
    }

    public static String formatLocalDate(LocalDate localDate, String dateFormatString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormatString);
        return localDate.format(formatter);
    }

    public static Date getDateByLocalDateAndLocalTime(LocalDate localDate, LocalTime localTime) {
        return new DateTime(localDate.getYear(), localDate.getMonthValue(), localDate.getDayOfMonth(), localTime.getHour(), localTime.getMinute()).toDate();
    }

    public static Date getDateByLocalTime(Date date, LocalTime localTime) {
        return getDateByLocalDateAndLocalTime(asLocalDate(date),localTime);
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
        if(DAYS.equals(durationType)){
            return ChronoUnit.DAYS.between(startDate, endDate);
        }else {
            return null;
        }
    }

    public static LocalDateTime addDurationInLocalDateTime(LocalDateTime localDateTime, int duration, DurationType durationType, int recurringNumber) {
        switch (durationType) {
            case DAYS: return localDateTime.plusDays(duration * (long)recurringNumber);
            case WEEKS: return localDateTime.plusDays(duration * (long)recurringNumber * 7);
            case MONTHS: return localDateTime.plusMonths(duration * (long)recurringNumber);
            case HOURS: return localDateTime.plusHours(duration * (long)recurringNumber);
            case MINUTES: return localDateTime.plusMinutes(duration * (long)recurringNumber);
            default:break;


        }
        return localDateTime;
    }
    public static LocalDateTime substractDurationInLocalDateTime(LocalDateTime localDateTime, int duration, DurationType durationType) {
        switch (durationType) {
            case DAYS: return localDateTime.minusDays(duration);
            case HOURS: return localDateTime.minusHours(duration );
            case MINUTES: return localDateTime.minusMinutes(duration );
            default:break;
        }
        return localDateTime;
    }

    public static LocalDate addDurationInLocalDate(LocalDate localDate, int duration, DurationType durationType, int recurringNumber) {
        switch (durationType) {
            case DAYS: return localDate.plusDays(duration * (long)recurringNumber);
            case WEEKS: return localDate.plusDays(duration * (long)recurringNumber * 7);
            case MONTHS: return localDate.plusMonths(duration * (long)recurringNumber);
            case YEAR: return localDate.plusYears(duration * (long)recurringNumber);
            default:break;
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

        return Date.from(ZonedDateTime.ofInstant(date.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant(),
                ZoneId.systemDefault()).with(DayOfWeek.SUNDAY).toInstant());
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

    public static Date asDateStartOfDay(LocalDate localDate) {
        return Date.from(localDate.atTime(LocalTime.MIN).atZone(ZoneId.systemDefault()).toInstant());
    }

    public static Date asDate(LocalDate localDate, LocalTime localTime) {
        return Date.from(localDate.atTime(localTime).atZone(ZoneId.systemDefault()).toInstant());
    }

    public static Date getStartDateOfWeekFromDate(LocalDate date) {
        return asDate(date.with(previousOrSame(DayOfWeek.MONDAY)));

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

    public static LocalDateTime getLocalDateTimeFromZoneId(ZoneId unitTimeZone) {
        return LocalDateTime.now(unitTimeZone);
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

    public static Date getEndOfDayDateFromLocalDate(LocalDate localDate) {

        return Date.from(localDate.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant());
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
            LOGGER.error("error {}",e.getMessage());
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
        return Double.valueOf(Math.abs(hours)+"."+Math.abs(minutes));
    }

    public static Double getHoursByMinutes(double totalMinutes){
        Integer hour  = (int) totalMinutes/(60);
        Integer minutes = (int)totalMinutes % 60;
        return Double.valueOf(hour+"."+Math.abs(minutes));
    }

    public static int getHourByMinutes(double totalMinutes){
        return (int) totalMinutes/(60);
    }

    public static int getHourMinutesByMinutes(double totalMinutes){
        return (int)totalMinutes % 60;
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

    public static LocalDateTime getLocalDateTimeFromLocalDate(LocalDate localDate){
        return localDate.atStartOfDay();
    }

    public static LocalDateTime getLocalDateTimeFromLocalDateAndLocalTime(LocalDate localDate,LocalTime localTime){
        return LocalDateTime.of(localDate,localTime);
    }

    public static Date getDateFromTimeZone(String timeZone){
       return new Date(Timestamp.valueOf(LocalDateTime.now(ZoneId.of(timeZone))).getTime());
    }

    public static Date minusDays(Date date,int minusDays) {
        return DateUtils.asDate(DateUtils.asZoneDateTime(date).minusDays(minusDays));
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
                LOGGER.error("error {}",e.getMessage());
            }
        }
        return date;
    }


    public static Long getCurrentDateMillis() {
        DateTime date = new DateTime().withTime(0, 0, 0, 0);
        return date.getMillis();
    }
    public static Long getDateFromEpoch(LocalDate localDate) {
        return localDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();
    }

    public static String getLocalDateStringByPattern(LocalDate localDate, String pattern){
        return localDate.format(DateTimeFormatter.ofPattern(pattern));
    }

    public static String getLocalTimeStringByPattern(LocalTime localTime, String pattern) {
        return localTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    public static LocalDate getlastDayOfYear(Integer year){
        return LocalDate.of(year,1,1).with(TemporalAdjusters.lastDayOfYear());
    }

    public static LocalDate getlastDayOfYear(LocalDate localDate){
        return localDate.with(TemporalAdjusters.lastDayOfYear());
    }

    public static LocalDate getFirstDayOfMonth(LocalDate localDate){
        return localDate.withDayOfMonth(1);
    }

    public static LocalDate getFirstDayOfYear(Integer year){
        return LocalDate.of(year,1,1);
    }

    public static LocalDate getFirstDayOfNextYear(LocalDate localDate){
        return localDate.with(TemporalAdjusters.firstDayOfNextYear());
    }
    public static Date minusMonths(Date date,int minusMonths) {
        return DateUtils.asDate(DateUtils.asZoneDateTime(date).minusMonths(minusMonths));
    }
    public static Date parseDate(String date){
        DateTime dateTime = new DateTime(date);
        return dateTime.toDate();
    }

    public static LocalDate getNextLocaDateByDurationType(LocalDate date, DurationType durationType) {
        switch (durationType) {
            case MONTHS:
                date = date.with(TemporalAdjusters.lastDayOfMonth());
                break;
            case WEEKS:
                date = date.with(TemporalAdjusters.next(DayOfWeek.SUNDAY));
                break;
            case YEAR:
                date = date.with(TemporalAdjusters.lastDayOfYear());
                break;
            default:
                break;
        }
        return date;
    }

    public static LocalDate getPriviousLocaDateByDurationType(LocalDate date, DurationType durationType) {
        switch (durationType) {
            case MONTHS:
                date = date.with(TemporalAdjusters.firstDayOfMonth());
                break;
            case WEEKS:
                date = date.with(TemporalAdjusters.previous(DayOfWeek.MONDAY));
                break;
            case YEAR:
                date = date.with(TemporalAdjusters.firstDayOfYear());
                break;
            default:
                break;
        }
        return date;
    }

    public static LocalDate getLastLocaDateByDurationType(LocalDate date, DurationType durationType) {
        switch (durationType) {
            case DAYS:
                date = date.plusDays(1);
                break;
            case MONTHS:
                date = date.with(TemporalAdjusters.lastDayOfMonth());
                break;
            case WEEKS:
                date = date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
                break;
            case YEAR:
                date = date.with(TemporalAdjusters.lastDayOfYear());
                break;
            default:
                break;
        }
        return date;
    }

    public static LocalDate getFirstLocalDateByDurationType(LocalDate date, DurationType durationType) {
        switch (durationType) {
            case DAYS:
                break;
            case MONTHS:
                date = date.with(TemporalAdjusters.firstDayOfMonth());
                break;
            case WEEKS:
                date = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                break;
            case YEAR:
                date = date.with(TemporalAdjusters.firstDayOfYear());
                break;
            default:
                break;
        }
        return date;
    }

    public static LocalDate getLastDateByFrequencyType(DurationType durationType,LocalDate localDate){
        switch (durationType) {
            case DAYS:
                localDate = localDate.minusDays(1);
                break;
            case MONTHS:
                localDate = localDate.minusMonths(1).with(TemporalAdjusters.lastDayOfMonth());
                break;
            case WEEKS:
                localDate = localDate.with(TemporalAdjusters.previous(DayOfWeek.SUNDAY));
                break;
            case YEAR:
                localDate = localDate.minusYears(1).with(TemporalAdjusters.lastDayOfYear());
                break;
            default:
                break;
        }
        return localDate;
    }

    public static LocalDate getNextDateByFrequencyType(DurationType durationType,LocalDate localDate){
        switch (durationType) {
            case DAYS:
                localDate = localDate.plusDays(1);
                break;
            case MONTHS:
                localDate = localDate.plusMonths(1).with(TemporalAdjusters.firstDayOfMonth());
                break;
            case WEEKS:
                localDate = localDate.with(TemporalAdjusters.next(DayOfWeek.MONDAY));
                break;
            case YEAR:
                localDate = localDate.plusYears(1).with(TemporalAdjusters.firstDayOfYear());
                break;
            default:
                break;
        }
        return localDate;
    }

    public static String getDateTimeintervalString(DateTimeInterval dateTimeInterval){
        return  getLocalDateStringByPattern(dateTimeInterval.getStartLocalDate() ,KPI_DATE_FORMAT)+" - "+ getLocalDateStringByPattern(dateTimeInterval.getEndLocalDate(),KPI_DATE_FORMAT);
    }
    public static String getStartDateTimeintervalString(DateTimeInterval dateTimeInterval){
        return getLocalDateStringByPattern(dateTimeInterval.getStartLocalDate() ,KPI_DATE_FORMAT)+"";
    }

    public static String getLocalTimeByFormat(LocalDateTime localDateTime){
        return localDateTime.format(DateTimeFormatter.ofPattern(ISO_FORMAT));
    }

    public static long getMinutesBetweenDate(Date toDate,Date fromDate){
        return Duration.between(asLocalDateTime(toDate),asLocalDateTime(fromDate)).toMinutes();
    }
    public static boolean isEqualOrBefore(Date date1,Date date2){
        return date1.equals(date2) || date1.before(date2);
    }

    public static String getEmailDateTimeWithFormat(LocalDateTime dateTime){
        LocalTime time=getLocalTimeFromLocalDateTime(dateTime);
        String localtime=time.format(DateTimeFormatter.ofPattern(COMMON_TIME_FORMAT));
        return dateTime.getDayOfWeek().toString() +", "+ dateTime.getDayOfMonth()+" "+dateTime.getMonth()+" "+dateTime.getYear()+" "+localtime;
    }

    public static ZonedDateTime roundDateByMinutes(ZonedDateTime zonedDateTime,int minutes){
        return zonedDateTime.truncatedTo(ChronoUnit.HOURS).plusMinutes((int)Math.ceil((double)zonedDateTime.get(ChronoField.MINUTE_OF_HOUR)/minutes)* (long)minutes);
    }

    public static Date roundDateByMinutes(Date date,int minutes){
        ZonedDateTime zonedDateTime = asZoneDateTime(date);
        return asDate(zonedDateTime.truncatedTo(ChronoUnit.HOURS).plusMinutes((int)Math.round((double)zonedDateTime.get(ChronoField.MINUTE_OF_HOUR)/minutes)*(long)minutes));
    }
    public static Set<DayOfWeek> getAllDaysBetweenDays(DayOfWeek startDayOfWeek, DayOfWeek endDayOfWeek) {
        Set<DayOfWeek> dayOfWeeks = new HashSet<>();
        while (true){
            dayOfWeeks.add(startDayOfWeek);
            if(startDayOfWeek.equals(endDayOfWeek)){
                break;
            }
            startDayOfWeek=startDayOfWeek.plus(1);
        }
        return dayOfWeeks;
    }

    public static LocalDateTime getLocaDateTimebyString(String localdate){
        return LocalDateTime.parse(localdate,DateTimeFormatter.ofPattern(ISO_FORMAT));}

    public static boolean isEqualOrAfter(ZonedDateTime date1,ZonedDateTime date2){
        return date1.equals(date2) || date1.isAfter(date2);
    }

    public static boolean isEqualOrAfter(LocalDate date1,LocalDate date2){
        return date1.equals(date2) || date1.isAfter(date2);
    }

}

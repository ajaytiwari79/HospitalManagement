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


    public static Date convertToOnlyDate(String receivedDate, String dateFormat) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = simpleDateFormat.parse(receivedDate);
        return date;
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





}

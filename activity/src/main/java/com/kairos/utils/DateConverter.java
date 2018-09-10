package com.kairos.utils;

import org.joda.time.DateTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by prabjot on 29/11/16.
 */
public class DateConverter {

    public static final String ISO_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

    public static Date getDate(long time) {

        return new DateTime(time).toDate();
    }

    public static Date parseDate(String date) throws ParseException {

        DateTime dateTime = new DateTime(date);
        return dateTime.toDate();
    }

   public static Date convertToDate(String receivedDate) throws ParseException{
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date date = formatter.parse(receivedDate);
        return date;
    }

    public static Date convertInUTC(String utc){
       try {

           SimpleDateFormat dateFormatGmt = new SimpleDateFormat(ISO_FORMAT);
           dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
           SimpleDateFormat dateFormatLocal = new SimpleDateFormat(ISO_FORMAT);
           return dateFormatLocal.parse( dateFormatGmt.format(dateFormatGmt.parse(utc)) );

       }catch (Exception ex){
           return null;
       }
    }
}

package com.kairos.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.Months;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by oodles on 27/1/17.
 */
public  class Tst {





    Date date;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Tst{" +
                "date=" + date +
                '}';
    }

    public static void main(String[] agrs){

        /*List<Integer> list = Arrays.asList();
        Optional<Integer> a = list.stream()
                .findFirst();
        System.out.println(a.isPresent());


        Tst tst = new Tst();
        tst.setDate(DateUtil.getCurrentDate());

        System.out.println("to string " + tst.toString());

        ObjectMapper objectMapper = new ObjectMapper();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        objectMapper.setDateFormat(df);

        Map<String,Object> map = objectMapper.convertValue(tst,Map.class);

        System.out.println(map);




        int index = 3;
        int week = 7;
        int diff ;
        DateTime dateTime = DateTime.now();
        System.out.print("\nDate: "+ dateTime);
        System.out.print("\nDay: "+ dateTime.getDayOfWeek());

        diff = 7-dateTime.getDayOfWeek();
        System.out.print("\nDifference: "+diff);


        if (false) {
            System.out.print("\nPrepend");

            int result =   week-index;
            System.out.print("Minus days: "+result);
            dateTime = dateTime.minusDays(result);
            System.out.print("\nNew Date: "+dateTime);
            System.out.print("\nNew Day: "+dateTime.getDayOfWeek());

        }
        else {
            System.out.print("\nPostpone");

            int result =   week+index;
            result = 7-result;
            result  = Math.abs(result);
            System.out.print("plus days: "+result);
            dateTime = dateTime.plusDays(result);
            System.out.print("\nNew Date: "+dateTime);
            System.out.print("\nNew Day: "+dateTime.getDayOfWeek());


        }*/

        DateTime startDate = new DateTime();
        //DateTime endDate = startDate.plus(Months.months(2));
        // Ceates an interval from a start to an end instant.
        //Interval interval = new Interval(startDate, endDate);
        System.out.println("Interval = " + startDate);


    }
}

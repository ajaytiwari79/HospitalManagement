package com.kairos.service.cta_wta.template;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;

/**
 * Created by pawanmandhan on 17/8/17.
 */
public class A {


    public static void main(String[] args) throws ParseException {
        String time = "15:30";

        DateFormat sdf = new SimpleDateFormat("hh:mm");
        Date date = sdf.parse(time);

        System.out.println("Time: " + sdf.format(date));

        Duration dur = Duration.ofHours(date.getTime());

        System.out.println(dur.getSeconds());

    }




}

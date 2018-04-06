package com.kairos.activity.service.staffing_level;

import com.kairos.activity.util.DateUtils;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Date;

public class Test {
    public static void main(String ...args){
        int startTimeCounter=0;
        LocalTime startTime=LocalTime.MIN;
        System.out.print("start "+startTime.plusMinutes(startTimeCounter));

        System.out.print("end  "+startTime.plusMinutes(startTimeCounter+=15));
        System.out.print("start1  "+startTime.plusMinutes(startTimeCounter));
        System.out.print("end1 "+startTime.plusMinutes(startTimeCounter+=15));

    }
}

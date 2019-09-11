package com.kairos.service.time_bank;

import com.kairos.persistence.model.shift.Shift;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Interval;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class DailyTimeBankEntryServiceUnitTest {

    private static final Logger logger = LoggerFactory.getLogger(DailyTimeBankEntryServiceUnitTest.class);

    @Inject
    TimeBankCalculationService timeBankCalculationService;

    @InjectMocks
    TimeBankService timeBankService;


    List<Shift> shiftList = new ArrayList<>(3);
    Interval interval = null;

    

 //   @Test
    public void getIntervals(){
        Integer i = 2018;
        int dayCount = 1;
        List<Interval> intervals = new ArrayList<>(12);
        DateTime startDate = new DateTime().withYear(i).dayOfYear().withMinimumValue().withTimeAtStartOfDay();
        DateTime endDate = startDate.dayOfMonth().withMaximumValue().plusMinutes(1439);
        DateTime lastDateTimeOfYear = new DateTime().withYear(i).dayOfYear().withMaximumValue().withTimeAtStartOfDay().plusMinutes(1439);
        while (true){
            intervals.add(new Interval(startDate,endDate));
            startDate = endDate.plusMinutes(1);
            endDate = startDate.dayOfMonth().withMaximumValue().plusMinutes(1439);
            if(endDate.equals(lastDateTimeOfYear)){
                intervals.add(new Interval(startDate,lastDateTimeOfYear));
                break;
            }
        }
        logger.info("interval size "+intervals.size());
    }


    @Test
    public void getWeeklyIntervals(){
        Integer i = 2020;
        List<Interval> intervals = new ArrayList<>(60);
        DateTime startDate = new DateTime().withYear(i).dayOfYear().withMinimumValue().withTimeAtStartOfDay();
        DateTime endDate = startDate.plusMinutes(10079).withDayOfWeek(DateTimeConstants.SUNDAY);
        DateTime lastDateTimeOfYear = new DateTime().withYear(i).dayOfYear().withMaximumValue().withTimeAtStartOfDay().plusMinutes(1439);
        while (true){
            if(endDate.getYear()!=i){
                intervals.add(new Interval(startDate,lastDateTimeOfYear));
                break;
            }
            intervals.add(new Interval(startDate,endDate));
            startDate = endDate.plusMinutes(1);
            if(lastDateTimeOfYear.equals(endDate)){
                break;
            }
            endDate = startDate.plusMinutes(10079);
        }
        logger.info("interval size "+intervals.size());
    }


}

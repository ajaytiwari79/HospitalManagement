package com.kairos.activity.service.time_bank;

import com.kairos.activity.persistence.model.activity.Shift;
import com.kairos.activity.response.dto.time_bank.CalculatedTimeBankByDateDTO;
import com.kairos.activity.response.dto.time_bank.TimebankWrapper;
import com.kairos.activity.util.DateUtils;
import com.kairos.activity.util.time_bank.TimeBankCalculationService;
import org.joda.time.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.*;

@RunWith(MockitoJUnitRunner.class)
public class DailyTimeBankServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(DailyTimeBankServiceTest.class);

    @Inject
    TimeBankCalculationService timeBankCalculationService;

    @InjectMocks
    TimeBankService timeBankService;


    List<Shift> shiftList = new ArrayList<>(3);
    Interval interval = null;


    @Test
    public void getTimeBank(){

        DateTime startDateTime = new DateTime().withTimeAtStartOfDay();
        DateTime endDateTime = new DateTime().plusDays(7).withTimeAtStartOfDay();
        int days = (int)new Duration(startDateTime,endDateTime).getStandardDays()+1;
        Long startMillis = new Date().getTime();

        //timeBankService.setTimeBankCalculationService(new TimeBankCalculationService());
        List<CalculatedTimeBankByDateDTO> timeBanks = timeBankService.getTimeBankFromCurrentDateByUEP(145l);
        timeBanks.forEach(ct->{
            logger.info("date "+ct.getDate()+" - "+ct.getTimeBankMin());
        });
        logger.info("endTime "+(new Date().getTime()-startMillis));
    }

    @Test
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

    @Test
    public void getIntervalByDateOverviewTimebank(){
        /*int year  = 2018;
        TimebankWrapper timebankWrapper = new TimebankWrapper(DateUtils.asLocalDate(new DateTime().withYear(2018).minusDays(5).toDate()));
        DateTime startDate = new DateTime().withYear(year).dayOfYear().withMinimumValue().withTimeAtStartOfDay();
        DateTime endDate = new DateTime().withYear(year).dayOfYear().withMaximumValue().withTimeAtStartOfDay();
        if(startDate.getYear()==new DateTime(timebankWrapper.getUnitPositionDate()).getYear() && startDate.isBefore(DateUtils.toJodaDateTime(timebankWrapper.getUnitPositionDate()).withTimeAtStartOfDay())){
            startDate = DateUtils.toJodaDateTime(timebankWrapper.getUnitPositionDate()).withTimeAtStartOfDay();
        }
        if(startDate.getYear()!=DateUtils.toJodaDateTime(timebankWrapper.getUnitPositionDate()).getYear() && startDate.isAfter(DateUtils.toJodaDateTime(timebankWrapper.getUnitPositionDate()))){
            startDate = new DateTime().withYear(year).dayOfYear().withMinimumValue().withTimeAtStartOfDay();
        }
        if(endDate.isAfter(new DateTime().plusDays(1).withTimeAtStartOfDay()) && endDate.getYear()==new DateTime().getYear()){
            endDate = new DateTime().withTimeAtStartOfDay();
        }
        Interval interval = new Interval(startDate,endDate);*/
    }
    @Test
    public void getWeeksBYMonth(){
        logger.info("week "+new DateTime().dayOfWeek().withMaximumValue());
    }
}

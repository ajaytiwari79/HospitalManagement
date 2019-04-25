package com.kairos.service.time_bank;

import com.kairos.commons.utils.DateUtils;
import com.kairos.dto.activity.pay_out.EmploymentWithCtaDetailsDTO;
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
public class DailyTimeBankServiceUnitTest {

    private static final Logger logger = LoggerFactory.getLogger(DailyTimeBankServiceUnitTest.class);

    @Inject
    TimeBankCalculationService timeBankCalculationService;

    @InjectMocks
    TimeBankService timeBankService;


    List<Shift> shiftList = new ArrayList<>(3);
    Interval interval = null;




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
        //TODO Pradeep will fix it. as logic has been changed
        int year  = 2018;
        EmploymentWithCtaDetailsDTO employmentWithCtaDetailsDTO = new EmploymentWithCtaDetailsDTO(DateUtils.asLocalDate(new DateTime().withYear(2018).minusDays(5).toDate()));
        DateTime startDate = new DateTime().withYear(year).dayOfYear().withMinimumValue().withTimeAtStartOfDay();
        DateTime endDate = new DateTime().withYear(year).dayOfYear().withMaximumValue().withTimeAtStartOfDay();
        if(startDate.getYear()==new DateTime(employmentWithCtaDetailsDTO.getEmploymentStartDate()).getYear() && startDate.isBefore(DateUtils.toJodaDateTime(employmentWithCtaDetailsDTO.getEmploymentStartDate()).withTimeAtStartOfDay())){
            startDate = DateUtils.toJodaDateTime(employmentWithCtaDetailsDTO.getEmploymentStartDate()).withTimeAtStartOfDay();
        }
        if(startDate.getYear()!=DateUtils.toJodaDateTime(employmentWithCtaDetailsDTO.getEmploymentStartDate()).getYear() && startDate.isAfter(DateUtils.toJodaDateTime(employmentWithCtaDetailsDTO.getEmploymentStartDate()))){
            startDate = new DateTime().withYear(year).dayOfYear().withMinimumValue().withTimeAtStartOfDay();
        }
        if(endDate.isAfter(new DateTime().plusDays(1).withTimeAtStartOfDay()) && endDate.getYear()==new DateTime().getYear()){
            endDate = new DateTime().withTimeAtStartOfDay();
        }
        Interval interval = new Interval(startDate,endDate);
    }
    @Test
    public void getWeeksBYMonth(){
        logger.info("week "+new DateTime().dayOfWeek().withMaximumValue());
    }
}

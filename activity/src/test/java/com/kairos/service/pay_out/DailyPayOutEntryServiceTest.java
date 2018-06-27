package com.kairos.service.pay_out;

import com.kairos.persistence.model.activity.Shift;
import com.kairos.response.dto.pay_out.CalculatedPayOutByDateDTO;
import com.kairos.response.dto.pay_out.UnitPositionWithCtaDetailsDTO;
import com.kairos.util.DateUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Duration;
import org.joda.time.Interval;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class DailyPayOutEntryServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(DailyPayOutEntryServiceTest.class);

    @Inject
    PayOutCalculationService payOutCalculationService;

    @InjectMocks
    PayOutService payOutService;


    List<Shift> shiftList = new ArrayList<>(3);
    Interval interval = null;


    @Test
    public void getPayOut(){

        DateTime startDateTime = new DateTime().withTimeAtStartOfDay();
        DateTime endDateTime = new DateTime().plusDays(7).withTimeAtStartOfDay();
        int days = (int)new Duration(startDateTime,endDateTime).getStandardDays()+1;
        Long startMillis = new Date().getTime();

        //payOutService.setPayOutCalculationService(new PayOutCalculationService());
        List<CalculatedPayOutByDateDTO> payOuts = payOutService.getPayOutFromCurrentDateByUEP(145l);
        payOuts.forEach(ct->{
            logger.info("date "+ct.getDate()+" - "+ct.getPayOutMin());
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
    public void getIntervalByDateOverviewPayOut(){
        int year  = 2018;
        UnitPositionWithCtaDetailsDTO unitPositionWithCtaDetailsDTO = new UnitPositionWithCtaDetailsDTO(DateUtils.asLocalDate(new DateTime().withYear(2018).minusDays(5).toDate()));
        DateTime startDate = new DateTime().withYear(year).dayOfYear().withMinimumValue().withTimeAtStartOfDay();
        DateTime endDate = new DateTime().withYear(year).dayOfYear().withMaximumValue().withTimeAtStartOfDay();
        if(startDate.getYear()==new DateTime(unitPositionWithCtaDetailsDTO.getUnitPositionStartDate()).getYear() && startDate.isBefore(DateUtils.toJodaDateTime(unitPositionWithCtaDetailsDTO.getUnitPositionStartDate()).withTimeAtStartOfDay())){
            startDate = DateUtils.toJodaDateTime(unitPositionWithCtaDetailsDTO.getUnitPositionStartDate()).withTimeAtStartOfDay();
        }
        if(startDate.getYear()!=DateUtils.toJodaDateTime(unitPositionWithCtaDetailsDTO.getUnitPositionStartDate()).getYear() && startDate.isAfter(DateUtils.toJodaDateTime(unitPositionWithCtaDetailsDTO.getUnitPositionStartDate()))){
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

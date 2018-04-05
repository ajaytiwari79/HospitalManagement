package com.kairos.activity.service.time_bank;

import com.kairos.activity.persistence.model.activity.Activity;
import com.kairos.activity.persistence.model.activity.tabs.BalanceSettingsActivityTab;
import com.kairos.activity.persistence.model.time_bank.DailyTimeBank;
import com.kairos.activity.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.activity.response.dto.ActivityDTO;
import com.kairos.activity.response.dto.ShiftQueryResultWithActivity;
import com.kairos.activity.response.dto.time_bank.TimebankWrapper;
import com.kairos.activity.util.DateUtils;
import com.kairos.activity.util.time_bank.TimeBankCalculationService;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.format.DateTimeFormat;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class TImeBankCalculationServiceTest {


    private static final Logger logger = LoggerFactory.getLogger(TImeBankCalculationServiceTest.class);

    @InjectMocks
    TimeBankCalculationService timeBankCalculationService;

    //This is for Temp CTA
    @InjectMocks
    TimeBankService timeBankService;
    @Mock
    ActivityMongoRepository activityMongoRepository;

    List<ShiftQueryResultWithActivity> shifts = new ArrayList<>(3);
    Interval interval = null;
    Activity activity = null;

    @Before
    public void getMockShifts(){
        activity = new Activity(new BalanceSettingsActivityTab(new BigInteger("123")));
        activity.setId(new BigInteger("125"));
        DateTime startDate = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss").parseDateTime("22/02/2018 00:00:00");
        DateTime endDate = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss").parseDateTime("23/02/2018 00:00:00");
        interval = new Interval(startDate,endDate);
        ShiftQueryResultWithActivity shift = new ShiftQueryResultWithActivity(interval.getStart().minusHours(2).toDate(),interval.getStart().plusMinutes(120).toDate(),activity);
        shifts.add(shift);
        shift = new ShiftQueryResultWithActivity(interval.getStart().plusMinutes(240).toDate(),interval.getStart().plusMinutes(720).toDate(),activity);
        shifts.add(shift);
        shift = new ShiftQueryResultWithActivity(interval.getStart().plusMinutes(1020).toDate(),interval.getStart().plusMinutes(1560).toDate(),activity);
        shifts.add(shift);
    }

    @Test
    public void calculateTimeBank(){
        when(activityMongoRepository.findAllActivityByUnitId(Mockito.anyLong())).thenReturn(Arrays.asList(new ActivityDTO(activity.getId())));
        TimebankWrapper timebankWrapper = timeBankService.getCostTimeAgreement(1225l,95l);
        DailyTimeBank dailyTimeBank = new DailyTimeBank(timebankWrapper.getUnitPositionId(), timebankWrapper.getStaffId(), timebankWrapper.getWorkingDaysPerWeek(), DateUtils.asLocalDate(interval.getStart().toDate()));
        timeBankCalculationService.calculateDailyTimebank(interval, timebankWrapper,shifts,dailyTimeBank);
        Assert.assertEquals(dailyTimeBank.getTotalTimeBankMin(),1130);
        Assert.assertEquals(dailyTimeBank.getScheduledMin(),1020);
        Assert.assertEquals(dailyTimeBank.getContractualMin(),300);
    }



}

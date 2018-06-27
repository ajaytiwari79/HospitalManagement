package com.kairos.activity.service.time_bank;

import com.kairos.activity.persistence.model.activity.Activity;
import com.kairos.activity.persistence.model.activity.tabs.BalanceSettingsActivityTab;
import com.kairos.activity.persistence.model.time_bank.DailyTimeBankEntry;
import com.kairos.activity.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.activity.response.dto.ActivityDTO;
import com.kairos.activity.response.dto.ShiftWithActivityDTO;
import com.kairos.activity.response.dto.time_bank.UnitPositionWithCtaDetailsDTO;
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

    List<ShiftWithActivityDTO> shifts = new ArrayList<>(3);
    Interval interval = null;
    Activity activity = null;

    @Before
    public void getMockShifts(){
        activity = new Activity(new BalanceSettingsActivityTab(new BigInteger("123")));
        activity.setId(new BigInteger("125"));
        DateTime startDate = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss").parseDateTime("22/02/2018 00:00:00");
        DateTime endDate = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss").parseDateTime("23/02/2018 00:00:00");
        interval = new Interval(startDate,endDate);
        ShiftWithActivityDTO shift = new ShiftWithActivityDTO(interval.getStart().minusHours(2).toDate(),interval.getStart().plusMinutes(120).toDate(),activity);
        shifts.add(shift);
        shift = new ShiftWithActivityDTO(interval.getStart().plusMinutes(240).toDate(),interval.getStart().plusMinutes(720).toDate(),activity);
        shifts.add(shift);
        shift = new ShiftWithActivityDTO(interval.getStart().plusMinutes(1020).toDate(),interval.getStart().plusMinutes(1560).toDate(),activity);
        shifts.add(shift);
    }

    @Test
    public void calculateTimeBank(){
        when(activityMongoRepository.findAllActivityByUnitId(Mockito.anyLong())).thenReturn(Arrays.asList(new ActivityDTO(activity.getId())));
        UnitPositionWithCtaDetailsDTO unitPositionWithCtaDetailsDTO = null;//timeBankService.getCostTimeAgreement(1225l);
        DailyTimeBankEntry dailyTimeBankEntry = new DailyTimeBankEntry(unitPositionWithCtaDetailsDTO.getUnitPositionId(), unitPositionWithCtaDetailsDTO.getStaffId(), unitPositionWithCtaDetailsDTO.getWorkingDaysPerWeek(), DateUtils.asLocalDate(interval.getStart().toDate()));
        timeBankCalculationService.calculateDailyTimebank(interval, null,shifts, dailyTimeBankEntry);
        Assert.assertEquals(dailyTimeBankEntry.getTotalTimeBankMin(),1130);
        Assert.assertEquals(dailyTimeBankEntry.getScheduledMin(),1020);
        Assert.assertEquals(dailyTimeBankEntry.getContractualMin(),300);
    }



}

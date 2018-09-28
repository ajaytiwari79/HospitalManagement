package com.kairos.service.time_bank;

import com.kairos.dto.activity.activity.ActivityDTO;
import com.kairos.dto.activity.time_bank.time_bank_basic.time_bank.UnitPositionWithCtaDetailsDTO;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.activity.tabs.BalanceSettingsActivityTab;
import com.kairos.persistence.model.time_bank.DailyTimeBankEntry;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.commons.utils.DateUtils;
import com.kairos.utils.time_bank.TimeBankCalculationService;
import com.kairos.wrapper.shift.ShiftWithActivityDTO;
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

    }

    @Test
    public void calculateTimeBank(){

    }



}

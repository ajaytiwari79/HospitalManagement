package com.kairos.service.pay_out;

import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.wrapper.shift.ShiftWithActivityDTO;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import com.kairos.commons.utils.DateTimeInterval;


@RunWith(MockitoJUnitRunner.class)
public class PayOutCalculationServiceTest {


    private static final Logger logger = LoggerFactory.getLogger(PayOutCalculationServiceTest.class);

    @InjectMocks
    PayOutCalculationService payOutCalculationService;

    //This is for Temp CTA
    @InjectMocks
    PayOutService payOutService;
    @Mock
    ActivityMongoRepository activityMongoRepository;

    List<ShiftWithActivityDTO> shifts = new ArrayList<>(3);
    DateTimeInterval interval = null;
    Activity activity = null;

  /*  @Before
    public void getMockShifts(){
        activity = new Activity(new BalanceSettingsActivityTab(new BigInteger("123")));
        activity.setId(new BigInteger("125"));
        activity.setName("Activity1");
        activity.setParentId(new BigInteger("12"));
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
    public void calculatePayOut(){
        when(activityMongoRepository.findAllActivityByUnitId(Mockito.anyLong())).thenReturn(Arrays.asList(new ActivityDTO(activity.getId(), activity.getName(), activity.getParentId())));
        UnitPositionWithCtaDetailsDTO unitPositionWithCtaDetailsDTO = payOutService.getCostTimeAgreement(1225l);
        PayOut payOut = new PayOut(unitPositionWithCtaDetailsDTO.getPositionLineId(), unitPositionWithCtaDetailsDTO.getStaffId(), unitPositionWithCtaDetailsDTO.getWorkingDaysPerWeek(), DateUtils.asLocalDate(interval.getStart().toDate()));
        payOutCalculationService.calculateAndUpdatePayOut(interval, unitPositionWithCtaDetailsDTO,shifts, payOut);
        Assert.assertEquals(payOut.getTotalPayOutMin(),1130);
        Assert.assertEquals(payOut.getScheduledMin(),1020);
        Assert.assertEquals(payOut.getContractualMin(),300);
    }*/



}

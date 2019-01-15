package com.kairos.service.staffing_level;
/*
 *Created By Pavan on 16/8/18
 *
 */

import com.kairos.config.env.EnvConfig;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.model.staffing_level.StaffingLevel;
import com.kairos.dto.activity.staffing_level.Duration;
import com.kairos.dto.activity.staffing_level.StaffingLevelInterval;
import com.kairos.dto.activity.staffing_level.StaffingLevelSetting;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.staffing_level.StaffingLevelMongoRepository;
import com.kairos.service.phase.PhaseService;
import com.kairos.commons.utils.DateUtils;
import com.kairos.utils.event.ShiftNotificationEvent;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@RunWith(MockitoJUnitRunner.class)
public class StaffingLevelServiceUnitTest {
    @InjectMocks
    private StaffingLevelService staffingLevelService;

    //mocking all dependency
    @Mock
    private StaffingLevelMongoRepository staffingLevelMongoRepository;
    @Mock
    private PhaseService phaseService;
    @Mock
    private EnvConfig envConfig;
    @Mock
    private OrganizationRestClient organizationRestClient;
    @Mock
    ActivityMongoRepository activityMongoRepository;
    StaffingLevel staffingLevel=null;

    @Before
    public void setUp() throws Exception {
        Duration duration=new Duration(LocalTime.MIN,LocalTime.MAX);
        StaffingLevelSetting staffingLevelSetting=new StaffingLevelSetting(15,duration);
        LocalDate date = LocalDate.now();
        TemporalField woy = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear();
        int currentWeekCount = date.get(woy);
        staffingLevel=new StaffingLevel(DateUtils.getDate(),1,new Long("2567"),new BigInteger("1"),staffingLevelSetting);
        List<StaffingLevelInterval> StaffingLevelIntervals=new ArrayList<>();
        int startTimeCounter=0;
        LocalTime startTime=LocalTime.MIN;
        for(int i=0;i<=95;i++){
            StaffingLevelInterval staffingLevelInterval=new StaffingLevelInterval(i,0,0,new Duration(startTime.plusMinutes(startTimeCounter),
                    startTime.plusMinutes(startTimeCounter+=15)));

            staffingLevelInterval.setAvailableNoOfStaff(0);
            StaffingLevelIntervals.add(staffingLevelInterval);
        }
        staffingLevel.setPresenceStaffingLevelInterval(StaffingLevelIntervals);

    }

    @Test
    public void  updateStaffingLevelAvailableStaffCountForNewlyCreatedShiftTest(){
        ShiftNotificationEvent shiftNotificationEvent=new ShiftNotificationEvent();
        Shift shift =new Shift();
        //shift.setStartDate(DateUtils.getDateFromLocalDate(LocalTime.MIN));
        //shift.setEndDate(DateUtils.getDateFromLocalDate(LocalTime.MAX));
        shiftNotificationEvent.setShift(shift);
        StaffingLevel staffingLevel1=staffingLevelService.updateStaffingLevelAvailableStaffCountForNewlyCreatedShift(staffingLevel,shiftNotificationEvent);
        staffingLevel1.getPresenceStaffingLevelInterval().stream().forEach(staffingLevelInterval -> Assert.assertEquals(1L,staffingLevelInterval.getAvailableNoOfStaff()));

    }

    @Test
    public void  updateStaffingLevelAvailableStaffCountForUpdatedShiftTest(){

        ShiftNotificationEvent shiftNotificationEvent=new ShiftNotificationEvent();
        Shift shift =new Shift();
        //   shift.setStartDate(DateUtils.getDateFromLocalDate(LocalTime.MIN));
        // shift.setEndDate(DateUtils.getDateFromLocalDate(LocalTime.MAX));
        shiftNotificationEvent.setShift(shift);
        StaffingLevel updatedStaffingLevel1=staffingLevelService.updateStaffingLevelAvailableStaffCountForNewlyCreatedShift(staffingLevel,shiftNotificationEvent);
        Shift updatedShift=new Shift();
        //updatedShift.setStartDate(DateUtils.getDateFromLocalDate(LocalTime.MIN));
        //updatedShift.setEndDate(DateUtils.getDateFromLocalDate(LocalTime.NOON));
        shiftNotificationEvent.setPreviousStateShift(shift);
        shiftNotificationEvent.setShift(updatedShift);
        Object[] array;
        List<StaffingLevel> updatedStaffingLevel2=staffingLevelService.updateStaffingLevelAvailableStaffCountForUpdatedShift(Arrays.asList(updatedStaffingLevel1),shiftNotificationEvent);
        updatedStaffingLevel2.forEach(staffingLevel->{
            staffingLevel.getPresenceStaffingLevelInterval().forEach(staffingLevelInterval -> {
                        if(staffingLevelInterval.getStaffingLevelDuration().getFrom().compareTo(LocalTime.NOON)<0){
                            Assert.assertEquals(1L,staffingLevelInterval.getAvailableNoOfStaff());
                        }
                    }
            );
        });

    }

    @After
    public void tearDown() throws Exception {

    }

}

package com.kairos.service.staffing_level;
/*
 *Created By Pavan on 16/8/18
 *
 */

import com.kairos.config.env.EnvConfig;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.model.shift.ShiftActivity;
import com.kairos.persistence.model.staffing_level.StaffingLevel;
import com.kairos.dto.activity.staffing_level.Duration;
import com.kairos.dto.activity.staffing_level.StaffingLevelInterval;
import com.kairos.dto.activity.staffing_level.StaffingLevelSetting;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.staffing_level.StaffingLevelMongoRepository;
import com.kairos.service.phase.PhaseService;
import com.kairos.commons.utils.DateUtils;
import com.kairos.utils.event.ShiftNotificationEvent;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
        shift.setActivities(Arrays.asList(new ShiftActivity("Senior Day Activity",DateUtils.asDate(LocalDateTime.now()),DateUtils.asDate(LocalDateTime.now().plusMinutes(120)),new BigInteger("11"))));
        shift.setStartDate(shift.getActivities().get(0).getStartDate());
        shift.setEndDate(shift.getActivities().get(shift.getActivities().size()-1).getEndDate());
        shiftNotificationEvent.setShift(shift);
        StaffingLevel staffingLevel1=staffingLevelService.updateStaffingLevelAvailableStaffCountForNewlyCreatedShift(staffingLevel,shiftNotificationEvent);
        LocalTime shiftStartTime=DateUtils.getLocalTimeFromLocalDateTime(DateUtils.dateToLocalDateTime(shift.getStartDate()));
        LocalTime shiftEndTime=DateUtils.getLocalTimeFromLocalDateTime(DateUtils.dateToLocalDateTime(shift.getEndDate()));
        staffingLevel1.getPresenceStaffingLevelInterval().forEach(staffingLevelInterval -> {
            if(staffingLevelInterval.getStaffingLevelDuration().getFrom().isBefore(staffingLevelInterval.getStaffingLevelDuration().getTo()) && (staffingLevelInterval.getStaffingLevelDuration().getFrom().equals(shiftStartTime) || staffingLevelInterval.getStaffingLevelDuration().getFrom().isAfter(shiftStartTime)) &&
                    (staffingLevelInterval.getStaffingLevelDuration().getTo().equals(shiftEndTime) || staffingLevelInterval.getStaffingLevelDuration().getTo().isBefore(shiftEndTime))){
                Assert.assertEquals(1L,staffingLevelInterval.getAvailableNoOfStaff());
            }

        });

    }

    @Test
    @Ignore
    //Todo Yatharth please check and resolve the issue of this test case
    public void  updateStaffingLevelAvailableStaffCountForUpdatedShiftTest(){

        ShiftNotificationEvent shiftNotificationEvent=new ShiftNotificationEvent();
        Shift shift =new Shift();
        shift.setActivities(Arrays.asList(new ShiftActivity("Senior Day Activity",DateUtils.asDate(LocalDateTime.now()),DateUtils.asDate(LocalDateTime.now().plusMinutes(300)),new BigInteger("11"))));
        shift.setStartDate(shift.getActivities().get(0).getStartDate());
        shift.setEndDate(shift.getActivities().get(shift.getActivities().size()-1).getEndDate());
        shiftNotificationEvent.setShift(shift);
        StaffingLevel updatedStaffingLevel1=staffingLevelService.updateStaffingLevelAvailableStaffCountForNewlyCreatedShift(staffingLevel,shiftNotificationEvent);
        Shift updatedShift=new Shift();
        updatedShift.setActivities(Arrays.asList(new ShiftActivity("Senior Day Activity",DateUtils.asDate(LocalDateTime.now()),DateUtils.asDate(LocalDateTime.now().plusMinutes(300)),new BigInteger("11"))));
        updatedShift.setStartDate(updatedShift.getActivities().get(0).getStartDate());
        updatedShift.setEndDate(updatedShift.getActivities().get(updatedShift.getActivities().size()-1).getEndDate());
        shiftNotificationEvent.setPreviousStateShift(shift);
        shiftNotificationEvent.setShift(updatedShift);
        Object[] array;
        List<StaffingLevel> updatedStaffingLevel2=staffingLevelService.updateStaffingLevelAvailableStaffCountForUpdatedShift(Arrays.asList(updatedStaffingLevel1),shiftNotificationEvent);
        LocalTime shiftStartTime=DateUtils.getLocalTimeFromLocalDateTime(DateUtils.dateToLocalDateTime(updatedShift.getStartDate()));
        LocalTime shiftEndTime=DateUtils.getLocalTimeFromLocalDateTime(DateUtils.dateToLocalDateTime(updatedShift.getEndDate()));
        updatedStaffingLevel2.forEach(staffingLevel->{
            staffingLevel.getPresenceStaffingLevelInterval().forEach(staffingLevelInterval -> {
                        if(staffingLevelInterval.getStaffingLevelDuration().getFrom().isBefore(staffingLevelInterval.getStaffingLevelDuration().getTo()) && (staffingLevelInterval.getStaffingLevelDuration().getFrom().equals(shiftStartTime) || staffingLevelInterval.getStaffingLevelDuration().getFrom().isAfter(shiftStartTime)) &&
                                (staffingLevelInterval.getStaffingLevelDuration().getTo().equals(shiftEndTime) || staffingLevelInterval.getStaffingLevelDuration().getTo().isBefore(shiftEndTime)) && staffingLevelInterval.getStaffingLevelDuration().getFrom().compareTo(LocalTime.NOON)<0){
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

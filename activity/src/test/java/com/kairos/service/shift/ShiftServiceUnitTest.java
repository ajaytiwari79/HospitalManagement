package com.kairos.service.shift;

import com.kairos.dto.activity.shift.ButtonConfig;
import com.kairos.dto.activity.shift.ShiftActivityDTO;
import com.kairos.dto.activity.shift.ShiftDTO;
import com.kairos.dto.user.access_permission.AccessGroupRole;
import com.kairos.enums.phase.PhaseDefaultName;
import com.kairos.enums.shift.ShiftStatus;
import com.kairos.persistence.model.phase.Phase;
import com.kairos.persistence.model.shift.ShiftActivity;
import com.kairos.persistence.model.shift.ShiftState;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.phase.PhaseMongoRepository;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.persistence.repository.shift.ShiftStateMongoRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.phase.PhaseService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigInteger;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

import static junit.framework.TestCase.assertTrue;
import static org.mockito.Mockito.when;

/**
 * Created by vipul on 19/1/18.
 */
@RunWith(MockitoJUnitRunner.class)
public class ShiftServiceUnitTest {
    public static final String TIME_ATTENDANCE = "TIME & ATTENDANCE";
    @InjectMocks
    private ShiftStateService shiftStateService;
    @InjectMocks
    private ShiftValidatorService shiftValidatorService;

    @Mock
    private PhaseMongoRepository phaseMongoRepository;
    @Mock
    private UserIntegrationService userIntegrationService;
    @Mock
    private ShiftStateMongoRepository shiftStateMongoRepository;
    @Mock
    private ShiftMongoRepository shiftMongoRepository;
    @Mock
    private ActivityMongoRepository activityMongoRepository;
    @Mock private PhaseService phaseService;
    public ShiftDTO shiftDTO;
    public ShiftActivity activity;
    public ShiftActivityDTO activity1;
    public String timeZone;
    public ShiftState shiftState;
    public boolean realtime=true;
    boolean thrown=true;

    /**
     * This method is being used to check the all shift is not validated. So it should return false
     * send to payroll button will be shown only if all shifts is validated
     */
    @Test
    public void findButtonConfigForSendToPayrollNegativeCase() {
        LocalDate startdate = LocalDate.now();
        startdate = startdate.with(TemporalAdjusters.next(DayOfWeek.MONDAY));
        LocalDate enddate=startdate.plusDays(7);
        List<ShiftDTO> shifts = new ArrayList<>();
        ShiftDTO shift = new ShiftDTO(BigInteger.valueOf(13870L),new Date(2018,11,19,13,0),new Date(2018,11,19,16,0),35602L,14139L);
        shifts.add(shift);
        shift = new ShiftDTO(BigInteger.valueOf(13879L),new Date(2018,11,21,15,0),new Date(2018,11,21,20,0),35602L,14139L);
        shifts.add(shift);
        shift = new ShiftDTO(BigInteger.valueOf(13880L),new Date(2018,11,21,15,0),new Date(2018,11,21,21,0),35602L,18752L);
        shifts.add(shift);
        shift = new ShiftDTO(BigInteger.valueOf(13562L),new Date(2018,11,22,9,0),new Date(2018,11,22,14,0),35602L,32545L);
        shifts.add(shift);
        Set<BigInteger> shiftIds = shifts.stream().map(shiftDTO -> shiftDTO.getId()).collect(Collectors.toSet());
        when(shiftStateMongoRepository.getCountByShiftIdInAndAccessGroupRoleAndValidatedNotNull(shiftIds,AccessGroupRole.MANAGEMENT)).thenReturn(1l);
        ButtonConfig buttonConfig = shiftStateService.findButtonConfig(shifts,true);
        Assert.assertEquals(buttonConfig.isSendToPayrollEnabled(),false);;
    }
    @Test
    public void findButtonConfigForSendToPayrollPositiveCase() {
        LocalDate startdate = LocalDate.now();
        startdate = startdate.with(TemporalAdjusters.next(DayOfWeek.MONDAY));
        LocalDate enddate=startdate.plusDays(7);
        List<ShiftDTO> shifts = new ArrayList<>();
        ShiftDTO shift = new ShiftDTO(BigInteger.valueOf(13879L),new Date(2018,11,21,15,0),new Date(2018,11,21,20,0),35602L,14139L);
        shifts.add(shift);
        shift = new ShiftDTO(BigInteger.valueOf(13880L),new Date(2018,11,21,15,0),new Date(2018,11,21,21,0),35602L,18752L);
        shifts.add(shift);
        Set<BigInteger> shiftIds = shifts.stream().map(shiftDTO -> shiftDTO.getId()).collect(Collectors.toSet());
        when(shiftStateMongoRepository.getCountByShiftIdInAndAccessGroupRoleAndValidatedNotNull(shiftIds,AccessGroupRole.MANAGEMENT)).thenReturn(2l);
        ButtonConfig buttonConfig = shiftStateService.findButtonConfig(shifts,true);
        Assert.assertEquals(buttonConfig.isSendToPayrollEnabled(),true);;
    }

    @Test
    public void validateRealTimeShift(){
        timeZone="Asia/Kolkata";
        Long unitId=958l;
        Phase phase=new Phase();
        phase.setPhaseEnum(PhaseDefaultName.REALTIME);
        phase.setRealtimeDuration(5);
        phase.setId(BigInteger.valueOf(69));
        Map<String,Phase> phaseMap=new HashMap<>();
        phaseMap.put(phase.getPhaseEnum().toString(),phase);
        updateShiftActivity();
        shiftState=new ShiftState();
        shiftState.setActivities(Arrays.asList(activity));
        updateShiftDTO();
        when(userIntegrationService.getTimeZoneByUnitId(unitId)).thenReturn(timeZone);
        when(shiftStateMongoRepository.findShiftStateByShiftIdAndActualPhase(shiftDTO.getShiftId(), phaseMap.get(PhaseDefaultName.REALTIME.toString()).getId())).thenReturn(shiftState);
        when(phaseService.shiftEditableInRealtime(timeZone,phaseMap,shiftDTO.getActivities().get(0).getStartDate(),shiftDTO.getActivities().get(shiftDTO.getActivities().size()-1).getEndDate())).thenReturn(realtime);
        try {
            shiftValidatorService.validateRealTimeShift(unitId,shiftDTO,phaseMap);
        }catch (Exception e){
            thrown=false;
        }
        assertTrue(thrown);

    }

    private void updateShiftDTO() {
        shiftDTO=new ShiftDTO();
        shiftDTO.setId(BigInteger.valueOf(93));
        shiftDTO.setUnitId(958l);
        shiftDTO.setStaffId(834l);
        shiftDTO.setEmploymentId(1136l);
        shiftDTO.setActivities(Arrays.asList(activity1));
        shiftDTO.setScheduledMinutes(360);
        shiftDTO.setDurationMinutes(360);
        shiftDTO.setEditable(true);
        shiftDTO.setFunctionDeleted(false);
        shiftDTO.setShiftStatePhaseId(BigInteger.valueOf(69));
        shiftDTO.setShiftDate(LocalDate.of(2018,11,28));
        shiftDTO.setShiftId(BigInteger.valueOf(354));
    }

    private void updateShiftActivity() {
        activity=new ShiftActivity();
        activity.setId(new BigInteger("12"));
        activity.setActivityId(BigInteger.valueOf(47));
        activity.setStartDate(new Date(2018,10,28,12,30));
        activity.setEndDate(new Date(2018,10,28,18,00));
        activity.setScheduledMinutes(360);
        activity.setScheduledMinutes(360);
        activity.setActivityName("12 Bronze");
        activity.setRemarks("");
        activity.setTimeType("WORKING_TYPE");
        activity.setBackgroundColor("");
        Set<ShiftStatus> status=new HashSet<>();
        status.add(ShiftStatus.UNPUBLISH);
        activity.setStatus(status);
        activity.setStatus(new HashSet<>());
        activity1=new ShiftActivityDTO();
        activity1.setActivityId(BigInteger.valueOf(47));
        activity1.setStartDate(new Date(2018,10,28,12,30));
        activity1.setEndDate(new Date(2018,10,28,18,10));
        activity1.setScheduledMinutes(360);
        activity1.setScheduledMinutes(360);
        activity1.setActivityName("12 Bronze");
        activity1.setRemarks("");
        activity1.setTimeType("WORKING_TYPE");
        activity1.setBackgroundColor("");
        activity1.setHaltBreak(false);
        activity1.setBreakShift(false);
        status.add(ShiftStatus.UNPUBLISH);
        activity1.setStatus(status);
        activity1.setStatus(new HashSet<>());
    }
}
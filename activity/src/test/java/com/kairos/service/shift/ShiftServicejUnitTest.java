package com.kairos.service.shift;

import com.kairos.dto.activity.shift.ButtonConfig;
import com.kairos.dto.activity.shift.ShiftActivityDTO;
import com.kairos.persistence.model.shift.ShiftActivity;
import com.kairos.dto.activity.shift.ShiftDTO;
import com.kairos.dto.user.access_permission.AccessGroupRole;
import com.kairos.enums.phase.PhaseDefaultName;
import com.kairos.enums.shift.ShiftStatus;
import com.kairos.persistence.model.phase.Phase;
import com.kairos.persistence.model.shift.ShiftState;
import com.kairos.persistence.repository.phase.PhaseMongoRepository;
import com.kairos.persistence.repository.shift.ShiftStateMongoRepository;
import com.kairos.rest_client.GenericIntegrationService;
import com.kairos.service.phase.PhaseService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.Mockito.when;

/**
 * Created by vipul on 19/1/18.
 */
@RunWith(MockitoJUnitRunner.class)
public class ShiftServicejUnitTest {
    private final Logger logger = LoggerFactory.getLogger(ShiftServicejUnitTest.class);
    @InjectMocks
    private ShiftService shiftService;

    @Mock
    private PhaseMongoRepository phaseMongoRepository;
    @Mock
    private GenericIntegrationService genericIntegrationService;
    @Mock
    private ShiftStateMongoRepository shiftStateMongoRepository;
    @Mock private PhaseService phaseService;
    public Phase phase;
    public ShiftDTO shiftDTO;
    public ShiftActivity activity;
    public ShiftActivityDTO activity1;
    public String timeZone;
    public ShiftState shiftState;
    public boolean realtime=true;
    boolean thrown=true;

    @Test
    public void findButtonConfigForSendToPayrollNegativeCase() {
        Date startDate = new Date(2018,11,19);
        Date endDate = new Date(2018,11,25);
        List<ShiftDTO> shifts = new ArrayList<>();

        ShiftDTO shift = new ShiftDTO(BigInteger.valueOf(13870L),new Date(2018,11,19,13,0),new Date(2018,11,19,16,0),35602L,14139L);
        shifts.add(shift);
        shift = new ShiftDTO(BigInteger.valueOf(13879L),new Date(2018,11,21,15,0),new Date(2018,11,21,20,0),35602L,14139L);
        shifts.add(shift);
        shift = new ShiftDTO(BigInteger.valueOf(13880L),new Date(2018,11,21,15,0),new Date(2018,11,21,21,0),35602L,18752L);
        shifts.add(shift);
        shift = new ShiftDTO(BigInteger.valueOf(13562L),new Date(2018,11,22,9,0),new Date(2018,11,22,14,0),35602L,32545L);
        shifts.add(shift);

        List<ShiftState> shiftStates = new ArrayList<>();
        ShiftState shiftState = new ShiftState(BigInteger.valueOf(13879L),AccessGroupRole.MANAGEMENT,"TIME & ATTENDANCE",LocalDate.of(2018,11,21),new Date(2018,11,21,15,0),new Date(2018,11,21,20,0),35602L,14139L);
        shiftStates.add(shiftState);
        Set<BigInteger> shiftIds = shifts.stream().map(shiftDTO -> shiftDTO.getId()).collect(Collectors.toSet());

        when(shiftStateMongoRepository.findAllByShiftIdInAndAccessGroupRoleAndValidatedNotNull(shiftIds,AccessGroupRole.MANAGEMENT)).thenReturn(shiftStates);

        ButtonConfig buttonConfig = shiftService.findButtonConfig(shifts,startDate,endDate,true);
        Assert.assertEquals(buttonConfig.isSendToPayrollEnabled(),false);;


    }


    @Test
    public void findButtonConfigForSendToPayrollPositiveCase() {
        Date startDate = new Date(2018,11,19);
        Date endDate = new Date(2018,11,25);
        List<ShiftDTO> shifts = new ArrayList<>();

        ShiftDTO shift = new ShiftDTO(BigInteger.valueOf(13879L),new Date(2018,11,21,15,0),new Date(2018,11,21,20,0),35602L,14139L);
        shifts.add(shift);
        shift = new ShiftDTO(BigInteger.valueOf(13880L),new Date(2018,11,21,15,0),new Date(2018,11,21,21,0),35602L,18752L);
        shifts.add(shift);


        List<ShiftState> shiftStates = new ArrayList<>();
        ShiftState shiftState = new ShiftState(BigInteger.valueOf(13879L),AccessGroupRole.MANAGEMENT,"TIME & ATTENDANCE",LocalDate.of(2018,11,21),new Date(2018,11,21,15,0),new Date(2018,11,21,20,0),35602L,14139L);
        shiftStates.add(shiftState);
        shiftState = new ShiftState(BigInteger.valueOf(13880L),AccessGroupRole.MANAGEMENT,"TIME & ATTENDANCE",LocalDate.of(2018,11,21),new Date(2018,11,21,15,0),new Date(2018,11,21,21,0),35602L,14139L);
        shiftStates.add(shiftState);
        Set<BigInteger> shiftIds = shifts.stream().map(shiftDTO -> shiftDTO.getId()).collect(Collectors.toSet());

        when(shiftStateMongoRepository.findAllByShiftIdInAndAccessGroupRoleAndValidatedNotNull(shiftIds,AccessGroupRole.MANAGEMENT)).thenReturn(shiftStates);

        ButtonConfig buttonConfig = shiftService.findButtonConfig(shifts,startDate,endDate,true);
        Assert.assertEquals(buttonConfig.isSendToPayrollEnabled(),true);;


    }

    @Test
    public void validateRealTimeShift(){
        timeZone="Asia/Kolkata";
        Long unitId=958l;
        phase=new Phase();
        phase.setPhaseEnum(PhaseDefaultName.REALTIME);
        phase.setRealtimeDuration(5);
        phase.setId(BigInteger.valueOf(69));
        Map<String,Phase> phaseMap=new HashMap<>();
        phaseMap.put(phase.getPhaseEnum().toString(),phase);
        activity=new ShiftActivity();
        activity.setActivityId(BigInteger.valueOf(47));
        activity.setStartDate(new Date(2018,10,28,12,30));
        activity.setEndDate(new Date(2018,10,28,18,00));
        activity.setScheduledMinutes(360);
        activity.setScheduledMinutes(360);
        activity.setActivityName("12 Bronze");
        activity.setpId(0);
        activity.setBid(0);
        activity.setRemarks("");
        activity.setTimeType("WORKING_TYPE");
        activity.setBackgroundColor("");
        activity.setHaltBreak(false);
        activity.setBreakShift(false);
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
        activity1.setpId(0);
        activity1.setBid(0);
        activity1.setRemarks("");
        activity1.setTimeType("WORKING_TYPE");
        activity1.setBackgroundColor("");
        activity1.setHaltBreak(false);
        activity1.setBreakShift(false);
        status.add(ShiftStatus.UNPUBLISH);
        activity1.setStatus(status);
        activity1.setStatus(new HashSet<>());
        shiftState=new ShiftState();
        shiftState.setActivities(Arrays.asList(activity));
        shiftDTO=new ShiftDTO();
        shiftDTO.setId(BigInteger.valueOf(93));
        shiftDTO.setBid(0l);
        shiftDTO.setpId(0l);
        shiftDTO.setAmount(0);
        shiftDTO.setProbability(0);
        shiftDTO.setUnitId(958l);
        shiftDTO.setStaffId(834l);
        shiftDTO.setUnitPositionId(1136l);
        shiftDTO.setActivities(Arrays.asList(activity1));
        shiftDTO.setScheduledMinutes(360);
        shiftDTO.setDurationMinutes(360);
        shiftDTO.setEditable(true);
        shiftDTO.setFunctionDeleted(false);
        shiftDTO.setShiftStatePhaseId(BigInteger.valueOf(69));
        shiftDTO.setShiftDate(LocalDate.of(2018,11,28));
        shiftDTO.setShiftId(BigInteger.valueOf(354));
        when(phaseMongoRepository.findByUnitIdAndName(958l,PhaseDefaultName.REALTIME.toString())).thenReturn(phase);
        when(genericIntegrationService.getTimeZoneByUnitId(unitId)).thenReturn(timeZone);
        when(shiftStateMongoRepository.findShiftStateByShiftIdAndActualPhase(shiftDTO.getShiftId(), phaseMap.get(PhaseDefaultName.REALTIME.toString()).getId())).thenReturn(shiftState);
        when(phaseService.shiftEdititableInRealtime(timeZone,phaseMap,shiftDTO.getActivities().get(0).getStartDate(),shiftDTO.getActivities().get(shiftDTO.getActivities().size()-1).getEndDate())).thenReturn(realtime);
        try {
            shiftService.validateRealTimeShift(unitId,shiftDTO,phaseMap);
        }catch (Exception e){
        thrown=false;
        }
        assertTrue(thrown);

    }

}

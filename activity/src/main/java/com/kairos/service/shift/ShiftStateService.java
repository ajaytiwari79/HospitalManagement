package com.kairos.service.shift;

import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.user.access_permission.AccessGroupRole;
import com.kairos.enums.phase.PhaseDefaultName;
import com.kairos.persistence.model.phase.Phase;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.model.shift.ShiftActivity;
import com.kairos.persistence.model.shift.ShiftState;
import com.kairos.persistence.repository.common.MongoSequenceRepository;
import com.kairos.persistence.repository.phase.PhaseMongoRepository;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.persistence.repository.shift.ShiftStateMongoRepository;
import com.kairos.service.exception.ExceptionService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.kairos.enums.phase.PhaseType.ACTUAL;

/*
 *Created By Pavan on 21/12/18
 *
 */
@Service
public class ShiftStateService {

    @Inject
    private ExceptionService exceptionService;
    @Inject
    private ShiftMongoRepository shiftMongoRepository;
    @Inject
    private PhaseMongoRepository phaseMongoRepository;
    @Inject
    private ShiftStateMongoRepository shiftStateMongoRepository;
    @Inject
    private MongoSequenceRepository mongoSequenceRepository;

    public boolean createShiftState(Long unitId, Date startDate, Date endDate){
        if(!startDate.before(DateUtils.getCurrentDayStart()) || !endDate.before(DateUtils.getCurrentDayStart())){
            exceptionService.actionNotPermittedException("past.date.allowed");
        }
        List<Shift> shifts=shiftMongoRepository.findShiftBetweenDurationAndUnitIdAndDeletedFalse(startDate,endDate,unitId);
        createShiftState(shifts,false,unitId);
        return true;
    }

    public void createShiftState(List<Shift> shifts,boolean checkIn,Long unitId){
        List<Phase> phases=phaseMongoRepository.findByOrganizationIdAndPhaseTypeAndDeletedFalse(unitId, ACTUAL.toString());
        List<ShiftState> realtimeShiftStates=new ArrayList<>();
        List<ShiftState> timeAndAttendanceShiftStates=null;
        List<ShiftState> oldRealtimeShiftStates=shiftStateMongoRepository.findShiftStateByShiftIdsAndPhaseId(shifts.stream().map(s->s.getId()).collect(Collectors.toList()),phases.stream().filter(phase -> phase.getPhaseEnum().equals(PhaseDefaultName.REALTIME)).findFirst().get().getId());
        realtimeShiftStates=createRealTimeShiftState(realtimeShiftStates,oldRealtimeShiftStates,shifts,phases.stream().filter(phase -> phase.getPhaseEnum().equals(PhaseDefaultName.REALTIME)).findFirst().get().getId());
        if( !realtimeShiftStates.isEmpty()) {
             shiftMongoRepository.saveEntities(realtimeShiftStates);
          }
        if(!checkIn) {
            timeAndAttendanceShiftStates = createTimeAndAttendanceShiftState(timeAndAttendanceShiftStates, oldRealtimeShiftStates, shifts, phases.stream().filter(phase -> phase.getPhaseEnum().equals(PhaseDefaultName.TIME_ATTENDANCE)).findFirst().get().getId());
            if (!timeAndAttendanceShiftStates.isEmpty())
                shiftStateMongoRepository.saveEntities(timeAndAttendanceShiftStates);
        }
    }

    public List<ShiftState> createRealTimeShiftState(List<ShiftState> realtimeShiftStates,List<ShiftState> oldRealtimeShiftStates,List<Shift> shifts,BigInteger phaseId){
        Map<BigInteger,ShiftState> realtimeShiftStateMap=oldRealtimeShiftStates.stream().collect(Collectors.toMap(k->k.getShiftId(), v->v));
        ShiftState realtimeShiftState;
        for (Shift shift:shifts) {
            if (realtimeShiftStateMap.get(shift.getId()) == null&&!DateUtils.asLocalDate(shift.getStartDate()).isAfter(DateUtils.getCurrentLocalDate())) {
                realtimeShiftState = ObjectMapperUtils.copyPropertiesByMapper(shift, ShiftState.class);
                realtimeShiftState.setId(null);
                realtimeShiftState.setShiftId(shift.getId());
                realtimeShiftState.setShiftStatePhaseId(phaseId);
                realtimeShiftState.getActivities().forEach(a -> a.setId(mongoSequenceRepository.nextSequence(ShiftActivity.class.getSimpleName())));
                realtimeShiftStates.add(realtimeShiftState);
            }
        }
        return realtimeShiftStates;
    }

    public List<ShiftState> createTimeAndAttendanceShiftState(List<ShiftState> timeAndAttendanceShiftStates,List<ShiftState> realtimeShiftStates,List<Shift> shifts,BigInteger phaseId){
        ShiftState timeAndAttendanceShiftState;
        timeAndAttendanceShiftStates=shiftStateMongoRepository.findShiftStateByShiftIdsAndPhaseId(shifts.stream().map(shift -> shift.getId()).collect(Collectors.toList()),phaseId);
        Map<BigInteger,ShiftState> realtimeShiftStateMap=realtimeShiftStates.stream().collect(Collectors.toMap(k->k.getShiftId(),v->v));
        Map<BigInteger,ShiftState> timeAndAttendanceShiftStateMap=timeAndAttendanceShiftStates.stream().filter(shiftState -> shiftState.getShiftStatePhaseId().equals(phaseId)&&shiftState.getAccessGroupRole().equals(AccessGroupRole.STAFF)).collect(Collectors.toMap(k->k.getShiftId(), v->v));
        for (Shift shift:shifts) {
            if (timeAndAttendanceShiftStateMap.containsKey(shift.getId())) {
                ShiftState existingTimeAttendanceShiftState = timeAndAttendanceShiftStateMap.get(shift.getId());
                ShiftState realTimeShiftState = realtimeShiftStateMap.get(shift.getId());
                ShiftState timeAttendanceShiftState = ObjectMapperUtils.copyPropertiesByMapper(realTimeShiftState,ShiftState.class);
                timeAttendanceShiftState.setShiftStatePhaseId(existingTimeAttendanceShiftState.getShiftStatePhaseId());
                timeAttendanceShiftState.setAccessGroupRole(existingTimeAttendanceShiftState.getAccessGroupRole());
                timeAttendanceShiftState.setId(existingTimeAttendanceShiftState.getId());
                timeAttendanceShiftState.getActivities().forEach(a -> a.setId(mongoSequenceRepository.nextSequence(ShiftActivity.class.getSimpleName())));
                timeAndAttendanceShiftStates.add(timeAttendanceShiftState);
            } else {
                if (realtimeShiftStateMap.get(shift.getId()) != null) {
                    timeAndAttendanceShiftState = ObjectMapperUtils.copyPropertiesByMapper(realtimeShiftStateMap.get(shift.getId()), ShiftState.class);
                }else{
                    timeAndAttendanceShiftState = ObjectMapperUtils.copyPropertiesByMapper(shift, ShiftState.class);
                    timeAndAttendanceShiftState.setShiftId(shift.getId());
                }
                timeAndAttendanceShiftState.setId(null);
                timeAndAttendanceShiftState.setShiftStatePhaseId(phaseId);
                timeAndAttendanceShiftState.setAccessGroupRole(AccessGroupRole.STAFF);
                timeAndAttendanceShiftState.getActivities().forEach(a -> a.setId(mongoSequenceRepository.nextSequence(ShiftActivity.class.getSimpleName())));
                timeAndAttendanceShiftStates.add(timeAndAttendanceShiftState);
            }
        }
        return timeAndAttendanceShiftStates;
    }
}

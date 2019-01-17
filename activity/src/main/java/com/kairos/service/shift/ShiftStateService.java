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
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;
import java.util.function.Function;
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
//        if(!startDate.before(DateUtils.getCurrentDayStart()) || !endDate.before(DateUtils.getCurrentDayStart())){
//            exceptionService.actionNotPermittedException("past.date.allowed");
//        }
        List<Shift> shifts=shiftMongoRepository.findShiftBetweenDurationAndUnitIdAndDeletedFalse(startDate,endDate,unitId);
        createShiftState(shifts,false,unitId);
        return true;
    }

    public void createShiftState(List<Shift> shifts,boolean checkIn,Long unitId){
        List<Phase> phases=phaseMongoRepository.findByOrganizationIdAndDeletedFalse(unitId);
        Map<String, Phase> phaseMap = phases.stream().collect(Collectors.toMap(p->p.getPhaseEnum().toString(), Function.identity()));
        List<ShiftState> newShiftState=new ArrayList<>();
        List<ShiftState> timeAndAttendanceShiftStates=null;
        List<ShiftState> oldRealtimeShiftStates=shiftStateMongoRepository.findShiftStateByShiftIdsAndPhaseId(shifts.stream().map(s->s.getId()).collect(Collectors.toList()),phaseMap.get(PhaseDefaultName.REALTIME.toString()).getId());
        newShiftState=createRealTimeShiftState(newShiftState,oldRealtimeShiftStates,shifts,phaseMap.get(PhaseDefaultName.REALTIME.toString()).getId());
        newShiftState.addAll(createDraftShiftState(newShiftState,shifts,phaseMap.get(PhaseDefaultName.DRAFT.toString()).getId()));
        if( !newShiftState.isEmpty()) {
             shiftMongoRepository.saveEntities(newShiftState);
          }
        if(!checkIn) {
            timeAndAttendanceShiftStates = createTimeAndAttendanceShiftState(timeAndAttendanceShiftStates, oldRealtimeShiftStates, shifts, phaseMap.get(PhaseDefaultName.TIME_ATTENDANCE.toString()).getId());
            if (!timeAndAttendanceShiftStates.isEmpty())
                shiftStateMongoRepository.saveEntities(timeAndAttendanceShiftStates);
        }
    }

    public List<ShiftState> createRealTimeShiftState(List<ShiftState> realtimeShiftStates,List<ShiftState> oldRealtimeShiftStates,List<Shift> shifts,BigInteger phaseId){
        Map<BigInteger,ShiftState> realtimeShiftStateMap= CollectionUtils.isNotEmpty(oldRealtimeShiftStates)?oldRealtimeShiftStates.stream().collect(Collectors.toMap(k->k.getShiftId(), v->v)):new HashMap<>();
        ShiftState realtimeShiftState;
        for (Shift shift:shifts) {
            if (realtimeShiftStateMap.get(shift.getId()) == null&&!DateUtils.asLocalDate(shift.getStartDate()).isAfter(DateUtils.getCurrentLocalDate())) {
                realtimeShiftState = ObjectMapperUtils.copyPropertiesByMapper(shift, ShiftState.class);
                realtimeShiftState.setId(null);
                realtimeShiftState.setShiftId(shift.getId());
                realtimeShiftState.setShiftStatePhaseId(phaseId);
                realtimeShiftState.setStartDate(realtimeShiftState.getActivities().get(0).getStartDate());
                realtimeShiftState.setEndDate(realtimeShiftState.getActivities().get(realtimeShiftState.getActivities().size()-1).getEndDate());
                realtimeShiftState.getActivities().forEach(a -> a.setId(mongoSequenceRepository.nextSequence(ShiftActivity.class.getSimpleName())));
                realtimeShiftStates.add(realtimeShiftState);
            }
        }
        return realtimeShiftStates;
    }

    public List<ShiftState> createDraftShiftState(List<ShiftState> draftShiftStates,List<Shift> shifts,BigInteger phaseId){
        List<ShiftState> oldDraftShiftStates=shiftStateMongoRepository.findShiftStateByShiftIdsAndPhaseId(shifts.stream().map(s->s.getId()).collect(Collectors.toList()),phaseId);
        Map<BigInteger,ShiftState> draftShiftStateMap=CollectionUtils.isNotEmpty(oldDraftShiftStates)?oldDraftShiftStates.stream().collect(Collectors.toMap(k->k.getShiftId(), v->v)):new HashMap<>();
        ShiftState draftShiftState;
        for (Shift shift:shifts) {
            if (draftShiftStateMap.get(shift.getId()) == null&&!DateUtils.asLocalDate(shift.getStartDate()).isAfter(DateUtils.getCurrentLocalDate())) {
                draftShiftState = ObjectMapperUtils.copyPropertiesByMapper(shift, ShiftState.class);
                draftShiftState.setId(null);
                draftShiftState.setShiftId(shift.getId());
                draftShiftState.setStartDate(draftShiftState.getActivities().get(0).getStartDate());
                draftShiftState.setEndDate(draftShiftState.getActivities().get(draftShiftState.getActivities().size()-1).getEndDate());
                draftShiftState.setShiftStatePhaseId(phaseId);
                draftShiftState.getActivities().forEach(a -> a.setId(mongoSequenceRepository.nextSequence(ShiftActivity.class.getSimpleName())));
                draftShiftStates.add(draftShiftState);
            }
        }
        return draftShiftStates;
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

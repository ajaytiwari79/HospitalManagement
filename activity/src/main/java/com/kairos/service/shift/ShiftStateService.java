package com.kairos.service.shift;

import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.cta.CTAResponseDTO;
import com.kairos.dto.activity.cta.CTARuleTemplateDTO;
import com.kairos.dto.activity.shift.ShiftDTO;
import com.kairos.dto.user.access_permission.AccessGroupRole;
import com.kairos.dto.user.user.staff.StaffAdditionalInfoDTO;
import com.kairos.enums.phase.PhaseDefaultName;
import com.kairos.persistence.model.activity.ActivityWrapper;
import com.kairos.persistence.model.phase.Phase;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.model.shift.ShiftActivity;
import com.kairos.persistence.model.shift.ShiftState;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.common.MongoSequenceRepository;
import com.kairos.persistence.repository.cta.CostTimeAgreementRepository;
import com.kairos.persistence.repository.phase.PhaseMongoRepository;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.persistence.repository.shift.ShiftStateMongoRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.cta.CostTimeAgreementService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.time_bank.TimeBankService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.kairos.enums.phase.PhaseType.ACTUAL;
import static com.kairos.utils.worktimeagreement.RuletemplateUtils.setDayTypeToCTARuleTemplate;
import static java.util.stream.Collectors.groupingBy;

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
    @Inject private TimeBankService timeBankService;
    @Inject private CostTimeAgreementRepository costTimeAgreementRepository;
    @Inject private ActivityMongoRepository activityMongoRepository;
    @Inject private UserIntegrationService userIntegrationService;

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
        newShiftState.addAll(createDraftShiftState(new ArrayList<>(),shifts,phaseMap.get(PhaseDefaultName.DRAFT.toString()).getId()));
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

    public ShiftDTO updateShiftStateAfterValidatingWtaRule(ShiftDTO shiftDTO, BigInteger shiftStateId, BigInteger shiftStatePhaseId) {
        shiftDTO.setShiftStatePhaseId(shiftStatePhaseId);
        ShiftState shiftState = shiftStateMongoRepository.findOne(shiftStateId);
        if (shiftState != null) {
            shiftDTO.setId(shiftState.getId());
            shiftDTO.setAccessGroupRole(shiftState.getAccessGroupRole());
            shiftDTO.setValidated(shiftState.getValidated());
            shiftDTO.setShiftId(shiftState.getShiftId());
            shiftDTO.setPhaseId(shiftState.getPhaseId());
            shiftDTO.setStartDate(shiftDTO.getActivities().get(0).getStartDate());
            shiftDTO.setEndDate(shiftDTO.getActivities().get(shiftState.getActivities().size() - 1).getEndDate());
        }
        shiftState = ObjectMapperUtils.copyPropertiesByMapper(shiftDTO, ShiftState.class);
        shiftMongoRepository.save(shiftState);
        return shiftDTO;
    }

    public List<ShiftState> checkAndCreateRealtimeAndDraftState(List<Shift> shifts, List<ShiftState> shiftStates, Map<String, Phase> phaseMap) {
        List<ShiftState> newShiftStates = new ArrayList<>();
        newShiftStates = createRealTimeShiftState(newShiftStates, shiftStates, shifts, phaseMap.get(PhaseDefaultName.REALTIME.toString()).getId());
        newShiftStates.addAll(createDraftShiftState(new ArrayList<>(), shifts, phaseMap.get(PhaseDefaultName.DRAFT.toString()).getId()));
        if (!newShiftStates.isEmpty()) shiftStateMongoRepository.saveEntities(newShiftStates);
        return newShiftStates;
    }

    public void updateShiftDailyTimeBankAndPaidOut(List<Shift> shifts, List<Shift> shiftsList, Long unitId) {
        if (!Optional.ofNullable(shifts).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.shift.ids");
        }
        List<Long> staffIds = shifts.stream().map(shift -> shift.getStaffId()).collect(Collectors.toList());
        List<Long> unitPositionIds = shifts.stream().map(shift -> shift.getUnitPositionId()).collect(Collectors.toList());
        List<NameValuePair> requestParam = new ArrayList<>();
        requestParam.add(new BasicNameValuePair("staffIds", staffIds.toString()));
        requestParam.add(new BasicNameValuePair("unitPositionIds", unitPositionIds.toString()));
        List<StaffAdditionalInfoDTO> staffAdditionalInfoDTOS = userIntegrationService.getStaffAditionalDTOS(unitId, requestParam);
        List<BigInteger> activityIdsList = shifts.stream().flatMap(s -> s.getActivities().stream().map(ShiftActivity::getActivityId)).distinct().collect(Collectors.toList());
        List<ActivityWrapper> activities = activityMongoRepository.findActivitiesAndTimeTypeByActivityId(activityIdsList);
        Map<BigInteger, ActivityWrapper> activityWrapperMap = activities.stream().collect(Collectors.toMap(k -> k.getActivity().getId(), v -> v));
        shifts.sort(Comparator.comparing(Shift::getStartDate));
        shiftsList.sort((shift, shiftSecond) -> shift.getStartDate().compareTo(shiftSecond.getStartDate()));
        Date startDate = shifts.get(0).getStartDate();
        Date endDate = shifts.get(shifts.size() - 1).getEndDate();
        Date shiftStartDate = shiftsList.get(0).getStartDate();
        Date shiftEndDate = shiftsList.get(shiftsList.size() - 1).getEndDate();
        startDate = startDate.before(shiftStartDate) ? startDate : shiftStartDate;
        endDate = endDate.after(shiftEndDate) ? endDate : shiftEndDate;
        List<CTAResponseDTO> ctaResponseDTOS = costTimeAgreementRepository.getCTAByUnitPositionIdsAndDate(unitPositionIds, startDate, endDate);
        Map<Long, List<CTAResponseDTO>> unitPositionAndCTAResponseMap = ctaResponseDTOS.stream().collect(groupingBy(CTAResponseDTO::getUnitPositionId));
        staffAdditionalInfoDTOS.forEach(staffAdditionalInfoDTO -> {
            if (unitPositionAndCTAResponseMap.get(staffAdditionalInfoDTO.getUnitPosition().getId()) != null) {
                List<CTAResponseDTO> ctaResponseDTOSList = unitPositionAndCTAResponseMap.get(staffAdditionalInfoDTO.getUnitPosition().getId());
                List<CTARuleTemplateDTO> ctaRuleTemplateDTOS = ctaResponseDTOSList.stream().flatMap(ctaResponseDTO -> ctaResponseDTO.getRuleTemplates().stream()).collect(Collectors.toList());
                staffAdditionalInfoDTO.getUnitPosition().setCtaRuleTemplates(ctaRuleTemplateDTOS);
                setDayTypeToCTARuleTemplate(staffAdditionalInfoDTO);
            }
        });
        timeBankService.saveTimeBanksAndPayOut(staffAdditionalInfoDTOS, shifts, activityWrapperMap, startDate, endDate);

    }
}

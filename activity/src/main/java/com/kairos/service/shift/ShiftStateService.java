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
import com.kairos.service.attendence_setting.TimeAndAttendanceService;
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

import static com.kairos.commons.utils.DateUtils.*;
import static com.kairos.commons.utils.ObjectUtils.isNotNull;
import static com.kairos.commons.utils.ObjectUtils.isNull;
import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.constants.ActivityMessagesConstants.MESSAGE_SHIFT_IDS;
import static com.kairos.constants.ActivityMessagesConstants.PAST_DATE_ALLOWED;
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
    @Inject private ShiftService shiftService;
    @Inject private TimeAndAttendanceService timeAndAttendanceService;

    public boolean sendShiftInTimeAndAttendancePhase(Long unitId, Date startDate, Date endDate,Long staffId){
        if(!startDate.before(DateUtils.getCurrentDayStart()) || !endDate.before(DateUtils.getCurrentDayStart())){
            exceptionService.actionNotPermittedException(PAST_DATE_ALLOWED);
        }
        timeAndAttendanceService.checkOutBySchedulerJob(unitId, startDate, endDate,staffId);
        return true;
    }

    public void createShiftState(List<Shift> shifts,boolean checkIn,Long unitId){
        List<Phase> phases=phaseMongoRepository.findByOrganizationIdAndDeletedFalse(unitId);
        Map<String, Phase> phaseMap = phases.stream().collect(Collectors.toMap(p->p.getPhaseEnum().toString(), Function.identity()));
        List<ShiftState> newShiftState=new ArrayList<>();
        List<ShiftState> oldRealtimeShiftStates=shiftStateMongoRepository.findShiftStateByShiftIdsAndPhaseId(shifts.stream().map(s->s.getId()).collect(Collectors.toList()),phaseMap.get(PhaseDefaultName.REALTIME.toString()).getId());
        newShiftState=createRealTimeShiftState(newShiftState,oldRealtimeShiftStates,shifts,phaseMap.get(PhaseDefaultName.REALTIME.toString()).getId());
        newShiftState.addAll(createDraftShiftState(new ArrayList<>(),shifts,phaseMap.get(PhaseDefaultName.DRAFT.toString()).getId()));
        if( !newShiftState.isEmpty()) {
             shiftMongoRepository.saveEntities(newShiftState);
          }
        if(!checkIn) {
            List<ShiftState> timeAndAttendanceShiftStates = createTimeAndAttendanceShiftState( new ArrayList<>(), shifts, phaseMap.get(PhaseDefaultName.TIME_ATTENDANCE.toString()).getId());
            deleteDraftShiftsViaTimeAndAttendanceJob(unitId);
            if (!timeAndAttendanceShiftStates.isEmpty())
                shiftStateMongoRepository.saveEntities(timeAndAttendanceShiftStates);
        }
    }

    public List<ShiftState> createRealTimeShiftState(List<ShiftState> realtimeShiftStates,List<ShiftState> oldRealtimeShiftStates,List<Shift> shifts,BigInteger phaseId){
        Map<BigInteger,ShiftState> realtimeShiftStateMap= CollectionUtils.isNotEmpty(oldRealtimeShiftStates)?oldRealtimeShiftStates.stream().collect(Collectors.toMap(k->k.getShiftId(), v->v)):new HashMap<>();
        getShiftStateLists(realtimeShiftStates, shifts, phaseId, realtimeShiftStateMap,true);
        return realtimeShiftStates;
    }

    public List<ShiftState> createDraftShiftState(List<ShiftState> draftShiftStates,List<Shift> shifts,BigInteger phaseId){
        List<ShiftState> oldDraftShiftStates=shiftStateMongoRepository.findShiftStateByShiftIdsAndPhaseId(shifts.stream().map(s->s.getId()).collect(Collectors.toList()),phaseId);
        Map<BigInteger,ShiftState> draftShiftStateMap=CollectionUtils.isNotEmpty(oldDraftShiftStates)?oldDraftShiftStates.stream().collect(Collectors.toMap(k->k.getShiftId(), v->v)):new HashMap<>();
        getShiftStateLists(draftShiftStates, shifts, phaseId, draftShiftStateMap,false);
        return draftShiftStates;
    }

    public List<ShiftState> createTimeAndAttendanceShiftState(List<ShiftState> timeAndAttendanceShiftStates,List<Shift> shifts,BigInteger phaseId){
        List<ShiftState> oldTimeAndAttendanceShiftStates=shiftStateMongoRepository.findShiftStateByShiftIdsAndPhaseId(shifts.stream().map(shift -> shift.getId()).collect(Collectors.toList()),phaseId);
        Map<BigInteger,ShiftState> timeAndAttendanceShiftStateMap=oldTimeAndAttendanceShiftStates.stream().filter(shiftState -> shiftState.getShiftStatePhaseId().equals(phaseId)&&shiftState.getAccessGroupRole().equals(AccessGroupRole.STAFF)).collect(Collectors.toMap(k->k.getShiftId(), v->v));
        getShiftStateLists(timeAndAttendanceShiftStates, shifts, phaseId, timeAndAttendanceShiftStateMap,false);
        timeAndAttendanceShiftStates.stream().forEach(shiftState -> shiftState.setAccessGroupRole(AccessGroupRole.STAFF));
        return timeAndAttendanceShiftStates;
    }

    private void getShiftStateLists(List<ShiftState> shiftStates, List<Shift> shifts, BigInteger phaseId, Map<BigInteger, ShiftState> shiftStateMap , boolean isRealTime) {
        ShiftState shiftState;
        boolean shiftUpdated =false;
        for (Shift shift:shifts) {
            if (!DateUtils.asLocalDate(shift.getStartDate()).isAfter(DateUtils.getCurrentLocalDate())) {
                ShiftState oldshiftState=shiftStateMap.get(shift.getId());
                if(isNotNull(oldshiftState) && isRealTime ){
                    shiftUpdated = shift.isShiftUpdated(ObjectMapperUtils.copyPropertiesByMapper(oldshiftState, Shift.class));
                }
                if(isNull(oldshiftState) || shiftUpdated) {
                    shiftState = ObjectMapperUtils.copyPropertiesByMapper(shift, ShiftState.class);
                    shiftState.setId((shiftStateMap.containsKey(shift.getId())) ? shiftStateMap.get(shift.getId()).getId() : null);
                    shiftState.setStartDate(shiftState.getActivities().get(0).getStartDate());
                    shiftState.setEndDate(shiftState.getActivities().get(shiftState.getActivities().size() - 1).getEndDate());
                    shiftState.setShiftId(shift.getId());
                    shiftState.getActivities().forEach(a -> a.setId(mongoSequenceRepository.nextSequence(ShiftActivity.class.getSimpleName())));
                    shiftState.setShiftStatePhaseId(phaseId);
                    shiftState.setDraftShift(null);
                    shiftStates.add(shiftState);
                }
            }
        }
    }



    public void deleteDraftShiftsViaTimeAndAttendanceJob(Long unitId){
        List<Shift> draftShifts = shiftMongoRepository.findDraftShiftBetweenDurationAndUnitIdAndDeletedFalse(getStartOfDay(asDate(getLocalDate().plusDays(1))),asDate(getEndOfDayFromLocalDateTime().plusDays(1)),unitId);
        shiftService.deleteDraftShiftAndViolatedRules(draftShifts);
    }

    public ShiftDTO updateShiftStateAfterValidatingWtaRule(ShiftDTO shiftDTO, BigInteger shiftStateId, BigInteger shiftStatePhaseId) {
        shiftDTO.setShiftStatePhaseId(shiftStatePhaseId);
        shiftDTO.setShiftId(shiftStateId);
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
            exceptionService.dataNotFoundByIdException(MESSAGE_SHIFT_IDS);
        }
        List<Long> staffIds = shifts.stream().map(shift -> shift.getStaffId()).collect(Collectors.toList());
        List<Long> employmentIds = shifts.stream().map(shift -> shift.getEmploymentId()).collect(Collectors.toList());
        List<NameValuePair> requestParam = new ArrayList<>();
        requestParam.add(new BasicNameValuePair("staffIds", staffIds.toString()));
        requestParam.add(new BasicNameValuePair("employmentIds", employmentIds.toString()));
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
        List<CTAResponseDTO> ctaResponseDTOS = costTimeAgreementRepository.getCTAByEmploymentIdsAndDate(employmentIds, startDate, endDate);
        Map<Long, List<CTAResponseDTO>> employmentAndCTAResponseMap = ctaResponseDTOS.stream().collect(groupingBy(CTAResponseDTO::getEmploymentId));
        staffAdditionalInfoDTOS.forEach(staffAdditionalInfoDTO -> {
            if (employmentAndCTAResponseMap.get(staffAdditionalInfoDTO.getEmployment().getId()) != null) {
                List<CTAResponseDTO> ctaResponseDTOSList = employmentAndCTAResponseMap.get(staffAdditionalInfoDTO.getEmployment().getId());
                List<CTARuleTemplateDTO> ctaRuleTemplateDTOS = ctaResponseDTOSList.stream().flatMap(ctaResponseDTO -> ctaResponseDTO.getRuleTemplates().stream()).collect(Collectors.toList());
                staffAdditionalInfoDTO.getEmployment().setCtaRuleTemplates(ctaRuleTemplateDTOS);
                setDayTypeToCTARuleTemplate(staffAdditionalInfoDTO);
            }
        });
        timeBankService.saveTimeBanksAndPayOut(staffAdditionalInfoDTOS, shifts, activityWrapperMap, startDate, endDate);

    }


    public List<ShiftState> findAllByShiftIdsByAccessgroupRole(Set<BigInteger> shiftIds, Set<String> accessGroupRole){
        return shiftStateMongoRepository.findAllByShiftIdsByAccessgroupRole(shiftIds,accessGroupRole);
    }
}

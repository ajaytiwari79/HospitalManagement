package com.kairos.service.shift;

import com.kairos.commons.utils.DateUtils;
import com.kairos.dto.activity.shift.*;
import com.kairos.dto.user.reason_code.ReasonCodeDTO;
import com.kairos.dto.user.reason_code.ReasonCodeWrapper;
import com.kairos.enums.shift.ShiftStatus;
import com.kairos.enums.shift.TodoStatus;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.model.shift.ShiftActivity;
import com.kairos.persistence.model.todo.Todo;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.persistence.repository.todo.TodoRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.phase.PhaseService;
import com.kairos.service.reason_code.ReasonCodeService;
import com.kairos.service.unit_settings.ActivityConfigurationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.*;
import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.constants.ActivityMessagesConstants.SICK_ACTIVITY_NOT_FOUND;
import static com.kairos.enums.shift.ShiftType.SICK;


/**
 * CreatedBy vipulpandey on 17/12/18
 **/
@Service
@Transactional
public class ShiftDetailsService {

    @Inject
    private ShiftMongoRepository shiftMongoRepository;
    @Inject
    private UserIntegrationService userIntegrationService;
    @Inject
    private PhaseService phaseService;
    @Inject
    private ActivityConfigurationService activityConfigurationService;
    @Inject
    private ShiftService shiftService;
    @Inject
    private TodoRepository todoRepository;
    @Inject
    private ActivityMongoRepository activityMongoRepository;
    @Inject
    private ReasonCodeService reasonCodeService;
    @Inject
    private ExceptionService exceptionService;

    public List<ShiftWithActivityDTO> shiftDetailsById(Long unitId, List<BigInteger> shiftIds, boolean showDraft) {
        List<ShiftWithActivityDTO> shiftWithActivityDTOS;
        if (showDraft) {
            List<ShiftWithActivityDTO> originalShift = shiftMongoRepository.findAllShiftsByIds(shiftIds);
            Map<BigInteger, Boolean> shiftIdAndOriginalShiftMap = originalShift.stream().collect(Collectors.toMap(k -> k.getId(), v -> (!v.isDraft() && isNotNull(v.getDraftShift()))));
            shiftWithActivityDTOS = new ArrayList<>(shiftMongoRepository.findAllDraftShiftsByIds(shiftIds, showDraft));
            shiftWithActivityDTOS.stream().forEach(shiftWithActivityDTO -> shiftWithActivityDTO.setHasOriginalShift(shiftIdAndOriginalShiftMap.get(shiftWithActivityDTO.getId())));
            List<BigInteger> draftShiftIds = shiftWithActivityDTOS.stream().map(shiftWithActivityDTO -> shiftWithActivityDTO.getId()).collect(Collectors.toList());
            shiftIds.removeAll(draftShiftIds);
            if (isCollectionNotEmpty(shiftIds)) {
                shiftWithActivityDTOS.addAll(shiftMongoRepository.findAllShiftsByIds(shiftIds));
            }
        } else {
            shiftWithActivityDTOS = shiftMongoRepository.findAllShiftsByIds(shiftIds);
        }
        setReasonCodeAndRuleViolationsInShifts(shiftWithActivityDTOS, unitId, shiftIds, showDraft);
        return shiftWithActivityDTOS;
    }

    public boolean updateRemarkInShiftActivity(BigInteger shiftActivityId, ShiftActivityDTO shiftActivityDTO) {
        shiftMongoRepository.updateRemarkInShiftActivity(shiftActivityId, shiftActivityDTO.getRemarks());
        Shift shift = shiftMongoRepository.findShiftByShiftActivityId(shiftActivityId);
        List<ShiftActivity> activities = shift.getActivities();
        for (ShiftActivity activity : activities) {
            if (activity.getId().equals(shiftActivityId)) {
                Todo todo = todoRepository.findTodoBySubEntityId(activity.getActivityId(), shift.getId(), newHashSet(TodoStatus.PENDING, TodoStatus.VIEWED, TodoStatus.REQUESTED));
                todo.setRemark(shiftActivityDTO.getRemarks());
                todoRepository.save(todo);
            }
        }

        return true;
    }

    private void setReasonCodeAndRuleViolationsInShifts(List<ShiftWithActivityDTO> shiftWithActivityDTOS, Long unitId, List<BigInteger> shiftIds, boolean showDraft) {
        Set<BigInteger> absenceReasonCodeIds = shiftWithActivityDTOS.stream().flatMap(shifts -> shifts.getActivities().stream().filter(shiftActivityDTO -> shiftActivityDTO.getAbsenceReasonCodeId() != null).map(shiftActivityDTO -> shiftActivityDTO.getAbsenceReasonCodeId())).collect(Collectors.toSet());
        List<ReasonCodeDTO> reasonCodeDTOS=reasonCodeService.findAllByIds(absenceReasonCodeIds);
        ReasonCodeWrapper reasonCodeWrapper = findUnitAddress(unitId);
        Map<BigInteger, ReasonCodeDTO> reasonCodeDTOMap = reasonCodeDTOS.stream().collect(Collectors.toMap(k->k.getId(),v->v));
        for (ShiftWithActivityDTO shift : shiftWithActivityDTOS) {
            for (ShiftActivityDTO shiftActivityDTO : shift.getActivities()) {
                if (!shiftActivityDTO.isBreakShift()) {
                    shiftActivityDTO.setReasonCode(reasonCodeDTOMap.get(shiftActivityDTO.getAbsenceReasonCodeId()));
                }
                shiftActivityDTO.setLocation(reasonCodeWrapper.getContactAddressData());
            }
            if (isNotNull(shift.getShiftViolatedRules())) {
                shift.setWtaRuleViolations(shift.getShiftViolatedRules().getWorkTimeAgreements());
            }
        }
    }

    private ReasonCodeWrapper findUnitAddress(Long unitId) {
        return userIntegrationService.getUnitInfoAndReasonCodes(unitId);
    }

    /*public List<ShiftDTO> setLayerInShifts(List<ShiftDTO> shifts,Set<BigInteger> sickActivityIds) {
        if(isCollectionNotEmpty(sickActivityIds)){
            List<ShiftDTO> updatedShifts = new ArrayList<>();
            Map<LocalDate, List<ShiftDTO>> shiftsMap = shifts.stream().collect(Collectors.groupingBy(k -> DateUtils.asLocalDate(k.getStartDate()), Collectors.toList()));
            List<Activity> activityWrappers = activityMongoRepository.findActivitiesSickSettingByActivityIds(sickActivityIds);
            Map<BigInteger, Activity> activityMap = activityWrappers.stream().collect(Collectors.toMap(k -> k.getId(), v -> v));
            if(isMapEmpty(activityMap)){
                exceptionService.dataNotFoundException(SICK_ACTIVITY_NOT_FOUND);
            }
            shiftsMap.forEach((localDate, shiftDTOS) -> {
                ShiftDTO sickShift = shiftDTOS.stream().filter(k -> k.getShiftType().equals(SICK)).findAny().orElse(null);
                if (sickShift != null) {
                    Activity activity = getWorkingSickActivity(sickShift, activityMap);
                    if (!activity.getActivityRulesSettings().getSicknessSetting().isShowAslayerOnTopOfPublishedShift()) {
                        updatedShifts.addAll(shiftDTOS.stream().filter(k -> k.getActivities().stream().noneMatch(act -> act.getStatus().contains(ShiftStatus.PUBLISH) && !SICK.equals(k.getShiftType()) && sickShift.getStaffId().equals(k.getStaffId()))).collect(Collectors.toList()));
                    }
                    if (!activity.getActivityRulesSettings().getSicknessSetting().isShowAslayerOnTopOfUnPublishedShift()) {
                        updatedShifts.addAll(shiftDTOS.stream().filter(k -> k.getActivities().stream().noneMatch(act -> !act.getStatus().contains(ShiftStatus.PUBLISH) && !SICK.equals(k.getShiftType()) && sickShift.getStaffId().equals(k.getStaffId()))).collect(Collectors.toList()));
                    }else {
                        updatedShifts.addAll(shiftDTOS);
                    }
                    updatedShifts.add(sickShift);
                }
            });
            return updatedShifts;
        }else {
            return shifts;
        }
    }*/

    public List<ShiftDTO> setLayerInShifts(List<ShiftDTO> shifts,Set<BigInteger> sicknessActivityIds) {
        if(isCollectionNotEmpty(sicknessActivityIds)) {
            List<Activity> activityWrappers = activityMongoRepository.findActivitiesSickSettingByActivityIds(sicknessActivityIds);
            if(isCollectionEmpty(activityWrappers)){
                exceptionService.dataNotFoundException(SICK_ACTIVITY_NOT_FOUND);
            }
            Map<LocalDate, List<ShiftDTO>> shiftsMap = shifts.stream().collect(Collectors.groupingBy(k -> DateUtils.asLocalDate(k.getStartDate()), Collectors.toList()));
            Map<BigInteger, Activity> activityMap = activityWrappers.stream().collect(Collectors.toMap(k -> k.getId(), v -> v));
            shiftsMap.forEach((date, shiftDTOS) -> {
                ShiftDTO sickShift = shiftDTOS.stream().filter(k -> k.getShiftType().equals(SICK)).findAny().orElse(null);
                if (sickShift != null) {
                    Activity activity = getWorkingSickActivity(sickShift, activityMap);
                    if (!activity.getActivityRulesSettings().getSicknessSetting().isShowAslayerOnTopOfPublishedShift()) {
                        shiftDTOS.removeAll(shiftDTOS.stream().filter(k -> k.getActivities().stream().anyMatch(act -> act.getStatus().contains(ShiftStatus.PUBLISH) && !SICK.equals(k.getShiftType()) && sickShift.getStaffId().equals(k.getStaffId()))).collect(Collectors.toList()));
                    }
                    if (!activity.getActivityRulesSettings().getSicknessSetting().isShowAslayerOnTopOfUnPublishedShift()) {
                        shiftDTOS.removeAll(shiftDTOS.stream().filter(k -> k.getActivities().stream().anyMatch(act -> !act.getStatus().contains(ShiftStatus.PUBLISH) && !SICK.equals(k.getShiftType()) && sickShift.getStaffId().equals(k.getStaffId()))).collect(Collectors.toList()));
                    }
                }
            });
            return shiftsMap.values().stream().flatMap(shiftDTOS -> shiftDTOS.stream()).collect(Collectors.toList());
        }else {
            return shifts;
        }
    }

    public Activity getWorkingSickActivity(ShiftDTO shift, Map<BigInteger, Activity> activityWrapperMap) {
        Activity activity = null;
        for (ShiftActivityDTO shiftActivity : shift.getActivities()) {
            if (activityWrapperMap.get(shiftActivity.getActivityId()).getActivityRulesSettings().isSicknessSettingValid()) {
                activity= activityWrapperMap.get(shiftActivity.getActivityId());
                break;
            }
        }
        if(isNull(activity)){
            exceptionService.dataNotFoundException(SICK_ACTIVITY_NOT_FOUND);
        }
        return activity;
    }
    public void updateTimingChanges(Shift oldShift, ShiftDTO shiftDTO, ShiftWithViolatedInfoDTO shiftWithViolatedInfoDTO) {
        WorkTimeAgreementRuleViolation workTimeAgreementRuleViolation = shiftWithViolatedInfoDTO.getViolatedRules().getWorkTimeAgreements().stream().filter(k -> "Minimum shift’s length".equals(k.getName()) || "Maximum shift’s length".equals(k.getName()) || "Maximum night shift’s length".equals(k.getName())).findAny().orElse(null);
        if (isNotNull(workTimeAgreementRuleViolation)) {
            Map<String, Object> map = new HashMap<>();
            if (!oldShift.getStartDate().equals(shiftDTO.getStartDate()) && oldShift.getEndDate().equals(shiftDTO.getEndDate())) {
                Date startDate = shiftDTO.getStartDate().before(oldShift.getStartDate()) ? shiftDTO.getStartDate() : oldShift.getStartDate();
                Date endDate = shiftDTO.getStartDate().before(oldShift.getStartDate()) ? oldShift.getStartDate() : shiftDTO.getStartDate();
                boolean shiftExtends = shiftDTO.getStartDate().before(oldShift.getStartDate());
                int minutes = getMinutesFromTime(workTimeAgreementRuleViolation.getUnitValue());
                map.put("escalatedStartDate", shiftExtends ? asZonedDateTime(shiftDTO.getStartDate()) : asZonedDateTime(oldShift.getStartDate()));
                map.put("escalatedEndDate", shiftExtends?asZonedDateTime(shiftDTO.getEndDate()).minusMinutes(minutes):asZonedDateTime(shiftDTO.getEndDate()).minusMinutes(minutes));
                map.put("startDate", startDate);
                map.put("endDate", endDate);
                map.put("shiftExtend", shiftExtends);
                map.put("minutes", getMinutesBetweenDate(startDate, endDate));
            } else if (!oldShift.getEndDate().equals(shiftDTO.getEndDate())) {
                Date startDate = shiftDTO.getEndDate().before(oldShift.getEndDate()) ? shiftDTO.getEndDate() : oldShift.getEndDate();
                Date endDate = shiftDTO.getEndDate().before(oldShift.getEndDate()) ? oldShift.getEndDate() : shiftDTO.getEndDate();
                boolean shiftExtends = shiftDTO.getEndDate().after(oldShift.getEndDate());
                int minutes = getMinutesFromTime(workTimeAgreementRuleViolation.getUnitValue());
                map.put("escalatedStartDate", shiftExtends ? asZonedDateTime(shiftDTO.getStartDate()).plusMinutes(minutes) : asZonedDateTime(shiftDTO.getStartDate()).plusMinutes(minutes));
                map.put("escalatedEndDate", shiftExtends?asZonedDateTime(shiftDTO.getEndDate()):asZonedDateTime(oldShift.getEndDate()));
                map.put("startDate", startDate);
                map.put("endDate", endDate);
                map.put("shiftExtend", shiftExtends);
                map.put("minutes", getMinutesBetweenDate(startDate, endDate));
            }
            shiftDTO.setChanges(map);
        }
    }
}

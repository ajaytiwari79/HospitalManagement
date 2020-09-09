package com.kairos.service.shift;

import com.kairos.dto.activity.shift.ShiftActivityDTO;
import com.kairos.dto.activity.shift.ShiftDTO;
import com.kairos.dto.activity.shift.ShiftWithActivityDTO;
import com.kairos.dto.user.reason_code.ReasonCodeDTO;
import com.kairos.dto.user.reason_code.ReasonCodeWrapper;
import com.kairos.enums.shift.ShiftStatus;
import com.kairos.enums.shift.TodoStatus;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.activity.ActivityWrapper;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.model.shift.ShiftActivity;
import com.kairos.persistence.model.shift.ShiftViolatedRules;
import com.kairos.persistence.model.todo.Todo;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.persistence.repository.shift.ShiftViolatedRulesMongoRepository;
import com.kairos.persistence.repository.todo.TodoRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.MongoBaseService;
import com.kairos.service.phase.PhaseService;
import com.kairos.service.unit_settings.ActivityConfigurationService;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.enums.shift.ShiftType.SICK;
import static java.util.stream.Collectors.toMap;


/**
 * CreatedBy vipulpandey on 17/12/18
 **/
@Service
@Transactional
public class ShiftDetailsService extends MongoBaseService {

    @Inject
    private ShiftMongoRepository shiftMongoRepository;
    @Inject
    private UserIntegrationService userIntegrationService;
    @Inject
    private ShiftViolatedRulesMongoRepository shiftViolatedRulesMongoRepository;
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
        ReasonCodeWrapper reasonCodeWrapper = findReasonCodes(shiftWithActivityDTOS, unitId);
        Map<BigInteger, ShiftViolatedRules> shiftViolatedRulesMap = findAllShiftViolatedRules(shiftIds, showDraft);
        Map<Long, ReasonCodeDTO> reasonCodeDTOMap = reasonCodeWrapper.getReasonCodes().stream().collect(toMap(ReasonCodeDTO::getId, Function.identity()));
        for (ShiftWithActivityDTO shift : shiftWithActivityDTOS) {
            for (ShiftActivityDTO shiftActivityDTO : shift.getActivities()) {
                if (!shiftActivityDTO.isBreakShift()) {
                    shiftActivityDTO.setReasonCode(reasonCodeDTOMap.get(shiftActivityDTO.getAbsenceReasonCodeId()));
                }
                shiftActivityDTO.setLocation(reasonCodeWrapper.getContactAddressData());
            }
            if (shiftViolatedRulesMap.containsKey(shift.getId())) {
                shift.setWtaRuleViolations(shiftViolatedRulesMap.get(shift.getId()).getWorkTimeAgreements());
            }
        }
    }

    private Map<BigInteger, ShiftViolatedRules> findAllShiftViolatedRules(List<BigInteger> shiftIds, boolean showDraft) {
        Map<BigInteger, ShiftViolatedRules> shiftViolatedRulesMap = new HashMap<>();
        List<ShiftViolatedRules> shiftViolatedRules = shiftViolatedRulesMongoRepository.findAllViolatedRulesByShiftIds(shiftIds);
        Map<BigInteger, ShiftViolatedRules> draftShiftViolatedRules = shiftViolatedRules.stream().filter(ShiftViolatedRules::isDraft).collect(Collectors.toMap(ShiftViolatedRules::getShiftId, Function.identity()));
        Map<BigInteger, ShiftViolatedRules> originalShiftViolatedRules = shiftViolatedRules.stream().filter(shiftViolatedRule -> !shiftViolatedRule.isDraft()).collect(Collectors.toMap(ShiftViolatedRules::getShiftId, Function.identity()));
        if (showDraft) {
            for (BigInteger shiftId : originalShiftViolatedRules.keySet()) {
                if (draftShiftViolatedRules.containsKey(shiftId)) {
                    shiftViolatedRulesMap.put(shiftId, draftShiftViolatedRules.get(shiftId));
                } else {
                    shiftViolatedRulesMap.put(shiftId, originalShiftViolatedRules.get(shiftId));
                }
            }
        } else {
            shiftViolatedRulesMap = originalShiftViolatedRules;
        }
        return shiftViolatedRulesMap;

    }

    private ReasonCodeWrapper findReasonCodes(List<ShiftWithActivityDTO> shiftWithActivityDTOS, Long unitId) {
        Set<Long> absenceReasonCodeIds = shiftWithActivityDTOS.stream().flatMap(shifts -> shifts.getActivities().stream().filter(shiftActivityDTO -> shiftActivityDTO.getAbsenceReasonCodeId() != null).map(shiftActivityDTO -> shiftActivityDTO.getAbsenceReasonCodeId())).collect(Collectors.toSet());
        List<NameValuePair> requestParam = new ArrayList<>();
        requestParam.add(new BasicNameValuePair("absenceReasonCodeIds", absenceReasonCodeIds.toString()));
        return userIntegrationService.getUnitInfoAndReasonCodes(unitId, requestParam);
    }

    public void setLayerInShifts(Map<LocalDate, List<ShiftDTO>> shiftsMap) {
        Set<BigInteger> activityIds = getAllActivityIds(shiftsMap);
        List<ActivityWrapper> activityWrappers = activityMongoRepository.findActivitiesAndTimeTypeByActivityId(activityIds);
        Map<BigInteger, ActivityWrapper> activityWrapperMap = activityWrappers.stream().collect(Collectors.toMap(k -> k.getActivity().getId(), v -> v));
        shiftsMap.forEach((date, shifts) -> {
            ShiftDTO sickShift = shifts.stream().filter(k -> k.getShiftType().equals(SICK)).findAny().orElse(null);
            if (sickShift != null) {
                Activity activity = getWorkingSickActivity(sickShift, activityWrapperMap);
                if (!activity.getActivityRulesSettings().getSicknessSetting().isShowAslayerOnTopOfPublishedShift()) {
                    shifts.removeAll(shifts.stream().filter(k -> k.getActivities().stream().anyMatch(act -> act.getStatus().contains(ShiftStatus.PUBLISH) && k.isDisabled())).collect(Collectors.toList()));
                }
                if (!activity.getActivityRulesSettings().getSicknessSetting().isShowAslayerOnTopOfUnPublishedShift()) {
                    shifts.removeAll(shifts.stream().filter(k -> k.getActivities().stream().anyMatch(act -> !act.getStatus().contains(ShiftStatus.PUBLISH) && k.isDisabled())).collect(Collectors.toList()));
                }
            }
        });
    }

    public Set<BigInteger> getAllActivityIds(Map<LocalDate, List<ShiftDTO>> shiftsMap) {
        Set<BigInteger> activityIds = new HashSet<>();
        shiftsMap.forEach((date, shifts) -> {
            shifts.forEach(shiftDTO -> {
                activityIds.addAll(shiftDTO.getActivities().stream().map(ShiftActivityDTO::getActivityId).collect(Collectors.toSet()));
            });
        });
        return activityIds;
    }

    public Activity getWorkingSickActivity(ShiftDTO shift, Map<BigInteger, ActivityWrapper> activityWrapperMap) {
        Activity activity = null;
        for (ShiftActivityDTO shiftActivity : shift.getActivities()) {
            if (activityWrapperMap.get(shiftActivity.getActivityId()).getActivity().getActivityRulesSettings().isSicknessSettingValid()) {
                return activityWrapperMap.get(shiftActivity.getActivityId()).getActivity();
            }
        }
        return activity;
    }



    // This function is used for the change the status of the draft shift



}

package com.kairos.service.shift;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;
import com.kairos.dto.activity.activity.ActivityDTO;
import com.kairos.dto.activity.shift.ShiftActivityDTO;
import com.kairos.dto.activity.shift.ShiftDTO;
import com.kairos.dto.activity.shift.ShiftWithActivityDTO;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.activity.ActivityWrapper;
import com.kairos.persistence.model.activity.TimeType;
import com.kairos.persistence.model.phase.Phase;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.model.shift.ShiftActivity;
import com.kairos.persistence.model.wta.WorkingTimeAgreement;
import com.kairos.persistence.model.wta.templates.template_types.DurationBetweenShiftsWTATemplate;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.phase.PhaseService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.asDate;
import static com.kairos.commons.utils.DateUtils.asLocalDate;
import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.utils.worktimeagreement.RuletemplateUtils.getValueByPhase;

@Service
public class ShiftHelperService {

    @Inject
    private ShiftMongoRepository shiftMongoRepository;
    @Inject private ShiftValidatorService shiftValidatorService;
    @Inject private PhaseService phaseService;
    @Inject private UserIntegrationService userIntegrationService;

    //@Async
    public void updateBackgroundColorInActivityAndShift(Activity activity, TimeType timeType) {
        List<Shift> shifts = shiftMongoRepository.findShiftByShiftActivityIdAndBetweenDate(newArrayList(activity.getId()), null, null, null);
        updateShiftActivityBackGroundColor(activity, timeType, shifts);
        if (isCollectionNotEmpty(shifts)) {
            shiftMongoRepository.saveEntities(shifts);
        }
    }

    private void updateShiftActivityBackGroundColor(Activity activity, TimeType timeType, List<Shift> shifts) {
        shifts.forEach(shift -> shift.getActivities().forEach(shiftActivity -> {
            if (shiftActivity.getActivityId().equals(activity.getId())) {
                if (isNotNull(timeType)) {
                    shiftActivity.setBackgroundColor(timeType.getBackgroundColor());
                    shiftActivity.setSecondLevelTimeType(timeType.getSecondLevelType());
                }
                shiftActivity.setUltraShortName(activity.getActivityGeneralSettings().getUltraShortName());
                shiftActivity.setShortName(activity.getActivityGeneralSettings().getShortName());
            }
            shiftActivity.getChildActivities().forEach(childActivity -> {
                if (childActivity.getActivityId().equals(activity.getId())) {
                    if (isNotNull(timeType)) {
                        childActivity.setBackgroundColor(timeType.getBackgroundColor());
                        childActivity.setSecondLevelTimeType(timeType.getSecondLevelType());
                    }
                    shiftActivity.setUltraShortName(activity.getActivityGeneralSettings().getUltraShortName());
                    shiftActivity.setShortName(activity.getActivityGeneralSettings().getShortName());
                }
            });
        }));
    }

    public void updateShiftActivityDetails(Shift shift, ShiftWithActivityDTO shiftWithActivityDTO){
        for (int i = 0; i < shift.getActivities().size(); i++) {
            ShiftActivity shiftActivity = shift.getActivities().get(i);
            ShiftActivityDTO shiftActivityDTO = shiftWithActivityDTO.getActivities().get(i);
            updateActivityAndTimeTypeDetails(shiftActivity, shiftActivityDTO);
            for (int j = 0; j < shiftActivity.getChildActivities().size(); j++) {
                shiftActivity = shiftActivity.getChildActivities().get(j);
                shiftActivityDTO = shiftActivityDTO.getChildActivities().get(j);
                updateActivityAndTimeTypeDetails(shiftActivity, shiftActivityDTO);
            }
        }
        shift.setRestingMinutes(shiftWithActivityDTO.getRestingMinutes());
        shift.setDurationMinutes(shiftWithActivityDTO.getDurationMinutes());
        shift.setScheduledMinutes(shiftWithActivityDTO.getScheduledMinutes());
    }

    private void updateActivityAndTimeTypeDetails(ShiftActivity shiftActivity, ShiftActivityDTO shiftActivityDTO) {
        shiftActivity.setTimeType(shiftActivityDTO.getTimeType());
        shiftActivity.setSecondLevelTimeType(shiftActivityDTO.getSecondLevelTimeType());
        shiftActivity.setBackgroundColor(shiftActivityDTO.getBackgroundColor());
        shiftActivity.setActivityName(shiftActivityDTO.getActivityName());
        shiftActivity.setUltraShortName(shiftActivityDTO.getUltraShortName());
        shiftActivity.setShortName(shiftActivityDTO.getShortName());
        shiftActivity.setTimeTypeId(shiftActivityDTO.getTimeTypeId());
        shiftActivity.setSecondLevelTimeType(shiftActivityDTO.getSecondLevelTimeType());
        shiftActivity.setTimeType(shiftActivityDTO.getTimeType());
        shiftActivity.setMethodForCalculatingTime(shiftActivityDTO.getMethodForCalculatingTime());
    }

    public void updateShiftResponse(ShiftDTO shiftDTO, String timeZone, Phase phase){
        if(isNotNull(shiftDTO.getShiftViolatedRules())){
            shiftDTO.setEscalationReasons(shiftDTO.getShiftViolatedRules().getEscalationReasons());
            shiftDTO.setEscalationResolved(shiftDTO.getShiftViolatedRules().isEscalationResolved());
        }
        boolean editable=shiftValidatorService.validateGracePeriod(shiftDTO.getStartDate(), true, shiftDTO.getUnitId(), phase,timeZone);
        shiftDTO.setEditable(editable);
    }

    @Async
    public void updateRestingHoursInShiftsOnWtaUpdate(WorkingTimeAgreement workingTimeAgreement, List<DurationBetweenShiftsWTATemplate> durationBetweenShiftsWTATemplates){
        Date startDate = asDate(workingTimeAgreement.getStartDate());
        Date endDate = isNotNull(workingTimeAgreement.getEndDate()) ? asDate(workingTimeAgreement.getEndDate()): null;
        List<Shift> shifts = shiftMongoRepository.findAllShiftByIntervalAndEmploymentId(workingTimeAgreement.getEmploymentId(),startDate,endDate);
        if(isCollectionNotEmpty(shifts)){
            Set<LocalDateTime> dateTimes = shifts.stream().map(s -> DateUtils.asLocalDateTime(s.getActivities().get(0).getStartDate())).collect(Collectors.toSet());
            Map<Date, Phase> phaseMapByDate = phaseService.getPhasesByDates(shifts.get(0).getUnitId(), dateTimes);
            for (Shift shift : shifts) {
                shift.setRestingMinutes(updateRestingHours(durationBetweenShiftsWTATemplates,shift, phaseMapByDate));
            }
            shiftMongoRepository.saveEntities(shifts);
        }
    }

    public int updateRestingHours(List<DurationBetweenShiftsWTATemplate> durationBetweenShiftsWTATemplates,Shift shift,Map<Date, Phase> phaseMapByDate){
        int restingMinutes = 0;
        for (DurationBetweenShiftsWTATemplate durationBetweenShiftsWTATemplate : durationBetweenShiftsWTATemplates) {
            boolean anyActivityValid = shift.getActivities().stream().filter(shiftActivityDTO -> durationBetweenShiftsWTATemplate.getTimeTypeIds().contains(shiftActivityDTO.getTimeTypeId())).findAny().isPresent();
            if(anyActivityValid && phaseMapByDate.containsKey(shift.getStartDate())) {
                Integer currentRuletemplateRestingMinutes = getValueByPhase( durationBetweenShiftsWTATemplate.getPhaseTemplateValues(), phaseMapByDate.get(shift.getStartDate()).getId());
                if(isNotNull(currentRuletemplateRestingMinutes) && restingMinutes < currentRuletemplateRestingMinutes) {
                    restingMinutes = currentRuletemplateRestingMinutes;
                }
            }
        }
        return restingMinutes;
    }
}

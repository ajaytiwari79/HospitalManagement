package com.kairos.persistence.model.wta.templates.template_types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.dto.activity.shift.ShiftWithActivityDTO;
import com.kairos.enums.DurationType;
import com.kairos.enums.wta.MinMaxSetting;
import com.kairos.enums.wta.WTATemplateType;
import com.kairos.persistence.model.night_worker.ExpertiseNightWorkerSetting;
import com.kairos.persistence.model.wta.templates.WTABaseRuleTemplate;
import com.kairos.wrapper.wta.RuleTemplateSpecificInfo;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.*;

import static com.kairos.commons.utils.DateUtils.asLocalDate;
import static com.kairos.commons.utils.ObjectUtils.isNotNull;
import static com.kairos.service.night_worker.NightWorkerService.getNightInterval;
import static com.kairos.utils.worktimeagreement.RuletemplateUtils.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class DaysOffAfterASeriesWTATemplate extends WTABaseRuleTemplate {

    private static final long serialVersionUID = -7145432848747779050L;
    @Positive
    private long intervalLength;
    @NotEmpty(message = "message.ruleTemplate.interval.notNull")
    private String intervalUnit;
    private int nightShiftSequence;
    private boolean restingTimeAllowed;
    private int restingTime;
    private transient DateTimeInterval interval;

    public DaysOffAfterASeriesWTATemplate() {
        this.wtaTemplateType = WTATemplateType.DAYS_OFF_AFTER_A_SERIES;
    }

    @Override
    public void validateRules(RuleTemplateSpecificInfo infoWrapper) {
        if(!isDisabled() && infoWrapper.isNightWorker() && isNotNull(infoWrapper.getExpertiseNightWorkerSetting())){
            Set<LocalDate> shiftDates = getNightShifts(infoWrapper.getExpertiseNightWorkerSetting(),infoWrapper.getShifts(),interval);
            LocalDate shiftDate = asLocalDate(infoWrapper.getShift().getActivities().get(0).getStartDate());
            boolean currentNightShift = shiftDates.contains(shiftDate);
            int consecutiveNightDays = getConsecutiveDaysInDate(new ArrayList<>(shiftDates));
            Integer[] limitAndCounter = getValueByPhaseAndCounter(infoWrapper, getPhaseTemplateValues(), this);
            boolean isValid = validate(shiftDates, shiftDate, currentNightShift, consecutiveNightDays);
            brakeRuleTemplateAndUpdateViolationDetails(infoWrapper,limitAndCounter[1],isValid, this,
                    limitAndCounter[2], DurationType.DAYS.toValue(),String.valueOf(nightShiftSequence));
        }
    }

    private boolean validate(Set<LocalDate> shiftDates, LocalDate shiftDate, boolean currentNightShift, int consecutiveNightDays) {
        boolean isValid = true;
        if(currentNightShift && consecutiveNightDays>nightShiftSequence){
            restingTime = Math.max(restingTime,1);
            int daysOffCount = 0;
            LocalDate daysOffDate = shiftDate.minusDays(restingTime);
            while (!daysOffDate.isAfter(shiftDate)){
                if(!shiftDates.contains(daysOffDate)){
                    daysOffCount++;
                }
                daysOffDate = daysOffDate.plusDays(1);
            }
            isValid = isValid(MinMaxSetting.MINIMUM, restingTime, daysOffCount);
        }
        return isValid;
    }

    public DaysOffAfterASeriesWTATemplate(String name, boolean disabled, String description, long intervalLength, String intervalUnit, int nightShiftSequence) {
        this.name=name;
        this.disabled=disabled;
        this.description=description;
        this.intervalLength = intervalLength;
        this.intervalUnit = intervalUnit;
        this.nightShiftSequence = nightShiftSequence;
        wtaTemplateType=WTATemplateType.DAYS_OFF_AFTER_A_SERIES;
    }

    private Set<LocalDate> getNightShifts(ExpertiseNightWorkerSetting expertiseNightWorkerSetting, List<ShiftWithActivityDTO> shiftWithActivityDTOS, DateTimeInterval dateTimeInterval) {
        Set<LocalDate> nightShifts = new TreeSet<>();
        for (ShiftWithActivityDTO shiftWithActivityDTO : shiftWithActivityDTOS) {
            if (dateTimeInterval.contains(shiftWithActivityDTO.getStartDate())) {
                DateTimeInterval nightInterval = getNightInterval(shiftWithActivityDTO.getStartDate(), shiftWithActivityDTO.getEndDate(), expertiseNightWorkerSetting.getTimeSlot());
                if (nightInterval.overlaps(shiftWithActivityDTO.getInterval())) {
                    int overlapMinutes = (int) nightInterval.overlap(shiftWithActivityDTO.getInterval()).getMinutes();
                    if (overlapMinutes >= expertiseNightWorkerSetting.getMinMinutesToCheckNightShift()) {
                        nightShifts.add(asLocalDate(shiftWithActivityDTO.getStartDate()));
                    }
                }
            }
        }
        return nightShifts;
    }

    @Override
    public boolean isCalculatedValueChanged(WTABaseRuleTemplate wtaBaseRuleTemplate) {
        DaysOffAfterASeriesWTATemplate daysOffAfterASeriesWTATemplate = (DaysOffAfterASeriesWTATemplate)wtaBaseRuleTemplate;
        return (this != daysOffAfterASeriesWTATemplate) && !(intervalLength == daysOffAfterASeriesWTATemplate.intervalLength &&
                nightShiftSequence == daysOffAfterASeriesWTATemplate.nightShiftSequence &&
                restingTimeAllowed == daysOffAfterASeriesWTATemplate.restingTimeAllowed &&
                restingTime == daysOffAfterASeriesWTATemplate.restingTime &&
                Objects.equals(intervalUnit, daysOffAfterASeriesWTATemplate.intervalUnit) && Objects.equals(this.phaseTemplateValues,daysOffAfterASeriesWTATemplate.phaseTemplateValues));
    }


}

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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.kairos.commons.utils.DateUtils.asLocalDate;
import static com.kairos.commons.utils.ObjectUtils.isNotNull;
import static com.kairos.service.night_worker.NightWorkerService.getNightInterval;
import static com.kairos.utils.worktimeagreement.RuletemplateUtils.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class DaysOffAfterASeriesWTATemplate extends WTABaseRuleTemplate {

    @Positive
    private long intervalLength;
    @NotEmpty(message = "message.ruleTemplate.interval.notNull")
    private String intervalUnit;
    private int nightShiftSequence;
    private boolean restingTimeAllowed;
    private int restingTime;

    public DaysOffAfterASeriesWTATemplate() {
        this.wtaTemplateType = WTATemplateType.DAYS_OFF_AFTER_A_SERIES;
    }

    @Override
    public void validateRules(RuleTemplateSpecificInfo infoWrapper) {
        if(!isDisabled() && infoWrapper.isNightWorker() && isNotNull(infoWrapper.getExpertiseNightWorkerSetting())){
            DateTimeInterval dateTimeInterval = getIntervalByRuleTemplate(infoWrapper.getShift(),this.intervalUnit,this.intervalLength);
            List<ShiftWithActivityDTO> shiftWithActivityDTOS = infoWrapper.getShifts();
            shiftWithActivityDTOS.add(infoWrapper.getShift());
            List<ShiftWithActivityDTO> nightShifts = getNightMinutesOrCount(infoWrapper.getExpertiseNightWorkerSetting(),shiftWithActivityDTOS,dateTimeInterval);
            Set<LocalDate> shiftDates = getSortedAndUniqueDates(nightShifts);
            LocalDate shiftDate = asLocalDate(infoWrapper.getShift().getActivities().get(0).getStartDate());
            boolean currentNightShift = shiftDates.removeIf(date -> date.equals(shiftDate));
            int consecutiveNightDays = getConsecutiveDaysInDate(new ArrayList<>(shiftDates));
            if(currentNightShift){
                shiftDates.add(shiftDate);
            }
            int daysOffCount = 0;
            Integer[] limitAndCounter = getValueByPhaseAndCounter(infoWrapper, getPhaseTemplateValues(), this);
            boolean isValid = true;
            if(currentNightShift && consecutiveNightDays>=nightShiftSequence){
                LocalDate daysOffDate = shiftDate.minusDays(restingTime);
                while (!daysOffDate.isAfter(shiftDate)){
                    if(!shiftDates.contains(daysOffDate)){
                        daysOffCount++;
                    }
                    daysOffDate = daysOffDate.plusDays(1);
                }
                isValid = isValid(MinMaxSetting.MINIMUM, restingTime, daysOffCount);
                if(isValid){
                    daysOffDate = shiftDate.plusDays(restingTime);
                    while (!daysOffDate.isBefore(shiftDate)){
                        if(!shiftDates.contains(daysOffDate)){
                            daysOffCount++;
                        }
                        daysOffDate = daysOffDate.minusDays(1);
                    }
                    isValid = isValid(MinMaxSetting.MINIMUM, restingTime, daysOffCount);
                }
            }
            brakeRuleTemplateAndUpdateViolationDetails(infoWrapper,limitAndCounter[1],isValid, this,
                    limitAndCounter[2], DurationType.DAYS,String.valueOf(restingTime));
        }
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

    private List<ShiftWithActivityDTO> getNightMinutesOrCount(ExpertiseNightWorkerSetting expertiseNightWorkerSetting, List<ShiftWithActivityDTO> shiftWithActivityDTOS,DateTimeInterval dateTimeInterval) {
        List<ShiftWithActivityDTO> nightShifts = new ArrayList<>();
        for (ShiftWithActivityDTO shiftWithActivityDTO : shiftWithActivityDTOS) {
            if (dateTimeInterval.contains(shiftWithActivityDTO.getStartDate())) {
                DateTimeInterval nightInterval = getNightInterval(shiftWithActivityDTO.getStartDate(), shiftWithActivityDTO.getEndDate(), expertiseNightWorkerSetting.getTimeSlot());
                if (nightInterval.overlaps(shiftWithActivityDTO.getInterval())) {
                    int overlapMinutes = (int) nightInterval.overlap(shiftWithActivityDTO.getInterval()).getMinutes();
                    if (overlapMinutes >= expertiseNightWorkerSetting.getMinMinutesToCheckNightShift()) {
                        nightShifts.add(shiftWithActivityDTO);
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

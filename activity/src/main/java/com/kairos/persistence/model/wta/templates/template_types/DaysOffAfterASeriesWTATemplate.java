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

    @Positive
    private long intervalLength;
    @NotEmpty(message = "message.ruleTemplate.interval.notNull")
    private String intervalUnit;
    private int nightShiftSequence;
    private boolean restingTimeAllowed;
    private int restingTime;

    public DaysOffAfterASeriesWTATemplate() {
        wtaTemplateType = WTATemplateType.DAYS_OFF_AFTER_A_SERIES;
    }

    @Override
    public void validateRules(RuleTemplateSpecificInfo infoWrapper) {
        if(infoWrapper.isNightWorker() && isNotNull(infoWrapper.getExpertiseNightWorkerSetting())){
            DateTimeInterval dateTimeInterval = getIntervalByRuleTemplate(infoWrapper.getShift(),this.intervalUnit,this.intervalLength);
            List<ShiftWithActivityDTO> shiftWithActivityDTOS = infoWrapper.getShifts();
            shiftWithActivityDTOS.add(infoWrapper.getShift());
            List<ShiftWithActivityDTO> nightShifts = getNightMinutesOrCount(infoWrapper.getExpertiseNightWorkerSetting(),shiftWithActivityDTOS,dateTimeInterval);
            Set<LocalDate> shiftDates = getSortedAndUniqueDates(nightShifts);
            int consecutiveNightDays = getConsecutiveDaysInDate(new ArrayList<>(shiftDates));
            int daysOffCount = 0;
            Integer[] limitAndCounter = getValueByPhaseAndCounter(infoWrapper, getPhaseTemplateValues(), this);
            if(consecutiveNightDays>=nightShiftSequence){
                LocalDate shiftDate = asLocalDate(infoWrapper.getShift().getActivities().get(0).getStartDate());
                LocalDate daysOffDate = shiftDate.minusDays(limitAndCounter[0]);
                while (!daysOffDate.isAfter(shiftDate)){
                    if(!shiftDates.contains(daysOffDate)){
                        daysOffCount++;
                    }
                    daysOffDate = daysOffDate.plusDays(1);
                }
            }
            boolean isValid = isValid(MinMaxSetting.MINIMUM, limitAndCounter[0], daysOffCount);
            brakeRuleTemplateAndUpdateViolationDetails(infoWrapper,limitAndCounter[1],isValid, this,
                    limitAndCounter[2], DurationType.DAYS,String.valueOf(limitAndCounter[0]));
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

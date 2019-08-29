package com.kairos.persistence.model.wta.templates.template_types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.dto.activity.shift.ShiftWithActivityDTO;
import com.kairos.enums.DurationType;
import com.kairos.enums.wta.MinMaxSetting;
import com.kairos.enums.wta.WTATemplateType;
import com.kairos.persistence.model.wta.templates.WTABaseRuleTemplate;
import com.kairos.wrapper.wta.RuleTemplateSpecificInfo;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.kairos.utils.worktimeagreement.RuletemplateUtils.*;


/**
 * Created by pawanmandhan on 5/8/17.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class RestPeriodInAnIntervalWTATemplate extends WTABaseRuleTemplate {

    @Positive(message = "message.ruleTemplate.interval.notNull")
    private long intervalLength;
    @NotEmpty(message = "message.ruleTemplate.interval.notNull")
    private String intervalUnit;
    private float recommendedValue;
    private List<BigInteger> timeTypeIds = new ArrayList<>();

    public RestPeriodInAnIntervalWTATemplate(String name, boolean disabled,
                                             String description) {
        this.name = name;
        this.disabled = disabled;
        this.description = description;
        wtaTemplateType = WTATemplateType.WEEKLY_REST_PERIOD;

    }

    public RestPeriodInAnIntervalWTATemplate() {
        this.wtaTemplateType = WTATemplateType.WEEKLY_REST_PERIOD;
    }

    @Override
    public void validateRules(RuleTemplateSpecificInfo infoWrapper) {
        if(!isDisabled() && isValidForPhase(infoWrapper.getPhaseId(),this.phaseTemplateValues)){
            DateTimeInterval dateTimeInterval = getIntervalByRuleTemplate(infoWrapper.getShift(), intervalUnit, intervalLength);
            List<ShiftWithActivityDTO> shifts = getShiftsByInterval(dateTimeInterval, infoWrapper.getShifts(), null);
            shifts.add(infoWrapper.getShift());
            shifts = sortShifts(shifts);
            int maxRestingTime = getMaxRestingTime(shifts);
            Integer[] limitAndCounter = getValueByPhaseAndCounter(infoWrapper, getPhaseTemplateValues(), this);
            boolean isValid = isValid(MinMaxSetting.MINIMUM, limitAndCounter[0], maxRestingTime/60);
            brakeRuleTemplateAndUpdateViolationDetails(infoWrapper,limitAndCounter[1],isValid, this,limitAndCounter[2], DurationType.HOURS,getHoursByMinutes(limitAndCounter[0],this.name));
        }
    }


    public int getMaxRestingTime(List<ShiftWithActivityDTO> shifts) {
        int maxRestTime = 0;
        for (int i=1;i<shifts.size();i++) {
            int restTime = (int)new DateTimeInterval(shifts.get(i-1).getEndDate().getTime(),shifts.get(i).getStartDate().getTime()).getMinutes();
            if(restTime>maxRestTime){
                maxRestTime = restTime;
            }
        }
        return maxRestTime;
    }

    @Override
    public boolean isCalculatedValueChanged(WTABaseRuleTemplate wtaBaseRuleTemplate) {
        RestPeriodInAnIntervalWTATemplate restPeriodInAnIntervalWTATemplate = (RestPeriodInAnIntervalWTATemplate) wtaBaseRuleTemplate;
        return (this != restPeriodInAnIntervalWTATemplate) && !(intervalLength == restPeriodInAnIntervalWTATemplate.intervalLength &&
                Float.compare(restPeriodInAnIntervalWTATemplate.recommendedValue, recommendedValue) == 0 &&
                Objects.equals(intervalUnit, restPeriodInAnIntervalWTATemplate.intervalUnit) &&
                Objects.equals(timeTypeIds, restPeriodInAnIntervalWTATemplate.timeTypeIds) && Objects.equals(this.phaseTemplateValues,restPeriodInAnIntervalWTATemplate.phaseTemplateValues));
    }

}

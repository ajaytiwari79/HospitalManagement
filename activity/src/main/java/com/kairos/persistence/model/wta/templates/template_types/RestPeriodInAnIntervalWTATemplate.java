package com.kairos.persistence.model.wta.templates.template_types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.dto.activity.shift.ShiftDTO;
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
import java.util.Comparator;
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
    private transient DateTimeInterval interval;

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
            Integer[] limitAndCounter = getValueByPhaseAndCounter(infoWrapper, getPhaseTemplateValues(), this);
            boolean isValid = getMaxRestingTime(infoWrapper.getShifts(),interval,limitAndCounter[0]);
            brakeRuleTemplateAndUpdateViolationDetails(infoWrapper,limitAndCounter[1],isValid, this,limitAndCounter[2], DurationType.HOURS.toValue(),getHoursByMinutes(limitAndCounter[0],this.name));
        }
    }


    public boolean getMaxRestingTime(List<ShiftWithActivityDTO> shifts,DateTimeInterval dateTimeInterval,int value) {
        int maxRestTime = 0;
        shifts.sort(Comparator.comparing(ShiftDTO::getStartDate));
        for (int i=1;i<shifts.size();i++) {
            int restTime = (int)new DateTimeInterval(shifts.get(i-1).getEndDate().getTime(),shifts.get(i).getStartDate().getTime()).getMinutes();
            boolean isValid = (dateTimeInterval.contains(shifts.get(i).getStartDate()) || dateTimeInterval.getEndLocalDate().equals(shifts.get(i).getEndLocalDate()));
            if(isValid && restTime>maxRestTime){
                maxRestTime = restTime;
                if(!isValid(MinMaxSetting.MINIMUM, value, restTime/60)){
                    return false;
                }
            }
        }
        return true;
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

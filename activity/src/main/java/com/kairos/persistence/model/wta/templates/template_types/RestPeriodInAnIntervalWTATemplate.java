package com.kairos.persistence.model.wta.templates.template_types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.activity.shift.WorkTimeAgreementRuleViolation;
import com.kairos.persistence.model.wta.templates.WTABaseRuleTemplate;
import com.kairos.enums.wta.MinMaxSetting;
import com.kairos.enums.wta.WTATemplateType;
import com.kairos.wrapper.wta.RuleTemplateSpecificInfo;
import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.wrapper.shift.ShiftWithActivityDTO;
import org.springframework.security.core.parameters.P;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.kairos.utils.ShiftValidatorService.*;


/**
 * Created by pawanmandhan on 5/8/17.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RestPeriodInAnIntervalWTATemplate extends WTABaseRuleTemplate {

    @Positive(message = "message.ruleTemplate.interval.notNull")
    private long intervalLength;
    @NotEmpty(message = "message.ruleTemplate.interval.notNull")
    private String intervalUnit;
    private float recommendedValue;
    private List<BigInteger> timeTypeIds = new ArrayList<>();


    public long getIntervalLength() {
        return intervalLength;
    }

    public void setIntervalLength(long intervalLength) {
        this.intervalLength = intervalLength;
    }

    public String getIntervalUnit() {
        return intervalUnit;
    }

    public void setIntervalUnit(String intervalUnit) {
        this.intervalUnit = intervalUnit;
    }


    public float getRecommendedValue() {
        return recommendedValue;
    }

    public void setRecommendedValue(float recommendedValue) {
        this.recommendedValue = recommendedValue;
    }


    public WTATemplateType getWtaTemplateType() {
        return wtaTemplateType;
    }

    public void setWtaTemplateType(WTATemplateType wtaTemplateType) {
        this.wtaTemplateType = wtaTemplateType;
    }

    public RestPeriodInAnIntervalWTATemplate(String name, boolean disabled,
                                             String description) {
        this.name = name;
        this.disabled = disabled;
        this.description = description;
        wtaTemplateType = WTATemplateType.WEEKLY_REST_PERIOD;

    }

    public RestPeriodInAnIntervalWTATemplate() {
        wtaTemplateType = WTATemplateType.WEEKLY_REST_PERIOD;
    }


    public List<BigInteger> getTimeTypeIds() {
        return timeTypeIds;
    }

    public void setTimeTypeIds(List<BigInteger> timeTypeIds) {
        this.timeTypeIds = timeTypeIds;
    }

    @Override
    public void validateRules(RuleTemplateSpecificInfo infoWrapper) {
        if(!isDisabled() && isValidForPhase(infoWrapper.getPhase(),this.phaseTemplateValues)){
            DateTimeInterval dateTimeInterval = getIntervalByRuleTemplate(infoWrapper.getShift(), intervalUnit, intervalLength);
            List<ShiftWithActivityDTO> shifts = getShiftsByInterval(dateTimeInterval, infoWrapper.getShifts(), null);
            shifts.add(infoWrapper.getShift());
            shifts = sortShifts(shifts);
            int maxRestingTime = getMaxRestingTime(shifts);
            Integer[] limitAndCounter = getValueByPhase(infoWrapper, getPhaseTemplateValues(), this);
            boolean isValid = isValid(MinMaxSetting.MINIMUM, limitAndCounter[0], maxRestingTime/60);
            brokeRuleTemplate(infoWrapper,limitAndCounter[1],isValid, this);
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (!super.equals(o)) return false;
        RestPeriodInAnIntervalWTATemplate that = (RestPeriodInAnIntervalWTATemplate) o;
        return intervalLength == that.intervalLength &&
                Float.compare(that.recommendedValue, recommendedValue) == 0 &&
                Objects.equals(intervalUnit, that.intervalUnit) &&
                Objects.equals(timeTypeIds, that.timeTypeIds);
    }

    @Override
    public int hashCode() {

        return Objects.hash(super.hashCode(), intervalLength, intervalUnit, recommendedValue, timeTypeIds);
    }
}

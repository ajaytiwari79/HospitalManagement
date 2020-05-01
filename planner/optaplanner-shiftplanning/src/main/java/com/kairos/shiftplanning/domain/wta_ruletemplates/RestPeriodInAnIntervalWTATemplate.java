package com.kairos.shiftplanning.domain.wta_ruletemplates;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.enums.wta.WTATemplateType;
import com.kairos.shiftplanning.domain.shift.ShiftImp;
import com.kairos.shiftplanning.domain.unit.Unit;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.kairos.enums.wta.MinMaxSetting.MAXIMUM;
import static com.kairos.shiftplanning.utils.ShiftPlanningUtility.*;


/**
 * Created by pawanmandhan on 5/8/17.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class RestPeriodInAnIntervalWTATemplate extends WTABaseRuleTemplate {

    @Positive(message = "message.ruleTemplate.interval.notNull")
    private long intervalLength;
    @NotEmpty(message = "message.ruleTemplate.interval.notNull")
    private String intervalUnit;
    private float recommendedValue;
    private List<BigInteger> timeTypeIds = new ArrayList<>();

    public RestPeriodInAnIntervalWTATemplate() {
        this.wtaTemplateType = WTATemplateType.WEEKLY_REST_PERIOD;
    }

    public int checkConstraints(Unit unit, ShiftImp shiftImp, List<ShiftImp> shiftImps) {
        int penality = 0;
        if(!isDisabled() && isValidForPhase(unit.getPlanningPeriod().getPhase().getId(),this.phaseTemplateValues)){
            DateTimeInterval dateTimeInterval = getIntervalByRuleTemplate(shiftImp, intervalUnit, intervalLength);
            shiftImps = getShiftsByInterval(dateTimeInterval, shiftImps, null);
            shiftImps.add(shiftImp);
            shiftImps = sortShifts(shiftImps);
            int maxRestingTime = getMaxRestingTime(shiftImps);
            int limit = getValueByPhaseAndCounter(unit, getPhaseTemplateValues());
            penality = isValid(MAXIMUM, limit, maxRestingTime);
        }
        return penality;
    }


    public int getMaxRestingTime(List<ShiftImp> shifts) {
        int maxRestTime = 0;
        for (int i=1;i<shifts.size();i++) {
            int restTime = (int)new DateTimeInterval(shifts.get(i-1).getEnd(),shifts.get(i).getStart()).getMinutes();
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

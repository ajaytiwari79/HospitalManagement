package com.kairos.shiftplanning.domain.wta_ruletemplates;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.TimeInterval;
import com.kairos.enums.wta.MinMaxSetting;
import com.kairos.enums.wta.PartOfDay;
import com.kairos.enums.wta.WTATemplateType;
import com.kairos.shiftplanning.domain.shift.ShiftImp;
import com.kairos.shiftplanning.domain.unit.Unit;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import java.math.BigInteger;
import java.util.*;

import static com.kairos.enums.wta.MinMaxSetting.MAXIMUM;
import static com.kairos.shiftplanning.utils.ShiftPlanningUtility.*;

/**
 * Created by pawanmandhan on 5/8/17.
 * TEMPLATE9
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class NumberOfPartOfDayShiftsWTATemplate extends WTABaseRuleTemplate {

    @Positive(message = "message.ruleTemplate.interval.notNull")
    private long intervalLength;
    @NotEmpty(message = "message.ruleTemplate.interval.notNull")
    private String intervalUnit;
    private Set<BigInteger> timeTypeIds = new HashSet<>();
    private Set<BigInteger> plannedTimeIds = new HashSet<>();
    private List<PartOfDay> partOfDays = Arrays.asList(PartOfDay.DAY);
    private float recommendedValue;
    private MinMaxSetting minMaxSetting = MinMaxSetting.MAXIMUM;

    public NumberOfPartOfDayShiftsWTATemplate() {
        wtaTemplateType = WTATemplateType.NUMBER_OF_PARTOFDAY;
    }
    
    public int checkConstraints(Unit unit, ShiftImp shiftImp, List<ShiftImp> shiftImps) {
        int penality = 0;
        if(!isDisabled() && isValidShift(unit.getPhase().getId(),shiftImp,this.phaseTemplateValues,timeTypeIds,plannedTimeIds)){
            TimeInterval[] timeIntervals = getTimeSlotsByPartOfDay(partOfDays,unit.getTimeSlotMap(),shiftImp);
            if(timeIntervals.length>0) {
                DateTimeInterval[] dateTimeIntervals = getIntervalsByRuleTemplate(shiftImp, intervalUnit, intervalLength);
                List<ShiftImp> shifts = filterShiftsByPlannedTypeAndTimeTypeIds(shiftImps, timeTypeIds, plannedTimeIds);
                for (DateTimeInterval dateTimeInterval : dateTimeIntervals) {
                    Set<BigInteger> shiftIds = getShiftIdsByInterval(dateTimeInterval, shifts, timeIntervals);
                    int totalCountOfShifts = shiftIds.size();
                    int limit = getValueByPhaseAndCounter(unit, getPhaseTemplateValues());
                    penality = isValid(MAXIMUM, limit, totalCountOfShifts);
                }
            }
        }
        return penality;
    }

}

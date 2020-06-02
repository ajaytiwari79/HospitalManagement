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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.kairos.enums.wta.MinMaxSetting.MAXIMUM;
import static com.kairos.shiftplanning.utils.ShiftPlanningUtility.*;


/**
 * Created by pawanmandhan on 5/8/17.
 * TEMPLATE3
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class ConsecutiveWorkWTATemplate extends WTABaseRuleTemplate {

    private List<PartOfDay> partOfDays = Arrays.asList(PartOfDay.DAY);
    private Set<BigInteger> plannedTimeIds = new HashSet<>();
    private Set<BigInteger> timeTypeIds = new HashSet<>();
    private float recommendedValue;
    private MinMaxSetting minMaxSetting = MinMaxSetting.MAXIMUM;
    @Positive(message = "message.ruleTemplate.interval.notNull")
    private int intervalLength;
    @NotEmpty(message = "message.ruleTemplate.interval.notNull")
    private String intervalUnit;

    public ConsecutiveWorkWTATemplate() {
        this.wtaTemplateType = WTATemplateType.CONSECUTIVE_WORKING_PARTOFDAY;
    }

    public int checkConstraints(Unit unit, ShiftImp shiftImp, List<ShiftImp> shiftImps) {
        int penality = 0;
        if(!isDisabled() && isValidForPhase(unit.getPlanningPeriod().getPhase().getId(),this.phaseTemplateValues)) {
            if (CollectionUtils.containsAny(timeTypeIds,shiftImp.getActivitiesTimeTypeIds())) {
                TimeInterval timeInterval = getTimeSlotByPartOfDay(partOfDays, unit.getTimeSlotMap(), shiftImp);
                if (timeInterval != null) {
                    shiftImps = filterShiftsByPlannedTypeAndTimeTypeIds(shiftImps, timeTypeIds, plannedTimeIds);
                    DateTimeInterval dateTimeInterval = getIntervalByRuleTemplate(shiftImp, intervalUnit, intervalLength);
                    shiftImps = getShiftsByInterval(dateTimeInterval, shiftImps, timeInterval);
                    if(MAXIMUM.equals(minMaxSetting)){
                        shiftImps.add(shiftImp);
                    }
                    int consecutiveDays = getConsecutiveDays(getSortedAndUniqueLocalDates(shiftImps), shiftImp.getStartDate());
                    int limitAndCounter = getValueByPhaseAndCounter(unit, getPhaseTemplateValues());
                    penality = isValid(minMaxSetting, limitAndCounter, consecutiveDays);
                }
            }
        }
        return 0;
    }

    private int getConsecutiveDays(List<LocalDate> shiftDates, LocalDate localDate){
        int beforeConsecutiveDays = getConsecutiveDaysInDate(shiftDates.stream().filter(date-> !date.isAfter(localDate)).collect(Collectors.toList()));
        int afterConsecutiveDays = getConsecutiveDaysInDate(shiftDates.stream().filter(date-> !date.isBefore(localDate)).collect(Collectors.toList()));
        int consecutiveDays;
        if(MAXIMUM.equals(minMaxSetting)){
            consecutiveDays = beforeConsecutiveDays > afterConsecutiveDays ? beforeConsecutiveDays : afterConsecutiveDays;
        }else{
            consecutiveDays = beforeConsecutiveDays < afterConsecutiveDays ? beforeConsecutiveDays : afterConsecutiveDays;
        }
        return consecutiveDays;
    }

}

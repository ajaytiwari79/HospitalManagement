package com.kairos.shiftplanning.domain.wta_ruletemplates;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.enums.TimeTypeEnum;
import com.kairos.enums.wta.MinMaxSetting;
import com.kairos.enums.wta.WTATemplateType;
import com.kairos.shiftplanning.domain.activity.ShiftActivity;
import com.kairos.shiftplanning.domain.shift.ShiftImp;
import com.kairos.shiftplanning.domain.unit.Unit;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;

import java.math.BigInteger;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;
import static com.kairos.commons.utils.ObjectUtils.newHashSet;
import static com.kairos.shiftplanning.utils.ShiftPlanningUtility.*;


/**
 * Created by pawanmandhan on 5/8/17.
 * TEMPLATE16
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class DurationBetweenShiftsWTATemplate extends WTABaseRuleTemplate {


    private static final int NOT_VALID_VALUE = 0;
    private Set<BigInteger> plannedTimeIds = new HashSet<>();
    private Set<BigInteger> timeTypeIds = new HashSet<>();
    private float recommendedValue;
    private MinMaxSetting minMaxSetting = MinMaxSetting.MINIMUM;

    public DurationBetweenShiftsWTATemplate(String name, boolean disabled, String description) {
        this.name = name;
        this.disabled = disabled;
        this.description = description;

    }

    public DurationBetweenShiftsWTATemplate() {
        this.wtaTemplateType = WTATemplateType.DURATION_BETWEEN_SHIFTS;
    }

    public int checkConstraints(Unit unit, ShiftImp shiftImp, List<ShiftImp> shiftImps) {
        int penality = 0;
        if (!isDisabled() && isValidForPhase(unit.getPhase().getId(), this.phaseTemplateValues) && isCollectionNotEmpty(plannedTimeIds) && CollectionUtils.containsAny(plannedTimeIds, shiftImp.getActivitiesPlannedTimeIds()) && isCollectionNotEmpty(timeTypeIds) && CollectionUtils.containsAny(timeTypeIds, shiftImp.getActivitiesTimeTypeIds())) {
            if(isCollectionNotEmpty(shiftImps)){
                int restingHours = getRestingHoursByTimeType(shiftImp,shiftImps,true);
                int limit = getValueByPhaseAndCounter(unit, getPhaseTemplateValues());
                penality = restingHours==NOT_VALID_VALUE ? 0 : isValid(minMaxSetting, limit, restingHours);
                if (penality==0) {
                    restingHours = getRestingHoursByTimeType(shiftImp,shiftImps,false);
                    limit = getValueByPhaseAndCounter(unit, getPhaseTemplateValues());
                    penality = restingHours==NOT_VALID_VALUE ? 0 : isValid(minMaxSetting, limit, restingHours);
                }
            }
        }
        return penality;
    }

    @Override
    public boolean isCalculatedValueChanged(WTABaseRuleTemplate wtaBaseRuleTemplate) {
        DurationBetweenShiftsWTATemplate durationBetweenShiftsWTATemplate = (DurationBetweenShiftsWTATemplate) wtaBaseRuleTemplate;
        return (this != durationBetweenShiftsWTATemplate) && !(Float.compare(durationBetweenShiftsWTATemplate.recommendedValue, recommendedValue) == 0 && Objects.equals(plannedTimeIds, durationBetweenShiftsWTATemplate.plannedTimeIds) && Objects.equals(timeTypeIds, durationBetweenShiftsWTATemplate.timeTypeIds) && minMaxSetting == durationBetweenShiftsWTATemplate.minMaxSetting && Objects.equals(this.phaseTemplateValues, durationBetweenShiftsWTATemplate.phaseTemplateValues));
    }

    public int getRestingHoursByTimeType(ShiftImp shiftImp,List<ShiftImp> shiftImps,boolean checkBefore){
        ShiftActivity shiftActivity = checkBefore ? shiftImp.firstShiftActivity() : shiftImp.lastShiftActivity();
        TimeTypeEnum timeTypeEnum = getTimeTypeEnum(shiftActivity);
        switch (timeTypeEnum){
            case ABSENCE:
                return getDurationByAbsenceOrPresenceType(shiftImps,shiftActivity,checkBefore,newHashSet(TimeTypeEnum.PRESENCE));
            case PRESENCE:
                return getDurationByAbsenceOrPresenceType(shiftImps,shiftActivity,checkBefore,newHashSet(TimeTypeEnum.PRESENCE,TimeTypeEnum.ABSENCE));
                default:
                    break;
        }
        return NOT_VALID_VALUE;
    }

    private int getDurationByAbsenceOrPresenceType(List<ShiftImp> shifts, ShiftActivity shiftActivity, boolean checkBefore, Set<TimeTypeEnum> timeTypeEnums) {
        ZonedDateTime date = checkBefore ? shiftActivity.getStartDate() : shiftActivity.getEndDate();
        int restingHours = NOT_VALID_VALUE;
        for (ShiftImp shiftImp : shifts) {
            for (ShiftActivity activity : shiftImp.getShiftActivities()) {
                if(checkBefore && !activity.getEndDate().isAfter(date) && timeTypeEnums.contains(activity.getActivity().getTimeType().getTimeTypeEnum())){
                    int duration = (int)new DateTimeInterval(activity.getEndDate(),date).getMinutes();
                    restingHours = restingHours > duration || restingHours==NOT_VALID_VALUE ? duration : restingHours;
                }
                if(!checkBefore && !activity.getStartDate().isBefore(date) && timeTypeEnums.contains(activity.getActivity().getTimeType().getTimeTypeEnum())){
                    int duration = (int)new DateTimeInterval(date,activity.getStartDate()).getMinutes();
                    restingHours = restingHours > duration || restingHours==NOT_VALID_VALUE ? duration : restingHours;
                }
            }
        }
        return restingHours;
    }

    public TimeTypeEnum getTimeTypeEnum(ShiftActivity shiftActivity){
        return shiftActivity.getActivity().getTimeType().getTimeTypeEnum();
    }
}

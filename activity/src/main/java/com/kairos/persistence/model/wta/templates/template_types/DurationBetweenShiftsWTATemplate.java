package com.kairos.persistence.model.wta.templates.template_types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.dto.activity.shift.ShiftActivityDTO;
import com.kairos.dto.activity.shift.ShiftWithActivityDTO;
import com.kairos.enums.DurationType;
import com.kairos.enums.TimeTypeEnum;
import com.kairos.enums.wta.MinMaxSetting;
import com.kairos.enums.wta.WTATemplateType;
import com.kairos.persistence.model.wta.templates.WTABaseRuleTemplate;
import com.kairos.wrapper.wta.RuleTemplateSpecificInfo;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.*;

import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.constants.AppConstants.NOT_VALID_VALUE;
import static com.kairos.utils.worktimeagreement.RuletemplateUtils.*;
import static org.apache.commons.collections.CollectionUtils.containsAny;

/**
 * Created by pawanmandhan on 5/8/17.
 * TEMPLATE16
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class DurationBetweenShiftsWTATemplate extends WTABaseRuleTemplate {


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

    @Override
    public void validateRules(RuleTemplateSpecificInfo infoWrapper) {
        if (!isDisabled() && isValidForPhase(infoWrapper.getPhaseId(), this.phaseTemplateValues) && isCollectionNotEmpty(plannedTimeIds) && containsAny(plannedTimeIds, infoWrapper.getShift().getActivitiesPlannedTimeIds()) && isCollectionNotEmpty(timeTypeIds) && containsAny(timeTypeIds, infoWrapper.getShift().getActivitiesTimeTypeIds())) {
            if(isCollectionNotEmpty(infoWrapper.getShifts())){
                int restingHours = getRestingHoursByTimeType(infoWrapper,true);
                Integer[] limitAndCounter = getValueByPhaseAndCounter(infoWrapper, getPhaseTemplateValues(), this);
                boolean isValid = isValid(minMaxSetting, limitAndCounter[0], restingHours) || restingHours==NOT_VALID_VALUE;
                if (isValid) {
                    restingHours = getRestingHoursByTimeType(infoWrapper,false);
                    limitAndCounter = getValueByPhaseAndCounter(infoWrapper, getPhaseTemplateValues(), this);
                    isValid = isValid(minMaxSetting, limitAndCounter[0], restingHours) || restingHours==NOT_VALID_VALUE;
                }
                brakeRuleTemplateAndUpdateViolationDetails(infoWrapper, limitAndCounter[1], isValid, this, limitAndCounter[2], DurationType.HOURS, getHoursByMinutes(limitAndCounter[0],this.name));
            }
        }
    }

    @Override
    public boolean isCalculatedValueChanged(WTABaseRuleTemplate wtaBaseRuleTemplate) {
        DurationBetweenShiftsWTATemplate durationBetweenShiftsWTATemplate = (DurationBetweenShiftsWTATemplate) wtaBaseRuleTemplate;
        return (this != durationBetweenShiftsWTATemplate) && !(Float.compare(durationBetweenShiftsWTATemplate.recommendedValue, recommendedValue) == 0 && Objects.equals(plannedTimeIds, durationBetweenShiftsWTATemplate.plannedTimeIds) && Objects.equals(timeTypeIds, durationBetweenShiftsWTATemplate.timeTypeIds) && minMaxSetting == durationBetweenShiftsWTATemplate.minMaxSetting && Objects.equals(this.phaseTemplateValues, durationBetweenShiftsWTATemplate.phaseTemplateValues));
    }

    public int getRestingHoursByTimeType(RuleTemplateSpecificInfo ruleTemplateSpecificInfo,boolean checkBefore){
        ShiftActivityDTO shiftActivityDTO = checkBefore ? ruleTemplateSpecificInfo.getShift().getFirstActivity() : ruleTemplateSpecificInfo.getShift().getLastActivity();
        TimeTypeEnum timeTypeEnum = getTimeTypeEnum(shiftActivityDTO);
        switch (timeTypeEnum){
            case ABSENCE:
                return getDurationByAbsenceOrPresenceType(ruleTemplateSpecificInfo.getShifts(),shiftActivityDTO,checkBefore,newHashSet(TimeTypeEnum.PRESENCE));
            case PRESENCE:
                return getDurationByAbsenceOrPresenceType(ruleTemplateSpecificInfo.getShifts(),shiftActivityDTO,checkBefore,newHashSet(TimeTypeEnum.PRESENCE,TimeTypeEnum.ABSENCE));
                default:
                    break;
        }
        return NOT_VALID_VALUE;
    }

    private int getDurationByAbsenceOrPresenceType(List<ShiftWithActivityDTO> shifts, ShiftActivityDTO shiftActivityDTO, boolean checkBefore, Set<TimeTypeEnum> timeTypeEnums) {
        Date date = checkBefore ? shiftActivityDTO.getStartDate() : shiftActivityDTO.getEndDate();
        int restingHours = NOT_VALID_VALUE;
        for (ShiftWithActivityDTO shiftWithActivityDTO : shifts) {
            for (ShiftActivityDTO activity : shiftWithActivityDTO.getActivities()) {
                if(checkBefore && activity.getEndDate().before(date) && timeTypeEnums.contains(activity.getActivity().getBalanceSettingsActivityTab().getTimeType())){
                    int duration = (int)new DateTimeInterval(activity.getEndDate(),date).getMinutes();
                    restingHours = restingHours > duration || restingHours==NOT_VALID_VALUE ? duration : restingHours;
                }if(!checkBefore && activity.getStartDate().after(date) && timeTypeEnums.contains(activity.getActivity().getBalanceSettingsActivityTab().getTimeType())){
                    int duration = (int)new DateTimeInterval(date,activity.getStartDate()).getMinutes();
                    restingHours = restingHours > duration || restingHours==NOT_VALID_VALUE ? duration : restingHours;
                }
            }
        }
        return restingHours;
    }

    public TimeTypeEnum getTimeTypeEnum(ShiftActivityDTO shiftActivityDTO){
        return shiftActivityDTO.getActivity().getBalanceSettingsActivityTab().getTimeType();
    }
}

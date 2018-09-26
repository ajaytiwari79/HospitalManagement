package com.kairos.persistence.model.wta.templates.template_types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.activity.shift.WorkTimeAgreementRuleViolation;
import com.kairos.persistence.model.wta.templates.WTABaseRuleTemplate;
import com.kairos.enums.wta.MinMaxSetting;
import com.kairos.enums.wta.WTATemplateType;
import com.kairos.wrapper.wta.RuleTemplateSpecificInfo;
import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;
import com.kairos.wrapper.shift.ShiftWithActivityDTO;

import java.math.BigInteger;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.kairos.utils.ShiftValidatorService.*;

/**
 * Created by pawanmandhan on 5/8/17.
 * TEMPLATE16
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DurationBetweenShiftsWTATemplate extends WTABaseRuleTemplate {


    private List<BigInteger> plannedTimeIds = new ArrayList<>();
    private List<BigInteger> timeTypeIds = new ArrayList<>();
    private float recommendedValue;
    private MinMaxSetting minMaxSetting = MinMaxSetting.MINIMUM;


    public MinMaxSetting getMinMaxSetting() {
        return minMaxSetting;
    }

    public void setMinMaxSetting(MinMaxSetting minMaxSetting) {
        this.minMaxSetting = minMaxSetting;
    }


    public List<BigInteger> getPlannedTimeIds() {
        return plannedTimeIds;
    }

    public void setPlannedTimeIds(List<BigInteger> plannedTimeIds) {
        this.plannedTimeIds = plannedTimeIds;
    }

    public List<BigInteger> getTimeTypeIds() {
        return timeTypeIds;
    }

    public void setTimeTypeIds(List<BigInteger> timeTypeIds) {
        this.timeTypeIds = timeTypeIds;
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



    public DurationBetweenShiftsWTATemplate(String name, boolean disabled,
                                            String description) {
        this.name = name;
        this.disabled = disabled;
        this.description = description;

    }
    public DurationBetweenShiftsWTATemplate() {
    }

    @Override
    public void validateRules(RuleTemplateSpecificInfo infoWrapper) {
        //TODO It should work on Multiple activity
        if(!isDisabled() && isValidForPhase(infoWrapper.getPhase(),this.phaseTemplateValues) && timeTypeIds.contains(infoWrapper.getShift().getActivities().get(0).getActivity().getBalanceSettingsActivityTab().getTimeTypeId())) {
            int timefromPrevShift = 0;
            List<ShiftWithActivityDTO> shifts = filterShifts(infoWrapper.getShifts(), timeTypeIds, plannedTimeIds, null);
            shifts = (List<ShiftWithActivityDTO>) shifts.stream().filter(shift1 -> DateUtils.asZoneDateTime(shift1.getActivitiesEndDate()).isBefore(DateUtils.asZoneDateTime(infoWrapper.getShift().getActivitiesStartDate()))).sorted(getShiftStartTimeComparator()).collect(Collectors.toList());
            if (shifts.size() > 0) {
                ZonedDateTime prevShiftEnd = DateUtils.asZoneDateTime(shifts.size() > 1 ? shifts.get(shifts.size() - 1).getActivitiesEndDate() : shifts.get(0).getActivitiesEndDate());
                timefromPrevShift = (int)new DateTimeInterval(prevShiftEnd, DateUtils.asZoneDateTime(infoWrapper.getShift().getActivitiesStartDate())).getMinutes();
                Integer[] limitAndCounter = getValueByPhase(infoWrapper, getPhaseTemplateValues(), this);
                boolean isValid = isValid(minMaxSetting, limitAndCounter[0], timefromPrevShift/60);
                if (!isValid) {
                    if (limitAndCounter[1] != null) {
                        int counterValue = limitAndCounter[1] - 1;
                        if (counterValue < 0) {
                            WorkTimeAgreementRuleViolation workTimeAgreementRuleViolation = new WorkTimeAgreementRuleViolation(this.id,this.name,0,true,false);
                            infoWrapper.getViolatedRules().getWorkTimeAgreements().add(workTimeAgreementRuleViolation);

                        }else {
                            WorkTimeAgreementRuleViolation workTimeAgreementRuleViolation = new WorkTimeAgreementRuleViolation(this.id,this.name,limitAndCounter[1],true,true);
                            infoWrapper.getViolatedRules().getWorkTimeAgreements().add(workTimeAgreementRuleViolation);
                        }
                    }else {
                        WorkTimeAgreementRuleViolation workTimeAgreementRuleViolation = new WorkTimeAgreementRuleViolation(this.id,this.name,0,true,false);
                        infoWrapper.getViolatedRules().getWorkTimeAgreements().add(workTimeAgreementRuleViolation);
                    }
                }
            }
        }
    }
}
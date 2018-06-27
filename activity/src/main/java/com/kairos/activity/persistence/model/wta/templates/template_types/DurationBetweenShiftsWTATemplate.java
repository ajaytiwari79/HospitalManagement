package com.kairos.activity.persistence.model.wta.templates.template_types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.activity.custom_exception.InvalidRequestException;
import com.kairos.activity.enums.MinMaxSetting;
import com.kairos.activity.persistence.enums.PartOfDay;
import com.kairos.activity.persistence.enums.WTATemplateType;
import com.kairos.activity.persistence.model.wta.templates.WTABaseRuleTemplate;
import com.kairos.activity.persistence.model.wta.wrapper.RuleTemplateSpecificInfo;
import com.kairos.activity.response.dto.ShiftWithActivityDTO;
import com.kairos.activity.util.DateTimeInterval;
import com.kairos.activity.util.DateUtils;

import java.math.BigInteger;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.kairos.activity.util.WTARuleTemplateValidatorUtility.*;

/**
 * Created by pawanmandhan on 5/8/17.
 * TEMPLATE16
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DurationBetweenShiftsWTATemplate extends WTABaseRuleTemplate {


    private List<PartOfDay> partOfDays = new ArrayList<>();
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

    public List<PartOfDay> getPartOfDays() {
        return partOfDays;
    }

    public void setPartOfDays(List<PartOfDay> partOfDays) {
        this.partOfDays = partOfDays;
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
        wtaTemplateType = WTATemplateType.DURATION_BETWEEN_SHIFTS;

    }
    public DurationBetweenShiftsWTATemplate() {
        wtaTemplateType = WTATemplateType.DURATION_BETWEEN_SHIFTS;
    }

    @Override
    public String isSatisfied(RuleTemplateSpecificInfo infoWrapper) {
        if(!isDisabled() && isValidForPhase(infoWrapper.getPhase(),this.phaseTemplateValues) && (plannedTimeIds.contains(infoWrapper.getShift().getPlannedTypeId()) && timeTypeIds.contains(infoWrapper.getShift().getActivity().getBalanceSettingsActivityTab().getTimeTypeId()))) {
            int timefromPrevShift = 0;
            List<ShiftWithActivityDTO> shifts = filterShifts(infoWrapper.getShifts(), timeTypeIds, plannedTimeIds, null);
            shifts = (List<ShiftWithActivityDTO>) shifts.stream().filter(shift1 -> DateUtils.getZoneDateTime(shift1.getEndDate()).isBefore(DateUtils.getZoneDateTime(infoWrapper.getShift().getStartDate()))).sorted(getShiftStartTimeComparator()).collect(Collectors.toList());
            if (shifts.size() > 0) {
                ZonedDateTime prevShiftEnd = DateUtils.getZoneDateTime(shifts.size() > 1 ? shifts.get(shifts.size() - 1).getEndDate() : shifts.get(0).getEndDate());
                timefromPrevShift = new DateTimeInterval(prevShiftEnd, DateUtils.getZoneDateTime(infoWrapper.getShift().getStartDate())).getMinutes();
                Integer[] limitAndCounter = getValueByPhase(infoWrapper, getPhaseTemplateValues(), getId());
                boolean isValid = isValid(minMaxSetting, limitAndCounter[0], timefromPrevShift/60);
                if (!isValid) {
                    if (limitAndCounter[1] != null) {
                        int counterValue = limitAndCounter[1] - 1;
                        if (counterValue < 0) {
                            throw new InvalidRequestException(getName() + " is Broken");
                        } else {
                            infoWrapper.getCounterMap().put(getId(), infoWrapper.getCounterMap().getOrDefault(getId(), 0) + 1);
                            infoWrapper.getShift().getBrokenRuleTemplateIds().add(getId());
                        }
                    } else {
                        throw new InvalidRequestException(getName() + " is Broken");
                    }
                }
            }
        }
        return "";
    }
}
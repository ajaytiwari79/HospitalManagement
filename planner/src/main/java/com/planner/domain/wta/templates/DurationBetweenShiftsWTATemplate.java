package com.planner.domain.wta.templates;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.enums.wta.MinMaxSetting;
import com.kairos.enums.wta.PartOfDay;
import com.kairos.enums.wta.WTATemplateType;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

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

    }
    public DurationBetweenShiftsWTATemplate() {
    }


}
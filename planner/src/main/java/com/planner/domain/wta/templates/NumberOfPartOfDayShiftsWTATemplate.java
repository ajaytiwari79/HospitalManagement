package com.planner.domain.wta.templates;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.enums.wta.MinMaxSetting;
import com.kairos.enums.wta.PartOfDay;
import com.kairos.enums.wta.WTATemplateType;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by pawanmandhan on 5/8/17.
 * TEMPLATE9
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class NumberOfPartOfDayShiftsWTATemplate extends WTABaseRuleTemplate {

    private long noOfPartOfDayWorked;
    private long intervalLength;
    private String intervalUnit;

    private List<BigInteger> timeTypeIds = new ArrayList<>();
    private List<BigInteger> plannedTimeIds = new ArrayList<>();
    private List<PartOfDay> partOfDays = Arrays.asList(PartOfDay.DAY);
    private float recommendedValue;
    private MinMaxSetting minMaxSetting = MinMaxSetting.MAXIMUM;


    public MinMaxSetting getMinMaxSetting() {
        return minMaxSetting;
    }

    public void setMinMaxSetting(MinMaxSetting minMaxSetting) {
        this.minMaxSetting = minMaxSetting;
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

    public List<BigInteger> getTimeTypeIds() {
        return timeTypeIds;
    }

    public void setTimeTypeIds(List<BigInteger> timeTypeIds) {
        this.timeTypeIds = timeTypeIds;
    }


    public List<BigInteger> getPlannedTimeIds() {
        return plannedTimeIds;
    }

    public void setPlannedTimeIds(List<BigInteger> plannedTimeIds) {
        this.plannedTimeIds = plannedTimeIds;
    }

    public WTATemplateType getWtaTemplateType() {
        return wtaTemplateType;
    }

    public void setWtaTemplateType(WTATemplateType wtaTemplateType) {
        this.wtaTemplateType = wtaTemplateType;
    }

    public String getIntervalUnit() {
        return intervalUnit;
    }

    public void setIntervalUnit(String intervalUnit) {
        this.intervalUnit = intervalUnit;
    }


    public long getNoOfPartOfDayWorked() {
        return noOfPartOfDayWorked;
    }

    public void setNoOfPartOfDayWorked(long noOfPartOfDayWorked) {
        this.noOfPartOfDayWorked = noOfPartOfDayWorked;
    }

    public long getIntervalLength() {
        return intervalLength;
    }

    public void setIntervalLength(long intervalLength) {
        this.intervalLength = intervalLength;
    }


    public NumberOfPartOfDayShiftsWTATemplate(String name, boolean disabled, String description, long noOfPartOfDayWorked) {
        this.noOfPartOfDayWorked = noOfPartOfDayWorked;
        this.name = name;
        this.disabled = disabled;
        this.description = description;
        wtaTemplateType = WTATemplateType.NUMBER_OF_PARTOFDAY;
    }
    public NumberOfPartOfDayShiftsWTATemplate() {
        wtaTemplateType = WTATemplateType.NUMBER_OF_PARTOFDAY;
    }




}

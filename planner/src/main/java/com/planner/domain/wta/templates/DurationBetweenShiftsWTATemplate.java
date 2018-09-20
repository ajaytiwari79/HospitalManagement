package com.planner.domain.wta.templates;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.enums.wta.MinMaxSetting;
import com.kairos.enums.wta.PartOfDay;
import com.kairos.enums.wta.WTATemplateType;
import com.planner.domain.wta.WTABaseRuleTemplate;

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

    private long durationBetweenShifts;

    private List<PartOfDay> partOfDays = new ArrayList<>();
    private List<BigInteger> activityIds = new ArrayList<>();
    private float recommendedValue;
    private MinMaxSetting minMaxSetting = MinMaxSetting.MINIMUM;


    public MinMaxSetting getMinMaxSetting() {
        return minMaxSetting;
    }

    public void setMinMaxSetting(MinMaxSetting minMaxSetting) {
        this.minMaxSetting = minMaxSetting;
    }


    public List<BigInteger> getActivityIds() {
        return activityIds;
    }

    public void setActivityIds(List<BigInteger> activityIds) {
        this.activityIds = activityIds;
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


    public long getDurationBetweenShifts() {
        return durationBetweenShifts;
    }

    public void setDurationBetweenShifts(long durationBetweenShifts) {
        this.durationBetweenShifts = durationBetweenShifts;
    }

    public DurationBetweenShiftsWTATemplate(String name, boolean disabled,
                                            String description, long durationBetweenShifts) {
        this.name = name;
        this.disabled = disabled;
        this.description = description;
        this.durationBetweenShifts = durationBetweenShifts;

    }
    public DurationBetweenShiftsWTATemplate() {
        wtaTemplateType = WTATemplateType.DURATION_BETWEEN_SHIFTS;
    }
    }
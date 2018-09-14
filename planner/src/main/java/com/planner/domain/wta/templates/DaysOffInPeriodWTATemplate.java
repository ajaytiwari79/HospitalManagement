package com.planner.domain.wta.templates;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.enums.wta.MinMaxSetting;

/**
 * Created by pawanmandhan on 5/8/17.
 * TEMPLATE10
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DaysOffInPeriodWTATemplate extends WTABaseRuleTemplate {

    private long intervalLength;
    private String intervalUnit;
    private MinMaxSetting minMaxSetting = MinMaxSetting.MINIMUM;
    private boolean restingTimeAllowed;
    private int restingTime;
    private float recommendedValue;


    public MinMaxSetting getMinMaxSetting() {
        return minMaxSetting;
    }

    public void setMinMaxSetting(MinMaxSetting minMaxSetting) {
        this.minMaxSetting = minMaxSetting;
    }

    public boolean isRestingTimeAllowed() {
        return restingTimeAllowed;
    }

    public void setRestingTimeAllowed(boolean restingTimeAllowed) {
        this.restingTimeAllowed = restingTimeAllowed;
    }
    public int getRestingTime() {
        return restingTime;
    }

    public void setRestingTime(int restingTime) {
        this.restingTime = restingTime;
    }


    public float getRecommendedValue() {
        return recommendedValue;
    }

    public void setRecommendedValue(float recommendedValue) {
        this.recommendedValue = recommendedValue;
    }



    public String getIntervalUnit() {
        return intervalUnit;
    }

    public void setIntervalUnit(String intervalUnit) {
        this.intervalUnit = intervalUnit;
    }


    public long getIntervalLength() {
        return intervalLength;
    }

    public void setIntervalLength(long intervalLength) {
        this.intervalLength = intervalLength;
    }


    public DaysOffInPeriodWTATemplate(String name, boolean disabled,
                                      String description, long intervalLength, String intervalUnit) {
        this.intervalLength = intervalLength;
        this.name = name;
        this.disabled = disabled;
        this.description = description;
        this.intervalUnit = intervalUnit;


    }

    public DaysOffInPeriodWTATemplate() {

    }


}

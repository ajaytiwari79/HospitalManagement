package com.kairos.shiftplanning.domain.wta.updated_wta;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.enums.wta.WTATemplateType;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DaysOffAfterASeriesWTATemplate extends WTABaseRuleTemplate {

    private long intervalLength;
    private String intervalUnit;
    private int nightShiftSequence;
    private boolean restingTimeAllowed;
    private int restingTime;

    public long getIntervalLength() {
        return intervalLength;
    }

    public void setIntervalLength(long intervalLength) {
        this.intervalLength = intervalLength;
    }

    public String getIntervalUnit() {
        return intervalUnit;
    }

    public void setIntervalUnit(String intervalUnit) {
        this.intervalUnit = intervalUnit;
    }

    public int getNightShiftSequence() {
        return nightShiftSequence;
    }

    public void setNightShiftSequence(int nightShiftSequence) {
        this.nightShiftSequence = nightShiftSequence;
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

    public WTATemplateType getWtaTemplateType() {
        return wtaTemplateType;
    }

    public void setWtaTemplateType(WTATemplateType wtaTemplateType) {
        this.wtaTemplateType = wtaTemplateType;
    }
    public DaysOffAfterASeriesWTATemplate() {
        wtaTemplateType = WTATemplateType.DAYS_OFF_AFTER_A_SERIES;
    }


}

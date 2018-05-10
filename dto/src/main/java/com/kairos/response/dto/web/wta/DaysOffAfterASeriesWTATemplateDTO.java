package com.kairos.response.dto.web.wta;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.activity.persistence.enums.WTATemplateType;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DaysOffAfterASeriesWTATemplateDTO extends WTABaseRuleTemplateDTO {

    private long intervalLength;
    private String intervalUnit;
    private int sequence;
    private boolean isRestingTimeAllowed;
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

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public boolean isRestingTimeAllowed() {
        return isRestingTimeAllowed;
    }

    public void setRestingTimeAllowed(boolean restingTimeAllowed) {
        isRestingTimeAllowed = restingTimeAllowed;
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

    public DaysOffAfterASeriesWTATemplateDTO() {
        this.wtaTemplateType=WTATemplateType.DAYS_OFF_AFTER_A_SERIES;
    }
}

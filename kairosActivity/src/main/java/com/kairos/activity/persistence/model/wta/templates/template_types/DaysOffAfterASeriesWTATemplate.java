package com.kairos.activity.persistence.model.wta.templates.template_types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.activity.persistence.enums.WTATemplateType;
import com.kairos.activity.persistence.model.wta.templates.WTABaseRuleTemplate;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DaysOffAfterASeriesWTATemplate extends WTABaseRuleTemplate {

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

    public void setWtaTemplateType(WTATemplateType wtaTemplateType) {
        this.wtaTemplateType = wtaTemplateType;
    }
    public DaysOffAfterASeriesWTATemplate() {
        wtaTemplateType = WTATemplateType.DAYS_OFF_AFTER_A_SERIES;
    }

    public DaysOffAfterASeriesWTATemplate(String name, boolean disabled, String description, long intervalLength, String intervalUnit, int sequence) {
        this.name=name;
        this.disabled=disabled;
        this.description=description;
        this.intervalLength = intervalLength;
        this.intervalUnit = intervalUnit;
        this.sequence = sequence;
        wtaTemplateType=WTATemplateType.DAYS_OFF_AFTER_A_SERIES;
    }
}

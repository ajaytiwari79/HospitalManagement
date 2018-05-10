package com.kairos.activity.persistence.model.wta.templates.template_types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.activity.persistence.enums.PartOfDay;
import com.kairos.activity.persistence.enums.WTATemplateType;
import com.kairos.activity.persistence.model.wta.templates.WTABaseRuleTemplate;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class NoOfSequenceShiftWTATemplate extends WTABaseRuleTemplate{

    private int sequence;
    private boolean isRestingTimeAllowed;
    private int restingTime;
    private PartOfDay from;
    private PartOfDay to;

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

    public PartOfDay getFrom() {
        return from;
    }

    public void setFrom(PartOfDay from) {
        this.from = from;
    }

    public PartOfDay getTo() {
        return to;
    }

    public void setTo(PartOfDay to) {
        this.to = to;
    }

    public WTATemplateType getWtaTemplateType() {
        return wtaTemplateType;
    }

    public void setWtaTemplateType(WTATemplateType wtaTemplateType) {
        this.wtaTemplateType = wtaTemplateType;
    }

    public NoOfSequenceShiftWTATemplate() {
        wtaTemplateType=WTATemplateType.NO_OF_SEQUENCE_SHIFT;
    }

    public NoOfSequenceShiftWTATemplate(String name,boolean disabled, String description, int sequence,PartOfDay from,PartOfDay to) {
        this.name = name;
        this.disabled = disabled;
        this.description = description;
        this.sequence=sequence;
        this.to=to;
        this.from=from;
    }
}

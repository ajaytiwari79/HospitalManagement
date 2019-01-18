package com.kairos.persistence.model.wta.templates.template_types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.enums.wta.PartOfDay;
import com.kairos.enums.wta.WTATemplateType;
import com.kairos.persistence.model.wta.templates.WTABaseRuleTemplate;
import com.kairos.wrapper.wta.RuleTemplateSpecificInfo;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class NoOfSequenceShiftWTATemplate extends WTABaseRuleTemplate{

    //private int sequence;
    private boolean restingTimeAllowed;
    private int restingTime;
    private PartOfDay sequenceShiftFrom;
    private PartOfDay sequenceShiftTo;

    private List<BigInteger> timeTypeIds = new ArrayList<>();

   /* public int getNightShiftSequence() {
        return sequence;
    }

    public void setNightShiftSequence(int sequence) {
        this.sequence = sequence;
    }*/

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

    public PartOfDay getSequenceShiftFrom() {
        return sequenceShiftFrom;
    }

    public void setSequenceShiftFrom(PartOfDay sequenceShiftFrom) {
        this.sequenceShiftFrom = sequenceShiftFrom;
    }

    public PartOfDay getSequenceShiftTo() {
        return sequenceShiftTo;
    }

    public void setSequenceShiftTo(PartOfDay sequenceShiftTo) {
        this.sequenceShiftTo = sequenceShiftTo;
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
    

    public List<BigInteger> getTimeTypeIds() {
        return timeTypeIds;
    }

    public void setTimeTypeIds(List<BigInteger> timeTypeIds) {
        this.timeTypeIds = timeTypeIds;
    }

    @Override
    public void validateRules(RuleTemplateSpecificInfo infoWrapper) {

    }

    public NoOfSequenceShiftWTATemplate(String name, boolean disabled, String description,  PartOfDay sequenceShiftFrom, PartOfDay sequenceShiftTo) {
        this.name = name;
        this.disabled = disabled;
        this.description = description;
        this.wtaTemplateType = WTATemplateType.NO_OF_SEQUENCE_SHIFT;
        //this.sequence=sequence;
        this.sequenceShiftTo = sequenceShiftTo;
        this.sequenceShiftFrom = sequenceShiftFrom;
    }

    @Override
    public boolean isCalculatedValueChanged(WTABaseRuleTemplate wtaBaseRuleTemplate) {
        NoOfSequenceShiftWTATemplate noOfSequenceShiftWTATemplate = (NoOfSequenceShiftWTATemplate)wtaBaseRuleTemplate;
        return (this != noOfSequenceShiftWTATemplate) && !(restingTimeAllowed == noOfSequenceShiftWTATemplate.restingTimeAllowed &&
                restingTime == noOfSequenceShiftWTATemplate.restingTime &&
                sequenceShiftFrom == noOfSequenceShiftWTATemplate.sequenceShiftFrom &&
                sequenceShiftTo == noOfSequenceShiftWTATemplate.sequenceShiftTo &&
                Objects.equals(timeTypeIds, noOfSequenceShiftWTATemplate.timeTypeIds) && Objects.equals(this.phaseTemplateValues,noOfSequenceShiftWTATemplate.phaseTemplateValues));
    }

}

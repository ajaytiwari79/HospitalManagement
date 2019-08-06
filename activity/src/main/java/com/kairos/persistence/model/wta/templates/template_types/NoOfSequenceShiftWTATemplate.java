package com.kairos.persistence.model.wta.templates.template_types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.enums.wta.PartOfDay;
import com.kairos.enums.wta.WTATemplateType;
import com.kairos.persistence.model.wta.templates.WTABaseRuleTemplate;
import com.kairos.wrapper.wta.RuleTemplateSpecificInfo;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class NoOfSequenceShiftWTATemplate extends WTABaseRuleTemplate{

    //private int sequence;
    private boolean restingTimeAllowed;
    private int restingTime;
    private PartOfDay sequenceShiftFrom;
    private PartOfDay sequenceShiftTo;

    private List<BigInteger> timeTypeIds = new ArrayList<>();

    public NoOfSequenceShiftWTATemplate() {
        wtaTemplateType=WTATemplateType.NO_OF_SEQUENCE_SHIFT;
    }

    @Override
    public void validateRules(RuleTemplateSpecificInfo infoWrapper) {
        //This is override method
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

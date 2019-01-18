package com.kairos.persistence.model.wta.templates.template_types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.activity.wta.templates.BreakTemplateValue;
import com.kairos.persistence.model.wta.templates.WTABaseRuleTemplate;
import com.kairos.wrapper.wta.RuleTemplateSpecificInfo;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by pavan on 20/4/18.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BreaksInShiftWTATemplate extends WTABaseRuleTemplate{
    private List<BreakTemplateValue> breakTemplateValues;
    private List<BigInteger> timeTypeIds = new ArrayList<>();
    private List<BigInteger> plannedTimeIds = new ArrayList<>();;


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



    @Override
    public void validateRules(RuleTemplateSpecificInfo infoWrapper) {

    }

    public BreaksInShiftWTATemplate(String name,  boolean disabled, String description, List<BreakTemplateValue> breakTemplateValues) {
        this.name = name;
        this.disabled = disabled;
        this.description = description;
        this.breakTemplateValues=breakTemplateValues;
    }

    public List<BreakTemplateValue> getBreakTemplateValues() {
        return breakTemplateValues;
    }

    public void setBreakTemplateValues(List<BreakTemplateValue> breakTemplateValues) {
        this.breakTemplateValues = breakTemplateValues;
    }

    @Override
    public boolean isCalculatedValueChanged(WTABaseRuleTemplate wtaBaseRuleTemplate) {
        BreaksInShiftWTATemplate breaksInShiftWTATemplate = (BreaksInShiftWTATemplate)wtaBaseRuleTemplate;
        return (this != breaksInShiftWTATemplate) && !(Objects.equals(breakTemplateValues, breaksInShiftWTATemplate.breakTemplateValues) &&
                Objects.equals(timeTypeIds, breaksInShiftWTATemplate.timeTypeIds) &&
                Objects.equals(plannedTimeIds, breaksInShiftWTATemplate.plannedTimeIds));
    }
}

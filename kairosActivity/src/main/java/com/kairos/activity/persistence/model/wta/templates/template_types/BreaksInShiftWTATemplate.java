package com.kairos.activity.persistence.model.wta.templates.template_types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.activity.persistence.enums.WTATemplateType;
import com.kairos.activity.persistence.model.wta.templates.BreakTemplateValue;
import com.kairos.activity.persistence.model.wta.templates.WTABaseRuleTemplate;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pavan on 20/4/18.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BreaksInShiftWTATemplate extends WTABaseRuleTemplate {
    private List<BreakTemplateValue> breakTemplateValues;
    private List<BigInteger> timeTypeIds = new ArrayList<>();
    private List<Long> plannedTimeIds = new ArrayList<>();;


    public List<BigInteger> getTimeTypeIds() {
        return timeTypeIds;
    }

    public void setTimeTypeIds(List<BigInteger> timeTypeIds) {
        this.timeTypeIds = timeTypeIds;
    }

    public List<Long> getPlannedTimeIds() {
        return plannedTimeIds;
    }

    public void setPlannedTimeIds(List<Long> plannedTimeIds) {
        this.plannedTimeIds = plannedTimeIds;
    }

    public BreaksInShiftWTATemplate() {
        this.wtaTemplateType = WTATemplateType.BREAK_IN_SHIFT;
    }
    public BreaksInShiftWTATemplate(String name,  boolean disabled, String description, List<BreakTemplateValue> breakTemplateValues) {
        this.name = name;
        this.disabled = disabled;
        this.description = description;
        this.breakTemplateValues=breakTemplateValues;
        this.wtaTemplateType = WTATemplateType.BREAK_IN_SHIFT;
    }

    public List<BreakTemplateValue> getBreakTemplateValues() {
        return breakTemplateValues;
    }

    public void setBreakTemplateValues(List<BreakTemplateValue> breakTemplateValues) {
        this.breakTemplateValues = breakTemplateValues;
    }
}

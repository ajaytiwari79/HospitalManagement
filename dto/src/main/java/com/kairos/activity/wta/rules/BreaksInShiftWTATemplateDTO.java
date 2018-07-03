package com.kairos.activity.wta.rules;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.activity.wta.basic_details.WTABaseRuleTemplateDTO;
import com.kairos.enums.WTATemplateType;
import com.kairos.activity.wta.templates.BreakTemplateValue;

import java.math.BigInteger;
import java.util.List;

/**
 * Created by pavan on 20/4/18.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BreaksInShiftWTATemplateDTO extends WTABaseRuleTemplateDTO {
    private List<BreakTemplateValue> breakTemplateValues;
    private List<BigInteger> timeTypeIds;
    private List<BigInteger> plannedTimeIds;


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

    public BreaksInShiftWTATemplateDTO() {
        this.wtaTemplateType = WTATemplateType.BREAK_IN_SHIFT;
    }
    public BreaksInShiftWTATemplateDTO(String name, String templateType, boolean disabled, String description, List<BreakTemplateValue> breakTemplateValues) {
        this.name = name;
        //this.templateType = WTATemplateType.;
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
}

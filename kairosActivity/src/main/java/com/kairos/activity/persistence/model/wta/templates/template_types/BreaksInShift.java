package com.kairos.activity.persistence.model.wta.templates.template_types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.activity.persistence.enums.WTATemplateType;
import com.kairos.activity.persistence.model.wta.templates.BreakTemplateValue;
import com.kairos.activity.persistence.model.wta.templates.WTABaseRuleTemplate;

import java.util.List;

/**
 * Created by pavan on 20/4/18.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BreaksInShift extends WTABaseRuleTemplate {
    private List<BreakTemplateValue> breakTemplateValues;

    public BreaksInShift() {
        //Default Constructor
    }
    public BreaksInShift(String name, String templateType, boolean disabled, String description, List<BreakTemplateValue> breakTemplateValues) {
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

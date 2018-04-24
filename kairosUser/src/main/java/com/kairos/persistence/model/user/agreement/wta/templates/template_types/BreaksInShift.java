package com.kairos.persistence.model.user.agreement.wta.templates.template_types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.user.agreement.wta.templates.BreakTemplateValue;
import com.kairos.persistence.model.user.agreement.wta.templates.WTABaseRuleTemplate;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.List;

import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_BREAK_MATRIX;

/**
 * Created by pavan on 20/4/18.
 */
@NodeEntity
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.ALWAYS)
public class BreaksInShift extends WTABaseRuleTemplate{
    @Relationship(type = HAS_BREAK_MATRIX)
    private List<BreakTemplateValue> breakTemplateValues;

    public BreaksInShift() {
        //Default Constructor
    }
    public BreaksInShift(String name, String templateType, boolean disabled, String description, List<BreakTemplateValue> breakTemplateValues) {
        this.name = name;
        this.templateType = templateType;
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

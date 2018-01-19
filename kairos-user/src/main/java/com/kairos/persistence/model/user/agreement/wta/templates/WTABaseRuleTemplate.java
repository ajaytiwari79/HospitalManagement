package com.kairos.persistence.model.user.agreement.wta.templates;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.persistence.model.user.agreement.cta.RuleTemplate;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.springframework.beans.BeanUtils;

import java.util.List;

import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_TEMPLATE_MATRIX;

/**
 * Created by vipul on 26/7/17.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
@NodeEntity
public class WTABaseRuleTemplate extends RuleTemplate{

    protected String templateType;
    @Relationship(type = HAS_TEMPLATE_MATRIX)
    protected List<PhaseTemplateValue> phaseTemplateValues;
    protected int recommendedValue;

    public int getRecommendedValue() {
        return recommendedValue;
    }

    public void setRecommendedValue(int recommendedValue) {
        this.recommendedValue = recommendedValue;
    }

    public List<PhaseTemplateValue> getPhaseTemplateValues() {
        return phaseTemplateValues;
    }

    public void setPhaseTemplateValues(List<PhaseTemplateValue> phaseTemplateValues) {
        this.phaseTemplateValues = phaseTemplateValues;
    }



    public WTABaseRuleTemplate(){}

    public WTABaseRuleTemplate(String name, String templateType, String description) {
        this.name = name;
        this.templateType = templateType;
        this.description = description;
    }

    @Override
    public String toString() {
        return "WTABaseRuleTemplate{" +
                "phaseTemplateValues=" + phaseTemplateValues +
                ", templateType='" + templateType + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTemplateType() {
        return templateType;
    }

    public void setTemplateType(String templateType) {
        this.templateType = templateType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static WTABaseRuleTemplate copyProperties(WTABaseRuleTemplate source, WTABaseRuleTemplate target){
        BeanUtils.copyProperties(source,target);
        return target;
    }

}

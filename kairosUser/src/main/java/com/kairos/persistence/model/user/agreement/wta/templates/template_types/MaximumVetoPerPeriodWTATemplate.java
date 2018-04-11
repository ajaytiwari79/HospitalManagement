package com.kairos.persistence.model.user.agreement.wta.templates.template_types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.user.agreement.wta.templates.WTABaseRuleTemplate;
import org.neo4j.ogm.annotation.NodeEntity;

/**
 * Created by pawanmandhan on 5/8/17.
 * TEMPLATE12
 */@NodeEntity
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MaximumVetoPerPeriodWTATemplate extends WTABaseRuleTemplate {

    private double maximumVetoPercentage;

    public double getMaximumVetoPercentage() {
        return maximumVetoPercentage;
    }

    public void setMaximumVetoPercentage(double maximumVetoPercentage) {
        this.maximumVetoPercentage = maximumVetoPercentage;
    }

    public MaximumVetoPerPeriodWTATemplate(String name, String templateType, boolean disabled,
                                           String description, double maximumVetoPercentage) {
        this.name = name;
        this.templateType = templateType;
        this.disabled = disabled;
        this.description = description;
        this.maximumVetoPercentage =maximumVetoPercentage;

    }
    public MaximumVetoPerPeriodWTATemplate() {
    }

}

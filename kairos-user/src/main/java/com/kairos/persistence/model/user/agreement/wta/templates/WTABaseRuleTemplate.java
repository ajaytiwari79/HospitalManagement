package com.kairos.persistence.model.user.agreement.wta.templates;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.persistence.model.user.agreement.cta.RuleTemplate;
import org.neo4j.ogm.annotation.NodeEntity;
import org.springframework.beans.BeanUtils;

/**
 * Created by vipul on 26/7/17.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
@NodeEntity
public class WTABaseRuleTemplate extends RuleTemplate{
    protected String templateType;
    public WTABaseRuleTemplate(){}

    public WTABaseRuleTemplate(String name, String templateType, String description) {
        this.name = name;
        this.templateType = templateType;
        this.description = description;
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

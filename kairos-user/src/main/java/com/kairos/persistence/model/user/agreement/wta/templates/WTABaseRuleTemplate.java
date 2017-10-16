package com.kairos.persistence.model.user.agreement.wta.templates;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.common.UserBaseEntity;
import org.neo4j.ogm.annotation.NodeEntity;
import org.springframework.beans.BeanUtils;

/**
 * Created by pawanmandhan on 26/7/17.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@NodeEntity
public class WTABaseRuleTemplate extends UserBaseEntity{

    protected String name;
    protected String templateType;
    protected boolean isActive=true;
    protected String description;

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

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
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

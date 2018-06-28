package com.kairos.wrapper;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.persistence.model.agreement.wta.templates.WTABaseRuleTemplate;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by prerna on 29/11/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AddRuleTemplateCategoryDTO {

    @NotEmpty(message = "error.RuleTemplate.description.notEmpty")
    @NotNull(message = "error.RuleTemplate.description.notnull")
    private String name;
    //@NotEmpty(message = "error.RuleTemplate.description.notEmpty")
    //@NotNull(message = "error.RuleTemplate.description.name.notnull")
    private String description;

    private List<WTABaseRuleTemplate> wtaBaseRuleTemplates;
    private List<Long> tags;

    AddRuleTemplateCategoryDTO(){}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<WTABaseRuleTemplate> getWtaBaseRuleTemplates() {
        return wtaBaseRuleTemplates;
    }

    public void setWtaBaseRuleTemplates(List<WTABaseRuleTemplate> wtaBaseRuleTemplates) {
        this.wtaBaseRuleTemplates = wtaBaseRuleTemplates;
    }

    public List<Long> getTags() {
        return tags;
    }

    public void setTags(List<Long> tags) {
        this.tags = tags;
    }
}

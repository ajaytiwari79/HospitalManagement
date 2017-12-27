package com.kairos.response.dto.web;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.persistence.model.organization.AddressDTO;
import com.kairos.persistence.model.user.agreement.wta.templates.WTABaseRuleTemplate;
import org.hibernate.validator.constraints.NotEmpty;
import org.neo4j.ogm.annotation.Relationship;

import javax.validation.constraints.NotNull;
import java.util.List;

import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_RULE_TEMPLATES;

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

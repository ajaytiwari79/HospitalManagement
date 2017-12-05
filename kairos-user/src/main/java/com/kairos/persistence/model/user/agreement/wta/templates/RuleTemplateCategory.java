package com.kairos.persistence.model.user.agreement.wta.templates;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.user.agreement.cta.RuleTemplate;
import com.kairos.persistence.model.user.agreement.cta.RuleTemplateCategoryType;
import com.kairos.persistence.model.user.country.Country;
import org.hibernate.validator.constraints.NotEmpty;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_RULE_TEMPLATES;
import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_RULE_TEMPLATE_CATEGORY;
import static org.neo4j.ogm.annotation.Relationship.UNDIRECTED;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@NodeEntity
public class RuleTemplateCategory extends UserBaseEntity {

    @NotEmpty(message = "error.RuleTemplate.description.notEmpty")
    @NotNull(message = "error.RuleTemplate.description.notnull")
    private String name;
    @NotEmpty(message = "error.RuleTemplate.description.notEmpty")
    @NotNull(message = "error.RuleTemplate.description.name.notnull")
    private String description;
    private RuleTemplateCategoryType ruleTemplateCategoryType;
    @JsonIgnore
    @Relationship(type = HAS_RULE_TEMPLATE_CATEGORY,direction =UNDIRECTED )
    private Country country;
    @JsonBackReference
    @Relationship(type = HAS_RULE_TEMPLATES,direction =UNDIRECTED)
    private List<RuleTemplate> ruleTemplates=new ArrayList<>();
    public RuleTemplateCategory(String name, String description, boolean deleted) {
        this.name = name;
        this.description = description;
        this.deleted = deleted;
    }

    public RuleTemplateCategory() {
        //default
    }

    public RuleTemplateCategory(String name) {
        this.name = name;
    }

    public List<RuleTemplate> getRuleTemplates() {
        return ruleTemplates;
    }

    public void setRuleTemplates(List<RuleTemplate> ruleTemplates) {
        this.ruleTemplates = ruleTemplates;
    }

    public void addRuleTemplate(RuleTemplate ruleTemplate) {
        if (ruleTemplate == null)
            throw new NullPointerException("Can't add null ruleTemplate");
        if (ruleTemplate.getRuleTemplateCategory() != null)
            throw new IllegalStateException("ruleTemplate is already assigned to an RuleTemplateCategory");
        getRuleTemplates().add(ruleTemplate);
        ruleTemplate.setRuleTemplateCategory(this);
    }
    public void removeRuleTemplate(RuleTemplate ruleTemplate) {
        if (ruleTemplate == null)
            throw new NullPointerException("Can't add null ruleTemplate");
        getRuleTemplates().remove(ruleTemplate);
        ruleTemplate.setRuleTemplateCategory(null);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }
    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public RuleTemplateCategoryType getRuleTemplateCategoryType() {
        return ruleTemplateCategoryType;
    }

    public void setRuleTemplateCategoryType(RuleTemplateCategoryType ruleTemplateCategoryType) {
        this.ruleTemplateCategoryType = ruleTemplateCategoryType;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public Map<String, Object> printRuleTemp() {
        Map<String, Object> mp = new HashMap();
        mp.put("id", this.getId());
        mp.put("name", this.getName());
        mp.put("description", this.getDescription());
        mp.put("lastModificationDate", this.getLastModificationDate());
        mp.put("creationDate", this.getCreationDate());
        return mp;
    }



}


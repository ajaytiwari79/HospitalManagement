package com.kairos.user.agreement.cta;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.user.agreement.wta.templates.RuleTemplateCategory;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_RULE_TEMPLATES;
import static org.neo4j.ogm.annotation.Relationship.UNDIRECTED;
@JsonIgnoreProperties(ignoreUnknown = true)
@NodeEntity
public  abstract class RuleTemplate extends UserBaseEntity {
    protected String name;
    protected String description;
    protected boolean disabled;
    @JsonManagedReference
    @Relationship(type = HAS_RULE_TEMPLATES,direction =UNDIRECTED)
    protected RuleTemplateCategory ruleTemplateCategory;


    public RuleTemplate() {
        //default constructor
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

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public RuleTemplateCategory getRuleTemplateCategory() {
        return ruleTemplateCategory;
    }

    public void setRuleTemplateCategory(RuleTemplateCategory ruleTemplateCategory) {
        this.ruleTemplateCategory = ruleTemplateCategory;
    }

}

package com.kairos.persistence.model.user.agreement.wta.templates;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.common.UserBaseEntity;
import org.hibernate.validator.constraints.NotEmpty;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import javax.validation.constraints.NotNull;
import java.util.*;

import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_RULE_TEMPLATES;

/**
 * Created by vipul on 2/8/17.
 * used to store Rule Template Category
 */


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

    @Relationship(type = HAS_RULE_TEMPLATES)
    private List<WTABaseRuleTemplate> wtaBaseRuleTemplates;

    public List<WTABaseRuleTemplate> getWtaBaseRuleTemplates() {
        return Optional.ofNullable(wtaBaseRuleTemplates).orElse(new ArrayList<>());
    }

    public void setWtaBaseRuleTemplates(List<WTABaseRuleTemplate> wtaBaseRuleTemplates) {
        this.wtaBaseRuleTemplates = wtaBaseRuleTemplates;
    }

    boolean deleted =false;

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

    public Map<String, Object> printRuleTemp() {
        Map<String, Object> mp = new HashMap();
        mp.put("id", this.getId());
        mp.put("name", this.getName());
        mp.put("description", this.getDescription());
        mp.put("lastModificationDate", this.getLastModificationDate());
        mp.put("creationDate", this.getCreationDate());
        return mp;
    }


    public RuleTemplateCategory(String name, String description, boolean deleted) {
        this.name = name;
        this.description = description;
        this.deleted = deleted;
    }

    public RuleTemplateCategory() {
    }

    public RuleTemplateCategory(String name) {
        this.name = name;
    }
}


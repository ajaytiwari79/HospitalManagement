package com.kairos.persistence.model.wta.templates;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.enums.RuleTemplateCategoryType;
import com.kairos.persistence.model.common.MongoBaseEntity;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by vipul on 2/8/17.
 * used to store Rule Template Category
 */


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class WTARuleTemplateCategory extends MongoBaseEntity {

     @NotNull(message = "error.ruleTemplateCategoryId.name.notnull")
    private String name;
    private String description;
    private RuleTemplateCategoryType ruleTemplateCategoryType = RuleTemplateCategoryType.WTA;
    private Long country;


    public WTARuleTemplateCategory() {
        //default
    }

    public WTARuleTemplateCategory(String name) {
        this.name = name;
    }

    private List<Long> tags = new ArrayList<>();

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

    public Long getCountry() {
        return country;
    }

    public void setCountry(Long country) {
        this.country = country;
    }
    public List<Long> getTags() {
        return tags;
    }

    public void setTags(List<Long> tags) {
        this.tags = tags;
    }


    public WTARuleTemplateCategory(@NotNull(message = "error.RuleTemplate.description.notnull") String name, RuleTemplateCategoryType ruleTemplateCategoryType) {
        this.name = name;
        this.ruleTemplateCategoryType = ruleTemplateCategoryType;
    }
}


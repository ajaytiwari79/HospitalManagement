package com.kairos.persistence.model.wta.templates;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.common.MongoBaseEntity;
import com.kairos.enums.RuleTemplateCategoryType;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Created by vipul on 2/8/17.
 * used to store Rule Template Category
 */


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Document
public class RuleTemplateCategory extends MongoBaseEntity {

     @NotNull(message = "error.RuleTemplateCategory.name.notnull")
    private String name;
    private String description;
    private RuleTemplateCategoryType ruleTemplateCategoryType;
    private Long countryId;
    private List<BigInteger> tags = new ArrayList<>();

    public RuleTemplateCategory(String name, String description, RuleTemplateCategoryType ruleTemplateCategoryType) {
        this.name = name;
        this.description = description;
        this.deleted = false;
        this.ruleTemplateCategoryType = ruleTemplateCategoryType;
    }

    public RuleTemplateCategory() {
        //default
    }

    public RuleTemplateCategory(String name) {
        this.name = name;
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

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }
    public List<BigInteger> getTags() {
        return tags;
    }

    public void setTags(List<BigInteger> tags) {
        this.tags = tags;
    }


    public Map<String, Object> printRuleTemp() {
        Map<String, Object> mp = new HashMap();
        mp.put("id", this.getId());
        mp.put("name", this.getName());
        mp.put("description", this.getDescription());
        mp.put("lastModificationDate", this.getUpdatedAt());
        mp.put("creationDate", this.getCreatedAt());
        return mp;
    }


}


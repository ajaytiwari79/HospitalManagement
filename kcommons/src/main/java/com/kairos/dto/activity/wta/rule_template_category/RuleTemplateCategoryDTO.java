package com.kairos.dto.activity.wta.rule_template_category;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.kairos.enums.RuleTemplateCategoryType;
import com.kairos.dto.activity.tags.TagDTO;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by vipul on 2/8/17.
 * used to store Rule Template Category
 */


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RuleTemplateCategoryDTO {


    @NotNull(message = "error.RuleTemplateCategory.name.notnull")
    // @JsonProperty(value = "categoryName")
    private String name;
    private String description;
    private RuleTemplateCategoryType ruleTemplateCategoryType;
    private Long country;
    private BigInteger id;
    private Date createdAt;
    private Date updatedAt;
    private boolean deleted;
    private List<BigInteger> ruleTemplateIds;

    private List<TagDTO> tags = new ArrayList<>();


    public RuleTemplateCategoryDTO(String name, String description, boolean deleted) {
        this.name = name;
        this.description = description;
        this.deleted = deleted;
    }

    public RuleTemplateCategoryDTO(@NotNull(message = "error.RuleTemplateCategory.name.notnull") String name, String description, Long country, BigInteger id) {
        this.name = name;
        this.description = description;
        this.country = country;
        this.id = id;
    }

    public RuleTemplateCategoryDTO() {
        //default
    }

    public List<BigInteger> getRuleTemplateIds() {
        return ruleTemplateIds;
    }

    public void setRuleTemplateIds(List<BigInteger> ruleTemplateIds) {
        this.ruleTemplateIds = ruleTemplateIds;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }


    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public RuleTemplateCategoryDTO(String name) {
        this.name = name;
    }


    public String getName() {
        return name;
    }

    @JsonSetter("categoryName")
    public void setCategoryategoryName(String name) {
        if (this.name == null) {
            this.name = name;
        }
    }

    @JsonSetter("name")
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

    public List<TagDTO> getTags() {
        return tags;
    }

    public void setTags(List<TagDTO> tags) {
        this.tags = tags;
    }
}


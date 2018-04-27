package com.kairos.response.dto.web.wta;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.response.dto.web.enums.RuleTemplateCategoryType;

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
public class RuleTemplateCategoryDTO{

    @NotNull(message = "error.RuleTemplateCategory.name.notnull")
    private String name;
    private String description;
    private RuleTemplateCategoryType ruleTemplateCategoryType;
    private Long country;
    private BigInteger id;
    private Date createdAt;
    private Date updatedAt;
    private boolean deleted ;
    public RuleTemplateCategoryDTO(String name, String description, boolean deleted) {
        this.name = name;
        this.description = description;
        this.deleted = deleted;
    }

    public RuleTemplateCategoryDTO() {
        //default
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

}


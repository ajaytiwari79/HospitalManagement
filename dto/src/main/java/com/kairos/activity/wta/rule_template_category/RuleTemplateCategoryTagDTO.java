package com.kairos.activity.wta.rule_template_category;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.activity.tags.TagDTO;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by prerna on 29/11/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RuleTemplateCategoryTagDTO {

    private BigInteger id;
    private String name;
    private String description;
    private List<TagDTO> tags = new ArrayList<>();
    private List<BigInteger> ruleTemplateIds;

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
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

    public List<BigInteger> getRuleTemplateIds() {
        return ruleTemplateIds;
    }

    public void setRuleTemplateIds(List<BigInteger> ruleTemplateIds) {
        this.ruleTemplateIds = ruleTemplateIds;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<TagDTO> getTags() {
        return tags;
    }

    public void setTags(List<TagDTO> tags) {
        this.tags = tags;
    }
}

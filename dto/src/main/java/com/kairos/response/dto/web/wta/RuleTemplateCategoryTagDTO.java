package com.kairos.response.dto.web.wta;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.response.dto.web.tag.TagDTO;


import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by prerna on 29/11/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RuleTemplateCategoryTagDTO {

    private Long id;
    private String name;
    private String description;
    private List<TagDTO> tags = new ArrayList<>();
    private List<BigInteger> ruleTemplates;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public List<BigInteger> getRuleTemplates() {
        return ruleTemplates;
    }

    public void setRuleTemplates(List<BigInteger> ruleTemplates) {
        this.ruleTemplates = ruleTemplates;
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

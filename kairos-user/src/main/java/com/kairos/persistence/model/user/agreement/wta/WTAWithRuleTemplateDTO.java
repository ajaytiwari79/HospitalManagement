package com.kairos.persistence.model.user.agreement.wta;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.user.agreement.wta.templates.WTARuleTemplateQueryResponse;
import com.kairos.persistence.model.user.expertise.Expertise;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;

/**
 * Created by vipul on 8/11/17.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@QueryResult
public class WTAWithRuleTemplateDTO {
    private Long startDateMillis;
    private Long endDateMillis;
    private Long expiryDate;
    private String name;
    private String description;
    private long id;

    private Expertise expertise;
    private List<WTARuleTemplateQueryResponse> ruleTemplates;

    public Long getStartDateMillis() {
        return startDateMillis;
    }

    public void setStartDateMillis(Long startDateMillis) {
        this.startDateMillis = startDateMillis;
    }

    public Long getEndDateMillis() {
        return endDateMillis;
    }

    public void setEndDateMillis(Long endDateMillis) {
        this.endDateMillis = endDateMillis;
    }

    public Long getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Long expiryDate) {
        this.expiryDate = expiryDate;
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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Expertise getExpertise() {
        return expertise;
    }

    public void setExpertise(Expertise expertise) {
        this.expertise = expertise;
    }

    public List<WTARuleTemplateQueryResponse> getRuleTemplates() {

        return ruleTemplates;

    }

    public void setRuleTemplates(List<WTARuleTemplateQueryResponse> ruleTemplates) {
        this.ruleTemplates = ruleTemplates;
    }
}

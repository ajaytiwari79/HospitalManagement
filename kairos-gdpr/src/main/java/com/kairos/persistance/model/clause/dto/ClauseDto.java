package com.kairos.persistance.model.clause.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kairos.persistance.model.clause.Clause;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ClauseDto {


    @NotEmpty(message = "error.clause.title.cannotbe.empty")
    @NotNull(message = "error.clause.title.cannotbe.null")
    private String title;

    private List<String> tags = new ArrayList<>();

    @NotEmpty(message = "error.clause.title.cannotbe.empty")
    @NotNull(message = "error.clause.title.cannotbe.null")
    private String description;

    @NotNull(message = "error.message.list.cannot.be.null")
    @NotEmpty(message = "error.message.list.cannot.be.empty")
    private Set<Long> organisationType;

    @NotNull(message = "error.message.list.cannot.be.null")
    @NotEmpty(message = "error.message.list.cannot.be.empty")
    private Set <Long> organisationSubType;

    @NotNull(message = "error.message.list.cannot.be.null")
    @NotEmpty(message = "error.message.list.cannot.be.empty")
    private Set <Long>organisationService;

    @NotNull(message = "error.message.list.cannot.be.null")
    @NotEmpty(message = "error.message.list.cannot.be.empty")
    private Set <Long> organisationSubService;

    private List<BigInteger> accountType;

    public String getTitle() {
        return title;
    }

    public List<String> getTags() {
        return tags;
    }

    public String getDescription() {
        return description;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Long> getOrganisationType() {
        return organisationType;
    }

    public void setOrganisationType(Set<Long> organisationType) {
        this.organisationType = organisationType;
    }

    public Set<Long> getOrganisationSubType() {
        return organisationSubType;
    }

    public void setOrganisationSubType(Set<Long> organisationSubType) {
        this.organisationSubType = organisationSubType;
    }

    public Set<Long> getOrganisationService() {
        return organisationService;
    }

    public void setOrganisationService(Set<Long> organisationService) {
        this.organisationService = organisationService;
    }

    public Set<Long> getOrganisationSubService() {
        return organisationSubService;
    }

    public void setOrganisationSubService(Set<Long> organisationSubService) {
        this.organisationSubService = organisationSubService;
    }

    public void setAccountType(List<BigInteger> accountType) {
        this.accountType = accountType;
    }

    public List<BigInteger> getAccountType() {
        return accountType;
    }

}

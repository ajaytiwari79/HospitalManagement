package com.kairos.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClauseDto {


    @NotEmpty(message = "error.clause.title.cannotbe.empty")
    private String title;

    @NotEmpty(message = "error.message.list.cannot.be.empty")
    private List<ClauseTagDto> tags = new ArrayList<>();

    @NotNull(message = "error.clause.title.cannotbe.null")
    private String description;

    @NotEmpty(message = "error.message.list.cannot.be.empty")
    private Set<Long> organizationTypes;

    @NotEmpty(message = "error.message.list.cannot.be.empty")
    private Set <Long> organizationSubTypes;

    @NotEmpty(message = "error.message.list.cannot.be.empty")
    private Set <Long>organizationServices;

    @NotEmpty(message = "error.message.list.cannot.be.empty")
    private Set <Long> organizationSubServices;

    @NotEmpty(message = "error.message.list.cannot.be.empty")
    private Set<BigInteger> accountType;

    public String getTitle() {
        return title;
    }



    public String getDescription() {
        return description;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<ClauseTagDto> getTags() {
        return tags;
    }

    public void setTags(List<ClauseTagDto> tags) {
        this.tags = tags;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Long> getOrganizationTypes() {
        return organizationTypes;
    }

    public void setOrganizationTypes(Set<Long> organizationTypes) {
        this.organizationTypes = organizationTypes;
    }

    public Set<Long> getOrganizationSubTypes() {
        return organizationSubTypes;
    }

    public void setOrganizationSubTypes(Set<Long> organizationSubTypes) {
        this.organizationSubTypes = organizationSubTypes;
    }

    public Set<Long> getOrganizationServices() {
        return organizationServices;
    }

    public void setOrganizationServices(Set<Long> organizationServices) {
        this.organizationServices = organizationServices;
    }

    public Set<Long> getOrganizationSubServices() {
        return organizationSubServices;
    }

    public void setOrganizationSubServices(Set<Long> organizationSubServices) {
        this.organizationSubServices = organizationSubServices;
    }

    public Set<BigInteger> getAccountType() {
        return accountType;
    }

    public void setAccountType(Set<BigInteger> accountType) {
        this.accountType = accountType;
    }
}

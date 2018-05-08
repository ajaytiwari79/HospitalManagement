package com.kairos.persistance.model.agreement_template.response.dto;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.persistance.country.Country;
import com.kairos.persistance.model.clause.AccountType;
import com.kairos.persistance.model.clause.Clause;
import com.kairos.persistance.model.organization.OrganizationService;
import com.kairos.persistance.model.organization.OrganizationType;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown=true)
public class AgreementQueryResult {



    private BigInteger id;
    private String name;

    private Date startDate;

    private Date endDate;


    String description;
    OrganizationType organisationType;
    OrganizationService orgService;
    AccountType accountType;
    List<Clause> clauses;

    Country country;

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

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public OrganizationType getOrganisationType() {
        return organisationType;
    }

    public void setOrganisationType(OrganizationType organisationType) {
        this.organisationType = organisationType;
    }

    public OrganizationService getOrgService() {
        return orgService;
    }

    public void setOrgService(OrganizationService orgService) {
        this.orgService = orgService;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public List<Clause> getClauses() {
        return clauses;
    }

    public void setClauses(List<Clause> clauses) {
        this.clauses = clauses;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }
}

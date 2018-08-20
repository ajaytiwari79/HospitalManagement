package com.kairos.persistence.model.organization;

import com.kairos.persistence.model.country.default_data.BusinessType;
import com.kairos.persistence.model.country.default_data.CompanyCategory;
import com.kairos.persistence.model.country.default_data.account_type.AccountType;
import com.kairos.user.organization.CompanyType;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;

/**
 * Created by vipul on 26/2/18.
 */
@QueryResult
public class OrganizationBasicResponse {
    private Long id;
    private String name;
    private String shortCompanyName;
    private String description;
    private String desiredUrl;
    private CompanyCategory companyCategory;
    private List<BusinessType> businessTypes;
    private CompanyType companyType;
    private String vatId;
    private AccountType accountType;
    private Boolean boardingCompleted;

    public OrganizationBasicResponse() {
    }

    public OrganizationBasicResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

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

    public String getShortCompanyName() {
        return shortCompanyName;
    }

    public void setShortCompanyName(String shortCompanyName) {
        this.shortCompanyName = shortCompanyName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDesiredUrl() {
        return desiredUrl;
    }

    public void setDesiredUrl(String desiredUrl) {
        this.desiredUrl = desiredUrl;
    }

    public CompanyCategory getCompanyCategory() {
        return companyCategory;
    }

    public void setCompanyCategory(CompanyCategory companyCategory) {
        this.companyCategory = companyCategory;
    }

    public List<BusinessType> getBusinessTypes() {
        return businessTypes;
    }

    public void setBusinessTypes(List<BusinessType> businessTypes) {
        this.businessTypes = businessTypes;
    }

    public CompanyType getCompanyType() {
        return companyType;
    }

    public void setCompanyType(CompanyType companyType) {
        this.companyType = companyType;
    }

    public String getVatId() {
        return vatId;
    }

    public void setVatId(String vatId) {
        this.vatId = vatId;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public Boolean getBoardingCompleted() {
        return boardingCompleted;
    }

    public void setBoardingCompleted(Boolean boardingCompleted) {
        this.boardingCompleted = boardingCompleted;
    }
}

package com.kairos.user.organization;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.enums.OrganizationLevel;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by prabjot on 20/1/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrganizationBasicDTO {
    private Long id;
    @NotNull(message = "error.name.notnull")
    private String name;
    private String shortCompanyName;
    private String description;
    private String desiredUrl;
    private Long companyCategoryId;
    private List<Long> businessTypeIds;
    private CompanyType companyType;
    private String vatId;
    private Long accountTypeId;
    private Long levelId;
    private Long typeId;
    private List<Long> subTypeId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name.trim();
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

    public Long getCompanyCategoryId() {
        return companyCategoryId;
    }

    public void setCompanyCategoryId(Long companyCategoryId) {
        this.companyCategoryId = companyCategoryId;
    }

    public List<Long> getBusinessTypeIds() {
        return Optional.ofNullable(businessTypeIds).orElse(new ArrayList<>());
    }

    public void setBusinessTypeIds(List<Long> businessTypeIds) {
        this.businessTypeIds = businessTypeIds;
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

    public Long getAccountTypeId() {
        return accountTypeId;
    }

    public void setAccountTypeId(Long accountTypeId) {
        this.accountTypeId = accountTypeId;
    }

    public Long getLevelId() {
        return levelId;
    }

    public void setLevelId(Long levelId) {
        this.levelId = levelId;
    }

    public Long getTypeId() {
        return typeId;
    }

    public void setTypeId(Long typeId) {
        this.typeId = typeId;
    }

    public List<Long> getSubTypeId() {
        return subTypeId;
    }

    public void setSubTypeId(List<Long> subTypeId) {
        this.subTypeId = subTypeId;
    }
}

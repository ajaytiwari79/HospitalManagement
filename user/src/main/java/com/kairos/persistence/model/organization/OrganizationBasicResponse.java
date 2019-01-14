package com.kairos.persistence.model.organization;

import com.kairos.persistence.model.staff.personal_details.StaffPersonalDetailDTO;
import com.kairos.dto.user.organization.CompanyType;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;
import java.util.Map;

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
    private Long companyCategoryId;
    private List<Long> businessTypeIds;
    private CompanyType companyType;
    private String vatId;
    private String kairosCompanyId;
    private Long accountTypeId;
    private Boolean boardingCompleted;
    private Long zipCodeId;
    private Long typeId;
    private List<Long> subTypeId;
    // Used in case of child
    Map<String, Object> contactAddress;
    private StaffPersonalDetailDTO unitManager;
    private Long unitTypeId;
    private boolean workcentre;


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

    public Long getCompanyCategoryId() {
        return companyCategoryId;
    }

    public void setCompanyCategoryId(Long companyCategoryId) {
        this.companyCategoryId = companyCategoryId;
    }

    public String getKairosCompanyId() {
        return kairosCompanyId;
    }

    public void setKairosCompanyId(String kairosCompanyId) {
        this.kairosCompanyId = kairosCompanyId;
    }

    public List<Long> getBusinessTypeIds() {
        return businessTypeIds;
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

    public Boolean getBoardingCompleted() {
        return boardingCompleted;
    }

    public void setBoardingCompleted(Boolean boardingCompleted) {
        this.boardingCompleted = boardingCompleted;
    }

    public Long getZipCodeId() {
        return zipCodeId;
    }

    public void setZipCodeId(Long zipCodeId) {
        this.zipCodeId = zipCodeId;
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

    public Map<String, Object> getContactAddress() {
        return contactAddress;
    }

    public void setContactAddress(Map<String, Object> contactAddress) {
        this.contactAddress = contactAddress;
    }

    public StaffPersonalDetailDTO getUnitManager() {
        return unitManager;
    }

    public void setUnitManager(StaffPersonalDetailDTO unitManager) {
        this.unitManager = unitManager;
    }

    public Long getUnitTypeId() {
        return unitTypeId;
    }

    public void setUnitTypeId(Long unitTypeId) {
        this.unitTypeId = unitTypeId;
    }

    public boolean isWorkcentre() {
        return workcentre;
    }

    public void setWorkcentre(boolean workcentre) {
        this.workcentre = workcentre;
    }
}

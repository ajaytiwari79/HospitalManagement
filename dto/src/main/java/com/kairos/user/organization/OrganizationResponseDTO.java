package com.kairos.user.organization;


import com.kairos.user.staff.client.ContactAddressDTO;

import java.util.List;

/**
 * Created by oodles on 25/4/18.
 */
public class OrganizationResponseDTO {
    private  Long  id;
    private  String name;
    private  boolean prekairos;
    private  boolean kairosHub;
    private  String description;
    private  List<Long> businessTypeIds;
    private  List<Long> typeId;
    private  List<Long> subTypeId;
    private  String externalId;
    private ContactAddressDTO contactAddress;
    private  Long levelId;
    private String kairosId;
    private Boolean union;
    private String desiredUrl;
    private String shortCompanyName;
    private Long companyCategoryId;
    private Integer kairosCompanyId;
    private CompanyType companyType;
    private Long accountTypeId;
    private String vatId;
    private boolean costCenter;
    private Integer costCenterId;
    private CompanyUnitType companyUnitType;
    private boolean boardingCompleted;
    private UnitManagerDTO unitManager;


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

    public boolean isPrekairos() {
        return prekairos;
    }

    public void setPrekairos(boolean prekairos) {
        this.prekairos = prekairos;
    }

    public boolean isKairosHub() {
        return kairosHub;
    }

    public void setKairosHub(boolean kairosHub) {
        this.kairosHub = kairosHub;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Long> getBusinessTypeIds() {
        return businessTypeIds;
    }

    public void setBusinessTypeIds(List<Long> businessTypeIds) {
        this.businessTypeIds = businessTypeIds;
    }

    public List<Long> getTypeId() {
        return typeId;
    }

    public void setTypeId(List<Long> typeId) {
        this.typeId = typeId;
    }

    public List<Long> getSubTypeId() {
        return subTypeId;
    }

    public void setSubTypeId(List<Long> subTypeId) {
        this.subTypeId = subTypeId;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public ContactAddressDTO getContactAddress() {
        return contactAddress;
    }

    public void setContactAddress(ContactAddressDTO contactAddress) {
        this.contactAddress = contactAddress;
    }

    public Long getLevelId() {
        return levelId;
    }

    public void setLevelId(Long levelId) {
        this.levelId = levelId;
    }

    public Boolean getUnion() {
        return union;
    }

    public void setUnion(Boolean union) {
        this.union = union;
    }

    public String getDesiredUrl() {
        return desiredUrl;
    }

    public void setDesiredUrl(String desiredUrl) {
        this.desiredUrl = desiredUrl;
    }

    public String getShortCompanyName() {
        return shortCompanyName;
    }

    public void setShortCompanyName(String shortCompanyName) {
        this.shortCompanyName = shortCompanyName;
    }

    public Long getCompanyCategoryId() {
        return companyCategoryId;
    }

    public void setCompanyCategoryId(Long companyCategoryId) {
        this.companyCategoryId = companyCategoryId;
    }

    public Integer getKairosCompanyId() {
        return kairosCompanyId;
    }

    public void setKairosCompanyId(Integer kairosCompanyId) {
        this.kairosCompanyId = kairosCompanyId;
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

    public boolean isCostCenter() {
        return costCenter;
    }

    public void setCostCenter(boolean costCenter) {
        this.costCenter = costCenter;
    }

    public Integer getCostCenterId() {
        return costCenterId;
    }

    public void setCostCenterId(Integer costCenterId) {
        this.costCenterId = costCenterId;
    }

    public CompanyUnitType getCompanyUnitType() {
        return companyUnitType;
    }

    public void setCompanyUnitType(CompanyUnitType companyUnitType) {
        this.companyUnitType = companyUnitType;
    }

    public boolean isBoardingCompleted() {
        return boardingCompleted;
    }

    public void setBoardingCompleted(boolean boardingCompleted) {
        this.boardingCompleted = boardingCompleted;
    }

    public UnitManagerDTO getUnitManager() {
        return unitManager;
    }

    public void setUnitManager(UnitManagerDTO unitManager) {
        this.unitManager = unitManager;
    }

    public String getKairosId() {
        return kairosId;
    }

    public void setKairosId(String kairosId) {
        this.kairosId = kairosId;
    }

    public Long getAccountTypeId() {
        return accountTypeId;
    }

    public void setAccountTypeId(Long accountTypeId) {
        this.accountTypeId = accountTypeId;
    }
}

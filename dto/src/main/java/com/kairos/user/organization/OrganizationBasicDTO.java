package com.kairos.user.organization;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.enums.OrganizationLevel;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.time.LocalTime;
import java.util.List;

/**
 * Created by prabjot on 20/1/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrganizationBasicDTO {
    private Long id;
    @NotEmpty(message = "error.name.notnull")
    @NotNull(message = "error.name.notnull")
    private String name;
    private String description;
    private boolean preKairos;
    private List<Long> typeId;
    private List<Long> subTypeId;
    private List<Long> businessTypeId;
    private AddressDTO contactAddress;
    private int dayShiftTimeDeduction = 4; //in percentage
    private int nightShiftTimeDeduction = 7; //in percentage
    private OrganizationLevel organizationLevel = OrganizationLevel.CITY;
    private boolean isOneTimeSyncPerformed;
    private LocalTime nightStartTime;
    private LocalTime nightEndTime;
    private String externalId;

    private String desiredUrl;
    private String shortCompanyName;
    private Long companyCategoryId;
    private Integer kairosCompanyId;
    private CompanyType companyType;

    private String vatId;
    private Long accountTypeId;
    private boolean costCenter;
    private Integer costCenterId;
    private CompanyUnitType companyUnitType;


    private List<Long> businessTypeIds;
    private boolean isAddressProtected;
    private boolean isVerifiedByGoogleMap;
    private boolean kairosHub;
    private Long levelId;
    private  Boolean union;
    private boolean boardingCompleted;
    private Long unitTypeId;
    // properties to create unit manager
    private UnitManagerDTO unitManager;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name.trim();
    }

    public AddressDTO getContactAddress() {
        return contactAddress;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setContactAddress(AddressDTO contactAddress) {
        this.contactAddress = contactAddress;
    }

    public List<Long> getTypeId() {
        return typeId;
    }

    public List<Long> getSubTypeId() {
        return subTypeId;
    }

    public List<Long> getBusinessTypeId() {
        return businessTypeId;
    }

    public void setTypeId(List<Long> typeId) {
        this.typeId = typeId;
    }

    public void setSubTypeId(List<Long> subTypeId) {
        this.subTypeId = subTypeId;
    }

    public void setBusinessTypeId(List<Long> businessTypeId) {
        this.businessTypeId = businessTypeId;
    }

    public int getDayShiftTimeDeduction() {
        return dayShiftTimeDeduction;
    }

    public void setDayShiftTimeDeduction(int dayShiftTimeDeduction) {
        this.dayShiftTimeDeduction = dayShiftTimeDeduction;
    }

    public int getNightShiftTimeDeduction() {
        return nightShiftTimeDeduction;
    }

    public void setNightShiftTimeDeduction(int nightShiftTimeDeduction) {
        this.nightShiftTimeDeduction = nightShiftTimeDeduction;
    }

    public OrganizationLevel getOrganizationLevel() {
        return organizationLevel;
    }

    public void setOrganizationLevel(OrganizationLevel organizationLevel) {
        this.organizationLevel = organizationLevel;
    }

    public boolean isOneTimeSyncPerformed() {
        return isOneTimeSyncPerformed;
    }

    public void setOneTimeSyncPerformed(boolean oneTimeSyncPerformed) {
        isOneTimeSyncPerformed = oneTimeSyncPerformed;
    }

    public LocalTime getNightStartTime() {
        return nightStartTime;
    }

    public void setNightStartTime(LocalTime nightStartTime) {
        this.nightStartTime = nightStartTime;
    }

    public LocalTime getNightEndTime() {
        return nightEndTime;
    }

    public void setNightEndTime(LocalTime nightEndTime) {
        this.nightEndTime = nightEndTime;
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

    public List<Long> getBusinessTypeIds() {
        return businessTypeIds;
    }

    public void setBusinessTypeIds(List<Long> businessTypeIds) {
        this.businessTypeIds = businessTypeIds;
    }

    public boolean isAddressProtected() {
        return isAddressProtected;
    }

    public void setAddressProtected(boolean addressProtected) {
        isAddressProtected = addressProtected;
    }

    public boolean isVerifiedByGoogleMap() {
        return isVerifiedByGoogleMap;
    }

    public void setVerifiedByGoogleMap(boolean verifiedByGoogleMap) {
        isVerifiedByGoogleMap = verifiedByGoogleMap;
    }

    public boolean isKairosHub() {
        return kairosHub;
    }

    public void setKairosHub(boolean kairosHub) {
        this.kairosHub = kairosHub;
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

    public boolean isBoardingCompleted() {
        return boardingCompleted;
    }

    public void setBoardingCompleted(boolean boardingCompleted) {
        this.boardingCompleted = boardingCompleted;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public boolean isPreKairos() {
        return preKairos;
    }

    public void setPreKairos(boolean preKairos) {
        this.preKairos = preKairos;
    }

    public UnitManagerDTO getUnitManager() {
        return unitManager;
    }

    public void setUnitManager(UnitManagerDTO unitManager) {
        this.unitManager = unitManager;
    }

    public Long getAccountTypeId() {
        return accountTypeId;
    }

    public void setAccountTypeId(Long accountTypeId) {
        this.accountTypeId = accountTypeId;
    }

    public Long getUnitTypeId() {
        return unitTypeId;
    }

    public void setUnitTypeId(Long unitTypeId) {
        this.unitTypeId = unitTypeId;
    }
}

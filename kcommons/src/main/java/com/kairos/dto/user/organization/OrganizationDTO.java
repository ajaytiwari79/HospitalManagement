package com.kairos.dto.user.organization;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.user.organization.address.AddressDTO;
import com.kairos.enums.OrganizationLevel;

import java.util.List;

/**
 * Created by prabjot on 20/1/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrganizationDTO {
    private Long id;
    private String name;
    private String description;
    private boolean isPreKairos;
    private OrganizationTypeDTO organizationType;
    private List<OrganizationTypeDTO> organizationSubTypes;
    private List<Long> businessTypeId;
    private AddressDTO contactAddress;
    private int dayShiftTimeDeduction = 4; //in percentage

    private int nightShiftTimeDeduction = 7; //in percentage
    private OrganizationLevel organizationLevel = OrganizationLevel.CITY;
    private boolean isOneTimeSyncPerformed;
    private Long countryId;
    private boolean isParentOrganization;


    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

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
        return name;
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

    public boolean isPreKairos() {
        return isPreKairos;
    }

    public void setPreKairos(boolean preKairos) {
        isPreKairos = preKairos;
    }

    public OrganizationTypeDTO getOrganizationType() {
        return organizationType;
    }

    public void setOrganizationType(OrganizationTypeDTO organizationType) {
        this.organizationType = organizationType;
    }

    public List<OrganizationTypeDTO> getOrganizationSubTypes() {
        return organizationSubTypes;
    }

    public void setOrganizationSubTypes(List<OrganizationTypeDTO> organizationSubTypes) {
        this.organizationSubTypes = organizationSubTypes;
    }

    public List<Long> getBusinessTypeId() {
        return businessTypeId;
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

    public void setOrganizationLevel(OrganizationLevel organizationLevel) {
        this.organizationLevel = organizationLevel;
    }

    public OrganizationLevel getOrganizationLevel() {
        return organizationLevel;
    }


    public boolean isOneTimeSyncPerformed() {
        return isOneTimeSyncPerformed;
    }

    public void setOneTimeSyncPerformed(boolean oneTimeSyncPerformed) {
        isOneTimeSyncPerformed = oneTimeSyncPerformed;
    }

    public boolean isParentOrganization() {
        return isParentOrganization;
    }

    public void setParentOrganization(boolean parentOrganization) {
        isParentOrganization = parentOrganization;
    }
}



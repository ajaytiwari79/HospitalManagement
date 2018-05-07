package com.kairos.persistence.model.organization;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.persistence.model.organization.enums.OrganizationLevel;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.time.LocalTime;
import java.util.List;

/**
 * Created by prabjot on 20/1/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrganizationDTO {
    private Long id;
    @NotEmpty(message = "error.name.notnull")
    @NotNull(message = "error.name.notnull")
    private String name;
    private String description;
    private boolean isPreKairos;
    private List<Long> organizationTypeId;
    private List<Long> organizationSubTypeId;
    private List<Long> businessTypeId;
    private AddressDTO contactAddress;
    private int dayShiftTimeDeduction = 4; //in percentage
    private boolean isKairosHub;
    private int nightShiftTimeDeduction = 7; //in percentage
    private OrganizationLevel organizationLevel = OrganizationLevel.CITY;
    private boolean isOneTimeSyncPerformed;
    private LocalTime nightStartTime;
    private LocalTime nightEndTime;

    public boolean isKairosHub() {
        return isKairosHub;
    }

    public void setKairosHub(boolean kairosHub) {
        isKairosHub = kairosHub;
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

    public List<Long> getOrganizationTypeId() {
        return organizationTypeId;
    }

    public List<Long> getOrganizationSubTypeId() {
        return organizationSubTypeId;
    }

    public List<Long> getBusinessTypeId() {
        return businessTypeId;
    }

    public void setOrganizationTypeId(List<Long> organizationTypeId) {
        this.organizationTypeId = organizationTypeId;
    }

    public void setOrganizationSubTypeId(List<Long> organizationSubTypeId) {
        this.organizationSubTypeId = organizationSubTypeId;
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
}

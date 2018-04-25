package com.kairos.persistence.model.organization;

import com.kairos.persistence.model.user.client.ContactAddressDTO;

import java.util.List;

/**
 * Created by oodles on 25/4/18.
 */
public class OrganizationResponseDTO {
    Long  id;
    String name;
    boolean prekairos;
    boolean kairosHub;
    String description;
    List<Long> businessTypeIds;
    List<Long> typeId;
    List<Long> subTypeId;
    String externalId;
    ContactAddressDTO homeAddress;
    Long levelId;

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

    public ContactAddressDTO getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(ContactAddressDTO homeAddress) {
        this.homeAddress = homeAddress;
    }

    public Long getLevelId() {
        return levelId;
    }

    public void setLevelId(Long levelId) {
        this.levelId = levelId;
    }
}

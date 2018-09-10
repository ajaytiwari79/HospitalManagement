package com.kairos.dto.activity.tags;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.enums.MasterDataTypeEnum;

import java.math.BigInteger;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TagDTO {
    private BigInteger id;
    private String name;
    private MasterDataTypeEnum masterDataType;
    private boolean countryTag;
    private long countryId;
    private long organizationId;

    public TagDTO() {
        //dv
    }

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

    public MasterDataTypeEnum getMasterDataType() {
        return masterDataType;
    }

    public void setMasterDataType(MasterDataTypeEnum masterDataType) {
        this.masterDataType = masterDataType;
    }

    public boolean isCountryTag() {
        return countryTag;
    }

    public void setCountryTag(boolean countryTag) {
        this.countryTag = countryTag;
    }

    public long getCountryId() {
        return countryId;
    }

    public void setCountryId(long countryId) {
        this.countryId = countryId;
    }

    public long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(long organizationId) {
        this.organizationId = organizationId;
    }
}

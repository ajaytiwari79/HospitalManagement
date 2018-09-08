package com.kairos.user.country.tag;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.enums.MasterDataTypeEnum;

/**
 * Created by prerna on 10/11/17.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TagDTO {

    private Long id;
    private String name;

    private MasterDataTypeEnum masterDataType;

    private boolean countryTag;

    private long countryId;

    private long organizationId;

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

    public boolean isCountryTag() {
        return countryTag;
    }

    public void setCountryTag(boolean countryTag) {
        this.countryTag = countryTag;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MasterDataTypeEnum getMasterDataType() {
        return masterDataType;
    }

    public void setMasterDataType(MasterDataTypeEnum masterDataType) {
        this.masterDataType = masterDataType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TagDTO(String name, MasterDataTypeEnum masterDataType){
        this.name = name;
        this.masterDataType = masterDataType;
    }

    public TagDTO(Long id, String name, MasterDataTypeEnum masterDataType){
        this.id = id;
        this.name = name;
        this.masterDataType = masterDataType;
    }

    public TagDTO(){}
}

package com.kairos.persistence.model.tag;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.enums.MasterDataTypeEnum;
import com.kairos.persistence.model.common.MongoBaseEntity;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by prerna on 20/11/17.
 */
@Document
@JsonIgnoreProperties(ignoreUnknown = true)
public class Tag extends MongoBaseEntity {

    private String name;
    @Indexed
    private MasterDataTypeEnum masterDataType;

    private boolean countryTag;

    private long countryId;

    private long organizationId;

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

    public Tag(){

    }

    public Tag(String name, MasterDataTypeEnum masterDataType, boolean countryTag, Long countryOrOrgId){
        this.name = name;
        this.masterDataType = masterDataType;
        this.countryTag = countryTag;
        if(countryTag){
            this.countryId = countryOrOrgId;
        } else {
            this.organizationId = countryOrOrgId;
        }
    }

}

package com.kairos.shiftplanning.domain.tag;

import com.kairos.enums.MasterDataTypeEnum;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.springframework.data.mongodb.core.index.Indexed;

import java.math.BigInteger;

@XStreamAlias("Tag")
public class Tag {
    BigInteger tagId;
    private String name;
    @Indexed
    private MasterDataTypeEnum masterDataType;

    private boolean countryTag;

    private long countryId;

    private long organizationId;

    public Tag(BigInteger tagId,String name, MasterDataTypeEnum masterDataType, boolean countryTag, long countryOrOrdId){
        this.tagId = tagId;
        this.name = name;
        this.masterDataType = masterDataType;
        this.countryTag = countryTag;
        if(countryTag) {
            this.countryId = countryOrOrdId;
        }else {
            this.organizationId = countryOrOrdId;
        }
    }

    public BigInteger getTagId() {
        return tagId;
    }

    public void setTagId(BigInteger tagId) {
        this.tagId = tagId;
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

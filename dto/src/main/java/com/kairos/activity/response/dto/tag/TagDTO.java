package com.kairos.activity.response.dto.tag;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.activity.persistence.model.tag.MasterDataTypeEnum;

import java.math.BigInteger;

/**
 * Created by prerna on 20/11/17.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TagDTO {
    private Long id;
    private MasterDataTypeEnum masterDataType;
    private String name;

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

    public TagDTO(String name, MasterDataTypeEnum masterDataTypeEnum){
        this.name = name;
        this.masterDataType = masterDataTypeEnum;
    }

    public TagDTO(BigInteger id, String name, MasterDataTypeEnum masterDataTypeEnum){
        this.id = id.longValue();
        this.name = name;
        this.masterDataType = masterDataTypeEnum;
    }

    public TagDTO(){

    }

}

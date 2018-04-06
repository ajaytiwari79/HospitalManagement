package com.kairos.response.dto.web.tag;

import com.kairos.persistence.model.enums.MasterDataTypeEnum;

/**
 * Created by prerna on 10/11/17.
 */
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

    public TagDTO(String name, MasterDataTypeEnum masterDataType){
        this.name = name;
        this.masterDataType = masterDataType;
    }

    public TagDTO(Long id, String name, MasterDataTypeEnum masterDataType){
        this.id = id;
        this.name = name;
        this.masterDataType = masterDataType;
    }

    TagDTO(){}
}

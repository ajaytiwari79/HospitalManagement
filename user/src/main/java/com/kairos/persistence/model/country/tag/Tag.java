package com.kairos.persistence.model.country.tag;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.enums.MasterDataTypeEnum;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.dto.user.country.tag.TagDTO;
import org.hibernate.validator.constraints.NotEmpty;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.typeconversion.EnumString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Created by prerna on 10/11/17.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@NodeEntity
public class Tag extends UserBaseEntity {

    @NotBlank(message = "error.Tag.name.notEmptyOrNotNull")
    private String name;

    @Property(name = "masterDataType")
    @EnumString(MasterDataTypeEnum.class)
    private MasterDataTypeEnum masterDataType;

    private boolean countryTag;

    Tag(){}
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isCountryTag() {
        return countryTag;
    }

    public void setCountryTag(boolean countryTag) {
        this.countryTag = countryTag;
    }

    public Tag(TagDTO tagDTO, boolean countryTag){
        this.setName(tagDTO.getName());
        this.setMasterDataType(tagDTO.getMasterDataType());
        this.setCountryTag(countryTag);
    }

    public MasterDataTypeEnum getMasterDataType() {
        return masterDataType;
    }

    public void setMasterDataType(MasterDataTypeEnum masterDataType) {
        this.masterDataType = masterDataType;
    }
}

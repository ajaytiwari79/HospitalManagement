package com.kairos.persistence.model.tag;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.enums.MasterDataTypeEnum;
import com.kairos.persistence.model.common.MongoBaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;

/**
 * Created by prerna on 20/11/17.
 */
@Document
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
public class Tag extends MongoBaseEntity {

    private String name;
    @Indexed
    private MasterDataTypeEnum masterDataType;

    private boolean countryTag;

    private long countryId;

    private long organizationId;

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

    public Tag(BigInteger id, String name, MasterDataTypeEnum masterDataType, boolean countryTag, Long countryOrOrgId){
        this.id = id;
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

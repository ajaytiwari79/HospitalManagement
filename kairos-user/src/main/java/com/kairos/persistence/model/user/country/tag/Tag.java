package com.kairos.persistence.model.user.country.tag;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.enums.MasterDataTypeEnum;
import org.hibernate.validator.constraints.NotEmpty;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.typeconversion.EnumString;

import javax.validation.constraints.NotNull;

/**
 * Created by prerna on 10/11/17.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@NodeEntity
public class Tag extends UserBaseEntity {

    @NotEmpty(message = "error.Tag.name.notEmptyOrNotNull") @NotNull(message = "error.Tag.name.notEmptyOrNotNull")
    private String name;

    private boolean deleted;

    public Tag(){}
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public Tag(String name){
        this.setName(name);
    }
}

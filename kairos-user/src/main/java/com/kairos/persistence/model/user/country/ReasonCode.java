package com.kairos.persistence.model.user.country;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.enums.ReasonCodeType;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import static com.kairos.persistence.model.constants.RelationshipConstants.BELONGS_TO;

/**
 * Created by pavan on 23/3/18.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@NodeEntity
public class ReasonCode extends UserBaseEntity {
    private String name;
    private String code;
    private String description;
    private ReasonCodeType reasonCodeType;
    @Relationship(type = BELONGS_TO)
    private Country country;

    public ReasonCode() {
        //Default Constructor
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ReasonCodeType getReasonCodeType() {
        return reasonCodeType;
    }

    public void setReasonCodeType(ReasonCodeType reasonCodeType) {
        this.reasonCodeType = reasonCodeType;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public ReasonCode(String name, String code, String description, ReasonCodeType reasonCodeType, Country country) {
        this.name = name;
        this.code = code;
        this.description = description;
        this.reasonCodeType = reasonCodeType;
        this.country = country;
    }
}

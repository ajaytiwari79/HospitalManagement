package com.kairos.persistence.model.country.reason_code;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.enums.reason_code.ReasonCodeType;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.organization.Unit;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.math.BigInteger;

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
    @Relationship(type = BELONGS_TO)
    private Unit unit;
    // this is only persist when we create any Absence type reason code
    private BigInteger timeTypeId;

    public ReasonCode() {
        //Default Constructor
    }
    public ReasonCode(String name, String code, String description, ReasonCodeType reasonCodeType, Country country,BigInteger timeTypeId) {
        this.name = name;
        this.code = code;
        this.description = description;
        this.reasonCodeType = reasonCodeType;
        this.country = country;
        this.timeTypeId=timeTypeId;
    }

    public ReasonCode(String name, String code, String description, ReasonCodeType reasonCodeType, Unit unit, BigInteger timeTypeId) {
        this.name = name;
        this.code = code;
        this.description = description;
        this.reasonCodeType = reasonCodeType;
        this.unit = unit;
        this.timeTypeId=timeTypeId;
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

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public BigInteger getTimeTypeId() {
        return timeTypeId;
    }

    public void setTimeTypeId(BigInteger timeTypeId) {
        this.timeTypeId = timeTypeId;
    }
}

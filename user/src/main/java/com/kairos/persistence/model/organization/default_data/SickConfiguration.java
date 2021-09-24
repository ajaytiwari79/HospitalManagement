package com.kairos.persistence.model.organization.default_data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.organization.Unit;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.math.BigInteger;
import java.util.Set;

import static com.kairos.persistence.model.constants.RelationshipConstants.IN_UNIT;

/**
 * CreatedBy vipulpandey on 29/8/18
 **/
@NodeEntity
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SickConfiguration extends UserBaseEntity {

    private Set<BigInteger> timeTypes;

    @Relationship(type = IN_UNIT)
    private Unit unit;

    public SickConfiguration() {
        // DC
    }

    public Set<BigInteger> getTimeTypes() {
        return timeTypes;
    }

    public void setTimeTypes(Set<BigInteger> timeTypes) {
        this.timeTypes = timeTypes;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public SickConfiguration(Set<BigInteger> timeTypes, Unit unit) {
        this.timeTypes = timeTypes;
        this.unit = unit;
    }
}

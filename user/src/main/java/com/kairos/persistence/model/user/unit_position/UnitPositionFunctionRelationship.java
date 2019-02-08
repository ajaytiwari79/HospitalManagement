package com.kairos.persistence.model.user.unit_position;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.config.neo4j.converter.LocalDateListConverter;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.country.functions.Function;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;
import org.neo4j.ogm.annotation.typeconversion.Convert;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.time.LocalDate;
import java.util.*;

import static com.kairos.persistence.model.constants.RelationshipConstants.APPLIED_FUNCTION;

/**
 * Created by oodles on 5/6/18.
 */

@RelationshipEntity(type = APPLIED_FUNCTION)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UnitPositionFunctionRelationship extends UserBaseEntity {

    @StartNode
    private UnitPosition unitPosition;
    @EndNode
    private Function function;

    private Set<LocalDate> appliedDates;


    public UnitPositionFunctionRelationship() {
    }

    public UnitPositionFunctionRelationship(Long id,UnitPosition unitPosition, Function function, Set<LocalDate> appliedDates) {
        this.id=id;
        this.unitPosition = unitPosition;
        this.function = function;
        this.appliedDates = appliedDates;
    }

    public UnitPosition getUnitPosition() {
        return unitPosition;
    }

    public void setUnitPosition(UnitPosition unitPosition) {
        this.unitPosition = unitPosition;
    }

    public Function getFunction() {
        return function;
    }

    public void setFunction(Function function) {
        this.function = function;
    }

    public Set<LocalDate> getDate() {
        return appliedDates = Optional.ofNullable(appliedDates).orElse(new HashSet<>());
    }

    public void setDate(Set<LocalDate> appliedDates) {
        this.appliedDates = appliedDates;
    }

}

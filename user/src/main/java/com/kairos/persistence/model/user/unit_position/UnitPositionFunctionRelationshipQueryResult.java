package com.kairos.persistence.model.user.unit_position;/*
 *Created By Pavan on 2/10/18
 *
 */

import com.kairos.persistence.model.country.functions.Function;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@QueryResult
public class UnitPositionFunctionRelationshipQueryResult {
    private Long id;
    private UnitPosition unitPosition;
    private Function function;
    private Set<LocalDate> appliedDates;

    public UnitPositionFunctionRelationshipQueryResult() {
        //Default Constructor
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Set<LocalDate> getAppliedDates() {
        return appliedDates;
    }

    public void setAppliedDates(Set<LocalDate> appliedDates) {
        this.appliedDates = appliedDates;
    }
}

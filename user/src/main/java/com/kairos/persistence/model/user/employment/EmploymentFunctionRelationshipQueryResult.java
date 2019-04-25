package com.kairos.persistence.model.user.employment;/*
 *Created By Pavan on 2/10/18
 *
 */

import com.kairos.persistence.model.country.functions.Function;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.time.LocalDate;
import java.util.Set;

@QueryResult
public class EmploymentFunctionRelationshipQueryResult {
    private Long id;
    private Employment employment;
    private Function function;
    private Set<LocalDate> appliedDates;

    public EmploymentFunctionRelationshipQueryResult() {
        //Default Constructor
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Employment getEmployment() {
        return employment;
    }

    public void setEmployment(Employment employment) {
        this.employment = employment;
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

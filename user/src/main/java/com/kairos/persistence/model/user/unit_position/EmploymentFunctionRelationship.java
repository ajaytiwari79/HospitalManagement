package com.kairos.persistence.model.user.unit_position;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.country.functions.Function;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

import java.time.LocalDate;
import java.util.*;

import static com.kairos.persistence.model.constants.RelationshipConstants.APPLIED_FUNCTION;

/**
 * Created by oodles on 5/6/18.
 */

@RelationshipEntity(type = APPLIED_FUNCTION)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmploymentFunctionRelationship extends UserBaseEntity {

    @StartNode
    private Employment employment;
    @EndNode
    private Function function;

    private Set<LocalDate> appliedDates;


    public EmploymentFunctionRelationship() {
    }

    public EmploymentFunctionRelationship(Long id, Employment employment, Function function, Set<LocalDate> appliedDates) {
        this.id=id;
        this.employment = employment;
        this.function = function;
        this.appliedDates = appliedDates;
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

    public Set<LocalDate> getDate() {
        return appliedDates = Optional.ofNullable(appliedDates).orElse(new HashSet<>());
    }

    public void setDate(Set<LocalDate> appliedDates) {
        this.appliedDates = appliedDates;
    }

}

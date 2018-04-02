package com.kairos.persistence.model.user.expertise;

import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.user.country.Function;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

import java.math.BigDecimal;

import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_FUNCTION;

/**
 * Created by vipul on 28/3/18.
 */
@RelationshipEntity(type=HAS_FUNCTION)
public class SeniorityLevelFunctionsRelationship extends UserBaseEntity{
    @StartNode private SeniorityLevel seniorityLevel;
    @EndNode
    private Function function;
    private BigDecimal amount;

    public SeniorityLevelFunctionsRelationship() {
    }

    public SeniorityLevel getSeniorityLevel() {
        return seniorityLevel;
    }

    public void setSeniorityLevel(SeniorityLevel seniorityLevel) {
        this.seniorityLevel = seniorityLevel;
    }

    public Function getFunction() {
        return function;
    }

    public void setFunction(Function function) {
        this.function = function;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public SeniorityLevelFunctionsRelationship(SeniorityLevel seniorityLevel, Function function, BigDecimal amount) {
        this.seniorityLevel = seniorityLevel;
        this.function = function;
        this.amount = amount;
    }
}

package com.kairos.persistence.model.user.unit_position;

import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.country.functions.Function;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

import java.math.BigDecimal;

import static com.kairos.persistence.model.constants.RelationshipConstants.APPLICABLE_FUNCTION;


/**
 * Created by vipul on 6/4/18.
 */
@RelationshipEntity(type = APPLICABLE_FUNCTION)
public class UnitPositionLineFunctionRelationShip extends UserBaseEntity {


    @StartNode
    private UnitPositionLine unitPositionLine;
    @EndNode
    private Function function;
    @Property
    private BigDecimal amount;

    public UnitPositionLineFunctionRelationShip() {

    }

    public UnitPositionLineFunctionRelationShip(UnitPositionLine unitPositionLine, Function function,BigDecimal amount) {
        this.unitPositionLine = unitPositionLine;
        this.function = function;
        this.amount=amount;
    }
    public UnitPositionLine getUnitPositionLine() {
        return unitPositionLine;
    }

    public void setUnitPositionLine(UnitPositionLine unitPositionLine) {
        this.unitPositionLine = unitPositionLine;
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
}
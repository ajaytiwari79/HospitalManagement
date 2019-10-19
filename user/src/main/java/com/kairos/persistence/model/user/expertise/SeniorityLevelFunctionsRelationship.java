package com.kairos.persistence.model.user.expertise;

import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.country.functions.Function;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

import java.math.BigDecimal;

import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_FUNCTIONAL_AMOUNT;

/**
 * Created by vipul on 28/3/18.
 */

@RelationshipEntity(type = HAS_FUNCTIONAL_AMOUNT)
@Getter
@Setter
@NoArgsConstructor
public class SeniorityLevelFunctionsRelationship extends UserBaseEntity {
    @StartNode
    private SeniorityLevelFunction seniorityLevelFunction;
    @EndNode
    private Function function;
    private BigDecimal amount;
    private boolean amountEditableAtUnit;

    public SeniorityLevelFunctionsRelationship(Function function, SeniorityLevelFunction seniorityLevelFunction, BigDecimal amount,boolean amountEditableAtUnit) {
        this.seniorityLevelFunction = seniorityLevelFunction;
        this.function = function;
        this.amount = amount;
        this.amountEditableAtUnit=amountEditableAtUnit;
    }
}

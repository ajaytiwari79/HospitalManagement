package com.kairos.persistence.model.user.employment;

import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.country.functions.Function;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmploymentLineFunctionRelationShip extends UserBaseEntity {


    @StartNode
    private EmploymentLine employmentLine;
    @EndNode
    private Function function;
    @Property
    private BigDecimal amount;

}
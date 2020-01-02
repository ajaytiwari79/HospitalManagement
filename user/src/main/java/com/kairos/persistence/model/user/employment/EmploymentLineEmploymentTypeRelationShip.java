package com.kairos.persistence.model.user.employment;

import com.kairos.enums.employment_type.EmploymentCategory;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.country.employment_type.EmploymentType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_EMPLOYMENT_TYPE;


/**
 * Created by vipul on 6/4/18.
 */
@RelationshipEntity(type = HAS_EMPLOYMENT_TYPE)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmploymentLineEmploymentTypeRelationShip extends UserBaseEntity {

    @StartNode
    private EmploymentLine employmentLine;
    @EndNode
    private EmploymentType employmentType;
    @Property
    private EmploymentCategory employmentTypeCategory;
}
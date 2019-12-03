package com.kairos.persistence.model.user.expertise;

import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.country.employment_type.EmploymentType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

import java.math.BigInteger;

import static com.kairos.persistence.model.constants.RelationshipConstants.EXPERTISE_HAS_PLANNED_TIME_FOR_EMPLOYMENT;

@RelationshipEntity(type = EXPERTISE_HAS_PLANNED_TIME_FOR_EMPLOYMENT)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExpertiseEmploymentTypeRelationship extends UserBaseEntity {
    @StartNode
    private Expertise expertise;
    @EndNode
    private EmploymentType employmentType;
    private BigInteger includedPlannedTime;
    private BigInteger excludedPlannedTime;
}

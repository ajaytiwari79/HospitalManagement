package com.kairos.persistence.repository.user.expertise;

import com.kairos.persistence.model.user.expertise.ExpertiseEmploymentTypeRelationship;
import com.kairos.persistence.model.user.expertise.Response.ExpertisePlannedTimeQueryResult;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.kairos.persistence.model.constants.RelationshipConstants.EXPERTISE_HAS_PLANNED_TIME_FOR_EMPLOYMENT;

@Repository
public interface ExpertiseEmploymentTypeRelationshipGraphRepository extends Neo4jBaseRepository<ExpertiseEmploymentTypeRelationship, Long> {
    @Query("match(expertise:Expertise) where id(expertise)={0} " +
            "match(expertise)-[relation:" + EXPERTISE_HAS_PLANNED_TIME_FOR_EMPLOYMENT + "]->(employmentType:EmploymentType)\n DETACH DELETE relation")
    void removeAllPreviousEmploymentType(Long expertiseId);

    @Query("match(expertise:Expertise) where id(expertise)={0} " +
            "match(expertise)-[relation:" + EXPERTISE_HAS_PLANNED_TIME_FOR_EMPLOYMENT + "]->(employmentType:EmploymentType)\n" +
            "return relation.excludedPlannedTime as excludedPlannedTime,relation.includedPlannedTime as includedPlannedTime ,collect(employmentType) as employmentTypes")
    List<ExpertisePlannedTimeQueryResult> findPlannedTimeByExpertise(Long expertiseId);


    @Query("match(expertise:Expertise) where id(expertise)={0} " +
            "match(expertise)-[relation:" + EXPERTISE_HAS_PLANNED_TIME_FOR_EMPLOYMENT + "]->(employmentType:EmploymentType) where id(employmentType)={1}\n" +
            "return relation.excludedPlannedTime as excludedPlannedTime,relation.includedPlannedTime as includedPlannedTime ")
    ExpertisePlannedTimeQueryResult findPlannedTimeByExpertise(Long expertiseId, Long employmentTypeId);


    @Query("match(expertise:Expertise) where id(expertise)={0} " +
            "match(expertise)-[relation:" + EXPERTISE_HAS_PLANNED_TIME_FOR_EMPLOYMENT + "]->(employmentType:EmploymentType)\n" +
            "return relation.excludedPlannedTime as excludedPlannedTime,relation.includedPlannedTime as includedPlannedTime ,collect(employmentType) as employmentTypes")
    ExpertisePlannedTimeQueryResult getPlannedTimeConfigurationByExpertise(Long expertiseId);
}

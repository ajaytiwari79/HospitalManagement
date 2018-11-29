package com.kairos.persistence.repository.user.expertise;

import com.kairos.persistence.model.user.expertise.OrganizationPersonalizeLocationRelationShip;
import com.kairos.persistence.model.user.expertise.Response.ExpertiseLocationStaffQueryResult;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_PERSONALIZED_LOCATION;

@Repository
public interface OrganizationPersonalizeLocationRelationShipGraphRepository extends Neo4jBaseRepository<OrganizationPersonalizeLocationRelationShip, Long> {

    @Query("MATCH (org:Organization),(expertise:Expertise) WHERE id(org)={1} AND id(expertise) ={0} " +
            "MERGE (org)-[rel:" + HAS_PERSONALIZED_LOCATION + "]-(expertise) " +
            "SET rel.locationId={2} " +
            "RETURN count(rel)")
    int setLocationInOrganizationForExpertise(Long expertiseId, Long organizationId, Long locationId);


    @Query("MATCH (org:Organization),(expertise:Expertise) WHERE id(org)={1} AND id(expertise) IN {0} " +
            "MATCH (org)-[rel:" + HAS_PERSONALIZED_LOCATION + "]-(expertise) " +
            "MATCH (location:Location) WHERE id(location)= rel.locationId " +
            "RETURN location as location,id(expertise) as expertiseId ")
    List<ExpertiseLocationStaffQueryResult> getExpertisesLocationInOrganization(List<Long> expertiseId, Long organizationId);
}

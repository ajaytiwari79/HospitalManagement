package com.kairos.persistence.repository.organization;

import com.kairos.persistence.model.organization.OrganizationExternalServiceRelationship;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizationExternalServiceRelationshipRepository extends Neo4jBaseRepository<OrganizationExternalServiceRelationship,Long> {
}

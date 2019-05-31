package com.kairos.persistence.repository.organization;/*
 *Created By Pavan on 30/5/19
 *
 */

import com.kairos.persistence.model.organization.OrganizationBaseEntity;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;

public interface OrganizationBaseRepository extends Neo4jBaseRepository<OrganizationBaseEntity,Long> {
    @Override
    OrganizationBaseEntity findOne(Long id);
}

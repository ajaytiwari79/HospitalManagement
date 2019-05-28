package com.kairos.persistence.repository.organization;/*
 *Created By Pavan on 27/5/19
 *
 */

import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizationGraphRepository extends Neo4jBaseRepository<Organization,Long> {

}

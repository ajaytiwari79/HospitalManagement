package com.kairos.persistence.repository.user.access_permission;

import com.kairos.persistence.model.access_permission.AccessPageLanguageRelationShip;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccessPageLanguageRelationShipRepository extends Neo4jBaseRepository<AccessPageLanguageRelationShip,Long> {

}

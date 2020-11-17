package com.kairos.persistence.repository.user.access_permission;

import com.kairos.persistence.model.access_permission.AccessPageLanguageRelationShip;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.kairos.constants.AppConstants.ACCESS_PAGE_HAS_LANGUAGE;

@Repository
public interface AccessPageLanguageRelationShipRepository extends Neo4jBaseRepository<AccessPageLanguageRelationShip,Long> {


    @Query("MATCH(n:AccessPage)-[r:"+ACCESS_PAGE_HAS_LANGUAGE+"]->(m:SystemLanguage) " +
            "RETURN r")
    Optional<AccessPageLanguageRelationShip> findByModuleIdAndLanguageId(String moduleId, Long languageId);
}

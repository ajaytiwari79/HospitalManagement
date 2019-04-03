package com.kairos.persistence.repository.user.region;

import com.kairos.persistence.model.organization.Level;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import static com.kairos.persistence.model.constants.RelationshipConstants.BELONGS_TO;
import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_LEVEL;

@Repository
public interface LevelGraphRepository extends Neo4jBaseRepository<Level,Long> {

    @Query("MATCH(country:Country)-[:" + HAS_LEVEL + "]->(level:Level {isEnabled:true}) WHERE id(country)={0} AND id(level)<>{2} AND level.name =~{1}  " +
            " WITH count(level) as totalCount " +
            " RETURN CASE WHEN totalCount>0 THEN TRUE ELSE FALSE END as result")
    Boolean levelExistInCountryByName(Long countryId, String name, Long currentLevelId);
}

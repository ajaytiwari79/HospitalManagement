package com.kairos.persistence.repository.user.user_filter;

import com.kairos.persistence.model.user.filter.FilterGroup;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import static com.kairos.persistence.model.constants.RelationshipConstants.APPLICABLE_FOR;

/**
 * Created by prerna on 1/5/18.
 */
@Repository
public interface FilterGroupGraphRepository extends Neo4jBaseRepository<FilterGroup, Long> {

    @Query("MATCH (ap:AccessPage)-[r:"+APPLICABLE_FOR+"]-(fg:FilterGroup{deleted:false}) WHERE ap.moduleId={0} return fg")
    FilterGroup getFilterGroupByModuleId(String moduleId);

    @Query("MATCH (ap:AccessPage)-[r:"+APPLICABLE_FOR+"]-(fg:FilterGroup{deleted:false}) WHERE ap.moduleId={0} return COUNT(fg)>0")
    boolean checkIfFilterGroupExistsForModuleId(String moduleId);
}

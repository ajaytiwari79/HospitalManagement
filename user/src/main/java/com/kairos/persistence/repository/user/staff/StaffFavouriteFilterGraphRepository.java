package com.kairos.persistence.repository.user.staff;

import com.kairos.persistence.model.staff.StaffFavouriteFilter;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import static com.kairos.persistence.model.constants.RelationshipConstants.APPLICABLE_FOR;

/**
 * Created by prerna on 1/5/18.
 */
@Repository
public interface StaffFavouriteFilterGraphRepository extends Neo4jBaseRepository<StaffFavouriteFilter, Long> {

    @Query("MATCH (ap:AccessPage)-[r:"+APPLICABLE_FOR+"]-(fg:FilterGroup{deleted:false})-[:HAS_FILTER_GROUP]-(staffFavouriteFilter:StaffFavouriteFilter{deleted:false})\n"+
            "WHERE ap.moduleId={0} AND LOWER(staffFavouriteFilter.name) = LOWER({1}) return COUNT(fg)>0")
    Boolean checkIfFavouriteFilterExistsWithName(String moduleId, String name);

    @Query("MATCH (ap:AccessPage)-[r:"+APPLICABLE_FOR+"]-(fg:FilterGroup{deleted:false})-[:HAS_FILTER_GROUP]-(staffFavouriteFilter:StaffFavouriteFilter{deleted:false})\n"+
            "WHERE ap.moduleId={0} AND LOWER(staffFavouriteFilter.name) = LOWER({1}) AND NOT(id(staffFavouriteFilter) = {2}) return COUNT(fg)>0")
    Boolean checkIfFavouriteFilterExistsWithNameExceptId(String moduleId, String name, Long staffFavouriteFilterId);
}

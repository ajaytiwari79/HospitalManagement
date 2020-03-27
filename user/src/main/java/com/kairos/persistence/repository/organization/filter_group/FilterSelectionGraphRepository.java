package com.kairos.persistence.repository.organization.filter_group;

import com.kairos.persistence.model.user.filter.FilterSelection;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface FilterSelectionGraphRepository extends Neo4jRepository<FilterSelection,Long> {

    @Query("MATCH (fs:FilterSelection)<-[:HAS_FILTERS]-(g:Group)<-[:HAS_GROUPS]-(unit:Unit) \n" +
            "WHERE id(unit)={0} AND g.deleted={1} \n" +
            "RETURN fs order by fs.name")
    List<FilterSelection> findAllByUnitAndDeleted(Long unitId, boolean deleted);

    @Query("MATCH (fs:FilterSelection)<-[:HAS_FILTERS]-(g:Group)<-[:HAS_GROUPS]-(unit:Unit) \n" +
            "WHERE id(unit)={0} AND g in {1} AND g.deleted={2} \n" +
            "RETURN fs order by fs.name")
    Set<FilterSelection> findAllByUnitAndSelectedGroupsAndDeleted(Long unitId, Set<Long> groupIds, boolean groupDeleted);
}

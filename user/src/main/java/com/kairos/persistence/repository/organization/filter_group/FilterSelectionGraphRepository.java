package com.kairos.persistence.repository.organization.filter_group;

import com.kairos.persistence.model.user.filter.FilterSelection;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface FilterSelectionGraphRepository extends Neo4jBaseRepository<FilterSelection,Long> {

    @Query("MATCH (fs:FilterSelection)<-[:HAS_FILTERS]-(g:Group)<-[:HAS_GROUPS]-(unit:Unit) \n" +
            "WHERE id(unit)={0} AND g.deleted={1} \n" +
            "RETURN fs order by fs.name")
    List<FilterSelection> findAllByUnitAndDeleted(Long unitId, boolean deleted);

    @Query("MATCH (unit:Unit)-[:HAS_GROUPS]->(g:Group) \n" +
            "WHERE id(unit)={0}  AND g.deleted={2} WITH g \n" +//AND id(g) IN {1}
            "MATCH (g)-[:HAS_FILTERS]->(fs:FilterSelection) " +
            "RETURN fs ORDER BY fs.name")
    Set<FilterSelection> findAllByUnitIdAndSelectedGroupIdsAndGroupDeleted(Long unitId, Set<Long> groupIds, boolean groupDeleted);
}

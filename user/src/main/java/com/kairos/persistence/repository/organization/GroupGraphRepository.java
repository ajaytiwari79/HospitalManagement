package com.kairos.persistence.repository.organization;

import com.kairos.persistence.model.organization.group.Group;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_FILTERS;
import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_GROUPS;

/**
 * Created By G.P.Ranjan on 19/11/19
 **/
@Repository
public interface GroupGraphRepository extends Neo4jBaseRepository<Group,Long> {
    @Query("MATCH(unit:Unit)-[:" + HAS_GROUPS + "]->(group:Group{deleted:false}) WHERE id(unit)={0} AND id(group)<>{1} AND group.name =~{2}  \n" +
            "RETURN COUNT(group)>0")
    boolean existsByName(Long unitId, Long groupId, String name);

    @Query("MATCH(group:Group{deleted:false}) WHERE id(group)={0} " +
            "OPTIONAL MATCH (group)-[rel:" + HAS_FILTERS + "]->(filterSelection:FilterSelection)" +
            "RETURN group,COLLECT(rel),COLLECT(filterSelection)")
    Group findGroupByIdAndDeletedFalse(Long groupId);

    @Query("MATCH(group:Group{deleted:false}) WHERE id(group) IN {0} " +
            "OPTIONAL MATCH (group)-[rel:" + HAS_FILTERS + "]->(filterSelection:FilterSelection)" +
            "RETURN group,COLLECT(rel),COLLECT(filterSelection)")
    List<Group> findAllGroupsByIdSAndDeletedFalse(List<Long> groupIds);

    @Query("MATCH (group:Group)-[rel:" + HAS_FILTERS + "]->(filterSelection:FilterSelection) WHERE id(group)={0} detach delete filterSelection")
    void deleteAllFiltersByGroupId(Long groupId);
}

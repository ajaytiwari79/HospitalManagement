package com.kairos.persistence.repository.organization;

import com.kairos.persistence.model.organization.group.Group;
import com.kairos.persistence.model.organization.group.GroupDTO;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;

/**
 * Created By G.P.Ranjan on 19/11/19
 **/
@Repository
public interface GroupGraphRepository extends Neo4jBaseRepository<Group,Long> {
    @Query("MATCH(unit:Unit)-[:" + HAS_GROUPS + "]->(group:Group{deleted:false}) WHERE id(unit)={0} AND id(group)<>{1} AND group.name =~{2}  \n" +
            "RETURN COUNT(group)>0")
    boolean existsByName(Long unitId, Long groupId, String name);

    @Query("MATCH(group:Group{deleted:false}) WHERE id(group)={0} RETURN group")
    Group findGroupByIdAndDeletedFalse(Long groupId);
}

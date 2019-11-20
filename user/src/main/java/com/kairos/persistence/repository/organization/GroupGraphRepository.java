package com.kairos.persistence.repository.organization;

import com.kairos.persistence.model.organization.group.Group;
import com.kairos.persistence.model.organization.group.GroupDTO;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;

/**
 * Created By G.P.Ranjan on 19/11/19
 **/
public interface GroupGraphRepository extends Neo4jBaseRepository<Group,Long> {
    @Query("MATCH(unit:Unit)-[:" + HAS_GROUPS + "]->(group:Group {isEnabled:true}) WHERE id(unit)={0} AND id(group)<>{1} AND group.name =~{2}  \n" +
            "RETURN COUNT(group)>0")
    boolean existsByName(Long unitId, Long groupId, String name);

    GroupDTO getGroupById(Long groupId);
}

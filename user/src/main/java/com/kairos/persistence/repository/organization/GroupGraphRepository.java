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

    @Query("MATCH (group:Group) WHERE id(group)={0} with group \n" +
            "OPTIONAL MATCH (group)-[staffRel:" + GROUP_HAS_MEMBER + "]->(groupMembers:Staff) \n" +
            "WITH group,COLLECT(id(groupMembers)) as staffIds \n" +
            "RETURN id(group) as id, group.name as name, staffIds")
    GroupDTO getGroupDetailsById(Long groupId);
}

package com.kairos.persistence.repository.organization;

import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.organization.OrganizationService;
import com.kairos.persistence.model.organization.group.Group;
import com.kairos.persistence.model.user.skill.Skill;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

import static com.kairos.persistence.model.constants.RelationshipConstants.GROUP_HAS_SKILLS;
import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_GROUP;
import static com.kairos.persistence.model.constants.RelationshipConstants.ORGANISATION_HAS_SKILL;


/**
 * Created by oodles on 7/10/16.
 */
@Repository
public interface GroupGraphRepository extends GraphRepository<Group> {

    @Query(" MATCH (o:Organization)-[:" + HAS_GROUP + "]->(g:Group) where id(g) = {0} with o as org " +
            " MATCH (org)-[:" + ORGANISATION_HAS_SKILL + "]->(s:Skill)  with s AS skill " +
            " MATCH (sc:SkillCategory)-[:HAS_CATEGORY]-(skill) return " +
            " { id:id(sc), " +
            "  name:sc.name, " +
            "  skills:collect({ " +
            "  id:id(skill), " +
            "  name:skill.name " +
            " }) " +
            " } AS skillList ")
    List<Map<String, Object>> getGroupOrganizationSkills(Long groupId);


    @Query(" Match (g:Group),(s:Skill) where id(g) IN {0} AND id(s)={1}  " +
            " CREATE (g)-[:GROUP_HAS_SKILLS]->(s) return s")
    List<Skill> saveSkill(Long groupId, Long[] skill);

    @Query(" Match (g:Group),(os:OrganizationService) where id(g) IN {0} AND id(os)={1}  " +
            " CREATE (g)-[:GROUP_HAS_SERVICES]->(os) return os")
    List<OrganizationService> addSelectedService(Long groupId, Long[] service);

    @Query("MATCH(g:Group)-[:" + GROUP_HAS_SKILLS + "]->(s:Skill) where id(g)={0}" +
            " with s AS skill " +
            "MATCH (sc:SkillCategory)-[:HAS_CATEGORY]-(skill) return  " +
            "{ id:id(sc), " +
            "  name:sc.name, " +
            "  skills:collect({ " +
            "  id:id(skill), " +
            "  name:skill.name " +
            "}) " +
            "} AS skillList ")
    List<Map<String, Object>> getGroupSelectedSkills(Long groupId);

    @Query(" MATCH (grp:Group) where id(grp)={0} with grp AS group ")
    Organization getUnitForThisGroup(Long groupId);

    @Query("start group=node({0})\n" +
            "match(group)-[:"+HAS_GROUP+"]-(org:Organization)\n" +
            "return org")
    Organization getUnitByGroupId(Long groupId);
}

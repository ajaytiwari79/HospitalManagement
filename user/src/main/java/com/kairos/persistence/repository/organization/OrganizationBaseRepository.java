package com.kairos.persistence.repository.organization;
/*
 *Created By Pavan on 30/5/19
 *
 */

import com.kairos.persistence.model.organization.OrganizationBaseEntity;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;

@Repository
public interface OrganizationBaseRepository extends Neo4jBaseRepository<OrganizationBaseEntity,Long> {
    @Override
    OrganizationBaseEntity findOne(Long id);

    @Query("MATCH(n{deleted:false}) where id(n)={0} " +
            "OPTIONAL MATCH(n)-[r:"+BELONGS_TO+"]-(country:Country) RETURN n,r,country")
    OrganizationBaseEntity findOneById(Long id);


    @Query("MATCH(n{deleted:false}) where id(n)={0}\n" +
            "OPTIONAL MATCH(n)<-[:"+HAS_UNIT+"]-(org) \n" +
            "RETURN CASE WHEN 'Organization' in labels(n) THEN id(n) ELSE id(org) end as parentOrgId")
    Long findParentOrgId(Long id);

    @Query("MATCH(org) where id(org)={0} RETURN org.showCountryTags ")
    boolean showCountryTags(Long orgId);

    @Query("MATCH(o{isEnable:true,boardingCompleted: true}) where id(o)={0}\n" +
            "OPTIONAL MATCH(o)-[orgRel:HAS_SUB_ORGANIZATION*]->(org:Organization{isEnable:true,boardingCompleted: true}) \n" +
            "OPTIONAL MATCH(o)-[unitRel:HAS_UNIT]->(u:Unit{isEnable:true,boardingCompleted: true}) \n" +
            "OPTIONAL MATCH(org)-[orgUnitRel:HAS_UNIT]->(un:Unit{isEnable:true,boardingCompleted: true}) \n" +
            "WITH collect(id(u)) as unit,collect(id(un)) as uis,CASE WHEN 'Unit' IN labels(o) THEN collect(id(o)) ELSE [] END as data\n" +
            "WITH unit+uis+data as t\n" +
            "unwind t as x with distinct x\n" +
            "Return x")
    List<Long> fetchAllUnitIds(Long orgId);
}

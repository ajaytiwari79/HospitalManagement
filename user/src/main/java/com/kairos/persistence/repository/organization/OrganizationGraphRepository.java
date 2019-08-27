package com.kairos.persistence.repository.organization;/*
 *Created By Pavan on 27/5/19
 *
 */

import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.organization.OrganizationBaseEntity;
import com.kairos.persistence.model.organization.union.UnionDataQueryResult;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;

@Repository
public interface OrganizationGraphRepository extends Neo4jBaseRepository<Organization,Long> {

    Organization findByExternalId(String externalId);

    Organization findByKmdExternalId(String kmdExternalId);


    @Query("MATCH(o:Organization{isEnable:true,boardingCompleted: true,isKairoHub:true}) RETURN o limit 1")
    Organization findHub();


    @Query("MATCH(o{isEnable:true,boardingCompleted: true}) where id(o) IN {0} "+
            "OPTIONAL MATCH(o)-[orgRel:"+HAS_SUB_ORGANIZATION+"*]->(org:Organization{isEnable:true,boardingCompleted: true}) " +
            "OPTIONAL MATCH(o)-[unitRel:"+HAS_UNIT+"]->(u:Unit{isEnable:true,boardingCompleted: true}) " +
            "OPTIONAL MATCH(org)-[orgUnitRel:"+HAS_UNIT+"]->(un:Unit{isEnable:true,boardingCompleted: true}) " +
            "RETURN o,org,orgRel,unitRel,u,orgUnitRel,un")
    List<OrganizationBaseEntity> generateHierarchy(Collection<Long> ids);

    @Query("MATCH (union:Organization{union:true,isEnable:true}) WHERE id (union)={0}  RETURN union")
    Organization findByIdAndUnionTrueAndIsEnableTrue(Long unionId);

    @Query("MATCH(union:Unit{deleted:false,union:true}) WHERE id(union)<>{1} AND union.name=~{0} RETURN count(union)>0")
    boolean existsByName(String name,Long unionId);

    @Query("MATCH(union:Organization{deleted:false,union:true}) WHERE id(union)={0} or union.name={1} WITH union MATCH(union)-[:" + BELONGS_TO + "]-(country:Country) WITH union,country OPTIONAL " +
            "MATCH(union)-[:" + HAS_SECTOR + "]-(sector:Sector) WITH union,collect(sector) as sectors,country OPTIONAL MATCH(union)-[:" + CONTACT_ADDRESS + "]-" +
            "(address:ContactAddress) OPTIONAL MATCH(address)-[:" + ZIP_CODE + "]-(zipCode:ZipCode) WITH union,sectors,address,zipCode,country OPTIONAL MATCH(address)-[:" + MUNICIPALITY + "]-" +
            "(municipality:Municipality) WITH union,sectors,address,zipCode,country,municipality " +
            "OPTIONAL MATCH(union)-[:" + HAS_LOCATION + "]-(location:Location{deleted:false})" +
            "RETURN union,country,address,zipCode,sectors,municipality,collect(location) as locations ")
    List<UnionDataQueryResult> getUnionCompleteById(Long unionId, String name);

    @Query("MATCH (n:Organization) WHERE id(n)={0}\n" +
            "MATCH (n)-[:" + SUB_TYPE_OF + "]->(subType:OrganizationType) WITH subType,n\n" +
            "MATCH (subType)-[:" + ORGANIZATION_TYPE_HAS_SERVICES + "]->(organizationService:OrganizationService) WITH organizationService,n\n" +
            "create unique (n)-[:" + PROVIDE_SERVICE + "{isEnabled:true,creationDate:{1},lastModificationDate:{2}}]->(organizationService) ")
    void assignDefaultServicesToOrg(long orgId, long creationDate, long lastModificationDate);


    @Query("MATCH (organization:Organization) WHERE id(organization)={0} \n" +
            "MATCH (organization)-[:" + SUB_TYPE_OF + "]->(subType:OrganizationType) \n" +
            "MATCH (subType)-[:" + ORG_TYPE_HAS_SKILL + "]->(skill:Skill) WITH skill,organization\n" +
            "create unique (organization)-[r:" + ORGANISATION_HAS_SKILL + "{creationDate:{1},lastModificationDate:{2},isEnabled:true,customName:skill.name}]->(skill)")
    void assignDefaultSkillsToOrg(long orgId, long creationDate, long lastModificationDate);
}

package com.planner.repository.shift_planning;

import com.planner.domain.query_results.dummy_model.Dummy;
import com.planner.domain.query_results.organization_service.OrganizationServiceQueryResult;
import com.planner.domain.query_results.staff.StaffQueryResult;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.List;

import static com.planner.constants.AppConstants.*;

//@Repository

/**
 * Very important Note:-
 * This interface must not contain any named query
 * i.e this interface must contain methods annotated with {@Query}
 */
public interface UserNeo4jRepo extends Neo4jRepository<Dummy, Long> {
    @Query("MATCH (organization:Organization) where id(organization)={0} with organization  " +
            "MATCH (organization)-[:"+CONTACT_ADDRESS+"]->(contactAddress:ContactAddress)-[:"+MUNICIPALITY+"]->(municipality:Municipality)-[:"+PROVINCE+"]->(province:Province)-[:"+REGION+"]->(region:Region) with region \n" +
            "MATCH (region)-[:"+BELONGS_TO+"]->(country:Country) RETURN id(country)")
    Long getCountryIdByUnitId(long unitId);

    @Query("Match(unit:Organization) where id(unit)={0} " +
            "Match(staff:Staff)  where id(staff) in {1} with staff,unit " +
            "Optional Match(skill:Skill{isEnabled:true})<-[:" + STAFF_HAS_SKILLS + "]-(staff) " +
            "Optional Match(unit)<-[:" + IN_UNIT + "]-(employment:Employment)<-[:" + BELONGS_TO_STAFF + "]-(staff) " +
            "return " +
            "id(staff) as staffId,\n" +
            "staff.firstName+staff.lastName as staffName,\n" +
            "collect({skillId:id(skill),name:skill.name,weight:skill.weight}) as staffSkills,\n" +
            "id(employment) as employmentId limit 1"
    )
    List<StaffQueryResult> getStaffWithSkillsAndEmploymentIds(Long unitId, List<Long> staffIds);

    /**
     * this method will return all OrganizationServices and Its SubServices
     * by country Id
     *
     * @param countryId
     * @return
     */
    @Query("Match(c:Country{deleted:false}) where id(c)={0} with c " +
            "Match (os:OrganizationService{deleted:false,isEnabled:true})<-[:" + HAS_ORGANIZATION_SERVICES + "]-(c) " +
            "Match (osSub:OrganizationService{deleted:false,isEnabled:true})<-[:" + ORGANIZATION_SUB_SERVICE + "]-(os) " +
            "return id(os) as id,os.name as name,CASE WHEN osSub IS NULL THEN [] ELSE collect({id:id(osSub),name:osSub.name}) END as organizationSubServices")
    List<OrganizationServiceQueryResult> getAllOrganizationServices(Long countryId);

    @Query("Match(os:OrganizationService{deleted:false,isEnabled:true})-[:"+ORGANIZATION_SUB_SERVICE+"]->(ossub:OrganizationService{deleted:false,isEnabled:true})<-[:"+PROVIDE_SERVICE+"]-(o:Organization) where id(os)={0} AND id(ossub)={1} return id(o)")
    List<Long> getUnitIdsByOrganizationServiceAndSubServiceId(Long organizationServiceId,Long organizationSubServiceId);


    @Query("Match(os:OrganizationService{deleted:false,isEnabled:true})-[:"+ORGANIZATION_SUB_SERVICE+"]->(ossub:OrganizationService{deleted:false,isEnabled:true})<-[:"+PROVIDE_SERVICE+"]-(o:Organization) WHERE id(ossub) IN {0} RETURN DISTINCT id(o)")
    List<Long> getUnitIdsByOrganizationSubServiceIds(List<Long> organizationSubServiceIds);


    //=======Below validations might not required============FixMe
    @Query("Optional Match(c:Country) where id(c)={0} " +
            "Optional Match(os:OrganizationService{deleted:false,isEnabled:true}) where id(os)={1} " +
            "Optional Match(osSub:OrganizationService{deleted:false,isEnabled:true}) where id(osSub)={2} " +
            "Optional Match (c)-[link:"+HAS_ORGANIZATION_SERVICES+"]-(os)-[child:"+ORGANIZATION_SUB_SERVICE+"]-(osSub) " +
            "return " +
            "case when c is null then \"countryNotExists\" " +
            "when os is null then \"organizationServiceNotExists\" " +
            "when osSub is null then \"organizationSubServiceNotExists\" " +
            "when child is null or link is null  then \"relationShipNotValid\" " +
            "else \"valid\" end as result")
    String validateCountryOrganizationServiceAndSubService(Long countryId,Long organizationServiceId,Long organizationSubServiceId);

    @Query("Optional Match(c:Country) where id(c)={0} " +
            "Optional Match(os:OrganizationService{deleted:false,isEnabled:true}) where id(os)={1} " +
            "Optional Match(osSub:OrganizationService{deleted:false,isEnabled:true}) where id(osSub)={2} " +
            "Optional Match (c)-[link:"+HAS_ORGANIZATION_SERVICES+"]-(os)-[child:"+ORGANIZATION_SUB_SERVICE+"]-(osSub) " +
            "return " +
            "case when c is null then \"countryNotExists\" " +
            "when os is null then \"organizationServiceNotExists\" " +
            "when osSub is null then \"organizationSubServiceNotExists\" " +
            "when child is null or link is null  then \"relationShipNotValid\" " +
            "else \"valid\" end as result")
    String validateCountryOrganizationServiceAndSubService(Long countryId,List<Long> organizationSubServiceIds);

    @Query("Optional Match(unit:Organization) where id(unit)={0} " +
            "return " +
            "case when unit is null then \"unitNotExists\" else \"valid\" end " +
            "as result")
    String validateUnit(Long unitId);

    @Query("MATCH  (unit:Organization)-[:SUB_TYPE_OF]-(o:OrganizationType)-[:ORGANIZATION_TYPE_HAS_SERVICES]->(ss:OrganizationService{isEnabled:true}) where id(unit) = {0} \n" +
            "MATCH (ss)<-[:ORGANIZATION_SUB_SERVICE]-(os:OrganizationService {isEnabled:true} ) \n" +
            "WITH case when os  is NULL then [] else collect({id:id(ss),name:ss.name}) END as organizationSubServices,os\n" +
            "RETURN  id(os) as id ,os.name as name,organizationSubServices as organizationSubServices")
    List<OrganizationServiceQueryResult> getAllOrganizationServicesByUnitId(Long unitId);
}

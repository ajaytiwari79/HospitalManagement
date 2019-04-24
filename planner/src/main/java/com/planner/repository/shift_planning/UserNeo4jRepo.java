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
    @Query("Match(c:Country) where id(c)={0} with c " +
            "Match (os:OrganizationService)<-[:" + HAS_ORGANIZATION_SERVICES + "]-(c) " +
            "Match (osSub:OrganizationService)<-[:" + ORGANIZATION_SUB_SERVICE + "]-(os) " +
            "return id(os) as id,os.name as name,CASE WHEN osSub IS NULL THEN [] ELSE collect({id:id(osSub),name:osSub.name}) END as organizationSubServices")
    List<OrganizationServiceQueryResult> getAllOrganizationServices(Long countryId);

    @Query("Match(os:OrganizationService)-[:"+ORGANIZATION_SUB_SERVICE+"]->(ossub:OrganizationService)<-[:"+PROVIDE_SERVICE+"]-(o:Organization) where id(os)={0} AND id(ossub)={1} return id(o)")
    List<Long> getUnitIdsByOrganizationServiceAndSubServiceId(Long organizationServiceId,Long organizationSubServiceId);


    //=======Below validations might not required============FixMe
    @Query("Optional Match(c:Country) where id(c)={0} " +
            "Optional Match(os:OrganizationService) where id(os)={1} " +
            "Optional Match(osSub:OrganizationService) where id(osSub)={2} " +
            "Optional Match (c)-[link:"+HAS_ORGANIZATION_SERVICES+"]-(os)-[child:"+ORGANIZATION_SUB_SERVICE+"]-(osSub) " +
            "return " +
            "case when c is null then \"countryNotExists\" " +
            "when os is null then \"organizationServiceNotExists\" " +
            "when osSub is null then \"organizationSubServiceNotExists\" " +
            "when child is null or link is null  then \"relationShipNotValid\" " +
            "else \"valid\" end as result")
    String validateCountryOrganizationServiceAndSubService(Long countryId,Long organizationServiceId,Long organizationSubServiceId);

    @Query("Optional Match(unit:Organization) where id(unit)={0} " +
            "return " +
            "case when unit is null then \"unitNotExists\" else \"valid\" end " +
            "as result")
    String validateUnit(Long unitId);
}

package com.kairos.persistence.repository.user.unit_position;


import com.kairos.persistence.model.staff.employment.EmploymentUnitPositionQueryResult;
import com.kairos.persistence.model.user.unit_position.*;
import com.kairos.persistence.model.user.unit_position.query_result.*;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;

/**
 * Created by pawanmandhan on 26/7/17.
 */

@Repository
public interface UnitPositionGraphRepository extends Neo4jBaseRepository<UnitPosition, Long> {

    @Query("MATCH (unitPosition:UnitPosition{deleted:false}) where id(unitPosition)={0}\n" +
            "match (unitPosition)-[:" + HAS_EMPLOYMENT_TYPE + "]->(et:EmploymentType)\n" +
            "match (unitPosition)-[:" + HAS_EXPERTISE_IN + "]->(e:Expertise)\n" +
            "optional match (unitPosition)-[rel:" + APPLIED_FUNCTION + "]->(appliedFunction:Function)  \n" +
            "return e as expertise," +
            "unitPosition.totalWeeklyHours as totalWeeklyHours," +
            "unitPosition.startDate as startDate," +
            "unitPosition.endDate as endDate," +
            "unitPosition.salary as salary," +
            "unitPosition.workingDaysInWeek as workingDaysInWeek," +
            "et as employmentType," +
            "unitPosition.hourlyWages as hourlyWages," +
            "id(unitPosition)   as id,unitPosition.history as history,unitPosition.editable as editable,unitPosition.published as published," +
            "unitPosition.avgDailyWorkingHours as avgDailyWorkingHours," +
            "unitPosition.lastWorkingDate as lastWorkingDate," +
            "unitPosition.totalWeeklyMinutes as totalWeeklyMinutes," +
            "unitPosition.fullTimeWeeklyMinutes as fullTimeWeeklyMinutes, " +
            "Collect({id:id(appliedFunction),name:appliedFunction.name,icon:appliedFunction.icon,appliedDates:rel.appliedDates}) as appliedFunctions")
    StaffUnitPositionDetails getUnitPositionById(long unitEmploymentId);



    /*@Query("Match (org:Organization) where id(org)={0} WITH org\n" +
            "Match (e:Expertise) where id(e)={1} WITH e,org\n" +
            "OPTIONAL Match (org)-[:" + HAS_WTA + "]->(wta:WorkingTimeAgreement{disabled:false}) WITH  wta,org,e\n" +
            "MATCH (wta)-[:" + HAS_EXPERTISE_IN + "]->(e) \n" +
            "return collect(wta)")
    List<WorkingTimeAgreement> getWtaByExpertise(Long organizationId, Long expertiseId);*/


    @Query("match(s:Staff)-[:" + BELONGS_TO_STAFF + "]-(unitPosition:UnitPosition{deleted:false,published:true})-[:" + IN_UNIT + "]-(o:Organization) where id(o)={0} AND id(s)={1} \n" +
            "match(unitPosition)-[:HAS_EXPERTISE_IN]-(e:Expertise) where id(e)={2}\n" +
            "return unitPosition ORDER BY unitPosition.startDate")
    List<UnitPosition> getStaffUnitPositionsByExpertise(Long unitId, Long staffId, Long expertiseId);


    @Query("match(s:Staff)-[:" + BELONGS_TO_STAFF + "]-(unitPosition:UnitPosition{deleted:false,published:true})-[:" + IN_UNIT + "]-(o:Organization) where id(o)={0} AND id(s)={1}  AND Id(unitPosition)<>{3}\n" +
            "match(unitPosition)-[:" + HAS_EXPERTISE_IN + "]-(e:Expertise) where id(e)={2}\n" +
            "return unitPosition")
    List<UnitPosition> getAllUEPByExpertiseExcludingCurrent(Long unitId, Long staffId, Long expertiseId, Long currentUnitPositionId);

   /* @Query("Match (org:Organization) where id(org)={0}\n" +
            "Match (e:Expertise) where id(e)={1}\n" +
            "OPTIONAL MATCH (org)-[:" + HAS_WTA + "]->(wta:WorkingTimeAgreement{deleted:false})-[:" + HAS_EXPERTISE_IN + "]->(e)\n" +
            "return wta LIMIT 1")
    WorkingTimeAgreement getOneDefaultWTA(Long organizationId, Long expertiseId);*/


    @Query("MATCH (user:User)-[:BELONGS_TO]-(staff:Staff) where id(user)={0}\n" +
            "match(staff)<-[:BELONGS_TO]-(employment:Employment)<-[:HAS_EMPLOYMENTS]-(org:Organization) \n" +
            "match(org)-[:HAS_SUB_ORGANIZATION*]->(subOrg:Organization)  \n" +
            "optional match(subOrg)<-[:IN_UNIT]-(unitPosition:UnitPosition{deleted:false})<-[:BELONGS_TO_STAFF]-(staff) with unitPosition,org,subOrg,staff,employment \n" +
            "match(unitPosition)-[:HAS_EXPERTISE_IN]->(expertise:Expertise) \n" +
            "match(unitPosition)-[:HAS_POSITION_CODE]->(positionCode:PositionCode{deleted:false}) \n" +
            "OPTIONAL MATCH (unitPosition)-[:HAS_REASON_CODE]->(reasonCode:ReasonCode) \n" +
            "optional match (expertise)-[:SUPPORTED_BY_UNION]->(unionData:Organization{isEnable:true,union:true}) \n" +
            "return expertise as expertise,unionData as union, positionCode as positionCode, id(unitPosition) as id,\n" +
            " unitPosition.startDate as startDate, unitPosition.endDate as endDate, \n" +
            "CASE reasonCode WHEN null THEN null else {id:id(reasonCode),name:reasonCode.name} END as reasonCode,unitPosition.history as history,unitPosition.editable as editable,unitPosition.published as published, \n" +
            "unitPosition.lastWorkingDate as lastWorkingDate,id(org) as parentUnitId, id(subOrg) as unitId, {id:id(subOrg),name:subOrg.name} as unitInfo " +
            "UNION " +
            "MATCH (user:User)-[:BELONGS_TO]-(staff:Staff) where id(user)={0} \n" +
            "match(staff)<-[:BELONGS_TO]-(employment:Employment)<-[:HAS_EMPLOYMENTS]-(org:Organization) \n" +
            "match(org)<-[:IN_UNIT]-(unitPosition:UnitPosition{deleted:false})<-[:BELONGS_TO_STAFF]-(staff)  \n" +
            "match(unitPosition)-[:HAS_EXPERTISE_IN]->(expertise:Expertise) \n" +
            "match(unitPosition)-[:HAS_POSITION_CODE]->(positionCode:PositionCode{deleted:false}) \n" +
            "OPTIONAL MATCH (unitPosition)-[:HAS_REASON_CODE]->(reasonCode:ReasonCode) \n" +
            "optional match (expertise)-[:SUPPORTED_BY_UNION]->(unionData:Organization{isEnable:true,union:true}) \n" +
            "return expertise as expertise,unionData as union, positionCode as positionCode,id(unitPosition) as id,\n" +
            " unitPosition.startDate as startDate, unitPosition.endDate as endDate, \n" +
            "CASE reasonCode WHEN null THEN null else {id:id(reasonCode),name:reasonCode.name} END as reasonCode,unitPosition.history as history,unitPosition.editable as editable,unitPosition.published as published, \n" +
            "unitPosition.lastWorkingDate as lastWorkingDate,id(org) as parentUnitId,id(org) as unitId,\n" +
            "{id:id(org),name:org.name} as unitInfo ORDER BY unitPosition.startDate")
    List<UnitPositionQueryResult> getAllUnitPositionsByUser(long userId);

    @Query("Match(staff:Staff)-[:" + BELONGS_TO_STAFF + "]->(up:UnitPosition{deleted:false}) where id(staff)={0} return up.startDate as startDate")
    List<Long> getAllUnitPositionsByStaffId(Long staffId);

    @Query("MATCH(unitPosition:UnitPosition)-[:" + IN_UNIT + "]->(subOrg:Organization) where id(unitPosition)={0} " +
            "MATCH(unitPosition)<-[:" + BELONGS_TO_STAFF + "]-(staff:Staff) " +
            "MATCH(staff)<-[:" + BELONGS_TO + "]-(employment:Employment) " +
            "MATCH(employment)<-[:" + HAS_EMPLOYMENTS + "]-(org:Organization) " +
            "RETURN id(subOrg) as unitId,id(org) as parentUnitId")
    UnitPositionQueryResult getUnitIdAndParentUnitIdByUnitPositionId(Long unitPositionId);

    @Query("match(positionLine:PositionLine)-[employmentRel:" + HAS_EMPLOYMENT_TYPE + "]->(employmentType:EmploymentType)  where id(positionLine)={0} return positionLine,employmentRel,employmentType")
    UnitPositionEmploymentTypeRelationShip findEmploymentTypeByUnitPositionId(Long unitPositionId);

    // TODO INCORRECT
    @Query("match(unitPosition:UnitPosition)-[employmentRel:" + HAS_EMPLOYMENT_TYPE + "]-(employmentType:EmploymentType) " +
            "MATCH(unitPosition)-[:" + IN_UNIT + "]-(o:Organization) " +
            "MATCH (unitPosition)-[:" + HAS_EXPERTISE_IN + "]->(e:Expertise)" +
            "where id(o)={0}" +
            "return unitPosition,employmentRel,employmentType")
    List<UnitPositionEmploymentTypeRelationShip> findUnitPositionEmploymentTypeRelationshipByParentOrganizationId(Long parentOrganizationId);

    @Query("match(unitPosition)-[rel:" + HAS_FUNCTION + "]->(functions:Function) where id(unitPosition)={0}  detach delete rel")
    void removeOlderFunctionsFromUnitPosition(Long unitPositionId);

    @Query("Match(staff:Staff)-[:" + BELONGS_TO_STAFF + "]->(up:UnitPosition{deleted:false}) where id(staff)={0} and ( up.startDate > {1} or up.startDate is null)  set up.startDate = {1}")
    void updateUnitPositionEndDateFromEmployment(Long staffId, Long startDate);

    @Query("Match(staff:Staff)-[:" + BELONGS_TO_STAFF + "]->(up:UnitPosition{deleted:false}) where id(staff)={0} and ( up.startDate > {1} or up.startDate is null)  return up")
    List<UnitPosition> getUnitPositionsFromEmploymentEndDate(Long staffId, Long startDate);

    @Query("Match(staff:Staff)-[:" + BELONGS_TO_STAFF + "]->(up:UnitPosition{deleted:false}) where id(staff)={0} Match(staff)<-[:" + BELONGS_TO + "]-(emp:Employment) return min(up.startDate) as earliestUnitPositionstartDate, emp.startDate as employmentstartDate")
    EmploymentUnitPositionQueryResult getEarliestUnitPositionStartDateAndEmploymentByStaffId(Long staffId);

    @Query("Match(staff:Staff)-[:" + BELONGS_TO_STAFF + "]->(up:UnitPosition) where id(up)={0} return id(staff) as staffId")
    Long getStaffIdFromUnitPosition(Long unitPositionId);

    @Query("Match(staff:Staff)-[:" + BELONGS_TO_STAFF + "]->(up:UnitPosition{deleted:false}) where id(staff)={0} return max(up.startDateMillis) as maxStartDateMillis")
    Long getMaxUnitPositionStartDate(Long staffId);

    @Query("match(staff:Staff)<-[:" + BELONGS_TO + "]-(employment:Employment)<-[:" + HAS_EMPLOYMENTS + "]-(org:Organization) where id(staff)={0} " +
            "match(org)-[:" + HAS_SUB_ORGANIZATION + "*]->(subOrg:Organization) \n" +
            "optional match(subOrg)<-[:" + IN_UNIT + "]-(unitPosition:UnitPosition{deleted:false})<-[:" + BELONGS_TO_STAFF + "]-(staff) with unitPosition,org,subOrg,staff \n" +
            "match(unitPosition)-[:" + HAS_EXPERTISE_IN + "]->(expertise:Expertise) \n" +
            "match(unitPosition)-[:" + HAS_POSITION_CODE + "]->(positionCode:PositionCode{deleted:false}) \n" +
            "OPTIONAL MATCH (unitPosition)-[:" + HAS_REASON_CODE + "]->(reasonCode:ReasonCode) \n" +
            "optional match (expertise)-[:" + SUPPORTED_BY_UNION + "]->(unionData:Organization{isEnable:true,union:true}) \n" +
            "RETURN id(unitPosition) as id,unitPosition.startDate as startDate, unitPosition.endDate as endDate, \n" +
            " CASE reasonCode WHEN null THEN null else {id:id(reasonCode),name:reasonCode.name} END as reasonCode, unitPosition.history as history,unitPosition.editable as editable,unitPosition.published as published,\n" +
            "unitPosition.lastWorkingDate as lastWorkingDate,id(org) as parentUnitId, id(subOrg)  as unitId,{id:id(subOrg),name:subOrg.name} as unitInfo,expertise as expertise,unionData as union, positionCode as positionCode " +
            "UNION " +
            "match(staff:Staff)<-[:" + BELONGS_TO + "]-(employment:Employment)<-[:" + HAS_EMPLOYMENTS + "]-(org:Organization) where id(staff)={0} " +
            "match(subOrg)<-[:" + IN_UNIT + "]-(unitPosition:UnitPosition{deleted:false})<-[:" + BELONGS_TO_STAFF + "]-(staff) " +
            "match(unitPosition)-[:" + HAS_EXPERTISE_IN + "]->(expertise:Expertise) \n" +
            "match(unitPosition)-[:" + HAS_POSITION_CODE + "]->(positionCode:PositionCode{deleted:false}) \n" +
            "OPTIONAL MATCH (unitPosition)-[:" + HAS_REASON_CODE + "]->(reasonCode:ReasonCode) \n" +
            "optional match (expertise)-[:" + SUPPORTED_BY_UNION + "]->(unionData:Organization{isEnable:true,union:true}) \n" +
            "RETURN id(unitPosition) as id, unitPosition.startDate as startDate, unitPosition.endDate as endDate, \n" +
            " CASE reasonCode WHEN null THEN null else {id:id(reasonCode),name:reasonCode.name} END as reasonCode, unitPosition.history as history,unitPosition.editable as editable,unitPosition.published as published,\n" +
            "unitPosition.lastWorkingDate as lastWorkingDate,id(org) as parentUnitId, id(org)  as unitId ,\n" +
            "{id:id(org),name:org.name} as unitInfo,expertise as expertise,unionData as union, positionCode as positionCode")
    List<UnitPositionQueryResult> getAllUnitPositionsForCurrentOrganization(long staffId);

    @Query("MATCH(unitPosition:UnitPosition{deleted:false,published:true})-[:" + HAS_EXPERTISE_IN + "]->(expertise:Expertise) \n" +
            "MATCH(unitPosition)<-[:" + BELONGS_TO_STAFF + "]-(staff:Staff) " +
            "MATCH(unitPosition)-[:" + IN_UNIT + "]-(organization:Organization) where id(organization)={0} AND id(staff)={1} and id(expertise)={2} \n" +
            "AND unitPosition.startDate<={3} AND  (unitPosition.startDate IS NULL or unitPosition.startDate>={3})  \n" +
            "return id(unitPosition)")
    Long getUnitPositionIdByStaffAndExpertise(Long unitId, Long staffId, Long expertiseId, Long currentMillis);

    @Query("MATCH (unit:Organization) WHERE id(unit)={0} \n" +
            "MATCH (unit)<-[:" + IN_UNIT + "]-(unitPosition:UnitPosition{deleted:false,published:true})-[:" + HAS_EXPERTISE_IN + "]-(expertise:Expertise) \n" +
            "RETURN id(unitPosition) as unitPositionId, id(expertise) as expertiseId")
    List<Map<Long, Long>> getMapOfUnitPositionAndExpertiseId(Long unitId);


    @Query("MATCH (user:User)-[:BELONGS_TO]-(staff:Staff)<-[:" + BELONGS_TO + "]-(employment:Employment)<-[:HAS_EMPLOYMENTS]-(org:Organization) where id(user)={0}\n" +
            "match(org)-[:HAS_SUB_ORGANIZATION*]->(subOrg:Organization)\n" +
            "match(subOrg)<-[:IN_UNIT]-(unitPosition:UnitPosition{deleted:false,published:true})<-[:BELONGS_TO_STAFF]-(staff)\n" +
            "match(unitPosition)-[:HAS_POSITION_CODE]->(positionCode:PositionCode{deleted:false}) \n" +
            "return  id(unitPosition) as id,positionCode as positionCode,unitPosition.history as history, \n" +
            "id(org) as parentUnitId, id(subOrg) as unitId, {id:id(subOrg),name:subOrg.name} as unitInfo ORDER BY unitPosition.creationDate" +
            " UNION " +
            "MATCH (user:User)-[:BELONGS_TO]-(staff:Staff) where id(user)={0}\n" +
            "match(staff)<-[:BELONGS_TO]-(employment:Employment)<-[:HAS_EMPLOYMENTS]-(org:Organization) \n" +
            "match(org)<-[:IN_UNIT]-(unitPosition:UnitPosition{deleted:false,published:true})<-[:BELONGS_TO_STAFF]-(staff)  \n" +
            "match(unitPosition)-[:HAS_POSITION_CODE]->(positionCode:PositionCode{deleted:false}) \n" +
            "return id(unitPosition) as id, positionCode as positionCode,unitPosition.history as history,\n" +
            "id(org) as parentUnitId,id(org) as unitId,{id:id(org),name:org.name} as unitInfo ORDER BY unitPosition.creationDate")
    List<UnitPositionQueryResult> getAllUnitPositionsBasicDetailsAndWTAByUser(long userId);

    /**
     *
     * @return
     * match(staff : Staff)-[expertise_from_date:STAFF_HAS_EXPERTISE]->(expertise:Expertise{deleted:false}) where expertise_from_date.expertiseStartDate is not null
     * and datetime({epochmillis:expertise_from_date.expertiseStartDate}).month=datetime().month and
     * datetime({epochmillis:expertise_from_date.expertiseStartDate}).day=datetime().day and datetime({epochmillis:expertise_from_date.expertiseStartDate}).year<>datetime().year
     * Match(activePositionLine:PositionLine)<-[:HAS_POSITION_LINES]-(unitPosition:UnitPosition{deleted:false,published:true})-[:HAS_EXPERTISE_IN]->(expertise) where activePositionLine.endDate is null OR activePositionLine.endDate >= date()
     * with staff,expertise,unitPosition,activePositionLine, datetime().year-datetime({epochmillis:expertise_from_date.expertiseStartDate}).year as years_experience_in_expertise
     *
     * match(staff)-[:BELONGS_TO_STAFF]->(unitPosition)-[:HAS_EXPERTISE_IN]->(expertise) with staff,unitPosition,expertise,years_experience_in_expertise,activePositionLine
     *
     * match(activePositionLine)-[:HAS_SENIORITY_LEVEL]->(sl:SeniorityLevel) where sl.to<=years_experience_in_expertise
     *
     * match(activePositionLine)-[unitPositionEmploymentTypeRelationShip:HAS_EMPLOYMENT_TYPE]->(employmentType:EmploymentType)
     *  match(expertise)-[:FOR_SENIORITY_LEVEL]->(nextSeniorityLevel:SeniorityLevel) where nextSeniorityLevel.from <= years_experience_in_expertise and (nextSeniorityLevel.to > years_experience_in_expertise or nextSeniorityLevel.to is null)
     * return unitPosition,expertise,sl,activePositionLine,nextSeniorityLevel//seniorityLevel,unitPosition,unitPositionEmploymentTypeRelationShip,employmentType,positionLine
     */

    @Query( "match(staff:Staff)-[expertise_from_date:"+STAFF_HAS_EXPERTISE+"]->(expertise:Expertise{deleted:false}) where expertise_from_date.expertiseStartDate is not null\n" +
            "and datetime({epochmillis:expertise_from_date.expertiseStartDate}).month=datetime().month and\n" +
            "datetime({epochmillis:expertise_from_date.expertiseStartDate}).day=datetime().day and datetime({epochmillis:expertise_from_date.expertiseStartDate}).year<>datetime().year \n" +
            "Match(activePositionLine:PositionLine)<-[:"+HAS_POSITION_LINES+"]-(unitPosition:UnitPosition{deleted:false,published:true})-[:"+HAS_EXPERTISE_IN+"]->(expertise) where activePositionLine.endDate is null OR activePositionLine.endDate >= date()\n" +
            "with staff,expertise,unitPosition,activePositionLine, datetime().year-datetime({epochmillis:expertise_from_date.expertiseStartDate}).year as years_experience_in_expertise \n" +
            "match(staff)-[:"+BELONGS_TO_STAFF+"]->(unitPosition)-[:"+HAS_EXPERTISE_IN+"]->(expertise) with staff,unitPosition,expertise,years_experience_in_expertise,activePositionLine\n" +
            "match(activePositionLine)-[:"+HAS_SENIORITY_LEVEL+"]->(sl:SeniorityLevel) where sl.to<=years_experience_in_expertise \n" +
            "match(activePositionLine)-[unitPositionEmploymentTypeRelationShip:"+HAS_EMPLOYMENT_TYPE+"]->(employmentType:EmploymentType) \n" +
            " match(expertise)-[:"+FOR_SENIORITY_LEVEL+"]->(nextSeniorityLevel:SeniorityLevel) where nextSeniorityLevel.from <= years_experience_in_expertise and (nextSeniorityLevel.to > years_experience_in_expertise or nextSeniorityLevel.to is null) \n" +
            "return id(unitPosition) as unitPositionId,unitPositionEmploymentTypeRelationShip,employmentType,activePositionLine as positionLine,nextSeniorityLevel as seniorityLevel")
    List<UnitPositionSeniorityLevelQueryResult> findUnitPositionSeniorityLeveltoUpdate();
//

    @Query("Match(staff:Staff)-[:" + BELONGS_TO_STAFF + "]->(unitPosition:UnitPosition{deleted:false,published:true})-[:" + HAS_EXPERTISE_IN + "]->(expertise:Expertise) where id(staff)={0} " +
            "and id(expertise)=expertiseId and unitPosition.startDate<=timeStamp and (uniPosition.startDate>=timestamp or unitPosition.startDate is null)" +
            "Match(unitPosition)-[:" + HAS_SENIORITY_LEVEL + "]->(seniorityLevel:SeniorityLevel) return unitPosition,seniorityLevel")
    UnitPositionSeniorityLevelQueryResult getSeniorityLevelFromStaffUnitPosition(Long staffId, Long expertiseId);

    @Query("Match(staff:Staff)-[:" + BELONGS_TO_STAFF + "]->(up:UnitPosition)-[:" + HAS_EXPERTISE_IN + "]->(expertise:Expertise) where id(staff)={0} and id(expertise)={1} match(up)-[rel:" + HAS_SENIORITY_LEVEL + "]->(sl:SeniorityLevel) delete rel")
    void deleteUnitPositionSeniorityLevel(Long staffId, Long expertiseId);

    @Query("Match(staff:Staff)-[:" + BELONGS_TO_STAFF + "]->(up:UnitPosition)-[:" + HAS_EXPERTISE_IN + "]->(expetise:Expertise) where id(staff)={0} and id(expetise)={1} match(sl:SeniorityLevel) where id(sl)={2} with up," +
            " sl merge(up)-[:" + HAS_SENIORITY_LEVEL + "]->(sl)")
    void createUnitPositionSeniorityLevelRelatioship(Long staffId, Long expertiseId, Long seniorityLevelId);


    @Query("match(staff:Staff),(unit:Organization) where id(staff)={0} and id(unit)={1} \n" +
            "match(unit)<-[:" + IN_UNIT + "]-(unitPosition:UnitPosition{deleted:false,published:true})<-[:" + BELONGS_TO_STAFF + "]-(staff) where unitPosition.startDate<={2} and (unitPosition.startDate>={2} or unitPosition.startDate is null) \n" +
            "match(unitPosition)-[:" + HAS_EXPERTISE_IN + "]->(expertise:Expertise) \n" +
            "match(unitPosition)-[employmentRel:" + HAS_EMPLOYMENT_TYPE + "]->(employmentType:EmploymentType) \n" +
            "return expertise as expertise, unitPosition.workingDaysInWeek as workingDaysInWeek,\n" +
            "unitPosition.totalWeeklyMinutes as totalWeeklyMinutes, unitPosition.startDate as startDate, unitPosition.endDate as endDate, \n" +
            "{employmentTypeCategory:employmentRel.employmentTypeCategory,name:employmentType.name,id:id(employmentType)} as employmentType, \n" +
            "unitPosition.hourlyWages as hourlyWages, id(unitPosition) as id,unitPosition.avgDailyWorkingHours as avgDailyWorkingHours,\n" +
            "unitPosition.lastWorkingDate as lastWorkingDate,unitPosition.fullTimeWeeklyMinutes as fullTimeWeeklyMinutes")
    UnitPositionQueryResult getUnitPositionOfStaff(Long staffId, Long unitId, Long currentDateMillies);

    @Query("Match(up:UnitPosition)-[:HAS_POSITION_CODE]-(pc:PositionCode) where id(up)={0} return up.published as published ,pc as positionCode")
    UnitPositionQueryResult findByUnitPositionId(Long unitPositionId);

    @Query(" MATCH (unitPosition:UnitPosition) where id(unitPosition) IN {0} " +
            " MATCH(unitPosition)-[:" + HAS_POSITION_LINES + "]-(positionLine:PositionLine) " +
            "MATCH(positionLine)-[:" + HAS_SENIORITY_LEVEL + "]->(seniorityLevel:SeniorityLevel)-[:" + HAS_BASE_PAY_GRADE + "]-(payGrade:PayGrade) " +
            "MATCH(positionLine)-[employmentRel:" + HAS_EMPLOYMENT_TYPE + "]->(employmentType:EmploymentType) " +
            "OPTIONAL MATCH (positionLine)-[:" + HAS_FUNCTION + "]-(function:Function) with unitPosition,positionLine,payGrade,seniorityLevel,employmentType,employmentRel,collect(function) as functionData "+
            "OPTIONAL MATCH(unitPosition)-[:" + IN_UNIT + "]-(org:Organization)-[:" + CONTACT_ADDRESS + "]->(contactAddress:ContactAddress)-[:" + MUNICIPALITY + "]->(municipality:Municipality)-[:" + HAS_MUNICIPALITY + "]-(pga:PayGroupArea)<-[pgaRel:" + HAS_PAY_GROUP_AREA + "]-(payGrade) " +
            " with  unitPosition,positionLine,payGrade,seniorityLevel,employmentType,employmentRel, functionData ,CASE when pgaRel.payGroupAreaAmount IS NULL THEN '0' ELSE toString((toInteger(pgaRel.payGroupAreaAmount)/(52*37))) END as hourlyCost "+
            "return id(positionLine) as id,id(unitPosition) as unitPositionId," +
            "{id:id(seniorityLevel),from:seniorityLevel.from,pensionPercentage:seniorityLevel.pensionPercentage,freeChoicePercentage:seniorityLevel.freeChoicePercentage," +
            " freeChoiceToPension:seniorityLevel.freeChoiceToPension,to:seniorityLevel.to,moreThan:seniorityLevel.moreThan,functions:functionData," +
            "payGrade:{id:id(payGrade),payGradeLevel:payGrade.payGradeLevel}} as seniorityLevel, "+
            " {employmentTypeCategory:employmentRel.employmentTypeCategory,name:employmentType.name,id:id(employmentType)} as employmentType," +
            "positionLine.workingDaysInWeek as workingDaysInWeek,positionLine.fullTimeWeeklyMinutes as fullTimeWeeklyMinutes, \n" +
            "positionLine.totalWeeklyMinutes as totalWeeklyMinutes, positionLine.startDate as startDate, positionLine.endDate as endDate, \n" +
            "positionLine.hourlyWages as hourlyWages,positionLine.avgDailyWorkingHours as avgDailyWorkingHours ORDER BY positionLine.startDate"
    )
    List<PositionLinesQueryResult> findAllPositionLines(List<Long> unitPositionIds);

}

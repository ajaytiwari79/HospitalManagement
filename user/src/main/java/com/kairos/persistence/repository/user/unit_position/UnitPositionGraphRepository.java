package com.kairos.persistence.repository.user.unit_position;


import com.kairos.persistence.model.staff.employment.EmploymentUnitPositionQueryResult;
import com.kairos.persistence.model.staff.personal_details.StaffPersonalDetail;
import com.kairos.persistence.model.user.unit_position.UnitPosition;
import com.kairos.persistence.model.user.unit_position.UnitPositionEmploymentTypeRelationShip;
import com.kairos.persistence.model.user.unit_position.query_result.StaffUnitPositionDetails;
import com.kairos.persistence.model.user.unit_position.query_result.UnitPositionLinesQueryResult;
import com.kairos.persistence.model.user.unit_position.query_result.UnitPositionQueryResult;
import com.kairos.persistence.model.user.unit_position.query_result.UnitPositionSeniorityLevelQueryResult;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;

/**
 * Created by pawanmandhan on 26/7/17.
 */

@Repository
public interface UnitPositionGraphRepository extends Neo4jBaseRepository<UnitPosition, Long> {

    @Query("MATCH (unitPosition:UnitPosition{deleted:false}) where id(unitPosition)={0}" +
            "MATCH(unitPosition)-[:"+HAS_POSITION_LINES+"]-(positionLine:UnitPositionLine) WHERE  date(positionLine.startDate) <= date() AND (NOT exists(positionLine.endDate) OR date(positionLine.endDate) >= date())" +
            "match (unitPosition)-[:" + HAS_EXPERTISE_IN + "]->(expertise:Expertise)\n" +
            "match(positionLine)-[employmentRel:" + HAS_EMPLOYMENT_TYPE + "]->(employmentType:EmploymentType) \n" +
            "with expertise,unitPosition,positionLine,{employmentTypeCategory:employmentRel.employmentTypeCategory,name:employmentType.name,id:id(employmentType)} as employmentType \n" +
            "optional match (unitPosition)-[rel:" + APPLIED_FUNCTION + "]->(appliedFunction:Function)  \n" +
            "return expertise as expertise,unitPosition.startDate as startDate, unitPosition.endDate as endDate, id(unitPosition) as id,unitPosition.lastWorkingDate as lastWorkingDate,\n" +
            "CASE positionLine when null then [] else COLLECT({totalWeeklyMinutes:(positionLine.totalWeeklyMinutes % 60),totalWeeklyHours:(positionLine.totalWeeklyMinutes / 60), hourlyCost:positionLine.hourlyCost,id:id(positionLine), workingDaysInWeek:positionLine.workingDaysInWeek ,\n" +
            " avgDailyWorkingHours:positionLine.avgDailyWorkingHours,employmentType:employmentType}) end as positionLines, "+
            " case appliedFunction when NULL then [] else  Collect({id:id(appliedFunction),name:appliedFunction.name,icon:appliedFunction.icon,appliedDates:rel.appliedDates}) end as appliedFunctions")
    UnitPositionQueryResult getUnitPositionById(Long unitPositionId);


    @Query("Match(staff:Staff{deleted:false}) where id(staff) IN {2}\n" +
            "MATCH (expertise:Expertise) where id(expertise)={1}\n" +
            "match(staff)-[:" + BELONGS_TO_STAFF + "]->(unitPosition:UnitPosition)-[:" + IN_UNIT + "]->(unit:Organization) where id(unit)={0}\n " +
            "MATCH(unitPosition)-[:"+HAS_POSITION_LINES+"]-(positionLine:UnitPositionLine) WHERE  date(positionLine.startDate) <= date() AND (NOT exists(positionLine.endDate) OR date(positionLine.endDate) >= date())" +
            " MATCH (expertise)<-[:" + HAS_EXPERTISE_IN + "]-(unitPosition) \n" +
            "match (positionLine)-[relation:" + HAS_EMPLOYMENT_TYPE + "]->(et:EmploymentType)\n" +
            "with expertise,staff,unit,unitPosition,positionLine,{employmentTypeCategory:relation.employmentTypeCategory,name:et.name,id:id(et)} as employmentType \n" +
            "return id(staff) as staffId,staff as staff,expertise as expertise,unit.unitTimeZone as unitTimeZone," +
            "CASE positionLine when null then [] else COLLECT({totalWeeklyMinutes:(positionLine.totalWeeklyMinutes % 60),totalWeeklyHours:(positionLine.totalWeeklyMinutes / 60), hourlyCost:positionLine.hourlyCost,id:id(positionLine), workingDaysInWeek:positionLine.workingDaysInWeek ,\n" +
            " avgDailyWorkingHours:positionLine.avgDailyWorkingHours,employmentType:employmentType}) end as positionLines , " +
            "id(unitPosition) as id,unitPosition.startDate as startDate")
    List<StaffUnitPositionDetails> getStaffInfoByUnitIdAndStaffId(Long unitId, Long expertiseId, List<Long> staffId);

    @Query("MATCH (staff:Staff) where id(staff) IN {0} " +
            "MATCH(staff)-[rel:"+STAFF_HAS_EXPERTISE+"]->(expertise:Expertise) " +
            "match(staff)-[:" + BELONGS_TO_STAFF + "]->(unitPosition:UnitPosition)"+
            "MATCH(unitPosition)-[:"+HAS_POSITION_LINES+"]-(positionLine:UnitPositionLine) WHERE  date(positionLine.startDate) <= date() AND (NOT exists(positionLine.endDate) OR date(positionLine.endDate) >= date()) " +
            "match (positionLine)-[relation:" + HAS_EMPLOYMENT_TYPE + "]->(et:EmploymentType)\n" +
            "return id(staff) as id,collect(id(expertise)) as expertiseIds,id(et) as employmentTypeId")
    List<StaffPersonalDetail> getStaffDetailByIds(Set<Long> staffId, LocalDate currentDate);


    @Query("MATCH (unitPosition:UnitPosition{deleted:false}) where id(unitPosition) IN {0} \n" +
            "MATCH(unitPosition)-[:"+BELONGS_TO_STAFF+"]-(staff:Staff) \n"+
            "MATCH(unitPosition)-[:"+HAS_POSITION_LINES+"]-(positionLine:UnitPositionLine) WHERE  date(positionLine.startDate) <= date() AND (NOT exists(positionLine.endDate) OR date(positionLine.endDate) >= date())" +
            "match (unitPosition)-[:" + HAS_EXPERTISE_IN + "]->(expertise:Expertise)\n" +
            "match(positionLine)-[employmentRel:" + HAS_EMPLOYMENT_TYPE + "]->(employmentType:EmploymentType) \n" +
            "with staff,expertise,unitPosition,positionLine,{employmentTypeCategory:employmentRel.employmentTypeCategory,name:employmentType.name,id:id(employmentType)} as employmentType \n" +
            "optional match (unitPosition)-[rel:" + APPLIED_FUNCTION + "]->(appliedFunction:Function)  \n" +
            "return expertise as expertise,id(staff) as staffId,unitPosition.startDate as startDate, unitPosition.endDate as endDate, id(unitPosition) as id,unitPosition.lastWorkingDate as lastWorkingDate,\n" +
            "CASE positionLine when null then [] else COLLECT({totalWeeklyMinutes:(positionLine.totalWeeklyMinutes % 60),totalWeeklyHours:(positionLine.totalWeeklyMinutes / 60), hourlyCost:positionLine.hourlyCost,id:id(positionLine), workingDaysInWeek:positionLine.workingDaysInWeek ,\n" +
            " avgDailyWorkingHours:positionLine.avgDailyWorkingHours,employmentType:employmentType}) end as positionLines, "+
            " case appliedFunction when NULL then [] else  Collect({id:id(appliedFunction),name:appliedFunction.name,icon:appliedFunction.icon,appliedDates:rel.appliedDates}) end as appliedFunctions")
    List<UnitPositionQueryResult> getUnitPositionByIds(List<Long> unitPositionIds);



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


    @Query("MATCH (user:User)-[:"+BELONGS_TO+"]-(staff:Staff) where id(user)={0}\n" +
            "match(staff)<-[:"+BELONGS_TO+"]-(employment:Employment)<-[:"+HAS_EMPLOYMENTS+"]-(org:Organization) \n" +
            "match(org)-[:"+HAS_SUB_ORGANIZATION+"*]->(subOrg:Organization)  \n" +
            "optional match(subOrg)<-[:"+IN_UNIT+"]-(unitPosition:UnitPosition{deleted:false})<-[:"+BELONGS_TO_STAFF+"]-(staff) with unitPosition,org,subOrg,staff,employment \n" +
            "match(unitPosition)-[:"+HAS_EXPERTISE_IN+"]->(expertise:Expertise) \n" +
            "match(unitPosition)-[:"+HAS_POSITION_CODE+"]->(positionCode:PositionCode{deleted:false}) \n" +
            "OPTIONAL MATCH (unitPosition)-[:"+HAS_REASON_CODE+"]->(reasonCode:ReasonCode) \n" +
            "optional match (expertise)-[:"+SUPPORTED_BY_UNION+"]->(unionData:Organization{isEnable:true,union:true}) \n" +
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
//Date is not supported as a return type in Bolt protocol version 1.
// Please make sure driver supports at least protocol version 2.
// Driver upgrade is most likely required.; nested exception is org.neo4j.ogm.exception.TransactionException:
// Date is not supported as a return type in Bolt protocol version 1. Please make sure driver supports at least protocol version 2. Driver upgrade is most likely required
    @Query("Match(staff:Staff)-[:" + BELONGS_TO_STAFF + "]->(up:UnitPosition{deleted:false}) where id(staff)={0} return up.startDate as startDate")
    List<String> getAllUnitPositionsByStaffId(Long staffId);

    @Query("MATCH(unitPosition:UnitPosition)-[:" + IN_UNIT + "]->(subOrg:Organization) where id(unitPosition)={0} " +
            "MATCH(unitPosition)<-[:" + BELONGS_TO_STAFF + "]-(staff:Staff) " +
            "MATCH(staff)<-[:" + BELONGS_TO + "]-(employment:Employment) " +
            "MATCH(employment)<-[:" + HAS_EMPLOYMENTS + "]-(org:Organization) " +
            "RETURN id(subOrg) as unitId,id(org) as parentUnitId")
    UnitPositionQueryResult getUnitIdAndParentUnitIdByUnitPositionId(Long unitPositionId);

    @Query("match(positionLine:UnitPositionLine)-[employmentRel:" + HAS_EMPLOYMENT_TYPE + "]->(employmentType:EmploymentType)  where id(positionLine)={0} return positionLine,employmentRel,employmentType")
    UnitPositionEmploymentTypeRelationShip findEmploymentTypeByUnitPositionId(Long unitPositionId);

    // TODO its INCORRECT
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
    List<UnitPosition> getUnitPositionsFromEmploymentEndDate(Long staffId, LocalDate startDate);

    @Query("Match(staff:Staff)-[:" + BELONGS_TO_STAFF + "]->(up:UnitPosition{deleted:false}) where id(staff)={0} Match(staff)<-[:" + BELONGS_TO + "]-(emp:Employment) return min(up.startDate) as earliestUnitPositionstartDate, emp.startDate as employmentstartDate")
    EmploymentUnitPositionQueryResult getEarliestUnitPositionStartDateAndEmploymentByStaffId(Long staffId);

    @Query("Match(staff:Staff)-[:" + BELONGS_TO_STAFF + "]->(up:UnitPosition) where id(up)={0} return id(staff) as staffId")
    Long getStaffIdFromUnitPosition(Long unitPositionId);

    @Query("Match(staff:Staff)-[:" + BELONGS_TO_STAFF + "]->(up:UnitPosition{deleted:false}) where id(staff)={0} return max(up.startDate) as maxStartDate")
    LocalDate getMaxUnitPositionStartDate(Long staffId);

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

    @Query("MATCH(unitPosition:UnitPosition{deleted:false,published:true})-[:" + HAS_EXPERTISE_IN + "]->(expertise:Expertise) where id(expertise)={2}\n" +
            " MATCH(unitPosition)-[:"+HAS_POSITION_LINES+"]-(positionLine:UnitPositionLine) WHERE  date(positionLine.startDate) <= date() AND (NOT exists(positionLine.endDate) OR date(positionLine.endDate) >= date())\n"+
            "MATCH(unitPosition)<-[:" + BELONGS_TO_STAFF + "]-(staff:Staff) where id(staff)={1}" +
            "MATCH(unitPosition)-[:" + IN_UNIT + "]-(organization:Organization) where id(organization)={0}  \n" +
            "return id(unitPosition)")
    Long getUnitPositionIdByStaffAndExpertise(Long unitId, Long staffId, Long expertiseId, LocalDate requestedDate);

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
     * Match(activePositionLine:UnitPositionLine)<-[:HAS_POSITION_LINES]-(unitPosition:UnitPosition{deleted:false,published:true})-[:HAS_EXPERTISE_IN]->(expertise) where activePositionLine.endDate is null OR activePositionLine.endDate >= date()
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
            "Match(activePositionLine:UnitPositionLine)<-[:"+HAS_POSITION_LINES+"]-(unitPosition:UnitPosition{deleted:false,published:true})-[:"+HAS_EXPERTISE_IN+"]->(expertise) where activePositionLine.endDate is null OR activePositionLine.endDate >= date()\n" +
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
            "match(unit)<-[:" + IN_UNIT + "]-(unitPosition:UnitPosition{deleted:false,published:true})<-[:" + BELONGS_TO_STAFF + "]-(staff)" +
            "MATCH(unitPosition)-[:"+HAS_POSITION_LINES+"]-(positionLine:UnitPositionLine) WHERE  date(positionLine.startDate) <= date() AND (NOT exists(positionLine.endDate) OR date(positionLine.endDate) >= date()) \n" +
            "match(unitPosition)-[:" + HAS_EXPERTISE_IN + "]->(expertise:Expertise) \n" +
            "match(positionLine)-[employmentRel:" + HAS_EMPLOYMENT_TYPE + "]->(employmentType:EmploymentType) \n" +
            "with expertise,unitPosition,positionLine,{employmentTypeCategory:employmentRel.employmentTypeCategory,name:employmentType.name,id:id(employmentType)} as employmentType \n" +
            "return expertise as expertise,unitPosition.startDate as startDate, unitPosition.endDate as endDate, id(unitPosition) as id,unitPosition.lastWorkingDate as lastWorkingDate,\n" +
            "CASE positionLine when null then [] else COLLECT({totalWeeklyMinutes:(positionLine.totalWeeklyMinutes%60),totalWeeklyHours:(positionLine.totalWeeklyMinutes / 60),fullTimeWeeklyMinutes:positionLine.fullTimeWeeklyMinutes, hourlyCost:positionLine.hourlyCost,id:id(positionLine), workingDaysInWeek:positionLine.workingDaysInWeek ,\n" +
            " avgDailyWorkingHours:positionLine.avgDailyWorkingHours,employmentType:employmentType}) end as positionLines")
    UnitPositionQueryResult getUnitPositionOfStaff(Long staffId, Long unitId);

    @Query("Match(up:UnitPosition)-[:HAS_POSITION_CODE]-(pc:PositionCode) where id(up)={0} return up.published as published ,pc as positionCode")
    UnitPositionQueryResult findByUnitPositionId(Long unitPositionId);

    @Query(" MATCH (unitPosition:UnitPosition) where id(unitPosition) IN {0} " +
            "MATCH(unitPosition)-[:" + HAS_POSITION_LINES + "]-(positionLine:UnitPositionLine) " +
            "MATCH(positionLine)-[:" + HAS_SENIORITY_LEVEL + "]->(seniorityLevel:SeniorityLevel)-[:" + HAS_BASE_PAY_GRADE + "]-(payGrade:PayGrade) " +
            "MATCH(positionLine)-[employmentRel:" + HAS_EMPLOYMENT_TYPE + "]->(employmentType:EmploymentType) " +
            "OPTIONAL MATCH (positionLine)-[:" + HAS_FUNCTION + "]-(function:Function) "+
            "OPTIONAL MATCH(unitPosition)-[:" + IN_UNIT + "]-(org:Organization)-[:" + CONTACT_ADDRESS + "]->(contactAddress:ContactAddress)-[:" + MUNICIPALITY + "]->(municipality:Municipality)-[:" + HAS_MUNICIPALITY + "]-(pga:PayGroupArea)<-[pgaRel:" + HAS_PAY_GROUP_AREA + "]-(payGrade) " +
            " with  unitPosition,positionLine,payGrade,seniorityLevel,employmentType,employmentRel,pgaRel, case function when  null  then [] else collect({id:id(function),name:function.name}) end as functionData "+
            "return id(positionLine) as id,id(unitPosition) as unitPositionId," +
            "{id:id(seniorityLevel),from:seniorityLevel.from,pensionPercentage:seniorityLevel.pensionPercentage,freeChoicePercentage:seniorityLevel.freeChoicePercentage," +
            " freeChoiceToPension:seniorityLevel.freeChoiceToPension,to:seniorityLevel.to,functions:collect(functionData[0])," +
            "payGrade:{id:id(payGrade),payGradeLevel:payGrade.payGradeLevel}} as seniorityLevel, "+
            " {employmentTypeCategory:employmentRel.employmentTypeCategory,name:employmentType.name,id:id(employmentType)} as employmentType," +
            "positionLine.workingDaysInWeek as workingDaysInWeek,positionLine.fullTimeWeeklyMinutes as fullTimeWeeklyMinutes, \n" +
            "(positionLine.totalWeeklyMinutes % 60) as totalWeeklyMinutes,(positionLine.totalWeeklyMinutes / 60) as  totalWeeklyHours," +
            " positionLine.startDate as startDate, positionLine.endDate as endDate, CASE when pgaRel.payGroupAreaAmount IS NULL THEN 0.0 ELSE toInteger(toString((toInteger(pgaRel.payGroupAreaAmount)/(52*37)))) END as hourlyCost,\n" +
            "positionLine.avgDailyWorkingHours as avgDailyWorkingHours ORDER BY positionLine.startDate"
    )
    List<UnitPositionLinesQueryResult> findAllPositionLines(List<Long> unitPositionIds);



    @Query(" MATCH (unitPosition:UnitPosition) where id(unitPosition) IN {0}\n" +
            "MATCH(unitPosition)-[:"+HAS_POSITION_LINES+"]-(positionLine:UnitPositionLine) \n" +
            "MATCH(positionLine)-[:"+HAS_SENIORITY_LEVEL+"]->(seniorityLevel:SeniorityLevel)-[:HAS_BASE_PAY_GRADE]-(payGrade:PayGrade) \n" +
            "MATCH(unitPosition)-[:HAS_EXPERTISE_IN]->(expertise:Expertise{published:true}) \n" +
            "OPTIONAL MATCH(unitPosition)-[:IN_UNIT]-(org:Organization)-[:CONTACT_ADDRESS]->(contactAddress:ContactAddress)-[:MUNICIPALITY]->(municipality:Municipality)-[:HAS_MUNICIPALITY]-(pga:PayGroupArea)<-[pgaRel:HAS_PAY_GROUP_AREA]-(payGrade) \n" +
            "with  unitPosition,positionLine,payGrade,seniorityLevel, CASE when pgaRel.payGroupAreaAmount IS NULL THEN '0' ELSE toInteger(pgaRel.payGroupAreaAmount) END as hourlyCost \n" +
            " OPTIONAL MATCH (positionLine)-[:HAS_FUNCTION]-(function:Function) \n" +
            " OPTIONAL MATCH(functionalPayment:FunctionalPayment)-[:APPLICABLE_FOR_EXPERTISE]->(expertise) where date(datetime({epochmillis:functionalPayment.startDate})) <= date(positionLine.startDate) AND (positionLine.endDate IS NULL OR date(positionLine.endDate)>= date(datetime({epochmillis:functionalPayment.startDate})))\n" +
            " OPTIONAL MATCH(functionalPayment)-[:FUNCTIONAL_PAYMENT_MATRIX]->(fpm:FunctionalPaymentMatrix) \n" +
            " OPTIONAL MATCH(fpm)-[:SENIORITY_LEVEL_FUNCTIONS]->(slf:SeniorityLevelFunction)-[:FOR_SENIORITY_LEVEL]->(seniorityLevel) \n" +
            " OPTIONAL MATCH(slf)-[rel:HAS_FUNCTIONAL_AMOUNT]-(function) with positionLine, hourlyCost+rel.amount as hourlyCost \n" +
            " return DISTINCT(id(positionLine)) as id,  case hourlyCost when null then 0.0 else toInteger(hourlyCost) end as hourlyCost")
    List<UnitPositionLinesQueryResult> findFunctionalHourlyCost(List<Long> unitPositionIds);


}

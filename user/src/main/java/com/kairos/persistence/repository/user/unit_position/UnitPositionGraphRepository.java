package com.kairos.persistence.repository.user.unit_position;


import com.kairos.persistence.model.country.functions.FunctionWithAmountQueryResult;
import com.kairos.persistence.model.staff.employment.EmploymentUnitPositionQueryResult;
import com.kairos.persistence.model.staff.personal_details.StaffPersonalDetail;
import com.kairos.persistence.model.user.unit_position.UnitPosition;
import com.kairos.persistence.model.user.unit_position.UnitPositionLineEmploymentTypeRelationShip;
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

    @Query("MATCH (unitPosition:UnitPosition{deleted:false}) where id(unitPosition)={0} " +
            "MATCH(unitPosition)-[:"+HAS_POSITION_LINES+"]-(positionLine:UnitPositionLine) WHERE  date(positionLine.startDate) <= date({1}) AND (NOT exists(positionLine.endDate) OR date(positionLine.endDate) >= date({1}))" +
            "MATCH (unitPosition)-[:" + HAS_EXPERTISE_IN + "]->(expertise:Expertise)\n" +
            "MATCH(positionLine)-[employmentRel:" + HAS_EMPLOYMENT_TYPE + "]->(employmentType:EmploymentType) \n" +
            "WITH expertise,unitPosition,positionLine,{employmentTypeCategory:employmentRel.employmentTypeCategory,name:employmentType.name,id:id(employmentType)} as employmentType \n" +
            "optional MATCH (unitPosition)-[rel:" + APPLIED_FUNCTION + "]->(appliedFunction:Function)  \n" +
            "WITH expertise,unitPosition,positionLine, employmentType,CASE WHEN appliedFunction IS NULL THEN [] ELSE Collect({id:id(appliedFunction),name:appliedFunction.name,icon:appliedFunction.icon,appliedDates:rel.appliedDates}) end as appliedFunctions "+
            " RETURN expertise as expertise,unitPosition.startDate as startDate, unitPosition.endDate as endDate, id(unitPosition) as id,unitPosition.lastWorkingDate as lastWorkingDate,\n" +
            "CASE positionLine when null then [] else COLLECT({totalWeeklyMinutes:(positionLine.totalWeeklyMinutes % 60),startDate:positionLine.startDate,totalWeeklyHours:(positionLine.totalWeeklyMinutes / 60), hourlyCost:positionLine.hourlyCost,id:id(positionLine), workingDaysInWeek:positionLine.workingDaysInWeek ,\n" +
            " avgDailyWorkingHours:positionLine.avgDailyWorkingHours,employmentType:employmentType,fullTimeWeeklyMinutes:positionLine.fullTimeWeeklyMinutes,totalWeeklyMinutes:positionLine.totalWeeklyMinutes}) end as positionLines, "+
            " appliedFunctions as appliedFunctions")
    UnitPositionQueryResult getUnitPositionById(Long unitPositionId,String shiftDate);


    @Query("MATCH(staff:Staff{deleted:false}) where id(staff) IN {2}\n" +
            "MATCH (expertise:Expertise) where id(expertise)={1}\n" +
            "MATCH(staff)-[:" + BELONGS_TO_STAFF + "]->(unitPosition:UnitPosition)-[:" + IN_UNIT + "]->(unit:Organization) where id(unit)={0}\n " +
            "MATCH(unitPosition)-[:"+HAS_POSITION_LINES+"]-(positionLine:UnitPositionLine) WHERE  date(positionLine.startDate) <= date() AND (NOT exists(positionLine.endDate) OR date(positionLine.endDate) >= date())" +
            " MATCH (expertise)<-[:" + HAS_EXPERTISE_IN + "]-(unitPosition) \n" +
            "MATCH (positionLine)-[relation:" + HAS_EMPLOYMENT_TYPE + "]->(et:EmploymentType)\n" +
            "WITH expertise,staff,unit,unitPosition,positionLine,{employmentTypeCategory:relation.employmentTypeCategory,name:et.name,id:id(et)} as employmentType \n" +
            "return id(staff) as staffId,staff as staff,expertise as expertise,unit.unitTimeZone as unitTimeZone," +
            "CASE positionLine when null then [] else COLLECT({totalWeeklyMinutes:(positionLine.totalWeeklyMinutes % 60),totalWeeklyHours:(positionLine.totalWeeklyMinutes / 60), hourlyCost:positionLine.hourlyCost,id:id(positionLine), workingDaysInWeek:positionLine.workingDaysInWeek ,\n" +
            " avgDailyWorkingHours:positionLine.avgDailyWorkingHours,employmentType:employmentType}) end as positionLines , " +
            "id(unitPosition) as id,unitPosition.startDate as startDate")
    List<StaffUnitPositionDetails> getStaffInfoByUnitIdAndStaffId(Long unitId, Long expertiseId, List<Long> staffId);

    @Query("MATCH (staff:Staff) where id(staff) IN {0} " +
            "MATCH(staff)-[rel:"+STAFF_HAS_EXPERTISE+"]->(expertise:Expertise) " +
            "MATCH(staff)-[:" + BELONGS_TO_STAFF + "]->(unitPosition:UnitPosition)"+
            "MATCH(unitPosition)-[:"+HAS_POSITION_LINES+"]-(positionLine:UnitPositionLine) WHERE  date(positionLine.startDate) <= date() AND (NOT exists(positionLine.endDate) OR date(positionLine.endDate) >= date()) " +
            "MATCH (positionLine)-[relation:" + HAS_EMPLOYMENT_TYPE + "]->(et:EmploymentType)\n" +
            "return id(staff) as id,collect(id(expertise)) as expertiseIds,id(et) as employmentTypeId")
    List<StaffPersonalDetail> getStaffDetailByIds(Set<Long> staffId, LocalDate currentDate);


    @Query("MATCH (unitPosition:UnitPosition{deleted:false}) where id(unitPosition) IN {0} \n" +
            "MATCH(unitPosition)-[:"+BELONGS_TO_STAFF+"]-(staff:Staff) \n"+
            "MATCH(unitPosition)-[:"+HAS_POSITION_LINES+"]-(positionLine:UnitPositionLine) WHERE  date(positionLine.startDate) <= date() AND (NOT exists(positionLine.endDate) OR date(positionLine.endDate) >= date())" +
            "MATCH (unitPosition)-[:" + HAS_EXPERTISE_IN + "]->(expertise:Expertise)\n" +
            "MATCH(positionLine)-[employmentRel:" + HAS_EMPLOYMENT_TYPE + "]->(employmentType:EmploymentType) \n" +
            "WITH staff,expertise,unitPosition,positionLine,{employmentTypeCategory:employmentRel.employmentTypeCategory,name:employmentType.name,id:id(employmentType)} as employmentType \n" +
            "optional MATCH (unitPosition)-[rel:" + APPLIED_FUNCTION + "]->(appliedFunction:Function)  \n" +
            "return expertise as expertise,id(staff) as staffId,unitPosition.startDate as startDate, unitPosition.endDate as endDate, id(unitPosition) as id,unitPosition.lastWorkingDate as lastWorkingDate,\n" +
            "CASE positionLine when null then [] else COLLECT({totalWeeklyMinutes:(positionLine.totalWeeklyMinutes % 60),totalWeeklyHours:(positionLine.totalWeeklyMinutes / 60), hourlyCost:positionLine.hourlyCost,id:id(positionLine), workingDaysInWeek:positionLine.workingDaysInWeek ,\n" +
            " avgDailyWorkingHours:positionLine.avgDailyWorkingHours,employmentType:employmentType}) end as positionLines, "+
            " case appliedFunction when NULL then [] else  Collect({id:id(appliedFunction),name:appliedFunction.name,icon:appliedFunction.icon,appliedDates:rel.appliedDates}) end as appliedFunctions")
    List<UnitPositionQueryResult> getUnitPositionByIds(List<Long> unitPositionIds);



    @Query("MATCH(s:Staff)-[:" + BELONGS_TO_STAFF + "]-(unitPosition:UnitPosition{deleted:false,published:true})-[:" + IN_UNIT + "]-(o:Organization) where id(o)={0} AND id(s)={1} \n" +
            "MATCH(unitPosition)-[:HAS_EXPERTISE_IN]-(e:Expertise) where id(e)={2}\n" +
            "return unitPosition ORDER BY unitPosition.startDate")
    List<UnitPosition> getStaffUnitPositionsByExpertise(Long unitId, Long staffId, Long expertiseId);


    @Query("MATCH (user:User)-[:"+BELONGS_TO+"]-(staff:Staff) where id(user)={0}\n" +
            "MATCH(staff)<-[:"+BELONGS_TO+"]-(employment:Employment)<-[:"+HAS_EMPLOYMENTS+"]-(org:Organization) \n" +
            "MATCH(org)-[:"+HAS_SUB_ORGANIZATION+"*]->(subOrg:Organization)  \n" +
            "optional MATCH(subOrg)<-[:"+IN_UNIT+"]-(unitPosition:UnitPosition{deleted:false})<-[:"+BELONGS_TO_STAFF+"]-(staff) WITH unitPosition,org,subOrg,staff,employment \n" +
            "MATCH(unitPosition)-[:"+HAS_EXPERTISE_IN+"]->(expertise:Expertise) \n" +
            "MATCH(unitPosition)-[:"+HAS_POSITION_CODE+"]->(positionCode:PositionCode{deleted:false}) \n" +
            "OPTIONAL MATCH (unitPosition)-[:"+HAS_REASON_CODE+"]->(reasonCode:ReasonCode) \n" +
            "optional MATCH (expertise)-[:"+SUPPORTED_BY_UNION+"]->(unionData:Organization{isEnable:true,union:true}) \n" +
            "return expertise as expertise,unionData as union, positionCode as positionCode, id(unitPosition) as id,\n" +
            " unitPosition.startDate as startDate, unitPosition.endDate as endDate, \n" +
            "CASE reasonCode WHEN null THEN null else {id:id(reasonCode),name:reasonCode.name} END as reasonCode,unitPosition.history as history,unitPosition.editable as editable,unitPosition.published as published, \n" +
            "unitPosition.lastWorkingDate as lastWorkingDate,id(org) as parentUnitId, id(subOrg) as unitId, {id:id(subOrg),name:subOrg.name} as unitInfo " +
            "UNION " +
            "MATCH (user:User)-[:"+BELONGS_TO+"]-(staff:Staff) where id(user)={0} \n" +
            "MATCH(staff)<-[:BELONGS_TO]-(employment:Employment)<-[:HAS_EMPLOYMENTS]-(org:Organization) \n" +
            "MATCH(org)<-[:IN_UNIT]-(unitPosition:UnitPosition{deleted:false})<-[:BELONGS_TO_STAFF]-(staff)  \n" +
            "MATCH(unitPosition)-[:HAS_EXPERTISE_IN]->(expertise:Expertise) \n" +
            "MATCH(unitPosition)-[:HAS_POSITION_CODE]->(positionCode:PositionCode{deleted:false}) \n" +
            "OPTIONAL MATCH (unitPosition)-[:HAS_REASON_CODE]->(reasonCode:ReasonCode) \n" +
            "optional MATCH (expertise)-[:"+SUPPORTED_BY_UNION+"]->(unionData:Organization{isEnable:true,union:true}) \n" +
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
    @Query("MATCH(staff:Staff)-[:" + BELONGS_TO_STAFF + "]->(up:UnitPosition{deleted:false}) where id(staff)={0} return up.endDate as endDate")
    List<String> getAllUnitPositionsByStaffId(Long staffId);

    @Query("MATCH(unitPosition:UnitPosition)-[:" + IN_UNIT + "]->(subOrg:Organization) where id(unitPosition)={0} " +
            "MATCH(unitPosition)<-[:" + BELONGS_TO_STAFF + "]-(staff:Staff) " +
            "MATCH(staff)<-[:" + BELONGS_TO + "]-(employment:Employment) " +
            "MATCH(employment)<-[:" + HAS_EMPLOYMENTS + "]-(org:Organization) " +
            "RETURN id(subOrg) as unitId,id(org) as parentUnitId")
    UnitPositionQueryResult getUnitIdAndParentUnitIdByUnitPositionId(Long unitPositionId);

    @Query("MATCH(positionLine:UnitPositionLine)-[employmentRel:" + HAS_EMPLOYMENT_TYPE + "]->(employmentType:EmploymentType)  where id(positionLine)={0} return positionLine,employmentRel,employmentType")
    UnitPositionLineEmploymentTypeRelationShip findEmploymentTypeByUnitPositionId(Long unitPositionId);

    // TODO its INCORRECT
    @Query("MATCH(unitPosition:UnitPosition)-[employmentRel:" + HAS_EMPLOYMENT_TYPE + "]-(employmentType:EmploymentType) " +
            "MATCH(unitPosition)-[:" + IN_UNIT + "]-(o:Organization) " +
            "MATCH (unitPosition)-[:" + HAS_EXPERTISE_IN + "]->(e:Expertise)" +
            "where id(o)={0}" +
            "return unitPosition,employmentRel,employmentType")
    List<UnitPositionLineEmploymentTypeRelationShip> findUnitPositionEmploymentTypeRelationshipByParentOrganizationId(Long parentOrganizationId);

    @Query("MATCH(unitPosition)-[rel:" + HAS_FUNCTION + "]->(functions:Function) where id(unitPosition)={0}  detach delete rel")
    void removeOlderFunctionsFromUnitPosition(Long unitPositionId);

    @Query("MATCH(staff:Staff)-[:" + BELONGS_TO_STAFF + "]->(up:UnitPosition{deleted:false}) where id(staff)={0} and ( up.startDate > {1} or up.startDate is null)  set up.startDate = {1}")
    void updateUnitPositionEndDateFromEmployment(Long staffId, Long startDate);

    @Query("MATCH(staff:Staff)-[:" + BELONGS_TO_STAFF + "]->(up:UnitPosition{deleted:false}) where id(staff)={0} and ( up.startDate > {1} or up.startDate is null)  return up")
    List<UnitPosition> getUnitPositionsFromEmploymentEndDate(Long staffId, LocalDate startDate);

    @Query("MATCH(staff:Staff)-[:" + BELONGS_TO_STAFF + "]->(up:UnitPosition{deleted:false}) where id(staff)={0} MATCH(staff)<-[:" + BELONGS_TO + "]-(emp:Employment) return min(up.startDate) as earliestUnitPositionstartDate, emp.startDate as employmentstartDate")
    EmploymentUnitPositionQueryResult getEarliestUnitPositionStartDateAndEmploymentByStaffId(Long staffId);

    @Query("MATCH(staff:Staff)-[:" + BELONGS_TO_STAFF + "]->(up:UnitPosition) where id(up)={0} return id(staff) as staffId")
    Long getStaffIdFromUnitPosition(Long unitPositionId);

    @Query("MATCH(staff:Staff)-[:" + BELONGS_TO_STAFF + "]->(up:UnitPosition{deleted:false}) where id(staff)={0} return max(up.startDate) as maxStartDate")
    LocalDate getMaxUnitPositionStartDate(Long staffId);

    @Query("MATCH(staff:Staff)<-[:" + BELONGS_TO + "]-(employment:Employment)<-[:" + HAS_EMPLOYMENTS + "]-(org:Organization) where id(staff)={0} " +
            "MATCH(org)-[:" + HAS_SUB_ORGANIZATION + "*]->(subOrg:Organization) \n" +
            "optional MATCH(subOrg)<-[:" + IN_UNIT + "]-(unitPosition:UnitPosition{deleted:false})<-[:" + BELONGS_TO_STAFF + "]-(staff) WITH unitPosition,org,subOrg,staff \n" +
            "MATCH(unitPosition)-[:" + HAS_EXPERTISE_IN + "]->(expertise:Expertise) \n" +
            "MATCH(unitPosition)-[:" + HAS_POSITION_CODE + "]->(positionCode:PositionCode{deleted:false}) \n" +
            "OPTIONAL MATCH (unitPosition)-[:" + HAS_REASON_CODE + "]->(reasonCode:ReasonCode) \n" +
            "optional MATCH (expertise)-[:" + SUPPORTED_BY_UNION + "]->(unionData:Organization{isEnable:true,union:true}) \n" +
            "RETURN id(unitPosition) as id,unitPosition.startDate as startDate, unitPosition.endDate as endDate, \n" +
            " CASE reasonCode WHEN null THEN null else {id:id(reasonCode),name:reasonCode.name} END as reasonCode, unitPosition.history as history,unitPosition.editable as editable,unitPosition.published as published,\n" +
            "unitPosition.lastWorkingDate as lastWorkingDate,id(org) as parentUnitId, id(subOrg)  as unitId,{id:id(subOrg),name:subOrg.name} as unitInfo,expertise as expertise,unionData as union, positionCode as positionCode " +
            "UNION " +
            "MATCH(staff:Staff)<-[:" + BELONGS_TO + "]-(employment:Employment)<-[:" + HAS_EMPLOYMENTS + "]-(org:Organization) where id(staff)={0} " +
            "MATCH(subOrg)<-[:" + IN_UNIT + "]-(unitPosition:UnitPosition{deleted:false})<-[:" + BELONGS_TO_STAFF + "]-(staff) " +
            "MATCH(unitPosition)-[:" + HAS_EXPERTISE_IN + "]->(expertise:Expertise) \n" +
            "MATCH(unitPosition)-[:" + HAS_POSITION_CODE + "]->(positionCode:PositionCode{deleted:false}) \n" +
            "OPTIONAL MATCH (unitPosition)-[:" + HAS_REASON_CODE + "]->(reasonCode:ReasonCode) \n" +
            "optional MATCH (expertise)-[:" + SUPPORTED_BY_UNION + "]->(unionData:Organization{isEnable:true,union:true}) \n" +
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
            "MATCH(org)-[:HAS_SUB_ORGANIZATION*]->(subOrg:Organization)\n" +
            "MATCH(subOrg)<-[:IN_UNIT]-(unitPosition:UnitPosition{deleted:false,published:true})<-[:BELONGS_TO_STAFF]-(staff)\n" +
            "MATCH(unitPosition)-[:HAS_POSITION_CODE]->(positionCode:PositionCode{deleted:false}) \n" +
            "return  id(unitPosition) as id,positionCode as positionCode,unitPosition.history as history, \n" +
            "id(org) as parentUnitId, id(subOrg) as unitId, {id:id(subOrg),name:subOrg.name} as unitInfo ORDER BY unitPosition.creationDate" +
            " UNION " +
            "MATCH (user:User)-[:BELONGS_TO]-(staff:Staff) where id(user)={0}\n" +
            "MATCH(staff)<-[:BELONGS_TO]-(employment:Employment)<-[:HAS_EMPLOYMENTS]-(org:Organization) \n" +
            "MATCH(org)<-[:IN_UNIT]-(unitPosition:UnitPosition{deleted:false,published:true})<-[:BELONGS_TO_STAFF]-(staff)  \n" +
            "MATCH(unitPosition)-[:HAS_POSITION_CODE]->(positionCode:PositionCode{deleted:false}) \n" +
            "return id(unitPosition) as id, positionCode as positionCode,unitPosition.history as history,\n" +
            "id(org) as parentUnitId,id(org) as unitId,{id:id(org),name:org.name} as unitInfo ORDER BY unitPosition.creationDate")
    List<UnitPositionQueryResult> getAllUnitPositionsBasicDetailsAndWTAByUser(long userId);

    /**
     *
     * @return
     * MATCH(staff : Staff)-[expertise_from_date:STAFF_HAS_EXPERTISE]->(expertise:Expertise{deleted:false}) where expertise_from_date.expertiseStartDate is not null
     * and datetime({epochmillis:expertise_from_date.expertiseStartDate}).month=datetime().month and
     * datetime({epochmillis:expertise_from_date.expertiseStartDate}).day=datetime().day and datetime({epochmillis:expertise_from_date.expertiseStartDate}).year<>datetime().year
     * MATCH(activePositionLine:UnitPositionLine)<-[:HAS_POSITION_LINES]-(unitPosition:UnitPosition{deleted:false,published:true})-[:HAS_EXPERTISE_IN]->(expertise) where activePositionLine.endDate is null OR activePositionLine.endDate >= date()
     * WITH staff,expertise,unitPosition,activePositionLine, datetime().year-datetime({epochmillis:expertise_from_date.expertiseStartDate}).year as years_experience_in_expertise
     *
     * MATCH(staff)-[:BELONGS_TO_STAFF]->(unitPosition)-[:HAS_EXPERTISE_IN]->(expertise) WITH staff,unitPosition,expertise,years_experience_in_expertise,activePositionLine
     *
     * MATCH(activePositionLine)-[:HAS_SENIORITY_LEVEL]->(sl:SeniorityLevel) where sl.to<=years_experience_in_expertise
     *
     * MATCH(activePositionLine)-[unitPositionEmploymentTypeRelationShip:HAS_EMPLOYMENT_TYPE]->(employmentType:EmploymentType)
     *  MATCH(expertise)-[:FOR_SENIORITY_LEVEL]->(nextSeniorityLevel:SeniorityLevel) where nextSeniorityLevel.from <= years_experience_in_expertise and (nextSeniorityLevel.to > years_experience_in_expertise or nextSeniorityLevel.to is null)
     * return unitPosition,expertise,sl,activePositionLine,nextSeniorityLevel//seniorityLevel,unitPosition,unitPositionEmploymentTypeRelationShip,employmentType,positionLine
     */

    @Query( "MATCH(staff:Staff)-[expertise_from_date:"+STAFF_HAS_EXPERTISE+"]->(expertise:Expertise{deleted:false}) where expertise_from_date.expertiseStartDate is not null\n" +
            "and datetime({epochmillis:expertise_from_date.expertiseStartDate}).month=datetime().month and\n" +
            "datetime({epochmillis:expertise_from_date.expertiseStartDate}).day=datetime().day and datetime({epochmillis:expertise_from_date.expertiseStartDate}).year<>datetime().year \n" +
            "MATCH(activePositionLine:UnitPositionLine)<-[:"+HAS_POSITION_LINES+"]-(unitPosition:UnitPosition{deleted:false,published:true})-[:"+HAS_EXPERTISE_IN+"]->(expertise) where activePositionLine.endDate is null OR activePositionLine.endDate >= date()\n" +
            "WITH staff,expertise,unitPosition,activePositionLine, datetime().year-datetime({epochmillis:expertise_from_date.expertiseStartDate}).year as years_experience_in_expertise \n" +
            "MATCH(staff)-[:"+BELONGS_TO_STAFF+"]->(unitPosition)-[:"+HAS_EXPERTISE_IN+"]->(expertise) WITH staff,unitPosition,expertise,years_experience_in_expertise,activePositionLine\n" +
            "MATCH(activePositionLine)-[:"+HAS_SENIORITY_LEVEL+"]->(sl:SeniorityLevel) where sl.to<=years_experience_in_expertise \n" +
            "MATCH(activePositionLine)-[unitPositionEmploymentTypeRelationShip:"+HAS_EMPLOYMENT_TYPE+"]->(employmentType:EmploymentType) \n" +
            " MATCH(expertise)-[:"+FOR_SENIORITY_LEVEL+"]->(nextSeniorityLevel:SeniorityLevel) where nextSeniorityLevel.from <= years_experience_in_expertise and (nextSeniorityLevel.to > years_experience_in_expertise or nextSeniorityLevel.to is null) \n" +
            "return id(unitPosition) as unitPositionId,unitPositionEmploymentTypeRelationShip,employmentType,activePositionLine as positionLine,nextSeniorityLevel as seniorityLevel")
    List<UnitPositionSeniorityLevelQueryResult> findUnitPositionSeniorityLeveltoUpdate();
//

    @Query("MATCH(staff:Staff)-[:" + BELONGS_TO_STAFF + "]->(unitPosition:UnitPosition{deleted:false,published:true})-[:" + HAS_EXPERTISE_IN + "]->(expertise:Expertise) where id(staff)={0} " +
            "and id(expertise)=expertiseId and unitPosition.startDate<=timeStamp and (uniPosition.startDate>=timestamp or unitPosition.startDate is null)" +
            "MATCH(unitPosition)-[:" + HAS_SENIORITY_LEVEL + "]->(seniorityLevel:SeniorityLevel) return unitPosition,seniorityLevel")
    UnitPositionSeniorityLevelQueryResult getSeniorityLevelFromStaffUnitPosition(Long staffId, Long expertiseId);

    @Query("MATCH(staff:Staff)-[:" + BELONGS_TO_STAFF + "]->(up:UnitPosition)-[:" + HAS_EXPERTISE_IN + "]->(expertise:Expertise) where id(staff)={0} and id(expertise)={1} MATCH(up)-[rel:" + HAS_SENIORITY_LEVEL + "]->(sl:SeniorityLevel) delete rel")
    void deleteUnitPositionSeniorityLevel(Long staffId, Long expertiseId);

    @Query("MATCH(staff:Staff)-[:" + BELONGS_TO_STAFF + "]->(up:UnitPosition)-[:" + HAS_EXPERTISE_IN + "]->(expetise:Expertise) where id(staff)={0} and id(expetise)={1} MATCH(sl:SeniorityLevel) where id(sl)={2} WITH up," +
            " sl merge(up)-[:" + HAS_SENIORITY_LEVEL + "]->(sl)")
    void createUnitPositionSeniorityLevelRelatioship(Long staffId, Long expertiseId, Long seniorityLevelId);


    @Query("MATCH(staff:Staff),(unit:Organization) where id(staff)={0} and id(unit)={1} \n" +
            "MATCH(unit)<-[:" + IN_UNIT + "]-(unitPosition:UnitPosition{deleted:false,published:true})<-[:" + BELONGS_TO_STAFF + "]-(staff)" +
            "MATCH(unitPosition)-[:"+HAS_POSITION_LINES+"]-(positionLine:UnitPositionLine) WHERE  date(positionLine.startDate) <= date() AND (NOT exists(positionLine.endDate) OR date(positionLine.endDate) >= date()) \n" +
            "MATCH(unitPosition)-[:" + HAS_EXPERTISE_IN + "]->(expertise:Expertise) \n" +
            "MATCH(positionLine)-[employmentRel:" + HAS_EMPLOYMENT_TYPE + "]->(employmentType:EmploymentType) \n" +
            "WITH expertise,unitPosition,positionLine,{employmentTypeCategory:employmentRel.employmentTypeCategory,name:employmentType.name,id:id(employmentType)} as employmentType \n" +
            "return expertise as expertise,unitPosition.startDate as startDate, unitPosition.endDate as endDate, id(unitPosition) as id,unitPosition.lastWorkingDate as lastWorkingDate,\n" +
            "CASE positionLine when null then [] else COLLECT({totalWeeklyMinutes:(positionLine.totalWeeklyMinutes%60),totalWeeklyHours:(positionLine.totalWeeklyMinutes / 60),fullTimeWeeklyMinutes:positionLine.fullTimeWeeklyMinutes, hourlyCost:positionLine.hourlyCost,id:id(positionLine), workingDaysInWeek:positionLine.workingDaysInWeek ,\n" +
            " avgDailyWorkingHours:positionLine.avgDailyWorkingHours,employmentType:employmentType}) end as positionLines")
    UnitPositionQueryResult getUnitPositionOfStaff(Long staffId, Long unitId);

    @Query("MATCH(unitPosition:UnitPosition)-[:HAS_POSITION_CODE]-(pc:PositionCode) where id(unitPosition)={0} return unitPosition.published as published ," +
            "unitPosition.startDate as startDate, unitPosition.endDate as endDate, id(unitPosition) as id,pc as positionCode")
    UnitPositionQueryResult findByUnitPositionId(Long unitPositionId);

    @Query(" MATCH (unitPosition:UnitPosition) where id(unitPosition) IN {0} " +
            "MATCH(unitPosition)-[:" + HAS_POSITION_LINES + "]-(positionLine:UnitPositionLine) " +
            "MATCH(positionLine)-[:" + HAS_SENIORITY_LEVEL + "]->(seniorityLevel:SeniorityLevel)-[:" + HAS_BASE_PAY_GRADE + "]-(payGrade:PayGrade) " +
            "MATCH(positionLine)-[employmentRel:" + HAS_EMPLOYMENT_TYPE + "]->(employmentType:EmploymentType) " +
            "OPTIONAL MATCH (positionLine)-[functionalRel:" + APPLICABLE_FUNCTION + "]-(function:Function) "+
            "OPTIONAL MATCH(unitPosition)-[:" + IN_UNIT + "]-(org:Organization)-[:" + CONTACT_ADDRESS + "]->(contactAddress:ContactAddress)-[:" + MUNICIPALITY + "]->(municipality:Municipality)-[:" + HAS_MUNICIPALITY + "]-(pga:PayGroupArea)<-[pgaRel:" + HAS_PAY_GROUP_AREA + "]-(payGrade) " +
            " WITH  unitPosition,positionLine,payGrade,seniorityLevel,employmentType,employmentRel,pgaRel,functionalRel, case function when  null  then [] else collect({id:id(function),name:function.name,icon:function.icon, amount:functionalRel.amount}) end as functionData "+
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

    @Query(" MATCH (positionLine:UnitPositionLine) where id(positionLine) IN {0} " +
            " MATCH (positionLine)-[functionalRel:" + APPLICABLE_FUNCTION + "]-(function:Function) "+
            "RETURN distinct function as function,functionalRel.amount as amount")
    List<FunctionWithAmountQueryResult> findAllAppliedFunctionOnPositionLines(Long unitPositionLineId);

    @Query(" MATCH (positionLine:UnitPositionLine) where id(positionLine) IN {0} " +
            "MATCH (positionLine)-[functionalRel:" + APPLICABLE_FUNCTION + "]-(function:Function) "+
            "detach delete functionalRel")
    void removeAllAppliedFunctionOnPositionLines(Long unitPositionLineId);


    @Query(" MATCH (unitPosition:UnitPosition{deleted:false}) where id(unitPosition) IN  {0} \n" +
            "MATCH(unitPosition)-[:"+HAS_POSITION_LINES+"]-(positionLine:UnitPositionLine) \n" +
            "MATCH(positionLine)-[:"+HAS_SENIORITY_LEVEL+"]->(seniorityLevel:SeniorityLevel)-[:"+HAS_BASE_PAY_GRADE+"]-(payGrade:PayGrade) \n" +
            "MATCH(unitPosition)-[:"+HAS_EXPERTISE_IN+"]->(expertise:Expertise{published:true}) \n" +
            "OPTIONAL MATCH(unitPosition)-[:"+IN_UNIT+"]-(org:Organization)-[:"+CONTACT_ADDRESS+"]->(contactAddress:ContactAddress)-[:"+MUNICIPALITY+"]->(municipality:Municipality)-[:"+HAS_MUNICIPALITY+"]-(pga:PayGroupArea)<-[pgaRel:"+HAS_PAY_GROUP_AREA+"]-(payGrade) \n" +
            "WITH  unitPosition,positionLine,payGrade,expertise,seniorityLevel, CASE when pgaRel.payGroupAreaAmount IS NULL THEN toInteger('0') ELSE toInteger(pgaRel.payGroupAreaAmount) END as hourlyCost \n" +
            "OPTIONAL MATCH (positionLine)-[:"+APPLICABLE_FUNCTION+"]-(function:Function) \n" +
            "WITH  unitPosition,positionLine,expertise,seniorityLevel,hourlyCost,function\n" +
            "OPTIONAL MATCH(functionalPayment:FunctionalPayment)-[:"+APPLICABLE_FOR_EXPERTISE+"]->(expertise) where date(datetime({epochmillis:functionalPayment.startDate})) <= date(positionLine.startDate) AND (functionalPayment.endDate IS NULL OR date(positionLine.startDate)<= date(datetime({epochmillis:functionalPayment.startDate})))\n" +
            "WITH  unitPosition,positionLine,expertise,functionalPayment,seniorityLevel,function,hourlyCost\n" +
            "OPTIONAL MATCH(functionalPayment)-[:"+FUNCTIONAL_PAYMENT_MATRIX+"]->(fpm:FunctionalPaymentMatrix) \n" +
            "WITH  unitPosition,positionLine,expertise,fpm,seniorityLevel,function,functionalPayment,hourlyCost\n" +
            "OPTIONAL MATCH(fpm)-[:"+SENIORITY_LEVEL_FUNCTIONS+"]->(slf:SeniorityLevelFunction)-[:"+FOR_SENIORITY_LEVEL+"]->(seniorityLevel) \n" +
            "WITH  unitPosition,positionLine,expertise,fpm,slf,function,functionalPayment,hourlyCost\n" +
            "OPTIONAL MATCH(slf)-[rel:"+HAS_FUNCTIONAL_AMOUNT+"]-(function) \n" +
            "WITH functionalPayment,positionLine,hourlyCost, sum(toInteger(rel.amount)) as totalCostOfFunctions WITH positionLine, hourlyCost+totalCostOfFunctions as hourlyCost,functionalPayment\n" +
            "RETURN id(positionLine) as id,  CASE WHEN functionalPayment.paymentUnit='MONTHLY' THEN hourlyCost*12   ELSE hourlyCost END as hourlyCost ")
    List<UnitPositionLinesQueryResult> findFunctionalHourlyCost(List<Long> unitPositionIds);

//====================================================
@Query("OPTIONAL MATCH (organization:Organization)  where id(organization)={0}\n" +
        "OPTIONAL MATCH (staff:Staff)  where id(staff)={1}\n" +
        "OPTIONAL MATCH(organization)-[:"+HAS_EMPLOYMENTS+"]-(employment:Employment)-[:"+BELONGS_TO+"]-(staff)\n" +
        "OPTIONAL MATCH(staff)-[:"+BELONGS_TO_STAFF+"]->(unitPosition:UnitPosition{published:true}) \n" +
        "OPTIONAL MATCH(unitPosition)-[:"+HAS_POSITION_CODE+"]->(positionCode:PositionCode) " +
        "RETURN \n" +
        "CASE \n" +
        "WHEN organization IS NULL THEN \"org\" \n" +
        "WHEN staff IS NULL THEN \"staff\"\n" +
        "WHEN employment IS NULL THEN  \"emp\" \n" +
        "WHEN unitPosition IS NULL THEN \"unitPosition\" \n" +
        "ELSE collect({id:id(unitPosition),positionCodeName:positionCode.name}) END ")
        Object getUnitPositionsByUnitIdAndStaffId(Long unitId,Long staffId);

//==========================================================
    @Query("OPTIONAL MATCH(organization:Organization) WHERE id(unitId)={0} \n" +
            "OPTIONAL MATCH(staff:Staff) WHERE id(staff)={1} \n" +
            "OPTIONAL MATCH(unitPosition:UnitPosition) WHERE id(unitPosition)={2} \n" +
            "OPTIONAL MATCH(unitPosition:UnitPosition{published:true})-[unitPositionOrgRel:"+IN_UNIT+"]-(organization) \n" +
            "OPTIONAL MATCH(staff)-[unitPositionStaffRel:"+BELONGS_TO_STAFF+"]->(unitPosition) \n" +
            "WITH organization,staff,unitPosition,unitPositionOrgRel,unitPositionStaffRel \n" +
            "MATCH(positionLine:UnitPositionLine)-[:"+HAS_POSITION_LINES+"]-(unitPosition) \n" +
            "MATCH(positionLine)-[:"+HAS_SENIORITY_LEVEL+"]->(seniorityLevel:SeniorityLevel)-[:"+HAS_BASE_PAY_GRADE+"]-(payGrade:PayGrade) \n" +
            "MATCH(unitPosition)-[:"+HAS_EXPERTISE_IN+"]->(expertise:Expertise{published:true}) \n" +
            "OPTIONAL MATCH(organization)-[:"+CONTACT_ADDRESS+"]->(contactAddress:ContactAddress)-[:"+MUNICIPALITY+"]->(municipality:Municipality)-[:"+HAS_MUNICIPALITY+"]-(pga:PayGroupArea)<-[pgaRel:"+HAS_PAY_GROUP_AREA+"]-(payGrade) \n" +
            "WITH  organization,staff,unitPosition,unitPositionOrgRel,unitPositionStaffRel,positionLine,payGrade,expertise,seniorityLevel, " +
            "CASE when pgaRel.payGroupAreaAmount IS NULL THEN toInteger('0') ELSE toInteger(pgaRel.payGroupAreaAmount) END as basePayAmount \n" +
            "OPTIONAL MATCH(functionalPayment:FunctionalPayment)-[:"+APPLICABLE_FOR_EXPERTISE+"]->(expertise) where date(datetime({epochmillis:functionalPayment.startDate})) <= date(positionLine.startDate) AND (functionalPayment.endDate IS NULL OR date(positionLine.startDate)<= date(datetime({epochmillis:functionalPayment.startDate}))) \n" +
            "WITH  organization,staff,unitPosition,unitPositionOrgRel,unitPositionStaffRel,expertise,functionalPayment,seniorityLevel,function,basePayAmount \n" +
            "OPTIONAL MATCH(functionalPayment)-[:"+FUNCTIONAL_PAYMENT_MATRIX+"]->(fpm:FunctionalPaymentMatrix) \n" +
            "WITH  organization,staff,unitPosition,unitPositionOrgRel,unitPositionStaffRel,expertise,fpm,seniorityLevel,function,functionalPayment,basePayAmount \n" +
            "OPTIONAL MATCH(fpm)-[:"+SENIORITY_LEVEL_FUNCTIONS+"]->(slf:SeniorityLevelFunction)-[:"+FOR_SENIORITY_LEVEL+"]->(seniorityLevel) \n" +
            "WITH  organization,staff,unitPosition,unitPositionOrgRel,unitPositionStaffRel,expertise,fpm,slf,function,functionalPayment,basePayAmount \n" +
            "OPTIONAL MATCH(slf)-[rel:"+HAS_FUNCTIONAL_AMOUNT+"]-(function)\n" +
            "RETURN " +
            "CASE " +
            "WHEN organization IS NULL THEN \"org\" \n" +
            "WHEN staff IS NULL THEN \"staff\" \n " +
            "WHEN unitPosition IS NULL THEN \"unitPosition\" \n" +
            "WHEN unitPositionOrgRel IS NULL THEN  \"unitPositionOrgRel\" \n" +
            "WHEN unitPositionStaffRel IS NULL THEN  \"unitPositionStaffRel\" \n" +
            "ELSE END ")
         Object getFunctionalHourlyCostByUnitPositionId(Long unitId,Long staffId,Long unitPositionId);

}

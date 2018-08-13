package com.kairos.persistence.repository.user.unit_position;


import com.kairos.persistence.model.staff.employment.EmploymentUnitPositionQueryResult;
import com.kairos.persistence.model.user.unit_position.StaffUnitPositionDetails;
import com.kairos.persistence.model.user.unit_position.UnitPosition;
import com.kairos.persistence.model.user.unit_position.UnitPositionEmploymentTypeRelationShip;
import com.kairos.persistence.model.user.unit_position.UnitPositionQueryResult;
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
            "return e as expertise,unitPosition.workingTimeAgreementId as workingTimeAgreementId," +
            "unitPosition.totalWeeklyHours as totalWeeklyHours," +
            "unitPosition.startDateMillis as startDateMillis," +
            "unitPosition.endDateMillis as endDateMillis," +
            "unitPosition.salary as salary," +
            "unitPosition.workingDaysInWeek as workingDaysInWeek," +
            "et as employmentType," +
            "unitPosition.hourlyWages as hourlyWages," +
            "id(unitPosition)   as id,unitPosition.history as history,unitPosition.editable as editable,unitPosition.published as published," +
            "unitPosition.avgDailyWorkingHours as avgDailyWorkingHours,unitPosition.costTimeAgreementId as costTimeAgreementId," +
            "unitPosition.lastWorkingDateMillis as lastWorkingDateMillis," +
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
            "return unitPosition ORDER BY unitPosition.startDateMillis")
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
            "match(org)-[:HAS_SUB_ORGANIZATION*]->(subOrg:Organization) with org,subOrg,staff,employment \n" +
            "optional match(subOrg)<-[:IN_UNIT]-(unitPosition:UnitPosition{deleted:false})<-[:BELONGS_TO_STAFF]-(staff) with unitPosition,org,subOrg,staff,employment \n" +
            "match(unitPosition)-[:HAS_EXPERTISE_IN]->(expertise:Expertise) \n" +
            "match(unitPosition)-[employmentRel:HAS_EMPLOYMENT_TYPE]->(employmentType:EmploymentType) \n" +
            "match(unitPosition)-[:HAS_POSITION_CODE]->(positionCode:PositionCode{deleted:false}) \n" +
            "match(unitPosition)-[:HAS_SENIORITY_LEVEL]->(seniorityLevel:SeniorityLevel)-[:HAS_BASE_PAY_GRADE]->(payGrade:PayGrade)OPTIONAL MATCH (unitPosition)-[:HAS_REASON_CODE]->(reasonCode:ReasonCode) \n" +
            "optional match (expertise)-[:SUPPORTED_BY_UNION]->(unionData:Organization{isEnable:true,union:true}) \n" +
            "optional match(unitPosition)-[rel:HAS_FUNCTION]->(functions:Function)with expertise ,org,subOrg,reasonCode,unitPosition,positionCode ,unionData ,seniorityLevel,employmentRel,employmentType,payGrade,CASE when functions IS NULL THEN [] ELSE collect({name:functions.name,id:id(functions),amount:rel.amount }) END as functionData return expertise as expertise,unionData as union, positionCode as positionCode, \n" +
            " {id:id(seniorityLevel),from:seniorityLevel.from,pensionPercentage:seniorityLevel.pensionPercentage,freeChoicePercentage:seniorityLevel.freeChoicePercentage, freeChoiceToPension:seniorityLevel.freeChoiceToPension, to:seniorityLevel.to,moreThan:seniorityLevel.moreThan,functions:functionData,payGrade:{id:id(payGrade),payGradeLevel:payGrade.payGradeLevel}} as seniorityLevel, \n" +
            "unitPosition.totalWeeklyMinutes as totalWeeklyMinutes, unitPosition.startDateMillis as startDateMillis, unitPosition.endDateMillis as endDateMillis, \n" +
            "unitPosition.salary as salary,id(reasonCode) as reasonCodeId,unitPosition.workingDaysInWeek as workingDaysInWeek,unitPosition.history as history,unitPosition.editable as editable,unitPosition.published as published, \n" +
            "{employmentTypeCategory:employmentRel.employmentTypeCategory,name:employmentType.name,id:id(employmentType)} as employmentType, \n" +
            "unitPosition.hourlyWages as hourlyWages, id(unitPosition) as id,unitPosition.workingTimeAgreementId as workingTimeAgreementId,unitPosition.avgDailyWorkingHours as avgDailyWorkingHours,unitPosition.costTimeAgreementId as costTimeAgreementId, \n" +
            "unitPosition.lastWorkingDateMillis as lastWorkingDateMillis,unitPosition.fullTimeWeeklyMinutes as fullTimeWeeklyMinutes,id(org) as parentUnitId, id(subOrg) as unitId, {id:id(subOrg),name:subOrg.name} as unitInfo " +
            "UNION MATCH (user:User)-[:BELONGS_TO]-(staff:Staff) where id(user)={0}\n" +
            "match(staff)<-[:BELONGS_TO]-(employment:Employment)<-[:HAS_EMPLOYMENTS]-(org:Organization) \n" +
            "match(org)<-[:IN_UNIT]-(unitPosition:UnitPosition{deleted:false})<-[:BELONGS_TO_STAFF]-(staff)  \n" +
            "match(unitPosition)-[:HAS_EXPERTISE_IN]->(expertise:Expertise) \n" +
            "match(unitPosition)-[employmentRel:HAS_EMPLOYMENT_TYPE]->(employmentType:EmploymentType) \n" +
            "match(unitPosition)-[:HAS_POSITION_CODE]->(positionCode:PositionCode{deleted:false}) \n" +
            "match(unitPosition)-[:HAS_SENIORITY_LEVEL]->(seniorityLevel:SeniorityLevel)-[:HAS_BASE_PAY_GRADE]->(payGrade:PayGrade)OPTIONAL MATCH (unitPosition)-[:HAS_REASON_CODE]->(reasonCode:ReasonCode) \n" +
            "optional match (expertise)-[:SUPPORTED_BY_UNION]->(unionData:Organization{isEnable:true,union:true}) \n" +
            "optional match(unitPosition)-[rel:HAS_FUNCTION]->(functions:Function)with expertise ,org,reasonCode,unitPosition,positionCode ,unionData ,seniorityLevel,employmentRel,employmentType,payGrade,CASE when functions IS NULL THEN [] ELSE collect({name:functions.name,id:id(functions),amount:rel.amount,icon:functions.icon }) END as functionData " +
            "return expertise as expertise,unionData as union, positionCode as positionCode,\n" +
            " {id:id(seniorityLevel),from:seniorityLevel.from,pensionPercentage:seniorityLevel.pensionPercentage,freeChoicePercentage:seniorityLevel.freeChoicePercentage, freeChoiceToPension:seniorityLevel.freeChoiceToPension, to:seniorityLevel.to,moreThan:seniorityLevel.moreThan,functions:functionData,payGrade:{id:id(payGrade),payGradeLevel:payGrade.payGradeLevel}} as seniorityLevel, \n" +
            "unitPosition.totalWeeklyMinutes as totalWeeklyMinutes, unitPosition.startDateMillis as startDateMillis, unitPosition.endDateMillis as endDateMillis, unitPosition.salary as salary, id(reasonCode) as reasonCodeId,\n" +
            "unitPosition.workingDaysInWeek as workingDaysInWeek,unitPosition.history as history,unitPosition.editable as editable,unitPosition.published as published, \n" +
            "{employmentTypeCategory:employmentRel.employmentTypeCategory,name:employmentType.name,id:id(employmentType)} as employmentType, \n" +
            "unitPosition.hourlyWages as hourlyWages, id(unitPosition) as id,unitPosition.workingTimeAgreementId as workingTimeAgreementId,\n" +
            "unitPosition.avgDailyWorkingHours as avgDailyWorkingHours,unitPosition.costTimeAgreementId as costTimeAgreementId,\n" +
            "unitPosition.lastWorkingDateMillis as lastWorkingDateMillis,unitPosition.fullTimeWeeklyMinutes as fullTimeWeeklyMinutes, \n" +
            "id(org) as parentUnitId,id(org) as unitId,\n" +
            "{id:id(org),name:org.name} as unitInfo")
    List<UnitPositionQueryResult> getAllUnitPositionsByUser(long userId);

    @Query("Match(staff:Staff)-[:" + BELONGS_TO_STAFF + "]->(up:UnitPosition{deleted:false}) where id(staff)={0} return up.endDateMillis as endDateMillis")
    List<Long> getAllUnitPositionsByStaffId(Long staffId);

    @Query("MATCH(unitPosition:UnitPosition)-[:" + IN_UNIT + "]->(subOrg:Organization) where id(unitPosition)={0} " +
            "MATCH(unitPosition)<-[:" + BELONGS_TO_STAFF + "]-(staff:Staff) " +
            "MATCH(staff)<-[:" + BELONGS_TO + "]-(employment:Employment) " +
            "MATCH(employment)<-[:" + HAS_EMPLOYMENTS + "]-(org:Organization) " +
            "RETURN id(subOrg) as unitId,id(org) as parentUnitId")
    UnitPositionQueryResult getUnitIdAndParentUnitIdByUnitPositionId(Long unitPositionId);

    @Query("match(unitPosition)-[employmentRel:" + HAS_EMPLOYMENT_TYPE + "]->(employmentType:EmploymentType)  where id(unitPosition)={0} return unitPosition,employmentRel,employmentType")
    UnitPositionEmploymentTypeRelationShip findEmploymentTypeByUnitPositionId(Long unitPositionId);


    @Query("match(unitPosition:UnitPosition)-[employmentRel:" + HAS_EMPLOYMENT_TYPE + "]-(employmentType:EmploymentType) " +
            "MATCH(unitPosition)-[:" + IN_UNIT + "]-(o:Organization) " +
            "MATCH (unitPosition)-[:" + HAS_EXPERTISE_IN + "]->(e:Expertise)" +
            "where id(o)={0}" +
            "return unitPosition,employmentRel,employmentType")
    List<UnitPositionEmploymentTypeRelationShip> findUnitPositionEmploymentTypeRelationshipByParentOrganizationId(Long parentOrganizationId);

    @Query("match(unitPosition)-[rel:" + HAS_FUNCTION + "]->(functions:Function) where id(unitPosition)={0}  detach delete rel")
    void removeOlderFunctionsFromUnitPosition(Long unitPositionId);

    @Query("Match(staff:Staff)-[:" + BELONGS_TO_STAFF + "]->(up:UnitPosition{deleted:false}) where id(staff)={0} and ( up.endDateMillis > {1} or up.endDateMillis is null)  set up.endDateMillis = {1}")
    void updateUnitPositionEndDateFromEmployment(Long staffId, Long endDateMillis);

    @Query("Match(staff:Staff)-[:" + BELONGS_TO_STAFF + "]->(up:UnitPosition{deleted:false}) where id(staff)={0} and ( up.endDateMillis > {1} or up.endDateMillis is null)  return up")
    List<UnitPosition> getUnitPositionsFromEmploymentEndDate(Long staffId, Long endDateMillis);

    @Query("Match(staff:Staff)-[:" + BELONGS_TO_STAFF + "]->(up:UnitPosition{deleted:false}) where id(staff)={0} Match(staff)<-[:" + BELONGS_TO + "]-(emp:Employment) return min(up.startDateMillis) as earliestUnitPositionStartDateMillis, emp.endDateMillis as employmentEndDateMillis")
    EmploymentUnitPositionQueryResult getEarliestUnitPositionStartDateAndEmploymentByStaffId(Long staffId);

    @Query("Match(staff:Staff)-[:" + BELONGS_TO_STAFF + "]->(up:UnitPosition) where id(up)={0} return id(staff) as staffId")
    Long getStaffIdFromUnitPosition(Long unitPositionId);

    @Query("Match(staff:Staff)-[:" + BELONGS_TO_STAFF + "]->(up:UnitPosition{deleted:false}) where id(staff)={0} return max(up.startDateMillis) as maxStartDateMillis")
    Long getMaxUnitPositionStartDate(Long staffId);

    @Query("match(staff)<-[:" + BELONGS_TO + "]-(employment:Employment)<-[:" + HAS_EMPLOYMENTS + "]-(org:Organization) where id(staff)={0} " +
            "match(org)-[:" + HAS_SUB_ORGANIZATION + "*]->(subOrg:Organization) \n" +
            "optional match(subOrg)<-[:" + IN_UNIT + "]-(unitPosition:UnitPosition{deleted:false})<-[:" + BELONGS_TO_STAFF + "]-(staff) with unitPosition,org,subOrg,staff \n" +
            "match(unitPosition)-[:" + HAS_EXPERTISE_IN + "]->(expertise:Expertise) \n" +
            "match(unitPosition)-[employmentRel:" + HAS_EMPLOYMENT_TYPE + "]->(employmentType:EmploymentType) \n" +
            "match(unitPosition)-[:" + HAS_POSITION_CODE + "]->(positionCode:PositionCode{deleted:false}) \n" +
            "match(unitPosition)-[:" + HAS_SENIORITY_LEVEL + "]->(seniorityLevel:SeniorityLevel)-[:" + HAS_BASE_PAY_GRADE + "]->(payGrade:PayGrade) \n" +
            "OPTIONAL MATCH (unitPosition)-[:" + HAS_REASON_CODE + "]->(reasonCode:ReasonCode) \n" +
            "optional match (expertise)-[:" + SUPPORTED_BY_UNION + "]->(unionData:Organization{isEnable:true,union:true}) \n" +
            "optional match(unitPosition)-[rel:" + HAS_FUNCTION + "]->(functions:Function) \n" +
            "with expertise ,org,subOrg,reasonCode,unitPosition,positionCode ,unionData ,seniorityLevel,employmentRel,employmentType,payGrade,CASE when functions IS NULL THEN [] ELSE collect({name:functions.name,id:id(functions),amount:rel.amount }) END as functionData return expertise as expertise,unionData as union, positionCode as positionCode, \n" +
            "{id:id(seniorityLevel),from:seniorityLevel.from,pensionPercentage:seniorityLevel.pensionPercentage,freeChoicePercentage:seniorityLevel.freeChoicePercentage, freeChoiceToPension:seniorityLevel.freeChoiceToPension, to:seniorityLevel.to,moreThan:seniorityLevel.moreThan,functions:functionData,payGrade:{id:id(payGrade),payGradeLevel:payGrade.payGradeLevel}} as seniorityLevel, \n" +
            "unitPosition.totalWeeklyMinutes as totalWeeklyMinutes, unitPosition.startDateMillis as startDateMillis, unitPosition.endDateMillis as endDateMillis, \n" +
            "unitPosition.salary as salary,id(reasonCode) as reasonCodeId,unitPosition.workingDaysInWeek as workingDaysInWeek, unitPosition.history as history,unitPosition.editable as editable,unitPosition.published as published,\n" +
            "{employmentTypeCategory:employmentRel.employmentTypeCategory,name:employmentType.name,id:id(employmentType)} as employmentType, \n" +
            "unitPosition.hourlyWages as hourlyWages, id(unitPosition) as id,unitPosition.workingTimeAgreementId as workingTimeAgreementId,unitPosition.avgDailyWorkingHours as avgDailyWorkingHours,unitPosition.costTimeAgreementId as costTimeAgreementId, \n" +
            "unitPosition.lastWorkingDateMillis as lastWorkingDateMillis,unitPosition.fullTimeWeeklyMinutes as fullTimeWeeklyMinutes,id(org) as parentUnitId, id(subOrg)  as unitId,{id:id(subOrg),name:subOrg.name} as unitInfo " +
            "UNION " +
            "match(staff)<-[:" + BELONGS_TO + "]-(employment:Employment)<-[:" + HAS_EMPLOYMENTS + "]-(org:Organization) where id(staff)={0} " +
            "match(subOrg)<-[:" + IN_UNIT + "]-(unitPosition:UnitPosition{deleted:false})<-[:" + BELONGS_TO_STAFF + "]-(staff) " +
            "match(unitPosition)-[:" + HAS_EXPERTISE_IN + "]->(expertise:Expertise) \n" +
            "match(unitPosition)-[employmentRel:" + HAS_EMPLOYMENT_TYPE + "]->(employmentType:EmploymentType) \n" +
            "match(unitPosition)-[:" + HAS_POSITION_CODE + "]->(positionCode:PositionCode{deleted:false}) \n" +
            "match(unitPosition)-[:" + HAS_SENIORITY_LEVEL + "]->(seniorityLevel:SeniorityLevel)-[:" + HAS_BASE_PAY_GRADE + "]->(payGrade:PayGrade) \n" +
            "OPTIONAL MATCH (unitPosition)-[:" + HAS_REASON_CODE + "]->(reasonCode:ReasonCode) \n" +
            "optional match (expertise)-[:" + SUPPORTED_BY_UNION + "]->(unionData:Organization{isEnable:true,union:true}) \n" +
            "optional match(unitPosition)-[rel:" + HAS_FUNCTION + "]->(functions:Function) \n" +
            "with expertise ,org,subOrg,reasonCode,unitPosition,positionCode ,unionData ,seniorityLevel,employmentRel,employmentType,payGrade,CASE when functions IS NULL THEN [] ELSE collect({name:functions.name,id:id(functions),amount:rel.amount,icon:functions.icon }) END as functionData return expertise as expertise,unionData as union, positionCode as positionCode, \n" +
            "{id:id(seniorityLevel),from:seniorityLevel.from,pensionPercentage:seniorityLevel.pensionPercentage,freeChoicePercentage:seniorityLevel.freeChoicePercentage, freeChoiceToPension:seniorityLevel.freeChoiceToPension, to:seniorityLevel.to,moreThan:seniorityLevel.moreThan,functions:functionData,payGrade:{id:id(payGrade),payGradeLevel:payGrade.payGradeLevel}} as seniorityLevel, \n" +
            "unitPosition.totalWeeklyMinutes as totalWeeklyMinutes, unitPosition.startDateMillis as startDateMillis, unitPosition.endDateMillis as endDateMillis, \n" +
            "unitPosition.salary as salary,id(reasonCode) as reasonCodeId,unitPosition.workingDaysInWeek as workingDaysInWeek, unitPosition.history as history,unitPosition.editable as editable,unitPosition.published as published,\n" +
            "{employmentTypeCategory:employmentRel.employmentTypeCategory,name:employmentType.name,id:id(employmentType)} as employmentType, \n" +
            "unitPosition.hourlyWages as hourlyWages, id(unitPosition) as id,unitPosition.workingTimeAgreementId as workingTimeAgreementId,unitPosition.avgDailyWorkingHours as avgDailyWorkingHours,unitPosition.costTimeAgreementId as costTimeAgreementId, \n" +
            "unitPosition.lastWorkingDateMillis as lastWorkingDateMillis,unitPosition.fullTimeWeeklyMinutes as fullTimeWeeklyMinutes,id(org) as parentUnitId, id(org)  as unitId ,\n" +
            "{id:id(org),name:org.name} as unitInfo")
    List<UnitPositionQueryResult> getAllUnitPositionsForCurrentOrganization(long staffId);

    @Query("MATCH(unitPosition:UnitPosition)-[:" + HAS_EXPERTISE_IN + "]->(expertise:Expertise) \n" +
            "MATCH(unitPosition)<-[:" + BELONGS_TO_STAFF + "]-(staff:Staff) " +
            "MATCH(unitPosition)-[:" + IN_UNIT + "]-(organization:Organization) where id(organization)={0} AND id(staff)={1} and id(expertise)={2} \n" +
            "AND unitPosition.startDateMillis<={3} AND  (unitPosition.endDateMillis IS NULL or unitPosition.endDateMillis>={3})  \n" +
            "return id(unitPosition)")
    Long getUnitPositionIdByStaffAndExpertise(Long unitId, Long staffId, Long expertiseId, Long currentMillis);

    @Query("MATCH (unit:Organization) WHERE id(unit)={0} \n" +
            "MATCH (unit)<-[:" + IN_UNIT + "]-(unitPosition:UnitPosition)-[:" + HAS_EXPERTISE_IN + "]-(expertise:Expertise) \n" +
            "RETURN id(unitPosition) as unitPositionId, id(expertise) as expertiseId")
    List<Map<Long, Long>> getMapOfUnitPositionAndExpertiseId(Long unitId);


    @Query("MATCH (user:User)-[:BELONGS_TO]-(staff:Staff)<-[:" + BELONGS_TO + "]-(employment:Employment)<-[:HAS_EMPLOYMENTS]-(org:Organization) where id(user)={0}\n" +
            "match(org)-[:HAS_SUB_ORGANIZATION*]->(subOrg:Organization)\n" +
            "match(subOrg)<-[:IN_UNIT]-(unitPosition:UnitPosition{deleted:false,published:true})<-[:BELONGS_TO_STAFF]-(staff)\n" +
            "match(unitPosition)-[:HAS_POSITION_CODE]->(positionCode:PositionCode{deleted:false}) \n" +
            "return  id(unitPosition) as id,positionCode as positionCode,unitPosition.history as history, unitPosition.workingTimeAgreementId as workingTimeAgreementId,\n" +
            "id(org) as parentUnitId, id(subOrg) as unitId, {id:id(subOrg),name:subOrg.name} as unitInfo ORDER BY unitPosition.creationDate" +
            " UNION " +
            "MATCH (user:User)-[:BELONGS_TO]-(staff:Staff) where id(user)={0}\n" +
            "match(staff)<-[:BELONGS_TO]-(employment:Employment)<-[:HAS_EMPLOYMENTS]-(org:Organization) \n" +
            "match(org)<-[:IN_UNIT]-(unitPosition:UnitPosition{deleted:false,published:true})<-[:BELONGS_TO_STAFF]-(staff)  \n" +
            "match(unitPosition)-[:HAS_POSITION_CODE]->(positionCode:PositionCode{deleted:false}) \n" +
            "return id(unitPosition) as id, positionCode as positionCode,unitPosition.history as history,unitPosition.workingTimeAgreementId as workingTimeAgreementId,\n" +
            "id(org) as parentUnitId,id(org) as unitId,{id:id(org),name:org.name} as unitInfo ORDER BY unitPosition.creationDate ")
    List<UnitPositionQueryResult> getAllUnitPositionsBasicDetailsAndWTAByUser(long userId);


}

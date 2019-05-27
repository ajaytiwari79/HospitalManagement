package com.kairos.persistence.repository.user.employment;


import com.kairos.enums.EmploymentSubType;
import com.kairos.persistence.model.country.functions.FunctionWithAmountQueryResult;
import com.kairos.persistence.model.staff.personal_details.StaffPersonalDetail;
import com.kairos.persistence.model.user.employment.Employment;
import com.kairos.persistence.model.user.employment.EmploymentLineEmploymentTypeRelationShip;
import com.kairos.persistence.model.user.employment.query_result.*;
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
public interface EmploymentGraphRepository extends Neo4jBaseRepository<Employment, Long> {


    @Query("MATCH (employment:Employment{deleted:false}) where id(employment)={0} \n" +
            "MATCH (employment)-[:"+HAS_EXPERTISE_IN+"]->(expertise:Expertise)\n" +
            "MATCH(employment)-[:"+HAS_EMPLOYMENT_LINES+"]-(employmentLine:EmploymentLine) \n" +
            "MATCH(employmentLine)-[employmentRel:"+HAS_EMPLOYMENT_TYPE+"]->(employmentType:EmploymentType) \n" +
            "WITH expertise,employment,employmentLine, employmentRel,employmentType\n" +
            "OPTIONAL MATCH (employment)-[rel:"+APPLIED_FUNCTION+"]->(appliedFunction:Function)  \n" +
            "WITH expertise,employment,employmentLine, employmentType,employmentRel,CASE WHEN appliedFunction IS NULL THEN [] ELSE Collect({id:id(appliedFunction),name:appliedFunction.name,icon:appliedFunction.icon,appliedDates:rel.appliedDates}) end as appliedFunctions  \n" +
            "with expertise,employment,appliedFunctions,\n" +
            "CASE employmentLine when null then [] else COLLECT({totalWeeklyMinutes:(employmentLine.totalWeeklyMinutes % 60),startDate:employmentLine.startDate,endDate:employmentLine.endDate,totalWeeklyHours:(employmentLine.totalWeeklyMinutes / 60), hourlyCost:employmentLine.hourlyCost,id:id(employmentLine), workingDaysInWeek:employmentLine.workingDaysInWeek ,\n" +
            " avgDailyWorkingHours:employmentLine.avgDailyWorkingHours,employmentType:{employmentTypeCategory:employmentRel.employmentTypeCategory,name:employmentType.name,id:id(employmentType)},fullTimeWeeklyMinutes:employmentLine.fullTimeWeeklyMinutes,totalWeeklyMinutes:employmentLine.totalWeeklyMinutes}) end as employmentLines\n" +
            "RETURN  DISTINCT expertise as expertise,employment.startDate as startDate,employment.accumulatedTimebankDate as accumulatedTimebankDate,employment.accumulatedTimebankMinutes as accumulatedTimebankMinutes,employment.endDate as endDate, id(employment) as id,employment.lastWorkingDate as lastWorkingDate,employment.published as published, appliedFunctions as appliedFunctions,collect(employmentLines[0]) as employmentLines")
    EmploymentQueryResult getEmploymentById(Long employmentId);

    @Query("MATCH(staff:Staff{deleted:false})-[:"+BELONGS_TO+"]-(user:User) WHERE id(staff) IN {2}\n" +
            "MATCH (expertise:Expertise) where id(expertise)={1}\n" +
            "MATCH(staff)-[:" + BELONGS_TO_STAFF + "]->(employment:Employment)-[:" + IN_UNIT + "]->(unit:Unit) where id(unit)={0}\n " +
            "MATCH(employment)-[:"+ HAS_EMPLOYMENT_LINES +"]-(employmentLine:EmploymentLine) WHERE  NOT EXISTS(employmentLine.endDate) OR date(employmentLine.endDate) >= date()" +
            "MATCH (expertise)<-[:" + HAS_EXPERTISE_IN + "]-(employment) \n" +
            "MATCH (employmentLine)-[relation:" + HAS_EMPLOYMENT_TYPE + "]->(et:EmploymentType)\n" +
            "WITH expertise,staff,unit,user,employment,employmentLine,{employmentTypeCategory:relation.employmentTypeCategory,name:et.name,id:id(et)} as employmentType \n" +
            "RETURN id(staff) as staffId,id(user) as userId,staff as staff,expertise as expertise,unit.unitTimeZone as unitTimeZone," +
            "CASE employmentLine when null then [] else COLLECT({totalWeeklyMinutes:(employmentLine.totalWeeklyMinutes % 60),totalWeeklyHours:(employmentLine.totalWeeklyMinutes / 60),id:id(employmentLine), workingDaysInWeek:employmentLine.workingDaysInWeek ,\n" +
            " avgDailyWorkingHours:employmentLine.avgDailyWorkingHours,fullTimeWeeklyMinutes:employmentLine.fullTimeWeeklyMinutes,employmentType:employmentType}) end as employmentLines , " +
            "id(employment) as id,employment.startDate as startDate")
    List<StaffEmploymentDetails> getStaffInfoByUnitIdAndStaffId(Long unitId, Long expertiseId, List<Long> staffId);

    @Query("MATCH (staff:Staff) where id(staff) IN {0} " +
            "MATCH(staff)-[rel:"+STAFF_HAS_EXPERTISE+"]->(expertise:Expertise) " +
            "return id(staff) as id,collect(id(expertise)) as expertiseIds")
    List<StaffPersonalDetail> getStaffDetailByIds(Set<Long> staffId, LocalDate currentDate);


    @Query("MATCH (employment:Employment{deleted:false}) where id(employment) IN {0} \n" +
            "MATCH(employment)-[:"+BELONGS_TO_STAFF+"]-(staff:Staff) \n" +
            "MATCH(employment)-[:"+HAS_EMPLOYMENT_LINES+"]-(employmentLine:EmploymentLine)  \n" +
            "MATCH (employment)-[:" + HAS_EXPERTISE_IN + "]->(expertise:Expertise)\n" +
            "MATCH(employmentLine)-[employmentRel:" + HAS_EMPLOYMENT_TYPE + "]->(employmentType:EmploymentType) \n" +
            "WITH staff,expertise,employment,employmentLine,{employmentTypeCategory:employmentRel.employmentTypeCategory,name:employmentType.name,id:id(employmentType)} as employmentType \n" +
            "OPTIONAL MATCH (employment)-[rel:" + APPLIED_FUNCTION + "]->(appliedFunction:Function)  \n" +
            "WITH employment, Collect({id:id(appliedFunction),name:appliedFunction.name,icon:appliedFunction.icon,appliedDates:rel.appliedDates}) as appliedFunctions ,staff,expertise,employmentType,employmentLine\n" +
            "WITH staff,expertise,employment,employmentType,appliedFunctions,\n" +
            "COLLECT({totalWeeklyMinutes:(employmentLine.totalWeeklyMinutes % 60),totalWeeklyHours:(employmentLine.totalWeeklyMinutes / 60),startDate:employmentLine.startDate, hourlyCost:employmentLine.hourlyCost,id:id(employmentLine), workingDaysInWeek:employmentLine.workingDaysInWeek ,\n" +
            "fullTimeWeeklyMinutes:employmentLine.fullTimeWeeklyMinutes,totalWeeklyMinutes:employmentLine.totalWeeklyMinutes , \n" +
            "avgDailyWorkingHours:employmentLine.avgDailyWorkingHours,employmentType:employmentType}) as employmentLines  \n" +
            "return expertise as expertise,id(staff) as staffId,employment.startDate as startDate,employment.accumulatedTimebankDate as accumulatedTimebankDate,employment.accumulatedTimebankMinutes as accumulatedTimebankMinutes,employment.published as published, employment.endDate as endDate, id(employment) as id,employment.lastWorkingDate as lastWorkingDate,\n" +
           "employmentLines,appliedFunctions ")
    List<EmploymentQueryResult> getEmploymentByIds(List<Long> employmentIds);



    @Query("MATCH(s:Staff)-[:" + BELONGS_TO_STAFF + "]-(employment:Employment{deleted:false,published:true})-[:" + IN_UNIT + "]-(o:Unit) where id(o)={0} AND id(s)={1} \n" +
            "MATCH(employment)-[:HAS_EXPERTISE_IN]-(e:Expertise) where id(e)={2}\n" +
            "return employment ORDER BY employment.startDate")
    List<Employment> getStaffEmploymentsByExpertise(Long unitId, Long staffId, Long expertiseId);


    @Query("MATCH (user:User)-[:"+BELONGS_TO+"]-(staff:Staff) where id(user)={0}\n" +
            "MATCH(staff)<-[:"+BELONGS_TO+"]-(position:Position)<-[:"+ HAS_POSITIONS +"]-(org:Unit) \n" +
            "MATCH(org)-[:"+HAS_SUB_ORGANIZATION+"*]->(subOrg:Unit)  \n" +
            "OPTIONAL MATCH(subOrg)<-[:"+IN_UNIT+"]-(employment:Employment{deleted:false})<-[:"+BELONGS_TO_STAFF+"]-(staff) WITH employment,org,subOrg,staff,position \n" +
            "MATCH(employment)-[:"+HAS_EXPERTISE_IN+"]->(expertise:Expertise) \n" +
            "OPTIONAL MATCH (employment)-[:"+HAS_REASON_CODE+"]->(reasonCode:ReasonCode) \n" +
            "OPTIONAL MATCH (expertise)-[:"+SUPPORTED_BY_UNION+"]->(unionData:Unit{isEnable:true,union:true}) \n" +
            "RETURN expertise as expertise,unionData as union, id(employment) as id,\n" +
            " employment.startDate as startDate,employment.employmentSubType as employmentSubType,employment.taxDeductionPercentage as taxDeductionPercentage, employment.endDate as endDate, \n" +
            "CASE reasonCode WHEN null THEN null else {id:id(reasonCode),name:reasonCode.name} END as reasonCode,employment.history as history,employment.editable as editable,employment.published as published, \n" +
            "employment.lastWorkingDate as lastWorkingDate,employment.accumulatedTimebankDate as accumulatedTimebankDate,employment.accumulatedTimebankMinutes as accumulatedTimebankMinutes,id(org) as parentUnitId, id(subOrg) as unitId, {id:id(subOrg),name:subOrg.name} as unitInfo " +
            "UNION " +
            "MATCH (user:User)-[:"+BELONGS_TO+"]-(staff:Staff) where id(user)={0} \n" +
            "MATCH(staff)<-[:"+BELONGS_TO+"]-(position:Position)<-[:"+ HAS_POSITIONS +"]-(org:Unit) \n" +
            "MATCH(org)<-[:"+IN_UNIT+"]-(employment:Employment{deleted:false})<-[:"+BELONGS_TO_STAFF+"]-(staff)  \n" +
            "MATCH(employment)-[:"+HAS_EXPERTISE_IN+"]->(expertise:Expertise) WHERE expertise.startDateMillis <= TIMESTAMP() AND (expertise.endDateMillis IS NULL OR expertise.endDateMillis >= TIMESTAMP())\n" +
            "OPTIONAL MATCH (employment)-[:"+HAS_REASON_CODE+"]->(reasonCode:ReasonCode) \n" +
            "OPTIONAL MATCH (expertise)-[:"+SUPPORTED_BY_UNION+"]->(unionData:Unit{isEnable:true,union:true}) \n" +
            "RETURN expertise as expertise,employment.employmentSubType as employmentSubType,employment.taxDeductionPercentage as taxDeductionPercentage,unionData as union, id(employment) as id,\n" +
            " employment.startDate as startDate, employment.endDate as endDate, \n" +
            "CASE reasonCode WHEN null THEN null else {id:id(reasonCode),name:reasonCode.name} END as reasonCode,employment.history as history,employment.editable as editable,employment.published as published, \n" +
            "employment.lastWorkingDate as lastWorkingDate,employment.accumulatedTimebankDate as accumulatedTimebankDate,employment.accumulatedTimebankMinutes as accumulatedTimebankMinutes,id(org) as parentUnitId,id(org) as unitId,\n" +
            "{id:id(org),name:org.name} as unitInfo ORDER BY employment.startDate")
    List<EmploymentQueryResult> getAllEmploymentsByUser(long userId);
//Date is not supported as a return type in Bolt protocol version 1.
// Please make sure driver supports at least protocol version 2.
// Driver upgrade is most likely required.; nested exception is org.neo4j.ogm.exception.TransactionException:
// Date is not supported as a return type in Bolt protocol version 1. Please make sure driver supports at least protocol version 2. Driver upgrade is most likely required
    @Query("MATCH(staff:Staff)-[:" + BELONGS_TO_STAFF + "]->(employment:Employment{deleted:false}) where id(staff)={0} return employment.endDate as endDate")
    List<String> getAllEmploymentsByStaffId(Long staffId);

    @Query("MATCH(employment:Employment)-[:" + IN_UNIT + "]->(subOrg:Unit) where id(employment)={0} " +
            "MATCH(employment)<-[:" + BELONGS_TO_STAFF + "]-(staff:Staff) " +
            "MATCH(staff)<-[:" + BELONGS_TO + "]-(position:Position) " +
            "MATCH(position)<-[:" + HAS_POSITIONS + "]-(org:Unit) " +
            "RETURN id(subOrg) as unitId,id(org) as parentUnitId")
    EmploymentQueryResult getUnitIdAndParentUnitIdByEmploymentId(Long employmentId);

    @Query("MATCH(employmentLine:EmploymentLine)-[employmentRel:" + HAS_EMPLOYMENT_TYPE + "]->(employmentType:EmploymentType)  where id(employmentLine)={0} return employmentLine,employmentRel,employmentType")
    EmploymentLineEmploymentTypeRelationShip findEmploymentTypeByEmploymentId(Long employmentId);


    @Query("MATCH(staff:Staff)-[:" + BELONGS_TO_STAFF + "]->(employment:Employment{deleted:false}) where id(staff)={0} AND ( employment.endDate IS NULL OR DATE(employment.endDate) > DATE({1}))  return employment")
    List<Employment> getEmploymentsFromEmploymentEndDate(Long staffId, String endDate);

    @Query("MATCH(staff:Staff)-[:" + BELONGS_TO_STAFF + "]->(employment:Employment) where id(employment)={0} return id(staff) as staffId")
    Long getStaffIdFromEmployment(Long employmentId);

    @Query("MATCH(staff:Staff)-[:" + BELONGS_TO_STAFF + "]->(employment:Employment{deleted:false}) where id(staff)={0} return max(employment.startDate) as maxStartDate")
    String getMaxEmploymentStartDate(Long staffId);

    @Query("MATCH(org:Unit)<-[:"+IN_UNIT+"]-(employment:Employment{deleted:false})<-[:"+BELONGS_TO_STAFF+"]-(staff) WHERE id(staff)={0} AND id(org)={1}\n" +
            "MATCH(employment)-[:"+HAS_EXPERTISE_IN+"]->(expertise:Expertise)  \n" +
            "OPTIONAL MATCH (employment)-[:"+HAS_REASON_CODE+"]->(reasonCode:ReasonCode) \n" +
            "OPTIONAL MATCH (expertise)-[:"+SUPPORTED_BY_UNION+"]->(unionData:Unit{isEnable:true,union:true}) \n" +
            "RETURN id(employment) as id,employment.startDate as startDate, employment.endDate as endDate,employment.employmentSubType as employmentSubType,employment.accumulatedTimebankDate as accumulatedTimebankDate,employment.accumulatedTimebankMinutes as accumulatedTimebankMinutes, \n" +
            "CASE WHEN reasonCode IS NULL THEN null else {id:id(reasonCode),name:reasonCode.name} END as reasonCode, employment.history as history,employment.taxDeductionPercentage as taxDeductionPercentage,employment.editable as editable,employment.published as published,\n" +
            "employment.lastWorkingDate as lastWorkingDate,id(org)  as unitId,{id:id(org),name:org.name} as unitInfo,expertise as expertise,unionData as union")
    List<EmploymentQueryResult> getAllEmploymentsForCurrentOrganization(long staffId, Long unitId);

    @Query("MATCH(employment:Employment{deleted:false,published:true})-[:" + HAS_EXPERTISE_IN + "]->(expertise:Expertise) where id(expertise)={2}\n" +
            "MATCH(employment)-[:"+ HAS_EMPLOYMENT_LINES +"]-(employmentLine:EmploymentLine) WHERE  date(employmentLine.startDate) <= date() AND (NOT exists(employmentLine.endDate) OR date(employmentLine.endDate) >= date())\n"+
            "MATCH(employment)<-[:" + BELONGS_TO_STAFF + "]-(staff:Staff) where id(staff)={1}" +
            "MATCH(employment)-[:" + IN_UNIT + "]-(organization:Unit) where id(organization)={0}  \n" +
            "return id(employment)")
    Long getEmploymentIdByStaffAndExpertise(Long unitId, Long staffId, Long expertiseId);

    @Query("MATCH (unit:Unit) WHERE id(unit)={0} \n" +
            "MATCH (unit)<-[:" + IN_UNIT + "]-(employment:Employment{deleted:false,published:true})-[:" + HAS_EXPERTISE_IN + "]-(expertise:Expertise) \n" +
            "RETURN id(employment) as employmentId, id(expertise) as expertiseId")
    List<Map<Long, Long>> getMapOfEmploymentAndExpertiseId(Long unitId);


    @Query("MATCH (user:User)-[:BELONGS_TO]-(staff:Staff)<-[:" + BELONGS_TO + "]-(position:Position)<-[:"+HAS_POSITIONS+"]-(org:Unit) where id(user)={0}\n" +
            "MATCH(org)-[:"+HAS_SUB_ORGANIZATION+"*]->(subOrg:Unit)\n" +
            "MATCH(subOrg)<-[:"+IN_UNIT+"]-(employment:Employment{deleted:false,published:true})<-[:"+BELONGS_TO_STAFF+"]-(staff)\n" +
            "return  id(employment) as id,employment.history as history, \n" +
            "id(org) as parentUnitId, id(subOrg) as unitId, {id:id(subOrg),name:subOrg.name} as unitInfo ORDER BY employment.creationDate" +
            " UNION " +
            "MATCH (user:User)-[:"+BELONGS_TO+"]-(staff:Staff) where id(user)={0}\n" +
            "MATCH(staff)<-[:"+BELONGS_TO+"]-(position:Position)<-[:"+HAS_POSITIONS+"]-(org:Unit) \n" +
            "MATCH(org)<-[:"+IN_UNIT+"]-(employment:Employment{deleted:false,published:true})<-[:"+BELONGS_TO_STAFF+"]-(staff)  \n" +
            "return id(employment) as id, employment.history as history,\n" +
            "id(org) as parentUnitId,id(org) as unitId,{id:id(org),name:org.name} as unitInfo ORDER BY employment.creationDate")
    List<EmploymentQueryResult> getAllEmploymentsBasicDetailsAndWTAByUser(long userId);



    @Query( "MATCH(sector:Sector)-[:"+BELONGS_TO_SECTOR+"]-(expertise:Expertise{deleted:false}) \n" +
            "WITH sector,expertise \n" +
            "MATCH(staff:Staff)-[expertise_from_date:"+STAFF_HAS_EXPERTISE+"]->(expertise)\n" +
            "WITH staff,sector,expertise,expertise_from_date \n" +
            "WITH MIN(expertise_from_date.expertiseStartDate) as expertise_from_date,staff,expertise\n" +
            "WHERE expertise_from_date IS NOT NULL AND datetime({epochmillis:expertise_from_date}).month=datetime().month AND \n" +
            "datetime({epochmillis:expertise_from_date}).day=datetime().day AND datetime({epochmillis:expertise_from_date}).year<>datetime().year \n" +
            "MATCH(activeEmploymentLine:EmploymentLine)<-[:"+ HAS_EMPLOYMENT_LINES +"]-(employment:Employment{deleted:false,published:true})-[:"+HAS_EXPERTISE_IN+"]->(expertise) WHERE activeEmploymentLine.endDate IS NULL OR activeEmploymentLine.endDate >= date()\n" +
            "WITH staff,expertise,employment,activeEmploymentLine, datetime().year-datetime({epochmillis:expertise_from_date}).year as years_experience_in_expertise  \n" +
            "MATCH(staff)-[:"+BELONGS_TO_STAFF+"]->(employment)-[:"+HAS_EXPERTISE_IN+"]->(expertise) " +
            "WITH staff,employment,expertise,years_experience_in_expertise,activeEmploymentLine \n" +
            "MATCH(activeEmploymentLine)-[:"+HAS_SENIORITY_LEVEL+"]->(sl:SeniorityLevel) WHERE sl.to<=years_experience_in_expertise \n" +
            "MATCH(activeEmploymentLine)-[employmentEmploymentTypeRelationShip:"+HAS_EMPLOYMENT_TYPE+"]->(employmentType:EmploymentType)\n" +
            "MATCH(expertise)-[:"+FOR_SENIORITY_LEVEL+"]->(nextSeniorityLevel:SeniorityLevel) WHERE nextSeniorityLevel.from <= years_experience_in_expertise and (nextSeniorityLevel.to > years_experience_in_expertise or nextSeniorityLevel.to is null) \n" +
            "RETURN id(employment) as employmentId,employmentEmploymentTypeRelationShip as employmentLineEmploymentTypeRelationShip ,employmentType,activeEmploymentLine as employmentLine,nextSeniorityLevel as seniorityLevel")
    List<EmploymentSeniorityLevelQueryResult> findEmploymentSeniorityLeveltoUpdate();

    @Query("MATCH(staff:Staff),(unit:Unit) where id(staff)={0} and id(unit)={1} \n" +
            "MATCH(unit)<-[:" + IN_UNIT + "]-(employment:Employment{deleted:false,published:true})<-[:" + BELONGS_TO_STAFF + "]-(staff)" +
            "MATCH(employment)-[:"+ HAS_EMPLOYMENT_LINES +"]-(employmentLine:EmploymentLine) WHERE  date(employmentLine.startDate) <= date() AND (NOT exists(employmentLine.endDate) OR date(employmentLine.endDate) >= date()) \n" +
            "MATCH(employment)-[:" + HAS_EXPERTISE_IN + "]->(expertise:Expertise) \n" +
            "MATCH(employmentLine)-[employmentRel:" + HAS_EMPLOYMENT_TYPE + "]->(employmentType:EmploymentType) \n" +
            "WITH expertise,employment,employmentLine,{employmentTypeCategory:employmentRel.employmentTypeCategory,name:employmentType.name,id:id(employmentType)} as employmentType \n" +
            "return expertise as expertise,employment.startDate as startDate,employment.accumulatedTimebankDate as accumulatedTimebankDate,employment.accumulatedTimebankMinutes as accumulatedTimebankMinutes, employment.published as published,employment.endDate as endDate, id(employment) as id,employment.lastWorkingDate as lastWorkingDate,\n" +
            "CASE employmentLine when null then [] else COLLECT({totalWeeklyMinutes:(employmentLine.totalWeeklyMinutes%60),totalWeeklyHours:(employmentLine.totalWeeklyMinutes / 60),fullTimeWeeklyMinutes:employmentLine.fullTimeWeeklyMinutes, hourlyCost:employmentLine.hourlyCost,id:id(employmentLine), workingDaysInWeek:employmentLine.workingDaysInWeek ,\n" +
            " avgDailyWorkingHours:employmentLine.avgDailyWorkingHours,employmentType:employmentType}) end as employmentLines")
    EmploymentQueryResult getEmploymentOfStaff(Long staffId, Long unitId);

    @Query("MATCH(employment:Employment) where id(employment)={0} return employment.published as published ," +
            "employment.startDate as startDate, employment.endDate as endDate, id(employment) as id")
    EmploymentQueryResult findByEmploymentId(Long employmentId);

    @Query(" MATCH (employment:Employment) where id(employment) IN {0} " +
            "MATCH(employment)-[:" + HAS_EMPLOYMENT_LINES + "]-(employmentLine:EmploymentLine) " +
            "MATCH(employmentLine)-[:" + HAS_SENIORITY_LEVEL + "]->(seniorityLevel:SeniorityLevel)-[:" + HAS_BASE_PAY_GRADE + "]-(payGrade:PayGrade) " +
            "MATCH(employmentLine)-[employmentRel:" + HAS_EMPLOYMENT_TYPE + "]->(employmentType:EmploymentType) " +
            "OPTIONAL MATCH (employmentLine)-[functionalRel:" + APPLICABLE_FUNCTION + "]-(function:Function) "+
            "OPTIONAL MATCH(employment)-[:" + IN_UNIT + "]-(org:Unit)-[:" + CONTACT_ADDRESS + "]->(contactAddress:ContactAddress)-[:" + MUNICIPALITY + "]->(municipality:Municipality)-[:" + HAS_MUNICIPALITY + "]-(pga:PayGroupArea)<-[pgaRel:" + HAS_PAY_GROUP_AREA + "]-(payGrade) " +
            " WITH  employment,employmentLine,payGrade,seniorityLevel,employmentType,employmentRel,pgaRel,functionalRel, case function when  null  then [] else collect({id:id(function),name:function.name,icon:function.icon, amount:functionalRel.amount}) end as functionData "+
            "return id(employmentLine) as id,id(employment) as employmentId," +
            "{id:id(seniorityLevel),from:seniorityLevel.from,pensionPercentage:seniorityLevel.pensionPercentage,freeChoicePercentage:seniorityLevel.freeChoicePercentage," +
            " freeChoiceToPension:seniorityLevel.freeChoiceToPension,to:seniorityLevel.to,functions:collect(functionData[0])," +
            "payGrade:{id:id(payGrade),payGradeLevel:payGrade.payGradeLevel}} as seniorityLevel, "+
            " {employmentTypeCategory:employmentRel.employmentTypeCategory,name:employmentType.name,id:id(employmentType),editableAtEmployment:employmentType.editableAtEmployment,weeklyMinutes:employmentType.weeklyMinutes,markMainEmployment:employmentType.markMainEmployment} as employmentType," +
            "employmentLine.workingDaysInWeek as workingDaysInWeek,employmentLine.fullTimeWeeklyMinutes as fullTimeWeeklyMinutes, \n" +
            "(employmentLine.totalWeeklyMinutes % 60) as totalWeeklyMinutes,(employmentLine.totalWeeklyMinutes / 60) as  totalWeeklyHours," +
            " employmentLine.startDate as startDate, employmentLine.endDate as endDate ,\n" +
            "employmentLine.avgDailyWorkingHours as avgDailyWorkingHours ORDER BY employmentLine.startDate"
    )
    List<EmploymentLinesQueryResult> findAllEmploymentLines(List<Long> employmentIds);

    @Query(" MATCH (employmentLine:EmploymentLine) where id(employmentLine) IN {0} " +
            " MATCH (employmentLine)-[functionalRel:" + APPLICABLE_FUNCTION + "]-(function:Function) "+
            "RETURN distinct function as function,functionalRel.amount as amount")
    List<FunctionWithAmountQueryResult> findAllAppliedFunctionOnEmploymentLines(Long employmentLineId);

    @Query(" MATCH (employmentLine:EmploymentLine) where id(employmentLine) IN {0} " +
            "MATCH (employmentLine)-[functionalRel:" + APPLICABLE_FUNCTION + "]-(function:Function) "+
            "detach delete functionalRel")
    void removeAllAppliedFunctionOnEmploymentLines(Long employmentLineId);


    @Query(" MATCH (employment:Employment{deleted:false}) where id(employment) IN  {0} \n" +
            "MATCH(employment)-[:"+ HAS_EMPLOYMENT_LINES +"]-(employmentLine:EmploymentLine) \n" +
            "MATCH(employmentLine)-[:"+HAS_SENIORITY_LEVEL+"]->(seniorityLevel:SeniorityLevel)-[:"+HAS_BASE_PAY_GRADE+"]-(payGrade:PayGrade) \n" +
            "MATCH(employment)-[:"+HAS_EXPERTISE_IN+"]->(expertise:Expertise{published:true}) \n" +
            "OPTIONAL MATCH(employment)-[:"+IN_UNIT+"]-(org:Unit)-[:"+CONTACT_ADDRESS+"]->(contactAddress:ContactAddress)-[:"+MUNICIPALITY+"]->(municipality:Municipality)-[:"+HAS_MUNICIPALITY+"]-(pga:PayGroupArea)<-[pgaRel:"+HAS_PAY_GROUP_AREA+"]-(payGrade) \n" +
            "WITH  employment,employmentLine,payGrade,expertise,seniorityLevel, CASE when pgaRel.payGroupAreaAmount IS NULL THEN toInteger('0') ELSE toInteger(pgaRel.payGroupAreaAmount) END as hourlyCost \n" +
            "OPTIONAL MATCH (employmentLine)-[:"+APPLICABLE_FUNCTION+"]-(function:Function) \n" +
            "WITH  employment,employmentLine,expertise,seniorityLevel,hourlyCost,function\n" +
            "OPTIONAL MATCH(functionalPayment:FunctionalPayment)-[:"+APPLICABLE_FOR_EXPERTISE+"]->(expertise) where date(functionalPayment.startDate) <= date(employmentLine.startDate) AND (functionalPayment.endDate IS NULL OR date(employmentLine.startDate)<= date(functionalPayment.endDate))\n" +
            "WITH  employment,employmentLine,expertise,functionalPayment,seniorityLevel,function,hourlyCost\n" +
            "OPTIONAL MATCH(functionalPayment)-[:"+FUNCTIONAL_PAYMENT_MATRIX+"]->(fpm:FunctionalPaymentMatrix) \n" +
            "WITH  employment,employmentLine,expertise,fpm,seniorityLevel,function,functionalPayment,hourlyCost\n" +
            "OPTIONAL MATCH(fpm)-[:"+SENIORITY_LEVEL_FUNCTIONS+"]->(slf:SeniorityLevelFunction)-[:"+FOR_SENIORITY_LEVEL+"]->(seniorityLevel) \n" +
            "WITH  employment,employmentLine,expertise,fpm,slf,function,functionalPayment,hourlyCost\n" +
            "OPTIONAL MATCH(slf)-[rel:"+HAS_FUNCTIONAL_AMOUNT+"]-(function) \n" +
            "WITH functionalPayment,employmentLine,hourlyCost, sum(toInteger(rel.amount)) as totalCostOfFunctions WITH employmentLine,CASE WHEN functionalPayment.paymentUnit='MONTHLY' THEN totalCostOfFunctions*12+hourlyCost ELSE totalCostOfFunctions+hourlyCost END as hourlyCost,functionalPayment\n" +
            "RETURN id(employmentLine) as id, toString(hourlyCost) as hourlyCost ")
    List<EmploymentLinesQueryResult> findFunctionalHourlyCost(List<Long> employmentIds);


@Query("OPTIONAL MATCH (organization:Unit)  WHERE id(organization)={0}\n" +
        "OPTIONAL MATCH (staff:Staff)  WHERE id(staff)={1}\n" +
        "OPTIONAL MATCH(staff)-[:"+BELONGS_TO_STAFF+"]->(employment:Employment{published:true})-[:"+IN_UNIT+"]->(organization) " +
        "WITH staff,organization,employment " +
        "MATCH(employment)-[:"+HAS_EXPERTISE_IN+"]-(expertise:Expertise)\n" +
        "RETURN \n" +
        "CASE \n" +
        "WHEN organization IS NULL THEN \"organization\" \n" +
        "WHEN staff IS NULL THEN \"staff\"\n" +
        "WHEN employment IS NULL THEN \"employment\" \n" +
        "ELSE COLLECT({id:id(employment),startDate:employment.startDate,endDate:employment.endDate,expertise:{id:id(expertise), name:expertise.name}}) END ")
        Object getEmploymentsByUnitIdAndStaffId(Long unitId, Long staffId);

    @Query("MATCH(employment:Employment{published:true})-[employmentOrgRel:"+ IN_UNIT +"]-(organization:Unit) \n" +
            "WHERE id(organization)={0}  AND id(employment)={1} \n" +
            "MATCH(employmentLine:EmploymentLine)-[:"+ HAS_EMPLOYMENT_LINES +" ]-(employment)   \n" +
            "MATCH(employmentLine)-[: "+HAS_SENIORITY_LEVEL +"]->(seniorityLevel:SeniorityLevel)-[: HAS_BASE_PAY_GRADE ]-(payGrade:PayGrade)   \n" +
            "MATCH(employment)-[:"+ HAS_EXPERTISE_IN +"]->(expertise:Expertise{published:true})   \n" +
            "OPTIONAL MATCH(organization)-[: "+CONTACT_ADDRESS +"]->(contactAddress:ContactAddress)-[:"+ MUNICIPALITY +"]->(municipality:Municipality)-[:"+ HAS_MUNICIPALITY+"]-(pga:PayGroupArea)<-[pgaRel:"+ HAS_PAY_GROUP_AREA+" ]-(payGrade)   \n" +
            "WITH  employmentLine,payGrade,expertise,seniorityLevel,  \n" +
            "CASE WHEN pgaRel.payGroupAreaAmount IS NULL THEN toInteger('0') ELSE toInteger(pgaRel.payGroupAreaAmount) END AS basePayGradeAmount   \n" +
            "OPTIONAL MATCH (employmentLine)-[: "+APPLICABLE_FUNCTION+" ]-(function:Function)   \n" +
            "OPTIONAL MATCH(functionalPayment:FunctionalPayment)-[: "+APPLICABLE_FOR_EXPERTISE+" ]->(expertise) where DATE(functionalPayment.startDate) <= date(employmentLine.startDate) AND (functionalPayment.endDate IS NULL OR date(employmentLine.startDate)<= date(functionalPayment.endDate))   \n" +
            "WITH  employmentLine,functionalPayment,seniorityLevel,function,basePayGradeAmount   \n" +
            "OPTIONAL MATCH(functionalPayment)-[: "+FUNCTIONAL_PAYMENT_MATRIX+" ]->(fpm:FunctionalPaymentMatrix)    \n" +
            "WITH  employmentLine,fpm,seniorityLevel,function,functionalPayment,basePayGradeAmount   \n" +
            "OPTIONAL MATCH(fpm)-[: "+SENIORITY_LEVEL_FUNCTIONS+" ]->(slf:SeniorityLevelFunction)-[: "+FOR_SENIORITY_LEVEL+" ]->(seniorityLevel)   \n" +
            " WITH  employmentLine,fpm,slf,function,functionalPayment,basePayGradeAmount   \n" +
            "OPTIONAL MATCH(slf)-[rel: "+HAS_FUNCTIONAL_AMOUNT +"]-(function)   \n" +
            "WITH  employmentLine,COLLECT(DISTINCT {id:id(function),amount:rel.amount,name:function.name}) AS functions,basePayGradeAmount,CASE WHEN functionalPayment.paymentUnit='MONTHLY' THEN sum(toInteger(rel.amount))*12 ELSE sum(toInteger(rel.amount)) END  as totalCostOfFunctions\n" +
            "RETURN  \n" +
            "employmentLine.startDate AS startDate,toString(basePayGradeAmount) AS basePayGradeAmount,functions,toString(basePayGradeAmount+totalCostOfFunctions) AS hourlyCost ")
    List<EmploymentLineFunctionQueryResult> getFunctionalHourlyCostByEmploymentId(Long unitId, Long employmentId);

    @Query("OPTIONAL MATCH(organization:Unit) WHERE id(organization)={0}\n" +
            "OPTIONAL MATCH(staff:Staff) WHERE id(staff)={1}\n" +
            "OPTIONAL MATCH(employment:Employment) WHERE id(employment)={2}\n" +
            "OPTIONAL MATCH(employment:Employment{published:true})-[employmentOrgRel: IN_UNIT ]-(organization) \n" +
            "OPTIONAL MATCH(staff)-[employmentStaffRel: BELONGS_TO_STAFF ]->(employment) " +
            "RETURN  \n" +
            " CASE  \n" +
            " WHEN organization IS NULL THEN \"organization\"    \n" +
            " WHEN staff IS NULL THEN \"staff\"     \n" +
            " WHEN employment IS NULL THEN \"employment\"    \n" +
            " WHEN employmentOrgRel IS NULL THEN  \"employmentOrgRel\"    \n" +
            " WHEN employmentStaffRel IS NULL THEN  \"employmentStaffRel\" \n" +
            "ELSE \"valid\" \n" +
            "END")
    String validateOrganizationStaffEmployment(Long unitId, Long staffId, Long employmentId);

    @Query("MATCH(staff:Staff)-[:"+BELONGS_TO+"]->(user:User)  where id(staff)={0}\n" +
            "MATCH(user)<-[:"+BELONGS_TO+"]-(staffList:Staff)\n" +
            "OPTIONAL MATCH(staffList)-[:"+BELONGS_TO_STAFF+"]->(employment:Employment) WHERE id(employment)<> {3} " +
            "AND employment.employmentSubType ={4} AND  " +
            "(({2} IS NULL AND (employment.endDate IS NULL OR DATE(employment.endDate) >= DATE({1}))) \n" +
            "OR ({2} IS NOT NULL AND DATE(employment.startDate) <= DATE({2}) AND (employment.endDate IS NULL OR DATE(employment.endDate)>DATE({1}))) ) \n" +
            "WITH employment  \n" +
            "MATCH(employment)-[:"+IN_UNIT+"]-(org:Unit)\n" +
            "RETURN id(employment) as id,employment.startDate as startDate,employment.endDate as endDate,org.name as unitName \n ")
    EmploymentQueryResult findAllByStaffIdAndBetweenDates(Long staffId, String startDate, String endDate, long id, EmploymentSubType employmentSubType);



    @Query("MATCH(employment:Employment)-[:"+HAS_EMPLOYMENT_LINES+"]->(employmentLines:EmploymentLine{deleted:false}) " +
            "WHERE id(employment) IN {0} AND ( employmentLines.endDate IS NULL OR DATE(employmentLines.endDate) > DATE({1})) WITH employmentLines SET employmentLines.endDate = {1} RETURN  COUNT(employmentLines)>0")
    boolean updateEmploymentLineEndDateByEmploymentIds(Set<Long> employmentIds,String endDate);


}

package com.kairos.persistence.repository.user.agreement.wta;

import com.kairos.persistence.model.user.agreement.wta.*;
import com.kairos.persistence.model.user.expertise.ExpertiseIdListDTO;
import org.springframework.data.neo4j.annotation.Query;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;

/**
 * Created by pawanmandhan on 27/7/17.
 */
@Repository
public interface WorkingTimeAgreementGraphRepository extends Neo4jBaseRepository<WorkingTimeAgreement, Long> {

    @Query("MATCH (wta:WorkingTimeAgreement {deleted:false}) where id(wta)={0} \n" +
            "match(wta)-[:" + HAS_EXPERTISE_IN + "]->(expertise:Expertise{isEnabled:true})\n" +
            "return wta")
    WorkingTimeAgreement getWta(Long wtaId);

    @Query("match(wta:WorkingTimeAgreement{deleted:false})-[:" + BELONGS_TO + "]->(c:Country) where id(c)={1} AND wta.name =~{0} return wta LIMIT 1")
    WorkingTimeAgreement getWtaByName(String wtaName, Long countryId);

    @Query("match(wta:WorkingTimeAgreement{deleted:false})-[:" + BELONGS_TO + "]->(c:Country) where id(c)={1} AND wta.name =~{0} AND id(wta) <> {2} " +
            "match(wta)-[:" + BELONGS_TO_ORG_TYPE + "]->(orgType:OrganizationType) where id(orgType)={3}\n" +
            "match(wta)-[:" + BELONGS_TO_ORG_SUB_TYPE + "]->(orgSubType:OrganizationType) where id(orgSubType)={4} \n" +
            "return wta")
    WorkingTimeAgreement getWtaByNameExcludingCurrent(String wtaName, Long countryId, Long wtaId, Long organizationTypeId, Long subOrganizationTypeId);

    @Query("match(wta:WorkingTimeAgreement{deleted:false})-[:" + BELONGS_TO_ORG_TYPE + "]->(o:OrganizationType) where id(o)={0}\n" +
            "match(wta)-[:" + HAS_EXPERTISE_IN + "]->(expertises:Expertise{isEnabled:true})\n" +
            "optional match(wta)-[:" + HAS_RULE_TEMPLATE + "]->(ruleTemp:WTABaseRuleTemplate)-[:" + HAS_RULE_TEMPLATES + "]-(ruleTempCatg:RuleTemplateCategory)" +
            " with wta,o,expertises,ruleTemp,ruleTempCatg\n" +
            "with wta,expertises, CASE  WHEN ruleTemp IS NOT NULL THEN collect({disabled:ruleTemp.disabled,daysLimit:ruleTemp.daysLimit,fromDayOfWeek:ruleTemp.fromDayOfWeek," +
            "minimumDurationBetweenShifts:ruleTemp.minimumDurationBetweenShifts, fromTime:ruleTemp.fromTime,activityCode:ruleTemp.activityCode," +
            "onlyCompositeShifts:ruleTemp.onlyCompositeShifts,shiftsLimit:ruleTemp.shiftsLimit,shiftAffiliation:ruleTemp.shiftAffiliation,averageRest:ruleTemp.averageRest," +
            "continuousWeekRest:ruleTemp.continuousWeekRest,proportional:ruleTemp.proportional,toTime:ruleTemp.toTime,toDayOfWeek:ruleTemp.toDayOfWeek," +
            "continuousDayRestHours:ruleTemp.continuousDayRestHours,name:ruleTemp.name,id:Id(ruleTemp),minimumRest:ruleTemp.minimumRest,timeLimit:ruleTemp.timeLimit,balanceType:ruleTemp.balanceType," +
            "templateType:ruleTemp.templateType,nightsWorked:ruleTemp.nightsWorked,description:ruleTemp.description, numberShiftsPerPeriod:ruleTemp.numberShiftsPerPeriod," +
            "numberOfWeeks:ruleTemp.numberOfWeeks,maximumVetoPercentage:ruleTemp.maximumVetoPercentage,maximumAvgTime:ruleTemp.maximumAvgTime,useShiftTimes:ruleTemp.useShiftTimes," +
            "balanceAdjustment:ruleTemp.balanceAdjustment,intervalLength:ruleTemp.intervalLength,intervalUnit:ruleTemp.intervalUnit," +
            "validationStartDateMillis:ruleTemp.validationStartDateMillis,daysWorked:ruleTemp.daysWorked,nightsWorked:ruleTemp.nightsWorked,description:ruleTemp.description," +
            "checkAgainstTimeRules:ruleTemp.checkAgainstTimeRules,ruleTemplateCategory:{name:ruleTempCatg.name,id:Id(ruleTempCatg)}}) else [] END as ruleTemplates\n" +
            "RETURN ruleTemplates as ruleTemplates, wta.endDateMillis as endDateMillis,wta.startDateMillis as startDateMillis,wta.expiryDate as expiryDate,wta.description as description," +
            "expertises as expertises,wta.creationDate as creationDate,wta.name as name,id(wta) as id"
    )
    List<WTAResponseDTO> getAllWTAByOrganizationTypeId(long organizationId);

    @Query("match(wta:WorkingTimeAgreement{deleted:false})-[:" + BELONGS_TO + "]->(c:Country) where id(c)={0} \n" +
            "match(wta)-[:" + HAS_EXPERTISE_IN + "]->(expertise:Expertise{isEnabled:true}) \n" +
            "match(wta)-[:" + BELONGS_TO_ORG_TYPE + "]->(orgType:OrganizationType)\n" +
            "match(wta)-[:" + BELONGS_TO_ORG_SUB_TYPE + "]->(orgSubType:OrganizationType) \n" +
            "optional match(wta)-[:" + HAS_RULE_TEMPLATE + "]->(ruleTemp:WTABaseRuleTemplate)-[:" + HAS_RULE_TEMPLATES + "]-(ruleTempCatg:RuleTemplateCategory)\n" +
            "with wta,expertise,orgType,orgSubType,ruleTemp,ruleTempCatg optional MATCH (wta)-[r:" + HAS_TAG + "]->(t:Tag)<-[:" + COUNTRY_HAS_TAG + "]-(c) WHERE t.masterDataType='WTA' AND t.countryTag=true AND t.deleted =false\n" +
            "return wta.isEnabled as isEnabled," +
            "CASE when t IS NULL THEN [] ELSE collect({id:id(t),name:t.name,countryTag:t.countryTag})   END as tags, \n" +
            "wta.startDateMillis as startDateMillis,CASE  WHEN ruleTemp IS NOT NULL THEN collect({disabled:ruleTemp.disabled,daysLimit:ruleTemp.daysLimit,fromDayOfWeek:ruleTemp.fromDayOfWeek,minimumDurationBetweenShifts:ruleTemp.minimumDurationBetweenShifts," +
            " fromTime:ruleTemp.fromTime,activityCode:ruleTemp.activityCode,onlyCompositeShifts:ruleTemp.onlyCompositeShifts,shiftsLimit:ruleTemp.shiftsLimit," +
            "shiftAffiliation:ruleTemp.shiftAffiliation,averageRest:ruleTemp.averageRest,continuousWeekRest:ruleTemp.continuousWeekRest,proportional:ruleTemp.proportional," +
            "toTime:ruleTemp.toTime,toDayOfWeek:ruleTemp.toDayOfWeek,continuousDayRestHours:ruleTemp.continuousDayRestHours,name:ruleTemp.name,id:Id(ruleTemp)," +
            "minimumRest:ruleTemp.minimumRest,timeLimit:ruleTemp.timeLimit,balanceType:ruleTemp.balanceType,templateType:ruleTemp.templateType,nightsWorked:ruleTemp.nightsWorked," +
            "description:ruleTemp.description, numberShiftsPerPeriod:ruleTemp.numberShiftsPerPeriod,numberOfWeeks:ruleTemp.numberOfWeeks,maximumVetoPercentage:ruleTemp.maximumVetoPercentage," +
            "maximumAvgTime:ruleTemp.maximumAvgTime,useShiftTimes:ruleTemp.useShiftTimes,balanceAdjustment:ruleTemp.balanceAdjustment,intervalLength:ruleTemp.intervalLength," +
            "intervalUnit:ruleTemp.intervalUnit,validationStartDateMillis:ruleTemp.validationStartDateMillis,daysWorked:ruleTemp.daysWorked,nightsWorked:ruleTemp.nightsWorked," +
            "description:ruleTemp.description,ruleTemplateCategory:{name:ruleTempCatg.name,id:Id(ruleTempCatg)}," +
            "checkAgainstTimeRules:ruleTemp.checkAgainstTimeRules,ruleTemplateCategoryId:id(ruleTempCatg)}) else [] END as ruleTemplates," +
            "wta.endDateMillis as endDateMillis,orgType as organizationType,orgSubType as organizationSubType,expertise as expertise," +
            "wta.expiryDate as expiryDate," +
            "wta.description as description," +
            "wta.name as name," +
            "id(wta) as id")
    List<WTAResponseDTO> getAllWTAByCountryId(long countryId);

    @Query("match(wta:WorkingTimeAgreement{deleted:false})-[:" + BELONGS_TO_ORG_SUB_TYPE + "]->(o:OrganizationType) where id(o)={0} \n" +
            "optional match(wta)-[:" + HAS_EXPERTISE_IN + "]->(expertise:Expertise)\n" +
            "optional match(wta)-[:" + HAS_RULE_TEMPLATE + "]->(ruleTemp:WTABaseRuleTemplate)-[:" + HAS_RULE_TEMPLATES + "]-(ruleTemplateCatg:RuleTemplateCategory)\n" +
            "RETURN CASE  WHEN ruleTemp IS NOT NULL THEN collect({disabled:ruleTemp.disabled,daysLimit:ruleTemp.daysLimit,isDisabled:ruleTemp.isDisabled,ruleTemplateCategory:{name:ruleTemplateCatg.name,id:Id(ruleTemplateCatg)},fromDayOfWeek:ruleTemp.fromDayOfWeek," +
            "minimumDurationBetweenShifts:ruleTemp.minimumDurationBetweenShifts, fromTime:ruleTemp.fromTime,activityCode:ruleTemp.activityCode,onlyCompositeShifts:ruleTemp.onlyCompositeShifts," +
            "shiftsLimit:ruleTemp.shiftsLimit,shiftAffiliation:ruleTemp.shiftAffiliation,averageRest:ruleTemp.averageRest,continuousWeekRest:ruleTemp.continuousWeekRest,proportional:ruleTemp.proportional," +
            "toTime:ruleTemp.toTime,toDayOfWeek:ruleTemp.toDayOfWeek,continuousDayRestHours:ruleTemp.continuousDayRestHours,name:ruleTemp.name,id:Id(ruleTemp),minimumRest:ruleTemp.minimumRest," +
            "timeLimit:ruleTemp.timeLimit,balanceType:ruleTemp.balanceType,templateType:ruleTemp.templateType,nightsWorked:ruleTemp.nightsWorked,description:ruleTemp.description," +
            " numberShiftsPerPeriod:ruleTemp.numberShiftsPerPeriod,numberOfWeeks:ruleTemp.numberOfWeeks,maximumVetoPercentage:ruleTemp.maximumVetoPercentage,maximumAvgTime:ruleTemp.maximumAvgTime," +
            "useShiftTimes:ruleTemp.useShiftTimes,balanceAdjustment:ruleTemp.balanceAdjustment,intervalLength:ruleTemp.intervalLength,intervalUnit:ruleTemp.intervalUnit,validationStartDateMillis:ruleTemp.validationStartDateMillis," +
            "daysWorked:ruleTemp.daysWorked,nightsWorked:ruleTemp.nightsWorked,description:ruleTemp.description,checkAgainstTimeRules:ruleTemp.checkAgainstTimeRules}) else [] END as ruleTemplates, wta.endDateMillis as endDateMillis,wta.startDateMillis as startDateMillis,wta.expiryDate as expiryDate,wta.description as description," +
            "expertise as expertise,wta.creationDate as creationDate, wta.endDate as endDate,wta.name as name,id(wta) as id"
    )
    List<WTAResponseDTO> getAllWTAByOrganizationSubType(long organizationSubTypeId);

    @Query("match(c:Country) where id(c)={0}\n" +
            "match(c)<-[:" + BELONGS_TO + "]-(or:OrganizationType{isEnable:true})\n" +
            "optional match(or)-[:" + HAS_SUB_TYPE + "]->(ora:OrganizationType{isEnable:true})\n" +
            "optional match(w:WorkingTimeAgreement{deleted:false})-[:" + BELONGS_TO_ORG_SUB_TYPE + "]->(ora)\n" +
            "with or,ora,w\n" +
            "with or,ora,{WTA:CASE WHEN w IS NOT NULL THEN collect({id:id(w),name:w.name}) ELSE [] END} as oraRes\n" +
            "WITH {name: or.name,id:id(or), children: CASE WHEN ora IS NOT NULL THEN collect({id:id(ora),name:ora.name,wtaa:oraRes}) ELSE [] END} as orga\n" +
            "RETURN orga as result")
    List<Map<String, Object>> getAllWTAWithOrganization(long countryId);

    @Query("match(c:Country) where id(c)={0}\n" +
            "match(wta:WorkingTimeAgreement{deleted:false}) where id(wta)={1}\n" +
            "match(wta)-[:" + BELONGS_TO_ORG_TYPE + "]-(or:OrganizationType{isEnable:true})\n" +
            "match(wta)-[:" + BELONGS_TO_ORG_SUB_TYPE + "]->(org:OrganizationType{isEnable:true})\n" +
            "with or ,org ,{WTA:CASE WHEN wta IS NOT NULL THEN collect({id:id(wta),name:wta.name}) ELSE [] END} as oraRes\n" +
            "WITH {name: or.name,id:id(or), children: CASE WHEN org IS NOT NULL THEN collect({id:id(org),name:org.name,wta:oraRes}) ELSE [] END} as orga \n" +
            "RETURN orga as result")
    List<Map<String, Object>> getAllWTAWithWTAId(long countryId, long wtaId);

    @Query("match (wta:WorkingTimeAgreement{deleted:false})-[:" + BELONGS_TO + "]->(c:Country) where id(c)={3}\n" +
            "match(wta)-[:" + BELONGS_TO_ORG_TYPE + "]->(o:OrganizationType) where Id(o)={1}\n" +
            "match(wta)-[:" + BELONGS_TO_ORG_SUB_TYPE + "]->(orSubType:OrganizationType) where Id(orSubType)={0}\n" +
            "match(wta)-[:" + HAS_EXPERTISE_IN + "]->(exp:Expertise{isEnabled:true}) where Id(exp)={2}\n" +
            "return wta")
    WorkingTimeAgreement checkUniquenessOfData(long orgSubTypeId, long orgTypeId, long expertiseId, long countryId);

    @Query("match (wta:WorkingTimeAgreement{deleted:false})-[:" + BELONGS_TO + "]->(c:Country) where id(c)={3} AND Id(wta) <> {4}\n" +
            "match(wta)-[:" + BELONGS_TO_ORG_TYPE + "]->(o:OrganizationType) where Id(o)={1}\n" +
            "match(wta)-[:" + BELONGS_TO_ORG_SUB_TYPE + "]->(orSubType:OrganizationType) where Id(orSubType)={0}\n" +
            "match(wta)-[:" + HAS_EXPERTISE_IN + "]->(exp:Expertise{isEnabled:true}) where Id(exp)={2}\n" +
            "return wta")
    WorkingTimeAgreement checkUniquenessOfDataExcludingCurrent(long orgSubTypeId, long orgTypeId, long expertiseId, long countryId, long wtaId);

    @Query("match (linkedEx:Expertise{deleted:false})<-[:" + HAS_EXPERTISE_IN + "]-(wta:WorkingTimeAgreement{deleted:false})-[:" + BELONGS_TO_ORG_SUB_TYPE + "]->(o:OrganizationType) where id(o)={1}\n" +
            "with collect (id(linkedEx)) as linkedExpertiseIds match(allExp:Expertise{isEnabled:true})-[:" + BELONGS_TO + "]->(c:Country) where Id(c)={0} \n" +
            "with linkedExpertiseIds, collect (id(allExp)) as allExpertiseIds\n" +
            "return linkedExpertiseIds,allExpertiseIds")
    ExpertiseIdListDTO getAvailableAndFreeExpertise(long countryId, long organizationSubTypeId);


    @Query("match(expertise:Expertise) where Id(expertise)={0} " +
            "optional match(wta:WorkingTimeAgreement{deleted:false})-[:" + HAS_EXPERTISE_IN + "]->(expertise) with wta,expertise\n" +
            "optional match(wta)-[:" + HAS_RULE_TEMPLATE + "]-(ruleTemp:WTABaseRuleTemplate)-[:" + HAS_RULE_TEMPLATES + "]-(ruleTemplateCatg:RuleTemplateCategory) with wta,expertise,ruleTemp,ruleTemplateCatg\n" +
            "RETURN CASE  WHEN ruleTemp IS NOT NULL THEN collect({disabled:ruleTemp.disabled,daysLimit:ruleTemp.daysLimit,isDisabled:ruleTemp.isDisabled,ruleTemplateCategory:{name:ruleTemplateCatg.name,id:Id(ruleTemplateCatg)},fromDayOfWeek:ruleTemp.fromDayOfWeek," +
            "minimumDurationBetweenShifts:ruleTemp.minimumDurationBetweenShifts, fromTime:ruleTemp.fromTime,activityCode:ruleTemp.activityCode,onlyCompositeShifts:ruleTemp.onlyCompositeShifts," +
            "shiftsLimit:ruleTemp.shiftsLimit,shiftAffiliation:ruleTemp.shiftAffiliation,averageRest:ruleTemp.averageRest,continuousWeekRest:ruleTemp.continuousWeekRest,proportional:ruleTemp.proportional," +
            "toTime:ruleTemp.toTime,toDayOfWeek:ruleTemp.toDayOfWeek,continuousDayRestHours:ruleTemp.continuousDayRestHours,name:ruleTemp.name,id:Id(ruleTemp),minimumRest:ruleTemp.minimumRest," +
            "timeLimit:ruleTemp.timeLimit,balanceType:ruleTemp.balanceType,templateType:ruleTemp.templateType,nightsWorked:ruleTemp.nightsWorked,description:ruleTemp.description," +
            " numberShiftsPerPeriod:ruleTemp.numberShiftsPerPeriod,numberOfWeeks:ruleTemp.numberOfWeeks,maximumVetoPercentage:ruleTemp.maximumVetoPercentage,maximumAvgTime:ruleTemp.maximumAvgTime," +
            "useShiftTimes:ruleTemp.useShiftTimes,balanceAdjustment:ruleTemp.balanceAdjustment,intervalLength:ruleTemp.intervalLength,intervalUnit:ruleTemp.intervalUnit,validationStartDateMillis:ruleTemp.validationStartDateMillis," +
            "daysWorked:ruleTemp.daysWorked,nightsWorked:ruleTemp.nightsWorked,description:ruleTemp.description,checkAgainstTimeRules:ruleTemp.checkAgainstTimeRules}) else [] END as ruleTemplates, wta.endDateMillis as endDateMillis,wta.startDateMillis as startDateMillis,wta.expiryDate as expiryDate,wta.description as description," +
            "expertise as expertise,wta.creationDate as creationDate, wta.endDate as endDate,wta.name as name,id(wta) as id LIMIT 1"
    )
    WTAResponseDTO getWTAByExpertiseAndCountry(Long expertiseId);


    @Query("match(wta:WorkingTimeAgreement{deleted:false,disabled:false})-[:" + HAS_WTA + "]-(organization:Organization) where id(organization)={0}\n" +
            "optional match(wta)-[:" + HAS_EXPERTISE_IN + "]->(expertise:Expertise)\n" +
            "optional match(wta)-[:" + HAS_RULE_TEMPLATE + "]->(ruleTemp:WTABaseRuleTemplate)-[:" + HAS_RULE_TEMPLATES + "]-(ruleTemplateCatg:RuleTemplateCategory)\n" +
            "RETURN CASE  WHEN ruleTemp IS NOT NULL THEN collect({disabled:ruleTemp.disabled,daysLimit:ruleTemp.daysLimit,ruleTemplateCategory:{name:ruleTemplateCatg.name,id:Id(ruleTemplateCatg)},fromDayOfWeek:ruleTemp.fromDayOfWeek," +
            "minimumDurationBetweenShifts:ruleTemp.minimumDurationBetweenShifts, fromTime:ruleTemp.fromTime,activityCode:ruleTemp.activityCode,onlyCompositeShifts:ruleTemp.onlyCompositeShifts," +
            "shiftsLimit:ruleTemp.shiftsLimit,shiftAffiliation:ruleTemp.shiftAffiliation,averageRest:ruleTemp.averageRest,continuousWeekRest:ruleTemp.continuousWeekRest,proportional:ruleTemp.proportional," +
            "toTime:ruleTemp.toTime,toDayOfWeek:ruleTemp.toDayOfWeek,continuousDayRestHours:ruleTemp.continuousDayRestHours,name:ruleTemp.name,id:Id(ruleTemp),minimumRest:ruleTemp.minimumRest," +
            "timeLimit:ruleTemp.timeLimit,balanceType:ruleTemp.balanceType,templateType:ruleTemp.templateType,nightsWorked:ruleTemp.nightsWorked,description:ruleTemp.description," +
            " numberShiftsPerPeriod:ruleTemp.numberShiftsPerPeriod,numberOfWeeks:ruleTemp.numberOfWeeks,maximumVetoPercentage:ruleTemp.maximumVetoPercentage,maximumAvgTime:ruleTemp.maximumAvgTime," +
            "useShiftTimes:ruleTemp.useShiftTimes,balanceAdjustment:ruleTemp.balanceAdjustment,intervalLength:ruleTemp.intervalLength,intervalUnit:ruleTemp.intervalUnit,validationStartDateMillis:ruleTemp.validationStartDateMillis," +
            "daysWorked:ruleTemp.daysWorked,nightsWorked:ruleTemp.nightsWorked,description:ruleTemp.description,checkAgainstTimeRules:ruleTemp.checkAgainstTimeRules}) else [] END as ruleTemplates, wta.endDateMillis as endDateMillis,wta.startDateMillis as startDateMillis,wta.expiryDate as expiryDate,wta.description as description," +
            "expertise as expertise,wta.creationDate as creationDate, wta.endDate as endDate,wta.name as name,id(wta) as id"
    )
    List<WTAResponseDTO> getWtaByOrganization(Long organizationId);

    @Query("match(wta:WorkingTimeAgreement{deleted:false})-[:" + BELONGS_TO_ORGANIZATION + "]->(organization:Organization)" +
            " where id(organization)={1} AND wta.name =~{0} AND id(wta)<>{2} return CASE  WHEN count(wta) >0   THEN true else false end")
    boolean checkUniqueWTANameInOrganization(String name, Long unitId, Long wtaId);

    @Query("match(organization:Organization)-[r:" + HAS_WTA + "]-(w:WorkingTimeAgreement) where Id(organization)={1} AND ID(w)={0} detach delete r set w.endDateMillis={2}")
    void removeOldWorkingTimeAgreement(Long wtaId, Long organizationId, Long endDateInMillis);

    @Query("match (countryWta:WorkingTimeAgreement{deleted:false})-[:" + BELONGS_TO + "]->(c:Country) where id(countryWta)={1} \n" +
            "match(countryWta)-[:" + HAS_COUNTRY_PARENT_WTA + "]-(organizationWta:WorkingTimeAgreement{deleted:false})-[:" + HAS_WTA + "]-(o:Organization) where id(o)={0} return organizationWta")
    WorkingTimeAgreement getOrganizationCopyOfWTA(Long unitId, Long wtaId);

    @Query("match(wta:WorkingTimeAgreement{deleted:false}) where id(wta)={0}\n" +
            "optional match(wta)-[:" + HAS_EXPERTISE_IN + "]->(expertise:Expertise)\n" +
            "optional match(wta)-[:" + HAS_RULE_TEMPLATE + "]->(ruleTemp:WTABaseRuleTemplate)-[:" + HAS_RULE_TEMPLATES + "]-(ruleTemplateCatg:RuleTemplateCategory)\n" +
            "RETURN CASE  WHEN ruleTemp IS NOT NULL THEN collect({disabled:ruleTemp.disabled,daysLimit:ruleTemp.daysLimit,ruleTemplateCategory:{name:ruleTemplateCatg.name,id:Id(ruleTemplateCatg)},fromDayOfWeek:ruleTemp.fromDayOfWeek," +
            "minimumDurationBetweenShifts:ruleTemp.minimumDurationBetweenShifts, fromTime:ruleTemp.fromTime,activityCode:ruleTemp.activityCode,onlyCompositeShifts:ruleTemp.onlyCompositeShifts," +
            "shiftsLimit:ruleTemp.shiftsLimit,shiftAffiliation:ruleTemp.shiftAffiliation,averageRest:ruleTemp.averageRest,continuousWeekRest:ruleTemp.continuousWeekRest,proportional:ruleTemp.proportional," +
            "toTime:ruleTemp.toTime,toDayOfWeek:ruleTemp.toDayOfWeek,continuousDayRestHours:ruleTemp.continuousDayRestHours,name:ruleTemp.name,id:Id(ruleTemp),minimumRest:ruleTemp.minimumRest," +
            "timeLimit:ruleTemp.timeLimit,balanceType:ruleTemp.balanceType,templateType:ruleTemp.templateType,nightsWorked:ruleTemp.nightsWorked,description:ruleTemp.description," +
            " numberShiftsPerPeriod:ruleTemp.numberShiftsPerPeriod,numberOfWeeks:ruleTemp.numberOfWeeks,maximumVetoPercentage:ruleTemp.maximumVetoPercentage,maximumAvgTime:ruleTemp.maximumAvgTime," +
            "useShiftTimes:ruleTemp.useShiftTimes,balanceAdjustment:ruleTemp.balanceAdjustment,intervalLength:ruleTemp.intervalLength,intervalUnit:ruleTemp.intervalUnit,validationStartDateMillis:ruleTemp.validationStartDateMillis," +
            "daysWorked:ruleTemp.daysWorked,nightsWorked:ruleTemp.nightsWorked,description:ruleTemp.description,checkAgainstTimeRules:ruleTemp.checkAgainstTimeRules}) else [] END as ruleTemplates, wta.endDateMillis as endDateMillis,wta.startDateMillis as startDateMillis,wta.expiryDate as expiryDate,wta.description as description," +
            "expertise as expertise,wta.creationDate as creationDate, wta.endDate as endDate,wta.name as name,id(wta) as id"
    )
    WTAResponseDTO getVersionOfWTA(Long organizationId);

    @Query("match (n:UnitEmploymentPosition)-[:" + HAS_WTA + "]-(wta:WorkingTimeAgreement) where id(n)={0}\n" +
            "optional match(wta)-[:" + HAS_EXPERTISE_IN + "]->(expertise:Expertise)\n" +
            "optional match(wta)-[:" + HAS_RULE_TEMPLATE + "]->(ruleTemp:WTABaseRuleTemplate)-[:" + HAS_RULE_TEMPLATES + "]-(ruleTemplateCatg:RuleTemplateCategory)\n" +
            "RETURN CASE  WHEN ruleTemp IS NOT NULL THEN collect({disabled:ruleTemp.disabled,daysLimit:ruleTemp.daysLimit,ruleTemplateCategory:{name:ruleTemplateCatg.name,id:Id(ruleTemplateCatg)},fromDayOfWeek:ruleTemp.fromDayOfWeek," +
            "minimumDurationBetweenShifts:ruleTemp.minimumDurationBetweenShifts, fromTime:ruleTemp.fromTime,activityCode:ruleTemp.activityCode,onlyCompositeShifts:ruleTemp.onlyCompositeShifts," +
            "shiftsLimit:ruleTemp.shiftsLimit,shiftAffiliation:ruleTemp.shiftAffiliation,averageRest:ruleTemp.averageRest,continuousWeekRest:ruleTemp.continuousWeekRest,proportional:ruleTemp.proportional," +
            "toTime:ruleTemp.toTime,toDayOfWeek:ruleTemp.toDayOfWeek,continuousDayRestHours:ruleTemp.continuousDayRestHours,name:ruleTemp.name,id:Id(ruleTemp),minimumRest:ruleTemp.minimumRest," +
            "timeLimit:ruleTemp.timeLimit,balanceType:ruleTemp.balanceType,templateType:ruleTemp.templateType,nightsWorked:ruleTemp.nightsWorked,description:ruleTemp.description," +
            " numberShiftsPerPeriod:ruleTemp.numberShiftsPerPeriod,numberOfWeeks:ruleTemp.numberOfWeeks,maximumVetoPercentage:ruleTemp.maximumVetoPercentage,maximumAvgTime:ruleTemp.maximumAvgTime," +
            "useShiftTimes:ruleTemp.useShiftTimes,balanceAdjustment:ruleTemp.balanceAdjustment,intervalLength:ruleTemp.intervalLength,intervalUnit:ruleTemp.intervalUnit,validationStartDateMillis:ruleTemp.validationStartDateMillis," +
            "daysWorked:ruleTemp.daysWorked,nightsWorked:ruleTemp.nightsWorked,description:ruleTemp.description,checkAgainstTimeRules:ruleTemp.checkAgainstTimeRules}) else [] END as ruleTemplates, wta.endDateMillis as endDateMillis,wta.startDateMillis as startDateMillis,wta.expiryDate as expiryDate,wta.description as description," +
            "expertise as expertise,wta.creationDate as creationDate, wta.endDate as endDate,wta.name as name,id(wta) as id"
    )
    WTAResponseDTO findWtaByUnitEmploymentPosition(Long unitEmploymentPositionId);

}
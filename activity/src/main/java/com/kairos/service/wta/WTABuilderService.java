package com.kairos.service.wta;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.dto.activity.wta.basic_details.WTABaseRuleTemplateDTO;
import com.kairos.dto.activity.wta.templates.ActivityCareDayCount;
import com.kairos.persistence.model.wta.WorkingTimeAgreement;
import com.kairos.persistence.model.wta.templates.WTABaseRuleTemplate;
import com.kairos.persistence.model.wta.templates.template_types.*;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.wta.rule_template.WTABaseRuleTemplateMongoRepository;
import com.kairos.service.MongoBaseService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author pradeep
 * @date - 13/4/18
 */

@Service
public class WTABuilderService extends MongoBaseService {

    @Inject
    private WTABaseRuleTemplateMongoRepository wtaBaseRuleTemplateMongoRepository;
    @Inject
    private ActivityMongoRepository activityMongoRepository;

    public List<WTABaseRuleTemplate> copyRuleTemplates(List<WTABaseRuleTemplateDTO> WTARuleTemplateDTOS, boolean ignoreId) {
        List<WTABaseRuleTemplate> wtaBaseRuleTemplates = new ArrayList<>();
        for (WTABaseRuleTemplateDTO ruleTemplate : WTARuleTemplateDTOS) {
            wtaBaseRuleTemplates.add(copyRuleTemplate(ruleTemplate, ignoreId));
        }
        return wtaBaseRuleTemplates;
    }

    public List<WTABaseRuleTemplate> copyRuleTemplatesWithUpdateActivity(Map<String,BigInteger> activitiesIdsAndUnitIdsMap,Long unitId,List<WTABaseRuleTemplateDTO> WTARuleTemplateDTOS, boolean ignoreId) {
        List<WTABaseRuleTemplate> wtaBaseRuleTemplates = new ArrayList<>();
        for (WTABaseRuleTemplateDTO ruleTemplate : WTARuleTemplateDTOS) {
            WTABaseRuleTemplate wtaBaseRuleTemplate = copyRuleTemplate(ruleTemplate, ignoreId);
            switch (ruleTemplate.getWtaTemplateType()) {
                case VETO_AND_STOP_BRICKS:
                    updateActivityInVetoAndStopBricks(activitiesIdsAndUnitIdsMap, unitId, (VetoAndStopBricksWTATemplate) wtaBaseRuleTemplate);
                    break;
                case SENIOR_DAYS_PER_YEAR:
                    updateActivityInSeniorDays(activitiesIdsAndUnitIdsMap, unitId, (SeniorDaysPerYearWTATemplate) wtaBaseRuleTemplate);
                    break;
                case CHILD_CARE_DAYS_CHECK:
                    updateActivityInChildCareDays(activitiesIdsAndUnitIdsMap, unitId, (ChildCareDaysCheckWTATemplate) wtaBaseRuleTemplate);
                    break;
                case WTA_FOR_CARE_DAYS:
                    updateActivityInWTACareDays(activitiesIdsAndUnitIdsMap, unitId, (WTAForCareDays) wtaBaseRuleTemplate);
                    break;
                case PROTECTED_DAYS_OFF:
                    updateActivityInProtectedDaysOff(activitiesIdsAndUnitIdsMap, unitId, (ProtectedDaysOffWTATemplate) wtaBaseRuleTemplate);
                    break;
                default:
                    break;
            }
            wtaBaseRuleTemplates.add(wtaBaseRuleTemplate);

        }
        return wtaBaseRuleTemplates;
    }

    private void updateActivityInChildCareDays(Map<String, BigInteger> activitiesIdsAndUnitIdsMap, Long unitId, ChildCareDaysCheckWTATemplate wtaBaseRuleTemplate) {
        List<BigInteger> activityIds;
        ChildCareDaysCheckWTATemplate childCareDaysCheckWTATemplate = wtaBaseRuleTemplate;
        activityIds = getActivityIdsByCountryActvityIds(activitiesIdsAndUnitIdsMap,unitId,childCareDaysCheckWTATemplate.getActivityIds());
        childCareDaysCheckWTATemplate.setActivityIds(activityIds);
    }

    private void updateActivityInSeniorDays(Map<String, BigInteger> activitiesIdsAndUnitIdsMap, Long unitId, SeniorDaysPerYearWTATemplate wtaBaseRuleTemplate) {
        List<BigInteger> activityIds;
        SeniorDaysPerYearWTATemplate seniorDaysPerYearWTATemplate = wtaBaseRuleTemplate;
        activityIds = getActivityIdsByCountryActvityIds(activitiesIdsAndUnitIdsMap,unitId,seniorDaysPerYearWTATemplate.getActivityIds());
        seniorDaysPerYearWTATemplate.setActivityIds(activityIds);
    }

    private void updateActivityInVetoAndStopBricks(Map<String, BigInteger> activitiesIdsAndUnitIdsMap, Long unitId, VetoAndStopBricksWTATemplate wtaBaseRuleTemplate) {
        VetoAndStopBricksWTATemplate vetoAndStopBricksWTATemplate = wtaBaseRuleTemplate;
        vetoAndStopBricksWTATemplate.setStopBrickActivityId(activitiesIdsAndUnitIdsMap.get(vetoAndStopBricksWTATemplate.getStopBrickActivityId()+"-"+unitId));
        vetoAndStopBricksWTATemplate.setVetoActivityId(activitiesIdsAndUnitIdsMap.get(vetoAndStopBricksWTATemplate.getVetoActivityId()+"-"+unitId));
    }

    private void updateActivityInProtectedDaysOff(Map<String, BigInteger> activitiesIdsAndUnitIdsMap, Long unitId, ProtectedDaysOffWTATemplate wtaBaseRuleTemplate) {
        List<BigInteger> activityIds;
        ProtectedDaysOffWTATemplate protectedDaysOffWTATemplate = wtaBaseRuleTemplate;
        activityIds = getActivityIdsByCountryActvityIds(activitiesIdsAndUnitIdsMap,unitId, Arrays.asList(protectedDaysOffWTATemplate.getActivityId()));
        protectedDaysOffWTATemplate.setActivityId(activityIds.get(0));
    }

    private void updateActivityInWTACareDays(Map<String, BigInteger> activitiesIdsAndUnitIdsMap, Long unitId, WTAForCareDays wtaBaseRuleTemplate) {
        WTAForCareDays wtaForCareDays = wtaBaseRuleTemplate;
        for (ActivityCareDayCount careDayCount : wtaForCareDays.getCareDayCounts()) {
            BigInteger activityId = activitiesIdsAndUnitIdsMap.get(careDayCount.getActivityId()+"-"+unitId);
            careDayCount.setActivityId(activityId);
        }
    }

    private List<BigInteger> getActivityIdsByCountryActvityIds(Map<String,BigInteger> activitiesIdsAndUnitIdsMap,Long unitId,List<BigInteger> activityIds){
        List<BigInteger> activityIdList = new ArrayList<>();
        for (BigInteger activityId : activityIds) {
            activityIdList.add(activitiesIdsAndUnitIdsMap.get(activityId+"-"+unitId));
        }
        return activityIdList;
    }

    public static WTABaseRuleTemplate copyRuleTemplate(WTABaseRuleTemplateDTO ruleTemplate, Boolean isIdnull) {
        WTABaseRuleTemplate wtaBaseRuleTemplate = null;
        switch (ruleTemplate.getWtaTemplateType()) {
            case SHIFT_LENGTH:
                wtaBaseRuleTemplate = ObjectMapperUtils.copyPropertiesOrCloneByMapper(ruleTemplate, ShiftLengthWTATemplate.class);
                break;
            case CONSECUTIVE_WORKING_PARTOFDAY:
                wtaBaseRuleTemplate = ObjectMapperUtils.copyPropertiesOrCloneByMapper(ruleTemplate, ConsecutiveWorkWTATemplate.class);
                break;
            case REST_IN_CONSECUTIVE_DAYS_AND_NIGHTS:
                wtaBaseRuleTemplate = ObjectMapperUtils.copyPropertiesOrCloneByMapper(ruleTemplate, ConsecutiveRestPartOfDayWTATemplate.class);
                break;
            case NUMBER_OF_PARTOFDAY:
                wtaBaseRuleTemplate = ObjectMapperUtils.copyPropertiesOrCloneByMapper(ruleTemplate, NumberOfPartOfDayShiftsWTATemplate.class);
                break;
            case DAYS_OFF_IN_PERIOD:
                wtaBaseRuleTemplate = ObjectMapperUtils.copyPropertiesOrCloneByMapper(ruleTemplate, DaysOffInPeriodWTATemplate.class);
                break;
            case AVERAGE_SHEDULED_TIME:
                wtaBaseRuleTemplate = ObjectMapperUtils.copyPropertiesOrCloneByMapper(ruleTemplate, AverageScheduledTimeWTATemplate.class);
                break;
            case VETO_AND_STOP_BRICKS:
                wtaBaseRuleTemplate = ObjectMapperUtils.copyPropertiesOrCloneByMapper(ruleTemplate, VetoAndStopBricksWTATemplate.class);
                break;
            case NUMBER_OF_WEEKEND_SHIFT_IN_PERIOD:
                wtaBaseRuleTemplate = ObjectMapperUtils.copyPropertiesOrCloneByMapper(ruleTemplate, NumberOfWeekendShiftsInPeriodWTATemplate.class);
                break;
            case DAILY_RESTING_TIME:
                wtaBaseRuleTemplate = ObjectMapperUtils.copyPropertiesOrCloneByMapper(ruleTemplate, DurationBetweenShiftsWTATemplate.class);
                break;
            case DURATION_BETWEEN_SHIFTS:
                wtaBaseRuleTemplate = ObjectMapperUtils.copyPropertiesOrCloneByMapper(ruleTemplate, DurationBetweenShiftsWTATemplate.class);
                break;
            case WEEKLY_REST_PERIOD:
                wtaBaseRuleTemplate = ObjectMapperUtils.copyPropertiesOrCloneByMapper(ruleTemplate, RestPeriodInAnIntervalWTATemplate.class);
                break;
            case SHORTEST_AND_AVERAGE_DAILY_REST:
                wtaBaseRuleTemplate = ObjectMapperUtils.copyPropertiesOrCloneByMapper(ruleTemplate, ShortestAndAverageDailyRestWTATemplate.class);
                break;
            case TIME_BANK:
                wtaBaseRuleTemplate = ObjectMapperUtils.copyPropertiesOrCloneByMapper(ruleTemplate, TimeBankWTATemplate.class);
                break;
            case SENIOR_DAYS_PER_YEAR:
                wtaBaseRuleTemplate = ObjectMapperUtils.copyPropertiesOrCloneByMapper(ruleTemplate, SeniorDaysPerYearWTATemplate.class);
                break;
            case CHILD_CARE_DAYS_CHECK:
                wtaBaseRuleTemplate = ObjectMapperUtils.copyPropertiesOrCloneByMapper(ruleTemplate, ChildCareDaysCheckWTATemplate.class);
                break;
            case DAYS_OFF_AFTER_A_SERIES:
                wtaBaseRuleTemplate = ObjectMapperUtils.copyPropertiesOrCloneByMapper(ruleTemplate, DaysOffAfterASeriesWTATemplate.class);
                break;
            case NO_OF_SEQUENCE_SHIFT:
                wtaBaseRuleTemplate = ObjectMapperUtils.copyPropertiesOrCloneByMapper(ruleTemplate, NoOfSequenceShiftWTATemplate.class);
                break;
            case EMPLOYEES_WITH_INCREASE_RISK:
                wtaBaseRuleTemplate = ObjectMapperUtils.copyPropertiesOrCloneByMapper(ruleTemplate, EmployeesWithIncreasedRiskWTATemplate.class);
                break;
            case WTA_FOR_CARE_DAYS:
                wtaBaseRuleTemplate = ObjectMapperUtils.copyPropertiesOrCloneByMapper(ruleTemplate, WTAForCareDays.class);
                break;
            case WTA_FOR_BREAKS_IN_SHIFT:
                wtaBaseRuleTemplate = ObjectMapperUtils.copyPropertiesOrCloneByMapper(ruleTemplate, BreakWTATemplate.class);
                break;
            case PROTECTED_DAYS_OFF:
                wtaBaseRuleTemplate = ObjectMapperUtils.copyPropertiesOrCloneByMapper(ruleTemplate, ProtectedDaysOffWTATemplate.class);
                break;
            default:
                throw new DataNotFoundByIdException("Invalid TEMPLATE");
        }
            wtaBaseRuleTemplate.setRuleTemplateCategoryId(ruleTemplate.getRuleTemplateCategoryId());
            if (isIdnull) {
                wtaBaseRuleTemplate.setId(null);
                wtaBaseRuleTemplate.setCountryId(null);
            }

        return wtaBaseRuleTemplate;
    }

    public static List<WTABaseRuleTemplateDTO> copyRuleTemplatesToDTO(List<WTABaseRuleTemplate> WTARuleTemplates) {
        List<WTABaseRuleTemplateDTO> wtaBaseRuleTemplates = new ArrayList<>();
        for (WTABaseRuleTemplate ruleTemplate : WTARuleTemplates) {
            wtaBaseRuleTemplates.add(ObjectMapperUtils.copyPropertiesOrCloneByMapper(ruleTemplate, WTABaseRuleTemplateDTO.class));
        }
        return wtaBaseRuleTemplates;
    }

    public WorkingTimeAgreement getWtaObject(WorkingTimeAgreement oldWta, WorkingTimeAgreement newWta) {
        newWta.setName(oldWta.getName());
        newWta.setDescription(oldWta.getDescription());
        newWta.setStartDate(oldWta.getStartDate());
        newWta.setEndDate(oldWta.getEndDate());
        newWta.setExpertise(oldWta.getExpertise());
        newWta.setId(null);
        return newWta;

    }

}

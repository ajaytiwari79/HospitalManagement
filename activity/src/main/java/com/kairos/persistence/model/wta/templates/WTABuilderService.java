package com.kairos.persistence.model.wta.templates;

import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.dto.activity.wta.basic_details.WTABaseRuleTemplateDTO;
import com.kairos.dto.activity.wta.basic_details.WTADTO;
import com.kairos.dto.activity.wta.basic_details.WTAResponseDTO;
import com.kairos.dto.activity.wta.templates.ActivityCareDayCount;
import com.kairos.persistence.model.wta.WorkingTimeAgreement;
import com.kairos.persistence.model.wta.templates.template_types.*;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.wta.rule_template.WTABaseRuleTemplateMongoRepository;
import com.kairos.service.MongoBaseService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.kairos.constants.AppConstants.COPY_OF;

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
        List<BigInteger> activityIds;
        for (WTABaseRuleTemplateDTO ruleTemplate : WTARuleTemplateDTOS) {
            WTABaseRuleTemplate wtaBaseRuleTemplate = copyRuleTemplate(ruleTemplate, ignoreId);
            switch (ruleTemplate.getWtaTemplateType()) {
                case VETO_AND_STOP_BRICKS:
                    VetoAndStopBricksWTATemplate vetoAndStopBricksWTATemplate = (VetoAndStopBricksWTATemplate)wtaBaseRuleTemplate;
                    vetoAndStopBricksWTATemplate.setStopBrickActivityId(activitiesIdsAndUnitIdsMap.get(vetoAndStopBricksWTATemplate.getStopBrickActivityId()+"-"+unitId));
                    vetoAndStopBricksWTATemplate.setVetoActivityId(activitiesIdsAndUnitIdsMap.get(vetoAndStopBricksWTATemplate.getVetoActivityId()+"-"+unitId));
                    break;
                case SENIOR_DAYS_PER_YEAR:
                    SeniorDaysPerYearWTATemplate seniorDaysPerYearWTATemplate = (SeniorDaysPerYearWTATemplate)wtaBaseRuleTemplate;
                    activityIds = getActivityIdsByCountryActvityIds(activitiesIdsAndUnitIdsMap,unitId,seniorDaysPerYearWTATemplate.getActivityIds());
                    seniorDaysPerYearWTATemplate.setActivityIds(activityIds);
                    break;
                case CHILD_CARE_DAYS_CHECK:
                    ChildCareDaysCheckWTATemplate childCareDaysCheckWTATemplate = (ChildCareDaysCheckWTATemplate)wtaBaseRuleTemplate;
                    activityIds = getActivityIdsByCountryActvityIds(activitiesIdsAndUnitIdsMap,unitId,childCareDaysCheckWTATemplate.getActivityIds());
                    childCareDaysCheckWTATemplate.setActivityIds(activityIds);
                    break;
                case WTA_FOR_CARE_DAYS:
                    WTAForCareDays wtaForCareDays = (WTAForCareDays)wtaBaseRuleTemplate;
                    for (ActivityCareDayCount careDayCount : wtaForCareDays.getCareDayCounts()) {
                        BigInteger activityId = activitiesIdsAndUnitIdsMap.get(careDayCount.getActivityId()+"-"+unitId);
                        careDayCount.setActivityId(activityId);
                    }
                    break;
            }
            wtaBaseRuleTemplates.add(wtaBaseRuleTemplate);

        }
        return wtaBaseRuleTemplates;
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
                wtaBaseRuleTemplate = ObjectMapperUtils.copyPropertiesByMapper(ruleTemplate, ShiftLengthWTATemplate.class);
                break;
            case CONSECUTIVE_WORKING_PARTOFDAY:
                wtaBaseRuleTemplate = ObjectMapperUtils.copyPropertiesByMapper(ruleTemplate, ConsecutiveWorkWTATemplate.class);
                break;

          /*  case CONSECUTIVE_NIGHTS_AND_DAYS:
                ConsecutiveRestPartOfDayWTATemplate consecutiveRestPartOfDayWTATemplate = new ConsecutiveRestPartOfDayWTATemplate();
                copyProperties(ruleTemplate,consecutiveRestPartOfDayWTATemplate);
                consecutiveRestPartOfDayWTATemplate.setRuleTemplateCategoryId((BigInteger) ruleTemplate.get("ruleTemplateCategoryId"));
                wtaBaseRuleTemplate = consecutiveRestPartOfDayWTATemplate;
                break;*/
            case REST_IN_CONSECUTIVE_DAYS_AND_NIGHTS:
                wtaBaseRuleTemplate = ObjectMapperUtils.copyPropertiesByMapper(ruleTemplate, ConsecutiveRestPartOfDayWTATemplate.class);
                break;
            case NUMBER_OF_PARTOFDAY:
                wtaBaseRuleTemplate = ObjectMapperUtils.copyPropertiesByMapper(ruleTemplate, NumberOfPartOfDayShiftsWTATemplate.class);
                break;
            case DAYS_OFF_IN_PERIOD:
                wtaBaseRuleTemplate = ObjectMapperUtils.copyPropertiesByMapper(ruleTemplate, DaysOffInPeriodWTATemplate.class);
                break;
            case AVERAGE_SHEDULED_TIME:
                wtaBaseRuleTemplate = ObjectMapperUtils.copyPropertiesByMapper(ruleTemplate, AverageScheduledTimeWTATemplate.class);
                break;
            case VETO_AND_STOP_BRICKS:
                wtaBaseRuleTemplate = ObjectMapperUtils.copyPropertiesByMapper(ruleTemplate, VetoAndStopBricksWTATemplate.class);
                break;
            case NUMBER_OF_WEEKEND_SHIFT_IN_PERIOD:
                wtaBaseRuleTemplate = ObjectMapperUtils.copyPropertiesByMapper(ruleTemplate, NumberOfWeekendShiftsInPeriodWTATemplate.class);
                break;
            /*case CARE_DAYS_CHECK:
                wtaBaseRuleTemplate = ObjectMapperUtils.copyPropertiesByMapper(ruleTemplate,ChildCareDayCheckWTATemplate.class);
                break;*/
            case DAILY_RESTING_TIME:
                wtaBaseRuleTemplate = ObjectMapperUtils.copyPropertiesByMapper(ruleTemplate, DurationBetweenShiftsWTATemplate.class);
                break;
            case DURATION_BETWEEN_SHIFTS:
                wtaBaseRuleTemplate = ObjectMapperUtils.copyPropertiesByMapper(ruleTemplate, DurationBetweenShiftsWTATemplate.class);
                break;
            case WEEKLY_REST_PERIOD:
                wtaBaseRuleTemplate = ObjectMapperUtils.copyPropertiesByMapper(ruleTemplate, RestPeriodInAnIntervalWTATemplate.class);
                break;
            case SHORTEST_AND_AVERAGE_DAILY_REST:
                wtaBaseRuleTemplate = ObjectMapperUtils.copyPropertiesByMapper(ruleTemplate, ShortestAndAverageDailyRestWTATemplate.class);
                break;
            case NUMBER_OF_SHIFTS_IN_INTERVAL:
                wtaBaseRuleTemplate = ObjectMapperUtils.copyPropertiesByMapper(ruleTemplate, ShiftsInIntervalWTATemplate.class);
                break;
            case TIME_BANK:
                wtaBaseRuleTemplate = ObjectMapperUtils.copyPropertiesByMapper(ruleTemplate, TimeBankWTATemplate.class);
                break;
            case SENIOR_DAYS_PER_YEAR:
                wtaBaseRuleTemplate = ObjectMapperUtils.copyPropertiesByMapper(ruleTemplate, SeniorDaysPerYearWTATemplate.class);
                break;
            case CHILD_CARE_DAYS_CHECK:
                wtaBaseRuleTemplate = ObjectMapperUtils.copyPropertiesByMapper(ruleTemplate, ChildCareDaysCheckWTATemplate.class);
                break;
            case DAYS_OFF_AFTER_A_SERIES:
                wtaBaseRuleTemplate = ObjectMapperUtils.copyPropertiesByMapper(ruleTemplate, DaysOffAfterASeriesWTATemplate.class);
                break;
            case NO_OF_SEQUENCE_SHIFT:
                wtaBaseRuleTemplate = ObjectMapperUtils.copyPropertiesByMapper(ruleTemplate, NoOfSequenceShiftWTATemplate.class);
                break;
            case EMPLOYEES_WITH_INCREASE_RISK:
                wtaBaseRuleTemplate = ObjectMapperUtils.copyPropertiesByMapper(ruleTemplate, EmployeesWithIncreasedRiskWTATemplate.class);
                break;
            case WTA_FOR_CARE_DAYS:
                wtaBaseRuleTemplate = ObjectMapperUtils.copyPropertiesByMapper(ruleTemplate, WTAForCareDays.class);
                break;
            case WTA_FOR_BREAKS_IN_SHIFT:
                wtaBaseRuleTemplate = ObjectMapperUtils.copyPropertiesByMapper(ruleTemplate, BreakWTATemplate.class);
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
            wtaBaseRuleTemplates.add(ObjectMapperUtils.copyPropertiesByMapper(ruleTemplate, WTABaseRuleTemplateDTO.class));
        }
        //List<WTABaseRuleTemplateDTO> wtaBaseRuleTemplates  = ObjectMapperUtils.copyPropertiesByMapper(WTARuleTemplates,new WTABaseRuleTemplateDTO());
        return wtaBaseRuleTemplates;
    }

    public void copyRuleTemplateToNewWTA(WorkingTimeAgreement oldWta, WorkingTimeAgreement newWTA) {
        List<WTABaseRuleTemplate> wtaBaseRuleTemplates = (List<WTABaseRuleTemplate>) wtaBaseRuleTemplateMongoRepository.findAllById(oldWta.getRuleTemplateIds());
        List<WTABaseRuleTemplate> wtaBaseRuleTemplates1 = new ArrayList<>();
        // copyProperties(wtaBaseRuleTemplates,wtaBaseRuleTemplates1);
        if(!wtaBaseRuleTemplates1.isEmpty()){
            save(wtaBaseRuleTemplates1);
        }

    }


    public WorkingTimeAgreement copyWta(WorkingTimeAgreement oldWta, WorkingTimeAgreement newWta) {
        newWta.setName(COPY_OF + oldWta.getName());
        newWta.setDescription(oldWta.getDescription());
        newWta.setStartDate(oldWta.getStartDate());
        newWta.setEndDate(oldWta.getEndDate());
        newWta.setExpertise(oldWta.getExpertise());
        newWta.setId(null);
        return newWta;
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
    public WTAResponseDTO prepareWtaWhileUpdate(WorkingTimeAgreement oldWta, WTADTO updateDTO) {
        List<WTABaseRuleTemplate> ruleTemplates = new ArrayList<>();
        if (DateUtils.getLocalDateFromDate(oldWta.getStartDate()).isEqual(updateDTO.getStartDate()) && (updateDTO.getStartDate().isAfter(DateUtils.getCurrentLocalDate()) || updateDTO.getStartDate().isEqual(DateUtils.getCurrentLocalDate())) && !updateDTO.getRuleTemplates().isEmpty())
        {
            ruleTemplates = updateRuleTemplatesAndSave(updateDTO.getRuleTemplates(), oldWta.getRuleTemplateIds());
        } else{
            WorkingTimeAgreement versionWTA = ObjectMapperUtils.copyPropertiesByMapper(oldWta, WorkingTimeAgreement.class);
            versionWTA.setId(null);
            versionWTA.setDeleted(false);
            versionWTA.setStartDate(oldWta.getStartDate());
            versionWTA.setEndDate(new Date(updateDTO.getStartDateMillis()));
            versionWTA.setCountryParentWTA(null);
            ruleTemplates = copyRuleTemplates(updateDTO.getRuleTemplates(), true);
            if (!ruleTemplates.isEmpty()) {
                save(ruleTemplates);
            }
            save(versionWTA);
            oldWta.setParentId(versionWTA.getId());
        }
        oldWta.setDescription(updateDTO.getDescription());
        oldWta.setName(updateDTO.getName());
        oldWta.setStartDate(new Date(updateDTO.getStartDateMillis()));
        if (updateDTO.getEndDate() != null) {
            oldWta.setEndDate(new Date(updateDTO.getEndDateMillis()));
        }else {
            oldWta.setEndDate(null);
        }
        oldWta.setRuleTemplateIds(ruleTemplates.stream().map(ruleTemplate -> ruleTemplate.getId()).collect(Collectors.toList()));
        save(oldWta);
        WTAResponseDTO wtaResponseDTO = ObjectMapperUtils.copyPropertiesByMapper(oldWta, WTAResponseDTO.class);
        wtaResponseDTO.setRuleTemplates(WTABuilderService.copyRuleTemplatesToDTO(ruleTemplates));
        return wtaResponseDTO;
    }

    public List<WTABaseRuleTemplate> updateRuleTemplatesAndSave(List<WTABaseRuleTemplateDTO> newRuleTemplatesToBeLinked, List<BigInteger> oldRuleTemplatesId) {
        List<WTABaseRuleTemplateDTO> oldRuleTemplates = wtaBaseRuleTemplateMongoRepository.findAllByIdIn(oldRuleTemplatesId);
        List<WTABaseRuleTemplate> ruleTemplates = new ArrayList<>();
        for (WTABaseRuleTemplateDTO currentRuleTemplateToBeLinked : newRuleTemplatesToBeLinked) {
            WTABaseRuleTemplateDTO existingWtaRuleTemplate = oldRuleTemplates.stream().filter(wtaBaseRuleTemplate -> wtaBaseRuleTemplate.getId().equals(currentRuleTemplateToBeLinked.getId())).findAny().orElse(null);

            if (existingWtaRuleTemplate != null) {// existing rule template found so we need to update in same
                ruleTemplates.add(copyRuleTemplate(currentRuleTemplateToBeLinked, false));
            } else {
                ruleTemplates.add(copyRuleTemplate(currentRuleTemplateToBeLinked, true));
            }
        }
        if (!ruleTemplates.isEmpty()) {
            save(ruleTemplates);
        }

        return ruleTemplates;
    }


}

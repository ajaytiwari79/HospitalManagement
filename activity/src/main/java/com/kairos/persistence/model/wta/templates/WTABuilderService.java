package com.kairos.persistence.model.wta.templates;

import com.kairos.dto.activity.wta.basic_details.WTABaseRuleTemplateDTO;
import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.persistence.model.wta.WorkingTimeAgreement;
import com.kairos.persistence.model.wta.templates.template_types.*;
import com.kairos.persistence.repository.wta.rule_template.WTABaseRuleTemplateMongoRepository;
import com.kairos.service.MongoBaseService;
import com.kairos.commons.utils.ObjectMapperUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static com.kairos.constants.AppConstants.COPY_OF;

/**
 * @author pradeep
 * @date - 13/4/18
 */

@Service
public class WTABuilderService extends MongoBaseService {

    @Inject
    private WTABaseRuleTemplateMongoRepository wtaBaseRuleTemplateMongoRepository;

    public List<WTABaseRuleTemplate> copyRuleTemplates(List<WTABaseRuleTemplateDTO> WTARuleTemplateDTOS, boolean ignoreId) {
        List<WTABaseRuleTemplate> wtaBaseRuleTemplates = new ArrayList<>();
        for (WTABaseRuleTemplateDTO ruleTemplate : WTARuleTemplateDTOS) {
            wtaBaseRuleTemplates.add(copyRuleTemplate(ruleTemplate, ignoreId));

        }
        return wtaBaseRuleTemplates;
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
            case VETO_PER_PERIOD:
                wtaBaseRuleTemplate = ObjectMapperUtils.copyPropertiesByMapper(ruleTemplate, VetoPerPeriodWTATemplate.class);
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


}

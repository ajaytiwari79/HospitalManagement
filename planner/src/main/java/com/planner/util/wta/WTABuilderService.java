package com.planner.util.wta;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.wta.basic_details.WTABaseRuleTemplateDTO;
import com.planner.domain.wta.templates.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
public class WTABuilderService {


    public static WTABaseRuleTemplate copyRuleTemplate(WTABaseRuleTemplateDTO ruleTemplate) {
        WTABaseRuleTemplate wtaBaseRuleTemplate = new WTABaseRuleTemplate();
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
                wtaBaseRuleTemplate = ObjectMapperUtils.copyPropertiesOrCloneByMapper(ruleTemplate, VetoPerPeriodWTATemplate.class);
                break;
            case NUMBER_OF_WEEKEND_SHIFT_IN_PERIOD:
                wtaBaseRuleTemplate = ObjectMapperUtils.copyPropertiesOrCloneByMapper(ruleTemplate, NumberOfWeekendShiftsInPeriodWTATemplate.class);
                break;
            /*case CARE_DAYS_CHECK:
                wtaBaseRuleTemplate = ObjectMapperUtils.copyPropertiesOrCloneByMapper(ruleTemplate,ChildCareDayCheckWTATemplate.class);
                break;*/
            case DAILY_RESTING_TIME:
              //  wtaBaseRuleTemplate = ObjectMapperUtils.copyPropertiesOrCloneByMapper(ruleTemplate, DailyRestingTimeWTATemplate.class);
                break;
            case DURATION_BETWEEN_SHIFTS:
                wtaBaseRuleTemplate = ObjectMapperUtils.copyPropertiesOrCloneByMapper(ruleTemplate, DurationBetweenShiftsWTATemplate.class);
                break;
            case WEEKLY_REST_PERIOD:
               // wtaBaseRuleTemplate = ObjectMapperUtils.copyPropertiesOrCloneByMapper(ruleTemplate, WeeklyRestPeriodWTATemplate.class);
                break;
            case SHORTEST_AND_AVERAGE_DAILY_REST:
                wtaBaseRuleTemplate = ObjectMapperUtils.copyPropertiesOrCloneByMapper(ruleTemplate, ShortestAndAverageDailyRestWTATemplate.class);
                break;
            case NUMBER_OF_SHIFTS_IN_INTERVAL:
                wtaBaseRuleTemplate = ObjectMapperUtils.copyPropertiesOrCloneByMapper(ruleTemplate, ShiftsInIntervalWTATemplate.class);
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
            default:
                throw new IllegalStateException("Invalid TEMPLATE");
        }
        //wtaBaseRuleTemplate.setKairosId(ruleTemplate.getId());
        return wtaBaseRuleTemplate;
    }

    public static List<WTABaseRuleTemplate> copyRuleTemplates(List<WTABaseRuleTemplateDTO> WTARuleTemplateDTOS) {
        List<WTABaseRuleTemplate> wtaBaseRuleTemplates = new ArrayList<>();
        for (WTABaseRuleTemplateDTO ruleTemplate : WTARuleTemplateDTOS) {
            wtaBaseRuleTemplates.add(copyRuleTemplate(ruleTemplate));

        }
        return wtaBaseRuleTemplates;
    }


}

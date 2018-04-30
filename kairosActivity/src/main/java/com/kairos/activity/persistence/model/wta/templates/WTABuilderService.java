package com.kairos.activity.persistence.model.wta.templates;

import com.kairos.activity.custom_exception.DataNotFoundByIdException;
import com.kairos.activity.persistence.enums.WTATemplateType;
import com.kairos.activity.persistence.model.wta.WTAQueryResultDTO;
import com.kairos.activity.persistence.model.wta.WorkingTimeAgreement;
import com.kairos.activity.persistence.model.wta.templates.template_types.*;
import com.kairos.activity.persistence.repository.wta.WTABaseRuleTemplateMongoRepository;
import com.kairos.activity.service.MongoBaseService;
import com.kairos.activity.util.ObjectMapperUtils;
import com.kairos.response.dto.web.wta.*;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.kairos.activity.constants.AppConstants.COPY_OF;
import static com.kairos.activity.persistence.enums.WTATemplateType.*;

/**
 * @author pradeep
 * @date - 13/4/18
 */

@Service
public class WTABuilderService extends MongoBaseService {

    @Inject private WTABaseRuleTemplateMongoRepository wtaBaseRuleTemplateMongoRepository;

    public  List<WTABaseRuleTemplate> copyRuleTemplates(List<WTABaseRuleTemplateDTO> WTARuleTemplateDTOS,boolean ignoreId) {
        List<WTABaseRuleTemplate> wtaBaseRuleTemplates = new ArrayList<>();
        for (WTABaseRuleTemplateDTO ruleTemplate : WTARuleTemplateDTOS) {
            wtaBaseRuleTemplates.add(copyRuleTemplate(ruleTemplate,ignoreId));

        }
        return wtaBaseRuleTemplates;
    }

    public static WTABaseRuleTemplate copyRuleTemplate(WTABaseRuleTemplateDTO ruleTemplate,Boolean isIdnull){
        WTABaseRuleTemplate wtaBaseRuleTemplate = new WTABaseRuleTemplate();
        switch (ruleTemplate.getWtaTemplateType()) {
            case SHIFT_LENGTH:
                ShiftLengthWTATemplate shiftLengthWTATemplate = new ShiftLengthWTATemplate();
                copyProperties(ruleTemplate,shiftLengthWTATemplate);
                shiftLengthWTATemplate.setRuleTemplateCategoryId(ruleTemplate.getRuleTemplateCategory().getId());
                shiftLengthWTATemplate.setId(null);
                wtaBaseRuleTemplate = shiftLengthWTATemplate;
                break;
            case CONSECUTIVE_WORKING_PARTOFDAY:
                ConsecutiveWorkWTATemplate consecutiveWorkWTATemplate = new ConsecutiveWorkWTATemplate();
                copyProperties(ruleTemplate,consecutiveWorkWTATemplate);
                consecutiveWorkWTATemplate.setRuleTemplateCategoryId(ruleTemplate.getRuleTemplateCategory().getId());
                consecutiveWorkWTATemplate.setId(null);
                wtaBaseRuleTemplate = consecutiveWorkWTATemplate;
                break;

         /*   case CONSECUTIVE_NIGHTS_AND_DAYS:
                ConsecutiveRestPartOfDayWTATemplate consecutiveRestPartOfDayWTATemplate = new ConsecutiveRestPartOfDayWTATemplate();
                copyProperties(ruleTemplate,consecutiveRestPartOfDayWTATemplate);
                consecutiveRestPartOfDayWTATemplate.setRuleTemplateCategoryId(ruleTemplate.getRuleTemplateCategory().getId());
                consecutiveRestPartOfDayWTATemplate.setId(null);
                wtaBaseRuleTemplate = consecutiveRestPartOfDayWTATemplate;
                break;*/
            case REST_IN_CONSECUTIVE_DAYS_AND_NIGHTS:
                ConsecutiveRestPartOfDayWTATemplate consecutiveRestPartOfDayWTATemplate1 = new ConsecutiveRestPartOfDayWTATemplate();
                copyProperties(ruleTemplate,consecutiveRestPartOfDayWTATemplate1);
                consecutiveRestPartOfDayWTATemplate1.setRuleTemplateCategoryId(ruleTemplate.getRuleTemplateCategory().getId());
                consecutiveRestPartOfDayWTATemplate1.setId(null);
                wtaBaseRuleTemplate = consecutiveRestPartOfDayWTATemplate1;
                break;
            case NUMBER_OF_PARTOFDAY:
                NumberOfPartOfDayShiftsWTATemplate numberOfPartOfDayShiftsWTATemplate = new NumberOfPartOfDayShiftsWTATemplate();
                copyProperties(ruleTemplate,numberOfPartOfDayShiftsWTATemplate);
                numberOfPartOfDayShiftsWTATemplate.setRuleTemplateCategoryId(ruleTemplate.getRuleTemplateCategory().getId());
                numberOfPartOfDayShiftsWTATemplate.setId(null);
                wtaBaseRuleTemplate = numberOfPartOfDayShiftsWTATemplate;
                break;
            case DAYS_OFF_IN_PERIOD:
                DaysOffInPeriodWTATemplate daysOffInPeriodWTATemplate = new DaysOffInPeriodWTATemplate();
                copyProperties(ruleTemplate,daysOffInPeriodWTATemplate);
                daysOffInPeriodWTATemplate.setRuleTemplateCategoryId(ruleTemplate.getRuleTemplateCategory().getId());
                daysOffInPeriodWTATemplate.setId(null);
                wtaBaseRuleTemplate = daysOffInPeriodWTATemplate;
                break;
            case AVERAGE_SHEDULED_TIME:
                AverageScheduledTimeWTATemplate averageScheduledTimeWTATemplate = new AverageScheduledTimeWTATemplate();
                copyProperties(ruleTemplate,averageScheduledTimeWTATemplate);
                averageScheduledTimeWTATemplate.setRuleTemplateCategoryId(ruleTemplate.getRuleTemplateCategory().getId());
                averageScheduledTimeWTATemplate.setId(null);
                wtaBaseRuleTemplate = averageScheduledTimeWTATemplate;
                break;
            case VETO_PER_PERIOD:
                VetoPerPeriodWTATemplate vetoPerPeriodWTATemplate = new VetoPerPeriodWTATemplate();
                copyProperties(ruleTemplate,vetoPerPeriodWTATemplate);
                vetoPerPeriodWTATemplate.setRuleTemplateCategoryId(ruleTemplate.getRuleTemplateCategory().getId());
                vetoPerPeriodWTATemplate.setId(null);
                wtaBaseRuleTemplate = vetoPerPeriodWTATemplate;
                break;
            case NUMBER_OF_WEEKEND_SHIFT_IN_PERIOD:
                NumberOfWeekendShiftsInPeriodWTATemplate numberOfWeekendShiftsInPeriodWTATemplate = new NumberOfWeekendShiftsInPeriodWTATemplate();
                copyProperties(ruleTemplate,numberOfWeekendShiftsInPeriodWTATemplate);
                numberOfWeekendShiftsInPeriodWTATemplate.setRuleTemplateCategoryId(ruleTemplate.getRuleTemplateCategory().getId());
                numberOfWeekendShiftsInPeriodWTATemplate.setId(null);
                wtaBaseRuleTemplate = numberOfWeekendShiftsInPeriodWTATemplate;
                break;
            case CARE_DAYS_CHECK:
                CareDayCheckWTATemplate careDayCheckWTATemplate = new CareDayCheckWTATemplate();
                copyProperties(ruleTemplate,careDayCheckWTATemplate);
                careDayCheckWTATemplate.setRuleTemplateCategoryId(ruleTemplate.getRuleTemplateCategory().getId());
                careDayCheckWTATemplate.setId(null);
                wtaBaseRuleTemplate = careDayCheckWTATemplate;
                break;
            case DAILY_RESTING_TIME:
                DailyRestingTimeWTATemplate dailyRestingTimeWTATemplate = new DailyRestingTimeWTATemplate();
                copyProperties(ruleTemplate,dailyRestingTimeWTATemplate);
                dailyRestingTimeWTATemplate.setRuleTemplateCategoryId(ruleTemplate.getRuleTemplateCategory().getId());
                dailyRestingTimeWTATemplate.setId(null);
                wtaBaseRuleTemplate = dailyRestingTimeWTATemplate;
                break;
            case DURATION_BETWEEN_SHIFTS:
                DurationBetweenShiftsWTATemplate durationBetweenShiftsWTATemplate = new DurationBetweenShiftsWTATemplate();
                copyProperties(ruleTemplate,durationBetweenShiftsWTATemplate);
                durationBetweenShiftsWTATemplate.setRuleTemplateCategoryId(ruleTemplate.getRuleTemplateCategory().getId());
                durationBetweenShiftsWTATemplate.setId(null);
                wtaBaseRuleTemplate = durationBetweenShiftsWTATemplate;
                break;
            case WEEKLY_REST_PERIOD:
                WeeklyRestPeriodWTATemplate weeklyRestPeriodWTATemplate = new WeeklyRestPeriodWTATemplate();
                copyProperties(ruleTemplate,weeklyRestPeriodWTATemplate);
                weeklyRestPeriodWTATemplate.setRuleTemplateCategoryId(ruleTemplate.getRuleTemplateCategory().getId());
                weeklyRestPeriodWTATemplate.setId(null);
                wtaBaseRuleTemplate = weeklyRestPeriodWTATemplate;
                break;
            case SHORTEST_AND_AVERAGE_DAILY_REST:
                ShortestAndAverageDailyRestWTATemplate shortestAndAverageDailyRestWTATemplate = new ShortestAndAverageDailyRestWTATemplate();
                copyProperties(ruleTemplate,shortestAndAverageDailyRestWTATemplate);
                shortestAndAverageDailyRestWTATemplate.setRuleTemplateCategoryId(ruleTemplate.getRuleTemplateCategory().getId());
                shortestAndAverageDailyRestWTATemplate.setId(null);
                wtaBaseRuleTemplate = shortestAndAverageDailyRestWTATemplate;
                break;
            case NUMBER_OF_SHIFTS_IN_INTERVAL:
                ShiftsInIntervalWTATemplate shiftsInIntervalWTATemplate = new ShiftsInIntervalWTATemplate();
                copyProperties(ruleTemplate,shiftsInIntervalWTATemplate);
                shiftsInIntervalWTATemplate.setRuleTemplateCategoryId(ruleTemplate.getRuleTemplateCategory().getId());
                shiftsInIntervalWTATemplate.setId(null);
                wtaBaseRuleTemplate = shiftsInIntervalWTATemplate;
                break;
            case MAXIMUM_SENIOR_DAYS_IN_YEAR:
                SeniorDaysInYearWTATemplate seniorDaysInYearWTATemplate = new SeniorDaysInYearWTATemplate();
                copyProperties(ruleTemplate,seniorDaysInYearWTATemplate);
                seniorDaysInYearWTATemplate.setRuleTemplateCategoryId(ruleTemplate.getRuleTemplateCategory().getId());
                seniorDaysInYearWTATemplate.setId(null);
                wtaBaseRuleTemplate = seniorDaysInYearWTATemplate;
                break;
            case TIME_BANK:
                TimeBankWTATemplate timeBankWTATemplate = new TimeBankWTATemplate();
                copyProperties(ruleTemplate,timeBankWTATemplate);
                timeBankWTATemplate.setRuleTemplateCategoryId(ruleTemplate.getRuleTemplateCategory().getId());
                timeBankWTATemplate.setId(null);
                wtaBaseRuleTemplate = timeBankWTATemplate;
                break;

            default:
                throw new DataNotFoundByIdException("Invalid TEMPLATE");
        }
        return wtaBaseRuleTemplate;
    }

    public static List<WTABaseRuleTemplateDTO> copyRuleTemplatesToDTO(List<WTABaseRuleTemplate> WTARuleTemplates) {
        List<WTABaseRuleTemplateDTO> wtaBaseRuleTemplates = new ArrayList<>();
        for (WTABaseRuleTemplate ruleTemplate : WTARuleTemplates) {
            wtaBaseRuleTemplates.add(copyRuleTemplateToDTO(ruleTemplate));

        }
        return wtaBaseRuleTemplates;
    }


    public static WTABaseRuleTemplate copyDTOToRuleTemplate(WTABaseRuleTemplateDTO ruleTemplate){
        WTABaseRuleTemplate wtaBaseRuleTemplate = new WTABaseRuleTemplate();
        switch (ruleTemplate.getWtaTemplateType()) {
            case SHIFT_LENGTH:
                ShiftLengthWTATemplate shiftLengthWTATemplate = new ShiftLengthWTATemplate();
                copyProperties((ShiftLengthWTATemplateDTO)ruleTemplate,shiftLengthWTATemplate);
                shiftLengthWTATemplate.setRuleTemplateCategoryId(ruleTemplate.getRuleTemplateCategory().getId());
                wtaBaseRuleTemplate = shiftLengthWTATemplate;
                break;
            case CONSECUTIVE_WORKING_PARTOFDAY:
                ConsecutiveWorkWTATemplate consecutiveWorkWTATemplate = new ConsecutiveWorkWTATemplate();
                copyProperties((ConsecutiveWorkWTATemplateDTO)ruleTemplate,consecutiveWorkWTATemplate);
                consecutiveWorkWTATemplate.setRuleTemplateCategoryId(ruleTemplate.getRuleTemplateCategory().getId());
                wtaBaseRuleTemplate = consecutiveWorkWTATemplate;
                break;

            /*case CONSECUTIVE_NIGHTS_AND_DAYS:
                ConsecutiveRestPartOfDayWTATemplate consecutiveRestPartOfDayWTATemplate = new ConsecutiveRestPartOfDayWTATemplate();
                copyProperties(ruleTemplate,consecutiveRestPartOfDayWTATemplate);
                consecutiveRestPartOfDayWTATemplate.setRuleTemplateCategoryId(ruleTemplate.getRuleTemplateCategory().getId());
                wtaBaseRuleTemplate = consecutiveRestPartOfDayWTATemplate;
                break;*/
            case REST_IN_CONSECUTIVE_DAYS_AND_NIGHTS:
                ConsecutiveRestPartOfDayWTATemplate consecutiveRestPartOfDayWTATemplate1 = new ConsecutiveRestPartOfDayWTATemplate();
                copyProperties((ConsecutiveRestPartOfDayWTATemplateDTO)ruleTemplate,consecutiveRestPartOfDayWTATemplate1);
                consecutiveRestPartOfDayWTATemplate1.setRuleTemplateCategoryId(ruleTemplate.getRuleTemplateCategory().getId());
                wtaBaseRuleTemplate = consecutiveRestPartOfDayWTATemplate1;
                break;
            case NUMBER_OF_PARTOFDAY:
                NumberOfPartOfDayShiftsWTATemplate numberOfPartOfDayShiftsWTATemplate = new NumberOfPartOfDayShiftsWTATemplate();
                copyProperties((NumberOfPartOfDayShiftsWTATemplateDTO)ruleTemplate,numberOfPartOfDayShiftsWTATemplate);
                numberOfPartOfDayShiftsWTATemplate.setRuleTemplateCategoryId(ruleTemplate.getRuleTemplateCategory().getId());
                wtaBaseRuleTemplate = numberOfPartOfDayShiftsWTATemplate;
                break;
            case DAYS_OFF_IN_PERIOD:
                DaysOffInPeriodWTATemplate daysOffInPeriodWTATemplate = new DaysOffInPeriodWTATemplate();
                copyProperties((DaysOffInPeriodWTATemplateDTO)ruleTemplate,daysOffInPeriodWTATemplate);
                daysOffInPeriodWTATemplate.setRuleTemplateCategoryId(ruleTemplate.getRuleTemplateCategory().getId());
                wtaBaseRuleTemplate = daysOffInPeriodWTATemplate;
                break;
            case AVERAGE_SHEDULED_TIME:
                AverageScheduledTimeWTATemplate averageScheduledTimeWTATemplate = new AverageScheduledTimeWTATemplate();
                copyProperties((AverageScheduledTimeWTATemplateDTO)ruleTemplate,averageScheduledTimeWTATemplate);
                averageScheduledTimeWTATemplate.setRuleTemplateCategoryId(ruleTemplate.getRuleTemplateCategory().getId());
                wtaBaseRuleTemplate = averageScheduledTimeWTATemplate;
                break;
            case VETO_PER_PERIOD:
                VetoPerPeriodWTATemplate vetoPerPeriodWTATemplate = new VetoPerPeriodWTATemplate();
                copyProperties((VetoPerPeriodWTATemplateDTO)ruleTemplate,vetoPerPeriodWTATemplate);
                vetoPerPeriodWTATemplate.setRuleTemplateCategoryId(ruleTemplate.getRuleTemplateCategory().getId());
                wtaBaseRuleTemplate = vetoPerPeriodWTATemplate;
                break;
            case NUMBER_OF_WEEKEND_SHIFT_IN_PERIOD:
                NumberOfWeekendShiftsInPeriodWTATemplate numberOfWeekendShiftsInPeriodWTATemplate = new NumberOfWeekendShiftsInPeriodWTATemplate();
                copyProperties((NumberOfWeekendShiftsInPeriodWTATemplateDTO)ruleTemplate,numberOfWeekendShiftsInPeriodWTATemplate);
                numberOfWeekendShiftsInPeriodWTATemplate.setRuleTemplateCategoryId(ruleTemplate.getRuleTemplateCategory().getId());
                wtaBaseRuleTemplate = numberOfWeekendShiftsInPeriodWTATemplate;
                break;
            case CARE_DAYS_CHECK:
                CareDayCheckWTATemplate careDayCheckWTATemplate = new CareDayCheckWTATemplate();
                copyProperties((CareDayCheckWTATemplateDTO)ruleTemplate,careDayCheckWTATemplate);
                careDayCheckWTATemplate.setRuleTemplateCategoryId(ruleTemplate.getRuleTemplateCategory().getId());
                wtaBaseRuleTemplate = careDayCheckWTATemplate;
                break;
            case DAILY_RESTING_TIME:
                DailyRestingTimeWTATemplate dailyRestingTimeWTATemplate = new DailyRestingTimeWTATemplate();
                copyProperties((DailyRestingTimeWTATemplateDTO)ruleTemplate,dailyRestingTimeWTATemplate);
                dailyRestingTimeWTATemplate.setRuleTemplateCategoryId(ruleTemplate.getRuleTemplateCategory().getId());
                wtaBaseRuleTemplate = dailyRestingTimeWTATemplate;
                break;
            case DURATION_BETWEEN_SHIFTS:
                DurationBetweenShiftsWTATemplate durationBetweenShiftsWTATemplate = new DurationBetweenShiftsWTATemplate();
                copyProperties((DurationBetweenShiftsWTATemplateDTO)ruleTemplate,durationBetweenShiftsWTATemplate);
                durationBetweenShiftsWTATemplate.setRuleTemplateCategoryId(ruleTemplate.getRuleTemplateCategory().getId());
                wtaBaseRuleTemplate = durationBetweenShiftsWTATemplate;
                break;
            case WEEKLY_REST_PERIOD:
                WeeklyRestPeriodWTATemplate weeklyRestPeriodWTATemplate = new WeeklyRestPeriodWTATemplate();
                copyProperties((WeeklyRestPeriodWTATemplateDTO)ruleTemplate,weeklyRestPeriodWTATemplate);
                weeklyRestPeriodWTATemplate.setRuleTemplateCategoryId(ruleTemplate.getRuleTemplateCategory().getId());
                wtaBaseRuleTemplate = weeklyRestPeriodWTATemplate;
                break;
            case SHORTEST_AND_AVERAGE_DAILY_REST:
                ShortestAndAverageDailyRestWTATemplate shortestAndAverageDailyRestWTATemplate = new ShortestAndAverageDailyRestWTATemplate();
                copyProperties((ShortestAndAverageDailyRestWTATemplateDTO)ruleTemplate,shortestAndAverageDailyRestWTATemplate);
                shortestAndAverageDailyRestWTATemplate.setRuleTemplateCategoryId(ruleTemplate.getRuleTemplateCategory().getId());
                wtaBaseRuleTemplate = shortestAndAverageDailyRestWTATemplate;
                break;
            case NUMBER_OF_SHIFTS_IN_INTERVAL:
                ShiftsInIntervalWTATemplate shiftsInIntervalWTATemplate = new ShiftsInIntervalWTATemplate();
                copyProperties((ShiftsInIntervalWTATemplateDTO)ruleTemplate,shiftsInIntervalWTATemplate);
                shiftsInIntervalWTATemplate.setRuleTemplateCategoryId(ruleTemplate.getRuleTemplateCategory().getId());
                wtaBaseRuleTemplate = shiftsInIntervalWTATemplate;
                break;
            case MAXIMUM_SENIOR_DAYS_IN_YEAR:
                SeniorDaysInYearWTATemplate seniorDaysInYearWTATemplate = new SeniorDaysInYearWTATemplate();
                copyProperties((SeniorDaysInYearWTATemplateDTO)ruleTemplate,seniorDaysInYearWTATemplate);
                seniorDaysInYearWTATemplate.setRuleTemplateCategoryId(ruleTemplate.getRuleTemplateCategory().getId());
                wtaBaseRuleTemplate = seniorDaysInYearWTATemplate;
                break;
            case TIME_BANK:
                TimeBankWTATemplate timeBankWTATemplate = new TimeBankWTATemplate();
                copyProperties((TimeBankWTATemplateDTO)ruleTemplate,timeBankWTATemplate);
                timeBankWTATemplate.setRuleTemplateCategoryId(ruleTemplate.getRuleTemplateCategory().getId());
                wtaBaseRuleTemplate = timeBankWTATemplate;
                break;

            default:
                throw new DataNotFoundByIdException("Invalid TEMPLATE");
        }
        return wtaBaseRuleTemplate;
    }


    public static WTABaseRuleTemplateDTO copyRuleTemplateToDTO(WTABaseRuleTemplate ruleTemplate){
        WTABaseRuleTemplateDTO wtaBaseRuleTemplate = new WTABaseRuleTemplateDTO();
        switch (ruleTemplate.getWtaTemplateType()) {
            case SHIFT_LENGTH:
                wtaBaseRuleTemplate = ObjectMapperUtils.copyPropertiesByMapper(ruleTemplate,ShiftLengthWTATemplateDTO.class);
                break;
            case CONSECUTIVE_WORKING_PARTOFDAY:
                wtaBaseRuleTemplate = ObjectMapperUtils.copyPropertiesByMapper(ruleTemplate,ConsecutiveWorkWTATemplateDTO.class);
                break;

          /*  case CONSECUTIVE_NIGHTS_AND_DAYS:
                ConsecutiveRestPartOfDayWTATemplateDTO consecutiveRestPartOfDayWTATemplate = new ConsecutiveRestPartOfDayWTATemplateDTO();
                copyProperties(ruleTemplate,consecutiveRestPartOfDayWTATemplate);
                consecutiveRestPartOfDayWTATemplate.setRuleTemplateCategoryId((BigInteger) ruleTemplate.get("ruleTemplateCategoryId"));
                wtaBaseRuleTemplate = consecutiveRestPartOfDayWTATemplate;
                break;*/
            case REST_IN_CONSECUTIVE_DAYS_AND_NIGHTS:
                wtaBaseRuleTemplate = ObjectMapperUtils.copyPropertiesByMapper(ruleTemplate,ConsecutiveRestPartOfDayWTATemplateDTO.class);
                break;
            case NUMBER_OF_PARTOFDAY:
                wtaBaseRuleTemplate = ObjectMapperUtils.copyPropertiesByMapper(ruleTemplate,NumberOfPartOfDayShiftsWTATemplateDTO.class);
                break;
            case DAYS_OFF_IN_PERIOD:
                wtaBaseRuleTemplate = ObjectMapperUtils.copyPropertiesByMapper(ruleTemplate,DaysOffInPeriodWTATemplateDTO.class);
                break;
            case AVERAGE_SHEDULED_TIME:
                wtaBaseRuleTemplate = ObjectMapperUtils.copyPropertiesByMapper(ruleTemplate,AverageScheduledTimeWTATemplateDTO.class);
                break;
            case VETO_PER_PERIOD:
                wtaBaseRuleTemplate = ObjectMapperUtils.copyPropertiesByMapper(ruleTemplate,VetoPerPeriodWTATemplateDTO.class);
                break;
            case NUMBER_OF_WEEKEND_SHIFT_IN_PERIOD:
                wtaBaseRuleTemplate = ObjectMapperUtils.copyPropertiesByMapper(ruleTemplate,NumberOfWeekendShiftsInPeriodWTATemplateDTO.class);
                break;
            case CARE_DAYS_CHECK:
                wtaBaseRuleTemplate = ObjectMapperUtils.copyPropertiesByMapper(ruleTemplate,CareDayCheckWTATemplateDTO.class);
                break;
            case DAILY_RESTING_TIME:
                wtaBaseRuleTemplate = ObjectMapperUtils.copyPropertiesByMapper(ruleTemplate,DailyRestingTimeWTATemplateDTO.class);
                break;
            case DURATION_BETWEEN_SHIFTS:
                wtaBaseRuleTemplate = ObjectMapperUtils.copyPropertiesByMapper(ruleTemplate,DurationBetweenShiftsWTATemplateDTO.class);
                break;
            case WEEKLY_REST_PERIOD:
                wtaBaseRuleTemplate = ObjectMapperUtils.copyPropertiesByMapper(ruleTemplate,WeeklyRestPeriodWTATemplateDTO.class);
                break;
            case SHORTEST_AND_AVERAGE_DAILY_REST:
                wtaBaseRuleTemplate = ObjectMapperUtils.copyPropertiesByMapper(ruleTemplate,ShortestAndAverageDailyRestWTATemplateDTO.class);
                break;
            case NUMBER_OF_SHIFTS_IN_INTERVAL:
                wtaBaseRuleTemplate = ObjectMapperUtils.copyPropertiesByMapper(ruleTemplate,ShiftsInIntervalWTATemplateDTO.class);
                break;
            case MAXIMUM_SENIOR_DAYS_IN_YEAR:
                wtaBaseRuleTemplate = ObjectMapperUtils.copyPropertiesByMapper(ruleTemplate,SeniorDaysInYearWTATemplateDTO.class);
                break;
            case TIME_BANK:
                wtaBaseRuleTemplate = ObjectMapperUtils.copyPropertiesByMapper(ruleTemplate,TimeBankWTATemplateDTO.class);
                break;
            case MAXIMUM_SENIOR_DAYS_PER_YEAR:
                wtaBaseRuleTemplate = ObjectMapperUtils.copyPropertiesByMapper(ruleTemplate,MaximumSeniorDaysPerYearDTO.class);
                break;
            case CHILD_CARE_DAYS_CHECK:
                wtaBaseRuleTemplate = ObjectMapperUtils.copyPropertiesByMapper(ruleTemplate,CareDaysCheckDTO.class);
                break;


            default:
                throw new DataNotFoundByIdException("Invalid TEMPLATE");
        }
        return wtaBaseRuleTemplate;
    }


    public static void copyWTARuleTemplateToWTA(WorkingTimeAgreement workingTimeAgreement, WTAQueryResultDTO wtaQueryResultDTO){


    }

    public static List<WTABaseRuleTemplateDTO> copyPropertiesMapToDTO(List<Map> templates){
            List<WTABaseRuleTemplateDTO> wtaBaseRuleTemplates = new ArrayList<>();
            for (Map ruleTemplate : templates) {
                wtaBaseRuleTemplates.add(copyRuleTemplateMapToDTO(ruleTemplate));

            }
            return wtaBaseRuleTemplates;

    }


    public static WTABaseRuleTemplateDTO copyRuleTemplateMapToDTO(Map ruleTemplate){
        WTABaseRuleTemplateDTO wtaBaseRuleTemplate = new WTABaseRuleTemplateDTO();
        switch (WTATemplateType.valueOf((String) ruleTemplate.get("wtaTemplateType"))) {
            case SHIFT_LENGTH:
                wtaBaseRuleTemplate = ObjectMapperUtils.copyPropertiesByMapper(ruleTemplate,ShiftLengthWTATemplateDTO.class);
                break;
            case CONSECUTIVE_WORKING_PARTOFDAY:
                wtaBaseRuleTemplate = ObjectMapperUtils.copyPropertiesByMapper(ruleTemplate,ConsecutiveWorkWTATemplateDTO.class);
                break;

          /*  case CONSECUTIVE_NIGHTS_AND_DAYS:
                ConsecutiveRestPartOfDayWTATemplateDTO consecutiveRestPartOfDayWTATemplate = new ConsecutiveRestPartOfDayWTATemplateDTO();
                copyProperties(ruleTemplate,consecutiveRestPartOfDayWTATemplate);
                consecutiveRestPartOfDayWTATemplate.setRuleTemplateCategoryId((BigInteger) ruleTemplate.get("ruleTemplateCategoryId"));
                wtaBaseRuleTemplate = consecutiveRestPartOfDayWTATemplate;
                break;*/
            case REST_IN_CONSECUTIVE_DAYS_AND_NIGHTS:
                wtaBaseRuleTemplate = ObjectMapperUtils.copyPropertiesByMapper(ruleTemplate,ConsecutiveRestPartOfDayWTATemplateDTO.class);
                break;
            case NUMBER_OF_PARTOFDAY:
                wtaBaseRuleTemplate = ObjectMapperUtils.copyPropertiesByMapper(ruleTemplate,NumberOfPartOfDayShiftsWTATemplateDTO.class);
                break;
            case DAYS_OFF_IN_PERIOD:
                wtaBaseRuleTemplate = ObjectMapperUtils.copyPropertiesByMapper(ruleTemplate,DaysOffInPeriodWTATemplateDTO.class);
                break;
            case AVERAGE_SHEDULED_TIME:
                wtaBaseRuleTemplate = ObjectMapperUtils.copyPropertiesByMapper(ruleTemplate,AverageScheduledTimeWTATemplateDTO.class);
                break;
            case VETO_PER_PERIOD:
                wtaBaseRuleTemplate = ObjectMapperUtils.copyPropertiesByMapper(ruleTemplate,VetoPerPeriodWTATemplateDTO.class);
                break;
            case NUMBER_OF_WEEKEND_SHIFT_IN_PERIOD:
                wtaBaseRuleTemplate = ObjectMapperUtils.copyPropertiesByMapper(ruleTemplate,NumberOfWeekendShiftsInPeriodWTATemplateDTO.class);
                break;
            case CARE_DAYS_CHECK:
                wtaBaseRuleTemplate = ObjectMapperUtils.copyPropertiesByMapper(ruleTemplate,CareDayCheckWTATemplateDTO.class);
                break;
            case DAILY_RESTING_TIME:
                wtaBaseRuleTemplate = ObjectMapperUtils.copyPropertiesByMapper(ruleTemplate,DailyRestingTimeWTATemplateDTO.class);
                break;
            case DURATION_BETWEEN_SHIFTS:
                wtaBaseRuleTemplate = ObjectMapperUtils.copyPropertiesByMapper(ruleTemplate,DurationBetweenShiftsWTATemplateDTO.class);
                break;
            case WEEKLY_REST_PERIOD:
                wtaBaseRuleTemplate = ObjectMapperUtils.copyPropertiesByMapper(ruleTemplate,WeeklyRestPeriodWTATemplateDTO.class);
                break;
            case SHORTEST_AND_AVERAGE_DAILY_REST:
                wtaBaseRuleTemplate = ObjectMapperUtils.copyPropertiesByMapper(ruleTemplate,ShortestAndAverageDailyRestWTATemplateDTO.class);
                break;
            case NUMBER_OF_SHIFTS_IN_INTERVAL:
                wtaBaseRuleTemplate = ObjectMapperUtils.copyPropertiesByMapper(ruleTemplate,ShiftsInIntervalWTATemplateDTO.class);
                break;
            case MAXIMUM_SENIOR_DAYS_IN_YEAR:
                wtaBaseRuleTemplate = ObjectMapperUtils.copyPropertiesByMapper(ruleTemplate,SeniorDaysInYearWTATemplateDTO.class);
                break;
            case TIME_BANK:
                wtaBaseRuleTemplate = ObjectMapperUtils.copyPropertiesByMapper(ruleTemplate,TimeBankWTATemplateDTO.class);
                break;
            case MAXIMUM_SENIOR_DAYS_PER_YEAR:
                wtaBaseRuleTemplate = ObjectMapperUtils.copyPropertiesByMapper(ruleTemplate,MaximumSeniorDaysPerYearDTO.class);
                break;
            case CHILD_CARE_DAYS_CHECK:
                wtaBaseRuleTemplate = ObjectMapperUtils.copyPropertiesByMapper(ruleTemplate,CareDaysCheckDTO.class);
                break;


            default:
                throw new DataNotFoundByIdException("Invalid TEMPLATE");
        }
        return wtaBaseRuleTemplate;
    }

    public static void copyProperties(Object source,Object destination){
        try {
            PropertyUtils.copyProperties(destination,source);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    /*public static List<T> copyPhaseTemplateValue(List<Object> source,List<Object> phaseTemplateValues) {
        List<T> phases = null;
        if (phaseTemplateValues != null) {
            phases = new ArrayList<>(4);
            for (T phaseTemplateValueDTO : phaseTemplateValues) {
                T newPhaseTemplateValue = new PhaseTemplateValue();
                copyProperties(phaseTemplateValueDTO,newPhaseTemplateValue);
                phases.add(newPhaseTemplateValue);
            }
        }
        return phases;
    }*/


    public static List<WTARuleTemplateDTO> getRuleTemplateDTO(WTAQueryResultDTO wtaQueryResultDTO) {
        List<WTARuleTemplateDTO> wtaRuleTemplateDTOS = new ArrayList<>();
        copyProperties(wtaQueryResultDTO.getRuleTemplates(),wtaRuleTemplateDTOS);
        return wtaRuleTemplateDTOS;
    }

    public void copyRuleTemplateToNewWTA(WorkingTimeAgreement oldWta,WorkingTimeAgreement newWTA) {
        List<WTABaseRuleTemplate> wtaBaseRuleTemplates = (List<WTABaseRuleTemplate>) wtaBaseRuleTemplateMongoRepository.findAllById(oldWta.getRuleTemplateIds());
        List<WTABaseRuleTemplate> wtaBaseRuleTemplates1 = new ArrayList<>();
        copyProperties(wtaBaseRuleTemplates,wtaBaseRuleTemplates1);
        save(wtaBaseRuleTemplates1);
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


}

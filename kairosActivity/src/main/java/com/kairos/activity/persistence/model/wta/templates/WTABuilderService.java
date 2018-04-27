package com.kairos.activity.persistence.model.wta.templates;

import com.kairos.activity.custom_exception.DataNotFoundByIdException;
import com.kairos.activity.persistence.model.wta.WTAQueryResultDTO;
import com.kairos.activity.persistence.model.wta.WorkingTimeAgreement;
import com.kairos.activity.persistence.model.wta.templates.template_types.*;
import com.kairos.activity.persistence.repository.wta.WTABaseRuleTemplateMongoRepository;
import com.kairos.activity.service.MongoBaseService;
import com.kairos.response.dto.web.wta.*;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import static com.kairos.activity.constants.AppConstants.COPY_OF;
import static com.kairos.activity.persistence.enums.WTATemplateType.*;

/**
 * @author pradeep
 * @date - 13/4/18
 */

@Service
public class WTABuilderService extends MongoBaseService {

    @Inject private WTABaseRuleTemplateMongoRepository wtaBaseRuleTemplateMongoRepository;

    public  List<WTABaseRuleTemplate> copyRuleTemplates(List<WTARuleTemplateDTO> WTARuleTemplateDTOS,boolean ignoreId) {
        List<WTABaseRuleTemplate> wtaBaseRuleTemplates = new ArrayList<>();
        for (WTARuleTemplateDTO ruleTemplate : WTARuleTemplateDTOS) {
            wtaBaseRuleTemplates.add(copyRuleTemplate(ruleTemplate,ignoreId));

        }
        return wtaBaseRuleTemplates;
    }

    public WTABaseRuleTemplate copyRuleTemplate(WTARuleTemplateDTO ruleTemplate,Boolean isIdnull){
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

            case CONSECUTIVE_NIGHTS_AND_DAYS:
                ConsecutiveRestPartOfDayWTATemplate consecutiveRestPartOfDayWTATemplate = new ConsecutiveRestPartOfDayWTATemplate();
                copyProperties(ruleTemplate,consecutiveRestPartOfDayWTATemplate);
                consecutiveRestPartOfDayWTATemplate.setRuleTemplateCategoryId(ruleTemplate.getRuleTemplateCategory().getId());
                consecutiveRestPartOfDayWTATemplate.setId(null);
                wtaBaseRuleTemplate = consecutiveRestPartOfDayWTATemplate;
                break;
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

    public  List<WTABaseRuleTemplateDTO> copyRuleTemplatesToDTO(List<WTABaseRuleTemplate> WTARuleTemplates) {
        List<WTABaseRuleTemplateDTO> wtaBaseRuleTemplates = new ArrayList<>();
        for (WTABaseRuleTemplate ruleTemplate : WTARuleTemplates) {
            wtaBaseRuleTemplates.add(copyRuleTemplateToDTO(ruleTemplate));

        }
        return wtaBaseRuleTemplates;
    }


    public WTABaseRuleTemplateDTO copyRuleTemplateToDTO(WTABaseRuleTemplate ruleTemplate){
        WTABaseRuleTemplateDTO wtaBaseRuleTemplate = new WTABaseRuleTemplateDTO();
        switch (ruleTemplate.getWtaTemplateType()) {
            case SHIFT_LENGTH:
                ShiftLengthWTATemplateDTO shiftLengthWTATemplate = new ShiftLengthWTATemplateDTO();
                copyProperties(ruleTemplate,shiftLengthWTATemplate);
                shiftLengthWTATemplate.setRuleTemplateCategoryId(ruleTemplate.getRuleTemplateCategoryId());
                wtaBaseRuleTemplate = shiftLengthWTATemplate;
                break;
            case CONSECUTIVE_WORKING_PARTOFDAY:
                ConsecutiveWorkWTATemplateDTO consecutiveWorkWTATemplate = new ConsecutiveWorkWTATemplateDTO();
                copyProperties(ruleTemplate,consecutiveWorkWTATemplate);
                consecutiveWorkWTATemplate.setRuleTemplateCategoryId(ruleTemplate.getRuleTemplateCategoryId());
                wtaBaseRuleTemplate = consecutiveWorkWTATemplate;
                break;

          /*  case CONSECUTIVE_NIGHTS_AND_DAYS:
                ConsecutiveRestPartOfDayWTATemplateDTO consecutiveRestPartOfDayWTATemplate = new ConsecutiveRestPartOfDayWTATemplateDTO();
                copyProperties(ruleTemplate,consecutiveRestPartOfDayWTATemplate);
                consecutiveRestPartOfDayWTATemplate.setRuleTemplateCategoryId(ruleTemplate.getRuleTemplateCategoryId());
                wtaBaseRuleTemplate = consecutiveRestPartOfDayWTATemplate;
                break;*/
            case REST_IN_CONSECUTIVE_DAYS_AND_NIGHTS:
                ConsecutiveRestPartOfDayWTATemplateDTO consecutiveRestPartOfDayWTATemplate1 = new ConsecutiveRestPartOfDayWTATemplateDTO();
                copyProperties(ruleTemplate,consecutiveRestPartOfDayWTATemplate1);
                consecutiveRestPartOfDayWTATemplate1.setRuleTemplateCategoryId(ruleTemplate.getRuleTemplateCategoryId());
                wtaBaseRuleTemplate = consecutiveRestPartOfDayWTATemplate1;
                break;
            case NUMBER_OF_PARTOFDAY:
                NumberOfPartOfDayShiftsWTATemplateDTO numberOfPartOfDayShiftsWTATemplate = new NumberOfPartOfDayShiftsWTATemplateDTO();
                copyProperties(ruleTemplate,numberOfPartOfDayShiftsWTATemplate);
                numberOfPartOfDayShiftsWTATemplate.setRuleTemplateCategoryId(ruleTemplate.getRuleTemplateCategoryId());
                wtaBaseRuleTemplate = numberOfPartOfDayShiftsWTATemplate;
                break;
            case DAYS_OFF_IN_PERIOD:
                DaysOffInPeriodWTATemplateDTO daysOffInPeriodWTATemplate = new DaysOffInPeriodWTATemplateDTO();
                copyProperties(ruleTemplate,daysOffInPeriodWTATemplate);
                daysOffInPeriodWTATemplate.setRuleTemplateCategoryId(ruleTemplate.getRuleTemplateCategoryId());
                wtaBaseRuleTemplate = daysOffInPeriodWTATemplate;
                break;
            case AVERAGE_SHEDULED_TIME:
                AverageScheduledTimeWTATemplateDTO averageScheduledTimeWTATemplate = new AverageScheduledTimeWTATemplateDTO();
                copyProperties(ruleTemplate,averageScheduledTimeWTATemplate);
                averageScheduledTimeWTATemplate.setRuleTemplateCategoryId(ruleTemplate.getRuleTemplateCategoryId());
                wtaBaseRuleTemplate = averageScheduledTimeWTATemplate;
                break;
            case VETO_PER_PERIOD:
                VetoPerPeriodWTATemplateDTO vetoPerPeriodWTATemplate = new VetoPerPeriodWTATemplateDTO();
                copyProperties(ruleTemplate,vetoPerPeriodWTATemplate);
                vetoPerPeriodWTATemplate.setRuleTemplateCategoryId(ruleTemplate.getRuleTemplateCategoryId());
                wtaBaseRuleTemplate = vetoPerPeriodWTATemplate;
                break;
            case NUMBER_OF_WEEKEND_SHIFT_IN_PERIOD:
                NumberOfWeekendShiftsInPeriodWTATemplateDTO numberOfWeekendShiftsInPeriodWTATemplate = new NumberOfWeekendShiftsInPeriodWTATemplateDTO();
                copyProperties(ruleTemplate,numberOfWeekendShiftsInPeriodWTATemplate);
                numberOfWeekendShiftsInPeriodWTATemplate.setRuleTemplateCategoryId(ruleTemplate.getRuleTemplateCategoryId());
                wtaBaseRuleTemplate = numberOfWeekendShiftsInPeriodWTATemplate;
                break;
            case CARE_DAYS_CHECK:
                CareDayCheckWTATemplateDTO careDayCheckWTATemplate = new CareDayCheckWTATemplateDTO();
                copyProperties(ruleTemplate,careDayCheckWTATemplate);
                careDayCheckWTATemplate.setRuleTemplateCategoryId(ruleTemplate.getRuleTemplateCategoryId());
                wtaBaseRuleTemplate = careDayCheckWTATemplate;
                break;
            case DAILY_RESTING_TIME:
                DailyRestingTimeWTATemplateDTO dailyRestingTimeWTATemplate = new DailyRestingTimeWTATemplateDTO();
                copyProperties(ruleTemplate,dailyRestingTimeWTATemplate);
                dailyRestingTimeWTATemplate.setRuleTemplateCategoryId(ruleTemplate.getRuleTemplateCategoryId());
                wtaBaseRuleTemplate = dailyRestingTimeWTATemplate;
                break;
            case DURATION_BETWEEN_SHIFTS:
                DurationBetweenShiftsWTATemplateDTO durationBetweenShiftsWTATemplate = new DurationBetweenShiftsWTATemplateDTO();
                copyProperties(ruleTemplate,durationBetweenShiftsWTATemplate);
                durationBetweenShiftsWTATemplate.setRuleTemplateCategoryId(ruleTemplate.getRuleTemplateCategoryId());
                wtaBaseRuleTemplate = durationBetweenShiftsWTATemplate;
                break;
            case WEEKLY_REST_PERIOD:
                WeeklyRestPeriodWTATemplateDTO weeklyRestPeriodWTATemplate = new WeeklyRestPeriodWTATemplateDTO();
                copyProperties(ruleTemplate,weeklyRestPeriodWTATemplate);
                weeklyRestPeriodWTATemplate.setRuleTemplateCategoryId(ruleTemplate.getRuleTemplateCategoryId());
                wtaBaseRuleTemplate = weeklyRestPeriodWTATemplate;
                break;
            case SHORTEST_AND_AVERAGE_DAILY_REST:
                ShortestAndAverageDailyRestWTATemplateDTO shortestAndAverageDailyRestWTATemplate = new ShortestAndAverageDailyRestWTATemplateDTO();
                copyProperties(ruleTemplate,shortestAndAverageDailyRestWTATemplate);
                shortestAndAverageDailyRestWTATemplate.setRuleTemplateCategoryId(ruleTemplate.getRuleTemplateCategoryId());
                wtaBaseRuleTemplate = shortestAndAverageDailyRestWTATemplate;
                break;
            case NUMBER_OF_SHIFTS_IN_INTERVAL:
                ShiftsInIntervalWTATemplateDTO shiftsInIntervalWTATemplate = new ShiftsInIntervalWTATemplateDTO();
                copyProperties(ruleTemplate,shiftsInIntervalWTATemplate);
                shiftsInIntervalWTATemplate.setRuleTemplateCategoryId(ruleTemplate.getRuleTemplateCategoryId());
                wtaBaseRuleTemplate = shiftsInIntervalWTATemplate;
                break;
            case MAXIMUM_SENIOR_DAYS_IN_YEAR:
                SeniorDaysInYearWTATemplateDTO seniorDaysInYearWTATemplate = new SeniorDaysInYearWTATemplateDTO();
                copyProperties(ruleTemplate,seniorDaysInYearWTATemplate);
                seniorDaysInYearWTATemplate.setRuleTemplateCategoryId(ruleTemplate.getRuleTemplateCategoryId());
                wtaBaseRuleTemplate = seniorDaysInYearWTATemplate;
                break;
            case TIME_BANK:
                TimeBankWTATemplateDTO timeBankWTATemplate = new TimeBankWTATemplateDTO();
                copyProperties(ruleTemplate,timeBankWTATemplate);
                timeBankWTATemplate.setRuleTemplateCategoryId(ruleTemplate.getRuleTemplateCategoryId());
                wtaBaseRuleTemplate = timeBankWTATemplate;
                break;

            default:
                throw new DataNotFoundByIdException("Invalid TEMPLATE");
        }
        return wtaBaseRuleTemplate;
    }


    public static void copyWTARuleTemplateToWTA(WorkingTimeAgreement workingTimeAgreement, WTAQueryResultDTO wtaQueryResultDTO){


    }

    private static void copyProperties(Object source,Object destination){
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

package com.kairos.activity.persistence.model.wta.templates;

import com.kairos.activity.custom_exception.DataNotFoundByIdException;
import com.kairos.activity.persistence.enums.WTATemplateType;
import com.kairos.activity.persistence.model.wta.WTAQueryResultDTO;
import com.kairos.activity.persistence.model.wta.WorkingTimeAgreement;
import com.kairos.activity.persistence.model.wta.templates.template_types.*;
import com.kairos.response.dto.web.wta.WTARuleTemplateDTO;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

import static com.kairos.activity.persistence.enums.WTATemplateType.*;

/**
 * @author pradeep
 * @date - 13/4/18
 */

public class WTABuilderFactory {


    public static void copyRuleTemplate(WTAQueryResultDTO wtaQueryResultDTO, List<WTARuleTemplateDTO> WTARuleTemplateDTOS) {
        List<ShiftLengthWTATemplate> shiftLengths = new ArrayList<>();
        List<AverageScheduledTimeWTATemplate> averageScheduledTimes = new ArrayList<>();
        List<CareDayCheckWTATemplate> careDayChecks = new ArrayList<>();
        List<ConsecutiveRestPartOfDayWTATemplate> consecutiveRestPartOfDays = new ArrayList<>();
        List<ConsecutiveWorkWTATemplate> consecutiveWorks = new ArrayList<>();
        List<DailyRestingTimeWTATemplate> dailyRestingTimes = new ArrayList<>();
        List<DaysOffInPeriodWTATemplate> daysOffInPeriods = new ArrayList<>();
        List<DurationBetweenShiftWTATemplate> durationBetweenShifts = new ArrayList<>();
        List<NumberOfPartOfDayShiftsWTATemplate> numberOfPartOfDayShifts = new ArrayList<>();
        List<NumberOfWeekendShiftInPeriodWTATemplate> numberOfWeekendShiftInPeriods = new ArrayList<>();
        List<SeniorDaysInYearWTATemplate> seniorDaysInYears = new ArrayList<>();
        List<ShiftsInIntervalWTATemplate> shiftsInIntervals = new ArrayList<>();
        List<ShortestAndAverageDailyRestWTATemplate> shortestAndAverageDailyRests = new ArrayList<>();
        List<TimeBankWTATemplate> timeBanks = new ArrayList<>();
        List<VetoPerPeriodWTATemplate> vetoPerPeriods = new ArrayList<>();
        List<WeeklyRestPeriodWTATemplate> weeklyRestPeriods = new ArrayList<>();
        for (WTARuleTemplateDTO ruleTemplate : WTARuleTemplateDTOS) {
            List<PhaseTemplateValue> phaseTemplateValues = new ArrayList<>(ruleTemplate.getPhaseTemplateValues().size());
            BeanUtils.copyProperties(ruleTemplate.getPhaseTemplateValues(),phaseTemplateValues);
            WTATemplateType wtaTemplateType = getByTemplateType(ruleTemplate.getTemplateType());
            switch (wtaTemplateType) {
                case SHIFT_LENGTH:
                    ShiftLengthWTATemplate shiftLengthWTATemplate = new ShiftLengthWTATemplate();
                    BeanUtils.copyProperties(shiftLengthWTATemplate,ruleTemplate);
                    shiftLengthWTATemplate.setWTARuleTemplateCategory(ruleTemplate.getRuleTemplateCategory().getId());
                    shiftLengths.add(shiftLengthWTATemplate);
                    break;
                case CONSECUTIVE_WORKING_PARTOFDAY:
                    ConsecutiveWorkWTATemplate consecutiveWorkWTATemplate = new ConsecutiveWorkWTATemplate();
                    BeanUtils.copyProperties(consecutiveWorkWTATemplate,ruleTemplate);
                    consecutiveWorkWTATemplate.setWTARuleTemplateCategory(ruleTemplate.getRuleTemplateCategory().getId());
                    consecutiveWorks.add(consecutiveWorkWTATemplate);
                    break;

                case CONSECUTIVE_NIGHTS_AND_DAYS:
                    ConsecutiveRestPartOfDayWTATemplate consecutiveRestPartOfDayWTATemplate = new ConsecutiveRestPartOfDayWTATemplate();
                    BeanUtils.copyProperties(consecutiveRestPartOfDayWTATemplate,ruleTemplate);
                    consecutiveRestPartOfDayWTATemplate.setWTARuleTemplateCategory(ruleTemplate.getRuleTemplateCategory().getId());
                    consecutiveRestPartOfDays.add(consecutiveRestPartOfDayWTATemplate);
                    break;
                case REST_IN_CONSECUTIVE_DAYS_AND_NIGHTS:
                    ConsecutiveRestPartOfDayWTATemplate consecutiveRestPartOfDayWTATemplate1 = new ConsecutiveRestPartOfDayWTATemplate();
                    BeanUtils.copyProperties(consecutiveRestPartOfDayWTATemplate1,ruleTemplate);
                    consecutiveRestPartOfDayWTATemplate1.setWTARuleTemplateCategory(ruleTemplate.getRuleTemplateCategory().getId());
                    consecutiveRestPartOfDays.add(consecutiveRestPartOfDayWTATemplate1);
                    break;
                case NUMBER_OF_PARTOFDAY:
                    NumberOfPartOfDayShiftsWTATemplate numberOfPartOfDayShiftsWTATemplate = new NumberOfPartOfDayShiftsWTATemplate();
                    BeanUtils.copyProperties(numberOfPartOfDayShiftsWTATemplate,ruleTemplate);
                    numberOfPartOfDayShiftsWTATemplate.setWTARuleTemplateCategory(ruleTemplate.getRuleTemplateCategory().getId());
                    numberOfPartOfDayShifts.add(numberOfPartOfDayShiftsWTATemplate);
                    break;
                case DAYS_OFF_IN_PERIOD:
                    DaysOffInPeriodWTATemplate daysOffInPeriodWTATemplate = new DaysOffInPeriodWTATemplate();
                    BeanUtils.copyProperties(daysOffInPeriodWTATemplate,ruleTemplate);
                    daysOffInPeriodWTATemplate.setWTARuleTemplateCategory(ruleTemplate.getRuleTemplateCategory().getId());
                    daysOffInPeriods.add(daysOffInPeriodWTATemplate);
                    break;
                case AVERAGE_SHEDULED_TIME:
                    AverageScheduledTimeWTATemplate averageScheduledTimeWTATemplate = new AverageScheduledTimeWTATemplate();
                    BeanUtils.copyProperties(averageScheduledTimeWTATemplate,ruleTemplate);
                    averageScheduledTimeWTATemplate.setWTARuleTemplateCategory(ruleTemplate.getRuleTemplateCategory().getId());
                    averageScheduledTimes.add(averageScheduledTimeWTATemplate);
                    break;
                case VETO_PER_PERIOD:
                    VetoPerPeriodWTATemplate vetoPerPeriodWTATemplate = new VetoPerPeriodWTATemplate();
                    BeanUtils.copyProperties(vetoPerPeriodWTATemplate,ruleTemplate);
                    vetoPerPeriodWTATemplate.setWTARuleTemplateCategory(ruleTemplate.getRuleTemplateCategory().getId());
                    vetoPerPeriods.add(vetoPerPeriodWTATemplate);
                    break;
                case NUMBER_OF_WEEKEND_SHIFT_IN_PERIOD:
                    NumberOfWeekendShiftInPeriodWTATemplate numberOfWeekendShiftInPeriodWTATemplate = new NumberOfWeekendShiftInPeriodWTATemplate();
                    BeanUtils.copyProperties(numberOfWeekendShiftInPeriodWTATemplate,ruleTemplate);
                    numberOfWeekendShiftInPeriodWTATemplate.setWTARuleTemplateCategory(ruleTemplate.getRuleTemplateCategory().getId());
                    numberOfWeekendShiftInPeriods.add(numberOfWeekendShiftInPeriodWTATemplate);
                    break;
                case CARE_DAYS_CHECK:
                    CareDayCheckWTATemplate careDayCheckWTATemplate = new CareDayCheckWTATemplate();
                    BeanUtils.copyProperties(careDayCheckWTATemplate,ruleTemplate);
                    careDayCheckWTATemplate.setWTARuleTemplateCategory(ruleTemplate.getRuleTemplateCategory().getId());
                    careDayChecks.add(careDayCheckWTATemplate);
                    break;
                case DAILY_RESTING_TIME:
                    DailyRestingTimeWTATemplate dailyRestingTimeWTATemplate = new DailyRestingTimeWTATemplate();
                    BeanUtils.copyProperties(dailyRestingTimeWTATemplate,ruleTemplate);
                    dailyRestingTimeWTATemplate.setWTARuleTemplateCategory(ruleTemplate.getRuleTemplateCategory().getId());
                    dailyRestingTimes.add(dailyRestingTimeWTATemplate);
                    break;
                case DURATION_BETWEEN_SHIFTS:
                    DurationBetweenShiftWTATemplate durationBetweenShiftWTATemplate = new DurationBetweenShiftWTATemplate();
                    BeanUtils.copyProperties(durationBetweenShiftWTATemplate,ruleTemplate);
                    durationBetweenShiftWTATemplate.setWTARuleTemplateCategory(ruleTemplate.getRuleTemplateCategory().getId());
                    durationBetweenShifts.add(durationBetweenShiftWTATemplate);
                    break;
                case WEEKLY_REST_PERIOD:
                    WeeklyRestPeriodWTATemplate weeklyRestPeriodWTATemplate = new WeeklyRestPeriodWTATemplate();
                    BeanUtils.copyProperties(weeklyRestPeriodWTATemplate,ruleTemplate);
                    weeklyRestPeriodWTATemplate.setWTARuleTemplateCategory(ruleTemplate.getRuleTemplateCategory().getId());
                    weeklyRestPeriods.add(weeklyRestPeriodWTATemplate);
                    break;
                case SHORTEST_AND_AVERAGE_DAILY_REST:
                    ShortestAndAverageDailyRestWTATemplate shortestAndAverageDailyRestWTATemplate = new ShortestAndAverageDailyRestWTATemplate();
                    BeanUtils.copyProperties(shortestAndAverageDailyRestWTATemplate,ruleTemplate);
                    shortestAndAverageDailyRestWTATemplate.setWTARuleTemplateCategory(ruleTemplate.getRuleTemplateCategory().getId());
                    shortestAndAverageDailyRests.add(shortestAndAverageDailyRestWTATemplate);
                    break;
                case NUMBER_OF_SHIFTS_IN_INTERVAL:
                    ShiftsInIntervalWTATemplate shiftsInIntervalWTATemplate = new ShiftsInIntervalWTATemplate();
                    BeanUtils.copyProperties(shiftsInIntervalWTATemplate,ruleTemplate);
                    shiftsInIntervalWTATemplate.setWTARuleTemplateCategory(ruleTemplate.getRuleTemplateCategory().getId());
                    shiftsInIntervals.add(shiftsInIntervalWTATemplate);
                    break;
                case MAXIMUM_SENIOR_DAYS_IN_YEAR:
                    SeniorDaysInYearWTATemplate seniorDaysInYearWTATemplate = new SeniorDaysInYearWTATemplate();
                    BeanUtils.copyProperties(seniorDaysInYearWTATemplate,ruleTemplate);
                    seniorDaysInYearWTATemplate.setWTARuleTemplateCategory(ruleTemplate.getRuleTemplateCategory().getId());
                    seniorDaysInYears.add(seniorDaysInYearWTATemplate);
                    break;
                case TIME_BANK:
                    TimeBankWTATemplate timeBankWTATemplate = new TimeBankWTATemplate();
                    BeanUtils.copyProperties(timeBankWTATemplate,ruleTemplate);
                    timeBankWTATemplate.setWTARuleTemplateCategory(ruleTemplate.getRuleTemplateCategory().getId());
                    timeBanks.add(timeBankWTATemplate);
                    break;

                default:
                    throw new DataNotFoundByIdException("Invalid TEMPLATE");
            }

        }
        wtaQueryResultDTO.setShiftLengths(shiftLengths);
        wtaQueryResultDTO.setAverageScheduledTimes(averageScheduledTimes);
        wtaQueryResultDTO.setCareDayChecks(careDayChecks);
        wtaQueryResultDTO.setConsecutiveRestPartOfDays(consecutiveRestPartOfDays);
        wtaQueryResultDTO.setConsecutiveWorks(consecutiveWorks);
        wtaQueryResultDTO.setDailyRestingTimes(dailyRestingTimes);
        wtaQueryResultDTO.setDaysOffInPeriods(daysOffInPeriods);
        wtaQueryResultDTO.setDurationBetweenShifts(durationBetweenShifts);
        wtaQueryResultDTO.setNumberOfPartOfDayShifts(numberOfPartOfDayShifts);
        wtaQueryResultDTO.setNumberOfWeekendShiftInPeriods(numberOfWeekendShiftInPeriods);
        wtaQueryResultDTO.setSeniorDaysInYears(seniorDaysInYears);
        wtaQueryResultDTO.setShiftsInIntervals(shiftsInIntervals);
        wtaQueryResultDTO.setShortestAndAverageDailyRests(shortestAndAverageDailyRests);
        wtaQueryResultDTO.setTimeBanks(timeBanks);
        wtaQueryResultDTO.setVetoPerPeriods(vetoPerPeriods);
        wtaQueryResultDTO.setWeeklyRestPeriods(weeklyRestPeriods);
    }


    public static void copyWTARuleTemplateToWTA(WorkingTimeAgreement workingTimeAgreement, WTAQueryResultDTO wtaQueryResultDTO){
        workingTimeAgreement.setShiftLengths(wtaQueryResultDTO.getShiftLengths());
        workingTimeAgreement.setAverageScheduledTimes(wtaQueryResultDTO.getAverageScheduledTimes());
        workingTimeAgreement.setCareDayChecks(wtaQueryResultDTO.getCareDayChecks());
        workingTimeAgreement.setConsecutiveRestPartOfDays(wtaQueryResultDTO.getConsecutiveRestPartOfDays());
        workingTimeAgreement.setConsecutiveWorks(wtaQueryResultDTO.getConsecutiveWorks());
        workingTimeAgreement.setDailyRestingTimes(wtaQueryResultDTO.getDailyRestingTimes());
        workingTimeAgreement.setDaysOffInPeriods(wtaQueryResultDTO.getDaysOffInPeriods());
        workingTimeAgreement.setDurationBetweenShifts(wtaQueryResultDTO.getDurationBetweenShifts());
        workingTimeAgreement.setNumberOfPartOfDayShifts(wtaQueryResultDTO.getNumberOfPartOfDayShifts());
        workingTimeAgreement.setNumberOfWeekendShiftInPeriods(wtaQueryResultDTO.getNumberOfWeekendShiftInPeriods());
        workingTimeAgreement.setSeniorDaysInYears(wtaQueryResultDTO.getSeniorDaysInYears());
        workingTimeAgreement.setShiftsInIntervals(wtaQueryResultDTO.getShiftsInIntervals());
        workingTimeAgreement.setShortestAndAverageDailyRests(wtaQueryResultDTO.getShortestAndAverageDailyRests());
        workingTimeAgreement.setTimeBanks(wtaQueryResultDTO.getTimeBanks());
        workingTimeAgreement.setVetoPerPeriods(wtaQueryResultDTO.getVetoPerPeriods());
        workingTimeAgreement.setWeeklyRestPeriods(wtaQueryResultDTO.getWeeklyRestPeriods());

    }

    /*public static List<T> copyPhaseTemplateValue(List<Object> source,List<Object> phaseTemplateValues) {
        List<T> phases = null;
        if (phaseTemplateValues != null) {
            phases = new ArrayList<>(4);
            for (T phaseTemplateValueDTO : phaseTemplateValues) {
                T newPhaseTemplateValue = new PhaseTemplateValue();
                BeanUtils.copyProperties(phaseTemplateValueDTO,newPhaseTemplateValue);
                phases.add(newPhaseTemplateValue);
            }
        }
        return phases;
    }*/


    public static List<WTARuleTemplateDTO> getRuleTemplateDTO(WorkingTimeAgreement workingTimeAgreement) {
        List<WTARuleTemplateDTO> WTARuleTemplateDTOS = new ArrayList<>();
        WTARuleTemplateDTO ruleTemplate = new WTARuleTemplateDTO();
        for (ShiftLengthWTATemplate shiftLengthWTATemplate : workingTimeAgreement.getShiftLengths()) {
            ruleTemplate = new WTARuleTemplateDTO();
            BeanUtils.copyProperties(shiftLengthWTATemplate,ruleTemplate);
            WTARuleTemplateDTOS.add(ruleTemplate);
        }
        for (NumberOfPartOfDayShiftsWTATemplate numberOfPartOfDayShiftsWTATemplate : workingTimeAgreement.getNumberOfPartOfDayShifts()) {
            ruleTemplate = new WTARuleTemplateDTO();
            BeanUtils.copyProperties(numberOfPartOfDayShiftsWTATemplate,ruleTemplate);
            WTARuleTemplateDTOS.add(ruleTemplate);
        }
        for (DurationBetweenShiftWTATemplate durationBetweenShiftWTATemplate : workingTimeAgreement.getDurationBetweenShifts()) {
            ruleTemplate = new WTARuleTemplateDTO();
            BeanUtils.copyProperties(durationBetweenShiftWTATemplate,ruleTemplate);
            WTARuleTemplateDTOS.add(ruleTemplate);
        }
        for (DaysOffInPeriodWTATemplate daysOffInPeriodWTATemplate : workingTimeAgreement.getDaysOffInPeriods()) {
            ruleTemplate = new WTARuleTemplateDTO();
            BeanUtils.copyProperties(daysOffInPeriodWTATemplate,ruleTemplate);
            WTARuleTemplateDTOS.add(ruleTemplate);
        }
        for (DailyRestingTimeWTATemplate dailyRestingTimeWTATemplate : workingTimeAgreement.getDailyRestingTimes()) {
            ruleTemplate = new WTARuleTemplateDTO();
            BeanUtils.copyProperties(dailyRestingTimeWTATemplate,ruleTemplate);
            WTARuleTemplateDTOS.add(ruleTemplate);
        }
        for (ConsecutiveWorkWTATemplate consecutiveWorkWTATemplate : workingTimeAgreement.getConsecutiveWorks()) {
            ruleTemplate = new WTARuleTemplateDTO();
            BeanUtils.copyProperties(consecutiveWorkWTATemplate,ruleTemplate);
            WTARuleTemplateDTOS.add(ruleTemplate);
        }
        for (ConsecutiveRestPartOfDayWTATemplate consecutiveRestPartOfDayWTATemplate : workingTimeAgreement.getConsecutiveRestPartOfDays()) {
            ruleTemplate = new WTARuleTemplateDTO();
            BeanUtils.copyProperties(consecutiveRestPartOfDayWTATemplate,ruleTemplate);
            WTARuleTemplateDTOS.add(ruleTemplate);
        }
        for (CareDayCheckWTATemplate careDayCheckWTATemplate : workingTimeAgreement.getCareDayChecks()) {
            ruleTemplate = new WTARuleTemplateDTO();
            BeanUtils.copyProperties(careDayCheckWTATemplate,ruleTemplate);
            WTARuleTemplateDTOS.add(ruleTemplate);
        }
        for (AverageScheduledTimeWTATemplate averageScheduledTimeWTATemplate : workingTimeAgreement.getAverageScheduledTimes()) {
            ruleTemplate = new WTARuleTemplateDTO();
            BeanUtils.copyProperties(averageScheduledTimeWTATemplate,ruleTemplate);
            WTARuleTemplateDTOS.add(ruleTemplate);
        }

        for (WeeklyRestPeriodWTATemplate weeklyRestPeriodWTATemplate : workingTimeAgreement.getWeeklyRestPeriods()) {
            ruleTemplate = new WTARuleTemplateDTO();
            BeanUtils.copyProperties(weeklyRestPeriodWTATemplate,ruleTemplate);
            WTARuleTemplateDTOS.add(ruleTemplate);
        }
        for (VetoPerPeriodWTATemplate vetoPerPeriodWTATemplate : workingTimeAgreement.getVetoPerPeriods()) {
            ruleTemplate = new WTARuleTemplateDTO();
            BeanUtils.copyProperties(vetoPerPeriodWTATemplate,ruleTemplate);
            WTARuleTemplateDTOS.add(ruleTemplate);
        }
        for (TimeBankWTATemplate timeBankWTATemplate : workingTimeAgreement.getTimeBanks()) {
            ruleTemplate = new WTARuleTemplateDTO();
            BeanUtils.copyProperties(timeBankWTATemplate,ruleTemplate);
            WTARuleTemplateDTOS.add(ruleTemplate);
        }
        for (ShortestAndAverageDailyRestWTATemplate shortestAndAverageDailyRestWTATemplate : workingTimeAgreement.getShortestAndAverageDailyRests()) {
            ruleTemplate = new WTARuleTemplateDTO();
            BeanUtils.copyProperties(shortestAndAverageDailyRestWTATemplate,ruleTemplate);
            WTARuleTemplateDTOS.add(ruleTemplate);
        }
        for (ShiftsInIntervalWTATemplate shiftsInIntervalWTATemplate : workingTimeAgreement.getShiftsInIntervals()) {
            ruleTemplate = new WTARuleTemplateDTO();
            BeanUtils.copyProperties(shiftsInIntervalWTATemplate,ruleTemplate);
            WTARuleTemplateDTOS.add(ruleTemplate);
        }
        for (SeniorDaysInYearWTATemplate seniorDaysInYearWTATemplate : workingTimeAgreement.getSeniorDaysInYears()) {
            ruleTemplate = new WTARuleTemplateDTO();
            BeanUtils.copyProperties(seniorDaysInYearWTATemplate,ruleTemplate);
            WTARuleTemplateDTOS.add(ruleTemplate);
        }
        for (NumberOfWeekendShiftInPeriodWTATemplate numberOfWeekendShiftInPeriodWTATemplate : workingTimeAgreement.getNumberOfWeekendShiftInPeriods()) {

            BeanUtils.copyProperties(numberOfWeekendShiftInPeriodWTATemplate,ruleTemplate);
            WTARuleTemplateDTOS.add(ruleTemplate);
        }
        return WTARuleTemplateDTOS;
    }

    public static void copyRuleTemplateToNewWTA(WorkingTimeAgreement oldWta,WorkingTimeAgreement newWTA) {
        newWTA.setShiftLengths(oldWta.getShiftLengths());
        newWTA.setAverageScheduledTimes(oldWta.getAverageScheduledTimes());
        newWTA.setCareDayChecks(oldWta.getCareDayChecks());
        newWTA.setConsecutiveRestPartOfDays(oldWta.getConsecutiveRestPartOfDays());
        newWTA.setConsecutiveWorks(oldWta.getConsecutiveWorks());
        newWTA.setDailyRestingTimes(oldWta.getDailyRestingTimes());
        newWTA.setDaysOffInPeriods(oldWta.getDaysOffInPeriods());
        newWTA.setDurationBetweenShifts(oldWta.getDurationBetweenShifts());
        newWTA.setNumberOfPartOfDayShifts(oldWta.getNumberOfPartOfDayShifts());
        newWTA.setNumberOfWeekendShiftInPeriods(oldWta.getNumberOfWeekendShiftInPeriods());
        newWTA.setSeniorDaysInYears(oldWta.getSeniorDaysInYears());
        newWTA.setShiftsInIntervals(oldWta.getShiftsInIntervals());
        newWTA.setShortestAndAverageDailyRests(oldWta.getShortestAndAverageDailyRests());
        newWTA.setTimeBanks(oldWta.getTimeBanks());
        newWTA.setVetoPerPeriods(oldWta.getVetoPerPeriods());
        newWTA.setWeeklyRestPeriods(oldWta.getWeeklyRestPeriods());
    }




}

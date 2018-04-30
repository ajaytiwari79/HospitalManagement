package com.kairos.activity.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.activity.persistence.enums.WTATemplateType;
import com.kairos.activity.persistence.model.wta.templates.template_types.CareDaysCheckDTO;
import com.kairos.activity.persistence.model.wta.templates.template_types.MaximumSeniorDaysPerYearDTO;
import com.kairos.activity.response.dto.ActivityDTO;
import com.kairos.response.dto.web.wta.*;
import org.apache.commons.beanutils.PropertyUtils;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * @author pradeep
 * @date - 26/4/18
 */

public class ObjectMapperUtils {


    /*public static <T,E> List<T> copyProperties(List<T> objects1, List<E> objects) {
        //List<T> objects = assignBlankObject(objects1.size(), t);
        for (int i = 0; i < objects1.size(); i++) {
            try {

                PropertyUtils.copyProperties(objects1.get(i), );
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        return objects;
    }*/

    /*private static <E> List<E> assignBlankObject(int size, List<E> objects) {
        List<E> objects = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            try {
                objects.add(t.getClass().newInstance());
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        }
        return objects;
    }*/

    public static <T> List<T> copyList(Class<T> klazz) {
        List<T> list = new ArrayList<>();
        Object actuallyT = new Object();
        list.add(klazz.cast(actuallyT));
        try {
            list.add(klazz.getConstructor().newInstance()); // If default constructor
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        return list;
    }

    public static <T extends Object> List<T> copyPropertiesByObjectMapper(List<T> objects1, T Object) {

        ObjectMapper mapper = new ObjectMapper();
        return mapper.convertValue(mapper, new TypeReference<List<T>>() {
        });
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
        switch ((WTATemplateType)ruleTemplate.get("wtaTemplateType")) {
            case SHIFT_LENGTH:
                ShiftLengthWTATemplateDTO shiftLengthWTATemplate = new ShiftLengthWTATemplateDTO();
                copyProperties(ruleTemplate,shiftLengthWTATemplate);
                shiftLengthWTATemplate.setRuleTemplateCategoryId((BigInteger) ruleTemplate.get("ruleTemplateCategoryId"));
                wtaBaseRuleTemplate = shiftLengthWTATemplate;
                break;
            case CONSECUTIVE_WORKING_PARTOFDAY:
                ConsecutiveWorkWTATemplateDTO consecutiveWorkWTATemplate = new ConsecutiveWorkWTATemplateDTO();
                copyProperties(ruleTemplate,consecutiveWorkWTATemplate);
                consecutiveWorkWTATemplate.setRuleTemplateCategoryId((BigInteger) ruleTemplate.get("ruleTemplateCategoryId"));
                wtaBaseRuleTemplate = consecutiveWorkWTATemplate;
                break;

          /*  case CONSECUTIVE_NIGHTS_AND_DAYS:
                ConsecutiveRestPartOfDayWTATemplateDTO consecutiveRestPartOfDayWTATemplate = new ConsecutiveRestPartOfDayWTATemplateDTO();
                copyProperties(ruleTemplate,consecutiveRestPartOfDayWTATemplate);
                consecutiveRestPartOfDayWTATemplate.setRuleTemplateCategoryId((BigInteger) ruleTemplate.get("ruleTemplateCategoryId"));
                wtaBaseRuleTemplate = consecutiveRestPartOfDayWTATemplate;
                break;*/
            case REST_IN_CONSECUTIVE_DAYS_AND_NIGHTS:
                ConsecutiveRestPartOfDayWTATemplateDTO consecutiveRestPartOfDayWTATemplate1 = new ConsecutiveRestPartOfDayWTATemplateDTO();
                copyProperties(ruleTemplate,consecutiveRestPartOfDayWTATemplate1);
                consecutiveRestPartOfDayWTATemplate1.setRuleTemplateCategoryId((BigInteger) ruleTemplate.get("ruleTemplateCategoryId"));
                wtaBaseRuleTemplate = consecutiveRestPartOfDayWTATemplate1;
                break;
            case NUMBER_OF_PARTOFDAY:
                NumberOfPartOfDayShiftsWTATemplateDTO numberOfPartOfDayShiftsWTATemplate = new NumberOfPartOfDayShiftsWTATemplateDTO();
                copyProperties(ruleTemplate,numberOfPartOfDayShiftsWTATemplate);
                numberOfPartOfDayShiftsWTATemplate.setRuleTemplateCategoryId((BigInteger) ruleTemplate.get("ruleTemplateCategoryId"));
                wtaBaseRuleTemplate = numberOfPartOfDayShiftsWTATemplate;
                break;
            case DAYS_OFF_IN_PERIOD:
                DaysOffInPeriodWTATemplateDTO daysOffInPeriodWTATemplate = new DaysOffInPeriodWTATemplateDTO();
                copyProperties(ruleTemplate,daysOffInPeriodWTATemplate);
                daysOffInPeriodWTATemplate.setRuleTemplateCategoryId((BigInteger) ruleTemplate.get("ruleTemplateCategoryId"));
                wtaBaseRuleTemplate = daysOffInPeriodWTATemplate;
                break;
            case AVERAGE_SHEDULED_TIME:
                AverageScheduledTimeWTATemplateDTO averageScheduledTimeWTATemplate = new AverageScheduledTimeWTATemplateDTO();
                copyProperties(ruleTemplate,averageScheduledTimeWTATemplate);
                averageScheduledTimeWTATemplate.setRuleTemplateCategoryId((BigInteger) ruleTemplate.get("ruleTemplateCategoryId"));
                wtaBaseRuleTemplate = averageScheduledTimeWTATemplate;
                break;
            case VETO_PER_PERIOD:
                VetoPerPeriodWTATemplateDTO vetoPerPeriodWTATemplate = new VetoPerPeriodWTATemplateDTO();
                copyProperties(ruleTemplate,vetoPerPeriodWTATemplate);
                vetoPerPeriodWTATemplate.setRuleTemplateCategoryId((BigInteger) ruleTemplate.get("ruleTemplateCategoryId"));
                wtaBaseRuleTemplate = vetoPerPeriodWTATemplate;
                break;
            case NUMBER_OF_WEEKEND_SHIFT_IN_PERIOD:
                NumberOfWeekendShiftsInPeriodWTATemplateDTO numberOfWeekendShiftsInPeriodWTATemplate = new NumberOfWeekendShiftsInPeriodWTATemplateDTO();
                copyProperties(ruleTemplate,numberOfWeekendShiftsInPeriodWTATemplate);
                numberOfWeekendShiftsInPeriodWTATemplate.setRuleTemplateCategoryId((BigInteger) ruleTemplate.get("ruleTemplateCategoryId"));
                wtaBaseRuleTemplate = numberOfWeekendShiftsInPeriodWTATemplate;
                break;
            case CARE_DAYS_CHECK:
                CareDayCheckWTATemplateDTO careDayCheckWTATemplate = new CareDayCheckWTATemplateDTO();
                copyProperties(ruleTemplate,careDayCheckWTATemplate);
                careDayCheckWTATemplate.setRuleTemplateCategoryId((BigInteger) ruleTemplate.get("ruleTemplateCategoryId"));
                wtaBaseRuleTemplate = careDayCheckWTATemplate;
                break;
            case DAILY_RESTING_TIME:
                DailyRestingTimeWTATemplateDTO dailyRestingTimeWTATemplate = new DailyRestingTimeWTATemplateDTO();
                copyProperties(ruleTemplate,dailyRestingTimeWTATemplate);
                dailyRestingTimeWTATemplate.setRuleTemplateCategoryId((BigInteger) ruleTemplate.get("ruleTemplateCategoryId"));
                wtaBaseRuleTemplate = dailyRestingTimeWTATemplate;
                break;
            case DURATION_BETWEEN_SHIFTS:
                DurationBetweenShiftsWTATemplateDTO durationBetweenShiftsWTATemplate = new DurationBetweenShiftsWTATemplateDTO();
                copyProperties(ruleTemplate,durationBetweenShiftsWTATemplate);
                durationBetweenShiftsWTATemplate.setRuleTemplateCategoryId((BigInteger) ruleTemplate.get("ruleTemplateCategoryId"));
                wtaBaseRuleTemplate = durationBetweenShiftsWTATemplate;
                break;
            case WEEKLY_REST_PERIOD:
                WeeklyRestPeriodWTATemplateDTO weeklyRestPeriodWTATemplate = new WeeklyRestPeriodWTATemplateDTO();
                copyProperties(ruleTemplate,weeklyRestPeriodWTATemplate);
                weeklyRestPeriodWTATemplate.setRuleTemplateCategoryId((BigInteger) ruleTemplate.get("ruleTemplateCategoryId"));
                wtaBaseRuleTemplate = weeklyRestPeriodWTATemplate;
                break;
            case SHORTEST_AND_AVERAGE_DAILY_REST:
                ShortestAndAverageDailyRestWTATemplateDTO shortestAndAverageDailyRestWTATemplate = new ShortestAndAverageDailyRestWTATemplateDTO();
                copyProperties(ruleTemplate,shortestAndAverageDailyRestWTATemplate);
                shortestAndAverageDailyRestWTATemplate.setRuleTemplateCategoryId((BigInteger) ruleTemplate.get("ruleTemplateCategoryId"));
                wtaBaseRuleTemplate = shortestAndAverageDailyRestWTATemplate;
                break;
            case NUMBER_OF_SHIFTS_IN_INTERVAL:
                ShiftsInIntervalWTATemplateDTO shiftsInIntervalWTATemplate = new ShiftsInIntervalWTATemplateDTO();
                copyProperties(ruleTemplate,shiftsInIntervalWTATemplate);
                shiftsInIntervalWTATemplate.setRuleTemplateCategoryId((BigInteger) ruleTemplate.get("ruleTemplateCategoryId"));
                wtaBaseRuleTemplate = shiftsInIntervalWTATemplate;
                break;
            case MAXIMUM_SENIOR_DAYS_IN_YEAR:
                SeniorDaysInYearWTATemplateDTO seniorDaysInYearWTATemplate = new SeniorDaysInYearWTATemplateDTO();
                copyProperties(ruleTemplate,seniorDaysInYearWTATemplate);
                seniorDaysInYearWTATemplate.setRuleTemplateCategoryId((BigInteger) ruleTemplate.get("ruleTemplateCategoryId"));
                wtaBaseRuleTemplate = seniorDaysInYearWTATemplate;
                break;
            case TIME_BANK:
                TimeBankWTATemplateDTO timeBankWTATemplate = new TimeBankWTATemplateDTO();
                copyProperties(ruleTemplate,timeBankWTATemplate);
                timeBankWTATemplate.setRuleTemplateCategoryId((BigInteger) ruleTemplate.get("ruleTemplateCategoryId"));
                wtaBaseRuleTemplate = timeBankWTATemplate;
                break;
            case MAXIMUM_SENIOR_DAYS_PER_YEAR:
                MaximumSeniorDaysPerYearDTO maximumSeniorDaysPerYear=new MaximumSeniorDaysPerYearDTO();
                copyProperties(ruleTemplate,maximumSeniorDaysPerYear);
                maximumSeniorDaysPerYear.setRuleTemplateCategoryId((BigInteger) ruleTemplate.get("ruleTemplateCategoryId"));
                wtaBaseRuleTemplate=maximumSeniorDaysPerYear;
                break;
            case CHILD_CARE_DAYS_CHECK:
                CareDaysCheckDTO careDaysCheck=new CareDaysCheckDTO();
                copyProperties(ruleTemplate,careDaysCheck);
                careDaysCheck.setRuleTemplateCategoryId((BigInteger) ruleTemplate.get("ruleTemplateCategoryId"));
                wtaBaseRuleTemplate=careDaysCheck;
                break;
        }
        return wtaBaseRuleTemplate;
    }

    public static <T> T copyPropertiesByMapper(Object object,Class<T> valueType){
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            return objectMapper.readValue(objectMapper.writeValueAsString(object), valueType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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


}

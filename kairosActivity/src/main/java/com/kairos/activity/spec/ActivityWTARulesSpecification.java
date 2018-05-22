package com.kairos.activity.spec;

import com.kairos.activity.client.dto.TimeSlotWrapper;
import com.kairos.activity.client.dto.staff.StaffAdditionalInfoDTO;
import com.kairos.activity.custom_exception.ActionNotPermittedException;
import com.kairos.activity.custom_exception.DataNotFoundByIdException;
import com.kairos.activity.custom_exception.DataNotFoundException;
import com.kairos.activity.enums.RuleTemplates;
import com.kairos.activity.persistence.model.activity.Activity;
import com.kairos.activity.persistence.model.activity.Shift;
import com.kairos.activity.persistence.model.phase.Phase;


import com.kairos.activity.persistence.model.wta.WTAQueryResultDTO;
import com.kairos.activity.persistence.model.wta.templates.WTABaseRuleTemplate;
import com.kairos.activity.persistence.model.wta.templates.template_types.*;
import com.kairos.activity.response.dto.ShiftQueryResultWithActivity;
import com.kairos.activity.service.exception.ExceptionService;
import com.kairos.activity.util.DateUtils;
import com.kairos.activity.util.ObjectMapperUtils;
import com.kairos.activity.util.WTARuleTemplateValidatorUtility;
import com.kairos.response.dto.web.wta.WTAResponseDTO;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static com.kairos.activity.enums.RuleTemplates.getByTemplateType;
/**
 * Created by vipul on 8/2/18.
 */
public class ActivityWTARulesSpecification extends AbstractActivitySpecification<Activity> {
    Logger logger = LoggerFactory.getLogger(ActivityWTARulesSpecification.class);
    private WTAQueryResultDTO wtaResponseDTO;
    private ShiftQueryResultWithActivity shift;
    private Phase phase;
    private List<ShiftQueryResultWithActivity> shifts;
    private TimeSlotWrapper timeSlotWrapper;
    //private StaffAdditionalInfoDTO staffAdditionalInfoDTO;
    private StaffAdditionalInfoDTO staffAdditionalInfoDTO;
    @Autowired
    private ExceptionService exceptionService;


    public ActivityWTARulesSpecification(WTAQueryResultDTO wtaResponseDTO, Phase phase, ShiftQueryResultWithActivity shift,List<ShiftQueryResultWithActivity> shifts, StaffAdditionalInfoDTO staffAdditionalInfoDTO) {
        this.wtaResponseDTO = wtaResponseDTO;
        this.shift = shift;
        this.phase = phase;
        this.shifts = shifts;
        this.timeSlotWrapper = staffAdditionalInfoDTO.getTimeSlotSets().get(0);
    }



    @Override
    public boolean isSatisfied(Activity activity) {
        if (wtaResponseDTO.getEndDate()!=null && new DateTime(wtaResponseDTO.getEndDate()).isBefore(shift.getEndDate().getTime())) {
            throw new ActionNotPermittedException("WTA is Expired for unit employment.");
        }
        /*for (WTABaseRuleTemplate ruleTemplate : wtaResponseDTO.getRuleTemplates()){
            switch (ruleTemplate.getWtaTemplateType()) {
                case SHIFT_LENGTH:
                    WTARuleTemplateValidatorUtility.checkConstraints(shift,(ShiftLengthWTATemplate)ruleTemplate);
                    break;
                case CONSECUTIVE_WORKING_PARTOFDAY:
                    WTARuleTemplateValidatorUtility.checkConstraints(shifts,shift,(ConsecutiveWorkWTATemplate)ruleTemplate);
                    break;

          *//*  case CONSECUTIVE_NIGHTS_AND_DAYS:
                ConsecutiveRestPartOfDayWTATemplate consecutiveRestPartOfDayWTATemplate = new ConsecutiveRestPartOfDayWTATemplate();
                copyProperties(ruleTemplate,consecutiveRestPartOfDayWTATemplate);
                consecutiveRestPartOfDayWTATemplate.setRuleTemplateCategoryId((BigInteger) ruleTemplate.get("ruleTemplateCategoryId"));
                wtaBaseRuleTemplate = consecutiveRestPartOfDayWTATemplate;
                break;*//*
                case REST_IN_CONSECUTIVE_DAYS_AND_NIGHTS:
                    WTARuleTemplateValidatorUtility.checkConstraints(shifts,(ConsecutiveRestPartOfDayWTATemplate)ruleTemplate,timeSlotWrapper);
                    break;
                case NUMBER_OF_PARTOFDAY:
                    WTARuleTemplateValidatorUtility.checkConstraints(shifts,(NumberOfPartOfDayShiftsWTATemplate)ruleTemplate,timeSlotWrapper);
                    break;
                case DAYS_OFF_IN_PERIOD:
                    WTARuleTemplateValidatorUtility.checkConstraints(shifts,(DaysOffInPeriodWTATemplate)ruleTemplate);
                    break;
                case AVERAGE_SHEDULED_TIME:
                    WTARuleTemplateValidatorUtility.checkConstraints(shift,shifts,(AverageScheduledTimeWTATemplate)ruleTemplate);
                    break;
                case VETO_PER_PERIOD:
                    //WTARuleTemplateValidatorUtility.checkConstraints((VetoPerPeriodWTATemplate)ruleTemplate);
                    break;
                case NUMBER_OF_WEEKEND_SHIFT_IN_PERIOD:
                    WTARuleTemplateValidatorUtility.checkConstraints(shift,shifts,(NumberOfWeekendShiftsInPeriodWTATemplate)ruleTemplate);
                    break;
            *//*case CARE_DAYS_CHECK:
                wtaBaseRuleTemplate = ObjectMapperUtils.copyPropertiesByMapper(ruleTemplate,ChildCareDayCheckWTATemplate.class);
                break;*//*
                case DAILY_RESTING_TIME:
                    WTARuleTemplateValidatorUtility.checkConstraints(shifts,(DailyRestingTimeWTATemplate)ruleTemplate);
                    break;
                case DURATION_BETWEEN_SHIFTS:
                    WTARuleTemplateValidatorUtility.checkConstraints(shifts,shift,(DurationBetweenShiftsWTATemplate)ruleTemplate);
                    break;
                case WEEKLY_REST_PERIOD:
                    WTARuleTemplateValidatorUtility.checkConstraints(shift,shifts,(WeeklyRestPeriodWTATemplate)ruleTemplate);
                    break;
                case SHORTEST_AND_AVERAGE_DAILY_REST:
                    WTARuleTemplateValidatorUtility.checkConstraints(shifts,(ShortestAndAverageDailyRestWTATemplate)ruleTemplate);
                    break;
                case NUMBER_OF_SHIFTS_IN_INTERVAL:
                    WTARuleTemplateValidatorUtility.checkConstraints(shift,shifts,(ShiftsInIntervalWTATemplate)ruleTemplate);
                    break;
                case TIME_BANK:
                    //WTARuleTemplateValidatorUtility.checkConstraints((TimeBankWTATemplate)ruleTemplate);
                    break;
                case SENIOR_DAYS_PER_YEAR:
                    //WTARuleTemplateValidatorUtility.checkConstraints((SeniorDaysPerYearWTATemplate)ruleTemplate);
                    break;
                case CHILD_CARE_DAYS_CHECK:
                    //WTARuleTemplateValidatorUtility.checkConstraints((ChildCareDaysCheckWTATemplate)ruleTemplate);
                    break;
                case BREAK_IN_SHIFT:
                    //WTARuleTemplateValidatorUtility.checkConstraints((BreaksInShiftWTATemplate)ruleTemplate);
                    break;
                default:
                    throw new DataNotFoundByIdException("Invalid TEMPLATE");
            }
        }*/
        return true;
    }
    private String getTemplateType(String templateType){
        if(!templateType.contains("_")){
            return templateType;
        }
        int lastCharIndex=templateType.lastIndexOf("_");
        if(lastCharIndex>0){
            char nextCharacter=templateType.charAt(lastCharIndex+1);
            if(!Character.isDigit(templateType.charAt(lastCharIndex+1))){
                return templateType;
            }
            else{
                return templateType.substring(0,lastCharIndex);
            }
        }
        return null;
    }
}

package com.kairos.activity.service.wta;


import com.kairos.activity.client.CountryRestClient;
import com.kairos.activity.custom_exception.DataNotFoundByIdException;
import com.kairos.activity.custom_exception.DuplicateDataException;
import com.kairos.activity.persistence.enums.WTATemplateType;
import com.kairos.activity.persistence.model.wta.WTAQueryResultDTO;
import com.kairos.activity.persistence.model.wta.templates.RuleTemplateCategory;
import com.kairos.activity.persistence.model.wta.templates.WTABaseRuleTemplate;
import com.kairos.activity.persistence.model.wta.templates.*;
import com.kairos.activity.persistence.model.wta.templates.template_types.*;
import com.kairos.activity.persistence.repository.wta.RuleTemplateCategoryMongoRepository;
import com.kairos.activity.persistence.repository.wta.WTABaseRuleTemplateMongoRepository;
import com.kairos.activity.service.MongoBaseService;
import com.kairos.activity.service.tag.TagService;
import com.kairos.activity.util.DateUtils;
import com.kairos.activity.util.userContext.CurrentUserDetails;
import com.kairos.activity.util.userContext.UserContext;

import com.kairos.persistence.model.enums.TimeBankTypeEnum;
import com.kairos.response.dto.web.CountryDTO;
import com.kairos.response.dto.web.enums.RuleTemplateCategoryType;
import com.kairos.response.dto.web.wta.RuleTemplateCategoryDTO;
import com.kairos.response.dto.web.wta.WTARuleTemplateDTO;
import com.kairos.response.dto.web.wta.RuleTemplateCategoryTagDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.activity.persistence.enums.WTATemplateType.getByTemplateType;


/**
 * Created by pawanmandhan on 5/8/17.
 */
@Transactional
@Service
public class RuleTemplateService extends MongoBaseService {



    @Inject
    private CountryRestClient countryRestClient;
    @Inject
    private RuleTemplateCategoryMongoRepository ruleTemplateCategoryMongoRepository;
    @Inject
    private TagService tagService;
    @Inject
    private WTABaseRuleTemplateMongoRepository wtaBaseRuleTemplateMongoRepository;
    @Inject
    private WTAOrganizationService wtaOrganizationService;
    @Inject private WTABuilderService wtaBuilderService;
    private final Logger logger = LoggerFactory.getLogger(RuleTemplateService.class);

    public boolean createRuleTemplate(long countryId) {
        CountryDTO countryDTO = countryRestClient.getCountryById(countryId);

        if (countryDTO == null) {
            throw new DataNotFoundByIdException("Invalid Country");
        }


        List<RuleTemplateResponseDTO> wtaBaseRuleTemplates = wtaBaseRuleTemplateMongoRepository.getWTABaseRuleTemplateByCountryId(countryId);
        RuleTemplateCategory ruleTemplateCategory = ruleTemplateCategoryMongoRepository.findByName(countryId, "NONE", RuleTemplateCategoryType.WTA);
        if (!Optional.ofNullable(ruleTemplateCategory).isPresent()) {
            ruleTemplateCategory = new RuleTemplateCategory("NONE","", RuleTemplateCategoryType.WTA);
            ruleTemplateCategory.setCountryId(countryDTO.getId());
            save(ruleTemplateCategory);
        }
        if (Optional.ofNullable(wtaBaseRuleTemplates).isPresent() && !wtaBaseRuleTemplates.isEmpty()) {
            throw new DataNotFoundByIdException("WTA Rule Template already exists");
        }

        String MONTHS = "MONTHS";
        String TUESDAY = "TUESDAY";
        long timeInMins = 10;
        long daysCount = 10;
        long dateInMillis = DateUtils.getCurrentDate().getTime();
        List<WTABaseRuleTemplate> wtaBaseRuleTemplates1 = new ArrayList<>();

        List<PhaseTemplateValue> phaseTemplateValues = new ArrayList<>();
        phaseTemplateValues.add(new PhaseTemplateValue(1,"REQUEST",(short) 0,(short)0,true,0,false));
        phaseTemplateValues.add(new PhaseTemplateValue(2,"PUZZLE",(short) 0,(short)0,true,0,false));
        phaseTemplateValues.add(new PhaseTemplateValue(3,"DRAFT",(short) 0,(short)0,true,0,false));
        phaseTemplateValues.add(new PhaseTemplateValue(4,"CONSTRUCTION",(short) 0,(short)0,true,0,false));

        ShiftLengthWTATemplate shiftLengthWTATemplate = new ShiftLengthWTATemplate("Maximum night shift’s length",true,"Maximum night shift’s length",400,true);
        shiftLengthWTATemplate.setCountryId(countryDTO.getId());
        shiftLengthWTATemplate.setPhaseTemplateValues(phaseTemplateValues);
        shiftLengthWTATemplate.setWTARuleTemplateCategoryId(ruleTemplateCategory.getId());
        wtaBaseRuleTemplates1.add(shiftLengthWTATemplate);

        ConsecutiveWorkWTATemplate consecutiveWorking = new ConsecutiveWorkWTATemplate("Maximum number of consecutive days",true,"Maximum number of consecutive days",true,daysCount);
        consecutiveWorking.setCountryId(countryDTO.getId());
        consecutiveWorking.setPhaseTemplateValues(phaseTemplateValues);
        consecutiveWorking.setWTARuleTemplateCategoryId(ruleTemplateCategory.getId());
        wtaBaseRuleTemplates1.add(consecutiveWorking);

        ConsecutiveRestPartOfDayWTATemplate restInConsecutiveDays = new ConsecutiveRestPartOfDayWTATemplate("Minimum rest after after consecutive days worked",true,"Minimum rest after after consecutive days worked",timeInMins, daysCount);
        restInConsecutiveDays.setCountryId(countryDTO.getId());
        restInConsecutiveDays.setPhaseTemplateValues(phaseTemplateValues);
        restInConsecutiveDays.setWTARuleTemplateCategoryId(ruleTemplateCategory.getId());
        wtaBaseRuleTemplates1.add(restInConsecutiveDays);

        NumberOfPartOfDayShiftsWTATemplate numberOfPartOfDayShiftsWTATemplate = new NumberOfPartOfDayShiftsWTATemplate("Maximum number of shifts per interval",true,"Maximum number of shifts per interval",daysCount);
        numberOfPartOfDayShiftsWTATemplate.setValidationStartDateMillis(dateInMillis);
        numberOfPartOfDayShiftsWTATemplate.setIntervalLength(12);
        numberOfPartOfDayShiftsWTATemplate.setIntervalUnit(MONTHS);
        numberOfPartOfDayShiftsWTATemplate.setCountryId(countryDTO.getId());
        numberOfPartOfDayShiftsWTATemplate.setPhaseTemplateValues(phaseTemplateValues);
        numberOfPartOfDayShiftsWTATemplate.setWTARuleTemplateCategoryId(ruleTemplateCategory.getId());
        wtaBaseRuleTemplates1.add(numberOfPartOfDayShiftsWTATemplate);

        DaysOffInPeriodWTATemplate daysOffInPeriodWTATemplate = new DaysOffInPeriodWTATemplate("Minimum number of days off per period",true,"Minimum number of days off per period",12, dateInMillis, 12, MONTHS);
        daysOffInPeriodWTATemplate.setCountryId(countryDTO.getId());
        daysOffInPeriodWTATemplate.setPhaseTemplateValues(phaseTemplateValues);
        daysOffInPeriodWTATemplate.setWTARuleTemplateCategoryId(ruleTemplateCategory.getId());
        wtaBaseRuleTemplates1.add(daysOffInPeriodWTATemplate);

        AverageScheduledTimeWTATemplate averageScheduledTimeWTATemplate = new AverageScheduledTimeWTATemplate("Maximum average duration per week in an interval",true,"Maximum average duration per week in an interval",12, dateInMillis, true, true, timeInMins, MONTHS);
        averageScheduledTimeWTATemplate.setCountryId(countryDTO.getId());
        averageScheduledTimeWTATemplate.setPhaseTemplateValues(phaseTemplateValues);
        averageScheduledTimeWTATemplate.setWTARuleTemplateCategoryId(ruleTemplateCategory.getId());
        wtaBaseRuleTemplates1.add(averageScheduledTimeWTATemplate);

        VetoPerPeriodWTATemplate vetoPerPeriodWTATemplate = new VetoPerPeriodWTATemplate("Maximum veto per period",true,"Maximum veto per period",2.0);
        vetoPerPeriodWTATemplate.setCountryId(countryDTO.getId());
        vetoPerPeriodWTATemplate.setPhaseTemplateValues(phaseTemplateValues);
        vetoPerPeriodWTATemplate.setWTARuleTemplateCategoryId(ruleTemplateCategory.getId());
        wtaBaseRuleTemplates1.add(vetoPerPeriodWTATemplate);

        NumberOfWeekendShiftsInPeriodWTATemplate numberofWeekendShiftsInPeriodWTATemplate = new NumberOfWeekendShiftsInPeriodWTATemplate("Weekend off Distribution",true,"Weekend off Distribution",12, 12, TUESDAY, 2, true, TUESDAY, 1);
        numberofWeekendShiftsInPeriodWTATemplate.setCountryId(countryDTO.getId());
        numberofWeekendShiftsInPeriodWTATemplate.setPhaseTemplateValues(phaseTemplateValues);
        numberofWeekendShiftsInPeriodWTATemplate.setWTARuleTemplateCategoryId(ruleTemplateCategory.getId());
        wtaBaseRuleTemplates1.add(numberofWeekendShiftsInPeriodWTATemplate);

        CareDayCheckWTATemplate careDayCheckWTATemplate = new CareDayCheckWTATemplate("Care days check",true,"Care days check",2, dateInMillis, MONTHS, 1);
        careDayCheckWTATemplate.setCountryId(countryDTO.getId());
        careDayCheckWTATemplate.setPhaseTemplateValues(phaseTemplateValues);
        careDayCheckWTATemplate.setWTARuleTemplateCategoryId(ruleTemplateCategory.getId());
        wtaBaseRuleTemplates1.add(careDayCheckWTATemplate);

        DailyRestingTimeWTATemplate dailyRestingTimeWTATemplate = new DailyRestingTimeWTATemplate("Minimum resting hours daily",true,"Minimum resting hours daily",timeInMins);
        dailyRestingTimeWTATemplate.setCountryId(countryDTO.getId());
        dailyRestingTimeWTATemplate.setPhaseTemplateValues(phaseTemplateValues);
        dailyRestingTimeWTATemplate.setWTARuleTemplateCategoryId(ruleTemplateCategory.getId());
        wtaBaseRuleTemplates1.add(dailyRestingTimeWTATemplate);


        DurationBetweenShiftsWTATemplate durationBetweenShiftsWTATemplate = new DurationBetweenShiftsWTATemplate("Minimum duuration between shifts",true,"Minimum duuration between shifts",timeInMins);
        durationBetweenShiftsWTATemplate.setCountryId(countryDTO.getId());
        durationBetweenShiftsWTATemplate.setPhaseTemplateValues(phaseTemplateValues);
        durationBetweenShiftsWTATemplate.setWTARuleTemplateCategoryId(ruleTemplateCategory.getId());
        wtaBaseRuleTemplates1.add(durationBetweenShiftsWTATemplate);


        WeeklyRestPeriodWTATemplate weeklyRestPeriodWTATemplate = new WeeklyRestPeriodWTATemplate("Minimum rest period in an interval",true,"Minimum rest period in an interval",timeInMins);
        weeklyRestPeriodWTATemplate.setCountryId(countryDTO.getId());
        weeklyRestPeriodWTATemplate.setPhaseTemplateValues(phaseTemplateValues);
        weeklyRestPeriodWTATemplate.setWTARuleTemplateCategoryId(ruleTemplateCategory.getId());
        wtaBaseRuleTemplates1.add(weeklyRestPeriodWTATemplate);

        ShortestAndAverageDailyRestWTATemplate shortestAndAverageDailyRestWTATemplate = new ShortestAndAverageDailyRestWTATemplate("Shortest and Average daily Rest",true,"Shortest and Average daily Rest",1, "NA", dateInMillis, timeInMins, timeInMins, "");
        shortestAndAverageDailyRestWTATemplate.setCountryId(countryDTO.getId());
        shortestAndAverageDailyRestWTATemplate.setPhaseTemplateValues(phaseTemplateValues);
        shortestAndAverageDailyRestWTATemplate.setWTARuleTemplateCategoryId(ruleTemplateCategory.getId());
        wtaBaseRuleTemplates1.add(shortestAndAverageDailyRestWTATemplate);

        ShiftsInIntervalWTATemplate maximumShiftsInIntervalWTATemplate = new ShiftsInIntervalWTATemplate("Shifts In Interval",true,"Shifts In Interval",1, "NA", dateInMillis, 1, true);
        maximumShiftsInIntervalWTATemplate.setCountryId(countryDTO.getId());
        maximumShiftsInIntervalWTATemplate.setPhaseTemplateValues(phaseTemplateValues);
        maximumShiftsInIntervalWTATemplate.setWTARuleTemplateCategoryId(ruleTemplateCategory.getId());
        wtaBaseRuleTemplates1.add(maximumShiftsInIntervalWTATemplate);

        SeniorDaysInYearWTATemplate seniorDaysInYearWTATemplate = new SeniorDaysInYearWTATemplate("Maximum senior days per year",true,"Maximum senior days per year",1, "NA", dateInMillis, 1, "");
        seniorDaysInYearWTATemplate.setCountryId(countryDTO.getId());
        seniorDaysInYearWTATemplate.setPhaseTemplateValues(phaseTemplateValues);
        seniorDaysInYearWTATemplate.setWTARuleTemplateCategoryId(ruleTemplateCategory.getId());
        wtaBaseRuleTemplates1.add(seniorDaysInYearWTATemplate);

        TimeBankWTATemplate timeBankWTATemplate = new TimeBankWTATemplate("Maximum Time Bank",true, "Maximum Time Bank",TimeBankTypeEnum.HOURLY,45, false, false);
        timeBankWTATemplate.setCountryId(countryDTO.getId());
        timeBankWTATemplate.setPhaseTemplateValues(phaseTemplateValues);
        timeBankWTATemplate.setWTARuleTemplateCategoryId(ruleTemplateCategory.getId());
        wtaBaseRuleTemplates1.add(timeBankWTATemplate);
        save(wtaBaseRuleTemplates1);


        return true;
    }

    public Map getRuleTemplate(long countryId) {
        CountryDTO country = countryRestClient.getCountryById(countryId);
        if (country == null) {
            throw new DataNotFoundByIdException("Invalid Country");
        }

        List<RuleTemplateCategoryTagDTO> categoryList = ruleTemplateCategoryMongoRepository.getAllRulesOfCountry(countryId);

        if (categoryList == null) {
            throw new DataNotFoundByIdException("Category List is null");
        }

        List<RuleTemplateResponseDTO> templateList = wtaBaseRuleTemplateMongoRepository.getWTABaseRuleTemplateByCountryId(countryId);
        if (templateList == null) {
            throw new DataNotFoundByIdException("Template List is null");
        }

        assignCategoryToRuleTemplate(categoryList,templateList);
        Map response = new HashMap();
        response.put("categoryList", categoryList);
        response.put("templateList", templateList);

        return response;
    }

    public void assignCategoryToRuleTemplate(List<RuleTemplateCategoryTagDTO> categoryList,List<RuleTemplateResponseDTO> templateList){
        for (RuleTemplateCategoryTagDTO ruleTemplateCategoryTagDTO : categoryList) {
            for (RuleTemplateResponseDTO ruleTemplateResponseDTO : templateList) {
                if(ruleTemplateCategoryTagDTO.getRuleTemplateIds()!=null && ruleTemplateCategoryTagDTO.getRuleTemplateIds().contains(ruleTemplateResponseDTO.getId())){
                    RuleTemplateCategoryDTO ruleTemplateCategoryDTO = new RuleTemplateCategoryDTO();
                    BeanUtils.copyProperties(ruleTemplateCategoryTagDTO,ruleTemplateCategoryDTO);
                    ruleTemplateResponseDTO.setRuleTemplateCategory(ruleTemplateCategoryDTO);
                    ruleTemplateResponseDTO.setTemplateType("MAXIMUM_SHIFT_LENGTH");
                }
            }
        }
    }

    public void saveRuleTempates(WTAQueryResultDTO wtaQueryResultDTO){
        /*if(wtaQueryResultDTO.getShiftLengths().isEmpty()){
            save(wtaQueryResultDTO.getShiftLengths());
        }
        if(wtaQueryResultDTO.getShiftLengths().isEmpty()){
            save(wtaQueryResultDTO.getShiftLengths());
        }
        if(wtaQueryResultDTO.getShortestAndAverageDailyRests().isEmpty()){
            save(wtaQueryResultDTO.getShortestAndAverageDailyRests());
        }
        if(wtaQueryResultDTO.getShiftsInIntervals().isEmpty()){
            save(wtaQueryResultDTO.getShiftsInIntervals());
        }
        if(wtaQueryResultDTO.getSeniorDaysInYears().isEmpty()){
            save(wtaQueryResultDTO.getSeniorDaysInYears());
        }
        if(wtaQueryResultDTO.getNumberOfWeekendShiftInPeriods().isEmpty()){
            save(wtaQueryResultDTO.getNumberOfWeekendShiftInPeriods());
        }
        if(wtaQueryResultDTO.getNumberOfPartOfDayShifts().isEmpty()){
            save(wtaQueryResultDTO.getNumberOfPartOfDayShifts());
        }
        if(wtaQueryResultDTO.getDaysOffInPeriods().isEmpty()){
            save(wtaQueryResultDTO.getDaysOffInPeriods());
        }
        if(wtaQueryResultDTO.getDurationBetweenShifts().isEmpty()){
            save(wtaQueryResultDTO.getDurationBetweenShifts());
        }
        if(wtaQueryResultDTO.getDailyRestingTimes().isEmpty()){
            save(wtaQueryResultDTO.getDailyRestingTimes());
        }
        if(wtaQueryResultDTO.getConsecutiveWorks().isEmpty()){
            save(wtaQueryResultDTO.getConsecutiveWorks());
        }
        if(wtaQueryResultDTO.getConsecutiveRestPartOfDays().isEmpty()){
            save(wtaQueryResultDTO.getConsecutiveRestPartOfDays());
        }
        if(wtaQueryResultDTO.getCareDayChecks().isEmpty()){
            save(wtaQueryResultDTO.getCareDayChecks());
        }
        if(wtaQueryResultDTO.getAverageScheduledTimes().isEmpty()){
            save(wtaQueryResultDTO.getAverageScheduledTimes());
        }
        if(wtaQueryResultDTO.getVetoPerPeriods().isEmpty()){
            save(wtaQueryResultDTO.getVetoPerPeriods());
        }
        if(wtaQueryResultDTO.getWeeklyRestPeriods().isEmpty()){
            save(wtaQueryResultDTO.getWeeklyRestPeriods());
        }
        if(wtaQueryResultDTO.getTimeBanks().isEmpty()){
            save(wtaQueryResultDTO.getTimeBanks());
        }*/
    }


    public WTARuleTemplateDTO updateRuleTemplate(long countryId, WTARuleTemplateDTO templateDTO) {
        CountryDTO country = countryRestClient.getCountryById(countryId);
        if (!Optional.ofNullable(country).isPresent()) {
            throw new DataNotFoundByIdException("Invalid Country");
        }

        WTABaseRuleTemplate oldTemplate = wtaBaseRuleTemplateMongoRepository.findOne(templateDTO.getId());
        if (!Optional.ofNullable(oldTemplate).isPresent()) {
            throw new DataNotFoundByIdException("Invalid TemplateType id " + templateDTO.getId());
        }
        String templateType=getTemplateType(templateDTO.getTemplateType());
        WTATemplateType ruleTemplateType = getByTemplateType(templateType);
        logger.info("templateType:"+templateType);
        switch (ruleTemplateType) {
            case SHIFT_LENGTH:
                ShiftLengthWTATemplate shiftLengthWTATemplate = (ShiftLengthWTATemplate) oldTemplate;//oldTemplate;
                shiftLengthWTATemplate.setDescription(templateDTO.getDescription());
                shiftLengthWTATemplate.setTimeLimit(templateDTO.getTimeLimit());
                shiftLengthWTATemplate.setCheckAgainstTimeRules(templateDTO.getCheckAgainstTimeRules());
                break;
            case CONSECUTIVE_WORKING_PARTOFDAY:
                ConsecutiveWorkWTATemplate consecutiveWorkingDays = (ConsecutiveWorkWTATemplate) oldTemplate;
                consecutiveWorkingDays.setDescription(templateDTO.getDescription());
                consecutiveWorkingDays.setLimitCount(templateDTO.getDaysLimit());
                consecutiveWorkingDays.setCheckAgainstTimeRules(templateDTO.getCheckAgainstTimeRules());
                break;
            case REST_IN_CONSECUTIVE_DAYS_AND_NIGHTS:
                ConsecutiveRestPartOfDayWTATemplate consecutiveRestPartOfDay = (ConsecutiveRestPartOfDayWTATemplate) oldTemplate;
                consecutiveRestPartOfDay.setDescription(templateDTO.getDescription());
                consecutiveRestPartOfDay.setMinimumRest(templateDTO.getMinimumRest());
                consecutiveRestPartOfDay.setDaysWorked(templateDTO.getDaysWorked());
                break;
            case NUMBER_OF_PARTOFDAY:
                NumberOfPartOfDayShiftsWTATemplate numberOfPartOfDayShifts = (NumberOfPartOfDayShiftsWTATemplate) oldTemplate;
                numberOfPartOfDayShifts.setDescription(templateDTO.getDescription());
                numberOfPartOfDayShifts.setNoOfPartOfDayWorked(templateDTO.getNightsWorked());
                numberOfPartOfDayShifts.setIntervalLength(templateDTO.getIntervalLength());
                numberOfPartOfDayShifts.setIntervalUnit(templateDTO.getIntervalUnit());
                numberOfPartOfDayShifts.setValidationStartDateMillis(templateDTO.getValidationStartDateMillis());
                break;
            case DAYS_OFF_IN_PERIOD:
                DaysOffInPeriodWTATemplate maximumDaysOffInPeriodWTATemplate = (DaysOffInPeriodWTATemplate) oldTemplate;
                maximumDaysOffInPeriodWTATemplate.setDescription(templateDTO.getDescription());
                maximumDaysOffInPeriodWTATemplate.setIntervalLength(templateDTO.getIntervalLength());
                maximumDaysOffInPeriodWTATemplate.setIntervalUnit(templateDTO.getIntervalUnit());
                maximumDaysOffInPeriodWTATemplate.setValidationStartDateMillis(templateDTO.getValidationStartDateMillis());
                maximumDaysOffInPeriodWTATemplate.setDaysLimit(templateDTO.getDaysLimit());
                break;
            case AVERAGE_SHEDULED_TIME:
                AverageScheduledTimeWTATemplate maximumAverageScheduledTimeWTATemplate = (AverageScheduledTimeWTATemplate) oldTemplate;
                maximumAverageScheduledTimeWTATemplate.setDescription(templateDTO.getDescription());
                maximumAverageScheduledTimeWTATemplate.setUseShiftTimes(templateDTO.getUseShiftTimes());
                maximumAverageScheduledTimeWTATemplate.setIntervalLength(templateDTO.getIntervalLength());
                maximumAverageScheduledTimeWTATemplate.setIntervalUnit(templateDTO.getIntervalUnit());
                maximumAverageScheduledTimeWTATemplate.setMaximumAvgTime(templateDTO.getMaximumAvgTime());
                maximumAverageScheduledTimeWTATemplate.setValidationStartDateMillis(templateDTO.getValidationStartDateMillis());
                maximumAverageScheduledTimeWTATemplate.setBalanceAdjustment(templateDTO.getBalanceAdjustment());
                break;
            case VETO_PER_PERIOD:
                VetoPerPeriodWTATemplate maximumVetoPerPeriodWTATemplate = (VetoPerPeriodWTATemplate) oldTemplate;
                maximumVetoPerPeriodWTATemplate.setDescription(templateDTO.getDescription());
                maximumVetoPerPeriodWTATemplate.setMaximumVetoPercentage(templateDTO.getMaximumVetoPercentage());
                break;
            case NUMBER_OF_WEEKEND_SHIFT_IN_PERIOD:
                NumberOfWeekendShiftsInPeriodWTATemplate numberOfWeekendShiftsInPeriodWTATemplate = (NumberOfWeekendShiftsInPeriodWTATemplate) oldTemplate;
                numberOfWeekendShiftsInPeriodWTATemplate.setDescription(templateDTO.getDescription());
                numberOfWeekendShiftsInPeriodWTATemplate.setNumberShiftsPerPeriod(templateDTO.getNumberShiftsPerPeriod());
                numberOfWeekendShiftsInPeriodWTATemplate.setNumberOfWeeks(templateDTO.getNumberOfWeeks());
                numberOfWeekendShiftsInPeriodWTATemplate.setFromDayOfWeek(templateDTO.getFromDayOfWeek());
                numberOfWeekendShiftsInPeriodWTATemplate.setFromTime(templateDTO.getFromTime());
                numberOfWeekendShiftsInPeriodWTATemplate.setToTime(templateDTO.getToTime());
                numberOfWeekendShiftsInPeriodWTATemplate.setToDayOfWeek(templateDTO.getToDayOfWeek());
                numberOfWeekendShiftsInPeriodWTATemplate.setProportional(templateDTO.getProportional());
                break;
            case CARE_DAYS_CHECK:
                CareDayCheckWTATemplate careDayCheckWTATemplate = (CareDayCheckWTATemplate) oldTemplate;
                careDayCheckWTATemplate.setDescription(templateDTO.getDescription());
                careDayCheckWTATemplate.setIntervalLength(templateDTO.getIntervalLength());
                careDayCheckWTATemplate.setIntervalUnit(templateDTO.getIntervalUnit());
                careDayCheckWTATemplate.setDaysLimit(templateDTO.getDaysLimit());
                careDayCheckWTATemplate.setValidationStartDateMillis(templateDTO.getValidationStartDateMillis());
                break;
            case DAILY_RESTING_TIME:
                DailyRestingTimeWTATemplate minimumDailyRestingTimeWTATemplate = (DailyRestingTimeWTATemplate) oldTemplate;
                minimumDailyRestingTimeWTATemplate.setId(oldTemplate.getId());
                minimumDailyRestingTimeWTATemplate.setDescription(templateDTO.getDescription());
                minimumDailyRestingTimeWTATemplate.setContinuousDayRestHours(templateDTO.getContinuousDayRestHours());
                break;
            case DURATION_BETWEEN_SHIFTS:
                DurationBetweenShiftsWTATemplate minimumDurationBetweenShiftsWTATemplate = (DurationBetweenShiftsWTATemplate) oldTemplate;
                minimumDurationBetweenShiftsWTATemplate.setDescription(templateDTO.getDescription());
                minimumDurationBetweenShiftsWTATemplate.setDurationBetweenShifts(templateDTO.getMinimumDurationBetweenShifts());
                break;
            case WEEKLY_REST_PERIOD:
                WeeklyRestPeriodWTATemplate minimumWeeklyRestPeriodWTATemplate = (WeeklyRestPeriodWTATemplate) oldTemplate;
                minimumWeeklyRestPeriodWTATemplate.setDescription(templateDTO.getDescription());
                minimumWeeklyRestPeriodWTATemplate.setContinuousWeekRest(templateDTO.getContinuousWeekRest());
                break;
            case SHORTEST_AND_AVERAGE_DAILY_REST:
                ShortestAndAverageDailyRestWTATemplate shortestAndAverageDailyRestWTATemplate = (ShortestAndAverageDailyRestWTATemplate) oldTemplate;
                shortestAndAverageDailyRestWTATemplate.setDescription(templateDTO.getDescription());
                shortestAndAverageDailyRestWTATemplate.setIntervalLength(templateDTO.getIntervalLength());
                shortestAndAverageDailyRestWTATemplate.setIntervalUnit(templateDTO.getIntervalUnit());
                shortestAndAverageDailyRestWTATemplate.setValidationStartDateMillis(templateDTO.getValidationStartDateMillis());
                shortestAndAverageDailyRestWTATemplate.setContinuousDayRestHours(templateDTO.getContinuousDayRestHours());
                shortestAndAverageDailyRestWTATemplate.setAverageRest(templateDTO.getAverageRest());
                shortestAndAverageDailyRestWTATemplate.setShiftAffiliation(templateDTO.getShiftAffiliation());
                break;
            case NUMBER_OF_SHIFTS_IN_INTERVAL:
                ShiftsInIntervalWTATemplate maximumShiftsInIntervalWTATemplate = (ShiftsInIntervalWTATemplate) oldTemplate;
                maximumShiftsInIntervalWTATemplate.setDescription(templateDTO.getDescription());
                maximumShiftsInIntervalWTATemplate.setIntervalLength(templateDTO.getIntervalLength());
                maximumShiftsInIntervalWTATemplate.setIntervalUnit(templateDTO.getIntervalUnit());
                maximumShiftsInIntervalWTATemplate.setValidationStartDateMillis(templateDTO.getValidationStartDateMillis());
                maximumShiftsInIntervalWTATemplate.setShiftsLimit(templateDTO.getShiftsLimit());
                maximumShiftsInIntervalWTATemplate.setOnlyCompositeShifts(templateDTO.getOnlyCompositeShifts());
                break;
            case MAXIMUM_SENIOR_DAYS_IN_YEAR:
                SeniorDaysInYearWTATemplate maximumSeniorDaysInYearWTATemplate = (SeniorDaysInYearWTATemplate) oldTemplate;
                maximumSeniorDaysInYearWTATemplate.setDescription(templateDTO.getDescription());
                maximumSeniorDaysInYearWTATemplate.setIntervalLength(templateDTO.getIntervalLength());
                maximumSeniorDaysInYearWTATemplate.setIntervalUnit(templateDTO.getIntervalUnit());
                maximumSeniorDaysInYearWTATemplate.setValidationStartDateMillis(templateDTO.getValidationStartDateMillis());
                maximumSeniorDaysInYearWTATemplate.setDaysLimit(templateDTO.getDaysLimit());
                maximumSeniorDaysInYearWTATemplate.setActivityCode(templateDTO.getActivityCode());
                break;
            case TIME_BANK:
                TimeBankWTATemplate minimumTimeBank = (TimeBankWTATemplate) oldTemplate;
                minimumTimeBank.setDescription(templateDTO.getDescription());
                minimumTimeBank.setFrequency(templateDTO.getFrequency());
                minimumTimeBank.setYellowZone(templateDTO.getYellowZone());
                minimumTimeBank.setForbid(templateDTO.isForbid());
                minimumTimeBank.setAllowExtraActivity(templateDTO.isAllowExtraActivity());
                break;
            default:
                throw new DataNotFoundByIdException("Invalid TEMPLATE");
        }
        BigInteger ruleTemplateCategoryId = checkAndAssignRuleTemplateCategory(oldTemplate, templateDTO);
        oldTemplate.setWTARuleTemplateCategoryId(ruleTemplateCategoryId);
        CurrentUserDetails currentUserDetails = UserContext.getUserDetails();
        List<PhaseTemplateValue> phaseTemplateValues = new ArrayList<>();
        BeanUtils.copyProperties(phaseTemplateValues,templateDTO.getPhaseTemplateValues());
        oldTemplate.setPhaseTemplateValues(phaseTemplateValues);
        oldTemplate.setDisabled(templateDTO.getDisabled());
        oldTemplate.setWTARuleTemplateCategoryId(ruleTemplateCategoryId);
        //oldTemplate.setRecommendedValue(templateDTO.getRecommendedValue());

        oldTemplate.setLastUpdatedBy(currentUserDetails.getFirstName());

        save(oldTemplate);
        return templateDTO;
    }

    protected BigInteger checkAndAssignRuleTemplateCategory(WTABaseRuleTemplate oldTemplate, WTARuleTemplateDTO templateDTO) {
        RuleTemplateCategory ruleTemplateCategory = null;
        if (!oldTemplate.getName().equalsIgnoreCase(templateDTO.getRuleTemplateCategory().getName())) {
            ruleTemplateCategory = ruleTemplateCategoryMongoRepository.findByName(templateDTO.getRuleTemplateCategory().getName(), RuleTemplateCategoryType.WTA);
            if (!Optional.ofNullable(ruleTemplateCategory).isPresent()) {
                throw new DataNotFoundByIdException("Invalid ruleTemplateCategory name " + templateDTO.getRuleTemplateCategory().getName());
            }
            wtaBaseRuleTemplateMongoRepository.deleteCategoryFromTemplate(oldTemplate.getId(), oldTemplate.getId(), templateDTO.getRuleTemplateCategory().getName());
        }
        return oldTemplate.getId();
    }

    /*
    *
    * This method will change the category of rule Template when we change the rule template all existing rule templates wil set to none
     * and new rule temp wll be setted to  this new rule template category
    * */
    /*public Map<String, Object> updateRuleTemplateCategory(RuleTemplateDTO wtaRuleTemplateDTO, long countryId) {
        // This Method will get all the previous
        Map<String, Object> response = new HashMap();
        List<RuleTemplate> wtaBaseRuleTemplates = wtaBaseRuleTemplateMongoRepository.getWtaBaseRuleTemplateByIds(wtaRuleTemplateDTO.getRuleTemplateIds());
        RuleTemplateCategory previousRuleTemplateCategory = ruleTemplateCategoryMongoRepository.findByName(countryId, "(?i)" + wtaRuleTemplateDTO.getCategoryName(), RuleTemplateCategoryType.WTA);
        if (!Optional.ofNullable(previousRuleTemplateCategory).isPresent()) {  // Rule Template Category does not exist So creating  a new one and adding in country
            previousRuleTemplateCategory = new RuleTemplateCategory(wtaRuleTemplateDTO.getCategoryName());
            CountryDTO country = countryRestClient.getCountryById(countryId);
            List<RuleTemplateCategory> ruleTemplateCategories = country.getRuleTemplateCategories();
            ruleTemplateCategories.add(previousRuleTemplateCategory);
            country.setRuleTemplateCategories(ruleTemplateCategories);
            countryGraphRepository.save(country);
            // Break Previous Relation
            wtaBaseRuleTemplateMongoRepository.deleteOldCategories(wtaRuleTemplateDTO.getRuleTemplateIds());
            previousRuleTemplateCategory.setRuleTemplateIds(wtaBaseRuleTemplates);
            // Save Tags in Rule Template Category
            previousRuleTemplateCategory.setTags(tagService.getCountryTagsByIdsAndMasterDataType(wtaRuleTemplateDTO.getTags(), MasterDataTypeEnum.RULE_TEMPLATE_CATEGORY));
            save(previousRuleTemplateCategory);
            response.put("category", previousRuleTemplateCategory);
            response.put("templateList", getJsonOfUpdatedTemplates(wtaBaseRuleTemplates, previousRuleTemplateCategory));

        } else {
            List<Long> previousBaseRuleTemplates = ruleTemplateCategoryMongoRepository.findAllExistingRuleTemplateAddedToThiscategory(wtaRuleTemplateDTO.getCategoryName(), countryId);
            List<Long> newRuleTemplates = wtaRuleTemplateDTO.getRuleTemplateIds();
            List<Long> ruleTemplateIdsNeedToAddInCategory = ArrayUtil.getUniqueElementWhichIsNotInFirst(previousBaseRuleTemplates, newRuleTemplates);
            List<Long> ruleTemplateIdsNeedToRemoveFromCategory = ArrayUtil.getUniqueElementWhichIsNotInFirst(newRuleTemplates, previousBaseRuleTemplates);
            ruleTemplateCategoryMongoRepository.updateCategoryOfRuleTemplate(ruleTemplateIdsNeedToAddInCategory, wtaRuleTemplateDTO.getCategoryName());
            ruleTemplateCategoryMongoRepository.updateCategoryOfRuleTemplate(ruleTemplateIdsNeedToRemoveFromCategory, "NONE");
            // Save Tags in Rule Template Category
            previousRuleTemplateCategory.setTags(tagService.getCountryTagsByIdsAndMasterDataType(wtaRuleTemplateDTO.getTags(), MasterDataTypeEnum.RULE_TEMPLATE_CATEGORY));
            save(previousRuleTemplateCategory);
            response.put("templateList", getJsonOfUpdatedTemplates(wtaBaseRuleTemplates, previousRuleTemplateCategory));
        }
        return response;
    }*/

   /* private List<RuleTemplateDTO> getJsonOfUpdatedTemplates(List<RuleTemplate> wtaBaseRuleTemplates, RuleTemplateCategory ruleTemplateCategory) {

        ObjectMapper objectMapper = new ObjectMapper();
        List<RuleTemplateDTO> wtaBaseRuleTemplateDTOS = new ArrayList<>(wtaBaseRuleTemplates.size());
        wtaBaseRuleTemplates.forEach(wtaBaseRuleTemplate -> {
            RuleTemplateDTO wtaBaseRuleTemplateDTO = objectMapper.convertValue(wtaBaseRuleTemplate, RuleTemplateDTO.class);
            wtaBaseRuleTemplateDTO.setRuleTemplateCategory(ruleTemplateCategory);

            wtaBaseRuleTemplateDTOS.add(wtaBaseRuleTemplateDTO);
        });

        return wtaBaseRuleTemplateDTOS;
    }*/

    public WTARuleTemplateDTO copyRuleTemplate(Long countryId, WTARuleTemplateDTO wtaRuleTemplateDTO) {
        CountryDTO country = countryRestClient.getCountryById(countryId);
        if (!Optional.ofNullable(country).isPresent()) {
            throw new DataNotFoundByIdException("Invalid Country");
        }
       /* if(!Optional.ofNullable(wtaRuleTemplateDTO.getTemplateType()).isPresent()){
            throw new DataNotFoundByIdException("No templateType found");
        }*/
        RuleTemplateCategory ruleTemplateCategory = ruleTemplateCategoryMongoRepository.findByName(countryId, wtaRuleTemplateDTO.getRuleTemplateCategory().getName(), RuleTemplateCategoryType.WTA);
        if (!Optional.ofNullable(ruleTemplateCategory).isPresent()) {
            throw new DataNotFoundByIdException("Category Not matched");
        }

        WTABaseRuleTemplate wtaBaseRuleTemplate1 = wtaBaseRuleTemplateMongoRepository.existsByName(countryId,wtaRuleTemplateDTO.getName().trim());
        if (Optional.ofNullable(wtaBaseRuleTemplate1).isPresent()) {
            throw new DuplicateDataException("WTA Rule template already existed  " + wtaRuleTemplateDTO.getName());
        }
        WTABaseRuleTemplate wtaBaseRuleTemplate = wtaBuilderService.copyRuleTemplate(wtaRuleTemplateDTO,"id");
        wtaBaseRuleTemplate.setWTARuleTemplateCategoryId(ruleTemplateCategory.getId());
        save(wtaBaseRuleTemplate);
        wtaRuleTemplateDTO.setId(wtaBaseRuleTemplate.getId());
        RuleTemplateCategoryDTO ruleTemplateCategoryDTO = new RuleTemplateCategoryDTO();
        BeanUtils.copyProperties(ruleTemplateCategory,ruleTemplateCategoryDTO);
        wtaRuleTemplateDTO.setRuleTemplateCategory(ruleTemplateCategoryDTO);
        /*int number=getNumberFromlastInsertedTemplateType(lastInsertedTemplateType);

        String templateTypeToBeSet=originalTemplateType+"_";*/
        return wtaRuleTemplateDTO;


    }
    private String getTemplateType(String templateType){
        if(!templateType.contains("_")){
            return templateType;
        }
        int lastCharIndex = templateType.lastIndexOf("_");
        if(lastCharIndex > 0){
            char nextCharacter = templateType.charAt(lastCharIndex + 1);
            if(!Character.isDigit(templateType.charAt(lastCharIndex + 1))){
                return templateType;
            }
            else{
                return templateType.substring(0, lastCharIndex);
            }
        }
        return null;
    }


    int getNumberFromlastInsertedTemplateType(String templateType) {
        if (!templateType.contains("_")) {
            return 0;
        }
        int lastCharIndex = templateType.lastIndexOf("_");
        if (lastCharIndex > 0) {
            char nextCharacter = templateType.charAt(lastCharIndex + 1);
            if(!Character.isDigit(templateType.charAt(lastCharIndex + 1))){
                return 0;
            }
            else{
                return (int) Integer.parseInt(templateType.substring(++lastCharIndex,templateType.length()));
            }
        }

        return 0;
    }

}

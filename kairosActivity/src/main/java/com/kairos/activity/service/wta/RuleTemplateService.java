package com.kairos.activity.service.wta;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.activity.client.CountryRestClient;
import com.kairos.activity.custom_exception.DataNotFoundByIdException;
import com.kairos.activity.custom_exception.DuplicateDataException;
import com.kairos.activity.persistence.enums.WTATemplateType;
import com.kairos.activity.persistence.model.wta.WTAQueryResultDTO;
import com.kairos.activity.persistence.model.wta.templates.PhaseTemplateValue;
import com.kairos.activity.persistence.model.wta.templates.RuleTemplateCategory;
import com.kairos.activity.persistence.model.wta.templates.RuleTemplateCategoryType;
import com.kairos.activity.persistence.model.wta.templates.WTABaseRuleTemplate;
import com.kairos.activity.persistence.model.wta.templates.template_types.*;
import com.kairos.activity.service.MongoBaseService;
import com.kairos.activity.service.tag.TagService;
import com.kairos.activity.util.DateUtils;
import com.kairos.activity.util.userContext.CurrentUserDetails;
import com.kairos.activity.util.userContext.UserContext;
import com.kairos.persistence.model.enums.MasterDataTypeEnum;
import com.kairos.persistence.model.enums.TimeBankTypeEnum;

import com.kairos.response.dto.web.CountryDTO;
import com.kairos.response.dto.web.wta.RuleTemplateCategoryDTO;
import com.kairos.response.dto.web.wta.RuleTemplateCategoryTagDTO;
import com.kairos.response.dto.web.wta.RuleTemplateDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.*;

import static com.kairos.activity.persistence.enums.WTATemplateType.getByTemplateType;


/**
 * Created by pawanmandhan on 5/8/17.
 */
@Transactional
@Service
public class RuleTemplateService extends MongoBaseService {



    @Inject
    private WTABaseRuleTemplateGraphRepository wtaRuleTemplateGraphRepository;
    @Inject
    private CountryRestClient countryRestClient;
    @Inject
    private RuleTemplateCategoryGraphRepository ruleTemplateCategoryRepository;
    @Inject
    private TagService tagService;
    @Inject
    private WTABaseRuleTemplateGraphRepository wtaBaseRuleTemplateGraphRepository;
    @Inject
    private WTAOrganizationService wtaOrganizationService;
    private final Logger logger = LoggerFactory.getLogger(RuleTemplateService.class);

    public boolean createRuleTemplate(long countryId) {
        CountryDTO countryDTO = countryRestClient.getCountryById(countryId);

        if (countryDTO == null) {
            throw new DataNotFoundByIdException("Invalid Country");
        }


        RuleTemplateCategory ruleTemplateCategory = ruleTemplateCategoryRepository.findByName(countryId, "NONE", RuleTemplateCategoryType.WTA);
        if (!Optional.ofNullable(ruleTemplateCategory).isPresent()) {
            ruleTemplateCategory = new RuleTemplateCategory("NONE","", RuleTemplateCategoryType.WTA);
            ruleTemplateCategory.setCountry(countryDTO.getId());
            save(ruleTemplateCategory);
        }
        if (Optional.ofNullable(countryDTO.getWTABaseRuleTemplate()).isPresent() && !countryDTO.getWTABaseRuleTemplate().isEmpty()) {
            throw new DataNotFoundByIdException("WTA Rule Template already exists");
        }

        String MONTHS = "MONTHS";
        String TUESDAY = "TUESDAY";
        long timeInMins = 10;
        long daysCount = 10;
        long dateInMillis = DateUtils.getCurrentDate().getTime();


        ShiftLengthWTATemplate maximumShiftLengthWTATemplate = new ShiftLengthWTATemplate();
        maximumShiftLengthWTATemplate.setCountryId(countryDTO.getId());


        ConsecutiveWorkWTATemplate consecutiveWorking = new ConsecutiveWorkWTATemplate();
        consecutiveWorking.setCountryId(countryDTO.getId());

        ConsecutiveRestPartOfDayWTATemplate restInConsecutiveDays = new ConsecutiveRestPartOfDayWTATemplate();
        restInConsecutiveDays.setCountryId(countryDTO.getId());

        ConsecutiveRestPartOfDayWTATemplate consecutiveRestPartOfDayWTATemplate = new ConsecutiveRestPartOfDayWTATemplate();
        consecutiveRestPartOfDayWTATemplate.setCountryId(countryDTO.getId());

        NumberOfPartOfDayShiftsWTATemplate numberOfPartOfDayShiftsWTATemplate = new NumberOfPartOfDayShiftsWTATemplate();
        numberOfPartOfDayShiftsWTATemplate.setCountryId(countryDTO.getId());

        DaysOffInPeriodWTATemplate daysOffInPeriodWTATemplate = new DaysOffInPeriodWTATemplate();
        daysOffInPeriodWTATemplate.setCountryId(countryDTO.getId());

        AverageScheduledTimeWTATemplate averageScheduledTimeWTATemplate = new AverageScheduledTimeWTATemplate();
        averageScheduledTimeWTATemplate.setCountryId(countryDTO.getId());

        VetoPerPeriodWTATemplate vetoPerPeriodWTATemplate = new VetoPerPeriodWTATemplate();
        vetoPerPeriodWTATemplate.setCountryId(countryDTO.getId());

        NumberOfWeekendShiftInPeriodWTATemplate ofWeekendShiftInPeriodWTATemplate = new NumberOfWeekendShiftInPeriodWTATemplate();
        ofWeekendShiftInPeriodWTATemplate.setCountryId(countryDTO.getId());

        CareDayCheckWTATemplate careDayCheckWTATemplate = new CareDayCheckWTATemplate();
        careDayCheckWTATemplate.setCountryId(countryDTO.getId());

        DailyRestingTimeWTATemplate dailyRestingTimeWTATemplate = new DailyRestingTimeWTATemplate();
        dailyRestingTimeWTATemplate.setCountryId(countryDTO.getId());

        DurationBetweenShiftWTATemplate durationBetweenShiftWTATemplate = new DurationBetweenShiftWTATemplate();
        durationBetweenShiftWTATemplate.setCountryId(countryDTO.getId());


        WeeklyRestPeriodWTATemplate weeklyRestPeriodWTATemplate = new WeeklyRestPeriodWTATemplate();
        weeklyRestPeriodWTATemplate.setCountryId(countryDTO.getId());

        ShortestAndAverageDailyRestWTATemplate shortestAndAverageDailyRestWTATemplate = new ShortestAndAverageDailyRestWTATemplate();
        shortestAndAverageDailyRestWTATemplate.setCountryId(countryDTO.getId());

        ShiftsInIntervalWTATemplate maximumShiftsInIntervalWTATemplate = new ShiftsInIntervalWTATemplate();
        maximumShiftsInIntervalWTATemplate.setCountryId(countryDTO.getId());

        SeniorDaysInYearWTATemplate seniorDaysInYearWTATemplate = new SeniorDaysInYearWTATemplate();
        seniorDaysInYearWTATemplate.setCountryId(countryDTO.getId());

        TimeBankWTATemplate timeBankWTATemplate = new TimeBankWTATemplate();
        timeBankWTATemplate.setCountryId(countryDTO.getId());


        save(timeBankWTATemplate);
        save(timeBankWTATemplate);
        save(timeBankWTATemplate);
        save(timeBankWTATemplate);
        save(timeBankWTATemplate);
        save(timeBankWTATemplate);
        save(timeBankWTATemplate);
        save(timeBankWTATemplate);
        save(timeBankWTATemplate);
        save(timeBankWTATemplate);
        save(timeBankWTATemplate);
        save(timeBankWTATemplate);
        save(timeBankWTATemplate);
        save(timeBankWTATemplate);




        ruleTemplateCategory.setRuleTemplates(baseRuleTemplates);
        ruleTemplateCategoryRepository.save(ruleTemplateCategory);

        return true;
    }

    public Map getRuleTemplate(long countryId) {

        List<RuleTemplateCategoryDTO> wtaResponse = null;
        try {
            wtaResponse = countryGraphRepository.getRuleTemplatesAndCategories(countryId);
        } catch (Exception e) {
            System.out.println(e);


        }
        CountryDTO country = countryRestClient.getCountryById(countryId);
        if (country == null) {
            throw new DataNotFoundByIdException("Invalid Country");
        }

        List<RuleTemplateCategoryTagDTO> categoryList = ruleTemplateCategoryRepository.getAllRulesOfCountry(countryId);

        if (categoryList == null) {
            throw new DataNotFoundByIdException("Category List is null");
        }

        List<RuleTemplateResponseDTO> templateList = wtaBaseRuleTemplateGraphRepository.getWTABaseRuleTemplateByCountryId(countryId);
        if (templateList == null) {
            throw new DataNotFoundByIdException("Template List is null");
        }


        Map response = new HashMap();
        response.put("categoryList", categoryList);
        response.put("templateList", templateList);

        return response;
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


    public RuleTemplateCategoryDTO updateRuleTemplate(long countryId, RuleTemplateCategoryDTO templateDTO) {

        CountryDTO country = countryRestClient.getCountryById(countryId);
        if (!Optional.ofNullable(country).isPresent()) {
            throw new DataNotFoundByIdException("Invalid Country");
        }

        WTABaseRuleTemplate oldTemplate = wtaRuleTemplateGraphRepository.findOne(templateDTO.getId());
        if (!Optional.ofNullable(oldTemplate).isPresent()) {
            throw new DataNotFoundByIdException("Invalid TemplateType id " + templateDTO.getId());
        }
        String templateType=getTemplateType(templateDTO.getTemplateType());
        WTATemplateType ruleTemplateType = getByTemplateType(templateType);
        logger.info("templateType:"+templateType);
        switch (ruleTemplateType) {
            case SHIFT_LENGTH:
                MaximumShiftLengthWTATemplate maximumShiftLengthWTATemplate = (MaximumShiftLengthWTATemplate) oldTemplate;//oldTemplate;
                maximumShiftLengthWTATemplate.setDescription(templateDTO.getDescription());
                maximumShiftLengthWTATemplate.setTimeLimit(templateDTO.getTimeLimit());
                maximumShiftLengthWTATemplate.setBalanceType(templateDTO.getBalanceType());
                maximumShiftLengthWTATemplate.setCheckAgainstTimeRules(templateDTO.getCheckAgainstTimeRules());
                break;
            case SHIFT_LENGTH:
                MinimumShiftLengthWTATemplate minimumShiftLengthWTATemplate = (MinimumShiftLengthWTATemplate) oldTemplate;
                minimumShiftLengthWTATemplate.setDescription(templateDTO.getDescription());
                minimumShiftLengthWTATemplate.setTimeLimit(templateDTO.getTimeLimit());
                minimumShiftLengthWTATemplate.setBalanceType(templateDTO.getBalanceType());
                minimumShiftLengthWTATemplate.setCheckAgainstTimeRules(templateDTO.getCheckAgainstTimeRules());
                break;
            case CONSECUTIVE_WORKING_PARTOFDAY:
                MaximumConsecutiveWorkingDaysWTATemplate maximumConsecutiveWorkingDaysWTATemplate = (MaximumConsecutiveWorkingDaysWTATemplate) oldTemplate;
                maximumConsecutiveWorkingDaysWTATemplate.setDescription(templateDTO.getDescription());
                maximumConsecutiveWorkingDaysWTATemplate.setDaysLimit(templateDTO.getDaysLimit());
                maximumConsecutiveWorkingDaysWTATemplate.setBalanceType(templateDTO.getBalanceType());
                maximumConsecutiveWorkingDaysWTATemplate.setCheckAgainstTimeRules(templateDTO.getCheckAgainstTimeRules());
                break;
            case REST_IN_CONSECUTIVE_DAYS_AND_NIGHTS:
                MinimumRestInConsecutiveDaysWTATemplate minimumRestInConsecutiveDaysWTATemplate = (MinimumRestInConsecutiveDaysWTATemplate) oldTemplate;
                minimumRestInConsecutiveDaysWTATemplate.setDescription(templateDTO.getDescription());
                minimumRestInConsecutiveDaysWTATemplate.setMinimumRest(templateDTO.getMinimumRest());
                minimumRestInConsecutiveDaysWTATemplate.setDaysWorked(templateDTO.getDaysWorked());
                break;


            case NUMBER_OF_CONSECUTIVE_NIGHTS:
                MinimumConsecutiveNightsWTATemplate minimumConsecutiveNightsWTATemplate = (MinimumConsecutiveNightsWTATemplate) oldTemplate;
                minimumConsecutiveNightsWTATemplate.setDescription(templateDTO.getDescription());
                minimumConsecutiveNightsWTATemplate.setDaysLimit(templateDTO.getDaysLimit());
                break;

            case MAXIMUM_NUMBER_OF_CONSECUTIVE_NIGHTS:
                MaximumConsecutiveWorkingNightsWTATemplate maximumConsecutiveWorkingNights = (MaximumConsecutiveWorkingNightsWTATemplate) oldTemplate;//oldTemplate;
                maximumConsecutiveWorkingNights.setDescription(templateDTO.getDescription());
                maximumConsecutiveWorkingNights.setNightsWorked(templateDTO.getNightsWorked());
                maximumConsecutiveWorkingNights.setBalanceType(templateDTO.getBalanceType());
                maximumConsecutiveWorkingNights.setCheckAgainstTimeRules(templateDTO.getCheckAgainstTimeRules());
                break;
            case MINIMUM_REST_AFTER_CONSECUTIVE_NIGHTS_WORKED:
                MinimumRestConsecutiveNightsWTATemplate minimumRestConsecutiveNightsWTATemplate = (MinimumRestConsecutiveNightsWTATemplate) oldTemplate;
                minimumRestConsecutiveNightsWTATemplate.setDescription(templateDTO.getDescription());
                minimumRestConsecutiveNightsWTATemplate.setNightsWorked(templateDTO.getNightsWorked());
                minimumRestConsecutiveNightsWTATemplate.setBalanceType(templateDTO.getBalanceType());
                minimumRestConsecutiveNightsWTATemplate.setMinimumRest(templateDTO.getMinimumRest());
                break;
            case MAXIMUM_NUMBER_OF_WORK_NIGHTS:
                MaximumNumberOfNightsWTATemplate maximumNumberOfNightsWTATemplate = (MaximumNumberOfNightsWTATemplate) oldTemplate;
                maximumNumberOfNightsWTATemplate.setDescription(templateDTO.getDescription());
                maximumNumberOfNightsWTATemplate.setNightsWorked(templateDTO.getNightsWorked());
                maximumNumberOfNightsWTATemplate.setBalanceType(templateDTO.getBalanceType());
                maximumNumberOfNightsWTATemplate.setIntervalLength(templateDTO.getIntervalLength());
                maximumNumberOfNightsWTATemplate.setIntervalUnit(templateDTO.getIntervalUnit());
                maximumNumberOfNightsWTATemplate.setValidationStartDateMillis(templateDTO.getValidationStartDateMillis());
                break;
            case MINIMUM_NUMBER_OF_DAYS_OFF_PER_PERIOD:
                MaximumDaysOffInPeriodWTATemplate maximumDaysOffInPeriodWTATemplate = (MaximumDaysOffInPeriodWTATemplate) oldTemplate;
                maximumDaysOffInPeriodWTATemplate.setDescription(templateDTO.getDescription());
                maximumDaysOffInPeriodWTATemplate.setIntervalLength(templateDTO.getIntervalLength());
                maximumDaysOffInPeriodWTATemplate.setIntervalUnit(templateDTO.getIntervalUnit());
                maximumDaysOffInPeriodWTATemplate.setValidationStartDateMillis(templateDTO.getValidationStartDateMillis());
                maximumDaysOffInPeriodWTATemplate.setBalanceType(templateDTO.getBalanceType());
                maximumDaysOffInPeriodWTATemplate.setDaysLimit(templateDTO.getDaysLimit());
                break;
            case MAXIMUM_AVERAGE_SCHEDULED_TIME_PER_WEEK_WITHIN_AN_INTERVAL:
                MaximumAverageScheduledTimeWTATemplate maximumAverageScheduledTimeWTATemplate = (MaximumAverageScheduledTimeWTATemplate) oldTemplate;
                maximumAverageScheduledTimeWTATemplate.setDescription(templateDTO.getDescription());
                maximumAverageScheduledTimeWTATemplate.setUseShiftTimes(templateDTO.getUseShiftTimes());
                maximumAverageScheduledTimeWTATemplate.setIntervalLength(templateDTO.getIntervalLength());
                maximumAverageScheduledTimeWTATemplate.setIntervalUnit(templateDTO.getIntervalUnit());
                maximumAverageScheduledTimeWTATemplate.setMaximumAvgTime(templateDTO.getMaximumAvgTime());
                maximumAverageScheduledTimeWTATemplate.setBalanceType(templateDTO.getBalanceType());
                maximumAverageScheduledTimeWTATemplate.setValidationStartDateMillis(templateDTO.getValidationStartDateMillis());
                maximumAverageScheduledTimeWTATemplate.setBalanceAdjustment(templateDTO.getBalanceAdjustment());
                break;
            case MAXIMUM_VETO_PER_PERIOD:
                MaximumVetoPerPeriodWTATemplate maximumVetoPerPeriodWTATemplate = (MaximumVetoPerPeriodWTATemplate) oldTemplate;
                maximumVetoPerPeriodWTATemplate.setDescription(templateDTO.getDescription());
                maximumVetoPerPeriodWTATemplate.setMaximumVetoPercentage(templateDTO.getMaximumVetoPercentage());
                break;
            case NUMBER_OF_WEEKEND_SHIFTS_IN_A_PERIOD_COMPARED_TO_AVERAGE:
                NumberOfWeekendShiftInPeriodWTATemplate numberOfWeekendShiftInPeriodWTATemplate = (NumberOfWeekendShiftInPeriodWTATemplate) oldTemplate;
                numberOfWeekendShiftInPeriodWTATemplate.setDescription(templateDTO.getDescription());
                numberOfWeekendShiftInPeriodWTATemplate.setNumberShiftsPerPeriod(templateDTO.getNumberShiftsPerPeriod());
                numberOfWeekendShiftInPeriodWTATemplate.setNumberOfWeeks(templateDTO.getNumberOfWeeks());
                numberOfWeekendShiftInPeriodWTATemplate.setFromDayOfWeek(templateDTO.getFromDayOfWeek());
                numberOfWeekendShiftInPeriodWTATemplate.setFromTime(templateDTO.getFromTime());
                numberOfWeekendShiftInPeriodWTATemplate.setToTime(templateDTO.getToTime());
                numberOfWeekendShiftInPeriodWTATemplate.setToDayOfWeek(templateDTO.getToDayOfWeek());
                numberOfWeekendShiftInPeriodWTATemplate.setProportional(templateDTO.getProportional());
                break;
            case CARE_DAYS_CHECK:
                CareDayCheckWTATemplate careDayCheckWTATemplate = (CareDayCheckWTATemplate) oldTemplate;
                careDayCheckWTATemplate.setDescription(templateDTO.getDescription());
                careDayCheckWTATemplate.setIntervalLength(templateDTO.getIntervalLength());
                careDayCheckWTATemplate.setIntervalUnit(templateDTO.getIntervalUnit());
                careDayCheckWTATemplate.setDaysLimit(templateDTO.getDaysLimit());
                careDayCheckWTATemplate.setValidationStartDateMillis(templateDTO.getValidationStartDateMillis());
                break;
            case MINIMUM_DAILY_RESTING_TIME:
                MinimumDailyRestingTimeWTATemplate minimumDailyRestingTimeWTATemplate = (MinimumDailyRestingTimeWTATemplate) oldTemplate;
                minimumDailyRestingTimeWTATemplate.setId(oldTemplate.getId());
                minimumDailyRestingTimeWTATemplate.setDescription(templateDTO.getDescription());
                minimumDailyRestingTimeWTATemplate.setContinuousDayRestHours(templateDTO.getContinuousDayRestHours());
                break;
            case MINIMUM_DURATION_BETWEEN_SHIFTS:
                MinimumDurationBetweenShiftWTATemplate minimumDurationBetweenShiftWTATemplate = (MinimumDurationBetweenShiftWTATemplate) oldTemplate;
                minimumDurationBetweenShiftWTATemplate.setDescription(templateDTO.getDescription());
                minimumDurationBetweenShiftWTATemplate.setBalanceType(templateDTO.getBalanceType());
                minimumDurationBetweenShiftWTATemplate.setMinimumDurationBetweenShifts(templateDTO.getMinimumDurationBetweenShifts());
                break;
            case MINIMUM_WEEKLY_REST_PERIOD_FIXED_WEEKS:
                MinimumWeeklyRestPeriodWTATemplate minimumWeeklyRestPeriodWTATemplate = (MinimumWeeklyRestPeriodWTATemplate) oldTemplate;
                minimumWeeklyRestPeriodWTATemplate.setDescription(templateDTO.getDescription());
                minimumWeeklyRestPeriodWTATemplate.setContinuousWeekRest(templateDTO.getContinuousWeekRest());
                break;
            case SHORTEST_AND_AVERAGE_DAILY_REST_FIXED_TIMES:
                ShortestAndAverageDailyRestWTATemplate shortestAndAverageDailyRestWTATemplate = (ShortestAndAverageDailyRestWTATemplate) oldTemplate;
                shortestAndAverageDailyRestWTATemplate.setDescription(templateDTO.getDescription());
                shortestAndAverageDailyRestWTATemplate.setBalanceType(templateDTO.getBalanceType());
                shortestAndAverageDailyRestWTATemplate.setIntervalLength(templateDTO.getIntervalLength());
                shortestAndAverageDailyRestWTATemplate.setIntervalUnit(templateDTO.getIntervalUnit());
                shortestAndAverageDailyRestWTATemplate.setValidationStartDateMillis(templateDTO.getValidationStartDateMillis());
                shortestAndAverageDailyRestWTATemplate.setContinuousDayRestHours(templateDTO.getContinuousDayRestHours());
                shortestAndAverageDailyRestWTATemplate.setAverageRest(templateDTO.getAverageRest());
                shortestAndAverageDailyRestWTATemplate.setShiftAffiliation(templateDTO.getShiftAffiliation());
                break;
            case MAXIMUM_NUMBER_OF_SHIFTS_PER_INTERVAL:
                MaximumShiftsInIntervalWTATemplate maximumShiftsInIntervalWTATemplate = (MaximumShiftsInIntervalWTATemplate) oldTemplate;
                maximumShiftsInIntervalWTATemplate.setDescription(templateDTO.getDescription());
                maximumShiftsInIntervalWTATemplate.setBalanceType(templateDTO.getBalanceType());
                maximumShiftsInIntervalWTATemplate.setIntervalLength(templateDTO.getIntervalLength());
                maximumShiftsInIntervalWTATemplate.setIntervalUnit(templateDTO.getIntervalUnit());
                maximumShiftsInIntervalWTATemplate.setValidationStartDateMillis(templateDTO.getValidationStartDateMillis());
                maximumShiftsInIntervalWTATemplate.setShiftsLimit(templateDTO.getShiftsLimit());
                maximumShiftsInIntervalWTATemplate.setOnlyCompositeShifts(templateDTO.getOnlyCompositeShifts());
                break;
            case MAXIMUM_SENIOR_DAYS_PER_YEAR:
                MaximumSeniorDaysInYearWTATemplate maximumSeniorDaysInYearWTATemplate = (MaximumSeniorDaysInYearWTATemplate) oldTemplate;
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
        RuleTemplateCategory ruleTemplateCategory = checkAndAssignRuleTemplateCategory(oldTemplate, templateDTO);
        oldTemplate.setWTARuleTemplateCategory(ruleTemplateCategory.getId());
        CurrentUserDetails currentUserDetails = UserContext.getUserDetails();
        List<PhaseTemplateValue> phaseTemplateValues = new ArrayList<>();
        BeanUtils.copyProperties(phaseTemplateValues,templateDTO.getPhaseTemplateValues());
        oldTemplate.setPhaseTemplateValues(phaseTemplateValues);
        oldTemplate.setDisabled(templateDTO.getDisabled());
        oldTemplate.setWTARuleTemplateCategory(ruleTemplateCategory.getId());
        oldTemplate.setRecommendedValue(templateDTO.getRecommendedValue());

        oldTemplate.setLastUpdatedBy(currentUserDetails.getFirstName());

        save(oldTemplate);
        return templateDTO;
    }

    protected RuleTemplateCategory checkAndAssignRuleTemplateCategory(WTABaseRuleTemplate oldTemplate, RuleTemplateCategoryDTO templateDTO) {
        RuleTemplateCategory ruleTemplateCategory = null;
        if (!oldTemplate.getRuleTemplateCategory().getName().equalsIgnoreCase(templateDTO.getRuleTemplateCategory().getName())) {
            ruleTemplateCategory = ruleTemplateCategoryRepository.findByName(templateDTO.getRuleTemplateCategory().getName(), RuleTemplateCategoryType.WTA);
            if (!Optional.ofNullable(ruleTemplateCategory).isPresent()) {
                throw new DataNotFoundByIdException("Invalid ruleTemplateCategory name " + templateDTO.getRuleTemplateCategory().getName());
            }
            wtaRuleTemplateGraphRepository.deleteCategoryFromTemplate(oldTemplate.getId(), oldTemplate.getRuleTemplateCategory().getId(), templateDTO.getRuleTemplateCategory().getName());
            templateDTO.setRuleTemplateCategory(ruleTemplateCategory);
        } else {
            ruleTemplateCategory = oldTemplate.getRuleTemplateCategory();
        }
        return ruleTemplateCategory;
    }

    /*
    *
    * This method will change the category of rule Template when we change the rule template all existing rule templates wil set to none
     * and new rule temp wll be setted to  this new rule template category
    * */
    public Map<String, Object> updateRuleTemplateCategory(RuleTemplateDTO wtaRuleTemplateDTO, long countryId) {
        // This Method will get all the previous
        Map<String, Object> response = new HashMap();
        List<RuleTemplate> wtaBaseRuleTemplates = wtaRuleTemplateGraphRepository.getWtaBaseRuleTemplateByIds(wtaRuleTemplateDTO.getRuleTemplateIds());
        RuleTemplateCategory previousRuleTemplateCategory = ruleTemplateCategoryRepository.findByName(countryId, "(?i)" + wtaRuleTemplateDTO.getCategoryName(), RuleTemplateCategoryType.WTA);
        if (!Optional.ofNullable(previousRuleTemplateCategory).isPresent()) {  // Rule Template Category does not exist So creating  a new one and adding in country
            previousRuleTemplateCategory = new RuleTemplateCategory(wtaRuleTemplateDTO.getCategoryName());
            CountryDTO country = countryRestClient.getCountryById(countryId);
            List<RuleTemplateCategory> ruleTemplateCategories = country.getRuleTemplateCategories();
            ruleTemplateCategories.add(previousRuleTemplateCategory);
            country.setRuleTemplateCategories(ruleTemplateCategories);
            countryGraphRepository.save(country);
            // Break Previous Relation
            wtaRuleTemplateGraphRepository.deleteOldCategories(wtaRuleTemplateDTO.getRuleTemplateIds());
            previousRuleTemplateCategory.setRuleTemplates(wtaBaseRuleTemplates);
            // Save Tags in Rule Template Category
            previousRuleTemplateCategory.setTags(tagService.getCountryTagsByIdsAndMasterDataType(wtaRuleTemplateDTO.getTags(), MasterDataTypeEnum.RULE_TEMPLATE_CATEGORY));
            save(previousRuleTemplateCategory);
            response.put("category", previousRuleTemplateCategory);
            response.put("templateList", getJsonOfUpdatedTemplates(wtaBaseRuleTemplates, previousRuleTemplateCategory));

        } else {
            List<Long> previousBaseRuleTemplates = ruleTemplateCategoryRepository.findAllExistingRuleTemplateAddedToThiscategory(wtaRuleTemplateDTO.getCategoryName(), countryId);
            List<Long> newRuleTemplates = wtaRuleTemplateDTO.getRuleTemplateIds();
            List<Long> ruleTemplateIdsNeedToAddInCategory = ArrayUtil.getUniqueElementWhichIsNotInFirst(previousBaseRuleTemplates, newRuleTemplates);
            List<Long> ruleTemplateIdsNeedToRemoveFromCategory = ArrayUtil.getUniqueElementWhichIsNotInFirst(newRuleTemplates, previousBaseRuleTemplates);
            ruleTemplateCategoryRepository.updateCategoryOfRuleTemplate(ruleTemplateIdsNeedToAddInCategory, wtaRuleTemplateDTO.getCategoryName());
            ruleTemplateCategoryRepository.updateCategoryOfRuleTemplate(ruleTemplateIdsNeedToRemoveFromCategory, "NONE");
            // Save Tags in Rule Template Category
            previousRuleTemplateCategory.setTags(tagService.getCountryTagsByIdsAndMasterDataType(wtaRuleTemplateDTO.getTags(), MasterDataTypeEnum.RULE_TEMPLATE_CATEGORY));
            save(previousRuleTemplateCategory);
            response.put("templateList", getJsonOfUpdatedTemplates(wtaBaseRuleTemplates, previousRuleTemplateCategory));
        }
        return response;
    }

    private List<RuleTemplateDTO> getJsonOfUpdatedTemplates(List<RuleTemplate> wtaBaseRuleTemplates, RuleTemplateCategory ruleTemplateCategory) {

        ObjectMapper objectMapper = new ObjectMapper();
        List<RuleTemplateDTO> wtaBaseRuleTemplateDTOS = new ArrayList<>(wtaBaseRuleTemplates.size());
        wtaBaseRuleTemplates.forEach(wtaBaseRuleTemplate -> {
            RuleTemplateDTO wtaBaseRuleTemplateDTO = objectMapper.convertValue(wtaBaseRuleTemplate, RuleTemplateDTO.class);
            wtaBaseRuleTemplateDTO.setRuleTemplateCategory(ruleTemplateCategory);

            wtaBaseRuleTemplateDTOS.add(wtaBaseRuleTemplateDTO);
        });

        return wtaBaseRuleTemplateDTOS;
    }

    public WTABaseRuleTemplate copyRuleTemplate(Long countryId, RuleTemplateCategoryDTO wtaRuleTemplateDTO) {
        CountryDTO country = countryRestClient.getCountryById(countryId);
        if (!Optional.ofNullable(country).isPresent()) {
            throw new DataNotFoundByIdException("Invalid Country");
        }
        if(!Optional.ofNullable(wtaRuleTemplateDTO.getTemplateType()).isPresent()){
            throw new DataNotFoundByIdException("No templateType found");
        }
        RuleTemplateCategory ruleTemplateCategory = ruleTemplateCategoryRepository.findByName(countryId, wtaRuleTemplateDTO.getRuleTemplateCategory().getName(), RuleTemplateCategoryType.WTA);
        if (!Optional.ofNullable(ruleTemplateCategory).isPresent()) {
            throw new DataNotFoundByIdException("Category Not matched");
        }

        WTABaseRuleTemplate wtaBaseRuleTemplate1 = wtaBaseRuleTemplateGraphRepository.existsByName(countryId,wtaRuleTemplateDTO.getName().trim().toLowerCase());
        if (Optional.ofNullable(wtaBaseRuleTemplate1).isPresent()) {
            throw new DuplicateDataException("WTA Rule template already existed  " + wtaRuleTemplateDTO.getName());
        }

        WTABaseRuleTemplate wtaBaseRuleTemplate = new WTABaseRuleTemplate();
        List<PhaseTemplateValue> phaseTemplateValues = wtaOrganizationService.copyPhaseTemplateValue(wtaRuleTemplateDTO.getPhaseTemplateValues());
        String originalTemplateType=getTemplateType(wtaRuleTemplateDTO.getTemplateType());
        WTATemplateType ruleTemplateType = getByTemplateType(originalTemplateType);
        String lastInsertedTemplateType=wtaBaseRuleTemplateGraphRepository.getLastInsertedTemplateType(countryId,originalTemplateType);
        if(!Optional.ofNullable(lastInsertedTemplateType).isPresent()){
            throw new DataNotFoundByIdException("No templateType found"+originalTemplateType);
        }

        switch (ruleTemplateType){
            case MAXIMUM_SHIFT_LENGTH:
                wtaBaseRuleTemplate = new MaximumShiftLengthWTATemplate(wtaRuleTemplateDTO.getName().trim(),
                        wtaRuleTemplateDTO.getTemplateType(), wtaRuleTemplateDTO.getDisabled(), wtaRuleTemplateDTO.getDescription(), wtaRuleTemplateDTO.getTimeLimit(), wtaRuleTemplateDTO.getBalanceType(), wtaRuleTemplateDTO.getCheckAgainstTimeRules());
                break;
            case MINIMUM_SHIFT_LENGTH:
                wtaBaseRuleTemplate = new MinimumShiftLengthWTATemplate(wtaRuleTemplateDTO.getName().trim(),
                        wtaRuleTemplateDTO.getTemplateType(), wtaRuleTemplateDTO.getDisabled(), wtaRuleTemplateDTO.getDescription(), wtaRuleTemplateDTO.getTimeLimit(), wtaRuleTemplateDTO.getBalanceType(), wtaRuleTemplateDTO.getCheckAgainstTimeRules());
                break;
            case MAXIMUM_NUMBER_OF_CONSECUTIVE_DAYS:
                wtaBaseRuleTemplate = new MaximumConsecutiveWorkingDaysWTATemplate(wtaRuleTemplateDTO.getName().trim(),
                        wtaRuleTemplateDTO.getTemplateType(), wtaRuleTemplateDTO.getDisabled(), wtaRuleTemplateDTO.getDescription(), wtaRuleTemplateDTO.getBalanceType(), wtaRuleTemplateDTO.getCheckAgainstTimeRules(), wtaRuleTemplateDTO.getDaysLimit());
                break;
            case MINIMUM_REST_AFTER_CONSECUTIVE_DAYS_WORKED:
                wtaBaseRuleTemplate = new MinimumRestInConsecutiveDaysWTATemplate(wtaRuleTemplateDTO.getName().trim(),
                        wtaRuleTemplateDTO.getTemplateType(), wtaRuleTemplateDTO.getDisabled(), wtaRuleTemplateDTO.getDescription(), wtaRuleTemplateDTO.getMinimumRest(), wtaRuleTemplateDTO.getDaysWorked());
                break;
            case MAXIMUM_NIGHT_SHIFTS_LENGTH:
                wtaBaseRuleTemplate = new MaximumNightShiftLengthWTATemplate(wtaRuleTemplateDTO.getName().trim(),
                        wtaRuleTemplateDTO.getTemplateType(), wtaRuleTemplateDTO.getDisabled(), wtaRuleTemplateDTO.getDescription(), wtaRuleTemplateDTO.getTimeLimit(), wtaRuleTemplateDTO.getBalanceType(), wtaRuleTemplateDTO.getCheckAgainstTimeRules());
                break;
            case MINIMUM_NUMBER_OF_CONSECUTIVE_NIGHTS:
                wtaBaseRuleTemplate = new MinimumConsecutiveNightsWTATemplate(wtaRuleTemplateDTO.getName().trim(),
                        wtaRuleTemplateDTO.getTemplateType(), wtaRuleTemplateDTO.getDisabled(), wtaRuleTemplateDTO.getDescription(), wtaRuleTemplateDTO.getDaysLimit());
                break;
            case MAXIMUM_NUMBER_OF_CONSECUTIVE_NIGHTS:
                wtaBaseRuleTemplate = new MaximumConsecutiveWorkingNightsWTATemplate(wtaRuleTemplateDTO.getName().trim(),
                        wtaRuleTemplateDTO.getTemplateType(), wtaRuleTemplateDTO.getDisabled(), wtaRuleTemplateDTO.getDescription(), wtaRuleTemplateDTO.getBalanceType(), wtaRuleTemplateDTO.getCheckAgainstTimeRules(), wtaRuleTemplateDTO.getNightsWorked());
                break;
            case MINIMUM_REST_AFTER_CONSECUTIVE_NIGHTS_WORKED:
                wtaBaseRuleTemplate = new MinimumRestConsecutiveNightsWTATemplate(wtaRuleTemplateDTO.getName().trim(),
                        wtaRuleTemplateDTO.getTemplateType(), wtaRuleTemplateDTO.getDisabled(), wtaRuleTemplateDTO.getDescription(), wtaRuleTemplateDTO.getBalanceType(), wtaRuleTemplateDTO.getMinimumRest(), wtaRuleTemplateDTO.getNightsWorked());
                break;
            case MAXIMUM_NUMBER_OF_WORK_NIGHTS:
                wtaBaseRuleTemplate = new MaximumNumberOfNightsWTATemplate(wtaRuleTemplateDTO.getName().trim(),
                        wtaRuleTemplateDTO.getTemplateType(), wtaRuleTemplateDTO.getDisabled(), wtaRuleTemplateDTO.getDescription(), wtaRuleTemplateDTO.getBalanceType(), wtaRuleTemplateDTO.getNightsWorked(), wtaRuleTemplateDTO.getIntervalLength(), wtaRuleTemplateDTO.getValidationStartDateMillis(), wtaRuleTemplateDTO.getIntervalUnit());
                break;
            case MINIMUM_NUMBER_OF_DAYS_OFF_PER_PERIOD:
                wtaBaseRuleTemplate = new MaximumDaysOffInPeriodWTATemplate(wtaRuleTemplateDTO.getName().trim(),
                        wtaRuleTemplateDTO.getTemplateType(), wtaRuleTemplateDTO.getDisabled(), wtaRuleTemplateDTO.getDescription(), wtaRuleTemplateDTO.getBalanceType(), wtaRuleTemplateDTO.getIntervalLength(), wtaRuleTemplateDTO.getValidationStartDateMillis(), wtaRuleTemplateDTO.getDaysLimit(), wtaRuleTemplateDTO.getIntervalUnit());
                break;
            case MAXIMUM_AVERAGE_SCHEDULED_TIME_PER_WEEK_WITHIN_AN_INTERVAL:
                wtaBaseRuleTemplate = new MaximumAverageScheduledTimeWTATemplate(wtaRuleTemplateDTO.getName().trim(),
                        wtaRuleTemplateDTO.getTemplateType(), wtaRuleTemplateDTO.getDisabled(), wtaRuleTemplateDTO.getDescription(), wtaRuleTemplateDTO.getBalanceType(), wtaRuleTemplateDTO.getIntervalLength(), wtaBaseRuleTemplate.getRecommendedValue(), wtaRuleTemplateDTO.getBalanceAdjustment(), wtaRuleTemplateDTO.getUseShiftTimes(), wtaRuleTemplateDTO.getMaximumAvgTime(), wtaRuleTemplateDTO.getIntervalUnit());
                break;
            case MAXIMUM_VETO_PER_PERIOD:
                wtaBaseRuleTemplate = new MaximumVetoPerPeriodWTATemplate(wtaRuleTemplateDTO.getName().trim(),
                        wtaRuleTemplateDTO.getTemplateType(), wtaRuleTemplateDTO.getDisabled(), wtaRuleTemplateDTO.getDescription(), wtaRuleTemplateDTO.getMaximumVetoPercentage());
                break;
            case NUMBER_OF_WEEKEND_SHIFTS_IN_A_PERIOD_COMPARED_TO_AVERAGE:
                wtaBaseRuleTemplate = new NumberOfWeekendShiftInPeriodWTATemplate(wtaRuleTemplateDTO.getName().trim(),
                        wtaRuleTemplateDTO.getTemplateType(), wtaRuleTemplateDTO.getDisabled(), wtaRuleTemplateDTO.getDescription(), wtaRuleTemplateDTO.getNumberShiftsPerPeriod(), wtaRuleTemplateDTO.getNumberOfWeeks(), wtaRuleTemplateDTO.getFromDayOfWeek(), wtaRuleTemplateDTO.getFromTime(), wtaRuleTemplateDTO.getProportional(), wtaRuleTemplateDTO.getToDayOfWeek(), wtaRuleTemplateDTO.getToTime());
                break;
            case CARE_DAYS_CHECK:
                wtaBaseRuleTemplate = new CareDayCheckWTATemplate(wtaRuleTemplateDTO.getName().trim(),
                        wtaRuleTemplateDTO.getTemplateType(), wtaRuleTemplateDTO.getDisabled(), wtaRuleTemplateDTO.getDescription(), wtaRuleTemplateDTO.getIntervalLength(), wtaRuleTemplateDTO.getValidationStartDateMillis(), wtaRuleTemplateDTO.getIntervalUnit(), wtaRuleTemplateDTO.getDaysLimit());
                break;
            case MINIMUM_DAILY_RESTING_TIME:
                wtaBaseRuleTemplate = new MinimumDailyRestingTimeWTATemplate(wtaRuleTemplateDTO.getName().trim(),
                        wtaRuleTemplateDTO.getTemplateType(), wtaRuleTemplateDTO.getDisabled(), wtaRuleTemplateDTO.getDescription(), wtaRuleTemplateDTO.getContinuousDayRestHours());
                break;
            case MINIMUM_DURATION_BETWEEN_SHIFTS:
                wtaBaseRuleTemplate = new MinimumDurationBetweenShiftWTATemplate(wtaRuleTemplateDTO.getName().trim(),
                        wtaRuleTemplateDTO.getTemplateType(), wtaRuleTemplateDTO.getDisabled(), wtaRuleTemplateDTO.getDescription(), wtaRuleTemplateDTO.getBalanceType(), wtaRuleTemplateDTO.getMinimumDurationBetweenShifts());
                break;
            case MINIMUM_WEEKLY_REST_PERIOD_FIXED_WEEKS:
                wtaBaseRuleTemplate = new MinimumWeeklyRestPeriodWTATemplate(wtaRuleTemplateDTO.getName().trim(),
                        wtaRuleTemplateDTO.getTemplateType(), wtaRuleTemplateDTO.getDisabled(), wtaRuleTemplateDTO.getDescription(), wtaRuleTemplateDTO.getContinuousWeekRest());
                break;
            case SHORTEST_AND_AVERAGE_DAILY_REST_FIXED_TIMES:
                wtaBaseRuleTemplate = new ShortestAndAverageDailyRestWTATemplate(wtaRuleTemplateDTO.getName().trim(),
                        wtaRuleTemplateDTO.getTemplateType(), wtaRuleTemplateDTO.getDisabled(), wtaRuleTemplateDTO.getDescription(), wtaRuleTemplateDTO.getBalanceType(), wtaRuleTemplateDTO.getIntervalLength(), wtaRuleTemplateDTO.getIntervalUnit(), wtaRuleTemplateDTO.getValidationStartDateMillis(), wtaRuleTemplateDTO.getContinuousDayRestHours(), wtaRuleTemplateDTO.getAverageRest(), wtaRuleTemplateDTO.getShiftAffiliation());
                break;
            case MAXIMUM_NUMBER_OF_SHIFTS_PER_INTERVAL:
                wtaBaseRuleTemplate = new MaximumShiftsInIntervalWTATemplate(wtaRuleTemplateDTO.getName().trim(),
                        wtaRuleTemplateDTO.getTemplateType(), wtaRuleTemplateDTO.getDisabled(), wtaRuleTemplateDTO.getDescription(), wtaRuleTemplateDTO.getBalanceType(), wtaRuleTemplateDTO.getIntervalLength(), wtaRuleTemplateDTO.getIntervalUnit(), wtaRuleTemplateDTO.getValidationStartDateMillis(), wtaRuleTemplateDTO.getShiftsLimit(), wtaRuleTemplateDTO.getOnlyCompositeShifts());
                break;
            case MAXIMUM_SENIOR_DAYS_PER_YEAR:
                wtaBaseRuleTemplate = new MaximumSeniorDaysInYearWTATemplate(wtaRuleTemplateDTO.getName().trim(),
                        wtaRuleTemplateDTO.getTemplateType(), wtaRuleTemplateDTO.getDisabled(), wtaRuleTemplateDTO.getDescription(), wtaRuleTemplateDTO.getIntervalLength(), wtaRuleTemplateDTO.getIntervalUnit(), wtaRuleTemplateDTO.getValidationStartDateMillis(), wtaRuleTemplateDTO.getDaysLimit(), wtaRuleTemplateDTO.getActivityCode());
                break;
            case MAXIMUM_TIME_BANK:
                wtaBaseRuleTemplate = new MaximumTimeBank(wtaRuleTemplateDTO.getName().trim(),
                        wtaRuleTemplateDTO.getTemplateType(), wtaRuleTemplateDTO.getDisabled(), wtaRuleTemplateDTO.getDescription(), wtaRuleTemplateDTO.getFrequency(), wtaRuleTemplateDTO.getYellowZone(), wtaRuleTemplateDTO.isForbid(), wtaRuleTemplateDTO.isAllowExtraActivity());
                break;
            case MINIMUM_TIME_BANK:
                wtaBaseRuleTemplate = new MinimumTimeBank(wtaRuleTemplateDTO.getName().trim(),
                        wtaRuleTemplateDTO.getTemplateType(), wtaRuleTemplateDTO.getDisabled(), wtaRuleTemplateDTO.getDescription(), wtaRuleTemplateDTO.getFrequency(), wtaRuleTemplateDTO.getYellowZone(), wtaRuleTemplateDTO.isForbid(), wtaRuleTemplateDTO.isAllowExtraActivity());
                break;
        }
        int number=getNumberFromlastInsertedTemplateType(lastInsertedTemplateType);

        String templateTypeToBeSet=originalTemplateType+"_";
        wtaBaseRuleTemplate.setTemplateType(templateTypeToBeSet+=++number);
        wtaBaseRuleTemplate.setRecommendedValue(wtaRuleTemplateDTO.getRecommendedValue());
        wtaBaseRuleTemplate.setPhaseTemplateValues(phaseTemplateValues);
        ruleTemplateCategory.getRuleTemplates().add(wtaBaseRuleTemplate);


        country.getWTABaseRuleTemplate().add(wtaBaseRuleTemplate);
        country.getRuleTemplateCategories().add(ruleTemplateCategory);
        save(country);
        wtaBaseRuleTemplate.setWTARuleTemplateCategory(wtaRuleTemplateDTO.getRuleTemplateCategory().getId());
        return wtaBaseRuleTemplate;


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

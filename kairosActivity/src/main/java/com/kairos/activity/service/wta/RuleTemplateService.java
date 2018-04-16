package com.kairos.activity.service.wta;


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
import com.kairos.activity.persistence.repository.wta.RuleTemplateCategoryMongoRepository;
import com.kairos.activity.persistence.repository.wta.WTABaseRuleTemplateMongoRepository;
import com.kairos.activity.service.MongoBaseService;
import com.kairos.activity.service.tag.TagService;
import com.kairos.activity.util.DateUtils;
import com.kairos.activity.util.userContext.CurrentUserDetails;
import com.kairos.activity.util.userContext.UserContext;

import com.kairos.response.dto.web.CountryDTO;
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
            ruleTemplateCategory.setCountry(countryDTO.getId());
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
        ruleTemplateCategoryMongoRepository.save(ruleTemplateCategory);

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
                consecutiveWorkingDays.setLimit(templateDTO.getDaysLimit());
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
                numberOfPartOfDayShifts.setNightsWorked(templateDTO.getNightsWorked());
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
                maximumDaysOffInPeriodWTATemplate.setBalanceType(templateDTO.getBalanceType());
                maximumDaysOffInPeriodWTATemplate.setDaysLimit(templateDTO.getDaysLimit());
                break;
            case AVERAGE_SHEDULED_TIME:
                AverageScheduledTimeWTATemplate maximumAverageScheduledTimeWTATemplate = (AverageScheduledTimeWTATemplate) oldTemplate;
                maximumAverageScheduledTimeWTATemplate.setDescription(templateDTO.getDescription());
                maximumAverageScheduledTimeWTATemplate.setUseShiftTimes(templateDTO.getUseShiftTimes());
                maximumAverageScheduledTimeWTATemplate.setIntervalLength(templateDTO.getIntervalLength());
                maximumAverageScheduledTimeWTATemplate.setIntervalUnit(templateDTO.getIntervalUnit());
                maximumAverageScheduledTimeWTATemplate.setMaximumAvgTime(templateDTO.getMaximumAvgTime());
                maximumAverageScheduledTimeWTATemplate.setBalanceType(templateDTO.getBalanceType());
                maximumAverageScheduledTimeWTATemplate.setValidationStartDateMillis(templateDTO.getValidationStartDateMillis());
                maximumAverageScheduledTimeWTATemplate.setBalanceAdjustment(templateDTO.getBalanceAdjustment());
                break;
            case VETO_PER_PERIOD:
                VetoPerPeriodWTATemplate maximumVetoPerPeriodWTATemplate = (VetoPerPeriodWTATemplate) oldTemplate;
                maximumVetoPerPeriodWTATemplate.setDescription(templateDTO.getDescription());
                maximumVetoPerPeriodWTATemplate.setMaximumVetoPercentage(templateDTO.getMaximumVetoPercentage());
                break;
            case NUMBER_OF_WEEKEND_SHIFT_IN_PERIOD:
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
            case DAILY_RESTING_TIME:
                DailyRestingTimeWTATemplate minimumDailyRestingTimeWTATemplate = (DailyRestingTimeWTATemplate) oldTemplate;
                minimumDailyRestingTimeWTATemplate.setId(oldTemplate.getId());
                minimumDailyRestingTimeWTATemplate.setDescription(templateDTO.getDescription());
                minimumDailyRestingTimeWTATemplate.setContinuousDayRestHours(templateDTO.getContinuousDayRestHours());
                break;
            case DURATION_BETWEEN_SHIFTS:
                DurationBetweenShiftWTATemplate minimumDurationBetweenShiftWTATemplate = (DurationBetweenShiftWTATemplate) oldTemplate;
                minimumDurationBetweenShiftWTATemplate.setDescription(templateDTO.getDescription());
                minimumDurationBetweenShiftWTATemplate.setBalanceType(templateDTO.getBalanceType());
                minimumDurationBetweenShiftWTATemplate.setMinimumDurationBetweenShifts(templateDTO.getMinimumDurationBetweenShifts());
                break;
            case WEEKLY_REST_PERIOD:
                WeeklyRestPeriodWTATemplate minimumWeeklyRestPeriodWTATemplate = (WeeklyRestPeriodWTATemplate) oldTemplate;
                minimumWeeklyRestPeriodWTATemplate.setDescription(templateDTO.getDescription());
                minimumWeeklyRestPeriodWTATemplate.setContinuousWeekRest(templateDTO.getContinuousWeekRest());
                break;
            case SHORTEST_AND_AVERAGE_DAILY_REST:
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
            case NUMBER_OF_SHIFTS_IN_INTERVAL:
                ShiftsInIntervalWTATemplate maximumShiftsInIntervalWTATemplate = (ShiftsInIntervalWTATemplate) oldTemplate;
                maximumShiftsInIntervalWTATemplate.setDescription(templateDTO.getDescription());
                maximumShiftsInIntervalWTATemplate.setBalanceType(templateDTO.getBalanceType());
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
        oldTemplate.setWTARuleTemplateCategory(ruleTemplateCategoryId);
        CurrentUserDetails currentUserDetails = UserContext.getUserDetails();
        List<PhaseTemplateValue> phaseTemplateValues = new ArrayList<>();
        BeanUtils.copyProperties(phaseTemplateValues,templateDTO.getPhaseTemplateValues());
        oldTemplate.setPhaseTemplateValues(phaseTemplateValues);
        oldTemplate.setDisabled(templateDTO.getDisabled());
        oldTemplate.setWTARuleTemplateCategory(ruleTemplateCategoryId);
        oldTemplate.setRecommendedValue(templateDTO.getRecommendedValue());

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
            previousRuleTemplateCategory.setRuleTemplates(wtaBaseRuleTemplates);
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
        if(!Optional.ofNullable(wtaRuleTemplateDTO.getTemplateType()).isPresent()){
            throw new DataNotFoundByIdException("No templateType found");
        }
        RuleTemplateCategory ruleTemplateCategory = ruleTemplateCategoryMongoRepository.findByName(countryId, wtaRuleTemplateDTO.getRuleTemplateCategory().getName(), RuleTemplateCategoryType.WTA);
        if (!Optional.ofNullable(ruleTemplateCategory).isPresent()) {
            throw new DataNotFoundByIdException("Category Not matched");
        }

        WTABaseRuleTemplate wtaBaseRuleTemplate1 = wtaBaseRuleTemplateMongoRepository.existsByName(countryId,wtaRuleTemplateDTO.getName().trim().toLowerCase());
        if (Optional.ofNullable(wtaBaseRuleTemplate1).isPresent()) {
            throw new DuplicateDataException("WTA Rule template already existed  " + wtaRuleTemplateDTO.getName());
        }

        List<PhaseTemplateValue> phaseTemplateValues = wtaOrganizationService.copyPhaseTemplateValue(wtaRuleTemplateDTO.getPhaseTemplateValues());
        String originalTemplateType=getTemplateType(wtaRuleTemplateDTO.getTemplateType());
        WTATemplateType ruleTemplateType = getByTemplateType(originalTemplateType);
        String lastInsertedTemplateType= wtaBaseRuleTemplateMongoRepository.getLastInsertedTemplateType(countryId,originalTemplateType);
        if(!Optional.ofNullable(lastInsertedTemplateType).isPresent()){
            throw new DataNotFoundByIdException("No templateType found"+originalTemplateType);
        }

        switch (ruleTemplateType){
            case SHIFT_LENGTH:
                ShiftLengthWTATemplate shiftLengthWTATemplate = new ShiftLengthWTATemplate(wtaRuleTemplateDTO.getName().trim(),true, wtaRuleTemplateDTO.getDescription(), wtaRuleTemplateDTO.getTimeLimit(),  wtaRuleTemplateDTO.getCheckAgainstTimeRules());
                shiftLengthWTATemplate.setRecommendedValue(wtaRuleTemplateDTO.getRecommendedValue());
                shiftLengthWTATemplate.setPhaseTemplateValues(phaseTemplateValues);
                shiftLengthWTATemplate.setWTARuleTemplateCategory(ruleTemplateCategory.getId());
                shiftLengthWTATemplate.setCountryId(country.getId());
                save(shiftLengthWTATemplate);
                wtaRuleTemplateDTO = new WTARuleTemplateDTO();
                BeanUtils.copyProperties(shiftLengthWTATemplate,wtaRuleTemplateDTO);
                break;
            case CONSECUTIVE_WORKING_PARTOFDAY:
                ConsecutiveWorkWTATemplate consecutiveWorkWTATemplate = new ConsecutiveWorkWTATemplate(wtaRuleTemplateDTO.getName().trim(),
                        true, wtaRuleTemplateDTO.getDescription(), wtaRuleTemplateDTO.getCheckAgainstTimeRules(), wtaRuleTemplateDTO.getDaysLimit());
                consecutiveWorkWTATemplate.setRecommendedValue(wtaRuleTemplateDTO.getRecommendedValue());
                consecutiveWorkWTATemplate.setPhaseTemplateValues(phaseTemplateValues);
                consecutiveWorkWTATemplate.setWTARuleTemplateCategory(ruleTemplateCategory.getId());
                consecutiveWorkWTATemplate.setCountryId(country.getId());
                save(consecutiveWorkWTATemplate);
                wtaRuleTemplateDTO = new WTARuleTemplateDTO();
                BeanUtils.copyProperties(consecutiveWorkWTATemplate,wtaRuleTemplateDTO);
                break;

            case NUMBER_OF_PARTOFDAY:
                NumberOfPartOfDayShiftsWTATemplate numberOfPartOfDayShiftsWTATemplate = new NumberOfPartOfDayShiftsWTATemplate(wtaRuleTemplateDTO.getName().trim(), wtaRuleTemplateDTO.getDisabled(), wtaRuleTemplateDTO.getDescription(), wtaRuleTemplateDTO.getDaysLimit());
                numberOfPartOfDayShiftsWTATemplate.setRecommendedValue(wtaRuleTemplateDTO.getRecommendedValue());
                numberOfPartOfDayShiftsWTATemplate.setPhaseTemplateValues(phaseTemplateValues);
                numberOfPartOfDayShiftsWTATemplate.setWTARuleTemplateCategory(ruleTemplateCategory.getId());
                numberOfPartOfDayShiftsWTATemplate.setCountryId(country.getId());
                save(numberOfPartOfDayShiftsWTATemplate);
                wtaRuleTemplateDTO = new WTARuleTemplateDTO();
                BeanUtils.copyProperties(numberOfPartOfDayShiftsWTATemplate,wtaRuleTemplateDTO);
                break;
            case CONSECUTIVE_NIGHTS_AND_DAYS:
                ConsecutiveRestPartOfDayWTATemplate consecutiveRestPartOfDayWTATemplate = new ConsecutiveRestPartOfDayWTATemplate(wtaRuleTemplateDTO.getName().trim(), wtaRuleTemplateDTO.getDisabled(), wtaRuleTemplateDTO.getDescription(), wtaRuleTemplateDTO.getMinimumRest(), wtaRuleTemplateDTO.getNightsWorked());
                consecutiveRestPartOfDayWTATemplate.setRecommendedValue(wtaRuleTemplateDTO.getRecommendedValue());
                consecutiveRestPartOfDayWTATemplate.setPhaseTemplateValues(phaseTemplateValues);
                consecutiveRestPartOfDayWTATemplate.setWTARuleTemplateCategory(ruleTemplateCategory.getId());
                consecutiveRestPartOfDayWTATemplate.setCountryId(country.getId());
                save(consecutiveRestPartOfDayWTATemplate);
                wtaRuleTemplateDTO = new WTARuleTemplateDTO();
                BeanUtils.copyProperties(consecutiveRestPartOfDayWTATemplate,wtaRuleTemplateDTO);
                break;
            case DAYS_OFF_IN_PERIOD:
                DaysOffInPeriodWTATemplate daysOffInPeriodWTATemplate = new DaysOffInPeriodWTATemplate(wtaRuleTemplateDTO.getName().trim(),
                        wtaRuleTemplateDTO.getTemplateType(), wtaRuleTemplateDTO.getDisabled(), wtaRuleTemplateDTO.getDescription(), wtaRuleTemplateDTO.getBalanceType(), wtaRuleTemplateDTO.getIntervalLength(), wtaRuleTemplateDTO.getValidationStartDateMillis(), wtaRuleTemplateDTO.getDaysLimit(), wtaRuleTemplateDTO.getIntervalUnit());
                daysOffInPeriodWTATemplate.setRecommendedValue(wtaRuleTemplateDTO.getRecommendedValue());
                daysOffInPeriodWTATemplate.setPhaseTemplateValues(phaseTemplateValues);
                daysOffInPeriodWTATemplate.setWTARuleTemplateCategory(ruleTemplateCategory.getId());
                daysOffInPeriodWTATemplate.setCountryId(country.getId());
                save(daysOffInPeriodWTATemplate);
                wtaRuleTemplateDTO = new WTARuleTemplateDTO();
                BeanUtils.copyProperties(daysOffInPeriodWTATemplate,wtaRuleTemplateDTO);
                break;
            case AVERAGE_SHEDULED_TIME:
                AverageScheduledTimeWTATemplate averageScheduledTimeWTATemplate = new AverageScheduledTimeWTATemplate(wtaRuleTemplateDTO.getName().trim(), wtaRuleTemplateDTO.getDisabled(), wtaRuleTemplateDTO.getDescription(), wtaRuleTemplateDTO.getBalanceType(), wtaRuleTemplateDTO.getIntervalLength(), wtaRuleTemplateDTO.getRecommendedValue(), wtaRuleTemplateDTO.getBalanceAdjustment(), wtaRuleTemplateDTO.getUseShiftTimes(), wtaRuleTemplateDTO.getMaximumAvgTime(), wtaRuleTemplateDTO.getIntervalUnit());
                averageScheduledTimeWTATemplate.setRecommendedValue(wtaRuleTemplateDTO.getRecommendedValue());
                averageScheduledTimeWTATemplate.setPhaseTemplateValues(phaseTemplateValues);
                averageScheduledTimeWTATemplate.setWTARuleTemplateCategory(ruleTemplateCategory.getId());
                averageScheduledTimeWTATemplate.setCountryId(country.getId());
                save(averageScheduledTimeWTATemplate);
                wtaRuleTemplateDTO = new WTARuleTemplateDTO();
                BeanUtils.copyProperties(averageScheduledTimeWTATemplate,wtaRuleTemplateDTO);
                break;
            case VETO_PER_PERIOD:
                VetoPerPeriodWTATemplate vetoPerPeriodWTATemplate = new VetoPerPeriodWTATemplate(wtaRuleTemplateDTO.getName().trim(),
                        wtaRuleTemplateDTO.getTemplateType(), wtaRuleTemplateDTO.getDisabled(), wtaRuleTemplateDTO.getDescription(), wtaRuleTemplateDTO.getMaximumVetoPercentage());
                vetoPerPeriodWTATemplate.setRecommendedValue(wtaRuleTemplateDTO.getRecommendedValue());
                vetoPerPeriodWTATemplate.setPhaseTemplateValues(phaseTemplateValues);
                vetoPerPeriodWTATemplate.setWTARuleTemplateCategory(ruleTemplateCategory.getId());
                vetoPerPeriodWTATemplate.setCountryId(country.getId());
                save(vetoPerPeriodWTATemplate);
                wtaRuleTemplateDTO = new WTARuleTemplateDTO();
                BeanUtils.copyProperties(vetoPerPeriodWTATemplate,wtaRuleTemplateDTO);
                break;
            case NUMBER_OF_WEEKEND_SHIFT_IN_PERIOD:
                NumberOfWeekendShiftInPeriodWTATemplate numberOfWeekendShiftInPeriodWTATemplate = new NumberOfWeekendShiftInPeriodWTATemplate(wtaRuleTemplateDTO.getName().trim(),
                        wtaRuleTemplateDTO.getTemplateType(), wtaRuleTemplateDTO.getDisabled(), wtaRuleTemplateDTO.getDescription(), wtaRuleTemplateDTO.getNumberShiftsPerPeriod(), wtaRuleTemplateDTO.getNumberOfWeeks(), wtaRuleTemplateDTO.getFromDayOfWeek(), wtaRuleTemplateDTO.getFromTime(), wtaRuleTemplateDTO.getProportional(), wtaRuleTemplateDTO.getToDayOfWeek(), wtaRuleTemplateDTO.getToTime());
                numberOfWeekendShiftInPeriodWTATemplate.setRecommendedValue(wtaRuleTemplateDTO.getRecommendedValue());
                numberOfWeekendShiftInPeriodWTATemplate.setPhaseTemplateValues(phaseTemplateValues);
                numberOfWeekendShiftInPeriodWTATemplate.setWTARuleTemplateCategory(ruleTemplateCategory.getId());
                numberOfWeekendShiftInPeriodWTATemplate.setCountryId(country.getId());
                save(numberOfWeekendShiftInPeriodWTATemplate);
                wtaRuleTemplateDTO = new WTARuleTemplateDTO();
                BeanUtils.copyProperties(numberOfWeekendShiftInPeriodWTATemplate,wtaRuleTemplateDTO);
                break;

            case CARE_DAYS_CHECK:
                CareDayCheckWTATemplate careDayCheckWTATemplate = new CareDayCheckWTATemplate(wtaRuleTemplateDTO.getName().trim(),
                        wtaRuleTemplateDTO.getTemplateType(), wtaRuleTemplateDTO.getDisabled(), wtaRuleTemplateDTO.getDescription(), wtaRuleTemplateDTO.getIntervalLength(), wtaRuleTemplateDTO.getValidationStartDateMillis(), wtaRuleTemplateDTO.getIntervalUnit(), wtaRuleTemplateDTO.getDaysLimit());
                careDayCheckWTATemplate.setRecommendedValue(wtaRuleTemplateDTO.getRecommendedValue());
                careDayCheckWTATemplate.setPhaseTemplateValues(phaseTemplateValues);
                careDayCheckWTATemplate.setWTARuleTemplateCategory(ruleTemplateCategory.getId());
                careDayCheckWTATemplate.setCountryId(country.getId());
                save(careDayCheckWTATemplate);
                wtaRuleTemplateDTO = new WTARuleTemplateDTO();
                BeanUtils.copyProperties(careDayCheckWTATemplate,wtaRuleTemplateDTO);
                break;
            case DAILY_RESTING_TIME:
                DailyRestingTimeWTATemplate dailyRestingTimeWTATemplate = new DailyRestingTimeWTATemplate(wtaRuleTemplateDTO.getName().trim(),
                         wtaRuleTemplateDTO.getDisabled(), wtaRuleTemplateDTO.getDescription(), wtaRuleTemplateDTO.getContinuousDayRestHours());
                dailyRestingTimeWTATemplate.setRecommendedValue(wtaRuleTemplateDTO.getRecommendedValue());
                dailyRestingTimeWTATemplate.setPhaseTemplateValues(phaseTemplateValues);
                dailyRestingTimeWTATemplate.setWTARuleTemplateCategory(ruleTemplateCategory.getId());
                dailyRestingTimeWTATemplate.setCountryId(country.getId());
                save(dailyRestingTimeWTATemplate);
                wtaRuleTemplateDTO = new WTARuleTemplateDTO();
                BeanUtils.copyProperties(dailyRestingTimeWTATemplate,wtaRuleTemplateDTO);
                break;
            case DURATION_BETWEEN_SHIFTS:
                DurationBetweenShiftWTATemplate durationBetweenShiftWTATemplate = new DurationBetweenShiftWTATemplate(wtaRuleTemplateDTO.getName().trim(),
                        wtaRuleTemplateDTO.getTemplateType(), wtaRuleTemplateDTO.getDisabled(), wtaRuleTemplateDTO.getDescription(), wtaRuleTemplateDTO.getBalanceType(), wtaRuleTemplateDTO.getMinimumDurationBetweenShifts());
                durationBetweenShiftWTATemplate.setRecommendedValue(wtaRuleTemplateDTO.getRecommendedValue());
                durationBetweenShiftWTATemplate.setPhaseTemplateValues(phaseTemplateValues);
                durationBetweenShiftWTATemplate.setWTARuleTemplateCategory(ruleTemplateCategory.getId());
                durationBetweenShiftWTATemplate.setCountryId(country.getId());
                save(durationBetweenShiftWTATemplate);
                wtaRuleTemplateDTO = new WTARuleTemplateDTO();
                BeanUtils.copyProperties(durationBetweenShiftWTATemplate,wtaRuleTemplateDTO);
                break;
            case WEEKLY_REST_PERIOD:
                WeeklyRestPeriodWTATemplate weeklyRestPeriodWTATemplate = new WeeklyRestPeriodWTATemplate(wtaRuleTemplateDTO.getName().trim(),
                        wtaRuleTemplateDTO.getTemplateType(), wtaRuleTemplateDTO.getDisabled(), wtaRuleTemplateDTO.getDescription(), wtaRuleTemplateDTO.getContinuousWeekRest());
                weeklyRestPeriodWTATemplate.setRecommendedValue(wtaRuleTemplateDTO.getRecommendedValue());
                weeklyRestPeriodWTATemplate.setPhaseTemplateValues(phaseTemplateValues);
                weeklyRestPeriodWTATemplate.setWTARuleTemplateCategory(ruleTemplateCategory.getId());
                weeklyRestPeriodWTATemplate.setCountryId(country.getId());
                save(weeklyRestPeriodWTATemplate);
                wtaRuleTemplateDTO = new WTARuleTemplateDTO();
                BeanUtils.copyProperties(weeklyRestPeriodWTATemplate,wtaRuleTemplateDTO);
                break;
            case SHORTEST_AND_AVERAGE_DAILY_REST:
                ShortestAndAverageDailyRestWTATemplate shortestAndAverageDailyRestWTATemplate = new ShortestAndAverageDailyRestWTATemplate(wtaRuleTemplateDTO.getName().trim(),
                        wtaRuleTemplateDTO.getTemplateType(), wtaRuleTemplateDTO.getDisabled(), wtaRuleTemplateDTO.getDescription(), wtaRuleTemplateDTO.getBalanceType(), wtaRuleTemplateDTO.getIntervalLength(), wtaRuleTemplateDTO.getIntervalUnit(), wtaRuleTemplateDTO.getValidationStartDateMillis(), wtaRuleTemplateDTO.getContinuousDayRestHours(), wtaRuleTemplateDTO.getAverageRest(), wtaRuleTemplateDTO.getShiftAffiliation());
                shortestAndAverageDailyRestWTATemplate.setRecommendedValue(wtaRuleTemplateDTO.getRecommendedValue());
                shortestAndAverageDailyRestWTATemplate.setPhaseTemplateValues(phaseTemplateValues);
                shortestAndAverageDailyRestWTATemplate.setWTARuleTemplateCategory(ruleTemplateCategory.getId());
                shortestAndAverageDailyRestWTATemplate.setCountryId(country.getId());
                save(shortestAndAverageDailyRestWTATemplate);
                wtaRuleTemplateDTO = new WTARuleTemplateDTO();
                BeanUtils.copyProperties(shortestAndAverageDailyRestWTATemplate,wtaRuleTemplateDTO);
                break;
            case NUMBER_OF_SHIFTS_IN_INTERVAL:
                ShiftsInIntervalWTATemplate shiftsInIntervalWTATemplate = new ShiftsInIntervalWTATemplate(wtaRuleTemplateDTO.getName().trim(),
                        wtaRuleTemplateDTO.getTemplateType(), wtaRuleTemplateDTO.getDisabled(), wtaRuleTemplateDTO.getDescription(), wtaRuleTemplateDTO.getBalanceType(), wtaRuleTemplateDTO.getIntervalLength(), wtaRuleTemplateDTO.getIntervalUnit(), wtaRuleTemplateDTO.getValidationStartDateMillis(), wtaRuleTemplateDTO.getShiftsLimit(), wtaRuleTemplateDTO.getOnlyCompositeShifts());
                shiftsInIntervalWTATemplate.setRecommendedValue(wtaRuleTemplateDTO.getRecommendedValue());
                shiftsInIntervalWTATemplate.setPhaseTemplateValues(phaseTemplateValues);
                shiftsInIntervalWTATemplate.setWTARuleTemplateCategory(ruleTemplateCategory.getId());
                shiftsInIntervalWTATemplate.setCountryId(country.getId());
                save(shiftsInIntervalWTATemplate);
                wtaRuleTemplateDTO = new WTARuleTemplateDTO();
                BeanUtils.copyProperties(shiftsInIntervalWTATemplate,wtaRuleTemplateDTO);
                break;
            case MAXIMUM_SENIOR_DAYS_IN_YEAR:
                SeniorDaysInYearWTATemplate seniorDaysInYearWTATemplate = new SeniorDaysInYearWTATemplate(wtaRuleTemplateDTO.getName().trim(),
                        wtaRuleTemplateDTO.getTemplateType(), wtaRuleTemplateDTO.getDisabled(), wtaRuleTemplateDTO.getDescription(), wtaRuleTemplateDTO.getIntervalLength(), wtaRuleTemplateDTO.getIntervalUnit(), wtaRuleTemplateDTO.getValidationStartDateMillis(), wtaRuleTemplateDTO.getDaysLimit(), wtaRuleTemplateDTO.getActivityCode());
                seniorDaysInYearWTATemplate.setRecommendedValue(wtaRuleTemplateDTO.getRecommendedValue());
                seniorDaysInYearWTATemplate.setPhaseTemplateValues(phaseTemplateValues);
                seniorDaysInYearWTATemplate.setWTARuleTemplateCategory(ruleTemplateCategory.getId());
                seniorDaysInYearWTATemplate.setCountryId(country.getId());
                save(seniorDaysInYearWTATemplate);
                wtaRuleTemplateDTO = new WTARuleTemplateDTO();
                BeanUtils.copyProperties(seniorDaysInYearWTATemplate,wtaRuleTemplateDTO);
                break;
            case TIME_BANK:
                TimeBankWTATemplate timeBankWTATemplate = new TimeBankWTATemplate(wtaRuleTemplateDTO.getName().trim(),
                        wtaRuleTemplateDTO.getTemplateType(), wtaRuleTemplateDTO.getDisabled(), wtaRuleTemplateDTO.getDescription(), wtaRuleTemplateDTO.getFrequency(), wtaRuleTemplateDTO.getYellowZone(), wtaRuleTemplateDTO.isForbid(), wtaRuleTemplateDTO.isAllowExtraActivity());
                timeBankWTATemplate.setRecommendedValue(wtaRuleTemplateDTO.getRecommendedValue());
                timeBankWTATemplate.setPhaseTemplateValues(phaseTemplateValues);
                timeBankWTATemplate.setWTARuleTemplateCategory(ruleTemplateCategory.getId());
                timeBankWTATemplate.setCountryId(country.getId());
                save(timeBankWTATemplate);
                wtaRuleTemplateDTO = new WTARuleTemplateDTO();
                BeanUtils.copyProperties(timeBankWTATemplate,wtaRuleTemplateDTO);
                break;
        }
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

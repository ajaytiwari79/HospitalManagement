package com.kairos.service.agreement;


import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.InvalidRequestException;
import com.kairos.persistence.model.user.agreement.cta.RuleTemplate;
import com.kairos.persistence.model.user.agreement.cta.RuleTemplateCategoryType;
import com.kairos.persistence.model.user.agreement.wta.templates.RuleTemplateCategory;
import com.kairos.persistence.model.user.agreement.wta.templates.WTABaseRuleTemplate;
import com.kairos.persistence.model.user.agreement.wta.templates.WTARuleTemplateQueryResponse;
import com.kairos.persistence.model.user.agreement.wta.templates.template_types.*;
import com.kairos.persistence.model.user.country.Country;
import com.kairos.persistence.repository.user.agreement.wta.RuleTemplateCategoryGraphRepository;
import com.kairos.persistence.repository.user.agreement.wta.TemplateCategoryRelationGraphRepository;
import com.kairos.persistence.repository.user.agreement.wta.WTABaseRuleTemplateGraphRepository;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.response.dto.web.WTARuleTemplateDTO;
import com.kairos.service.UserBaseService;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.*;

import static com.kairos.constants.AppConstants.*;

/**
 * Created by pawanmandhan on 5/8/17.
 */

@Service
public class RuleTemplateService extends UserBaseService {


    private List<String> ruleTemplate = new ArrayList<String>();
    @Inject
    private TemplateCategoryRelationGraphRepository templateRelationShipGraphRepository;
    /* Stream.of(RuleTemplate.values())
            .map(RuleTemplate::name)
            .collect(Collectors.toList());*/
    @Inject
    private WTABaseRuleTemplateGraphRepository wtaRuleTemplateGraphRepository;
    @Inject
    private CountryGraphRepository countryGraphRepository;
    @Inject
    private RuleTemplateCategoryService ruleTemplateCategoryService;
    @Inject
    private RuleTemplateCategoryGraphRepository ruleTemplateCategoryRepository;

    public boolean createRuleTemplate(long countryId) {

        List<RuleTemplate> baseRuleTemplates = new ArrayList<>();

        Country country = countryGraphRepository.findOne(countryId);

        if (country == null) {
            throw new DataNotFoundByIdException("Invalid organisation");
        }


        RuleTemplateCategory ruleTemplateCategory = new RuleTemplateCategory("NONE");
        ruleTemplateCategoryService.createRuleTemplateCategory(countryId, ruleTemplateCategory);
        ruleTemplateCategory = ruleTemplateCategoryRepository.findByName(countryId, "NONE", RuleTemplateCategoryType.WTA);
        String MONTHS = "MONTHS";
        String TUESDAY = "TUESDAY";
        long timeInMins = 10;
        long daysCount = 10;
        long dateInMillis = new Date().getTime();


        MaximumShiftLengthWTATemplate maximumShiftLengthWTATemplate = new MaximumShiftLengthWTATemplate(TEMPLATE1_NAME, TEMPLATE1, true, TEMPLATE1_DESCRIPTION, timeInMins, ruleTemplate, true);
        baseRuleTemplates.add(maximumShiftLengthWTATemplate);

        MinimumShiftLengthWTATemplate minimumShiftLengthWTATemplate = new MinimumShiftLengthWTATemplate(TEMPLATE2_NAME, TEMPLATE2, true, TEMPLATE2_DESCRIPTION, timeInMins, ruleTemplate, true);
        baseRuleTemplates.add(minimumShiftLengthWTATemplate);

        MaximumConsecutiveWorkingDaysWTATemplate maximumConsecutiveWorkingDaysWTATemplate = new MaximumConsecutiveWorkingDaysWTATemplate(TEMPLATE3_NAME, TEMPLATE3, true, TEMPLATE3_DESCRIPTION, ruleTemplate, true, daysCount);
        baseRuleTemplates.add(maximumConsecutiveWorkingDaysWTATemplate);

        MinimumRestInConsecutiveDaysWTATemplate minimumRestInConsecutiveDaysWTATemplate = new MinimumRestInConsecutiveDaysWTATemplate(TEMPLATE4_NAME, TEMPLATE4, true, TEMPLATE4_DESCRIPTION, timeInMins, daysCount);
        baseRuleTemplates.add(minimumRestInConsecutiveDaysWTATemplate);

        MaximumNightShiftLengthWTATemplate maximumNightShiftLengthWTATemplate = new MaximumNightShiftLengthWTATemplate(TEMPLATE5_NAME, TEMPLATE5, true, TEMPLATE5_DESCRIPTION, timeInMins, ruleTemplate, true);
        baseRuleTemplates.add(maximumNightShiftLengthWTATemplate);

        MinimumConsecutiveNightsWTATemplate minimumConsecutiveNightsWTATemplate = new MinimumConsecutiveNightsWTATemplate(TEMPLATE6_NAME, TEMPLATE6, true, TEMPLATE6_DESCRIPTION, daysCount);
        baseRuleTemplates.add(minimumConsecutiveNightsWTATemplate);

        MaximumConsecutiveWorkingNightsWTATemplate maximumConsecutiveWorkingNights = new MaximumConsecutiveWorkingNightsWTATemplate(TEMPLATE7_NAME, TEMPLATE7, true, TEMPLATE7_DESCRIPTION, ruleTemplate, true, daysCount);
        baseRuleTemplates.add(maximumConsecutiveWorkingNights);

        MinimumRestConsecutiveNightsWTATemplate minimumRestConsecutiveNightsWTATemplate = new MinimumRestConsecutiveNightsWTATemplate(TEMPLATE8_NAME, TEMPLATE8, true, TEMPLATE7_DESCRIPTION, ruleTemplate, timeInMins, daysCount);
        baseRuleTemplates.add(minimumRestConsecutiveNightsWTATemplate);

        MaximumNumberOfNightsWTATemplate maximumNumberOfNightsWTATemplate = new MaximumNumberOfNightsWTATemplate(TEMPLATE9_NAME, TEMPLATE9, true, TEMPLATE9_DESCRIPTION, ruleTemplate, daysCount, 12, dateInMillis, MONTHS);
        baseRuleTemplates.add(maximumNumberOfNightsWTATemplate);

        MaximumDaysOffInPeriodWTATemplate maximumDaysOffInPeriodWTATemplate = new MaximumDaysOffInPeriodWTATemplate(TEMPLATE10_NAME, TEMPLATE10, true, TEMPLATE10_DESCRIPTION, ruleTemplate, 12, dateInMillis, 12, MONTHS);
        baseRuleTemplates.add(maximumDaysOffInPeriodWTATemplate);

        MaximumAverageScheduledTimeWTATemplate maximumAverageScheduledTimeWTATemplate = new MaximumAverageScheduledTimeWTATemplate(TEMPLATE11_NAME, TEMPLATE11, true, TEMPLATE11_DESCRIPTION, ruleTemplate, 12, dateInMillis, true, true, timeInMins, MONTHS);
        baseRuleTemplates.add(maximumAverageScheduledTimeWTATemplate);

        MaximumVetoPerPeriodWTATemplate maximumVetoPerPeriodWTATemplate = new MaximumVetoPerPeriodWTATemplate(TEMPLATE12_NAME, TEMPLATE12, true, TEMPLATE12_DESCRIPTION, 2.0);
        baseRuleTemplates.add(maximumVetoPerPeriodWTATemplate);

        NumberOfWeekendShiftInPeriodWTATemplate numberOfWeekendShiftInPeriodWTATemplate = new NumberOfWeekendShiftInPeriodWTATemplate(TEMPLATE13_NAME, TEMPLATE13, true, TEMPLATE13_DESCRIPTION, 12, 12, TUESDAY, 2, true, TUESDAY, 1);
        baseRuleTemplates.add(numberOfWeekendShiftInPeriodWTATemplate);

        CareDayCheckWTATemplate careDayCheckWTATemplate = new CareDayCheckWTATemplate(TEMPLATE14_NAME, TEMPLATE14, true, TEMPLATE14_DESCRIPTION, 2, dateInMillis, MONTHS, 1);
        baseRuleTemplates.add(careDayCheckWTATemplate);

        MinimumDailyRestingTimeWTATemplate minimumDailyRestingTimeWTATemplate = new MinimumDailyRestingTimeWTATemplate(TEMPLATE15_NAME, TEMPLATE15, true, TEMPLATE15_DESCRIPTION, timeInMins);
        baseRuleTemplates.add(minimumDailyRestingTimeWTATemplate);

        MinimumDurationBetweenShiftWTATemplate minimumDurationBetweenShiftWTATemplate = new MinimumDurationBetweenShiftWTATemplate(TEMPLATE16_NAME, TEMPLATE16, true, TEMPLATE16_DESCRIPTION, ruleTemplate, timeInMins);
        baseRuleTemplates.add(minimumDurationBetweenShiftWTATemplate);


        MinimumWeeklyRestPeriodWTATemplate minimumWeeklyRestPeriodWTATemplate = new MinimumWeeklyRestPeriodWTATemplate(TEMPLATE17_NAME, TEMPLATE17, true, TEMPLATE17_DESCRIPTION, timeInMins);
        baseRuleTemplates.add(minimumWeeklyRestPeriodWTATemplate);

        ShortestAndAverageDailyRestWTATemplate shortestAndAverageDailyRestWTATemplate = new ShortestAndAverageDailyRestWTATemplate(TEMPLATE18_NAME, TEMPLATE18, true, TEMPLATE18_DESCRIPTION, ruleTemplate, 1, "NA", dateInMillis, timeInMins, timeInMins, "");
        baseRuleTemplates.add(shortestAndAverageDailyRestWTATemplate);

        MaximumShiftsInIntervalWTATemplate maximumShiftsInIntervalWTATemplate = new MaximumShiftsInIntervalWTATemplate(TEMPLATE19_NAME, TEMPLATE19, true, TEMPLATE19_DESCRIPTION, ruleTemplate, 1, "NA", dateInMillis, 1, true);
        baseRuleTemplates.add(maximumShiftsInIntervalWTATemplate);

        MaximumSeniorDaysInYearWTATemplate wta20 = new MaximumSeniorDaysInYearWTATemplate(TEMPLATE20_NAME, TEMPLATE20, true, TEMPLATE20_DESCRIPTION, 1, "NA", dateInMillis, 1, "");
        baseRuleTemplates.add(wta20);

        country.setWTABaseRuleTemplate(baseRuleTemplates);
        save(country);

        ruleTemplateCategory.setRuleTemplates(baseRuleTemplates);
        ruleTemplateCategoryRepository.save(ruleTemplateCategory);
        /*for (WTABaseRuleTemplate template : country.getWTABaseRuleTemplate()) {
            template.setRuleTemplateCategory(ruleTemplateCategory);
            //wtaRuleTemplateGraphRepository.findOne(template.getId());
            //TemplateCategoryRelation rel = new TemplateCategoryRelation(template, ruleTemplateCategory);
            //save(rel);
        }*/

        //wtaRuleTemplateGraphRepository.addCategoryInAllTemplate(ruleTemplateIdList, ruleTemplateCategory.getId());

        return true;
    }

    public Map getRuleTemplate(long countryId) {

        List<WTABaseRuleTemplate> baseRuleTemplates = new ArrayList<WTABaseRuleTemplate>();

        List<WTARuleTemplateQueryResponse> wtaResponse = null;

        if (countryGraphRepository == null) {
            System.out.println("country is " + countryGraphRepository);
        }
        try {
            wtaResponse
                    = countryGraphRepository.getRuleTemplatesAndCategories(countryId);
        } catch (Exception e) {
            System.out.println(e);


        }
        Country country = countryGraphRepository.findOne(countryId);
        if (country == null) {
            throw new DataNotFoundByIdException("Invalid Country");
        }

        List<RuleTemplateCategory> categoryList = ruleTemplateCategoryRepository.getRuleTemplateCategoryByCountry(countryId, RuleTemplateCategoryType.WTA);

        if (categoryList == null) {
            throw new DataNotFoundByIdException("Category List is null");
        }

        List<RuleTemplate> templateList = country.getWTABaseRuleTemplate();
        if (templateList == null) {
            throw new DataNotFoundByIdException("Template List is null");
        }


        Map response = new HashMap();
        response.put("categoryList", categoryList);
        response.put("templateList", wtaResponse);

        return response;
    }

    public WTARuleTemplateQueryResponse updateRuleTemplate(long countryId, String templateType, WTARuleTemplateDTO templateDTO) {
        Country country = countryGraphRepository.findOne(countryId);
        if (!Optional.ofNullable(country).isPresent()) {
            throw new DataNotFoundByIdException("Invalid Country");
        }

        WTABaseRuleTemplate oldTemplate = wtaRuleTemplateGraphRepository.findOne(templateDTO.getId());
        if (!Optional.ofNullable(oldTemplate).isPresent()) {
            throw new DataNotFoundByIdException("Invalid TemplateType id " + templateDTO.getId());
        }
        switch (oldTemplate.getTemplateType()) {

            case TEMPLATE1:
                MaximumShiftLengthWTATemplate maximumShiftLengthWTATemplate = (MaximumShiftLengthWTATemplate) oldTemplate;//oldTemplate;

                maximumShiftLengthWTATemplate.setDescription(templateDTO.getDescription());
                maximumShiftLengthWTATemplate.setTimeLimit(templateDTO.getTimeLimit());
                maximumShiftLengthWTATemplate.setBalanceType(templateDTO.getBalanceType());
                maximumShiftLengthWTATemplate.setCheckAgainstTimeRules(templateDTO.isCheckAgainstTimeRules());
                maximumShiftLengthWTATemplate.setDisabled(templateDTO.isActive());
                oldTemplate = save(maximumShiftLengthWTATemplate);

                break;

            case TEMPLATE2:
                MinimumShiftLengthWTATemplate minimumShiftLengthWTATemplate = (MinimumShiftLengthWTATemplate) oldTemplate;
                minimumShiftLengthWTATemplate.setDescription(templateDTO.getDescription());
                minimumShiftLengthWTATemplate.setTimeLimit(templateDTO.getTimeLimit());
                minimumShiftLengthWTATemplate.setBalanceType(templateDTO.getBalanceType());
                minimumShiftLengthWTATemplate.setCheckAgainstTimeRules(templateDTO.isCheckAgainstTimeRules());
                minimumShiftLengthWTATemplate.setDisabled(templateDTO.isActive());
                oldTemplate = save(minimumShiftLengthWTATemplate);
                break;

            case TEMPLATE3:
                MaximumConsecutiveWorkingDaysWTATemplate maximumConsecutiveWorkingDaysWTATemplate = (MaximumConsecutiveWorkingDaysWTATemplate) oldTemplate;
                maximumConsecutiveWorkingDaysWTATemplate.setDisabled(templateDTO.isActive());

                maximumConsecutiveWorkingDaysWTATemplate.setDescription(templateDTO.getDescription());
                maximumConsecutiveWorkingDaysWTATemplate.setDaysLimit(templateDTO.getDaysLimit());
                maximumConsecutiveWorkingDaysWTATemplate.setBalanceType(templateDTO.getBalanceType());
                maximumConsecutiveWorkingDaysWTATemplate.setCheckAgainstTimeRules(templateDTO.isCheckAgainstTimeRules());
                oldTemplate = save(maximumConsecutiveWorkingDaysWTATemplate);
                break;

            case TEMPLATE4:
                MinimumRestInConsecutiveDaysWTATemplate minimumRestInConsecutiveDaysWTATemplate = (MinimumRestInConsecutiveDaysWTATemplate) oldTemplate;
                minimumRestInConsecutiveDaysWTATemplate.setDisabled(templateDTO.isActive());
                minimumRestInConsecutiveDaysWTATemplate.setDescription(templateDTO.getDescription());
                minimumRestInConsecutiveDaysWTATemplate.setMinimumRest(templateDTO.getMinimumRest());
                minimumRestInConsecutiveDaysWTATemplate.setDaysWorked(templateDTO.getDaysWorked());

                oldTemplate = save(minimumRestInConsecutiveDaysWTATemplate);
                break;

            case TEMPLATE5:
                MaximumNightShiftLengthWTATemplate maximumNightShiftLengthWTATemplate = (MaximumNightShiftLengthWTATemplate) oldTemplate;
                maximumNightShiftLengthWTATemplate.setDescription(templateDTO.getDescription());
                maximumNightShiftLengthWTATemplate.setTimeLimit(templateDTO.getTimeLimit());
                maximumNightShiftLengthWTATemplate.setBalanceType(templateDTO.getBalanceType());
                maximumNightShiftLengthWTATemplate.setCheckAgainstTimeRules(templateDTO.isCheckAgainstTimeRules());
                maximumNightShiftLengthWTATemplate.setDisabled(templateDTO.isActive());
                oldTemplate = save(maximumNightShiftLengthWTATemplate);
                break;

            case TEMPLATE6:
                MinimumConsecutiveNightsWTATemplate minimumConsecutiveNightsWTATemplate = (MinimumConsecutiveNightsWTATemplate) oldTemplate;
                minimumConsecutiveNightsWTATemplate.setDisabled(templateDTO.isActive());
                minimumConsecutiveNightsWTATemplate.setDescription(templateDTO.getDescription());
                minimumConsecutiveNightsWTATemplate.setDaysLimit(templateDTO.getDaysLimit());
                oldTemplate = save(minimumConsecutiveNightsWTATemplate);
                break;

            case TEMPLATE7:
                MaximumConsecutiveWorkingNightsWTATemplate maximumConsecutiveWorkingNights = (MaximumConsecutiveWorkingNightsWTATemplate) oldTemplate;//oldTemplate;
                maximumConsecutiveWorkingNights.setDisabled(templateDTO.isActive());
                maximumConsecutiveWorkingNights.setDescription(templateDTO.getDescription());
                maximumConsecutiveWorkingNights.setNightsWorked(templateDTO.getNightsWorked());
                maximumConsecutiveWorkingNights.setBalanceType(templateDTO.getBalanceType());
                maximumConsecutiveWorkingNights.setCheckAgainstTimeRules(templateDTO.isCheckAgainstTimeRules());
                oldTemplate = save(maximumConsecutiveWorkingNights);
                break;
            case TEMPLATE8:
                MinimumRestConsecutiveNightsWTATemplate minimumRestConsecutiveNightsWTATemplate = (MinimumRestConsecutiveNightsWTATemplate) oldTemplate;
                minimumRestConsecutiveNightsWTATemplate.setDisabled(templateDTO.isActive());
                minimumRestConsecutiveNightsWTATemplate.setDescription(templateDTO.getDescription());
                minimumRestConsecutiveNightsWTATemplate.setNightsWorked(templateDTO.getNightsWorked());
                minimumRestConsecutiveNightsWTATemplate.setBalanceType(templateDTO.getBalanceType());
                minimumRestConsecutiveNightsWTATemplate.setMinimumRest(templateDTO.getMinimumRest());
                oldTemplate = save(minimumRestConsecutiveNightsWTATemplate);
                break;
            case TEMPLATE9:
                MaximumNumberOfNightsWTATemplate maximumNumberOfNightsWTATemplate = (MaximumNumberOfNightsWTATemplate) oldTemplate;
                maximumNumberOfNightsWTATemplate.setDisabled(templateDTO.isActive());
                maximumNumberOfNightsWTATemplate.setDescription(templateDTO.getDescription());
                maximumNumberOfNightsWTATemplate.setNightsWorked(templateDTO.getNightsWorked());
                maximumNumberOfNightsWTATemplate.setBalanceType(templateDTO.getBalanceType());
                maximumNumberOfNightsWTATemplate.setIntervalLength(templateDTO.getIntervalLength());
                maximumNumberOfNightsWTATemplate.setIntervalUnit(templateDTO.getIntervalUnit());
                maximumNumberOfNightsWTATemplate.setValidationStartDateMillis(templateDTO.getValidationStartDateMillis());
                oldTemplate = save(maximumNumberOfNightsWTATemplate);
                break;
            case TEMPLATE10:
                MaximumDaysOffInPeriodWTATemplate maximumDaysOffInPeriodWTATemplate = (MaximumDaysOffInPeriodWTATemplate) oldTemplate;
                maximumDaysOffInPeriodWTATemplate.setDisabled(templateDTO.isActive());
                maximumDaysOffInPeriodWTATemplate.setDescription(templateDTO.getDescription());
                maximumDaysOffInPeriodWTATemplate.setIntervalLength(templateDTO.getIntervalLength());
                maximumDaysOffInPeriodWTATemplate.setIntervalUnit(templateDTO.getIntervalUnit());
                maximumDaysOffInPeriodWTATemplate.setValidationStartDateMillis(templateDTO.getValidationStartDateMillis());
                maximumDaysOffInPeriodWTATemplate.setBalanceType(templateDTO.getBalanceType());
                maximumDaysOffInPeriodWTATemplate.setDaysLimit(templateDTO.getDaysLimit());
                oldTemplate = save(maximumDaysOffInPeriodWTATemplate);
                break;
            case TEMPLATE11:
                MaximumAverageScheduledTimeWTATemplate maximumAverageScheduledTimeWTATemplate = (MaximumAverageScheduledTimeWTATemplate) oldTemplate;
                maximumAverageScheduledTimeWTATemplate.setDescription(templateDTO.getDescription());
                maximumAverageScheduledTimeWTATemplate.setUseShiftTimes(templateDTO.isUseShiftTimes());
                maximumAverageScheduledTimeWTATemplate.setIntervalLength(templateDTO.getIntervalLength());
                maximumAverageScheduledTimeWTATemplate.setIntervalUnit(templateDTO.getIntervalUnit());
                maximumAverageScheduledTimeWTATemplate.setMaximumAvgTime(templateDTO.getMaximumAvgTime());
                maximumAverageScheduledTimeWTATemplate.setBalanceType(templateDTO.getBalanceType());
                maximumAverageScheduledTimeWTATemplate.setValidationStartDateMillis(templateDTO.getValidationStartDateMillis());
                maximumAverageScheduledTimeWTATemplate.setBalanceAdjustment(templateDTO.isBalanceAdjustment());
                maximumAverageScheduledTimeWTATemplate.setDisabled(templateDTO.isActive());

                oldTemplate = save(maximumAverageScheduledTimeWTATemplate);
                break;
            case TEMPLATE12:
                MaximumVetoPerPeriodWTATemplate maximumVetoPerPeriodWTATemplate = (MaximumVetoPerPeriodWTATemplate) oldTemplate;
                maximumVetoPerPeriodWTATemplate.setDisabled(templateDTO.isActive());
                maximumVetoPerPeriodWTATemplate.setDescription(templateDTO.getDescription());
                maximumVetoPerPeriodWTATemplate.setMaximumVetoPercentage(templateDTO.getMaximumVetoPercentage());
                oldTemplate = save(maximumVetoPerPeriodWTATemplate);
                break;
            case TEMPLATE13:
                NumberOfWeekendShiftInPeriodWTATemplate numberOfWeekendShiftInPeriodWTATemplate = (NumberOfWeekendShiftInPeriodWTATemplate) oldTemplate;
                numberOfWeekendShiftInPeriodWTATemplate.setDisabled(templateDTO.isActive());
                numberOfWeekendShiftInPeriodWTATemplate.setDescription(templateDTO.getDescription());
                numberOfWeekendShiftInPeriodWTATemplate.setNumberShiftsPerPeriod(templateDTO.getNumberShiftsPerPeriod());
                numberOfWeekendShiftInPeriodWTATemplate.setNumberOfWeeks(templateDTO.getNumberOfWeeks());
                numberOfWeekendShiftInPeriodWTATemplate.setFromDayOfWeek(templateDTO.getFromDayOfWeek());
                numberOfWeekendShiftInPeriodWTATemplate.setFromTime(templateDTO.getFromTime());
                numberOfWeekendShiftInPeriodWTATemplate.setToTime(templateDTO.getToTime());
                numberOfWeekendShiftInPeriodWTATemplate.setToDayOfWeek(templateDTO.getToDayOfWeek());
                numberOfWeekendShiftInPeriodWTATemplate.setProportional(templateDTO.getProportional());
                oldTemplate = save(numberOfWeekendShiftInPeriodWTATemplate);
                break;
            case TEMPLATE14:
                CareDayCheckWTATemplate careDayCheckWTATemplate = (CareDayCheckWTATemplate) oldTemplate;
                careDayCheckWTATemplate.setDisabled(templateDTO.isActive());
                careDayCheckWTATemplate.setDescription(templateDTO.getDescription());
                careDayCheckWTATemplate.setIntervalLength(templateDTO.getIntervalLength());
                careDayCheckWTATemplate.setIntervalUnit(templateDTO.getIntervalUnit());
                careDayCheckWTATemplate.setDaysLimit(templateDTO.getDaysLimit());
                careDayCheckWTATemplate.setValidationStartDateMillis(templateDTO.getValidationStartDateMillis());
                oldTemplate = save(careDayCheckWTATemplate);
                break;
            case TEMPLATE15:
                MinimumDailyRestingTimeWTATemplate minimumDailyRestingTimeWTATemplate = (MinimumDailyRestingTimeWTATemplate) oldTemplate;
                minimumDailyRestingTimeWTATemplate.setDisabled(templateDTO.isActive());
                minimumDailyRestingTimeWTATemplate.setId(oldTemplate.getId());
                minimumDailyRestingTimeWTATemplate.setDescription(templateDTO.getDescription());
                minimumDailyRestingTimeWTATemplate.setContinuousDayRestHours(templateDTO.getContinuousDayRestHours());
                oldTemplate = save(minimumDailyRestingTimeWTATemplate);
                break;
            case TEMPLATE16:
                MinimumDurationBetweenShiftWTATemplate minimumDurationBetweenShiftWTATemplate = (MinimumDurationBetweenShiftWTATemplate) oldTemplate;
                minimumDurationBetweenShiftWTATemplate.setDescription(templateDTO.getDescription());
                minimumDurationBetweenShiftWTATemplate.setDisabled(templateDTO.isActive());

                minimumDurationBetweenShiftWTATemplate.setBalanceType(templateDTO.getBalanceType());
                minimumDurationBetweenShiftWTATemplate.setMinimumDurationBetweenShifts(templateDTO.getMinimumDurationBetweenShifts());
                oldTemplate = save(minimumDurationBetweenShiftWTATemplate);
                break;
            case TEMPLATE17:
                MinimumWeeklyRestPeriodWTATemplate minimumWeeklyRestPeriodWTATemplate = (MinimumWeeklyRestPeriodWTATemplate) oldTemplate;
                minimumWeeklyRestPeriodWTATemplate.setDisabled(templateDTO.isActive());
                minimumWeeklyRestPeriodWTATemplate.setDescription(templateDTO.getDescription());
                minimumWeeklyRestPeriodWTATemplate.setContinuousWeekRest(templateDTO.getContinuousWeekRest());
                oldTemplate = save(minimumWeeklyRestPeriodWTATemplate);
                break;
            case TEMPLATE18:
                ShortestAndAverageDailyRestWTATemplate shortestAndAverageDailyRestWTATemplate = (ShortestAndAverageDailyRestWTATemplate) oldTemplate;
                shortestAndAverageDailyRestWTATemplate.setDisabled(templateDTO.isActive());
                shortestAndAverageDailyRestWTATemplate.setDescription(templateDTO.getDescription());
                shortestAndAverageDailyRestWTATemplate.setBalanceType(templateDTO.getBalanceType());
                shortestAndAverageDailyRestWTATemplate.setIntervalLength(templateDTO.getIntervalLength());
                shortestAndAverageDailyRestWTATemplate.setIntervalUnit(templateDTO.getIntervalUnit());
                shortestAndAverageDailyRestWTATemplate.setValidationStartDateMillis(templateDTO.getValidationStartDateMillis());
                shortestAndAverageDailyRestWTATemplate.setContinuousDayRestHours(templateDTO.getContinuousDayRestHours());
                shortestAndAverageDailyRestWTATemplate.setAverageRest(templateDTO.getAverageRest());
                shortestAndAverageDailyRestWTATemplate.setShiftAffiliation(templateDTO.getShiftAffiliation());
                oldTemplate = save(shortestAndAverageDailyRestWTATemplate);
                break;
            case TEMPLATE19:
                MaximumShiftsInIntervalWTATemplate maximumShiftsInIntervalWTATemplate = (MaximumShiftsInIntervalWTATemplate) oldTemplate;
                maximumShiftsInIntervalWTATemplate.setDisabled(templateDTO.isActive());
                maximumShiftsInIntervalWTATemplate.setDescription(templateDTO.getDescription());
                maximumShiftsInIntervalWTATemplate.setBalanceType(templateDTO.getBalanceType());
                maximumShiftsInIntervalWTATemplate.setIntervalLength(templateDTO.getIntervalLength());
                maximumShiftsInIntervalWTATemplate.setIntervalUnit(templateDTO.getIntervalUnit());
                maximumShiftsInIntervalWTATemplate.setValidationStartDateMillis(templateDTO.getValidationStartDateMillis());
                maximumShiftsInIntervalWTATemplate.setShiftsLimit(templateDTO.getShiftsLimit());
                maximumShiftsInIntervalWTATemplate.setOnlyCompositeShifts(templateDTO.isOnlyCompositeShifts());
                oldTemplate = save(maximumShiftsInIntervalWTATemplate);
                break;
            case TEMPLATE20:
                MaximumSeniorDaysInYearWTATemplate maximumSeniorDaysInYearWTATemplate = (MaximumSeniorDaysInYearWTATemplate) oldTemplate;
                maximumSeniorDaysInYearWTATemplate.setDisabled(templateDTO.isActive());
                maximumSeniorDaysInYearWTATemplate.setDescription(templateDTO.getDescription());
                maximumSeniorDaysInYearWTATemplate.setIntervalLength(templateDTO.getIntervalLength());
                maximumSeniorDaysInYearWTATemplate.setIntervalUnit(templateDTO.getIntervalUnit());
                maximumSeniorDaysInYearWTATemplate.setValidationStartDateMillis(templateDTO.getValidationStartDateMillis());
                maximumSeniorDaysInYearWTATemplate.setDaysLimit(templateDTO.getDaysLimit());
                maximumSeniorDaysInYearWTATemplate.setActivityCode(templateDTO.getActivityCode());
                oldTemplate = save(maximumSeniorDaysInYearWTATemplate);
                break;
            default:
                throw new DataNotFoundByIdException("Invalid TEMPLATE");
        }

        oldTemplate.setDisabled(templateDTO.isActive());

        wtaRuleTemplateGraphRepository.deleteCategoryFromTemplate(oldTemplate.getId());

        RuleTemplateCategory templateCategory = null;
        if (StringUtils.isEmpty(templateDTO.getCategory())) {
            templateCategory = ruleTemplateCategoryRepository.findByName(countryId, "NONE", RuleTemplateCategoryType.WTA);
        } else {
            templateCategory = ruleTemplateCategoryRepository.findByName(countryId, templateDTO.getCategory(), RuleTemplateCategoryType.WTA);
        }
        if (!Optional.ofNullable(templateCategory).isPresent())
            throw new InvalidRequestException("Incorrect category " + templateDTO.getCategory());
        List<RuleTemplate> wtaBaseRuleTemplates = templateCategory.getRuleTemplates();
        wtaBaseRuleTemplates.add(oldTemplate);
        templateCategory.setRuleTemplates(wtaBaseRuleTemplates);
        save(templateCategory);
        return wtaRuleTemplateGraphRepository.getRuleTemplateAndCategoryById(oldTemplate.getId());
    }




    public WTARuleTemplateQueryResponse getRuleTemplateById(Long ruleTemplateId) {
        return wtaRuleTemplateGraphRepository.getRuleTemplateAndCategoryById(ruleTemplateId);
    }

}

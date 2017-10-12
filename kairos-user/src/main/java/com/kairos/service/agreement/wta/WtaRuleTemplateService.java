package com.kairos.service.agreement.wta;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.InvalidRequestException;
import com.kairos.persistence.model.user.agreement.wta.templates.*;
import com.kairos.persistence.model.user.country.Country;
import com.kairos.persistence.repository.user.agreement.wta.RuleTemplateCategoryGraphRepository;
import com.kairos.persistence.repository.user.agreement.wta.TemplateCategoryRelationGraphRepository;
import com.kairos.persistence.repository.user.agreement.wta.WTABaseRuleTemplateGraphRepository;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.response.dto.web.WTARuleTemplateDTO;
import com.kairos.response.dto.web.WtaRuleTemplateDTO;
import com.kairos.service.UserBaseService;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.*;

import static com.kairos.constants.AppConstants.*;

/**
 * Created by pawanmandhan on 5/8/17.
 */

@Service
public class WtaRuleTemplateService extends UserBaseService {


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

        List<WTABaseRuleTemplate> baseRuleTemplates = new ArrayList<WTABaseRuleTemplate>();

        Country country = countryGraphRepository.findOne(countryId);

        if (country == null) {
            throw new DataNotFoundByIdException("Invalid organisation");
        }


        RuleTemplateCategory ruleTemplateCategory = new RuleTemplateCategory("NONE");
        ruleTemplateCategoryService.createRuleTemplate(countryId, ruleTemplateCategory);
        ruleTemplateCategory = ruleTemplateCategoryRepository.findByName(countryId, "NONE");
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

        ruleTemplateCategory.setWtaBaseRuleTemplates(baseRuleTemplates);
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

        List<RuleTemplateCategory> categoryList =  ruleTemplateCategoryRepository.getAllRulesOfCountry(countryId);

        if (categoryList == null) {
            throw new DataNotFoundByIdException("Category List is null");
        }

        List<WTABaseRuleTemplate> templateList = country.getWTABaseRuleTemplate();
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

        WTABaseRuleTemplate oldTemplate = getTemplateByType(countryId, templateType);
        if (!Optional.ofNullable(oldTemplate).isPresent()) {
            throw new DataNotFoundByIdException("Invalid TemplateType "+ templateType);
        }
        switch (templateType) {

            case TEMPLATE1:
                MaximumShiftLengthWTATemplate maximumShiftLengthWTATemplate = (MaximumShiftLengthWTATemplate) getTemplateByType(countryId, templateType);

                maximumShiftLengthWTATemplate.setDescription(templateDTO.getDescription());
                maximumShiftLengthWTATemplate.setTimeLimit(templateDTO.getTimeLimit());
                maximumShiftLengthWTATemplate.setBalanceType(templateDTO.getBalanceType());
                maximumShiftLengthWTATemplate.setCheckAgainstTimeRules(templateDTO.isCheckAgainstTimeRules());
                oldTemplate = save(maximumShiftLengthWTATemplate);

                break;

            case TEMPLATE2:
                MinimumShiftLengthWTATemplate minimumShiftLengthWTATemplate = (MinimumShiftLengthWTATemplate) getTemplateByType(countryId, templateType);

                minimumShiftLengthWTATemplate.setDescription(templateDTO.getDescription());
                minimumShiftLengthWTATemplate.setTimeLimit(templateDTO.getTimeLimit());
                minimumShiftLengthWTATemplate.setBalanceType(templateDTO.getBalanceType());
                minimumShiftLengthWTATemplate.setCheckAgainstTimeRules(templateDTO.isCheckAgainstTimeRules());
                oldTemplate = save(minimumShiftLengthWTATemplate);
                break;

            case TEMPLATE3:
                MaximumConsecutiveWorkingDaysWTATemplate maximumConsecutiveWorkingDaysWTATemplate = (MaximumConsecutiveWorkingDaysWTATemplate) getTemplateByType(countryId, templateType);

                maximumConsecutiveWorkingDaysWTATemplate.setDescription(templateDTO.getDescription());
                maximumConsecutiveWorkingDaysWTATemplate.setDaysLimit(templateDTO.getDaysLimit());
                maximumConsecutiveWorkingDaysWTATemplate.setBalanceType(templateDTO.getBalanceType());
                maximumConsecutiveWorkingDaysWTATemplate.setCheckAgainstTimeRules(templateDTO.isCheckAgainstTimeRules());
                oldTemplate = save(maximumConsecutiveWorkingDaysWTATemplate);
                break;

            case TEMPLATE4:
                MinimumRestInConsecutiveDaysWTATemplate minimumRestInConsecutiveDaysWTATemplate = (MinimumRestInConsecutiveDaysWTATemplate) getTemplateByType(countryId, templateType);

                minimumRestInConsecutiveDaysWTATemplate.setDescription(templateDTO.getDescription());
                minimumRestInConsecutiveDaysWTATemplate.setMinimumRest(templateDTO.getMinimumRest());
                minimumRestInConsecutiveDaysWTATemplate.setDaysWorked(templateDTO.getDaysWorked());
                oldTemplate = save(minimumRestInConsecutiveDaysWTATemplate);
                break;

            case TEMPLATE5:
                MaximumNightShiftLengthWTATemplate maximumNightShiftLengthWTATemplate = (MaximumNightShiftLengthWTATemplate) getTemplateByType(countryId, templateType);

                maximumNightShiftLengthWTATemplate.setDescription(templateDTO.getDescription());
                maximumNightShiftLengthWTATemplate.setTimeLimit(templateDTO.getTimeLimit());
                maximumNightShiftLengthWTATemplate.setBalanceType(templateDTO.getBalanceType());
                maximumNightShiftLengthWTATemplate.setCheckAgainstTimeRules(templateDTO.isCheckAgainstTimeRules());
                oldTemplate = save(maximumNightShiftLengthWTATemplate);
                break;

            case TEMPLATE6:
                MinimumConsecutiveNightsWTATemplate minimumConsecutiveNightsWTATemplate = (MinimumConsecutiveNightsWTATemplate) getTemplateByType(countryId, templateType);

                minimumConsecutiveNightsWTATemplate.setDescription(templateDTO.getDescription());
                minimumConsecutiveNightsWTATemplate.setDaysLimit(templateDTO.getDaysLimit());
                oldTemplate = save(minimumConsecutiveNightsWTATemplate);
                break;

            case TEMPLATE7:
                MaximumConsecutiveWorkingNightsWTATemplate maximumConsecutiveWorkingNights = (MaximumConsecutiveWorkingNightsWTATemplate) getTemplateByType(countryId, templateType);

                maximumConsecutiveWorkingNights.setDescription(templateDTO.getDescription());
                maximumConsecutiveWorkingNights.setNightsWorked(templateDTO.getNightsWorked());
                maximumConsecutiveWorkingNights.setBalanceType(templateDTO.getBalanceType());
                maximumConsecutiveWorkingNights.setCheckAgainstTimeRules(templateDTO.isCheckAgainstTimeRules());
                oldTemplate = save(maximumConsecutiveWorkingNights);
                break;
            case TEMPLATE8:
                MinimumRestConsecutiveNightsWTATemplate minimumRestConsecutiveNightsWTATemplate = (MinimumRestConsecutiveNightsWTATemplate) getTemplateByType(countryId, templateType);

                minimumRestConsecutiveNightsWTATemplate.setDescription(templateDTO.getDescription());
                minimumRestConsecutiveNightsWTATemplate.setNightsWorked(templateDTO.getNightsWorked());
                minimumRestConsecutiveNightsWTATemplate.setBalanceType(templateDTO.getBalanceType());
                minimumRestConsecutiveNightsWTATemplate.setMinimumRest(templateDTO.getMinimumRest());
                oldTemplate = save(minimumRestConsecutiveNightsWTATemplate);
                break;
            case TEMPLATE9:
                MaximumNumberOfNightsWTATemplate maximumNumberOfNightsWTATemplate = (MaximumNumberOfNightsWTATemplate) getTemplateByType(countryId, templateType);

                maximumNumberOfNightsWTATemplate.setDescription(templateDTO.getDescription());
                maximumNumberOfNightsWTATemplate.setNightsWorked(templateDTO.getNightsWorked());
                maximumNumberOfNightsWTATemplate.setBalanceType(templateDTO.getBalanceType());
                maximumNumberOfNightsWTATemplate.setIntervalLength(templateDTO.getIntervalLength());
                maximumNumberOfNightsWTATemplate.setIntervalUnit(templateDTO.getIntervalUnit());
                maximumNumberOfNightsWTATemplate.setValidationStartDateMillis(templateDTO.getValidationStartDateMillis());
                oldTemplate = save(maximumNumberOfNightsWTATemplate);
                break;
            case TEMPLATE10:
                MaximumDaysOffInPeriodWTATemplate maximumDaysOffInPeriodWTATemplate = (MaximumDaysOffInPeriodWTATemplate) getTemplateByType(countryId, templateType);

                maximumDaysOffInPeriodWTATemplate.setDescription(templateDTO.getDescription());
                maximumDaysOffInPeriodWTATemplate.setIntervalLength(templateDTO.getIntervalLength());
                maximumDaysOffInPeriodWTATemplate.setIntervalUnit(templateDTO.getIntervalUnit());
                maximumDaysOffInPeriodWTATemplate.setValidationStartDateMillis(templateDTO.getValidationStartDateMillis());
                maximumDaysOffInPeriodWTATemplate.setBalanceType(templateDTO.getBalanceType());
                maximumDaysOffInPeriodWTATemplate.setDaysLimit(templateDTO.getDaysLimit());
                oldTemplate = save(maximumDaysOffInPeriodWTATemplate);
                break;
            case TEMPLATE11:
                MaximumAverageScheduledTimeWTATemplate maximumAverageScheduledTimeWTATemplate = (MaximumAverageScheduledTimeWTATemplate) getTemplateByType(countryId, templateType);
                maximumAverageScheduledTimeWTATemplate.setDescription(templateDTO.getDescription());
                maximumAverageScheduledTimeWTATemplate.setUseShiftTimes(templateDTO.isUseShiftTimes());
                maximumAverageScheduledTimeWTATemplate.setIntervalLength(templateDTO.getIntervalLength());
                maximumAverageScheduledTimeWTATemplate.setIntervalUnit(templateDTO.getIntervalUnit());
                maximumAverageScheduledTimeWTATemplate.setMaximumAvgTime(templateDTO.getMaximumAvgTime());
                maximumAverageScheduledTimeWTATemplate.setBalanceType(templateDTO.getBalanceType());
                maximumAverageScheduledTimeWTATemplate.setValidationStartDateMillis(templateDTO.getValidationStartDateMillis());
                maximumAverageScheduledTimeWTATemplate.setBalanceAdjustment(templateDTO.isBalanceAdjustment());
                oldTemplate = save(maximumAverageScheduledTimeWTATemplate);
                break;
            case TEMPLATE12:
                MaximumVetoPerPeriodWTATemplate maximumVetoPerPeriodWTATemplate = (MaximumVetoPerPeriodWTATemplate) getTemplateByType(countryId, templateType);

                maximumVetoPerPeriodWTATemplate.setDescription(templateDTO.getDescription());
                maximumVetoPerPeriodWTATemplate.setMaximumVetoPercentage(templateDTO.getMaximumVetoPercentage());
                oldTemplate = save(maximumVetoPerPeriodWTATemplate);
                break;
            case TEMPLATE13:
                NumberOfWeekendShiftInPeriodWTATemplate numberOfWeekendShiftInPeriodWTATemplate = (NumberOfWeekendShiftInPeriodWTATemplate) getTemplateByType(countryId, templateType);

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
                CareDayCheckWTATemplate careDayCheckWTATemplate = (CareDayCheckWTATemplate) getTemplateByType(countryId, templateType);

                careDayCheckWTATemplate.setDescription(templateDTO.getDescription());
                careDayCheckWTATemplate.setIntervalLength(templateDTO.getIntervalLength());
                careDayCheckWTATemplate.setIntervalUnit(templateDTO.getIntervalUnit());
                careDayCheckWTATemplate.setDaysLimit(templateDTO.getDaysLimit());
                careDayCheckWTATemplate.setValidationStartDateMillis(templateDTO.getValidationStartDateMillis());
                oldTemplate = save(careDayCheckWTATemplate);
                break;
            case TEMPLATE15:
                MinimumDailyRestingTimeWTATemplate minimumDailyRestingTimeWTATemplate = (MinimumDailyRestingTimeWTATemplate) getTemplateByType(countryId, templateType);

                minimumDailyRestingTimeWTATemplate.setDescription(templateDTO.getDescription());
                minimumDailyRestingTimeWTATemplate.setContinuousDayRestHours(templateDTO.getContinuousDayRestHours());
                oldTemplate = save(minimumDailyRestingTimeWTATemplate);
                break;
            case TEMPLATE16:
                MinimumDurationBetweenShiftWTATemplate minimumDurationBetweenShiftWTATemplate = (MinimumDurationBetweenShiftWTATemplate) getTemplateByType(countryId, templateType);

                minimumDurationBetweenShiftWTATemplate.setDescription(templateDTO.getDescription());
                minimumDurationBetweenShiftWTATemplate.setBalanceType(templateDTO.getBalanceType());
                minimumDurationBetweenShiftWTATemplate.setMinimumDurationBetweenShifts(templateDTO.getMinimumDurationBetweenShifts());
                oldTemplate = save(minimumDurationBetweenShiftWTATemplate);
                break;
            case TEMPLATE17:
                MinimumWeeklyRestPeriodWTATemplate minimumWeeklyRestPeriodWTATemplate = (MinimumWeeklyRestPeriodWTATemplate) getTemplateByType(countryId, templateType);

                minimumWeeklyRestPeriodWTATemplate.setDescription(templateDTO.getDescription());
                minimumWeeklyRestPeriodWTATemplate.setContinuousWeekRest(templateDTO.getContinuousWeekRest());
                oldTemplate = save(minimumWeeklyRestPeriodWTATemplate);
                break;
            case TEMPLATE18:
                ShortestAndAverageDailyRestWTATemplate shortestAndAverageDailyRestWTATemplate = (ShortestAndAverageDailyRestWTATemplate) getTemplateByType(countryId, templateType);

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
                MaximumShiftsInIntervalWTATemplate maximumShiftsInIntervalWTATemplate = (MaximumShiftsInIntervalWTATemplate) getTemplateByType(countryId, templateType);

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
                MaximumSeniorDaysInYearWTATemplate template20 = (MaximumSeniorDaysInYearWTATemplate) getTemplateByType(countryId, templateType);

                template20.setDescription(templateDTO.getDescription());
                template20.setIntervalLength(templateDTO.getIntervalLength());
                template20.setIntervalUnit(templateDTO.getIntervalUnit());
                template20.setValidationStartDateMillis(templateDTO.getValidationStartDateMillis());
                template20.setDaysLimit(templateDTO.getDaysLimit());
                template20.setActivityCode(templateDTO.getActivityCode());

                oldTemplate = save(template20);
                break;
            default:
                throw new DataNotFoundByIdException("Invalid TEMPLATE");
        }

        oldTemplate.setActive(templateDTO.isActive());

        wtaRuleTemplateGraphRepository.deleteCategoryFromTemplate(oldTemplate.getId());

        RuleTemplateCategory templateCategory =null;
        if(templateDTO.getCategory()==""){
            templateCategory =   ruleTemplateCategoryRepository.findByName(countryId, "NONE");
        }else {
            templateCategory =   ruleTemplateCategoryRepository.findByName(countryId, templateDTO.getCategory());
        }
        if(!Optional.ofNullable(templateCategory).isPresent())
            throw new InvalidRequestException("Incorrect category "+templateDTO.getCategory());
        List<WTABaseRuleTemplate> wtaBaseRuleTemplates = templateCategory.getWtaBaseRuleTemplates();
        wtaBaseRuleTemplates.add(oldTemplate);
        templateCategory.setWtaBaseRuleTemplates(wtaBaseRuleTemplates);
        save(templateCategory);
        return wtaRuleTemplateGraphRepository.getRuleTemplateAndCategoryById(oldTemplate.getId());
    }


    private WTABaseRuleTemplate getTemplateByType(Long countryId, String templateType) {
        return countryGraphRepository.getTemplateByType(countryId, templateType);
    }

    public Map<String, Object> updateRuleTemplateCategory(WtaRuleTemplateDTO wtaRuleTemplateDTO, long countryId) {


        if(wtaRuleTemplateDTO.getCategoryName()==null || wtaRuleTemplateDTO.getCategoryName().isEmpty()){
            throw new InvalidRequestException("category name cant be null or empty!!");
        }

        wtaRuleTemplateGraphRepository.deleteOldCategories(wtaRuleTemplateDTO.getRuleTemplateIds());

        List<WTABaseRuleTemplate> wtaBaseRuleTemplates = wtaRuleTemplateGraphRepository.getWtaBaseRuleTemplateByIds(wtaRuleTemplateDTO.getRuleTemplateIds());

        RuleTemplateCategory ruleTemplateCategory = ruleTemplateCategoryRepository.findByName(countryId, wtaRuleTemplateDTO.getCategoryName());
        Map<String, Object> response = new HashedMap();
        if (ruleTemplateCategory == null) {
            ruleTemplateCategory = new RuleTemplateCategory(wtaRuleTemplateDTO.getCategoryName());
            Country country = countryGraphRepository.findOne(countryId);
            if (country == null) {
                throw new InternalError("country is null");
            }
            List<RuleTemplateCategory> ruleTemplateCategories = country.getRuleTemplateCategories();
            ruleTemplateCategories.add(ruleTemplateCategory);
            country.setRuleTemplateCategories(ruleTemplateCategories);
            countryGraphRepository.save(country);
            response.put("category", ruleTemplateCategory);
        }
        List<WTABaseRuleTemplate> baseRuleTemplates = ruleTemplateCategory.getWtaBaseRuleTemplates();
        baseRuleTemplates.addAll(wtaBaseRuleTemplates);
        ruleTemplateCategory.setWtaBaseRuleTemplates(baseRuleTemplates);
        save(ruleTemplateCategory);
        response.put("templateList", getJsonOfUpdatedTemplates(wtaBaseRuleTemplates, ruleTemplateCategory));
        return response;
    }

    private List<WTABaseRuleTemplateDTO> getJsonOfUpdatedTemplates(List<WTABaseRuleTemplate> wtaBaseRuleTemplates, RuleTemplateCategory ruleTemplateCategory) {

        ObjectMapper objectMapper = new ObjectMapper();
        List<WTABaseRuleTemplateDTO> wtaBaseRuleTemplateDTOS = new ArrayList<>(wtaBaseRuleTemplates.size());
        wtaBaseRuleTemplates.forEach(wtaBaseRuleTemplate -> {
            WTABaseRuleTemplateDTO wtaBaseRuleTemplateDTO = objectMapper.convertValue(wtaBaseRuleTemplate, WTABaseRuleTemplateDTO.class);
            wtaBaseRuleTemplateDTO.setRuleTemplateCategory(ruleTemplateCategory);
            wtaBaseRuleTemplateDTOS.add(wtaBaseRuleTemplateDTO);
        });

        return wtaBaseRuleTemplateDTOS;
    }
    public  WTARuleTemplateQueryResponse getRuleTemplateById(Long ruleTemplateId){
        return wtaRuleTemplateGraphRepository.getRuleTemplateAndCategoryById(ruleTemplateId);
    }

}

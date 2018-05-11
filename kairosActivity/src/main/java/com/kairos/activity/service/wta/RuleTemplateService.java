package com.kairos.activity.service.wta;


import com.kairos.activity.client.CountryRestClient;
import com.kairos.activity.client.OrganizationRestClient;
import com.kairos.activity.client.dto.organization.OrganizationDTO;
import com.kairos.activity.custom_exception.DataNotFoundByIdException;
import com.kairos.activity.custom_exception.DuplicateDataException;
import com.kairos.activity.persistence.enums.PartOfDay;
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
import com.kairos.activity.util.ObjectMapperUtils;
import com.kairos.activity.util.userContext.CurrentUserDetails;
import com.kairos.activity.util.userContext.UserContext;

import com.kairos.persistence.model.enums.TimeBankTypeEnum;
import com.kairos.response.dto.web.CountryDTO;
import com.kairos.response.dto.web.aggrements.RuleTemplateWrapper;
import com.kairos.response.dto.web.enums.RuleTemplateCategoryType;
import com.kairos.response.dto.web.wta.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;


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
    @Inject private OrganizationRestClient organizationRestClient;
    private final Logger logger = LoggerFactory.getLogger(RuleTemplateService.class);

    public boolean createRuleTemplate(long countryId) {
        CountryDTO countryDTO = countryRestClient.getCountryById(countryId);

        if (countryDTO == null) {
            throw new DataNotFoundByIdException("Invalid Country");
        }


        List<WTABaseRuleTemplate> wtaBaseRuleTemplates = wtaBaseRuleTemplateMongoRepository.getWTABaseRuleTemplateByCountryId(countryId);
        RuleTemplateCategory ruleTemplateCategory = ruleTemplateCategoryMongoRepository.findByName(countryId, "NONE", RuleTemplateCategoryType.WTA);
        if (!Optional.ofNullable(ruleTemplateCategory).isPresent()) {
            ruleTemplateCategory = new RuleTemplateCategory("NONE","None", RuleTemplateCategoryType.WTA);
            ruleTemplateCategory.setCountryId(countryDTO.getId());
            save(ruleTemplateCategory);
        }
        if (Optional.ofNullable(wtaBaseRuleTemplates).isPresent() && !wtaBaseRuleTemplates.isEmpty()) {
            throw new DataNotFoundByIdException("WTA Rule Template already exists");
        }

        String week = "WEEK";
        String TUESDAY = "TUESDAY";
        long timeInMins = 10;
        long daysCount = 10;
        LocalDate localDate = LocalDate.now();
        List<WTABaseRuleTemplate> wtaBaseRuleTemplates1 = new ArrayList<>();
        AgeRange range=new AgeRange(0,0,0);

        List<PhaseTemplateValue> phaseTemplateValues = new ArrayList<>();
        phaseTemplateValues.add(new PhaseTemplateValue(1,"REQUEST",(short) 0,(short)0,true,0,false));
        phaseTemplateValues.add(new PhaseTemplateValue(2,"PUZZLE",(short) 0,(short)0,true,0,false));
        phaseTemplateValues.add(new PhaseTemplateValue(3,"DRAFT",(short) 0,(short)0,true,0,false));
        phaseTemplateValues.add(new PhaseTemplateValue(4,"CONSTRUCTION",(short) 0,(short)0,true,0,false));

        ShiftLengthWTATemplate shiftLengthWTATemplate = new ShiftLengthWTATemplate("Maximum night shift’s length",true,"Maximum night shift’s length",400,true);
        shiftLengthWTATemplate.setCountryId(countryDTO.getId());
        shiftLengthWTATemplate.setPhaseTemplateValues(phaseTemplateValues);
        shiftLengthWTATemplate.setRuleTemplateCategoryId(ruleTemplateCategory.getId());
        wtaBaseRuleTemplates1.add(shiftLengthWTATemplate);

        ConsecutiveWorkWTATemplate consecutiveWorking = new ConsecutiveWorkWTATemplate("Maximum number of consecutive days",true,"Maximum number of consecutive days",true,daysCount);
        consecutiveWorking.setCountryId(countryDTO.getId());
        consecutiveWorking.setPhaseTemplateValues(phaseTemplateValues);
        consecutiveWorking.setRuleTemplateCategoryId(ruleTemplateCategory.getId());
        wtaBaseRuleTemplates1.add(consecutiveWorking);

        ConsecutiveRestPartOfDayWTATemplate restInConsecutiveDays = new ConsecutiveRestPartOfDayWTATemplate("Minimum rest after consecutive days worked",false,"Minimum rest after consecutive days worked",timeInMins, daysCount);
        restInConsecutiveDays.setCountryId(countryDTO.getId());
        restInConsecutiveDays.setPhaseTemplateValues(phaseTemplateValues);
        restInConsecutiveDays.setRuleTemplateCategoryId(ruleTemplateCategory.getId());
        wtaBaseRuleTemplates1.add(restInConsecutiveDays);

        NumberOfPartOfDayShiftsWTATemplate numberOfPartOfDayShiftsWTATemplate = new NumberOfPartOfDayShiftsWTATemplate("Maximum number of shifts per interval",false,"Maximum number of shifts per interval",daysCount);
        //numberOfPartOfDayShiftsWTATemplate.setValidationStartDate(dateInMillis);
        numberOfPartOfDayShiftsWTATemplate.setIntervalLength(1);
        numberOfPartOfDayShiftsWTATemplate.setIntervalUnit(week);
        numberOfPartOfDayShiftsWTATemplate.setCountryId(countryDTO.getId());
        numberOfPartOfDayShiftsWTATemplate.setPhaseTemplateValues(phaseTemplateValues);
        numberOfPartOfDayShiftsWTATemplate.setRuleTemplateCategoryId(ruleTemplateCategory.getId());
        wtaBaseRuleTemplates1.add(numberOfPartOfDayShiftsWTATemplate);

        DaysOffInPeriodWTATemplate daysOffInPeriodWTATemplate = new DaysOffInPeriodWTATemplate("Minimum number of days off per period",false,"Minimum number of days off per period",12, localDate, 12, week);
        daysOffInPeriodWTATemplate.setCountryId(countryDTO.getId());
        daysOffInPeriodWTATemplate.setPhaseTemplateValues(phaseTemplateValues);
        daysOffInPeriodWTATemplate.setRuleTemplateCategoryId(ruleTemplateCategory.getId());
        wtaBaseRuleTemplates1.add(daysOffInPeriodWTATemplate);

        AverageScheduledTimeWTATemplate averageScheduledTimeWTATemplate = new AverageScheduledTimeWTATemplate("Maximum average duration per week in an interval",false,"Maximum average duration per week in an interval",1, localDate, true, true, timeInMins, week);
        averageScheduledTimeWTATemplate.setCountryId(countryDTO.getId());
        averageScheduledTimeWTATemplate.setPhaseTemplateValues(phaseTemplateValues);
        averageScheduledTimeWTATemplate.setRuleTemplateCategoryId(ruleTemplateCategory.getId());
        wtaBaseRuleTemplates1.add(averageScheduledTimeWTATemplate);

        VetoPerPeriodWTATemplate vetoPerPeriodWTATemplate = new VetoPerPeriodWTATemplate("Maximum veto per period",false,"Maximum veto per period",2.0);
        vetoPerPeriodWTATemplate.setCountryId(countryDTO.getId());
        //vetoPerPeriodWTATemplate.setPhaseTemplateValues(phaseTemplateValues);
        vetoPerPeriodWTATemplate.setRuleTemplateCategoryId(ruleTemplateCategory.getId());
        wtaBaseRuleTemplates1.add(vetoPerPeriodWTATemplate);

        NumberOfWeekendShiftsInPeriodWTATemplate numberofWeekendShiftsInPeriodWTATemplate = new NumberOfWeekendShiftsInPeriodWTATemplate("Weekend off Distribution",false,"Weekend off Distribution",12, 1, TUESDAY, 2, true, TUESDAY, 1);
        numberofWeekendShiftsInPeriodWTATemplate.setCountryId(countryDTO.getId());
        numberofWeekendShiftsInPeriodWTATemplate.setPhaseTemplateValues(phaseTemplateValues);
        numberofWeekendShiftsInPeriodWTATemplate.setRuleTemplateCategoryId(ruleTemplateCategory.getId());
        wtaBaseRuleTemplates1.add(numberofWeekendShiftsInPeriodWTATemplate);

        /*ChildCareDaysCheckWTATemplate careDayCheckWTATemplate = new ChildCareDaysCheckWTATemplate("Care days check",true,"Care days check",2, dateInMillis, MONTHS, 1);
        careDayCheckWTATemplate.setCountryId(countryDTO.getId());
        careDayCheckWTATemplate.setPhaseTemplateValues(phaseTemplateValues);
        careDayCheckWTATemplate.setRuleTemplateCategoryId(ruleTemplateCategory.getId());
        wtaBaseRuleTemplates1.add(careDayCheckWTATemplate);*/

        DailyRestingTimeWTATemplate dailyRestingTimeWTATemplate = new DailyRestingTimeWTATemplate("Minimum resting hours daily",false,"Minimum resting hours daily",timeInMins);
        dailyRestingTimeWTATemplate.setCountryId(countryDTO.getId());
        dailyRestingTimeWTATemplate.setPhaseTemplateValues(phaseTemplateValues);
        dailyRestingTimeWTATemplate.setRuleTemplateCategoryId(ruleTemplateCategory.getId());
        wtaBaseRuleTemplates1.add(dailyRestingTimeWTATemplate);


        DurationBetweenShiftsWTATemplate durationBetweenShiftsWTATemplate = new DurationBetweenShiftsWTATemplate("Minimum duration between shifts",false,"Minimum duration between shifts",timeInMins);
        durationBetweenShiftsWTATemplate.setCountryId(countryDTO.getId());
        durationBetweenShiftsWTATemplate.setPhaseTemplateValues(phaseTemplateValues);
        durationBetweenShiftsWTATemplate.setRuleTemplateCategoryId(ruleTemplateCategory.getId());
        wtaBaseRuleTemplates1.add(durationBetweenShiftsWTATemplate);


        WeeklyRestPeriodWTATemplate weeklyRestPeriodWTATemplate = new WeeklyRestPeriodWTATemplate("Minimum rest period in an interval",false,"Minimum rest period in an interval",timeInMins);
        weeklyRestPeriodWTATemplate.setCountryId(countryDTO.getId());
        weeklyRestPeriodWTATemplate.setPhaseTemplateValues(phaseTemplateValues);
        weeklyRestPeriodWTATemplate.setRuleTemplateCategoryId(ruleTemplateCategory.getId());
        wtaBaseRuleTemplates1.add(weeklyRestPeriodWTATemplate);

        ShortestAndAverageDailyRestWTATemplate shortestAndAverageDailyRestWTATemplate = new ShortestAndAverageDailyRestWTATemplate("Shortest and Average daily Rest",false,"Shortest and Average daily Rest",1, week, localDate, timeInMins, timeInMins, "");
        shortestAndAverageDailyRestWTATemplate.setCountryId(countryDTO.getId());
        shortestAndAverageDailyRestWTATemplate.setPhaseTemplateValues(phaseTemplateValues);
        shortestAndAverageDailyRestWTATemplate.setRuleTemplateCategoryId(ruleTemplateCategory.getId());
        wtaBaseRuleTemplates1.add(shortestAndAverageDailyRestWTATemplate);

        ShiftsInIntervalWTATemplate maximumShiftsInIntervalWTATemplate = new ShiftsInIntervalWTATemplate("Shifts In Interval",false,"Shifts In Interval",1, week, localDate, 1, true);
        maximumShiftsInIntervalWTATemplate.setCountryId(countryDTO.getId());
        maximumShiftsInIntervalWTATemplate.setPhaseTemplateValues(phaseTemplateValues);
        maximumShiftsInIntervalWTATemplate.setRuleTemplateCategoryId(ruleTemplateCategory.getId());
        wtaBaseRuleTemplates1.add(maximumShiftsInIntervalWTATemplate);

        /*SeniorDaysInYearWTATemplate seniorDaysInYearWTATemplate = new SeniorDaysInYearWTATemplate("Maximum senior days per year",true,"Maximum senior days per year",1, "NA", dateInMillis, 1, "");
        seniorDaysInYearWTATemplate.setCountryId(countryDTO.getId());
        seniorDaysInYearWTATemplate.setPhaseTemplateValues(phaseTemplateValues);
        seniorDaysInYearWTATemplate.setRuleTemplateCategoryId(ruleTemplateCategory.getId());
        wtaBaseRuleTemplates1.add(seniorDaysInYearWTATemplate);*/

        TimeBankWTATemplate timeBankWTATemplate = new TimeBankWTATemplate("Maximum Time Bank",false, "Maximum Time Bank",TimeBankTypeEnum.HOURLY,45, false, false);
        timeBankWTATemplate.setCountryId(countryDTO.getId());
        timeBankWTATemplate.setPhaseTemplateValues(phaseTemplateValues);
        timeBankWTATemplate.setRuleTemplateCategoryId(ruleTemplateCategory.getId());
        wtaBaseRuleTemplates1.add(timeBankWTATemplate);

        SeniorDaysPerYearWTATemplate seniorDaysPerYearWTATemplate =new SeniorDaysPerYearWTATemplate("Senior Days per Year",true,false,"Senior Days per Year",Arrays.asList(range),new ArrayList<>(),localDate,12L);
        seniorDaysPerYearWTATemplate.setCountryId(countryDTO.getId());
        seniorDaysPerYearWTATemplate.setPhaseTemplateValues(phaseTemplateValues);
        seniorDaysPerYearWTATemplate.setRuleTemplateCategoryId(ruleTemplateCategory.getId());
        wtaBaseRuleTemplates1.add(seniorDaysPerYearWTATemplate);

        ChildCareDaysCheckWTATemplate careDaysCheck=new ChildCareDaysCheckWTATemplate("Child Care Days Check",false,"Child Care Days Check",Arrays.asList(range),new ArrayList<>(),5,localDate,12);
        careDaysCheck.setCountryId(countryDTO.getId());
        //careDaysCheck.setPhaseTemplateValues(phaseTemplateValues);
        careDaysCheck.setRuleTemplateCategoryId(ruleTemplateCategory.getId());
        wtaBaseRuleTemplates1.add(careDaysCheck);

        DaysOffAfterASeriesWTATemplate daysOffAfterASeriesWTATemplate=new DaysOffAfterASeriesWTATemplate("Days Off After a Series",false,"Days Off After a Series",1,week,1);
        dailyRestingTimeWTATemplate.setCountryId(countryDTO.getId());
        dailyRestingTimeWTATemplate.setRuleTemplateCategoryId(ruleTemplateCategory.getId());
        wtaBaseRuleTemplates1.add(daysOffAfterASeriesWTATemplate);

        NoOfSequenceShiftWTATemplate noOfSequenceShiftWTATemplate=new NoOfSequenceShiftWTATemplate("No Of Sequence Shift",false,"No OF Sequence Shift",1, PartOfDay.DAY,PartOfDay.NIGHT);
        noOfSequenceShiftWTATemplate.setCountryId(countryDTO.getId());
        noOfSequenceShiftWTATemplate.setRuleTemplateCategoryId(ruleTemplateCategory.getId());
        wtaBaseRuleTemplates1.add(noOfSequenceShiftWTATemplate);

        EmployeesWithIncreasedRiskWTATemplate employeesWithIncreasedRiskWTATemplate=new EmployeesWithIncreasedRiskWTATemplate("Employees with Increased Risk",false,"Employees with increased risk",18,62,false);
        employeesWithIncreasedRiskWTATemplate.setCountryId(countryDTO.getId());
        employeesWithIncreasedRiskWTATemplate.setRuleTemplateCategoryId(ruleTemplateCategory.getId());
        wtaBaseRuleTemplates1.add(employeesWithIncreasedRiskWTATemplate);

        BreaksInShiftWTATemplate breaksInShiftWTATemplate = new BreaksInShiftWTATemplate("Break In Shift",false,"Break In Shift",Arrays.asList(new BreakTemplateValue()));
        breaksInShiftWTATemplate.setCountryId(countryDTO.getId());
        breaksInShiftWTATemplate.setPhaseTemplateValues(phaseTemplateValues);
        breaksInShiftWTATemplate.setRuleTemplateCategoryId(ruleTemplateCategory.getId());
        wtaBaseRuleTemplates1.add(breaksInShiftWTATemplate);
        save(wtaBaseRuleTemplates1);


        return true;
    }

    public RuleTemplateWrapper getRuleTemplate(long countryId) {
        CountryDTO country = countryRestClient.getCountryById(countryId);
        if (country == null) {
            throw new DataNotFoundByIdException("Invalid Country");
        }

        List<RuleTemplateCategoryTagDTO> categoryList = ruleTemplateCategoryMongoRepository.getAllRulesOfCountry(countryId);

        if (categoryList == null) {
            throw new DataNotFoundByIdException("Category List is null");
        }

        List<WTABaseRuleTemplate> templateList = wtaBaseRuleTemplateMongoRepository.getWTABaseRuleTemplateByCountryId(countryId);
        if (templateList == null) {
            throw new DataNotFoundByIdException("Template List is null");
        }

        //
        List<WTABaseRuleTemplateDTO> wtaBaseRuleTemplateDTOS = WTABuilderService.copyRuleTemplatesToDTO(templateList);
        assignCategoryToRuleTemplate(categoryList,wtaBaseRuleTemplateDTOS);
        RuleTemplateWrapper wrapper = new RuleTemplateWrapper();
        wrapper.setCategoryList(categoryList);
        wrapper.setTemplateList(wtaBaseRuleTemplateDTOS);
        return wrapper;
    }

    public RuleTemplateWrapper getRulesTemplateCategoryByUnit(Long unitId) {
        OrganizationDTO organization = organizationRestClient.getOrganizationWithCountryId(unitId);
        if (!Optional.ofNullable(organization).isPresent()) {
            throw new DataNotFoundByIdException("Organization does not exist");
        }
        //List<WTAResponseDTO> wtaResponseDTOS = workingTimeAgreementMongoRepository.getWtaByOrganization(organization.getId());
        List<RuleTemplateCategoryTagDTO> categoryList = ruleTemplateCategoryMongoRepository.getRuleTemplateCategoryByUnitId(unitId);
        List<WTABaseRuleTemplate> templateList = wtaBaseRuleTemplateMongoRepository.getWTABaseRuleTemplateByCountryId(organization.getCountryId());
        List<WTABaseRuleTemplateDTO> wtaBaseRuleTemplateDTOS = WTABuilderService.copyRuleTemplatesToDTO(templateList);
        RuleTemplateWrapper ruleTemplateWrapper = new RuleTemplateWrapper();
        assignCategoryToRuleTemplate(categoryList,wtaBaseRuleTemplateDTOS);
        ruleTemplateWrapper.setCategoryList(categoryList);
        ruleTemplateWrapper.setTemplateList(wtaBaseRuleTemplateDTOS);

        return ruleTemplateWrapper;

    }

    public void assignCategoryToRuleTemplate(List<RuleTemplateCategoryTagDTO> categoryList,List<WTABaseRuleTemplateDTO> templateList){
        for (RuleTemplateCategoryTagDTO ruleTemplateCategoryTagDTO : categoryList) {
            for (WTABaseRuleTemplateDTO ruleTemplateResponseDTO : templateList) {
                if(ruleTemplateCategoryTagDTO.getId()!=null && ruleTemplateResponseDTO!=null && ruleTemplateCategoryTagDTO.getId().equals(ruleTemplateResponseDTO.getRuleTemplateCategoryId())){
                    RuleTemplateCategoryDTO ruleTemplateCategoryDTO = new RuleTemplateCategoryDTO();
                    BeanUtils.copyProperties(ruleTemplateCategoryTagDTO,ruleTemplateCategoryDTO);
                    ruleTemplateResponseDTO.setRuleTemplateCategory(ruleTemplateCategoryDTO);
                    //ruleTemplateResponseDTO.setTemplateType("MAXIMUM_SHIFT_LENGTH");
                }
            }
        }
    }


    public WTABaseRuleTemplateDTO updateRuleTemplate(long countryId, WTABaseRuleTemplateDTO templateDTO) {
        CountryDTO country = countryRestClient.getCountryById(countryId);
        if (!Optional.ofNullable(country).isPresent()) {
            throw new DataNotFoundByIdException("Invalid Country");
        }
        WTABaseRuleTemplate oldTemplate = wtaBaseRuleTemplateMongoRepository.findOne(templateDTO.getId());
        if (!Optional.ofNullable(oldTemplate).isPresent()) {
            throw new DataNotFoundByIdException("Invalid TemplateType id " + templateDTO.getId());
        }
       /* String templateType=getTemplateType(templateDTO.getTemplateType());
        WTATemplateType ruleTemplateType = getByTemplateType(templateType);*/
        oldTemplate = WTABuilderService.copyRuleTemplate(templateDTO,false);
        //BigInteger ruleTemplateCategoryId = checkAndAssignRuleTemplateCategory(oldTemplate, templateDTO);
        CurrentUserDetails currentUserDetails = UserContext.getUserDetails();
        //List<PhaseTemplateValue> phaseTemplateValues = new ArrayList<>();
        //BeanUtils.copyProperties(phaseTemplateValues,templateDTO.getPhaseTemplateValues());
        //oldTemplate.setPhaseTemplateValues(phaseTemplateValues);
        //oldTemplate.setDisabled(templateDTO.isDisabled());
        //oldTemplate.setRecommendedValue(templateDTO.getRecommendedValue());

        oldTemplate.setLastUpdatedBy(currentUserDetails.getFirstName());
        oldTemplate.setRuleTemplateCategoryId(templateDTO.getRuleTemplateCategory().getId());
        oldTemplate.setCountryId(countryId);
        save(oldTemplate);
        return templateDTO;
    }

    /*protected BigInteger checkAndAssignRuleTemplateCategory(WTABaseRuleTemplate oldTemplate, WTARuleTemplateDTO templateDTO) {
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
*/
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

    public WTABaseRuleTemplateDTO copyRuleTemplate(Long countryId, WTABaseRuleTemplateDTO wtaRuleTemplateDTO) {
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
        WTABaseRuleTemplate wtaBaseRuleTemplate = WTABuilderService.copyRuleTemplate(wtaRuleTemplateDTO,true);
        wtaBaseRuleTemplate.setRuleTemplateCategoryId(ruleTemplateCategory.getId());
        save(wtaBaseRuleTemplate);
        wtaRuleTemplateDTO.setId(wtaBaseRuleTemplate.getId());
       // RuleTemplateCategoryDTO ruleTemplateCategoryDTO = new RuleTemplateCategoryDTO();
        //BeanUtils.copyProperties(ruleTemplateCategory,ruleTemplateCategoryDTO);
        wtaRuleTemplateDTO.setRuleTemplateCategory(wtaRuleTemplateDTO.getRuleTemplateCategory());
        /*int number=getNumberFromlastInsertedTemplateType(lastInsertedTemplateType);

        String templateTypeToBeSet=originalTemplateType+"_";*/
        return wtaRuleTemplateDTO;


    }
    /*private String getTemplateType(String templateType){
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
    }*/


  /*  int getNumberFromlastInsertedTemplateType(String templateType) {
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
    }*/

}

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
import com.kairos.activity.service.exception.ExceptionService;
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
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private ExceptionService exceptionService;

    public boolean createRuleTemplate(long countryId) {
        CountryDTO countryDTO = countryRestClient.getCountryById(countryId);

        if (countryDTO == null) {
            exceptionService.dataNotFoundByIdException("message.country.id",countryId);
        }
        List<WTABaseRuleTemplate> wtaBaseRuleTemplates = wtaBaseRuleTemplateMongoRepository.getWTABaseRuleTemplateByCountryId(countryId);
        RuleTemplateCategory ruleTemplateCategory = ruleTemplateCategoryMongoRepository.findByName(countryId, "NONE", RuleTemplateCategoryType.WTA);
        if (!Optional.ofNullable(ruleTemplateCategory).isPresent()) {
            ruleTemplateCategory = new RuleTemplateCategory("NONE","None", RuleTemplateCategoryType.WTA);
            ruleTemplateCategory.setCountryId(countryDTO.getId());
            save(ruleTemplateCategory);
        }
        if (Optional.ofNullable(wtaBaseRuleTemplates).isPresent() && !wtaBaseRuleTemplates.isEmpty()) {
            exceptionService.dataNotFoundByIdException("message.wtaruletemplate.alreadyexists");
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
        consecutiveWorking.setIntervalLength(12);
        consecutiveWorking.setIntervalUnit(week);
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
        careDaysCheck.setRuleTemplateCategoryId(ruleTemplateCategory.getId());
        wtaBaseRuleTemplates1.add(careDaysCheck);

        DaysOffAfterASeriesWTATemplate daysOffAfterASeriesWTATemplate=new DaysOffAfterASeriesWTATemplate("Minimum days off after a series of night shifts in sequence",false,"Minimum days off after a series of night shifts in sequence",1,week,1);
        daysOffAfterASeriesWTATemplate.setPhaseTemplateValues(phaseTemplateValues);
        daysOffAfterASeriesWTATemplate.setCountryId(countryDTO.getId());
        daysOffAfterASeriesWTATemplate.setRuleTemplateCategoryId(ruleTemplateCategory.getId());
        wtaBaseRuleTemplates1.add(daysOffAfterASeriesWTATemplate);

        NoOfSequenceShiftWTATemplate noOfSequenceShiftWTATemplate=new NoOfSequenceShiftWTATemplate("No Of Sequence Shift",false,"No OF Sequence Shift", PartOfDay.DAY,PartOfDay.NIGHT);
        noOfSequenceShiftWTATemplate.setPhaseTemplateValues(phaseTemplateValues);
        noOfSequenceShiftWTATemplate.setCountryId(countryDTO.getId());
        noOfSequenceShiftWTATemplate.setRuleTemplateCategoryId(ruleTemplateCategory.getId());
        wtaBaseRuleTemplates1.add(noOfSequenceShiftWTATemplate);

        EmployeesWithIncreasedRiskWTATemplate employeesWithIncreasedRiskWTATemplate=new EmployeesWithIncreasedRiskWTATemplate("Employees with Increased Risk",false,"Employees with increased risk",18,62,false);
        employeesWithIncreasedRiskWTATemplate.setPhaseTemplateValues(phaseTemplateValues);
        employeesWithIncreasedRiskWTATemplate.setCountryId(countryDTO.getId());
        employeesWithIncreasedRiskWTATemplate.setRuleTemplateCategoryId(ruleTemplateCategory.getId());
        //wtaBaseRuleTemplates1.add(employeesWithIncreasedRiskWTATemplate);

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
            exceptionService.dataNotFoundByIdException("message.country.id",countryId);
        }

        List<RuleTemplateCategoryTagDTO> categoryList = ruleTemplateCategoryMongoRepository.getAllRulesOfCountry(countryId);

        if (categoryList == null) {
            exceptionService.dataNotFoundByIdException("message.category.null-list");
        }

        List<WTABaseRuleTemplate> templateList = wtaBaseRuleTemplateMongoRepository.getWTABaseRuleTemplateByCountryId(countryId);
        if (templateList == null) {
            exceptionService.dataNotFoundByIdException("message.wta-base-rule-template.null-list");
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
            exceptionService.dataNotFoundByIdException("message.organization.id");
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
                }
            }
        }
    }


    public WTABaseRuleTemplateDTO updateRuleTemplate(long countryId, WTABaseRuleTemplateDTO templateDTO) {
        CountryDTO country = countryRestClient.getCountryById(countryId);
        if (!Optional.ofNullable(country).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.country.id",countryId);
        }
        WTABaseRuleTemplate oldTemplate = wtaBaseRuleTemplateMongoRepository.findOne(templateDTO.getId());
        if (!Optional.ofNullable(oldTemplate).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.wta-base-rule-template.id",templateDTO.getId());
        }
        oldTemplate = WTABuilderService.copyRuleTemplate(templateDTO,false);
        CurrentUserDetails currentUserDetails = UserContext.getUserDetails();
        oldTemplate.setLastUpdatedBy(currentUserDetails.getFirstName());
        oldTemplate.setRuleTemplateCategoryId(templateDTO.getRuleTemplateCategory().getId());
        oldTemplate.setCountryId(countryId);
        save(oldTemplate);
        return templateDTO;
    }
    public WTABaseRuleTemplateDTO copyRuleTemplate(Long countryId, WTABaseRuleTemplateDTO wtaRuleTemplateDTO) {
        CountryDTO country = countryRestClient.getCountryById(countryId);
        if (!Optional.ofNullable(country).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.country.id",countryId);
        }
        RuleTemplateCategory ruleTemplateCategory = ruleTemplateCategoryMongoRepository.findByName(countryId, wtaRuleTemplateDTO.getRuleTemplateCategory().getName(), RuleTemplateCategoryType.WTA);
        if (!Optional.ofNullable(ruleTemplateCategory).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.category.not-matched");
        }

        WTABaseRuleTemplate wtaBaseRuleTemplate1 = wtaBaseRuleTemplateMongoRepository.existsByName(countryId,wtaRuleTemplateDTO.getName().trim());
        if (Optional.ofNullable(wtaBaseRuleTemplate1).isPresent()) {
            exceptionService.duplicateDataException("message.wta-base-rule-template.name.alreadyexist",wtaRuleTemplateDTO.getName());

        }
        WTABaseRuleTemplate wtaBaseRuleTemplate = WTABuilderService.copyRuleTemplate(wtaRuleTemplateDTO,true);
        wtaBaseRuleTemplate.setRuleTemplateCategoryId(ruleTemplateCategory.getId());
        save(wtaBaseRuleTemplate);
        wtaRuleTemplateDTO.setId(wtaBaseRuleTemplate.getId());
        wtaRuleTemplateDTO.setRuleTemplateCategory(wtaRuleTemplateDTO.getRuleTemplateCategory());
        return wtaRuleTemplateDTO;

    }
}

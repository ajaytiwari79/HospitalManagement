package com.kairos.service.wta;


import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.wta.AgeRange;
import com.kairos.dto.activity.wta.basic_details.WTABaseRuleTemplateDTO;
import com.kairos.dto.activity.wta.rule_template_category.RuleTemplateCategoryDTO;
import com.kairos.dto.activity.wta.rule_template_category.RuleTemplateCategoryTagDTO;
import com.kairos.dto.activity.wta.rule_template_category.RuleTemplateWrapper;
import com.kairos.dto.activity.wta.templates.PhaseTemplateValue;
import com.kairos.dto.user.country.basic_details.CountryDTO;
import com.kairos.dto.user.organization.OrganizationDTO;
import com.kairos.enums.RuleTemplateCategoryType;
import com.kairos.enums.wta.PartOfDay;
import com.kairos.enums.wta.WTATemplateType;
import com.kairos.dto.activity.wta.templates.BreakAvailabilitySettings;
import com.kairos.persistence.model.wta.templates.RuleTemplateCategory;
import com.kairos.persistence.model.wta.templates.WTABaseRuleTemplate;
import com.kairos.persistence.model.wta.templates.WTABuilderService;
import com.kairos.persistence.model.wta.templates.template_types.*;
import com.kairos.persistence.repository.wta.rule_template.RuleTemplateCategoryRepository;
import com.kairos.persistence.repository.wta.rule_template.WTABaseRuleTemplateMongoRepository;
import com.kairos.rest_client.GenericIntegrationService;
import com.kairos.rest_client.OrganizationRestClient;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.tag.TagService;
import com.kairos.utils.user_context.CurrentUserDetails;
import com.kairos.utils.user_context.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static com.kairos.constants.AppConstants.WEEKS;


/**
 * Created by pawanmandhan on 5/8/17.
 */
@Transactional
@Service
public class RuleTemplateService extends MongoBaseService {
    @Inject
    private GenericIntegrationService genericIntegrationService;
    @Autowired
    private RuleTemplateCategoryRepository ruleTemplateCategoryMongoRepository;
    @Inject
    private TagService tagService;
    @Inject
    private WTABaseRuleTemplateMongoRepository wtaBaseRuleTemplateMongoRepository;
    @Inject
    private WTAOrganizationService wtaOrganizationService;
    @Inject
    private OrganizationRestClient organizationRestClient;
    private final Logger logger = LoggerFactory.getLogger(RuleTemplateService.class);
    @Autowired
    private ExceptionService exceptionService;

    public boolean createRuleTemplate(long countryId) {
        CountryDTO countryDTO = genericIntegrationService.getCountryById(countryId);

        if (countryDTO == null) {
            exceptionService.dataNotFoundByIdException("message.country.id", countryId);
        }
        List<WTABaseRuleTemplate> wtaBaseRuleTemplates = wtaBaseRuleTemplateMongoRepository.getWTABaseRuleTemplateByCountryId(countryId);
        RuleTemplateCategory ruleTemplateCategory = ruleTemplateCategoryMongoRepository.findByName(countryId, "NONE", RuleTemplateCategoryType.WTA);
        if (!Optional.ofNullable(ruleTemplateCategory).isPresent()) {
            ruleTemplateCategory = new RuleTemplateCategory("NONE", "None", RuleTemplateCategoryType.WTA);
            ruleTemplateCategory.setCountryId(countryDTO.getId());
            save(ruleTemplateCategory);
        }
        if (Optional.ofNullable(wtaBaseRuleTemplates).isPresent() && !wtaBaseRuleTemplates.isEmpty()) {
            exceptionService.dataNotFoundByIdException("message.wtaruletemplate.alreadyexists");
        }

        String weeks = WEEKS;
        String TUESDAY = "TUESDAY";
        List<WTABaseRuleTemplate> wtaBaseRuleTemplates1 = new ArrayList<>();
        AgeRange range = new AgeRange(0, 0, 0);

        List<PhaseTemplateValue> phaseTemplateValues = new ArrayList<>();
        phaseTemplateValues.add(new PhaseTemplateValue(1, "REQUEST", (short) 0, (short) 0, true, false, false,1));
        phaseTemplateValues.add(new PhaseTemplateValue(2, "PUZZLE", (short) 0, (short) 0, true, false, false,2));
        phaseTemplateValues.add(new PhaseTemplateValue(4, "CONSTRUCTION", (short) 0, (short) 0, true, false, false,3));
        phaseTemplateValues.add(new PhaseTemplateValue(3, "DRAFT", (short) 0, (short) 0, true, false, false,4));
        phaseTemplateValues.add(new PhaseTemplateValue(7, "TENTATIVE", (short) 0, (short) 0, true, false, false,5));
        phaseTemplateValues.add(new PhaseTemplateValue(5, "REALTIME", (short) 0, (short) 0, true, false, false,6));
        phaseTemplateValues.add(new PhaseTemplateValue(6, "TIME & ATTENDANCE", (short) 0, (short) 0, true, false, false,7));
        phaseTemplateValues.add(new PhaseTemplateValue(8, "PAYROLL", (short) 0, (short) 0, true, false, false,8));

        ShiftLengthWTATemplate shiftLengthWTATemplate = new ShiftLengthWTATemplate("Maximum night shift’s length", "Maximum night shift’s length", 400);
        shiftLengthWTATemplate.setCountryId(countryDTO.getId());
        shiftLengthWTATemplate.setPhaseTemplateValues(phaseTemplateValues);
        shiftLengthWTATemplate.setRuleTemplateCategoryId(ruleTemplateCategory.getId());
        wtaBaseRuleTemplates1.add(shiftLengthWTATemplate);

        ConsecutiveWorkWTATemplate consecutiveWorking = new ConsecutiveWorkWTATemplate("Maximum number of consecutive days",  "Maximum number of consecutive days");
        consecutiveWorking.setCountryId(countryDTO.getId());
        consecutiveWorking.setIntervalLength(12);
        consecutiveWorking.setIntervalUnit(weeks);
        consecutiveWorking.setPhaseTemplateValues(phaseTemplateValues);
        consecutiveWorking.setRuleTemplateCategoryId(ruleTemplateCategory.getId());
        wtaBaseRuleTemplates1.add(consecutiveWorking);

        ConsecutiveRestPartOfDayWTATemplate restInConsecutiveDays = new ConsecutiveRestPartOfDayWTATemplate("Minimum rest after consecutive days worked", false, "Minimum rest after consecutive days worked");
        restInConsecutiveDays.setCountryId(countryDTO.getId());
        restInConsecutiveDays.setPhaseTemplateValues(phaseTemplateValues);
        restInConsecutiveDays.setRuleTemplateCategoryId(ruleTemplateCategory.getId());
        wtaBaseRuleTemplates1.add(restInConsecutiveDays);

        NumberOfPartOfDayShiftsWTATemplate numberOfPartOfDayShiftsWTATemplate = new NumberOfPartOfDayShiftsWTATemplate("Maximum number of shifts per interval", false, "Maximum number of shifts per interval");
        numberOfPartOfDayShiftsWTATemplate.setIntervalLength(1);
        numberOfPartOfDayShiftsWTATemplate.setIntervalUnit(weeks);
        numberOfPartOfDayShiftsWTATemplate.setCountryId(countryDTO.getId());
        numberOfPartOfDayShiftsWTATemplate.setPhaseTemplateValues(phaseTemplateValues);
        numberOfPartOfDayShiftsWTATemplate.setRuleTemplateCategoryId(ruleTemplateCategory.getId());
        wtaBaseRuleTemplates1.add(numberOfPartOfDayShiftsWTATemplate);

        DaysOffInPeriodWTATemplate daysOffInPeriodWTATemplate = new DaysOffInPeriodWTATemplate("Minimum number of days off per period", false, "Minimum number of days off per period", 12, weeks);
        daysOffInPeriodWTATemplate.setCountryId(countryDTO.getId());
        daysOffInPeriodWTATemplate.setPhaseTemplateValues(phaseTemplateValues);
        daysOffInPeriodWTATemplate.setRuleTemplateCategoryId(ruleTemplateCategory.getId());
        wtaBaseRuleTemplates1.add(daysOffInPeriodWTATemplate);

        AverageScheduledTimeWTATemplate averageScheduledTimeWTATemplate = new AverageScheduledTimeWTATemplate("Maximum average duration per week in an interval", false, "Maximum average duration per week in an interval", 1,weeks);
        averageScheduledTimeWTATemplate.setCountryId(countryDTO.getId());
        averageScheduledTimeWTATemplate.setPhaseTemplateValues(phaseTemplateValues);
        averageScheduledTimeWTATemplate.setRuleTemplateCategoryId(ruleTemplateCategory.getId());
        wtaBaseRuleTemplates1.add(averageScheduledTimeWTATemplate);

        VetoPerPeriodWTATemplate vetoPerPeriodWTATemplate = new VetoPerPeriodWTATemplate("Maximum veto per period", false, "Maximum veto per period");
        vetoPerPeriodWTATemplate.setCountryId(countryDTO.getId());
        //vetoPerPeriodWTATemplate.setPhaseTemplateValues(phaseTemplateValues);
        vetoPerPeriodWTATemplate.setRuleTemplateCategoryId(ruleTemplateCategory.getId());
        wtaBaseRuleTemplates1.add(vetoPerPeriodWTATemplate);

        NumberOfWeekendShiftsInPeriodWTATemplate numberofWeekendShiftsInPeriodWTATemplate = new NumberOfWeekendShiftsInPeriodWTATemplate("Weekend off Distribution", false, "Weekend off Distribution", TUESDAY, LocalTime.of(10, 30), TUESDAY, LocalTime.of(10, 30));
        numberofWeekendShiftsInPeriodWTATemplate.setCountryId(countryDTO.getId());
        numberofWeekendShiftsInPeriodWTATemplate.setPhaseTemplateValues(phaseTemplateValues);
        numberofWeekendShiftsInPeriodWTATemplate.setRuleTemplateCategoryId(ruleTemplateCategory.getId());
        wtaBaseRuleTemplates1.add(numberofWeekendShiftsInPeriodWTATemplate);

        /*ChildCareDaysCheckWTATemplate careDayCheckWTATemplate = new ChildCareDaysCheckWTATemplate("Care days check",true,"Care days check",2, dateInMillis, MONTHS, 1);
        careDayCheckWTATemplate.setCountryId(countryDTO.getId());
        careDayCheckWTATemplate.setPhaseTemplateValues(phaseTemplateValues);
        careDayCheckWTATemplate.setRuleTemplateCategoryId(ruleTemplateCategory.getId());
        wtaBaseRuleTemplates1.add(careDayCheckWTATemplate);*/

        DurationBetweenShiftsWTATemplate dailyRestingTimeWTATemplate = new DurationBetweenShiftsWTATemplate("Minimum resting hours daily", false, "Minimum resting hours daily");
        dailyRestingTimeWTATemplate.setCountryId(countryDTO.getId());
        dailyRestingTimeWTATemplate.setPhaseTemplateValues(phaseTemplateValues);
        dailyRestingTimeWTATemplate.setRuleTemplateCategoryId(ruleTemplateCategory.getId());
        dailyRestingTimeWTATemplate.setWtaTemplateType(WTATemplateType.DURATION_BETWEEN_SHIFTS);
        wtaBaseRuleTemplates1.add(dailyRestingTimeWTATemplate);

        DurationBetweenShiftsWTATemplate durationBetweenShiftsWTATemplate = new DurationBetweenShiftsWTATemplate("Minimum duration between shifts", false, "Minimum duration between shifts");
        durationBetweenShiftsWTATemplate.setCountryId(countryDTO.getId());
        durationBetweenShiftsWTATemplate.setPhaseTemplateValues(phaseTemplateValues);
        durationBetweenShiftsWTATemplate.setWtaTemplateType(WTATemplateType.DURATION_BETWEEN_SHIFTS);
        durationBetweenShiftsWTATemplate.setRuleTemplateCategoryId(ruleTemplateCategory.getId());
        wtaBaseRuleTemplates1.add(durationBetweenShiftsWTATemplate);


        RestPeriodInAnIntervalWTATemplate restPeriodInAnIntervalWTATemplate = new RestPeriodInAnIntervalWTATemplate("Minimum rest period in an interval", false, "Minimum rest period in an interval");
        restPeriodInAnIntervalWTATemplate.setCountryId(countryDTO.getId());
        restPeriodInAnIntervalWTATemplate.setPhaseTemplateValues(phaseTemplateValues);
        restPeriodInAnIntervalWTATemplate.setRuleTemplateCategoryId(ruleTemplateCategory.getId());
        wtaBaseRuleTemplates1.add(restPeriodInAnIntervalWTATemplate);


        ShortestAndAverageDailyRestWTATemplate shortestAndAverageDailyRestWTATemplate = new ShortestAndAverageDailyRestWTATemplate("Shortest and Average daily Rest", false, "Shortest and Average daily Rest", 1, weeks);
        shortestAndAverageDailyRestWTATemplate.setCountryId(countryDTO.getId());
        shortestAndAverageDailyRestWTATemplate.setPhaseTemplateValues(phaseTemplateValues);
        shortestAndAverageDailyRestWTATemplate.setRuleTemplateCategoryId(ruleTemplateCategory.getId());
        wtaBaseRuleTemplates1.add(shortestAndAverageDailyRestWTATemplate);

        /*ShiftsInIntervalWTATemplate maximumShiftsInIntervalWTATemplate = new ShiftsInIntervalWTATemplate("Shifts In Interval",false,"Shifts In Interval",1, week, localDate);
        maximumShiftsInIntervalWTATemplate.setCountryId(countryDTO.getId());
        maximumShiftsInIntervalWTATemplate.setPhaseTemplateValues(phaseTemplateValues);
        maximumShiftsInIntervalWTATemplate.setRuleTemplateCategoryId(ruleTemplateCategory.getId());
        wtaBaseRuleTemplates1.add(maximumShiftsInIntervalWTATemplate);*/

        /*SeniorDaysInYearWTATemplate seniorDaysInYearWTATemplate = new SeniorDaysInYearWTATemplate("Maximum senior days per year",true,"Maximum senior days per year",1, "NA", dateInMillis, 1, "");
        seniorDaysInYearWTATemplate.setCountryId(countryDTO.getId());
        seniorDaysInYearWTATemplate.setPhaseTemplateValues(phaseTemplateValues);
        seniorDaysInYearWTATemplate.setRuleTemplateCategoryId(ruleTemplateCategory.getId());
        wtaBaseRuleTemplates1.add(seniorDaysInYearWTATemplate);*/

        TimeBankWTATemplate timeBankWTATemplate = new TimeBankWTATemplate("Maximum Time Bank", false, "Maximum Time Bank");
        timeBankWTATemplate.setCountryId(countryDTO.getId());
        timeBankWTATemplate.setPhaseTemplateValues(phaseTemplateValues);
        timeBankWTATemplate.setRuleTemplateCategoryId(ruleTemplateCategory.getId());
        wtaBaseRuleTemplates1.add(timeBankWTATemplate);

        SeniorDaysPerYearWTATemplate seniorDaysPerYearWTATemplate = new SeniorDaysPerYearWTATemplate("Senior Days per Year", true, false, "Senior Days per Year", Arrays.asList(range));
        seniorDaysPerYearWTATemplate.setCountryId(countryDTO.getId());
        seniorDaysPerYearWTATemplate.setPhaseTemplateValues(phaseTemplateValues);
        seniorDaysPerYearWTATemplate.setRuleTemplateCategoryId(ruleTemplateCategory.getId());
        wtaBaseRuleTemplates1.add(seniorDaysPerYearWTATemplate);

        ChildCareDaysCheckWTATemplate careDaysCheck = new ChildCareDaysCheckWTATemplate("Child Care Days Check", false, "Child Care Days Check", Arrays.asList(range));
        careDaysCheck.setCountryId(countryDTO.getId());
        careDaysCheck.setRuleTemplateCategoryId(ruleTemplateCategory.getId());
        wtaBaseRuleTemplates1.add(careDaysCheck);

        DaysOffAfterASeriesWTATemplate daysOffAfterASeriesWTATemplate = new DaysOffAfterASeriesWTATemplate("Minimum days off after a series of night shifts in sequence", false, "Minimum days off after a series of night shifts in sequence", 1, weeks, 1);
        daysOffAfterASeriesWTATemplate.setPhaseTemplateValues(phaseTemplateValues);
        daysOffAfterASeriesWTATemplate.setCountryId(countryDTO.getId());
        daysOffAfterASeriesWTATemplate.setRuleTemplateCategoryId(ruleTemplateCategory.getId());
        wtaBaseRuleTemplates1.add(daysOffAfterASeriesWTATemplate);

        NoOfSequenceShiftWTATemplate noOfSequenceShiftWTATemplate = new NoOfSequenceShiftWTATemplate("No Of Sequence Shift", false, "No of Sequence Shift", PartOfDay.DAY, PartOfDay.NIGHT);
        noOfSequenceShiftWTATemplate.setPhaseTemplateValues(phaseTemplateValues);
        noOfSequenceShiftWTATemplate.setCountryId(countryDTO.getId());
        noOfSequenceShiftWTATemplate.setRuleTemplateCategoryId(ruleTemplateCategory.getId());
        wtaBaseRuleTemplates1.add(noOfSequenceShiftWTATemplate);

        EmployeesWithIncreasedRiskWTATemplate employeesWithIncreasedRiskWTATemplate = new EmployeesWithIncreasedRiskWTATemplate("Employees with Increased RISK", false, "Employees with increased risk", 18, 62, false);
        employeesWithIncreasedRiskWTATemplate.setPhaseTemplateValues(phaseTemplateValues);
        employeesWithIncreasedRiskWTATemplate.setCountryId(countryDTO.getId());
        employeesWithIncreasedRiskWTATemplate.setRuleTemplateCategoryId(ruleTemplateCategory.getId());


        Set<BreakAvailabilitySettings> breakAvailabilitySettings=new HashSet<>();
        BreakAvailabilitySettings breakAvailabilitySettingsForDay=new BreakAvailabilitySettings(PartOfDay.DAY,(short)60,(short)120);
        BreakAvailabilitySettings breakAvailabilitySettingsForEvening=new BreakAvailabilitySettings(PartOfDay.EVENING,(short)90,(short)60);
        BreakAvailabilitySettings breakAvailabilitySettingsForNight=new BreakAvailabilitySettings(PartOfDay.NIGHT,(short)90,(short)60);
        breakAvailabilitySettings.add(breakAvailabilitySettingsForDay);
        breakAvailabilitySettings.add(breakAvailabilitySettingsForEvening);
        breakAvailabilitySettings.add(breakAvailabilitySettingsForNight);

        BreakWTATemplate breakWTATemplate=new BreakWTATemplate("WTA for breaks in shift","WTA for breaks in shift",(short)30,breakAvailabilitySettings);
        wtaBaseRuleTemplates1.add(breakWTATemplate);
        //wtaBaseRuleTemplates1.add(employeesWithIncreasedRiskWTATemplate);

        WTAForCareDays careDays = new WTAForCareDays("WTA For Care Days","WTA For Care Days");
        wtaBaseRuleTemplates1.add(careDays);
        save(wtaBaseRuleTemplates1);


        return true;
    }

    public RuleTemplateWrapper getRuleTemplate(long countryId) {

        List<RuleTemplateCategoryTagDTO> categoryList = ruleTemplateCategoryMongoRepository.findAllUsingCountryId(countryId);

        if (categoryList == null) {
            exceptionService.dataNotFoundByIdException("message.category.null-list");
        }

        List<WTABaseRuleTemplate> templateList = wtaBaseRuleTemplateMongoRepository.getWTABaseRuleTemplateByCountryId(countryId);
        if (templateList == null) {
            exceptionService.dataNotFoundByIdException("message.wta-base-rule-template.null-list");
        }

        //
        List<WTABaseRuleTemplateDTO> wtaBaseRuleTemplateDTOS = ObjectMapperUtils.copyPropertiesOfListByMapper(templateList, WTABaseRuleTemplateDTO.class);
        assignCategoryToRuleTemplate(categoryList, wtaBaseRuleTemplateDTOS);
        RuleTemplateWrapper wrapper = new RuleTemplateWrapper();
        wrapper.setCategoryList(categoryList);
        wrapper.setTemplateList(wtaBaseRuleTemplateDTOS);
        return wrapper;
    }

    public RuleTemplateWrapper getRulesTemplateCategoryByUnit(Long unitId) {
        OrganizationDTO organization = genericIntegrationService.getOrganizationWithCountryId(unitId);
        if (!Optional.ofNullable(organization).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.organization.id");
        }
        //List<WTAResponseDTO> wtaResponseDTOS = workingTimeAgreementMongoRepository.getWtaByOrganization(organization.getId());
        List<RuleTemplateCategoryTagDTO> categoryList = ruleTemplateCategoryMongoRepository.findAllUsingCountryId(organization.getCountryId());
        List<WTABaseRuleTemplate> templateList = wtaBaseRuleTemplateMongoRepository.getWTABaseRuleTemplateByCountryId(organization.getCountryId());
        List<WTABaseRuleTemplateDTO> wtaBaseRuleTemplateDTOS = ObjectMapperUtils.copyPropertiesOfListByMapper(templateList, WTABaseRuleTemplateDTO.class);
        RuleTemplateWrapper ruleTemplateWrapper = new RuleTemplateWrapper();
        assignCategoryToRuleTemplate(categoryList, wtaBaseRuleTemplateDTOS);
        ruleTemplateWrapper.setCategoryList(categoryList);
        ruleTemplateWrapper.setTemplateList(wtaBaseRuleTemplateDTOS);

        return ruleTemplateWrapper;

    }

    public void assignCategoryToRuleTemplate(List<RuleTemplateCategoryTagDTO> categoryList, List<WTABaseRuleTemplateDTO> templateList) {
        for (RuleTemplateCategoryTagDTO ruleTemplateCategoryTagDTO : categoryList) {
            for (WTABaseRuleTemplateDTO ruleTemplateResponseDTO : templateList) {
                if (ruleTemplateCategoryTagDTO.getId() != null && ruleTemplateResponseDTO != null && ruleTemplateCategoryTagDTO.getId().equals(ruleTemplateResponseDTO.getRuleTemplateCategoryId())) {
                    RuleTemplateCategoryDTO ruleTemplateCategoryDTO = new RuleTemplateCategoryDTO();
                    BeanUtils.copyProperties(ruleTemplateCategoryTagDTO, ruleTemplateCategoryDTO);
                    ruleTemplateResponseDTO.setRuleTemplateCategory(ruleTemplateCategoryDTO);
                }
            }
        }
    }


    public WTABaseRuleTemplateDTO updateRuleTemplate(long countryId, BigInteger ruleTemplateId, WTABaseRuleTemplateDTO templateDTO) {
        CountryDTO country = genericIntegrationService.getCountryById(countryId);
        if (!Optional.ofNullable(country).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.country.id", countryId);
        }
        WTABaseRuleTemplate oldTemplate = wtaBaseRuleTemplateMongoRepository.findOne(ruleTemplateId);
        if (!Optional.ofNullable(oldTemplate).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.wta-base-rule-template.id", templateDTO.getId());
        }
        oldTemplate = WTABuilderService.copyRuleTemplate(templateDTO, false);
        CurrentUserDetails currentUserDetails = UserContext.getUserDetails();
        oldTemplate.setLastUpdatedBy(currentUserDetails.getFirstName());
        oldTemplate.setRuleTemplateCategoryId(templateDTO.getRuleTemplateCategory().getId());
        oldTemplate.setCountryId(countryId);
        save(oldTemplate);
        return templateDTO;
    }


    public WTABaseRuleTemplateDTO copyRuleTemplate(Long countryId, WTABaseRuleTemplateDTO wtaRuleTemplateDTO) {
        CountryDTO country = genericIntegrationService.getCountryById(countryId);
        if (!Optional.ofNullable(country).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.country.id", countryId);
        }
        RuleTemplateCategory ruleTemplateCategory = ruleTemplateCategoryMongoRepository.findByName(countryId, wtaRuleTemplateDTO.getRuleTemplateCategory().getName(), RuleTemplateCategoryType.WTA);
        if (!Optional.ofNullable(ruleTemplateCategory).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.category.not-matched");
        }

        WTABaseRuleTemplate wtaBaseRuleTemplate1 = wtaBaseRuleTemplateMongoRepository.existsByName(countryId, wtaRuleTemplateDTO.getName().trim());
        if (Optional.ofNullable(wtaBaseRuleTemplate1).isPresent()) {
            exceptionService.duplicateDataException("message.wta-base-rule-template.name.alreadyexist", wtaRuleTemplateDTO.getName());

        }
        WTABaseRuleTemplate wtaBaseRuleTemplate = WTABuilderService.copyRuleTemplate(wtaRuleTemplateDTO, true);
        wtaBaseRuleTemplate.setCountryId(countryId);
        wtaBaseRuleTemplate.setRuleTemplateCategoryId(ruleTemplateCategory.getId());
        save(wtaBaseRuleTemplate);
        wtaRuleTemplateDTO.setId(wtaBaseRuleTemplate.getId());
        wtaRuleTemplateDTO.setRuleTemplateCategory(wtaRuleTemplateDTO.getRuleTemplateCategory());
        return wtaRuleTemplateDTO;

    }
}

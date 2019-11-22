package com.kairos.service.wta;

import com.kairos.constants.AppConstants;
import com.kairos.dto.user_context.CurrentUserDetails;
import com.kairos.dto.activity.wta.AgeRange;
import com.kairos.dto.activity.wta.basic_details.WTABaseRuleTemplateDTO;
import com.kairos.dto.activity.wta.rule_template_category.RuleTemplateCategoryDTO;
import com.kairos.dto.activity.wta.rule_template_category.RuleTemplateCategoryTagDTO;
import com.kairos.dto.activity.wta.rule_template_category.RuleTemplateWrapper;
import com.kairos.dto.activity.wta.templates.BreakAvailabilitySettings;
import com.kairos.dto.activity.wta.templates.PhaseTemplateValue;
import com.kairos.dto.user.country.basic_details.CountryDTO;
import com.kairos.dto.user.organization.OrganizationDTO;
import com.kairos.dto.user_context.UserContext;
import com.kairos.enums.RuleTemplateCategoryType;
import com.kairos.enums.wta.PartOfDay;
import com.kairos.enums.wta.WTATemplateType;
import com.kairos.persistence.model.phase.Phase;
import com.kairos.persistence.model.wta.templates.RuleTemplateCategory;
import com.kairos.persistence.model.wta.templates.WTABaseRuleTemplate;
import com.kairos.persistence.model.wta.templates.template_types.*;
import com.kairos.persistence.repository.phase.PhaseMongoRepository;
import com.kairos.persistence.repository.wta.rule_template.RuleTemplateCategoryRepository;
import com.kairos.persistence.repository.wta.rule_template.WTABaseRuleTemplateMongoRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.tag.TagService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.ObjectUtils.isCollectionEmpty;
import static com.kairos.constants.ActivityMessagesConstants.*;
import static com.kairos.service.wta.WTABuilderService.copyRuleTemplatesToDTO;

/**
 * Created by pawanmandhan on 5/8/17.
 */
@Transactional
@Service
public class RuleTemplateService{
    @Inject
    private UserIntegrationService userIntegrationService;
    @Autowired
    private RuleTemplateCategoryRepository ruleTemplateCategoryMongoRepository;
    @Inject
    private TagService tagService;
    @Inject
    private WTABaseRuleTemplateMongoRepository wtaBaseRuleTemplateMongoRepository;
    @Inject
    private WTAOrganizationService wtaOrganizationService;
    @Inject private PhaseMongoRepository phaseMongoRepository;

    @Autowired
    private ExceptionService exceptionService;

    public boolean createRuleTemplate(long countryId) {
        CountryDTO countryDTO = userIntegrationService.getCountryById(countryId);

        if (countryDTO == null) {
            exceptionService.dataNotFoundByIdException(MESSAGE_COUNTRY_ID, countryId);
        }
        List<WTABaseRuleTemplate> wtaBaseRuleTemplates = wtaBaseRuleTemplateMongoRepository.getWTABaseRuleTemplateByCountryId(countryId);
        RuleTemplateCategory ruleTemplateCategory = ruleTemplateCategoryMongoRepository.findByName(countryId, "NONE", RuleTemplateCategoryType.WTA);
        if (!Optional.ofNullable(ruleTemplateCategory).isPresent()) {
            ruleTemplateCategory = new RuleTemplateCategory("NONE", "None", RuleTemplateCategoryType.WTA);
            ruleTemplateCategory.setCountryId(countryDTO.getId());
            ruleTemplateCategoryMongoRepository.save(ruleTemplateCategory);
        }
        if (Optional.ofNullable(wtaBaseRuleTemplates).isPresent() && !wtaBaseRuleTemplates.isEmpty()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_WTARULETEMPLATE_ALREADYEXISTS);
        }

        final String WEEKS = AppConstants.WEEKS;
        final String TUESDAY = "TUESDAY";
        List<WTABaseRuleTemplate> wtaBaseRuleTemplates1 = new ArrayList<>();
        AgeRange range = new AgeRange(0, 0, 0);

        List<PhaseTemplateValue> phaseTemplateValues = new ArrayList<>();
        List<Phase> countryPhase = phaseMongoRepository.findAllBycountryIdAndDeletedFalse(countryId);
        if(isCollectionEmpty(countryPhase)){
            exceptionService.actionNotPermittedException("message.country.phase.notFound");
        }
        Map<String,BigInteger> phaseMap = countryPhase.stream().collect(Collectors.toMap(k->k.getPhaseEnum().toString(),v->v.getId()));
        phaseTemplateValues.add(new PhaseTemplateValue(phaseMap.get("REQUEST"), "REQUEST", (short) 0, (short) 0, true, false, false,1));
        phaseTemplateValues.add(new PhaseTemplateValue(phaseMap.get("PUZZLE"), "PUZZLE", (short) 0, (short) 0, true, false, false,2));
        phaseTemplateValues.add(new PhaseTemplateValue(phaseMap.get("CONSTRUCTION"), "CONSTRUCTION", (short) 0, (short) 0, true, false, false,3));
        phaseTemplateValues.add(new PhaseTemplateValue(phaseMap.get("DRAFT"), "DRAFT", (short) 0, (short) 0, true, false, false,4));
        phaseTemplateValues.add(new PhaseTemplateValue(phaseMap.get("TENTATIVE"), "TENTATIVE", (short) 0, (short) 0, true, false, false,5));
        phaseTemplateValues.add(new PhaseTemplateValue(phaseMap.get("REALTIME"), "REALTIME", (short) 0, (short) 0, true, false, false,6));
        phaseTemplateValues.add(new PhaseTemplateValue(phaseMap.get("TIME_ATTENDANCE"), "TIME & ATTENDANCE", (short) 0, (short) 0, true, false, false,7));

        ShiftLengthWTATemplate shiftLengthWTATemplate = new ShiftLengthWTATemplate("Maximum night shift’s length", "Maximum night shift’s length", 400);
        shiftLengthWTATemplate.setCountryId(countryDTO.getId());
        shiftLengthWTATemplate.setPhaseTemplateValues(phaseTemplateValues);
        shiftLengthWTATemplate.setRuleTemplateCategoryId(ruleTemplateCategory.getId());
        wtaBaseRuleTemplates1.add(shiftLengthWTATemplate);

        ConsecutiveWorkWTATemplate consecutiveWorking = new ConsecutiveWorkWTATemplate("Maximum number of consecutive shifts",  "Maximum number of consecutive shifts");
        consecutiveWorking.setCountryId(countryDTO.getId());
        consecutiveWorking.setIntervalLength(12);
        consecutiveWorking.setIntervalUnit(WEEKS);
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
        numberOfPartOfDayShiftsWTATemplate.setIntervalUnit(WEEKS);
        numberOfPartOfDayShiftsWTATemplate.setCountryId(countryDTO.getId());
        numberOfPartOfDayShiftsWTATemplate.setPhaseTemplateValues(phaseTemplateValues);
        numberOfPartOfDayShiftsWTATemplate.setRuleTemplateCategoryId(ruleTemplateCategory.getId());
        wtaBaseRuleTemplates1.add(numberOfPartOfDayShiftsWTATemplate);

        DaysOffInPeriodWTATemplate daysOffInPeriodWTATemplate = new DaysOffInPeriodWTATemplate("Minimum number of days off per period", false, "Minimum number of days off per period", 12, WEEKS);
        daysOffInPeriodWTATemplate.setCountryId(countryDTO.getId());
        daysOffInPeriodWTATemplate.setPhaseTemplateValues(phaseTemplateValues);
        daysOffInPeriodWTATemplate.setRuleTemplateCategoryId(ruleTemplateCategory.getId());
        wtaBaseRuleTemplates1.add(daysOffInPeriodWTATemplate);

        AverageScheduledTimeWTATemplate averageScheduledTimeWTATemplate = new AverageScheduledTimeWTATemplate("Maximum average duration per week in an interval", false, "Maximum average duration per week in an interval", 1,WEEKS);
        averageScheduledTimeWTATemplate.setCountryId(countryDTO.getId());
        averageScheduledTimeWTATemplate.setPhaseTemplateValues(phaseTemplateValues);
        averageScheduledTimeWTATemplate.setRuleTemplateCategoryId(ruleTemplateCategory.getId());
        wtaBaseRuleTemplates1.add(averageScheduledTimeWTATemplate);

        VetoAndStopBricksWTATemplate vetoAndStopBricksWTATemplate = new VetoAndStopBricksWTATemplate("Veto and stop bricks", "Veto and stop bricks",1,LocalDate.now(),null,null);
        vetoAndStopBricksWTATemplate.setCountryId(countryDTO.getId());
        vetoAndStopBricksWTATemplate.setRuleTemplateCategoryId(ruleTemplateCategory.getId());
        wtaBaseRuleTemplates1.add(vetoAndStopBricksWTATemplate);

        NumberOfWeekendShiftsInPeriodWTATemplate numberofWeekendShiftsInPeriodWTATemplate = new NumberOfWeekendShiftsInPeriodWTATemplate("Weekend off Distribution", false, "Weekend off Distribution", TUESDAY, LocalTime.of(10, 30), TUESDAY, LocalTime.of(10, 30));
        numberofWeekendShiftsInPeriodWTATemplate.setCountryId(countryDTO.getId());
        numberofWeekendShiftsInPeriodWTATemplate.setPhaseTemplateValues(phaseTemplateValues);
        numberofWeekendShiftsInPeriodWTATemplate.setRuleTemplateCategoryId(ruleTemplateCategory.getId());
        wtaBaseRuleTemplates1.add(numberofWeekendShiftsInPeriodWTATemplate);

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


        ShortestAndAverageDailyRestWTATemplate shortestAndAverageDailyRestWTATemplate = new ShortestAndAverageDailyRestWTATemplate("Shortest and Average daily Rest", false, "Shortest and Average daily Rest", 1, WEEKS);
        shortestAndAverageDailyRestWTATemplate.setCountryId(countryDTO.getId());
        shortestAndAverageDailyRestWTATemplate.setPhaseTemplateValues(phaseTemplateValues);
        shortestAndAverageDailyRestWTATemplate.setRuleTemplateCategoryId(ruleTemplateCategory.getId());
        wtaBaseRuleTemplates1.add(shortestAndAverageDailyRestWTATemplate);

        TimeBankWTATemplate timeBankWTATemplate = new TimeBankWTATemplate("Maximum Time Bank", false, "Maximum Time Bank");
        timeBankWTATemplate.setCountryId(countryDTO.getId());
        timeBankWTATemplate.setPhaseTemplateValues(phaseTemplateValues);
        timeBankWTATemplate.setRuleTemplateCategoryId(ruleTemplateCategory.getId());
        wtaBaseRuleTemplates1.add(timeBankWTATemplate);

        SeniorDaysPerYearWTATemplate seniorDaysPerYearWTATemplate = new SeniorDaysPerYearWTATemplate("Senior Days per Year",  false, "Senior Days per Year", Arrays.asList(range));
        seniorDaysPerYearWTATemplate.setCountryId(countryDTO.getId());
        seniorDaysPerYearWTATemplate.setPhaseTemplateValues(phaseTemplateValues);
        seniorDaysPerYearWTATemplate.setRuleTemplateCategoryId(ruleTemplateCategory.getId());
        wtaBaseRuleTemplates1.add(seniorDaysPerYearWTATemplate);

        ChildCareDaysCheckWTATemplate careDaysCheck = new ChildCareDaysCheckWTATemplate("Child Care Days Check", false, "Child Care Days Check");
        careDaysCheck.setCountryId(countryDTO.getId());
        careDaysCheck.setRuleTemplateCategoryId(ruleTemplateCategory.getId());
        wtaBaseRuleTemplates1.add(careDaysCheck);

        DaysOffAfterASeriesWTATemplate daysOffAfterASeriesWTATemplate = new DaysOffAfterASeriesWTATemplate("Night worker - Minimum days off after a series of night shifts in sequence", false, "Night worker - Minimum days off after a series of night shifts in sequence", 1, WEEKS, 1);
        daysOffAfterASeriesWTATemplate.setPhaseTemplateValues(phaseTemplateValues);
        daysOffAfterASeriesWTATemplate.setCountryId(countryDTO.getId());
        daysOffAfterASeriesWTATemplate.setRuleTemplateCategoryId(ruleTemplateCategory.getId());
        wtaBaseRuleTemplates1.add(daysOffAfterASeriesWTATemplate);

        NoOfSequenceShiftWTATemplate noOfSequenceShiftWTATemplate = new NoOfSequenceShiftWTATemplate("Number of Sequence Shift", false, "Number of Sequence Shift", PartOfDay.DAY, PartOfDay.NIGHT);
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
        WTAForCareDays careDays = new WTAForCareDays("WTA For Care Days","WTA For Care Days");
        wtaBaseRuleTemplates1.add(careDays);
        wtaBaseRuleTemplateMongoRepository.saveEntities(wtaBaseRuleTemplates1);


        return true;
    }

    public RuleTemplateWrapper getRuleTemplate(long countryId) {

        List<RuleTemplateCategoryTagDTO> categoryList = ruleTemplateCategoryMongoRepository.findAllUsingCountryId(countryId);

        if (categoryList == null) {
            exceptionService.dataNotFoundByIdException(MESSAGE_CATEGORY_NULL_LIST);
        }

        List<WTABaseRuleTemplate> templateList = wtaBaseRuleTemplateMongoRepository.getWTABaseRuleTemplateByCountryId(countryId);
        if (templateList == null) {
            exceptionService.dataNotFoundByIdException(MESSAGE_WTA_BASE_RULE_TEMPLATE_NULL_LIST);
        }

        //
        List<WTABaseRuleTemplateDTO> wtaBaseRuleTemplateDTOS = copyRuleTemplatesToDTO(templateList);
        assignCategoryToRuleTemplate(countryId, wtaBaseRuleTemplateDTOS);
        RuleTemplateWrapper wrapper = new RuleTemplateWrapper();
        wrapper.setCategoryList(categoryList);
        wrapper.setTemplateList(wtaBaseRuleTemplateDTOS);
        return wrapper;
    }

    public RuleTemplateWrapper getRulesTemplateCategoryByUnit(Long unitId) {
        OrganizationDTO organization = userIntegrationService.getOrganizationWithCountryId(unitId);
        if (!Optional.ofNullable(organization).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_ORGANIZATION_ID);
        }
        List<WTABaseRuleTemplate> templateList = wtaBaseRuleTemplateMongoRepository.getWTABaseRuleTemplateByCountryId(organization.getCountryId());
        List<WTABaseRuleTemplateDTO> wtaBaseRuleTemplateDTOS = copyRuleTemplatesToDTO(templateList);
        RuleTemplateWrapper ruleTemplateWrapper = new RuleTemplateWrapper();
        assignCategoryToRuleTemplate(organization.getCountryId(), wtaBaseRuleTemplateDTOS);
        List<RuleTemplateCategoryTagDTO> categoryList = ruleTemplateCategoryMongoRepository.findAllUsingCountryId(organization.getCountryId());
        ruleTemplateWrapper.setCategoryList(categoryList);
        ruleTemplateWrapper.setTemplateList(wtaBaseRuleTemplateDTOS);
        return ruleTemplateWrapper;

    }

    public void assignCategoryToRuleTemplate(Long countryId,List<WTABaseRuleTemplateDTO> templateList) {
        List<RuleTemplateCategoryTagDTO> categoryList = ruleTemplateCategoryMongoRepository.findAllUsingCountryId(countryId);
        if (categoryList == null) {
            exceptionService.dataNotFoundByIdException(MESSAGE_CATEGORY_NULL_LIST);
        }
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
        CountryDTO country = userIntegrationService.getCountryById(countryId);
        if (!Optional.ofNullable(country).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_COUNTRY_ID, countryId);
        }
        WTABaseRuleTemplate oldTemplate = wtaBaseRuleTemplateMongoRepository.findOne(ruleTemplateId);
        if (!Optional.ofNullable(oldTemplate).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_WTA_BASE_RULE_TEMPLATE_ID, templateDTO.getId());
        }
        oldTemplate = WTABuilderService.copyRuleTemplate(templateDTO, false);
        CurrentUserDetails currentUserDetails = UserContext.getUserDetails();
        oldTemplate.setLastUpdatedBy(currentUserDetails.getFirstName());
        oldTemplate.setRuleTemplateCategoryId(templateDTO.getRuleTemplateCategory().getId());
        oldTemplate.setCountryId(countryId);
        wtaBaseRuleTemplateMongoRepository.save(oldTemplate);
        return templateDTO;
    }


    public WTABaseRuleTemplateDTO copyRuleTemplate(Long countryId, WTABaseRuleTemplateDTO wtaRuleTemplateDTO) {
        CountryDTO country = userIntegrationService.getCountryById(countryId);
        if (!Optional.ofNullable(country).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_COUNTRY_ID, countryId);
        }
        RuleTemplateCategory ruleTemplateCategory = ruleTemplateCategoryMongoRepository.findByName(countryId, wtaRuleTemplateDTO.getRuleTemplateCategory().getName(), RuleTemplateCategoryType.WTA);
        if (!Optional.ofNullable(ruleTemplateCategory).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_CATEGORY_NOT_MATCHED);
        }

        WTABaseRuleTemplate wtaBaseRuleTemplate1 = wtaBaseRuleTemplateMongoRepository.existsByName(countryId, wtaRuleTemplateDTO.getName().trim());
        if (Optional.ofNullable(wtaBaseRuleTemplate1).isPresent()) {
            exceptionService.duplicateDataException(MESSAGE_WTA_BASE_RULE_TEMPLATE_NAME_ALREADYEXIST, wtaRuleTemplateDTO.getName());

        }
        WTABaseRuleTemplate wtaBaseRuleTemplate = WTABuilderService.copyRuleTemplate(wtaRuleTemplateDTO, true);
        wtaBaseRuleTemplate.setCountryId(countryId);
        wtaBaseRuleTemplate.setRuleTemplateCategoryId(ruleTemplateCategory.getId());
        wtaBaseRuleTemplateMongoRepository.save(wtaBaseRuleTemplate);
        wtaRuleTemplateDTO.setId(wtaBaseRuleTemplate.getId());
        wtaRuleTemplateDTO.setRuleTemplateCategory(wtaRuleTemplateDTO.getRuleTemplateCategory());
        return wtaRuleTemplateDTO;

    }
}

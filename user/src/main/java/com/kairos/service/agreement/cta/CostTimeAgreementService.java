package com.kairos.service.agreement.cta;

import com.kairos.config.listener.ApplicationContextProviderNonManageBean;
import com.kairos.enums.FixedValueType;
import com.kairos.persistence.model.agreement.cta.*;
import com.kairos.persistence.model.agreement.cta.cta_response.CTARuleTemplateCategoryWrapper;
import com.kairos.persistence.model.agreement.cta.cta_response.CollectiveTimeAgreementDTO;
import com.kairos.persistence.model.agreement.wta.templates.RuleTemplateCategory;
import com.kairos.persistence.model.auth.User;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.country.Currency;
import com.kairos.persistence.model.country.employment_type.EmploymentType;
import com.kairos.persistence.model.organization.OrganizationType;
import com.kairos.persistence.model.user.expertise.Expertise;
import com.kairos.persistence.model.user.expertise.Response.ExpertiseTagDTO;
import com.kairos.persistence.model.user.unit_position.UnitPosition;
import com.kairos.persistence.model.user.unit_position.UnitPositionQueryResult;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.organization.OrganizationTypeGraphRepository;
import com.kairos.persistence.repository.user.access_permission.AccessGroupRepository;
import com.kairos.persistence.repository.user.agreement.cta.CTARuleTemplateGraphRepository;
import com.kairos.persistence.repository.user.agreement.cta.CollectiveTimeAgreementGraphRepository;
import com.kairos.persistence.repository.user.agreement.wta.RuleTemplateCategoryGraphRepository;
import com.kairos.persistence.repository.user.auth.UserGraphRepository;
import com.kairos.persistence.repository.user.country.*;
import com.kairos.persistence.repository.user.expertise.ExpertiseGraphRepository;
import com.kairos.persistence.repository.user.unit_position.UnitPositionGraphRepository;
import com.kairos.rest_client.activity_types.ActivityTypesRestClient;
import com.kairos.service.AsynchronousService;
import com.kairos.service.UserBaseService;
import com.kairos.service.auth.UserService;
import com.kairos.service.country.CurrencyService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.organization.OrganizationService;
import com.kairos.service.unit_position.UnitPositionService;
import com.kairos.util.userContext.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static javax.management.timer.Timer.ONE_DAY;

@Transactional
@Service
public class CostTimeAgreementService extends UserBaseService {
    private final Logger logger = LoggerFactory.getLogger(CostTimeAgreementService.class);


    private @Inject
    UserService userService;
    private @Inject
    RuleTemplateCategoryGraphRepository ruleTemplateCategoryGraphRepository;
    private @Inject
    CountryGraphRepository countryGraphRepository;
    private @Inject
    CTARuleTemplateGraphRepository ctaRuleTemplateGraphRepository;
    private @Inject
    AsynchronousService asynchronousService;
    private @Inject
    DayTypeGraphRepository dayTypeGraphRepository;
    private @Inject
    EmploymentTypeGraphRepository employmentTypeGraphRepository;
    private @Inject
    AccessGroupRepository accessGroupRepository;
    // private @Inject TimeTypeGraphRepository timeTypeGraphRepository;
    private @Inject
    UserGraphRepository userGraphRepository;
    private @Inject
    CurrencyService currencyService;
    private @Inject
    ExpertiseGraphRepository expertiseGraphRepository;
    private @Inject
    OrganizationTypeGraphRepository organizationTypeGraphRepository;
    private @Inject
    CurrencyGraphRepository currencyGraphRepository;
    private @Inject
    CountryHolidayCalenderGraphRepository countryHolidayCalenderGraphRepository;
    private @Inject
    CollectiveTimeAgreementGraphRepository collectiveTimeAgreementGraphRepository;
    private @Inject
    OrganizationGraphRepository organizationGraphRepository;
    private @Inject
    OrganizationTypeGraphRepository organizationTypeRepository;
    private @Inject
    OrganizationService organizationService;
    private @Inject
    ActivityTypesRestClient activityTypesRestClient;
    private @Inject
    UnitPositionGraphRepository unitPositionGraphRepository;
    private @Inject
    UnitPositionService unitPositionService;
    private @Inject
    ExceptionService exceptionService;
    private @Inject
    CountryCTAService countryCTAService;

    public boolean isDefaultCTARuleTemplateExists() {
        return ctaRuleTemplateGraphRepository.isDefaultCTARuleTemplateExists();
    }

    public void createDefaultCtaRuleTemplate(Long countryId) {
        RuleTemplateCategory category = ruleTemplateCategoryGraphRepository
                .findByName(countryId, "NONE", RuleTemplateCategoryType.CTA);
        Currency currency = currencyService.getCurrencyByCountryId(countryId);
        List<RuleTemplate> ctaRuleTemplates = new ArrayList<>();
        if (category != null) {
            Arrays.stream(CTARuleTemplateType.values()).forEach(cTARuleTemplate -> {
                CTARuleTemplate ctaRuleTemplate = createDefaultRuleTemplate(cTARuleTemplate.toString(), currency);
                category.addRuleTemplate(ctaRuleTemplate);
                ctaRuleTemplates.add(ctaRuleTemplate);
            });
            Country country = countryGraphRepository.findOne(countryId);
            country.setCtaRuleTemplates(ctaRuleTemplates);
            countryGraphRepository.save(country);
            this.save(category);
        } else {
            logger.info("default CTARuleTemplateCategory is not exist");
        }

    }

    public CTARuleTemplateDTO createCTARuleTemplate(Long countryId, CTARuleTemplateDTO ctaRuleTemplateDTO) throws ExecutionException, InterruptedException {
        if (ctaRuleTemplateGraphRepository.isCTARuleTemplateExistWithSameName(countryId, ctaRuleTemplateDTO.getName())) {
            exceptionService.dataNotFoundByIdException("message.cta.ruleTemplate.alreadyExist", ctaRuleTemplateDTO.getName());

        }
        // Set id null as new entry should be created
        ctaRuleTemplateDTO.setId(null);
        CTARuleTemplate ctaRuleTemplate = new CTARuleTemplate();
        Long userId = UserContext.getUserDetails().getId();
        User user = userGraphRepository.findOne(userId, 0);
        // While updating rule template, do not update template type
        ctaRuleTemplateDTO.setRuleTemplateType(ctaRuleTemplateDTO.getName());
        this.buildCTARuleTemplate(ctaRuleTemplate, ctaRuleTemplateDTO, false);
        ctaRuleTemplate.setLastModifiedBy(user);
        this.save(ctaRuleTemplate);
        ctaRuleTemplateDTO.setId(ctaRuleTemplate.getId());
        ctaRuleTemplateGraphRepository.addCTARuleTemplateInCountry(countryId, ctaRuleTemplate.getId());

        return ctaRuleTemplateDTO;
    }

    private CTARuleTemplate createDefaultRuleTemplate(String ctaRuleTemplateType, Currency currency) {
        CTARuleTemplate ctaRuleTemplate = null;
        switch (ctaRuleTemplateType) {
            case "RULE_TEMPLATE_1":
                ctaRuleTemplate = new CTARuleTemplate("Working Evening Shifts",
                        "CTA rule for evening shift, from 17-23 o'clock.  For this organization/unit this is payroll type '210:  Evening compensation'",
                        "210:  Evening compensation", "xyz");
                break;
            case "RULE_TEMPLATE_2":

                ctaRuleTemplate = new CTARuleTemplate("Working Night Shifts",
                        "CTA rule for night shift, from 23-07 o. clock.  For this organization/unit this is payroll type “212:  Night compensation”",
                        "212:  Night compensation", "xyz");
                break;
            case "RULE_TEMPLATE_3":

                ctaRuleTemplate = new CTARuleTemplate("Working On a Saturday",
                        "CTA rule for Saturdays shift, from 08-24 o. clock. For this organization/unit this is payroll type " +
                                "“214:  Saturday compensation”. If you are working from 00-07 on Saturday, you only gets evening " +
                                "compensation",
                        "214:  Saturday compensation", "xyz");
                break;
            case "RULE_TEMPLATE_4":
                ctaRuleTemplate = new CTARuleTemplate("Working On a Sunday",
                        "CTA rule for Saturdays shift, from 00-24 o. clock. For this organization/unit this is " +
                                "payroll type “214:Saturday compensation”.All working time on Sundays gives compensation"
                        ,
                        "214:Saturday compensation", "xyz");
                break;
            case "RULE_TEMPLATE_5":
                ctaRuleTemplate = new CTARuleTemplate("Working On a Full Public Holiday",
                        "CTA rule for full public holiday shift, from 00-24 o. clock.  For this organization/unit this is " +
                                "payroll type “216:  public holiday compensation”. All working time on full PH gives " +
                                "compensation",
                        "216:public holiday compensation", "xyz");
                break;
            case "RULE_TEMPLATE_6":
                ctaRuleTemplate = new CTARuleTemplate("Working On a Half Public Holiday",
                        "CTA rule for full public holiday shift, from 12-24 o. clock. For this organization/unit" +
                                " this is payroll type “218:  half public holiday compensation”.All working time on " +
                                "half PH gives compensation",
                        "218: half public holiday compensation", "xyz");
                break;
            case "RULE_TEMPLATE_7":
                ctaRuleTemplate = new CTARuleTemplate("Working Overtime",
                        "CTA rule for overtime shift, from 00-24 o. clock.  For this organization/unit this is payroll type “230: " +
                                " 50% overtime compensation”.",
                        "230:50% overtime compensation", "xyz");
                break;
            case "RULE_TEMPLATE_8":
                ctaRuleTemplate = new CTARuleTemplate("Working Extratime",
                        "CTA rule for extra time shift, from 00-24 o. clock.  For this organization/unit this is payroll type" +
                                " “250:  extratime compensation”. ",
                        "250:  extratime compensation", "xyz");
                break;
            case "RULE_TEMPLATE_9":
                ctaRuleTemplate = new CTARuleTemplate("Late Notice Compensation",
                        "CTA rule for late notification on changes to working times.  If notice of change is done within 72 hours" +
                                " before start of working day, then staff is entitled to at compensation of 105 kroner",
                        "", "xyz");
                break;
            case "RULE_TEMPLATE_10":
                ctaRuleTemplate = new CTARuleTemplate("Extra Dutyfree Day For Each Public Holiday",
                        "CTA rule for each public holiday.  Whenever there is a public holiday staff are entitled to an" +
                                " extra day off, within 3 month or just compensated in the timebank.",
                        "", "xyz");
                break;
            default:
                exceptionService.illegalArgumentException("message.InvalidTemplateType");


        }
        ctaRuleTemplate.setCalculationUnit(CalculationUnit.HOURS);
        CompensationTable compensationTable = new CompensationTable(10);
        ctaRuleTemplate.setCompensationTable(compensationTable);
        FixedValue fixedValue = new FixedValue(10, currency, FixedValueType.PER_ACTIVITY);
        ctaRuleTemplate.setCalculateValueAgainst(new CalculateValueAgainst(CalculateValueAgainst.CalculateValueType.FIXED_VALUE, 10.5f, fixedValue));
        ctaRuleTemplate.setApprovalWorkFlow(ApprovalWorkFlow.NO_APPROVAL_NEEDED);
        ctaRuleTemplate.setBudgetType(BudgetType.ACTIVITY_COST);
        ctaRuleTemplate.setActivityTypeForCostCalculation(ActivityTypeForCostCalculation.SELECTED_ACTIVITY_TYPE);
        ctaRuleTemplate.setPlanningCategory(PlanningCategory.DEVIATION_FROM_PLANNED);
        //ctaRuleTemplate.setStaffFunctions(Stream.of(StaffFunction.TRAINING_COORDINATOR).collect(Collectors.toList()));
        ctaRuleTemplate.setPlannedTimeWithFactor(PlannedTimeWithFactor.buildPlannedTimeWithFactor(10, true, AccountType.DUTYTIME_ACCOUNT));
        return ctaRuleTemplate;


    }

    public CTARuleTemplateCategoryWrapper loadAllCTARuleTemplateByCountry(Long countryId) {
        Country country = countryGraphRepository.findOne(countryId);
        List<RuleTemplateCategory> ruleTemplateCategories = country.getRuleTemplateCategories();
        List<RuleTemplateCategory> ctaRuleTemplateCategoryList = ruleTemplateCategories.parallelStream().filter(
                ruleTemplateCategory -> RuleTemplateCategoryType.CTA.equals(ruleTemplateCategory.getRuleTemplateCategoryType()) && !ruleTemplateCategory.isDeleted())
                .collect(Collectors.toList());
        List<Long> ruleTemplateCategoryIds = ctaRuleTemplateCategoryList.parallelStream().map(RuleTemplateCategory::getId)
                .collect(Collectors.toList());

        List<CTARuleTemplateQueryResult> ruleTemplates = ctaRuleTemplateGraphRepository.findByRuleTemplateCategoryIdInAndCountryAndDeletedFalse(ruleTemplateCategoryIds, countryId);
        CTARuleTemplateCategoryWrapper ctaRuleTemplateCategoryWrapper = new CTARuleTemplateCategoryWrapper();
        ctaRuleTemplateCategoryWrapper.getRuleTemplateCategories().addAll(ctaRuleTemplateCategoryList);
        ctaRuleTemplateCategoryWrapper.setRuleTemplates(ruleTemplates);
        return ctaRuleTemplateCategoryWrapper;
    }

    public CTARuleTemplateCategoryWrapper loadAllCTARuleTemplateByUnit(Long unitId) {
        Long countryId = organizationService.getCountryIdOfOrganization(unitId);
        return loadAllCTARuleTemplateByCountry(countryId);
    }

    private CTARuleTemplate buildCTARuleTemplate(CTARuleTemplate ctaRuleTemplate, CTARuleTemplateDTO ctaRuleTemplateDTO, Boolean doUpdate) throws ExecutionException, InterruptedException {

        BeanUtils.copyProperties(ctaRuleTemplateDTO, ctaRuleTemplate, "calculateOnDayTypes");

        CompletableFuture<Boolean> hasUpdated = ApplicationContextProviderNonManageBean.getApplicationContext().getBean(CostTimeAgreementService.class)
                .buildEmploymentTypeAndAccessGroups(ctaRuleTemplate, ctaRuleTemplateDTO);
        //Load reference only
        RuleTemplateCategory ruleTemplateCategory =
                ruleTemplateCategoryGraphRepository.findOne(ctaRuleTemplateDTO.getRuleTemplateCategory(), 0);

        if (doUpdate && !ctaRuleTemplate.getRuleTemplateCategory().getId().equals(ctaRuleTemplateDTO.getRuleTemplateCategory())) {
            // Detach rule template from older category If category has been updated
            ruleTemplateCategoryGraphRepository.detachRuleTemplateCategoryFromCTARuleTemplate(ctaRuleTemplate.getId(), ctaRuleTemplate.getRuleTemplateCategory().getId());
        }
        ctaRuleTemplate.setRuleTemplateCategory(ruleTemplateCategory);

        setActivityBasesCostCalculationSettings(ctaRuleTemplate);
        if (ctaRuleTemplate.getCalculateValueAgainst() != null && ctaRuleTemplate.getCalculateValueAgainst().getCalculateValue() != null) {
            switch (ctaRuleTemplate.getCalculateValueAgainst().getCalculateValue().toString()) {
                case "FIXED_VALUE": {
                    if (doUpdate && ctaRuleTemplate.getCalculateValueAgainst().getFixedValue().getCurrencyId() != null) {
                        Currency currency = currencyGraphRepository.findOne(ctaRuleTemplate.getCalculateValueAgainst().getFixedValue().getCurrencyId());
                        ctaRuleTemplate.getCalculateValueAgainst().getFixedValue().setCurrency(currency);
                    }
                    break;
                }

                case "WEEKLY_HOURS": {
                    ctaRuleTemplate.getCalculateValueAgainst().setScale(ctaRuleTemplate.getCalculateValueAgainst().getScale());
                    break;
                }

                case "WEEKLY_SALARY":
                    ctaRuleTemplate.getCalculateValueAgainst().setScale(ctaRuleTemplate.getCalculateValueAgainst().getScale());
                    break;
            }
        }
        ctaRuleTemplate.getCalculateValueAgainst().setCalculateValue(ctaRuleTemplateDTO.getCalculateValueAgainst().getCalculateValue());
//        ctaRuleTemplate.set
        logger.info("ctaRuleTemplate.getCalculateValueAgainst().getScale : {}", ctaRuleTemplate.getCalculateValueAgainst().getScale());
        // Wait until they are all done
        CompletableFuture.allOf(hasUpdated).join();

        return ctaRuleTemplate;
    }

    @Async
    public CompletableFuture<Boolean> buildEmploymentTypeAndAccessGroups
            (CTARuleTemplate ctaRuleTemplate, CTARuleTemplateDTO ctaRuleTemplateDTO)
            throws InterruptedException, ExecutionException {

        /*Callable<List<TimeType>> timeTypesTask = () -> {
            Iterable<TimeType> timeTypes = timeTypeGraphRepository.findAllById(ctaRuleTemplateDTO.getTimeTypes(), 0);
            return StreamSupport.stream(timeTypes.spliterator(), true).collect(Collectors.toList());
        };

        Future<List<TimeType>> timeTypesFuture = asynchronousService.executeAsynchronously(timeTypesTask);*/

        Callable<List<EmploymentType>> employmentTypesTask = () -> {
            Iterable<EmploymentType> employmentTypes = employmentTypeGraphRepository.findAllById(ctaRuleTemplateDTO.getEmploymentTypes(), 0);
            return StreamSupport.stream(employmentTypes.spliterator(), true).collect(Collectors.toList());
        };

        Future<List<EmploymentType>> employmentTypesFuture = asynchronousService.executeAsynchronously(employmentTypesTask);

        /*Callable<List<AccessGroup>> accessGroupsTask = () -> {
            Iterable<AccessGroup> accessGroups = accessGroupRepository.findAllById(ctaRuleTemplateDTO.getCalculateValueIfPlanned(), 0);
            return StreamSupport.stream(accessGroups.spliterator(), true).collect(Collectors.toList());

        };
        Future<List<AccessGroup>> accessGroupsFuture = asynchronousService.executeAsynchronously(accessGroupsTask);
        */
        //set data
//        ctaRuleTemplate.setTimeTypes(timeTypesFuture.get());
        ctaRuleTemplate.setEmploymentTypes(employmentTypesFuture.get());
//        ctaRuleTemplate.setCalculateValueIfPlanned(accessGroupsFuture.get());
        return CompletableFuture.completedFuture(true);
    }


    public void saveInterval() {
        CompensationTableInterval tableInterval = new CompensationTableInterval();
        tableInterval.setValue(2.3F);
        this.save(tableInterval);
    }

    public Boolean deleteCostTimeAgreement(Long countryId, Long ctaId) {
        CostTimeAgreement costTimeAgreement = collectiveTimeAgreementGraphRepository.findCTAByCountryAndIdAndDeleted(countryId, ctaId, false);
        if (costTimeAgreement == null) {
            exceptionService.dataNotFoundByIdException("message.cta.id.notFound", ctaId);
        }
        costTimeAgreement.setDeleted(true);
        this.save(costTimeAgreement);
        return true;
    }

    @Async
    public CompletableFuture<Boolean> buildCTAToCopy(CostTimeAgreement costTimeAgreementToBeCreated, CostTimeAgreement oldCTA)
            throws InterruptedException, ExecutionException {

        // Get Experties
        Callable<Optional<Expertise>> expertiseCallable = () -> expertiseGraphRepository.findById(oldCTA.getExpertise().getId());

        Future<Optional<Expertise>> expertiseFuture = asynchronousService.executeAsynchronously(expertiseCallable);

        // Get Rule Templates
        Callable<List<RuleTemplate>> ctaRuleTemplatesCallable = () -> {
            List<RuleTemplate> ruleTemplates = new ArrayList<>();
            // TODO need to fetch rule templates
            for (RuleTemplate ruleTemplate : oldCTA.getRuleTemplates()) {
                CTARuleTemplate ctaRuleTemplate = new CTARuleTemplate();
                BeanUtils.copyProperties(ruleTemplate, ctaRuleTemplate);
                ctaRuleTemplate.cloneCTARuleTemplate();
                setActivityBasesCostCalculationSettings(ctaRuleTemplate);
                BeanUtils.copyProperties(ctaRuleTemplate, ruleTemplate, "createdBy");
                ruleTemplates.add(ctaRuleTemplate);
            }
            return ruleTemplates;
        };
        Future<List<RuleTemplate>> ctaRuleTemplatesFuture = asynchronousService.executeAsynchronously(ctaRuleTemplatesCallable);

        // Get Organization Type
        Callable<Optional<OrganizationType>> OrganizationTypesListCallable = () -> {
            return organizationTypeGraphRepository.findById(oldCTA.getOrganizationType().getId());
        };
        Future<Optional<OrganizationType>> organizationTypesFuture = asynchronousService.executeAsynchronously(OrganizationTypesListCallable);


        // Get Organization Sub Type
        Callable<Optional<OrganizationType>> OrganizationSubTypesListCallable = () -> {
            Optional<OrganizationType> organizationType = organizationTypeGraphRepository.findById(oldCTA.getOrganizationSubType().getId());
            return organizationType;
        };
        Future<Optional<OrganizationType>> organizationSubTypesFuture = asynchronousService.executeAsynchronously(OrganizationSubTypesListCallable);


        //set data
        if (expertiseFuture.get().isPresent())
            costTimeAgreementToBeCreated.setExpertise(expertiseFuture.get().get());
        costTimeAgreementToBeCreated.setRuleTemplates(ctaRuleTemplatesFuture.get());
        costTimeAgreementToBeCreated.setOrganizationType(organizationTypesFuture.get().get());
        costTimeAgreementToBeCreated.setOrganizationSubType(organizationSubTypesFuture.get().get());
        costTimeAgreementToBeCreated.setStartDateMillis(oldCTA.getStartDateMillis());
        costTimeAgreementToBeCreated.setEndDateMillis(oldCTA.getEndDateMillis());

        return CompletableFuture.completedFuture(true);
    }

    public CostTimeAgreement createCopyOfCTA(Long ctaId) throws InterruptedException, ExecutionException {
        CostTimeAgreement costTimeAgreement = collectiveTimeAgreementGraphRepository.findOne(ctaId, 2);
        CostTimeAgreement newCostTimeAgreement = new CostTimeAgreement();
        BeanUtils.copyProperties(costTimeAgreement, newCostTimeAgreement, "createdBy");
        // In case of copy CTA need to remove ID of CTA
        newCostTimeAgreement.setId(null);
        CompletableFuture<Boolean> hasUpdated = ApplicationContextProviderNonManageBean.getApplicationContext().getBean(CostTimeAgreementService.class)
                .buildCTAToCopy(newCostTimeAgreement, costTimeAgreement);

        // Wait until they are all done
        CompletableFuture.allOf(hasUpdated).join();
//        newCostTimeAgreement.setCountry(null);
        this.save(newCostTimeAgreement);
        return newCostTimeAgreement;
    }


    public List<CTAListQueryResult> loadAllCTAByCountry(Long countryId) {
//        Country country = countryGraphRepository.findOne(countryId);
        return collectiveTimeAgreementGraphRepository.findCTAByCountryId(countryId);
    }

    public List<CTAListQueryResult> loadAllCTAByUnit(Long unitId) {
//        Country country = countryGraphRepository.findOne(countryId);
        return collectiveTimeAgreementGraphRepository.findCTAByUnitId(unitId);
    }

    public CTARuleTemplate saveEmbeddedEntitiesOfCTARuleTemplate(CTARuleTemplate ctaRuleTemplate, CTARuleTemplateDTO ctaRuleTemplateDTO) {
        if (ctaRuleTemplate.getId() != null) {
            ctaRuleTemplateGraphRepository.detachAllTimeTypesFromCTARuleTemplate(ctaRuleTemplate.getId());
            ctaRuleTemplateGraphRepository.detachAllTimeTypesFromCTARuleTemplate(ctaRuleTemplate.getId());
        }

        // Fetch Employment Type
        List<Long> employmentTypeIds = ctaRuleTemplateDTO.getEmploymentTypes();
        ctaRuleTemplate.setEmploymentTypes(employmentTypeGraphRepository.getEmploymentTypeByIds(employmentTypeIds, false));

        // Fetch Time Type

        /*List<Long> timeTypeIds = ctaRuleTemplateDTO.getTimeTypes();
        ctaRuleTemplate.setTimeTypes(timeTypeGraphRepository.findTimeTypeByIds(timeTypeIds));
*/
        Long ruleTemplateId = ctaRuleTemplateDTO.getRuleTemplateCategory();
        if (ruleTemplateId != null) {
            ctaRuleTemplate.setRuleTemplateCategory(ruleTemplateCategoryGraphRepository.findOne(ruleTemplateId));
        }

        return ctaRuleTemplate;
    }


    @Async
    private CompletableFuture<Boolean> buildCTA(CostTimeAgreement costTimeAgreement, CollectiveTimeAgreementDTO collectiveTimeAgreementDTO, Boolean doUpdate, List<Long> ruleTemplateIds, Expertise expertise)
            throws InterruptedException, ExecutionException {


        // Get Rule Templates
        Callable<List<RuleTemplate>> ctaRuleTemplatesCallable = () -> {
            List<RuleTemplate> ruleTemplates = new ArrayList<>();
            for (CTARuleTemplateDTO ctaRuleTemplateDTO : collectiveTimeAgreementDTO.getRuleTemplates()) {
                CTARuleTemplate ctaRuleTemplate = new CTARuleTemplate();
                BeanUtils.copyProperties(ctaRuleTemplateDTO, ctaRuleTemplate);
                // Check if cta_response exists with same rule template Id
                if (!doUpdate || (doUpdate && !ruleTemplateIds.contains(ctaRuleTemplate.getId()))) {
                    ctaRuleTemplate.cloneCTARuleTemplate();
//                    ctaRuleTemplate = saveEmbeddedEntitiesOfCTARuleTemplate(ctaRuleTemplate, ctaRuleTemplateDTO);
                }
                setActivityBasesCostCalculationSettings(ctaRuleTemplate);
                ctaRuleTemplate = saveEmbeddedEntitiesOfCTARuleTemplate(ctaRuleTemplate, ctaRuleTemplateDTO);
//                BeanUtils.copyProperties(ctaRuleTemplate,ctaRuleTemplateDTO,"timeTypes");
                ruleTemplates.add(ctaRuleTemplate);
            }
            return ruleTemplates;
        };
        Future<List<RuleTemplate>> ctaRuleTemplatesFuture = asynchronousService.executeAsynchronously(ctaRuleTemplatesCallable);

        //set data
        if (!doUpdate) {
            costTimeAgreement.setExpertise(expertise);
        }
//        if(organizationTypesFuture.get().isPresent())
//            costTimeAgreement.setOrganizationType(organizationTypesFuture.get().get());
//        if(organizationSubTypesFuture.get().isPresent())
//            costTimeAgreement.setOrganizationSubType(organizationSubTypesFuture.get().get());
        costTimeAgreement.setRuleTemplates(ctaRuleTemplatesFuture.get());
        costTimeAgreement.setStartDateMillis(collectiveTimeAgreementDTO.getStartDateMillis());
        costTimeAgreement.setEndDateMillis(collectiveTimeAgreementDTO.getEndDateMillis());

        return CompletableFuture.completedFuture(true);
    }

    private void setActivityBasesCostCalculationSettings(CTARuleTemplate ctaRuleTemplate) {

        switch (ctaRuleTemplate.getActivityTypeForCostCalculation()) {
            case TIME_TYPE_ACTIVITY:
                ctaRuleTemplate.setActivityIds(new ArrayList<>());
                break;
            default:
                ctaRuleTemplate.setPlannedTimeIds(null);
                ctaRuleTemplate.setTimeTypeIds(null);
                break;
        }
    }


    public CollectiveTimeAgreementDTO updateCostTimeAgreementForOrg(Long ctaId, Long orgId, CollectiveTimeAgreementDTO collectiveTimeAgreementDTO) throws ExecutionException, InterruptedException {
        Boolean cTALinkedWithCountry = collectiveTimeAgreementGraphRepository.isCTALinkedWithCountry(ctaId);
        CostTimeAgreement costTimeAgreement = collectiveTimeAgreementGraphRepository.findOne(ctaId);
        CostTimeAgreement newCostTimeAgreement = new CostTimeAgreement();
        if (costTimeAgreement == null) {
            exceptionService.dataNotFoundByIdException("message.cta.id.notFound", ctaId);

        }
        // Clone and create new
        if (cTALinkedWithCountry) {
            // Remove linking of old CTA with org
            collectiveTimeAgreementGraphRepository.detachCTAFromOrganization(orgId, ctaId);
        } else {
            // Set disabled of old CTA as true
            costTimeAgreement.setDisabled(true);
        }


        BeanUtils.copyProperties(collectiveTimeAgreementDTO, newCostTimeAgreement);
        CompletableFuture<Boolean> hasUpdated = ApplicationContextProviderNonManageBean.getApplicationContext().getBean(CostTimeAgreementService.class)
                .buildCTA(costTimeAgreement, collectiveTimeAgreementDTO, true, null,null);

        // Wait until they are all done
        CompletableFuture.allOf(hasUpdated).join();
        // Set Parent CTA in new CTA
        newCostTimeAgreement.setParent(costTimeAgreement);
        this.save(newCostTimeAgreement);
        BeanUtils.copyProperties(costTimeAgreement, collectiveTimeAgreementDTO, "timeTypes");
        return collectiveTimeAgreementDTO;
    }

    public CTARuleTemplateDTO updateCTARuleTemplate(Long countryId, Long id, CTARuleTemplateDTO ctaRuleTemplateDTO) throws ExecutionException, InterruptedException {
        CTARuleTemplate ctaRuleTemplate = ctaRuleTemplateGraphRepository.findOne(id, 3);
        Long userId = UserContext.getUserDetails().getId();
        User user = userGraphRepository.findOne(userId, 0);
        // While updating rule template, do not update template type
        ctaRuleTemplateDTO.setRuleTemplateType(ctaRuleTemplate.getRuleTemplateType());
        this.buildCTARuleTemplate(ctaRuleTemplate, ctaRuleTemplateDTO, true);
        ctaRuleTemplate.setLastModifiedBy(user);
        this.save(ctaRuleTemplate);
        return ctaRuleTemplateDTO;
    }


// TODO REMOVE NOT in USE

//    public CostTimeAgreement updateCostTimeAgreementForOrganization(CostTimeAgreement countryCTA, CollectiveTimeAgreementDTO collectiveTimeAgreementDTO) throws ExecutionException, InterruptedException {
//
//        CostTimeAgreement costTimeAgreement = new CostTimeAgreement();
//        BeanUtils.copyProperties(collectiveTimeAgreementDTO, costTimeAgreement);
//        CompletableFuture<Boolean> hasUpdated = ApplicationContextProviderNonManageBean.getApplicationContext().getBean(CostTimeAgreementService.class)
//                .buildCTA(costTimeAgreement, collectiveTimeAgreementDTO, false, null);
//
//        // Wait until they are all done
//        CompletableFuture.allOf(hasUpdated).join();
//        this.save(costTimeAgreement);
//        return costTimeAgreement;
//    }
//

//    public Boolean publishUpdatedCountryCTAToOrganization(CostTimeAgreement costTimeAgreement, CollectiveTimeAgreementDTO collectiveTimeAgreementDTO) throws ExecutionException, InterruptedException {
//        List<CostTimeAgreement> organizationCTAs = collectiveTimeAgreementGraphRepository.getListOfOrganizationCTAByParentCountryCTA(costTimeAgreement.getId());
//        organizationCTAs.forEach(organizationCTA ->
//        {
//            try {
//                CostTimeAgreement newCostTimeAgreement = createCopyOfCTA(costTimeAgreement.getId());
//                updateCostTimeAgreement(null, null, organizationCTA.getId(), collectiveTimeAgreementDTO);
//                /*organization.getCostTimeAgreements().add(newCostTimeAgreement);
//                newCostTimeAgreement.setParentCountryCTA(costTimeAgreement);
//                save(organization);*/
//            } catch (Exception e) {
//                // Exception occured
//                logger.info("Exception occured on setting cta_response to organization");
//            }
//
//        });
//        return true;
//    }

    public List<ExpertiseTagDTO> getExpertiseForOrgCTA(long unitId) {
        Long countryId = organizationService.getCountryIdOfOrganization(unitId);
        return expertiseGraphRepository.getAllExpertiseWithTagsByCountry(countryId);
    }

    public Long getCTAIdByNameAndCountry(String name, Long countryId) {
        CostTimeAgreement cta = collectiveTimeAgreementGraphRepository.getCTAIdByCountryAndName(countryId, name);
        return (Optional.ofNullable(cta).isPresent()) ? null : cta.getId();
    }

    public CTAListQueryResult getUnitPositionCTA(Long unitId, Long unitEmploymentPositionId) {
        UnitPosition unitPosition = unitPositionGraphRepository.findOne(unitEmploymentPositionId);
        if (!Optional.ofNullable(unitPosition).isPresent() || unitPosition.isDeleted() == true) {
            exceptionService.dataNotFoundByIdException("message.InvalidEmploymentPostionId", unitEmploymentPositionId);

        }
        CTAListQueryResult cta = collectiveTimeAgreementGraphRepository.getCTAByUnitPositionId(unitEmploymentPositionId);
        return cta;
    }

    public UnitPositionQueryResult updateCostTimeAgreementForUnitPosition(Long unitId, Long unitPositionId, Long ctaId, CollectiveTimeAgreementDTO ctaDTO) {
        UnitPosition unitPosition = unitPositionGraphRepository.findOne(unitPositionId);
        if (!Optional.ofNullable(unitPosition).isPresent() || unitPosition.isDeleted() == true) {
            exceptionService.dataNotFoundByIdException("message.InvalidEmploymentPostionId", unitPositionId);

        }

        CostTimeAgreement oldCTA = collectiveTimeAgreementGraphRepository.getLinkedCTAWithUnitPosition(unitPositionId);
        CostTimeAgreement responseCTA;

        if (unitPosition.isPublished()) {
            CostTimeAgreement costTimeAgreement = new CostTimeAgreement();
            ctaDTO.setId(null);
            BeanUtils.copyProperties(ctaDTO, costTimeAgreement);
            costTimeAgreement.setId(null);
            copyRules(costTimeAgreement, ctaDTO, null);
            collectiveTimeAgreementGraphRepository.detachOldCTAFromUnitPosition(unitPositionId);
            oldCTA.setDisabled(true);
            oldCTA.setEndDateMillis(ctaDTO.getStartDateMillis() - ONE_DAY);
            costTimeAgreement.setDisabled(false);
            costTimeAgreement.setParent(oldCTA);
            costTimeAgreement.setExpertise(unitPosition.getExpertise());
            unitPosition.setCta(costTimeAgreement);
            unitPositionService.save(unitPosition);
            responseCTA = new CostTimeAgreement(costTimeAgreement.getId(), costTimeAgreement.getName(), oldCTA.getExpertise(), costTimeAgreement.getRuleTemplates(), costTimeAgreement.getStartDateMillis(), costTimeAgreement.getEndDateMillis(), false);
        } else {

            List<Long> ruleTemplateIds = null;
            if (Optional.ofNullable(oldCTA.getRuleTemplates()).isPresent() && !oldCTA.getRuleTemplates().isEmpty()) {
                ruleTemplateIds = new ArrayList<>();
                oldCTA.getRuleTemplates().stream().map(UserBaseEntity::getId).collect(Collectors.toList());
            }
            copyRules(oldCTA, ctaDTO, ruleTemplateIds);
            this.save(oldCTA);
            responseCTA = new CostTimeAgreement(oldCTA.getId(), oldCTA.getName(), oldCTA.getExpertise(), oldCTA.getRuleTemplates(), oldCTA.getStartDateMillis(), oldCTA.getEndDateMillis(), false);

        }


        UnitPositionQueryResult unitPositionQueryResult = unitPositionService.getBasicDetails(unitPosition, null);
        responseCTA.setExpertise(unitPositionQueryResult.getExpertise());
        // for FE compactibility
        unitPositionQueryResult.setCostTimeAgreement(responseCTA);
        return unitPositionQueryResult;
    }

    public CostTimeAgreement getCTALinkedWithUnitPosition(Long unitPositionId) {
        return unitPositionGraphRepository.getCTALinkedWithUnitPosition(unitPositionId);
    }

    public Long getExpertiseIdOfCTA(Long ctaId) {
        return collectiveTimeAgreementGraphRepository.getExpertiseOfCTA(ctaId);
    }

    public Long getOrgTypeOfCTA(Long ctaId) {
        return collectiveTimeAgreementGraphRepository.getOrgTypeOfCTA(ctaId);
    }

    public Long getOrgSubTypeOfCTA(Long ctaId) {
        return collectiveTimeAgreementGraphRepository.getOrgSubTypeOfCTA(ctaId);
    }

    public CollectiveTimeAgreementDTO createCopyOfUnitCTA(Long unitId, CollectiveTimeAgreementDTO collectiveTimeAgreementDTO) throws ExecutionException, InterruptedException {
        logger.info("saving CostTimeAgreement unit {}", unitId);
        if (collectiveTimeAgreementGraphRepository.isCTAExistWithSameNameInUnit(unitId, collectiveTimeAgreementDTO.getName().trim(), -1L)) {
            exceptionService.duplicateDataException("message.cta.name.alreadyExist", collectiveTimeAgreementDTO.getName());

        }
        CostTimeAgreement costTimeAgreement = new CostTimeAgreement();
        collectiveTimeAgreementDTO.setId(null);
        BeanUtils.copyProperties(collectiveTimeAgreementDTO, costTimeAgreement);
        Optional<Expertise> expertise= expertiseGraphRepository.findById(collectiveTimeAgreementDTO.getExpertise(),0);

        CompletableFuture<Boolean> hasUpdated = ApplicationContextProviderNonManageBean.getApplicationContext().getBean(CostTimeAgreementService.class)
                .buildCTA(costTimeAgreement, collectiveTimeAgreementDTO, false, null,expertise.get());

        // Wait until they are all done
        CompletableFuture.allOf(hasUpdated).join();
        this.save(costTimeAgreement);
        collectiveTimeAgreementGraphRepository.linkUnitCTAToOrganization(costTimeAgreement.getId(), unitId);
        collectiveTimeAgreementDTO.setId(costTimeAgreement.getId());
        return collectiveTimeAgreementDTO;
    }

    public List<CTAResponseDTO> getAllCTAByOrganizationSubType(Long organizationSubTypeId) {
        return collectiveTimeAgreementGraphRepository.getAllCTAByOrganizationSubType(organizationSubTypeId);
    }

    public CollectiveTimeAgreementDTO setCTAWithOrganizationType(Long countryId, long ctaId, CollectiveTimeAgreementDTO collectiveTimeAgreementDTO, long organizationSubTypeId, boolean checked) throws ExecutionException, InterruptedException {
        OrganizationType organizationSubType = organizationTypeRepository.findOne(organizationSubTypeId);
        if (!Optional.ofNullable(organizationSubType).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.InvalidOrganizationSubtype", organizationSubTypeId);

        }
        if (checked) {
            Integer lastSuffixNumber = collectiveTimeAgreementGraphRepository.getLastSuffixNumberOfCTAName("(?i)" + collectiveTimeAgreementDTO.getName());
            String name = collectiveTimeAgreementDTO.getName();
            collectiveTimeAgreementDTO.setName(name.contains("-") ? name.replace(name.substring(name.lastIndexOf("-") + 1, name.length()), (++lastSuffixNumber).toString()) : collectiveTimeAgreementDTO.getName() + "-" + ++lastSuffixNumber);
            Long organizationTypeId = organizationTypeGraphRepository.findOrganizationTypeIdBySubTypeId(organizationSubTypeId);
            collectiveTimeAgreementDTO.setOrganizationType(organizationTypeId);
            collectiveTimeAgreementDTO.setOrganizationSubType(organizationSubTypeId);
            return countryCTAService.createCostTimeAgreementInCountry(countryId, collectiveTimeAgreementDTO);
        } else {
            Optional<CostTimeAgreement> cta = collectiveTimeAgreementGraphRepository.findById(ctaId, 0);
            if (!cta.isPresent()) {
                exceptionService.dataNotFoundByIdException("message.cta.id.notFound", ctaId);

            }
            CostTimeAgreement costTimeAgreement = cta.get();
            costTimeAgreement.setDeleted(true);
            save(costTimeAgreement);
        }
        return null;
    }


    private Boolean copyRules(CostTimeAgreement costTimeAgreement, CollectiveTimeAgreementDTO collectiveTimeAgreementDTO, List<Long> ruleTemplateIds) {

        Set<Long> ruleTemplateCategoryIds = collectiveTimeAgreementDTO.getRuleTemplates().stream().map(CTARuleTemplateDTO::getRuleTemplateCategory).collect(Collectors.toSet());
        List<RuleTemplateCategory> ruleTemplateCategories = ruleTemplateCategoryGraphRepository.findRuleTemplatesByIds(ruleTemplateCategoryIds);
        Map<Long, RuleTemplateCategory> ruleTemplateCategoryMap = ruleTemplateCategories.stream().collect(Collectors.toMap(RuleTemplateCategory::getId, v -> v));
        Set<Long> employmentTypeIds = collectiveTimeAgreementDTO.getRuleTemplates()
                .stream().flatMap(ctaRuleTemplateDTO ->
                        ctaRuleTemplateDTO.getEmploymentTypes().stream().map(e -> e.longValue()))
                .collect(Collectors.toSet());
        List<EmploymentType> employmentTypes = employmentTypeGraphRepository.getEmploymentTypeByIds(employmentTypeIds);
        Map<Long, EmploymentType> employmentTypeMap = employmentTypes.stream().collect(Collectors.toMap(EmploymentType::getId, v -> v));


        List<RuleTemplate> ruleTemplates = new ArrayList<>();
        for (CTARuleTemplateDTO ctaRuleTemplateDTO : collectiveTimeAgreementDTO.getRuleTemplates()) {
            CTARuleTemplate ctaRuleTemplate = new CTARuleTemplate();
            BeanUtils.copyProperties(ctaRuleTemplateDTO, ctaRuleTemplate);
            // Check if cta exists with same rule template Id
            if (ruleTemplateIds != null && ruleTemplateIds.contains(ctaRuleTemplate.getId())) {
                ctaRuleTemplate.cloneCTARuleTemplate();
            }
            setActivityBasesCostCalculationSettings(ctaRuleTemplate);
            if (ctaRuleTemplateDTO.getRuleTemplateCategory() != null) {
                ctaRuleTemplate.setRuleTemplateCategory(ruleTemplateCategoryMap.get(ctaRuleTemplateDTO.getRuleTemplateCategory()));
            }


            //https://stackoverflow.com/questions/8104692/how-to-avoid-java-util-concurrentmodificationexception-when-iterating-through-an
            ctaRuleTemplate.setEmploymentTypes(new ArrayList<>());
            if (!ctaRuleTemplateDTO.getEmploymentTypes().isEmpty()) {
                for (Iterator<Long> iterator = ctaRuleTemplateDTO.getEmploymentTypes().iterator(); iterator.hasNext(); ) {
                    Long value = iterator.next();
                    ctaRuleTemplate.addEmploymentType(employmentTypeMap.get(value));
                }
            }
            ruleTemplates.add(ctaRuleTemplate);
        }
        costTimeAgreement.setRuleTemplates(ruleTemplates);
        costTimeAgreement.setStartDateMillis(collectiveTimeAgreementDTO.getStartDateMillis());
        costTimeAgreement.setEndDateMillis(collectiveTimeAgreementDTO.getEndDateMillis());

        return true;
    }
}

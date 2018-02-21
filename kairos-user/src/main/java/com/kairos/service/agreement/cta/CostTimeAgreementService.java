package com.kairos.service.agreement.cta;

import com.kairos.config.listener.ApplicationContextProviderNonManageBean;
import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.organization.OrganizationType;
import com.kairos.persistence.model.user.agreement.cta.*;
import com.kairos.persistence.model.user.agreement.wta.templates.RuleTemplateCategory;
import com.kairos.persistence.model.user.auth.User;
import com.kairos.persistence.model.user.country.*;
import com.kairos.persistence.model.user.expertise.Expertise;
import com.kairos.persistence.model.user.expertise.ExpertiseTagDTO;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.organization.OrganizationTypeGraphRepository;
import com.kairos.persistence.repository.user.access_permission.AccessGroupRepository;
import com.kairos.persistence.repository.user.agreement.cta.CTARuleTemplateGraphRepository;
import com.kairos.persistence.repository.user.agreement.cta.CollectiveTimeAgreementGraphRepository;
import com.kairos.persistence.repository.user.agreement.wta.RuleTemplateCategoryGraphRepository;
import com.kairos.persistence.repository.user.auth.UserGraphRepository;
import com.kairos.persistence.repository.user.country.*;
import com.kairos.persistence.repository.user.expertise.ExpertiseGraphRepository;
import com.kairos.response.dto.web.cta.CTARuleTemplateCategoryWrapper;
import com.kairos.response.dto.web.cta.CTARuleTemplateDayTypeDTO;
import com.kairos.response.dto.web.cta.CollectiveTimeAgreementDTO;
import com.kairos.service.AsynchronousService;
import com.kairos.service.UserBaseService;
import com.kairos.service.auth.UserService;
import com.kairos.service.country.CurrencyService;
import com.kairos.service.organization.OrganizationService;
import com.kairos.util.userContext.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Transactional
@Service
public class CostTimeAgreementService extends UserBaseService {
    private Logger logger = LoggerFactory.getLogger(CostTimeAgreementService.class);


    private @Autowired UserService userService;
    private @Autowired RuleTemplateCategoryGraphRepository ruleTemplateCategoryGraphRepository;
    private @Autowired CountryGraphRepository countryGraphRepository;
    private @Autowired CTARuleTemplateGraphRepository ctaRuleTemplateGraphRepository;
    private @Autowired AsynchronousService asynchronousService;
    private @Autowired DayTypeGraphRepository dayTypeGraphRepository;
    private @Autowired  EmploymentTypeGraphRepository employmentTypeGraphRepository;
    private @Autowired AccessGroupRepository accessGroupRepository;
    private @Autowired TimeTypeGraphRepository timeTypeGraphRepository;
    private @Autowired UserGraphRepository userGraphRepository;
    private  @Autowired CurrencyService currencyService;
    private  @Autowired ExpertiseGraphRepository expertiseGraphRepository;
    private @Autowired OrganizationTypeGraphRepository organizationTypeGraphRepository;
    private @Autowired CurrencyGraphRepository currencyGraphRepository;
    private @Autowired CountryHolidayCalenderGraphRepository countryHolidayCalenderGraphRepository;
    private @Autowired CollectiveTimeAgreementGraphRepository collectiveTimeAgreementGraphRepository;
    private @Autowired OrganizationGraphRepository organizationGraphRepository;
    private @Inject OrganizationTypeGraphRepository organizationTypeRepository;
    private @Inject OrganizationService organizationService;


    public boolean isDefaultCTARuleTemplateExists(){
        return ctaRuleTemplateGraphRepository.isDefaultCTARuleTemplateExists();
    }

    public void createDefaultCtaRuleTemplate(Long countryId) {
        RuleTemplateCategory category = ruleTemplateCategoryGraphRepository
                .findByName(countryId, "NONE", RuleTemplateCategoryType.CTA);
        Currency currency = currencyService.getCurrencyByCountryId(countryId);
        List<RuleTemplate> ctaRuleTemplates = new ArrayList<>();
        if (category != null) {
            Arrays.stream(CTARuleTemplateType.values()).forEach(cTARuleTemplate -> {
                CTARuleTemplate ctaRuleTemplate = createDefaultRuleTemplate(cTARuleTemplate, currency);
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

    public CTARuleTemplateDTO createCTARuleTemplate(Long countryId, CTARuleTemplateDTO ctaRuleTemplateDTO) throws ExecutionException, InterruptedException{
        if(ctaRuleTemplateGraphRepository.isCTARuleTemplateExistWithSameName(countryId, ctaRuleTemplateDTO.getName())){
            throw new DuplicateDataException("CTA Rule Template already exists with same name : " +ctaRuleTemplateDTO.getName() );
        }
        // Set id null as new entry should be created
        ctaRuleTemplateDTO.setId(null);
        CTARuleTemplate ctaRuleTemplate= new CTARuleTemplate();
        Long userId = UserContext.getUserDetails().getId();
        User user = userGraphRepository.findOne(userId, 0);
        // While updating rule template, do not update template type
        ctaRuleTemplateDTO.setRuleTemplateType(ctaRuleTemplateDTO.getName());
        this.buildCTARuleTemplate(ctaRuleTemplate, ctaRuleTemplateDTO, false);
        ctaRuleTemplate.setLastModifiedBy(user);
        this.save(ctaRuleTemplate);

        ctaRuleTemplateGraphRepository.addCTARuleTemplateInCountry(countryId,ctaRuleTemplate.getId());

        return ctaRuleTemplateDTO;
    }

    private CTARuleTemplate createDefaultRuleTemplate(CTARuleTemplateType ctaRuleTemplateType, Currency currency) {
        CTARuleTemplate ctaRuleTemplate = null;
        switch (ctaRuleTemplateType) {
            case RULE_TEMPLATE_1:
                ctaRuleTemplate = new CTARuleTemplate("Working Evening Shifts",
                        "CTA rule for evening shift, from 17-23 o'clock.  For this organization/unit this is payroll type '210:  Evening compensation'",
                         "210:  Evening compensation", "xyz");
                break;
            case RULE_TEMPLATE_2:

                ctaRuleTemplate = new CTARuleTemplate("Working Night Shifts",
                        "CTA rule for night shift, from 23-07 o. clock.  For this organization/unit this is payroll type “212:  Night compensation”",
                         "212:  Night compensation", "xyz");
                break;
            case RULE_TEMPLATE_3:

                ctaRuleTemplate = new CTARuleTemplate("Working On a Saturday",
                        "CTA rule for Saturdays shift, from 08-24 o. clock. For this organization/unit this is payroll type " +
                                "“214:  Saturday compensation”. If you are working from 00-07 on Saturday, you only gets evening " +
                                "compensation",
                        "214:  Saturday compensation", "xyz");
                break;
            case RULE_TEMPLATE_4:
                ctaRuleTemplate = new CTARuleTemplate("Working On a Sunday",
                        "CTA rule for Saturdays shift, from 00-24 o. clock. For this organization/unit this is " +
                                "payroll type “214:Saturday compensation”.All working time on Sundays gives compensation"
                        ,
                        "214:Saturday compensation", "xyz");
                break;
            case RULE_TEMPLATE_5:
                ctaRuleTemplate = new CTARuleTemplate("Working On a Full Public Holiday",
                        "CTA rule for full public holiday shift, from 00-24 o. clock.  For this organization/unit this is " +
                                "payroll type “216:  public holiday compensation”. All working time on full PH gives " +
                                "compensation",
                        "216:public holiday compensation", "xyz");
                break;
            case RULE_TEMPLATE_6:
                ctaRuleTemplate = new CTARuleTemplate("Working On a Half Public Holiday",
                        "CTA rule for full public holiday shift, from 12-24 o. clock. For this organization/unit" +
                                " this is payroll type “218:  half public holiday compensation”.All working time on " +
                                "half PH gives compensation",
                        "218: half public holiday compensation", "xyz");
                break;
            case RULE_TEMPLATE_7:
                ctaRuleTemplate = new CTARuleTemplate("Working Overtime",
                        "CTA rule for overtime shift, from 00-24 o. clock.  For this organization/unit this is payroll type “230: " +
                                " 50% overtime compensation”.",
                        "230:50% overtime compensation", "xyz");
                break;
            case RULE_TEMPLATE_8:
                ctaRuleTemplate = new CTARuleTemplate("Working Extratime",
                        "CTA rule for extra time shift, from 00-24 o. clock.  For this organization/unit this is payroll type" +
                                " “250:  extratime compensation”. ",
                        "250:  extratime compensation", "xyz");
                break;
            case RULE_TEMPLATE_9:
                ctaRuleTemplate = new CTARuleTemplate("Late Notice Compensation",
                        "CTA rule for late notification on changes to working times.  If notice of change is done within 72 hours" +
                                " before start of working day, then staff is entitled to at compensation of 105 kroner",
                        "", "xyz");
                break;
            case RULE_TEMPLATE_10:
                ctaRuleTemplate = new CTARuleTemplate("Extra Dutyfree Day For Each Public Holiday",
                        "CTA rule for each public holiday.  Whenever there is a public holiday staff are entitled to an" +
                                " extra day off, within 3 month or just compensated in the timebank.",
                        "", "xyz");
                break;
            default:
                throw new IllegalArgumentException("invalid template type");

        }
        ctaRuleTemplate.setCalculationUnit(CalculationUnit.HOURS);
        CompensationTable compensationTable = new CompensationTable(10);
        ctaRuleTemplate.setCompensationTable(compensationTable);
        FixedValue fixedValue = new FixedValue(10, currency, FixedValue.Type.PER_ACTIVITY);
        ctaRuleTemplate.setCalculateValueAgainst(new CalculateValueAgainst(CalculateValueAgainst.CalculateValueType.FIXED_VALUE, 10.5f, fixedValue));
        ctaRuleTemplate.setApprovalWorkFlow(ApprovalWorkFlow.NO_APPROVAL_NEEDED);
        ctaRuleTemplate.setBudgetType(BudgetType.ACTIVITY_COST);
        ctaRuleTemplate.setActivityTypeForCostCalculation(ActivityTypeForCostCalculation.SELECTED_ACTIVITY_TYPE);
        ctaRuleTemplate.setPlanningCategory(PlanningCategory.DEVIATION_FROM_PLANNED);
        ctaRuleTemplate.setStaffFunctions(Stream.of(StaffFunction.TRAINING_COORDINATOR).collect(Collectors.toList()));
        ctaRuleTemplate.setPlannedTimeWithFactor(PlannedTimeWithFactor.buildPlannedTimeWithFactor(10,true,AccountType.DUTYTIME_ACCOUNT));
        return ctaRuleTemplate;


    }

    public CTARuleTemplateCategoryWrapper loadAllCTARuleTemplateByCountry(Long countryId) {
        Country country = countryGraphRepository.findOne(countryId);
        List<RuleTemplateCategory> ruleTemplateCategories = country.getRuleTemplateCategories();
        List<RuleTemplateCategory> ctaRuleTemplateCategoryList = ruleTemplateCategories.parallelStream().filter(
                ruleTemplateCategory -> RuleTemplateCategoryType.CTA.equals(ruleTemplateCategory.getRuleTemplateCategoryType())&&ruleTemplateCategory.isDeleted()==false)
                .collect(Collectors.toList());
        List<Long> ruleTemplateCategoryIds = ctaRuleTemplateCategoryList.parallelStream().map(RuleTemplateCategory::getId)
                .collect(Collectors.toList());

        List<CTARuleTemplateQueryResult> ruleTemplates=ctaRuleTemplateGraphRepository.findByRuleTemplateCategoryIdInAndCountryAndDeletedFalse(ruleTemplateCategoryIds,countryId);
        CTARuleTemplateCategoryWrapper ctaRuleTemplateCategoryWrapper=new CTARuleTemplateCategoryWrapper();
        ctaRuleTemplateCategoryWrapper.getRuleTemplateCategories().addAll(ctaRuleTemplateCategoryList);
        ctaRuleTemplateCategoryWrapper.setRuleTemplates(ruleTemplates);
        return ctaRuleTemplateCategoryWrapper;
    }

    public CTARuleTemplateCategoryWrapper loadAllCTARuleTemplateByUnit(Long unitId) {
        Long countryId = organizationService.getCountryIdOfOrganization(unitId);
        return loadAllCTARuleTemplateByCountry(countryId);
    }

    public CTARuleTemplate buildCTARuleTemplate(CTARuleTemplate ctaRuleTemplate,CTARuleTemplateDTO ctaRuleTemplateDTO, Boolean doUpdate) throws ExecutionException, InterruptedException {

        BeanUtils.copyProperties(ctaRuleTemplateDTO,ctaRuleTemplate,"calculateOnDayTypes");

        CompletableFuture<Boolean> hasUpdated = ApplicationContextProviderNonManageBean.getApplicationContext().getBean(CostTimeAgreementService.class)
                .buildEmploymentTypeAndAccessGroups(ctaRuleTemplate, ctaRuleTemplateDTO);
        //Load reference only
        RuleTemplateCategory ruleTemplateCategory =
                ruleTemplateCategoryGraphRepository.findOne(ctaRuleTemplateDTO.getRuleTemplateCategory(), 0);
        List<Long> dayTypesIds = ctaRuleTemplateDTO.getCalculateOnDayTypes().parallelStream().map(CTARuleTemplateDayTypeDTO::getDayType).collect(Collectors.toList());
        Iterable<DayType> dayTypeList = dayTypeGraphRepository.findAllById(dayTypesIds, 0);
        List<Long> countryHolidayCalendersIds = ctaRuleTemplateDTO.getCalculateOnDayTypes().parallelStream()
                .map(CTARuleTemplateDayTypeDTO::getCountryHolidayCalenders).flatMap(countryHolidayCalenders ->
                {
                    return countryHolidayCalenders.stream();
                }).collect(Collectors.toList());

        if( doUpdate && !ctaRuleTemplate.getRuleTemplateCategory().getId().equals(ctaRuleTemplateDTO.getRuleTemplateCategory())){
            // Detach rule template from older category If category has been updated
            ruleTemplateCategoryGraphRepository.detachRuleTemplateCategoryFromCTARuleTemplate(ctaRuleTemplate.getId(),ctaRuleTemplate.getRuleTemplateCategory().getId());
        }
        ctaRuleTemplate.setRuleTemplateCategory(ruleTemplateCategory);

        setActivityBasesCostCalculationSettings(ctaRuleTemplate);
        if(ctaRuleTemplate.getCalculateValueAgainst()!=null && ctaRuleTemplate.getCalculateValueAgainst().getCalculateValue() !=null){
            switch (ctaRuleTemplate.getCalculateValueAgainst().getCalculateValue().toString()){
                case "FIXED_VALUE" :{
                    if(doUpdate && ctaRuleTemplate.getCalculateValueAgainst().getFixedValue().getCurrencyId()!=null){
                        Currency currency=currencyGraphRepository.findOne(ctaRuleTemplate.getCalculateValueAgainst().getFixedValue().getCurrencyId());
                        ctaRuleTemplate.getCalculateValueAgainst().getFixedValue().setCurrency(currency);
                    }
                    break;
                }

                case "WEEKLY_HOURS" :{
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
        logger.info("ctaRuleTemplate.getCalculateValueAgainst().getScale : {}",ctaRuleTemplate.getCalculateValueAgainst().getScale());
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

    public Boolean deleteCostTimeAgreement(Long countryId, Long ctaId){
        CostTimeAgreement costTimeAgreement = collectiveTimeAgreementGraphRepository.findCTAByCountryAndIdAndDeleted(countryId,ctaId,false);
        if(costTimeAgreement == null){
            throw new DataNotFoundByIdException("Invalid CTA Id");
        }
        costTimeAgreement.setDeleted(true);
        this.save(costTimeAgreement);
        return true;
    }
/// =============================================================================  CTA  ==================================


    public CTARuleTemplate saveEmbeddedEntitiesOfCTARuleTemplate(CTARuleTemplate ctaRuleTemplate, RuleTemplate oldCTA){

        // Fetch Employment Type
        /*List<Long> employmentTypeIds = oldCTA.getEmploymentTypes();
        ctaRuleTemplate.setEmploymentTypes(employmentTypeGraphRepository.getEmploymentTypeByIds(employmentTypeIds, false));

        // Fetch Time Type
        List<Long> timeTypeIds = ctaRuleTemplateDTO.getTimeTypes();
        ctaRuleTemplate.setTimeTypes(timeTypeGraphRepository.findTimeTypeByIds(timeTypeIds));

        Long ruleTemplateId = ctaRuleTemplateDTO.getRuleTemplateCategory();
        if(ruleTemplateId != null ){
            ctaRuleTemplate.setRuleTemplateCategory(ruleTemplateCategoryGraphRepository.findOne(ruleTemplateId));
        }*/

        return ctaRuleTemplate;
    }

    @Async
    public CompletableFuture<Boolean> buildCTAToCopy(CostTimeAgreement costTimeAgreementToBeCreated, CostTimeAgreement oldCTA)
            throws InterruptedException, ExecutionException {

        // Get Experties
        Callable<Optional<Expertise>> expertiseCallable=()->{
            Optional<Expertise> expertise=expertiseGraphRepository.findById(oldCTA.getExpertise().getId());
            return  expertise;
        };

        Future<Optional<Expertise>>expertiseFuture=asynchronousService.executeAsynchronously(expertiseCallable);

        // Get Rule Templates
        Callable<List<RuleTemplate>> ctaRuleTemplatesCallable=()-> {
            List<RuleTemplate> ruleTemplates = new ArrayList<>();
            // TODO need to fetch rule templates
            for(RuleTemplate ruleTemplate : oldCTA.getRuleTemplates()){
                CTARuleTemplate ctaRuleTemplate = new CTARuleTemplate() ;
                BeanUtils.copyProperties(ruleTemplate,ctaRuleTemplate);
                ctaRuleTemplate.cloneCTARuleTemplate();
                setActivityBasesCostCalculationSettings(ctaRuleTemplate);
                BeanUtils.copyProperties(ctaRuleTemplate,ruleTemplate,"createdBy");
                ruleTemplates.add(ctaRuleTemplate);
            }
            return ruleTemplates;
        };
        Future<List<RuleTemplate>>ctaRuleTemplatesFuture=asynchronousService.executeAsynchronously(ctaRuleTemplatesCallable);

        // Get Organization Type
        Callable<Optional<OrganizationType>> OrganizationTypesListCallable=()->{
            Optional<OrganizationType> organizationType=organizationTypeGraphRepository.findById(oldCTA.getOrganizationType().getId());
            return  organizationType;
        };
        Future<Optional<OrganizationType>>organizationTypesFuture=asynchronousService.executeAsynchronously(OrganizationTypesListCallable);


        // Get Organization Sub Type
        Callable<Optional<OrganizationType>> OrganizationSubTypesListCallable=()->{
            Optional<OrganizationType> organizationType=organizationTypeGraphRepository.findById(oldCTA.getOrganizationSubType().getId());
            return  organizationType;
        };
        Future<Optional<OrganizationType>>organizationSubTypesFuture=asynchronousService.executeAsynchronously(OrganizationSubTypesListCallable);


        //set data
        if(expertiseFuture.get().isPresent())
            costTimeAgreementToBeCreated.setExpertise(expertiseFuture.get().get());
        costTimeAgreementToBeCreated.setRuleTemplates(ctaRuleTemplatesFuture.get());
        costTimeAgreementToBeCreated.setOrganizationType(organizationTypesFuture.get().get());
        costTimeAgreementToBeCreated.setOrganizationSubType(organizationSubTypesFuture.get().get());
        costTimeAgreementToBeCreated.setStartDateMillis(oldCTA.getStartDateMillis());
        costTimeAgreementToBeCreated.setEndDateMillis(oldCTA.getEndDateMillis());

        return CompletableFuture.completedFuture(true);
    }

    public CostTimeAgreement createCopyOfCTA(Long ctaId) throws InterruptedException,ExecutionException{
        CostTimeAgreement costTimeAgreement=collectiveTimeAgreementGraphRepository.findOne(ctaId, 2);
        CostTimeAgreement newCostTimeAgreement = new CostTimeAgreement();
        BeanUtils.copyProperties(costTimeAgreement, newCostTimeAgreement, "createdBy");
        // In case of copy CTA need to remove ID of CTA
        newCostTimeAgreement.setId(null);
        CompletableFuture<Boolean> hasUpdated= ApplicationContextProviderNonManageBean.getApplicationContext().getBean(CostTimeAgreementService.class)
                .buildCTAToCopy(newCostTimeAgreement, costTimeAgreement);

        // Wait until they are all done
        CompletableFuture.allOf(hasUpdated).join();
//        newCostTimeAgreement.setCountry(null);
        this.save(newCostTimeAgreement);
        return newCostTimeAgreement;
    }

    public CollectiveTimeAgreementDTO updateCostTimeAgreement(Long countryId, Long unitId, Long ctaId, CollectiveTimeAgreementDTO collectiveTimeAgreementDTO) throws ExecutionException, InterruptedException {
        if( countryId != null && collectiveTimeAgreementGraphRepository.isCTAExistWithSameNameInCountry(countryId, collectiveTimeAgreementDTO.getName(), ctaId)){
            throw new DuplicateDataException("CTA already exists with same name " +collectiveTimeAgreementDTO.getName() );
        } else if( unitId != null && collectiveTimeAgreementGraphRepository.isCTAExistWithSameNameInUnit(unitId, collectiveTimeAgreementDTO.getName(), ctaId )){
            throw new DuplicateDataException("CTA already exists with same name " +collectiveTimeAgreementDTO.getName() );
        }
        CostTimeAgreement costTimeAgreement=collectiveTimeAgreementGraphRepository.findOne(ctaId,2);

        List<Long> ruleTemplateIds = new ArrayList<>();
        logger.info("costTimeAgreement.getRuleTemplates() : {}",costTimeAgreement.getRuleTemplates().size());
        for(RuleTemplate ruleTemplate : costTimeAgreement.getRuleTemplates()){
            ruleTemplateIds.add(ruleTemplate.getId());
        }
//        CostTimeAgreement newCostTimeAgreement = createCopyOfCTA(costTimeAgreement.getId());

        BeanUtils.copyProperties(collectiveTimeAgreementDTO, costTimeAgreement);
        costTimeAgreement.setName(collectiveTimeAgreementDTO.getName());
        costTimeAgreement.setDescription(collectiveTimeAgreementDTO.getDescription());
        CompletableFuture<Boolean> hasUpdated= ApplicationContextProviderNonManageBean.getApplicationContext().getBean(CostTimeAgreementService.class)
                .buildCTA(costTimeAgreement,collectiveTimeAgreementDTO, true, ruleTemplateIds);
        CompletableFuture.allOf(hasUpdated).join();


        // Check for child CTA
        /*CostTimeAgreement childCTA = collectiveTimeAgreementGraphRepository.fetchChildCTA(ctaId);
        if(childCTA != null){
            // detach old parent CTA and assign new one
            collectiveTimeAgreementGraphRepository.detachParentCTA(childCTA.getId());
            childCTA.setParent(newCostTimeAgreement);
            this.save(childCTA);
        }
        newCostTimeAgreement.setParent(costTimeAgreement);
        this.save(newCostTimeAgreement);*/

        this.save(costTimeAgreement);
        return collectiveTimeAgreementDTO;
    }

    public List<CTAListQueryResult> loadAllCTAByCountry(Long countryId) {
//        Country country = countryGraphRepository.findOne(countryId);
        return  collectiveTimeAgreementGraphRepository.findCTAByCountryId(countryId);
    }

    public List<CTAListQueryResult> loadAllCTAByUnit(Long unitId) {
//        Country country = countryGraphRepository.findOne(countryId);
        return  collectiveTimeAgreementGraphRepository.findCTAByUnitId(unitId);
    }

    public CTARuleTemplate saveEmbeddedEntitiesOfCTARuleTemplate(CTARuleTemplate ctaRuleTemplate, CTARuleTemplateDTO ctaRuleTemplateDTO){
        /*for (CTARuleTemplateDayType ctaRuleTemplateDayType : ctaRuleTemplate.getCalculateOnDayTypes()) {

            DayType dayType = dayTypeGraphRepository.findOne(ctaRuleTemplateDayType.getDayType().getId());
            List<Long> countryHolidayCalendarIds = new ArrayList<>();
            for (CountryHolidayCalender countryHolidayCalender : ctaRuleTemplateDayType.getCountryHolidayCalenders()) {
                countryHolidayCalendarIds.add(countryHolidayCalender.getId());
            }
            List<CountryHolidayCalender> countryHolidayCalenders = countryHolidayCalenderGraphRepository.getCountryHolidayCalendarsById(countryHolidayCalendarIds);
            ctaRuleTemplateDayType.setDayType(dayTypeGraphRepository.findOne(ctaRuleTemplateDayType.getDayType().getId()));
            ctaRuleTemplateDayType.setCountryHolidayCalenders(countryHolidayCalenders);
        }*/

        // Fetch Access Group
//        List<Long> accessGroupIds = ctaRuleTemplateDTO.getCalculateValueIfPlanned();
//        ctaRuleTemplate.setCalculateValueIfPlanned(accessGroupRepository.getAccessGroupById(accessGroupIds));

        if(ctaRuleTemplate.getId() != null){
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
        if(ruleTemplateId != null ){
            ctaRuleTemplate.setRuleTemplateCategory(ruleTemplateCategoryGraphRepository.findOne(ruleTemplateId));
        }

        return ctaRuleTemplate;
    }


    @Async
    public CompletableFuture<Boolean> buildCTA(CostTimeAgreement costTimeAgreement, CollectiveTimeAgreementDTO collectiveTimeAgreementDTO, Boolean doUpdate, List<Long> ruleTemplateIds)
            throws InterruptedException, ExecutionException {

        // Get Experties
        Callable<Optional<Expertise>> expertiseCallable=()->{
            Optional<Expertise> expertise=expertiseGraphRepository.findById(collectiveTimeAgreementDTO.getExpertise());
            return  expertise;
        };

        Future<Optional<Expertise>>expertiseFuture=asynchronousService.executeAsynchronously(expertiseCallable);

              // Get Rule Templates
        Callable<List<RuleTemplate>> ctaRuleTemplatesCallable=()-> {
            List<RuleTemplate> ruleTemplates = new ArrayList<>();
            for(CTARuleTemplateDTO ctaRuleTemplateDTO : collectiveTimeAgreementDTO.getRuleTemplates()){
                CTARuleTemplate ctaRuleTemplate = new CTARuleTemplate() ;
                BeanUtils.copyProperties(ctaRuleTemplateDTO,ctaRuleTemplate);
                // Check if cta exists with same rule template Id
                if(!doUpdate || (doUpdate && !ruleTemplateIds.contains(ctaRuleTemplate.getId()))){
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
        Future<List<RuleTemplate>>ctaRuleTemplatesFuture=asynchronousService.executeAsynchronously(ctaRuleTemplatesCallable);

        // Get Organization Type
        Callable<Optional<OrganizationType>> OrganizationTypesListCallable=()->{
            Optional<OrganizationType> organizationType=organizationTypeGraphRepository.findById(collectiveTimeAgreementDTO.getOrganizationType());
            return  organizationType;
        };
        Future<Optional<OrganizationType>>organizationTypesFuture=asynchronousService.executeAsynchronously(OrganizationTypesListCallable);


        // Get Organization Sub Type
        Callable<Optional<OrganizationType>> OrganizationSubTypesListCallable=()->{
            Optional<OrganizationType> organizationType=organizationTypeGraphRepository.findById(collectiveTimeAgreementDTO.getOrganizationSubType());
            return  organizationType;
        };
        Future<Optional<OrganizationType>>organizationSubTypesFuture=asynchronousService.executeAsynchronously(OrganizationSubTypesListCallable);

        //set data
         if(expertiseFuture.get().isPresent())
             costTimeAgreement.setExpertise(expertiseFuture.get().get());
        costTimeAgreement.setOrganizationType(organizationTypesFuture.get().get());
        costTimeAgreement.setOrganizationSubType(organizationSubTypesFuture.get().get());
        costTimeAgreement.setRuleTemplates(ctaRuleTemplatesFuture.get());
        costTimeAgreement.setStartDateMillis(collectiveTimeAgreementDTO.getStartDateMillis());
        costTimeAgreement.setEndDateMillis(collectiveTimeAgreementDTO.getEndDateMillis());

        return CompletableFuture.completedFuture(true);
    }

    public void setActivityBasesCostCalculationSettings(CTARuleTemplate ctaRuleTemplate){

        switch (ctaRuleTemplate.getActivityTypeForCostCalculation().toString()) {
            case "TIME_TYPE_ACTIVITY":
                ctaRuleTemplate.setActivityIds(new ArrayList<>());
                break;
            default:
                ctaRuleTemplate.setPlannedTimeId(null);
                ctaRuleTemplate.setTimeTypeId(null);
                break;
        }
    }

    public CollectiveTimeAgreementDTO createCostTimeAgreement(Long countryId,CollectiveTimeAgreementDTO collectiveTimeAgreementDTO) throws ExecutionException, InterruptedException {
        logger.info("saving CostTimeAgreement country {}",countryId);
        if( collectiveTimeAgreementGraphRepository.isCTAExistWithSameNameInCountry(countryId, collectiveTimeAgreementDTO.getName())){
            throw new DuplicateDataException("CTA already exists with same name " +collectiveTimeAgreementDTO.getName() );
        }
        CostTimeAgreement costTimeAgreement=new CostTimeAgreement();
        collectiveTimeAgreementDTO.setId(null);
        // In case of copy CTA need to remove ID of CTA
        BeanUtils.copyProperties(collectiveTimeAgreementDTO, costTimeAgreement);

//        costTimeAgreement.setId(null);
        CompletableFuture<Boolean> hasUpdated= ApplicationContextProviderNonManageBean.getApplicationContext().getBean(CostTimeAgreementService.class)
                .buildCTA(costTimeAgreement,collectiveTimeAgreementDTO, false, null);

        // Wait until they are all done
        CompletableFuture.allOf(hasUpdated).join();
        costTimeAgreement.setCountry(countryGraphRepository.findOne(countryId,0));
        this.save(costTimeAgreement);
        // TO create CTA for organizations too which are linked with same sub type
        publishNewCountryCTAToOrganizationByOrgSubType(costTimeAgreement, collectiveTimeAgreementDTO, costTimeAgreement.getOrganizationSubType().getId());

        collectiveTimeAgreementDTO.setId(costTimeAgreement.getId());
        /*BeanUtils.copyProperties(costTimeAgreement, collectiveTimeAgreementDTO);
        for(CTARuleTemplateDTO templateDTO : collectiveTimeAgreementDTO.getRuleTemplates()){
            templateDTO.setRuleTemplateCategory();
        }*/
        return collectiveTimeAgreementDTO;
    }

    public CollectiveTimeAgreementDTO updateCostTimeAgreementForOrg(Long ctaId, Long orgId, CollectiveTimeAgreementDTO collectiveTimeAgreementDTO) throws ExecutionException, InterruptedException {
        Boolean cTALinkedWithCountry = collectiveTimeAgreementGraphRepository.isCTALinkedWithCountry(ctaId);
        CostTimeAgreement costTimeAgreement = collectiveTimeAgreementGraphRepository.findOne(ctaId);
        CostTimeAgreement newCostTimeAgreement=new CostTimeAgreement();
        if(costTimeAgreement == null){
            throw new DataNotFoundByIdException("Invalid CTA Id");
        }
        // Clone and create new
        if(cTALinkedWithCountry){
            // Remove linking of old CTA with org
            collectiveTimeAgreementGraphRepository.detachCTAFromOrganization(orgId, ctaId);
        } else {
            // Set disabled of old CTA as true
            costTimeAgreement.setDisabled(true);
        }


        BeanUtils.copyProperties(collectiveTimeAgreementDTO, newCostTimeAgreement);
        CompletableFuture<Boolean> hasUpdated= ApplicationContextProviderNonManageBean.getApplicationContext().getBean(CostTimeAgreementService.class)
                .buildCTA(costTimeAgreement,collectiveTimeAgreementDTO, true, null);

        // Wait until they are all done
        CompletableFuture.allOf(hasUpdated).join();
        // Set Parent CTA in new CTA
        newCostTimeAgreement.setParent(costTimeAgreement);
        this.save(newCostTimeAgreement);
        BeanUtils.copyProperties(costTimeAgreement,collectiveTimeAgreementDTO,"timeTypes");
        return collectiveTimeAgreementDTO;
    }

    public CTARuleTemplateDTO updateCTARuleTemplate(Long countryId, Long id, CTARuleTemplateDTO ctaRuleTemplateDTO) throws ExecutionException, InterruptedException {
        CTARuleTemplate ctaRuleTemplate= ctaRuleTemplateGraphRepository.findOne(id,3);
        Long userId = UserContext.getUserDetails().getId();
        User user = userGraphRepository.findOne(userId, 0);
        // While updating rule template, do not update template type
        ctaRuleTemplateDTO.setRuleTemplateType(ctaRuleTemplate.getRuleTemplateType());
        this.buildCTARuleTemplate(ctaRuleTemplate, ctaRuleTemplateDTO, true);
        ctaRuleTemplate.setLastModifiedBy(user);
        this.save(ctaRuleTemplate);
        return ctaRuleTemplateDTO;
    }

    public Boolean publishCountryCTAToNewOrganizationByOrgSubType(Long organizationId, List<Long> orgSubTypeId){
        List<CostTimeAgreement> costTimeAgreements  = collectiveTimeAgreementGraphRepository.getAllCTAByOrganiationSubType(orgSubTypeId, false);
        collectiveTimeAgreementGraphRepository.detachAllCTAFromOrganization(organizationId);
        Organization org = organizationGraphRepository.findOne(organizationId);
        org.setCostTimeAgreements(costTimeAgreements);
        organizationGraphRepository.save(org);
        return true;
    }


    public CostTimeAgreement updateCostTimeAgreementForOrganization(CostTimeAgreement countryCTA, CollectiveTimeAgreementDTO collectiveTimeAgreementDTO) throws ExecutionException, InterruptedException {

        CostTimeAgreement costTimeAgreement=new CostTimeAgreement();
        BeanUtils.copyProperties(collectiveTimeAgreementDTO, costTimeAgreement);
        CompletableFuture<Boolean> hasUpdated= ApplicationContextProviderNonManageBean.getApplicationContext().getBean(CostTimeAgreementService.class)
                .buildCTA(costTimeAgreement,collectiveTimeAgreementDTO, false, null);

        // Wait until they are all done
        CompletableFuture.allOf(hasUpdated).join();
        this.save(costTimeAgreement);
        return costTimeAgreement;
    }

    public CostTimeAgreement createCostTimeAgreementForOrganization(CollectiveTimeAgreementDTO collectiveTimeAgreementDTO) throws ExecutionException, InterruptedException {

        CostTimeAgreement costTimeAgreement=new CostTimeAgreement();
        BeanUtils.copyProperties(collectiveTimeAgreementDTO, costTimeAgreement);
        CompletableFuture<Boolean> hasUpdated= ApplicationContextProviderNonManageBean.getApplicationContext().getBean(CostTimeAgreementService.class)
                .buildCTA(costTimeAgreement,collectiveTimeAgreementDTO, false, null);

        // Wait until they are all done
        CompletableFuture.allOf(hasUpdated).join();
        this.save(costTimeAgreement);
        return costTimeAgreement;
    }

    public Boolean publishNewCountryCTAToOrganizationByOrgSubType(CostTimeAgreement costTimeAgreement, CollectiveTimeAgreementDTO collectiveTimeAgreementDTO, Long organizationSubTypeId) throws ExecutionException, InterruptedException{
        List<Organization> organizations = organizationTypeRepository.getOrganizationsByOrganizationType(organizationSubTypeId);
        organizations.forEach(organization ->
        {
           try{
               CostTimeAgreement newCostTimeAgreement =  createCostTimeAgreementForOrganization(collectiveTimeAgreementDTO);
               organization.getCostTimeAgreements().add(newCostTimeAgreement);
//               newCostTimeAgreement.setParentCountryCTA(costTimeAgreement);
               collectiveTimeAgreementGraphRepository.linkParentCountryCTAToOrganization(costTimeAgreement.getId(), newCostTimeAgreement.getId());
               save(organization);
           } catch (Exception e){
               // Exception occured
               logger.info("Exception occured on setting cta to organization");
           }

        });
        return true;
    }

    public Boolean publishUpdatedCountryCTAToOrganization(CostTimeAgreement costTimeAgreement, CollectiveTimeAgreementDTO collectiveTimeAgreementDTO) throws ExecutionException, InterruptedException{
        List<CostTimeAgreement> organizationCTAs = collectiveTimeAgreementGraphRepository.getListOfOrganizationCTAByParentCountryCTA(costTimeAgreement.getId());
        organizationCTAs.forEach(organizationCTA ->
        {
            try{
                CostTimeAgreement newCostTimeAgreement =  createCopyOfCTA(costTimeAgreement.getId());
                updateCostTimeAgreement(null, null, organizationCTA.getId(), collectiveTimeAgreementDTO);
                /*organization.getCostTimeAgreements().add(newCostTimeAgreement);
                newCostTimeAgreement.setParentCountryCTA(costTimeAgreement);
                save(organization);*/
            } catch (Exception e){
                // Exception occured
                logger.info("Exception occured on setting cta to organization");
            }

        });
        return true;
    }


    public List<ExpertiseTagDTO> getExpertiseForOrgCTA(long unitId) {
        Long countryId = organizationService.getCountryIdOfOrganization(unitId);
        return expertiseGraphRepository.getAllExpertiseWithTagsByCountry(countryId);
    }

    public Long getCTAIdByNameAndCountry(String name, Long countryId){
        CostTimeAgreement cta = collectiveTimeAgreementGraphRepository.getCTAIdByCountryAndName(countryId, name);
        return  (Optional.ofNullable(cta).isPresent()) ? null : cta.getId();
    }
}

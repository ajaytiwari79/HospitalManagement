package com.kairos.service.agreement.cta;

import com.kairos.config.listener.ApplicationContextProviderNonManageBean;
import com.kairos.persistence.model.agreement.cta.CTARuleTemplate;
import com.kairos.persistence.model.agreement.cta.CTARuleTemplateDTO;
import com.kairos.persistence.model.agreement.cta.CostTimeAgreement;
import com.kairos.persistence.model.agreement.cta.RuleTemplate;
import com.kairos.persistence.model.agreement.cta.cta_response.CTADetailsWrapper;
import com.kairos.persistence.model.agreement.cta.cta_response.CollectiveTimeAgreementDTO;
import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.organization.OrganizationType;
import com.kairos.persistence.model.user.expertise.Expertise;
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

@Service
@Transactional
public class CountryCTAService extends UserBaseService {
    private Logger logger = LoggerFactory.getLogger(CountryCTAService.class);
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
    UnitPositionService unitPositionService;
    private @Inject
    ExceptionService exceptionService;

    public CollectiveTimeAgreementDTO createCostTimeAgreementInCountry(Long countryId, CollectiveTimeAgreementDTO collectiveTimeAgreementDTO) throws ExecutionException, InterruptedException {
        logger.info("saving CostTimeAgreement country {}", countryId);
        if (collectiveTimeAgreementGraphRepository.isCTAExistWithSameNameInCountry(countryId, collectiveTimeAgreementDTO.getName())) {
            exceptionService.duplicateDataException("message.cta.name.alreadyExist", collectiveTimeAgreementDTO.getName());

        }
        CTADetailsWrapper ctaDetailsWrapper = new CTADetailsWrapper();
        CompletableFuture<Boolean> allBasicDetails = ApplicationContextProviderNonManageBean.getApplicationContext().getBean(CountryCTAService.class)
                .findAllBasicDetails(countryId, collectiveTimeAgreementDTO, ctaDetailsWrapper);
        CompletableFuture.allOf(allBasicDetails).join();

        CostTimeAgreement costTimeAgreement = new CostTimeAgreement();
        collectiveTimeAgreementDTO.setId(null);
        // In case of copy CTA need to remove ID of CTA
        BeanUtils.copyProperties(collectiveTimeAgreementDTO, costTimeAgreement);


        costTimeAgreement.setId(null);
        CompletableFuture<Boolean> hasUpdated = ApplicationContextProviderNonManageBean.getApplicationContext().getBean(CountryCTAService.class)
                .buildCTA(costTimeAgreement, collectiveTimeAgreementDTO, ctaDetailsWrapper);

        // Wait until they are all done
        CompletableFuture.allOf(hasUpdated).join();
        costTimeAgreement.setCountry(countryGraphRepository.findOne(countryId, 0));
        this.save(costTimeAgreement);

        // TO create CTA for organizations too which are linked with same sub type
        publishNewCountryCTAToOrganizationByOrgSubType(countryId, costTimeAgreement, collectiveTimeAgreementDTO, costTimeAgreement.getOrganizationSubType().getId(), ctaDetailsWrapper);

        collectiveTimeAgreementDTO.setId(costTimeAgreement.getId());
        /*BeanUtils.copyProperties(costTimeAgreement, collectiveTimeAgreementDTO);
        for(CTARuleTemplateDTO templateDTO : collectiveTimeAgreementDTO.getRuleTemplateIds()){
            templateDTO.setRuleTemplateCategory();
        }*/
        return collectiveTimeAgreementDTO;
    }

    @Async
    public CompletableFuture<Boolean> findAllBasicDetails(Long countryId, CollectiveTimeAgreementDTO collectiveTimeAgreementDTO, CTADetailsWrapper ctaDetailsWrapper) throws InterruptedException, ExecutionException {

        Callable<Optional<Country>> callableCountry = () -> {
            Optional<Country> country = countryGraphRepository.findById(countryId, 0);
            return country;
        };
        Future<Optional<Country>> futureCountry = asynchronousService.executeAsynchronously(callableCountry);
        if (futureCountry.get().isPresent())
            ctaDetailsWrapper.setCountry(futureCountry.get().get());

        // Get Organization Type
        Callable<Optional<OrganizationType>> OrganizationTypesListCallable = () -> {
            Optional<OrganizationType> organizationType = organizationTypeGraphRepository.findById(collectiveTimeAgreementDTO.getOrganizationType(), 0);
            return organizationType;
        };

        Future<Optional<OrganizationType>> organizationTypesFuture = asynchronousService.executeAsynchronously(OrganizationTypesListCallable);
        if (organizationTypesFuture.get().isPresent())
            ctaDetailsWrapper.setOrganizationType(organizationTypesFuture.get().get());

        // Get Organization Sub Type
        Callable<Optional<OrganizationType>> OrganizationSubTypesListCallable = () -> {
            Optional<OrganizationType> organizationType = organizationTypeGraphRepository.findById(collectiveTimeAgreementDTO.getOrganizationSubType(), 0);
            return organizationType;
        };
        Future<Optional<OrganizationType>> organizationSubTypesFuture = asynchronousService.executeAsynchronously(OrganizationSubTypesListCallable);
        if (organizationSubTypesFuture.get().isPresent())
            ctaDetailsWrapper.setOrganizationSubType(organizationSubTypesFuture.get().get());

        Callable<Optional<Expertise>> callableExpertise = () -> {
            Optional<Expertise> expertise = expertiseGraphRepository.findById(collectiveTimeAgreementDTO.getExpertise(), 0);
            return expertise;
        };
        Future<Optional<Expertise>> futureExpertise = asynchronousService.executeAsynchronously(callableExpertise);
        if (futureExpertise.get().isPresent()) {
            ctaDetailsWrapper.setExpertise(futureExpertise.get().get());
        }
        //




        Long ruleTemplateId = ctaRuleTemplateDTO.getRuleTemplateCategory();
        if (ruleTemplateId != null) {
            ctaRuleTemplate.setRuleTemplateCategory(ruleTemplateCategoryGraphRepository.findOne(ruleTemplateId));
        }


        ctaDetailsWrapper.setAll(true);
        return CompletableFuture.completedFuture(true);

    }

    @Async
    public CompletableFuture<Boolean> buildCTA(CostTimeAgreement costTimeAgreement, CollectiveTimeAgreementDTO collectiveTimeAgreementDTO, CTADetailsWrapper ctaDetailsWrapper)
            throws InterruptedException, ExecutionException {
        // Get Rule Templates
        Callable<List<RuleTemplate>> ctaRuleTemplatesCallable = () -> {
            List<RuleTemplate> ruleTemplates = new ArrayList<>();
            for (CTARuleTemplateDTO ctaRuleTemplateDTO : collectiveTimeAgreementDTO.getRuleTemplates()) {
                CTARuleTemplate ctaRuleTemplate = new CTARuleTemplate();
                BeanUtils.copyProperties(ctaRuleTemplateDTO, ctaRuleTemplate);
                // Check if cta_response exists with same rule template Id

                ctaRuleTemplate.cloneCTARuleTemplate();
//                    ctaRuleTemplate = saveEmbeddedEntitiesOfCTARuleTemplate(ctaRuleTemplate, ctaRuleTemplateDTO);

                CTARuleTemplate.setActivityBasesCostCalculationSettings(ctaRuleTemplate);
                ctaRuleTemplate = saveEmbeddedEntitiesOfCTARuleTemplate(ctaRuleTemplate, ctaRuleTemplateDTO);
//                BeanUtils.copyProperties(ctaRuleTemplate,ctaRuleTemplateDTO,"timeTypes");
                ruleTemplates.add(ctaRuleTemplate);
            }
            return ruleTemplates;
        };
        Future<List<RuleTemplate>> ctaRuleTemplatesFuture = asynchronousService.executeAsynchronously(ctaRuleTemplatesCallable);


        costTimeAgreement.setExpertise(ctaDetailsWrapper.getExpertise());
        costTimeAgreement.setExpertise(ctaDetailsWrapper.getExpertise());
        costTimeAgreement.setExpertise(ctaDetailsWrapper.getExpertise());
        costTimeAgreement.setOrganizationType(ctaDetailsWrapper.getOrganizationType());
        costTimeAgreement.setOrganizationSubType(ctaDetailsWrapper.getOrganizationSubType());
        costTimeAgreement.setRuleTemplates(ctaRuleTemplatesFuture.get());
        costTimeAgreement.setStartDateMillis(collectiveTimeAgreementDTO.getStartDateMillis());
        costTimeAgreement.setEndDateMillis(collectiveTimeAgreementDTO.getEndDateMillis());

        return CompletableFuture.completedFuture(true);
    }

    public Boolean publishNewCountryCTAToOrganizationByOrgSubType(Long countryId, CostTimeAgreement costTimeAgreement, CollectiveTimeAgreementDTO collectiveTimeAgreementDTO, Long organizationSubTypeId, Expertise expertise) throws ExecutionException, InterruptedException {
        List<Organization> organizations1 = organizationTypeRepository.getOrganizationsByOrganizationType(organizationSubTypeId);
        List<Organization> organizations = organizationGraphRepository.findOrganizationsByIdsIn(Collections.singletonList(2567L));
        List<Long> organizationIds = new ArrayList<>();
        List<Long> activityIds = new ArrayList<>();
        organizations.stream().forEach(organization -> organizationIds.add(organization.getId()));
        collectiveTimeAgreementDTO.getRuleTemplates().stream().forEach(ruleTemp -> {
            if (Optional.ofNullable(ruleTemp.getActivityIds()).isPresent()) {
                activityIds.addAll(ruleTemp.getActivityIds());
            }
        });


        HashMap<Long, HashMap<Long, Long>> unitActivities = activityTypesRestClient.getActivityIdsForUnitsByParentActivityId(countryId, organizationIds, activityIds);
        organizations.forEach(organization ->
        {
            try {
                CostTimeAgreement newCostTimeAgreement = createCostTimeAgreementForOrganization(collectiveTimeAgreementDTO, unitActivities.get(organization.getId()), expertise);
                organization.getCostTimeAgreements().add(newCostTimeAgreement);
//               newCostTimeAgreement.setParentCountryCTA(costTimeAgreement);
                collectiveTimeAgreementGraphRepository.linkParentCountryCTAToOrganization(costTimeAgreement.getId(), newCostTimeAgreement.getId());
                // save(organization);
            } catch (Exception e) {
                // Exception occured
                logger.info("Exception occured on setting cta_response to organization");
            }

        });
        save(organizations);
        return true;
    }

    public CostTimeAgreement createCostTimeAgreementForOrganization(CollectiveTimeAgreementDTO collectiveTimeAgreementDTO, HashMap<Long, Long> parentUnitActivityMap, Expertise expertise) throws ExecutionException, InterruptedException {

        CostTimeAgreement costTimeAgreement = new CostTimeAgreement();
        BeanUtils.copyProperties(collectiveTimeAgreementDTO, costTimeAgreement);

        // Set activity Ids according to unit activity Ids
        for (CTARuleTemplateDTO ruleTemplateDTO : collectiveTimeAgreementDTO.getRuleTemplates()) {
            List<Long> parentActivityIds = ruleTemplateDTO.getActivityIds();
            List<Long> unitActivityIds = new ArrayList<Long>();
            parentActivityIds.forEach(parentActivityId -> {
                if (Optional.ofNullable(parentUnitActivityMap).isPresent() && Optional.ofNullable(parentUnitActivityMap.get(parentActivityId)).isPresent()) {
                    unitActivityIds.add(parentUnitActivityMap.get(parentActivityId));
                }
            });
            ruleTemplateDTO.setActivityIds(unitActivityIds);
        }

        CompletableFuture<Boolean> hasUpdated = ApplicationContextProviderNonManageBean.getApplicationContext().getBean(CountryCTAService.class)
                .buildCTA(costTimeAgreement, collectiveTimeAgreementDTO, expertise);

        // Wait until they are all done
        CompletableFuture.allOf(hasUpdated).join();

        this.save(costTimeAgreement);
        return costTimeAgreement;
    }

    public CTARuleTemplate saveEmbeddedEntitiesOfCTARuleTemplate(CTARuleTemplate ctaRuleTemplate, CTARuleTemplateDTO ctaRuleTemplateDTO) {
        if (ctaRuleTemplate.getId() != null) {
            ctaRuleTemplateGraphRepository.detachAllTimeTypesFromCTARuleTemplate(ctaRuleTemplate.getId());
        }

        // Fetch Employment Type
        List<Long> employmentTypeIds = ctaRuleTemplateDTO.getEmploymentTypes();
        ctaRuleTemplate.setEmploymentTypes(employmentTypeGraphRepository.getEmploymentTypeByIds(employmentTypeIds, false));


        Long ruleTemplateId = ctaRuleTemplateDTO.getRuleTemplateCategory();
        if (ruleTemplateId != null) {
            ctaRuleTemplate.setRuleTemplateCategory(ruleTemplateCategoryGraphRepository.findOne(ruleTemplateId));
        }

        return ctaRuleTemplate;
    }

}

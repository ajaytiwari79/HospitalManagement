package com.kairos.service.agreement.cta;

import com.kairos.config.listener.ApplicationContextProviderNonManageBean;
import com.kairos.persistence.model.agreement.cta.CTARuleTemplate;
import com.kairos.persistence.model.agreement.cta.CTARuleTemplateDTO;
import com.kairos.persistence.model.agreement.cta.CostTimeAgreement;
import com.kairos.persistence.model.agreement.cta.RuleTemplate;
import com.kairos.persistence.model.agreement.cta.cta_response.CTADetailsWrapper;
import com.kairos.persistence.model.agreement.cta.cta_response.CollectiveTimeAgreementDTO;
import com.kairos.persistence.model.agreement.wta.templates.RuleTemplateCategory;
import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.country.employment_type.EmploymentType;
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
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.country.EmploymentTypeGraphRepository;
import com.kairos.persistence.repository.user.expertise.ExpertiseGraphRepository;
import com.kairos.rest_client.activity_types.ActivityTypesRestClient;
import com.kairos.service.AsynchronousService;
import com.kairos.service.auth.UserService;
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
import java.util.stream.Collectors;

@Service
@Transactional
public class CountryCTAService {
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
    EmploymentTypeGraphRepository employmentTypeGraphRepository;
    private @Inject
    AccessGroupRepository accessGroupRepository;

    private @Inject
    UserGraphRepository userGraphRepository;
    private @Inject
    ExpertiseGraphRepository expertiseGraphRepository;
    private @Inject
    OrganizationTypeGraphRepository organizationTypeGraphRepository;
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
                .getPreRequisiteForCTA(countryId, collectiveTimeAgreementDTO, ctaDetailsWrapper, false);
        CompletableFuture.allOf(allBasicDetails).join();

        CostTimeAgreement costTimeAgreement = new CostTimeAgreement();
        collectiveTimeAgreementDTO.setId(null);
        // In case of copy CTA need to remove ID of CTA
        BeanUtils.copyProperties(collectiveTimeAgreementDTO, costTimeAgreement);


        costTimeAgreement.setId(null);
        CompletableFuture<Boolean> hasUpdated = ApplicationContextProviderNonManageBean.getApplicationContext().getBean(CountryCTAService.class)
                .buildCTA(costTimeAgreement, collectiveTimeAgreementDTO, ctaDetailsWrapper, false, true);

        // Wait until they are all done
        CompletableFuture.allOf(hasUpdated).join();
        costTimeAgreement.setCountry(ctaDetailsWrapper.getCountry());
        collectiveTimeAgreementGraphRepository.save(costTimeAgreement);

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
    public CompletableFuture<Boolean> getPreRequisiteForCTA(Long countryId, CollectiveTimeAgreementDTO collectiveTimeAgreementDTO, CTADetailsWrapper ctaDetailsWrapper, boolean doUpdate) throws InterruptedException, ExecutionException {
        if (!doUpdate) {
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
        }
        Callable<Optional<Expertise>> callableExpertise = () -> {
            Optional<Expertise> expertise = expertiseGraphRepository.findById(collectiveTimeAgreementDTO.getExpertise(), 0);
            return expertise;
        };
        Future<Optional<Expertise>> futureExpertise = asynchronousService.executeAsynchronously(callableExpertise);
        if (futureExpertise.get().isPresent()) {
            ctaDetailsWrapper.setExpertise(futureExpertise.get().get());
        }
        //
        Set<Long> ruleTemplateCategoryIds = collectiveTimeAgreementDTO.getRuleTemplates().stream().map(CTARuleTemplateDTO::getRuleTemplateCategory).collect(Collectors.toSet());

        Callable<List<RuleTemplateCategory>> callableRuleTemplateCategory = () -> {
            List<RuleTemplateCategory> ruleTemplateCategories = ruleTemplateCategoryGraphRepository.findRuleTemplatesByIds(ruleTemplateCategoryIds);
            return ruleTemplateCategories;
        };
        Future<List<RuleTemplateCategory>> futureRules = asynchronousService.executeAsynchronously(callableRuleTemplateCategory);
        Map<Long, RuleTemplateCategory> ruleTemplateCategoryMap = futureRules.get().stream().collect(Collectors.toMap(RuleTemplateCategory::getId, v -> v));
        ctaDetailsWrapper.setRuleTemplateCategoryIdMap(ruleTemplateCategoryMap);

        Set<Long> employmentTypeIds = collectiveTimeAgreementDTO.getRuleTemplates()
                .stream().flatMap(ctaRuleTemplateDTO ->
                        ctaRuleTemplateDTO.getEmploymentTypes().stream().map(e -> e.longValue()))
                .collect(Collectors.toSet());

        Callable<List<EmploymentType>> callableEmploymentTypes = () -> {
            return employmentTypeGraphRepository.getEmploymentTypeByIds(employmentTypeIds);
            };
        Future<List<EmploymentType>> futureEmploymentTypes = asynchronousService.executeAsynchronously(callableEmploymentTypes);
        Map<Long, EmploymentType> employmentTypeMap = futureEmploymentTypes.get().stream().collect(Collectors.toMap(EmploymentType::getId, v -> v));
        ctaDetailsWrapper.setEmploymentTypeIdMap(employmentTypeMap);


        ctaDetailsWrapper.setAll(true);
        return CompletableFuture.completedFuture(true);

    }

    @Async
    public CompletableFuture<Boolean> buildCTA(CostTimeAgreement costTimeAgreement, CollectiveTimeAgreementDTO collectiveTimeAgreementDTO, CTADetailsWrapper ctaDetailsWrapper, boolean doUpdate, boolean creatingFromCountry)
            throws InterruptedException, ExecutionException {
        // Get Rule Templates
        Callable<List<RuleTemplate>> ctaRuleTemplatesCallable = () -> {
            List<RuleTemplate> ruleTemplates = new ArrayList<>();
            for (CTARuleTemplateDTO ctaRuleTemplateDTO : collectiveTimeAgreementDTO.getRuleTemplates()) {
                CTARuleTemplate ctaRuleTemplate = new CTARuleTemplate();
                BeanUtils.copyProperties(ctaRuleTemplateDTO, ctaRuleTemplate);
                if (!doUpdate || (doUpdate && !ctaDetailsWrapper.getSelectedRuleTemplateIds().contains(ctaRuleTemplate.getId()))) {
                    ctaRuleTemplate.cloneCTARuleTemplate();
                }
                CTARuleTemplate.setActivityBasesCostCalculationSettings(ctaRuleTemplate);
                ctaRuleTemplate = saveEmbeddedEntitiesOfCTARuleTemplate(ctaRuleTemplate, ctaRuleTemplateDTO, ctaDetailsWrapper);
                ruleTemplates.add(ctaRuleTemplate);
            }
            return ruleTemplates;
        };
        Future<List<RuleTemplate>> ctaRuleTemplatesFuture = asynchronousService.executeAsynchronously(ctaRuleTemplatesCallable);

        costTimeAgreement.setExpertise(ctaDetailsWrapper.getExpertise());
        // if creating fro country and we are not updating then only.
        if (creatingFromCountry && !doUpdate) {
            costTimeAgreement.setOrganizationType(ctaDetailsWrapper.getOrganizationType());
            costTimeAgreement.setOrganizationSubType(ctaDetailsWrapper.getOrganizationSubType());
        }
        costTimeAgreement.setRuleTemplates(ctaRuleTemplatesFuture.get());
        costTimeAgreement.setStartDateMillis(collectiveTimeAgreementDTO.getStartDateMillis());
        costTimeAgreement.setEndDateMillis(collectiveTimeAgreementDTO.getEndDateMillis());

        return CompletableFuture.completedFuture(true);
    }

    public Boolean publishNewCountryCTAToOrganizationByOrgSubType(Long countryId, CostTimeAgreement costTimeAgreement, CollectiveTimeAgreementDTO collectiveTimeAgreementDTO, Long organizationSubTypeId, CTADetailsWrapper ctaDetailsWrapper) throws ExecutionException, InterruptedException {
        List<Organization> organizations = organizationTypeRepository.getOrganizationsByOrganizationType(organizationSubTypeId);
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
                CostTimeAgreement newCostTimeAgreement = createCostTimeAgreementForOrganization(collectiveTimeAgreementDTO, unitActivities.get(organization.getId()), ctaDetailsWrapper);
                organization.getCostTimeAgreements().add(newCostTimeAgreement);
//               newCostTimeAgreement.setParentCountryCTA(costTimeAgreement);
                collectiveTimeAgreementGraphRepository.linkParentCountryCTAToOrganization(costTimeAgreement.getId(), newCostTimeAgreement.getId());
                // save(organization);
            } catch (Exception e) {
                // Exception occured
                logger.info("Exception occured on setting cta_response to organization");
            }

        });
        organizationGraphRepository.saveAll(organizations);
        return true;
    }

    public CostTimeAgreement createCostTimeAgreementForOrganization(CollectiveTimeAgreementDTO collectiveTimeAgreementDTO, HashMap<Long, Long> parentUnitActivityMap, CTADetailsWrapper ctaDetailsWrapper) throws ExecutionException, InterruptedException {

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
                .buildCTA(costTimeAgreement, collectiveTimeAgreementDTO, ctaDetailsWrapper, false, false);

        // Wait until they are all done
        CompletableFuture.allOf(hasUpdated).join();

        collectiveTimeAgreementGraphRepository.save(costTimeAgreement);
        return costTimeAgreement;
    }

    public CollectiveTimeAgreementDTO updateCostTimeAgreement(Long countryId, Long unitId, Long ctaId, CollectiveTimeAgreementDTO collectiveTimeAgreementDTO) throws ExecutionException, InterruptedException {

        if (countryId != null && collectiveTimeAgreementGraphRepository.isCTAExistWithSameNameInCountry(countryId, collectiveTimeAgreementDTO.getName(), ctaId)) {
            exceptionService.duplicateDataException("message.cta.name.alreadyExist", collectiveTimeAgreementDTO.getName());
        } else if (unitId != null && collectiveTimeAgreementGraphRepository.isCTAExistWithSameNameInUnit(unitId, collectiveTimeAgreementDTO.getName(), ctaId)) {
            exceptionService.duplicateDataException("message.cta.name.alreadyExist", collectiveTimeAgreementDTO.getName());
        }
        CostTimeAgreement costTimeAgreement = collectiveTimeAgreementGraphRepository.findOne(ctaId, 2);


        CTADetailsWrapper ctaDetailsWrapper = new CTADetailsWrapper();
        CompletableFuture<Boolean> allBasicDetails = ApplicationContextProviderNonManageBean.getApplicationContext().getBean(CountryCTAService.class)
                .getPreRequisiteForCTA(countryId, collectiveTimeAgreementDTO, ctaDetailsWrapper, true);
        CompletableFuture.allOf(allBasicDetails).join();


        List<Long> previousRuleTemplateIds = new ArrayList<>();
        logger.info("costTimeAgreement.getRuleTemplateIds() : {}", costTimeAgreement.getRuleTemplates().size());
        for (RuleTemplate ruleTemplate : costTimeAgreement.getRuleTemplates()) {
            previousRuleTemplateIds.add(ruleTemplate.getId());
        }
        ctaDetailsWrapper.setSelectedRuleTemplateIds(previousRuleTemplateIds);
        BeanUtils.copyProperties(collectiveTimeAgreementDTO, costTimeAgreement);
        costTimeAgreement.setName(collectiveTimeAgreementDTO.getName());
        costTimeAgreement.setDescription(collectiveTimeAgreementDTO.getDescription());
        CompletableFuture<Boolean> hasUpdated = ApplicationContextProviderNonManageBean.getApplicationContext().getBean(CountryCTAService.class)
                .buildCTA(costTimeAgreement, collectiveTimeAgreementDTO, ctaDetailsWrapper, true, countryId != null);
        CompletableFuture.allOf(hasUpdated).join();

        collectiveTimeAgreementGraphRepository.save(costTimeAgreement);
        return collectiveTimeAgreementDTO;
    }


    public CTARuleTemplate saveEmbeddedEntitiesOfCTARuleTemplate(CTARuleTemplate ctaRuleTemplate, CTARuleTemplateDTO ctaRuleTemplateDTO, CTADetailsWrapper ctaDetailsWrapper) {

        ctaRuleTemplate.setEmploymentTypes(new ArrayList<>());
        if (!ctaRuleTemplateDTO.getEmploymentTypes().isEmpty()) {
            for (Iterator<Long> iterator = ctaRuleTemplateDTO.getEmploymentTypes().iterator(); iterator.hasNext(); ) {
                Long value = iterator.next();
                ctaRuleTemplate.addEmploymentType(ctaDetailsWrapper.getEmploymentTypeIdMap().get(value));
            }
        }
        if (ctaRuleTemplateDTO.getRuleTemplateCategory() != null) {
            ctaRuleTemplate.setRuleTemplateCategory(ctaDetailsWrapper.getRuleTemplateCategoryIdMap().get(ctaRuleTemplateDTO.getRuleTemplateCategory()));
        }

        return ctaRuleTemplate;
    }

}

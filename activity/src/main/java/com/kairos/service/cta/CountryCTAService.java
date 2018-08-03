package com.kairos.service.cta;

import com.kairos.activity.cta.CTABasicDetailsDTO;
import com.kairos.activity.cta.CTARuleTemplateDTO;
import com.kairos.activity.cta.CollectiveTimeAgreementDTO;
import com.kairos.enums.IntegrationOperation;
import com.kairos.persistence.model.cta.CTARuleTemplate;
import com.kairos.persistence.model.cta.CostTimeAgreement;
import com.kairos.persistence.repository.cta.CostTimeAgreementRepository;
import com.kairos.rest_client.GenericRestClient;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * @author pradeep
 * @date - 30/7/18
 */

@Service
@Transactional
public class CountryCTAService extends MongoBaseService {
    private Logger logger = LoggerFactory.getLogger(CountryCTAService.class);
    private @Inject
    ExceptionService exceptionService;
    @Inject
    private CostTimeAgreementRepository costTimeAgreementRepository;
    @Inject
    private GenericRestClient genericRestClient;

    public CollectiveTimeAgreementDTO createCostTimeAgreementInCountry(Long countryId, CollectiveTimeAgreementDTO collectiveTimeAgreementDTO) throws ExecutionException, InterruptedException {
        logger.info("saving CostTimeAgreement country {}", countryId);
        if (costTimeAgreementRepository.isCTAExistWithSameNameInCountry(countryId, collectiveTimeAgreementDTO.getName())) {
            exceptionService.duplicateDataException("message.cta.name.alreadyExist", collectiveTimeAgreementDTO.getName());

        }
        List<NameValuePair> requestParam = new ArrayList<>();
        requestParam.add(new BasicNameValuePair("countryId", countryId.toString()));
        requestParam.add(new BasicNameValuePair("organizationSubTypeId", collectiveTimeAgreementDTO.getOrganizationSubType().toString()));
        requestParam.add(new BasicNameValuePair("expertiseId", collectiveTimeAgreementDTO.getExpertise().toString()));
        requestParam.add(new BasicNameValuePair("organizationTypeId", collectiveTimeAgreementDTO.getOrganizationType().toString()));
        genericRestClient.publishRequest(null, null, false, IntegrationOperation.GET, "", requestParam, CTABasicDetailsDTO.class);
        CostTimeAgreement costTimeAgreement = new CostTimeAgreement();
        collectiveTimeAgreementDTO.setId(null);
        // In case of copy CTA need to remove ID of CTA
        BeanUtils.copyProperties(collectiveTimeAgreementDTO, costTimeAgreement);


        costTimeAgreement.setId(null);
        buildCTA(costTimeAgreement, collectiveTimeAgreementDTO, ctaDetailsWrapper, false, true);

        costTimeAgreement.setCountryId(countryId);
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


    public void buildCTA(CostTimeAgreement costTimeAgreement, CollectiveTimeAgreementDTO collectiveTimeAgreementDTO, CTADetailsWrapper ctaDetailsWrapper, boolean doUpdate, boolean creatingFromCountry)
            throws InterruptedException, ExecutionException {
        // Get Rule Templates
        List<CTARuleTemplate> ruleTemplates = new ArrayList<>();
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
        save(ruleTemplates);
        List<BigInteger> ruleTemplateIds = ruleTemplates.stream().map(rt->rt.getId()).collect(CExpertiseServiceollectors.toList());
        costTimeAgreement.setExpertise(ctaDetailsWrapper.getExpertise());
        // if creating fro country and we are not updating then only.
        if (creatingFromCountry && !doUpdate) {
            costTimeAgreement.setOrganizationType();
            costTimeAgreement.setOrganizationSubType();
        }
        costTimeAgreement.setRuleTemplateIds(ruleTemplateIds);
        costTimeAgreement.setStartDateMillis(collectiveTimeAgreementDTO.getStartDateMillis());
        costTimeAgreement.setEndDateMillis(collectiveTimeAgreementDTO.getEndDateMillis());
    }

    public Boolean publishNewCountryCTAToOrganizationByOrgSubType(Long countryId, CostTimeAgreement costTimeAgreement, CollectiveTimeAgreementDTO collectiveTimeAgreementDTO, Long organizationSubTypeId, CTADetailsWrapper ctaDetailsWrapper) {
        List<Organization> organizations = organizationTypeRepository.getOrganizationsByOrganizationType(organizationSubTypeId);
        List<Long> organizationIds = new ArrayList<>();
        List<BigInteger> activityIds = new ArrayList<>();
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
//               newCostTimeAgreement.setParentCountryCTAId(costTimeAgreement);
                costTimeAgreementRepository.linkParentCountryCTAToOrganization(costTimeAgreement.getId(), newCostTimeAgreement.getId());
                // save(organization);
            } catch (Exception e) {
                // Exception occured
                logger.info("Exception occured on setting cta_response to organization");
            }

        });
        save(organizations);
        return true;
    }

    public CostTimeAgreement createCostTimeAgreementForOrganization(CollectiveTimeAgreementDTO collectiveTimeAgreementDTO, HashMap<BigInteger, BigInteger> parentUnitActivityMap, CTADetailsWrapper ctaDetailsWrapper) throws ExecutionException, InterruptedException {

        CostTimeAgreement costTimeAgreement = new CostTimeAgreement();
        BeanUtils.copyProperties(collectiveTimeAgreementDTO, costTimeAgreement);

        // Set activity Ids according to unit activity Ids
        for (CTARuleTemplateDTO ruleTemplateDTO : collectiveTimeAgreementDTO.getRuleTemplates()) {
            List<BigInteger> parentActivityIds = ruleTemplateDTO.getActivityIds();
            List<BigInteger> unitActivityIds = new ArrayList<BigInteger>();
            parentActivityIds.forEach(parentActivityId -> {
                if (Optional.ofNullable(parentUnitActivityMap).isPresent() && Optional.ofNullable(parentUnitActivityMap.get(parentActivityId)).isPresent()) {
                    unitActivityIds.add(parentUnitActivityMap.get(parentActivityId));
                }
            });
            ruleTemplateDTO.setActivityIds(unitActivityIds);
        }

        buildCTA(costTimeAgreement, collectiveTimeAgreementDTO, ctaDetailsWrapper, false, false);

        this.save(costTimeAgreement);
        return costTimeAgreement;
    }

    public CollectiveTimeAgreementDTO updateCostTimeAgreement(Long countryId, Long unitId, BigInteger ctaId, CollectiveTimeAgreementDTO collectiveTimeAgreementDTO) throws ExecutionException, InterruptedException {

        if (countryId != null && costTimeAgreementRepository.isCTAExistWithSameNameInCountry(countryId, collectiveTimeAgreementDTO.getName(), ctaId)) {
            exceptionService.duplicateDataException("message.cta.name.alreadyExist", collectiveTimeAgreementDTO.getName());
        } else if (unitId != null && costTimeAgreementRepository.isCTAExistWithSameNameInUnit(unitId, collectiveTimeAgreementDTO.getName(), ctaId)) {
            exceptionService.duplicateDataException("message.cta.name.alreadyExist", collectiveTimeAgreementDTO.getName());
        }
        CostTimeAgreement costTimeAgreement = costTimeAgreementRepository.findOne(ctaId);


        List<NameValuePair> requestParam = new ArrayList<>();
        requestParam.add(new BasicNameValuePair("countryId", countryId.toString()));
        requestParam.add(new BasicNameValuePair("organizationSubTypeId", collectiveTimeAgreementDTO.getOrganizationSubType().toString()));
        requestParam.add(new BasicNameValuePair("expertiseId", collectiveTimeAgreementDTO.getExpertise().toString()));
        requestParam.add(new BasicNameValuePair("organizationTypeId", collectiveTimeAgreementDTO.getOrganizationType().toString()));
        genericRestClient.publishRequest(null, null, false, IntegrationOperation.GET, "", requestParam, CTABasicDetailsDTO.class);
        logger.info("costTimeAgreement.getRuleTemplateIds() : {}", costTimeAgreement.getRuleTemplateIds().size());
        ctaDetailsWrapper.setSelectedRuleTemplateIds(costTimeAgreement.getRuleTemplateIds());
        BeanUtils.copyProperties(collectiveTimeAgreementDTO, costTimeAgreement);
        costTimeAgreement.setName(collectiveTimeAgreementDTO.getName());
        costTimeAgreement.setDescription(collectiveTimeAgreementDTO.getDescription());
        buildCTA(costTimeAgreement, collectiveTimeAgreementDTO, ctaDetailsWrapper, true, countryId != null);
        this.save(costTimeAgreement);
        return collectiveTimeAgreementDTO;
    }


    public CTARuleTemplate saveEmbeddedEntitiesOfCTARuleTemplate(CTARuleTemplate ctaRuleTemplate, CTARuleTemplateDTO ctaRuleTemplateDTO, CTADetailsWrapper ctaDetailsWrapper) {

        if (!ctaRuleTemplateDTO.getEmploymentTypes().isEmpty()) {
            ctaRuleTemplateDTO.getEmploymentTypes().forEach(c -> {
                ctaRuleTemplate.getEmploymentTypes().add(ctaDetailsWrapper.getEmploymentTypeIdMap().get(c));
            });

        }
        if (ctaRuleTemplateDTO.getRuleTemplateCategory() != null) {
            ctaRuleTemplate.setRuleTemplateCategory(ctaDetailsWrapper.getRuleTemplateCategoryIdMap().get(ctaRuleTemplateDTO.getRuleTemplateCategory()));
        }

        return ctaRuleTemplate;
    }

}

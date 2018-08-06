package com.kairos.service.cta;

import com.kairos.activity.cta.CTABasicDetailsDTO;
import com.kairos.activity.cta.CTARuleTemplateDTO;
import com.kairos.activity.cta.CollectiveTimeAgreementDTO;
import com.kairos.enums.IntegrationOperation;
import com.kairos.persistence.model.cta.CTARuleTemplate;
import com.kairos.persistence.model.cta.CostTimeAgreement;
import com.kairos.persistence.model.wta.Expertise;
import com.kairos.persistence.model.wta.OrganizationType;
import com.kairos.persistence.repository.cta.CostTimeAgreementRepository;
import com.kairos.rest_client.GenericRestClient;
import com.kairos.service.MongoBaseService;
import com.kairos.service.activity.ActivityService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.util.ObjectMapperUtils;
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

import static com.kairos.constants.ApiConstants.CTA_BASIC_INFO;

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
    @Inject private ActivityService activityService;

    public CollectiveTimeAgreementDTO createCostTimeAgreementInCountry(Long countryId, CollectiveTimeAgreementDTO collectiveTimeAgreementDTO) {
        logger.info("saving CostTimeAgreement country {}", countryId);
        if (costTimeAgreementRepository.isCTAExistWithSameNameInCountry(countryId, collectiveTimeAgreementDTO.getName())) {
            exceptionService.duplicateDataException("message.cta.name.alreadyExist", collectiveTimeAgreementDTO.getName());

        }
        List<NameValuePair> requestParam = new ArrayList<>();
        requestParam.add(new BasicNameValuePair("organizationSubTypeId", collectiveTimeAgreementDTO.getOrganizationSubType().toString()));
        requestParam.add(new BasicNameValuePair("expertiseId", collectiveTimeAgreementDTO.getExpertise().toString()));
        CTABasicDetailsDTO ctaBasicDetailsDTO = genericRestClient.publishRequest(null, null, false, IntegrationOperation.GET, CTA_BASIC_INFO, requestParam, CTABasicDetailsDTO.class);
        CostTimeAgreement costTimeAgreement = ObjectMapperUtils.copyPropertiesByMapper(collectiveTimeAgreementDTO, CostTimeAgreement.class);
costTimeAgreement.setId(null);
        buildCTA(costTimeAgreement, collectiveTimeAgreementDTO,  false, true,ctaBasicDetailsDTO);

        costTimeAgreement.setCountryId(countryId);
        this.save(costTimeAgreement);

        // TO create CTA for organizations too which are linked with same sub type
        publishNewCountryCTAToOrganizationByOrgSubType(countryId, costTimeAgreement, collectiveTimeAgreementDTO, costTimeAgreement.getOrganizationSubType().getId(),ctaBasicDetailsDTO);

        collectiveTimeAgreementDTO.setId(costTimeAgreement.getId());
        return collectiveTimeAgreementDTO;
    }


    public void buildCTA(CostTimeAgreement costTimeAgreement, CollectiveTimeAgreementDTO collectiveTimeAgreementDTO, boolean doUpdate, boolean creatingFromCountry,CTABasicDetailsDTO ctaBasicDetailsDTO){
        // Get Rule Templates
        List<CTARuleTemplate> ruleTemplates = new ArrayList<>();
        for (CTARuleTemplateDTO ctaRuleTemplateDTO : collectiveTimeAgreementDTO.getRuleTemplates()) {
            CTARuleTemplate ctaRuleTemplate = ObjectMapperUtils.copyPropertiesByMapper(ctaRuleTemplateDTO, CTARuleTemplate.class);
            CTARuleTemplate.setActivityBasesCostCalculationSettings(ctaRuleTemplate);
            ruleTemplates.add(ctaRuleTemplate);
        }
        save(ruleTemplates);
        List<BigInteger> ruleTemplateIds = ruleTemplates.stream().map(rt->rt.getId()).collect(Collectors.toList());
        costTimeAgreement.setExpertise(new Expertise(ctaBasicDetailsDTO.getExpertise().getId(),ctaBasicDetailsDTO.getExpertise().getName(),ctaBasicDetailsDTO.getExpertise().getDescription()));
        // if creating fro country and we are not updating then only.
        if (creatingFromCountry && !doUpdate) {
            costTimeAgreement.setOrganizationType(new OrganizationType(ctaBasicDetailsDTO.getOrganizationType().getId(),ctaBasicDetailsDTO.getOrganizationType().getName(),ctaBasicDetailsDTO.getOrganizationType().getDescription()));
            costTimeAgreement.setOrganizationSubType(new OrganizationType(ctaBasicDetailsDTO.getOrganizationSubType().getId(),ctaBasicDetailsDTO.getOrganizationSubType().getName(),ctaBasicDetailsDTO.getOrganizationSubType().getDescription()));
        }
        costTimeAgreement.setRuleTemplateIds(ruleTemplateIds);
        costTimeAgreement.setStartDateMillis(collectiveTimeAgreementDTO.getStartDateMillis());
        costTimeAgreement.setEndDateMillis(collectiveTimeAgreementDTO.getEndDateMillis());
    }

    public Boolean publishNewCountryCTAToOrganizationByOrgSubType(Long countryId, CostTimeAgreement costTimeAgreement, CollectiveTimeAgreementDTO collectiveTimeAgreementDTO, Long organizationSubTypeId,CTABasicDetailsDTO ctaBasicDetailsDTO) {
        List<Long> organizationIds = ctaBasicDetailsDTO.getOrganizations().stream().map(o->o.getId()).collect(Collectors.toList());
        List<BigInteger> activityIds = collectiveTimeAgreementDTO.getRuleTemplates().stream().filter(ruleTemp->Optional.ofNullable(ruleTemp.getActivityIds()).isPresent()).flatMap(ctaRuleTemplateDTO -> ctaRuleTemplateDTO.getActivityIds().stream()).collect(Collectors.toList());


        HashMap<Long, HashMap<Long, BigInteger>> unitActivities = activityService.getListOfActivityIdsOfUnitByParentIds(activityIds,organizationIds);
        List<CostTimeAgreement> costTimeAgreements = new ArrayList<>(organizationIds.size());
        organizationIds.forEach(organizationId ->{
                CostTimeAgreement newCostTimeAgreement = createCostTimeAgreementForOrganization(collectiveTimeAgreementDTO, unitActivities.get(organizationId),ctaBasicDetailsDTO);
                costTimeAgreements.add(newCostTimeAgreement);

        });
        save(costTimeAgreements);
        return true;
    }

    public CostTimeAgreement createCostTimeAgreementForOrganization(CollectiveTimeAgreementDTO collectiveTimeAgreementDTO, HashMap<Long, BigInteger> parentUnitActivityMap,CTABasicDetailsDTO ctaBasicDetailsDTO) {

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

        buildCTA(costTimeAgreement, collectiveTimeAgreementDTO, false, false,ctaBasicDetailsDTO);

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
        requestParam.add(new BasicNameValuePair("organizationSubTypeId", collectiveTimeAgreementDTO.getOrganizationSubType().toString()));
        requestParam.add(new BasicNameValuePair("expertiseId", collectiveTimeAgreementDTO.getExpertise().toString()));
        CTABasicDetailsDTO ctaBasicDetailsDTO = genericRestClient.publishRequest(null, null, false, IntegrationOperation.GET, CTA_BASIC_INFO, requestParam, CTABasicDetailsDTO.class);
        logger.info("costTimeAgreement.getRuleTemplateIds() : {}", costTimeAgreement.getRuleTemplateIds().size());
        BeanUtils.copyProperties(collectiveTimeAgreementDTO, costTimeAgreement);
        costTimeAgreement.setName(collectiveTimeAgreementDTO.getName());
        costTimeAgreement.setDescription(collectiveTimeAgreementDTO.getDescription());
        buildCTA(costTimeAgreement, collectiveTimeAgreementDTO,  true, countryId != null,ctaBasicDetailsDTO);
        this.save(costTimeAgreement);
        return collectiveTimeAgreementDTO;
    }





}

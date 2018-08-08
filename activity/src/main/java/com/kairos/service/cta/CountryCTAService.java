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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
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
    @Inject private ExceptionService exceptionService;
    @Inject private CostTimeAgreementRepository costTimeAgreementRepository;
    @Inject private GenericRestClient genericRestClient;
    @Inject private ActivityService activityService;

    /**
     *
     * @param countryId
     * @param collectiveTimeAgreementDTO
     * @return
     */
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
        publishNewCountryCTAToOrganizationByOrgSubType(costTimeAgreement, collectiveTimeAgreementDTO, ctaBasicDetailsDTO);
        collectiveTimeAgreementDTO.setId(costTimeAgreement.getId());
        return collectiveTimeAgreementDTO;
    }


    /**
     *
     * @param costTimeAgreement
     * @param collectiveTimeAgreementDTO
     * @param doUpdate
     * @param creatingFromCountry
     * @param ctaBasicDetailsDTO
     */
    public void buildCTA(CostTimeAgreement costTimeAgreement, CollectiveTimeAgreementDTO collectiveTimeAgreementDTO, boolean doUpdate, boolean creatingFromCountry,CTABasicDetailsDTO ctaBasicDetailsDTO){
        // Get Rule Templates
        List<CTARuleTemplate> ruleTemplates = ObjectMapperUtils.copyPropertiesOfListByMapper(collectiveTimeAgreementDTO.getRuleTemplates(),CTARuleTemplate.class);
        save(ruleTemplates);
        List<BigInteger> ruleTemplateIds = ruleTemplates.stream().map(rt->rt.getId()).collect(Collectors.toList());
        if(creatingFromCountry) {
            Expertise expertise = new Expertise(ctaBasicDetailsDTO.getExpertise().getId(), ctaBasicDetailsDTO.getExpertise().getName(), ctaBasicDetailsDTO.getExpertise().getDescription());
            costTimeAgreement.setExpertise(expertise);
            if (!doUpdate) {
                OrganizationType organizationType = new OrganizationType(ctaBasicDetailsDTO.getOrganizationType().getId(), ctaBasicDetailsDTO.getOrganizationType().getName(), ctaBasicDetailsDTO.getOrganizationType().getDescription());
                costTimeAgreement.setOrganizationType(organizationType);
                OrganizationType organizationSubType = new OrganizationType(ctaBasicDetailsDTO.getOrganizationSubType().getId(), ctaBasicDetailsDTO.getOrganizationSubType().getName(), ctaBasicDetailsDTO.getOrganizationSubType().getDescription());
                costTimeAgreement.setOrganizationSubType(organizationSubType);
            }
        }
        costTimeAgreement.setRuleTemplateIds(ruleTemplateIds);
        costTimeAgreement.setStartDateMillis(collectiveTimeAgreementDTO.getStartDateMillis());
        costTimeAgreement.setEndDateMillis(collectiveTimeAgreementDTO.getEndDateMillis());
    }


    /**
     *
     * @param costTimeAgreement
     * @param collectiveTimeAgreementDTO
     * @param ctaBasicDetailsDTO
     * @return
     */
    public Boolean publishNewCountryCTAToOrganizationByOrgSubType( CostTimeAgreement costTimeAgreement, CollectiveTimeAgreementDTO collectiveTimeAgreementDTO,CTABasicDetailsDTO ctaBasicDetailsDTO) {
        List<Long> organizationIds = ctaBasicDetailsDTO.getOrganizations().stream().map(o->o.getId()).collect(Collectors.toList());
        List<BigInteger> activityIds = collectiveTimeAgreementDTO.getRuleTemplates().stream().filter(ruleTemp->Optional.ofNullable(ruleTemp.getActivityIds()).isPresent()).flatMap(ctaRuleTemplateDTO -> ctaRuleTemplateDTO.getActivityIds().stream()).collect(Collectors.toList());
        HashMap<Long, HashMap<Long, BigInteger>> unitActivities = activityService.getListOfActivityIdsOfUnitByParentIds(activityIds,organizationIds);
        List<CostTimeAgreement> costTimeAgreements = new ArrayList<>(organizationIds.size());
        costTimeAgreement.setCountryId(null);
        organizationIds.forEach(organizationId ->{
                CostTimeAgreement newCostTimeAgreement = createCostTimeAgreementForOrganization(costTimeAgreement,collectiveTimeAgreementDTO, unitActivities.get(organizationId),ctaBasicDetailsDTO);
                costTimeAgreements.add(newCostTimeAgreement);

        });
        save(costTimeAgreements);
        return true;
    }


    /**
     *
     * @param costTimeAgreement
     * @param collectiveTimeAgreementDTO
     * @param parentUnitActivityMap
     * @param ctaBasicDetailsDTO
     * @return
     */
    public CostTimeAgreement createCostTimeAgreementForOrganization(CostTimeAgreement costTimeAgreement,CollectiveTimeAgreementDTO collectiveTimeAgreementDTO, HashMap<Long, BigInteger> parentUnitActivityMap,CTABasicDetailsDTO ctaBasicDetailsDTO) {
        CostTimeAgreement organisationCTA = ObjectMapperUtils.copyPropertiesByMapper(costTimeAgreement, CostTimeAgreement.class);
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
        organisationCTA.setParentCountryCTAId(costTimeAgreement.getId());
        buildCTA(organisationCTA, collectiveTimeAgreementDTO, false, false,ctaBasicDetailsDTO);
        return organisationCTA;
    }


    /**
     *
     * @param countryId
     * @param ctaId
     * @param collectiveTimeAgreementDTO
     * @return
     */
    public CollectiveTimeAgreementDTO updateCostTimeAgreementInCountry(Long countryId,  BigInteger ctaId, CollectiveTimeAgreementDTO collectiveTimeAgreementDTO) {
        boolean ctaExistsInCountry = costTimeAgreementRepository.isCTAExistWithSameNameInCountry(countryId, collectiveTimeAgreementDTO.getName(), ctaId);
        if (ctaExistsInCountry) {
            exceptionService.duplicateDataException("message.cta.name.alreadyExist", collectiveTimeAgreementDTO.getName());
        }
        CostTimeAgreement costTimeAgreement = costTimeAgreementRepository.findOne(ctaId);

        List<NameValuePair> requestParam = new ArrayList<>();
        requestParam.add(new BasicNameValuePair("organizationSubTypeId", collectiveTimeAgreementDTO.getOrganizationSubType().toString()));
        requestParam.add(new BasicNameValuePair("expertiseId", collectiveTimeAgreementDTO.getExpertise().toString()));
        CTABasicDetailsDTO ctaBasicDetailsDTO = genericRestClient.publishRequest(null, null, false, IntegrationOperation.GET, CTA_BASIC_INFO, requestParam, CTABasicDetailsDTO.class);

        logger.info("costTimeAgreement.getRuleTemplateIds() : {}", costTimeAgreement.getRuleTemplateIds().size());

        CostTimeAgreement updateCostTimeAgreement = ObjectMapperUtils.copyPropertiesByMapper(collectiveTimeAgreementDTO, CostTimeAgreement.class);
        updateCostTimeAgreement.setId(costTimeAgreement.getId());
        updateCostTimeAgreement.setName(collectiveTimeAgreementDTO.getName());
        updateCostTimeAgreement.setDescription(collectiveTimeAgreementDTO.getDescription());
        buildCTA(updateCostTimeAgreement, collectiveTimeAgreementDTO,  true, true,ctaBasicDetailsDTO);
        this.save(updateCostTimeAgreement);
        return collectiveTimeAgreementDTO;
    }


    /**
     *
     * @param unitId
     * @param ctaId
     * @param collectiveTimeAgreementDTO
     * @return
     */
    public CollectiveTimeAgreementDTO updateCostTimeAgreementInUnit( Long unitId, BigInteger ctaId, CollectiveTimeAgreementDTO collectiveTimeAgreementDTO) {
        boolean ctaExistInUnit = costTimeAgreementRepository.isCTAExistWithSameNameInUnit(unitId, collectiveTimeAgreementDTO.getName(), ctaId);
        if (ctaExistInUnit) {
            exceptionService.duplicateDataException("message.cta.name.alreadyExist", collectiveTimeAgreementDTO.getName());
        }
        CostTimeAgreement costTimeAgreement = costTimeAgreementRepository.findOne(ctaId);

        List<NameValuePair> requestParam = new ArrayList<>();
        requestParam.add(new BasicNameValuePair("organizationSubTypeId", collectiveTimeAgreementDTO.getOrganizationSubType().toString()));
        requestParam.add(new BasicNameValuePair("expertiseId", collectiveTimeAgreementDTO.getExpertise().toString()));

        logger.info("costTimeAgreement.getRuleTemplateIds() : {}", costTimeAgreement.getRuleTemplateIds().size());

        CostTimeAgreement updateCostTimeAgreement = ObjectMapperUtils.copyPropertiesByMapper(collectiveTimeAgreementDTO, CostTimeAgreement.class);
        updateCostTimeAgreement.setId(costTimeAgreement.getId());
        updateCostTimeAgreement.setName(collectiveTimeAgreementDTO.getName());
        updateCostTimeAgreement.setDescription(collectiveTimeAgreementDTO.getDescription());
        buildCTA(updateCostTimeAgreement, collectiveTimeAgreementDTO,  true, false,null);
        this.save(updateCostTimeAgreement);
        return collectiveTimeAgreementDTO;
    }




}

package com.kairos.service.cta;

import com.kairos.dto.activity.cta.CTABasicDetailsDTO;
import com.kairos.dto.activity.cta.CollectiveTimeAgreementDTO;
import com.kairos.enums.phase.PhaseDefaultName;
import com.kairos.persistence.model.cta.CTARuleTemplate;
import com.kairos.persistence.model.cta.CostTimeAgreement;
import com.kairos.persistence.model.phase.Phase;
import com.kairos.persistence.model.wta.Expertise;
import com.kairos.persistence.model.wta.Organization;
import com.kairos.persistence.model.wta.OrganizationType;
import com.kairos.persistence.repository.cta.CostTimeAgreementRepository;
import com.kairos.persistence.repository.phase.PhaseMongoRepository;
import com.kairos.rest_client.GenericIntegrationService;
import com.kairos.service.MongoBaseService;
import com.kairos.service.activity.ActivityService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.dto.user.organization.OrganizationBasicDTO;
import com.kairos.commons.utils.ObjectMapperUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

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
    @Inject private GenericIntegrationService genericIntegrationService;
    @Inject private ActivityService activityService;
    @Inject private CostTimeAgreementService costTimeAgreementService;
    @Inject private PhaseMongoRepository phaseMongoRepository;

    /**
     *
     * @param countryId
     * @param collectiveTimeAgreementDTO
     * @return
     */
    public CollectiveTimeAgreementDTO createCostTimeAgreementInCountry(Long countryId, CollectiveTimeAgreementDTO collectiveTimeAgreementDTO,boolean mapWithOrgType) {
        logger.info("saving CostTimeAgreement country {}", countryId);
        boolean ctaValid = mapWithOrgType?costTimeAgreementRepository.isCTAExistWithSameOrgTypeAndSubType(collectiveTimeAgreementDTO.getOrganizationType().getId(),collectiveTimeAgreementDTO.getOrganizationSubType().getId(),collectiveTimeAgreementDTO.getName()):costTimeAgreementRepository.isCTAExistWithSameNameInCountry(countryId, collectiveTimeAgreementDTO.getName());
        if (ctaValid) {
            exceptionService.duplicateDataException("message.cta.name.alreadyExist", collectiveTimeAgreementDTO.getName());
        }
        List<NameValuePair> requestParam = new ArrayList<>();
        requestParam.add(new BasicNameValuePair("organizationSubTypeId", collectiveTimeAgreementDTO.getOrganizationSubType().getId().toString()));
        requestParam.add(new BasicNameValuePair("expertiseId", collectiveTimeAgreementDTO.getExpertise().getId().toString()));
        CTABasicDetailsDTO ctaBasicDetailsDTO = genericIntegrationService.getCtaBasicDetailsDTO(countryId,requestParam);
        CostTimeAgreement costTimeAgreement = ObjectMapperUtils.copyPropertiesByMapper(collectiveTimeAgreementDTO, CostTimeAgreement.class);
        costTimeAgreement.setId(null);
        buildCTA(null,costTimeAgreement, collectiveTimeAgreementDTO,  false, true,ctaBasicDetailsDTO,null);

        costTimeAgreement.setCountryId(countryId);
        this.save(costTimeAgreement);
        // TO create CTA for organizations too which are linked with same sub type
        publishNewCTAToOrganizationByOrgSubType(null,costTimeAgreement, collectiveTimeAgreementDTO, ctaBasicDetailsDTO);
        collectiveTimeAgreementDTO.setId(costTimeAgreement.getId());
        return ObjectMapperUtils.copyPropertiesByMapper(costTimeAgreement,CollectiveTimeAgreementDTO.class);
    }

    public CollectiveTimeAgreementDTO createCostTimeAgreementInOrganization(Long unitId, CollectiveTimeAgreementDTO collectiveTimeAgreementDTO) {

        boolean ctaExistInOrganization = costTimeAgreementRepository.isCTAExistWithSameNameInOrganization(unitId, collectiveTimeAgreementDTO.getName());
        if (ctaExistInOrganization) {
            exceptionService.duplicateDataException("message.cta.name.alreadyExist", collectiveTimeAgreementDTO.getName());
        }

        List<NameValuePair> requestParam = new ArrayList<>();
        requestParam.add(new BasicNameValuePair("organizationSubTypeId", collectiveTimeAgreementDTO.getOrganizationSubType().getId().toString()));
        requestParam.add(new BasicNameValuePair("expertiseId", collectiveTimeAgreementDTO.getExpertise().getId().toString()));
        if(CollectionUtils.isNotEmpty(collectiveTimeAgreementDTO.getUnitIds())){
            requestParam.add(new BasicNameValuePair("unitIds", collectiveTimeAgreementDTO.getUnitIds().toString().replace("[", "").replace("]", "")));
        }
        CTABasicDetailsDTO ctaBasicDetailsDTO = genericIntegrationService.getCtaBasicDetailsDTO(0L,requestParam);

        CostTimeAgreement costTimeAgreement = ObjectMapperUtils.copyPropertiesByMapper(collectiveTimeAgreementDTO, CostTimeAgreement.class);
        costTimeAgreement.setId(null);
        buildCTA(null,costTimeAgreement, collectiveTimeAgreementDTO,  false, false,ctaBasicDetailsDTO,null);

        //costTimeAgreement.setCountryId(countryId);
        this.save(costTimeAgreement);
        // TO create CTA for organizations too which are linked with same sub type
        publishNewCTAToOrganizationByOrgSubType(unitId,costTimeAgreement, collectiveTimeAgreementDTO, ctaBasicDetailsDTO);
        collectiveTimeAgreementDTO.setId(costTimeAgreement.getId());
        return ObjectMapperUtils.copyPropertiesByMapper(costTimeAgreement,CollectiveTimeAgreementDTO.class);
    }

    /**
     *
     * @param costTimeAgreement
     * @param collectiveTimeAgreementDTO
     * @param doUpdate
     * @param creatingFromCountry
     * @param ctaBasicDetailsDTO
     */
    public void buildCTA(Map<PhaseDefaultName,BigInteger> unitPhaseIdsMap,CostTimeAgreement costTimeAgreement, CollectiveTimeAgreementDTO collectiveTimeAgreementDTO, boolean doUpdate, boolean creatingFromCountry,CTABasicDetailsDTO ctaBasicDetailsDTO,Map<BigInteger,PhaseDefaultName> phaseDefaultNameMap){
        // Get Rule Templates
        List<CTARuleTemplate> ruleTemplates = ObjectMapperUtils.copyPropertiesOfListByMapper(collectiveTimeAgreementDTO.getRuleTemplates(),CTARuleTemplate.class);
        List<BigInteger> ruleTemplateIds = new ArrayList<>();
        if (!ruleTemplates.isEmpty()){
            ruleTemplates.forEach(ctaRuleTemplate -> {
                ctaRuleTemplate.setId(null);
                if(!doUpdate && !creatingFromCountry && Optional.ofNullable(unitPhaseIdsMap).isPresent()){
                    ctaRuleTemplate.getPhaseInfo().forEach(ctaRuleTemplatePhaseInfo -> {
                        PhaseDefaultName phaseDefaultName = phaseDefaultNameMap.get(ctaRuleTemplatePhaseInfo.getPhaseId());
                        ctaRuleTemplatePhaseInfo.setPhaseId(unitPhaseIdsMap.get(phaseDefaultName));
                    });
                }
            });
            save(ruleTemplates);
            ruleTemplateIds = ruleTemplates.stream().map(rt->rt.getId()).collect(Collectors.toList());
        }
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
        costTimeAgreement.setStartDate(collectiveTimeAgreementDTO.getStartDate());
        costTimeAgreement.setEndDate(collectiveTimeAgreementDTO.getEndDate());
    }


    /**
     *
     * @param costTimeAgreement
     * @param collectiveTimeAgreementDTO
     * @param ctaBasicDetailsDTO
     * @return
     */
    public Boolean publishNewCTAToOrganizationByOrgSubType(Long unitId,CostTimeAgreement costTimeAgreement, CollectiveTimeAgreementDTO collectiveTimeAgreementDTO, CTABasicDetailsDTO ctaBasicDetailsDTO) {
        List<Long> organizationIds = ctaBasicDetailsDTO.getOrganizations().stream().map(o->o.getId()).collect(Collectors.toList());
        List<BigInteger> activityIds = collectiveTimeAgreementDTO.getRuleTemplates().stream().filter(ruleTemp->Optional.ofNullable(ruleTemp.getActivityIds()).isPresent()).flatMap(ctaRuleTemplateDTO -> ctaRuleTemplateDTO.getActivityIds().stream()).collect(Collectors.toList());
        Map<Long, Map<Long, BigInteger>> unitActivities = activityService.getListOfActivityIdsOfUnitByParentIds(activityIds,organizationIds);
        List<CostTimeAgreement> costTimeAgreements = new ArrayList<>(organizationIds.size());
        Long countryId = costTimeAgreement.getCountryId();
        costTimeAgreement.setCountryId(null);
        List<CostTimeAgreement> costTimeAgreementList=costTimeAgreementRepository.findCTAByUnitIdAndOrgTypeAndName(organizationIds,collectiveTimeAgreementDTO.getName());
        Map<Long,Map<PhaseDefaultName,BigInteger>> unitPhasesMap;
        Map<BigInteger,PhaseDefaultName> phaseDefaultNameMap;
        if(!Optional.ofNullable(unitId).isPresent()){
            unitPhasesMap = costTimeAgreementService.getMapOfPhaseIdsAndUnitByParentIds(organizationIds);
            List<Phase> countryPhase = phaseMongoRepository.findAllBycountryIdAndDeletedFalse(countryId);
            phaseDefaultNameMap = countryPhase.stream().collect(Collectors.toMap(k->k.getId(), v->v.getPhaseEnum()));
        }
        else {
            List<Phase> phases = phaseMongoRepository.findByOrganizationIdAndDeletedFalse(unitId);
            phaseDefaultNameMap = phases.stream().collect(Collectors.toMap(k->k.getId(), v->v.getPhaseEnum()));
            List<Phase> unitPhase = phaseMongoRepository.findAllByUnitIdsAndDeletedFalse(organizationIds);
            unitPhasesMap = getUnitIdAndphaseDefaultNameAndIdMap(unitPhase);
        }
        Map<String,CostTimeAgreement> costTimeAgreementMap = costTimeAgreementList.stream().collect(Collectors.toMap(k->k.getName()+"_"+k.getOrganization().getId()+"_"+k.getOrganizationType().getId(),v->v, (previous, current) -> previous));
        for (OrganizationBasicDTO organization : ctaBasicDetailsDTO.getOrganizations()) {
            if(costTimeAgreementMap.get(collectiveTimeAgreementDTO.getName()+"_"+organization.getId()+"_"+costTimeAgreement.getOrganizationType().getId())==null){
                CostTimeAgreement newCostTimeAgreement = createCostTimeAgreementForOrganization(unitPhasesMap.get(organization.getId()),costTimeAgreement,collectiveTimeAgreementDTO, unitActivities.get(organization.getId()),ctaBasicDetailsDTO,organization,phaseDefaultNameMap);
                costTimeAgreements.add(newCostTimeAgreement);
            }
        }
        if(!costTimeAgreements.isEmpty()) {
            save(costTimeAgreements);
        }
        return true;
    }


    private Map<Long,Map<PhaseDefaultName,BigInteger>> getUnitIdAndphaseDefaultNameAndIdMap(List<Phase> unitPhase){
        Map<Long,List<Phase>> unitPhaseGroupMap = unitPhase.stream().collect(Collectors.groupingBy(k->k.getOrganizationId(),Collectors.toList()));
        Map<Long,Map<PhaseDefaultName,BigInteger>> unitPhaseMap = new HashMap<>();
        unitPhaseGroupMap.forEach((unitId, phases) -> {
            Map<PhaseDefaultName,BigInteger> unitPhaseIdAndDefaultNameMap = phases.stream().collect(Collectors.toMap(k->k.getPhaseEnum(),v->v.getId()));
            unitPhaseMap.put(unitId,unitPhaseIdAndDefaultNameMap);
        });
        return unitPhaseMap;
    }

    /**
     *
     * @param costTimeAgreement
     * @param collectiveTimeAgreementDTO
     * @param parentUnitActivityMap
     * @param ctaBasicDetailsDTO
     * @return
     */
    public CostTimeAgreement createCostTimeAgreementForOrganization(Map<PhaseDefaultName,BigInteger> unitPhaseIdsMap,CostTimeAgreement costTimeAgreement, CollectiveTimeAgreementDTO collectiveTimeAgreementDTO, Map<Long, BigInteger> parentUnitActivityMap, CTABasicDetailsDTO ctaBasicDetailsDTO, OrganizationBasicDTO organization,Map<BigInteger,PhaseDefaultName> phaseDefaultNameMap) {
        CostTimeAgreement organisationCTA = ObjectMapperUtils.copyPropertiesByMapper(costTimeAgreement, CostTimeAgreement.class);
        organisationCTA.setId(null);
        costTimeAgreementService.assignOrganisationActivitiesToRuleTemplate(collectiveTimeAgreementDTO.getRuleTemplates(),parentUnitActivityMap);
        organisationCTA.setOrganization(new Organization(organization.getId(),organization.getName(),organization.getDescription()));
        organisationCTA.setParentCountryCTAId(costTimeAgreement.getId());
        buildCTA(unitPhaseIdsMap,organisationCTA, collectiveTimeAgreementDTO, false, false,ctaBasicDetailsDTO,phaseDefaultNameMap);
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
        requestParam.add(new BasicNameValuePair("organizationSubTypeId", collectiveTimeAgreementDTO.getOrganizationSubType().getId().toString()));
        requestParam.add(new BasicNameValuePair("expertiseId", collectiveTimeAgreementDTO.getExpertise().getId().toString()));
        CTABasicDetailsDTO ctaBasicDetailsDTO = genericIntegrationService.getCtaBasicDetailsDTO(countryId,requestParam);
        logger.info("costTimeAgreement.getRuleTemplateIds() : {}", costTimeAgreement.getRuleTemplateIds().size());

        CostTimeAgreement updateCostTimeAgreement = ObjectMapperUtils.copyPropertiesByMapper(collectiveTimeAgreementDTO, CostTimeAgreement.class);
        updateCostTimeAgreement.setId(costTimeAgreement.getId());
        costTimeAgreement.setId(null);
        costTimeAgreement.setDisabled(true);
        this.save(costTimeAgreement);
        updateCostTimeAgreement.setCountryId(costTimeAgreement.getCountryId());
        updateCostTimeAgreement.setParentId(costTimeAgreement.getId());
        updateCostTimeAgreement.setName(collectiveTimeAgreementDTO.getName());
        updateCostTimeAgreement.setDescription(collectiveTimeAgreementDTO.getDescription());
        buildCTA(null,updateCostTimeAgreement, collectiveTimeAgreementDTO,  true, true,ctaBasicDetailsDTO,null);
        this.save(updateCostTimeAgreement);
        return ObjectMapperUtils.copyPropertiesByMapper(updateCostTimeAgreement, CollectiveTimeAgreementDTO.class);
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

        CostTimeAgreement updateCostTimeAgreement = ObjectMapperUtils.copyPropertiesByMapper(costTimeAgreement, CostTimeAgreement.class);
        updateCostTimeAgreement.setId(costTimeAgreement.getId());
        costTimeAgreement.setDisabled(true);
        this.save(costTimeAgreement);
        updateCostTimeAgreement.setParentId(costTimeAgreement.getId());
        updateCostTimeAgreement.setName(collectiveTimeAgreementDTO.getName());
        updateCostTimeAgreement.setDescription(collectiveTimeAgreementDTO.getDescription());
        buildCTA(null,updateCostTimeAgreement, collectiveTimeAgreementDTO,  true, false,null,null);

        this.save(updateCostTimeAgreement);
        return collectiveTimeAgreementDTO;
    }




}

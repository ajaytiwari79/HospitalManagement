package com.kairos.service.cta;


import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.activity.TableConfiguration;
import com.kairos.dto.activity.cta.*;
import com.kairos.dto.activity.shift.StaffUnitPositionDetails;
import com.kairos.dto.activity.wta.rule_template_category.RuleTemplateCategoryDTO;
import com.kairos.dto.user.country.basic_details.CountryDTO;
import com.kairos.dto.user.country.experties.ExpertiseResponseDTO;
import com.kairos.dto.user.organization.OrganizationDTO;
import com.kairos.dto.user.organization.OrganizationTypeDTO;
import com.kairos.dto.user.user.staff.StaffAdditionalInfoDTO;
import com.kairos.enums.RuleTemplateCategoryType;
import com.kairos.enums.phase.PhaseDefaultName;
import com.kairos.persistence.model.common.MongoBaseEntity;
import com.kairos.persistence.model.cta.CTARuleTemplate;
import com.kairos.persistence.model.cta.CostTimeAgreement;
import com.kairos.persistence.model.phase.Phase;
import com.kairos.persistence.model.wta.WTAOrganization;
import com.kairos.persistence.model.wta.templates.RuleTemplateCategory;
import com.kairos.persistence.repository.cta.CTARuleTemplateRepository;
import com.kairos.persistence.repository.cta.CostTimeAgreementRepository;
import com.kairos.persistence.repository.phase.PhaseMongoRepository;
import com.kairos.persistence.repository.wta.rule_template.RuleTemplateCategoryRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.MongoBaseService;
import com.kairos.service.activity.ActivityService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.table_settings.TableSettingService;
import com.kairos.service.time_bank.TimeBankService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.asDate;
import static com.kairos.commons.utils.DateUtils.asLocalDate;
import static com.kairos.commons.utils.ObjectUtils.isNotNull;
import static com.kairos.constants.AppConstants.COPY_OF;
import static com.kairos.constants.AppConstants.ORGANIZATION;
import static com.kairos.persistence.model.constants.TableSettingConstants.ORGANIZATION_CTA_AGREEMENT_VERSION_TABLE_ID;

/**
 * @author pradeep
 * @date - 07/08/18
 */

@Transactional
@Service
public class CostTimeAgreementService extends MongoBaseService {
    private final Logger logger = LoggerFactory.getLogger(CostTimeAgreementService.class);


    @Inject
    private RuleTemplateCategoryRepository ruleTemplateCategoryRepository;
    @Inject
    private UserIntegrationService userIntegrationService;
    @Inject
    private CTARuleTemplateRepository ctaRuleTemplateRepository;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private CountryCTAService countryCTAService;
    @Inject
    private CostTimeAgreementRepository costTimeAgreementRepository;
    @Inject
    private TableSettingService tableSettingService;
    @Inject
    private ActivityService activityService;
    @Inject
    private PhaseMongoRepository phaseMongoRepository;
    @Inject
    private TimeBankService timeBankService;




    /**
     * @param countryId
     * @return boolean
     */
    public boolean createDefaultCtaRuleTemplate(Long countryId) {
        RuleTemplateCategory category = ruleTemplateCategoryRepository.findByName(countryId, "NONE", RuleTemplateCategoryType.CTA);
        if (category == null) {
            category = new RuleTemplateCategory("NONE", "None", RuleTemplateCategoryType.CTA);
            category.setCountryId(countryId);
            save(category);
            /*CountryDTO country = countryRestClient.getCountryById(countryId);
            if (country != null) {
                List<CTARuleTemplate> ctaRuleTemplates = createDefaultRuleTemplate(countryId, country.getCurrencyId(), category.getId());
                save(ctaRuleTemplates);
            }*/
        } else {
            logger.info("default CTARuleTemplateCategory is not exist");
        }
        return true;
    }


    /**
     * @param countryId
     * @param ctaRuleTemplateDTO
     * @return CTARuleTemplateDTO
     */
    public CTARuleTemplateDTO createCTARuleTemplate(Long countryId, CTARuleTemplateDTO ctaRuleTemplateDTO) {
        if (ctaRuleTemplateRepository.isCTARuleTemplateExistWithSameName(countryId, ctaRuleTemplateDTO.getName())) {
            exceptionService.dataNotFoundByIdException("message.cta.ruleTemplate.alreadyExist", ctaRuleTemplateDTO.getName());
        }
        CountryDTO countryDTO = userIntegrationService.getCountryById(countryId);
        ctaRuleTemplateDTO.setId(null);
        ctaRuleTemplateDTO.setRuleTemplateType(ctaRuleTemplateDTO.getName());
        CTARuleTemplate ctaRuleTemplate = ObjectMapperUtils.copyPropertiesByMapper(ctaRuleTemplateDTO, CTARuleTemplate.class);
        this.buildCTARuleTemplate(ctaRuleTemplate, ctaRuleTemplateDTO, false, countryDTO);
        ctaRuleTemplate.setCountryId(countryId);
        ctaRuleTemplate.setStaffFunctions(null);
        this.save(ctaRuleTemplate);
        ctaRuleTemplateDTO.setId(ctaRuleTemplate.getId());
        return ctaRuleTemplateDTO;
    }


    /**
     *
     * @param countryId
     * @param organizationSubTypeId
     * @param organizationId
     */
    public void assignCountryCTAtoOrganisation(Long countryId, Long organizationSubTypeId,Long organizationId){
        List<CTAResponseDTO> ctaResponseDTOS = costTimeAgreementRepository.getAllCTAByOrganizationSubType(countryId, organizationSubTypeId);
        List<BigInteger> activityIds = ctaResponseDTOS.stream().flatMap(ctaResponseDTO -> ctaResponseDTO.getRuleTemplates().stream()).filter(ruleTemp->Optional.ofNullable(ruleTemp.getActivityIds()).isPresent()).flatMap(ctaRuleTemplateDTO -> ctaRuleTemplateDTO.getActivityIds().stream()).collect(Collectors.toList());
        List<Long> unitIds = Arrays.asList(organizationId);
        Map<Long, Map<Long, BigInteger>> unitActivities = activityService.getListOfActivityIdsOfUnitByParentIds(activityIds, unitIds);
         List<Phase> countryPhase = phaseMongoRepository.findAllBycountryIdAndDeletedFalse(countryId);
         Map<BigInteger,PhaseDefaultName> phaseDefaultNameMap = countryPhase.stream().collect(Collectors.toMap(k->k.getId(),v->v.getPhaseEnum()));
        Map<Long,Map<PhaseDefaultName,BigInteger>> unitsPhasesMap = getMapOfPhaseIdsAndUnitByParentIds(unitIds);
        List<CostTimeAgreement> costTimeAgreements = new ArrayList<>(ctaResponseDTOS.size());
        Map<PhaseDefaultName,BigInteger> organisationPhaseMap = unitsPhasesMap.get(organizationId);
        for (CTAResponseDTO ctaResponseDTO : ctaResponseDTOS) {
            CostTimeAgreement organisationCTA = ObjectMapperUtils.copyPropertiesByMapper(ctaResponseDTO, CostTimeAgreement.class);
            // Set activity Ids according to unit activity Ids
            organisationCTA.setId(null);
            assignOrganisationActivitiesToRuleTemplate(ctaResponseDTO.getRuleTemplates(),unitActivities.get(organisationCTA.getId()));
            organisationCTA.setOrganization(new WTAOrganization(organizationId, "", ""));
            organisationCTA.setParentCountryCTAId(ctaResponseDTO.getId());
            List<CTARuleTemplate> ruleTemplates = ObjectMapperUtils.copyPropertiesOfListByMapper(ctaResponseDTO.getRuleTemplates(),CTARuleTemplate.class);
            List<BigInteger> ruleTemplateIds = new ArrayList<>();
            if (!ruleTemplates.isEmpty()){
                ruleTemplates.forEach(ctaRuleTemplate -> {
                    ctaRuleTemplate.setId(null);
                    ctaRuleTemplate.getPhaseInfo().forEach(ctaRuleTemplatePhaseInfo -> {
                        PhaseDefaultName phaseDefaultName = phaseDefaultNameMap.get(ctaRuleTemplatePhaseInfo.getPhaseId());
                        ctaRuleTemplatePhaseInfo.setPhaseId(organisationPhaseMap.get(phaseDefaultName));
                    });
                });
                save(ruleTemplates);
                ruleTemplateIds = ruleTemplates.stream().map(rt->rt.getId()).collect(Collectors.toList());
            }
            organisationCTA.setRuleTemplateIds(ruleTemplateIds);
            costTimeAgreements.add(organisationCTA);
        }
        if(!costTimeAgreements.isEmpty()){
            save(costTimeAgreements);
        }

    }

    public Map<Long, Map<PhaseDefaultName, BigInteger>> getMapOfPhaseIdsAndUnitByParentIds( List<Long> unitIds) {
        List<Phase> unitPhases = phaseMongoRepository.findAllByUnitIdsAndDeletedFalse(unitIds);
        Map<Long,List<Phase>> phasesOrganizationMap = unitPhases.stream().collect(Collectors.groupingBy(k->k.getOrganizationId(),Collectors.toList()));
        Map<Long, Map<PhaseDefaultName, BigInteger>> organizationPhasesMapWithParentCountryPhaseId = new HashMap<>();
        phasesOrganizationMap.forEach((organisationId, phaseDTOS) -> {
            Map<PhaseDefaultName, BigInteger> parentPhasesAndUnitPhaseIdMap = phaseDTOS.stream().collect(Collectors.toMap(k->k.getPhaseEnum(),v->v.getId()));
            organizationPhasesMapWithParentCountryPhaseId.put(organisationId,parentPhasesAndUnitPhaseIdMap);
        });
        return organizationPhasesMapWithParentCountryPhaseId;
    }

    public void assignOrganisationActivitiesToRuleTemplate(List<CTARuleTemplateDTO> ruleTemplateDTOS,Map<Long, BigInteger> parentUnitActivityMap){
        ruleTemplateDTOS.forEach(ctaRuleTemplateDTO -> {
            List<BigInteger> parentActivityIds = ctaRuleTemplateDTO.getActivityIds();
            if(parentActivityIds!=null){
                List<BigInteger> unitActivityIds = new ArrayList<BigInteger>();
                parentActivityIds.forEach(parentActivityId -> {
                    if (Optional.ofNullable(parentUnitActivityMap).isPresent() && Optional.ofNullable(parentUnitActivityMap.get(parentActivityId)).isPresent()) {
                        unitActivityIds.add(parentUnitActivityMap.get(parentActivityId));
                    }
                });
                ctaRuleTemplateDTO.setActivityIds(unitActivityIds);
            }
        });
    }

    /**
     * @param unitId
     * @param ctaId
     * @return List<CTARuleTemplateDTO>
     */
    public List<CTARuleTemplateDTO> getCTARuleTemplateOfUnit(Long unitId, BigInteger ctaId) {
        CTAResponseDTO ctaResponseDTO = costTimeAgreementRepository.getOneCtaById(ctaId);
        List<CTARuleTemplateDTO> ctaRuleTemplateDTOS = new ArrayList<>();
        if (Optional.ofNullable(ctaResponseDTO).isPresent()) {
            ctaRuleTemplateDTOS = ctaResponseDTO.getRuleTemplates();
        }
        return ctaRuleTemplateDTOS;
    }


    /**
     * @param countryId
     * @param ctaId
     * @return List<CTARuleTemplateDTO>
     */
    public List<CTARuleTemplateDTO> getCTARuleTemplateOfCountry(Long countryId, BigInteger ctaId) {
        CTAResponseDTO ctaResponseDTO = costTimeAgreementRepository.getOneCtaById(ctaId);
        List<CTARuleTemplateDTO> ctaRuleTemplateDTOS = new ArrayList<>();
        if (ctaResponseDTO != null) {
            ctaRuleTemplateDTOS = ctaResponseDTO.getRuleTemplates();
        }
        return ctaRuleTemplateDTOS;
    }



    public CTAResponseDTO getUnitPositionCTA(Long unitId, Long unitEmploymentPositionId) {
        UnitPositionDTO unitPosition = userIntegrationService.getUnitPositionDTO(unitId,unitEmploymentPositionId);
        if (!Optional.ofNullable(unitPosition).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.InvalidEmploymentPostionId", unitEmploymentPositionId);

        }
        return costTimeAgreementRepository.getOneCtaById(unitPosition.getCostTimeAgreementId());
    }

    public StaffUnitPositionDetails updateCostTimeAgreementForUnitPosition(Long unitId, Long unitPositionId, BigInteger ctaId, CollectiveTimeAgreementDTO ctaDTO) {
        StaffAdditionalInfoDTO staffAdditionalInfoDTO = userIntegrationService.verifyUnitEmploymentOfStaffByUnitPositionId(unitId,null,ORGANIZATION,unitPositionId,new HashSet<>());
        if (!Optional.ofNullable(staffAdditionalInfoDTO.getUnitPosition()).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.InvalidEmploymentPostionId", unitPositionId);
        }
        if (staffAdditionalInfoDTO.getUnitPosition().getEndDate()!=null && ctaDTO.getEndDate()!=null && ctaDTO.getEndDate().isBefore(staffAdditionalInfoDTO.getUnitPosition().getEndDate())){
            exceptionService.actionNotPermittedException("end_date.from.end_date",ctaDTO.getEndDate(),staffAdditionalInfoDTO.getUnitPosition().getEndDate());
        }
        if (staffAdditionalInfoDTO.getUnitPosition().getEndDate()!=null && ctaDTO.getStartDate().isAfter(staffAdditionalInfoDTO.getUnitPosition().getEndDate())){
            exceptionService.actionNotPermittedException("start_date.from.end_date",ctaDTO.getStartDate(),staffAdditionalInfoDTO.getUnitPosition().getEndDate());
        }
        CostTimeAgreement oldCTA = costTimeAgreementRepository.findOne(ctaId);
        CTAResponseDTO responseCTA;
        boolean updateSameCTA = !staffAdditionalInfoDTO.getUnitPosition().isPublished() || ctaDTO.getStartDate().isBefore(oldCTA.getStartDate()) || ctaDTO.getStartDate().equals(oldCTA.getStartDate());
        if(!updateSameCTA){
            updateSameCTA = !isCalculatedValueChanged(oldCTA.getRuleTemplateIds(), ctaDTO.getRuleTemplates());
        }
        if(updateSameCTA) {
            responseCTA = updateUnitPositionCTA(oldCTA,ctaDTO);
        }else {
            responseCTA = updateUnitPositionCTAWhenCalculatedValueChanged(oldCTA, ctaDTO);
        }
        staffAdditionalInfoDTO.getUnitPosition().setCostTimeAgreement(responseCTA);
        timeBankService.updateDailyTimeBankOnCTAChangeOfUnitPosition(staffAdditionalInfoDTO,responseCTA);
        return staffAdditionalInfoDTO.getUnitPosition();
    }

    private CTAResponseDTO updateUnitPositionCTA(CostTimeAgreement costTimeAgreement,CollectiveTimeAgreementDTO ctaDTO){
        if(!ctaDTO.getStartDate().equals(costTimeAgreement.getStartDate())){
            boolean ctaExists = costTimeAgreementRepository.ctaExistsByUnitPositionIdAndDatesAndNotEqualToId(costTimeAgreement.getId(),costTimeAgreement.getUnitPositionId(),asDate(ctaDTO.getStartDate()),isNotNull(ctaDTO.getEndDate()) ? asDate(ctaDTO.getEndDate()): null);
            if(ctaExists){
                exceptionService.duplicateDataException("error.cta.invalid",ctaDTO.getStartDate(),isNotNull(ctaDTO.getEndDate()) ? asDate(ctaDTO.getEndDate()): "");
            }
        }
        List<CTARuleTemplate> ctaRuleTemplates = ObjectMapperUtils.copyPropertiesOfListByMapper(ctaDTO.getRuleTemplates(), CTARuleTemplate.class);
        ctaRuleTemplates.forEach(ctaRuleTemplate -> ctaRuleTemplate.setId(null));
        if(CollectionUtils.isNotEmpty(ctaRuleTemplates)){
            save(ctaRuleTemplates);
        }
        List<BigInteger> ruleTemplateIds = ctaRuleTemplates.stream().map(MongoBaseEntity::getId).collect(Collectors.toList());
        costTimeAgreement.setRuleTemplateIds(ruleTemplateIds);
        costTimeAgreement.setStartDate(ctaDTO.getStartDate());
        costTimeAgreement.setEndDate(ctaDTO.getEndDate());
        costTimeAgreement.setDescription(ctaDTO.getDescription());
        costTimeAgreement.setName(ctaDTO.getName());
        save(costTimeAgreement);
        List<CTARuleTemplateDTO> ctaRuleTemplateDTOS = ObjectMapperUtils.copyPropertiesOfListByMapper(ctaRuleTemplates, CTARuleTemplateDTO.class);
        ExpertiseResponseDTO expertiseResponseDTO = ObjectMapperUtils.copyPropertiesByMapper(costTimeAgreement.getExpertise(), ExpertiseResponseDTO.class);
        CTAResponseDTO responseCTA = new CTAResponseDTO(costTimeAgreement.getId(), costTimeAgreement.getName(), expertiseResponseDTO, ctaRuleTemplateDTOS, costTimeAgreement.getStartDate(), costTimeAgreement.getEndDate(), false,costTimeAgreement.getUnitPositionId(),costTimeAgreement.getDescription());
        return responseCTA;
    }

    private CTAResponseDTO updateUnitPositionCTAWhenCalculatedValueChanged(CostTimeAgreement oldCTA,CollectiveTimeAgreementDTO ctaDTO){
        if(!ctaDTO.getStartDate().equals(oldCTA.getStartDate())){
            boolean ctaExists = costTimeAgreementRepository.ctaExistsByUnitPositionIdAndDatesAndNotEqualToId(oldCTA.getId(),oldCTA.getUnitPositionId(),asDate(ctaDTO.getStartDate()),isNotNull(ctaDTO.getEndDate()) ? asDate(ctaDTO.getEndDate()): null);
            if(ctaExists){
                exceptionService.duplicateDataException("error.cta.invalid",ctaDTO.getStartDate(),isNotNull(ctaDTO.getEndDate()) ? asDate(ctaDTO.getEndDate()): "");
            }
        }
        ctaDTO.setId(null);
        CostTimeAgreement costTimeAgreement = ObjectMapperUtils.copyPropertiesByMapper(ctaDTO, CostTimeAgreement.class);
        List<CTARuleTemplate> ctaRuleTemplates = ObjectMapperUtils.copyPropertiesOfListByMapper(ctaDTO.getRuleTemplates(), CTARuleTemplate.class);
        if(!ctaRuleTemplates.isEmpty()) {
            ctaRuleTemplates.forEach(ctaRuleTemplate -> ctaRuleTemplate.setId(null));
            save(ctaRuleTemplates);
            List<BigInteger> ruleTemplateIds = ctaRuleTemplates.stream().map(MongoBaseEntity::getId).collect(Collectors.toList());
            costTimeAgreement.setRuleTemplateIds(ruleTemplateIds);
        }
        costTimeAgreement.setId(oldCTA.getId());
        oldCTA.setId(null);
        oldCTA.setDisabled(true);
        if(oldCTA.getStartDate().isBefore(ctaDTO.getStartDate()) || (isNotNull(oldCTA.getEndDate()) && oldCTA.getEndDate().equals(ctaDTO.getEndDate()))) {
            oldCTA.setEndDate(ctaDTO.getStartDate().minusDays(1));
        }
        costTimeAgreementRepository.save(oldCTA);
        costTimeAgreement.setParentId(oldCTA.getId());
        costTimeAgreement.setOrganizationParentId(oldCTA.getOrganizationParentId());
        costTimeAgreement.setExpertise(oldCTA.getExpertise());
        costTimeAgreement.setOrganizationType(oldCTA.getOrganizationType());
        costTimeAgreement.setOrganizationSubType(oldCTA.getOrganizationSubType());
        costTimeAgreement.setOrganization(oldCTA.getOrganization());
        costTimeAgreement.setUnitPositionId(oldCTA.getUnitPositionId());
        costTimeAgreement.setDescription(ctaDTO.getDescription());
        costTimeAgreementRepository.save(costTimeAgreement);
        List<CTARuleTemplateDTO> ctaRuleTemplateDTOS = ObjectMapperUtils.copyPropertiesOfListByMapper(ctaRuleTemplates, CTARuleTemplateDTO.class);
        ExpertiseResponseDTO expertiseResponseDTO = ObjectMapperUtils.copyPropertiesByMapper(oldCTA.getExpertise(), ExpertiseResponseDTO.class);
        CTAResponseDTO responseCTA = new CTAResponseDTO(costTimeAgreement.getId(), costTimeAgreement.getName(), expertiseResponseDTO, ctaRuleTemplateDTOS, costTimeAgreement.getStartDate(), costTimeAgreement.getEndDate(), false,oldCTA.getUnitPositionId(),costTimeAgreement.getDescription());
        responseCTA.setParentId(oldCTA.getId());
        responseCTA.setOrganizationParentId(oldCTA.getOrganizationParentId());
        CTAResponseDTO versionCTA = ObjectMapperUtils.copyPropertiesByMapper(oldCTA,CTAResponseDTO.class);
        List<CTARuleTemplate> existingCtaRuleTemplates = ctaRuleTemplateRepository.findAllByIdAndDeletedFalse(oldCTA.getRuleTemplateIds());
        List<CTARuleTemplateDTO> existingCtaRuleTemplatesDTOS = ObjectMapperUtils.copyPropertiesOfListByMapper(existingCtaRuleTemplates, CTARuleTemplateDTO.class);
        versionCTA.setRuleTemplates(existingCtaRuleTemplatesDTOS);
        responseCTA.setVersions(Arrays.asList(versionCTA));
        return responseCTA;
    }

    private void updateTimeBankByUnitPositionIdPerStaff(Long unitPositionId, LocalDate ctaStartDate, LocalDate ctaEndDate, Long unitId) {
        Date endDate=ctaEndDate!=null? DateUtils.asDate(ctaEndDate):null;
        StaffAdditionalInfoDTO staffAdditionalInfoDTO = userIntegrationService.verifyUnitEmploymentOfStaffByUnitPositionId(unitId,ctaStartDate, ORGANIZATION,unitPositionId,Collections.emptySet());
        timeBankService.updateTimeBankOnUnitPositionModification(null,unitPositionId, DateUtils.asDate(ctaStartDate), endDate, staffAdditionalInfoDTO);
    }

    /**
     * @param countryId
     * @return CTARuleTemplateCategoryWrapper
     */
    public CTARuleTemplateCategoryWrapper loadAllCTARuleTemplateByCountry(Long countryId) {
        List<RuleTemplateCategory> ruleTemplateCategories = ruleTemplateCategoryRepository.getRuleTemplateCategoryByCountry(countryId, RuleTemplateCategoryType.CTA);
        List<RuleTemplateCategoryDTO> ctaRuleTemplateCategoryList = ObjectMapperUtils.copyPropertiesOfListByMapper(ruleTemplateCategories, RuleTemplateCategoryDTO.class);
        Map<BigInteger,RuleTemplateCategoryDTO> ruleTemplateCategoryDTOMap = ctaRuleTemplateCategoryList.stream().collect(Collectors.toMap(k->k.getId(),v->v));
        List<CTARuleTemplateDTO> ctaRuleTemplateDTOS = ctaRuleTemplateRepository.findByCountryIdAndDeletedFalse(countryId);
        ctaRuleTemplateDTOS.forEach(c -> {
            c.setRuleTemplateCategory(c.getRuleTemplateCategoryId());
            c.setRuleTemplateCategoryName(ruleTemplateCategoryDTOMap.get(c.getRuleTemplateCategoryId()).getName());
        });
        return new CTARuleTemplateCategoryWrapper(ctaRuleTemplateCategoryList, ctaRuleTemplateDTOS);
    }

    /**
     * @param unitId
     * @return CTARuleTemplateCategoryWrapper
     */
    public CTARuleTemplateCategoryWrapper loadAllCTARuleTemplateByUnit(Long unitId) {
        Long countryId = userIntegrationService.getCountryIdOfOrganization(unitId);
        return loadAllCTARuleTemplateByCountry(countryId);
    }

    /**
     * @param ctaRuleTemplate
     * @param ctaRuleTemplateDTO
     * @param doUpdate
     * @param countryDTO
     */
    private void buildCTARuleTemplate(CTARuleTemplate ctaRuleTemplate, CTARuleTemplateDTO ctaRuleTemplateDTO, Boolean doUpdate, CountryDTO countryDTO) {
        ctaRuleTemplate.setRuleTemplateCategoryId(ctaRuleTemplateDTO.getRuleTemplateCategory());
        setActivityBasesCostCalculationSettings(ctaRuleTemplate);
        if (ctaRuleTemplate.getCalculateValueAgainst() != null && ctaRuleTemplate.getCalculateValueAgainst().getCalculateValue() != null) {
            switch (ctaRuleTemplate.getCalculateValueAgainst().getCalculateValue()) {
                case FIXED_VALUE: {
                    if (doUpdate && ctaRuleTemplate.getCalculateValueAgainst().getFixedValue().getCurrencyId() != null) {
                        ctaRuleTemplate.getCalculateValueAgainst().getFixedValue().setCurrency(countryDTO.getCurrencyId());
                    }
                    break;
                }
                case WEEKLY_HOURS:
                case WEEKLY_SALARY:
                    ctaRuleTemplate.getCalculateValueAgainst().setScale(ctaRuleTemplate.getCalculateValueAgainst().getScale());
                    break;
            }
        }
        ctaRuleTemplate.getCalculateValueAgainst().setCalculateValue(ctaRuleTemplateDTO.getCalculateValueAgainst().getCalculateValue());
        logger.info("ctaRuleTemplate.getCalculateValueAgainst().getScale : {}", ctaRuleTemplate.getCalculateValueAgainst().getScale());
    }


    /**
     * @param countryId
     * @param ctaId
     * @return Boolean
     */
    public Boolean deleteCostTimeAgreement(Long countryId, BigInteger ctaId) {
        CostTimeAgreement costTimeAgreement = costTimeAgreementRepository.findCTAByCountryAndIdAndDeleted(countryId, ctaId, false);
        if (costTimeAgreement == null) {
            exceptionService.dataNotFoundByIdException("message.cta.id.notFound", ctaId);
        }
        costTimeAgreement.setDeleted(true);
        this.save(costTimeAgreement);
        return true;
    }

    /**
     * @param countryId
     * @return List<CTAResponseDTO>
     */
    public List<CTAResponseDTO> loadAllCTAByCountry(Long countryId) {
        return costTimeAgreementRepository.findCTAByCountryId(countryId);
    }

    /**
     * @param unitId
     * @return List<CTAResponseDTO>
     */
    public List<CTAResponseDTO> loadAllCTAByUnit(Long unitId) {
        return costTimeAgreementRepository.findCTAByUnitId(unitId);
    }


    /**
     *
     * @param collectiveTimeAgreementDTO
     */
    private CostTimeAgreement buildCTA(CollectiveTimeAgreementDTO collectiveTimeAgreementDTO) {
        CostTimeAgreement costTimeAgreement = ObjectMapperUtils.copyPropertiesByMapper(collectiveTimeAgreementDTO, CostTimeAgreement.class);
        List<CTARuleTemplate> ctaRuleTemplates = new ArrayList<>(collectiveTimeAgreementDTO.getRuleTemplates().size());
        for (CTARuleTemplateDTO ctaRuleTemplateDTO : collectiveTimeAgreementDTO.getRuleTemplates()) {
            CTARuleTemplate ctaRuleTemplate = ObjectMapperUtils.copyPropertiesByMapper(ctaRuleTemplateDTO, CTARuleTemplate.class);
            ctaRuleTemplate.setId(null);
            setActivityBasesCostCalculationSettings(ctaRuleTemplate);
            ctaRuleTemplate.setEmploymentTypes(ctaRuleTemplateDTO.getEmploymentTypes());
            ctaRuleTemplate.setRuleTemplateCategoryId(ctaRuleTemplateDTO.getRuleTemplateCategory());
            ctaRuleTemplates.add(ctaRuleTemplate);
        }
        save(ctaRuleTemplates);
        List<BigInteger> ruleTemplateIds = ctaRuleTemplates.stream().map(MongoBaseEntity::getId).collect(Collectors.toList());
        costTimeAgreement.setRuleTemplateIds(ruleTemplateIds);
        costTimeAgreement.setStartDate(collectiveTimeAgreementDTO.getStartDate());
        costTimeAgreement.setEndDate(collectiveTimeAgreementDTO.getEndDate());
        return costTimeAgreement;
    }

    /**
     * @param ctaRuleTemplate
     */
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


    /**
     * @param countryId
     * @param id
     * @param ctaRuleTemplateDTO
     * @return CTARuleTemplateDTO
     */
    public CTARuleTemplateDTO updateCTARuleTemplate(Long countryId, BigInteger id, CTARuleTemplateDTO ctaRuleTemplateDTO) {
        CountryDTO countryDTO = userIntegrationService.getCountryById(countryId);
        CTARuleTemplate ctaRuleTemplate = ctaRuleTemplateRepository.findOne(id);
        ctaRuleTemplateDTO.setRuleTemplateType(ctaRuleTemplate.getRuleTemplateType());
        CTARuleTemplate udpdateCtaRuleTemplate = ObjectMapperUtils.copyPropertiesByMapper(ctaRuleTemplateDTO, CTARuleTemplate.class);
        this.buildCTARuleTemplate(udpdateCtaRuleTemplate, ctaRuleTemplateDTO, true, countryDTO);
        udpdateCtaRuleTemplate.setId(ctaRuleTemplate.getId());
        udpdateCtaRuleTemplate.setCountryId(countryId);
        this.save(udpdateCtaRuleTemplate);
        return ctaRuleTemplateDTO;
    }


    /**
     * @param unitId
     * @param collectiveTimeAgreementDTO
     * @return CollectiveTimeAgreementDTO
     */
    public CollectiveTimeAgreementDTO createCopyOfUnitCTA(Long unitId, CollectiveTimeAgreementDTO collectiveTimeAgreementDTO) {
        logger.info("saving CostTimeAgreement unit {}", unitId);
        if (costTimeAgreementRepository.isCTAExistWithSameNameInUnit(unitId, collectiveTimeAgreementDTO.getName().trim(), new BigInteger("1"))) {
            exceptionService.duplicateDataException("message.cta.name.alreadyExist", collectiveTimeAgreementDTO.getName());

        }
        OrganizationDTO organization = userIntegrationService.getOrganization();
        collectiveTimeAgreementDTO.setId(null);
        CostTimeAgreement costTimeAgreement = buildCTA(collectiveTimeAgreementDTO);
        costTimeAgreement.setOrganization(new WTAOrganization(organization.getId(), organization.getName(), organization.getDescription()));
        this.save(costTimeAgreement);
        collectiveTimeAgreementDTO.setId(costTimeAgreement.getId());
        return collectiveTimeAgreementDTO;
    }

    /**
     * @param countryId
     * @param organizationSubTypeId
     * @return List<CTAResponseDTO>
     */
    public List<CTAResponseDTO> getAllCTAByOrganizationSubType(Long countryId, Long organizationSubTypeId) {
        return costTimeAgreementRepository.getAllCTAByOrganizationSubType(countryId, organizationSubTypeId);
    }

    /**
     * @param countryId
     * @param ctaId
     * @param collectiveTimeAgreementDTO
     * @param organizationSubTypeId
     * @param checked
     * @return CollectiveTimeAgreementDTO
     */
    public CollectiveTimeAgreementDTO setCTAWithOrganizationType(Long countryId, BigInteger ctaId, CollectiveTimeAgreementDTO collectiveTimeAgreementDTO, long organizationSubTypeId, boolean checked) {
        CollectiveTimeAgreementDTO collectiveTimeAgreementDTO1 = null;
        if (checked) {
            String name = COPY_OF+collectiveTimeAgreementDTO.getName();
            collectiveTimeAgreementDTO.setName(name);
            collectiveTimeAgreementDTO.setOrganizationSubType(new OrganizationTypeDTO(organizationSubTypeId));
            collectiveTimeAgreementDTO1 = countryCTAService.createCostTimeAgreementInCountry(countryId, collectiveTimeAgreementDTO,true);
        } else {
            CostTimeAgreement cta = costTimeAgreementRepository.getCTAByIdAndOrganizationSubTypeAndCountryId(organizationSubTypeId, countryId, ctaId);
            if (!Optional.ofNullable(cta).isPresent())
                exceptionService.dataNotFoundByIdException("message.cta.id.notFound", ctaId);
            cta.setDeleted(true);
            save(cta);
        }
        return collectiveTimeAgreementDTO1;
    }

    public CTATableSettingWrapper getVersionsCTA(Long unitId, List<Long> upIds) {
        TableConfiguration tableConfiguration = tableSettingService.getTableConfigurationByTableId(unitId, ORGANIZATION_CTA_AGREEMENT_VERSION_TABLE_ID);
        List<CTAResponseDTO> ctaResponseDTOS = costTimeAgreementRepository.getParentCTAByUpIds(upIds);
        Map<Long, List<CTAResponseDTO>> ctaResponseMap = costTimeAgreementRepository.getVersionsCTA(upIds).stream().collect(Collectors.groupingBy(k -> k.getUnitPositionId(), Collectors.toList()));
        ctaResponseDTOS.forEach(c -> c.setVersions(ctaResponseMap.get(c.getUnitPositionId())));
        return new CTATableSettingWrapper(ctaResponseDTOS, tableConfiguration);
    }

    public CTAResponseDTO getDefaultCTA(Long unitId, Long expertiseId) {
        List<CTAResponseDTO> ctaResponseDTOS = costTimeAgreementRepository.getDefaultCTA(unitId, expertiseId);
        return ctaResponseDTOS.isEmpty() ? null : ctaResponseDTOS.get(0);
    }

    public List<CTAResponseDTO> getCTAByUpIds(Set<Long> unitPositionIds) {
        return costTimeAgreementRepository.getCTAByUpIds(unitPositionIds);
    }


    public CTAResponseDTO assignCTATOUnitPosition(Long unitPositionId, BigInteger ctaId, LocalDate startLocalDate) {
        CTAResponseDTO ctaResponseDTO = costTimeAgreementRepository.getOneCtaById(ctaId);
        CostTimeAgreement costTimeAgreement = ObjectMapperUtils.copyPropertiesByMapper(ctaResponseDTO, CostTimeAgreement.class);
        costTimeAgreement.setId(null);
        costTimeAgreement.setParentId(ctaId);
        costTimeAgreement.setOrganizationParentId(ctaId);
        costTimeAgreement.setStartDate(startLocalDate);
        List<CTARuleTemplate> ctaRuleTemplates = ObjectMapperUtils.copyPropertiesOfListByMapper(ctaResponseDTO.getRuleTemplates(), CTARuleTemplate.class);
        ctaRuleTemplates.forEach(ctaRuleTemplate -> ctaRuleTemplate.setId(null));
        if (!ctaRuleTemplates.isEmpty()) {
            save(ctaRuleTemplates);
        }
        costTimeAgreement.setUnitPositionId(unitPositionId);
        List<BigInteger> ruleTemplateIds = ctaRuleTemplates.stream().map(MongoBaseEntity::getId).collect(Collectors.toList());
        costTimeAgreement.setRuleTemplateIds(ruleTemplateIds);
        save(costTimeAgreement);

        return costTimeAgreementRepository.getOneCtaById(costTimeAgreement.getId());
    }


    public void updateExistingPhaseIdOfCTA(){
        List<CostTimeAgreement> costTimeAgreements = costTimeAgreementRepository.findAll();
        List<Phase> countryPhase = phaseMongoRepository.findAllBycountryIdAndDeletedFalse(18712l);
        Map<BigInteger,PhaseDefaultName> phaseDefaultNameMap = countryPhase.stream().collect(Collectors.toMap(k->k.getId(),v->v.getPhaseEnum()));
        Map<Long,Map<PhaseDefaultName, BigInteger>> mapMap = new HashMap<>();
        if(CollectionUtils.isNotEmpty(costTimeAgreements)){
            for (CostTimeAgreement costTimeAgreement : costTimeAgreements) {
                List<CTARuleTemplate> ctaRuleTemplates = ctaRuleTemplateRepository.findAllByIdAndDeletedFalse(costTimeAgreement.getRuleTemplateIds());
                if(costTimeAgreement.getOrganization()!=null){
                    if(!mapMap.containsKey(costTimeAgreement.getOrganization().getId())){
                        List<Phase> unitPhases = phaseMongoRepository.findByOrganizationIdAndDeletedFalse(costTimeAgreement.getOrganization().getId());
                        Map<PhaseDefaultName, BigInteger> parentPhasesAndUnitPhaseIdMap = unitPhases.stream().collect(Collectors.toMap(k->k.getPhaseEnum(),v->v.getId()));
                        mapMap.put(costTimeAgreement.getOrganization().getId(),parentPhasesAndUnitPhaseIdMap);
                    }
                    for (CTARuleTemplate ctaRuleTemplate : ctaRuleTemplates) {
                        Map<PhaseDefaultName, BigInteger> parentPhasesAndUnitPhaseIdMap = mapMap.get(costTimeAgreement.getOrganization().getId());
                        for (CTARuleTemplatePhaseInfo ctaRuleTemplatePhaseInfo : ctaRuleTemplate.getPhaseInfo()) {
                            BigInteger phaseId = parentPhasesAndUnitPhaseIdMap.getOrDefault(phaseDefaultNameMap.get(ctaRuleTemplatePhaseInfo.getPhaseId()),ctaRuleTemplatePhaseInfo.getPhaseId());
                            ctaRuleTemplatePhaseInfo.setPhaseId(phaseId);
                        }
                    }
                    if(CollectionUtils.isNotEmpty(ctaRuleTemplates)) {
                        ctaRuleTemplateRepository.saveEntities(ctaRuleTemplates);
                    }
                }
            }
        }
    }

    private boolean isCalculatedValueChanged(List<BigInteger> ruleTemplateIds,List<CTARuleTemplateDTO> ctaRuleTemplateDTOS){

        boolean isCalculatedValueChanged = false;
        if(ctaRuleTemplateDTOS.size()==ruleTemplateIds.size()){
            List<CTARuleTemplate> existingCtaRuleTemplates = ctaRuleTemplateRepository.findAllByIdAndDeletedFalse(ruleTemplateIds);
            Map<BigInteger,CTARuleTemplate> existingCtaRuleTemplateMap = existingCtaRuleTemplates.stream().collect(Collectors.toMap(k->k.getId(),v->v));
            List<CTARuleTemplate> ctaRuleTemplates = ObjectMapperUtils.copyPropertiesOfListByMapper(ctaRuleTemplateDTOS, CTARuleTemplate.class);
            for (CTARuleTemplate ctaRuleTemplate : ctaRuleTemplates) {
                if(existingCtaRuleTemplateMap.containsKey(ctaRuleTemplate.getId())){
                    CTARuleTemplate existingCTARuletemplate = existingCtaRuleTemplateMap.get(ctaRuleTemplate.getId());
                    isCalculatedValueChanged = ctaRuleTemplate.isCalculatedValueChanged(existingCTARuletemplate);
                }else {
                    isCalculatedValueChanged = true;
                }
                if(isCalculatedValueChanged){
                    break;
                }
            }
        }else {
            isCalculatedValueChanged = true;
        }
        return isCalculatedValueChanged;
    }

}


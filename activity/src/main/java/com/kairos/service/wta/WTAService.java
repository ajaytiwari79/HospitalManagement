package com.kairos.service.wta;

import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.activity.ActivityDTO;
import com.kairos.dto.activity.activity.TableConfiguration;
import com.kairos.dto.activity.cta.CTAResponseDTO;
import com.kairos.dto.activity.cta.CTAWTAAndAccumulatedTimebankWrapper;
import com.kairos.dto.activity.time_type.TimeTypeDTO;
import com.kairos.dto.activity.wta.CTAWTAResponseDTO;
import com.kairos.dto.activity.wta.basic_details.*;
import com.kairos.dto.activity.wta.version.WTATableSettingWrapper;
import com.kairos.dto.user.employment.UnitPositionIdDTO;
import com.kairos.dto.user.employment.UnitPositionLinesDTO;
import com.kairos.dto.user.organization.OrganizationBasicDTO;
import com.kairos.enums.MasterDataTypeEnum;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.cta.CTARuleTemplate;
import com.kairos.persistence.model.cta.CostTimeAgreement;
import com.kairos.persistence.model.tag.Tag;
import com.kairos.persistence.model.wta.*;
import com.kairos.persistence.model.wta.templates.WTABaseRuleTemplate;
import com.kairos.persistence.model.wta.templates.WTABuilderService;
import com.kairos.persistence.model.wta.templates.template_types.ChildCareDaysCheckWTATemplate;
import com.kairos.persistence.model.wta.templates.template_types.SeniorDaysPerYearWTATemplate;
import com.kairos.persistence.model.wta.templates.template_types.VetoAndStopBricksWTATemplate;
import com.kairos.persistence.model.wta.templates.template_types.WTAForCareDays;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.cta.CostTimeAgreementRepository;
import com.kairos.persistence.repository.wta.WorkingTimeAgreementMongoRepository;
import com.kairos.persistence.repository.wta.rule_template.RuleTemplateCategoryRepository;
import com.kairos.persistence.repository.wta.rule_template.WTABaseRuleTemplateMongoRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.MongoBaseService;
import com.kairos.service.activity.TimeTypeService;
import com.kairos.service.cta.CostTimeAgreementService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.integration.PlannerSyncService;
import com.kairos.service.solver_config.SolverConfigService;
import com.kairos.service.table_settings.TableSettingService;
import com.kairos.service.tag.TagService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.asDate;
import static com.kairos.commons.utils.DateUtils.asLocalDate;
import static com.kairos.commons.utils.DateUtils.getLocalDate;
import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;
import static com.kairos.commons.utils.ObjectUtils.isNotNull;
import static com.kairos.constants.AppConstants.COPY_OF;
import static com.kairos.persistence.model.constants.TableSettingConstants.ORGANIZATION_AGREEMENT_VERSION_TABLE_ID;
import static java.util.stream.Collectors.toMap;


/**
 * Created by pawanmandhan on 2/8/17.
 */

@Transactional
@Service
public class WTAService extends MongoBaseService {
    private static final Logger logger = LoggerFactory.getLogger(WTAService.class);
    @Inject
    private WorkingTimeAgreementMongoRepository wtaRepository;
    @Inject
    private RuleTemplateCategoryRepository ruleTemplateCategoryRepository;
    @Inject
    private WTABaseRuleTemplateMongoRepository wtaBaseRuleTemplateGraphRepository;
    @Inject
    private RuleTemplateService ruleTemplateService;
    @Inject
    private RuleTemplateCategoryService ruleTemplateCategoryService;
    @Inject
    private TagService tagService;
    @Inject
    private WTABuilderService wtaBuilderService;
    @Inject
    private ActivityMongoRepository activityMongoRepository;
    @Inject
    private TimeTypeService timeTypeService;
    @Inject
    private PlannerSyncService plannerSyncService;
    @Inject
    private SolverConfigService solverConfigService;
    @Autowired
    private ExceptionService exceptionService;
    @Inject
    private TableSettingService tableSettingService;
    @Inject
    private CostTimeAgreementService costTimeAgreementService;
    @Inject
    private CostTimeAgreementRepository costTimeAgreementRepository;
    @Inject
    private UserIntegrationService userIntegrationService;


    public WTAResponseDTO createWta(long referenceId, WTADTO wtaDTO, boolean creatingFromCountry, boolean mapWithOrgType) {
        if (creatingFromCountry) {
            boolean alreadyExists = mapWithOrgType ? wtaRepository.isWTAExistWithSameOrgTypeAndSubType(wtaDTO.getOrganizationType(), wtaDTO.getOrganizationSubType(), wtaDTO.getName()) : wtaRepository.getWtaByName(wtaDTO.getName(), referenceId);
            if (alreadyExists) {
                exceptionService.duplicateDataException("message.wta.name.duplicate", wtaDTO.getName());
            }

        } else if (wtaRepository.isWTAExistByOrganizationIdAndName(referenceId, wtaDTO.getName())) {
            exceptionService.duplicateDataException("message.wta.name.duplicate", wtaDTO.getName());
        }

        WorkingTimeAgreement wta = new WorkingTimeAgreement();
        if (isNotNull(wtaDTO.getEndDate())) {
            if (wtaDTO.getStartDate().isAfter(wtaDTO.getEndDate())) {
                exceptionService.invalidRequestException("message.wta.start-end-date");
            }
            wta.setEndDate(wtaDTO.getEndDate());
        }
        WTABasicDetailsDTO wtaBasicDetailsDTO = userIntegrationService.getWtaRelatedInfo(wtaDTO.getExpertiseId(), wtaDTO.getOrganizationSubType(), referenceId, null, wtaDTO.getOrganizationType(), wtaDTO.getUnitIds());
        if (creatingFromCountry) {
            if (!Optional.ofNullable(wtaBasicDetailsDTO.getCountryDTO()).isPresent()) {
                exceptionService.dataNotFoundByIdException("message.country.id", referenceId);
            }
            wta.setCountryId(referenceId);
        }
        wta.setStartDate(wtaDTO.getStartDate());
        if (isCollectionNotEmpty(wtaDTO.getTags())) {
            List<Tag> tags = tagService.getCountryTagsByIdsAndMasterDataType(wtaDTO.getTags(), MasterDataTypeEnum.WTA);
            wta.setTags(tags.stream().map(t -> t.getId()).collect(Collectors.toList()));
        }
        prepareWtaWhileCreate(wta, wtaDTO, wtaBasicDetailsDTO);
        List<WTABaseRuleTemplate> ruleTemplates = new ArrayList<>();
        if (isCollectionNotEmpty(wtaDTO.getRuleTemplates())) {
            ruleTemplates = wtaBuilderService.copyRuleTemplates(wtaDTO.getRuleTemplates(), true);
            save(ruleTemplates);
            List<BigInteger> ruleTemplatesIds = ruleTemplates.stream().map(ruleTemplate -> ruleTemplate.getId()).collect(Collectors.toList());
            wta.setRuleTemplateIds(ruleTemplatesIds);

        }
        WTAResponseDTO wtaResponseDTO = ObjectMapperUtils.copyPropertiesByMapper(wta, WTAResponseDTO.class);
        wtaResponseDTO.setRuleTemplates(WTABuilderService.copyRuleTemplatesToDTO(ruleTemplates));
        if (creatingFromCountry) {
            save(wta);
            wtaResponseDTO.setId(wta.getId());
        }
        Map<Long, WTAResponseDTO> wtaResponseDTOMap = assignWTAToNewOrganization(wta, wtaDTO, wtaBasicDetailsDTO, creatingFromCountry);

        if (!creatingFromCountry) {
            wtaResponseDTO = wtaResponseDTOMap.get(referenceId);
        }
        return wtaResponseDTO;
    }


    private Map<Long, WTAResponseDTO> assignWTAToNewOrganization(WorkingTimeAgreement wta, WTADTO wtadto, WTABasicDetailsDTO wtaBasicDetailsDTO, boolean creatingFromCountry) {
        List<WorkingTimeAgreement> workingTimeAgreements = new ArrayList<>(wtaBasicDetailsDTO.getOrganizations().size());
        List<Long> organizationIds = wtaBasicDetailsDTO.getOrganizations().stream().map(o -> o.getId()).collect(Collectors.toList());
        List<WorkingTimeAgreement> workingTimeAgreementList = wtaRepository.findWTAByUnitIdsAndName(organizationIds, wtadto.getName());
        Map<String, WorkingTimeAgreement> workingTimeAgreementMap = workingTimeAgreementList.stream().collect(toMap(k -> k.getName() + "_" + k.getOrganization().getId() + "_" + k.getOrganizationType().getId(), v -> v));
        Map<String, BigInteger> activitiesIdsAndUnitIdsMap = getActivityMapWithUnitId(wtadto.getRuleTemplates(), wtaBasicDetailsDTO.getOrganizations());
        Map<Long, WTAResponseDTO> wtaResponseDTOMap = new HashMap<>();
        wtaBasicDetailsDTO.getOrganizations().forEach(organization ->
        {
            if (workingTimeAgreementMap.get(wtadto.getName() + "_" + organization.getId() + "_" + wta.getOrganizationType().getId()) == null) {
                WorkingTimeAgreement workingTimeAgreement = new WorkingTimeAgreement();
                wtaBuilderService.getWtaObject(wta, workingTimeAgreement);
                if (creatingFromCountry) {
                    workingTimeAgreement.setCountryParentWTA(wta.getId());
                }
                workingTimeAgreement.setDisabled(false);
                if (CollectionUtils.isNotEmpty(wtadto.getRuleTemplates())) {

                    List<WTABaseRuleTemplate> ruleTemplates = wtaBuilderService.copyRuleTemplatesWithUpdateActivity(activitiesIdsAndUnitIdsMap, organization.getId(), wtadto.getRuleTemplates(), true);
                    ruleTemplates.forEach(wtaBaseRuleTemplate -> {
                        wtaBaseRuleTemplate.setCountryId(null);
                    });
                    save(ruleTemplates);
                    List<BigInteger> ruleTemplatesIds = ruleTemplates.stream().map(ruleTemplate -> ruleTemplate.getId()).collect(Collectors.toList());
                    workingTimeAgreement.setRuleTemplateIds(ruleTemplatesIds);
                    WTABuilderService.copyRuleTemplatesToDTO(ruleTemplates);

                }
                workingTimeAgreement.setOrganization(new WTAOrganization(organization.getId(), organization.getName(), organization.getDescription()));
                workingTimeAgreement.setOrganizationType(new OrganizationType(wtaBasicDetailsDTO.getOrganizationType().getId(), wtaBasicDetailsDTO.getOrganizationType().getName(), wtaBasicDetailsDTO.getOrganizationType().getDescription()));
                workingTimeAgreement.setOrganizationSubType(new OrganizationType(wtaBasicDetailsDTO.getOrganizationSubType().getId(), wtaBasicDetailsDTO.getOrganizationSubType().getName(), wtaBasicDetailsDTO.getOrganizationSubType().getDescription()));
                WTAResponseDTO wtaResponseDTO = ObjectMapperUtils.copyPropertiesByMapper(workingTimeAgreement, WTAResponseDTO.class);
                wtaResponseDTOMap.put(organization.getId(), wtaResponseDTO);
                workingTimeAgreements.add(workingTimeAgreement);
            }
        });
        if (!workingTimeAgreements.isEmpty()) {
            save(workingTimeAgreements);
            workingTimeAgreements.forEach(workingTimeAgreement -> {
                        WTAResponseDTO wtaResponseDTO = wtaResponseDTOMap.get(workingTimeAgreement.getOrganization().getId());
                        wtaResponseDTO.setId(workingTimeAgreement.getId());

                    }
            );
        }

        return wtaResponseDTOMap;
    }


    private WorkingTimeAgreement prepareWtaWhileCreate(WorkingTimeAgreement wta, WTADTO wtaDTO, WTABasicDetailsDTO wtaBasicDetailsDTO) {
        if (!Optional.ofNullable(wtaBasicDetailsDTO.getExpertiseResponse()).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.expertise.id", wtaDTO.getExpertiseId());
        }
        if (!Optional.ofNullable(wtaBasicDetailsDTO.getOrganizationType()).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.organization.type", wtaDTO.getOrganizationType());
        }
        if (!Optional.ofNullable(wtaBasicDetailsDTO.getOrganizationSubType()).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.organization.subtype", wtaDTO.getOrganizationSubType());
        }
        wta.setDescription(wtaDTO.getDescription());
        wta.setName(wtaDTO.getName());
        wta.setExpertise(new Expertise(wtaBasicDetailsDTO.getExpertiseResponse().getId(), wtaBasicDetailsDTO.getExpertiseResponse().getName(), wtaBasicDetailsDTO.getExpertiseResponse().getDescription()));
        wta.setOrganizationType(new OrganizationType(wtaBasicDetailsDTO.getOrganizationType().getId(), wtaBasicDetailsDTO.getOrganizationType().getName(), wtaBasicDetailsDTO.getOrganizationType().getDescription()));
        wta.setOrganizationSubType(new OrganizationType(wtaBasicDetailsDTO.getOrganizationSubType().getId(), wtaBasicDetailsDTO.getOrganizationSubType().getName(), wtaBasicDetailsDTO.getOrganizationSubType().getDescription()));
        return wta;
    }


    public WTAResponseDTO updateWtaOfCountry(Long countryId, BigInteger wtaId, WTADTO updateDTO) {
        if (isNotNull(updateDTO.getEndDate()) && updateDTO.getStartDate().isAfter(updateDTO.getEndDate())) {
            exceptionService.actionNotPermittedException("message.wta.start-end-date");
        }
        WorkingTimeAgreement workingTimeAgreement = wtaRepository.getWtaByNameExcludingCurrent(updateDTO.getName(), countryId, wtaId, updateDTO.getOrganizationType(), updateDTO.getOrganizationSubType());
        if (Optional.ofNullable(workingTimeAgreement).isPresent()) {
            exceptionService.duplicateDataException("message.wta.name.duplicate", updateDTO.getName());
        }
        WorkingTimeAgreement oldWta = wtaRepository.getWTAByCountryId(countryId, wtaId);
        if (!Optional.ofNullable(oldWta).isPresent()) {
            logger.info("wta not found while updating at unit %d", wtaId);
            exceptionService.dataNotFoundByIdException("message.wta.id", wtaId);
        }
        WTABasicDetailsDTO wtaBasicDetailsDTO = userIntegrationService.getWtaRelatedInfo(updateDTO.getExpertiseId(), updateDTO.getOrganizationSubType(), countryId, null, updateDTO.getOrganizationType(), Collections.EMPTY_LIST);
        WTAResponseDTO wtaResponseDTO = prepareWtaWhileUpdate(oldWta, updateDTO, wtaBasicDetailsDTO);
        wtaResponseDTO.setStartDate(oldWta.getStartDate());
        wtaResponseDTO.setEndDate(oldWta.getEndDate());
        return wtaResponseDTO;
    }


    private WTAResponseDTO prepareWtaWhileUpdate(WorkingTimeAgreement oldWta, WTADTO updateDTO, WTABasicDetailsDTO wtaBasicDetailsDTO) {
        if (!oldWta.getOrganizationType().getId().equals(updateDTO.getOrganizationType())) {
            exceptionService.actionNotPermittedException("message.organization.type.update", updateDTO.getOrganizationType());
        }
        if (!oldWta.getOrganizationSubType().getId().equals(updateDTO.getOrganizationSubType())) {
            exceptionService.actionNotPermittedException("message.organization.subtype.update", updateDTO.getOrganizationSubType());
        }
        List<WTABaseRuleTemplate> ruleTemplates = new ArrayList<>();
        if (isCollectionNotEmpty(updateDTO.getRuleTemplates())) {
            for (WTABaseRuleTemplateDTO ruleTemplateDTO : updateDTO.getRuleTemplates()) {
                ruleTemplates.add(wtaBuilderService.copyRuleTemplate(ruleTemplateDTO, true));
            }
            wtaBaseRuleTemplateGraphRepository.saveEntities(ruleTemplates);
            oldWta.setRuleTemplateIds(ruleTemplates.stream().map(ruleTemplate -> ruleTemplate.getId()).collect(Collectors.toList()));
        }
        boolean sameFutureDateWTA = oldWta.getStartDate().isEqual(updateDTO.getStartDate()) && (updateDTO.getStartDate().isAfter(DateUtils.getCurrentLocalDate()) || updateDTO.getStartDate().isEqual(DateUtils.getCurrentLocalDate()));
        if (!sameFutureDateWTA) {
            logger.info("Its a future date so we don't need to create we need to update in same");// since calculative values are changed and dates are not same so we need to make a new copy
            oldWta.setStartDate(updateDTO.getStartDate());
        }
        oldWta.setEndDate(updateDTO.getEndDate());
        // This is may be not used as We cant change expertise
        if (!oldWta.getExpertise().getId().equals(updateDTO.getExpertiseId())) {
            if (!Optional.ofNullable(wtaBasicDetailsDTO.getExpertiseResponse()).isPresent()) {
                exceptionService.dataNotFoundByIdException("message.expertise.id", updateDTO.getExpertiseId());
            }
            oldWta.setExpertise(new Expertise(wtaBasicDetailsDTO.getExpertiseResponse().getId(), wtaBasicDetailsDTO.getExpertiseResponse().getName(), wtaBasicDetailsDTO.getExpertiseResponse().getDescription()));
        }
        oldWta.setDescription(updateDTO.getDescription());
        oldWta.setName(updateDTO.getName());
        oldWta.setOrganizationType(new OrganizationType(wtaBasicDetailsDTO.getOrganizationType().getId(), wtaBasicDetailsDTO.getOrganizationType().getName(), wtaBasicDetailsDTO.getOrganizationType().getDescription()));
        oldWta.setOrganizationSubType(new OrganizationType(wtaBasicDetailsDTO.getOrganizationSubType().getId(), wtaBasicDetailsDTO.getOrganizationSubType().getName(), wtaBasicDetailsDTO.getOrganizationSubType().getDescription()));
        save(oldWta);
        WTAResponseDTO wtaResponseDTO = ObjectMapperUtils.copyPropertiesByMapper(oldWta, WTAResponseDTO.class);
        wtaResponseDTO.setRuleTemplates(WTABuilderService.copyRuleTemplatesToDTO(ruleTemplates));

        return wtaResponseDTO;
    }

    public WTAResponseDTO getWta(BigInteger wtaId) {
        WTAQueryResultDTO wtaQueryResult = wtaRepository.getOne(wtaId);
        return ObjectMapperUtils.copyPropertiesByMapper(wtaQueryResult, WTAResponseDTO.class);
    }


    public List<WTABaseRuleTemplateDTO> getwtaRuletemplates(BigInteger wtaId) {
        WTAQueryResultDTO wtaQueryResult = wtaRepository.getOne(wtaId);
        return ObjectMapperUtils.copyPropertiesByMapper(wtaQueryResult, WTAResponseDTO.class).getRuleTemplates();
    }

    public boolean removeWta(BigInteger wtaId) {
        WorkingTimeAgreement wta = wtaRepository.findOne(wtaId);
        if (!Optional.ofNullable(wta).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.wta.id", wtaId);
        }
        wta.setDeleted(true);
        save(wta);
        return true;
    }

    public List<WTAResponseDTO> getAllWTAByOrganizationId(long organizationId) {
        List<WTAQueryResultDTO> wtaQueryResultDTOS = wtaRepository.getAllWTAByOrganizationTypeId(organizationId);
        List<WTAResponseDTO> wtaResponseDTOS = new ArrayList<>();
        wtaQueryResultDTOS.forEach(wta -> {
            wtaResponseDTOS.add(ObjectMapperUtils.copyPropertiesByMapper(wta, WTAResponseDTO.class));
        });
        return wtaResponseDTOS;
    }

    public List<WTAResponseDTO> getAllWTAByCountryId(long countryId) {
        List<WTAQueryResultDTO> wtaQueryResultDTOS = wtaRepository.getAllWTAByCountryId(countryId);
        List<WTAResponseDTO> wtaResponseDTOS = new ArrayList<>();
        wtaQueryResultDTOS.forEach(wta -> {
            WTAResponseDTO wtaResponseDTO = ObjectMapperUtils.copyPropertiesByMapper(wta, WTAResponseDTO.class);
            wtaResponseDTOS.add(wtaResponseDTO);
        });

        return wtaResponseDTOS;
    }

    public List<WTAResponseDTO> getAllWTAByOrganizationSubType(long organizationSubTypeId, long countryId) {
        List<WTAQueryResultDTO> wtaQueryResultDTOS = wtaRepository.getAllWTAByOrganizationSubTypeIdAndCountryId(organizationSubTypeId, countryId);
        List<WTAResponseDTO> wtaResponseDTOS = new ArrayList<>();
        wtaQueryResultDTOS.forEach(wta -> wtaResponseDTOS.add(ObjectMapperUtils.copyPropertiesByMapper(wta, WTAResponseDTO.class)));
        return wtaResponseDTOS;
    }

    public List<WTAResponseDTO> getAllWTAWithOrganization(long countryId) {
        List<WTAQueryResultDTO> wtaQueryResultDTOS = wtaRepository.getAllWTAWithOrganization(countryId);
        List<WTAResponseDTO> wtaResponseDTOS = new ArrayList<>();
        wtaQueryResultDTOS.forEach(wta -> {
            wtaResponseDTOS.add(ObjectMapperUtils.copyPropertiesByMapper(wta, WTAResponseDTO.class));
        });
        return wtaResponseDTOS;
    }

    public List<WTAResponseDTO> getAllWTAWithWTAId(long countryId, BigInteger wtaId) {
        /*List<Map<String, Object>> map =
        List<Object> objectList = new ArrayList<>();
        for (Map<String, Object> result : map) {
            objectList.add(result.get("result"));
        }*/
        List<WTAQueryResultDTO> wtaQueryResultDTOS = wtaRepository.getAllWTAWithWTAId(countryId, wtaId);
        List<WTAResponseDTO> wtaResponseDTOS = new ArrayList<>();
        wtaQueryResultDTOS.forEach(wta -> {
            wtaResponseDTOS.add(ObjectMapperUtils.copyPropertiesByMapper(wta, WTAResponseDTO.class));
        });
        return wtaResponseDTOS;
    }


    public Map<String, Object> setWtaWithOrganizationType(Long countryId, BigInteger wtaId, long organizationSubTypeId, boolean checked) {
        Map<String, Object> map = new HashMap<>();
        WTABasicDetailsDTO wtaBasicDetailsDTO = userIntegrationService.getWtaRelatedInfo(0L, organizationSubTypeId, countryId, null, 0L, Collections.EMPTY_LIST);
        if (!Optional.ofNullable(wtaBasicDetailsDTO.getOrganizationSubType()).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.organization.subtype.id", organizationSubTypeId);
        }
        WorkingTimeAgreement wta = wtaRepository.findOne(wtaId);
        if (!Optional.ofNullable(wta).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.wta.id", wtaId);
        }
        if (checked) {
            List<WTABaseRuleTemplateDTO> wtaBaseRuleTemplates = wtaBaseRuleTemplateGraphRepository.findAllByIdIn(wta.getRuleTemplateIds());
            WTADTO wtadto = new WTADTO(COPY_OF + wta.getName(), wta.getDescription(), wta.getExpertise().getId(), wta.getStartDate(), wta.getEndDate() == null ? null : wta.getEndDate(), wtaBaseRuleTemplates, wtaBasicDetailsDTO.getOrganizationType().getId(), wtaBasicDetailsDTO.getOrganizationSubType().getId());
            WTAResponseDTO wtaResponseDTO = createWta(wta.getCountryId(), wtadto, true, true);
            map.put("wta", wtaResponseDTO);
            map.put("ruleTemplate", wtaResponseDTO.getRuleTemplates());
        } else {
            wta.setDeleted(true);
            save(wta);
        }
        return map;

    }


    public WTADefaultDataInfoDTO getDefaultWtaInfo(Long countryId) {
        List<ActivityDTO> activityDTOS = activityMongoRepository.findByDeletedFalseAndCountryId(countryId);
        List<TimeTypeDTO> timeTypeDTOS = timeTypeService.getAllTimeType(null, countryId);
        WTADefaultDataInfoDTO wtaDefaultDataInfoDTO = userIntegrationService.getWtaTemplateDefaultDataInfo(countryId);
        wtaDefaultDataInfoDTO.setTimeTypes(timeTypeDTOS);
        wtaDefaultDataInfoDTO.setActivityList(activityDTOS);
        return wtaDefaultDataInfoDTO;
    }

    public WTADefaultDataInfoDTO getDefaultWtaInfoForUnit(Long unitId) {
        WTADefaultDataInfoDTO wtaDefaultDataInfoDTO = userIntegrationService.getWtaTemplateDefaultDataInfoByUnitId();
        List<ActivityDTO> activities = activityMongoRepository.findByDeletedFalseAndUnitId(unitId);
        List<TimeTypeDTO> timeTypeDTOS = timeTypeService.getAllTimeType(null, wtaDefaultDataInfoDTO.getCountryID());
        wtaDefaultDataInfoDTO.setTimeTypes(timeTypeDTOS);
        wtaDefaultDataInfoDTO.setActivityList(activities);
        return wtaDefaultDataInfoDTO;
    }

    public CTAWTAAndAccumulatedTimebankWrapper getWTACTAByUpIds(Set<Long> unitPositionIds) {
        List<WTAQueryResultDTO> wtaQueryResultDTOS = wtaRepository.getAllWTAByUpIds(unitPositionIds, new Date());
        List<WTAResponseDTO> wtaResponseDTOS = ObjectMapperUtils.copyPropertiesOfListByMapper(wtaQueryResultDTOS, WTAResponseDTO.class);
        List<CTAResponseDTO> ctaResponseDTOS = costTimeAgreementService.getCTAByUpIds(unitPositionIds);
        return new CTAWTAAndAccumulatedTimebankWrapper(ctaResponseDTOS, wtaResponseDTOS);
    }

    public CTAWTAAndAccumulatedTimebankWrapper getWTACTAByOfUnitPosition(Long unitPositionId, LocalDate startDate) {
        WorkingTimeAgreement wta = wtaRepository.getWTABasicByUnitPositionAndDate(unitPositionId, asDate(startDate));
        CostTimeAgreement cta = costTimeAgreementRepository.getCTABasicByUnitPositionAndDate(unitPositionId, asDate(startDate));
        CTAWTAAndAccumulatedTimebankWrapper ctawtaAndAccumulatedTimebankWrapper = new CTAWTAAndAccumulatedTimebankWrapper();
        if (Optional.ofNullable(wta).isPresent()) {
            WTAResponseDTO wtaResponseDTO = new WTAResponseDTO(wta.getName(), wta.getId(), wta.getParentId());
            ctawtaAndAccumulatedTimebankWrapper.setWta(Collections.singletonList(wtaResponseDTO));
        }
        if (Optional.ofNullable(cta).isPresent()) {
            CTAResponseDTO ctaResponseDTO = new CTAResponseDTO(cta.getName(), cta.getId(), cta.getParentId());
            ctawtaAndAccumulatedTimebankWrapper.setCta(Collections.singletonList(ctaResponseDTO));
        }
        return ctawtaAndAccumulatedTimebankWrapper;
    }

    public WTATableSettingWrapper getWTAWithVersionIds(Long unitId, List<Long> upIds) {
        List<WTAQueryResultDTO> currentWTAList = wtaRepository.getAllParentWTAByIds(upIds);
        List<WTAQueryResultDTO> versionsOfWTAs = wtaRepository.getWTAWithVersionIds(upIds);
        List<WTAResponseDTO> parentWTA = ObjectMapperUtils.copyPropertiesOfListByMapper(currentWTAList, WTAResponseDTO.class);
        Map<Long, List<WTAQueryResultDTO>> verionWTAMap = versionsOfWTAs.stream().collect(Collectors.groupingBy(k -> k.getUnitPositionId(), Collectors.toList()));
        parentWTA.forEach(currentWTA -> {
            List<WTAResponseDTO> versionWTAs = ObjectMapperUtils.copyPropertiesOfListByMapper(verionWTAMap.get(currentWTA.getUnitPositionId()), WTAResponseDTO.class);
            if (versionWTAs != null && !versionWTAs.isEmpty()) {
                currentWTA.setVersions(versionWTAs);
            }
        });
        TableConfiguration tableConfiguration = tableSettingService.getTableConfigurationByTableId(unitId, ORGANIZATION_AGREEMENT_VERSION_TABLE_ID);
        WTATableSettingWrapper wtaTableSettingWrapper = new WTATableSettingWrapper(parentWTA, tableConfiguration);
        return wtaTableSettingWrapper;
    }


    public CTAWTAAndAccumulatedTimebankWrapper assignCTAWTAToUnitPosition(Long unitPositionId, BigInteger wtaId, BigInteger ctaId, LocalDate startDate) {
        CTAWTAAndAccumulatedTimebankWrapper ctawtaAndAccumulatedTimebankWrapper = new CTAWTAAndAccumulatedTimebankWrapper();
        if (wtaId != null) {
            WTAResponseDTO wtaResponseDTO = assignWTATOUnitPosition(unitPositionId, wtaId, startDate);
            ctawtaAndAccumulatedTimebankWrapper.setWta(Arrays.asList(wtaResponseDTO));
        }
        if (ctaId != null) {
            CTAResponseDTO ctaResponseDTO = costTimeAgreementService.assignCTATOUnitPosition(unitPositionId, ctaId, startDate);
            ctawtaAndAccumulatedTimebankWrapper.setCta(Arrays.asList(ctaResponseDTO));
        }
        return ctawtaAndAccumulatedTimebankWrapper;

    }

    public WTAResponseDTO getWTAOfUnitPosition(Long unitPositionId) {
        WTAQueryResultDTO wtaQueryResultDTO = wtaRepository.getWTAByUnitPositionIdAndDate(unitPositionId, new Date());
        return ObjectMapperUtils.copyPropertiesByMapper(wtaQueryResultDTO, WTAResponseDTO.class);
    }


    private WTAResponseDTO assignWTATOUnitPosition(Long unitPositionId, BigInteger wtaId, LocalDate startLocalDate) {
        WTAQueryResultDTO wtaQueryResultDTO = wtaRepository.getOne(wtaId);
        if (!Optional.ofNullable(wtaQueryResultDTO).isPresent()) {
            exceptionService.duplicateDataException("message.wta.id", wtaId);
        }
        WTAResponseDTO wtaResponseDTO = ObjectMapperUtils.copyPropertiesByMapper(wtaQueryResultDTO, WTAResponseDTO.class);
        WorkingTimeAgreement workingTimeAgreement = ObjectMapperUtils.copyPropertiesByMapper(wtaResponseDTO, WorkingTimeAgreement.class);
        List<WTABaseRuleTemplate> ruleTemplates = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(wtaResponseDTO.getRuleTemplates())) {
            ruleTemplates = wtaBuilderService.copyRuleTemplates(wtaResponseDTO.getRuleTemplates(), true);
            save(ruleTemplates);
            List<BigInteger> ruleTemplatesIds = ruleTemplates.stream().map(ruleTemplate -> ruleTemplate.getId()).collect(Collectors.toList());
            workingTimeAgreement.setRuleTemplateIds(ruleTemplatesIds);
        }
        workingTimeAgreement.setUnitPositionId(unitPositionId);
        if (wtaQueryResultDTO.getEndDate() != null) {
            workingTimeAgreement.setEndDate(wtaQueryResultDTO.getEndDate());
        }
        workingTimeAgreement.setStartDate(wtaQueryResultDTO.getStartDate());
        workingTimeAgreement.setId(null);
        workingTimeAgreement.setOrganization(null);
        workingTimeAgreement.setOrganizationParentId(wtaQueryResultDTO.getId());
        workingTimeAgreement.setParentId(wtaResponseDTO.getId());

        save(workingTimeAgreement);
        wtaResponseDTO = ObjectMapperUtils.copyPropertiesByMapper(workingTimeAgreement, WTAResponseDTO.class);
        wtaResponseDTO.setRuleTemplates(WTABuilderService.copyRuleTemplatesToDTO(ruleTemplates));
        return wtaResponseDTO;
    }

    public Boolean assignWTAToNewOrganization(List<Long> subTypeIds, Long organisationId, Long countryId) {
        List<WTAQueryResultDTO> wtaQueryResultDTOS = wtaRepository.getAllWTABySubType(subTypeIds, countryId);
        List<WorkingTimeAgreement> workingTimeAgreements = new ArrayList<>();
        wtaQueryResultDTOS.forEach(w -> {
            WTAResponseDTO wtaResponseDTO = ObjectMapperUtils.copyPropertiesByMapper(w, WTAResponseDTO.class);
            WorkingTimeAgreement workingTimeAgreement = ObjectMapperUtils.copyPropertiesByMapper(wtaResponseDTO, WorkingTimeAgreement.class);
            List<WTABaseRuleTemplate> ruleTemplates = new ArrayList<>();
            if (wtaResponseDTO.getRuleTemplates() != null && !wtaResponseDTO.getRuleTemplates().isEmpty()) {
                ruleTemplates = wtaBuilderService.copyRuleTemplates(wtaResponseDTO.getRuleTemplates(), true);
                save(ruleTemplates);
                List<BigInteger> ruleTemplatesIds = ruleTemplates.stream().map(ruleTemplate -> ruleTemplate.getId()).collect(Collectors.toList());
                workingTimeAgreement.setRuleTemplateIds(ruleTemplatesIds);
            }
            workingTimeAgreement.setId(null);
            workingTimeAgreement.setOrganization(new WTAOrganization(organisationId, "", ""));
            workingTimeAgreement.setCountryParentWTA(w.getId());
            workingTimeAgreement.setCountryId(null);
            workingTimeAgreement.setParentId(wtaResponseDTO.getId());
            workingTimeAgreements.add(workingTimeAgreement);
            //wtaResponseDTO = ObjectMapperUtils.copyPropertiesByMapper(workingTimeAgreement,WTAResponseDTO.class);
            //wtaResponseDTO.setRuleTemplate(WTABuilderService.copyRuleTemplatesToDTO(ruleTemplates));

        });
        if (!workingTimeAgreements.isEmpty()) {
            save(workingTimeAgreements);
            solverConfigService.createDefaultConfig(organisationId);
        }
        return true;
    }

    public List<WorkingTimeAgreement> findAllByIdAndDeletedFalse(Set<BigInteger> wtaIds) {
        return wtaRepository.findAllByIdsInAndDeletedFalse(wtaIds);
    }

    public WTAResponseDTO updateWtaOfUnitPosition(Long unitId, WTADTO wtadto, Boolean oldUnitPositionPublished) {
        Optional<WorkingTimeAgreement> oldWta = wtaRepository.findById(wtadto.getId());
        if (!Optional.ofNullable(oldWta).isPresent()) {
            logger.info("wta not found while updating at unit %d", wtadto.getId());
            exceptionService.dataNotFoundByIdException("message.wta.id", wtadto.getId());
        }
        WTAResponseDTO wtaResponseDTO;
        if (oldUnitPositionPublished) {
            wtaResponseDTO = updateWTAOfPublishedUnitPosition(oldWta.get(), wtadto);
        } else {
            wtaResponseDTO = updateWTAOfUnpublishedUnitPosition(oldWta.get(), wtadto);
            wtaRepository.save(oldWta.get());
        }
        wtaResponseDTO.setStartDate(wtadto.getStartDate());
        if(isNotNull(wtadto.getEndDate())) {
            wtaResponseDTO.setEndDate(wtadto.getEndDate());
        }
        return wtaResponseDTO;
    }

    private WTAResponseDTO updateWTAOfUnpublishedUnitPosition(WorkingTimeAgreement oldWta, WTADTO updateDTO) {
        if(!updateDTO.getStartDate().equals(oldWta.getStartDate())){
            boolean wtaExists = wtaRepository.wtaExistsByUnitPositionIdAndDatesAndNotEqualToId(oldWta.getId(),oldWta.getUnitPositionId(),asDate(updateDTO.getStartDate()),isNotNull(updateDTO.getEndDate()) ? asDate(updateDTO.getEndDate()): null);
            if(wtaExists){
                exceptionService.duplicateDataException("error.wta.invalid",updateDTO.getStartDate(),isNotNull(updateDTO.getEndDate()) ? asDate(updateDTO.getEndDate()): "");
            }
        }
        oldWta.setDescription(updateDTO.getDescription());
        oldWta.setName(updateDTO.getName());
        List<WTABaseRuleTemplate> ruleTemplates = new ArrayList<>();
        if (isCollectionNotEmpty(updateDTO.getRuleTemplates())) {
            ruleTemplates = wtaBuilderService.copyRuleTemplates(updateDTO.getRuleTemplates(), false);
            save(ruleTemplates);
            List<BigInteger> ruleTemplatesIds = ruleTemplates.stream().map(ruleTemplate -> ruleTemplate.getId()).collect(Collectors.toList());
            oldWta.setRuleTemplateIds(ruleTemplatesIds);
        }
        oldWta.setEndDate(isNotNull(updateDTO.getEndDate()) ?updateDTO.getEndDate() : null);
        WTAResponseDTO wtaResponseDTO = ObjectMapperUtils.copyPropertiesByMapper(oldWta, WTAResponseDTO.class);
        wtaResponseDTO.setRuleTemplates(WTABuilderService.copyRuleTemplatesToDTO(ruleTemplates));

        return wtaResponseDTO;
    }


    public List<CTAWTAResponseDTO> copyWtaCTA(List<UnitPositionIdDTO> unitPositionIDs) {

        // List<CTAWTADTO> ctaWtas = wtaRepository.getCTAWTAByUnitPositionId(oldUnitPositionId);

        logger.info("Inside wtaservice");
        List<Long> oldUnitPositionIds = unitPositionIDs.stream().map(unitPositionIdDTO -> unitPositionIdDTO.getOldUnitPositionID()).collect(Collectors.toList());
        Map<Long, Long> newOldunitPositionIdMap = unitPositionIDs.stream().collect(toMap(k -> k.getOldUnitPositionID(), v -> v.getNewUnitPositionID()));
        List<WTAQueryResultDTO> oldWtas = wtaRepository.getWTAByUnitPositionIds(oldUnitPositionIds, DateUtils.getCurrentDate());

        List<WorkingTimeAgreement> newWtas = new ArrayList<>();

        // cta = ctawtaWrapper.getCta();
        for (WTAQueryResultDTO wta : oldWtas) {
            List<WTABaseRuleTemplate> ruleTemplates = wta.getRuleTemplates();

            for (WTABaseRuleTemplate wtaBaseRuleTemplate : ruleTemplates) {
                wtaBaseRuleTemplate.setId(null);
            }

            if (!ruleTemplates.isEmpty()) {

                save(ruleTemplates);
            }

            List<BigInteger> ruleTemplateIds = ruleTemplates.stream().map(ruleTemplate -> ruleTemplate.getId()).collect(Collectors.toList());

            WorkingTimeAgreement wtaDB = ObjectMapperUtils.copyPropertiesByMapper(wta, WorkingTimeAgreement.class);
            wtaDB.setUnitPositionId(newOldunitPositionIdMap.get(wta.getUnitPositionId()));
            wtaDB.setRuleTemplateIds(ruleTemplateIds);
            wtaDB.setId(null);
            newWtas.add(wtaDB);
        }

        if (!newWtas.isEmpty()) {
            save(newWtas);
        }
        List<CTAResponseDTO> ctaResponseDTOs = costTimeAgreementRepository.getCTAByUnitPositionIds(oldUnitPositionIds, DateUtils.getCurrentDate());


        List<CostTimeAgreement> newCTAs = new ArrayList<>();
        for (CTAResponseDTO cta : ctaResponseDTOs) {
            CostTimeAgreement newCTA = ObjectMapperUtils.copyPropertiesByMapper(cta, CostTimeAgreement.class);
            List<CTARuleTemplate> ctaRuleTemplates = ObjectMapperUtils.copyPropertiesOfListByMapper(cta.getRuleTemplates(), CTARuleTemplate.class);
            for (CTARuleTemplate ctaRuleTemplate : ctaRuleTemplates) {
                ctaRuleTemplate.setId(null);
            }
            if (!ctaRuleTemplates.isEmpty()) {
                save(ctaRuleTemplates);
            }

            List<BigInteger> ctaRuleTemplateIds = ctaRuleTemplates.stream().map(ctaRuleTemplate -> ctaRuleTemplate.getId()).collect(Collectors.toList());
            newCTA.setRuleTemplateIds(ctaRuleTemplateIds);
            newCTA.setUnitPositionId(newOldunitPositionIdMap.get(cta.getUnitPositionId()));
            newCTA.setId(null);
            newCTAs.add(newCTA);
        }
        if (!newCTAs.isEmpty()) {
            save(newCTAs);
        }

        Map<Long, CostTimeAgreement> ctaMap = newCTAs.stream().collect(toMap(k -> k.getUnitPositionId(), v -> v));
        List<CTAWTAResponseDTO> ctaWtas = new ArrayList<>();
        for (WorkingTimeAgreement wta : newWtas) {

            CTAWTAResponseDTO ctaWTAResponseDTO = new CTAWTAResponseDTO(wta.getId(), wta.getName(), wta.getUnitPositionId(), ctaMap.get(wta.getUnitPositionId()).getId(),
                    ctaMap.get(wta.getUnitPositionId()).getName());
            ctaWtas.add(ctaWTAResponseDTO);

        }

        return ctaWtas;
    }

    public CTAWTAAndAccumulatedTimebankWrapper assignCTAWTAToUnitPosition(Long unitPositionId, BigInteger wtaId, BigInteger oldwtaId, BigInteger ctaId, BigInteger oldctaId, LocalDate startDate) {
        CTAWTAAndAccumulatedTimebankWrapper ctawtaAndAccumulatedTimebankWrapper = new CTAWTAAndAccumulatedTimebankWrapper();
        if (wtaId != null) {
            WTAResponseDTO wtaResponseDTO = assignWTATOUnitPosition(unitPositionId, wtaId, startDate);
            ctawtaAndAccumulatedTimebankWrapper.setWta(Arrays.asList(wtaResponseDTO));
            wtaRepository.disableOldWta(oldwtaId, startDate.minusDays(1));
        }
        if (ctaId != null) {
            CTAResponseDTO ctaResponseDTO = costTimeAgreementService.assignCTATOUnitPosition(unitPositionId, ctaId, startDate);
            ctawtaAndAccumulatedTimebankWrapper.setCta(Arrays.asList(ctaResponseDTO));
            costTimeAgreementRepository.disableOldCta(oldctaId, startDate.minusDays(1));
        }
        return ctawtaAndAccumulatedTimebankWrapper;

    }

    public boolean setEndCTAWTAOfUnitPosition(Long unitPositionId, LocalDate endDate) {
        wtaRepository.setEndDateToWTAOfUnitPosition(unitPositionId, endDate);
        costTimeAgreementRepository.setEndDateToCTAOfUnitPosition(unitPositionId, endDate);
        return true;
    }

    private Map<String, BigInteger> getActivityMapWithUnitId(List<WTABaseRuleTemplateDTO> wtaBaseRuleTemplateDTOS, List<OrganizationBasicDTO> organizations) {
        Set<BigInteger> activityIds = new HashSet<>();
        for (WTABaseRuleTemplateDTO ruleTemplate : wtaBaseRuleTemplateDTOS) {
            switch (ruleTemplate.getWtaTemplateType()) {
                case VETO_AND_STOP_BRICKS:
                    VetoAndStopBricksWTATemplate vetoAndStopBricksWTATemplate = ObjectMapperUtils.copyPropertiesByMapper(ruleTemplate, VetoAndStopBricksWTATemplate.class);
                    CollectionUtils.addIgnoreNull(activityIds, vetoAndStopBricksWTATemplate.getStopBrickActivityId());
                    CollectionUtils.addIgnoreNull(activityIds, vetoAndStopBricksWTATemplate.getVetoActivityId());
                    break;
                case SENIOR_DAYS_PER_YEAR:
                    SeniorDaysPerYearWTATemplate seniorDaysPerYearWTATemplate = ObjectMapperUtils.copyPropertiesByMapper(ruleTemplate, SeniorDaysPerYearWTATemplate.class);
                    activityIds.addAll(seniorDaysPerYearWTATemplate.getActivityIds());
                    break;
                case CHILD_CARE_DAYS_CHECK:
                    ChildCareDaysCheckWTATemplate childCareDaysCheckWTATemplate = ObjectMapperUtils.copyPropertiesByMapper(ruleTemplate, ChildCareDaysCheckWTATemplate.class);
                    activityIds.addAll(childCareDaysCheckWTATemplate.getActivityIds());
                    break;
                case WTA_FOR_CARE_DAYS:
                    WTAForCareDays wtaForCareDays = ObjectMapperUtils.copyPropertiesByMapper(ruleTemplate, WTAForCareDays.class);
                    activityIds.addAll(wtaForCareDays.getCareDayCounts().stream().map(activityCareDayCount -> activityCareDayCount.getActivityId()).collect(Collectors.toSet()));
                    break;
            }
        }
        List<Long> organisationIds = organizations.stream().map(organizationBasicDTO -> organizationBasicDTO.getId()).collect(Collectors.toList());
        List<Activity> activities = activityMongoRepository.findAllActivitiesByUnitIds(organisationIds, activityIds);
        Map<String, BigInteger> activitiesIdsAndUnitIdsMap = activities.stream().collect(toMap(k -> k.getParentId() + "-" + k.getUnitId(), v -> v.getId()));
        return activitiesIdsAndUnitIdsMap;
    }

    private WTAResponseDTO updateWTAOfPublishedUnitPosition(WorkingTimeAgreement oldWta, WTADTO wtadto) {
        if(!wtadto.getStartDate().equals(oldWta.getStartDate())){
            boolean wtaExists = wtaRepository.wtaExistsByUnitPositionIdAndDatesAndNotEqualToId(oldWta.getId(),oldWta.getUnitPositionId(),asDate(wtadto.getStartDate()),isNotNull(wtadto.getEndDate()) ? asDate(wtadto.getEndDate()): null);
            if(wtaExists){
                exceptionService.duplicateDataException("error.wta.invalid",wtadto.getStartDate(),isNotNull(wtadto.getEndDate()) ? wtadto.getEndDate() : "");
            }
        }
        List<WTABaseRuleTemplate> wtaBaseRuleTemplates = null;
        WTAResponseDTO wtaResponseDTO;
        if (isCollectionNotEmpty(wtadto.getRuleTemplates())) {
            wtaBaseRuleTemplates = wtaBuilderService.copyRuleTemplates(wtadto.getRuleTemplates(), false);
        }
        boolean isCalculatedValueChanged = isCalCulatedValueChangedForWTA(oldWta, wtaBaseRuleTemplates);
        if (wtadto.getStartDate().isBefore(oldWta.getStartDate())|| wtadto.getStartDate().equals(oldWta.getStartDate()) || !isCalculatedValueChanged) {
            updateWTAOfUnpublishedUnitPosition(oldWta, wtadto);
            oldWta.setStartDate(wtadto.getStartDate());
            wtaRepository.save(oldWta);
            wtaResponseDTO = ObjectMapperUtils.copyPropertiesByMapper(oldWta, WTAResponseDTO.class);
        } else {
            WorkingTimeAgreement newWta = ObjectMapperUtils.copyPropertiesByMapper(oldWta, WorkingTimeAgreement.class);
            newWta.setDescription(wtadto.getDescription());
            newWta.setName(wtadto.getName());
            newWta.setOrganizationParentId(oldWta.getOrganizationParentId());
            newWta.setStartDate(wtadto.getStartDate());
            newWta.setEndDate(wtadto.getEndDate() != null ? wtadto.getEndDate() : null);
            newWta.setRuleTemplateIds(null);
            oldWta.setDisabled(true);
            if(oldWta.getStartDate().isBefore(wtadto.getStartDate()) || (isNotNull(oldWta.getEndDate()) && oldWta.getEndDate().equals(wtadto.getEndDate()))) {
                oldWta.setEndDate(wtadto.getStartDate().minusDays(1));
            }
            oldWta.setId(null);
            if (isCollectionNotEmpty(wtadto.getRuleTemplates())) {
                wtaBaseRuleTemplates = wtaBuilderService.copyRuleTemplates(wtadto.getRuleTemplates(), true);
                save(wtaBaseRuleTemplates);
                List<BigInteger> ruleTemplatesIds = wtaBaseRuleTemplates.stream().map(ruleTemplate -> ruleTemplate.getId()).collect(Collectors.toList());
                newWta.setRuleTemplateIds(ruleTemplatesIds);
            }
            oldWta.setDisabled(true);
            wtaRepository.save(oldWta);
            newWta.setParentId(oldWta.getId());
            save(newWta);
            wtaResponseDTO = ObjectMapperUtils.copyPropertiesByMapper(newWta, WTAResponseDTO.class);
            WTAResponseDTO version = ObjectMapperUtils.copyPropertiesByMapper(oldWta, WTAResponseDTO.class);
            wtaResponseDTO.setParentId(oldWta.getId());
            List<WTABaseRuleTemplate> existingWtaBaseRuleTemplates = wtaBaseRuleTemplateGraphRepository.findAllByIdInAndDeletedFalse(oldWta.getRuleTemplateIds());
            version.setRuleTemplates(WTABuilderService.copyRuleTemplatesToDTO(existingWtaBaseRuleTemplates));
            wtaResponseDTO.setVersions(Collections.singletonList(version));
        }
        wtaResponseDTO.setRuleTemplates(WTABuilderService.copyRuleTemplatesToDTO(wtaBaseRuleTemplates));
        return wtaResponseDTO;
    }

    private boolean isCalCulatedValueChangedForWTA(WorkingTimeAgreement oldWorkingTimeAgreement, List<WTABaseRuleTemplate> wtaBaseRuleTemplates) {
        boolean isCalculatedValueChanged = false;
        if (oldWorkingTimeAgreement.getRuleTemplateIds().size() == wtaBaseRuleTemplates.size()) {
            List<WTABaseRuleTemplate> existingWtaBaseRuleTemplates = wtaBaseRuleTemplateGraphRepository.findAllByIdInAndDeletedFalse(oldWorkingTimeAgreement.getRuleTemplateIds());
            Map<BigInteger, WTABaseRuleTemplate> existingWtaBaseRuleTemplateMap = existingWtaBaseRuleTemplates.stream().collect(toMap(k -> k.getId(), v -> v));
            for (WTABaseRuleTemplate wtaBaseRuleTemplate : wtaBaseRuleTemplates) {
                if (existingWtaBaseRuleTemplateMap.containsKey(wtaBaseRuleTemplate.getId())) {
                    WTABaseRuleTemplate existingWtaBaseRuleTemplate = existingWtaBaseRuleTemplateMap.get(wtaBaseRuleTemplate.getId());
                    isCalculatedValueChanged = existingWtaBaseRuleTemplate.isCalculatedValueChanged(wtaBaseRuleTemplate);
                } else {
                    isCalculatedValueChanged = true;
                }
                if (isCalculatedValueChanged) {
                    break;
                }
            }
        } else {
            isCalculatedValueChanged = true;
        }
        return isCalculatedValueChanged;
    }

    public CTAWTAAndAccumulatedTimebankWrapper getUnitpositionCtaWtaAndAccumulatedTimebank(Long unitId,Map<Long, List<UnitPositionLinesDTO>> positionLinesMap){
        CTAWTAAndAccumulatedTimebankWrapper ctawtaAndAccumulatedTimebankWrapper = getWTACTAByUpIds(positionLinesMap.keySet());
        return ctawtaAndAccumulatedTimebankWrapper;
    }

}
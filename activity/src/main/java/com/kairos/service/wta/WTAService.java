package com.kairos.service.wta;

import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.activity.ActivityDTO;
import com.kairos.dto.activity.activity.TableConfiguration;
import com.kairos.dto.activity.cta.CTAResponseDTO;
import com.kairos.dto.activity.cta.CTAWTAWrapper;
import com.kairos.dto.activity.time_type.TimeTypeDTO;
import com.kairos.dto.activity.wta.CTAWTAResponseDTO;
import com.kairos.dto.activity.wta.basic_details.*;
import com.kairos.dto.activity.wta.version.WTATableSettingWrapper;
import com.kairos.dto.user.employment.UnitPositionIdDTO;
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
import com.kairos.rest_client.GenericIntegrationService;
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
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.constants.AppConstants.COPY_OF;
import static com.kairos.persistence.model.constants.TableSettingConstants.ORGANIZATION_AGREEMENT_VERSION_TABLE_ID;


/**
 * Created by pawanmandhan on 2/8/17.
 */

@Transactional
@Service
public class WTAService extends MongoBaseService {
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
    private GenericIntegrationService genericIntegrationService;


    private final Logger logger = LoggerFactory.getLogger(WTAService.class);

    /**
     * @param referenceId refrence id may be countryid or unitId
     * @param wtaDTO
     * @return
     * @Author Vipul
     */
    public WTAResponseDTO createWta(long referenceId, WTADTO wtaDTO, boolean creatingFromCountry, boolean mapWithOrgType) {

        //TODO  API functionality has been changed for now KP-958
        //  checkUniquenessOfData(countryId, wtaDTO.getOrganizationSubType(), wtaDTO.getOrganizationType(), wtaDTO.getSkillId());
        ;
        if (creatingFromCountry) {
            boolean alreadyExists = mapWithOrgType ? wtaRepository.isWTAExistWithSameOrgTypeAndSubType(wtaDTO.getOrganizationType(), wtaDTO.getOrganizationSubType(), wtaDTO.getName()) : wtaRepository.getWtaByName(wtaDTO.getName(), referenceId);
            if (alreadyExists) {
                exceptionService.duplicateDataException("message.wta.name.duplicate", wtaDTO.getName());
            }

        } else if (wtaRepository.isWTAExistByOrganizationIdAndName(referenceId, wtaDTO.getName())) {
            exceptionService.duplicateDataException("message.wta.name.duplicate", wtaDTO.getName());
        }

        WorkingTimeAgreement wta = new WorkingTimeAgreement();
        // Link tags to WTA
        Date startDate = (wtaDTO.getStartDateMillis() == 0) ? DateUtils.getCurrentDate() : new Date(wtaDTO.getStartDateMillis());
        startDate = DateUtils.getDateByZoneDateTime(DateUtils.asZoneDateTime(startDate).truncatedTo(ChronoUnit.DAYS));
        if (wtaDTO.getEndDateMillis() != null && wtaDTO.getEndDateMillis() > 0) {
            if (startDate.getTime() > wtaDTO.getEndDateMillis()) {
                exceptionService.invalidRequestException("message.wta.start-end-date");
            }
            Date endDate = new Date(wtaDTO.getEndDateMillis());
            endDate = DateUtils.getDateByZoneDateTime(DateUtils.asZoneDateTime(endDate).truncatedTo(ChronoUnit.DAYS));
            wta.setEndDate(endDate);
        }
        WTABasicDetailsDTO wtaBasicDetailsDTO = genericIntegrationService.getWtaRelatedInfo(wtaDTO.getExpertiseId(), wtaDTO.getOrganizationSubType(), referenceId, 0l, wtaDTO.getOrganizationType(), wtaDTO.getUnitIds());
        if (creatingFromCountry) {
            if (!Optional.ofNullable(wtaBasicDetailsDTO.getCountryDTO()).isPresent()) {
                exceptionService.dataNotFoundByIdException("message.country.id", referenceId);
            }
            wta.setCountryId(referenceId);
        }
        wta.setStartDate(startDate);
        if (CollectionUtils.isNotEmpty(wtaDTO.getTags())) {
            List<Tag> tags = tagService.getCountryTagsByIdsAndMasterDataType(wtaDTO.getTags(), MasterDataTypeEnum.WTA);
            wta.setTags(tags.stream().map(t -> t.getId()).collect(Collectors.toList()));
        }

        prepareWtaWhileCreate(wta, wtaDTO, wtaBasicDetailsDTO);
        List<WTABaseRuleTemplate> ruleTemplates = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(wtaDTO.getRuleTemplates())) {
            ruleTemplates = wtaBuilderService.copyRuleTemplates(wtaDTO.getRuleTemplates(), true);
            save(ruleTemplates);
            List<BigInteger> ruleTemplatesIds = ruleTemplates.stream().map(ruleTemplate -> ruleTemplate.getId()).collect(Collectors.toList());
            wta.setRuleTemplateIds(ruleTemplatesIds);

        }
        WTAResponseDTO wtaResponseDTO = ObjectMapperUtils.copyPropertiesByMapper(wta, WTAResponseDTO.class);
        //wtaResponseDTO.setRuleTemplateIds(wtaBuilderService.getRuleTemplateDTO(wta));
        wtaResponseDTO.setStartDateMillis(wta.getStartDate().getTime());
        if (wta.getEndDate() != null) {
            wtaResponseDTO.setEndDateMillis(wta.getEndDate().getTime());
        }
        wtaResponseDTO.setRuleTemplates(WTABuilderService.copyRuleTemplatesToDTO(ruleTemplates));
        if(creatingFromCountry) {
            save(wta);
            wtaResponseDTO.setId(wta.getId());
        }

        Map<Long,WTAResponseDTO> wtaResponseDTOMap = assignWTAToNewOrganization(wta, wtaDTO, wtaBasicDetailsDTO, creatingFromCountry);

        if(!creatingFromCountry) {
            wtaResponseDTO = wtaResponseDTOMap.get(referenceId);
        }

        // Adding this wta to all organization type


        // setting basic details

        return wtaResponseDTO;
    }


    private Map<Long,WTAResponseDTO> assignWTAToNewOrganization(WorkingTimeAgreement wta, WTADTO wtadto, WTABasicDetailsDTO wtaBasicDetailsDTO, boolean creatingFromCountry) {
        List<WorkingTimeAgreement> workingTimeAgreements = new ArrayList<>(wtaBasicDetailsDTO.getOrganizations().size());
        List<Long> organizationIds = wtaBasicDetailsDTO.getOrganizations().stream().map(o -> o.getId()).collect(Collectors.toList());
        List<WorkingTimeAgreement> workingTimeAgreementList = wtaRepository.findWTAByUnitIdsAndName(organizationIds, wtadto.getName());
        Map<String, WorkingTimeAgreement> workingTimeAgreementMap = workingTimeAgreementList.stream().collect(Collectors.toMap(k -> k.getName() + "_" + k.getOrganization().getId() + "_" + k.getOrganizationType().getId(), v -> v));
        Map<String, BigInteger> activitiesIdsAndUnitIdsMap = getActivityMapWithUnitId(wtadto.getRuleTemplates(), wtaBasicDetailsDTO.getOrganizations());
        Map<Long,WTAResponseDTO> wtaResponseDTOMap = new HashMap<>();
        List<WTAResponseDTO> wtaResponseDTOS = new ArrayList<WTAResponseDTO>();
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

                workingTimeAgreement.setOrganization(new Organization(organization.getId(), organization.getName(), organization.getDescription()));
                workingTimeAgreement.setOrganizationType(new OrganizationType(wtaBasicDetailsDTO.getOrganizationType().getId(), wtaBasicDetailsDTO.getOrganizationType().getName(), wtaBasicDetailsDTO.getOrganizationType().getDescription()));
                workingTimeAgreement.setOrganizationSubType(new OrganizationType(wtaBasicDetailsDTO.getOrganizationSubType().getId(), wtaBasicDetailsDTO.getOrganizationSubType().getName(), wtaBasicDetailsDTO.getOrganizationSubType().getDescription()));
                WTAResponseDTO wtaResponseDTO = ObjectMapperUtils.copyPropertiesByMapper(workingTimeAgreement, WTAResponseDTO.class);
                wtaResponseDTOMap.put(organization.getId(),wtaResponseDTO);


                workingTimeAgreements.add(workingTimeAgreement);
            }
        });
        if (!workingTimeAgreements.isEmpty()) {
            save(workingTimeAgreements);
            workingTimeAgreements.forEach(workingTimeAgreement ->  {
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

        if (updateDTO.getStartDateMillis() < System.currentTimeMillis()) {
            exceptionService.actionNotPermittedException("message.wta.start-current-date");
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
        WTABasicDetailsDTO wtaBasicDetailsDTO = genericIntegrationService.getWtaRelatedInfo(updateDTO.getExpertiseId(), updateDTO.getOrganizationSubType(), countryId, 0l, updateDTO.getOrganizationType(), Collections.EMPTY_LIST);
        WTAResponseDTO wtaResponseDTO = prepareWtaWhileUpdate(oldWta, updateDTO, wtaBasicDetailsDTO);

        wtaResponseDTO.setStartDateMillis(oldWta.getStartDate().getTime());
        if (oldWta.getEndDate() != null) {
            wtaResponseDTO.setEndDateMillis(oldWta.getEndDate().getTime());
        }

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

        boolean calculativeValueChanged=false;
        boolean sameFutureDateWTA = DateUtils.getLocalDateFromDate(oldWta.getStartDate()).isEqual(updateDTO.getStartDate()) && (updateDTO.getStartDate().isAfter(DateUtils.getCurrentLocalDate()) || updateDTO.getStartDate().isEqual(DateUtils.getCurrentLocalDate()));
        if (!updateDTO.getRuleTemplates().isEmpty()) {
            ruleTemplates = wtaBuilderService.updateRuleTemplates(updateDTO.getRuleTemplates(), oldWta.getRuleTemplateIds());
            calculativeValueChanged = ruleTemplates.get(0).isCalculativeValueChange();
        }
        if(!sameFutureDateWTA && calculativeValueChanged){ // since calculative values are changed and dates are not same so we need to make a new copy
            WorkingTimeAgreement versionWTA = ObjectMapperUtils.copyPropertiesByMapper(oldWta, WorkingTimeAgreement.class);
            versionWTA.setId(null);
            versionWTA.setDeleted(false);
            versionWTA.setEndDate(new Date(updateDTO.getStartDateMillis()));
            save(versionWTA);
            oldWta.setParentId(versionWTA.getId());
            oldWta.setStartDate(new Date(updateDTO.getStartDateMillis()));
            oldWta.setEndDate(updateDTO.getEndDateMillis() != null?new Date(updateDTO.getEndDateMillis()):null);
            ruleTemplates.forEach(ruleTemplate -> ruleTemplate.setId(null));
        }
        if (!ruleTemplates.isEmpty()) {
            save(ruleTemplates);
        }
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
        oldWta.setRuleTemplateIds(ruleTemplates.stream().map(ruleTemplate -> ruleTemplate.getId()).collect(Collectors.toList()));
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
            wtaResponseDTO.setStartDateMillis(wtaResponseDTO.getStartDate().getTime());
            if (wtaResponseDTO.getEndDate() != null) {
                wtaResponseDTO.setEndDateMillis(wtaResponseDTO.getEndDate().getTime());
            }
            wtaResponseDTOS.add(wtaResponseDTO);
        });

        return wtaResponseDTOS;
    }

    public List<WTAResponseDTO> getAllWTAByOrganizationSubType(long organizationSubTypeId, long countryId) {
        List<WTAQueryResultDTO> wtaQueryResultDTOS = wtaRepository.getAllWTAByOrganizationSubTypeIdAndCountryId(organizationSubTypeId, countryId);
        List<WTAResponseDTO> wtaResponseDTOS = new ArrayList<>();
        wtaQueryResultDTOS.forEach(wta -> {
            wtaResponseDTOS.add(ObjectMapperUtils.copyPropertiesByMapper(wta, WTAResponseDTO.class));
        });
        return wtaResponseDTOS;
    }

    public List<WTAResponseDTO> getAllWTAWithOrganization(long countryId) {
        /*List<Map<String, Object>> map =
        List<Object> objectList = new ArrayList<>();
        for (Map<String, Object> result : map) {
            objectList.add(result.get("result"));
        }*/
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
        /*List<WTARuleTemplateDTO> wtaRuleTemplateQueryResponseArrayList = new ArrayList<WTARuleTemplateDTO>();*/
        WTABasicDetailsDTO wtaBasicDetailsDTO = genericIntegrationService.getWtaRelatedInfo(0L, organizationSubTypeId, countryId, 0L, 0L, Collections.EMPTY_LIST);
        if (!Optional.ofNullable(wtaBasicDetailsDTO.getOrganizationSubType()).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.organization.subtype.id", organizationSubTypeId);
        }
        WorkingTimeAgreement wta = wtaRepository.findOne(wtaId);
        //TODO need to again activate check
        //checkUniquenessOfData(countryId, organizationSubTypeId, wta.getOrganizationType().getId(), wta.getExpertise().getId());
        if (!Optional.ofNullable(wta).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.wta.id", wtaId);
        }
        if (checked) {
            List<WTABaseRuleTemplateDTO> wtaBaseRuleTemplates = wtaBaseRuleTemplateGraphRepository.findAllByIdIn(wta.getRuleTemplateIds());
            WTADTO wtadto = new WTADTO(COPY_OF + wta.getName(), wta.getDescription(), wta.getExpertise().getId(), wta.getStartDate().getTime(), wta.getEndDate() == null ? null : wta.getEndDate().getTime(), wtaBaseRuleTemplates, wtaBasicDetailsDTO.getOrganizationType().getId(), wtaBasicDetailsDTO.getOrganizationSubType().getId());
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
        WTADefaultDataInfoDTO wtaDefaultDataInfoDTO = genericIntegrationService.getWtaTemplateDefaultDataInfo(countryId);
        wtaDefaultDataInfoDTO.setTimeTypes(timeTypeDTOS);
        wtaDefaultDataInfoDTO.setActivityList(activityDTOS);
        return wtaDefaultDataInfoDTO;
    }

    public WTADefaultDataInfoDTO getDefaultWtaInfoForUnit(Long unitId) {
        WTADefaultDataInfoDTO wtaDefaultDataInfoDTO = genericIntegrationService.getWtaTemplateDefaultDataInfoByUnitId();
        List<ActivityDTO> activities = activityMongoRepository.findByDeletedFalseAndUnitId(unitId);
        List<TimeTypeDTO> timeTypeDTOS = timeTypeService.getAllTimeType(null, wtaDefaultDataInfoDTO.getCountryID());
        wtaDefaultDataInfoDTO.setTimeTypes(timeTypeDTOS);
        wtaDefaultDataInfoDTO.setActivityList(activities);
        return wtaDefaultDataInfoDTO;
    }

    public CTAWTAWrapper getWTACTAByUpIds(List<Long> unitPositionIds) {
        List<WTAQueryResultDTO> wtaQueryResultDTOS = wtaRepository.getAllWTAByUpIds(unitPositionIds, new Date());
        List<WTAResponseDTO> wtaResponseDTOS = ObjectMapperUtils.copyPropertiesOfListByMapper(wtaQueryResultDTOS, WTAResponseDTO.class);
        List<CTAResponseDTO> ctaResponseDTOS = costTimeAgreementService.getCTAByUpIds(unitPositionIds);
        return new CTAWTAWrapper(ctaResponseDTOS, wtaResponseDTOS);
    }

    public CTAWTAWrapper getWTACTAByOfUnitPosition(Long unitPositionId, LocalDate startDate) {
        WorkingTimeAgreement wta = wtaRepository.getWTABasicByUnitPositionAndDate(unitPositionId, DateUtils.asDate(startDate));
        CostTimeAgreement cta = costTimeAgreementRepository.getCTABasicByUnitPositionAndDate(unitPositionId, DateUtils.asDate(startDate));
        CTAWTAWrapper ctawtaWrapper = new CTAWTAWrapper();
        if (Optional.ofNullable(wta).isPresent()) {
            WTAResponseDTO wtaResponseDTO = new WTAResponseDTO(wta.getName(), wta.getId(), wta.getParentId());
            ctawtaWrapper.setWta(Collections.singletonList(wtaResponseDTO));
        }
        if (Optional.ofNullable(cta).isPresent()) {
            CTAResponseDTO ctaResponseDTO = new CTAResponseDTO(cta.getName(), cta.getId(), cta.getParentId());
            ctawtaWrapper.setCta(Collections.singletonList(ctaResponseDTO));
        }
        return ctawtaWrapper;
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


    public CTAWTAWrapper assignCTAWTAToUnitPosition(Long unitPositionId, BigInteger wtaId, BigInteger ctaId, LocalDate startDate) {
        CTAWTAWrapper ctawtaWrapper = new CTAWTAWrapper();
        if (wtaId != null) {
            WTAResponseDTO wtaResponseDTO = assignWTATOUnitPosition(unitPositionId, wtaId, startDate);
            ctawtaWrapper.setWta(Arrays.asList(wtaResponseDTO));
        }
        if (ctaId != null) {
            CTAResponseDTO ctaResponseDTO = costTimeAgreementService.assignCTATOUnitPosition(unitPositionId, ctaId, startDate);
            ctawtaWrapper.setCta(Arrays.asList(ctaResponseDTO));
        }
        return ctawtaWrapper;

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
        Date startDate = startLocalDate != null ? DateUtils.asDate(startLocalDate) : DateUtils.getDateByZoneDateTime(DateUtils.asZoneDateTime(wtaQueryResultDTO.getStartDate()).truncatedTo(ChronoUnit.DAYS));
        if (wtaQueryResultDTO.getEndDate() != null) {
            Date endDate = DateUtils.getDateByZoneDateTime(DateUtils.asZoneDateTime(wtaQueryResultDTO.getEndDate()).truncatedTo(ChronoUnit.DAYS));
            workingTimeAgreement.setEndDate(endDate);
        }
        workingTimeAgreement.setStartDate(startDate);
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
            workingTimeAgreement.setOrganization(new Organization(organisationId, "", ""));
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
        if (!oldWta.isPresent()) {
            logger.info("wta not found while updating at unit %d", wtadto.getId());
            exceptionService.dataNotFoundByIdException("message.wta.id", wtadto.getId());
        }
        WTAResponseDTO wtaResponseDTO;
        if (oldUnitPositionPublished) {
            wtaResponseDTO = updateWTAOfPublishedUnitPosition(oldWta.get(), wtadto);
        } else {
            wtaResponseDTO = updateWTAOfUnpublishedUnitPosition(oldWta.get(), wtadto);
        }
        wtaResponseDTO.setStartDateMillis(oldWta.get().getStartDate().getTime());
        if (oldWta.get().getEndDate() != null) {
            wtaResponseDTO.setEndDateMillis(oldWta.get().getEndDate().getTime());
        }
        return wtaResponseDTO;
    }

    private WTAResponseDTO updateWTAOfUnpublishedUnitPosition(WorkingTimeAgreement oldWta, WTADTO updateDTO) {
        oldWta.setDescription(updateDTO.getDescription());
        oldWta.setName(updateDTO.getName());
        List<WTABaseRuleTemplate> ruleTemplates = new ArrayList<>();
        if (updateDTO.getRuleTemplates().size() > 0) {
            ruleTemplates = wtaBuilderService.copyRuleTemplates(updateDTO.getRuleTemplates(), false);
            save(ruleTemplates);
            List<BigInteger> ruleTemplatesIds = ruleTemplates.stream().map(ruleTemplate -> ruleTemplate.getId()).collect(Collectors.toList());
            oldWta.setRuleTemplateIds(ruleTemplatesIds);
        }
        oldWta.setEndDate(DateUtils.asDate(updateDTO.getEndDate()));
        save(oldWta);
        WTAResponseDTO wtaResponseDTO = ObjectMapperUtils.copyPropertiesByMapper(oldWta, WTAResponseDTO.class);
        wtaResponseDTO.setRuleTemplates(WTABuilderService.copyRuleTemplatesToDTO(ruleTemplates));
        wtaResponseDTO.setStartDateMillis(oldWta.getStartDate().getTime());
        if (oldWta.getEndDate() != null) {
            wtaResponseDTO.setEndDateMillis(oldWta.getEndDate().getTime());
        }
        return wtaResponseDTO;
    }


    private WTAResponseDTO updateWTAOfPublishedUnitPosition(WorkingTimeAgreement oldWta, WTADTO updateDTO) {
        List<WTABaseRuleTemplate> ruleTemplates = new ArrayList<>();
        boolean sameFutureDateWTA = DateUtils.getLocalDateFromDate(oldWta.getStartDate()).isEqual(updateDTO.getStartDate()) && (updateDTO.getStartDate().isAfter(DateUtils.getCurrentLocalDate()) || updateDTO.getStartDate().isEqual(DateUtils.getCurrentLocalDate()));
        boolean calculativeValueChanged=false;
        if (!updateDTO.getRuleTemplates().isEmpty()) {
            ruleTemplates = wtaBuilderService.updateRuleTemplates(updateDTO.getRuleTemplates(), oldWta.getRuleTemplateIds());
            calculativeValueChanged = ruleTemplates.get(0).isCalculativeValueChange();
        }
        if (!sameFutureDateWTA && calculativeValueChanged) { // since calculative values are changed and dates are not same so we need to make a new copy
            WorkingTimeAgreement versionWTA = ObjectMapperUtils.copyPropertiesByMapper(oldWta, WorkingTimeAgreement.class);
            versionWTA.setId(null);
            versionWTA.setDeleted(false);
            versionWTA.setStartDate(oldWta.getStartDate());
            versionWTA.setEndDate(DateUtils.asDate(updateDTO.getStartDate().minusDays(1)));
            versionWTA.setCountryParentWTA(null);
            versionWTA.setOrganizationParentId(oldWta.getOrganizationParentId());
            save(versionWTA);
            oldWta.setParentId(versionWTA.getId());
            oldWta.setStartDate(DateUtils.asDate(updateDTO.getStartDate()));
            oldWta.setEndDate(updateDTO.getEndDate() != null?DateUtils.asDate(updateDTO.getEndDate()):null);
            ruleTemplates.forEach(ruleTemplate -> ruleTemplate.setId(null));
        }
        if (!ruleTemplates.isEmpty()) {
            save(ruleTemplates);
        }
        oldWta.setDescription(updateDTO.getDescription());
        oldWta.setName(updateDTO.getName());
        oldWta.setRuleTemplateIds(ruleTemplates.stream().map(ruleTemplate -> ruleTemplate.getId()).collect(Collectors.toList()));
        save(oldWta);
        WTAResponseDTO wtaResponseDTO = ObjectMapperUtils.copyPropertiesByMapper(oldWta, WTAResponseDTO.class);
        wtaResponseDTO.setRuleTemplates(WTABuilderService.copyRuleTemplatesToDTO(ruleTemplates));
        return wtaResponseDTO;
    }


    public List<CTAWTAResponseDTO> copyWtaCTA(List<UnitPositionIdDTO> unitPositionIDs) {

        // List<CTAWTADTO> ctaWtas = wtaRepository.getCTAWTAByUnitPositionId(oldUnitPositionId);

        logger.info("Inside wtaservice");
        List<Long> oldUnitPositionIds = unitPositionIDs.stream().map(unitPositionIdDTO -> unitPositionIdDTO.getOldUnitPositionID()).collect(Collectors.toList());
        Map<Long, Long> newOldunitPositionIdMap = unitPositionIDs.stream().collect(Collectors.toMap(k -> k.getOldUnitPositionID(), v -> v.getNewUnitPositionID()));
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

        Map<Long, CostTimeAgreement> ctaMap = newCTAs.stream().collect(Collectors.toMap(k -> k.getUnitPositionId(), v -> v));
        List<CTAWTAResponseDTO> ctaWtas = new ArrayList<>();
        for (WorkingTimeAgreement wta : newWtas) {

            CTAWTAResponseDTO ctaWTAResponseDTO = new CTAWTAResponseDTO(wta.getId(), wta.getName(), wta.getUnitPositionId(), ctaMap.get(wta.getUnitPositionId()).getId(),
                    ctaMap.get(wta.getUnitPositionId()).getName());
            ctaWtas.add(ctaWTAResponseDTO);

        }

        return ctaWtas;
    }

    public CTAWTAWrapper assignCTAWTAToUnitPosition(Long unitPositionId, BigInteger wtaId, BigInteger oldwtaId, BigInteger ctaId, BigInteger oldctaId, LocalDate startDate) {
        CTAWTAWrapper ctawtaWrapper = new CTAWTAWrapper();
        if (wtaId != null) {
            WTAResponseDTO wtaResponseDTO = assignWTATOUnitPosition(unitPositionId, wtaId, startDate);
            ctawtaWrapper.setWta(Arrays.asList(wtaResponseDTO));
            wtaRepository.disableOldWta(oldwtaId, startDate.minusDays(1));
        }
        if (ctaId != null) {
            CTAResponseDTO ctaResponseDTO = costTimeAgreementService.assignCTATOUnitPosition(unitPositionId, ctaId, startDate);
            ctawtaWrapper.setCta(Arrays.asList(ctaResponseDTO));
            costTimeAgreementRepository.disableOldCta(oldctaId, startDate.minusDays(1));
        }
        return ctawtaWrapper;

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
        Map<String, BigInteger> activitiesIdsAndUnitIdsMap = activities.stream().collect(Collectors.toMap(k -> k.getParentId() + "-" + k.getUnitId(), v -> v.getId()));
        return activitiesIdsAndUnitIdsMap;
    }

}
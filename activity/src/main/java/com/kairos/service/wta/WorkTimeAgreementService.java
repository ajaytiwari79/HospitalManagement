package com.kairos.service.wta;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.activity.ActivityDTO;
import com.kairos.dto.activity.activity.TableConfiguration;
import com.kairos.dto.activity.cta.CTAResponseDTO;
import com.kairos.dto.activity.cta.CTAWTAAndAccumulatedTimebankWrapper;
import com.kairos.dto.activity.shift.ShiftWithActivityDTO;
import com.kairos.dto.activity.shift.StaffEmploymentDetails;
import com.kairos.dto.activity.tags.TagDTO;
import com.kairos.dto.activity.time_type.TimeTypeDTO;
import com.kairos.dto.activity.wta.CTAWTAResponseDTO;
import com.kairos.dto.activity.wta.IntervalBalance;
import com.kairos.dto.activity.wta.WorkTimeAgreementBalance;
import com.kairos.dto.activity.wta.WorkTimeAgreementRuleTemplateBalancesDTO;
import com.kairos.dto.activity.wta.basic_details.*;
import com.kairos.dto.activity.wta.templates.PhaseTemplateValue;
import com.kairos.dto.activity.wta.version.WTATableSettingWrapper;
import com.kairos.dto.user.employment.EmploymentIdDTO;
import com.kairos.dto.user.employment.EmploymentLinesDTO;
import com.kairos.dto.user.organization.OrganizationDTO;
import com.kairos.dto.user.user.staff.StaffAdditionalInfoDTO;
import com.kairos.enums.phase.PhaseDefaultName;
import com.kairos.enums.wta.WTATemplateType;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.activity.ActivityWrapper;
import com.kairos.persistence.model.cta.CTARuleTemplate;
import com.kairos.persistence.model.cta.CostTimeAgreement;
import com.kairos.persistence.model.period.PlanningPeriod;
import com.kairos.persistence.model.phase.Phase;
import com.kairos.persistence.model.wta.*;
import com.kairos.persistence.model.wta.templates.WTABaseRuleTemplate;
import com.kairos.persistence.model.wta.templates.template_types.*;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.cta.CostTimeAgreementRepository;
import com.kairos.persistence.repository.period.PlanningPeriodMongoRepository;
import com.kairos.persistence.repository.phase.PhaseMongoRepository;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.persistence.repository.tag.TagMongoRepository;
import com.kairos.persistence.repository.wta.WorkingTimeAgreementMongoRepository;
import com.kairos.persistence.repository.wta.rule_template.RuleTemplateCategoryRepository;
import com.kairos.persistence.repository.wta.rule_template.WTABaseRuleTemplateMongoRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.MongoBaseService;
import com.kairos.service.activity.TimeTypeService;
import com.kairos.service.cta.CostTimeAgreementService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.integration.PlannerSyncService;
import com.kairos.service.table_settings.TableSettingService;
import com.kairos.service.tag.TagService;
import com.kairos.service.time_bank.TimeBankService;
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
import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.constants.ActivityMessagesConstants.*;
import static com.kairos.constants.AppConstants.COPY_OF;
import static com.kairos.persistence.model.constants.TableSettingConstants.ORGANIZATION_AGREEMENT_VERSION_TABLE_ID;
import static java.util.stream.Collectors.toMap;


/**
 * Created by pawanmandhan on 2/8/17.
 */

@Transactional
@Service
public class WorkTimeAgreementService extends MongoBaseService {
    private static final Logger logger = LoggerFactory.getLogger(WorkTimeAgreementService.class);
    @Inject
    private WorkingTimeAgreementMongoRepository wtaRepository;
    @Inject
    private RuleTemplateCategoryRepository ruleTemplateCategoryRepository;
    @Inject
    private WTABaseRuleTemplateMongoRepository wtaBaseRuleTemplateRepository;
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
    @Inject
    private TimeBankService timeBankService;
    @Inject
    private RuleTemplateCategoryRepository ruleTemplateCategoryMongoRepository;
    @Inject
    private PhaseMongoRepository phaseMongoRepository;
    @Inject
    private TagMongoRepository tagMongoRepository;
    @Inject
    private PlanningPeriodMongoRepository planningPeriodMongoRepository;
    @Inject
    private ShiftMongoRepository shiftMongoRepository;
    @Inject
    private WorkTimeAgreementBalancesCalculationService workTimeAgreementBalancesCalculationService;


    public WTAResponseDTO createWta(long referenceId, WTADTO wtaDTO, boolean creatingFromCountry, boolean mapWithOrgType) {
        if (creatingFromCountry) {
            boolean alreadyExists = mapWithOrgType ? wtaRepository.isWTAExistWithSameOrgTypeAndSubType(wtaDTO.getOrganizationType(), wtaDTO.getOrganizationSubType(), wtaDTO.getName()) : wtaRepository.getWtaByName(wtaDTO.getName(), referenceId);
            if (alreadyExists) {
                exceptionService.duplicateDataException(MESSAGE_WTA_NAME_DUPLICATE, wtaDTO.getName());
            }

        } else if (wtaRepository.isWTAExistByOrganizationIdAndName(referenceId, wtaDTO.getName())) {
            exceptionService.duplicateDataException(MESSAGE_WTA_NAME_DUPLICATE, wtaDTO.getName());
        }

        WorkingTimeAgreement wta = new WorkingTimeAgreement();
        if (isNotNull(wtaDTO.getEndDate())) {
            if (wtaDTO.getStartDate().isAfter(wtaDTO.getEndDate())) {
                exceptionService.invalidRequestException(MESSAGE_WTA_START_ENDDATE);
            }
            wta.setEndDate(wtaDTO.getEndDate());
        }
        WTABasicDetailsDTO wtaBasicDetailsDTO = userIntegrationService.getWtaRelatedInfo(wtaDTO.getExpertiseId(), wtaDTO.getOrganizationSubType(), referenceId, null, wtaDTO.getOrganizationType(), wtaDTO.getUnitIds());
        if (creatingFromCountry) {
            if (!Optional.ofNullable(wtaBasicDetailsDTO.getCountryDTO()).isPresent()) {
                exceptionService.dataNotFoundByIdException(MESSAGE_COUNTRY_ID, referenceId);
            }
            wta.setCountryId(referenceId);
        }
        wta.setStartDate(wtaDTO.getStartDate());
        if (isCollectionNotEmpty(wtaDTO.getTags())) {
            wta.setTags(wtaDTO.getTags());
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
        Map<Long, WTAResponseDTO> wtaResponseDTOMap = assignWTAToNewOrganization(wta, wtaDTO, wtaBasicDetailsDTO, creatingFromCountry, referenceId);

        if (!creatingFromCountry) {
            wtaResponseDTO = wtaResponseDTOMap.get(referenceId);
        }
        return wtaResponseDTO;
    }


    private Map<Long, WTAResponseDTO> assignWTAToNewOrganization(WorkingTimeAgreement wta, WTADTO wtadto, WTABasicDetailsDTO wtaBasicDetailsDTO, boolean creatingFromCountry, Long referenceId) {
        List<WorkingTimeAgreement> workingTimeAgreements = new ArrayList<>(wtaBasicDetailsDTO.getOrganizations().size());
        List<Long> organizationIds = wtaBasicDetailsDTO.getOrganizations().stream().map(o -> o.getId()).collect(Collectors.toList());
        List<WorkingTimeAgreement> workingTimeAgreementList = wtaRepository.findWTAByUnitIdsAndName(organizationIds, wtadto.getName());
        Map<String, WorkingTimeAgreement> workingTimeAgreementMap = workingTimeAgreementList.stream().collect(toMap(k -> k.getName() + "_" + k.getOrganization().getId() + "_" + k.getOrganizationType().getId(), v -> v));
        Map<String, BigInteger> activitiesIdsAndUnitIdsMap = getActivityMapWithUnitId(wtadto.getRuleTemplates(), organizationIds);
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
                        updateExistingPhaseIdOfWTA(wtaBaseRuleTemplate.getPhaseTemplateValues(), organization.getId(), referenceId, creatingFromCountry);
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
            wtaRepository.saveEntities(workingTimeAgreements);
            workingTimeAgreements.forEach(workingTimeAgreement -> {
                WTAResponseDTO wtaResponseDTO = wtaResponseDTOMap.get(workingTimeAgreement.getOrganization().getId());
                wtaResponseDTO.setId(workingTimeAgreement.getId());
            });
        }

        return wtaResponseDTOMap;
    }


    private WorkingTimeAgreement prepareWtaWhileCreate(WorkingTimeAgreement wta, WTADTO wtaDTO, WTABasicDetailsDTO wtaBasicDetailsDTO) {
        if (!Optional.ofNullable(wtaBasicDetailsDTO.getExpertiseResponse()).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.expertise.id", wtaDTO.getExpertiseId());
        }
        if (!Optional.ofNullable(wtaBasicDetailsDTO.getOrganizationType()).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_ORGANIZATION_TYPE, wtaDTO.getOrganizationType());
        }
        if (!Optional.ofNullable(wtaBasicDetailsDTO.getOrganizationSubType()).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_ORGANIZATION_SUBTYPE, wtaDTO.getOrganizationSubType());
        }
        wta.setDescription(wtaDTO.getDescription());
        wta.setName(wtaDTO.getName());
        wta.setTags(wtaDTO.getTags());
        wta.setExpertise(new Expertise(wtaBasicDetailsDTO.getExpertiseResponse().getId(), wtaBasicDetailsDTO.getExpertiseResponse().getName(), wtaBasicDetailsDTO.getExpertiseResponse().getDescription()));
        wta.setOrganizationType(new OrganizationType(wtaBasicDetailsDTO.getOrganizationType().getId(), wtaBasicDetailsDTO.getOrganizationType().getName(), wtaBasicDetailsDTO.getOrganizationType().getDescription()));
        wta.setOrganizationSubType(new OrganizationType(wtaBasicDetailsDTO.getOrganizationSubType().getId(), wtaBasicDetailsDTO.getOrganizationSubType().getName(), wtaBasicDetailsDTO.getOrganizationSubType().getDescription()));
        return wta;
    }


    public WTAResponseDTO updateWtaOfCountry(Long countryId, BigInteger wtaId, WTADTO updateDTO) {
        if (isNotNull(updateDTO.getEndDate()) && updateDTO.getStartDate().isAfter(updateDTO.getEndDate())) {
            exceptionService.actionNotPermittedException(MESSAGE_WTA_START_ENDDATE);
        }
        WorkingTimeAgreement workingTimeAgreement = wtaRepository.getWtaByNameExcludingCurrent(updateDTO.getName(), countryId, wtaId, updateDTO.getOrganizationType(), updateDTO.getOrganizationSubType());
        if (Optional.ofNullable(workingTimeAgreement).isPresent()) {
            exceptionService.duplicateDataException(MESSAGE_WTA_NAME_DUPLICATE, updateDTO.getName());
        }
        WorkingTimeAgreement oldWta = wtaRepository.getWTAByCountryId(countryId, wtaId);
        if (!Optional.ofNullable(oldWta).isPresent()) {
            logger.info("wta not found while updating at unit %d", wtaId);
            exceptionService.dataNotFoundByIdException(MESSAGE_WTA_ID, wtaId);
        }
        WTABasicDetailsDTO wtaBasicDetailsDTO = userIntegrationService.getWtaRelatedInfo(updateDTO.getExpertiseId(), updateDTO.getOrganizationSubType(), countryId, null, updateDTO.getOrganizationType(), Collections.EMPTY_LIST);
        WTAResponseDTO wtaResponseDTO = prepareWtaWhileUpdate(oldWta, updateDTO, wtaBasicDetailsDTO);
        wtaResponseDTO.setStartDate(oldWta.getStartDate());
        wtaResponseDTO.setEndDate(oldWta.getEndDate());
        return wtaResponseDTO;
    }


    private WTAResponseDTO prepareWtaWhileUpdate(WorkingTimeAgreement oldWta, WTADTO updateDTO, WTABasicDetailsDTO wtaBasicDetailsDTO) {
        if (!oldWta.getOrganizationType().getId().equals(updateDTO.getOrganizationType())) {
            exceptionService.actionNotPermittedException(MESSAGE_ORGANIZATION_TYPE_UPDATE, updateDTO.getOrganizationType());
        }
        if (!oldWta.getOrganizationSubType().getId().equals(updateDTO.getOrganizationSubType())) {
            exceptionService.actionNotPermittedException(MESSAGE_ORGANIZATION_SUBTYPE_UPDATE, updateDTO.getOrganizationSubType());
        }
        List<WTABaseRuleTemplate> ruleTemplates = new ArrayList<>();
        if (isCollectionNotEmpty(updateDTO.getRuleTemplates())) {
            for (WTABaseRuleTemplateDTO ruleTemplateDTO : updateDTO.getRuleTemplates()) {
                ruleTemplates.add(wtaBuilderService.copyRuleTemplate(ruleTemplateDTO, true));
            }
            wtaBaseRuleTemplateRepository.saveEntities(ruleTemplates);
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
        oldWta.setTags(updateDTO.getTags());
        oldWta.setDescription(updateDTO.getDescription());
        oldWta.setName(updateDTO.getName());
        oldWta.setOrganizationType(new OrganizationType(wtaBasicDetailsDTO.getOrganizationType().getId(), wtaBasicDetailsDTO.getOrganizationType().getName(), wtaBasicDetailsDTO.getOrganizationType().getDescription()));
        oldWta.setOrganizationSubType(new OrganizationType(wtaBasicDetailsDTO.getOrganizationSubType().getId(), wtaBasicDetailsDTO.getOrganizationSubType().getName(), wtaBasicDetailsDTO.getOrganizationSubType().getDescription()));
        save(oldWta);
        List<TagDTO> tags = null;
        if (isCollectionNotEmpty(oldWta.getTags())) {
            tags = tagMongoRepository.findAllTagsByIdIn(oldWta.getTags());
            oldWta.setTags(null);
        }
        WTAResponseDTO wtaResponseDTO = ObjectMapperUtils.copyPropertiesByMapper(oldWta, WTAResponseDTO.class);
        wtaResponseDTO.setTags(tags);
        wtaResponseDTO.setRuleTemplates(WTABuilderService.copyRuleTemplatesToDTO(ruleTemplates));
        return wtaResponseDTO;
    }

    public WTAResponseDTO getWta(BigInteger wtaId) {
        WTAQueryResultDTO wtaQueryResult = wtaRepository.getOne(wtaId);
        return ObjectMapperUtils.copyPropertiesByMapper(wtaQueryResult, WTAResponseDTO.class);
    }


    public List<WTABaseRuleTemplateDTO> getwtaRuletemplates(Long unitId, BigInteger wtaId) {
        WTAQueryResultDTO wtaQueryResult = wtaRepository.getOne(wtaId);
        if (wtaQueryResult == null) {
            exceptionService.dataNotFoundByIdException(MESSAGE_WTA_NOTFOUND);
        }
        WTAResponseDTO wtaResponseDTO = ObjectMapperUtils.copyPropertiesByMapper(wtaQueryResult, WTAResponseDTO.class);
        Long countryId = userIntegrationService.getCountryIdOfOrganization(unitId);
        ruleTemplateService.assignCategoryToRuleTemplate(countryId, wtaResponseDTO.getRuleTemplates());
        return wtaResponseDTO.getRuleTemplates();
    }

    public boolean removeWta(BigInteger wtaId) {
        WorkingTimeAgreement wta = wtaRepository.findOne(wtaId);
        if (!Optional.ofNullable(wta).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_WTA_ID, wtaId);
        }
        wta.setDeleted(true);
        save(wta);
        return true;
    }

    public List<WTAResponseDTO> getWTAByCountryId(long countryId) {
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

    public List<WTAResponseDTO> getAllWTAWithOrganizationByCountryId(long countryId) {
        List<WTAQueryResultDTO> wtaQueryResultDTOS = wtaRepository.getAllWTAWithOrganization(countryId);
        List<WTAResponseDTO> wtaResponseDTOS = new ArrayList<>();
        wtaQueryResultDTOS.forEach(wta -> {
            wtaResponseDTOS.add(ObjectMapperUtils.copyPropertiesByMapper(wta, WTAResponseDTO.class));
        });
        return wtaResponseDTOS;
    }

    public List<WTAResponseDTO> getAllWTAWithWTAIdAndCountryId(long countryId, BigInteger wtaId) {
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
            exceptionService.dataNotFoundByIdException(MESSAGE_ORGANIZATION_SUBTYPE_ID, organizationSubTypeId);
        }
        WorkingTimeAgreement wta = wtaRepository.findOne(wtaId);
        if (!Optional.ofNullable(wta).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_WTA_ID, wtaId);
        }
        if (checked) {
            List<WTABaseRuleTemplateDTO> wtaBaseRuleTemplates = wtaBaseRuleTemplateRepository.findAllByIdIn(wta.getRuleTemplateIds());
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

    public CTAWTAAndAccumulatedTimebankWrapper getWTACTAByEmploymentIds(Set<Long> employmentIds) {
        List<WTAQueryResultDTO> wtaQueryResultDTOS = wtaRepository.getAllWTAByUpIds(employmentIds, new Date());
        List<WTAResponseDTO> wtaResponseDTOS = ObjectMapperUtils.copyPropertiesOfListByMapper(wtaQueryResultDTOS, WTAResponseDTO.class);
        List<CTAResponseDTO> ctaResponseDTOS = costTimeAgreementService.getCTAByEmploymentIds(employmentIds);
        return new CTAWTAAndAccumulatedTimebankWrapper(ctaResponseDTOS, wtaResponseDTOS);
    }

    public CTAWTAAndAccumulatedTimebankWrapper getWTACTAByEmployment(Long employmentId, LocalDate startDate) {
        WorkingTimeAgreement wta = wtaRepository.getWTABasicByEmploymentAndDate(employmentId, asDate(startDate));
        CostTimeAgreement cta = costTimeAgreementRepository.getCTABasicByEmploymentAndDate(employmentId, asDate(startDate));
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

    public WTATableSettingWrapper getWTAWithVersionIds(Long unitId, List<Long> employmentIds) {
        Long countryId = userIntegrationService.getCountryIdOfOrganization(unitId);
        List<WTAQueryResultDTO> currentWTAList = wtaRepository.getAllParentWTAByIds(employmentIds);
        List<WTAQueryResultDTO> versionsOfWTAs = wtaRepository.getWTAWithVersionIds(employmentIds);
        List<WTAResponseDTO> parentWTA = ObjectMapperUtils.copyPropertiesOfListByMapper(currentWTAList, WTAResponseDTO.class);
        Map<Long, List<WTAQueryResultDTO>> verionWTAMap = versionsOfWTAs.stream().collect(Collectors.groupingBy(k -> k.getEmploymentId(), Collectors.toList()));
        parentWTA.forEach(currentWTA -> {
            List<WTAResponseDTO> versionWTAs = ObjectMapperUtils.copyPropertiesOfListByMapper(verionWTAMap.get(currentWTA.getEmploymentId()), WTAResponseDTO.class);
            ruleTemplateService.assignCategoryToRuleTemplate(countryId, currentWTA.getRuleTemplates());
            if (versionWTAs != null && !versionWTAs.isEmpty()) {
                currentWTA.setVersions(versionWTAs);
            }
        });
        TableConfiguration tableConfiguration = tableSettingService.getTableConfigurationByTabId(unitId, ORGANIZATION_AGREEMENT_VERSION_TABLE_ID);
        return new WTATableSettingWrapper(parentWTA, tableConfiguration);
    }


    public CTAWTAAndAccumulatedTimebankWrapper assignCTAWTAToEmployment(Long employmentId,Long unitId, BigInteger wtaId, BigInteger ctaId, LocalDate startDate) {
        CTAWTAAndAccumulatedTimebankWrapper ctawtaAndAccumulatedTimebankWrapper = new CTAWTAAndAccumulatedTimebankWrapper();
        if (wtaId != null) {
            WTAResponseDTO wtaResponseDTO = assignWTAToEmployment(employmentId,unitId, wtaId, startDate);
            ctawtaAndAccumulatedTimebankWrapper.setWta(Arrays.asList(wtaResponseDTO));
        }
        if (ctaId != null) {
            CTAResponseDTO ctaResponseDTO = costTimeAgreementService.assignCTAToEmployment(employmentId, ctaId, startDate);
            ctawtaAndAccumulatedTimebankWrapper.setCta(Arrays.asList(ctaResponseDTO));
        }
        return ctawtaAndAccumulatedTimebankWrapper;

    }

    public List<WTAResponseDTO> getWTAOfEmployment(Long employmentId) {
        List<WTAQueryResultDTO> wtaQueryResultDTOS = wtaRepository.getWTAWithVersionIds(newArrayList(employmentId));
        List<WTAResponseDTO> wtaResponseDTOS = ObjectMapperUtils.copyPropertiesOfListByMapper(wtaQueryResultDTOS, WTAResponseDTO.class);
        wtaResponseDTOS.addAll(ObjectMapperUtils.copyPropertiesOfListByMapper(wtaRepository.getAllParentWTAByIds(newArrayList(employmentId)),WTAResponseDTO.class));
        return wtaResponseDTOS;
    }


    private WTAResponseDTO assignWTAToEmployment(Long employmentId,Long unitId, BigInteger wtaId, LocalDate startLocalDate) {
        WTAQueryResultDTO wtaQueryResultDTO = wtaRepository.getOne(wtaId);
        if (!Optional.ofNullable(wtaQueryResultDTO).isPresent()) {
            exceptionService.duplicateDataException(MESSAGE_WTA_ID, wtaId);
        }
        OrganizationDTO organizationDTO = userIntegrationService.getOrganizationWithCountryId(unitId);
        WTAResponseDTO wtaResponseDTO = ObjectMapperUtils.copyPropertiesByMapper(wtaQueryResultDTO, WTAResponseDTO.class);
        WorkingTimeAgreement workingTimeAgreement = ObjectMapperUtils.copyPropertiesByMapper(wtaResponseDTO, WorkingTimeAgreement.class);
        List<WTABaseRuleTemplate> ruleTemplates = new ArrayList<>();
        Map<String, BigInteger> activitiesIdsAndUnitIdsMap = getActivityMapWithUnitId(wtaResponseDTO.getRuleTemplates(), newArrayList(unitId));
        if (CollectionUtils.isNotEmpty(wtaResponseDTO.getRuleTemplates())) {
            ruleTemplates = wtaBuilderService.copyRuleTemplatesWithUpdateActivity(activitiesIdsAndUnitIdsMap, unitId, wtaResponseDTO.getRuleTemplates(), true);
            for (WTABaseRuleTemplate ruleTemplate : ruleTemplates) {
                updateExistingPhaseIdOfWTA(ruleTemplate.getPhaseTemplateValues(), unitId, organizationDTO.getCountryId(), true);
            }
            save(ruleTemplates);
            List<BigInteger> ruleTemplatesIds = ruleTemplates.stream().map(ruleTemplate -> ruleTemplate.getId()).collect(Collectors.toList());
            workingTimeAgreement.setRuleTemplateIds(ruleTemplatesIds);
        }
        workingTimeAgreement.setEmploymentId(employmentId);
        if (wtaQueryResultDTO.getEndDate() != null) {
            workingTimeAgreement.setEndDate(wtaQueryResultDTO.getEndDate());
        }
        workingTimeAgreement.setStartDate(startLocalDate);
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
            //TODO Refactor Tag assignment in WTA
            w.setTags(null);
            WTAResponseDTO wtaResponseDTO = ObjectMapperUtils.copyPropertiesByMapper(w, WTAResponseDTO.class);
            WorkingTimeAgreement workingTimeAgreement = ObjectMapperUtils.copyPropertiesByMapper(wtaResponseDTO, WorkingTimeAgreement.class);
            List<WTABaseRuleTemplate> ruleTemplates;
            if (wtaResponseDTO.getRuleTemplates() != null && !wtaResponseDTO.getRuleTemplates().isEmpty()) {
                ruleTemplates = wtaBuilderService.copyRuleTemplates(wtaResponseDTO.getRuleTemplates(), true);
                for (WTABaseRuleTemplate ruleTemplate : ruleTemplates) {
                    updateExistingPhaseIdOfWTA(ruleTemplate.getPhaseTemplateValues(), organisationId, countryId, true);
                }
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

        });
        if (!workingTimeAgreements.isEmpty()) {
            save(workingTimeAgreements);
        }
        return true;
    }

    public WTAResponseDTO updateWtaOfEmployment(Long unitId, WTADTO wtadto, Boolean oldEmploymentPublished) {
        Optional<WorkingTimeAgreement> oldWta = wtaRepository.findById(wtadto.getId());
        if (!Optional.ofNullable(oldWta).isPresent()) {
            logger.info("wta not found while updating at unit %d", wtadto.getId());
            exceptionService.dataNotFoundByIdException(MESSAGE_WTA_ID, wtadto.getId());
        }
        WTAResponseDTO wtaResponseDTO;
        if (oldEmploymentPublished) {
            wtaResponseDTO = updateWTAOfPublishedEmployment(oldWta.get(), wtadto, unitId);
        } else {
            wtaResponseDTO = updateWTAOfUnpublishedEmployment(oldWta.get(), wtadto, unitId);
            wtaRepository.save(oldWta.get());
        }
        wtaResponseDTO.setStartDate(wtadto.getStartDate());
        if (isNotNull(wtadto.getEndDate())) {
            wtaResponseDTO.setEndDate(wtadto.getEndDate());
        }
        return wtaResponseDTO;
    }

    private WTAResponseDTO updateWTAOfUnpublishedEmployment(WorkingTimeAgreement oldWta, WTADTO updateDTO, Long unitId) {
        if (!updateDTO.getStartDate().equals(oldWta.getStartDate())) {
            boolean wtaExists = wtaRepository.wtaExistsByEmploymentIdAndDatesAndNotEqualToId(oldWta.getId(), oldWta.getEmploymentId(), asDate(updateDTO.getStartDate()), isNotNull(updateDTO.getEndDate()) ? asDate(updateDTO.getEndDate()) : null);
            if (wtaExists) {
                exceptionService.duplicateDataException("error.wta.invalid", updateDTO.getStartDate(), isNotNull(updateDTO.getEndDate()) ? asDate(updateDTO.getEndDate()) : "");
            }
        }
        OrganizationDTO organization = userIntegrationService.getOrganizationWithCountryId(unitId);
        if (!Optional.ofNullable(organization).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_UNIT_ID, unitId);
        }
        oldWta.setDescription(updateDTO.getDescription());
        oldWta.setName(updateDTO.getName());
        List<WTABaseRuleTemplate> ruleTemplates = new ArrayList<>();
        if (isCollectionNotEmpty(updateDTO.getRuleTemplates())) {
            ruleTemplates = wtaBuilderService.copyRuleTemplates(updateDTO.getRuleTemplates(), true);
            for (WTABaseRuleTemplate ruleTemplate : ruleTemplates) {
                updateExistingPhaseIdOfWTA(ruleTemplate.getPhaseTemplateValues(), unitId, organization.getCountryId(), true);
            }
            save(ruleTemplates);
            List<BigInteger> ruleTemplatesIds = ruleTemplates.stream().map(ruleTemplate -> ruleTemplate.getId()).collect(Collectors.toList());
            oldWta.setRuleTemplateIds(ruleTemplatesIds);
        }
        oldWta.setEndDate(isNotNull(updateDTO.getEndDate()) ? updateDTO.getEndDate() : null);
        WTAResponseDTO wtaResponseDTO = ObjectMapperUtils.copyPropertiesByMapper(oldWta, WTAResponseDTO.class);
        wtaResponseDTO.setRuleTemplates(WTABuilderService.copyRuleTemplatesToDTO(ruleTemplates));

        return wtaResponseDTO;
    }


    public List<CTAWTAResponseDTO> copyWtaCTA(List<EmploymentIdDTO> employmentIds) {


        logger.info("Inside wtaservice");
        List<Long> oldEmploymentIds = employmentIds.stream().map(employmentIdDTO -> employmentIdDTO.getOldEmploymentId()).collect(Collectors.toList());
        Map<Long, Long> newOldemploymentIdMap = employmentIds.stream().collect(toMap(k -> k.getOldEmploymentId(), v -> v.getNewEmploymentId()));
        List<WTAQueryResultDTO> oldWtas = wtaRepository.getWTAByEmploymentIds(oldEmploymentIds, DateUtils.getCurrentDate());

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
            wtaDB.setEmploymentId(newOldemploymentIdMap.get(wta.getEmploymentId()));
            wtaDB.setRuleTemplateIds(ruleTemplateIds);
            wtaDB.setId(null);
            newWtas.add(wtaDB);
        }

        if (!newWtas.isEmpty()) {
            save(newWtas);
        }
        List<CTAResponseDTO> ctaResponseDTOs = costTimeAgreementRepository.getCTAByEmploymentIds(oldEmploymentIds, DateUtils.getCurrentDate());


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
            newCTA.setEmploymentId(newOldemploymentIdMap.get(cta.getEmploymentId()));
            newCTA.setId(null);
            newCTAs.add(newCTA);
        }
        if (!newCTAs.isEmpty()) {
            save(newCTAs);
        }

        Map<Long, CostTimeAgreement> ctaMap = newCTAs.stream().collect(toMap(k -> k.getEmploymentId(), v -> v));
        List<CTAWTAResponseDTO> ctaWtas = new ArrayList<>();
        for (WorkingTimeAgreement wta : newWtas) {

            CTAWTAResponseDTO ctaWTAResponseDTO = new CTAWTAResponseDTO(wta.getId(), wta.getName(), wta.getEmploymentId(), ctaMap.get(wta.getEmploymentId()).getId(),
                    ctaMap.get(wta.getEmploymentId()).getName());
            ctaWtas.add(ctaWTAResponseDTO);

        }

        return ctaWtas;
    }

    public CTAWTAAndAccumulatedTimebankWrapper assignCTAWTAToEmployment(Long employmentId,Long unitId, BigInteger wtaId, BigInteger oldwtaId, BigInteger ctaId, BigInteger oldctaId, LocalDate startDate) {
        CTAWTAAndAccumulatedTimebankWrapper ctawtaAndAccumulatedTimebankWrapper = new CTAWTAAndAccumulatedTimebankWrapper();
        if (wtaId != null) {
            WTAResponseDTO wtaResponseDTO = assignWTAToEmployment(employmentId,unitId, wtaId, startDate);
            ctawtaAndAccumulatedTimebankWrapper.setWta(Arrays.asList(wtaResponseDTO));
            wtaRepository.disableOldWta(oldwtaId, startDate.minusDays(1));
        }
        if (ctaId != null) {
            CTAResponseDTO ctaResponseDTO = costTimeAgreementService.assignCTAToEmployment(employmentId, ctaId, startDate);
            ctawtaAndAccumulatedTimebankWrapper.setCta(Arrays.asList(ctaResponseDTO));
            costTimeAgreementRepository.disableOldCta(oldctaId, startDate.minusDays(1));
        }
        return ctawtaAndAccumulatedTimebankWrapper;

    }

    public boolean setEndCTAWTAOfEmployment(Long employmentId, LocalDate endDate) {
        wtaRepository.setEndDateToWTAOfEmployment(employmentId, endDate);
        costTimeAgreementRepository.setEndDateToCTAOfEmployment(employmentId, endDate);
        return true;
    }

    private Map<String, BigInteger> getActivityMapWithUnitId(List<WTABaseRuleTemplateDTO> wtaBaseRuleTemplateDTOS, List<Long> organisationIds) {
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

        List<Activity> activities = activityMongoRepository.findAllActivitiesByUnitIds(organisationIds, activityIds);
        Map<String, BigInteger> activitiesIdsAndUnitIdsMap = activities.stream().collect(toMap(k -> k.getCountryParentId() + "-" + k.getUnitId(), v -> v.getId()));
        return activitiesIdsAndUnitIdsMap;
    }

    private WTAResponseDTO updateWTAOfPublishedEmployment(WorkingTimeAgreement oldWta, WTADTO wtadto, Long unitId) {
        if (!wtadto.getStartDate().equals(oldWta.getStartDate())) {
            boolean wtaExists = wtaRepository.wtaExistsByEmploymentIdAndDatesAndNotEqualToId(oldWta.getId(), oldWta.getEmploymentId(), asDate(wtadto.getStartDate()), isNotNull(wtadto.getEndDate()) ? asDate(wtadto.getEndDate()) : null);
            if (wtaExists) {
                exceptionService.duplicateDataException("error.wta.invalid", wtadto.getStartDate(), isNotNull(wtadto.getEndDate()) ? wtadto.getEndDate() : "");
            }
        }
        List<WTABaseRuleTemplate> wtaBaseRuleTemplates = null;
        WTAResponseDTO wtaResponseDTO;
        if (isCollectionNotEmpty(wtadto.getRuleTemplates())) {
            wtaBaseRuleTemplates = wtaBuilderService.copyRuleTemplates(wtadto.getRuleTemplates(), false);
        }
        boolean isCalculatedValueChanged = isCalCulatedValueChangedForWTA(oldWta, wtaBaseRuleTemplates);
        if (wtadto.getStartDate().isBefore(oldWta.getStartDate()) || wtadto.getStartDate().equals(oldWta.getStartDate()) || !isCalculatedValueChanged) {
            wtaResponseDTO = updateWTAOfUnpublishedEmployment(oldWta, wtadto, unitId);
            oldWta.setStartDate(wtadto.getStartDate());
            wtaResponseDTO.setStartDate(wtadto.getStartDate());
            wtaRepository.save(oldWta);
        } else {
            OrganizationDTO organization = userIntegrationService.getOrganizationWithCountryId(unitId);
            if (!Optional.ofNullable(organization).isPresent()) {
                exceptionService.dataNotFoundByIdException(MESSAGE_UNIT_ID, unitId);
            }
            WorkingTimeAgreement newWta = ObjectMapperUtils.copyPropertiesByMapper(oldWta, WorkingTimeAgreement.class);
            newWta.setDescription(wtadto.getDescription());
            newWta.setName(wtadto.getName());
            newWta.setOrganizationParentId(oldWta.getOrganizationParentId());
            newWta.setStartDate(wtadto.getStartDate());
            newWta.setEndDate(wtadto.getEndDate() != null ? wtadto.getEndDate() : null);
            newWta.setRuleTemplateIds(null);
            oldWta.setDisabled(true);
            if (oldWta.getStartDate().isBefore(wtadto.getStartDate()) || (isNotNull(oldWta.getEndDate()) && oldWta.getEndDate().equals(wtadto.getEndDate()))) {
                oldWta.setEndDate(wtadto.getStartDate().minusDays(1));
            }
            oldWta.setId(null);
            if (isCollectionNotEmpty(wtadto.getRuleTemplates())) {
                wtaBaseRuleTemplates = wtaBuilderService.copyRuleTemplates(wtadto.getRuleTemplates(), true);
                for (WTABaseRuleTemplate ruleTemplate : wtaBaseRuleTemplates) {
                    updateExistingPhaseIdOfWTA(ruleTemplate.getPhaseTemplateValues(), unitId, organization.getCountryId(), true);
                }
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
            List<WTABaseRuleTemplate> existingWtaBaseRuleTemplates = wtaBaseRuleTemplateRepository.findAllByIdInAndDeletedFalse(oldWta.getRuleTemplateIds());
            version.setRuleTemplates(WTABuilderService.copyRuleTemplatesToDTO(existingWtaBaseRuleTemplates));
            wtaResponseDTO.setVersions(Collections.singletonList(version));
            wtaResponseDTO.setRuleTemplates(WTABuilderService.copyRuleTemplatesToDTO(wtaBaseRuleTemplates));
        }
        return wtaResponseDTO;
    }

    private boolean isCalCulatedValueChangedForWTA(WorkingTimeAgreement oldWorkingTimeAgreement, List<WTABaseRuleTemplate> wtaBaseRuleTemplates) {
        boolean isCalculatedValueChanged = false;
        if (oldWorkingTimeAgreement.getRuleTemplateIds().size() == wtaBaseRuleTemplates.size()) {
            List<WTABaseRuleTemplate> existingWtaBaseRuleTemplates = wtaBaseRuleTemplateRepository.findAllByIdInAndDeletedFalse(oldWorkingTimeAgreement.getRuleTemplateIds());
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


    public CTAWTAAndAccumulatedTimebankWrapper getEmploymentCtaWtaAndAccumulatedTimebank(Long unitId, Map<Long, List<EmploymentLinesDTO>> employmentLinesMap) {
       return getWTACTAByEmploymentIds(employmentLinesMap.keySet());
    }

    public WorkTimeAgreementBalance getWorktimeAgreementBalance(Long unitId, Long employmentId, LocalDate startDate, LocalDate endDate) {
        return workTimeAgreementBalancesCalculationService.getWorktimeAgreementBalance(unitId, employmentId, startDate, endDate);
    }

    public IntervalBalance getProtectedDaysOffCount(Long unitId, LocalDate localDate, Long staffId , BigInteger activityId) {
        localDate =isNotNull(localDate)?localDate:DateUtils.getCurrentLocalDate();
        //WTAQueryResultDTO wtaQueryResultDTO = isNotNull(wtaId) ? wtaRepository.getOne(wtaId) : null;
        WorkTimeAgreementRuleTemplateBalancesDTO workTimeAgreementRuleTemplateBalancesDTO=null;
        StaffEmploymentDetails staffEmploymentDetails = userIntegrationService.mainUnitEmploymentOfStaff(staffId, unitId);
        if(isNotNull(staffEmploymentDetails)) {
            StaffAdditionalInfoDTO staffAdditionalInfoDTO = new StaffAdditionalInfoDTO(staffEmploymentDetails);
            ProtectedDaysOffWTATemplate protectedDaysOffWTATemplate = new ProtectedDaysOffWTATemplate(activityId,WTATemplateType.PROTECTED_DAYS_OFF);
            List<ActivityWrapper> activityWrappers = activityMongoRepository.findActivitiesAndTimeTypeByActivityId(newArrayList(activityId));
            Map<BigInteger, ActivityWrapper> activityWrapperMap = activityWrappers.stream().collect(Collectors.toMap(k -> k.getActivity().getId(), v -> v));
            PlanningPeriod planningPeriod = planningPeriodMongoRepository.getLastPlanningPeriod(unitId);
            DateTimeInterval dateTimeInterval = workTimeAgreementBalancesCalculationService.getIntervalByRuletemplates(activityWrapperMap, Arrays.asList(protectedDaysOffWTATemplate), localDate, planningPeriod.getEndDate(), unitId);
            List<ShiftWithActivityDTO> shiftWithActivityDTOS = shiftMongoRepository.findAllShiftsBetweenDurationByEmploymentAndActivityIds(staffAdditionalInfoDTO.getEmployment().getId(), dateTimeInterval.getStartDate(), dateTimeInterval.getEndDate(), newHashSet(activityId));
            workTimeAgreementRuleTemplateBalancesDTO = workTimeAgreementBalancesCalculationService.getProtectedDaysOffBalance(unitId, protectedDaysOffWTATemplate, shiftWithActivityDTOS, activityWrapperMap, new HashMap<>(), staffAdditionalInfoDTO, localDate, localDate, planningPeriod.getEndDate());
        }
        return isNotNull(workTimeAgreementRuleTemplateBalancesDTO)?workTimeAgreementRuleTemplateBalancesDTO.getIntervalBalances().get(0):new IntervalBalance();
    }

    public void updateExistingPhaseIdOfWTA(List<PhaseTemplateValue> phaseTemplateValues, Long unitId, Long referenceId, boolean creatingFromCountry) {
        List<Phase> countryPhase = creatingFromCountry ? phaseMongoRepository.findAllBycountryIdAndDeletedFalse(referenceId) : phaseMongoRepository.findByOrganizationIdAndDeletedFalse(referenceId);
        Map<BigInteger, PhaseDefaultName> phaseDefaultNameMap = countryPhase.stream().collect(Collectors.toMap(Phase::getId, Phase::getPhaseEnum));
        List<Phase> unitPhases = phaseMongoRepository.findByOrganizationIdAndDeletedFalse(unitId);
        Map<PhaseDefaultName, BigInteger> parentPhasesAndUnitPhaseIdMap = unitPhases.stream().collect(Collectors.toMap(Phase::getPhaseEnum, Phase::getId));
        for (PhaseTemplateValue phaseTemplateValue : phaseTemplateValues) {
            BigInteger phaseId = parentPhasesAndUnitPhaseIdMap.getOrDefault(phaseDefaultNameMap.get(phaseTemplateValue.getPhaseId()), phaseTemplateValue.getPhaseId());
            phaseTemplateValue.setPhaseId(phaseId);

        }
    }


    //TODO please remvoe this method when sprint 44 is close
    public boolean updatePhasesInRuletemplate() {
        List<WorkingTimeAgreement> workingTimeAgreements = wtaRepository.findWTAofOrganization();
        Map<Long, Map<String, BigInteger>> phasesMap = new HashMap<>();
        for (WorkingTimeAgreement workingTimeAgreement : workingTimeAgreements) {
            Map<String, BigInteger> stringBigIntegerMap = new HashMap<>();
            boolean valid = false;
            if (!phasesMap.containsKey(workingTimeAgreement.getOrganization().getId())) {
                List<Phase> phases = phaseMongoRepository.findByOrganizationIdAndDeletedFalse(workingTimeAgreement.getOrganization().getId());
                if (phases.size() == 8) {
                    valid = true;
                    stringBigIntegerMap = phases.stream().collect(Collectors.toMap(k -> k.getName(), v -> v.getId()));
                    phasesMap.put(workingTimeAgreement.getOrganization().getId(), stringBigIntegerMap);
                }
            } else {
                stringBigIntegerMap = phasesMap.get(workingTimeAgreement.getOrganization().getId());
                valid = true;
            }
            if (valid) {
                List<WTABaseRuleTemplate> wtaBaseRuleTemplates = wtaBaseRuleTemplateRepository.findAllByIdInAndDeletedFalse(workingTimeAgreement.getRuleTemplateIds());
                for (WTABaseRuleTemplate wtaBaseRuleTemplate : wtaBaseRuleTemplates) {
                    for (PhaseTemplateValue phaseTemplateValue : wtaBaseRuleTemplate.getPhaseTemplateValues()) {
                        phaseTemplateValue.setPhaseId(stringBigIntegerMap.getOrDefault(phaseTemplateValue.getPhaseName(), phaseTemplateValue.getPhaseId()));

                    }
                }
                wtaBaseRuleTemplateRepository.saveAll(wtaBaseRuleTemplates);
            }

        }
        workingTimeAgreements = wtaRepository.findWTAOfEmployments();
        Map<Long, Long> employmentAndUnitMap = new HashMap<>();
        for (WorkingTimeAgreement workingTimeAgreement : workingTimeAgreements) {
            Long unitId;
            if (!employmentAndUnitMap.containsKey(workingTimeAgreement.getEmploymentId())) {
                unitId = userIntegrationService.getUnitByEmploymentId(workingTimeAgreement.getEmploymentId());
                if (isNotNull(unitId)) {
                    employmentAndUnitMap.put(workingTimeAgreement.getEmploymentId(), unitId);
                }
            } else {
                unitId = employmentAndUnitMap.get(workingTimeAgreement.getEmploymentId());
            }
            Map<String, BigInteger> stringBigIntegerMap = new HashMap<>();
            boolean valid = false;
            if (!phasesMap.containsKey(unitId) && isNotNull(unitId)) {
                List<Phase> phases = phaseMongoRepository.findByOrganizationIdAndDeletedFalse(unitId);
                if (phases.size() == 8) {
                    valid = true;
                    stringBigIntegerMap = phases.stream().collect(Collectors.toMap(k -> k.getName(), v -> v.getId()));
                    phasesMap.put(unitId, stringBigIntegerMap);
                }
            } else {
                stringBigIntegerMap = phasesMap.get(unitId);
                valid = true;
            }
            if (valid && isNotNull(unitId)) {
                List<WTABaseRuleTemplate> wtaBaseRuleTemplates = wtaBaseRuleTemplateRepository.findAllByIdInAndDeletedFalse(workingTimeAgreement.getRuleTemplateIds());
                for (WTABaseRuleTemplate wtaBaseRuleTemplate : wtaBaseRuleTemplates) {
                    for (PhaseTemplateValue phaseTemplateValue : wtaBaseRuleTemplate.getPhaseTemplateValues()) {
                        phaseTemplateValue.setPhaseId(stringBigIntegerMap.getOrDefault(phaseTemplateValue.getPhaseName(), phaseTemplateValue.getPhaseId()));

                    }
                }
                wtaBaseRuleTemplateRepository.saveAll(wtaBaseRuleTemplates);
            }
        }
        return true;
    }

    public WTAQueryResultDTO getWtaQueryResultDTOByDateAndEmploymentId(Long employmentId,Date startDate) {
        WTAQueryResultDTO wtaQueryResultDTO = getWTAByEmploymentIdAndDate(employmentId, startDate);
        if (!Optional.ofNullable(wtaQueryResultDTO).isPresent()) {
            exceptionService.actionNotPermittedException(MESSAGE_WTA_NOTFOUND);
        }
        return wtaQueryResultDTO;
    }

    public boolean getWtaByName(String wtaName, Long countryId){
        return wtaRepository.getWtaByName(wtaName, countryId);
    }

    public WorkingTimeAgreement getWTAByCountryId(long countryId, BigInteger wtaId){
        return wtaRepository.getWTAByCountryId(countryId, wtaId);
    }

    public boolean isWTAExistWithSameOrgTypeAndSubType(Long orgType,Long orgSubType, String name){
        return wtaRepository.isWTAExistWithSameOrgTypeAndSubType(orgType, orgSubType, name);
    }

    public List<WorkingTimeAgreement> findWTAByUnitIdsAndName(List<Long> organizationIds, String name){
        return wtaRepository.findWTAByUnitIdsAndName(organizationIds, name);
    }

    public boolean isWTAExistByOrganizationIdAndName(long organizationId, String wtaName){
        return wtaRepository.isWTAExistByOrganizationIdAndName(organizationId, wtaName);
    }

    public List<WorkingTimeAgreement> findWTAofOrganization(){
        return wtaRepository.findWTAofOrganization();
    }

    public List<WorkingTimeAgreement> findWTAOfEmployments(){
        return wtaRepository.findWTAOfEmployments();
    }

    public List<WTAQueryResultDTO> getWtaByOrganization(Long organizationId){
        return wtaRepository.getWtaByOrganization(organizationId);
    }

    public WTAQueryResultDTO getOne(BigInteger wtaId){
        return wtaRepository.getOne(wtaId);
    }

    public List<WTAQueryResultDTO> getAllWTAByCountryId(long countryId){
        return wtaRepository.getAllWTAByCountryId(countryId);
    }

    public List<WTAQueryResultDTO> getAllWTAByOrganizationSubTypeIdAndCountryId(long organizationSubTypeId, long countryId){
        return wtaRepository.getAllWTAByOrganizationSubTypeIdAndCountryId(organizationSubTypeId, countryId);
    }

    public List<WTAQueryResultDTO> getAllWTABySubType(List<Long> subTypeIds, Long countryId){
        return wtaRepository.getAllWTABySubType(subTypeIds, countryId);
    }

    public List<WTAQueryResultDTO> getAllWTAWithOrganization(long countryId){
        return wtaRepository.getAllWTAWithOrganization(countryId);
    }

    public List<WTAQueryResultDTO> getAllWTAWithWTAId(long countryId, BigInteger wtaId){
        return wtaRepository.getAllWTAWithWTAId(countryId, wtaId);
    }

    public List<WTAQueryResultDTO> getAllWtaOfOrganizationByExpertise(Long unitId, Long expertiseId,LocalDate selectedDate){
        return wtaRepository.getAllWtaOfOrganizationByExpertise(unitId, expertiseId, selectedDate);
    }

    public List<WTAQueryResultDTO> getAllWtaOfEmploymentIdAndDate(Long employmentId,LocalDate selectedDate){
        return wtaRepository.getAllWtaOfEmploymentIdAndDate(employmentId, selectedDate);
    }

    public List<WTAQueryResultDTO> getAllWtaByIds(List<BigInteger> ids){
        return wtaRepository.getAllWtaByIds(ids);
    }

    public WorkingTimeAgreement getWtaByNameExcludingCurrent(String wtaName, Long countryId, BigInteger wtaId, Long organizationTypeId, Long subOrganizationTypeId){
        return wtaRepository.getWtaByNameExcludingCurrent(wtaName, countryId, wtaId, organizationTypeId, subOrganizationTypeId);
    }

    public WorkingTimeAgreement checkUniqueWTANameInOrganization(String name, Long unitId, BigInteger wtaId){
        return wtaRepository.checkUniqueWTANameInOrganization(name, unitId, wtaId);
    }

    public List<WTAQueryResultDTO> getAllWTAByUpIds(Set<Long> upIds, Date date){
        return wtaRepository.getAllWTAByUpIds(upIds, date);
    }

    public List<WTAQueryResultDTO> getAllParentWTAByIds(List<Long> employmentIds){
        return wtaRepository.getAllParentWTAByIds(employmentIds);
    }

    public List<WTAQueryResultDTO> getWTAWithVersionIds(List<Long> employmentIds){
        return wtaRepository.getWTAWithVersionIds(employmentIds);
    }

    public WTAQueryResultDTO getWTAByEmploymentIdAndDate(Long employmentId, Date date){
        return wtaRepository.getWTAByEmploymentIdAndDate(employmentId, date);
    }

    public List<WTAQueryResultDTO> getWTAByEmploymentIds(List<Long> employmentIds, Date date){
        return wtaRepository.getWTAByEmploymentIds(employmentIds, date);
    }

    public List<WTAQueryResultDTO> getWTAByEmploymentIdsAndDates(List<Long> employmentIds, Date startDate, Date endDate){
        return wtaRepository.getWTAByEmploymentIdsAndDates(employmentIds, startDate, endDate);
    }

    public WorkingTimeAgreement getWTABasicByEmploymentAndDate(Long employmentId, Date date){
        return wtaRepository.getWTABasicByEmploymentAndDate(employmentId, date);
    }

    public void disableOldWta(BigInteger oldwtaId, LocalDate endDate){
        wtaRepository.disableOldWta(oldwtaId, endDate);
    }

    public void setEndDateToWTAOfEmployment(Long employmentId, LocalDate endDate){
        wtaRepository.setEndDateToWTAOfEmployment(employmentId, endDate);
    }

    public boolean wtaExistsByEmploymentIdAndDatesAndNotEqualToId(BigInteger wtaId, Long employmentId, Date startDate, Date endDate){
        return wtaRepository.wtaExistsByEmploymentIdAndDatesAndNotEqualToId(wtaId, employmentId, startDate, endDate);
    }

    public List<WTAQueryResultDTO> getWTAByEmploymentIdAndDates(Long employmentId, Date startDate, Date endDate){
        return wtaRepository.getWTAByEmploymentIdAndDates(employmentId, startDate, endDate);
    }

    public List<WTAQueryResultDTO> getWTAByEmploymentIdAndDatesWithRuleTemplateType(Long employmentId, Date startDate, Date endDate, WTATemplateType templateType){
        return wtaRepository.getWTAByEmploymentIdAndDatesWithRuleTemplateType(employmentId, startDate, endDate, templateType);
    }

    public List<WTAQueryResultDTO> getAllWTAByEmploymentIds(Collection<Long> employmentIds){
        return wtaRepository.getAllWTAByEmploymentIds(employmentIds);
    }

}
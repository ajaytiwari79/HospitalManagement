package com.kairos.service.wta;

import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.TranslationInfo;
import com.kairos.dto.activity.activity.ActivityDTO;
import com.kairos.dto.activity.activity.TableConfiguration;
import com.kairos.dto.activity.cta.CTAResponseDTO;
import com.kairos.dto.activity.cta.CTAWTAAndAccumulatedTimebankWrapper;
import com.kairos.dto.activity.presence_type.PresenceTypeDTO;
import com.kairos.dto.activity.shift.ShiftDTO;
import com.kairos.dto.activity.tags.TagDTO;
import com.kairos.dto.activity.time_bank.EmploymentWithCtaDetailsDTO;
import com.kairos.dto.activity.time_type.TimeTypeDTO;
import com.kairos.dto.activity.wta.CTAWTAResponseDTO;
import com.kairos.dto.activity.wta.WorkTimeAgreementBalance;
import com.kairos.dto.activity.wta.basic_details.*;
import com.kairos.dto.activity.wta.templates.PhaseTemplateValue;
import com.kairos.dto.activity.wta.version.WTATableSettingWrapper;
import com.kairos.dto.gdpr.FilterSelectionDTO;
import com.kairos.dto.user.country.agreement.cta.cta_response.DayTypeDTO;
import com.kairos.dto.user.country.time_slot.TimeSlotDTO;
import com.kairos.dto.user.employment.EmploymentIdDTO;
import com.kairos.dto.user.employment.EmploymentLinesDTO;
import com.kairos.dto.user.organization.OrganizationBasicDTO;
import com.kairos.dto.user.organization.OrganizationDTO;
import com.kairos.dto.user.staff.StaffFilterDTO;
import com.kairos.dto.user.user.staff.StaffAdditionalInfoDTO;
import com.kairos.dto.user_context.UserContext;
import com.kairos.enums.FilterType;
import com.kairos.enums.StaffWorkingType;
import com.kairos.enums.kpermissions.FieldLevelPermission;
import com.kairos.enums.phase.PhaseDefaultName;
import com.kairos.enums.wta.WTATemplateType;
import com.kairos.persistence.model.cta.CostTimeAgreement;
import com.kairos.persistence.model.phase.Phase;
import com.kairos.persistence.model.wta.*;
import com.kairos.persistence.model.wta.templates.WTABaseRuleTemplate;
import com.kairos.persistence.model.wta.templates.template_types.DurationBetweenShiftsWTATemplate;
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
import com.kairos.service.activity.*;
import com.kairos.service.cta.CostTimeAgreementService;
import com.kairos.service.day_type.DayTypeService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.integration.PlannerSyncService;
import com.kairos.service.kpermissions.ActivityPermissionService;
import com.kairos.service.night_worker.NightWorkerService;
import com.kairos.service.shift.ShiftFilterService;
import com.kairos.service.shift.ShiftHelperService;
import com.kairos.service.table_settings.TableSettingService;
import com.kairos.service.tag.TagService;
import com.kairos.service.time_bank.TimeBankCalculationService;
import com.kairos.service.time_bank.TimeBankService;
import com.kairos.service.time_slot.TimeSlotSetService;
import com.kairos.service.unit_settings.ProtectedDaysOffService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.asDate;
import static com.kairos.commons.utils.DateUtils.getDate;
import static com.kairos.commons.utils.ObjectMapperUtils.copyCollectionPropertiesByMapper;
import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.constants.ActivityMessagesConstants.*;
import static com.kairos.constants.AppConstants.COPY_OF;
import static com.kairos.constants.AppConstants.ORGANIZATION;
import static com.kairos.enums.FilterType.NIGHT_WORKERS;
import static com.kairos.persistence.model.constants.TableSettingConstants.ORGANIZATION_AGREEMENT_VERSION_TABLE_ID;
import static java.util.stream.Collectors.toMap;

@Transactional
@Service
public class WorkTimeAgreementService{

    private static final Logger logger = LoggerFactory.getLogger(WorkTimeAgreementService.class);
    @Inject
    private WorkingTimeAgreementMongoRepository wtaRepository;
    @Inject
    private RuleTemplateCategoryRepository ruleTemplateCategoryRepository;
    @Inject
    private WTABaseRuleTemplateMongoRepository wtaBaseRuleTemplateRepository;
    @Inject
    @Lazy
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
    @Inject
    private ProtectedDaysOffService protectedDaysOffService;
    @Inject
    private TimeBankCalculationService timeBankCalculationService;
    @Inject
    private NightWorkerService nightWorkerService;
    @Inject
    private ShiftFilterService shiftFilterService;
    @Inject
    private WorkingTimeAgreementMongoRepository workingTimeAgreementMongoRepository;
    @Inject
    private PlannedTimeTypeService plannedTimeTypeService;
    @Inject
    private DayTypeService dayTypeService;
    @Inject
    private TimeSlotSetService timeSlotSetService;
    @Inject private ActivityPermissionService activityPermissionService;
    @Inject  private ShiftHelperService shiftHelperService;


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
        WTAResponseDTO wtaResponseDTO = createRuletemplatesForWorkTimeAgreement(wtaDTO, wta);
        if (creatingFromCountry) {
            wtaRepository.save(wta);
            wtaResponseDTO.setId(wta.getId());
        }
        Map<Long, WTAResponseDTO> wtaResponseDTOMap = assignWTAToNewOrganization(wta, wtaDTO, wtaBasicDetailsDTO, creatingFromCountry, referenceId);

        if (!creatingFromCountry) {
            wtaResponseDTO = wtaResponseDTOMap.get(referenceId);
        }
        return wtaResponseDTO;
    }

    private WTAResponseDTO createRuletemplatesForWorkTimeAgreement(WTADTO wtaDTO, WorkingTimeAgreement wta) {
        List<WTABaseRuleTemplate> ruleTemplates = new ArrayList<>();
        if (isCollectionNotEmpty(wtaDTO.getRuleTemplates())) {
            ruleTemplates = wtaBuilderService.copyRuleTemplates(wtaDTO.getRuleTemplates(), true);
            wtaBaseRuleTemplateRepository.saveEntities(ruleTemplates);
            List<BigInteger> ruleTemplatesIds = ruleTemplates.stream().map(ruleTemplate -> ruleTemplate.getId()).collect(Collectors.toList());
            wta.setRuleTemplateIds(ruleTemplatesIds);

        }
        WTAResponseDTO wtaResponseDTO = ObjectMapperUtils.copyPropertiesByMapper(wta, WTAResponseDTO.class);
        wtaResponseDTO.setRuleTemplates(WTABuilderService.copyRuleTemplatesToDTO(ruleTemplates));
        return wtaResponseDTO;
    }

    private Map<Long, WTAResponseDTO> assignWTAToNewOrganization(WorkingTimeAgreement wta, WTADTO wtadto, WTABasicDetailsDTO wtaBasicDetailsDTO, boolean creatingFromCountry, Long referenceId) {
        List<WorkingTimeAgreement> workingTimeAgreements = new ArrayList<>(wtaBasicDetailsDTO.getOrganizations().size());
        List<Long> organizationIds = wtaBasicDetailsDTO.getOrganizations().stream().map(OrganizationBasicDTO::getId).collect(Collectors.toList());
        List<WorkingTimeAgreement> workingTimeAgreementList = wtaRepository.findWTAByUnitIdsAndName(organizationIds, wtadto.getName());
        Map<String, WorkingTimeAgreement> workingTimeAgreementMap = workingTimeAgreementList.stream().collect(toMap(k -> k.getName() + "_" + k.getOrganization().getId() + "_" + k.getOrganizationType().getId(), v -> v));
        Map<String, BigInteger> activitiesIdsAndUnitIdsMap = ruleTemplateService.getActivityMapWithUnitId(wtadto.getRuleTemplates(), organizationIds);
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
                    wtaBaseRuleTemplateRepository.saveEntities(ruleTemplates);
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
            logger.info("wta not found while updating at unit {}", wtaId);
            exceptionService.dataNotFoundByIdException(MESSAGE_WTA_ID, wtaId);
        }
        WTABasicDetailsDTO wtaBasicDetailsDTO = userIntegrationService.getWtaRelatedInfo(updateDTO.getExpertiseId(), updateDTO.getOrganizationSubType(), countryId, null, updateDTO.getOrganizationType(), Collections.emptyList());
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
        wtaRepository.save(oldWta);
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
        wtaRepository.save(wta);
        return true;
    }

    public List<WTAResponseDTO> getWTAByCountryId(long countryId) {
        List<WTAQueryResultDTO> wtaQueryResultDTOS = wtaRepository.getAllWTAByCountryId(countryId);
        List<WTAResponseDTO> wtaResponseDTOS = new ArrayList<>();
        wtaQueryResultDTOS.forEach(wta -> {
            WTAResponseDTO wtaResponseDTO = ObjectMapperUtils.copyPropertiesByMapper(wta, WTAResponseDTO.class);
            wtaResponseDTO.setCountryId(countryId);
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
        wtaQueryResultDTOS.forEach(wta -> wtaResponseDTOS.add(ObjectMapperUtils.copyPropertiesByMapper(wta, WTAResponseDTO.class)));
        return wtaResponseDTOS;
    }

    public List<WTAResponseDTO> getAllWTAWithWTAIdAndCountryId(long countryId, BigInteger wtaId) {
        List<WTAQueryResultDTO> wtaQueryResultDTOS = wtaRepository.getAllWTAWithWTAId(countryId, wtaId);
        List<WTAResponseDTO> wtaResponseDTOS = new ArrayList<>();
        wtaQueryResultDTOS.forEach(wta -> wtaResponseDTOS.add(ObjectMapperUtils.copyPropertiesByMapper(wta, WTAResponseDTO.class)));
        return wtaResponseDTOS;
    }


    public Map<String, Object> setWtaWithOrganizationType(Long countryId, BigInteger wtaId, long organizationSubTypeId, boolean checked) {
        Map<String, Object> map = new HashMap<>();
        WTABasicDetailsDTO wtaBasicDetailsDTO = userIntegrationService.getWtaRelatedInfo(0L, organizationSubTypeId, countryId, null, 0L, Collections.emptyList());
        if (!Optional.ofNullable(wtaBasicDetailsDTO.getOrganizationSubType()).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_ORGANIZATION_SUBTYPE_ID, organizationSubTypeId);
        }
        WorkingTimeAgreement wta = wtaRepository.findOne(wtaId);
        if (!Optional.ofNullable(wta).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_WTA_ID, wtaId);
        }
        if (checked) {
            List<WTABaseRuleTemplateDTO> wtaBaseRuleTemplates = ObjectMapperUtils.copyCollectionPropertiesByMapper(wtaBaseRuleTemplateRepository.findAllByIdIn(wta.getRuleTemplateIds()),WTABaseRuleTemplateDTO.class);
            WTADTO wtadto = new WTADTO(COPY_OF + wta.getName(), wta.getDescription(), wta.getExpertise().getId(), wta.getStartDate(), wta.getEndDate() == null ? null : wta.getEndDate(), wtaBaseRuleTemplates, wtaBasicDetailsDTO.getOrganizationType().getId(), wtaBasicDetailsDTO.getOrganizationSubType().getId());
            WTAResponseDTO wtaResponseDTO = createWta(wta.getCountryId(), wtadto, true, true);
            map.put("wta", wtaResponseDTO);
            map.put("ruleTemplate", wtaResponseDTO.getRuleTemplates());
        } else {
            wta.setDeleted(true);
            wtaRepository.save(wta);
        }
        return map;

    }


    public WTADefaultDataInfoDTO getDefaultWtaInfo(Long countryId) {
        List<ActivityDTO> activityDTOS = activityMongoRepository.findByDeletedFalseAndCountryId(countryId);
        List<TimeTypeDTO> timeTypeDTOS = timeTypeService.getAllTimeType(null, countryId);
        List<PresenceTypeDTO> plannedTimeTypes=plannedTimeTypeService.getAllPresenceTypeByCountry(countryId);
        List<DayTypeDTO> dayTypeDTOS=dayTypeService.getDayTypeWithCountryHolidayCalender(countryId);
        WTADefaultDataInfoDTO wtaDefaultDataInfoDTO = new WTADefaultDataInfoDTO();
        wtaDefaultDataInfoDTO.setTimeTypes(timeTypeDTOS);
        wtaDefaultDataInfoDTO.setActivityList(activityDTOS);
        wtaDefaultDataInfoDTO.setPresenceTypes(plannedTimeTypes);
        wtaDefaultDataInfoDTO.setDayTypes(dayTypeDTOS);
        return wtaDefaultDataInfoDTO;
    }

    public WTADefaultDataInfoDTO getDefaultWtaInfoForUnit(Long unitId) {
        WTADefaultDataInfoDTO wtaDefaultDataInfoDTO = new WTADefaultDataInfoDTO();
        List<ActivityDTO> activities = activityMongoRepository.findByDeletedFalseAndUnitId(unitId);
        List<PresenceTypeDTO> presenceTypeDTOS=plannedTimeTypeService.getAllPresenceTypeByCountry(UserContext.getUserDetails().getCountryId());
        Map<String, Set<FieldLevelPermission>> fieldPermissionMap = activityPermissionService.getActivityPermissionMap(unitId,UserContext.getUserDetails().getId());
        activities.forEach(activity->{
            if(fieldPermissionMap.get("name").contains(FieldLevelPermission.HIDE) || fieldPermissionMap.get("name").isEmpty()){
                activity.setName("XXXXX");
            }
        });
        List<TimeTypeDTO> timeTypeDTOS = timeTypeService.getAllTimeType(null, UserContext.getUserDetails().getCountryId());
        List<TimeSlotDTO> timeSlotDTOS=timeSlotSetService.getShiftPlanningTimeSlotByUnit(unitId);
        wtaDefaultDataInfoDTO.setDayTypes(dayTypeService.getDayTypeWithCountryHolidayCalender(UserContext.getUserDetails().getCountryId()));
        wtaDefaultDataInfoDTO.setTimeTypes(timeTypeDTOS);
        wtaDefaultDataInfoDTO.setActivityList(activities);
        wtaDefaultDataInfoDTO.setTimeSlots(timeSlotDTOS);
        wtaDefaultDataInfoDTO.setPresenceTypes(presenceTypeDTOS);
        return wtaDefaultDataInfoDTO;
    }

    public CTAWTAAndAccumulatedTimebankWrapper getWTACTAByEmploymentIds(Set<Long> employmentIds) {
        List<WTAQueryResultDTO> wtaQueryResultDTOS = wtaRepository.getAllWTAByUpIds(employmentIds, getDate());
        List<WTAResponseDTO> wtaResponseDTOS = ObjectMapperUtils.copyCollectionPropertiesByMapper(wtaQueryResultDTOS, WTAResponseDTO.class);
        List<CTAResponseDTO> ctaResponseDTOS = costTimeAgreementService.getCTAByEmploymentIds(employmentIds);
        return new CTAWTAAndAccumulatedTimebankWrapper(ctaResponseDTOS, wtaResponseDTOS);
    }

    public CTAWTAAndAccumulatedTimebankWrapper getWTACTAByEmployment(Long employmentId, LocalDate startDate) {
        List<WorkingTimeAgreement> wtas = wtaRepository.getWTAByEmployment(employmentId);
        List<CostTimeAgreement> ctas = costTimeAgreementRepository.getCTAByEmployment(employmentId);
        CTAWTAAndAccumulatedTimebankWrapper ctawtaAndAccumulatedTimebankWrapper = new CTAWTAAndAccumulatedTimebankWrapper();
        if (isCollectionNotEmpty(wtas)) {
            List<WTAResponseDTO> wtaResponseDTOS = wtas.stream().map(wta->new WTAResponseDTO(wta.getName(), wta.getId(), wta.getParentId())).collect(Collectors.toList());
            ctawtaAndAccumulatedTimebankWrapper.setWta(wtaResponseDTOS);
        }
        if (isCollectionNotEmpty(ctas)) {
            List<CTAResponseDTO> ctaResponseDTOS = ctas.stream().map(cta->new CTAResponseDTO(cta.getName(), cta.getId(), cta.getParentId())).collect(Collectors.toList());
            ctawtaAndAccumulatedTimebankWrapper.setCta(ctaResponseDTOS);
        }
        return ctawtaAndAccumulatedTimebankWrapper;
    }

    public WTATableSettingWrapper getWTAWithVersionIds(Long unitId, List<Long> employmentIds) {
        Long countryId = userIntegrationService.getCountryIdOfOrganization(unitId);
        List<WTAQueryResultDTO> currentWTAList = wtaRepository.getAllParentWTAByIds(employmentIds);
        List<WTAQueryResultDTO> versionsOfWTAs = wtaRepository.getWTAWithVersionIds(employmentIds);
        WorkingTimeAgreement orgWTA = null;
        if(isCollectionNotEmpty(currentWTAList)) {
            orgWTA = wtaRepository.findOne(currentWTAList.get(0).getOrganizationParentId());
        }
        List<WTAResponseDTO> parentWTA = ObjectMapperUtils.copyCollectionPropertiesByMapper(currentWTAList, WTAResponseDTO.class);
        Map<Long, List<WTAQueryResultDTO>> verionWTAMap = versionsOfWTAs.stream().collect(Collectors.groupingBy(k -> k.getEmploymentId(), Collectors.toList()));
        WorkingTimeAgreement finalOrgWTA = orgWTA;
        parentWTA.forEach(currentWTA -> {

            ruleTemplateService.assignCategoryToRuleTemplate(countryId, currentWTA.getRuleTemplates());
            if (isCollectionNotEmpty(versionsOfWTAs)) {
                List<WTAResponseDTO> versionWTAs = ObjectMapperUtils.copyCollectionPropertiesByMapper(verionWTAMap.get(currentWTA.getEmploymentId()), WTAResponseDTO.class);
                if(isNotNull(finalOrgWTA)) {
                    versionWTAs.forEach(wtaResponseDTO -> wtaResponseDTO.setTranslations(finalOrgWTA.getTranslations()));
                }
                currentWTA.setVersions(versionWTAs);
            }
            currentWTA.setTranslations(finalOrgWTA.getTranslations());
        });
        TableConfiguration tableConfiguration = tableSettingService.getTableConfigurationByTabId(unitId, ORGANIZATION_AGREEMENT_VERSION_TABLE_ID);
        return new WTATableSettingWrapper(parentWTA, tableConfiguration);
    }


    public CTAWTAAndAccumulatedTimebankWrapper assignCTAWTAToEmployment(Long employmentId, Long unitId, BigInteger wtaId, BigInteger ctaId, LocalDate startDate) {
        CTAWTAAndAccumulatedTimebankWrapper ctawtaAndAccumulatedTimebankWrapper = new CTAWTAAndAccumulatedTimebankWrapper();
        if (wtaId != null) {
            WTAResponseDTO wtaResponseDTO = assignWTAToEmployment(employmentId, unitId, wtaId, startDate);
            ctawtaAndAccumulatedTimebankWrapper.setWta(Arrays.asList(wtaResponseDTO));
        }
        if (ctaId != null) {
            CTAResponseDTO ctaResponseDTO = costTimeAgreementService.assignCTAToEmployment(employmentId, ctaId, startDate);
            ctawtaAndAccumulatedTimebankWrapper.setCta(Arrays.asList(ctaResponseDTO));
        }
        return ctawtaAndAccumulatedTimebankWrapper;

    }

    public List<WTAResponseDTO> getWTAOfEmployment(Long employmentId) {
        /*List<WTAQueryResultDTO> wtaQueryResultDTOS = wtaRepository.getWTAWithVersionIds(newArrayList(employmentId));
        List<WTAResponseDTO> wtaResponseDTOS = ObjectMapperUtils.copyCollectionPropertiesByMapper(wtaQueryResultDTOS, WTAResponseDTO.class);
        wtaResponseDTOS.addAll();*/
        return ObjectMapperUtils.copyCollectionPropertiesByMapper(wtaRepository.getAllParentWTAByIds(newArrayList(employmentId)), WTAResponseDTO.class);
    }


    private WTAResponseDTO assignWTAToEmployment(Long employmentId, Long unitId, BigInteger wtaId, LocalDate startLocalDate) {
        WTAQueryResultDTO wtaQueryResultDTO = wtaRepository.getOne(wtaId);
        if (!Optional.ofNullable(wtaQueryResultDTO).isPresent()) {
            exceptionService.duplicateDataException(MESSAGE_WTA_ID, wtaId);
        }
        OrganizationDTO organizationDTO = userIntegrationService.getOrganizationWithCountryId(unitId);
        WTAResponseDTO wtaResponseDTO = ObjectMapperUtils.copyPropertiesByMapper(wtaQueryResultDTO, WTAResponseDTO.class);
        WorkingTimeAgreement workingTimeAgreement = ObjectMapperUtils.copyPropertiesByMapper(wtaResponseDTO, WorkingTimeAgreement.class);
        List<WTABaseRuleTemplate> ruleTemplates = new ArrayList<>();
        Map<String, BigInteger> activitiesIdsAndUnitIdsMap = ruleTemplateService.getActivityMapWithUnitId(wtaResponseDTO.getRuleTemplates(), newArrayList(unitId));
        if (CollectionUtils.isNotEmpty(wtaResponseDTO.getRuleTemplates())) {
            ruleTemplates = wtaBuilderService.copyRuleTemplatesWithUpdateActivity(activitiesIdsAndUnitIdsMap, unitId, wtaResponseDTO.getRuleTemplates(), true);
            for (WTABaseRuleTemplate ruleTemplate : ruleTemplates) {
                updateExistingPhaseIdOfWTA(ruleTemplate.getPhaseTemplateValues(), unitId, organizationDTO.getCountryId(), true);
            }
            wtaBaseRuleTemplateRepository.saveEntities(ruleTemplates);
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

        wtaRepository.save(workingTimeAgreement);
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
                wtaBaseRuleTemplateRepository.saveEntities(ruleTemplates);
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
            wtaRepository.saveEntities(workingTimeAgreements);
        }
        return true;
    }

    public WTAResponseDTO updateWtaOfEmployment(Long unitId, WTADTO wtadto, Boolean oldEmploymentPublished,Boolean save) {
        Optional<WorkingTimeAgreement> oldWta = wtaRepository.findById(wtadto.getId());
        if (!Optional.ofNullable(oldWta).isPresent()) {
            logger.info("wta not found while updating at unit {}", wtadto.getId());
            exceptionService.dataNotFoundByIdException(MESSAGE_WTA_ID, wtadto.getId());
        }
        validateEmploymentCTAWhileUpdate(wtadto,oldEmploymentPublished,oldWta.get());
        WTAResponseDTO wtaResponseDTO = null;
        List<WTABaseRuleTemplate> wtaBaseRuleTemplates = new ArrayList<>();
        if (isCollectionNotEmpty(wtadto.getRuleTemplates())) {
            wtaBaseRuleTemplates = wtaBuilderService.copyRuleTemplates(wtadto.getRuleTemplates(), false);
        }
        boolean calculatedValueChanged = isCalculatedValueChangedForWTA(oldWta.get(), wtaBaseRuleTemplates);
        if(calculatedValueChanged && isNull(wtadto.getPublishDate())){
            exceptionService.actionNotPermittedException(ERROR_VALUE_CHANGED_PUBLISH_DATE_NULL,"WTA");
        }
        if (!oldEmploymentPublished || isNull(wtadto.getPublishDate())) {
            wtaResponseDTO = updateWTAOfUnpublishedEmployment(oldWta.get(), wtadto, unitId);
            wtaRepository.save(oldWta.get());
        }else {
            if (!calculatedValueChanged) {
                exceptionService.actionNotPermittedException(MESSAGE_CTA_VALUE,"WTA");
            } else {
                wtaResponseDTO = updateWTAOfPublishedEmployment(oldWta.get(), wtadto, unitId, save);
            }
        }
        return wtaResponseDTO;
    }

    private WTAResponseDTO updateWTAOfUnpublishedEmployment(WorkingTimeAgreement oldWta, WTADTO updateDTO, Long unitId) {
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
            wtaBaseRuleTemplateRepository.saveEntities(ruleTemplates);
            List<BigInteger> ruleTemplatesIds = ruleTemplates.stream().map(ruleTemplate -> ruleTemplate.getId()).collect(Collectors.toList());
            oldWta.setRuleTemplateIds(ruleTemplatesIds);
        }
        oldWta.setEndDate(isNotNull(updateDTO.getEndDate()) ? updateDTO.getEndDate() : null);
        WTAResponseDTO wtaResponseDTO = ObjectMapperUtils.copyPropertiesByMapper(oldWta, WTAResponseDTO.class);
        wtaResponseDTO.setRuleTemplates(WTABuilderService.copyRuleTemplatesToDTO(ruleTemplates));
        return wtaResponseDTO;
    }

    private void validateEmploymentCTAWhileUpdate(WTADTO wtadto, Boolean oldEmploymentPublished, WorkingTimeAgreement oldWTA){
        if ((wtadto.getEmploymentEndDate() != null && wtadto.getEndDate() != null && wtadto.getEndDate().isBefore(wtadto.getEmploymentEndDate())) || (isNull(oldWTA.getEndDate()) && isNull(wtadto.getEmploymentEndDate()) && isNotNull(wtadto.getEndDate()))) {
            exceptionService.actionNotPermittedException(END_DATE_FROM_END_DATE, "WTA");
        }
        if (wtadto.getEmploymentEndDate() != null && wtadto.getStartDate().isAfter(wtadto.getEmploymentEndDate())) {
            exceptionService.actionNotPermittedException(START_DATE_FROM_END_DATE, "WTA");
        }
        if (wtadto.getEmploymentEndDate() != null && wtadto.getStartDate().isAfter(wtadto.getEmploymentEndDate())) {
            exceptionService.actionNotPermittedException(START_DATE_FROM_END_DATE, wtadto.getStartDate(), wtadto.getEmploymentEndDate());
        }
        if(oldEmploymentPublished){
            if(isNotNull(wtadto.getPublishDate()) && wtadto.getPublishDate().isBefore(LocalDate.now())){
                exceptionService.actionNotPermittedException(PUBLISH_DATE_SHOULD_BE_IN_FUTURE);
            }
            else if(isNotNull(wtadto.getPublishDate())){
                validateCtaOnUpdateEmploymentCta(oldWTA.getEmploymentId(),wtadto.getPublishDate(),wtadto.getId());
            }
            else if (!oldWTA.getStartDate().equals(wtadto.getStartDate())){
                exceptionService.actionNotPermittedException(STARTDATE_CANNOT_CHANGE,"WTA");
            }
            else if(isNotNull(oldWTA.getEndDate()) && !oldWTA.getEndDate().equals(wtadto.getEndDate())){
                validateCtaOnUpdateEmploymentCta(oldWTA.getEmploymentId(),wtadto.getEndDate(),wtadto.getId());
                validateGapBetweenCTA(wtadto, oldWTA.getEmploymentId());
            }
        }
    }

    private void validateGapBetweenCTA(WTADTO wtadto, Long employementId) {
        boolean gapExists = workingTimeAgreementMongoRepository.isGapExistsInEmploymentWTA(employementId,wtadto.getEndDate(),wtadto.getId());
        if (gapExists){
            exceptionService.actionNotPermittedException(ERROR_NO_GAP, "WTA");
        }
    }

    private void validateCtaOnUpdateEmploymentCta(Long employementId,LocalDate date,BigInteger wtaId) {
        boolean notValid = workingTimeAgreementMongoRepository.isEmploymentWTAExistsOnDate(employementId,date,wtaId);
        if (notValid) {
            exceptionService.duplicateDataException("error.cta.invalid", date, "");
        }
    }


    public List<CTAWTAResponseDTO> copyWtaCTA(List<EmploymentIdDTO> employmentIds) {
        logger.info("Inside wtaservice");
        List<Long> oldEmploymentIds = employmentIds.stream().map(employmentIdDTO -> employmentIdDTO.getOldEmploymentId()).collect(Collectors.toList());
        Map<Long, Long> newOldemploymentIdMap = employmentIds.stream().collect(toMap(k -> k.getOldEmploymentId(), v -> v.getNewEmploymentId()));
        List<WTAQueryResultDTO> oldWtas = wtaRepository.getWTAByEmploymentIds(oldEmploymentIds, DateUtils.getDate());
        List<WorkingTimeAgreement> newWtas = new ArrayList<>();
        for (WTAQueryResultDTO wta : oldWtas) {
            List<WTABaseRuleTemplate> ruleTemplates = wta.getRuleTemplates();
            for (WTABaseRuleTemplate wtaBaseRuleTemplate : ruleTemplates) {
                wtaBaseRuleTemplate.setId(null);
            }
            if (!ruleTemplates.isEmpty()) {
                wtaBaseRuleTemplateRepository.saveEntities(ruleTemplates);
            }
            List<BigInteger> ruleTemplateIds = ruleTemplates.stream().map(ruleTemplate -> ruleTemplate.getId()).collect(Collectors.toList());

            WorkingTimeAgreement wtaDB = ObjectMapperUtils.copyPropertiesByMapper(wta, WorkingTimeAgreement.class);
            wtaDB.setEmploymentId(newOldemploymentIdMap.get(wta.getEmploymentId()));
            wtaDB.setRuleTemplateIds(ruleTemplateIds);
            wtaDB.setId(null);
            newWtas.add(wtaDB);
        }
        if (!newWtas.isEmpty()) {
            wtaRepository.saveEntities(newWtas);
        }
        Map<Long, CostTimeAgreement> ctaMap = costTimeAgreementService.updateWTACTA(oldEmploymentIds,newOldemploymentIdMap);
        List<CTAWTAResponseDTO> ctaWtas = new ArrayList<>();
        for (WorkingTimeAgreement wta : newWtas) {
            CTAWTAResponseDTO ctaWTAResponseDTO = new CTAWTAResponseDTO( ctaMap.get(wta.getEmploymentId()).getId(),
                    ctaMap.get(wta.getEmploymentId()).getName(),wta.getId(), wta.getName(), wta.getEmploymentId());
            ctaWtas.add(ctaWTAResponseDTO);
        }
        return ctaWtas;
    }

    public CTAWTAAndAccumulatedTimebankWrapper assignCTAWTAToEmployment(Long employmentId, Long unitId, BigInteger wtaId, BigInteger oldwtaId, BigInteger ctaId, BigInteger oldctaId, LocalDate startDate) {
        CTAWTAAndAccumulatedTimebankWrapper ctawtaAndAccumulatedTimebankWrapper = new CTAWTAAndAccumulatedTimebankWrapper();
        if (wtaId != null) {
            WTAResponseDTO wtaResponseDTO = assignWTAToEmployment(employmentId, unitId, wtaId, startDate);
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
        if(!wtaRepository.existsOngoingWTAByEmployment(employmentId,asDate(endDate))) {
            wtaRepository.setEndDateToWTAOfEmployment(employmentId, endDate);
        }
        if(!costTimeAgreementRepository.existsOngoingCTAByEmployment(employmentId,asDate(endDate))) {
            costTimeAgreementRepository.setEndDateToCTAOfEmployment(employmentId, endDate);
        }
        return true;
    }

    private WTAResponseDTO updateWTAOfPublishedEmployment(WorkingTimeAgreement oldWta, WTADTO wtadto, Long unitId,Boolean save) {
        List<WTABaseRuleTemplate> wtaBaseRuleTemplates = new ArrayList<>();
        if (isCollectionNotEmpty(wtadto.getRuleTemplates())) {
            wtaBaseRuleTemplates = wtaBuilderService.copyRuleTemplates(wtadto.getRuleTemplates(), false);
        }
        return getcreateVersionOfPersionalisedWTA(oldWta, wtadto, unitId, wtaBaseRuleTemplates);
    }

    private WTAResponseDTO getcreateVersionOfPersionalisedWTA(WorkingTimeAgreement oldWta, WTADTO wtadto, Long unitId, List<WTABaseRuleTemplate> wtaBaseRuleTemplates){
        WTAResponseDTO wtaResponseDTO;
        OrganizationDTO organization = userIntegrationService.getOrganizationWithCountryId(unitId);
        if (!Optional.ofNullable(organization).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_UNIT_ID, unitId);
        }
        LocalDate publishDate = wtadto.getPublishDate();
        wtadto.setPublishDate(null);
        WorkingTimeAgreement newWta = ObjectMapperUtils.copyPropertiesByMapper(oldWta, WorkingTimeAgreement.class);
        newWta.setOrganizationParentId(oldWta.getOrganizationParentId());
        newWta.setStartDate(publishDate);
        newWta.setRuleTemplateIds(null);
        oldWta.setEndDate(publishDate.equals(oldWta.getStartDate()) ? oldWta.getStartDate() : publishDate.minusDays(1));
        oldWta.setId(null);
        if (isCollectionNotEmpty(wtadto.getRuleTemplates())) {
            wtaBaseRuleTemplates = wtaBuilderService.copyRuleTemplates(wtadto.getRuleTemplates(), true);
            List<DurationBetweenShiftsWTATemplate> durationBetweenShiftsWTATemplates = new ArrayList<>();
            for (WTABaseRuleTemplate ruleTemplate : wtaBaseRuleTemplates) {
                if(ruleTemplate instanceof DurationBetweenShiftsWTATemplate){
                    durationBetweenShiftsWTATemplates.add((DurationBetweenShiftsWTATemplate)ruleTemplate);
                }
                updateExistingPhaseIdOfWTA(ruleTemplate.getPhaseTemplateValues(), unitId, organization.getCountryId(), true);
            }
            wtaBaseRuleTemplateRepository.saveEntities(wtaBaseRuleTemplates);
            shiftHelperService.updateRestingHoursInShiftsOnWtaUpdate(newWta,durationBetweenShiftsWTATemplates);
            List<BigInteger> ruleTemplatesIds = wtaBaseRuleTemplates.stream().map(ruleTemplate -> ruleTemplate.getId()).collect(Collectors.toList());
            newWta.setRuleTemplateIds(ruleTemplatesIds);
        }
        wtaRepository.save(oldWta);
        newWta.setParentId(oldWta.getId());
        wtaRepository.save(newWta);
        wtaResponseDTO = ObjectMapperUtils.copyPropertiesByMapper(newWta, WTAResponseDTO.class);
        wtaResponseDTO.setTranslations(newWta.getTranslations());
        WTAResponseDTO version = ObjectMapperUtils.copyPropertiesByMapper(oldWta, WTAResponseDTO.class);
        wtaResponseDTO.setParentId(oldWta.getId());
        List<WTABaseRuleTemplate> existingWtaBaseRuleTemplates = wtaBaseRuleTemplateRepository.findAllByIdInAndDeletedFalse(oldWta.getRuleTemplateIds());
        version.setRuleTemplates(WTABuilderService.copyRuleTemplatesToDTO(existingWtaBaseRuleTemplates));
        wtaResponseDTO.setVersions(newArrayList(version));
        wtaResponseDTO.setRuleTemplates(WTABuilderService.copyRuleTemplatesToDTO(wtaBaseRuleTemplates));
        return wtaResponseDTO;
    }

    public boolean isCalculatedValueChangedForWTA(WorkingTimeAgreement oldWorkingTimeAgreement, List<WTABaseRuleTemplate> wtaBaseRuleTemplates) {
        boolean isCalculatedValueChanged = false;
        if (oldWorkingTimeAgreement.getRuleTemplateIds().size() == wtaBaseRuleTemplates.size()) {
            List<WTABaseRuleTemplate> existingWtaBaseRuleTemplates = wtaBaseRuleTemplateRepository.findAllByIdInAndDeletedFalse(oldWorkingTimeAgreement.getRuleTemplateIds());
            Map<BigInteger, WTABaseRuleTemplate> existingWtaBaseRuleTemplateMap = existingWtaBaseRuleTemplates.stream().collect(toMap(k -> k.getId(), v -> v));
            for (WTABaseRuleTemplate wtaBaseRuleTemplate : wtaBaseRuleTemplates) {
                if (existingWtaBaseRuleTemplateMap.containsKey(wtaBaseRuleTemplate.getId())) {
                    WTABaseRuleTemplate existingWtaBaseRuleTemplate = existingWtaBaseRuleTemplateMap.get(wtaBaseRuleTemplate.getId());
                    isCalculatedValueChanged = existingWtaBaseRuleTemplate.isDisabled() != wtaBaseRuleTemplate.isDisabled() || existingWtaBaseRuleTemplate.isCalculatedValueChanged(wtaBaseRuleTemplate);
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


    public CTAWTAAndAccumulatedTimebankWrapper getEmploymentCtaWtaAndAccumulatedTimebank(Map<Long, List<EmploymentLinesDTO>> employmentLinesMap) {
        return getWTACTAByEmploymentIds(employmentLinesMap.keySet());
    }

    public WorkTimeAgreementBalance getWorktimeAgreementBalance(Long unitId, Long employmentId, LocalDate startDate, LocalDate endDate) {
        return workTimeAgreementBalancesCalculationService.getWorkTimeAgreementBalance(unitId, employmentId, startDate, endDate,new HashSet<>(),null);
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

    public WTAQueryResultDTO getWtaQueryResultDTOByDateAndEmploymentId(Long employmentId, Date startDate) {
        WTAQueryResultDTO wtaQueryResultDTO = getWTAByEmploymentIdAndDate(employmentId, startDate);
        if (!Optional.ofNullable(wtaQueryResultDTO).isPresent()) {
            exceptionService.actionNotPermittedException(MESSAGE_WTA_NOTFOUND);
        }
        return wtaQueryResultDTO;
    }

    public WTAQueryResultDTO getWTAByEmploymentIdAndDate(Long employmentId, Date date) {
        return wtaRepository.getWTAByEmploymentIdAndDate(employmentId, date);
    }

    public List<WTAQueryResultDTO> getWTAByEmploymentIdAndDates(Long employmentId, Date startDate, Date endDate) {
        return wtaRepository.getWTAByEmploymentIdAndDates(employmentId, startDate, endDate);
    }

    public List<WTAQueryResultDTO> getWTAByEmploymentIdAndDatesWithRuleTemplateType(Long employmentId, Date startDate, Date endDate, WTATemplateType templateType) {
        return wtaRepository.getWTAByEmploymentIdAndDatesWithRuleTemplateType(employmentId, startDate, endDate, templateType);
    }

    public StaffFilterDTO getWorkTimeAgreement(StaffFilterDTO staffFilterDTO, LocalDate startDate, LocalDate endDate) {
        Set<Long> staffIds = staffFilterDTO.getMapOfStaffAndEmploymentIds().keySet();
        Map<Long, Boolean> staffIdNightWorkerMap = nightWorkerService.getStaffIdAndNightWorkerMap(staffIds);
        Map<FilterType, Set<String>> filterTypeMap = staffFilterDTO.getFiltersData().stream().collect(Collectors.toMap(FilterSelectionDTO::getName, v -> v.getValue()));

        if(filterTypeMap.containsKey(NIGHT_WORKERS) && filterTypeMap.get(NIGHT_WORKERS).size() == 1){
            staffIds = filterTypeMap.get(NIGHT_WORKERS).contains(StaffWorkingType.NOT_NIGHT_WORKER.toString()) ? staffIdNightWorkerMap.keySet().stream().filter(k->!staffIdNightWorkerMap.get(k)).collect(Collectors.toSet()) : staffIdNightWorkerMap.keySet().stream().filter(k->staffIdNightWorkerMap.get(k)).collect(Collectors.toSet());
            staffIds.forEach(staffId->staffFilterDTO.getMapOfStaffAndEmploymentIds().remove(staffId));
        }
        if (staffFilterDTO.isValidFilterForShift()) {
            List<ShiftDTO> shiftDTOS = shiftMongoRepository.findAllByStaffIdsAndDeleteFalse(isCollectionEmpty(staffFilterDTO.getStaffIds()) ? staffIds : staffFilterDTO.getStaffIds(), startDate, endDate);
            shiftDTOS = shiftFilterService.getShiftsByFilters(shiftDTOS, staffFilterDTO,new ArrayList<>());
            staffIds = shiftDTOS.stream().map(shiftDTO -> shiftDTO.getStaffId()).collect(Collectors.toSet());
            staffIds.forEach(staffId->staffFilterDTO.getMapOfStaffAndEmploymentIds().remove(staffId));
        }
        Set<Long> filteredStaffIds = costTimeAgreementService.filterStaffByCTATemplateAccountType(staffFilterDTO, staffIds, filterTypeMap);
        staffFilterDTO.setStaffIds(new ArrayList<>(filteredStaffIds));
        staffFilterDTO.setNightWorkerDetails(staffIdNightWorkerMap.entrySet().stream().filter(x->filteredStaffIds.contains(x.getKey())).collect(Collectors.toMap(Map.Entry::getKey,Map.Entry::getValue)));
        if(staffFilterDTO.isIncludeWorkTimeAgreement()) {
            List<WTAQueryResultDTO> wtaQueryResultDTOS = workingTimeAgreementMongoRepository.getAllWTAByEmploymentIds(staffFilterDTO.getMapOfStaffAndEmploymentIds().values().stream().flatMap(employmentIds -> employmentIds.stream()).collect(Collectors.toList()));
            List<WTAResponseDTO> wtaResponseDTOS = copyCollectionPropertiesByMapper(wtaQueryResultDTOS, WTAResponseDTO.class);
            Map<Long, List<WTAResponseDTO>> employmentIdAndwtaQueryResultDTOSMap = wtaResponseDTOS.stream().collect(Collectors.groupingBy(wtaQueryResultDTO -> wtaQueryResultDTO.getEmploymentId()));
            staffFilterDTO.setEmploymentIdAndWtaResponseMap(employmentIdAndwtaQueryResultDTOSMap);
        }
        return staffFilterDTO;
    }

    public boolean updateDatesInCTAWTA(Long unitId){
        List<EmploymentWithCtaDetailsDTO> employmentDTOS = userIntegrationService.getAllEmploymentByUnitId(unitId);
        Set<Long> employmentIds = employmentDTOS.stream().map(employmentWithCtaDetailsDTO -> employmentWithCtaDetailsDTO.getId()).collect(Collectors.toSet());
        List<WorkingTimeAgreement> workingTimeAgreements = workingTimeAgreementMongoRepository.findWTAOfEmployments(employmentIds);
        List<CostTimeAgreement> costTimeAgreements = costTimeAgreementRepository.findCTAOfEmployments(employmentIds);
        Map<Long,List<WorkingTimeAgreement>> workTimeAgreementMap = workingTimeAgreements.stream().collect(Collectors.groupingBy(WorkingTimeAgreement::getEmploymentId));
        Map<Long,List<CostTimeAgreement>> costTimeAgreementMap = costTimeAgreements.stream().collect(Collectors.groupingBy(CostTimeAgreement::getEmploymentId));
        for (EmploymentWithCtaDetailsDTO employmentDTO : employmentDTOS) {
            List<EmploymentLinesDTO> employmentLinesDTOS = employmentDTO.getEmploymentLines();
            Collections.sort(employmentLinesDTOS);
            List<CostTimeAgreement> costTimeAgreements1 = costTimeAgreementMap.getOrDefault(employmentDTO.getId(),new ArrayList<>());
            Collections.sort(costTimeAgreements1);
            List<WorkingTimeAgreement> workTimeAgreements1 = workTimeAgreementMap.getOrDefault(employmentDTO.getId(),new ArrayList<>());
            Collections.sort(workTimeAgreements1);
            costTimeAgreementService.updateDatesOnCTA(costTimeAgreements1);
            updateDatesOnWTA(workTimeAgreements1);
            for (EmploymentLinesDTO employmentLinesDTO : employmentLinesDTOS) {
                costTimeAgreementService.updateCTADates(costTimeAgreements1, employmentLinesDTO);
                updateWTADates(workTimeAgreements1, employmentLinesDTO);
            }
            StaffAdditionalInfoDTO staffAdditionalInfoDTO = userIntegrationService.verifyUnitEmploymentOfStaffByEmploymentId(unitId, null, ORGANIZATION, employmentDTO.getId(), new HashSet<>(),null);
            staffAdditionalInfoDTO.setDayTypes(dayTypeService.getDayTypeWithCountryHolidayCalender(UserContext.getUserDetails().getCountryId()));
            costTimeAgreementRepository.saveEntities(costTimeAgreements);
            for (CostTimeAgreement costTimeAgreement : costTimeAgreements) {
                CTAResponseDTO ctaResponseDTO = costTimeAgreementRepository.findCTAById(costTimeAgreement.getId());
                staffAdditionalInfoDTO.getEmployment().setCostTimeAgreement(ctaResponseDTO);
                timeBankService.updateDailyTimeBankOnCTAChangeOfEmployment(staffAdditionalInfoDTO, ctaResponseDTO);
            }
            workingTimeAgreementMongoRepository.saveEntities(workingTimeAgreements);
        }
        return true;
    }

    private void updateDatesOnWTA(List<WorkingTimeAgreement> workTimeAgreements1) {
        for (int i = 1; i < workTimeAgreements1.size(); i++) {
            WorkingTimeAgreement first = workTimeAgreements1.get(i-1);
            WorkingTimeAgreement second = workTimeAgreements1.get(i);
            first.setEndDate(second.getStartDate().minusDays(1));
            if(first.getStartDate().equals(second.getStartDate())){
                first.setEndDate(first.getStartDate());
                second.setStartDate(first.getEndDate().plusDays(1));
            }
        }
    }

    private void updateWTADates(List<WorkingTimeAgreement> workTimeAgreements1, EmploymentLinesDTO employmentLinesDTO) {
        for (WorkingTimeAgreement workingTimeAgreement : workTimeAgreements1) {
            if(workingTimeAgreement.getStartDate().isAfter(employmentLinesDTO.getStartDate())){
                workingTimeAgreement.setStartDate(employmentLinesDTO.getStartDate());
            }
            break;
        }
        if(isNull(employmentLinesDTO.getEndDate()) && !workTimeAgreements1.isEmpty()){
            workTimeAgreements1.get(workTimeAgreements1.size()-1).setEndDate(null);
        }else {
            for (WorkingTimeAgreement workingTimeAgreement : workTimeAgreements1) {
                if(isNull(workingTimeAgreement.getEndDate())){
                    break;
                }else if(workingTimeAgreement.getEndDate().isBefore(employmentLinesDTO.getEndDate())){
                    workingTimeAgreement.setEndDate(employmentLinesDTO.getEndDate());
                    break;
                }
            }
        }
    }

    public Map<String, TranslationInfo> updateTranslation(BigInteger wtaId, Map<String,TranslationInfo> translations) {
        WorkingTimeAgreement workingTimeAgreement= workingTimeAgreementMongoRepository.findOne(wtaId);
        workingTimeAgreement.setTranslations(translations);
        workingTimeAgreementMongoRepository.save(workingTimeAgreement);
        return workingTimeAgreement.getTranslations();
    }

    public Map<String, TranslationInfo> updateTranslationRuleTemplates(BigInteger wtaId, Map<String,TranslationInfo> translations) {
        WTABaseRuleTemplate wtaBaseRuleTemplate= wtaBaseRuleTemplateRepository.findOne(wtaId);
        wtaBaseRuleTemplate.setTranslations(translations);
        wtaBaseRuleTemplateRepository.save(wtaBaseRuleTemplate);
        return wtaBaseRuleTemplate.getTranslations();
    }
}
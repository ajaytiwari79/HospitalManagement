package com.kairos.service.organization;

import com.kairos.commons.custom_exception.DataNotFoundByIdException;
import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.constants.AppConstants;
import com.kairos.dto.TranslationInfo;
import com.kairos.dto.activity.activity.ActivityDTO;
import com.kairos.dto.activity.activity.ActivityWithTimeTypeDTO;
import com.kairos.dto.activity.activity.activity_tabs.*;
import com.kairos.dto.activity.activity.activity_tabs.communication_tab.ActivityReminderSettings;
import com.kairos.dto.activity.activity.activity_tabs.communication_tab.CommunicationActivityDTO;
import com.kairos.dto.activity.activity.activity_tabs.communication_tab.FrequencySettings;
import com.kairos.dto.activity.common.OrderAndActivityDTO;
import com.kairos.dto.activity.counter.configuration.CounterDTO;
import com.kairos.dto.activity.counter.enums.ModuleType;
import com.kairos.dto.activity.open_shift.OpenShiftIntervalDTO;
import com.kairos.dto.activity.phase.PhaseDTO;
import com.kairos.dto.activity.presence_type.PresenceTypeDTO;
import com.kairos.dto.activity.presence_type.PresenceTypeWithTimeTypeDTO;
import com.kairos.dto.activity.shift.ShiftActivityDTO;
import com.kairos.dto.activity.shift.ShiftDTO;
import com.kairos.dto.activity.time_type.TimeTypeDTO;
import com.kairos.dto.activity.unit_settings.TAndAGracePeriodSettingDTO;
import com.kairos.dto.activity.unit_settings.UnitSettingDTO;
import com.kairos.dto.activity.unit_settings.activity_configuration.ActivityRankingDTO;
import com.kairos.dto.user.access_permission.AccessGroupRole;
import com.kairos.dto.user.country.agreement.cta.cta_response.ActivityCategoryDTO;
import com.kairos.dto.user.country.agreement.cta.cta_response.DayTypeDTO;
import com.kairos.dto.user.country.agreement.cta.cta_response.EmploymentTypeDTO;
import com.kairos.dto.user.country.day_type.DayTypeEmploymentTypeWrapper;
import com.kairos.dto.user.country.tag.TagDTO;
import com.kairos.dto.user.organization.OrgTypeAndSubTypeDTO;
import com.kairos.dto.user.organization.OrganizationDTO;
import com.kairos.dto.user.organization.OrganizationTypeDTO;
import com.kairos.dto.user.organization.SelfRosteringMetaData;
import com.kairos.dto.user_context.UserContext;
import com.kairos.enums.*;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.activity.ActivityWrapper;
import com.kairos.persistence.model.activity.TimeType;
import com.kairos.persistence.model.activity.tabs.*;
import com.kairos.persistence.model.activity.tabs.rules_activity_tab.ActivityRulesSettings;
import com.kairos.persistence.model.common.MongoBaseEntity;
import com.kairos.persistence.model.phase.Phase;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.model.shift.ShiftActivity;
import com.kairos.persistence.model.unit_settings.ActivityRanking;
import com.kairos.persistence.repository.activity.ActivityCategoryRepository;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.counter.CounterRepository;
import com.kairos.persistence.repository.open_shift.OpenShiftIntervalRepository;
import com.kairos.persistence.repository.period.PlanningPeriodMongoRepository;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.persistence.repository.tag.TagMongoRepository;
import com.kairos.persistence.repository.time_type.TimeTypeMongoRepository;
import com.kairos.persistence.repository.unit_settings.UnitSettingRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.activity.*;
import com.kairos.service.auto_gap_fill_settings.AutoFillGapSettingsService;
import com.kairos.service.counter.CounterDistService;
import com.kairos.service.counter.KPISetService;
import com.kairos.service.cta.CostTimeAgreementService;
import com.kairos.service.day_type.CountryHolidayCalenderService;
import com.kairos.service.day_type.DayTypeService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.open_shift.OpenShiftRuleTemplateService;
import com.kairos.service.open_shift.OrderService;
import com.kairos.service.period.PeriodSettingsService;
import com.kairos.service.period.PlanningPeriodService;
import com.kairos.service.phase.PhaseService;
import com.kairos.service.priority_group.PriorityGroupService;
import com.kairos.service.reason_code.ReasonCodeService;
import com.kairos.service.scheduler_service.ActivitySchedulerJobService;
import com.kairos.service.shift.ShiftService;
import com.kairos.service.shift.ShiftTemplateService;
import com.kairos.service.time_slot.TimeSlotSetService;
import com.kairos.service.unit_settings.*;
import com.kairos.service.wta.WorkTimeAgreementService;
import com.kairos.wrapper.activity.ActivitySettingsWrapper;
import com.kairos.wrapper.activity.ActivityTagDTO;
import com.kairos.wrapper.activity.ActivityWithCompositeDTO;
import com.kairos.wrapper.activity.ActivityWithSelectedDTO;
import com.kairos.wrapper.phase.PhaseActivityDTO;
import com.kairos.wrapper.shift.ActivityWithUnitIdDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.CommonsExceptionUtil.convertMessage;
import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.constants.ActivityMessagesConstants.*;
import static com.kairos.constants.AppConstants.ACTIVITY_TYPE_IMAGE_PATH;
import static com.kairos.enums.ActivityStateEnum.PUBLISHED;
import static com.kairos.enums.TimeTypeEnum.PRESENCE;
import static com.kairos.enums.phase.PhaseDefaultName.TIME_ATTENDANCE;
import static com.kairos.enums.reason_code.ReasonCodeType.ORDER;
import static com.kairos.persistence.repository.activity.ActivityConstants.INVALID_ID;


/**
 * Created by vipul on 5/12/17.
 */
@Service
@Transactional
public class OrganizationActivityService {

    @Inject
    private ActivityMongoRepository activityMongoRepository;
    @Inject
    private ActivityRankingService activityRankingService;
    @Inject @Lazy
    private ActivityService activityService;
    @Inject
    private TagMongoRepository tagMongoRepository;
    @Inject
    private TimeTypeService timeTypeService;
    @Inject
    private ActivityCategoryRepository activityCategoryRepository;
    @Inject
    private OrderService orderService;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private PhaseService phaseService;
    @Inject
    private OpenShiftIntervalRepository openShiftIntervalRepository;
    @Inject
    private PlannedTimeTypeService plannedTimeTypeService;
    @Inject
    private PeriodSettingsService periodSettingsService;
    @Inject
    private PhaseSettingsService phaseSettingsService;
    @Inject
    private UnitSettingRepository unitSettingRepository;
    @Inject
    private UnitSettingService unitSettingService;
    @Inject
    private UserIntegrationService userIntegrationService;
    @Inject
    private ActivityConfigurationService activityConfigurationService;
    @Inject
    private TimeAttendanceGracePeriodService timeAttendanceGracePeriodService;
    @Inject
    private PriorityGroupService priorityGroupService;
    @Inject
    private CounterRepository counterRepository;
    @Inject
    private WorkTimeAgreementService workTimeAgreementService;
    @Inject
    private CostTimeAgreementService costTimeAgreementService;
    @Inject
    private TimeTypeMongoRepository timeTypeMongoRepository;
    @Inject
    private OpenShiftRuleTemplateService openShiftRuleTemplateService;
    @Inject
    private KPISetService kpiSetService;
    @Inject
    private ProtectedDaysOffService protectedDaysOffService;
    @Inject
    private ShiftService shiftService;
    @Inject
    private CounterDistService counterDistService;
    @Inject
    private ActivitySchedulerJobService activitySchedulerJobService;
    @Inject
    private ShiftTemplateService shiftTemplateService;
    @Inject
    private PlanningPeriodService planningPeriodService;
    @Inject
    private PlanningPeriodMongoRepository planningPeriodMongoRepository;
    @Inject
    private ActivitySettingsService activitySettingsService;
    @Inject
    private DayTypeService dayTypeService;
    @Inject
    private CountryHolidayCalenderService countryHolidayCalenderService;
    @Inject
    private ReasonCodeService reasonCodeService;
    @Inject
    private TimeSlotSetService timeSlotSetService;
    @Inject private ShiftMongoRepository shiftMongoRepository;
    @Inject private AutoFillGapSettingsService autoFillGapSettingsService;
    @Inject private ExecutorService executorService;
    @Inject private ActivityHelperService activityHelperService;

    private static final Logger LOGGER = LoggerFactory.getLogger(OrganizationActivityService.class);

    @CacheEvict(value={"getActivityMappingDetails","findAllActivityByUnitIdWithCompositeActivities"}, key="#unitId")
    public ActivityDTO copyActivity(Long unitId, BigInteger activityId, boolean checked) {
        Activity activityCopied;
        if (checked) {
            Activity activity = activityMongoRepository.findOne(activityId);
            if (!Optional.ofNullable(activity).isPresent()) {
                exceptionService.dataNotFoundByIdException(MESSAGE_ACTIVITY_ID, activityId);
            }
            Activity isActivityAlreadyExist = activityMongoRepository.findByNameIgnoreCaseAndUnitIdAndByDate(activity.getName().trim(), unitId, activity.getActivityGeneralSettings().getStartDate(), activity.getActivityGeneralSettings().getEndDate());
            if (Optional.ofNullable(isActivityAlreadyExist).isPresent()) {
                exceptionService.dataNotFoundException(isActivityAlreadyExist.getActivityGeneralSettings().getEndDate() == null ? MESSAGE_ACTIVITY_ENDDATE_REQUIRED : MESSAGE_ACTIVITY_ACTIVE_ALREADYEXISTS);
            }
            List<PhaseDTO> phaseDTOList = phaseService.getPhasesByUnit(unitId);
            Collections.reverse(phaseDTOList);

            Set<Long> parentAccessGroupIds = activity.getActivityPhaseSettings().getPhaseTemplateValues().stream().flatMap(a -> a.getActivityShiftStatusSettings().stream().flatMap(b -> b.getAccessGroupIds().stream())).collect(Collectors.toSet());
            Map<Long, Long> accessGroupIdsMap = userIntegrationService.getAccessGroupForUnit(unitId, parentAccessGroupIds);
            List<PhaseTemplateValue> phaseTemplateValues1 = activity.getActivityPhaseSettings().getPhaseTemplateValues();
            List<PhaseTemplateValue> phaseTemplateValues = new ArrayList<>();
            for (int i = 0; i < phaseDTOList.size(); i++) {
                List<ActivityShiftStatusSettings> existingActivityShiftStatusSettings = activity.getActivityPhaseSettings().getPhaseTemplateValues().get(i).getActivityShiftStatusSettings();
                List<ActivityShiftStatusSettings> activityShiftStatusSettings = new ArrayList<>();
                Set<Long> agIds = new HashSet<>();
                PhaseTemplateValue phaseTemplateValue = new PhaseTemplateValue(phaseDTOList.get(i).getId(), phaseDTOList.get(i).getName(), phaseDTOList.get(i).getDescription(), phaseTemplateValues1.get(i).getEligibleEmploymentTypes(),
                        phaseTemplateValues1.get(i).isEligibleForManagement(), phaseTemplateValues1.get(i).isStaffCanDelete(), phaseTemplateValues1.get(i).isManagementCanDelete(),
                        phaseTemplateValues1.get(i).isStaffCanSell(), phaseTemplateValues1.get(i).isManagementCanSell(), phaseTemplateValues1.get(i).getAllowedSettings());
                phaseTemplateValue.setSequence(phaseDTOList.get(i).getSequence());
                for (int j = 0; j < existingActivityShiftStatusSettings.size(); j++) {
                    List<Long> accessGroupIds = new ArrayList<>(existingActivityShiftStatusSettings.get(j).getAccessGroupIds());
                    accessGroupIds.forEach(a -> {
                        if (accessGroupIdsMap.get(a) != null) {
                            agIds.add(accessGroupIdsMap.get(a));
                        }
                    });
                    activityShiftStatusSettings.add(new ActivityShiftStatusSettings(existingActivityShiftStatusSettings.get(j).getShiftStatus(), agIds));
                }
                phaseTemplateValue.setActivityShiftStatusSettings(activityShiftStatusSettings);
                phaseTemplateValues.add(phaseTemplateValue);
            }
            activity.getActivityPhaseSettings().setPhaseTemplateValues(phaseTemplateValues);
            activityCopied = copyAllActivitySettingsInUnit(activity, unitId);
        } else {
            activityCopied = removeActivityFromUnity(unitId, activityId);
        }
        activityCopied.setState(ActivityStateEnum.PUBLISHED);
        activityMongoRepository.save(activityCopied);
        TimeType timeType = timeTypeService.getTimeTypeById(activityCopied.getActivityBalanceSettings().getTimeTypeId());
        if((PriorityFor.PRESENCE.equals(timeType.getPriorityFor()) || PriorityFor.PRESENCE.equals(activityCopied.getActivityBalanceSettings().getPriorityFor()))) {
            activityRankingService.addOrRemovePresenceActivityRanking(unitId, activityCopied, checked);
        }
        return retrieveBasicDetails(activityCopied);
    }


    @CacheEvict(value={"getActivityMappingDetails","findAllActivityByUnitIdWithCompositeActivities"}, key="#unitId")
    private Activity removeActivityFromUnity(Long unitId, BigInteger activityId) {
        Activity activityCopied;
        activityCopied = Optional.ofNullable(activityMongoRepository.findByParentIdAndDeletedFalseAndUnitId(activityId, unitId)).orElseThrow(()->new DataNotFoundByIdException(convertMessage(MESSAGE_ACTIVITY_ID, activityId)));
        if (!userIntegrationService.isUnit(unitId)) {
            List<Long> childUnitIds = userIntegrationService.getAllOrganizationIds(unitId);
            if (activityMongoRepository.existsByParentIdAndDeletedFalse(activityCopied.getId(), childUnitIds)) {
                exceptionService.actionNotPermittedException(ACTIVITY_USED_AT_UNIT);
            }
        }
        long activityCount = shiftService.countByActivityId(activityCopied.getId());
        if (activityCount > 0) {
            exceptionService.actionNotPermittedException(MESSAGE_ACTIVITY_TIMECAREACTIVITYTYPE, activityCopied.getName());
        }
        if (isNotNull(activityCopied)) {
            activityCopied.setDeleted(true);
        }
        return activityCopied;
    }

    private ActivityDTO retrieveBasicDetails(Activity activity) {
        ActivityDTO activityDTO = new ActivityDTO(activity.getId(), activity.getName(), activity.getParentId());
        activityDTO.setActivityBalanceSettings(ObjectMapperUtils.copyPropertiesByMapper(activity.getActivityBalanceSettings(), ActivityBalanceSettingDTO.class));
        BeanUtils.copyProperties(activity, activityDTO);
        return activityDTO;

    }

    @Cacheable(value = "getActivityMappingDetails", key = "#unitId", cacheManager = "cacheManager")
    public ActivityWithSelectedDTO getActivityMappingDetails(Long unitId) {
        ActivityWithSelectedDTO activityDetails = new ActivityWithSelectedDTO();
        ActivityWithUnitIdDTO activities = activityService.getActivityByUnitId(unitId);
        if (Optional.ofNullable(activities).isPresent() && Optional.ofNullable(activities.getActivityDTOList()).isPresent()) {
            activityDetails.setAllActivities(activities.getActivityDTOList());
        }
        activityDetails.setSelectedActivities(activityMongoRepository.findAllActivityByUnitIdAndDeleted(unitId,INVALID_ID, newArrayList()));
        return activityDetails;
    }


    public Map<String, Object> getAllActivityByUnit(Long unitId,boolean includeTeamActivity,boolean includeCountryActivity) {
        Map<String, Object> response = new HashMap<>();
        OrganizationDTO organizationDTO = userIntegrationService.getOrganizationWithCountryId(unitId);
        List<Long> orgSubTypes = organizationDTO.getOrganizationSubTypes().stream().map(OrganizationTypeDTO::getId).collect(Collectors.toList());
        List<ActivityTagDTO> activities = includeTeamActivity ? activityMongoRepository.findAllActivityByUnitIdAndDeleted(unitId, includeCountryActivity?UserContext.getUserDetails().getCountryId():INVALID_ID,orgSubTypes) : activityMongoRepository.findAllActivityByUnitIdAndNotPartOfTeam(unitId);
        for (ActivityTagDTO activityTagDTO : activities) {
            Set<OrganizationHierarchy> hierarchies = activityTagDTO.getActivityCanBeCopiedForOrganizationHierarchy();
            boolean activityCanBeCopied = ((isCollectionNotEmpty(hierarchies)) && ((organizationDTO.isParentOrganization() && hierarchies.contains(OrganizationHierarchy.ORGANIZATION)) ||
                    (!organizationDTO.isParentOrganization() && hierarchies.contains(OrganizationHierarchy.UNIT))));
            activityTagDTO.setActivityCanBeCopied(activityCanBeCopied);
        }
        List<ActivityCategoryDTO> activityCategoryDTOS = activityCategoryRepository.findByCountryId(organizationDTO.getCountryId());
        response.put("activities", activities);
        response.put("activityCategories", activityCategoryDTOS);
        return response;
    }


    @CacheEvict(value={"getActivityMappingDetails","findAllActivityByUnitIdWithCompositeActivities"}, key="#unitId")
    public Map<String, TranslationInfo> updateUnitActivityTranslationDetails(BigInteger activityId, Long unitId, Map<String, TranslationInfo> activityTranslationMap){
        Activity activity = activityMongoRepository.findByIdAndUnitIdAndDeleted(activityId,unitId,false);
        if(isNull(activity)) {
            exceptionService.dataNotFoundException(MESSAGE_DATA_NOTFOUND);
        }
        return activityService.updateActivityTranslations(activity,activityTranslationMap);
    }


    public ActivitySettingsWrapper getGeneralTabOfActivity(BigInteger activityId, Long unitId) {
        Activity activity = activityMongoRepository.findOne(activityId);
        if (!Optional.ofNullable(activity).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_ACTIVITY_ID, activityId);
        }
        OrganizationDTO organizationDTO = userIntegrationService.getOrganizationWithCountryId(unitId);
        ActivityGeneralSettings generalTab = activity.getActivityGeneralSettings();
        generalTab.setTranslations(activity.getTranslations());
        LOGGER.info("activity.getTags() ================ > " + activity.getTags());
        //generalTab.setTags(tagMongoRepository.getTagsById(activity.getTags()));
        LOGGER.info("activityId " + activityId);
        generalTab.setTags(null);
        GeneralActivityWithTagDTO generalActivityWithTagDTO = ObjectMapperUtils.copyPropertiesByMapper(generalTab, GeneralActivityWithTagDTO.class);
        if (isCollectionNotEmpty(activity.getTags())) {
            List<TagDTO> tags = new ArrayList<>();
            tags.addAll(tagMongoRepository.getTagsById(activity.getTags()));
            tags.addAll(organizationDTO.getTagDTOS().stream().filter(tagDTO -> activity.getTags().contains(new BigInteger(tagDTO.getId().toString()))).collect(Collectors.toList()));
            generalActivityWithTagDTO.setTags(tags);
        }
        List<PresenceTypeDTO> presenceTypeDTOS = plannedTimeTypeService.getAllPresenceTypeByCountry(organizationDTO.getCountryId());
        PresenceTypeWithTimeTypeDTO presenceType = new PresenceTypeWithTimeTypeDTO(presenceTypeDTOS, organizationDTO.getCountryId());
        ActivityBalanceSettings activityBalanceSettings = activity.getActivityBalanceSettings();
        setDataInGeneralActivityWithTagDTO(activity, generalActivityWithTagDTO, activityBalanceSettings);
        ActivitySettingsWrapper activitySettingsWrapper = new ActivitySettingsWrapper(generalActivityWithTagDTO, activityId, activityCategoryRepository.findByCountryId(organizationDTO.getCountryId()));
        activitySettingsWrapper.setTimeTypes(timeTypeService.getAllTimeType(activityBalanceSettings.getTimeTypeId(), presenceType.getCountryId()));
        activitySettingsWrapper.setPresenceTypeWithTimeType(presenceType);
        return activitySettingsWrapper;
    }

    private void setDataInGeneralActivityWithTagDTO(Activity activity, GeneralActivityWithTagDTO generalActivityWithTagDTO, ActivityBalanceSettings activityBalanceSettings) {
        generalActivityWithTagDTO.setAddTimeTo(activityBalanceSettings.getAddTimeTo());
        generalActivityWithTagDTO.setTimeTypeId(activityBalanceSettings.getTimeTypeId());
        generalActivityWithTagDTO.setOnCallTimePresent(activityBalanceSettings.isOnCallTimePresent());
        generalActivityWithTagDTO.setNegativeDayBalancePresent(activityBalanceSettings.getNegativeDayBalancePresent());
        generalActivityWithTagDTO.setTimeType(activityBalanceSettings.getTimeType());
        generalActivityWithTagDTO.setContent(activity.getActivityNotesSettings().getContent());
        generalActivityWithTagDTO.setOriginalDocumentName(activity.getActivityNotesSettings().getOriginalDocumentName());
        generalActivityWithTagDTO.setModifiedDocumentName(activity.getActivityNotesSettings().getModifiedDocumentName());
        generalActivityWithTagDTO.setTranslations(activity.getTranslations());
    }

    //TODO Need to make sure that its fine to not copy expertise/skills/employmentTypes
    @CacheEvict(value={"getActivityMappingDetails","findAllActivityByUnitIdWithCompositeActivities"}, key="#unitId")
    private Activity copyAllActivitySettingsInUnit(Activity activity, Long unitId) {
        Activity activityCopied = new Activity();
        Activity.copyProperties(activity, activityCopied, "id", "organizationTypes", "organizationSubTypes");
        activityCopied.setParentId(activity.getId());
        activityCopied.setCountryParentId(activity.getCountryParentId() == null ? activity.getId() : activity.getCountryParentId());
        activityCopied.setParentActivity(false);
        activityCopied.setOrganizationTypes(null);
        activityCopied.setOrganizationSubTypes(null);
        activityCopied.setState(null);
        activityCopied.setLevels(null);
        activityCopied.setRegions(null);
        activityCopied.setUnitId(unitId);
        activityCopied.setCountryId(null);
        //TODO Refactor below query or might need to add parent id in activity priority domain while copying from country to organization
        TimeType timeType = timeTypeMongoRepository.findOneById(activity.getActivityBalanceSettings().getTimeTypeId());
        updateSkills(activityCopied);
        // activityCopied.setCompositeActivities(null);
        return activityCopied;
    }

    @CacheEvict(value={"getActivityMappingDetails","findAllActivityByUnitIdWithCompositeActivities"}, key="#unitId")
    public ActivitySettingsWrapper updateGeneralTab(ActivityGeneralSettingsDTO generalDTO, Long unitId) {
        validateActivityOnGeneralDetailsUpdate(generalDTO, unitId);
        Activity activity = activityMongoRepository.findOne(generalDTO.getActivityId());
        generalDTO.setBackgroundColor(activity.getActivityGeneralSettings().getBackgroundColor());
        ActivityGeneralSettings generalTab = new ActivityGeneralSettings();
        ObjectMapperUtils.copyProperties(generalDTO, generalTab);
        if (Optional.ofNullable(activity.getActivityGeneralSettings().getModifiedIconName()).isPresent()) {
            generalTab.setModifiedIconName(activity.getActivityGeneralSettings().getModifiedIconName());
        }
        if (Optional.ofNullable(activity.getActivityGeneralSettings().getOriginalIconName()).isPresent()) {
            generalTab.setOriginalIconName(activity.getActivityGeneralSettings().getOriginalIconName());
        }
        LocalDate oldEndDate = activity.getActivityGeneralSettings().getEndDate();
        activity.setActivityGeneralSettings(generalTab);
        activity.setName(generalTab.getName());
        activity.setDescription(generalTab.getDescription());
        activity.setTags(generalDTO.getTags());

        // generalTab.setTags(tagMongoRepository.getTagsById(generalDTO.getTags()));
        OrganizationDTO organizationDTO = userIntegrationService.getOrganizationWithCountryId(unitId);
        generalTab.setTags(null);
        GeneralActivityWithTagDTO generalActivityWithTagDTO = ObjectMapperUtils.copyPropertiesByMapper(generalTab, GeneralActivityWithTagDTO.class);
        if (!activity.getTags().isEmpty()) {
            List<TagDTO> tags = new ArrayList<>();
            tags.addAll(tagMongoRepository.getTagsById(activity.getTags()));
            tags.addAll(organizationDTO.getTagDTOS().stream().filter(tagDTO -> activity.getTags().contains(new BigInteger(tagDTO.getId().toString()))).collect(Collectors.toList()));
            generalActivityWithTagDTO.setTags(tags);
            generalTab.setTags(activity.getTags());
        }
        activityService.updateBalanceSettingTab(generalDTO, activity);
        activityService.updateNotesTabOfActivity(generalDTO, activity);
        activitySettingsService.updateTimeTypePathInActivity(activity);
        activityMongoRepository.save(activity);
        TimeType timeType = timeTypeService.getTimeTypeById(activity.getActivityBalanceSettings().getTimeTypeId());
        if((PriorityFor.PRESENCE.equals(timeType.getPriorityFor()) || PriorityFor.PRESENCE.equals(activity.getActivityBalanceSettings().getPriorityFor())) && PUBLISHED.equals(activity.getState())){
            activityRankingService.updateEndDateOfPresenceActivity(unitId, activity, oldEndDate);
        }
        getGeneralActivityWithTagDTO(activity, generalActivityWithTagDTO);
        return new ActivitySettingsWrapper(generalActivityWithTagDTO, generalDTO.getActivityId(), activityCategoryRepository.findByCountryId(organizationDTO.getCountryId()));

    }

    private void validateActivityOnGeneralDetailsUpdate(ActivityGeneralSettingsDTO generalDTO, Long unitId) {
        boolean isPartOfTeam = timeTypeMongoRepository.existsByIdAndPartOfTeam(generalDTO.getTimeTypeId(), true);
        if (!isPartOfTeam) {
            boolean isActivityPresent = userIntegrationService.verifyingIsActivityAlreadyAssigned(generalDTO.getActivityId(), unitId);
            if (isActivityPresent) {
                exceptionService.actionNotPermittedException(MESSAGE_ACTIVITY_TIMETYPE_ALREADY_IN_TEAM);
            }
        }
        if (generalDTO.getEndDate() != null && generalDTO.getEndDate().isBefore(generalDTO.getStartDate())) {
            exceptionService.actionNotPermittedException(MESSAGE_ACTIVITY_ENDDATE_GREATERTHAN_STARTDATE);
        }
        Activity isActivityAlreadyExist = activityMongoRepository.findByNameExcludingCurrentInUnitAndDate(generalDTO.getName(), generalDTO.getActivityId(), unitId, generalDTO.getStartDate(), generalDTO.getEndDate());
        if (Optional.ofNullable(isActivityAlreadyExist).isPresent() && generalDTO.getStartDate().isBefore(isActivityAlreadyExist.getActivityGeneralSettings().getStartDate())) {
            exceptionService.actionNotPermittedException(MESSAGE_ACTIVITY_OVERLAPING);
        }
        if (Optional.ofNullable(isActivityAlreadyExist).isPresent()) {
            exceptionService.dataNotFoundException(isActivityAlreadyExist.getActivityGeneralSettings().getEndDate() == null ? MESSAGE_ACTIVITY_ENDDATE_REQUIRED : MESSAGE_ACTIVITY_ACTIVE_ALREADYEXISTS);
        }
        ActivityCategory activityCategory = activityCategoryRepository.getByIdAndNonDeleted(generalDTO.getCategoryId());
        if (activityCategory == null) {
            exceptionService.dataNotFoundByIdException(MESSAGE_CATEGORY_NOTEXIST);
        }
    }

    private void getGeneralActivityWithTagDTO(Activity activity, GeneralActivityWithTagDTO generalActivityWithTagDTO) {
        generalActivityWithTagDTO.setAddTimeTo(activity.getActivityBalanceSettings().getAddTimeTo());
        generalActivityWithTagDTO.setTimeTypeId(activity.getActivityBalanceSettings().getTimeTypeId());
        generalActivityWithTagDTO.setOnCallTimePresent(activity.getActivityBalanceSettings().isOnCallTimePresent());
        generalActivityWithTagDTO.setNegativeDayBalancePresent(activity.getActivityBalanceSettings().getNegativeDayBalancePresent());
        generalActivityWithTagDTO.setTimeType(activity.getActivityBalanceSettings().getTimeType());
        generalActivityWithTagDTO.setContent(activity.getActivityNotesSettings().getContent());
        generalActivityWithTagDTO.setOriginalDocumentName(activity.getActivityNotesSettings().getOriginalDocumentName());
        generalActivityWithTagDTO.setModifiedDocumentName(activity.getActivityNotesSettings().getModifiedDocumentName());
        generalActivityWithTagDTO.setBackgroundColor(activity.getActivityGeneralSettings().getBackgroundColor());
    }


    public ActivitySettingsWrapper getTimeCalculationTabOfActivity(BigInteger activityId, Long unitId) {
        List<DayTypeDTO> dayTypes = dayTypeService.getDayTypeWithCountryHolidayCalender(UserContext.getUserDetails().getCountryId());
        Activity activity = activityMongoRepository.findOne(activityId);
        ActivityTimeCalculationSettings activityTimeCalculationSettings = activity.getActivityTimeCalculationSettings();
        List<BigInteger> rulesTabDayTypes = activity.getActivityRulesSettings().getDayTypes();
        return new ActivitySettingsWrapper(activityTimeCalculationSettings, dayTypes, rulesTabDayTypes);
    }

    public ActivitySettingsWrapper getRulesTabOfActivity(BigInteger activityId, Long unitId) {
        DayTypeEmploymentTypeWrapper dayTypeEmploymentTypeWrapper = userIntegrationService.getDayTypesAndEmploymentTypesAtUnit(unitId);
        List<DayTypeDTO> dayTypes = ObjectMapperUtils.copyCollectionPropertiesByMapper(dayTypeEmploymentTypeWrapper.getDayTypes(), DayTypeDTO.class);
        Activity activity = activityMongoRepository.findOne(activityId);
        ActivityRulesSettings activityRulesSettings = activity.getActivityRulesSettings();
        TimeType timeType = timeTypeMongoRepository.findOneById(activity.getActivityBalanceSettings().getTimeTypeId());
        if(isNotNull(timeType)){
            activityRulesSettings.setSicknessSettingValid(timeType.isSicknessSettingValid());
        }
        return new ActivitySettingsWrapper(activityRulesSettings, dayTypes, dayTypeEmploymentTypeWrapper.getEmploymentTypes());
    }

    public ActivitySettingsWrapper getPhaseSettingTabOfActivity(BigInteger activityId, Long unitId) {
        Set<AccessGroupRole> roles = AccessGroupRole.getAllRoles();
        DayTypeEmploymentTypeWrapper dayTypeEmploymentTypeWrapper = userIntegrationService.getDayTypesAndEmploymentTypesAtUnit(unitId);
        List<DayTypeDTO> dayTypes = dayTypeEmploymentTypeWrapper.getDayTypes();
        List<EmploymentTypeDTO> employmentTypeDTOS = dayTypeEmploymentTypeWrapper.getEmploymentTypes();
        Activity activity = activityMongoRepository.findOne(activityId);
        ActivityPhaseSettings activityPhaseSettings = activity.getActivityPhaseSettings();
        return new ActivitySettingsWrapper(roles, activityPhaseSettings, dayTypes, employmentTypeDTOS);
    }

    @CacheEvict(value={"getActivityMappingDetails","findAllActivityByUnitIdWithCompositeActivities"}, key="#unitId")
    public ActivityDTO copyActivityDetails(Long unitId, BigInteger activityId, ActivityDTO activityDTO) {
        Activity activity = activityMongoRepository.
                findByNameIgnoreCaseAndUnitIdAndByDate(activityDTO.getName().trim(), unitId, activityDTO.getStartDate(), activityDTO.getEndDate());
        if (Optional.ofNullable(activity).isPresent() && activityDTO.getStartDate().isBefore(activity.getActivityGeneralSettings().getStartDate())) {
            exceptionService.actionNotPermittedException(MESSAGE_ACTIVITY_OVERLAPING);
        }
        if (Optional.ofNullable(activity).isPresent()) {
            exceptionService.dataNotFoundException(activity.getActivityGeneralSettings().getEndDate() == null ? MESSAGE_ACTIVITY_ENDDATE_REQUIRED : MESSAGE_ACTIVITY_ACTIVE_ALREADYEXISTS);
        }
        Optional<Activity> activityFromDatabase = activityMongoRepository.findById(activityId);
        if (!activityFromDatabase.isPresent() || activityFromDatabase.get().isDeleted() || !unitId.equals(activityFromDatabase.get().getUnitId())) {
            exceptionService.dataNotFoundByIdException(MESSAGE_ACTIVITY_ID, activityId);
        }
        TimeType timeType = timeTypeMongoRepository.findOneById(activityFromDatabase.get().getActivityBalanceSettings().getTimeTypeId());
        OrganizationDTO organizationDTO = userIntegrationService.getOrganizationWithCountryId(unitId);
        Set<OrganizationHierarchy> hierarchies = timeType.getActivityCanBeCopiedForOrganizationHierarchy();
        if ((isCollectionNotEmpty(hierarchies)) && ((organizationDTO.isParentOrganization() && hierarchies.contains(OrganizationHierarchy.ORGANIZATION)) ||
                (!organizationDTO.isParentOrganization() && hierarchies.contains(OrganizationHierarchy.UNIT)))) {
            Activity activityCopied = copyAllActivitySettingsInUnit(activityFromDatabase.get(), unitId);
            setDataInActivity(activityDTO, activityCopied);
            activityMongoRepository.save(activityCopied);
            activityDTO.setId(activityCopied.getId());
            activityDTO.setActivityCanBeCopied(true);
            activityDTO.setUnitId(unitId);
        } else {
            exceptionService.actionNotPermittedException(ACTIVITY_NOT_ELIGIBLE_FOR_COPY);
        }
        return activityDTO;
    }

    private void setDataInActivity(ActivityDTO activityDTO, Activity activityCopied) {
        activityCopied.setName(activityDTO.getName().trim());
        activityCopied.getActivityGeneralSettings().setName(activityDTO.getName().trim());
        activityCopied.getActivityGeneralSettings().setStartDate(activityDTO.getStartDate());
        activityCopied.getActivityGeneralSettings().setEndDate(activityDTO.getEndDate());
        activityCopied.setState(ActivityStateEnum.DRAFT);
    }

    public OrderAndActivityDTO getActivitiesWithBalanceSettings(long unitId) {
        OrderAndActivityDTO orderAndActivityDTO = new OrderAndActivityDTO();
        orderAndActivityDTO.setActivities(activityMongoRepository.findAllActivitiesWithBalanceSettings(unitId));
        orderAndActivityDTO.setOrders(orderService.getOrdersByUnitId(unitId));
        UnitSettingDTO unitSettingDTO = unitSettingRepository.getMinOpenShiftHours(unitId);
        orderAndActivityDTO.setMinOpenShiftHours(unitSettingDTO != null ? unitSettingDTO.getOpenShiftPhaseSetting().getMinOpenShiftHours() : null);
        orderAndActivityDTO.setCounters(counterRepository.getAllCounterBySupportedModule(ModuleType.OPEN_SHIFT));
        orderAndActivityDTO.setReasonCodeDTOS(reasonCodeService.getReasonCodesByUnitId(unitId, ORDER));
        orderAndActivityDTO.setPlannedTypes(plannedTimeTypeService.getAllPresenceTypeByCountry(UserContext.getUserDetails().getCountryId()));
        return orderAndActivityDTO;
    }

    public ActivityWithTimeTypeDTO getActivitiesWithTimeTypesByUnit(Long unitId, Long countryId) {
        List<ActivityDTO> activityDTOS = activityMongoRepository.findAllActivitiesWithTimeTypesByUnit(unitId);
        List<TimeTypeDTO> timeTypeDTOS = timeTypeService.getAllTimeType(null, countryId);
        List<OpenShiftIntervalDTO> intervals = openShiftIntervalRepository.getAllByCountryIdAndDeletedFalse(countryId);
        UnitSettingDTO minOpenShiftHours = unitSettingRepository.getMinOpenShiftHours(unitId);
        List<CounterDTO> counters = counterRepository.getAllCounterBySupportedModule(ModuleType.OPEN_SHIFT);

        return new ActivityWithTimeTypeDTO(activityDTOS, timeTypeDTOS, intervals,
                minOpenShiftHours.getOpenShiftPhaseSetting().getMinOpenShiftHours(), counters);
    }

    public boolean createDefaultDataForOrganization(Long unitId, OrgTypeAndSubTypeDTO orgTypeAndSubTypeDTO) {
        LOGGER.info("I am going to create default data or organization {}" , unitId);
        //unitDataService.addParentOrganizationAndCountryIdForUnit(unitId, parentOrganizationId, countryId);
        List<Phase> phases = phaseService.createDefaultPhase(unitId, orgTypeAndSubTypeDTO.getCountryId());
        phaseSettingsService.createDefaultPhaseSettings(unitId, phases);
        unitSettingService.createDefaultOpenShiftPhaseSettings(unitId, phases);
        activityConfigurationService.createDefaultSettings(unitId, orgTypeAndSubTypeDTO.getCountryId(), phases, orgTypeAndSubTypeDTO.getEmploymentTypeIds());
        timeSlotSetService.createDefaultTimeSlots(unitId);
        //createActivityforOrganisation(unitId, orgTypeAndSubTypeDTO, phases);
        TAndAGracePeriodSettingDTO tAndAGracePeriodSettingDTO = new TAndAGracePeriodSettingDTO(AppConstants.STAFF_GRACE_PERIOD_DAYS, AppConstants.MANAGEMENT_GRACE_PERIOD_DAYS);
        timeAttendanceGracePeriodService.updateTAndAGracePeriodSetting(unitId, tAndAGracePeriodSettingDTO);
        periodSettingsService.createDefaultPeriodSettings(unitId);
        priorityGroupService.copyPriorityGroupsForUnit(unitId, orgTypeAndSubTypeDTO.getCountryId());
        openShiftRuleTemplateService.copyOpenShiftRuleTemplateInUnit(unitId, orgTypeAndSubTypeDTO);
        kpiSetService.copyKPISets(unitId, orgTypeAndSubTypeDTO.getSubTypeId(), orgTypeAndSubTypeDTO.getCountryId());
        protectedDaysOffService.saveProtectedDaysOff(unitId, ProtectedDaysOffUnitSettings.ONCE_IN_A_YEAR);
        reasonCodeService.createReasonCodeForUnit(unitId,orgTypeAndSubTypeDTO.getCountryId());
        counterDistService.createDefaultCategory(unitId);
        autoFillGapSettingsService.createDefaultAutoFillGapSettings(unitId, orgTypeAndSubTypeDTO, phases);
        return true;
    }

    @CacheEvict(value={"getActivityMappingDetails","findAllActivityByUnitIdWithCompositeActivities"}, key="#unitId")
    private void createActivityforOrganisation(Long unitId, OrgTypeAndSubTypeDTO orgTypeAndSubTypeDTO, List<Phase> phases) {
        List<Activity> existingActivities;
        if (orgTypeAndSubTypeDTO.getParentOrganizationId() == null) {
            existingActivities = activityMongoRepository.findAllActivitiesByOrganizationTypeOrSubTypeOrBreakTypes(orgTypeAndSubTypeDTO.getOrganizationTypeId(), orgTypeAndSubTypeDTO.getSubTypeId());
        } else {
            existingActivities = activityMongoRepository.findAllByUnitIdAndDeletedFalse(orgTypeAndSubTypeDTO.getParentOrganizationId());
        }

        if (!existingActivities.isEmpty()) {
            Set<Long> parentAccessGroupIds = existingActivities.stream().flatMap(a -> a.getActivityPhaseSettings().getPhaseTemplateValues().stream().flatMap(b -> b.getActivityShiftStatusSettings().stream().flatMap(c -> c.getAccessGroupIds().stream()))).collect(Collectors.toSet());
            Map<Long, Long> accessGroupIdsMap = userIntegrationService.getAccessGroupForUnit(unitId, parentAccessGroupIds);
            List<Activity> activityCopiedList = new ArrayList<>(existingActivities.size());
            for (Activity activity : existingActivities) {
                LOGGER.info("I am act {}", activity.getName());
                List<PhaseTemplateValue> phaseTemplateValues = new ArrayList<>();
                for (int i = 0; i < phases.size(); i++) {
                    List<ActivityShiftStatusSettings> existingActivityShiftStatusSettings = activity.getActivityPhaseSettings().getPhaseTemplateValues().get(i).getActivityShiftStatusSettings();
                    List<ActivityShiftStatusSettings> activityShiftStatusSettings = new ArrayList<>();
                    Set<Long> agIds = new HashSet<>();
                    PhaseTemplateValue phaseTemplateValue = new PhaseTemplateValue(phases.get(i).getId(), phases.get(i).getName(), phases.get(i).getDescription(), activity.getActivityPhaseSettings().getPhaseTemplateValues().get(i).getEligibleEmploymentTypes(),
                            activity.getActivityPhaseSettings().getPhaseTemplateValues().get(i).isEligibleForManagement(), activity.getActivityPhaseSettings().getPhaseTemplateValues().get(i).isStaffCanDelete(), activity.getActivityPhaseSettings().getPhaseTemplateValues().get(i).isManagementCanDelete(),
                            activity.getActivityPhaseSettings().getPhaseTemplateValues().get(i).isStaffCanSell(), activity.getActivityPhaseSettings().getPhaseTemplateValues().get(i).isManagementCanSell(), activity.getActivityPhaseSettings().getPhaseTemplateValues().get(i).getAllowedSettings());
                    phaseTemplateValue.setSequence(phases.get(i).getSequence());
                    for (int j = 0; j < existingActivityShiftStatusSettings.size(); j++) {
                        List<Long> accessGroupIds = new ArrayList<>(existingActivityShiftStatusSettings.get(j).getAccessGroupIds());
                        accessGroupIds.forEach(a -> {
                            if (accessGroupIdsMap.get(a) != null) {
                                agIds.add(accessGroupIdsMap.get(a));
                            }
                        });
                        activityShiftStatusSettings.add(new ActivityShiftStatusSettings(existingActivityShiftStatusSettings.get(j).getShiftStatus(), agIds));
                    }
                    phaseTemplateValue.setActivityShiftStatusSettings(activityShiftStatusSettings);
                    phaseTemplateValues.add(phaseTemplateValue);
                }
                activity.getActivityPhaseSettings().setPhaseTemplateValues(phaseTemplateValues);
                activityCopiedList.add(copyAllActivitySettingsInUnit(activity, unitId));
            }
            activityMongoRepository.saveEntities(activityCopiedList);
            LinkedHashSet<BigInteger> presenceActivities=activityCopiedList.stream().filter(k->PRESENCE.equals(k.getActivityBalanceSettings().getTimeType())).map(MongoBaseEntity::getId).collect(Collectors.toCollection(LinkedHashSet::new));
            ActivityRankingDTO activityRankingDTO =new ActivityRankingDTO(null,DateUtils.getCurrentLocalDate(),null,null,true);
            activityRankingDTO.setPresenceActivities(presenceActivities);
            activityRankingDTO.setUnitId(unitId);
            activityRankingService.saveActivityRanking(activityRankingDTO);
            costTimeAgreementService.assignCountryCTAtoOrganisation(orgTypeAndSubTypeDTO.getCountryId(), orgTypeAndSubTypeDTO.getSubTypeId(), unitId);
            workTimeAgreementService.assignWTAToNewOrganization(orgTypeAndSubTypeDTO.getSubTypeId(), unitId, orgTypeAndSubTypeDTO.getCountryId());
            activitySchedulerJobService.registerJobForActivityCutoff(activityCopiedList);
        }
    }



    /**
     * @param activities
     * @param parentActivity
     * @Desc this method is being used to validate the cases of allowed activities
     */
    public void verifyChildActivity(List<ActivityDTO> activities, Activity parentActivity) {
        TimeType timeType = timeTypeMongoRepository.findOneById(parentActivity.getActivityBalanceSettings().getTimeTypeId());
        if (!timeType.isAllowChildActivities()) {
            exceptionService.actionNotPermittedException(MESSAGE_ACTIVITY_SETTING_ENABLE, parentActivity.getName());
        }
        if (activityMongoRepository.existsByActivityIdInChildActivities(parentActivity.getId())) {
            exceptionService.actionNotPermittedException(MESSAGE_ACTIVITY_BEING_USED_AS_CHILD, parentActivity.getName());
        }
        List<String> activityNames = activities.stream().filter(k -> isCollectionNotEmpty(k.getChildActivityIds())).map(ActivityDTO::getName).collect(Collectors.toList());
        if (isCollectionNotEmpty(activityNames)) {
            exceptionService.actionNotPermittedException(MESSAGE_ACTIVITY_BEING_USED_AS_PARENT, activityNames);
        }

    }

    public List<ActivityWithCompositeDTO> getTeamActivitiesOfStaff(Long unitId, Long staffId, boolean isActivityType) {
        Set<BigInteger> activityList = userIntegrationService.getTeamActivitiesOfStaff(unitId, staffId);
        Set<BigInteger> childActivityIds = activityMongoRepository.findChildActivityIdsByActivityIds(activityList).stream().flatMap(activityDTO -> activityDTO.getChildActivityIds().stream()).collect(Collectors.toSet());
        if(isCollectionNotEmpty(childActivityIds)){
            activityList.addAll(childActivityIds);
        }
        return activityMongoRepository.findAllActivityByIdsAndIncludeChildActivitiesWithMostUsedCountOfActivity(activityList,unitId,staffId,isActivityType);
    }

    private void updateSkills(Activity activityCopied){
        ActivityDTO activityDTO=userIntegrationService.getAllSkillsByUnit(activityCopied.getUnitId());
        List<ActivitySkill> activitySkills=activityCopied.getActivitySkillSettings().getActivitySkills().stream().filter(k->activityDTO.getSkills().contains(k.getSkillId())).collect(Collectors.toList());
        List<Long> expertiseIds=activityCopied.getExpertises().stream().filter(k->activityDTO.getExpertises().contains(k)).collect(Collectors.toList());
        activityCopied.getActivitySkillSettings().setActivitySkills(activitySkills);
        activityCopied.setExpertises(expertiseIds);
    }

    @CacheEvict(value={"getActivityMappingDetails","findAllActivityByUnitIdWithCompositeActivities"}, key="#unitId")
    public ActivityGeneralSettings addIconInActivity(BigInteger activityId, MultipartFile file) throws IOException {
        Activity activity =activityService.findActivityById(activityId);
        byte[] bytes = file.getBytes();
        String modifiedFileName = System.currentTimeMillis() + file.getOriginalFilename().substring(file.getOriginalFilename().length() - 4);
        Path path = Paths.get(ACTIVITY_TYPE_IMAGE_PATH + modifiedFileName);
        Files.write(path, bytes);
        activity.getActivityGeneralSettings().setOriginalIconName(file.getOriginalFilename());
        activity.getActivityGeneralSettings().setModifiedIconName(modifiedFileName);
        activityMongoRepository.save(activity);
        return activity.getActivityGeneralSettings();
    }

    @CacheEvict(value={"getActivityMappingDetails","findAllActivityByUnitIdWithCompositeActivities"}, key="#unitId")
    public ActivityGeneralSettings addIconInActivity(Long unitId,BigInteger activityId, MultipartFile file) throws IOException {
        return this.addIconInActivity(activityId,file);
    }

    public ActivityGeneralSettings addIconInActivity(BigInteger activityId, MultipartFile file,Long countryId) throws IOException {
        return this.addIconInActivity(activityId,file);
    }

    public PhaseActivityDTO getActivityAndPhaseByUnitId(Long unitId) {
        SelfRosteringMetaData publicHolidayDayTypeWrapper = getSelfRosteringMetaData(unitId);
        List<ActivityPhaseSettings> activityPhaseSettings = activityMongoRepository.findActivityIdAndStatusByUnitAndAccessGroupIds(unitId, new ArrayList<>(publicHolidayDayTypeWrapper.getReasonCodeWrapper().getUserAccessRoleDTO().getAccessGroupIds()));
        Phase phase=phaseService.getPhaseByName(unitId,TIME_ATTENDANCE.toString());
        LocalDate gracePeriodEndDate = getGracePeriodExpireDate(phase,publicHolidayDayTypeWrapper.getReasonCodeWrapper().getUserAccessRoleDTO().isManagement());
        Set<BigInteger> childActivityIds = activityMongoRepository.findChildActivityIdsByActivityIds(publicHolidayDayTypeWrapper.getActivityIds()).stream().flatMap(activityDTO -> activityDTO.getChildActivityIds().stream()).collect(Collectors.toSet());
        if(isCollectionNotEmpty(childActivityIds)){
            publicHolidayDayTypeWrapper.getActivityIds().addAll(childActivityIds);
        }
        List<ActivityWithCompositeDTO> activities = activityMongoRepository.findAllActivityByUnitIdWithCompositeActivities(unitId,publicHolidayDayTypeWrapper.getActivityIds());
        return PhaseActivityDTO.builder().activities(activities).activityPhaseSettings(activityPhaseSettings).gracePeriodExpireDate(gracePeriodEndDate).reasonCodes(publicHolidayDayTypeWrapper.getReasonCodeWrapper().getReasonCodes()).staffAccessRole(publicHolidayDayTypeWrapper.getReasonCodeWrapper().getUserAccessRoleDTO()).build();
    }

    private SelfRosteringMetaData getSelfRosteringMetaData(long unitId) {
        SelfRosteringMetaData publicHolidayDayTypeWrapper = userIntegrationService.getPublicHolidaysDayTypeAndReasonCodeByUnitId(unitId);
        if (!Optional.ofNullable(publicHolidayDayTypeWrapper).isPresent()) {
            exceptionService.internalServerError(MESSAGE_SELFROSTERING_METADATA_NULL);
        }
        publicHolidayDayTypeWrapper.setDayTypes(dayTypeService.getDayTypeWithCountryHolidayCalender(UserContext.getUserDetails().getCountryId()));
        publicHolidayDayTypeWrapper.setPublicHolidays(countryHolidayCalenderService.getAllCountryAllHolidaysByCountryId(UserContext.getUserDetails().getCountryId()));
        return publicHolidayDayTypeWrapper;
    }

    private LocalDate getGracePeriodExpireDate(Phase phase,boolean management) {
        ZonedDateTime startDate = DateUtils.asZonedDateTime(DateUtils.getStartOfDay(DateUtils.getDate()));
        ZonedDateTime endDate;
        if (management) {
            endDate = startDate.minusDays(phase.getGracePeriodByStaff() + phase.getGracePeriodByManagement()).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).minusDays(1);
        } else {
            endDate=startDate.minusDays(phase.getGracePeriodByStaff()).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).minusDays(1);
        }
        return endDate.toLocalDate();
    }


    public ActivityNotesSettings addDocumentInNotesTab(BigInteger activityId, MultipartFile file) throws IOException {
        Activity activity =activityService.findActivityById(activityId);
        byte[] bytes = file.getBytes();
        String modifiedFileName = System.currentTimeMillis() + file.getOriginalFilename().substring(file.getOriginalFilename().length() - 4);
        Path path = Paths.get(ACTIVITY_TYPE_IMAGE_PATH + modifiedFileName);
        Files.write(path, bytes);
        activity.getActivityNotesSettings().setOriginalDocumentName(file.getOriginalFilename());
        activity.getActivityNotesSettings().setModifiedDocumentName(modifiedFileName);
        activityMongoRepository.save(activity);
        return activity.getActivityNotesSettings();
    }

    @CacheEvict(value={"getActivityMappingDetails","findAllActivityByUnitIdWithCompositeActivities"}, key="#unitId")
    public ActivityNotesSettings addDocumentInNotesTab(Long unitId, BigInteger activityId, MultipartFile file) throws IOException{
        return this.addDocumentInNotesTab(activityId,file);
    }

    @CacheEvict(value = "findAllActivityByCountry",key = "#countryId")
    public ActivityNotesSettings addDocumentInNotesTab(BigInteger activityId, MultipartFile file,Long countryId) throws IOException{
        return this.addDocumentInNotesTab(activityId,file);
    }

    public ActivitySettingsWrapper updateCommunicationTabOfActivity(CommunicationActivityDTO communicationActivityDTO, boolean updateFromOrg) {
        validateReminderSettings(communicationActivityDTO.getActivityReminderSettings());
        validateReminderSettings(communicationActivityDTO.getActivityCutoffReminderSettings());
        ActivityCommunicationSettings activityCommunicationSettings = ObjectMapperUtils.copyPropertiesByMapper(communicationActivityDTO, ActivityCommunicationSettings.class);
        if(!activityCommunicationSettings.isAllowActivityCutoffReminder()){
            activityCommunicationSettings.setActivityCutoffReminderSettings(new ArrayList<>());
        }
        Activity activity = activityService.findActivityById(communicationActivityDTO.getActivityId());
        activity.setActivityCommunicationSettings(activityCommunicationSettings);
        activityMongoRepository.save(activity);
        if(updateFromOrg) {
            activitySchedulerJobService.registerJobForActivityCutoff(newArrayList(activity));
        }
        return new ActivitySettingsWrapper(activityCommunicationSettings);
    }
    @CacheEvict(value={"getActivityMappingDetails","findAllActivityByUnitIdWithCompositeActivities"}, key="#unitId")
    public ActivitySettingsWrapper updateCommunicationTabOfActivity(Long unitId,CommunicationActivityDTO communicationActivityDTO, boolean updateFromOrg){
        return this.updateCommunicationTabOfActivity(communicationActivityDTO,updateFromOrg);
    }

    @CacheEvict(value = "findAllActivityByCountry",key = "#countryId")
    public ActivitySettingsWrapper updateCommunicationTabOfActivity(CommunicationActivityDTO communicationActivityDTO, boolean updateFromOrg,Long countryId){
        return this.updateCommunicationTabOfActivity(communicationActivityDTO,updateFromOrg);
    }

    private boolean validateReminderSettings(List<ActivityReminderSettings> activityReminderSettings) {
        int counter = 0;
        if (isCollectionNotEmpty(activityReminderSettings)) {
            for (ActivityReminderSettings currentSettings : activityReminderSettings) {
                if (currentSettings.getSendReminder().getDurationType() == DurationType.MINUTES &&
                        (currentSettings.getRepeatReminder().getDurationType() == DurationType.DAYS)) {
                    exceptionService.actionNotPermittedException(REPEAT_VALUE_CANT_BE, currentSettings.getRepeatReminder().getDurationType());
                }
                // if both are same ie days or minute and reminder value id greater than time value
                if (currentSettings.getSendReminder().getDurationType() == currentSettings.getRepeatReminder().getDurationType() &&
                        currentSettings.getSendReminder().getTimeValue() < currentSettings.getRepeatReminder().getTimeValue()) {
                    exceptionService.actionNotPermittedException(REMINDER_VALUE_CANT_BE_GREATER_THAN_REPEAT_VALUE,
                            currentSettings.getRepeatReminder().getTimeValue(), currentSettings.getRepeatReminder().getDurationType(),
                            currentSettings.getSendReminder().getTimeValue(), currentSettings.getSendReminder().getDurationType());
                }
                validateFrequencyOfReminder(activityReminderSettings, counter, currentSettings);
                if (currentSettings.getId() == null) {
                    currentSettings.setId(activityMongoRepository.nextSequence(ActivityReminderSettings.class.getSimpleName()));
                }
                counter++;
            }
        }
        return true;
    }

    private void validateFrequencyOfReminder(List<ActivityReminderSettings> activityReminderSettings, int counter, ActivityReminderSettings currentSettings) {
        if (counter > 0) {
            ActivityReminderSettings previousSettings = activityReminderSettings.get(counter - 1);
            if (previousSettings.isRepeatAllowed()) {
                validateWithPreviousFrequency(currentSettings, previousSettings.getRepeatReminder());
            } else {
                validateWithPreviousFrequency(currentSettings, previousSettings.getSendReminder());
            }
        }
    }

    private void validateWithPreviousFrequency(ActivityReminderSettings currentSettings, FrequencySettings frequencySettings) {
        // if both are same ie days or minute and reminder value id greater than time value
        if (currentSettings.getSendReminder().getDurationType() == frequencySettings.getDurationType() &&
                currentSettings.getSendReminder().getTimeValue() > frequencySettings.getTimeValue()) {
            exceptionService.actionNotPermittedException(REMINDER_VALUE_CANT_BE_GREATER_THAN_LAST_REPEAT_VALUE,
                    currentSettings.getSendReminder().getTimeValue(), currentSettings.getSendReminder().getDurationType()
                    , frequencySettings.getTimeValue(), frequencySettings.getDurationType());
        }
        if (frequencySettings.getDurationType() == DurationType.MINUTES
                && currentSettings.getSendReminder().getDurationType() == DurationType.DAYS) {
            exceptionService.actionNotPermittedException(NEW_VALUE_CANT_BE_GREATER_THAN_PREVIOUS,
                    currentSettings.getSendReminder().getTimeValue(), currentSettings.getSendReminder().getDurationType(), frequencySettings.getTimeValue(), frequencySettings.getDurationType());
        }
        if (currentSettings.getSendReminder().getDurationType() == DurationType.MINUTES &&
                (currentSettings.getSendReminder().getDurationType() == DurationType.DAYS)) {
            exceptionService.actionNotPermittedException(NEW_VALUE_CANT_BE_GREATER_THAN_PREVIOUS,
                    currentSettings.getSendReminder().getTimeValue(), currentSettings.getSendReminder().getDurationType(), frequencySettings.getTimeValue(), frequencySettings.getDurationType());
        }
    }

    public  Map<String,Set<BigInteger>> getShowOnCallAndStandByActivityId(Long unitId,boolean showStandBy,boolean showOnCall){
        Map<String,Set<BigInteger>> showOnCallAndStandByActivityIds = new HashMap<>();
        showOnCallAndStandByActivityIds.put("showStandBy", showStandBy ? activityMongoRepository.findAllShowOnCallAndStandByActivitiesByUnitId(unitId, UnityActivitySetting.STAND_BY) : newHashSet());
        showOnCallAndStandByActivityIds.put("showOnCall", showOnCall ? activityMongoRepository.findAllShowOnCallAndStandByActivitiesByUnitId(unitId, UnityActivitySetting.ON_CALL) : newHashSet());
        return showOnCallAndStandByActivityIds;
    }

    public Map<String, TranslationInfo> updateUnitActivityCategoryTranslationDetails(BigInteger activityCategoryId,Map<String, TranslationInfo> activityCategoryTranslationMap){
        ActivityCategory activityCategory = activityCategoryRepository.getByIdAndNonDeleted(activityCategoryId);
        if(isNull(activityCategory)) {
            exceptionService.dataNotFoundException(MESSAGE_DATA_NOTFOUND);
        }
        activityCategory.setTranslations(activityCategoryTranslationMap);
        activityCategoryRepository.save(activityCategory);
        return activityCategory.getTranslations();
    }

    public Map<BigInteger, ActivityWrapper> getActivityWrapperMap(List<Shift> shifts, ShiftDTO shiftDTO) {
        Long unitId=null;
        Set<BigInteger> activityIds = new HashSet<>();
        for (Shift shift : shifts) {
            if(unitId==null){
                unitId=shift.getUnitId();
            }
            getActivityIdsByShift(activityIds, shift);
            if(isNotNull(shift.getDraftShift())){
                getActivityIdsByShift(activityIds, shift.getDraftShift());
            }
        }
        if (isNotNull(shiftDTO)) {
            unitId=shiftDTO.getUnitId();
            activityIds.addAll(shiftDTO.getActivities().stream().flatMap(shiftActivityDTO -> shiftActivityDTO.getChildActivities().stream()).map(ShiftActivityDTO::getActivityId).collect(Collectors.toList()));
            activityIds.addAll(shiftDTO.getActivities().stream().map(ShiftActivityDTO::getActivityId).collect(Collectors.toList()));
            activityIds.addAll(shiftDTO.getBreakActivities().stream().map(ShiftActivityDTO::getActivityId).collect(Collectors.toList()));
        }
        List<ActivityWrapper> activities = activityMongoRepository.findActivitiesAndTimeTypeByActivityId(activityIds);
        ActivityRanking activityRanking= activityRankingService.getCurrentlyActiveActivityRankingSettings(unitId,shiftDTO==null?DateUtils.getLocalDate():DateUtils.asLocalDate(shiftDTO.getStartDate()));
        ActivityUtil.updateRanking(activityRanking,activities);
        return activities.stream().collect(Collectors.toMap(k -> k.getActivity().getId(), v -> v));
    }

    private void getActivityIdsByShift(Set<BigInteger> activityIds, Shift shift) {
        activityIds.addAll(shift.getActivities().stream().flatMap(shiftActivity -> shiftActivity.getChildActivities().stream()).map(ShiftActivity::getActivityId).collect(Collectors.toList()));
        activityIds.addAll(shift.getActivities().stream().map(ShiftActivity::getActivityId).collect(Collectors.toList()));
        if(isCollectionNotEmpty(shift.getBreakActivities())){
            activityIds.addAll(shift.getBreakActivities().stream().map(ShiftActivity::getActivityId).collect(Collectors.toList()));
        }
    }
    public void updateBackgroundColorInShifts(TimeTypeDTO timeTypeDTO, String existingTimeTypeColor,BigInteger timeTypeId) {
        if(!existingTimeTypeColor.equals(timeTypeDTO.getBackgroundColor())){
                Set<BigInteger> activityIds = activityHelperService.updateColorInActivity(timeTypeDTO, timeTypeId);
                activityHelperService.updateColorInShift(timeTypeDTO.getBackgroundColor(),activityIds);
        }
    }

    public void validateActivityTimeRules( Short shortestTime, Short longestTime) {
        if (shortestTime != null && longestTime != null && shortestTime > longestTime) {
            exceptionService.actionNotPermittedException(SHORTEST_TIME_GREATER_LONGEST);
        }
    }
    public List<ActivityDTO> getAllAbsenceActivity(Long unitId) {
        List<ActivityDTO> activityDTOS = new ArrayList<>(activityMongoRepository.findAbsenceActivityByUnitId(unitId));
        List<ActivityDTO> filterActivityDto=activityDTOS.stream().filter(activityDTO -> activityDTO.getActivitySequence()>0).collect(Collectors.toList());
        activityDTOS.removeAll(filterActivityDto);
        filterActivityDto.sort(Comparator.comparing(ActivityDTO :: getActivitySequence));
        filterActivityDto.addAll(activityDTOS);
        return filterActivityDto;
    }
}

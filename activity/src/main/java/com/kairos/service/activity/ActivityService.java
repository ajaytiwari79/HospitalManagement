package com.kairos.service.activity;

import com.kairos.commons.custom_exception.DataNotFoundByIdException;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.constants.CommonConstants;
import com.kairos.dto.TranslationInfo;
import com.kairos.dto.activity.activity.ActivityDTO;
import com.kairos.dto.activity.activity.ActivityWithTimeTypeDTO;
import com.kairos.dto.activity.activity.OrganizationActivityDTO;
import com.kairos.dto.activity.activity.activity_tabs.*;
import com.kairos.dto.activity.activity.activity_tabs.communication_tab.ActivityReminderSettings;
import com.kairos.dto.activity.activity.activity_tabs.communication_tab.CommunicationActivityDTO;
import com.kairos.dto.activity.activity.activity_tabs.communication_tab.FrequencySettings;
import com.kairos.dto.activity.counter.configuration.CounterDTO;
import com.kairos.dto.activity.counter.enums.ModuleType;
import com.kairos.dto.activity.glide_time.GlideTimeSettingsDTO;
import com.kairos.dto.activity.open_shift.OpenShiftIntervalDTO;
import com.kairos.dto.activity.period.PlanningPeriodDTO;
import com.kairos.dto.activity.phase.PhaseDTO;
import com.kairos.dto.activity.phase.PhaseWeeklyDTO;
import com.kairos.dto.activity.presence_type.PresenceTypeDTO;
import com.kairos.dto.activity.presence_type.PresenceTypeWithTimeTypeDTO;
import com.kairos.dto.activity.shift.ShiftActivityDTO;
import com.kairos.dto.activity.shift.ShiftDTO;
import com.kairos.dto.activity.shift.ShiftTemplateDTO;
import com.kairos.dto.activity.time_type.TimeTypeDTO;
import com.kairos.dto.activity.unit_settings.activity_configuration.ActivityConfigurationDTO;
import com.kairos.dto.user.access_permission.AccessGroupRole;
import com.kairos.dto.user.country.agreement.cta.cta_response.EmploymentTypeDTO;
import com.kairos.dto.user.country.day_type.DayType;
import com.kairos.dto.user.country.day_type.DayTypeEmploymentTypeWrapper;
import com.kairos.dto.user.country.tag.TagDTO;
import com.kairos.dto.user.organization.OrganizationDTO;
import com.kairos.dto.user.organization.OrganizationTypeAndSubTypeDTO;
import com.kairos.dto.user.organization.OrganizationTypeDTO;
import com.kairos.dto.user.organization.SelfRosteringMetaData;
import com.kairos.dto.user.organization.skill.Skill;
import com.kairos.dto.user.reason_code.ReasonCodeWrapper;
import com.kairos.dto.user_context.UserContext;
import com.kairos.enums.ActivityStateEnum;
import com.kairos.enums.DurationType;
import com.kairos.enums.IntegrationOperation;
import com.kairos.enums.TimeTypeEnum;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.activity.ActivityWrapper;
import com.kairos.persistence.model.activity.TimeType;
import com.kairos.persistence.model.activity.tabs.*;
import com.kairos.persistence.model.activity.tabs.rules_activity_tab.RulesActivityTab;
import com.kairos.persistence.model.common.MongoBaseEntity;
import com.kairos.persistence.model.period.PlanningPeriod;
import com.kairos.persistence.model.phase.Phase;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.model.shift.ShiftActivity;
import com.kairos.persistence.model.unit_settings.ActivityConfiguration;
import com.kairos.persistence.repository.activity.ActivityCategoryRepository;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.activity.ActivityPriorityMongoRepository;
import com.kairos.persistence.repository.counter.CounterRepository;
import com.kairos.persistence.repository.open_shift.OpenShiftIntervalRepository;
import com.kairos.persistence.repository.period.PlanningPeriodMongoRepository;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.persistence.repository.staffing_level.StaffingLevelMongoRepository;
import com.kairos.persistence.repository.tag.TagMongoRepository;
import com.kairos.persistence.repository.time_type.TimeTypeMongoRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.glide_time.GlideTimeSettingsService;
import com.kairos.service.integration.PlannerSyncService;
import com.kairos.service.organization.OrganizationActivityService;
import com.kairos.service.period.PlanningPeriodService;
import com.kairos.service.phase.PhaseService;
import com.kairos.service.scheduler_service.ActivitySchedulerJobService;
import com.kairos.service.shift.ShiftService;
import com.kairos.service.shift.ShiftTemplateService;
import com.kairos.service.staffing_level.StaffingLevelService;
import com.kairos.service.unit_settings.ActivityConfigurationService;
import com.kairos.utils.external_plateform_shift.GetAllActivitiesResponse;
import com.kairos.utils.external_plateform_shift.TimeCareActivity;
import com.kairos.wrapper.activity.ActivityTabsWrapper;
import com.kairos.wrapper.activity.ActivityTagDTO;
import com.kairos.wrapper.activity.ActivityWithCompositeDTO;
import com.kairos.wrapper.phase.PhaseActivityDTO;
import com.kairos.wrapper.shift.ActivityWithUnitIdDTO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.CommonsExceptionUtil.convertMessage;
import static com.kairos.commons.utils.ObjectMapperUtils.copyPropertiesOfCollectionByMapper;
import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.constants.ActivityMessagesConstants.*;
import static com.kairos.constants.AppConstants.ACTIVITY_TYPE_IMAGE_PATH;
import static com.kairos.service.activity.ActivityUtil.*;

/**
 * Created by pawanmandhan on 17/8/17.
 */
@Transactional
@Service
public class ActivityService {
    @Inject
    private ActivityMongoRepository activityMongoRepository;
    @Inject
    private ActivityCategoryRepository activityCategoryRepository;
    @Inject
    private ShiftService shiftService;
    @Inject
    private PhaseService phaseService;
    @Inject
    private PlannedTimeTypeService plannedTimeTypeService;
    @Inject
    private TagMongoRepository tagMongoRepository;
    @Inject
    private OrganizationActivityService organizationActivityService;
    @Inject
    private TimeTypeMongoRepository timeTypeMongoRepository;
    @Inject
    private TimeTypeService timeTypeService;
    @Inject
    private PlannerSyncService plannerSyncService;
    @Inject
    private StaffingLevelMongoRepository staffingLevelMongoRepository;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private OpenShiftIntervalRepository openShiftIntervalRepository;
    @Inject
    private ShiftTemplateService shiftTemplateService;
    @Inject
    private UserIntegrationService userIntegrationService;
    @Inject
    private CounterRepository counterRepository;
    @Inject
    private GlideTimeSettingsService glideTimeSettingsService;
    @Inject
    private PlanningPeriodService planningPeriodService;
    @Inject
    private PlanningPeriodMongoRepository planningPeriodMongoRepository;
    @Inject
    private ActivityPriorityMongoRepository activityPriorityMongoRepository;
    @Inject
    private ShiftMongoRepository shiftMongoRepository;
    @Inject
    private ActivityConfigurationService activityConfigurationService;
    @Inject
    private StaffingLevelService staffingLevelService;
    @Inject
    private ActivitySchedulerJobService activitySchedulerJobService;

    private static final Logger LOGGER = LoggerFactory.getLogger(ActivityService.class);

    public ActivityTagDTO createActivity(Long countryId, ActivityDTO activityDTO) {
        if (activityDTO.getEndDate() != null && activityDTO.getEndDate().isBefore(activityDTO.getStartDate())) {
            exceptionService.actionNotPermittedException(MESSAGE_ACTIVITY_ENDDATE_GREATERTHAN_STARTDATE);
        }
        Activity activity = activityMongoRepository.findByNameIgnoreCaseAndCountryIdAndByDate(activityDTO.getName().trim(), countryId, activityDTO.getStartDate(), activityDTO.getEndDate());
        if (Optional.ofNullable(activity).isPresent() && activityDTO.getStartDate().isBefore(activity.getGeneralActivityTab().getStartDate())) {
            exceptionService.actionNotPermittedException(MESSAGE_ACTIVITY_OVERLAPING);
        }
        if (Optional.ofNullable(activity).isPresent()) {
            exceptionService.dataNotFoundException(activity.getGeneralActivityTab().getEndDate() == null ? MESSAGE_ACTIVITY_ENDDATE_REQUIRED : MESSAGE_ACTIVITY_ACTIVE_ALREADYEXISTS);
        }
        activity = buildActivity(activityDTO);


        initializeActivityTabs(activity, countryId, activityDTO);
        activityMongoRepository.save(activity);
        // Fetch tags detail
        List<TagDTO> tags = tagMongoRepository.getTagsById(activityDTO.getTags());
        ActivityTagDTO activityTagDTO = new ActivityTagDTO();
        activityTagDTO.buildActivityTagDTO(activity, tags);
        return activityTagDTO;
    }

    private void initializeActivityTabs(Activity activity, Long countryId, ActivityDTO activityDTO) {
        GeneralActivityTab generalActivityTab = new GeneralActivityTab(activity.getName(), activity.getDescription(), "");
        generalActivityTab.setColorPresent(false);
        generalActivityTab.setStartDate(activityDTO.getStartDate());
        generalActivityTab.setEndDate(activityDTO.getEndDate());
        activity.setCountryId(countryId);
        ActivityCategory activityCategory = activityCategoryRepository.getCategoryByNameAndCountryAndDeleted("NONE", countryId, false);
        if (activityCategory != null) {
            generalActivityTab.setCategoryId(activityCategory.getId());
        } else {
            ActivityCategory category = new ActivityCategory("NONE", "", countryId, null);
            activityCategoryRepository.save(category);
            generalActivityTab.setCategoryId(category.getId());
        }
        activity.setGeneralActivityTab(generalActivityTab);
        List<PhaseDTO> phases = phaseService.getPhasesByCountryId(countryId);
        if (CollectionUtils.isEmpty(phases)) {
            exceptionService.actionNotPermittedException(MESSAGE_COUNTRY_PHASE_NOTFOUND);
        }
        List<PhaseTemplateValue> phaseTemplateValues = getPhaseForRulesActivity(phases);
        GlideTimeSettingsDTO glideTimeSettingsDTO = glideTimeSettingsService.getGlideTimeSettings(countryId);
        if (!Optional.ofNullable(glideTimeSettingsDTO).isPresent()) {
            exceptionService.actionNotPermittedException(ERROR_GLIDETIME_NOTFOUND_COUNTRY);
        }
        ActivityUtil.initializeActivityTabs(activity, phaseTemplateValues, glideTimeSettingsDTO);
    }

    public Map<String, Object> findAllActivityByCountry(long countryId) {
        Map<String, Object> response = new HashMap<>();
        List<ActivityTagDTO> activityTagDTOS = activityMongoRepository.findAllActivityByCountry(countryId);
        //In Country Module any Activity can be copied
        activityTagDTOS.forEach(activityTagDTO -> activityTagDTO.setActivityCanBeCopied(true));
        List<ActivityCategory> acivitityCategories = activityCategoryRepository.findByCountryId(countryId);
        response.put("activities", activityTagDTOS);
        response.put("activityCategories", acivitityCategories);
        return response;
    }

    public Set<BigInteger> checkActivityAllowForChildActivities(List<ActivityTagDTO> activities, ActivityWithCompositeDTO activityWithCompositeDTO) {
        Set<BigInteger> allowChildActivityIds = new HashSet<>();
        Set<BigInteger> childActivitiesIds = activities.stream().flatMap(activityTagDTO -> activityTagDTO.getChildActivityIds().stream()).collect(Collectors.toSet());
        for (ActivityTagDTO activity : activities) {
            if (!childActivitiesIds.contains(activity.getId()) && !activity.getId().equals(activityWithCompositeDTO.getId()) && isCollectionEmpty(activity.getChildActivityIds())) {
                allowChildActivityIds.add(activity.getId());
            }
        }
        if (childActivitiesIds.contains(activityWithCompositeDTO.getId())) {
            activityWithCompositeDTO.setApplicableForChildActivities(false);
        }
        return allowChildActivityIds;
    }

    public List<ActivityWithCTAWTASettingsDTO> findAllActivityWithCtaWtaSettingByCountry(long countryId) {
        return activityMongoRepository.findAllActivityWithCtaWtaSettingByCountry(countryId);
    }

    public List<ActivityCategory> findAllActivityCategoriesByCountry( List<BigInteger> activityCategoriesIds) {
        return activityCategoryRepository.findAllByIdsIn(activityCategoriesIds);
    }

    public List<ActivityWithCTAWTASettingsDTO> findAllActivityWithCtaWtaSettingByUnit(long unitId) {
        return activityMongoRepository.findAllActivityWithCtaWtaSettingByUnit(unitId);
    }

    public Map<Long, Map<Long, BigInteger>> getListOfActivityIdsOfUnitByParentIds(List<BigInteger> parentActivityIds, List<Long> unitIds) {
        List<OrganizationActivityDTO> unitActivities = activityMongoRepository.findAllActivityOfUnitsByParentActivity(parentActivityIds, unitIds);
        Map<Long, Map<Long, BigInteger>> mappedParentUnitActivities = new HashMap<>();
        unitActivities.forEach(activityDTO -> {
            Map<Long, BigInteger> unitParentActivities = mappedParentUnitActivities.get(activityDTO.getUnitId());
            if (!Optional.ofNullable(unitParentActivities).isPresent()) {
                mappedParentUnitActivities.put(activityDTO.getUnitId(), new HashMap<>());
                unitParentActivities = mappedParentUnitActivities.get(activityDTO.getUnitId());
            }
            unitParentActivities.put(activityDTO.getParentId().longValue(), activityDTO.getId());
        });
        return mappedParentUnitActivities;
    }

    public boolean deleteActivity(BigInteger activityId) {
        Activity activity = findActivityById(activityId);
        long activityCount = shiftService.countByActivityId(activityId);
        if (activityCount > 0) {
            exceptionService.actionNotPermittedException(MESSAGE_ACTIVITY_TIMECAREACTIVITYTYPE, activity.getName());
        }
        activity.setDeleted(true);
        activityMongoRepository.save(activity);
        return true;
    }

    public ActivityTabsWrapper updateGeneralTab(Long countryId, GeneralActivityTabDTO generalDTO) {
        //check category is available in country
        validateActivityDetails(countryId, generalDTO);
        Activity activity = findActivityById(generalDTO.getActivityId());
        generalDTO.setBackgroundColor(activity.getGeneralActivityTab().getBackgroundColor());
        GeneralActivityTab generalTab = ObjectMapperUtils.copyPropertiesByMapper(generalDTO,GeneralActivityTab.class);
        if (Optional.ofNullable(activity.getGeneralActivityTab().getModifiedIconName()).isPresent()) {
            generalTab.setModifiedIconName(activity.getGeneralActivityTab().getModifiedIconName());
        }
        if (Optional.ofNullable(activity.getGeneralActivityTab().getOriginalIconName()).isPresent()) {
            generalTab.setOriginalIconName(activity.getGeneralActivityTab().getOriginalIconName());
        }
        activity.setGeneralActivityTab(generalTab);
        activity.setName(generalTab.getName());
        activity.setTags(generalDTO.getTags());
        activity.setDescription(generalTab.getDescription());
        List<ActivityCategory> activityCategories = checkCountryAndFindActivityCategory(countryId);
        generalTab.setTags(null);
        GeneralActivityTabWithTagDTO generalActivityTabWithTagDTO = ObjectMapperUtils.copyPropertiesByMapper(generalTab, GeneralActivityTabWithTagDTO.class);
        generalActivityTabWithTagDTO.setTags(null);
        if (!activity.getTags().isEmpty()) {
            generalActivityTabWithTagDTO.setTags(tagMongoRepository.getTagsById(activity.getTags()));
            generalTab.setTags(activity.getTags());
        }
        updateBalanceSettingTab(generalDTO, activity);
        updateNotesTabOfActivity(generalDTO, activity);
        activityMongoRepository.save(activity);
        return getActivityTabsWrapper(activity, activityCategories, generalActivityTabWithTagDTO);
    }

    private void validateActivityDetails(Long countryId, GeneralActivityTabDTO generalDTO) {
        if (generalDTO.getEndDate() != null && generalDTO.getEndDate().isBefore(generalDTO.getStartDate())) {
            exceptionService.actionNotPermittedException(MESSAGE_ACTIVITY_ENDDATE_GREATERTHAN_STARTDATE);
        }
        Activity isActivityAlreadyExists = activityMongoRepository.findByNameExcludingCurrentInCountryAndDate(generalDTO.getName().trim(), generalDTO.getActivityId(), countryId, generalDTO.getStartDate(), generalDTO.getEndDate());
        if (Optional.ofNullable(isActivityAlreadyExists).isPresent() && generalDTO.getStartDate().isBefore(isActivityAlreadyExists.getGeneralActivityTab().getStartDate())) {
            exceptionService.actionNotPermittedException(MESSAGE_ACTIVITY_OVERLAPING);
        }
        if (Optional.ofNullable(isActivityAlreadyExists).isPresent()) {
            exceptionService.dataNotFoundException(isActivityAlreadyExists.getGeneralActivityTab().getEndDate() == null ? MESSAGE_ACTIVITY_ENDDATE_REQUIRED : MESSAGE_ACTIVITY_ACTIVE_ALREADYEXISTS);
        }
        ActivityCategory activityCategory = activityCategoryRepository.getByIdAndNonDeleted(generalDTO.getCategoryId());
        if (activityCategory == null) {
            exceptionService.dataNotFoundByIdException(MESSAGE_CATEGORY_NOTEXIST);
        }
    }

    private ActivityTabsWrapper getActivityTabsWrapper(Activity activity, List<ActivityCategory> activityCategories, GeneralActivityTabWithTagDTO generalActivityTabWithTagDTO) {
        generalActivityTabWithTagDTO.setAddTimeTo(activity.getBalanceSettingsActivityTab().getAddTimeTo());
        generalActivityTabWithTagDTO.setTimeTypeId(activity.getBalanceSettingsActivityTab().getTimeTypeId());
        generalActivityTabWithTagDTO.setOnCallTimePresent(activity.getBalanceSettingsActivityTab().isOnCallTimePresent());
        generalActivityTabWithTagDTO.setNegativeDayBalancePresent(activity.getBalanceSettingsActivityTab().getNegativeDayBalancePresent());
        generalActivityTabWithTagDTO.setTimeType(activity.getBalanceSettingsActivityTab().getTimeType());
        generalActivityTabWithTagDTO.setContent(activity.getNotesActivityTab().getContent());
        generalActivityTabWithTagDTO.setOriginalDocumentName(activity.getNotesActivityTab().getOriginalDocumentName());
        generalActivityTabWithTagDTO.setModifiedDocumentName(activity.getNotesActivityTab().getModifiedDocumentName());
        generalActivityTabWithTagDTO.setBackgroundColor(activity.getGeneralActivityTab().getBackgroundColor());
        return new ActivityTabsWrapper(generalActivityTabWithTagDTO, activityCategories);
    }

    public ActivityTabsWrapper getGeneralTabOfActivity(Long countryId, BigInteger activityId) {
        List<ActivityCategory> activityCategories = checkCountryAndFindActivityCategory(countryId);
        Activity activity = findActivityById(activityId);
        GeneralActivityTab generalTab = activity.getGeneralActivityTab();
        generalTab.setTags(null);
        GeneralActivityTabWithTagDTO generalActivityTabWithTagDTO = ObjectMapperUtils.copyPropertiesByMapper(generalTab, GeneralActivityTabWithTagDTO.class);
        generalActivityTabWithTagDTO.setTags(null);
        if (!activity.getTags().isEmpty()) {
            generalActivityTabWithTagDTO.setTags(tagMongoRepository.getTagsById(activity.getTags()));
        }
        generalActivityTabWithTagDTO.setAddTimeTo(activity.getBalanceSettingsActivityTab().getAddTimeTo());
        generalActivityTabWithTagDTO.setTimeTypeId(activity.getBalanceSettingsActivityTab().getTimeTypeId());
        generalActivityTabWithTagDTO.setOnCallTimePresent(activity.getBalanceSettingsActivityTab().isOnCallTimePresent());
        generalActivityTabWithTagDTO.setNegativeDayBalancePresent(activity.getBalanceSettingsActivityTab().getNegativeDayBalancePresent());
        generalActivityTabWithTagDTO.setTimeType(activity.getBalanceSettingsActivityTab().getTimeType());
        generalActivityTabWithTagDTO.setContent(activity.getNotesActivityTab().getContent());
        generalActivityTabWithTagDTO.setOriginalDocumentName(activity.getNotesActivityTab().getOriginalDocumentName());
        generalActivityTabWithTagDTO.setModifiedDocumentName(activity.getNotesActivityTab().getModifiedDocumentName());
        ActivityTabsWrapper activityTabsWrapper = new ActivityTabsWrapper(generalActivityTabWithTagDTO, activityCategories);
        List<PresenceTypeDTO> presenceTypeDTOS = plannedTimeTypeService.getAllPresenceTypeByCountry(countryId);
        PresenceTypeWithTimeTypeDTO presenceType = new PresenceTypeWithTimeTypeDTO(presenceTypeDTOS, countryId);
        activityTabsWrapper.setPresenceTypeWithTimeType(presenceType);
        activityTabsWrapper.setTimeTypes(timeTypeService.getAllTimeType(activity.getBalanceSettingsActivityTab().getTimeTypeId(), countryId));
        return activityTabsWrapper;
    }

    private List<ActivityCategory> checkCountryAndFindActivityCategory(Long countryId) {
        return activityCategoryRepository.findByCountryId(countryId);
    }

    public BalanceSettingsActivityTab updateBalanceSettingTab(GeneralActivityTabDTO generalActivityTabDTO, Activity activity) {
        TimeType timeType = timeTypeMongoRepository.findOneById(generalActivityTabDTO.getTimeTypeId());
        if (!Optional.ofNullable(timeType).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_ACTIVITY_TIMETYPE_NOTFOUND);
        }
        if (!timeType.getBackgroundColor().equals(activity.getGeneralActivityTab().getBackgroundColor())) {
            List<Shift> shifts = shiftMongoRepository.findShiftByShiftActivityIdAndBetweenDate(newArrayList(activity.getId()), null, null, null);
            updateShiftActivityBackGroundColor(activity, timeType, shifts);
            if (isCollectionNotEmpty(shifts)) {
                shiftMongoRepository.saveEntities(shifts);
            }
        }
        if(isNotNull(generalActivityTabDTO.getTimeTypeId()) && !generalActivityTabDTO.getTimeTypeId().equals(activity.getBalanceSettingsActivityTab().getTimeTypeId())){
            if (activity.getState().equals(ActivityStateEnum.PUBLISHED)) {
                exceptionService.actionNotPermittedException(MESSAGE_ACTIVITY_TIMETYPE_PUBLISHED, activity.getId());
            }
            activity.setPhaseSettingsActivityTab(timeType.getPhaseSettingsActivityTab());
            activity.setRulesActivityTab(timeType.getRulesActivityTab());
            activity.setTimeCalculationActivityTab(timeType.getTimeCalculationActivityTab());
            activity.setEmploymentTypes(timeType.getEmploymentTypes());
            activity.setExpertises(timeType.getExpertises());
            activity.setOrganizationSubTypes(timeType.getOrganizationSubTypes());
            activity.setOrganizationTypes(timeType.getOrganizationTypes());
            activity.setSkillActivityTab(timeType.getSkillActivityTab());
            activity.setRegions(timeType.getRegions());
            activity.setLevels(timeType.getLevels());
            activity.setActivityPriorityId(timeType.getActivityPriorityId());
        }
        activity.getGeneralActivityTab().setBackgroundColor(timeType.getBackgroundColor());
        activity.getGeneralActivityTab().setColorPresent(true);
        Long countryId = activity.getCountryId();
        if (countryId == null) {
            countryId = userIntegrationService.getCountryIdOfOrganization(activity.getUnitId());
        }
        activity.getBalanceSettingsActivityTab().setTimeTypeId(generalActivityTabDTO.getTimeTypeId());
        activity.getBalanceSettingsActivityTab().setTimeType(timeType.getSecondLevelType());
        activity.getBalanceSettingsActivityTab().setAddTimeTo(generalActivityTabDTO.getAddTimeTo());
        activity.getBalanceSettingsActivityTab().setOnCallTimePresent(generalActivityTabDTO.isOnCallTimePresent());
        activity.getBalanceSettingsActivityTab().setNegativeDayBalancePresent(generalActivityTabDTO.getNegativeDayBalancePresent());
        updateActivityCategory(activity, countryId);
        return activity.getBalanceSettingsActivityTab();
    }

    private void updateShiftActivityBackGroundColor(Activity activity, TimeType timeType, List<Shift> shifts) {
        shifts.forEach(shift -> shift.getActivities().forEach(shiftActivity -> {
            if (shiftActivity.getActivityId().equals(activity.getId())) {
                shiftActivity.setBackgroundColor(timeType.getBackgroundColor());
            }
            shiftActivity.getChildActivities().forEach(childActivity -> {
                if (childActivity.getActivityId().equals(activity.getId())) {
                    childActivity.setBackgroundColor(timeType.getBackgroundColor());
                }
            });
        }));
    }

    public void updateActivityCategory(Activity activity, Long countryId) {
        TimeType timeType = timeTypeMongoRepository.findOneById(activity.getBalanceSettingsActivityTab().getTimeTypeId(), countryId);
        if (isNull(timeType))
            exceptionService.dataNotFoundByIdException(MESSAGE_TIMETYPE_NOTFOUND);
        ActivityCategory category = activityCategoryRepository.getCategoryByTimeType(countryId, activity.getBalanceSettingsActivityTab().getTimeTypeId());
        if (category == null) {
            category = new ActivityCategory(timeType.getLabel(), "", countryId, timeType.getId());
            activityCategoryRepository.save(category);
        }
        activity.getGeneralActivityTab().setCategoryId(category.getId());
    }

    public TimeCalculationActivityDTO updateTimeCalculationTabOfActivity(TimeCalculationActivityDTO timeCalculationActivityDTO, boolean availableAllowActivity) {
        TimeCalculationActivityTab timeCalculationActivityTab = ObjectMapperUtils.copyPropertiesByMapper(timeCalculationActivityDTO,TimeCalculationActivityTab.class);
        Activity activity = findActivityById(new BigInteger(String.valueOf(timeCalculationActivityDTO.getActivityId())));
        verifyAndDeleteCompositeActivity(timeCalculationActivityDTO, availableAllowActivity);
        if (!timeCalculationActivityDTO.isAvailableAllowActivity()) {
            activity.setTimeCalculationActivityTab(timeCalculationActivityTab);
            if (!timeCalculationActivityTab.getMethodForCalculatingTime().equals(CommonConstants.FULL_WEEK)) {
                timeCalculationActivityTab.setDayTypes(activity.getRulesActivityTab().getDayTypes());
            }
            activityMongoRepository.save(activity);
        }
        return timeCalculationActivityDTO;
    }

    private TimeCalculationActivityDTO verifyAndDeleteCompositeActivity(TimeCalculationActivityDTO timeCalculationActivityDTO, boolean availableAllowActivity) {
        if (timeCalculationActivityDTO.getMethodForCalculatingTime().equals(CommonConstants.FULL_WEEK) || timeCalculationActivityDTO.getMethodForCalculatingTime().equals(CommonConstants.FULL_DAY_CALCULATION)) {
            boolean availableAllowActivities = activityMongoRepository.existsByActivityIdInCompositeActivitiesAndDeletedFalse(new BigInteger((String.valueOf(timeCalculationActivityDTO.getActivityId()))));
            if (availableAllowActivities && availableAllowActivity) {
                activityMongoRepository.unassignCompositeActivityFromActivitiesByactivityId(new BigInteger((String.valueOf(timeCalculationActivityDTO.getActivityId()))));
            } else if (availableAllowActivities) {
                timeCalculationActivityDTO.setAvailableAllowActivity(true);
            }
        }
        return timeCalculationActivityDTO;
    }

    public Set<BigInteger> assignChildActivitiesInActivity(BigInteger activityId, Set<BigInteger> childActivitiesIds) {
        Activity activity = findActivityById(activityId);
        List<ActivityDTO> activityMatched = activityMongoRepository.findChildActivityActivityIds(childActivitiesIds);
        if (activityMatched.size() != childActivitiesIds.size()) {
            exceptionService.illegalArgumentException(MESSAGE_MISMATCHED_IDS);
        }
        organizationActivityService.verifyChildActivity(activityMatched, activity);
        activity.setChildActivityIds(childActivitiesIds);
        activityMongoRepository.save(activity);
        return childActivitiesIds;
    }


    public ActivityTabsWrapper getTimeCalculationTabOfActivity(BigInteger activityId, Long countryId) {
        List<DayType> dayTypes = userIntegrationService.getDayTypesByCountryId(countryId);
        Activity activity = activityMongoRepository.findOne(activityId);
        TimeCalculationActivityTab timeCalculationActivityTab = activity.getTimeCalculationActivityTab();
        List<Long> rulesTabDayTypes = activity.getRulesActivityTab().getDayTypes();
        return new ActivityTabsWrapper(timeCalculationActivityTab, dayTypes, rulesTabDayTypes);
    }


    public ActivityWithCompositeDTO getCompositeAndChildActivityOfCountryActivity(BigInteger activityId, Long countryId) {
        ActivityWithCompositeDTO activity = getCompositeShiftTabOfActivity(activityId);
        List<ActivityTagDTO> activityTagDTO = activityMongoRepository.findAllowChildActivityByCountryId(countryId);
        activity.setAvailableChildActivityIds(checkActivityAllowForChildActivities(activityTagDTO, activity));
        return activity;
    }


    public ActivityWithCompositeDTO getCompositeAndChildActivityOfUnitActivity(BigInteger activityId, Long unitId) {
        ActivityWithCompositeDTO activity = getCompositeShiftTabOfActivity(activityId);
        List<ActivityTagDTO> activityTagDTO = activityMongoRepository.findAllowChildActivityByUnitIdAndDeleted(unitId, false);
        activity.setAvailableChildActivityIds(checkActivityAllowForChildActivities(activityTagDTO, activity));
        return activity;
    }

    public ActivityWithCompositeDTO getCompositeShiftTabOfActivity(BigInteger activityId) {
        ActivityWithCompositeDTO activity = activityMongoRepository.findActivityByActivityId(activityId);
        if (isNull(activity)) {
            exceptionService.dataNotFoundByIdException(MESSAGE_ACTIVITY_ID, activityId);
        }
        return activity;
    }

    public ActivityTabsWrapper updateIndividualPointsTab(IndividualPointsActivityTabDTO individualPointsDTO) {
        IndividualPointsActivityTab individualPointsActivityTab = ObjectMapperUtils.copyPropertiesByMapper(individualPointsDTO,IndividualPointsActivityTab.class);
        Activity activity = findActivityById(new BigInteger(String.valueOf(individualPointsDTO.getActivityId())));
        activity.setIndividualPointsActivityTab(individualPointsActivityTab);
        activityMongoRepository.save(activity);
        return new ActivityTabsWrapper(individualPointsActivityTab);
    }

    public IndividualPointsActivityTab getIndividualPointsTabOfActivity(BigInteger activityId) {
        Activity activity = findActivityById(activityId);
        return activity.getIndividualPointsActivityTab();
    }

    public ActivityTabsWrapper updateRulesTab(RulesActivityTabDTO rulesActivityDTO,boolean updateFromOrg) {
        validateActivityTimeRules( rulesActivityDTO.getShortestTime(), rulesActivityDTO.getLongestTime());
        RulesActivityTab rulesActivityTab = ObjectMapperUtils.copyPropertiesByMapper(rulesActivityDTO, RulesActivityTab.class);
        Activity activity = findActivityById(rulesActivityDTO.getActivityId());
        checkEligibleStaffLevelDetails(rulesActivityDTO, activity);
        updateCutoffDetails(rulesActivityDTO, rulesActivityTab);
        if(activity.getRulesActivityTab().isEligibleForStaffingLevel() != rulesActivityTab.isEligibleForStaffingLevel() && !rulesActivityTab.isEligibleForStaffingLevel()){
            removedActivityFromStaffingLevelOfChildActivity(activity.getChildActivityIds());
            staffingLevelService.removedActivityFromStaffingLevel(activity.getId(), TimeTypeEnum.PRESENCE.equals(activity.getBalanceSettingsActivityTab().getTimeType()));
        }
        activity.setRulesActivityTab(rulesActivityTab);
        if (!activity.getTimeCalculationActivityTab().getMethodForCalculatingTime().equals(CommonConstants.FULL_WEEK)) {
            activity.getTimeCalculationActivityTab().setDayTypes(activity.getRulesActivityTab().getDayTypes());
        }
        activityMongoRepository.save(activity);
        if(updateFromOrg) {
            activitySchedulerJobService.registerJobForActivityCutoff(newArrayList(activity));
        }
        return new ActivityTabsWrapper(rulesActivityTab);
    }

    private void updateCutoffDetails(RulesActivityTabDTO rulesActivityDTO, RulesActivityTab rulesActivityTab) {
        if (rulesActivityDTO.getCutOffIntervalUnit() != null && rulesActivityDTO.getCutOffStartFrom() != null) {
            if (CutOffIntervalUnit.DAYS.equals(rulesActivityDTO.getCutOffIntervalUnit()) && rulesActivityDTO.getCutOffdayValue() == 0) {
                exceptionService.invalidRequestException(ERROR_DAYVALUE_ZERO);
            }
            List<CutOffInterval> cutOffIntervals = getCutoffInterval(rulesActivityDTO.getCutOffStartFrom(), rulesActivityDTO.getCutOffIntervalUnit(), rulesActivityDTO.getCutOffdayValue());
            rulesActivityTab.setCutOffIntervals(cutOffIntervals);
            rulesActivityDTO.setCutOffIntervals(cutOffIntervals);
        }
    }

    private void checkEligibleStaffLevelDetails(RulesActivityTabDTO rulesActivityDTO, Activity activity) {
        if(rulesActivityDTO.isEligibleForStaffingLevel() && !activity.getRulesActivityTab().isEligibleForStaffingLevel()){
            Activity parentActivity = activityMongoRepository.findByChildActivityId(rulesActivityDTO.getActivityId());
            if(isNotNull(parentActivity) && !parentActivity.getRulesActivityTab().isEligibleForStaffingLevel()){
                exceptionService.actionNotPermittedException(MESSAGE_PARENT_SETTING_FALSE);
            }
        }
    }

    private void removedActivityFromStaffingLevelOfChildActivity(Set<BigInteger> childActivityIds){
        if(isCollectionNotEmpty(childActivityIds)) {
            List<Activity> activities = activityMongoRepository.findAllActivitiesByIds(childActivityIds);
            for (Activity activity : activities) {
                if (activity.getRulesActivityTab().isEligibleForStaffingLevel()) {
                    staffingLevelService.removedActivityFromStaffingLevel(activity.getId(), TimeTypeEnum.PRESENCE.equals(activity.getBalanceSettingsActivityTab().getTimeType()));
                    activity.getRulesActivityTab().setEligibleForStaffingLevel(false);
                }
            }
            activityMongoRepository.saveAll(activities);
        }
    }
    public ActivityTabsWrapper getPhaseSettingTabOfActivity(BigInteger activityId, Long countryId) {
        Activity activity = findActivityById(activityId);
        DayTypeEmploymentTypeWrapper dayTypeEmploymentTypeWrapper = userIntegrationService.getDayTypesAndEmploymentTypes(countryId);
        List<DayType> dayTypes = dayTypeEmploymentTypeWrapper.getDayTypes();
        List<EmploymentTypeDTO> employmentTypeDTOS = dayTypeEmploymentTypeWrapper.getEmploymentTypes();
        Set<AccessGroupRole> roles = AccessGroupRole.getAllRoles();
        PhaseSettingsActivityTab phaseSettingsActivityTab = activity.getPhaseSettingsActivityTab();
        return new ActivityTabsWrapper(roles, phaseSettingsActivityTab, dayTypes, employmentTypeDTOS);
    }

    public PhaseSettingsActivityTab updatePhaseSettingTab(PhaseSettingsActivityTab phaseSettingsActivityTab) {
        Activity activity = findActivityById(phaseSettingsActivityTab.getActivityId());
        activity.setPhaseSettingsActivityTab(phaseSettingsActivityTab);
        activityMongoRepository.save(activity);
        return phaseSettingsActivityTab;
    }

    public ActivityTabsWrapper getRulesTabOfActivity(BigInteger activityId, Long countryId) {
        DayTypeEmploymentTypeWrapper dayTypeEmploymentTypeWrapper = userIntegrationService.getDayTypesAndEmploymentTypes(countryId);
        List<DayType> dayTypes = dayTypeEmploymentTypeWrapper.getDayTypes();
        List<EmploymentTypeDTO> employmentTypeDTOS = dayTypeEmploymentTypeWrapper.getEmploymentTypes();
        Activity activity = activityMongoRepository.findOne(activityId);
        RulesActivityTab rulesActivityTab = activity.getRulesActivityTab();
        return new ActivityTabsWrapper(rulesActivityTab, dayTypes, employmentTypeDTOS);
    }

    public NotesActivityTab updateNotesTabOfActivity(GeneralActivityTabDTO generalActivityTabDTO, Activity activity) {
        if (Optional.ofNullable(activity.getNotesActivityTab().getModifiedDocumentName()).isPresent()) {
            activity.getNotesActivityTab().setModifiedDocumentName(generalActivityTabDTO.getModifiedDocumentName());
        }
        if (Optional.ofNullable(activity.getNotesActivityTab().getOriginalDocumentName()).isPresent()) {
            activity.getNotesActivityTab().setOriginalDocumentName(generalActivityTabDTO.getOriginalDocumentName());
        }
        activity.getNotesActivityTab().setContent(generalActivityTabDTO.getContent());
        return activity.getNotesActivityTab();
    }

    public ActivityTabsWrapper getNotesTabOfActivity(BigInteger activityId) {
        Activity activity = findActivityById(activityId);
        return new ActivityTabsWrapper(activity.getNotesActivityTab());
    }


    public List<CutOffInterval> getCutOffInterValOfActivity(BigInteger activityId) {
        Activity activity = findActivityById(activityId);
        return ActivityUtil.getCutoffInterval(activity.getRulesActivityTab().getCutOffStartFrom(),activity.getRulesActivityTab().getCutOffIntervalUnit(), activity.getRulesActivityTab().getCutOffdayValue());
    }


    public ActivityTabsWrapper updateCommunicationTabOfActivity(CommunicationActivityDTO communicationActivityDTO, boolean updateFromOrg) {
        validateReminderSettings(communicationActivityDTO.getActivityReminderSettings());
        validateReminderSettings(communicationActivityDTO.getActivityCutoffReminderSettings());
        CommunicationActivityTab communicationActivityTab = ObjectMapperUtils.copyPropertiesByMapper(communicationActivityDTO,CommunicationActivityTab.class);
        if(!communicationActivityTab.isAllowActivityCutoffReminder()){
            communicationActivityTab.setActivityCutoffReminderSettings(new ArrayList<>());
        }
        Activity activity = findActivityById(communicationActivityDTO.getActivityId());
        activity.setCommunicationActivityTab(communicationActivityTab);
        activityMongoRepository.save(activity);
        if(updateFromOrg) {
            activitySchedulerJobService.registerJobForActivityCutoff(newArrayList(activity));
        }
        return new ActivityTabsWrapper(communicationActivityTab);
    }

    public ActivityTabsWrapper getCommunicationTabOfActivity(BigInteger activityId) {
        Activity activity = findActivityById(activityId);
        return new ActivityTabsWrapper(activity.getCommunicationActivityTab());
    }
    // BONUS

    public ActivityTabsWrapper updateBonusTabOfActivity(BonusActivityDTO bonusActivityDTO) {
        Activity activity = findActivityById(bonusActivityDTO.getActivityId());
        BonusActivityTab bonusActivityTab = new BonusActivityTab(bonusActivityDTO.getBonusHoursType(), bonusActivityDTO.isOverRuleCtaWta());
        activity.setBonusActivityTab(bonusActivityTab);
        activityMongoRepository.save(activity);
        return new ActivityTabsWrapper(bonusActivityTab);
    }

    public ActivityTabsWrapper getBonusTabOfActivity(BigInteger activityId) {
        Activity activity = findActivityById(activityId);
        return new ActivityTabsWrapper(activity.getBonusActivityTab());
    }

    public ActivityTabsWrapper updateSkillTabOfActivity(SkillActivityDTO skillActivityDTO) {
        Activity activity = findActivityById(skillActivityDTO.getActivityId());
        SkillActivityTab skillActivityTab = new SkillActivityTab(skillActivityDTO.getActivitySkills());
        activity.setSkillActivityTab(skillActivityTab);
        activityMongoRepository.save(activity);
        return new ActivityTabsWrapper(skillActivityTab);
    }

    public ActivityTabsWrapper getSkillTabOfActivity(BigInteger activityId) {
        Activity activity = findActivityById(activityId);
        return new ActivityTabsWrapper(activity.getSkillActivityTab());
    }

    public void updateOrgMappingDetailOfActivity(OrganizationMappingDTO organizationMappingDTO, BigInteger activityId) {
        Activity activity = findActivityById(activityId);
        boolean isSuccess = userIntegrationService.verifyOrganizationExpertizeAndRegions(organizationMappingDTO);
        if (!isSuccess) {
            exceptionService.dataNotFoundException(MESSAGE_PARAMETERS_INCORRECT);
        }
        activity.setRegions(organizationMappingDTO.getRegions());
        activity.setExpertises(organizationMappingDTO.getExpertises());
        activity.setOrganizationSubTypes(organizationMappingDTO.getOrganizationSubTypes());
        activity.setOrganizationTypes(organizationMappingDTO.getOrganizationTypes());
        activity.setLevels(organizationMappingDTO.getLevel());
        activity.setEmploymentTypes(organizationMappingDTO.getEmploymentTypes());
        activityMongoRepository.save(activity);
        if (activity.getUnitId() != null) {
            plannerSyncService.publishActivity(activity.getUnitId(), activity, IntegrationOperation.UPDATE);
        }
    }

    public OrganizationMappingDTO getOrgMappingDetailOfActivity(BigInteger activityId) {
        Activity activity = findActivityById(activityId);
        OrganizationMappingDTO organizationMappingDTO = new OrganizationMappingDTO();
        organizationMappingDTO.setOrganizationSubTypes(activity.getOrganizationSubTypes());
        organizationMappingDTO.setExpertises(activity.getExpertises());
        organizationMappingDTO.setRegions(activity.getRegions());
        organizationMappingDTO.setLevel(activity.getLevels());
        organizationMappingDTO.setOrganizationTypes(activity.getOrganizationTypes());
        organizationMappingDTO.setEmploymentTypes(activity.getEmploymentTypes());
        return organizationMappingDTO;

    }

    public ActivityWithUnitIdDTO getActivityByUnitId(long unitId) {
        OrganizationTypeAndSubTypeDTO organizationTypeAndSubTypeDTO = userIntegrationService.getOrganizationTypeAndSubTypeByUnitId(unitId);
        ActivityWithUnitIdDTO activityWithUnitIdDTO = new ActivityWithUnitIdDTO();
        if (!organizationTypeAndSubTypeDTO.isParent()) {
            List<ActivityTagDTO> activities = activityMongoRepository.findAllActivityByParentOrganization(organizationTypeAndSubTypeDTO.getParentOrganizationId());
            activityWithUnitIdDTO.setActivityDTOList(activities);
            activityWithUnitIdDTO.setUnitId(organizationTypeAndSubTypeDTO.getParentOrganizationId());
            return activityWithUnitIdDTO;
        } else {
            List<Long> orgSubTypeIds = organizationTypeAndSubTypeDTO.getOrganizationSubTypes();
            List<Long> orgTypeIds = organizationTypeAndSubTypeDTO.getOrganizationTypes();
            List<ActivityTagDTO> activities = Collections.emptyList();
            if (!orgTypeIds.isEmpty() || !orgSubTypeIds.isEmpty()) {
                activities = activityMongoRepository.findAllActivitiesByOrganizationType(orgTypeIds, orgSubTypeIds);
            }
            activityWithUnitIdDTO.setActivityDTOList(activities);
            activityWithUnitIdDTO.setUnitId(organizationTypeAndSubTypeDTO.getUnitId());
            return activityWithUnitIdDTO;
        }
    }

    public ActivityTabsWrapper updateOptaPlannerSettingsTabOfActivity(BigInteger activityId, OptaPlannerSettingActivityTab optaPlannerSettingActivityTab) {
        Activity activity = findActivityById(activityId);
        activity.setOptaPlannerSettingActivityTab(optaPlannerSettingActivityTab);
        activityMongoRepository.save(activity);
        return new ActivityTabsWrapper(optaPlannerSettingActivityTab);
    }

    public ActivityTabsWrapper getOptaPlannerSettingsTabOfActivity(BigInteger activityId) {
        Activity activity = findActivityById(activityId);
        return new ActivityTabsWrapper(activity.getOptaPlannerSettingActivityTab());
    }

    public ActivityTabsWrapper getCtaAndWtaSettingsTabOfActivity(BigInteger activityId) {
        Activity activity = activityMongoRepository.findOne(activityId);
        return new ActivityTabsWrapper(activity.getCtaAndWtaSettingsActivityTab());
    }

    public ActivityTabsWrapper updateCtaAndWtaSettingsTabOfActivity(CTAAndWTASettingsActivityTabDTO ctaAndWtaSettingsActivityTabDTO) {
        Activity activity =findActivityById(new BigInteger(String.valueOf(ctaAndWtaSettingsActivityTabDTO.getActivityId())));
        CTAAndWTASettingsActivityTab ctaAndWtaSettingsActivityTab = new CTAAndWTASettingsActivityTab(ctaAndWtaSettingsActivityTabDTO.isEligibleForCostCalculation());
        activity.setCtaAndWtaSettingsActivityTab(ctaAndWtaSettingsActivityTab);
        activityMongoRepository.save(activity);
        return new ActivityTabsWrapper(ctaAndWtaSettingsActivityTab);
    }

    public PhaseActivityDTO getActivityAndPhaseByUnitId(long unitId) {
        LOGGER.info("fetching details of activity and unit");
        SelfRosteringMetaData publicHolidayDayTypeWrapper = getSelfRosteringMetaData(unitId);
        List<DayType> dayTypes = publicHolidayDayTypeWrapper.getDayTypes();
        LocalDate date = LocalDate.now();
        int year = date.getYear();
        TemporalField weekOfWeekBasedYear = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear();
        int currentWeek = date.get(weekOfWeekBasedYear);
        // Set access Role of staff
        ReasonCodeWrapper reasonCodeWrapper = publicHolidayDayTypeWrapper.getReasonCodeWrapper();
        List<PhaseDTO> phaseDTOs = phaseService.getApplicablePlanningPhasesByOrganizationId(unitId, Sort.Direction.DESC);
        List<PhaseWeeklyDTO> phaseWeeklyDTOS = getPhaseWeeklyDTO(phaseDTOs,currentWeek,year);
        // Creating dummy next remaining 2 years as PHASE with lowest sequence
        createDummyPhase(year, currentWeek, phaseDTOs, phaseWeeklyDTOS);
        return getPhaseActivityDTO(unitId, publicHolidayDayTypeWrapper, dayTypes, reasonCodeWrapper, phaseDTOs, phaseWeeklyDTOS);

    }

    private PhaseActivityDTO getPhaseActivityDTO(long unitId, SelfRosteringMetaData publicHolidayDayTypeWrapper, List<DayType> dayTypes, ReasonCodeWrapper reasonCodeWrapper, List<PhaseDTO> phaseDTOs, List<PhaseWeeklyDTO> phaseWeeklyDTOS) {
        List<ActivityWithCompositeDTO> activities = activityMongoRepository.findAllActivityByUnitIdWithCompositeActivities(unitId);
        List<PhaseSettingsActivityTab> phaseSettingsActivityTab = activityMongoRepository.findActivityIdAndStatusByUnitAndAccessGroupIds(unitId, new ArrayList<>(reasonCodeWrapper.getUserAccessRoleDTO().getAccessGroupIds()));
        List<ShiftTemplateDTO> shiftTemplates = shiftTemplateService.getAllShiftTemplates(unitId);
        PlanningPeriodDTO planningPeriodDTO = planningPeriodService.findStartDateAndEndDateOfPlanningPeriodByUnitId(unitId);
        PlanningPeriod firstRequestPlanningPeriod = planningPeriodMongoRepository.findFirstRequestPhasePlanningPeriodByUnitId(unitId);
        LocalDate firstRequestPhasePlanningPeriodEndDate = isNotNull(firstRequestPlanningPeriod) ? firstRequestPlanningPeriod.getEndDate() : null;
        List<PresenceTypeDTO> plannedTimes = plannedTimeTypeService.getAllPresenceTypeByCountry(UserContext.getUserDetails().getCountryId());
        List<ActivityConfiguration> activityConfigurations = activityConfigurationService.findAllByUnitIdAndDeletedFalse(unitId);
        return new PhaseActivityDTO(activities, phaseWeeklyDTOS, dayTypes, reasonCodeWrapper.getUserAccessRoleDTO(), shiftTemplates, phaseDTOs, phaseService.getActualPhasesByOrganizationId(unitId), reasonCodeWrapper.getReasonCodes(), planningPeriodDTO.getStartDate(), planningPeriodDTO.getEndDate(),
                publicHolidayDayTypeWrapper.getPublicHolidays(), firstRequestPhasePlanningPeriodEndDate, plannedTimes, phaseSettingsActivityTab, copyPropertiesOfCollectionByMapper(activityConfigurations, ActivityConfigurationDTO.class));
    }

    private SelfRosteringMetaData getSelfRosteringMetaData(long unitId) {
        SelfRosteringMetaData publicHolidayDayTypeWrapper = userIntegrationService.getPublicHolidaysDayTypeAndReasonCodeByUnitId(unitId);
        if (!Optional.ofNullable(publicHolidayDayTypeWrapper).isPresent()) {
            exceptionService.internalServerError(MESSAGE_SELFROSTERING_METADATA_NULL);
        }
        return publicHolidayDayTypeWrapper;
    }

    private List<PhaseWeeklyDTO> getPhaseWeeklyDTO(List<PhaseDTO> phaseDTOs,int currentWeek,int year) {
        ArrayList<PhaseWeeklyDTO> phaseWeeklyDTOS = new ArrayList<>();
        for (PhaseDTO phaseObj : phaseDTOs) {
            if (phaseObj.getDurationType().equals(DurationType.WEEKS)) {
                for (int i = 0; i < phaseObj.getDuration(); i++) {
                    PhaseWeeklyDTO tempPhaseObj = phaseObj.buildWeekDTO();
                    tempPhaseObj.setWeekCount(++currentWeek);
                    tempPhaseObj.setYear(year);
                    if (currentWeek >= 52) {
                        year = year + 1;
                        currentWeek = 0;
                    }
                    phaseWeeklyDTOS.add(tempPhaseObj);
                }
            }
        }
        return phaseWeeklyDTOS;
    }


    private void createDummyPhase(int year, int currentWeek, List<PhaseDTO> phaseDTOs, List<PhaseWeeklyDTO> phaseWeeklyDTOS) {
        if (isCollectionNotEmpty(phaseDTOs)) {
            int indexOfPhaseWithLowestSeq = phaseDTOs.size() - 1;
            for (int start = phaseWeeklyDTOS.size(); start <= 104; start++) {
                PhaseWeeklyDTO tempPhaseObj = phaseDTOs.get(indexOfPhaseWithLowestSeq).buildWeekDTO();
                tempPhaseObj.setWeekCount(++currentWeek);
                tempPhaseObj.setYear(year);
                if (currentWeek >= 52) {
                    year = year + 1;
                    currentWeek = 0;
                }
                phaseWeeklyDTOS.add(tempPhaseObj);
            }
        }
    }

    public GeneralActivityTab addIconInActivity(BigInteger activityId, MultipartFile file) throws IOException {
        Activity activity =findActivityById(activityId);
        byte[] bytes = file.getBytes();
        String modifiedFileName = System.currentTimeMillis() + file.getOriginalFilename().substring(file.getOriginalFilename().length() - 4);
        Path path = Paths.get(ACTIVITY_TYPE_IMAGE_PATH + modifiedFileName);
        Files.write(path, bytes);
        activity.getGeneralActivityTab().setOriginalIconName(file.getOriginalFilename());
        activity.getGeneralActivityTab().setModifiedIconName(modifiedFileName);
        activityMongoRepository.save(activity);
        return activity.getGeneralActivityTab();
    }

    public boolean deleteCountryActivity(BigInteger activityId) {
        Activity activity =findActivityById(activityId);
        if (activity.getState().equals(ActivityStateEnum.LIVE)) {
            exceptionService.actionNotPermittedException(EXCEPTION_ALREADYINUSE, ACTIVITY);
        }
        activity.setDeleted(true);
        activityMongoRepository.save(activity);
        return true;
    }

    public List<Activity> createActivitiesFromTimeCare(GetAllActivitiesResponse getAllActivitiesResponse, Long unitId, Long countryId, BigInteger presenceTimeTypeId, BigInteger absenceTimeTypeId) {
        List<TimeCareActivity> timeCareActivities = getAllActivitiesResponse.getGetAllActivitiesResult();
        List<String> externalIdsOfAllActivities = timeCareActivities.stream().map(TimeCareActivity::getId).collect(Collectors.toList());
        List<Activity> countryActivities = createActivatesForCountryFromTimeCare(timeCareActivities, unitId, countryId, externalIdsOfAllActivities, presenceTimeTypeId, absenceTimeTypeId);
        mapActivitiesInOrganization(countryActivities, unitId, externalIdsOfAllActivities);
        return countryActivities;
    }

    private List<Activity> createActivatesForCountryFromTimeCare(List<TimeCareActivity> timeCareActivities, Long unitId, Long countryId,
                                                                 List<String> externalIdsOfAllActivities, BigInteger presenceTimeTypeId, BigInteger absenceTimeTypeId) {
        OrganizationDTO organizationDTO = userIntegrationService.getOrganizationDTO(unitId);
        if (isNull(organizationDTO)) {
            exceptionService.dataNotFoundByIdException(MESSAGE_ORGANIZATION_ID);
        }
        ActivityCategory activityCategory = activityCategoryRepository.getCategoryByNameAndCountryAndDeleted("NONE", countryId, false);
        if (activityCategory == null) {
            activityCategory = new ActivityCategory("NONE", "", countryId, null);
            activityCategoryRepository.save(activityCategory);
        }
        Long orgType = organizationDTO.getOrganizationType().getId();
        List<Long> orgSubTypes = organizationDTO.getOrganizationSubTypes().stream().map(OrganizationTypeDTO::getId).collect(Collectors.toList());
        Set<String> skillsOfAllTimeCareActivity = timeCareActivities.stream().flatMap(timeCareActivity -> timeCareActivity.getArrayOfSkill().stream().
                map(skill -> skill)).collect(Collectors.toSet());
        List<Skill> skills = userIntegrationService.getSkillsByName(skillsOfAllTimeCareActivity, countryId);
        List<Activity> activitiesByExternalIds = activityMongoRepository.findByExternalIdIn(externalIdsOfAllActivities);
        List<PhaseDTO> phases = phaseService.getPhasesByCountryId(countryId);
        GlideTimeSettingsDTO glideTimeSettingsDTO = glideTimeSettingsService.getGlideTimeSettings(countryId);
        List<Activity> activities = getActivitiesByTimeCareActivity(timeCareActivities, countryId, presenceTimeTypeId, absenceTimeTypeId, activityCategory, orgType, orgSubTypes, skills, activitiesByExternalIds, phases, glideTimeSettingsDTO);
        activityMongoRepository.saveEntities(activities);
        return activities;
    }

    private List<Activity> getActivitiesByTimeCareActivity(List<TimeCareActivity> timeCareActivities, Long countryId, BigInteger presenceTimeTypeId, BigInteger absenceTimeTypeId, ActivityCategory activityCategory, Long orgType, List<Long> orgSubTypes, List<Skill> skills, List<Activity> activitiesByExternalIds, List<PhaseDTO> phases, GlideTimeSettingsDTO glideTimeSettingsDTO) {
        List<Activity> activities = new ArrayList<>(timeCareActivities.size());
        for (TimeCareActivity timeCareActivity : timeCareActivities) {
            Activity activity = initializeTimeCareActivities(timeCareActivity, orgType, orgSubTypes, countryId,
                    glideTimeSettingsDTO, phases, activitiesByExternalIds, activityCategory, skills, presenceTimeTypeId, absenceTimeTypeId);
            TimeType timeType = timeTypeMongoRepository.findOneById(activity.getBalanceSettingsActivityTab().getTimeTypeId());
            if (!Optional.ofNullable(timeType).isPresent()) {
                exceptionService.dataNotFoundByIdException(MESSAGE_ACTIVITY_TIMETYPE_NOTFOUND);
            }
            activity.getBalanceSettingsActivityTab().setTimeType(timeType.getSecondLevelType());
            activities.add(activity);
        }
        return activities;
    }

    private void mapActivitiesInOrganization(List<Activity> countryActivities, Long unitId, List<String> externalIds) {
        List<Activity> unitActivities = activityMongoRepository.findByUnitIdAndExternalIdInAndDeletedFalse(unitId, externalIds);
        List<Activity> organizationActivities = new ArrayList<>();
        for (Activity countryActivity : countryActivities) {
            Optional<Activity> result = unitActivities.stream().filter(unitActivity -> unitActivity.getExternalId().equals(countryActivity.getExternalId())).findFirst();
            if (!result.isPresent()) {
                Activity activity = SerializationUtils.clone(countryActivity);
                activity.setId(null);
                activity.setParentId(countryActivity.getId());
                activity.setCountryParentId(countryActivity.getId());
                activity.setUnitId(unitId);
                activity.setParentActivity(false);
                activity.setOrganizationTypes(null);
                activity.setState(null);
                activity.setOrganizationSubTypes(null);
                activity.setLevels(null);
                activity.setRegions(null);
                activity.setCountryId(null);
                organizationActivities.add(activity);
            }
        }
        if (!organizationActivities.isEmpty()) {
            activityMongoRepository.saveEntities(organizationActivities);
        }
    }

    public NotesActivityTab addDocumentInNotesTab(BigInteger activityId, MultipartFile file) throws IOException {
        Activity activity =findActivityById(activityId);
        byte[] bytes = file.getBytes();
        String modifiedFileName = System.currentTimeMillis() + file.getOriginalFilename().substring(file.getOriginalFilename().length() - 4);
        Path path = Paths.get(ACTIVITY_TYPE_IMAGE_PATH + modifiedFileName);
        Files.write(path, bytes);
        activity.getNotesActivityTab().setOriginalDocumentName(file.getOriginalFilename());
        activity.getNotesActivityTab().setModifiedDocumentName(modifiedFileName);
        activityMongoRepository.save(activity);
        return activity.getNotesActivityTab();
    }

    public Boolean publishActivity(BigInteger activityId) {
        Activity activity =findActivityById(activityId);
        if (activity.getState().equals(ActivityStateEnum.PUBLISHED) || activity.getState().equals(ActivityStateEnum.LIVE)) {
            exceptionService.actionNotPermittedException(MESSAGE_ACTIVITY_PUBLISHED, activityId);
        }
        if (activity.getBalanceSettingsActivityTab().getTimeTypeId() == null) {
            exceptionService.actionNotPermittedException(MESSAGE_ACTIVITY_TIMETYPE_ABSENT, activity.getName());
        }
        activity.setState(ActivityStateEnum.PUBLISHED);
        activityMongoRepository.save(activity);
        return true;
    }

    public ActivityDTO copyActivityDetails(Long countryId, BigInteger activityId, ActivityDTO activityDTO) {
        //Need to know why we are returning object here as we can also return a simple boolean to check whether activity exist or not
        Activity activity = activityMongoRepository.
                findByNameIgnoreCaseAndCountryIdAndByDate(activityDTO.getName().trim(), countryId, activityDTO.getStartDate(), activityDTO.getEndDate());
        if (Optional.ofNullable(activity).isPresent() && activityDTO.getStartDate().isBefore(activity.getGeneralActivityTab().getStartDate())) {
            exceptionService.actionNotPermittedException(MESSAGE_ACTIVITY_OVERLAPING);
        }
        if (Optional.ofNullable(activity).isPresent()) {
            exceptionService.dataNotFoundException(activity.getGeneralActivityTab().getEndDate() == null ? MESSAGE_ACTIVITY_ENDDATE_REQUIRED : MESSAGE_ACTIVITY_ACTIVE_ALREADYEXISTS);
        }
        Activity activityFromDatabase = activityMongoRepository.findOne(activityId);
        if (isNull(activityFromDatabase) || activityFromDatabase.isDeleted() || !countryId.equals(activityFromDatabase.getCountryId())) {
            exceptionService.dataNotFoundByIdException(MESSAGE_ACTIVITY_ID, activityId);
        }

        Activity activityCopied = ObjectMapperUtils.copyPropertiesByMapper(activityFromDatabase, Activity.class);
        activityCopied.setId(null);
        activityCopied.setName(activityDTO.getName().trim());
        activityCopied.setCountryParentId(activityFromDatabase.getCountryParentId() == null ? activityFromDatabase.getId() : activityFromDatabase.getCountryParentId());
        activityCopied.getGeneralActivityTab().setName(activityDTO.getName().trim());
        activityCopied.getGeneralActivityTab().setStartDate(activityDTO.getStartDate());
        activityCopied.setState(ActivityStateEnum.DRAFT);
        activityCopied.getGeneralActivityTab().setEndDate(activityDTO.getEndDate());
        activityMongoRepository.save(activityCopied);
        activityDTO.setId(activityCopied.getId());
        activityDTO.setActivityCanBeCopied(true);
        return activityDTO;
    }

    public ActivityTabsWrapper getLocationsTabOfActivity(BigInteger activityId) {
        Activity activity = findActivityById(activityId);
        return new ActivityTabsWrapper(activity.getLocationActivityTab());
    }

    public ActivityTabsWrapper updateLocationsTabOfActivity(LocationActivityTabDTO locationActivityTabDTO) {

        Activity activity =findActivityById(locationActivityTabDTO.getActivityId());
        LocationActivityTab locationActivityTab = new LocationActivityTab(locationActivityTabDTO.getGlideTimeForCheckIn(), locationActivityTabDTO.getGlideTimeForCheckOut());
        activity.setLocationActivityTab(locationActivityTab);
        activityMongoRepository.save(activity);
        return new ActivityTabsWrapper(locationActivityTab);
    }

    public Map<String, TranslationInfo> updateTranslationData(BigInteger activityId, Map<String, TranslationInfo> activityTranslationDTO){
        LOGGER.debug("activity Id received is {}",activityId);
        Activity activity = activityMongoRepository.findActivityByIdAndEnabled(activityId);
        if(isNull(activity)) {
            exceptionService.dataNotFoundException(MESSAGE_DATA_NOTFOUND);
        }
        return updateActivityTranslations(activity,activityTranslationDTO);
    }

    public Map<String, TranslationInfo> updateActivityTranslations(@NotNull Activity activity, Map<String, TranslationInfo> activityTranslationDTO){
        final Map<String, TranslationInfo> activityLanguageDetailsMap = activity.getTranslations();
        LOGGER.debug("activity preset language details {}",activityLanguageDetailsMap);
        activityTranslationDTO.forEach((s, translation) -> {
            LOGGER.debug("saving language details {} ",translation);
            activityLanguageDetailsMap.put(s, translation);
        });
        activity.setTranslations(activityLanguageDetailsMap);
        activityMongoRepository.save(activity);
        return activity.getTranslations();
    }


    public ActivityWithTimeTypeDTO getActivitiesWithTimeTypes(long countryId) {
        List<ActivityDTO> activityDTOS = activityMongoRepository.findAllActivitiesWithTimeTypes(countryId);
        List<TimeTypeDTO> timeTypeDTOS = timeTypeService.getAllTimeType(null, countryId);
        List<OpenShiftIntervalDTO> intervals = openShiftIntervalRepository.getAllByCountryIdAndDeletedFalse(countryId);
        List<CounterDTO> counters = counterRepository.getAllCounterBySupportedModule(ModuleType.OPEN_SHIFT);
        return new ActivityWithTimeTypeDTO(activityDTOS, timeTypeDTOS, intervals, counters);
    }

    public void updateBackgroundColorInShifts(String newTimeTypeColor, String existingTimeTypeColor,BigInteger timeTypeId) {
        if(!existingTimeTypeColor.equals(newTimeTypeColor)){
            new Thread(() -> {
                Set<BigInteger> activityIds = updateColorInActivity(newTimeTypeColor, timeTypeId);
                updateColorInShift(newTimeTypeColor,activityIds);
            }).start();

        }
    }

    private Set<BigInteger> updateColorInActivity(String newTimeTypeColor,BigInteger timeTypeId) {
        List<Activity> activities = activityMongoRepository.findAllByTimeTypeId(timeTypeId);
        if (isCollectionNotEmpty(activities)) {
            activities.forEach(activity -> activity.getGeneralActivityTab().setBackgroundColor(newTimeTypeColor));
            activityMongoRepository.saveEntities(activities);
        }
        return activities.stream().map(MongoBaseEntity::getId).collect(Collectors.toSet());
    }

    private void updateColorInShift(String newTimeTypeColor,Set<BigInteger> activityIds) {
        List<Shift> shifts = shiftMongoRepository.findShiftByShiftActivityIdAndBetweenDate(activityIds,null,null,null);
        shifts.forEach(shift -> shift.getActivities().forEach(shiftActivity -> {
            updateBackgroundColorInShiftActivity(newTimeTypeColor, activityIds, shiftActivity);
            if(isNotNull(shift.getDraftShift())){
                shift.getDraftShift().getActivities().forEach(draftShiftActivity-> updateBackgroundColorInShiftActivity(newTimeTypeColor, activityIds, draftShiftActivity));
            }
        }));
        if(isCollectionNotEmpty(shifts)){
            shiftMongoRepository.saveEntities(shifts);
        }
    }

    private void updateBackgroundColorInShiftActivity(String newTimeTypeColor, Set<BigInteger> activitiyIds, ShiftActivity shiftActivity) {
        if(activitiyIds.contains(shiftActivity.getActivityId())){
            shiftActivity.setBackgroundColor(newTimeTypeColor);
        }
        shiftActivity.getChildActivities().forEach(childActivity -> {
            if(activitiyIds.contains(childActivity.getActivityId())){
                childActivity.setBackgroundColor(newTimeTypeColor);
            }
        });
    }

    public List<ActivityDTO> getActivitiesWithCategories(long unitId) {
        return activityMongoRepository.findAllActivityByUnitId(unitId, false);
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

    public void validateActivityTimeRules( Short shortestTime, Short longestTime) {
        if (shortestTime != null && longestTime != null && shortestTime > longestTime) {
            exceptionService.actionNotPermittedException(SHORTEST_TIME_GREATER_LONGEST);
        }
    }

    public boolean removeAttachementsFromActivity(BigInteger activityId, boolean removeNotes) {
        Activity activity = findActivityById(activityId);
        if (removeNotes) {
            activity.getNotesActivityTab().setOriginalDocumentName(null);
            activity.getNotesActivityTab().setModifiedDocumentName(null);
        } else {
            activity.getGeneralActivityTab().setOriginalIconName(null);
            activity.getGeneralActivityTab().setModifiedIconName(null);
        }
        activityMongoRepository.save(activity);
        return true;
    }

    public List<ActivityDTO> findAllActivityByDeletedFalseAndUnitId(List<Long> unitIds) {
        return activityMongoRepository.findAllActivityByDeletedFalseAndUnitId(unitIds);
    }

    //remove expertise from activity via schedular job
    public boolean unassighExpertiseFromActivities(BigInteger expertiseId) {
        LOGGER.info("remove expertise from activities by job");
        activityMongoRepository.unassignExpertiseFromActivitiesByExpertiesId(expertiseId.longValue());
        LOGGER.info("successfully remove expertise from activities by job");
        return true;
    }

    public Map<BigInteger, ActivityWrapper> getActivityWrapperMap(List<Shift> shifts, ShiftDTO shiftDTO) {
        Set<BigInteger> activityIds = new HashSet<>();
        for (Shift shift : shifts) {
            getActivityIdsByShift(activityIds, shift);
            if(isNotNull(shift.getDraftShift())){
                getActivityIdsByShift(activityIds, shift.getDraftShift());
            }
        }
        if (isNotNull(shiftDTO)) {
            activityIds.addAll(shiftDTO.getActivities().stream().flatMap(shiftActivityDTO -> shiftActivityDTO.getChildActivities().stream()).map(ShiftActivityDTO::getActivityId).collect(Collectors.toList()));
            activityIds.addAll(shiftDTO.getActivities().stream().map(ShiftActivityDTO::getActivityId).collect(Collectors.toList()));
            activityIds.addAll(shiftDTO.getBreakActivities().stream().map(ShiftActivityDTO::getActivityId).collect(Collectors.toList()));
        }
        List<ActivityWrapper> activities = activityMongoRepository.findActivitiesAndTimeTypeByActivityId(activityIds);
        return activities.stream().collect(Collectors.toMap(k -> k.getActivity().getId(), v -> v));
    }

    private void getActivityIdsByShift(Set<BigInteger> activityIds, Shift shift) {
        activityIds.addAll(shift.getActivities().stream().flatMap(shiftActivity -> shiftActivity.getChildActivities().stream()).map(ShiftActivity::getActivityId).collect(Collectors.toList()));
        activityIds.addAll(shift.getActivities().stream().map(ShiftActivity::getActivityId).collect(Collectors.toList()));
        if(isCollectionNotEmpty(shift.getBreakActivities())){
            activityIds.addAll(shift.getBreakActivities().stream().map(ShiftActivity::getActivityId).collect(Collectors.toList()));
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

    public Set<BigInteger> getAbsenceActivityIds(Long unitId, Date date) {
        Phase phase = phaseService.getCurrentPhaseByUnitIdAndDate(unitId, date, null);
        List<Activity> activities = activityMongoRepository.findAllAbsenceActivities(unitId,newHashSet(CommonConstants.FULL_DAY_CALCULATION, CommonConstants.FULL_WEEK), phase.getId());
        return activities.stream().map(MongoBaseEntity::getId).collect(Collectors.toSet());
    }

    public Activity findActivityById(BigInteger activityId){
        return activityMongoRepository.findById(activityId).orElseThrow(()->new DataNotFoundByIdException(convertMessage(MESSAGE_ACTIVITY_ID, activityId)));
    }

    public List<ActivityDTO> getActivitiesByUnitId(Long unitId){
        return activityMongoRepository.getActivitiesByUnitId(unitId);
    }
}

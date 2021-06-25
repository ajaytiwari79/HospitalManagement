package com.kairos.service.activity;

import com.kairos.commons.custom_exception.DataNotFoundByIdException;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.constants.CommonConstants;
import com.kairos.dto.TranslationInfo;
import com.kairos.dto.activity.activity.ActivityDTO;
import com.kairos.dto.activity.activity.ActivityWithTimeTypeDTO;
import com.kairos.dto.activity.activity.OrganizationActivityDTO;
import com.kairos.dto.activity.activity.activity_tabs.*;
import com.kairos.dto.activity.counter.configuration.CounterDTO;
import com.kairos.dto.activity.counter.enums.ModuleType;
import com.kairos.dto.activity.open_shift.OpenShiftIntervalDTO;
import com.kairos.dto.activity.presence_type.PresenceTypeDTO;
import com.kairos.dto.activity.presence_type.PresenceTypeWithTimeTypeDTO;
import com.kairos.dto.activity.time_type.TimeTypeDTO;
import com.kairos.dto.user.access_permission.AccessGroupRole;
import com.kairos.dto.user.country.agreement.cta.cta_response.ActivityCategoryDTO;
import com.kairos.dto.user.country.agreement.cta.cta_response.DayTypeDTO;
import com.kairos.dto.user.country.agreement.cta.cta_response.EmploymentTypeDTO;
import com.kairos.dto.user.country.day_type.DayTypeEmploymentTypeWrapper;
import com.kairos.dto.user.country.tag.TagDTO;
import com.kairos.dto.user.organization.OrganizationTypeAndSubTypeDTO;
import com.kairos.dto.user.organization.skill.Skill;
import com.kairos.dto.user_context.UserContext;
import com.kairos.enums.ActivityStateEnum;
import com.kairos.enums.IntegrationOperation;
import com.kairos.enums.PriorityFor;
import com.kairos.enums.TimeTypeEnum;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.activity.TimeType;
import com.kairos.persistence.model.activity.tabs.*;
import com.kairos.persistence.model.activity.tabs.rules_activity_tab.ActivityRulesSettings;
import com.kairos.persistence.model.activity.tabs.rules_activity_tab.SicknessSetting;
import com.kairos.persistence.model.common.MongoBaseEntity;
import com.kairos.persistence.model.period.PlanningPeriod;
import com.kairos.persistence.model.phase.Phase;
import com.kairos.persistence.repository.activity.ActivityCategoryRepository;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.counter.CounterRepository;
import com.kairos.persistence.repository.open_shift.OpenShiftIntervalRepository;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.persistence.repository.staffing_level.StaffingLevelMongoRepository;
import com.kairos.persistence.repository.tag.TagMongoRepository;
import com.kairos.persistence.repository.time_type.TimeTypeMongoRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.day_type.DayTypeService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.glide_time.GlideTimeSettingsService;
import com.kairos.service.integration.PlannerSyncService;
import com.kairos.service.kpermissions.ActivityPermissionService;
import com.kairos.service.organization.OrganizationActivityService;
import com.kairos.service.period.PlanningPeriodService;
import com.kairos.service.phase.PhaseService;
import com.kairos.service.scheduler_service.ActivitySchedulerJobService;
import com.kairos.service.shift.ShiftHelperService;
import com.kairos.service.shift.ShiftService;
import com.kairos.service.staffing_level.StaffingLevelService;
import com.kairos.service.unit_settings.ActivityRankingService;
import com.kairos.wrapper.activity.ActivitySettingsWrapper;
import com.kairos.wrapper.activity.ActivityTagDTO;
import com.kairos.wrapper.activity.ActivityWithCompositeDTO;
import com.kairos.wrapper.shift.ActivityWithUnitIdDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.constants.ActivityMessagesConstants.*;
import static com.kairos.enums.ActivityStateEnum.PUBLISHED;
import static com.kairos.enums.TimeTypeEnum.ABSENCE;
import static com.kairos.service.activity.ActivityUtil.buildActivity;
import static com.kairos.service.activity.ActivityUtil.getCutoffInterval;
import static com.kairos.service.shift.ShiftValidatorService.convertMessage;

/*** Created by pawanmandhan on 17/8/17.*/
@Transactional
@Service
public class ActivityService {
    public static final String XXXXX = "XXXXX";
    public static final String NAME = "name";
    public static final String ULTRA_SHORT_NAME = "ultraShortName";
    @Inject private ActivityMongoRepository activityMongoRepository;
    @Inject private ActivityCategoryRepository activityCategoryRepository;
    @Inject private ShiftService shiftService;
    @Inject private PhaseService phaseService;
    @Inject private PlannedTimeTypeService plannedTimeTypeService;
    @Inject private TagMongoRepository tagMongoRepository;
    @Inject @Lazy private OrganizationActivityService organizationActivityService;
    @Inject private ShiftHelperService shiftHelperService;
    @Inject private TimeTypeMongoRepository timeTypeMongoRepository;
    @Inject private TimeTypeService timeTypeService;
    @Inject private PlannerSyncService plannerSyncService;
    @Inject private ExceptionService exceptionService;
    @Inject private OpenShiftIntervalRepository openShiftIntervalRepository;
    @Inject private UserIntegrationService userIntegrationService;
    @Inject private CounterRepository counterRepository;
    @Inject private GlideTimeSettingsService glideTimeSettingsService;
    @Inject private ShiftMongoRepository shiftMongoRepository;

    @Inject @Lazy private StaffingLevelService staffingLevelService;
    @Inject private ActivitySchedulerJobService activitySchedulerJobService;
    @Inject private ActivitySettingsService activitySettingsService;
    @Inject private StaffActivityDetailsService staffActivityDetailsService;
    @Inject private PlanningPeriodService planningPeriodService;
    @Inject private DayTypeService dayTypeService;
    @Inject private ActivityRankingService activityRankingService;
    @Inject private StaffingLevelMongoRepository staffingLevelMongoRepository;
    @Inject private ActivityPermissionService activityPermissionService;
    @Inject @Lazy private ActivityHelperService activityHelperService;

    private static final Logger LOGGER = LoggerFactory.getLogger(ActivityService.class);

    @CacheEvict(value = "findAllActivityByCountry", key = "#countryId")
    public ActivityTagDTO createActivity(Long countryId, ActivityDTO activityDTO) {
        if (activityDTO.getEndDate() != null && activityDTO.getEndDate().isBefore(activityDTO.getStartDate())) {
            exceptionService.actionNotPermittedException(MESSAGE_ACTIVITY_ENDDATE_GREATERTHAN_STARTDATE);
        }
        Activity activity = activityMongoRepository.findByNameIgnoreCaseAndCountryIdAndByDate(activityDTO.getName().trim(), countryId, activityDTO.getStartDate(), activityDTO.getEndDate());
        if (Optional.ofNullable(activity).isPresent() && activityDTO.getStartDate().isBefore(activity.getActivityGeneralSettings().getStartDate())) {
            exceptionService.actionNotPermittedException(MESSAGE_ACTIVITY_OVERLAPING);
        }
        if (Optional.ofNullable(activity).isPresent()) {
            exceptionService.dataNotFoundException(activity.getActivityGeneralSettings().getEndDate() == null ? MESSAGE_ACTIVITY_ENDDATE_REQUIRED : MESSAGE_ACTIVITY_ACTIVE_ALREADYEXISTS);
        }
        activity = buildActivity(activityDTO);
        activityHelperService.initializeActivitySettings(activity, countryId, activityDTO);
        activityMongoRepository.save(activity);
        List<TagDTO> tags = tagMongoRepository.getTagsById(activityDTO.getTags());
        ActivityTagDTO activityTagDTO = new ActivityTagDTO();
        activityTagDTO.buildActivityTagDTO(activity, tags);
        return activityTagDTO;
    }


    @Cacheable(value = "findAllActivityByCountry", key = "#countryId", cacheManager = "cacheManager")
    public Map<String, Object> findAllActivityByCountry(long countryId) {
        Map<String, Object> response = new HashMap<>();
        List<ActivityTagDTO> activityTagDTOS = activityMongoRepository.findAllActivityByCountry(countryId);
        activityTagDTOS.parallelStream().forEach(activityTagDTO -> activityTagDTO.setActivityCanBeCopied(true));
        response.put("activities", activityTagDTOS);
        response.put("activityCategories", activityCategoryRepository.findByCountryId(countryId));
        return response;
    }

    public Set<BigInteger> checkActivityAllowForChildActivities(List<ActivityTagDTO> activities, ActivityWithCompositeDTO activityWithCompositeDTO) {
        Set<BigInteger> allowChildActivityIds = new HashSet<>();
        Set<BigInteger> childActivitiesIds = activities.stream().flatMap(activityTagDTO -> activityTagDTO.getChildActivityIds().stream()).collect(Collectors.toSet());
        for (ActivityTagDTO activity : activities) {
            if (!activity.getId().equals(activityWithCompositeDTO.getId()) && isCollectionEmpty(activity.getChildActivityIds()) && isNull(activity.getParentActivityId())) {
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

    public List<ActivityCategory> findAllActivityCategoriesByCountry(Collection<BigInteger> activityCategoriesIds) {
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
        List<Long> childUnitIds = userIntegrationService.getAllOrganizationIds(UserContext.getUnitId());
        if (isCollectionNotEmpty(childUnitIds) && activityMongoRepository.existsByParentIdAndDeletedFalse(activity.getId(), childUnitIds)) {
            exceptionService.actionNotPermittedException(ACTIVITY_USED_AT_UNIT);
        }
        long activityCount = shiftService.countByActivityId(activityId);

        if (activityCount > 0) {
            exceptionService.actionNotPermittedException(MESSAGE_ACTIVITY_TIMECAREACTIVITYTYPE, activity.getName());
        }
        boolean activityExistsInStaffingLevel=staffingLevelMongoRepository.activityExistsInStaffingLevel(activity.getUnitId(), activityId);
        if(activityExistsInStaffingLevel){
            exceptionService.actionNotPermittedException("activity.used.staffing.level");
        }
        activity.setDeleted(true);
        activityMongoRepository.save(activity);
        TimeType timeType = timeTypeService.getTimeTypeById(activity.getActivityBalanceSettings().getTimeTypeId());
        if(PriorityFor.PRESENCE.equals(timeType.getPriorityFor()) && !activity.isChildActivity()) {
            activityRankingService.addOrRemovePresenceActivityRanking(UserContext.getUnitId(), activity, false);
        }
        return true;
    }

    @CacheEvict(value={"getActivityMappingDetails","findAllActivityByUnitIdWithCompositeActivities"}, key="#unitId")
    public boolean deleteActivity(Long unitId, BigInteger activityId){
        return this.deleteActivity(activityId);
    }

    @CacheEvict(value = "findAllActivityByCountry",key = "#countryId")
    public boolean deleteActivity(BigInteger activityId,Long countryId){
        return this.deleteActivity(activityId);
    }

    @CacheEvict(value = "findAllActivityByCountry",allEntries = true)
    public ActivitySettingsWrapper updateGeneralTab(Long countryId, ActivityGeneralSettingsDTO generalDTO) {
        validateActivityDetails(countryId, generalDTO);
        Activity activity = findActivityById(generalDTO.getActivityId());
        generalDTO.setBackgroundColor(activity.getActivityGeneralSettings().getBackgroundColor());
        shiftHelperService.updateBackgroundColorInActivityAndShift(activity,null);
        ActivityGeneralSettings generalTab = ObjectMapperUtils.copyPropertiesByMapper(generalDTO, ActivityGeneralSettings.class);
        if (Optional.ofNullable(activity.getActivityGeneralSettings().getModifiedIconName()).isPresent()) {
            generalTab.setModifiedIconName(activity.getActivityGeneralSettings().getModifiedIconName());
        }
        if (Optional.ofNullable(activity.getActivityGeneralSettings().getOriginalIconName()).isPresent()) {
            generalTab.setOriginalIconName(activity.getActivityGeneralSettings().getOriginalIconName());
        }
        LocalDate oldEndDate = activity.getActivityGeneralSettings().getEndDate();
        activity.setActivityGeneralSettings(generalTab);
        activity.setName(generalTab.getName());
        activity.setTags(generalDTO.getTags());
        activity.setDescription(generalTab.getDescription());
        generalTab.setTags(null);
        GeneralActivityWithTagDTO generalActivityWithTagDTO = ObjectMapperUtils.copyPropertiesByMapper(generalTab, GeneralActivityWithTagDTO.class);
        generalActivityWithTagDTO.setTags(null);
        if (!activity.getTags().isEmpty()) {
            generalActivityWithTagDTO.setTags(tagMongoRepository.getTagsById(activity.getTags()));
            generalTab.setTags(activity.getTags());
        }
        updateBalanceSettingTab(generalDTO, activity);
        updateNotesTabOfActivity(generalDTO, activity);
        activityMongoRepository.save(activity);
        TimeType timeType = timeTypeService.getTimeTypeById(activity.getActivityBalanceSettings().getTimeTypeId());
        if(PriorityFor.ABSENCE.equals(timeType.getPriorityFor()) && PUBLISHED.equals(activity.getState()) && !activity.isChildActivity()){
            activityRankingService.updateEndDateOfAbsenceActivity(activity, oldEndDate);
        }
        return getActivitySettingsWrapper(activity, checkCountryAndFindActivityCategory(countryId), generalActivityWithTagDTO);
    }

    private void validateActivityDetails(Long countryId, ActivityGeneralSettingsDTO generalDTO) {
        if (generalDTO.getEndDate() != null && generalDTO.getEndDate().isBefore(generalDTO.getStartDate())) {
            exceptionService.actionNotPermittedException(MESSAGE_ACTIVITY_ENDDATE_GREATERTHAN_STARTDATE);
        }
        Activity isActivityAlreadyExists = activityMongoRepository.findByNameExcludingCurrentInCountryAndDate(generalDTO.getName().trim(), generalDTO.getActivityId(), countryId, generalDTO.getStartDate(), generalDTO.getEndDate());
        if (Optional.ofNullable(isActivityAlreadyExists).isPresent() && generalDTO.getStartDate().isBefore(isActivityAlreadyExists.getActivityGeneralSettings().getStartDate())) {
            exceptionService.actionNotPermittedException(MESSAGE_ACTIVITY_OVERLAPING);
        }
        if (Optional.ofNullable(isActivityAlreadyExists).isPresent()) {
            exceptionService.dataNotFoundException(isActivityAlreadyExists.getActivityGeneralSettings().getEndDate() == null ? MESSAGE_ACTIVITY_ENDDATE_REQUIRED : MESSAGE_ACTIVITY_ACTIVE_ALREADYEXISTS);
        }
        ActivityCategory activityCategory = activityCategoryRepository.getByIdAndNonDeleted(generalDTO.getCategoryId());
        if (activityCategory == null) {
            exceptionService.dataNotFoundByIdException(MESSAGE_CATEGORY_NOTEXIST);
        }
    }

    private ActivitySettingsWrapper getActivitySettingsWrapper(Activity activity, List<ActivityCategoryDTO> activityCategories, GeneralActivityWithTagDTO generalActivityWithTagDTO) {
        generalActivityWithTagDTO.setAddTimeTo(activity.getActivityBalanceSettings().getAddTimeTo());
        generalActivityWithTagDTO.setTimeTypeId(activity.getActivityBalanceSettings().getTimeTypeId());
        generalActivityWithTagDTO.setOnCallTimePresent(activity.getActivityBalanceSettings().isOnCallTimePresent());
        generalActivityWithTagDTO.setNegativeDayBalancePresent(activity.getActivityBalanceSettings().getNegativeDayBalancePresent());
        generalActivityWithTagDTO.setTimeType(activity.getActivityBalanceSettings().getTimeType());
        generalActivityWithTagDTO.setContent(activity.getActivityNotesSettings().getContent());
        generalActivityWithTagDTO.setOriginalDocumentName(activity.getActivityNotesSettings().getOriginalDocumentName());
        generalActivityWithTagDTO.setModifiedDocumentName(activity.getActivityNotesSettings().getModifiedDocumentName());
        generalActivityWithTagDTO.setBackgroundColor(activity.getActivityGeneralSettings().getBackgroundColor());
        return new ActivitySettingsWrapper(generalActivityWithTagDTO, activityCategories);
    }

    public ActivitySettingsWrapper getGeneralTabOfActivity(Long countryId, BigInteger activityId) {
        Activity activity = findActivityById(activityId);
        ActivityGeneralSettings generalTab = activity.getActivityGeneralSettings();
        generalTab.setTranslations(activity.getTranslations());
        generalTab.setTags(null);
        GeneralActivityWithTagDTO generalActivityWithTagDTO = ObjectMapperUtils.copyPropertiesByMapper(generalTab, GeneralActivityWithTagDTO.class);
        generalActivityWithTagDTO.setTags(null);
        if (!activity.getTags().isEmpty()) {
            generalActivityWithTagDTO.setTags(tagMongoRepository.getTagsById(activity.getTags()));
        }
        generalActivityWithTagDTO.setAddTimeTo(activity.getActivityBalanceSettings().getAddTimeTo());
        generalActivityWithTagDTO.setTimeTypeId(activity.getActivityBalanceSettings().getTimeTypeId());
        generalActivityWithTagDTO.setOnCallTimePresent(activity.getActivityBalanceSettings().isOnCallTimePresent());
        generalActivityWithTagDTO.setNegativeDayBalancePresent(activity.getActivityBalanceSettings().getNegativeDayBalancePresent());
        generalActivityWithTagDTO.setTimeType(activity.getActivityBalanceSettings().getTimeType());
        generalActivityWithTagDTO.setContent(activity.getActivityNotesSettings().getContent());
        generalActivityWithTagDTO.setOriginalDocumentName(activity.getActivityNotesSettings().getOriginalDocumentName());
        generalActivityWithTagDTO.setModifiedDocumentName(activity.getActivityNotesSettings().getModifiedDocumentName());
        ActivitySettingsWrapper activitySettingsWrapper = new ActivitySettingsWrapper(generalActivityWithTagDTO, checkCountryAndFindActivityCategory(countryId));
        List<PresenceTypeDTO> presenceTypeDTOS = plannedTimeTypeService.getAllPresenceTypeByCountry(countryId);
        PresenceTypeWithTimeTypeDTO presenceType = new PresenceTypeWithTimeTypeDTO(presenceTypeDTOS, countryId);
        activitySettingsWrapper.setPresenceTypeWithTimeType(presenceType);
        activitySettingsWrapper.setTimeTypes(timeTypeService.getAllTimeType(activity.getActivityBalanceSettings().getTimeTypeId(), countryId));
        return activitySettingsWrapper;
    }

    private List<ActivityCategoryDTO> checkCountryAndFindActivityCategory(Long countryId) {
        return activityCategoryRepository.findByCountryId(countryId);
    }

    public ActivityBalanceSettings updateBalanceSettingTab(ActivityGeneralSettingsDTO activityGeneralSettingsDTO, Activity activity) {
        TimeType timeType = timeTypeMongoRepository.findOneById(activityGeneralSettingsDTO.getTimeTypeId());
        if (!Optional.ofNullable(timeType).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_ACTIVITY_TIMETYPE_NOTFOUND);
        }
        if(isNotNull(activityGeneralSettingsDTO.getTimeTypeId()) && !activityGeneralSettingsDTO.getTimeTypeId().equals(activity.getActivityBalanceSettings().getTimeTypeId())){
            if (activity.getState().equals(PUBLISHED)) {
                exceptionService.actionNotPermittedException(MESSAGE_ACTIVITY_TIMETYPE_PUBLISHED, activity.getId());
            }
            setDataInActivity(activity, timeType);
        }
        shiftHelperService.updateBackgroundColorInActivityAndShift(activity, timeType);
        activity.getActivityGeneralSettings().setColorPresent(true);
        Long countryId = activity.getCountryId();
        if (countryId == null) {
            countryId = userIntegrationService.getCountryIdOfOrganization(activity.getUnitId());
        }
        updateBalanceSettingDetails(activityGeneralSettingsDTO, activity, timeType);
        updateActivityCategory(activity, countryId);
        activitySettingsService.updateTimeTypePathInActivity(activity);
        return activity.getActivityBalanceSettings();
    }

    private void setDataInActivity(Activity activity, TimeType timeType) {
        activity.setActivityPhaseSettings(timeType.getActivityPhaseSettings());
        activity.setActivityRulesSettings(timeType.getActivityRulesSettings());
        activity.setActivityTimeCalculationSettings(timeType.getActivityTimeCalculationSettings());
        activity.setEmploymentTypes(timeType.getEmploymentTypes());
        activity.setExpertises(timeType.getExpertises());
        activity.setOrganizationSubTypes(timeType.getOrganizationSubTypes());
        activity.setOrganizationTypes(timeType.getOrganizationTypes());
        activity.setActivitySkillSettings(timeType.getActivitySkillSettings());
        activity.setRegions(timeType.getRegions());
        activity.setLevels(timeType.getLevels());
        activity.getActivityRulesSettings().setSicknessSettingValid(timeType.isSicknessSettingValid());
        activity.getActivityRulesSettings().setSicknessSetting(timeType.getActivityRulesSettings().getSicknessSetting());
        activity.getActivityGeneralSettings().setBackgroundColor(timeType.getBackgroundColor());
    }

    private void updateBalanceSettingDetails(ActivityGeneralSettingsDTO activityGeneralSettingsDTO, Activity activity, TimeType timeType) {
        activity.getActivityBalanceSettings().setTimeTypeId(activityGeneralSettingsDTO.getTimeTypeId());
        activity.getActivityBalanceSettings().setTimeType(timeType.getSecondLevelType());
        activity.getActivityBalanceSettings().setTimeTypes(timeType.getTimeTypes());
        activity.getActivityBalanceSettings().setAddTimeTo(activityGeneralSettingsDTO.getAddTimeTo());
        activity.getActivityBalanceSettings().setOnCallTimePresent(activityGeneralSettingsDTO.isOnCallTimePresent());
        activity.getActivityBalanceSettings().setNegativeDayBalancePresent(activityGeneralSettingsDTO.getNegativeDayBalancePresent());
        activity.getActivityBalanceSettings().setPriorityFor(timeType.getPriorityFor());
        updateActivityCategory(activity, UserContext.getUserDetails().getCountryId());
    }

    public void updateActivityCategory(Activity activity, Long countryId) {
        TimeType timeType = timeTypeMongoRepository.findOneById(activity.getActivityBalanceSettings().getTimeTypeId(), countryId);
        if (isNull(timeType))
            exceptionService.dataNotFoundByIdException(MESSAGE_TIMETYPE_NOTFOUND);
        ActivityCategory category = activityCategoryRepository.getCategoryByTimeType(countryId, activity.getActivityBalanceSettings().getTimeTypeId());
        if (category == null) {
            category = new ActivityCategory(timeType.getLabel(), "", countryId, timeType.getId());
            activityCategoryRepository.save(category);
        }
        activity.getActivityGeneralSettings().setCategoryId(category.getId());
        activityMongoRepository.save(activity);
    }

    public TimeCalculationActivityDTO updateTimeCalculationTabOfActivity(TimeCalculationActivityDTO timeCalculationActivityDTO, boolean availableAllowActivity) {
        if (CommonConstants.FULL_WEEK.equals(timeCalculationActivityDTO.getMethodForCalculatingTime()) && isNull(timeCalculationActivityDTO.getFullWeekStart())) {
            exceptionService.dataNotFoundException(MESSAGE_FULLWEEK_STARTDAY_NOTFOUND);
        }
        ActivityTimeCalculationSettings activityTimeCalculationSettings = ObjectMapperUtils.copyPropertiesByMapper(timeCalculationActivityDTO, ActivityTimeCalculationSettings.class);
        Activity activity = findActivityById(new BigInteger(String.valueOf(timeCalculationActivityDTO.getActivityId())));
        verifyAndDeleteCompositeActivity(timeCalculationActivityDTO, availableAllowActivity);
        if (!timeCalculationActivityDTO.isAvailableAllowActivity()) {
            activity.setActivityTimeCalculationSettings(activityTimeCalculationSettings);
            if (!activityTimeCalculationSettings.getMethodForCalculatingTime().equals(CommonConstants.FULL_WEEK)) {
                activityTimeCalculationSettings.setDayTypes(activity.getActivityRulesSettings().getDayTypes());
            }
            activityMongoRepository.save(activity);
        }
        return timeCalculationActivityDTO;
    }

    @CacheEvict(value={"getActivityMappingDetails","findAllActivityByUnitIdWithCompositeActivities"}, key="#unitId")
    public TimeCalculationActivityDTO updateTimeCalculationTabOfActivity(Long unitId, TimeCalculationActivityDTO timeCalculationActivityDTO, boolean availableAllowActivity){
        return this.updateTimeCalculationTabOfActivity(timeCalculationActivityDTO,availableAllowActivity);
    }
    @CacheEvict(value = "findAllActivityByCountry",key = "#countryId")
    public TimeCalculationActivityDTO updateTimeCalculationTabOfActivity(TimeCalculationActivityDTO timeCalculationActivityDTO, boolean availableAllowActivity,Long countryId){
        return this.updateTimeCalculationTabOfActivity(timeCalculationActivityDTO,availableAllowActivity);
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

    @CacheEvict(value="getStaffSpecificActivitySettings", allEntries = true)
    public Set<BigInteger> assignChildActivitiesInActivity(BigInteger activityId, Set<BigInteger> childActivitiesIds) {
        Activity activity = findActivityById(activityId);
        List<ActivityDTO> activityMatched = activityMongoRepository.findChildActivityActivityIds(childActivitiesIds);
        if (activityMatched.size() != childActivitiesIds.size()) {
            exceptionService.illegalArgumentException(MESSAGE_MISMATCHED_IDS);
        }
        Set<BigInteger> oldChildActivityIds = activity.getChildActivityIds();
        organizationActivityService.verifyChildActivity(activityMatched, activity);
        activity.setChildActivityIds(childActivitiesIds);
        activityMongoRepository.save(activity);
        Set<BigInteger> newChildActivityIds = activity.getChildActivityIds().stream().collect(Collectors.toSet());
        newChildActivityIds.removeAll(oldChildActivityIds);
        Set<BigInteger> removeChildActivityIds = oldChildActivityIds.stream().collect(Collectors.toSet());
        removeChildActivityIds.removeAll(activity.getChildActivityIds());
        List<Activity> activities = activityMongoRepository.findAllActivitiesByIds(Stream.concat(newChildActivityIds.stream(), removeChildActivityIds.stream()).collect(Collectors.toList()));
        if(isCollectionNotEmpty(activities)) {
            activities.forEach(act -> act.setChildActivity(newChildActivityIds.contains(act.getId())));
            activityMongoRepository.saveAll(activities);
            if (isNotNull(activity.getUnitId())) {
                activityRankingService.updateRankListOnChangeActivityChildList(activity.getUnitId(), newChildActivityIds, removeChildActivityIds, activities);
            }
        }
        return childActivitiesIds;
    }

    @CacheEvict(value={"getActivityMappingDetails","findAllActivityByUnitIdWithCompositeActivities"}, key="#unitId")
    public Set<BigInteger> assignChildActivitiesInActivity(Long unitId, BigInteger activityId, Set<BigInteger> childActivitiesIds){
        return this.assignChildActivitiesInActivity(activityId,childActivitiesIds, unitId);
    }

    @CacheEvict(value = "findAllActivityByCountry",key = "#countryId")
    public Set<BigInteger> assignChildActivitiesInActivity(BigInteger activityId, Set<BigInteger> childActivitiesIds,Long countryId){
        return this.assignChildActivitiesInActivity(activityId,childActivitiesIds);
    }

    public ActivitySettingsWrapper getTimeCalculationTabOfActivity(BigInteger activityId, Long countryId) {
        List<DayTypeDTO> dayTypes = dayTypeService.getDayTypeWithCountryHolidayCalender(countryId);
        Activity activity = activityMongoRepository.findOne(activityId);
        ActivityTimeCalculationSettings activityTimeCalculationSettings = activity.getActivityTimeCalculationSettings();
        List<BigInteger> rulesTabDayTypes = activity.getActivityRulesSettings().getDayTypes();
        return new ActivitySettingsWrapper(activityTimeCalculationSettings, dayTypes, rulesTabDayTypes);
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
        activity.getAvailableChildActivityIds().removeAll(activity.getChildActivityIds());
        return activity;
    }

    public ActivityWithCompositeDTO getCompositeShiftTabOfActivity(BigInteger activityId) {
        ActivityWithCompositeDTO activity = activityMongoRepository.findActivityByActivityId(activityId);
        if (isNull(activity)) {
            exceptionService.dataNotFoundByIdException(MESSAGE_ACTIVITY_ID, activityId);
        }
        return activity;
    }

    public ActivitySettingsWrapper updateIndividualPointsTab(ActivityIndividualPointsSettingsDTO individualPointsDTO) {
        ActivityIndividualPointsSettings activityIndividualPointsSettings = ObjectMapperUtils.copyPropertiesByMapper(individualPointsDTO, ActivityIndividualPointsSettings.class);
        Activity activity = findActivityById(new BigInteger(String.valueOf(individualPointsDTO.getActivityId())));
        activity.setActivityIndividualPointsSettings(activityIndividualPointsSettings);
        activityMongoRepository.save(activity);
        return new ActivitySettingsWrapper(activityIndividualPointsSettings);
    }

    @CacheEvict(value={"getActivityMappingDetails","findAllActivityByUnitIdWithCompositeActivities"}, key="#unitId")
    public ActivitySettingsWrapper updateIndividualPointsTab(Long unitId, ActivityIndividualPointsSettingsDTO individualPointsDTO){
        return this.updateIndividualPointsTab(individualPointsDTO);
    }

    @CacheEvict(value = "findAllActivityByCountry",key = "#countryId")
    public ActivitySettingsWrapper updateIndividualPointsTab(ActivityIndividualPointsSettingsDTO individualPointsDTO,Long countryId){
        return this.updateIndividualPointsTab(individualPointsDTO);
    }

    public ActivityIndividualPointsSettings getIndividualPointsTabOfActivity(BigInteger activityId) {
        Activity activity = findActivityById(activityId);
        return activity.getActivityIndividualPointsSettings();
    }

    public ActivitySettingsWrapper updateRulesTab(ActivityRulesSettingsDTO rulesActivityDTO, boolean updateFromOrg) {
        organizationActivityService.validateActivityTimeRules( rulesActivityDTO.getShortestTime(), rulesActivityDTO.getLongestTime());
        ActivityRulesSettings activityRulesSettings = ObjectMapperUtils.copyPropertiesByMapper(rulesActivityDTO, ActivityRulesSettings.class);
        Activity activity = findActivityById(rulesActivityDTO.getActivityId());
        checkEligibleStaffLevelDetails(rulesActivityDTO, activity);
        updateCutoffDetails(rulesActivityDTO, activityRulesSettings);
        activityRulesSettings.setSicknessSetting(ObjectMapperUtils.copyPropertiesByMapper(rulesActivityDTO.getSicknessSetting(), SicknessSetting.class));
        if (activity.getActivityRulesSettings().isEligibleForStaffingLevel() != activityRulesSettings.isEligibleForStaffingLevel() && !activityRulesSettings.isEligibleForStaffingLevel()) {
            removedActivityFromStaffingLevelOfChildActivity(activity.getChildActivityIds());
            staffingLevelService.removedActivityFromStaffingLevel(activity.getId(), TimeTypeEnum.PRESENCE.equals(activity.getActivityBalanceSettings().getTimeType()));
        }
        activityRulesSettings.setSicknessSetting(ObjectMapperUtils.copyPropertiesByMapper(rulesActivityDTO.getSicknessSetting(), SicknessSetting.class));
        activity.setActivityRulesSettings(activityRulesSettings);
        if (!activity.getActivityTimeCalculationSettings().getMethodForCalculatingTime().equals(CommonConstants.FULL_WEEK)) {
            activity.getActivityTimeCalculationSettings().setDayTypes(activity.getActivityRulesSettings().getDayTypes());
        }
        activityMongoRepository.save(activity);
        if (updateFromOrg) {
            activitySchedulerJobService.registerJobForActivityCutoff(newArrayList(activity));
        }
        return new ActivitySettingsWrapper(activityRulesSettings);
    }

    @CacheEvict(value={"getActivityMappingDetails","findAllActivityByUnitIdWithCompositeActivities"}, key="#unitId")
    public ActivitySettingsWrapper updateRulesTab(Long unitId, ActivityRulesSettingsDTO rulesActivityDTO, boolean updateFromOrg){
        return this.updateRulesTab(rulesActivityDTO,updateFromOrg);
    }

    @CacheEvict(value = "findAllActivityByCountry",key = "#countryId")
    public ActivitySettingsWrapper updateRulesTab(ActivityRulesSettingsDTO rulesActivityDTO, boolean updateFromOrg,Long countryId){
        return this.updateRulesTab(rulesActivityDTO,updateFromOrg);
    }

    private void updateCutoffDetails(ActivityRulesSettingsDTO rulesActivityDTO, ActivityRulesSettings activityRulesSettings) {
        if (rulesActivityDTO.getCutOffIntervalUnit() != null && rulesActivityDTO.getCutOffStartFrom() != null) {
            if (CutOffIntervalUnit.DAYS.equals(rulesActivityDTO.getCutOffIntervalUnit()) && rulesActivityDTO.getCutOffdayValue() == 0) {
                exceptionService.invalidRequestException(ERROR_DAYVALUE_ZERO);
            }
            PlanningPeriod planningPeriod = planningPeriodService.getLastPlanningPeriod(UserContext.getUserDetails().getLastSelectedOrganizationId());
            List<CutOffInterval> cutOffIntervals = getCutoffInterval(rulesActivityDTO.getCutOffStartFrom(), rulesActivityDTO.getCutOffIntervalUnit(), rulesActivityDTO.getCutOffdayValue(), planningPeriod.getEndDate());
            activityRulesSettings.setCutOffIntervals(cutOffIntervals);
            rulesActivityDTO.setCutOffIntervals(cutOffIntervals);
        }
    }

    private void checkEligibleStaffLevelDetails(ActivityRulesSettingsDTO rulesActivityDTO, Activity activity) {
        if (rulesActivityDTO.isEligibleForStaffingLevel() && !activity.getActivityRulesSettings().isEligibleForStaffingLevel()) {
            Activity parentActivity = activityMongoRepository.findByChildActivityId(rulesActivityDTO.getActivityId());
            if (isNotNull(parentActivity) && !parentActivity.getActivityRulesSettings().isEligibleForStaffingLevel()) {
                exceptionService.actionNotPermittedException(MESSAGE_PARENT_SETTING_FALSE);
            }
        }
    }

    private void removedActivityFromStaffingLevelOfChildActivity(Set<BigInteger> childActivityIds) {
        if (isCollectionNotEmpty(childActivityIds)) {
            List<Activity> activities = activityMongoRepository.findAllActivitiesByIds(childActivityIds);
            for (Activity activity : activities) {
                if (activity.getActivityRulesSettings().isEligibleForStaffingLevel()) {
                    staffingLevelService.removedActivityFromStaffingLevel(activity.getId(), TimeTypeEnum.PRESENCE.equals(activity.getActivityBalanceSettings().getTimeType()));
                    activity.getActivityRulesSettings().setEligibleForStaffingLevel(false);
                }
            }
            activityMongoRepository.saveAll(activities);
        }
    }

    public ActivitySettingsWrapper getPhaseSettingTabOfActivity(BigInteger activityId, Long countryId) {
        Activity activity = findActivityById(activityId);
        DayTypeEmploymentTypeWrapper dayTypeEmploymentTypeWrapper = userIntegrationService.getDayTypesAndEmploymentTypes(countryId);
        List<DayTypeDTO> dayTypes = dayTypeEmploymentTypeWrapper.getDayTypes();
        List<EmploymentTypeDTO> employmentTypeDTOS = dayTypeEmploymentTypeWrapper.getEmploymentTypes();
        Set<AccessGroupRole> roles = AccessGroupRole.getAllRoles();
        ActivityPhaseSettings activityPhaseSettings = activity.getActivityPhaseSettings();
        return new ActivitySettingsWrapper(roles, activityPhaseSettings, dayTypes, employmentTypeDTOS);
    }

    public ActivityPhaseSettings updatePhaseSettingTab(ActivityPhaseSettings activityPhaseSettings) {
        Activity activity = findActivityById(activityPhaseSettings.getActivityId());
        activity.setActivityPhaseSettings(activityPhaseSettings);
        activityMongoRepository.save(activity);
        return activityPhaseSettings;
    }

    @CacheEvict(value={"getActivityMappingDetails","findAllActivityByUnitIdWithCompositeActivities"}, key="#unitId")
    public ActivityPhaseSettings updatePhaseSettingTab(Long unitId, ActivityPhaseSettings activityPhaseSettings){
        return this.updatePhaseSettingTab(activityPhaseSettings);
    }

    @CacheEvict(value = "findAllActivityByCountry",key = "#countryId")
    public ActivityPhaseSettings updatePhaseSettingTab(ActivityPhaseSettings activityPhaseSettings,Long countryId){
        return this.updatePhaseSettingTab(activityPhaseSettings);
    }

    public ActivitySettingsWrapper getRulesTabOfActivity(BigInteger activityId, Long countryId) {
        DayTypeEmploymentTypeWrapper dayTypeEmploymentTypeWrapper = userIntegrationService.getDayTypesAndEmploymentTypes(countryId);
        List<DayTypeDTO> dayTypes = dayTypeEmploymentTypeWrapper.getDayTypes();
        List<EmploymentTypeDTO> employmentTypeDTOS = dayTypeEmploymentTypeWrapper.getEmploymentTypes();
        Activity activity = activityMongoRepository.findOne(activityId);
        ActivityRulesSettings activityRulesSettings = activity.getActivityRulesSettings();
        return new ActivitySettingsWrapper(activityRulesSettings, dayTypes, employmentTypeDTOS);
    }

    public ActivityNotesSettings updateNotesTabOfActivity(ActivityGeneralSettingsDTO activityGeneralSettingsDTO, Activity activity) {
        if (Optional.ofNullable(activity.getActivityNotesSettings().getModifiedDocumentName()).isPresent()) {
            activity.getActivityNotesSettings().setModifiedDocumentName(activityGeneralSettingsDTO.getModifiedDocumentName());
        }
        if (Optional.ofNullable(activity.getActivityNotesSettings().getOriginalDocumentName()).isPresent()) {
            activity.getActivityNotesSettings().setOriginalDocumentName(activityGeneralSettingsDTO.getOriginalDocumentName());
        }
        activity.getActivityNotesSettings().setContent(activityGeneralSettingsDTO.getContent());
        return activity.getActivityNotesSettings();
    }

    public ActivitySettingsWrapper getNotesTabOfActivity(BigInteger activityId) {
        Activity activity = findActivityById(activityId);
        return new ActivitySettingsWrapper(activity.getActivityNotesSettings());
    }

    public List<CutOffInterval> getCutOffInterValOfActivity(BigInteger activityId) {
        Activity activity = findActivityById(activityId);
        PlanningPeriod planningPeriod = planningPeriodService.getLastPlanningPeriod(UserContext.getUserDetails().getLastSelectedOrganizationId());
        return ActivityUtil.getCutoffInterval(activity.getActivityRulesSettings().getCutOffStartFrom(), activity.getActivityRulesSettings().getCutOffIntervalUnit(), activity.getActivityRulesSettings().getCutOffdayValue(), planningPeriod.getEndDate());
    }

    public ActivitySettingsWrapper getCommunicationTabOfActivity(BigInteger activityId) {
        Activity activity = findActivityById(activityId);
        return new ActivitySettingsWrapper(activity.getActivityCommunicationSettings());
    }

    public ActivitySettingsWrapper updateBonusTabOfActivity(BonusActivityDTO bonusActivityDTO) {
        Activity activity = findActivityById(bonusActivityDTO.getActivityId());
        ActivityBonusSettings activityBonusSettings = new ActivityBonusSettings(bonusActivityDTO.getBonusHoursType(), bonusActivityDTO.isOverRuleCtaWta());
        activity.setActivityBonusSettings(activityBonusSettings);
        activityMongoRepository.save(activity);
        return new ActivitySettingsWrapper(activityBonusSettings);
    }

    @CacheEvict(value={"getActivityMappingDetails","findAllActivityByUnitIdWithCompositeActivities"}, key="#unitId")
    public ActivitySettingsWrapper updateBonusTabOfActivity(Long unitId, BonusActivityDTO bonusActivityDTO){
        return this.updateBonusTabOfActivity(bonusActivityDTO);
    }
    @CacheEvict(value = "findAllActivityByCountry",key = "#countryId")
    public ActivitySettingsWrapper updateBonusTabOfActivity(BonusActivityDTO bonusActivityDTO,Long countryId){
        return this.updateBonusTabOfActivity(bonusActivityDTO);
    }

    public ActivitySettingsWrapper getBonusTabOfActivity(BigInteger activityId) {
        Activity activity = findActivityById(activityId);
        return new ActivitySettingsWrapper(activity.getActivityBonusSettings());
    }


    @Caching(evict = {
            @CacheEvict(value = "findAllActivityByCountry",allEntries = true),
            @CacheEvict(value={"getActivityMappingDetails","findAllActivityByUnitIdWithCompositeActivities"}, key="#unitId") })
    public ActivitySettingsWrapper updateSkillTabOfActivity( SkillActivityDTO skillActivityDTO) {
        Activity activity = findActivityById(skillActivityDTO.getActivityId());
        ActivitySkillSettings activitySkillSettings = new ActivitySkillSettings(skillActivityDTO.getActivitySkills());
        activity.setActivitySkillSettings(activitySkillSettings);
        activityMongoRepository.save(activity);
        return new ActivitySettingsWrapper(activitySkillSettings);
    }

    @CacheEvict(value={"getActivityMappingDetails","findAllActivityByUnitIdWithCompositeActivities"}, key="#unitId")
    public ActivitySettingsWrapper updateSkillTabOfActivity(Long unitId, SkillActivityDTO skillActivityDTO){
        return this.updateSkillTabOfActivity(skillActivityDTO);
    }

    @CacheEvict(value = "findAllActivityByCountry",key = "#countryId")
    public ActivitySettingsWrapper updateSkillTabOfActivity(SkillActivityDTO skillActivityDTO,Long countryId){
        return this.updateSkillTabOfActivity(skillActivityDTO);
    }

    public ActivitySettingsWrapper getSkillTabOfActivity(BigInteger activityId) {
        Activity activity = findActivityById(activityId);
        ActivitySkillSettings activitySkillSettings = activity.getActivitySkillSettings();
        List<Long> skillIds = activitySkillSettings.getActivitySkillIds();
        if (isCollectionNotEmpty(skillIds)) {
            List<Skill> skills = userIntegrationService.getSkillsByIds(skillIds, activity.getCountryId());
            Map<Long, Skill> skillTranslationMap = skills.stream().collect(Collectors.toMap(Skill::getId, v -> v));
            activitySkillSettings.getActivitySkills().forEach(activitySkill -> {
                Skill skill = skillTranslationMap.get(activitySkill.getSkillId());
                activitySkill.setTranslations(skill.getTranslations());
            });
        }
        return new ActivitySettingsWrapper(activitySkillSettings);
    }

    public void updateOrgMappingDetailOfActivity(OrganizationMappingDTO organizationMappingDTO, BigInteger activityId) {
        Activity activity = findActivityById(activityId);
        boolean isSuccess = userIntegrationService.verifyOrganizationExpertizeAndRegions(organizationMappingDTO);
        if (!isSuccess) {
            exceptionService.dataNotFoundException(MESSAGE_PARAMETERS_INCORRECT);
        }
        List<Long> expertiseIds = activity.getExpertises();
        activity.setRegions(organizationMappingDTO.getRegions());
        activity.setExpertises(organizationMappingDTO.getExpertises());
        activity.setOrganizationSubTypes(organizationMappingDTO.getOrganizationSubTypes());
        activity.setOrganizationTypes(organizationMappingDTO.getOrganizationTypes());
        activity.setLevels(organizationMappingDTO.getLevel());
        activity.setEmploymentTypes(organizationMappingDTO.getEmploymentTypes());
        activityMongoRepository.save(activity);
        TimeType timeType = timeTypeService.getTimeTypeById(activity.getActivityBalanceSettings().getTimeTypeId());
        if(PriorityFor.ABSENCE.equals(timeType.getPriorityFor()) && PUBLISHED.equals(activity.getState()) && expertiseIds.size() != activity.getExpertises().size() && !activity.isChildActivity()){
            if(activity.getExpertises().size() > expertiseIds.size()){
                List<Long> updateExpertiseIds = activity.getExpertises();
                updateExpertiseIds.removeAll(expertiseIds);
                activityRankingService.createOrUpdateAbsenceActivityRanking(activity,updateExpertiseIds);
            } else {
                expertiseIds.removeAll(activity.getExpertises());
                activityRankingService.removeAbsenceActivityId(activity,expertiseIds);
            }
        }
        if (activity.getUnitId() != null) {
            plannerSyncService.publishActivity(activity.getUnitId(), activity, IntegrationOperation.UPDATE);
        }
    }

    @CacheEvict(value={"getActivityMappingDetails","findAllActivityByUnitIdWithCompositeActivities"}, key="#unitId")
    public void updateOrgMappingDetailOfActivity(Long unitId, OrganizationMappingDTO organizationMappingDTO, BigInteger activityId){
        this.updateOrgMappingDetailOfActivity(organizationMappingDTO,activityId);
    }
    @Caching(evict = {
            @CacheEvict(value = "findAllActivityByCountry",key = "#countryId"),
            @CacheEvict(value={"getActivityMappingDetails","findAllActivityByUnitIdWithCompositeActivities"},allEntries = true)
    })
    public void updateOrgMappingDetailOfActivity(OrganizationMappingDTO organizationMappingDTO, BigInteger activityId,Long countryId){
        this.updateOrgMappingDetailOfActivity(organizationMappingDTO,activityId);
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

    public ActivitySettingsWrapper updateOptaPlannerSettingsTabOfActivity(BigInteger activityId, ActivityOptaPlannerSetting activityOptaPlannerSetting) {
        Activity activity = findActivityById(activityId);
        activity.setActivityOptaPlannerSetting(activityOptaPlannerSetting);
        activityMongoRepository.save(activity);
        return new ActivitySettingsWrapper(activityOptaPlannerSetting);
    }

    @CacheEvict(value={"getActivityMappingDetails","findAllActivityByUnitIdWithCompositeActivities"}, key="#unitId")
    public ActivitySettingsWrapper updateOptaPlannerSettingsTabOfActivity(Long unitId, BigInteger activityId, ActivityOptaPlannerSetting activityOptaPlannerSetting){
        return updateOptaPlannerSettingsTabOfActivity(activityId,activityOptaPlannerSetting);
    }

    @CacheEvict(value = "findAllActivityByCountry",key = "#countryId")
    public ActivitySettingsWrapper updateOptaPlannerSettingsTabOfActivity(BigInteger activityId, ActivityOptaPlannerSetting activityOptaPlannerSetting,Long countryId){
        return updateOptaPlannerSettingsTabOfActivity(activityId,activityOptaPlannerSetting);
    }

    public ActivitySettingsWrapper getOptaPlannerSettingsTabOfActivity(BigInteger activityId) {
        Activity activity = findActivityById(activityId);
        return new ActivitySettingsWrapper(activity.getActivityOptaPlannerSetting());
    }

    public ActivitySettingsWrapper getCtaAndWtaSettingsTabOfActivity(BigInteger activityId) {
        Activity activity = findActivityById(activityId);
        return new ActivitySettingsWrapper(activity.getActivityCTAAndWTASettings());
    }

    public ActivitySettingsWrapper updateCtaAndWtaSettingsTabOfActivity(ActivityCTAAndWTASettingsDTO activityCTAAndWTASettingsDTO) {
        Activity activity = findActivityById(new BigInteger(String.valueOf(activityCTAAndWTASettingsDTO.getActivityId())));
        ActivityCTAAndWTASettings activityCTAAndWTASettings = new ActivityCTAAndWTASettings(activityCTAAndWTASettingsDTO.isEligibleForCostCalculation());
        activity.setActivityCTAAndWTASettings(activityCTAAndWTASettings);
        activityMongoRepository.save(activity);
        return new ActivitySettingsWrapper(activityCTAAndWTASettings);
    }

    @CacheEvict(value={"getActivityMappingDetails","findAllActivityByUnitIdWithCompositeActivities"}, key="#unitId")
    public ActivitySettingsWrapper updateCtaAndWtaSettingsTabOfActivity(Long unitId, ActivityCTAAndWTASettingsDTO activityCTAAndWTASettingsDTO){
        return this.updateCtaAndWtaSettingsTabOfActivity(activityCTAAndWTASettingsDTO);
    }

    @CacheEvict(value = "findAllActivityByCountry",key = "#countryId")
    public ActivitySettingsWrapper updateCtaAndWtaSettingsTabOfActivity(ActivityCTAAndWTASettingsDTO activityCTAAndWTASettingsDTO, Long countryId){
        return this.updateCtaAndWtaSettingsTabOfActivity(activityCTAAndWTASettingsDTO);
    }

    @CacheEvict(value = "findAllActivityByCountry",allEntries = true)
    public boolean deleteCountryActivity(BigInteger activityId) {
        Activity activity =findActivityById(activityId);
        if (activity.getState().equals(PUBLISHED) || activity.getState().equals(ActivityStateEnum.LIVE)) {
            exceptionService.actionNotPermittedException(EXCEPTION_ALREADYINUSE, ACTIVITY);
        }
        activity.setDeleted(true);
        activityMongoRepository.save(activity);
        TimeType timeType = timeTypeService.getTimeTypeById(activity.getActivityBalanceSettings().getTimeTypeId());
        if(PriorityFor.ABSENCE.equals(timeType.getPriorityFor()) && PUBLISHED.equals(activity.getState()) && activity.getExpertises().size() > 0 && !activity.isChildActivity()) {
            activityRankingService.removeAbsenceActivityId(activity, activity.getExpertises());
        }
        return true;
    }

    @CacheEvict(value = "findAllActivityByCountry",allEntries = true)
    public Boolean publishActivity(BigInteger activityId) {
        Activity activity = findActivityById(activityId);
        if (activity.getState().equals(PUBLISHED) || activity.getState().equals(ActivityStateEnum.LIVE)) {
            exceptionService.actionNotPermittedException(MESSAGE_ACTIVITY_PUBLISHED, activityId);
        }
        if (activity.getActivityBalanceSettings().getTimeTypeId() == null) {
            exceptionService.actionNotPermittedException(MESSAGE_ACTIVITY_TIMETYPE_ABSENT, activity.getName());
        }
        activity.setState(PUBLISHED);
        activityMongoRepository.save(activity);
        TimeType timeType = timeTypeService.getTimeTypeById(activity.getActivityBalanceSettings().getTimeTypeId());
        if(PriorityFor.ABSENCE.equals(timeType.getPriorityFor()) && isCollectionNotEmpty(activity.getExpertises()) && activity.getExpertises().size() > 0 && !activity.isChildActivity()) {
            activityRankingService.createOrUpdateAbsenceActivityRanking(activity, activity.getExpertises());
        }
        return true;
    }


    @CacheEvict(value = "findAllActivityByCountry",key = "#countryId")
    public ActivityDTO copyActivityDetails(Long countryId, BigInteger activityId, ActivityDTO activityDTO) {
        Activity activity = activityMongoRepository.findByNameIgnoreCaseAndCountryIdAndByDate(activityDTO.getName().trim(), countryId, activityDTO.getStartDate(), activityDTO.getEndDate());
        if (Optional.ofNullable(activity).isPresent() && activityDTO.getStartDate().isBefore(activity.getActivityGeneralSettings().getStartDate())) {
            exceptionService.actionNotPermittedException(MESSAGE_ACTIVITY_OVERLAPING);
        }
        if (Optional.ofNullable(activity).isPresent()) {
            exceptionService.dataNotFoundException(activity.getActivityGeneralSettings().getEndDate() == null ? MESSAGE_ACTIVITY_ENDDATE_REQUIRED : MESSAGE_ACTIVITY_ACTIVE_ALREADYEXISTS);
        }
        Activity activityFromDatabase = activityMongoRepository.findOne(activityId);
        if (isNull(activityFromDatabase) || activityFromDatabase.isDeleted() || !countryId.equals(activityFromDatabase.getCountryId())) {
            exceptionService.dataNotFoundByIdException(MESSAGE_ACTIVITY_ID, activityId);
        }
        Activity activityCopied = ObjectMapperUtils.copyPropertiesByMapper(activityFromDatabase, Activity.class);
        activityCopied.setId(null);
        activityCopied.setName(activityDTO.getName().trim());
        activityCopied.setCountryParentId(activityFromDatabase.getCountryParentId() == null ? activityFromDatabase.getId() : activityFromDatabase.getCountryParentId());
        activityCopied.getActivityGeneralSettings().setName(activityDTO.getName().trim());
        activityCopied.getActivityGeneralSettings().setStartDate(activityDTO.getStartDate());
        activityCopied.setState(ActivityStateEnum.DRAFT);
        activityCopied.getActivityGeneralSettings().setEndDate(activityDTO.getEndDate());
        activityMongoRepository.save(activityCopied);
        activityDTO.setId(activityCopied.getId());
        activityDTO.setActivityCanBeCopied(true);
        return activityDTO;
    }

    public ActivitySettingsWrapper getLocationsTabOfActivity(BigInteger activityId) {
        Activity activity = findActivityById(activityId);
        return new ActivitySettingsWrapper(activity.getActivityLocationSettings());
    }

    public ActivitySettingsWrapper updateLocationsTabOfActivity(ActivityLocationSettingsDTO activityLocationSettingsDTO) {
        Activity activity = findActivityById(activityLocationSettingsDTO.getActivityId());
        ActivityLocationSettings activityLocationSettings = new ActivityLocationSettings(activityLocationSettingsDTO.getGlideTimeForCheckIn(), activityLocationSettingsDTO.getGlideTimeForCheckOut());
        activity.setActivityLocationSettings(activityLocationSettings);
        activityMongoRepository.save(activity);
        return new ActivitySettingsWrapper(activityLocationSettings);
    }

    @CacheEvict(value={"getActivityMappingDetails","findAllActivityByUnitIdWithCompositeActivities"}, key="#unitId")
    public ActivitySettingsWrapper updateLocationsTabOfActivity(Long unitId, ActivityLocationSettingsDTO activityLocationSettingsDTO){
        return this.updateLocationsTabOfActivity(activityLocationSettingsDTO);
    }

    @CacheEvict(value = "findAllActivityByCountry",key = "#countryId")
    public ActivitySettingsWrapper updateLocationsTabOfActivity(ActivityLocationSettingsDTO activityLocationSettingsDTO,Long countryId){
        return this.updateLocationsTabOfActivity(activityLocationSettingsDTO);
    }


    @Caching(evict = {
            @CacheEvict(value = "findAllActivityByCountry",allEntries = true),
            @CacheEvict(value={"getActivityMappingDetails","findAllActivityByUnitIdWithCompositeActivities"}, allEntries = true)})
    public Map<String, TranslationInfo> updateTranslationData(BigInteger activityId, Map<String, TranslationInfo> activityTranslationDTO){
        Activity activity = activityMongoRepository.findActivityByIdAndEnabled(activityId);
        if (isNull(activity)) {
            exceptionService.dataNotFoundException(MESSAGE_DATA_NOTFOUND);
        }
        return updateActivityTranslations(activity, activityTranslationDTO);
    }


    @Caching(evict = {
            @CacheEvict(value = "findAllActivityByCountry",allEntries = true),
            @CacheEvict(value={"getActivityMappingDetails","findAllActivityByUnitIdWithCompositeActivities"}, allEntries = true)})
    public Map<String, TranslationInfo> updateActivityTranslations(@NotNull Activity activity, Map<String, TranslationInfo> activityTranslationDTO){
        final Map<String, TranslationInfo> activityLanguageDetailsMap = activity.getTranslations();
        activityTranslationDTO.forEach((s, translation) -> {
            LOGGER.debug("saving language details {} ", translation);
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

    public List<ActivityDTO> getActivitiesWithCategories(Long unitId) {
        return activityMongoRepository.findAllActivityByUnitId(unitId);
    }

    public boolean removeAttachementsFromActivity(BigInteger activityId, boolean removeNotes) {
        Activity activity = findActivityById(activityId);
        if (removeNotes) {
            activity.getActivityNotesSettings().setOriginalDocumentName(null);
            activity.getActivityNotesSettings().setModifiedDocumentName(null);
        } else {
            activity.getActivityGeneralSettings().setOriginalIconName(null);
            activity.getActivityGeneralSettings().setModifiedIconName(null);
        }
        activityMongoRepository.save(activity);
        return true;
    }


    @CacheEvict(value={"getActivityMappingDetails","findAllActivityByUnitIdWithCompositeActivities"}, key="#unitId")
    public boolean removeAttachementsFromActivity(Long unitId, BigInteger activityId, boolean removeNotes) {
        return this.removeAttachementsFromActivity(activityId,removeNotes);
    }

    @CacheEvict(value = "findAllActivityByCountry",key = "#countryId")
    public boolean removeAttachementsFromActivity(BigInteger activityId, boolean removeNotes,Long countryId) {
        return this.removeAttachementsFromActivity(activityId,removeNotes);
    }

    public List<ActivityDTO> findAllActivityByDeletedFalseAndUnitId(List<Long> unitIds) {
        return activityMongoRepository.findAllActivityByDeletedFalseAndUnitId(unitIds);
    }
    @Caching(evict = {
            @CacheEvict(value = "findAllActivityByCountry",allEntries = true),
            @CacheEvict(value={"getActivityMappingDetails","findAllActivityByUnitIdWithCompositeActivities"}, allEntries = true) })
    public void unassighExpertiseFromActivities(BigInteger expertiseId) {
        activityMongoRepository.unassignExpertiseFromActivitiesByExpertiesId(expertiseId.longValue());
        LOGGER.info("successfully remove expertise from activities by job");
    }

    public Set<BigInteger> getAbsenceActivityIds(Long unitId, Date date) {
        Phase phase = phaseService.getCurrentPhaseByUnitIdAndDate(unitId, date, null);
        List<Activity> activities = activityMongoRepository.findAllAbsenceActivities(unitId, ABSENCE, phase.getId());
        return activities.stream().map(MongoBaseEntity::getId).collect(Collectors.toSet());
    }

    public Activity findActivityById(BigInteger activityId) {
        return activityMongoRepository.findById(activityId).orElseThrow(() -> new DataNotFoundByIdException(convertMessage(MESSAGE_ACTIVITY_ID, activityId)));
    }

    public List<Activity> findAllByUnitIdAndTimeTypeIds(Long unitId, Collection<BigInteger> timeTypeIds) {
        return activityMongoRepository.findAllByUnitIdAndTimeTypeIds(unitId, timeTypeIds);
    }

    public Map<BigInteger, Integer> getActivityRankWithRankByUnitId(Long unitId) {
        return activityMongoRepository.getActivityRankWithRankByUnitId(unitId).stream().collect(Collectors.toMap(k -> k.getId(), v -> v.getActivitySequence()));
    }

    public Map<BigInteger,ActivityDTO> getActivityDetailsWithRankByUnitId(Long unitId) {
        Map<BigInteger,ActivityDTO> activityDTOMap= activityMongoRepository.getActivityDetailsWithRankByUnitId(unitId).stream().collect(Collectors.toMap(k->k.getId(),v->v));
        activityPermissionService.updateActivityMapByPermission(unitId,activityDTOMap);
        return activityDTOMap;
    }


    public List<Activity> findAllBySecondLevelTimeTypeAndUnitIds(TimeTypeEnum timeTypeEnum, Set<Long> unitIds){
        return activityMongoRepository.findAllBySecondLevelTimeTypeAndUnitIds(timeTypeEnum, unitIds);
    }

    public List[] findAllNonProductiveTypeActivityIdsAndAssignedStaffIds(Collection<BigInteger> activityIds) {
        return activityMongoRepository.findAllNonProductiveTypeActivityIdsAndAssignedStaffIds(activityIds);
    }

    public List<ActivityDTO> findAllActivitiesByTimeType(Long refId, TimeTypeEnum timeType){
        return activityMongoRepository.findAllActivitiesByTimeType(refId,timeType);
    }

    public List<Activity> findAllActivitiesByCountryAndTimeTypePriority(long countryOrUnitId, boolean countryId, PriorityFor priorityFor){
        return activityMongoRepository.findAllActivityByCountryAndPriorityFor(countryOrUnitId, countryId, priorityFor);
    }

    public List<Activity> findAllByTimeTypeId(BigInteger timeTypeId) {
        return activityMongoRepository.findAllByTimeTypeId(timeTypeId);
    }

    public List<Activity> findAllByUnitIdAndDeletedFalse(Long unitId) {
        return activityMongoRepository.findAllByUnitIdAndDeletedFalse(unitId);
    }
}
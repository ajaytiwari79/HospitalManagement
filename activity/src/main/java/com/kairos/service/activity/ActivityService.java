package com.kairos.service.activity;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.activity.ActivityDTO;
import com.kairos.dto.activity.activity.ActivityWithTimeTypeDTO;
import com.kairos.dto.activity.activity.CompositeActivityDTO;
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
import com.kairos.dto.activity.shift.ShiftTemplateDTO;
import com.kairos.dto.activity.time_type.TimeTypeDTO;
import com.kairos.dto.user.access_permission.AccessGroupRole;
import com.kairos.dto.user.country.agreement.cta.cta_response.EmploymentTypeDTO;
import com.kairos.dto.user.country.day_type.DayType;
import com.kairos.dto.user.country.day_type.DayTypeEmploymentTypeWrapper;
import com.kairos.dto.user.country.tag.TagDTO;
import com.kairos.dto.user.organization.OrganizationDTO;
import com.kairos.dto.user.organization.OrganizationTypeAndSubTypeDTO;
import com.kairos.dto.user.organization.SelfRosteringMetaData;
import com.kairos.dto.user.organization.skill.Skill;
import com.kairos.dto.user.reason_code.ReasonCodeWrapper;
import com.kairos.enums.ActivityStateEnum;
import com.kairos.enums.DurationType;
import com.kairos.enums.IntegrationOperation;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.activity.ActivityWrapper;
import com.kairos.persistence.model.activity.TimeType;
import com.kairos.persistence.model.activity.tabs.*;
import com.kairos.persistence.model.activity.tabs.rules_activity_tab.RulesActivityTab;
import com.kairos.persistence.repository.activity.ActivityCategoryRepository;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.counter.CounterRepository;
import com.kairos.persistence.repository.open_shift.OpenShiftIntervalRepository;
import com.kairos.persistence.repository.staffing_level.StaffingLevelMongoRepository;
import com.kairos.persistence.repository.tag.TagMongoRepository;
import com.kairos.persistence.repository.time_type.TimeTypeMongoRepository;
import com.kairos.rest_client.SkillRestClient;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.glide_time.GlideTimeSettingsService;
import com.kairos.service.integration.PlannerSyncService;
import com.kairos.service.organization.OrganizationActivityService;
import com.kairos.service.period.PlanningPeriodService;
import com.kairos.service.phase.PhaseService;
import com.kairos.service.shift.ShiftService;
import com.kairos.service.shift.ShiftTemplateService;
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
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;
import static com.kairos.commons.utils.ObjectUtils.isNull;
import static com.kairos.constants.AppConstants.ACTIVITY_TYPE_IMAGE_PATH;
import static com.kairos.constants.AppConstants.FULL_WEEK;
import static com.kairos.service.activity.ActivityUtil.*;

/**
 * Created by pawanmandhan on 17/8/17.
 */
@Service
public class ActivityService extends MongoBaseService {
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
    private SkillRestClient skillRestClient;
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

    private final static Logger LOGGER = LoggerFactory.getLogger(ActivityService.class);

    public ActivityTagDTO createActivity(Long countryId, ActivityDTO activityDTO) {
        if (activityDTO.getEndDate() != null && activityDTO.getEndDate().isBefore(activityDTO.getStartDate())) {
            exceptionService.actionNotPermittedException("message.activity.enddate.greaterthan.startdate");
        }
        Activity activity = activityMongoRepository.findByNameIgnoreCaseAndCountryIdAndByDate(activityDTO.getName().trim(), countryId, activityDTO.getStartDate(), activityDTO.getEndDate());
        if (Optional.ofNullable(activity).isPresent() && activityDTO.getStartDate().isBefore(activity.getGeneralActivityTab().getStartDate())) {
            exceptionService.actionNotPermittedException("message.activity.overlaping");
        }
        if (Optional.ofNullable(activity).isPresent()) {
            exceptionService.dataNotFoundException(activity.getGeneralActivityTab().getEndDate() == null ? "message.activity.enddate.required" : "message.activity.active.alreadyExists");
        }
        activity = buildActivity(activityDTO);
        initializeActivityTabs(activity, countryId, activityDTO);
        save(activity);
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
            save(category);
            generalActivityTab.setCategoryId(category.getId());
        }
        activity.setGeneralActivityTab(generalActivityTab);
        List<PhaseDTO> phases = phaseService.getPhasesByCountryId(countryId);
        if (CollectionUtils.isEmpty(phases)) {
            exceptionService.actionNotPermittedException("message.country.phase.notfound");
        }
        List<PhaseTemplateValue> phaseTemplateValues = getPhaseForRulesActivity(phases);
        GlideTimeSettingsDTO glideTimeSettingsDTO = glideTimeSettingsService.getGlideTimeSettings(countryId);
        if (!Optional.ofNullable(glideTimeSettingsDTO).isPresent()) {
            exceptionService.actionNotPermittedException("error.glidetime.notfound.country");
        }
        ActivityUtil.initializeActivityTabs(activity, phaseTemplateValues, glideTimeSettingsDTO);
    }

    public Map<String, Object> findAllActivityByCountry(long countryId) {
        Map<String, Object> response = new HashMap<>();
        List<ActivityTagDTO> activityTagDTOS = ObjectMapperUtils.copyPropertiesOfListByMapper(activityMongoRepository.findAllActivityByCountry(countryId), ActivityTagDTO.class);
        //In Country Module any Activity can be copied
        activityTagDTOS.forEach(activityTagDTO -> {
            activityTagDTO.setActivityCanBeCopied(true);
        });
        List<ActivityCategory> acivitityCategories = activityCategoryRepository.findByCountryId(countryId);
        response.put("activities", activityTagDTOS);
        response.put("activityCategories", acivitityCategories);
        return response;
    }

    public Set<BigInteger> checkActivityAllowForChildActivities(List<ActivityTagDTO> activities, ActivityWithCompositeDTO activityWithCompositeDTO){
        Set<BigInteger> allowChildActivityIds=new HashSet<>();
        Set<BigInteger> childActivitiesIds=activities.stream().flatMap(activityTagDTO -> activityTagDTO.getChildActivityIds().stream()).collect(Collectors.toSet());
        for (ActivityTagDTO activity : activities) {
            if(!childActivitiesIds.contains(activity.getId()) && !activity.getId().equals(activityWithCompositeDTO.getId())){
                allowChildActivityIds.add(activity.getId());
            }
        }
        if(childActivitiesIds.contains(activityWithCompositeDTO.getId())){
            activityWithCompositeDTO.setApplicableForChildActivities(false);
        }
        return allowChildActivityIds;
    }

    public List<ActivityWithCTAWTASettingsDTO> findAllActivityWithCtaWtaSettingByCountry(long countryId) {
        return activityMongoRepository.findAllActivityWithCtaWtaSettingByCountry(countryId);
    }

    public List<ActivityCategory> findAllActivityCategoriesByCountry(long countryId, List<BigInteger> activityCategoriesIds) {
        return activityCategoryRepository.findAllByIdsIn(activityCategoriesIds);
    }

    public List<ActivityWithCTAWTASettingsDTO> findAllActivityWithCtaWtaSettingByUnit(long unitId) {
        return activityMongoRepository.findAllActivityWithCtaWtaSettingByUnit(unitId);
    }

    public Map<Long, Map<Long, BigInteger>> getListOfActivityIdsOfUnitByParentIds(List<BigInteger> parentActivityIds, List<Long> unitIds) {
        List<OrganizationActivityDTO> unitActivities = activityMongoRepository.findAllActivityOfUnitsByParentActivity(parentActivityIds, unitIds);
        Map<Long, Map<Long, BigInteger>> mappedParentUnitActivities = new HashMap<>();
        unitActivities.forEach(activityDTO -> {
            Map<Long, BigInteger> unitParentActivities = mappedParentUnitActivities.get(activityDTO.getUnitId().longValue());
            if (!Optional.ofNullable(unitParentActivities).isPresent()) {
                mappedParentUnitActivities.put(activityDTO.getUnitId().longValue(), new HashMap<Long, BigInteger>());
                unitParentActivities = mappedParentUnitActivities.get(activityDTO.getUnitId().longValue());
            }
            unitParentActivities.put(activityDTO.getParentId().longValue(), activityDTO.getId());
        });
        return mappedParentUnitActivities;
    }

    public boolean deleteActivity(BigInteger activityId) {
        Activity activity = activityMongoRepository.findOne(activityId);
        if (!Optional.ofNullable(activity).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.activity.id", activityId);
        }
        long activityCount = shiftService.countByActivityId(activityId);
        if (activityCount > 0) {
            exceptionService.actionNotPermittedException("message.activity.timecareactivitytype");
        }
        activity.setDeleted(true);
        save(activity);
        return true;
    }

    public ActivityTabsWrapper updateGeneralTab(Long countryId, GeneralActivityTabDTO generalDTO) {
        //check category is available in country
        if (generalDTO.getEndDate() != null && generalDTO.getEndDate().isBefore(generalDTO.getStartDate())) {
            exceptionService.actionNotPermittedException("message.activity.enddate.greaterthan.startdate");
        }
        Activity isActivityAlreadyExists = activityMongoRepository.findByNameExcludingCurrentInCountryAndDate(generalDTO.getName().trim(), generalDTO.getActivityId(), countryId, generalDTO.getStartDate(), generalDTO.getEndDate());
        if (Optional.ofNullable(isActivityAlreadyExists).isPresent() && generalDTO.getStartDate().isBefore(isActivityAlreadyExists.getGeneralActivityTab().getStartDate())) {
            exceptionService.actionNotPermittedException("message.activity.overlaping");
        }
        if (Optional.ofNullable(isActivityAlreadyExists).isPresent()) {
            exceptionService.dataNotFoundException(isActivityAlreadyExists.getGeneralActivityTab().getEndDate() == null ? "message.activity.enddate.required" : "message.activity.active.alreadyExists");
        }
        ActivityCategory activityCategory = activityCategoryRepository.getByIdAndNonDeleted(generalDTO.getCategoryId());
        if (activityCategory == null) {
            exceptionService.dataNotFoundByIdException("message.category.notExist");
        }
        Activity activity = activityMongoRepository.findOne(generalDTO.getActivityId());
        generalDTO.setBackgroundColor(activity.getGeneralActivityTab().getBackgroundColor());
        GeneralActivityTab generalTab = new GeneralActivityTab();
        ObjectMapperUtils.copyProperties(generalDTO, generalTab);
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
        //   generalTab.setTags(tagMongoRepository.getTagsById(generalDTO.getTags()));
        GeneralActivityTabWithTagDTO generalActivityTabWithTagDTO = ObjectMapperUtils.copyPropertiesByMapper(generalTab, GeneralActivityTabWithTagDTO.class);
        generalActivityTabWithTagDTO.setTags(null);
        if (!generalDTO.getTags().isEmpty()) {
            generalActivityTabWithTagDTO.setTags(tagMongoRepository.getTagsById(generalDTO.getTags()));
        }
        updateBalanceSettingTab(generalDTO, activity);
        updateNotesTabOfActivity(generalDTO, activity);
        save(activity);
        generalActivityTabWithTagDTO.setAddTimeTo(activity.getBalanceSettingsActivityTab().getAddTimeTo());
        generalActivityTabWithTagDTO.setTimeTypeId(activity.getBalanceSettingsActivityTab().getTimeTypeId());
        generalActivityTabWithTagDTO.setOnCallTimePresent(activity.getBalanceSettingsActivityTab().getOnCallTimePresent());
        generalActivityTabWithTagDTO.setNegativeDayBalancePresent(activity.getBalanceSettingsActivityTab().getNegativeDayBalancePresent());
        generalActivityTabWithTagDTO.setTimeType(activity.getBalanceSettingsActivityTab().getTimeType());
        generalActivityTabWithTagDTO.setContent(activity.getNotesActivityTab().getContent());
        generalActivityTabWithTagDTO.setOriginalDocumentName(activity.getNotesActivityTab().getOriginalDocumentName());
        generalActivityTabWithTagDTO.setModifiedDocumentName(activity.getNotesActivityTab().getModifiedDocumentName());
        return new ActivityTabsWrapper(generalActivityTabWithTagDTO, activityCategories);
    }

    public ActivityTabsWrapper getGeneralTabOfActivity(Long countryId, BigInteger activityId) {
        List<ActivityCategory> activityCategories = checkCountryAndFindActivityCategory(countryId);
        Activity activity = activityMongoRepository.findOne(activityId);
        if (!Optional.ofNullable(activity).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.activity.timecare.id", activityId);
        }
        GeneralActivityTab generalTab = activity.getGeneralActivityTab();
        generalTab.setTags(null);
        GeneralActivityTabWithTagDTO generalActivityTabWithTagDTO = ObjectMapperUtils.copyPropertiesByMapper(generalTab, GeneralActivityTabWithTagDTO.class);
        generalActivityTabWithTagDTO.setTags(null);
        if (!activity.getTags().isEmpty()) {
            generalActivityTabWithTagDTO.setTags(tagMongoRepository.getTagsById(activity.getTags()));
        }
        generalActivityTabWithTagDTO.setAddTimeTo(activity.getBalanceSettingsActivityTab().getAddTimeTo());
        generalActivityTabWithTagDTO.setTimeTypeId(activity.getBalanceSettingsActivityTab().getTimeTypeId());
        generalActivityTabWithTagDTO.setOnCallTimePresent(activity.getBalanceSettingsActivityTab().getOnCallTimePresent());
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
            exceptionService.dataNotFoundByIdException("message.activity.timetype.notfound");
        }
        activity.getGeneralActivityTab().setBackgroundColor(timeType.getBackgroundColor());
        activity.getGeneralActivityTab().setColorPresent(true);
        activity.getBalanceSettingsActivityTab().setTimeType(timeType.getSecondLevelType());
        Long countryId = activity.getCountryId();
        if (countryId == null) {
            countryId = userIntegrationService.getCountryIdOfOrganization(activity.getUnitId());
        }
        activity.getBalanceSettingsActivityTab().setTimeTypeId(generalActivityTabDTO.getTimeTypeId());
        activity.getBalanceSettingsActivityTab().setAddTimeTo(generalActivityTabDTO.getAddTimeTo());
        activity.getBalanceSettingsActivityTab().setOnCallTimePresent(generalActivityTabDTO.isOnCallTimePresent());
        activity.getBalanceSettingsActivityTab().setNegativeDayBalancePresent(generalActivityTabDTO.getNegativeDayBalancePresent());
        updateActivityCategory(activity, countryId);
        return activity.getBalanceSettingsActivityTab();
    }

    public void updateActivityCategory(Activity activity, Long countryId) {
        TimeType timeType = timeTypeMongoRepository.findOneById(activity.getBalanceSettingsActivityTab().getTimeTypeId(), countryId);
        if (timeType == null)
            exceptionService.dataNotFoundByIdException("message.timetype.notfound");
        ActivityCategory category = activityCategoryRepository.getCategoryByTimeType(countryId, activity.getBalanceSettingsActivityTab().getTimeTypeId());
        if (category == null) {
            category = new ActivityCategory(timeType.getLabel(), "", countryId, timeType.getId());
            save(category);
        }
        activity.getGeneralActivityTab().setCategoryId(category.getId());
    }

    public ActivityTabsWrapper updateTimeCalculationTabOfActivity(TimeCalculationActivityDTO timeCalculationActivityDTO) {
        TimeCalculationActivityTab timeCalculationActivityTab = new TimeCalculationActivityTab();
        ObjectMapperUtils.copyProperties(timeCalculationActivityDTO, timeCalculationActivityTab);
        Activity activity = activityMongoRepository.findOne(new BigInteger(String.valueOf(timeCalculationActivityDTO.getActivityId())));
        if (!Optional.ofNullable(activity).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.activity.timecare.id", timeCalculationActivityDTO.getActivityId());
        }
        activity.setTimeCalculationActivityTab(timeCalculationActivityTab);
        if (!timeCalculationActivityTab.getMethodForCalculatingTime().equals(FULL_WEEK)) {
            timeCalculationActivityTab.setDayTypes(activity.getRulesActivityTab().getDayTypes());
        }
        save(activity);
        return new ActivityTabsWrapper(timeCalculationActivityTab);
    }

    public List<CompositeShiftActivityDTO> assignCompositeActivitiesInActivity(BigInteger activityId, List<CompositeShiftActivityDTO> compositeShiftActivityDTOs) {
        Activity activity = activityMongoRepository.findById(activityId).orElse(null);
        if (activity == null) {
            exceptionService.dataNotFoundByIdException("exception.dataNotFound", "activity", activityId);
        }
        Set<BigInteger> compositeShiftIds = compositeShiftActivityDTOs.stream().map(compositeShiftActivityDTO -> compositeShiftActivityDTO.getActivityId()).collect(Collectors.toSet());
        List<ActivityWrapper> activityMatched = activityMongoRepository.findActivityAndTimeTypeByActivityIds(compositeShiftIds);
        if (activityMatched.size() != compositeShiftIds.size()) {
            exceptionService.illegalArgumentException("message.mismatched-ids", compositeShiftIds);
        }
        organizationActivityService.verifyBreakAllowedOfActivities(activity.getRulesActivityTab().isBreakAllowed(), activityMatched);
        List<Activity> activityList = activityMongoRepository.findAllActivitiesByIds(activityMatched.stream().map(k -> k.getActivity().getId()).collect(Collectors.toSet()));
        List<CompositeActivity> compositeActivities = compositeShiftActivityDTOs.stream().map(compositeShiftActivityDTO -> new CompositeActivity(compositeShiftActivityDTO.getActivityId(), compositeShiftActivityDTO.isAllowedBefore(), compositeShiftActivityDTO.isAllowedAfter())).collect(Collectors.toList());
        activity.setCompositeActivities(compositeActivities);
        updateCompositeActivity(activityList, activity, compositeActivities);
        save(activity);
        return compositeShiftActivityDTOs;
    }

    private void updateCompositeActivity(List<Activity> activityList, Activity activity, List<CompositeActivity> compositeActivities) {
        Map<BigInteger, Activity> activityMap = activityList.stream().collect(Collectors.toMap(k -> k.getId(), v -> v));
        for (CompositeActivity compositeActivity : compositeActivities) {
            Activity composedActivity = activityMap.get(compositeActivity.getActivityId());
            Optional<CompositeActivity> optionalCompositeActivity = composedActivity.getCompositeActivities().stream().filter(a -> a.getActivityId().equals(activity.getId())).findFirst();
            if (optionalCompositeActivity.isPresent()) {
                CompositeActivity compositeActivityOfAnotherActivity = optionalCompositeActivity.get();
                compositeActivityOfAnotherActivity.setAllowedBefore(compositeActivity.isAllowedAfter());
                compositeActivityOfAnotherActivity.setAllowedAfter(compositeActivity.isAllowedBefore());
            }
        }
        if (isCollectionNotEmpty(activityList)) {
            save(activityList);
        }
    }

    public Set<BigInteger> assignChildActivitiesInActivity(BigInteger activityId, Set<BigInteger> childActivitiesIds) {
        Activity activity = activityMongoRepository.findById(activityId).orElse(null);
        if (activity == null) {
            exceptionService.dataNotFoundByIdException("exception.dataNotFound", "activity", activityId);
        }
        List<ActivityDTO> activityMatched = activityMongoRepository.findChildActivityActivityIds(childActivitiesIds);
        if (activityMatched.size() != childActivitiesIds.size()) {
            exceptionService.illegalArgumentException("message.mismatched-ids", childActivitiesIds);
        }
        organizationActivityService.verifyChildActivity(activityMatched, activity);
        activity.setChildActivityIds(childActivitiesIds);
        //updateCompositeActivity(activityList, activity, compositeActivities);
        save(activity);
        return childActivitiesIds;
    }


    public ActivityTabsWrapper getTimeCalculationTabOfActivity(BigInteger activityId, Long countryId) {
        List<DayType> dayTypes = userIntegrationService.getDayTypesByCountryId(countryId);
        Activity activity = activityMongoRepository.findOne(activityId);
        TimeCalculationActivityTab timeCalculationActivityTab = activity.getTimeCalculationActivityTab();
        List<Long> rulesTabDayTypes = activity.getRulesActivityTab().getDayTypes();
        return new ActivityTabsWrapper(timeCalculationActivityTab, dayTypes, rulesTabDayTypes);
    }


    public ActivityWithCompositeDTO getCompositeAndChildActivityOfCountryActivity(BigInteger activityId,Long countryId){
        ActivityWithCompositeDTO  activity=getCompositeShiftTabOfActivity(activityId);
        List<ActivityTagDTO> activityTagDTO=activityMongoRepository.findAllowChildActivityByCountryId(countryId);
        activity.setAvailableChildActivityIds(checkActivityAllowForChildActivities(activityTagDTO,activity));
        return activity;
    }


    public ActivityWithCompositeDTO getCompositeAndChildActivityOfUnitActivity(BigInteger activityId,Long unitId){
        ActivityWithCompositeDTO  activity=getCompositeShiftTabOfActivity(activityId);
        List<ActivityTagDTO> activityTagDTO = activityMongoRepository.findAllowChildActivityByUnitIdAndDeleted(unitId, false);
        activity.setAvailableChildActivityIds(checkActivityAllowForChildActivities(activityTagDTO,activity));
        return activity;
    }


    public ActivityWithCompositeDTO getCompositeShiftTabOfActivity(BigInteger activityId) {
        ActivityWithCompositeDTO  activity=activityMongoRepository.findActivityByActivityId(activityId);
        if (isNull(activity)) {
            exceptionService.dataNotFoundByIdException("message.activity.id", activityId);
        }
        List<CompositeActivityDTO> compositeActivities;
        if (Optional.ofNullable(activity.getCompositeActivities()).isPresent() && !activity.getCompositeActivities().isEmpty()) {
            compositeActivities = activityMongoRepository.getCompositeActivities(activityId);
            activity.setCompositeActivities(compositeActivities);
        }
        return activity;
    }

    public ActivityTabsWrapper updateIndividualPointsTab(IndividualPointsActivityTabDTO individualPointsDTO) {
        IndividualPointsActivityTab individualPointsActivityTab = new IndividualPointsActivityTab();
        ObjectMapperUtils.copyProperties(individualPointsDTO, individualPointsActivityTab);
        Activity activity = activityMongoRepository.findOne(new BigInteger(String.valueOf(individualPointsDTO.getActivityId())));
        if (!Optional.ofNullable(activity).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.activity.id", individualPointsDTO.getActivityId());
        }
        activity.setIndividualPointsActivityTab(individualPointsActivityTab);
        save(activity);
        return new ActivityTabsWrapper(individualPointsActivityTab);
    }

    public IndividualPointsActivityTab getIndividualPointsTabOfActivity(BigInteger activityId) {
        Activity activity = activityMongoRepository.findOne(activityId);
        return activity.getIndividualPointsActivityTab();
    }

    public ActivityTabsWrapper updateRulesTab(RulesActivityTabDTO rulesActivityDTO) {
        validateActivityTimeRules(rulesActivityDTO.getEarliestStartTime(), rulesActivityDTO.getLatestStartTime(), rulesActivityDTO.getMaximumEndTime(), rulesActivityDTO.getShortestTime(), rulesActivityDTO.getLongestTime());
        RulesActivityTab rulesActivityTab = ObjectMapperUtils.copyPropertiesByMapper(rulesActivityDTO, RulesActivityTab.class);
        Activity activity = activityMongoRepository.findOne(rulesActivityDTO.getActivityId());
        if (!Optional.ofNullable(activity).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.activity.id", rulesActivityDTO.getActivityId());
        }
        if (activity.getRulesActivityTab().isBreakAllowed() != rulesActivityDTO.isBreakAllowed()) {
            if (isCollectionNotEmpty(activity.getCompositeActivities()) || activityMongoRepository.existsByActivityIdInCompositeActivitiesAndDeletedFalse(rulesActivityDTO.getActivityId())) {
                exceptionService.actionNotPermittedException("error.activity.being.used", activity.getName());
            }
        }
        if (rulesActivityDTO.getCutOffIntervalUnit() != null && rulesActivityDTO.getCutOffStartFrom() != null) {
            if (CutOffIntervalUnit.DAYS.equals(rulesActivityDTO.getCutOffIntervalUnit()) && rulesActivityDTO.getCutOffdayValue() == 0) {
                exceptionService.invalidRequestException("error.DayValue.zero");
            }
            List<CutOffInterval> cutOffIntervals = getCutoffInterval(rulesActivityDTO.getCutOffStartFrom(), rulesActivityDTO.getCutOffIntervalUnit(), rulesActivityDTO.getCutOffdayValue());
            rulesActivityTab.setCutOffIntervals(cutOffIntervals);
            rulesActivityDTO.setCutOffIntervals(cutOffIntervals);
        }
        activity.setRulesActivityTab(rulesActivityTab);
        if (!activity.getTimeCalculationActivityTab().getMethodForCalculatingTime().equals(FULL_WEEK)) {
            activity.getTimeCalculationActivityTab().setDayTypes(activity.getRulesActivityTab().getDayTypes());
        }
        save(activity);
        return new ActivityTabsWrapper(rulesActivityTab);
    }

    public ActivityTabsWrapper getPhaseSettingTabOfActivity(BigInteger activityId, Long countryId) {
        Activity activity = activityMongoRepository.findOne(activityId);
        if (!Optional.ofNullable(activity).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.activity.id", activityId);
        }
        DayTypeEmploymentTypeWrapper dayTypeEmploymentTypeWrapper = userIntegrationService.getDayTypesAndEmploymentTypes(countryId);
        List<DayType> dayTypes = dayTypeEmploymentTypeWrapper.getDayTypes();
        List<EmploymentTypeDTO> employmentTypeDTOS = dayTypeEmploymentTypeWrapper.getEmploymentTypes();
        Set<AccessGroupRole> roles = AccessGroupRole.getAllRoles();
        PhaseSettingsActivityTab phaseSettingsActivityTab = activity.getPhaseSettingsActivityTab();
        return new ActivityTabsWrapper(roles, phaseSettingsActivityTab, dayTypes, employmentTypeDTOS);
    }

    public PhaseSettingsActivityTab updatePhaseSettingTab(PhaseSettingsActivityTab phaseSettingsActivityTab) {
        Activity activity = activityMongoRepository.findOne(phaseSettingsActivityTab.getActivityId());
        if (!Optional.ofNullable(activity).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.activity.id", phaseSettingsActivityTab.getActivityId());
        }
        activity.setPhaseSettingsActivityTab(phaseSettingsActivityTab);
        save(activity);
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
        Activity activity = activityMongoRepository.findOne(activityId);
        return new ActivityTabsWrapper(activity.getNotesActivityTab());
    }

    public ActivityTabsWrapper updateCommunicationTabOfActivity(CommunicationActivityDTO communicationActivityDTO) {
        CommunicationActivityTab communicationActivityTab = new CommunicationActivityTab();
        validateReminderSettings(communicationActivityDTO);
        ObjectMapperUtils.copyProperties(communicationActivityDTO, communicationActivityTab);
        Activity activity = activityMongoRepository.findOne(communicationActivityDTO.getActivityId());
        if (!Optional.ofNullable(activity).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.activity.id", communicationActivityDTO.getActivityId());
        }
        activity.setCommunicationActivityTab(communicationActivityTab);
        save(activity);
        return new ActivityTabsWrapper(communicationActivityTab);
    }

    public ActivityTabsWrapper getCommunicationTabOfActivity(BigInteger activityId) {
        Activity activity = activityMongoRepository.findOne(activityId);
        if (!Optional.ofNullable(activity).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.activity.id", activityId);
        }
        return new ActivityTabsWrapper(activity.getCommunicationActivityTab());
    }
    // BONUS

    public ActivityTabsWrapper updateBonusTabOfActivity(BonusActivityDTO bonusActivityDTO) {
        Activity activity = activityMongoRepository.findOne(bonusActivityDTO.getActivityId());
        if (!Optional.ofNullable(activity).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.activity.id", bonusActivityDTO.getActivityId());
        }
        BonusActivityTab bonusActivityTab = new BonusActivityTab(bonusActivityDTO.getBonusHoursType(), bonusActivityDTO.isOverRuleCtaWta());
        activity.setBonusActivityTab(bonusActivityTab);
        save(activity);
        return new ActivityTabsWrapper(bonusActivityTab);
    }

    public ActivityTabsWrapper getBonusTabOfActivity(BigInteger activityId) {
        Activity activity = activityMongoRepository.findOne(activityId);
        if (!Optional.ofNullable(activity).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.activity.id", activityId);
        }
        return new ActivityTabsWrapper(activity.getBonusActivityTab());
    }

    public ActivityTabsWrapper updateSkillTabOfActivity(SkillActivityDTO skillActivityDTO) {
        Activity activity = activityMongoRepository.findOne(skillActivityDTO.getActivityId());
        if (!Optional.ofNullable(activity).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.activity.id", skillActivityDTO.getActivityId());
        }
        SkillActivityTab skillActivityTab = new SkillActivityTab(skillActivityDTO.getActivitySkills());
        activity.setSkillActivityTab(skillActivityTab);
        save(activity);
        return new ActivityTabsWrapper(skillActivityTab);
    }

    public ActivityTabsWrapper getSkillTabOfActivity(BigInteger activityId) {
        Activity activity = activityMongoRepository.findOne(activityId);
        return new ActivityTabsWrapper(activity.getSkillActivityTab());
    }

    public void updateOrgMappingDetailOfActivity(OrganizationMappingActivityDTO organizationMappingActivityDTO, BigInteger activityId) {
        Activity activity = activityMongoRepository.findOne(activityId);
        if (!Optional.ofNullable(activity).isPresent()) {
            exceptionService.dataNotFoundByIdException("exception.dataNotFound", "activity", activityId);
        }
        boolean isSuccess = userIntegrationService.verifyOrganizationExpertizeAndRegions(organizationMappingActivityDTO);
        if (!isSuccess) {
            exceptionService.dataNotFoundException("message.parameters.incorrect");
        }
        activity.setRegions(organizationMappingActivityDTO.getRegions());
        activity.setExpertises(organizationMappingActivityDTO.getExpertises());
        activity.setOrganizationSubTypes(organizationMappingActivityDTO.getOrganizationSubTypes());
        activity.setOrganizationTypes(organizationMappingActivityDTO.getOrganizationTypes());
        activity.setLevels(organizationMappingActivityDTO.getLevel());
        activity.setEmploymentTypes(organizationMappingActivityDTO.getEmploymentTypes());
        save(activity);
        if (activity.getUnitId() != null) {
            plannerSyncService.publishActivity(activity.getUnitId(), activity, IntegrationOperation.UPDATE);
        }
    }

    public OrganizationMappingActivityDTO getOrgMappingDetailOfActivity(BigInteger activityId) {
        Activity activity = activityMongoRepository.findOne(activityId);
        if (!Optional.ofNullable(activity).isPresent()) {
            exceptionService.dataNotFoundByIdException("exception.dataNotFound", "activity", activityId);
        }
        OrganizationMappingActivityDTO organizationMappingActivityDTO = new OrganizationMappingActivityDTO();
        organizationMappingActivityDTO.setOrganizationSubTypes(activity.getOrganizationSubTypes());
        organizationMappingActivityDTO.setExpertises(activity.getExpertises());
        organizationMappingActivityDTO.setRegions(activity.getRegions());
        organizationMappingActivityDTO.setLevel(activity.getLevels());
        organizationMappingActivityDTO.setOrganizationTypes(activity.getOrganizationTypes());
        organizationMappingActivityDTO.setEmploymentTypes(activity.getEmploymentTypes());
        return organizationMappingActivityDTO;

    }

    public ActivityWithUnitIdDTO getActivityByUnitId(long unitId, String type) {
        OrganizationTypeAndSubTypeDTO organizationTypeAndSubTypeDTO = userIntegrationService.getOrganizationTypeAndSubTypeByUnitId(unitId, type);
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
        Activity activity = activityMongoRepository.findOne(activityId);
        if (!Optional.ofNullable(activity).isPresent()) {
            exceptionService.dataNotFoundByIdException("exception.dataNotFound", "activity", activityId);
        }
        activity.setOptaPlannerSettingActivityTab(optaPlannerSettingActivityTab);
        save(activity);
        return new ActivityTabsWrapper(optaPlannerSettingActivityTab);
    }

    public ActivityTabsWrapper getOptaPlannerSettingsTabOfActivity(BigInteger activityId) {
        Activity activity = activityMongoRepository.findOne(activityId);
        return new ActivityTabsWrapper(activity.getOptaPlannerSettingActivityTab());
    }

    public ActivityTabsWrapper getCtaAndWtaSettingsTabOfActivity(BigInteger activityId) {
        Activity activity = activityMongoRepository.findOne(activityId);
        return new ActivityTabsWrapper(activity.getCtaAndWtaSettingsActivityTab());
    }

    public ActivityTabsWrapper updateCtaAndWtaSettingsTabOfActivity(CTAAndWTASettingsActivityTabDTO ctaAndWtaSettingsActivityTabDTO) {
        Activity activity = activityMongoRepository.findOne(new BigInteger(String.valueOf(ctaAndWtaSettingsActivityTabDTO.getActivityId())));
        if (!Optional.ofNullable(activity).isPresent()) {
            exceptionService.dataNotFoundByIdException("exception.dataNotFound", "activity", ctaAndWtaSettingsActivityTabDTO.getActivityId());
        }
        CTAAndWTASettingsActivityTab ctaAndWtaSettingsActivityTab = new CTAAndWTASettingsActivityTab(ctaAndWtaSettingsActivityTabDTO.isEligibleForCostCalculation());
        activity.setCtaAndWtaSettingsActivityTab(ctaAndWtaSettingsActivityTab);
        save(activity);
        return new ActivityTabsWrapper(ctaAndWtaSettingsActivityTab);
    }

    public PhaseActivityDTO getActivityAndPhaseByUnitId(long unitId, String type) {
        SelfRosteringMetaData publicHolidayDayTypeWrapper = userIntegrationService.getPublicHolidaysDayTypeAndReasonCodeByUnitId(unitId);
        if (!Optional.ofNullable(publicHolidayDayTypeWrapper).isPresent()) {
            exceptionService.internalServerError("message.selfRostering.metaData.null");
        }
        List<DayType> dayTypes = publicHolidayDayTypeWrapper.getDayTypes();
        LocalDate date = LocalDate.now();
        int year = date.getYear();
        TemporalField weekOfWeekBasedYear = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear();
        int currentWeek = date.get(weekOfWeekBasedYear);
        List<PhaseDTO> phaseDTOs = phaseService.getApplicablePlanningPhasesByOrganizationId(unitId, Sort.Direction.DESC);
        // Set access Role of staff
        ReasonCodeWrapper reasonCodeWrapper = publicHolidayDayTypeWrapper.getReasonCodeWrapper();
        ArrayList<PhaseWeeklyDTO> phaseWeeklyDTOS = new ArrayList<PhaseWeeklyDTO>();
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
        // Creating dummy next remaining 2 years as PHASE with lowest sequence
        if (phaseDTOs.size() > 0) {
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
        List<ActivityWithCompositeDTO> activities = activityMongoRepository.findAllActivityByUnitIdWithCompositeActivities(unitId);
        List<ShiftTemplateDTO> shiftTemplates = shiftTemplateService.getAllShiftTemplates(unitId);
        PlanningPeriodDTO planningPeriodDTO = planningPeriodService.getStartDateAndEndDateOfPlanningPeriodByUnitId(unitId);
        if (isNull(planningPeriodDTO)) {
            exceptionService.dataNotFoundException("message.periodsetting.notFound");
        }
        return new PhaseActivityDTO(activities, phaseWeeklyDTOS, dayTypes, reasonCodeWrapper.getUserAccessRoleDTO(), shiftTemplates, phaseDTOs, phaseService.getActualPhasesByOrganizationId(unitId), reasonCodeWrapper.getReasonCodes(), planningPeriodDTO.getStartDate(), planningPeriodDTO.getEndDate(), publicHolidayDayTypeWrapper.getPublicHolidays());
    }

    public GeneralActivityTab addIconInActivity(BigInteger activityId, MultipartFile file) throws IOException {
        Activity activity = activityMongoRepository.findOne(activityId);
        if (!Optional.ofNullable(activity).isPresent()) {
            exceptionService.dataNotFoundByIdException("exception.dataNotFound", "activity", activityId);
        }
        byte[] bytes = file.getBytes();
        String modifiedFileName = System.currentTimeMillis() + file.getOriginalFilename().substring(file.getOriginalFilename().length() - 4);
        Path path = Paths.get(ACTIVITY_TYPE_IMAGE_PATH + modifiedFileName);
        Files.write(path, bytes);
        activity.getGeneralActivityTab().setOriginalIconName(file.getOriginalFilename());
        activity.getGeneralActivityTab().setModifiedIconName(modifiedFileName);
        save(activity);
        return activity.getGeneralActivityTab();
    }

    public boolean deleteCountryActivity(BigInteger activityId) {
        Activity activity = activityMongoRepository.findOne(activityId);
        if (!Optional.ofNullable(activity).isPresent()) {
            exceptionService.dataNotFoundException("message.activity.id", activityId);
        }
        if (activity.getState().equals(ActivityStateEnum.LIVE)) {
            exceptionService.actionNotPermittedException("exception.alreadyInUse", "activity");
        }
        activity.setDeleted(true);
        save(activity);
        return true;
    }

    public List<Activity> createActivitiesFromTimeCare(GetAllActivitiesResponse getAllActivitiesResponse, Long unitId, Long countryId, BigInteger presenceTimeTypeId, BigInteger absenceTimeTypeId) {
        List<TimeCareActivity> timeCareActivities = getAllActivitiesResponse.getGetAllActivitiesResult();
        List<String> externalIdsOfAllActivities = timeCareActivities.stream().map(timeCareActivity -> timeCareActivity.getId()).collect(Collectors.toList());
        List<Activity> countryActivities = createActivatesForCountryFromTimeCare(timeCareActivities, unitId, countryId, externalIdsOfAllActivities, presenceTimeTypeId, absenceTimeTypeId);
        mapActivitiesInOrganization(countryActivities, unitId, externalIdsOfAllActivities);
        return countryActivities;
    }

    private List<Activity> createActivatesForCountryFromTimeCare(List<TimeCareActivity> timeCareActivities, Long unitId, Long countryId,
                                                                 List<String> externalIdsOfAllActivities, BigInteger presenceTimeTypeId, BigInteger absenceTimeTypeId) {
        OrganizationDTO organizationDTO = userIntegrationService.getOrganizationDTO(unitId);
        if (organizationDTO == null) {
            exceptionService.dataNotFoundByIdException("message.organization.id");
        }
        ActivityCategory activityCategory = activityCategoryRepository.getCategoryByNameAndCountryAndDeleted("NONE", countryId, false);
        if (activityCategory == null) {
            activityCategory = new ActivityCategory("NONE", "", countryId, null);
            save(activityCategory);
        }
        Long orgType = organizationDTO.getOrganizationType().getId();
        List<Long> orgSubTypes = organizationDTO.getOrganizationSubTypes().stream().map(organizationTypeDTO -> organizationTypeDTO.getId()).collect(Collectors.toList());
        Set<String> skillsOfAllTimeCareActivity = timeCareActivities.stream().flatMap(timeCareActivity -> timeCareActivity.getArrayOfSkill().stream().
                map(skill -> skill)).collect(Collectors.toSet());
        List<Skill> skills = skillRestClient.getSkillsByName(skillsOfAllTimeCareActivity, countryId);
        List<Activity> activitiesByExternalIds = activityMongoRepository.findByExternalIdIn(externalIdsOfAllActivities);
        List<PhaseDTO> phases = phaseService.getPhasesByCountryId(countryId);
        List<Activity> activities = new ArrayList<>(timeCareActivities.size());
        GlideTimeSettingsDTO glideTimeSettingsDTO = glideTimeSettingsService.getGlideTimeSettings(countryId);
        for (TimeCareActivity timeCareActivity : timeCareActivities) {
            Activity activity = initializeTimeCareActivities(timeCareActivity, orgType, orgSubTypes, countryId,
                    glideTimeSettingsDTO, phases, activitiesByExternalIds, activityCategory, skills, presenceTimeTypeId, absenceTimeTypeId);
            activities.add(activity);
        }
        save(activities);
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
            save(organizationActivities);
        }
    }

    public NotesActivityTab addDocumentInNotesTab(BigInteger activityId, MultipartFile file) throws IOException {
        Activity activity = activityMongoRepository.findOne(activityId);
        if (!Optional.ofNullable(activity).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.organization.id", activityId);
        }
        byte[] bytes = file.getBytes();
        String modifiedFileName = System.currentTimeMillis() + file.getOriginalFilename().substring(file.getOriginalFilename().length() - 4);
        Path path = Paths.get(ACTIVITY_TYPE_IMAGE_PATH + modifiedFileName);
        Files.write(path, bytes);
        activity.getNotesActivityTab().setOriginalDocumentName(file.getOriginalFilename());
        activity.getNotesActivityTab().setModifiedDocumentName(modifiedFileName);
        save(activity);
        return activity.getNotesActivityTab();
    }

    public Boolean publishActivity(BigInteger activityId) {
        Activity activity = activityMongoRepository.findOne(activityId);
        if (!Optional.ofNullable(activity).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.organization.id", activityId);
        }
        if (activity.getState().equals(ActivityStateEnum.PUBLISHED) || activity.getState().equals(ActivityStateEnum.LIVE)) {
            exceptionService.actionNotPermittedException("message.activity.published", activityId);
        }
        if (activity.getBalanceSettingsActivityTab().getTimeTypeId() == null) {
            exceptionService.actionNotPermittedException("message.activity.timeTypeOrPresenceType.null", activity.getName());
        }
        activity.setState(ActivityStateEnum.PUBLISHED);
        save(activity);
        return true;
    }

    public ActivityDTO copyActivityDetails(Long countryId, BigInteger activityId, ActivityDTO activityDTO) {
        //Need to know why we are returning object here as we can also return a simple boolean to check whether activity exist or not
        Activity activity = activityMongoRepository.
                findByNameIgnoreCaseAndCountryIdAndByDate(activityDTO.getName().trim(), countryId, activityDTO.getStartDate(), activityDTO.getEndDate());
        if (Optional.ofNullable(activity).isPresent() && activityDTO.getStartDate().isBefore(activity.getGeneralActivityTab().getStartDate())) {
            exceptionService.actionNotPermittedException("message.activity.overlaping");
        }
        if (Optional.ofNullable(activity).isPresent()) {
            exceptionService.dataNotFoundException(activity.getGeneralActivityTab().getEndDate() == null ? "message.activity.enddate.required" : "message.activity.active.alreadyExists");
        }
        Optional<Activity> activityFromDatabase = activityMongoRepository.findById(activityId);
        if (!activityFromDatabase.isPresent() || activityFromDatabase.get().isDeleted() || !countryId.equals(activityFromDatabase.get().getCountryId())) {
            exceptionService.dataNotFoundByIdException("message.activity.id", activityId);
        }
        Activity activityCopied = ObjectMapperUtils.copyPropertiesByMapper(activityFromDatabase.get(), Activity.class);
        activityCopied.setId(null);
        activityCopied.setName(activityDTO.getName().trim());
        activityCopied.getGeneralActivityTab().setName(activityDTO.getName().trim());
        activityCopied.getGeneralActivityTab().setStartDate(activityDTO.getStartDate());
        activityCopied.setState(ActivityStateEnum.PUBLISHED);
        activityCopied.getGeneralActivityTab().setEndDate(activityDTO.getEndDate());
        save(activityCopied);
        activityDTO.setId(activityCopied.getId());
        return activityDTO;
    }

    public ActivityTabsWrapper getLocationsTabOfActivity(BigInteger activityId) {
        Activity activity = activityMongoRepository.findOne(activityId);
        return new ActivityTabsWrapper(activity.getLocationActivityTab());
    }

    public ActivityTabsWrapper updateLocationsTabOfActivity(LocationActivityTabDTO locationActivityTabDTO) {
        Activity activity = activityMongoRepository.findOne(locationActivityTabDTO.getActivityId());
        if (!Optional.ofNullable(activity).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.activity.id", locationActivityTabDTO.getActivityId());
        }
        LocationActivityTab locationActivityTab = new LocationActivityTab(locationActivityTabDTO.getGlideTimeForCheckIn(), locationActivityTabDTO.getGlideTimeForCheckOut());
        activity.setLocationActivityTab(locationActivityTab);
        save(activity);
        return new ActivityTabsWrapper(locationActivityTab);
    }

    public ActivityWithTimeTypeDTO getActivitiesWithTimeTypes(long countryId) {
        List<ActivityDTO> activityDTOS = activityMongoRepository.findAllActivitiesWithTimeTypes(countryId);
        List<TimeTypeDTO> timeTypeDTOS = timeTypeService.getAllTimeType(null, countryId);
        List<OpenShiftIntervalDTO> intervals = openShiftIntervalRepository.getAllByCountryIdAndDeletedFalse(countryId);
        List<CounterDTO> counters = counterRepository.getAllCounterBySupportedModule(ModuleType.OPEN_SHIFT);
        return new ActivityWithTimeTypeDTO(activityDTOS, timeTypeDTOS, intervals, counters);
    }

    public List<ActivityDTO> getActivitiesWithCategories(long unitId) {
        List<ActivityDTO> activityTypeList = activityMongoRepository.findAllActivityByUnitId(unitId, false);
        return activityTypeList;
    }

    private boolean validateReminderSettings(CommunicationActivityDTO communicationActivityDTO) {
        int counter = 0;
        if (!communicationActivityDTO.getActivityReminderSettings().isEmpty()) {
            for (ActivityReminderSettings currentSettings : communicationActivityDTO.getActivityReminderSettings()) {
                if (currentSettings.getSendReminder().getDurationType() == DurationType.MINUTES &&
                        (currentSettings.getRepeatReminder().getDurationType() == DurationType.DAYS)) {
                    exceptionService.actionNotPermittedException("repeat_value_cant_be", currentSettings.getRepeatReminder().getDurationType());
                }
                // if both are same ie days or minute and reminder value id greater than time value
                if (currentSettings.getSendReminder().getDurationType() == currentSettings.getRepeatReminder().getDurationType() &&
                        currentSettings.getSendReminder().getTimeValue() < currentSettings.getRepeatReminder().getTimeValue()) {
                    exceptionService.actionNotPermittedException("reminder_value_cant_be_greater_than_repeat_value",
                            currentSettings.getRepeatReminder().getTimeValue(), currentSettings.getRepeatReminder().getDurationType(),
                            currentSettings.getSendReminder().getTimeValue(), currentSettings.getSendReminder().getDurationType());
                }
                if (counter > 0) {
                    ActivityReminderSettings previousSettings = communicationActivityDTO.getActivityReminderSettings().get(counter - 1);
                    if (previousSettings.isRepeatAllowed()) {
                        validateWithPreviousFrequency(currentSettings, previousSettings.getRepeatReminder());
                    } else {
                        validateWithPreviousFrequency(currentSettings, previousSettings.getSendReminder());
                    }
                }
                if (currentSettings.getId() == null) {
                    currentSettings.setId(mongoSequenceRepository.nextSequence(ActivityReminderSettings.class.getSimpleName()));
                }
                counter++;
            }
        }
        return true;
    }

    private void validateWithPreviousFrequency(ActivityReminderSettings currentSettings, FrequencySettings frequencySettings) {
        // if both are same ie days or minute and reminder value id greater than time value
        if (currentSettings.getSendReminder().getDurationType() == frequencySettings.getDurationType() &&
                currentSettings.getSendReminder().getTimeValue() > frequencySettings.getTimeValue()) {
            exceptionService.actionNotPermittedException("reminder_value_cant_be_greater_than_last_repeat_value",
                    currentSettings.getSendReminder().getTimeValue(), currentSettings.getSendReminder().getDurationType()
                    , frequencySettings.getTimeValue(), frequencySettings.getDurationType());
        }
        if (frequencySettings.getDurationType() == DurationType.MINUTES
                && currentSettings.getSendReminder().getDurationType() == DurationType.DAYS) {
            exceptionService.actionNotPermittedException("new_value_cant_be_greater_than_previous",
                    currentSettings.getSendReminder().getTimeValue(), currentSettings.getSendReminder().getDurationType(), frequencySettings.getTimeValue(), frequencySettings.getDurationType());
        }
        if (currentSettings.getSendReminder().getDurationType() == DurationType.MINUTES &&
                (currentSettings.getSendReminder().getDurationType() == DurationType.DAYS)) {
            exceptionService.actionNotPermittedException("new_value_cant_be_greater_than_previous",
                    currentSettings.getSendReminder().getTimeValue(), currentSettings.getSendReminder().getDurationType(), frequencySettings.getTimeValue(), frequencySettings.getDurationType());
        }
    }

    public void validateActivityTimeRules(LocalTime earliestStartTime, LocalTime latestStartTime, LocalTime maximumEndTime, Short shortestTime, Short longestTime) {
        if (shortestTime != null && longestTime != null && shortestTime > longestTime) {
            exceptionService.actionNotPermittedException("shortest.time.greater.longest");
        }
    }

    public boolean removeAttachementsFromActivity(BigInteger activityId, boolean removeNotes) {
        Activity activity = activityMongoRepository.findOne(activityId);
        if (isNull(activity)) {
            exceptionService.dataNotFoundByIdException("message.organization.id", activityId);
        }
        if (removeNotes) {
            activity.getNotesActivityTab().setOriginalDocumentName(null);
            activity.getNotesActivityTab().setModifiedDocumentName(null);
        } else {
            activity.getGeneralActivityTab().setOriginalIconName(null);
            activity.getGeneralActivityTab().setModifiedIconName(null);
        }
        save(activity);
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
}

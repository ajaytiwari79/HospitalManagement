package com.kairos.service.organization;

import com.kairos.commons.custom_exception.DataNotFoundByIdException;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.constants.AppConstants;
import com.kairos.dto.TranslationInfo;
import com.kairos.dto.activity.activity.ActivityDTO;
import com.kairos.dto.activity.activity.ActivityWithTimeTypeDTO;
import com.kairos.dto.activity.activity.activity_tabs.*;
import com.kairos.dto.activity.counter.configuration.CounterDTO;
import com.kairos.dto.activity.counter.enums.ModuleType;
import com.kairos.dto.activity.open_shift.OpenShiftIntervalDTO;
import com.kairos.dto.activity.phase.PhaseDTO;
import com.kairos.dto.activity.presence_type.PresenceTypeDTO;
import com.kairos.dto.activity.presence_type.PresenceTypeWithTimeTypeDTO;
import com.kairos.dto.activity.time_type.TimeTypeDTO;
import com.kairos.dto.activity.unit_settings.TAndAGracePeriodSettingDTO;
import com.kairos.dto.activity.unit_settings.UnitSettingDTO;
import com.kairos.dto.user.access_permission.AccessGroupRole;
import com.kairos.dto.user.country.agreement.cta.cta_response.EmploymentTypeDTO;
import com.kairos.dto.user.country.day_type.DayType;
import com.kairos.dto.user.country.day_type.DayTypeEmploymentTypeWrapper;
import com.kairos.dto.user.country.tag.TagDTO;
import com.kairos.dto.user.organization.OrgTypeAndSubTypeDTO;
import com.kairos.dto.user.organization.OrganizationDTO;
import com.kairos.enums.ActivityStateEnum;
import com.kairos.enums.OrganizationHierarchy;
import com.kairos.enums.ProtectedDaysOffUnitSettings;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.activity.ActivityPriority;
import com.kairos.persistence.model.activity.TimeType;
import com.kairos.persistence.model.activity.tabs.ActivityCategory;
import com.kairos.persistence.model.activity.tabs.BalanceSettingsActivityTab;
import com.kairos.persistence.model.activity.tabs.GeneralActivityTab;
import com.kairos.persistence.model.activity.tabs.TimeCalculationActivityTab;
import com.kairos.persistence.model.activity.tabs.rules_activity_tab.RulesActivityTab;
import com.kairos.persistence.model.open_shift.OrderAndActivityDTO;
import com.kairos.persistence.model.phase.Phase;
import com.kairos.persistence.repository.activity.ActivityCategoryRepository;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.counter.CounterRepository;
import com.kairos.persistence.repository.open_shift.OpenShiftIntervalRepository;
import com.kairos.persistence.repository.tag.TagMongoRepository;
import com.kairos.persistence.repository.time_type.TimeTypeMongoRepository;
import com.kairos.persistence.repository.unit_settings.UnitSettingRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.MongoBaseService;
import com.kairos.service.activity.ActivityPriorityService;
import com.kairos.service.activity.ActivityService;
import com.kairos.service.activity.PlannedTimeTypeService;
import com.kairos.service.activity.TimeTypeService;
import com.kairos.service.counter.CounterDistService;
import com.kairos.service.counter.KPISetService;
import com.kairos.service.cta.CostTimeAgreementService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.open_shift.OpenShiftRuleTemplateService;
import com.kairos.service.open_shift.OrderService;
import com.kairos.service.period.PeriodSettingsService;
import com.kairos.service.phase.PhaseService;
import com.kairos.service.priority_group.PriorityGroupService;
import com.kairos.service.scheduler_service.ActivitySchedulerJobService;
import com.kairos.service.shift.ShiftService;
import com.kairos.service.unit_settings.*;
import com.kairos.service.wta.WorkTimeAgreementService;
import com.kairos.wrapper.activity.ActivityTabsWrapper;
import com.kairos.wrapper.activity.ActivityTagDTO;
import com.kairos.wrapper.activity.ActivityWithCompositeDTO;
import com.kairos.wrapper.activity.ActivityWithSelectedDTO;
import com.kairos.wrapper.shift.ActivityWithUnitIdDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.CommonsExceptionUtil.convertMessage;
import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.constants.ActivityMessagesConstants.*;

/**
 * Created by vipul on 5/12/17.
 */
@Service
@Transactional
public class OrganizationActivityService extends MongoBaseService {
    @Inject
    private ActivityMongoRepository activityMongoRepository;
    @Inject
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
    private ActivityPriorityService activityPriorityService;
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

    private static final Logger logger = LoggerFactory.getLogger(OrganizationActivityService.class);

    public ActivityDTO copyActivity(Long unitId, BigInteger activityId, boolean checked) {
        Activity activityCopied;
        if (checked) {
            Activity activity = activityMongoRepository.findOne(activityId);
            if (!Optional.ofNullable(activity).isPresent()) {
                exceptionService.dataNotFoundByIdException(MESSAGE_ACTIVITY_ID, activityId);
            }
            Activity isActivityAlreadyExist = activityMongoRepository.findByNameIgnoreCaseAndUnitIdAndByDate(activity.getName().trim(), unitId, activity.getGeneralActivityTab().getStartDate(), activity.getGeneralActivityTab().getEndDate());
            if (Optional.ofNullable(isActivityAlreadyExist).isPresent()) {
                exceptionService.dataNotFoundException(isActivityAlreadyExist.getGeneralActivityTab().getEndDate() == null ? MESSAGE_ACTIVITY_ENDDATE_REQUIRED : MESSAGE_ACTIVITY_ACTIVE_ALREADYEXISTS);
            }
            List<PhaseDTO> phaseDTOList = phaseService.getPhasesByUnit(unitId);
            Collections.reverse(phaseDTOList);

            Set<Long> parentAccessGroupIds = activity.getPhaseSettingsActivityTab().getPhaseTemplateValues().stream().flatMap(a -> a.getActivityShiftStatusSettings().stream().flatMap(b -> b.getAccessGroupIds().stream())).collect(Collectors.toSet());
            Map<Long, Long> accessGroupIdsMap = userIntegrationService.getAccessGroupForUnit(unitId, parentAccessGroupIds);
            List<PhaseTemplateValue> phaseTemplateValues1 = activity.getPhaseSettingsActivityTab().getPhaseTemplateValues();
            List<PhaseTemplateValue> phaseTemplateValues = new ArrayList<>();
            for (int i = 0; i < phaseDTOList.size(); i++) {
                List<ActivityShiftStatusSettings> existingActivityShiftStatusSettings = activity.getPhaseSettingsActivityTab().getPhaseTemplateValues().get(i).getActivityShiftStatusSettings();
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
            activity.getPhaseSettingsActivityTab().setPhaseTemplateValues(phaseTemplateValues);
            activityCopied = copyAllActivitySettingsInUnit(activity, unitId);
        } else {
            if (!userIntegrationService.isUnit(unitId)) {
                List<Long> childUnitIds = userIntegrationService.getAllOrganizationIds(unitId);
                if (activityMongoRepository.existsByParentIdAndDeletedFalse(activityId, childUnitIds)) {
                    exceptionService.actionNotPermittedException(ACTIVITY_USED_AT_UNIT);
                }
            }
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
        }
        activityCopied.setState(ActivityStateEnum.PUBLISHED);
        activityMongoRepository.save(activityCopied);
        return retrieveBasicDetails(activityCopied);
    }

    private ActivityDTO retrieveBasicDetails(Activity activity) {
        ActivityDTO activityDTO = new ActivityDTO(activity.getId(), activity.getName(), activity.getParentId());
        activityDTO.setBalanceSettingsActivityTab(ObjectMapperUtils.copyPropertiesOrCloneByMapper(activity.getBalanceSettingsActivityTab(), BalanceSettingActivityTabDTO.class));
        BeanUtils.copyProperties(activity, activityDTO);
        /*Optional<TimeType> timeType=timeTypeMongoRepository.findById(activity.getBalanceSettingsActivityTab().getTimeTypeId());
        if(timeType.isPresent()){
            activityDTO.setActivityCanBeCopied(timeType.get().isActivityCanBeCopied());
        }*/
        activityDTO.setActivityPriorityId(activity.getActivityPriorityId());
        return activityDTO;

    }

    public ActivityWithSelectedDTO getActivityMappingDetails(Long unitId) {
        ActivityWithSelectedDTO activityDetails = new ActivityWithSelectedDTO();
        ActivityWithUnitIdDTO activities = activityService.getActivityByUnitId(unitId);
        if (Optional.ofNullable(activities).isPresent() && Optional.ofNullable(activities.getActivityDTOList()).isPresent()) {
            activityDetails.setAllActivities(activities.getActivityDTOList());
        }
        List<ActivityTagDTO> activityTagDTOS = activityMongoRepository.findAllActivityByUnitIdAndDeleted(unitId, false);
        activityDetails.setSelectedActivities(activityTagDTOS);
        return activityDetails;
    }


    public Map<String, Object> getAllActivityByUnit(Long unitId, boolean includeTeamActivity) {
        Map<String, Object> response = new HashMap<>();
        OrganizationDTO organizationDTO = userIntegrationService.getOrganizationWithCountryId(unitId);
        List<ActivityTagDTO> activities = includeTeamActivity ? activityMongoRepository.findAllActivityByUnitIdAndDeleted(unitId, false) : activityMongoRepository.findAllActivityByUnitIdAndNotPartOfTeam(unitId);
        for (ActivityTagDTO activityTagDTO : activities) {
            boolean activityCanBeCopied = false;
            Set<OrganizationHierarchy> hierarchies = activityTagDTO.getActivityCanBeCopiedForOrganizationHierarchy();
            if ((isCollectionNotEmpty(hierarchies)) && ((organizationDTO.isParentOrganization() && hierarchies.contains(OrganizationHierarchy.ORGANIZATION)) ||
                    (!organizationDTO.isParentOrganization() && hierarchies.contains(OrganizationHierarchy.UNIT)))) {
                activityCanBeCopied = true;
            }
            activityTagDTO.setActivityCanBeCopied(activityCanBeCopied);
        }
        List<ActivityCategory> activityCategories = activityCategoryRepository.findByCountryId(organizationDTO.getCountryId());
        response.put("activities", activities);
        response.put("activityCategories", activityCategories);
        return response;
    }

    public Map<String, TranslationInfo> updateUnitActivityTranslationDetails(BigInteger activityId, Long unitId, Map<String, TranslationInfo> activityTranslationMap){
        Activity activity = activityMongoRepository.findByIdAndUnitIdAndDeleted(activityId,unitId,false);
        if(isNull(activity)) {
            exceptionService.dataNotFoundException(MESSAGE_DATA_NOTFOUND);
        }
        return activityService.updateActivityTranslations(activity,activityTranslationMap);
    }


    public ActivityTabsWrapper getGeneralTabOfActivity(BigInteger activityId, Long unitId) {
        Activity activity = activityMongoRepository.findOne(activityId);
        if (!Optional.ofNullable(activity).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_ACTIVITY_ID, activityId);
        }
        OrganizationDTO organizationDTO = userIntegrationService.getOrganizationWithCountryId(unitId);
        List<ActivityCategory> activityCategories = activityCategoryRepository.findByCountryId(organizationDTO.getCountryId());
        GeneralActivityTab generalTab = activity.getGeneralActivityTab();
        logger.info("activity.getTags() ================ > " + activity.getTags());
        //generalTab.setTags(tagMongoRepository.getTagsById(activity.getTags()));
        logger.info("activityId " + activityId);
        generalTab.setTags(null);
        GeneralActivityTabWithTagDTO generalActivityTabWithTagDTO = ObjectMapperUtils.copyPropertiesOrCloneByMapper(generalTab, GeneralActivityTabWithTagDTO.class);
        if (isCollectionNotEmpty(activity.getTags())) {
            List<TagDTO> tags = new ArrayList<>();
            tags.addAll(tagMongoRepository.getTagsById(activity.getTags()));
            tags.addAll(organizationDTO.getTagDTOS().stream().filter(tagDTO -> activity.getTags().contains(new BigInteger(tagDTO.getId().toString()))).collect(Collectors.toList()));
            generalActivityTabWithTagDTO.setTags(tags);
        }
        List<PresenceTypeDTO> presenceTypeDTOS = plannedTimeTypeService.getAllPresenceTypeByCountry(organizationDTO.getCountryId());
        PresenceTypeWithTimeTypeDTO presenceType = new PresenceTypeWithTimeTypeDTO(presenceTypeDTOS, organizationDTO.getCountryId());
        BalanceSettingsActivityTab balanceSettingsActivityTab = activity.getBalanceSettingsActivityTab();
        generalActivityTabWithTagDTO.setAddTimeTo(balanceSettingsActivityTab.getAddTimeTo());
        generalActivityTabWithTagDTO.setTimeTypeId(balanceSettingsActivityTab.getTimeTypeId());
        generalActivityTabWithTagDTO.setOnCallTimePresent(balanceSettingsActivityTab.isOnCallTimePresent());
        generalActivityTabWithTagDTO.setNegativeDayBalancePresent(balanceSettingsActivityTab.getNegativeDayBalancePresent());
        generalActivityTabWithTagDTO.setTimeType(balanceSettingsActivityTab.getTimeType());
        generalActivityTabWithTagDTO.setContent(activity.getNotesActivityTab().getContent());
        generalActivityTabWithTagDTO.setOriginalDocumentName(activity.getNotesActivityTab().getOriginalDocumentName());
        generalActivityTabWithTagDTO.setModifiedDocumentName(activity.getNotesActivityTab().getModifiedDocumentName());
        generalActivityTabWithTagDTO.setTranslations(activity.getTranslations());
        ActivityTabsWrapper activityTabsWrapper = new ActivityTabsWrapper(generalActivityTabWithTagDTO, activityId, activityCategories);
        activityTabsWrapper.setTimeTypes(timeTypeService.getAllTimeType(balanceSettingsActivityTab.getTimeTypeId(), presenceType.getCountryId()));
        activityTabsWrapper.setPresenceTypeWithTimeType(presenceType);
        return activityTabsWrapper;
    }

    //TODO Need to make sure that its fine to not copy expertise/skills/employmentTypes
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
        TimeType timeType = timeTypeMongoRepository.findOneById(activity.getBalanceSettingsActivityTab().getTimeTypeId());
        if (isNotNull(timeType.getActivityPriorityId())) {
            ActivityPriority activityPriority = activityPriorityService.getActivityPriorityById(timeType.getActivityPriorityId());
            ActivityPriority unitActivityPriority = activityPriorityService.getActivityPriorityNameAndOrganizationId(activityPriority.getName(), unitId);
            if (isNotNull(unitActivityPriority)) {
                activityCopied.setActivityPriorityId(unitActivityPriority.getId());
            }
        }
        updateSkills(activityCopied);
        // activityCopied.setCompositeActivities(null);
        return activityCopied;
    }

    public ActivityTabsWrapper updateGeneralTab(GeneralActivityTabDTO generalDTO, Long unitId) {
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
        if (Optional.ofNullable(isActivityAlreadyExist).isPresent() && generalDTO.getStartDate().isBefore(isActivityAlreadyExist.getGeneralActivityTab().getStartDate())) {
            exceptionService.actionNotPermittedException(MESSAGE_ACTIVITY_OVERLAPING);
        }
        if (Optional.ofNullable(isActivityAlreadyExist).isPresent()) {
            exceptionService.dataNotFoundException(isActivityAlreadyExist.getGeneralActivityTab().getEndDate() == null ? MESSAGE_ACTIVITY_ENDDATE_REQUIRED : MESSAGE_ACTIVITY_ACTIVE_ALREADYEXISTS);
        }
        ActivityCategory activityCategory = activityCategoryRepository.getByIdAndNonDeleted(generalDTO.getCategoryId());
        if (activityCategory == null) {
            exceptionService.dataNotFoundByIdException(MESSAGE_CATEGORY_NOTEXIST);
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
        activity.setDescription(generalTab.getDescription());
        activity.setTags(generalDTO.getTags());

        // generalTab.setTags(tagMongoRepository.getTagsById(generalDTO.getTags()));
        OrganizationDTO organizationDTO = userIntegrationService.getOrganizationWithCountryId(unitId);
        generalTab.setTags(null);
        List<ActivityCategory> activityCategories = activityCategoryRepository.findByCountryId(organizationDTO.getCountryId());
        GeneralActivityTabWithTagDTO generalActivityTabWithTagDTO = ObjectMapperUtils.copyPropertiesOrCloneByMapper(generalTab, GeneralActivityTabWithTagDTO.class);
        if (!activity.getTags().isEmpty()) {
            List<TagDTO> tags = new ArrayList<>();
            tags.addAll(tagMongoRepository.getTagsById(activity.getTags()));
            tags.addAll(organizationDTO.getTagDTOS().stream().filter(tagDTO -> activity.getTags().contains(new BigInteger(tagDTO.getId().toString()))).collect(Collectors.toList()));
            generalActivityTabWithTagDTO.setTags(tags);
            generalTab.setTags(activity.getTags());
        }
        activityService.updateBalanceSettingTab(generalDTO, activity);
        activityService.updateNotesTabOfActivity(generalDTO, activity);
        activityMongoRepository.save(activity);
        generalActivityTabWithTagDTO.setAddTimeTo(activity.getBalanceSettingsActivityTab().getAddTimeTo());
        generalActivityTabWithTagDTO.setTimeTypeId(activity.getBalanceSettingsActivityTab().getTimeTypeId());
        generalActivityTabWithTagDTO.setOnCallTimePresent(activity.getBalanceSettingsActivityTab().isOnCallTimePresent());
        generalActivityTabWithTagDTO.setNegativeDayBalancePresent(activity.getBalanceSettingsActivityTab().getNegativeDayBalancePresent());
        generalActivityTabWithTagDTO.setTimeType(activity.getBalanceSettingsActivityTab().getTimeType());
        generalActivityTabWithTagDTO.setContent(activity.getNotesActivityTab().getContent());
        generalActivityTabWithTagDTO.setOriginalDocumentName(activity.getNotesActivityTab().getOriginalDocumentName());
        generalActivityTabWithTagDTO.setModifiedDocumentName(activity.getNotesActivityTab().getModifiedDocumentName());
        generalActivityTabWithTagDTO.setBackgroundColor(activity.getGeneralActivityTab().getBackgroundColor());
        return new ActivityTabsWrapper(generalActivityTabWithTagDTO, generalDTO.getActivityId(), activityCategories);

    }

   /* public ActivityTabsWrapper getBalanceSettingsTabOfType(BigInteger activityId, Long unitId) {
        Long countryId = userIntegrationService.getCountryIdOfOrganization(unitId);
        List<PresenceTypeDTO> presenceTypeDTOS = plannedTimeTypeService.getAllPresenceTypeByCountry(countryId);
        PresenceTypeWithTimeTypeDTO presenceType = new PresenceTypeWithTimeTypeDTO(presenceTypeDTOS, countryId);
        Activity activity = activityMongoRepository.findOne(activityId);
        BalanceSettingsActivityTab balanceSettingsActivityTab = activity.getBalanceSettingsActivityTab();
        ActivityTabsWrapper activityTabsWrapper = new ActivityTabsWrapper(balanceSettingsActivityTab, presenceType);
        activityTabsWrapper.setTimeTypes(timeTypeService.getAllTimeType(balanceSettingsActivityTab.getTimeTypeId(), presenceType.getCountryId()));
        return activityTabsWrapper;
    }*/

    public ActivityTabsWrapper getTimeCalculationTabOfActivity(BigInteger activityId, Long unitId) {
        List<DayType> dayTypes = userIntegrationService.getDayTypes(unitId);
        Activity activity = activityMongoRepository.findOne(activityId);
        TimeCalculationActivityTab timeCalculationActivityTab = activity.getTimeCalculationActivityTab();
        List<Long> rulesTabDayTypes = activity.getRulesActivityTab().getDayTypes();
        return new ActivityTabsWrapper(timeCalculationActivityTab, dayTypes, rulesTabDayTypes);
    }

    public ActivityTabsWrapper getRulesTabOfActivity(BigInteger activityId, Long unitId) {
        DayTypeEmploymentTypeWrapper dayTypeEmploymentTypeWrapper = userIntegrationService.getDayTypesAndEmploymentTypesAtUnit(unitId);
        List<DayType> dayTypes = ObjectMapperUtils.copyPropertiesOrCloneCollectionByMapper(dayTypeEmploymentTypeWrapper.getDayTypes(), DayType.class);
        Activity activity = activityMongoRepository.findOne(activityId);
        RulesActivityTab rulesActivityTab = activity.getRulesActivityTab();
        TimeType timeType = timeTypeMongoRepository.findOneById(activity.getBalanceSettingsActivityTab().getTimeTypeId());
        if(isNotNull(timeType)){
            rulesActivityTab.setSicknessSettingValid(timeType.isSicknessSettingValid());
        }
        return new ActivityTabsWrapper(rulesActivityTab, dayTypes, dayTypeEmploymentTypeWrapper.getEmploymentTypes());
    }

    public ActivityTabsWrapper getPhaseSettingTabOfActivity(BigInteger activityId, Long unitId) {
        Set<AccessGroupRole> roles = AccessGroupRole.getAllRoles();
        DayTypeEmploymentTypeWrapper dayTypeEmploymentTypeWrapper = userIntegrationService.getDayTypesAndEmploymentTypesAtUnit(unitId);
        List<DayType> dayTypes = dayTypeEmploymentTypeWrapper.getDayTypes();
        List<EmploymentTypeDTO> employmentTypeDTOS = dayTypeEmploymentTypeWrapper.getEmploymentTypes();
        Activity activity = activityMongoRepository.findOne(activityId);
        PhaseSettingsActivityTab phaseSettingsActivityTab = activity.getPhaseSettingsActivityTab();
        return new ActivityTabsWrapper(roles, phaseSettingsActivityTab, dayTypes, employmentTypeDTOS);
    }

    public ActivityDTO copyActivityDetails(Long unitId, BigInteger activityId, ActivityDTO activityDTO) {
        //Need to know why we are returning object here as we can also return a simple boolean to check whether activity exist or not
        Activity activity = activityMongoRepository.
                findByNameIgnoreCaseAndUnitIdAndByDate(activityDTO.getName().trim(), unitId, activityDTO.getStartDate(), activityDTO.getEndDate());
        if (Optional.ofNullable(activity).isPresent() && activityDTO.getStartDate().isBefore(activity.getGeneralActivityTab().getStartDate())) {
            exceptionService.actionNotPermittedException(MESSAGE_ACTIVITY_OVERLAPING);
        }
        if (Optional.ofNullable(activity).isPresent()) {
            exceptionService.dataNotFoundException(activity.getGeneralActivityTab().getEndDate() == null ? MESSAGE_ACTIVITY_ENDDATE_REQUIRED : MESSAGE_ACTIVITY_ACTIVE_ALREADYEXISTS);
        }
        Optional<Activity> activityFromDatabase = activityMongoRepository.findById(activityId);
        if (!activityFromDatabase.isPresent() || activityFromDatabase.get().isDeleted() || !unitId.equals(activityFromDatabase.get().getUnitId())) {
            exceptionService.dataNotFoundByIdException(MESSAGE_ACTIVITY_ID, activityId);
        }
        //Checking the time type of activity whether it's eligible for copy or not
        TimeType timeType = timeTypeMongoRepository.findOneById(activityFromDatabase.get().getBalanceSettingsActivityTab().getTimeTypeId());
        OrganizationDTO organizationDTO = userIntegrationService.getOrganizationWithCountryId(unitId);
        Set<OrganizationHierarchy> hierarchies = timeType.getActivityCanBeCopiedForOrganizationHierarchy();
        if ((isCollectionNotEmpty(hierarchies)) && ((organizationDTO.isParentOrganization() && hierarchies.contains(OrganizationHierarchy.ORGANIZATION)) ||
                (!organizationDTO.isParentOrganization() && hierarchies.contains(OrganizationHierarchy.UNIT)))) {
            Activity activityCopied = copyAllActivitySettingsInUnit(activityFromDatabase.get(), unitId);
            activityCopied.setName(activityDTO.getName().trim());
            activityCopied.getGeneralActivityTab().setName(activityDTO.getName().trim());
            activityCopied.getGeneralActivityTab().setStartDate(activityDTO.getStartDate());
            activityCopied.getGeneralActivityTab().setEndDate(activityDTO.getEndDate());
            activityCopied.setState(ActivityStateEnum.DRAFT);
            activityMongoRepository.save(activityCopied);
            activityDTO.setId(activityCopied.getId());
            activityDTO.setActivityCanBeCopied(true);
            activityDTO.setUnitId(unitId);
        } else {
            exceptionService.actionNotPermittedException(ACTIVITY_NOT_ELIGIBLE_FOR_COPY);
        }
        return activityDTO;
    }

    public OrderAndActivityDTO getActivitiesWithBalanceSettings(long unitId) {
        OrderAndActivityDTO orderAndActivityDTO = new OrderAndActivityDTO();
        orderAndActivityDTO.setActivities(activityMongoRepository.findAllActivitiesWithBalanceSettings(unitId));
        orderAndActivityDTO.setOrders(orderService.getOrdersByUnitId(unitId));
        UnitSettingDTO unitSettingDTO = unitSettingRepository.getMinOpenShiftHours(unitId);
        orderAndActivityDTO.setMinOpenShiftHours(unitSettingDTO != null ? unitSettingDTO.getOpenShiftPhaseSetting().getMinOpenShiftHours() : null);
        orderAndActivityDTO.setCounters(counterRepository.getAllCounterBySupportedModule(ModuleType.OPEN_SHIFT));
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
        logger.info("I am going to create default data or organization {}" , unitId);
        //unitDataService.addParentOrganizationAndCountryIdForUnit(unitId, parentOrganizationId, countryId);

        List<Phase> phases = phaseService.createDefaultPhase(unitId, orgTypeAndSubTypeDTO.getCountryId());
        phaseSettingsService.createDefaultPhaseSettings(unitId, phases);
        unitSettingService.createDefaultOpenShiftPhaseSettings(unitId, phases);
        activityConfigurationService.createDefaultSettings(unitId, orgTypeAndSubTypeDTO.getCountryId(), phases, orgTypeAndSubTypeDTO.getEmploymentTypeIds());
        createActivityforOrganisation(unitId, orgTypeAndSubTypeDTO, phases);
        TAndAGracePeriodSettingDTO tAndAGracePeriodSettingDTO = new TAndAGracePeriodSettingDTO(AppConstants.STAFF_GRACE_PERIOD_DAYS, AppConstants.MANAGEMENT_GRACE_PERIOD_DAYS);
        timeAttendanceGracePeriodService.updateTAndAGracePeriodSetting(unitId, tAndAGracePeriodSettingDTO);
        activityPriorityService.createActivityPriorityForNewOrganization(unitId, orgTypeAndSubTypeDTO.getCountryId());
        periodSettingsService.createDefaultPeriodSettings(unitId);
        priorityGroupService.copyPriorityGroupsForUnit(unitId, orgTypeAndSubTypeDTO.getCountryId());
        openShiftRuleTemplateService.copyOpenShiftRuleTemplateInUnit(unitId, orgTypeAndSubTypeDTO);
        kpiSetService.copyKPISets(unitId, orgTypeAndSubTypeDTO.getSubTypeId(), orgTypeAndSubTypeDTO.getCountryId());
        protectedDaysOffService.saveProtectedDaysOff(unitId, ProtectedDaysOffUnitSettings.ONCE_IN_A_YEAR);
        counterDistService.createDefaultCategory(unitId);
        return true;
    }

    private void createActivityforOrganisation(Long unitId, OrgTypeAndSubTypeDTO orgTypeAndSubTypeDTO, List<Phase> phases) {
        List<Activity> existingActivities;
        if (orgTypeAndSubTypeDTO.getParentOrganizationId() == null) {
            existingActivities = activityMongoRepository.findAllActivitiesByOrganizationTypeOrSubTypeOrBreakTypes(orgTypeAndSubTypeDTO.getOrganizationTypeId(), orgTypeAndSubTypeDTO.getSubTypeId());
        } else {
            existingActivities = activityMongoRepository.findAllByUnitIdAndDeletedFalse(orgTypeAndSubTypeDTO.getParentOrganizationId());
        }

        if (!existingActivities.isEmpty()) {
            Set<Long> parentAccessGroupIds = existingActivities.stream().flatMap(a -> a.getPhaseSettingsActivityTab().getPhaseTemplateValues().stream().flatMap(b -> b.getActivityShiftStatusSettings().stream().flatMap(c -> c.getAccessGroupIds().stream()))).collect(Collectors.toSet());
            Map<Long, Long> accessGroupIdsMap = userIntegrationService.getAccessGroupForUnit(unitId, parentAccessGroupIds);
            List<Activity> activityCopiedList = new ArrayList<>(existingActivities.size());
            for (Activity activity : existingActivities) {
                logger.info("I am act {}", activity.getName());
                List<PhaseTemplateValue> phaseTemplateValues = new ArrayList<>();
                for (int i = 0; i < phases.size(); i++) {
                    List<ActivityShiftStatusSettings> existingActivityShiftStatusSettings = activity.getPhaseSettingsActivityTab().getPhaseTemplateValues().get(i).getActivityShiftStatusSettings();
                    List<ActivityShiftStatusSettings> activityShiftStatusSettings = new ArrayList<>();
                    Set<Long> agIds = new HashSet<>();
                    PhaseTemplateValue phaseTemplateValue = new PhaseTemplateValue(phases.get(i).getId(), phases.get(i).getName(), phases.get(i).getDescription(), activity.getPhaseSettingsActivityTab().getPhaseTemplateValues().get(i).getEligibleEmploymentTypes(),
                            activity.getPhaseSettingsActivityTab().getPhaseTemplateValues().get(i).isEligibleForManagement(), activity.getPhaseSettingsActivityTab().getPhaseTemplateValues().get(i).isStaffCanDelete(), activity.getPhaseSettingsActivityTab().getPhaseTemplateValues().get(i).isManagementCanDelete(),
                            activity.getPhaseSettingsActivityTab().getPhaseTemplateValues().get(i).isStaffCanSell(), activity.getPhaseSettingsActivityTab().getPhaseTemplateValues().get(i).isManagementCanSell(), activity.getPhaseSettingsActivityTab().getPhaseTemplateValues().get(i).getAllowedSettings());
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
                activity.getPhaseSettingsActivityTab().setPhaseTemplateValues(phaseTemplateValues);
                activityCopiedList.add(copyAllActivitySettingsInUnit(activity, unitId));
            }
            save(activityCopiedList);
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
        TimeType timeType = timeTypeMongoRepository.findOneById(parentActivity.getBalanceSettingsActivityTab().getTimeTypeId());
        if (!timeType.isAllowChildActivities()) {
            exceptionService.actionNotPermittedException(MESSAGE_ACTIVITY_SETTING_ENABLE, parentActivity.getName());

        }
        if (activityMongoRepository.existsByActivityIdInChildActivities(parentActivity.getId())) {
            exceptionService.actionNotPermittedException(MESSAGE_ACTIVITY_BEING_USED_AS_CHILD, parentActivity.getName());
        }

        List<Activity> activityList = activityMongoRepository.findByActivityIdInChildActivities(parentActivity.getId(), activities.stream().map(k -> k.getId()).collect(Collectors.toList()));
        if (isCollectionNotEmpty(activityList)) {
            List<String> activityNames = activityList.stream().map(Activity::getName).collect(Collectors.toList());
            exceptionService.actionNotPermittedException(MESSAGE_ACTIVITY_BEING_USED_AS_CHILD, activityNames);
        }
        activities = activities.stream().filter(k -> isCollectionNotEmpty(k.getChildActivityIds())).collect(Collectors.toList());
        if (isCollectionNotEmpty(activities)) {
            List<String> activityNames = activities.stream().map(ActivityDTO::getName).collect(Collectors.toList());
            exceptionService.actionNotPermittedException(MESSAGE_ACTIVITY_BEING_USED_AS_PARENT, activityNames);
        }

    }

    public List<ActivityWithCompositeDTO> getTeamActivitiesOfStaff(Long unitId, Long staffId, List<ActivityWithCompositeDTO> staffPersonalizedActivities) {
        Set<BigInteger> activityList = userIntegrationService.getTeamActivitiesOfStaff(unitId, staffId);
        activityList.addAll(staffPersonalizedActivities.stream().map(ActivityWithCompositeDTO::getActivityId).collect(Collectors.toSet()));
        return activityMongoRepository.findAllActivityByUnitIdWithCompositeActivities(new ArrayList<>(activityList));
    }

    private void updateSkills(Activity activityCopied){
        ActivityDTO activityDTO=userIntegrationService.getAllSkillsByUnit(activityCopied.getUnitId());
        List<ActivitySkill> activitySkills=activityCopied.getSkillActivityTab().getActivitySkills().stream().filter(k->activityDTO.getSkills().contains(k.getSkillId())).collect(Collectors.toList());
        List<Long> expertiseIds=activityCopied.getExpertises().stream().filter(k->activityDTO.getExpertises().contains(k)).collect(Collectors.toList());
        activityCopied.getSkillActivityTab().setActivitySkills(activitySkills);
        activityCopied.setExpertises(expertiseIds);
    }
}

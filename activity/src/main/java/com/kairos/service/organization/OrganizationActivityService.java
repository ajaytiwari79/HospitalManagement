package com.kairos.service.organization;

import com.kairos.activity.activity.ActivityDTO;
import com.kairos.activity.activity.ActivityWithTimeTypeDTO;
import com.kairos.activity.activity.activity_tabs.GeneralActivityTabDTO;
import com.kairos.activity.activity.activity_tabs.PermissionsActivityTabDTO;
import com.kairos.activity.counter.CounterDTO;
import com.kairos.activity.enums.counter.ModuleType;
import com.kairos.activity.open_shift.OpenShiftIntervalDTO;
import com.kairos.activity.phase.PhaseDTO;
import com.kairos.activity.presence_type.PresenceTypeDTO;
import com.kairos.activity.presence_type.PresenceTypeWithTimeTypeDTO;
import com.kairos.activity.shift.ShiftDTO;
import com.kairos.activity.time_type.TimeTypeDTO;
import com.kairos.activity.unit_settings.TAndAGracePeriodSettingDTO;
import com.kairos.activity.unit_settings.UnitSettingDTO;
import com.kairos.constants.AppConstants;
import com.kairos.enums.ActivityStateEnum;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.activity.tabs.*;
import com.kairos.persistence.model.activity.tabs.rules_activity_tab.RulesActivityTab;
import com.kairos.persistence.model.open_shift.OrderAndActivityDTO;
import com.kairos.persistence.model.phase.Phase;
import com.kairos.persistence.model.staff_settings.StaffActivitySetting;
import com.kairos.persistence.repository.activity.ActivityCategoryRepository;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.counter.CounterRepository;
import com.kairos.persistence.repository.open_shift.OpenShiftIntervalRepository;
import com.kairos.persistence.repository.staff_settings.StaffActivitySettingRepository;
import com.kairos.persistence.repository.tag.TagMongoRepository;
import com.kairos.persistence.repository.unit_settings.UnitSettingRepository;
import com.kairos.rest_client.GenericIntegrationService;
import com.kairos.rest_client.OrganizationRestClient;
import com.kairos.service.MongoBaseService;
import com.kairos.service.activity.ActivityService;
import com.kairos.service.activity.PlannedTimeTypeService;
import com.kairos.service.activity.TimeTypeService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.integration.PlannerSyncService;
import com.kairos.service.open_shift.OrderService;
import com.kairos.service.period.PeriodSettingsService;
import com.kairos.service.phase.PhaseService;
import com.kairos.service.priority_group.PriorityGroupService;
import com.kairos.service.staff_settings.StaffActivitySettingService;
import com.kairos.service.unit_settings.ActivityConfigurationService;
import com.kairos.service.unit_settings.PhaseSettingsService;
import com.kairos.service.unit_settings.TimeAttendanceGracePeriodService;
import com.kairos.service.unit_settings.UnitSettingService;
import com.kairos.service.user_service_data.UnitDataService;
import com.kairos.user.country.day_type.DayType;
import com.kairos.user.country.day_type.DayTypeEmploymentTypeWrapper;
import com.kairos.user.organization.OrgTypeAndSubTypeDTO;
import com.kairos.util.ObjectMapperUtils;
import com.kairos.wrapper.activity.ActivityTabsWrapper;
import com.kairos.wrapper.activity.ActivityTagDTO;
import com.kairos.wrapper.activity.ActivityWithSelectedDTO;
import com.kairos.wrapper.shift.ActivityWithUnitIdDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalTime;
import java.util.*;

import static javax.management.timer.Timer.ONE_MINUTE;

/**
 * Created by vipul on 5/12/17.
 */
@Service
@Transactional
public class OrganizationActivityService extends MongoBaseService {
    private final Logger logger = LoggerFactory.getLogger(OrganizationActivityService.class);

    @Inject
    private ActivityMongoRepository activityMongoRepository;
    @Inject
    private OrganizationRestClient organizationRestClient;
    @Inject
    private ActivityService activityService;
    @Inject
    private TagMongoRepository tagMongoRepository;
    @Inject
    private TimeTypeService timeTypeService;
    @Inject
    private ActivityCategoryRepository activityCategoryRepository;
    @Inject
    private PlannerSyncService plannerSyncService;
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
    private GenericIntegrationService genericIntegrationService;

    @Inject
    private ActivityConfigurationService activityConfigurationService;
    @Inject
    private UnitDataService unitDataService;
    @Inject
    private TimeAttendanceGracePeriodService timeAttendanceGracePeriodService;
    @Inject
    private PriorityGroupService priorityGroupService;
    @Inject
    private CounterRepository counterRepository;
    @Inject
    private StaffActivitySettingRepository staffActivitySettingRepository;


    public ActivityDTO copyActivity(Long unitId, BigInteger activityId, boolean checked) {
        Activity activityCopied;
        if (checked) {
            Activity activity = activityMongoRepository.findOne(activityId);
            if (!Optional.ofNullable(activity).isPresent()) {
                exceptionService.dataNotFoundByIdException("message.activity.id", activityId);
            }
            Activity isActivityAlreadyExist = activityMongoRepository.findByNameIgnoreCaseAndUnitIdAndByDate(activity.getName().trim(), unitId, activity.getGeneralActivityTab().getStartDate(), activity.getGeneralActivityTab().getEndDate());
            if (Optional.ofNullable(isActivityAlreadyExist).isPresent()) {
                exceptionService.dataNotFoundException(isActivityAlreadyExist.getGeneralActivityTab().getEndDate() == null ? "message.activity.enddate.required" : "message.activity.active.alreadyExists");
            }
            List<PhaseDTO> phaseDTOList = phaseService.getPhasesByUnit(unitId);
            List<PhaseTemplateValue> phaseTemplateValues = new ArrayList<>();
            for (int i = 0; i < phaseDTOList.size(); i++) {
                PhaseTemplateValue phaseTemplateValue = new PhaseTemplateValue(phaseDTOList.get(i).getId(), phaseDTOList.get(i).getName(), phaseDTOList.get(i).getDescription(), activity.getRulesActivityTab().getEligibleForSchedules().get(i).getEligibleEmploymentTypes(),
                        activity.getRulesActivityTab().getEligibleForSchedules().get(i).isEligibleForManagement(), activity.getRulesActivityTab().getEligibleForSchedules().get(i).isStaffCanDelete(), activity.getRulesActivityTab().getEligibleForSchedules().get(i).isManagementCanDelete(),
                        activity.getRulesActivityTab().getEligibleForSchedules().get(i).isStaffCanSell(), activity.getRulesActivityTab().getEligibleForSchedules().get(i).isManagementCanSell());
                phaseTemplateValues.add(phaseTemplateValue);
            }
            activity.getRulesActivityTab().setEligibleForSchedules(phaseTemplateValues);
            activityCopied = copyAllActivitySettingsInUnit(activity, unitId);
            save(activityCopied);
        } else {
            activityCopied = activityMongoRepository.findByParentIdAndDeletedFalseAndUnitId(activityId, unitId);
            activityCopied.setDeleted(true);
            activityService.deleteActivityAndShiftStatusOfThisActivity(activityCopied.getId());


        }
        save(activityCopied);
        return retrieveBasicDetails(activityCopied);
    }

    public ActivityDTO retrieveBasicDetails(Activity activity) {
        ActivityDTO activityDTO = new ActivityDTO(activity.getId(), activity.getName(), activity.getParentId());
        BeanUtils.copyProperties(activity, activityDTO);
        return activityDTO;

    }

    public ActivityWithSelectedDTO getActivityMappingDetails(Long unitId, String type) {
        ActivityWithSelectedDTO activityDetails = new ActivityWithSelectedDTO();
        ActivityWithUnitIdDTO activities = activityService.getActivityByUnitId(unitId, type);
        if (Optional.ofNullable(activities).isPresent()) {
            if (Optional.ofNullable(activities.getActivityDTOList()).isPresent())
                activityDetails.setAllActivities(activities.getActivityDTOList());
        }
        List<ActivityTagDTO> act = activityMongoRepository.findAllActivityByUnitIdAndDeleted(unitId, false);
        activityDetails.setSelectedActivities(act);
        return activityDetails;
    }

    public Map<String, Object> getAllActivityByUnitAndDeleted(Long unitId) {
        Map<String, Object> response = new HashMap<>();
        Long countryId = organizationRestClient.getCountryIdOfOrganization(unitId);
        List<ActivityTagDTO> activities = activityMongoRepository.findAllActivityByUnitIdAndDeleted(unitId, false);
        List<ActivityCategory> activityCategories = activityCategoryRepository.findByCountryId(countryId);
        response.put("activities", activities);
        response.put("activityCategories", activityCategories);
        return response;
    }

    public ActivityTabsWrapper getGeneralTabOfActivity(BigInteger activityId, Long unitId) {
        Activity activity = activityMongoRepository.findOne(activityId);
        if (!Optional.ofNullable(activity).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.activity.id", activityId);
        }
        Long countryId = organizationRestClient.getCountryIdOfOrganization(unitId);
        List<ActivityCategory> activityCategories = activityCategoryRepository.findByCountryId(countryId);
        GeneralActivityTab generalTab = activity.getGeneralActivityTab();
        logger.info("activity.getTags() ================ > " + activity.getTags());
        //generalTab.setTags(tagMongoRepository.getTagsById(activity.getTags()));
        logger.info("activityId " + activityId);
        ActivityTabsWrapper activityTabsWrapper = new ActivityTabsWrapper(generalTab, activityId, activityCategories);
        return activityTabsWrapper;
    }

    //TODO Need to make sure that its fine to not copy expertise/skills/employmentTypes
    private Activity copyAllActivitySettingsInUnit(Activity activity, Long unitId) {
        Activity activityCopied = new Activity();
        Activity.copyProperties(activity, activityCopied, "id", "organizationTypes", "organizationSubTypes");
        activityCopied.setParentId(activity.getId());
        activityCopied.setParentActivity(false);
        activityCopied.setOrganizationTypes(null);
        activityCopied.setOrganizationSubTypes(null);
        activityCopied.setState(null);
        activityCopied.setLevels(null);
        activityCopied.setRegions(null);
        activityCopied.setUnitId(unitId);
        activityCopied.setCountryId(null);
        activityCopied.setCompositeActivities(null);
        return activityCopied;
    }

    public ActivityTabsWrapper updateGeneralTab(GeneralActivityTabDTO generalDTO, Long unitId) {
        if (generalDTO.getEndDate() != null && generalDTO.getEndDate().isBefore(generalDTO.getStartDate())) {
            exceptionService.actionNotPermittedException("message.activity.enddate.greaterthan.startdate");
        }
        Activity isActivityAlreadyExist = activityMongoRepository.findByNameExcludingCurrentInUnitAndDate(generalDTO.getName(), generalDTO.getActivityId(), unitId, generalDTO.getStartDate(), generalDTO.getEndDate());
        if (Optional.ofNullable(isActivityAlreadyExist).isPresent() && generalDTO.getStartDate().isBefore(isActivityAlreadyExist.getGeneralActivityTab().getStartDate())) {
            exceptionService.actionNotPermittedException("message.activity.overlaping");
        }
        if (Optional.ofNullable(isActivityAlreadyExist).isPresent()) {
            exceptionService.dataNotFoundException(isActivityAlreadyExist.getGeneralActivityTab().getEndDate() == null ? "message.activity.enddate.required" : "message.activity.active.alreadyExists");
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
        activity.getBalanceSettingsActivityTab().setTimeTypeId(activityCategory.getTimeTypeId());
        activity.setGeneralActivityTab(generalTab);
        activity.setName(generalTab.getName());
        activity.setDescription(generalTab.getDescription());
        activity.setTags(generalDTO.getTags());

        save(activity);
        // generalTab.setTags(tagMongoRepository.getTagsById(generalDTO.getTags()));
        Long countryId = organizationRestClient.getCountryIdOfOrganization(unitId);
        List<ActivityCategory> activityCategories = activityCategoryRepository.findByCountryId(countryId);
        ActivityTabsWrapper activityTabsWrapper = new ActivityTabsWrapper(generalTab, generalDTO.getActivityId(), activityCategories);
        return activityTabsWrapper;
    }

    public ActivityTabsWrapper getBalanceSettingsTabOfType(BigInteger activityId, Long unitId) {
        Long countryId = organizationRestClient.getCountryIdOfOrganization(unitId);
        List<PresenceTypeDTO> presenceTypeDTOS = plannedTimeTypeService.getAllPresenceTypeByCountry(countryId);
        PresenceTypeWithTimeTypeDTO presenceType = new PresenceTypeWithTimeTypeDTO(presenceTypeDTOS, countryId);
        Activity activity = activityMongoRepository.findOne(activityId);
        BalanceSettingsActivityTab balanceSettingsActivityTab = activity.getBalanceSettingsActivityTab();
        ActivityTabsWrapper activityTabsWrapper = new ActivityTabsWrapper(balanceSettingsActivityTab, presenceType);
        activityTabsWrapper.setTimeTypes(timeTypeService.getAllTimeType(balanceSettingsActivityTab.getTimeTypeId(), presenceType.getCountryId()));
        return activityTabsWrapper;
    }

    public ActivityTabsWrapper getTimeCalculationTabOfActivity(BigInteger activityId, Long unitId) {
        List<DayType> dayTypes = organizationRestClient.getDayTypes(unitId);
        Activity activity = activityMongoRepository.findOne(activityId);
        TimeCalculationActivityTab timeCalculationActivityTab = activity.getTimeCalculationActivityTab();
        List<Long> rulesTabDayTypes= activity.getRulesActivityTab().getDayTypes();
        ActivityTabsWrapper activityTabsWrapper = new ActivityTabsWrapper(timeCalculationActivityTab, dayTypes,rulesTabDayTypes);
        return activityTabsWrapper;
    }

    public ActivityTabsWrapper getRulesTabOfActivity(BigInteger activityId, Long unitId) {
        DayTypeEmploymentTypeWrapper dayTypeEmploymentTypeWrapper = genericIntegrationService.getDayTypesAndEmploymentTypesAtUnit(unitId);
        List<DayType> dayTypes = ObjectMapperUtils.copyPropertiesOfListByMapper(dayTypeEmploymentTypeWrapper.getDayTypes(), DayType.class);
        Activity activity = activityMongoRepository.findOne(activityId);
        RulesActivityTab rulesActivityTab = activity.getRulesActivityTab();
        ActivityTabsWrapper activityTabsWrapper = new ActivityTabsWrapper(rulesActivityTab, dayTypes, dayTypeEmploymentTypeWrapper.getEmploymentTypes());

        return activityTabsWrapper;
    }

    public ActivityDTO copyActivityDetails(Long unitId, BigInteger activityId, ActivityDTO activityDTO) {
        //Need to know why we are returning object here as we can also return a simple boolean to check whether activity exist or not
        Activity activity = activityMongoRepository.
                findByNameIgnoreCaseAndUnitIdAndByDate(activityDTO.getName().trim(), unitId, activityDTO.getStartDate(), activityDTO.getEndDate());
        if (Optional.ofNullable(activity).isPresent() && activityDTO.getStartDate().isBefore(activity.getGeneralActivityTab().getStartDate())) {
            exceptionService.actionNotPermittedException("message.activity.overlaping");
        }
        if (Optional.ofNullable(activity).isPresent()) {
            exceptionService.dataNotFoundException(activity.getGeneralActivityTab().getEndDate() == null ? "message.activity.enddate.required" : "message.activity.active.alreadyExists");
        }
        Optional<Activity> activityFromDatabase = activityMongoRepository.findById(activityId);
        if (!activityFromDatabase.isPresent() || activityFromDatabase.get().isDeleted() || !unitId.equals(activityFromDatabase.get().getUnitId())) {
            exceptionService.dataNotFoundByIdException("message.activity.id", activityId);
        }
        if (!activityFromDatabase.get().getPermissionsActivityTab().isEligibleForCopy()) {
            exceptionService.actionNotPermittedException("activity.not.eligible.for.copy");
        }
        Activity activityCopied = copyAllActivitySettingsInUnit(activityFromDatabase.get(), unitId);
        activityCopied.setName(activityDTO.getName().trim());
        activityCopied.getGeneralActivityTab().setName(activityDTO.getName().trim());
        activityCopied.getGeneralActivityTab().setStartDate(activityDTO.getStartDate());
        activityCopied.getGeneralActivityTab().setEndDate(activityDTO.getEndDate());
        activityCopied.setState(ActivityStateEnum.DRAFT);
        save(activityCopied);

        // copying activity and shift status settings of this activity
        activityService.copyActivityAndShiftStatusOfThisActivity(activityId, activityCopied.getId());
        activityDTO.setId(activityCopied.getId());
        PermissionsActivityTabDTO permissionsActivityTabDTO = new PermissionsActivityTabDTO();
        BeanUtils.copyProperties(activityCopied.getPermissionsActivityTab(), permissionsActivityTabDTO);
        activityDTO.setPermissionsActivityTab(permissionsActivityTabDTO);
        return activityDTO;
    }

    public OrderAndActivityDTO getActivitiesWithBalanceSettings(long unitId) {
        OrderAndActivityDTO orderAndActivityDTO = new OrderAndActivityDTO();
        orderAndActivityDTO.setActivities(activityMongoRepository.findAllActivitiesWithBalanceSettings(unitId));
        orderAndActivityDTO.setOrders(orderService.getOrdersByUnitId(unitId));
        orderAndActivityDTO.setMinOpenShiftHours(unitSettingRepository.getMinOpenShiftHours(unitId).getOpenShiftPhaseSetting().getMinOpenShiftHours());
        orderAndActivityDTO.setCounters(counterRepository.getAllCounterBySupportedModule(ModuleType.OPEN_SHIFT));
        return orderAndActivityDTO;
    }

    public ActivityWithTimeTypeDTO getActivitiesWithTimeTypesByUnit(Long unitId, Long countryId) {
        List<ActivityDTO> activityDTOS = activityMongoRepository.findAllActivitiesWithTimeTypesByUnit(unitId);
        List<TimeTypeDTO> timeTypeDTOS = timeTypeService.getAllTimeType(null, countryId);
        List<OpenShiftIntervalDTO> intervals = openShiftIntervalRepository.getAllByCountryIdAndDeletedFalse(countryId);
        UnitSettingDTO minOpenShiftHours = unitSettingRepository.getMinOpenShiftHours(unitId);
        List<CounterDTO> counters = counterRepository.getAllCounterBySupportedModule(ModuleType.OPEN_SHIFT);

        ActivityWithTimeTypeDTO activityWithTimeTypeDTO = new ActivityWithTimeTypeDTO(activityDTOS, timeTypeDTOS, intervals,
                minOpenShiftHours.getOpenShiftPhaseSetting().getMinOpenShiftHours(), counters);
        return activityWithTimeTypeDTO;
    }

    public boolean createDefaultDataForOrganization(Long unitId, OrgTypeAndSubTypeDTO orgTypeAndSubTypeDTO) {
        logger.info("I am going to create default data or organization " + unitId);
        //unitDataService.addParentOrganizationAndCountryIdForUnit(unitId, parentOrganizationId, countryId);
        List<Phase> phases = phaseService.createDefaultPhase(unitId, orgTypeAndSubTypeDTO.getCountryId());
        periodSettingsService.createDefaultPeriodSettings(unitId);
        phaseSettingsService.createDefaultPhaseSettings(unitId, phases);
        unitSettingService.createDefaultOpenShiftPhaseSettings(unitId, phases);
        activityConfigurationService.createDefaultSettings(unitId, orgTypeAndSubTypeDTO.getCountryId(), phases);
        TAndAGracePeriodSettingDTO tAndAGracePeriodSettingDTO = new TAndAGracePeriodSettingDTO(AppConstants.STAFF_GRACE_PERIOD_DAYS, AppConstants.MANAGEMENT_GRACE_PERIOD_DAYS);
        timeAttendanceGracePeriodService.updateTAndAGracePeriodSetting(unitId, tAndAGracePeriodSettingDTO);
        priorityGroupService.copyPriorityGroupsForUnit(unitId, orgTypeAndSubTypeDTO.getCountryId());
        List<Activity> existingActivities;
        if (orgTypeAndSubTypeDTO.getParentOrganizationId() == null) {
            existingActivities = activityMongoRepository.findAllActivitiesByOrganizationTypeOrSubType(orgTypeAndSubTypeDTO.getOrganizationTypeId(), orgTypeAndSubTypeDTO.getSubTypeId());
        } else {
            existingActivities= activityMongoRepository.findAllByUnitIdAndDeletedFalse(orgTypeAndSubTypeDTO.getParentOrganizationId());
        }
        if (!existingActivities.isEmpty()) {
            List<Activity> activityCopiedList = new ArrayList<>(existingActivities.size());
            for (Activity activity : existingActivities) {
                logger.info("I am act {}", activity.getName());
                List<PhaseTemplateValue> phaseTemplateValues = new ArrayList<>();
                for (int i = 0; i < phases.size(); i++) {
                    PhaseTemplateValue phaseTemplateValue = new PhaseTemplateValue(phases.get(i).getId(), phases.get(i).getName(), phases.get(i).getDescription(), activity.getRulesActivityTab().getEligibleForSchedules().get(i).getEligibleEmploymentTypes(),
                            activity.getRulesActivityTab().getEligibleForSchedules().get(i).isEligibleForManagement(), activity.getRulesActivityTab().getEligibleForSchedules().get(i).isStaffCanDelete(), activity.getRulesActivityTab().getEligibleForSchedules().get(i).isManagementCanDelete(),
                            activity.getRulesActivityTab().getEligibleForSchedules().get(i).isStaffCanSell(), activity.getRulesActivityTab().getEligibleForSchedules().get(i).isManagementCanSell());
                    phaseTemplateValues.add(phaseTemplateValue);
                }
                activity.getRulesActivityTab().setEligibleForSchedules(phaseTemplateValues);
                activityCopiedList.add(copyAllActivitySettingsInUnit(activity, unitId));
            }
            save(activityCopiedList);
        }
        return true;
    }

    /**
     * @param staffId
     * @param shiftDTO
     * @Auther Pavan
     */
    public void validateShiftTime(Long staffId, ShiftDTO shiftDTO,RulesActivityTab rulesActivityTab){
              LocalTime earliestStartTime;
              LocalTime latestStartTime;
              LocalTime maximumEndTime;
              Short longestTime;
              Short shortestTime;
              StaffActivitySetting staffActivitySetting=staffActivitySettingRepository.findByStaffIdAndActivityIdAndDeletedFalse(staffId,shiftDTO.getActivityId());
              if(staffActivitySetting!=null){
                  earliestStartTime=staffActivitySetting.getEarliestStartTime();
                  latestStartTime=staffActivitySetting.getLatestStartTime();
                  maximumEndTime=staffActivitySetting.getMaximumEndTime();
                  longestTime=staffActivitySetting.getLongestTime();
                  shortestTime=staffActivitySetting.getShortestTime();
              }
              else {
                  earliestStartTime=rulesActivityTab.getEarliestStartTime();
                  latestStartTime=rulesActivityTab.getLatestStartTime();
                  maximumEndTime=rulesActivityTab.getMaximumEndTime();
                  longestTime=rulesActivityTab.getLongestTime();
                  shortestTime=rulesActivityTab.getShortestTime();
              }

              if(earliestStartTime!=null && earliestStartTime.isAfter(shiftDTO.getStartTime())){
                  exceptionService.actionNotPermittedException("error.start_time.greater_than.earliest_time");
              }
              if(latestStartTime!=null && latestStartTime.isBefore(shiftDTO.getStartTime())){
                  exceptionService.actionNotPermittedException("error.start_time.less_than.latest_time");
              }
              if(maximumEndTime!=null && maximumEndTime.isBefore(shiftDTO.getEndTime())){
                  exceptionService.actionNotPermittedException("error.end_time.less_than.maximum_end_time");
              }
              if(longestTime!=null && longestTime< (shiftDTO.getEndDate().getTime() - shiftDTO.getStartDate().getTime()) / ONE_MINUTE){
                  exceptionService.actionNotPermittedException("error.shift.duration_exceeds_longest_time");
              }
              if(shortestTime!=null && shortestTime > (shiftDTO.getEndDate().getTime() - shiftDTO.getStartDate().getTime()) / ONE_MINUTE){
                  exceptionService.actionNotPermittedException("error.shift.duration.less_than.shortest_time");
              }
    }

}

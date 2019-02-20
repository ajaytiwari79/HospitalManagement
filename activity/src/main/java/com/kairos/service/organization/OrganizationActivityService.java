package com.kairos.service.organization;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.constants.AppConstants;
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
import com.kairos.dto.user.organization.OrgTypeAndSubTypeDTO;
import com.kairos.dto.user.organization.OrganizationDTO;
import com.kairos.enums.ActivityStateEnum;
import com.kairos.enums.OrganizationHierarchy;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.activity.tabs.*;
import com.kairos.persistence.model.activity.tabs.rules_activity_tab.RulesActivityTab;
import com.kairos.persistence.model.open_shift.OrderAndActivityDTO;
import com.kairos.persistence.model.phase.Phase;
import com.kairos.persistence.repository.activity.ActivityCategoryRepository;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.counter.CounterRepository;
import com.kairos.persistence.repository.open_shift.OpenShiftIntervalRepository;
import com.kairos.persistence.repository.tag.TagMongoRepository;
import com.kairos.persistence.repository.unit_settings.UnitSettingRepository;
import com.kairos.rest_client.GenericIntegrationService;
import com.kairos.service.MongoBaseService;
import com.kairos.service.activity.ActivityService;
import com.kairos.service.activity.ActivityUtil;
import com.kairos.service.activity.PlannedTimeTypeService;
import com.kairos.service.activity.TimeTypeService;
import com.kairos.service.cta.CostTimeAgreementService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.open_shift.OrderService;
import com.kairos.service.period.PeriodSettingsService;
import com.kairos.service.phase.PhaseService;
import com.kairos.service.priority_group.PriorityGroupService;
import com.kairos.service.unit_settings.ActivityConfigurationService;
import com.kairos.service.unit_settings.PhaseSettingsService;
import com.kairos.service.unit_settings.TimeAttendanceGracePeriodService;
import com.kairos.service.unit_settings.UnitSettingService;
import com.kairos.service.wta.WTAService;
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
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;

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
    private GenericIntegrationService genericIntegrationService;
    @Inject
    private ActivityConfigurationService activityConfigurationService;
    @Inject
    private TimeAttendanceGracePeriodService timeAttendanceGracePeriodService;
    @Inject
    private PriorityGroupService priorityGroupService;
    @Inject
    private CounterRepository counterRepository;
    @Inject
    private WTAService wtaService;
    @Inject
    private CostTimeAgreementService costTimeAgreementService;

    private static final Logger logger = LoggerFactory.getLogger(OrganizationActivityService.class);

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
            Set<Long> parentAccessGroupIds = activity.getPhaseSettingsActivityTab().getPhaseTemplateValues().stream().flatMap(a -> a.getActivityShiftStatusSettings().stream().flatMap(b -> b.getAccessGroupIds().stream())).collect(Collectors.toSet());
            Map<Long, Long> accessGroupIdsMap = genericIntegrationService.getAccessGroupForUnit(unitId, parentAccessGroupIds);
            List<PhaseTemplateValue> phaseTemplateValues = new ArrayList<>();
            for (int i = 0; i < phaseDTOList.size(); i++) {
                List<ActivityShiftStatusSettings> existingActivityShiftStatusSettings = activity.getPhaseSettingsActivityTab().getPhaseTemplateValues().get(i).getActivityShiftStatusSettings();
                List<ActivityShiftStatusSettings> activityShiftStatusSettings = new ArrayList<>();
                Set<Long> agIds = new HashSet<>();
                PhaseTemplateValue phaseTemplateValue = new PhaseTemplateValue(phaseDTOList.get(i).getId(), phaseDTOList.get(i).getName(), phaseDTOList.get(i).getDescription(), activity.getPhaseSettingsActivityTab().getPhaseTemplateValues().get(i).getEligibleEmploymentTypes(),
                        activity.getPhaseSettingsActivityTab().getPhaseTemplateValues().get(i).isEligibleForManagement(), activity.getPhaseSettingsActivityTab().getPhaseTemplateValues().get(i).isStaffCanDelete(), activity.getPhaseSettingsActivityTab().getPhaseTemplateValues().get(i).isManagementCanDelete(),
                        activity.getPhaseSettingsActivityTab().getPhaseTemplateValues().get(i).isStaffCanSell(), activity.getPhaseSettingsActivityTab().getPhaseTemplateValues().get(i).isManagementCanSell(), activity.getPhaseSettingsActivityTab().getPhaseTemplateValues().get(i).getAllowedSettings());
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
            save(activityCopied);
        } else {
            activityCopied = activityMongoRepository.findByParentIdAndDeletedFalseAndUnitId(activityId, unitId);
            activityCopied.setDeleted(true);
        }
        save(activityCopied);
        return retrieveBasicDetails(activityCopied);
    }

    private ActivityDTO retrieveBasicDetails(Activity activity) {
        ActivityDTO activityDTO = new ActivityDTO(activity.getId(), activity.getName(), activity.getParentId());
        BeanUtils.copyProperties(activity, activityDTO);
        /*Optional<TimeType> timeType=timeTypeMongoRepository.findById(activity.getBalanceSettingsActivityTab().getTimeTypeId());
        if(timeType.isPresent()){
            activityDTO.setActivityCanBeCopied(timeType.get().isActivityCanBeCopied());
        }*/
        return activityDTO;

    }

    public ActivityWithSelectedDTO getActivityMappingDetails(Long unitId, String type) {
        ActivityWithSelectedDTO activityDetails = new ActivityWithSelectedDTO();
        ActivityWithUnitIdDTO activities = activityService.getActivityByUnitId(unitId, type);
        if (Optional.ofNullable(activities).isPresent()) {
            if (Optional.ofNullable(activities.getActivityDTOList()).isPresent())
                activityDetails.setAllActivities(activities.getActivityDTOList());
        }
        List<ActivityTagDTO> activityTagDTOS = activityMongoRepository.findAllActivityByUnitIdAndDeleted(unitId, false);
        activityDetails.setSelectedActivities(activityTagDTOS);
        return activityDetails;
    }

    public Map<String, Object> getAllActivityByUnit(Long unitId) {
        Map<String, Object> response = new HashMap<>();
        OrganizationDTO organizationDTO = genericIntegrationService.getOrganizationWithCountryId(unitId);
        List<ActivityTagDTO> activities = activityMongoRepository.findAllActivityByUnitIdAndDeleted(unitId, false);
        for (ActivityTagDTO activityTagDTO : activities) {
            boolean activityCanBeCopied = false;
            Set<OrganizationHierarchy> hierarchies = activityTagDTO.getActivityCanBeCopiedForOrganizationHierarchy();
            if (isCollectionNotEmpty(hierarchies)) {
                if ((organizationDTO.isParentOrganization() && hierarchies.contains(OrganizationHierarchy.ORGANIZATION)) ||
                        (!organizationDTO.isParentOrganization() && hierarchies.contains(OrganizationHierarchy.UNIT))) {
                    activityCanBeCopied = true;
                }
            }
            activityTagDTO.setActivityCanBeCopied(activityCanBeCopied);
        }
        List<ActivityCategory> activityCategories = activityCategoryRepository.findByCountryId(organizationDTO.getCountryId());
        response.put("activities", activities);
        response.put("activityCategories", activityCategories);
        return response;
    }

    public ActivityTabsWrapper getGeneralTabOfActivity(BigInteger activityId, Long unitId) {
        Activity activity = activityMongoRepository.findOne(activityId);
        if (!Optional.ofNullable(activity).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.activity.id", activityId);
        }
        OrganizationDTO organizationDTO = genericIntegrationService.getOrganizationWithCountryId(unitId);
        List<ActivityCategory> activityCategories = activityCategoryRepository.findByCountryId(organizationDTO.getCountryId());
        GeneralActivityTab generalTab = activity.getGeneralActivityTab();
        logger.info("activity.getTags() ================ > " + activity.getTags());
        //generalTab.setTags(tagMongoRepository.getTagsById(activity.getTags()));
        logger.info("activityId " + activityId);
        generalTab.setTags(null);
        GeneralActivityTabWithTagDTO generalActivityTabWithTagDTO = ObjectMapperUtils.copyPropertiesByMapper(generalTab, GeneralActivityTabWithTagDTO.class);
        if (!activity.getTags().isEmpty()) {
            generalActivityTabWithTagDTO.setTags(tagMongoRepository.getTagsById(activity.getTags()));
        }
        List<PresenceTypeDTO> presenceTypeDTOS = plannedTimeTypeService.getAllPresenceTypeByCountry(organizationDTO.getCountryId());
        PresenceTypeWithTimeTypeDTO presenceType = new PresenceTypeWithTimeTypeDTO(presenceTypeDTOS, organizationDTO.getCountryId());
        BalanceSettingsActivityTab balanceSettingsActivityTab = activity.getBalanceSettingsActivityTab();
        generalActivityTabWithTagDTO.setAddTimeTo(balanceSettingsActivityTab.getAddTimeTo());
        generalActivityTabWithTagDTO.setTimeTypeId(balanceSettingsActivityTab.getTimeTypeId());
        generalActivityTabWithTagDTO.setOnCallTimePresent(balanceSettingsActivityTab.getOnCallTimePresent());
        generalActivityTabWithTagDTO.setNegativeDayBalancePresent(balanceSettingsActivityTab.getNegativeDayBalancePresent());
        generalActivityTabWithTagDTO.setTimeType(balanceSettingsActivityTab.getTimeType());
        generalActivityTabWithTagDTO.setContent(activity.getNotesActivityTab().getContent());
        generalActivityTabWithTagDTO.setOriginalDocumentName(activity.getNotesActivityTab().getOriginalDocumentName());
        generalActivityTabWithTagDTO.setModifiedDocumentName(activity.getNotesActivityTab().getModifiedDocumentName());
        ActivityTabsWrapper activityTabsWrapper = new ActivityTabsWrapper(generalActivityTabWithTagDTO, activityId, activityCategories);
        activityTabsWrapper.setTimeTypes(timeTypeService.getAllTimeType(balanceSettingsActivityTab.getTimeTypeId(), presenceType.getCountryId()));
        /*TimeType timeType= timeTypeMongoRepository.findOneById(balanceSettingsActivityTab.getTimeTypeId());
        if(timeType!=null){
            boolean activityCanBeCopied = false;
            List<OrganizationHierarchy>  hierarchies = timeType.getAcitivityCanBeCopiedForHierarchy();
            if(isCollectionNotEmpty(hierarchies)) {
                if((organizationDTO.isParentOrganization() && hierarchies.contains(OrganizationHierarchy.ORGANIZATION)) ||
                        (!organizationDTO.isParentOrganization() && hierarchies.contains(OrganizationHierarchy.UNIT))){
                    activityCanBeCopied = true;
                }
            }
            generalActivityTabWithTagDTO.setActivityCanBeCopied(activityCanBeCopied);
        }*/
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
        // activityCopied.setCompositeActivities(null);
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
        activity.setGeneralActivityTab(generalTab);
        activity.setName(generalTab.getName());
        activity.setDescription(generalTab.getDescription());
        activity.setTags(generalDTO.getTags());


        // generalTab.setTags(tagMongoRepository.getTagsById(generalDTO.getTags()));
        Long countryId = genericIntegrationService.getCountryIdOfOrganization(unitId);
        List<ActivityCategory> activityCategories = activityCategoryRepository.findByCountryId(countryId);
        GeneralActivityTabWithTagDTO generalActivityTabWithTagDTO = ObjectMapperUtils.copyPropertiesByMapper(generalTab, GeneralActivityTabWithTagDTO.class);
        generalActivityTabWithTagDTO.setTags(null);
        if (!generalDTO.getTags().isEmpty()) {
            generalActivityTabWithTagDTO.setTags(tagMongoRepository.getTagsById(generalDTO.getTags()));
        }
        activityService.updateBalanceSettingTab(generalDTO, activity);
        activityService.updateNotesTabOfActivity(generalDTO, activity);
        save(activity);
        generalActivityTabWithTagDTO.setAddTimeTo(activity.getBalanceSettingsActivityTab().getAddTimeTo());
        generalActivityTabWithTagDTO.setTimeTypeId(activity.getBalanceSettingsActivityTab().getTimeTypeId());
        generalActivityTabWithTagDTO.setOnCallTimePresent(activity.getBalanceSettingsActivityTab().getOnCallTimePresent());
        generalActivityTabWithTagDTO.setNegativeDayBalancePresent(activity.getBalanceSettingsActivityTab().getNegativeDayBalancePresent());
        generalActivityTabWithTagDTO.setTimeType(activity.getBalanceSettingsActivityTab().getTimeType());
        generalActivityTabWithTagDTO.setContent(activity.getNotesActivityTab().getContent());
        generalActivityTabWithTagDTO.setOriginalDocumentName(activity.getNotesActivityTab().getOriginalDocumentName());
        generalActivityTabWithTagDTO.setModifiedDocumentName(activity.getNotesActivityTab().getModifiedDocumentName());
        return new ActivityTabsWrapper(generalActivityTabWithTagDTO, generalDTO.getActivityId(), activityCategories);

    }

   /* public ActivityTabsWrapper getBalanceSettingsTabOfType(BigInteger activityId, Long unitId) {
        Long countryId = genericIntegrationService.getCountryIdOfOrganization(unitId);
        List<PresenceTypeDTO> presenceTypeDTOS = plannedTimeTypeService.getAllPresenceTypeByCountry(countryId);
        PresenceTypeWithTimeTypeDTO presenceType = new PresenceTypeWithTimeTypeDTO(presenceTypeDTOS, countryId);
        Activity activity = activityMongoRepository.findOne(activityId);
        BalanceSettingsActivityTab balanceSettingsActivityTab = activity.getBalanceSettingsActivityTab();
        ActivityTabsWrapper activityTabsWrapper = new ActivityTabsWrapper(balanceSettingsActivityTab, presenceType);
        activityTabsWrapper.setTimeTypes(timeTypeService.getAllTimeType(balanceSettingsActivityTab.getTimeTypeId(), presenceType.getCountryId()));
        return activityTabsWrapper;
    }*/

    public ActivityTabsWrapper getTimeCalculationTabOfActivity(BigInteger activityId, Long unitId) {
        List<DayType> dayTypes = genericIntegrationService.getDayTypes(unitId);
        Activity activity = activityMongoRepository.findOne(activityId);
        TimeCalculationActivityTab timeCalculationActivityTab = activity.getTimeCalculationActivityTab();
        List<Long> rulesTabDayTypes = activity.getRulesActivityTab().getDayTypes();
        return new ActivityTabsWrapper(timeCalculationActivityTab, dayTypes, rulesTabDayTypes);
    }

    public ActivityTabsWrapper getRulesTabOfActivity(BigInteger activityId, Long unitId) {
        DayTypeEmploymentTypeWrapper dayTypeEmploymentTypeWrapper = genericIntegrationService.getDayTypesAndEmploymentTypesAtUnit(unitId);
        List<DayType> dayTypes = ObjectMapperUtils.copyPropertiesOfListByMapper(dayTypeEmploymentTypeWrapper.getDayTypes(), DayType.class);
        Activity activity = activityMongoRepository.findOne(activityId);
        RulesActivityTab rulesActivityTab = activity.getRulesActivityTab();
        return new ActivityTabsWrapper(rulesActivityTab, dayTypes, dayTypeEmploymentTypeWrapper.getEmploymentTypes());
    }

    public ActivityTabsWrapper getPhaseSettingTabOfActivity(BigInteger activityId, Long unitId) {
        Set<AccessGroupRole> roles = AccessGroupRole.getAllRoles();
        DayTypeEmploymentTypeWrapper dayTypeEmploymentTypeWrapper = genericIntegrationService.getDayTypesAndEmploymentTypesAtUnit(unitId);
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
            exceptionService.actionNotPermittedException("message.activity.overlaping");
        }
        if (Optional.ofNullable(activity).isPresent()) {
            exceptionService.dataNotFoundException(activity.getGeneralActivityTab().getEndDate() == null ? "message.activity.enddate.required" : "message.activity.active.alreadyExists");
        }
        Optional<Activity> activityFromDatabase = activityMongoRepository.findById(activityId);
        if (!activityFromDatabase.isPresent() || activityFromDatabase.get().isDeleted() || !unitId.equals(activityFromDatabase.get().getUnitId())) {
            exceptionService.dataNotFoundByIdException("message.activity.id", activityId);
        }
        //Checking the time type of activity whether it's eligible for copy or not
        ActivityDTO eligibleForCopy = activityMongoRepository.eligibleForCopy(activityId);
        if (eligibleForCopy == null || !eligibleForCopy.isActivityCanBeCopied()) {
            exceptionService.actionNotPermittedException("activity.not.eligible.for.copy");
        }
        Activity activityCopied = copyAllActivitySettingsInUnit(activityFromDatabase.get(), unitId);
        activityCopied.setName(activityDTO.getName().trim());
        activityCopied.getGeneralActivityTab().setName(activityDTO.getName().trim());
        activityCopied.getGeneralActivityTab().setStartDate(activityDTO.getStartDate());
        activityCopied.getGeneralActivityTab().setEndDate(activityDTO.getEndDate());
        activityCopied.setState(ActivityStateEnum.DRAFT);
        save(activityCopied);
        activityDTO.setId(activityCopied.getId());
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

        ActivityWithTimeTypeDTO activityWithTimeTypeDTO = new ActivityWithTimeTypeDTO(activityDTOS, timeTypeDTOS, intervals,
                minOpenShiftHours.getOpenShiftPhaseSetting().getMinOpenShiftHours(), counters);
        return activityWithTimeTypeDTO;
    }

    public boolean createDefaultDataForOrganization(Long unitId, OrgTypeAndSubTypeDTO orgTypeAndSubTypeDTO) {
        logger.info("I am going to create default data or organization " + unitId);
        //unitDataService.addParentOrganizationAndCountryIdForUnit(unitId, parentOrganizationId, countryId);
        if (orgTypeAndSubTypeDTO.isParentOrganization() || orgTypeAndSubTypeDTO.isWorkcentre()) {
            List<Phase> phases = phaseService.createDefaultPhase(unitId, orgTypeAndSubTypeDTO.getCountryId());
            phaseSettingsService.createDefaultPhaseSettings(unitId, phases);
            unitSettingService.createDefaultOpenShiftPhaseSettings(unitId, phases);
            activityConfigurationService.createDefaultSettings(unitId, orgTypeAndSubTypeDTO.getCountryId(), phases);
            List<Activity> existingActivities;
            if (orgTypeAndSubTypeDTO.getParentOrganizationId() == null) {
                existingActivities = activityMongoRepository.findAllActivitiesByOrganizationTypeOrSubTypeOrBreakTypes(orgTypeAndSubTypeDTO.getOrganizationTypeId(), orgTypeAndSubTypeDTO.getSubTypeId());
            } else {
                existingActivities = activityMongoRepository.findAllByUnitIdAndDeletedFalse(orgTypeAndSubTypeDTO.getParentOrganizationId());
            }

            if (!existingActivities.isEmpty()) {
                Set<Long> parentAccessGroupIds = existingActivities.stream().flatMap(a -> a.getPhaseSettingsActivityTab().getPhaseTemplateValues().stream().flatMap(b -> b.getActivityShiftStatusSettings().stream().flatMap(c -> c.getAccessGroupIds().stream()))).collect(Collectors.toSet());
                Map<Long, Long> accessGroupIdsMap = genericIntegrationService.getAccessGroupForUnit(unitId, parentAccessGroupIds);
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
                costTimeAgreementService.assignCountryCTAtoOrganisation(orgTypeAndSubTypeDTO.getCountryId(), orgTypeAndSubTypeDTO.getOrganizationSubTypeId(), unitId);
                wtaService.assignWTAToNewOrganization(orgTypeAndSubTypeDTO.getSubTypeId(), unitId, orgTypeAndSubTypeDTO.getCountryId());
                updateCompositeActivitiesIds(activityCopiedList);
            }
            TAndAGracePeriodSettingDTO tAndAGracePeriodSettingDTO = new TAndAGracePeriodSettingDTO(AppConstants.STAFF_GRACE_PERIOD_DAYS, AppConstants.MANAGEMENT_GRACE_PERIOD_DAYS);
            timeAttendanceGracePeriodService.updateTAndAGracePeriodSetting(unitId, tAndAGracePeriodSettingDTO);
        }
        periodSettingsService.createDefaultPeriodSettings(unitId);
        priorityGroupService.copyPriorityGroupsForUnit(unitId, orgTypeAndSubTypeDTO.getCountryId());
        return true;
    }

    /**
     * This method is used to update all composite activities Ids
     * which is initially set as country level composite activities,
     * after update all composite activities will be updated
     * as per Organizational level composite activities Ids.
     * CalledBy {#createDefaultDataForOrganization}
     *
     * @param activities{after copied into database}
     * @author mohit
     * @date 5-10-2018
     */
    private void updateCompositeActivitiesIds(List<Activity> activities) {
        Map<BigInteger, BigInteger> activityIdMap = activities.stream().collect(Collectors.toMap(k -> k.getParentId(), v -> v.getId()));
        for (Activity activity : activities) {
            Iterator<CompositeActivity> compositeActivityIterator = activity.getCompositeActivities().iterator();
            while (compositeActivityIterator.hasNext()) {
                CompositeActivity compositeActivity = compositeActivityIterator.next();
                if (activityIdMap.containsKey(compositeActivity.getActivityId())) {
                    compositeActivity.setActivityId(activityIdMap.get(compositeActivity.getActivityId()));
                } else {
                    compositeActivityIterator.remove();
                }
            }
        }
        save(activities);
    }


    public void verifyBreakAllowedOfActivities(boolean breakAllowed, List<Activity> activities) {
        List<String> invalidActivities = ActivityUtil.verifyCompositeActivities(breakAllowed, activities);
        if (invalidActivities.size() != 0) {
            List<String> errorMessages = new ArrayList<>(invalidActivities);
            if (breakAllowed) {
                exceptionService.actionNotPermittedException("activities.not.support.break", errorMessages);
            }
            if (!breakAllowed) {
                exceptionService.actionNotPermittedException("activities.support.break", errorMessages);
            }

        }
    }


}

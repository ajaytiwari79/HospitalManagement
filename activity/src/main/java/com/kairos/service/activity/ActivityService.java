package com.kairos.service.activity;

import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.config.env.EnvConfig;
import com.kairos.constants.AppConstants;
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
import com.kairos.dto.activity.open_shift.OpenShiftIntervalDTO;
import com.kairos.dto.activity.phase.PhaseDTO;
import com.kairos.dto.activity.phase.PhaseWeeklyDTO;
import com.kairos.dto.activity.presence_type.PresenceTypeDTO;
import com.kairos.dto.activity.presence_type.PresenceTypeWithTimeTypeDTO;
import com.kairos.dto.activity.shift.ShiftTemplateDTO;
import com.kairos.dto.activity.staffing_level.StaffingLevelPlanningDTO;
import com.kairos.dto.activity.time_type.TimeTypeDTO;
import com.kairos.dto.planner.planninginfo.PlannerSyncResponseDTO;
import com.kairos.dto.user.access_group.UserAccessRoleDTO;
import com.kairos.dto.user.access_permission.AccessGroupRole;
import com.kairos.dto.user.country.agreement.cta.cta_response.EmploymentTypeDTO;
import com.kairos.dto.user.country.day_type.DayType;
import com.kairos.dto.user.country.day_type.DayTypeEmploymentTypeWrapper;
import com.kairos.dto.user.country.tag.TagDTO;
import com.kairos.dto.user.organization.OrganizationDTO;
import com.kairos.dto.user.organization.OrganizationTypeAndSubTypeDTO;
import com.kairos.dto.user.organization.skill.Skill;
import com.kairos.enums.ActivityStateEnum;
import com.kairos.enums.DurationType;
import com.kairos.enums.IntegrationOperation;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.activity.TimeType;
import com.kairos.persistence.model.activity.tabs.*;
import com.kairos.persistence.model.activity.tabs.rules_activity_tab.RulesActivityTab;
import com.kairos.persistence.model.staffing_level.StaffingLevel;
import com.kairos.persistence.repository.activity.ActivityCategoryRepository;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.activity.TimeTypeMongoRepository;
import com.kairos.persistence.repository.common.MongoSequenceRepository;
import com.kairos.persistence.repository.counter.CounterRepository;
import com.kairos.persistence.repository.open_shift.OpenShiftIntervalRepository;
import com.kairos.persistence.repository.staffing_level.StaffingLevelMongoRepository;
import com.kairos.persistence.repository.tag.TagMongoRepository;
import com.kairos.rest_client.*;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.integration.PlannerSyncService;
import com.kairos.service.organization.OrganizationActivityService;
import com.kairos.service.phase.PhaseService;
import com.kairos.service.shift.ShiftService;
import com.kairos.service.shift.ShiftTemplateService;
import com.kairos.utils.external_plateform_shift.GetAllActivitiesResponse;
import com.kairos.utils.external_plateform_shift.TimeCareActivity;
import com.kairos.utils.external_plateform_shift.Transstatus;
import com.kairos.wrapper.activity.ActivityTabsWrapper;
import com.kairos.wrapper.activity.ActivityTagDTO;
import com.kairos.wrapper.activity.ActivityWithCompositeDTO;
import com.kairos.wrapper.activity.SkillActivityDTO;
import com.kairos.wrapper.phase.PhaseActivityDTO;
import com.kairos.wrapper.shift.ActivityWithUnitIdDTO;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.io.StringReader;
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

import static com.kairos.constants.AppConstants.*;
import static org.springframework.http.MediaType.APPLICATION_XML;


/**
 * Created by pawanmandhan on 17/8/17.
 */
@Service
public class ActivityService extends MongoBaseService {
    @Autowired
    private OrganizationRestClient organizationRestClient;
    @Inject
    private ActivityMongoRepository activityMongoRepository;
    @Inject
    private ActivityCategoryRepository activityCategoryRepository;
    @Inject
    private ShiftService shiftService;
    @Autowired
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
    private EnvConfig envConfig;
    @Inject
    private SkillRestClient skillRestClient;
    @Inject
    private TimeTypeService timeTypeService;
    @Inject
    private PlannerSyncService plannerSyncService;
    @Autowired
    private StaffingLevelMongoRepository staffingLevelMongoRepository;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private StaffRestClient staffRestClient;
    @Inject
    private OpenShiftIntervalRepository openShiftIntervalRepository;

    @Inject
    private ShiftTemplateService shiftTemplateService;
    @Inject
    private GenericIntegrationService genericIntegrationService;
    @Inject
    private CounterRepository counterRepository;
    @Inject
    private GenericRestClient genericRestClient;
    @Inject private MongoSequenceRepository mongoSequenceRepository;


    private final Logger logger = LoggerFactory.getLogger(this.getClass());

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

        BalanceSettingsActivityTab balanceSettingsActivityTab = new BalanceSettingsActivityTab(false, false);

        activity.setBalanceSettingsActivityTab(balanceSettingsActivityTab);

        List<PhaseDTO> phases = phaseService.getPhasesByCountryId(countryId);
        List<PhaseTemplateValue> phaseTemplateValues = getPhaseForRulesActivity(phases);


        RulesActivityTab rulesActivityTab = new RulesActivityTab();
        activity.setRulesActivityTab(rulesActivityTab);

        TimeCalculationActivityTab timeCalculationActivityTab = new TimeCalculationActivityTab(ENTERED_TIMES, 0L, true, LocalTime.of(7, 0), 1d);
        activity.setTimeCalculationActivityTab(timeCalculationActivityTab);

        IndividualPointsActivityTab individualPointsActivityTab = new IndividualPointsActivityTab("addHourValues", 0.0);
        activity.setIndividualPointsActivityTab(individualPointsActivityTab);

        CommunicationActivityTab communicationActivityTab = new CommunicationActivityTab(false, false);
        activity.setCommunicationActivityTab(communicationActivityTab);

        OptaPlannerSettingActivityTab optaPlannerSettingActivityTab = new OptaPlannerSettingActivityTab(AppConstants.MAX_ONE_ACTIVITY_PER_SHIFT, 0, true);
        activity.setOptaPlannerSettingActivityTab(optaPlannerSettingActivityTab);

        CTAAndWTASettingsActivityTab ctaAndWtaSettingsActivityTab = new CTAAndWTASettingsActivityTab(false);
        activity.setCtaAndWtaSettingsActivityTab(ctaAndWtaSettingsActivityTab);

        PhaseSettingsActivityTab phaseSettingsActivityTab=new PhaseSettingsActivityTab(phaseTemplateValues);
        activity.setPhaseSettingsActivityTab(phaseSettingsActivityTab);

        activity.setPermissionsActivityTab(new PermissionsActivityTab());

        activity.setNotesActivityTab(new NotesActivityTab());

        SkillActivityTab skillActivityTab = new SkillActivityTab();
        activity.setSkillActivityTab(skillActivityTab);
        LocationActivityTab locationActivityTab = new LocationActivityTab(Collections.EMPTY_LIST, Collections.EMPTY_LIST);
        activity.setLocationActivityTab(locationActivityTab);

    }

    public Map<String, Object> findAllActivityByCountry(long countryId) {
        Map<String, Object> response = new HashMap<>();
        List<ActivityTagDTO> activities = activityMongoRepository.findAllActivityByCountry(countryId);
        List<ActivityCategory> acivitityCategories = activityCategoryRepository.findByCountryId(countryId);
        response.put("activities", activities);
        response.put("activityCategories", acivitityCategories);
        return response;
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

    public HashMap<Long, HashMap<Long, BigInteger>> getListOfActivityIdsOfUnitByParentIds(List<BigInteger> parentActivityIds, List<Long> unitIds) {
        List<OrganizationActivityDTO> unitActivities = activityMongoRepository.findAllActivityOfUnitsByParentActivity(parentActivityIds, unitIds);
        HashMap<Long, HashMap<Long, BigInteger>> mappedParentUnitActivities = new HashMap<>();
        unitActivities.forEach(activityDTO -> {
            HashMap<Long, BigInteger> unitParentActivities = mappedParentUnitActivities.get(activityDTO.getUnitId().longValue());
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
        Activity activity = activityMongoRepository.findOne(new BigInteger(String.valueOf(generalDTO.getActivityId())));
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
        activity.setTags(generalDTO.getTags());
        activity.setDescription(generalTab.getDescription());
        save(activity);

        List<ActivityCategory> activityCategories = checkCountryAndFindActivityCategory(new BigInteger(String.valueOf(countryId)));
        //   generalTab.setTags(tagMongoRepository.getTagsById(generalDTO.getTags()));
        ActivityTabsWrapper activityTabsWrapper = new ActivityTabsWrapper(generalTab, activityCategories);

        return activityTabsWrapper;
    }


    public ActivityTabsWrapper getGeneralTabOfActivity(BigInteger countryId, BigInteger activityId) {
        List<ActivityCategory> activityCategories = checkCountryAndFindActivityCategory(countryId);
        Activity activity = activityMongoRepository.findOne(activityId);
        if (!Optional.ofNullable(activity).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.activity.timecare.id", activityId);
        }
        GeneralActivityTab generalTab = activity.getGeneralActivityTab();
//        generalTab.setTags(tagMongoRepository.getTagsById(activity.getTags()));
        ActivityTabsWrapper activityTabsWrapper = new ActivityTabsWrapper(generalTab, activityCategories);

        return activityTabsWrapper;
    }


    private List<ActivityCategory> checkCountryAndFindActivityCategory(BigInteger countryId) {
        List<ActivityCategory> activityCategories = activityCategoryRepository.findByCountryId(countryId.longValue());
        return activityCategories;
    }


    public ActivityTabsWrapper getBalanceSettingsTabOfActivity(BigInteger activityId, Long countryId) {
        List<PresenceTypeDTO> presenceTypeDTOS = plannedTimeTypeService.getAllPresenceTypeByCountry(countryId);
        PresenceTypeWithTimeTypeDTO presenceType = new PresenceTypeWithTimeTypeDTO(presenceTypeDTOS, countryId);
        Activity activity = activityMongoRepository.findOne(activityId);
        if (!Optional.ofNullable(activity).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.activity.id", activityId);
        }
        BalanceSettingsActivityTab balanceSettingsActivityTab = activity.getBalanceSettingsActivityTab();
        ActivityTabsWrapper activityTabsWrapper = new ActivityTabsWrapper(balanceSettingsActivityTab, presenceType);
        activityTabsWrapper.setTimeTypes(timeTypeService.getAllTimeType(balanceSettingsActivityTab.getTimeTypeId(), countryId));
        return activityTabsWrapper;
    }


    public ActivityTabsWrapper updateBalanceTab(BalanceSettingActivityTabDTO balanceDTO) {
        BalanceSettingsActivityTab balanceSettingsTab = new BalanceSettingsActivityTab();
        ObjectMapperUtils.copyProperties(balanceDTO, balanceSettingsTab);
        Activity activity = activityMongoRepository.findOne(new BigInteger(String.valueOf(balanceDTO.getActivityId())));
        if (!Optional.ofNullable(activity).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.activity.id", balanceDTO.getActivityId());
        }
        TimeType timeType = timeTypeMongoRepository.findOneById(balanceDTO.getTimeTypeId());
        if (!Optional.ofNullable(timeType).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.activity.timetype.notfound");
        }
        activity.getGeneralActivityTab().setBackgroundColor(timeType.getBackgroundColor());
        activity.getGeneralActivityTab().setColorPresent(true);
        activity.setBalanceSettingsActivityTab(balanceSettingsTab);
        //updating activity category based on time type
        Long countryId = activity.getCountryId();
        if (countryId == null)
            countryId = genericIntegrationService.getCountryIdOfOrganization(activity.getUnitId());

        updateActivityCategory(activity, countryId);
        save(activity);
        ActivityTabsWrapper activityTabsWrapper = new ActivityTabsWrapper(balanceSettingsTab);
        activityTabsWrapper.setActivityCategories(activityCategoryRepository.findByCountryId(countryId));

        return activityTabsWrapper;

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
        ActivityTabsWrapper activityTabsWrapper = new ActivityTabsWrapper(timeCalculationActivityTab);
        return activityTabsWrapper;

    }

    public List<CompositeShiftActivityDTO> assignCompositeActivitiesInActivity(BigInteger activityId, List<CompositeShiftActivityDTO> compositeShiftActivityDTOs) {
        Optional<Activity> activity = activityMongoRepository.findById(activityId);
        if (!activity.isPresent()) {
            exceptionService.dataNotFoundByIdException("exception.dataNotFound", "activity", activityId);
        }
        Set<BigInteger> compositeShiftIds = compositeShiftActivityDTOs.stream().map(compositeShiftActivityDTO -> compositeShiftActivityDTO.getActivityId()).collect(Collectors.toSet());
        List<Activity> activityMatched = activityMongoRepository.findAllActivitiesByIds(compositeShiftIds);
        if (activityMatched.size() != compositeShiftIds.size()) {
            exceptionService.illegalArgumentException("message.mismatched-ids", compositeShiftIds);
        }
        List<CompositeActivity> compositeActivities = compositeShiftActivityDTOs.stream().map(compositeShiftActivityDTO -> new CompositeActivity(compositeShiftActivityDTO.getActivityId(), compositeShiftActivityDTO.isAllowedBefore(), compositeShiftActivityDTO.isAllowedAfter())).collect(Collectors.toList());
        activity.get().setCompositeActivities(compositeActivities);
        save(activity.get());
        updateCompositeActivity(activityMatched, activity.get(), compositeActivities);
        return compositeShiftActivityDTOs;
    }

    public void updateCompositeActivity(List<Activity> activityMatched, Activity activity, List<CompositeActivity> compositeActivities) {
        Map<BigInteger, Activity> activityMap = activityMatched.stream().collect(Collectors.toMap(k -> k.getId(), v -> v));
        for (CompositeActivity compositeActivity : compositeActivities) {
            Activity composedActivity = activityMap.get(compositeActivity.getActivityId());
            Optional<CompositeActivity> optionalCompositeActivity = composedActivity.getCompositeActivities().stream().filter(a -> a.getActivityId().equals(activity.getId())).findFirst();
            CompositeActivity compositeActivityOfAnotherActivity = optionalCompositeActivity.orElseGet(CompositeActivity::new);
            compositeActivityOfAnotherActivity.setAllowedBefore(compositeActivity.isAllowedAfter());
            compositeActivityOfAnotherActivity.setAllowedAfter(compositeActivity.isAllowedBefore());
        }
        save(activityMatched);
    }

    public ActivityTabsWrapper getTimeCalculationTabOfActivity(BigInteger activityId, Long countryId) {
        List<DayType> dayTypes = genericIntegrationService.getDayTypesByCountryId(countryId);
        Activity activity = activityMongoRepository.findOne(activityId);
        TimeCalculationActivityTab timeCalculationActivityTab = activity.getTimeCalculationActivityTab();
        List<Long> rulesTabDayTypes = activity.getRulesActivityTab().getDayTypes();
        ActivityTabsWrapper activityTabsWrapper = new ActivityTabsWrapper(timeCalculationActivityTab, dayTypes, rulesTabDayTypes);
        return activityTabsWrapper;
    }

    public List<CompositeActivityDTO> getCompositeShiftTabOfActivity(BigInteger activityId) {
        Optional<Activity> activity = activityMongoRepository.findById(activityId);
        if (!activity.isPresent()) {
            exceptionService.dataNotFoundByIdException("message.activity.id", activityId);
        }
        List<CompositeActivityDTO> compositeActivities = new ArrayList<>();
        if (Optional.ofNullable(activity.get().getCompositeActivities()).isPresent() && !activity.get().getCompositeActivities().isEmpty()) {
            compositeActivities = activityMongoRepository.getCompositeActivities(activityId);
        }
        return compositeActivities;
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
        ActivityTabsWrapper activityTabsWrapper = new ActivityTabsWrapper(individualPointsActivityTab);

        return activityTabsWrapper;
    }

    public IndividualPointsActivityTab getIndividualPointsTabOfActivity(BigInteger activityId) {
        Activity activity = activityMongoRepository.findOne(activityId);
        IndividualPointsActivityTab individualPointsActivityTab = activity.getIndividualPointsActivityTab();
        return individualPointsActivityTab;
    }

    public ActivityTabsWrapper updateRulesTab(RulesActivityTabDTO rulesActivityDTO) {
        validateActivityTimeRules(rulesActivityDTO.getEarliestStartTime(), rulesActivityDTO.getLatestStartTime(), rulesActivityDTO.getMaximumEndTime(), rulesActivityDTO.getShortestTime(), rulesActivityDTO.getLongestTime());
        RulesActivityTab rulesActivityTab = ObjectMapperUtils.copyPropertiesByMapper(rulesActivityDTO, RulesActivityTab.class);
        Activity activity = activityMongoRepository.findOne(rulesActivityDTO.getActivityId());
        if (!Optional.ofNullable(activity).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.activity.id", rulesActivityDTO.getActivityId());
        }
        if (rulesActivityDTO.getCutOffIntervalUnit() != null && rulesActivityDTO.getCutOffStartFrom() != null) {
            if (rulesActivityDTO.getCutOffIntervalUnit().equals(DAYS) && rulesActivityDTO.getCutOffdayValue() == 0) {
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
        DayTypeEmploymentTypeWrapper dayTypeEmploymentTypeWrapper = genericIntegrationService.getDayTypesAndEmploymentTypes(countryId);
        List<DayType> dayTypes = dayTypeEmploymentTypeWrapper.getDayTypes();
        List<EmploymentTypeDTO> employmentTypeDTOS = dayTypeEmploymentTypeWrapper.getEmploymentTypes();
        Set<AccessGroupRole> roles=AccessGroupRole.getAllRoles();
        Activity activity = activityMongoRepository.findOne(activityId);

        PhaseSettingsActivityTab phaseSettingsActivityTab = activity.getPhaseSettingsActivityTab();
        return new ActivityTabsWrapper(roles,phaseSettingsActivityTab, dayTypes, employmentTypeDTOS);
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


    private List<CutOffInterval> getCutoffInterval(LocalDate dateFrom, CutOffIntervalUnit cutOffIntervalUnit, Integer dayValue) {
        LocalDate startDate = dateFrom;
        LocalDate endDate = startDate.plusYears(1);
        List<CutOffInterval> cutOffIntervals = new ArrayList<>();
        while (startDate.isBefore(endDate)) {
            LocalDate nextEndDate = startDate;
            switch (cutOffIntervalUnit) {
                case DAYS:
                    nextEndDate = startDate.plusDays(dayValue - 1);
                    break;
                case HALF_YEARLY:
                    nextEndDate = startDate.plusMonths(6).minusDays(1);
                    break;
                case WEEKS:
                    nextEndDate = startDate.plusWeeks(1).minusDays(1);
                    break;
                case MONTHS:
                    nextEndDate = startDate.plusMonths(1).minusDays(1);
                    break;
                case QUARTERS:
                    nextEndDate = startDate.plusMonths(3).minusDays(1);
                    break;
                case YEARS:
                    nextEndDate = startDate.plusYears(1).minusDays(1);
                    break;
            }
            cutOffIntervals.add(new CutOffInterval(startDate, nextEndDate));
            startDate = nextEndDate.plusDays(1);
        }
        return cutOffIntervals;
    }

    public ActivityTabsWrapper getRulesTabOfActivity(BigInteger activityId, Long countryId) {
        DayTypeEmploymentTypeWrapper dayTypeEmploymentTypeWrapper = genericIntegrationService.getDayTypesAndEmploymentTypes(countryId);
        List<DayType> dayTypes = dayTypeEmploymentTypeWrapper.getDayTypes();
        List<EmploymentTypeDTO> employmentTypeDTOS = dayTypeEmploymentTypeWrapper.getEmploymentTypes();
        Activity activity = activityMongoRepository.findOne(activityId);

        RulesActivityTab rulesActivityTab = activity.getRulesActivityTab();
        return new ActivityTabsWrapper(rulesActivityTab, dayTypes, employmentTypeDTOS);
    }

    public ActivityTabsWrapper updateNotesTabOfActivity(NotesActivityDTO notesActivityDTO) {
        NotesActivityTab notesActivityTab = new NotesActivityTab();
        ObjectMapperUtils.copyProperties(notesActivityDTO, notesActivityTab);

        Activity activity = activityMongoRepository.findOne(notesActivityDTO.getActivityId());
        if (Optional.ofNullable(activity.getNotesActivityTab().getModifiedDocumentName()).isPresent()) {
            notesActivityTab.setModifiedDocumentName(activity.getNotesActivityTab().getModifiedDocumentName());
        }
        if (Optional.ofNullable(activity.getNotesActivityTab().getOriginalDocumentName()).isPresent()) {
            notesActivityTab.setOriginalDocumentName(activity.getNotesActivityTab().getOriginalDocumentName());
        }
        if (!Optional.ofNullable(activity).isPresent()) {
            exceptionService.dataNotFoundByIdException("exception.dataNotFound", "activity", notesActivityDTO.getActivityId());
        }
        activity.setNotesActivityTab(notesActivityTab);
        save(activity);
        return new ActivityTabsWrapper(notesActivityTab);
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

    // PERMISSIONS

    public ActivityTabsWrapper updatePermissionsTabOfActivity(PermissionsActivityTabDTO permissionsActivityTabDTO) {
        PermissionsActivityTab permissionsActivityTab = new PermissionsActivityTab(permissionsActivityTabDTO.isEligibleForCopy());
        Activity activity = activityMongoRepository.findOne(permissionsActivityTabDTO.getActivityId());
        if (!Optional.ofNullable(activity).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.activity.id", permissionsActivityTabDTO.getActivityId());
        }
        activity.setPermissionsActivityTab(permissionsActivityTab);
        save(activity);
        return new ActivityTabsWrapper(permissionsActivityTab);
    }

    public ActivityTabsWrapper getPermissionsTabOfActivity(BigInteger activityId) {
        Activity activity = activityMongoRepository.findOne(activityId);
        if (!Optional.ofNullable(activity).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.activity.id", activityId);
        }
        return new ActivityTabsWrapper(activity.getPermissionsActivityTab());
    }

    // skills
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

    // organization Mapping
    public void updateOrgMappingDetailOfActivity(OrganizationMappingActivityDTO organizationMappingActivityDTO, BigInteger activityId) {
        Activity activity = activityMongoRepository.findOne(activityId);
        if (!Optional.ofNullable(activity).isPresent()) {
            exceptionService.dataNotFoundByIdException("exception.dataNotFound", "activity", activityId);
        }

        boolean isSuccess = genericIntegrationService.verifyOrganizationExpertizeAndRegions(organizationMappingActivityDTO);
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
        OrganizationTypeAndSubTypeDTO organizationTypeAndSubTypeDTO = genericIntegrationService.getOrganizationTypeAndSubTypeByUnitId(unitId, type);

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

    //optaPlannerSettings tab

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
        List<DayType> dayTypes = genericIntegrationService.getDayTypes(unitId);
        LocalDate date = LocalDate.now();
        int year = date.getYear();
        TemporalField weekOfWeekBasedYear = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear();
        int currentWeek = date.get(weekOfWeekBasedYear);
        int currentDayOfWeek = date.getDayOfWeek().getValue();
        List<PhaseDTO> phaseDTOs = phaseService.getApplicablePlanningPhasesByOrganizationId(unitId, Sort.Direction.DESC);

        // Set access Role of staff

        UserAccessRoleDTO userAccessRoleDTO = genericIntegrationService.getAccessOfCurrentLoggedInStaff();
        ArrayList<PhaseWeeklyDTO> phaseWeeklyDTOS = new ArrayList<PhaseWeeklyDTO>();
        for (PhaseDTO phaseObj : phaseDTOs) {
            if (phaseObj.getDurationType().equals(DurationType.WEEKS)) {
                for (int i = 0; i < phaseObj.getDuration(); i++) {
                    PhaseWeeklyDTO tempPhaseObj = phaseObj.buildWeekDTO();
                    /*if (tempPhaseObj.getName().equals(PUZZLE_PHASE_NAME) && !constructionPhaseAdded && currentDayOfWeek >= tempPhaseObj.getConstructionPhaseStartsAtDay()) {
                        tempPhaseObj.setName(CONSTRUCTION_PHASE_NAME);
                        tempPhaseObj.setDescription(CONSTRUCTION_PHASE_DESCRIPTION);
                        constructionPhaseAdded = true;
                    }*/
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
        return new PhaseActivityDTO(activities, phaseWeeklyDTOS, dayTypes, userAccessRoleDTO, shiftTemplates, phaseDTOs, phaseService.getActualPhasesByOrganizationId(unitId));
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


//        Integer activityTypeCount = activityMongoRepository.countByParentIdAndDeletedFalse(activityId);
//        if (activityTypeCount > 0) {
//            throw new ActionNotPermittedException("activity type is being used in organizations");
//        }
        activity.setDeleted(true);
        save(activity);
        return true;
    }

    public String getActivitesFromTimeCare() {
        String plainClientCredentials = "cluster:cluster";
        String base64ClientCredentials = new String(org.apache.commons.codec.binary.Base64.encodeBase64(plainClientCredentials.getBytes()));
        HttpHeaders headers = new HttpHeaders();
        List<MediaType> mediaTypes = new ArrayList<>();
        mediaTypes.add(APPLICATION_XML);
        headers.setAccept(mediaTypes);
        headers.add("Authorization", "Basic " + base64ClientCredentials);
        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);

        String importShiftURI = envConfig.getCarteServerHost() + KETTLE_EXECUTE_TRANS + "/home/prabjot/Desktop/Pentaho/data-integration/TimeCareIntegration/GetActivities.ktr";
        logger.info("importShiftURI----> " + importShiftURI);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> importResult = restTemplate.exchange(importShiftURI, HttpMethod.GET, entity, String.class);
        System.out.println(importResult.getStatusCode());
        if (importResult.getStatusCodeValue() == HttpStatus.OK.value()) {
            System.out.println(importResult);
            String importShiftStatusXMLURI = envConfig.getCarteServerHost() + "/kettle/transStatus/?name=GetActivities&xml=y";
            ResponseEntity<String> resultStatusXml = restTemplate.exchange(importShiftStatusXMLURI, HttpMethod.GET, entity, String.class);
            try {
                JAXBContext jaxbContext = JAXBContext.newInstance(Transstatus.class);
                Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
                StringReader reader = new StringReader(resultStatusXml.getBody());
                Transstatus transstatus = (Transstatus) jaxbUnmarshaller.unmarshal(reader);
                logger.info("trans status---> " + transstatus.getId());
            } catch (JAXBException exception) {
                logger.info("trans status---exception > " + exception);
            }

        }
        return importResult.toString();
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
        OrganizationDTO organizationDTO = genericIntegrationService.getOrganizationDTO(unitId);

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

        for (TimeCareActivity timeCareActivity : timeCareActivities) {

            Optional<Activity> result = activitiesByExternalIds.stream().filter(activityByExternalId -> timeCareActivity.getId().equals(activityByExternalId.getExternalId())).findFirst();
            Activity activity = (result.isPresent()) ? result.get() : new Activity();
            activity.setCountryId(countryId);
            activity.setParentActivity(true);
            activity.setState(ActivityStateEnum.LIVE);
            activity.setName(timeCareActivity.getName());
            activity.setOrganizationTypes(Collections.singletonList(orgType));
            activity.setOrganizationSubTypes(orgSubTypes);
            activity.setExternalId(timeCareActivity.getId());
            //general tab
            GeneralActivityTab generalActivityTab = (Optional.ofNullable(activity.getGeneralActivityTab()).isPresent()) ? activity.getGeneralActivityTab() :
                    new GeneralActivityTab();
            generalActivityTab.setName(activity.getName());
            generalActivityTab.setShortName(timeCareActivity.getShortName());
            generalActivityTab.setCategoryId(activityCategory.getId());
            activity.setGeneralActivityTab(generalActivityTab);

            //balance setting tab
            BalanceSettingsActivityTab balanceSettingsActivityTab = Optional.ofNullable(activity.getBalanceSettingsActivityTab()).isPresent() ? activity.getBalanceSettingsActivityTab() :
                    new BalanceSettingsActivityTab();
            balanceSettingsActivityTab.setTimeTypeId(timeCareActivity.getIsWork() && timeCareActivity.getIsPresence() ? presenceTimeTypeId : absenceTimeTypeId);
            balanceSettingsActivityTab.setNegativeDayBalancePresent(timeCareActivity.getNegativeDayBalance());
            activity.setBalanceSettingsActivityTab(balanceSettingsActivityTab);

            //rules activity tab
            RulesActivityTab rulesActivityTab = Optional.ofNullable(activity.getRulesActivityTab()).isPresent() ? activity.getRulesActivityTab() :
                    new RulesActivityTab();

            rulesActivityTab.setEligibleForStaffingLevel(timeCareActivity.getIsStaffing());
            List<PhaseTemplateValue> phaseTemplateValues = getPhaseForRulesActivity(phases);
            activity.setRulesActivityTab(rulesActivityTab);

            // location settings
            LocationActivityTab locationActivityTab = new LocationActivityTab(Collections.EMPTY_LIST, Collections.EMPTY_LIST);
            activity.setLocationActivityTab(locationActivityTab);

            //Time calculation tab
            TimeCalculationActivityTab timeCalculationActivityTab = Optional.ofNullable(activity.getTimeCalculationActivityTab()).isPresent() ?
                    activity.getTimeCalculationActivityTab() : new TimeCalculationActivityTab();
            List<String> balanceTypes = new ArrayList<>();
            balanceTypes.add(timeCareActivity.getBalanceType().replace(" ", "_"));
            timeCalculationActivityTab.setMethodForCalculatingTime(durationCalculationMethod(timeCareActivity.getTimeMethod()));
            timeCalculationActivityTab.setBalanceType(balanceTypes);
            if (timeCalculationActivityTab.getMethodForCalculatingTime().equals(FIXED_TIME)) {
                timeCalculationActivityTab.setFixedTimeValue(0l);
            }
            timeCalculationActivityTab.setDefaultStartTime(LocalTime.of(7, 0));
            timeCalculationActivityTab.setMultiplyWithValue(1d);
            timeCalculationActivityTab.setMultiplyWith(true);
            if (!StringUtils.isBlank(timeCareActivity.getMultiplyTimeWith())) {
                timeCalculationActivityTab.setMultiplyWithValue(Double.parseDouble(timeCareActivity.getMultiplyTimeWith()));
                timeCalculationActivityTab.setMultiplyWith(true);
            }
            activity.setTimeCalculationActivityTab(timeCalculationActivityTab);

            if (!timeCareActivity.getArrayOfSkill().isEmpty()) {
                List<ActivitySkill> activitySkills = skills.stream().filter(kairosSkill -> timeCareActivity.getArrayOfSkill().stream().map(timeCareSkill -> timeCareSkill).
                        anyMatch(timeCareSkill -> timeCareSkill.equals(kairosSkill.getName()))).map(skill -> new ActivitySkill(skill.getName(), "2", skill.getId())).collect(Collectors.toList());
                SkillActivityTab skillActivityTab = Optional.ofNullable(activity.getSkillActivityTab()).isPresent() ? activity.getSkillActivityTab() : new SkillActivityTab();
                skillActivityTab.setActivitySkills(activitySkills);
                activity.setSkillActivityTab(skillActivityTab);
            } else {
                SkillActivityTab skillActivityTab = new SkillActivityTab();
                activity.setSkillActivityTab(skillActivityTab);
            }
            activities.add(activity);
        }
        save(activities);
        return activities;
    }


    private String durationCalculationMethod(String method) {
        String calculationType = null;
        switch (method) {
            case FIXED_TIME_FOR_TIMECARE:
                calculationType = FIXED_TIME;
                break;
            case WEEKLY_WORK_TIME:
                calculationType = FULL_DAY_CALCULATION;
                break;
            case FULL_TIME_HOUR:
                calculationType = WEEKLY_HOURS;
                break;
            case CALCULATED_TIME:
                calculationType = ENTERED_TIMES;
                break;
       /*     case "":
                break;*/

        }
        return calculationType;
    }

    private void mapActivitiesInOrganization(List<Activity> countryActivities, Long unitId, List<String> externalIds) {

        List<Activity> unitActivities = activityMongoRepository.findByUnitIdAndExternalIdInAndDeletedFalse(unitId, externalIds);
        List<PhaseDTO> phases = phaseService.getPhasesByUnit(unitId);
        List<Activity> organizationActivities = new ArrayList<>();
        for (Activity countryActivity : countryActivities) {
            Optional<Activity> result = unitActivities.stream().filter(unitActivity -> unitActivity.getExternalId().equals(countryActivity.getExternalId())).findFirst();
            if (!result.isPresent()) {
                Activity activity = SerializationUtils.clone(countryActivity);
                List<PhaseTemplateValue> phaseTemplateValues = getPhaseForRulesActivity(phases);
                activity.setId(null);
                activity.setParentId(countryActivity.getId());
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

    private List<PhaseTemplateValue> getPhaseForRulesActivity(List<PhaseDTO> phases) {
        List<PhaseTemplateValue> phaseTemplateValues = new ArrayList<>();
        for (PhaseDTO phaseDTO : phases) {
            PhaseTemplateValue phaseTemplateValue = new PhaseTemplateValue();
            phaseTemplateValue.setPhaseId(phaseDTO.getId());
            phaseTemplateValue.setName(phaseDTO.getName());
            phaseTemplateValue.setDescription(phaseDTO.getDescription());
            phaseTemplateValue.setEligibleForManagement(false);
            phaseTemplateValue.setEligibleEmploymentTypes(new ArrayList<>());
            phaseTemplateValue.setAllowedSettings(new AllowedSettings());
            phaseTemplateValues.add(phaseTemplateValue);
        }
        return phaseTemplateValues;
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
        if (!activityFromDatabase.get().getPermissionsActivityTab().isEligibleForCopy()) {
            exceptionService.actionNotPermittedException("activity.not.eligible.for.copy");
        }


        Activity activityCopied = new Activity();
        ObjectMapperUtils.copyPropertiesExceptSpecific(activityFromDatabase.get(), activityCopied, "id");
        activityCopied.setName(activityDTO.getName().trim());
        activityCopied.getGeneralActivityTab().setName(activityDTO.getName().trim());
        activityCopied.getGeneralActivityTab().setStartDate(activityDTO.getStartDate());
        activityCopied.setState(ActivityStateEnum.PUBLISHED);
        activityCopied.getGeneralActivityTab().setEndDate(activityDTO.getEndDate());
        save(activityCopied);
        activityDTO.setId(activityCopied.getId());
        PermissionsActivityTabDTO permissionsActivityTabDTO = new PermissionsActivityTabDTO();
        BeanUtils.copyProperties(activityCopied.getPermissionsActivityTab(), permissionsActivityTabDTO);
        activityDTO.setPermissionsActivityTab(permissionsActivityTabDTO);
        return activityDTO;
    }

    public ActivityTabsWrapper getLocationsTabOfActivity(BigInteger activityId) {
        Activity activity = activityMongoRepository.findOne(activityId);
        ActivityTabsWrapper activityTabsWrapper = new ActivityTabsWrapper(activity.getLocationActivityTab());
        return activityTabsWrapper;
    }

    public ActivityTabsWrapper updateLocationsTabOfActivity(LocationActivityTabDTO locationActivityTabDTO) {
        Activity activity = activityMongoRepository.findOne(locationActivityTabDTO.getActivityId());
        if (!Optional.ofNullable(activity).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.activity.id", locationActivityTabDTO.getActivityId());
        }
        LocationActivityTab locationActivityTab = new LocationActivityTab(locationActivityTabDTO.getCanBeStartAt(), locationActivityTabDTO.getCanBeEndAt());
        activity.setLocationActivityTab(locationActivityTab);
        save(activity);
        ActivityTabsWrapper activityTabsWrapper = new ActivityTabsWrapper(locationActivityTab);
        return activityTabsWrapper;

    }

    public Activity buildActivity(ActivityDTO activityDTO) {
        List<BigInteger> tags = new ArrayList<>(activityDTO.getTags());
        Activity activity = new Activity(activityDTO.getName(), activityDTO.getDescription(), tags);
        return activity;
    }

    public PlannerSyncResponseDTO initialOptaplannerSync(Long organisationId, Long unitId) {
        List<Activity> activities = activityMongoRepository.findAllActivitiesByUnitId(unitId);
        List<StaffingLevel> staffingLevels = staffingLevelMongoRepository.findByUnitIdAndCurrentDateBetweenAndDeletedFalse(unitId, DateUtils.convertLocalDateToDate(LocalDate.now().minusMonths(1)), DateUtils.convertLocalDateToDate(LocalDate.now().plusMonths(1)));
        plannerSyncService.publishActivities(unitId, activities, IntegrationOperation.CREATE);
        List<StaffingLevelPlanningDTO> staffingLevelPlanningDTOS = new ArrayList<>();
        for (StaffingLevel staffingLevel : staffingLevels) {
            StaffingLevelPlanningDTO staffingLevelPlanningDTO = new StaffingLevelPlanningDTO(staffingLevel.getId(), staffingLevel.getPhaseId(), staffingLevel.getCurrentDate(), staffingLevel.getWeekCount(), staffingLevel.getStaffingLevelSetting(), staffingLevel.getPresenceStaffingLevelInterval(), null);
            staffingLevelPlanningDTOS.add(staffingLevelPlanningDTO);
        }
        plannerSyncService.publishStaffingLevels(unitId, staffingLevelPlanningDTOS, IntegrationOperation.CREATE);
        return new PlannerSyncResponseDTO(true);
    }

    public ActivityWithTimeTypeDTO getActivitiesWithTimeTypes(long countryId) {
        List<ActivityDTO> activityDTOS = activityMongoRepository.findAllActivitiesWithTimeTypes(countryId);
        List<TimeTypeDTO> timeTypeDTOS = timeTypeService.getAllTimeType(null, countryId);
        List<OpenShiftIntervalDTO> intervals = openShiftIntervalRepository.getAllByCountryIdAndDeletedFalse(countryId);
        List<CounterDTO> counters = counterRepository.getAllCounterBySupportedModule(ModuleType.OPEN_SHIFT);
        ActivityWithTimeTypeDTO activityWithTimeTypeDTO = new ActivityWithTimeTypeDTO(activityDTOS, timeTypeDTOS, intervals, counters);
        return activityWithTimeTypeDTO;
    }

    private boolean validateReminderSettings(CommunicationActivityDTO communicationActivityDTO) {
        //Collections.sort(communicationActivityDTO.getActivityReminderSettings(), Comparator.comparing(ActivityReminderSettings::getSequence));
        int counter = 0;
        if (!communicationActivityDTO.getActivityReminderSettings().isEmpty()) {
          //  byte lastSequence = communicationActivityDTO.getActivityReminderSettings().get(communicationActivityDTO.getActivityReminderSettings().size() - 1).getSequence();
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
                if (currentSettings.getId()==null) {
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
                    currentSettings.getSendReminder().getTimeValue(),currentSettings.getSendReminder().getDurationType(),frequencySettings.getTimeValue(),frequencySettings.getDurationType());
        }
        if (currentSettings.getSendReminder().getDurationType() == DurationType.MINUTES &&
                (currentSettings.getSendReminder().getDurationType() == DurationType.DAYS)) {
            exceptionService.actionNotPermittedException("new_value_cant_be_greater_than_previous",
                    currentSettings.getSendReminder().getTimeValue(),currentSettings.getSendReminder().getDurationType(),frequencySettings.getTimeValue(),frequencySettings.getDurationType());
        }

    }

    public void validateActivityTimeRules(LocalTime earliestStartTime, LocalTime latestStartTime, LocalTime maximumEndTime, Short shortestTime, Short longestTime) {

        if (shortestTime != null && longestTime != null && shortestTime > longestTime) {
            exceptionService.actionNotPermittedException("shortest.time.greater.longest");
        }
        //TODO Please don't remove this commented code as we need it
//        if(Optional.ofNullable(earliestStartTime).isPresent() &&
//                Optional.ofNullable(latestStartTime).isPresent() &&
//                earliestStartTime.isAfter(latestStartTime)){
//            exceptionService.actionNotPermittedException("earliest.start.time.less.latest");
//        }
//
//        if(Optional.ofNullable(earliestStartTime).isPresent() &&
//                Optional.ofNullable(latestStartTime).isPresent() && Optional.ofNullable(maximumEndTime).isPresent() && Optional.ofNullable(longestTime).isPresent() &&
//                earliestStartTime.plusMinutes(longestTime).isAfter(maximumEndTime)) {
//            exceptionService.actionNotPermittedException("longest.duration.exceed.limit");
//        }


    }


}

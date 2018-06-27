package com.kairos.service.organization;

import com.kairos.activity.dto.activity.ActivityTabsWrapper;
import com.kairos.activity.dto.activity.ActivityTagDTO;
import com.kairos.activity.dto.activity.ActivityWithSelectedDTO;
import com.kairos.activity.dto.activity.GeneralActivityTabDTO;
import com.kairos.activity.time_type.TimeTypeDTO;
import com.kairos.client.GenericIntegrationService;
import com.kairos.client.OrganizationRestClient;
import com.kairos.response.dto.web.day_type.DayType;
import com.kairos.persistence.repository.unit_settings.UnitSettingRepository;
import com.kairos.service.unit_settings.UnitSettingService;
import com.kairos.util.ObjectMapperUtils;
import com.kairos.response.dto.web.day_type.DayTypeEmploymentTypeWrapper;
import com.kairos.response.dto.web.presence_type.PresenceTypeWithTimeTypeDTO;

import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.activity.tabs.*;
import com.kairos.persistence.model.open_shift.OrderAndActivityDTO;
import com.kairos.persistence.model.phase.Phase;
import com.kairos.persistence.repository.activity.ActivityCategoryRepository;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.open_shift.OpenShiftIntervalRepository;
import com.kairos.persistence.repository.tag.TagMongoRepository;
import com.kairos.dto.ActivityDTO;
import com.kairos.activity.dto.ActivityWithUnitIdDTO;
import com.kairos.service.MongoBaseService;
import com.kairos.service.activity.ActivityService;
import com.kairos.service.activity.PlannedTimeTypeService;
import com.kairos.service.activity.TimeTypeService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.integration.PlannerSyncService;
import com.kairos.service.open_shift.OrderService;
import com.kairos.service.period.PeriodSettingsService;
import com.kairos.service.phase.PhaseService;
import com.kairos.service.unit_settings.ActivityConfigurationService;
import com.kairos.service.unit_settings.PhaseSettingsService;
import com.kairos.enums.ActivityStateEnum;
import com.kairos.response.dto.web.ActivityWithTimeTypeDTO;
import com.kairos.response.dto.web.open_shift.OpenShiftIntervalDTO;
import com.kairos.response.dto.web.phase.PhaseDTO;
import com.kairos.response.dto.web.presence_type.PresenceTypeDTO;
import com.kairos.activity.unit_settings.UnitSettingDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;

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

    private @Inject
    ActivityConfigurationService activityConfigurationService;


    public ActivityDTO copyActivity(Long unitId, BigInteger activityId, boolean checked) {
        ActivityDTO activityDetail = null;
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
                PhaseTemplateValue phaseTemplateValue = new PhaseTemplateValue(phaseDTOList.get(i).getId(), phaseDTOList.get(i).getName(), phaseDTOList.get(i).getDescription(),
                        activity.getRulesActivityTab().getEligibleForSchedules().get(i).getEligibleEmploymentTypes(), activity.getRulesActivityTab().getEligibleForSchedules().get(i).isEligibleForManagement());
                phaseTemplateValues.add(phaseTemplateValue);
            }
            activity.getRulesActivityTab().setEligibleForSchedules(phaseTemplateValues);
            activityCopied = copyAllActivitySettingsInUnit(activity, unitId);
            save(activityCopied);
        } else {
            activityCopied = activityMongoRepository.findByParentIdAndDeletedFalseAndUnitId(activityId, unitId);
            activityCopied.setDeleted(true);

        }
        save(activityCopied);
        return activityDetail;
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
        Long organizationId = organizationRestClient.getCountryIdOfOrganization(unitId);
        List<ActivityTagDTO> activities = activityMongoRepository.findAllActivityByUnitIdAndDeleted(unitId, false);
        List<ActivityCategory> activityCategories = activityCategoryRepository.findByCountryId(organizationId);
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
        generalTab.setTags(tagMongoRepository.getTagsById(activity.getTags()));
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
        GeneralActivityTab generalTab = generalDTO.buildGeneralActivityTab();
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
        generalTab.setTags(tagMongoRepository.getTagsById(generalDTO.getTags()));
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
        ActivityTabsWrapper activityTabsWrapper = new ActivityTabsWrapper(timeCalculationActivityTab, dayTypes);
        return activityTabsWrapper;
    }

    public ActivityTabsWrapper getRulesTabOfActivity(BigInteger activityId, Long unitId) {
        DayTypeEmploymentTypeWrapper dayTypeEmploymentTypeWrapper = genericIntegrationService.getDayTypesAndEmploymentTypesAtUnit(unitId);
        List<DayType> dayTypes = ObjectMapperUtils.copyProperties(dayTypeEmploymentTypeWrapper.getDayTypes(), DayType.class);
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
        activityDTO.setId(activityCopied.getId());
        return activityDTO;
    }

    public OrderAndActivityDTO getActivitiesWithBalanceSettings(long unitId) {
        OrderAndActivityDTO orderAndActivityDTO = new OrderAndActivityDTO();
        orderAndActivityDTO.setActivities(activityMongoRepository.findAllActivitiesWithBalanceSettings(unitId));
        orderAndActivityDTO.setOrders(orderService.getOrdersByUnitId(unitId));
        orderAndActivityDTO.setMinOpenShiftHours(unitSettingRepository.getMinOpenShiftHours(unitId).getOpenShiftPhaseSetting().getMinOpenShiftHours());
        return orderAndActivityDTO;
    }

    public ActivityWithTimeTypeDTO getActivitiesWithTimeTypesByUnit(Long unitId, Long countryId) {
        List<ActivityDTO> activityDTOS = activityMongoRepository.findAllActivitiesWithTimeTypesByUnit(unitId);
        List<TimeTypeDTO> timeTypeDTOS = timeTypeService.getAllTimeType(null, countryId);
        List<OpenShiftIntervalDTO> intervals = openShiftIntervalRepository.getAllByCountryIdAndDeletedFalse(countryId);
        UnitSettingDTO minOpenShiftHours = unitSettingRepository.getMinOpenShiftHours(unitId);
        ActivityWithTimeTypeDTO activityWithTimeTypeDTO = new ActivityWithTimeTypeDTO(activityDTOS, timeTypeDTOS, intervals, minOpenShiftHours.getOpenShiftPhaseSetting().getMinOpenShiftHours());
        return activityWithTimeTypeDTO;
    }

    public boolean createDefaultDataForOrganization(Long unitId, Long countryId) {
        List<Phase> phases = phaseService.createDefaultPhase(unitId, countryId);
        periodSettingsService.createDefaultPeriodSettings(unitId);
        phaseSettingsService.createDefaultPhaseSettings(unitId, phases);
        unitSettingService.createDefaultOpenShiftPhaseSettings(unitId, phases);
        activityConfigurationService.createDefaultSettings(countryId,unitId, phases);
        return true;
    }

}

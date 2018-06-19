package com.kairos.activity.service.organization;

import com.kairos.activity.client.OrganizationRestClient;
import com.kairos.activity.client.dto.DayType;
import com.kairos.activity.client.dto.Phase.PhaseDTO;
import com.kairos.activity.persistence.repository.unit_settings.UnitSettingRepository;
import com.kairos.activity.service.unit_settings.UnitSettingService;
import com.kairos.response.dto.web.presence_type.PresenceTypeWithTimeTypeDTO;
import com.kairos.activity.enums.IntegrationOperation;
import com.kairos.activity.persistence.model.activity.Activity;
import com.kairos.activity.persistence.model.activity.tabs.*;
import com.kairos.activity.persistence.model.open_shift.OrderAndActivityDTO;
import com.kairos.activity.persistence.model.phase.Phase;
import com.kairos.activity.persistence.repository.activity.ActivityCategoryRepository;
import com.kairos.activity.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.activity.persistence.repository.open_shift.OpenShiftIntervalRepository;
import com.kairos.activity.persistence.repository.tag.TagMongoRepository;

import com.kairos.activity.response.dto.ActivityDTO;
import com.kairos.activity.response.dto.ActivityWithUnitIdDTO;

import com.kairos.activity.response.dto.activity.*;
import com.kairos.activity.service.MongoBaseService;
import com.kairos.activity.service.activity.ActivityService;
import com.kairos.activity.service.activity.PlannedTimeTypeService;
import com.kairos.activity.service.activity.TimeTypeService;
import com.kairos.activity.service.exception.ExceptionService;
import com.kairos.activity.service.integration.PlannerSyncService;
import com.kairos.activity.service.open_shift.OrderService;
import com.kairos.activity.service.period.PeriodSettingsService;
import com.kairos.activity.service.phase.PhaseService;
import com.kairos.activity.service.unit_settings.PhaseSettingsService;
import com.kairos.activity.util.DateUtils;
import com.kairos.persistence.model.enums.ActivityStateEnum;

import com.kairos.response.dto.web.ActivityWithTimeTypeDTO;
import com.kairos.response.dto.web.open_shift.OpenShiftIntervalDTO;
import com.kairos.response.dto.web.presence_type.PresenceTypeDTO;
import com.kairos.response.dto.web.unit_settings.UnitSettingDTO;
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


    public HashMap copyActivity(Long unitId, BigInteger activityId, boolean checked) {
        logger.info("activityId,{}", activityId);
        Activity activity = activityMongoRepository.findOne(activityId);
        List<PhaseDTO> phaseDTOList = phaseService.getPhasesByUnit(unitId);
        List<PhaseTemplateValue> phaseTemplateValues = new ArrayList<>();
        for (PhaseDTO phaseDTO : phaseDTOList) {
            PhaseTemplateValue phaseTemplateValue = new PhaseTemplateValue(phaseDTO.getId(), phaseDTO.getName(), phaseDTO.getDescription(), false, false);
            phaseTemplateValues.add(phaseTemplateValue);
        }
        activity.getRulesActivityTab().setEligibleForSchedules(phaseTemplateValues);

        if (!Optional.ofNullable(activity).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.activity.id", activityId);
        }
        if (checked) {
            Activity activityCopied = copyAllActivitySettingsInUnit(activity, unitId);
            save(activityCopied);

            if (!activity.getState().equals(ActivityStateEnum.LIVE)) {
                activity.setState(ActivityStateEnum.LIVE);
                save(activity);
            }
            plannerSyncService.publishActivity(unitId, activity, IntegrationOperation.CREATE);
            return activityCopied.retrieveBasicDetails();

        } else {
            Activity activityCopied = activityMongoRepository.findByParentIdAndDeletedFalseAndUnitId(activityId, unitId);
            activityCopied.setDeleted(true);
            save(activityCopied);

        }
        return null;
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
        ActivityCategory activityCategory = activityCategoryRepository.getByIdAndNonDeleted(generalDTO.getCategoryId());
        if (activityCategory == null) {
            exceptionService.dataNotFoundByIdException("message.category.notExist");
        }
        if (generalDTO.getEndDate() != null && generalDTO.getEndDate().isBefore(generalDTO.getStartDate())) {
            exceptionService.actionNotPermittedException("message.activity.enddate.greaterthan.startdate");
        }
        Activity activity = activityMongoRepository.findOne(generalDTO.getActivityId());
        //Activity IsActivityExists = activityMongoRepository.findByNameExcludingCurrentInUnit(generalDTO.getName(), generalDTO.getActivityId(), activity.getUnitId());
        Date date = DateUtils.asDate(generalDTO.getStartDate());
        Activity IsActivityExists = activityMongoRepository.findByNameExcludingCurrentInUnitAndDate(generalDTO.getName(), generalDTO.getActivityId(), activity.getUnitId(), date);
        if (Optional.ofNullable(IsActivityExists).isPresent()) {
            if (!Optional.ofNullable(IsActivityExists.getGeneralActivityTab().getEndDate()).isPresent()) {
                exceptionService.dataNotFoundException("message.activity.enddate.required", generalDTO.getName());
            } else {
                exceptionService.dataNotFoundException("message.activity.active.alreadyExists");
            }
        }
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
        Activity activity = activityMongoRepository.findOne(activityId);
        List<DayType> dayTypes = organizationRestClient.getDayTypes(unitId);
        RulesActivityTab rulesActivityTab = activity.getRulesActivityTab();
        ActivityTabsWrapper activityTabsWrapper = new ActivityTabsWrapper(rulesActivityTab, dayTypes);

        return activityTabsWrapper;
    }

    public ActivityDTO copyActivityDetails(Long unitId, BigInteger activityId, ActivityDTO activityDTO) {
        //Need to know why we are returning object here as we can also return a simple boolean to check whether activity exist or not
        Activity activity = activityMongoRepository.
                findByNameIgnoreCaseAndUnitIdAndByDate(activityDTO.getName().trim(), unitId, activityDTO.getStartDate());
        if (Optional.ofNullable(activity).isPresent()) {
            logger.error("ActivityName already exist" + activityDTO.getName());
            exceptionService.duplicateDataException("message.activity.name", activityDTO.getName());
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
        unitSettingService.createDefaultOpenShiftPhaseSettings(unitId,phases);
        return true;
    }

}

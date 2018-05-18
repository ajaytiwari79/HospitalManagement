package com.kairos.activity.service.activity;


import com.kairos.activity.client.OrganizationRestClient;
import com.kairos.activity.client.SkillRestClient;
import com.kairos.activity.client.dto.DayType;
import com.kairos.activity.client.dto.Phase.PhaseDTO;
import com.kairos.activity.client.dto.Phase.PhaseWeeklyDTO;
import com.kairos.activity.client.dto.activityType.PresenceTypeWithTimeTypeDTO;
import com.kairos.activity.client.dto.organization.OrganizationDTO;
import com.kairos.activity.client.dto.skill.Skill;
import com.kairos.activity.config.env.EnvConfig;
import com.kairos.activity.custom_exception.ActionNotPermittedException;
import com.kairos.activity.custom_exception.DataNotFoundByIdException;
import com.kairos.activity.custom_exception.DataNotFoundException;
import com.kairos.activity.custom_exception.DuplicateDataException;
import com.kairos.activity.enums.IntegrationOperation;
import com.kairos.activity.persistence.model.activity.Activity;
import com.kairos.activity.persistence.model.activity.TimeType;
import com.kairos.activity.persistence.model.activity.tabs.*;
import com.kairos.activity.persistence.model.staffing_level.StaffingLevel;
import com.kairos.activity.persistence.repository.activity.ActivityCategoryRepository;
import com.kairos.activity.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.activity.persistence.repository.activity.TimeTypeMongoRepository;
import com.kairos.activity.persistence.repository.staffing_level.StaffingLevelMongoRepository;
import com.kairos.activity.persistence.repository.tag.TagMongoRepository;
import com.kairos.activity.response.dto.*;
import com.kairos.activity.response.dto.ActivityDTO;
import com.kairos.activity.response.dto.activity.*;
import com.kairos.activity.response.dto.staffing_level.StaffingLevelDTO;
import com.kairos.activity.response.dto.tag.TagDTO;
import com.kairos.activity.service.MongoBaseService;
import com.kairos.activity.service.exception.ExceptionService;
import com.kairos.activity.service.integration.PlannerSyncService;
import com.kairos.activity.service.organization.OrganizationActivityService;
import com.kairos.activity.service.phase.PhaseService;
import com.kairos.activity.service.shift.ShiftService;
import com.kairos.activity.util.DateUtils;
import com.kairos.activity.util.timeCareShift.GetAllActivitiesResponse;
import com.kairos.activity.util.timeCareShift.TimeCareActivity;
import com.kairos.activity.util.timeCareShift.Transstatus;
import com.kairos.persistence.model.enums.DurationType;
import com.kairos.persistence.model.enums.ActivityStateEnum;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

import static com.kairos.activity.constants.AppConstants.*;
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

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public ActivityTagDTO createActivity(Long countryId, ActivityDTO activityDTO) {
        logger.info(activityDTO.getName());
        Activity activity = activityMongoRepository.
                findByNameIgnoreCaseAndDeletedFalseAndCountryId(activityDTO.getName().trim(), countryId);

        if (Optional.ofNullable(activity).isPresent()) {
            exceptionService.duplicateDataException("exception.duplicateData", "activity", activityDTO.getName());
        }
        activity = buildActivity(activityDTO);
        initializeActivityTabs(activity, countryId);
        save(activity);
        // Fetch tags detail
        List<TagDTO> tags = tagMongoRepository.getTagsById(activityDTO.getTags());
        ActivityTagDTO activityTagDTO = new ActivityTagDTO();
        activityTagDTO.buildActivityTagDTO(activity, tags);

        return activityTagDTO;
    }

    private void initializeActivityTabs(Activity activity, Long countryId) {

        GeneralActivityTab generalActivityTab = new GeneralActivityTab(activity.getName(), activity.getDescription(), "");
        generalActivityTab.setColorPresent(false);
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

        RulesActivityTab rulesActivityTab = new RulesActivityTab(false, false, false,
                false, false, false, false, false, false, null, phaseTemplateValues);
        activity.setRulesActivityTab(rulesActivityTab);

        TimeCalculationActivityTab timeCalculationActivityTab = new TimeCalculationActivityTab(ENTERED_TIMES, 0l, true, LocalTime.of(7, 0), 1d);
        activity.setTimeCalculationActivityTab(timeCalculationActivityTab);

        IndividualPointsActivityTab individualPointsActivityTab = new IndividualPointsActivityTab("addHourValues", 0.0);
        activity.setIndividualPointsActivityTab(individualPointsActivityTab);

        CommunicationActivityTab communicationActivityTab = new CommunicationActivityTab(false, "hours", 1, false);
        activity.setCommunicationActivityTab(communicationActivityTab);

        OptaPlannerSettingActivityTab optaPlannerSettingActivityTab = new OptaPlannerSettingActivityTab(false, false, false,false);
        activity.setOptaPlannerSettingActivityTab(optaPlannerSettingActivityTab);

        CTAAndWTASettingsActivityTab ctaAndWtaSettingsActivityTab = new CTAAndWTASettingsActivityTab(false);
        activity.setCtaAndWtaSettingsActivityTab(ctaAndWtaSettingsActivityTab);

        activity.setNotesActivityTab(new NotesActivityTab());

        SkillActivityTab skillActivityTab = new SkillActivityTab();
        activity.setSkillActivityTab(skillActivityTab);
        LocationActivityTab locationActivityTab = new LocationActivityTab(Collections.EMPTY_LIST,Collections.EMPTY_LIST);
        activity.setLocationActivityTab(locationActivityTab);

    }

    /*public List<ActivityDTO> findAllActivityByCountry(long countryId) {
        return activityMongoRepository.findAllActivityByCountry(countryId);
    }*/

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

    public List<ActivityCategory> findAllActivityCategoriesByCountry(long countryId) {
        return activityCategoryRepository.findByCountryId(countryId);
    }

    public List<ActivityWithCTAWTASettingsDTO> findAllActivityWithCtaWtaSettingByUnit(long unitId) {
        return activityMongoRepository.findAllActivityWithCtaWtaSettingByUnit(unitId);
    }

    public HashMap<Long, HashMap<Long, Long>> getListOfActivityIdsOfUnitByParentIds(List<BigInteger> parentActivityIds, List<Long> unitIds) {
        List<OrganizationActivityDTO> unitActivities = activityMongoRepository.findAllActivityOfUnitsByParentActivity(parentActivityIds, unitIds);
        HashMap<Long, HashMap<Long, Long>> mappedParentUnitActivities = new HashMap<Long, HashMap<Long, Long>>();
        unitActivities.forEach(activityDTO -> {
            HashMap<Long, Long> unitParentActivities = mappedParentUnitActivities.get(activityDTO.getUnitId().longValue());
            if (!Optional.ofNullable(unitParentActivities).isPresent()) {
                mappedParentUnitActivities.put(activityDTO.getUnitId().longValue(), new HashMap<Long, Long>());
                unitParentActivities = mappedParentUnitActivities.get(activityDTO.getUnitId().longValue());
            }
            unitParentActivities.put(activityDTO.getParentId().longValue(), activityDTO.getId().longValue());
        });
        return mappedParentUnitActivities;
    }

    public boolean deleteActivity(BigInteger activityId) {

        Activity activity = activityMongoRepository.findOne(activityId);
        if (!Optional.ofNullable(activity).isPresent()) {
            throw new DataNotFoundByIdException("Invalid activity Id : " + activityId);
        }

        long activityCount = shiftService.countByActivityId(activityId);
        if (activityCount > 0) {
            throw new ActionNotPermittedException("TimeCareActivity type is being used in activities");
        }
        activity.setDeleted(true);
        save(activity);
        return true;
    }


    public ActivityTabsWrapper updateGeneralTab(Long countryId, GeneralActivityTabDTO generalDTO) {
        //check category is available in country
        logger.info(generalDTO.toString());
        ActivityCategory activityCategory = activityCategoryRepository.getByIdAndNonDeleted(generalDTO.getCategoryId());
        if (activityCategory == null) {
            throw new DataNotFoundByIdException("Category Not Available");
        }
        Activity isActivityAlreadyExists = activityMongoRepository.findByNameExcludingCurrentInCountry("^" + generalDTO.getName().trim() + "$", generalDTO.getActivityId(), countryId);
        if (Optional.ofNullable(isActivityAlreadyExists).isPresent()) {
            exceptionService.duplicateDataException("exception.duplicateData", "activity");
        }
        GeneralActivityTab generalTab = generalDTO.buildGeneralActivityTab();
        Activity activity = activityMongoRepository.findOne(new BigInteger(String.valueOf(generalDTO.getActivityId())));
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
        generalTab.setTags(tagMongoRepository.getTagsById(generalDTO.getTags()));
        ActivityTabsWrapper activityTabsWrapper = new ActivityTabsWrapper(generalTab, activityCategories);

        return activityTabsWrapper;
    }


    public ActivityTabsWrapper getGeneralTabOfActivity(BigInteger countryId, BigInteger activityId) {
        List<ActivityCategory> activityCategories = checkCountryAndFindActivityCategory(countryId);
        Activity activity = activityMongoRepository.findOne(activityId);
        if (!Optional.ofNullable(activity).isPresent()) {
            throw new DataNotFoundByIdException("Invalid TimeCareActivity Id : " + activityId);
        }
        GeneralActivityTab generalTab = activity.getGeneralActivityTab();

        logger.info("activity.getTags() ================ > " + activity.getTags());
        generalTab.setTags(tagMongoRepository.getTagsById(activity.getTags()));
        ActivityTabsWrapper activityTabsWrapper = new ActivityTabsWrapper(generalTab, activityCategories);

        return activityTabsWrapper;
    }


    private List<ActivityCategory> checkCountryAndFindActivityCategory(BigInteger countryId) {
        List<ActivityCategory> activityCategories = activityCategoryRepository.findByCountryId(countryId.longValue());
        return activityCategories;
    }


    public ActivityTabsWrapper getBalanceSettingsTabOfActivity(BigInteger activityId, Long countryId) {
        PresenceTypeWithTimeTypeDTO presenceType = organizationRestClient.getPresenceTypeAndTimeTypeByCountry(countryId);
        Activity activity = activityMongoRepository.findOne(activityId);
        if (!Optional.ofNullable(activity).isPresent()) {
            throw new DataNotFoundByIdException("TimeCareActivity not found : " + activityId);
        }
        BalanceSettingsActivityTab balanceSettingsActivityTab = activity.getBalanceSettingsActivityTab();
        ActivityTabsWrapper activityTabsWrapper = new ActivityTabsWrapper(balanceSettingsActivityTab, presenceType);
        activityTabsWrapper.setTimeTypes(timeTypeService.getAllTimeType(balanceSettingsActivityTab.getTimeTypeId(), countryId));
        return activityTabsWrapper;
    }


    public ActivityTabsWrapper updateBalanceTab(BalanceSettingActivityTabDTO balanceDTO) {
        BalanceSettingsActivityTab balanceSettingsTab = balanceDTO.buildBalanceSettingsActivityTab();
        Activity activity = activityMongoRepository.findOne(new BigInteger(String.valueOf(balanceDTO.getActivityId())));
        if (!Optional.ofNullable(activity).isPresent()) {
            throw new DataNotFoundByIdException("TimeCareActivity not found : " + balanceDTO.getActivityId());
        }
        activity.setBalanceSettingsActivityTab(balanceSettingsTab);
        //updating activity category based on time type
        Long countryId = activity.getCountryId();
        if(countryId == null)
            countryId = organizationRestClient.getCountryIdOfOrganization(activity.getUnitId());

        updateActivityCategory(activity, countryId);
        save(activity);
        ActivityTabsWrapper activityTabsWrapper = new ActivityTabsWrapper(balanceSettingsTab);
        activityTabsWrapper.setActivityCategories(activityCategoryRepository.findByCountryId(countryId));

        return activityTabsWrapper;

    }

    public void updateActivityCategory(Activity activity, Long countryId){

        TimeType timeType = timeTypeMongoRepository.findOneById(activity.getBalanceSettingsActivityTab().getTimeTypeId(), countryId);
        if(timeType == null)
            throw new DataNotFoundException("Related Time Type not found");
        ActivityCategory category = activityCategoryRepository.getCategoryByTimeType(countryId, activity.getBalanceSettingsActivityTab().getTimeTypeId());
        if(category == null){
            category = new ActivityCategory(timeType.getLabel(), "", countryId, timeType.getId());
            save(category);
        }
        activity.getGeneralActivityTab().setCategoryId(category.getId());

    }


    public ActivityTabsWrapper updateTimeCalculationTabOfActivity(TimeCalculationActivityDTO timeCalculationActivityDTO) {

        TimeCalculationActivityTab timeCalculationActivityTab = timeCalculationActivityDTO.buildTimeCalculationActivityTab();

        Activity activity = activityMongoRepository.findOne(new BigInteger(String.valueOf(timeCalculationActivityDTO.getActivityId())));
        if (!Optional.ofNullable(activity).isPresent()) {
            throw new DataNotFoundByIdException("TimeCareActivity not found by Id: " + timeCalculationActivityDTO.getActivityId());
        }
        activity.setTimeCalculationActivityTab(timeCalculationActivityTab);
        save(activity);
        ActivityTabsWrapper activityTabsWrapper = new ActivityTabsWrapper(timeCalculationActivityTab);
        return activityTabsWrapper;

    }

    public ActivityTabsWrapper updateCompositeShiftTabOfActivity(CompositeShiftActivityDTO compositeShiftActivityDTO) {


        Activity activity = activityMongoRepository.findOne(new BigInteger(String.valueOf(compositeShiftActivityDTO.getActivityId())));
        if (!Optional.ofNullable(activity).isPresent()) {
            exceptionService.dataNotFoundByIdException("exception.dataNotFound", "activity", compositeShiftActivityDTO.getActivityId());
        }
        Set<BigInteger> activityIds = compositeShiftActivityDTO.getActivityList();
        Integer activityMatchedCount = activityMongoRepository.findAllActivityByIds(activityIds);

        if (activityMatchedCount != activityIds.size()) {
            throw new IllegalArgumentException("Mismatched Ids  for " + compositeShiftActivityDTO.getActivityId());
        }

        activity.setCompositeActivities(compositeShiftActivityDTO.getActivityList());
        save(activity);
        ActivityTabsWrapper activityTabsWrapper = new ActivityTabsWrapper(compositeShiftActivityDTO.getActivityList());
        return activityTabsWrapper;

    }

    public ActivityTabsWrapper getTimeCalculationTabOfActivity(BigInteger activityId, Long countryId) {
        List<DayType> dayTypes = organizationRestClient.getDayTypesByCountryId(countryId);
        Activity activity = activityMongoRepository.findOne(activityId);
        TimeCalculationActivityTab timeCalculationActivityTab = activity.getTimeCalculationActivityTab();
        ActivityTabsWrapper activityTabsWrapper = new ActivityTabsWrapper(timeCalculationActivityTab, dayTypes);

        return activityTabsWrapper;
    }

    public List<ActivityDTO> getCompositeShiftTabOfActivity(BigInteger activityId) {
        Activity activity = activityMongoRepository.findOne(activityId);
        if (activity == null) {
            exceptionService.dataNotFoundByIdException("exception.dataNotFound", "activity", activityId);
        }
        Set<BigInteger> compositeShiftIds = new HashSet<>();
        if (activity.getCompositeActivities() != null) {
            compositeShiftIds = Optional.ofNullable(activity.getCompositeActivities()).orElse(Collections.EMPTY_SET);
        }
        List<ActivityDTO> activityDTOS = activityMongoRepository.findAllActivitiesWithDataByIds(compositeShiftIds);
        return activityDTOS;


    }

    public ActivityTabsWrapper updateIndividualPointsTab(IndividualPointsActivityTabDTO individualPointsDTO) {
        IndividualPointsActivityTab individualPointsActivityTab = individualPointsDTO.buildIndividualPointsActivityTab();
        Activity activity = activityMongoRepository.findOne(new BigInteger(String.valueOf(individualPointsDTO.getActivityId())));
        if (!Optional.ofNullable(activity).isPresent()) {
            throw new DataNotFoundByIdException("Invalid ActivityId : " + individualPointsDTO.getActivityId());
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
        RulesActivityTab rulesActivityTab = rulesActivityDTO.buildRulesActivityTab();
        Activity activity = activityMongoRepository.findOne(new BigInteger(String.valueOf(rulesActivityDTO.getActivityId())));
        if (!Optional.ofNullable(activity).isPresent()) {
            throw new DataNotFoundByIdException("Invalid ActivityId : " + rulesActivityDTO.getActivityId());
        }
        activity.setRulesActivityTab(rulesActivityTab);
        save(activity);
        ActivityTabsWrapper activityTabsWrapper = new ActivityTabsWrapper(rulesActivityTab);

        return activityTabsWrapper;
    }

    public ActivityTabsWrapper getRulesTabOfActivity(BigInteger activityId, Long countryId) {
        List<DayType> dayTypes = organizationRestClient.getDayTypesByCountryId(countryId);
        Activity activity = activityMongoRepository.findOne(activityId);

        RulesActivityTab rulesActivityTab = activity.getRulesActivityTab();

        ActivityTabsWrapper activityTabsWrapper = new ActivityTabsWrapper(rulesActivityTab, dayTypes);
        return activityTabsWrapper;
    }

    public ActivityTabsWrapper updateNotesTabOfActivity(NotesActivityDTO notesActivityDTO) {
        NotesActivityTab notesActivityTab = notesActivityDTO.buildNotesActivityTab();
        Activity activity = activityMongoRepository.findOne(new BigInteger(String.valueOf(notesActivityDTO.getActivityId())));
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
        ActivityTabsWrapper activityTabsWrapper = new ActivityTabsWrapper(notesActivityTab);
        return activityTabsWrapper;
    }

    public ActivityTabsWrapper getNotesTabOfActivity(BigInteger activityId) {


        Activity activity = activityMongoRepository.findOne(activityId);
        ActivityTabsWrapper activityTabsWrapper = new ActivityTabsWrapper(activity.getNotesActivityTab());
        return activityTabsWrapper;
    }


    public ActivityTabsWrapper updateCommunicationTabOfActivity(CommunicationActivityDTO communicationActivityDTO) {
        CommunicationActivityTab communicationActivityTab = communicationActivityDTO.buildSMSReminderActivityTab();
        Activity activity = activityMongoRepository.findOne(new BigInteger(String.valueOf(communicationActivityDTO.getActivityId())));
        if (!Optional.ofNullable(activity).isPresent()) {
            throw new DataNotFoundByIdException("Invalid ActivityId : " + communicationActivityDTO.getActivityId());
        }
        activity.setCommunicationActivityTab(communicationActivityTab);
        save(activity);
        ActivityTabsWrapper activityTabsWrapper = new ActivityTabsWrapper(communicationActivityTab);
        return activityTabsWrapper;
    }

    public ActivityTabsWrapper getCommunicationTabOfActivity(BigInteger activityId) {


        Activity activity = activityMongoRepository.findOne(activityId);
        if (!Optional.ofNullable(activity).isPresent()) {
            throw new DataNotFoundByIdException("Invalid ActivityId : " + activityId);
        }
        ActivityTabsWrapper activityTabsWrapper = new ActivityTabsWrapper(activity.getCommunicationActivityTab());
        return activityTabsWrapper;
    }
    // BONUS

    public ActivityTabsWrapper updateBonusTabOfActivity(BonusActivityDTO bonusActivityDTO) {
        BonusActivityTab bonusActivityTab = bonusActivityDTO.buildBonusActivityTab();
        Activity activity = activityMongoRepository.findOne(new BigInteger(String.valueOf(bonusActivityDTO.getActivityId())));
        if (!Optional.ofNullable(activity).isPresent()) {
            throw new DataNotFoundByIdException("Invalid ActivityId : " + bonusActivityDTO.getActivityId());
        }
        activity.setBonusActivityTab(bonusActivityTab);
        save(activity);
        ActivityTabsWrapper activityTabsWrapper = new ActivityTabsWrapper(bonusActivityTab);
        return activityTabsWrapper;
    }

    public ActivityTabsWrapper getBonusTabOfActivity(BigInteger activityId) {
        Activity activity = activityMongoRepository.findOne(activityId);
        if (!Optional.ofNullable(activity).isPresent()) {
            throw new DataNotFoundByIdException("Invalid ActivityId : " + activityId);
        }
        ActivityTabsWrapper activityTabsWrapper = new ActivityTabsWrapper(activity.getBonusActivityTab());
        return activityTabsWrapper;
    }

    // skills
    public ActivityTabsWrapper updateSkillTabOfActivity(SkillActivityDTO skillActivityDTO) {
        Activity activity = activityMongoRepository.findOne(new BigInteger(skillActivityDTO.getActivityId().toString()));
        if (!Optional.ofNullable(activity).isPresent()) {
            throw new DataNotFoundByIdException("Invalid ActivityId" + skillActivityDTO.getActivityId());
        }
        SkillActivityTab skillActivityTab = skillActivityDTO.buildSkillActivityTab();


        activity.setSkillActivityTab(skillActivityTab);
        save(activity);
        ActivityTabsWrapper activityTabsWrapper = new ActivityTabsWrapper(skillActivityTab);
        return activityTabsWrapper;
    }


    public ActivityTabsWrapper getSkillTabOfActivity(BigInteger activityId) {
        Activity activity = activityMongoRepository.findOne(activityId);
        ActivityTabsWrapper activityTabsWrapper = new ActivityTabsWrapper(activity.getSkillActivityTab());
        return activityTabsWrapper;

    }

    // organization Mapping
    public void updateOrgMappingDetailOfActivity(OrganizationMappingActivityDTO organizationMappingActivityDTO) {
        Activity activity = activityMongoRepository.findOne(new BigInteger(organizationMappingActivityDTO.getActivityId().toString()));
        if (!Optional.ofNullable(activity).isPresent()) {
            exceptionService.dataNotFoundByIdException("exception.dataNotFound", "activity", organizationMappingActivityDTO.getActivityId());
        }

        boolean isSuccess = organizationRestClient.verifyOrganizationExpertizeAndRegions(organizationMappingActivityDTO);
        if (!isSuccess) {
            throw new DataNotFoundException("InCorrect parameters");
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
        OrganizationTypeAndSubTypeDTO organizationTypeAndSubTypeDTO = organizationRestClient.getOrganizationTypeAndSubTypeByUnitId(unitId, type);

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

    public ActivityTabsWrapper updateOptaPlannerSettingsTabOfActivity(OptaPlannerSettingActivityTabDTO optaPlannerSettingActivityTabDTO) {
        OptaPlannerSettingActivityTab optaPlannerSettingActivityTab = optaPlannerSettingActivityTabDTO.buildOptaPlannerSettingTab();
        Activity activity = activityMongoRepository.findOne(new BigInteger(String.valueOf(optaPlannerSettingActivityTabDTO.getActivityId())));
        if (!Optional.ofNullable(activity).isPresent()) {
            exceptionService.dataNotFoundByIdException("exception.dataNotFound", "activity", optaPlannerSettingActivityTabDTO.getActivityId());
        }
        activity.setOptaPlannerSettingActivityTab(optaPlannerSettingActivityTab);
        save(activity);
        ActivityTabsWrapper activityTabsWrapper = new ActivityTabsWrapper(optaPlannerSettingActivityTab);
        return activityTabsWrapper;

    }

    public ActivityTabsWrapper getOptaPlannerSettingsTabOfActivity(BigInteger activityId) {
        Activity activity = activityMongoRepository.findOne(activityId);
        ActivityTabsWrapper activityTabsWrapper = new ActivityTabsWrapper(activity.getOptaPlannerSettingActivityTab());
        return activityTabsWrapper;
    }

    public ActivityTabsWrapper getCtaAndWtaSettingsTabOfActivity(BigInteger activityId) {
        Activity activity = activityMongoRepository.findOne(activityId);
        ActivityTabsWrapper activityTabsWrapper = new ActivityTabsWrapper(activity.getCtaAndWtaSettingsActivityTab());
        return activityTabsWrapper;
    }

    public ActivityTabsWrapper updateCtaAndWtaSettingsTabOfActivity(CTAAndWTASettingsActivityTabDTO ctaAndWtaSettingsActivityTabDTO) {
        CTAAndWTASettingsActivityTab ctaAndWtaSettingsActivityTab = ctaAndWtaSettingsActivityTabDTO.buildCTAAndWTASettingActivityTab();
        Activity activity = activityMongoRepository.findOne(new BigInteger(String.valueOf(ctaAndWtaSettingsActivityTabDTO.getActivityId())));
        if (!Optional.ofNullable(activity).isPresent()) {
            exceptionService.dataNotFoundByIdException("exception.dataNotFound", "activity", ctaAndWtaSettingsActivityTabDTO.getActivityId());
        }
        activity.setCtaAndWtaSettingsActivityTab(ctaAndWtaSettingsActivityTab);
        save(activity);
        ActivityTabsWrapper activityTabsWrapper = new ActivityTabsWrapper(ctaAndWtaSettingsActivityTab);
        return activityTabsWrapper;

    }

    public PhaseActivityDTO getActivityAndPhaseByUnitId(long unitId, String type) {
        List<DayType> dayTypes = organizationRestClient.getDayTypes(unitId);
        LocalDate date = LocalDate.now();
        int year = date.getYear();
        TemporalField weekOfWeekBasedYear = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear();
        int currentWeek = date.get(weekOfWeekBasedYear);
        int currentDayOfWeek = date.getDayOfWeek().getValue();
        PhaseActivityDTO phaseActivityDTO = new PhaseActivityDTO();
        phaseActivityDTO.setDayTypes(dayTypes);
        // getting all phases by countryId as NO unit phase is configured

//        List<PhaseDTO> phaseDTOs = phaseService.getPhasesByCountryId(countryId);
        List<PhaseDTO> phaseDTOs = phaseService.getApplicablePhasesByOrganizationId(unitId);

        phaseActivityDTO.setApplicablePhases(phaseDTOs);
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

        phaseActivityDTO.setActivities(activityMongoRepository.findAllActivityByUnitIdWithCompositeActivities(unitId));

        phaseActivityDTO.setPhases(phaseWeeklyDTOS);
        return phaseActivityDTO;
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
            throw new DataNotFoundByIdException("Invalid Activity Id : " + activityId);
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

        OrganizationDTO organizationDTO = organizationRestClient.getOrganization(unitId);
        if (organizationDTO == null) {
            throw new DataNotFoundByIdException("Incorrect Organization id");
        }
        ActivityCategory activityCategory = activityCategoryRepository.getCategoryByNameAndCountryAndDeleted("NONE", countryId, false);
        if (activityCategory == null) {
            activityCategory = new ActivityCategory("NONE", "", countryId, null);
            save(activityCategory);
        }
        List<Long> orgTypes = organizationDTO.getOrganizationTypes().stream().map(organizationTypeDTO -> organizationTypeDTO.getId()).collect(Collectors.toList());
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
            activity.setOrganizationTypes(orgTypes);
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
            balanceSettingsActivityTab.setAddDayTo(timeCareActivity.getBalanceDayType().replace(" ", "_"));
            activity.setBalanceSettingsActivityTab(balanceSettingsActivityTab);

            //rules activity tab
            RulesActivityTab rulesActivityTab = Optional.ofNullable(activity.getRulesActivityTab()).isPresent() ? activity.getRulesActivityTab() :
                    new RulesActivityTab();

            rulesActivityTab.setEligibleAgainstTimeRules(timeCareActivity.getUseTimeRules());
            rulesActivityTab.setEligibleForStaffingLevel(timeCareActivity.getIsStaffing());
            List<PhaseTemplateValue> phaseTemplateValues = getPhaseForRulesActivity(phases);
            rulesActivityTab.setEligibleForSchedules(phaseTemplateValues);
            activity.setRulesActivityTab(rulesActivityTab);

            // location settings
            LocationActivityTab locationActivityTab = new LocationActivityTab(Collections.EMPTY_LIST,Collections.EMPTY_LIST);
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

    private List<Activity> mapActivitiesInOrganization(List<Activity> countryActivities, Long unitId, List<String> externalIds) {

        List<Activity> unitActivities = activityMongoRepository.findByUnitIdAndExternalIdIn(unitId, externalIds);
        List<PhaseDTO> phases = phaseService.getPhasesByUnit(unitId);
        List<Activity> organizationActivities = new ArrayList<>();
        for (Activity countryActivity : countryActivities) {
            Optional<Activity> result = unitActivities.stream().filter(unitActivity -> unitActivity.getExternalId().equals(countryActivity.getExternalId())).findFirst();
            if (!result.isPresent()) {
                Activity activity = SerializationUtils.clone(countryActivity);
                List<PhaseTemplateValue> phaseTemplateValues = getPhaseForRulesActivity(phases);
                activity.getRulesActivityTab().setEligibleForSchedules(phaseTemplateValues);

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
        return organizationActivities;
    }

    private List<PhaseTemplateValue> getPhaseForRulesActivity(List<PhaseDTO> phases) {
        List<PhaseTemplateValue> phaseTemplateValues = new ArrayList<>();
        for (PhaseDTO phaseDTO : phases) {
            PhaseTemplateValue phaseTemplateValue = new PhaseTemplateValue();
            phaseTemplateValue.setPhaseId(phaseDTO.getId());
            phaseTemplateValue.setName(phaseDTO.getName());
            phaseTemplateValue.setDescription(phaseDTO.getDescription());
            phaseTemplateValue.setEligibleForManagement(false);
            phaseTemplateValue.setEligibleForStaff(false);
            phaseTemplateValues.add(phaseTemplateValue);
        }
        return phaseTemplateValues;
    }

    public NotesActivityTab addDocumentInNotesTab(BigInteger activityId, MultipartFile file) throws IOException {
        Activity activity = activityMongoRepository.findOne(activityId);
        if (!Optional.ofNullable(activity).isPresent()) {
            throw new DataNotFoundByIdException("Invalid ActivityId : " + activityId);
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
            throw new DataNotFoundByIdException("Invalid ActivityId : " + activityId);
        }
        if (activity.getState().equals(ActivityStateEnum.PUBLISHED) || activity.getState().equals(ActivityStateEnum.LIVE)) {
            throw new ActionNotPermittedException("activity is already published :" + activityId);
        }
        activity.setState(ActivityStateEnum.PUBLISHED);
        save(activity);
        return true;
    }

    public ActivityDTO copyActivityDetails(Long countryId, BigInteger activityId, ActivityDTO activityDTO) {
        Activity activity = activityMongoRepository.
                findByNameIgnoreCaseAndDeletedFalseAndCountryId(activityDTO.getName().trim(), countryId);
        if (Optional.ofNullable(activity).isPresent()) {
            logger.error("ActivityName already exist " + activityDTO.getName());
            throw new DuplicateDataException("ActivityName already exist : " + activityDTO.getName());
        }
        Optional<Activity> activityFromDatabase = activityMongoRepository.findById(activityId);
        if (!activityFromDatabase.isPresent() || activityFromDatabase.get().isDeleted() || !countryId.equals(activityFromDatabase.get().getCountryId())) {
            throw new DataNotFoundByIdException("Invalid ActivityId:" + activityId);
        }

        Activity activityCopied = new Activity();
        Activity.copyProperties(activityFromDatabase.get(), activityCopied, "id", "organizationTypes", "organizationSubTypes");
        activityCopied.setName(activityDTO.getName().trim());
        activityCopied.getGeneralActivityTab().setName(activityDTO.getName().trim());
        activityCopied.setState(ActivityStateEnum.DRAFT);
        save(activityCopied);
        activityDTO.setId(activityCopied.getId());
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
            throw new DataNotFoundByIdException("Invalid ActivityId : " + locationActivityTabDTO.getActivityId());
        }
        LocationActivityTab locationActivityTab = new LocationActivityTab(locationActivityTabDTO.getCanBeStartAt(), locationActivityTabDTO.getCanBeEndAt());
        activity.setLocationActivityTab(locationActivityTab);
        save(activity);
        ActivityTabsWrapper activityTabsWrapper = new ActivityTabsWrapper(locationActivityTab);
        return activityTabsWrapper;

    }

    public Activity buildActivity(ActivityDTO activityDTO) {
        List<BigInteger> tags = new ArrayList<>();
        for (BigInteger tag : activityDTO.getTags()) {
            tags.add(tag);
        }
        Activity activity = new Activity(activityDTO.getName(), activityDTO.getDescription(), tags);
        return activity;
    }

    public Object initialOptaplannerSync(Long organisationId, Long unitId) {
        List<Activity> activities=activityMongoRepository.findAllActivitiesByUnitId(unitId);
        plannerSyncService.publishActivities(unitId,activities,IntegrationOperation.CREATE);
        List<StaffingLevel> staffingLevels= staffingLevelMongoRepository.findByUnitIdAndCurrentDateBetweenAndDeletedFalse(unitId,DateUtils.convertLocalDateToDate(LocalDate.now().minusMonths(1)),DateUtils.convertLocalDateToDate(LocalDate.now().plusMonths(1)));
        List<StaffingLevelDTO> staffingLevelDTOS= new ArrayList<>();
        for(StaffingLevel staffingLevel:staffingLevels){
            StaffingLevelDTO staffingLevelDTO= new StaffingLevelDTO(staffingLevel.getId(),staffingLevel.getPhaseId(),staffingLevel.getCurrentDate(),staffingLevel.getWeekCount(),staffingLevel.getStaffingLevelSetting(),staffingLevel.getPresenceStaffingLevelInterval(),null);
            staffingLevelDTOS.add(staffingLevelDTO);
        }
        plannerSyncService.publishStaffingLevels(unitId,staffingLevelDTOS,IntegrationOperation.CREATE);
        return null;

    }
}
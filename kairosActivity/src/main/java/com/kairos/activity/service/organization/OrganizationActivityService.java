package com.kairos.activity.service.organization;

import com.kairos.activity.client.OrganizationRestClient;
import com.kairos.activity.client.dto.DayType;
import com.kairos.activity.client.dto.activityType.PresenceTypeWithTimeTypeDTO;
import com.kairos.activity.custom_exception.DataNotFoundByIdException;
import com.kairos.activity.custom_exception.DuplicateDataException;
import com.kairos.activity.persistence.model.activity.Activity;
import com.kairos.activity.persistence.model.activity.tabs.*;
import com.kairos.activity.persistence.repository.activity.ActivityCategoryRepository;
import com.kairos.activity.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.activity.persistence.repository.tag.TagMongoRepository;

import com.kairos.activity.response.dto.ActivityDTO;
import com.kairos.activity.response.dto.ActivityWithUnitIdDTO;

import com.kairos.activity.response.dto.activity.ActivityTabsWrapper;
import com.kairos.activity.response.dto.activity.ActivityTagDTO;
import com.kairos.activity.response.dto.activity.ActivityWithSelectedDTO;
import com.kairos.activity.response.dto.activity.GeneralActivityTabDTO;
import com.kairos.activity.service.MongoBaseService;
import com.kairos.activity.service.activity.ActivityService;
import com.kairos.activity.service.activity.TimeTypeService;
import com.kairos.persistence.model.enums.ActivityStateEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    public HashMap copyActivity(Long unitId, BigInteger activityId, boolean checked) {
        logger.info("activityId,{}", activityId);
        Activity activity = activityMongoRepository.findOne(activityId);
        if (!Optional.ofNullable(activity).isPresent()) {
            throw new DataNotFoundByIdException("Invalid Activity Id : " + activityId);
        }
        if (checked) {
            Activity activityCopied = copyAllActivitySettingsInUnit(activity, unitId);
            save(activityCopied);

            if (!activity.getState().equals(ActivityStateEnum.LIVE)) {
                activity.setState(ActivityStateEnum.LIVE);
                save(activity);
            }
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

    public List<Activity> getActivityByUnitId(Long unitId, String type) {
        List<Activity> activities = activityMongoRepository.findByDeletedFalseAndUnitId(unitId);
        return activities;

    }

    public Map<String, Object> getAllActivityByUnitAndDeleted(Long unitId) {
        Map<String, Object> response = new HashMap<>();
        List<ActivityTagDTO> activities = activityMongoRepository.findAllActivityByUnitIdAndDeleted(unitId, false);
        List<ActivityCategory> activityCategories = activityCategoryRepository.findByDeletedFalse();
        response.put("activities", activities);
        response.put("activityCategories", activityCategories);
        return response;
    }

    public ActivityTabsWrapper getGeneralTabOfActivity(BigInteger activityId) {
        List<ActivityCategory> activityCategories = activityCategoryRepository.findByDeletedFalse();
        Activity activity = activityMongoRepository.findOne(activityId);
        if (!Optional.ofNullable(activity).isPresent()) {
            throw new DataNotFoundByIdException("Invalid Activity Id : " + activityId);
        }
        GeneralActivityTab generalTab = activity.getGeneralActivityTab();
        logger.info("activity.getTags() ================ > " + activity.getTags());
        generalTab.setTags(tagMongoRepository.getTagsById(activity.getTags()));
        logger.info("activityId " + activityId);
        ActivityTabsWrapper activityTabsWrapper = new ActivityTabsWrapper(generalTab, activityId, activityCategories);
        return activityTabsWrapper;
    }

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
        Activity activity = activityMongoRepository.findOne(generalDTO.getActivityId());
        ActivityCategory activityCategory = activityCategoryRepository.getCategoryByName(generalDTO.getCategoryName());
        if (activityCategory != null) {

            generalDTO.setCategoryId(activityCategory.getId());
        } else {

            ActivityCategory category = new ActivityCategory(generalDTO.getCategoryName(), "", unitId);
            category.setUnitId(unitId);
            category.setCountryId(null);
            save(category);
            if (category == null) {
                throw new DataNotFoundByIdException("Category can't be created!!");
            }
            generalDTO.setCategoryId(category.getId());
        }

        Activity IsActivityExists = activityMongoRepository.findByNameExcludingCurrentInUnit(generalDTO.getName(), generalDTO.getActivityId(), activity.getUnitId());
        if (Optional.ofNullable(IsActivityExists).isPresent()) {
            throw new DuplicateDataException("Name already is use " + generalDTO.getName());
        }

        GeneralActivityTab generalTab = generalDTO.buildGeneralActivityTab();
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
        save(activity);
        generalTab.setTags(tagMongoRepository.getTagsById(generalDTO.getTags()));
        List<ActivityCategory> activityCategories = activityCategoryRepository.findByDeletedFalse();
        ActivityTabsWrapper activityTabsWrapper = new ActivityTabsWrapper(generalTab, generalDTO.getActivityId(), activityCategories);
        return activityTabsWrapper;
    }

    public ActivityTabsWrapper getBalanceSettingsTabOfType(BigInteger activityId, Long unitId) {
        PresenceTypeWithTimeTypeDTO presenceType = organizationRestClient.getPresenceTypeAndTimeType(unitId);
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
        Activity activity = activityMongoRepository.
                findByNameIgnoreCaseAndDeletedFalseAndUnitId(activityDTO.getName().trim(), unitId);
        if (Optional.ofNullable(activity).isPresent()) {
            logger.error("ActivityName already exist" + activityDTO.getName());
            throw new DuplicateDataException("ActivityName already exist : " + activityDTO.getName());
        }
        Optional<Activity> activityFromDatabase = activityMongoRepository.findById(activityId);
        if (!activityFromDatabase.isPresent() || activityFromDatabase.get().isDeleted() || !unitId.equals(activityFromDatabase.get().getUnitId())) {
            throw new DataNotFoundByIdException("Invalid ActivityId:" + activityId);
        }
        Activity activityCopied = copyAllActivitySettingsInUnit(activityFromDatabase.get(), unitId);
        activityCopied.setName(activityDTO.getName().trim());
        save(activityCopied);
        activityDTO.setId(activityCopied.getId());
        return activityDTO;
    }

}

package com.kairos.service.activity;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.activity.ActivityDTO;
import com.kairos.dto.activity.activity.activity_tabs.PhaseTemplateValue;
import com.kairos.dto.activity.glide_time.GlideTimeSettingsDTO;
import com.kairos.dto.activity.phase.PhaseDTO;
import com.kairos.dto.user.organization.OrganizationDTO;
import com.kairos.dto.user.organization.OrganizationTypeDTO;
import com.kairos.dto.user.organization.skill.Skill;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.activity.TimeType;
import com.kairos.persistence.model.activity.tabs.ActivityCategory;
import com.kairos.persistence.model.activity.tabs.ActivityGeneralSettings;
import com.kairos.persistence.repository.activity.ActivityCategoryRepository;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.time_type.TimeTypeMongoRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.glide_time.GlideTimeSettingsService;
import com.kairos.service.phase.PhaseService;
import com.kairos.utils.external_plateform_shift.GetAllActivitiesResponse;
import com.kairos.utils.external_plateform_shift.TimeCareActivity;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.ObjectUtils.isNull;
import static com.kairos.constants.ActivityMessagesConstants.*;
import static com.kairos.service.activity.ActivityUtil.getPhaseForRulesActivity;
import static com.kairos.service.activity.ActivityUtil.initializeTimeCareActivities;

@Service
public class ActivityHelperService {

    @Inject
    private ActivityCategoryRepository activityCategoryRepository;
    @Inject private PhaseService phaseService;
    @Inject private ExceptionService exceptionService;
    @Inject private GlideTimeSettingsService glideTimeSettingsService;
    @Inject private ActivityMongoRepository activityMongoRepository;
    @Inject private UserIntegrationService userIntegrationService;
    @Inject private TimeTypeMongoRepository timeTypeMongoRepository;

    public void initializeActivitySettings(Activity activity, Long countryId, ActivityDTO activityDTO) {
        ActivityGeneralSettings activityGeneralSettings = new ActivityGeneralSettings(activity.getName(), activity.getDescription(), "");
        activityGeneralSettings.setColorPresent(false);
        activityGeneralSettings.setStartDate(activityDTO.getStartDate());
        activityGeneralSettings.setEndDate(activityDTO.getEndDate());
        activity.setCountryId(countryId);
        ActivityCategory activityCategory = activityCategoryRepository.getCategoryByNameAndCountryAndDeleted("NONE", countryId, false);
        if (activityCategory != null) {
            activityGeneralSettings.setCategoryId(activityCategory.getId());
        } else {
            ActivityCategory category = new ActivityCategory("NONE", "", countryId, null);
            activityCategoryRepository.save(category);
            activityGeneralSettings.setCategoryId(category.getId());
        }
        activity.setActivityGeneralSettings(activityGeneralSettings);
        List<PhaseDTO> phases = phaseService.getPhasesByCountryId(countryId);
        if (CollectionUtils.isEmpty(phases)) {
            exceptionService.actionNotPermittedException(MESSAGE_COUNTRY_PHASE_NOTFOUND);
        }
        List<PhaseTemplateValue> phaseTemplateValues = getPhaseForRulesActivity(phases);
        GlideTimeSettingsDTO glideTimeSettingsDTO = glideTimeSettingsService.getGlideTimeSettings(countryId);
        if (!Optional.ofNullable(glideTimeSettingsDTO).isPresent()) {
            exceptionService.actionNotPermittedException(ERROR_GLIDETIME_NOTFOUND_COUNTRY);
        }
        ActivityUtil.initializeActivitySettings(activity, phaseTemplateValues, glideTimeSettingsDTO);
    }

    public List<Activity> createActivitiesFromTimeCare(GetAllActivitiesResponse getAllActivitiesResponse, Long unitId, Long countryId, BigInteger presenceTimeTypeId, BigInteger absenceTimeTypeId) {
        List<TimeCareActivity> timeCareActivities = getAllActivitiesResponse.getGetAllActivitiesResult();
        List<String> externalIdsOfAllActivities = timeCareActivities.stream().map(TimeCareActivity::getId).collect(Collectors.toList());
        List<Activity> countryActivities = createActivatesForCountryFromTimeCare(timeCareActivities, unitId, countryId, externalIdsOfAllActivities, presenceTimeTypeId, absenceTimeTypeId);
        mapActivitiesInOrganization(countryActivities, unitId, externalIdsOfAllActivities);
        return countryActivities;
    }

    private void mapActivitiesInOrganization(List<Activity> countryActivities, Long unitId, List<String> externalIds) {
        List<Activity> unitActivities = activityMongoRepository.findByUnitIdAndExternalIdInAndDeletedFalse(unitId, externalIds);
        List<Activity> organizationActivities = new ArrayList<>();
        for (Activity countryActivity : countryActivities) {
            Optional<Activity> result = unitActivities.stream().filter(unitActivity -> unitActivity.getExternalId().equals(countryActivity.getExternalId())).findFirst();
            if (!result.isPresent()) {
                Activity activity = ObjectMapperUtils.copyPropertiesByMapper(countryActivity,Activity.class);
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
            activityMongoRepository.saveEntities(organizationActivities);
        }
    }

    private List<Activity> createActivatesForCountryFromTimeCare(List<TimeCareActivity> timeCareActivities, Long unitId, Long countryId, List<String> externalIdsOfAllActivities, BigInteger presenceTimeTypeId, BigInteger absenceTimeTypeId) {
        OrganizationDTO organizationDTO = userIntegrationService.getOrganizationDTO(unitId);
        if (isNull(organizationDTO)) {
            exceptionService.dataNotFoundByIdException(MESSAGE_ORGANIZATION_ID);
        }
        ActivityCategory activityCategory = activityCategoryRepository.getCategoryByNameAndCountryAndDeleted("NONE", countryId, false);
        if (activityCategory == null) {
            activityCategory = new ActivityCategory("NONE", "", countryId, null);
            activityCategoryRepository.save(activityCategory);
        }
        Long orgType = organizationDTO.getOrganizationType().getId();
        List<Long> orgSubTypes = organizationDTO.getOrganizationSubTypes().stream().map(OrganizationTypeDTO::getId).collect(Collectors.toList());
        Set<String> skillsOfAllTimeCareActivity = timeCareActivities.stream().flatMap(timeCareActivity -> timeCareActivity.getArrayOfSkill().stream()).collect(Collectors.toSet());
        List<Skill> skills = userIntegrationService.getSkillsByName(skillsOfAllTimeCareActivity, countryId);
        List<Activity> activitiesByExternalIds = activityMongoRepository.findByExternalIdIn(externalIdsOfAllActivities);
        List<PhaseDTO> phases = phaseService.getPhasesByCountryId(countryId);
        GlideTimeSettingsDTO glideTimeSettingsDTO = glideTimeSettingsService.getGlideTimeSettings(countryId);
        List<Activity> activities = getActivitiesByTimeCareActivity(timeCareActivities, countryId, presenceTimeTypeId, absenceTimeTypeId, activityCategory, orgType, orgSubTypes, skills, activitiesByExternalIds, phases, glideTimeSettingsDTO);
        activityMongoRepository.saveEntities(activities);
        return activities;
    }

    private List<Activity> getActivitiesByTimeCareActivity(List<TimeCareActivity> timeCareActivities, Long countryId, BigInteger presenceTimeTypeId, BigInteger absenceTimeTypeId, ActivityCategory activityCategory, Long orgType, List<Long> orgSubTypes, List<Skill> skills, List<Activity> activitiesByExternalIds, List<PhaseDTO> phases, GlideTimeSettingsDTO glideTimeSettingsDTO) {
        List<Activity> activities = new ArrayList<>(timeCareActivities.size());
        for (TimeCareActivity timeCareActivity : timeCareActivities) {
            Activity activity = initializeTimeCareActivities(timeCareActivity, orgType, orgSubTypes, countryId,
                    glideTimeSettingsDTO, phases, activitiesByExternalIds, activityCategory, skills, presenceTimeTypeId, absenceTimeTypeId);
            TimeType timeType = timeTypeMongoRepository.findOneById(activity.getActivityBalanceSettings().getTimeTypeId());
            if (!Optional.ofNullable(timeType).isPresent()) {
                exceptionService.dataNotFoundByIdException(MESSAGE_ACTIVITY_TIMETYPE_NOTFOUND);
            }
            activity.getActivityBalanceSettings().setTimeType(timeType.getSecondLevelType());
            activity.getActivityBalanceSettings().setPriorityFor(timeType.getPriorityFor());
            activity.getActivityBalanceSettings().setTimeTypes(timeType.getTimeTypes());
            activities.add(activity);
        }
        return activities;
    }
}

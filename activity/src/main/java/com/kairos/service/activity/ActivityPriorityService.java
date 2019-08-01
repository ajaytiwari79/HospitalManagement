package com.kairos.service.activity;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.activity.ActivityPriorityDTO;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.activity.ActivityPriority;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.activity.ActivityPriorityMongoRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.exception.ExceptionService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.constants.ActivityMessagesConstants.*;

@Service
public class ActivityPriorityService {

    @Inject
    private ActivityPriorityMongoRepository activityPriorityMongoRepository;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private ActivityMongoRepository activityMongoRepository;
    @Inject
    private UserIntegrationService userIntegrationService;


    public ActivityPriorityDTO createActivityPriorityAtCountry(Long countryId, ActivityPriorityDTO activityPriorityDTO) {
        boolean existByName = activityPriorityMongoRepository.existsByNameAndCountryIdAndNotEqualToId(activityPriorityDTO.getName(), activityPriorityDTO.getColorCode(), null, countryId);
        if (existByName) {
            exceptionService.actionNotPermittedException(ERROR_NAME_DUPLICATE, activityPriorityDTO.getName());
        }
        int activityPriorityCount = activityPriorityMongoRepository.getActivityPriorityCountAtCountry(countryId);
        if ((activityPriorityCount + 1) != activityPriorityDTO.getSequence()) {
            exceptionService.actionNotPermittedException("message.activity.priority.sequence");
        }
        ActivityPriority activityPriority = activityPriorityMongoRepository.findBySequenceAndCountryId(activityPriorityDTO.getSequence(), countryId);
        if (isNotNull(activityPriority)) {
            exceptionService.actionNotPermittedException("message.activity.priority.same.sequence");
        }
        activityPriority = ObjectMapperUtils.copyPropertiesByMapper(activityPriorityDTO, ActivityPriority.class);
        activityPriority.setCountryId(countryId);
        activityPriorityMongoRepository.save(activityPriority);
        activityPriorityDTO.setId(activityPriority.getId());
        createActivityPriorityAtOrganizations(activityPriority, null);
        return activityPriorityDTO;
    }

    public boolean createActivityPriorityForNewOrganization(Long unitId, Long countryId) {
        List<ActivityPriorityDTO> activityPriorityDTOs = activityPriorityMongoRepository.findAllCountryId(countryId);
        List<ActivityPriority> activityPrioritiesOfOrganization = new ArrayList<>(activityPriorityDTOs.size());
        for (ActivityPriorityDTO activityPriorityDTO : activityPriorityDTOs) {
            ActivityPriority organizationActivityPriority = copyActivityPriorityToOrganization(unitId, ObjectMapperUtils.copyPropertiesByMapper(activityPriorityDTO, ActivityPriority.class));
            activityPrioritiesOfOrganization.add(organizationActivityPriority);
        }
        if (isCollectionNotEmpty(activityPrioritiesOfOrganization)) {
            activityPriorityMongoRepository.saveEntities(activityPrioritiesOfOrganization);
        }
        return true;
    }

    private ActivityPriority copyActivityPriorityToOrganization(Long unitId, ActivityPriority activityPriority) {
        ActivityPriority organizationActivityPriority = ObjectMapperUtils.copyPropertiesByMapper(activityPriority, ActivityPriority.class);
        organizationActivityPriority.setId(null);
        organizationActivityPriority.setCountryId(null);
        organizationActivityPriority.setSequence(activityPriority.getSequence());
        organizationActivityPriority.setOrganizationId(unitId);
        return organizationActivityPriority;
    }

    private boolean createActivityPriorityAtOrganizations(ActivityPriority activityPriority, Long unitId) {
        List<Long> organizationIds = userIntegrationService.getAllOrganizationIds(unitId);
        List<ActivityPriority> activityPriorities = activityPriorityMongoRepository.findLastSeqenceByOrganizationIds(organizationIds);
        Map<Long, Integer> activityPrioritiesMap = activityPriorities.stream().collect(Collectors.toMap(k -> k.getOrganizationId(), v -> v.getSequence()));
        List<ActivityPriority> activityPrioritiesOfOrganization = new ArrayList<>(organizationIds.size());
        for (Long organizationId : organizationIds) {
            ActivityPriority organizationActivityPriority = copyActivityPriorityToOrganization(organizationId, activityPriority);
            organizationActivityPriority.setSequence(activityPrioritiesMap.getOrDefault(organizationId, 0) + 1);
            activityPrioritiesOfOrganization.add(organizationActivityPriority);
        }
        if (isCollectionNotEmpty(activityPrioritiesOfOrganization)) {
            activityPriorityMongoRepository.saveEntities(activityPrioritiesOfOrganization);
        }
        return true;
    }

    public ActivityPriorityDTO createActivityPriorityAtOrganization(Long organizationId, ActivityPriorityDTO activityPriorityDTO) {
        boolean existByName = activityPriorityMongoRepository.existsByNameAndCountryIdAndNotEqualToId(activityPriorityDTO.getName(), activityPriorityDTO.getColorCode(), null, organizationId);
        if (existByName) {
            exceptionService.actionNotPermittedException(ERROR_NAME_DUPLICATE, activityPriorityDTO.getName());
        }
        int activityPriorityCount = activityPriorityMongoRepository.getActivityPriorityCountAtOrganization(organizationId);
        if ((activityPriorityCount + 1) != activityPriorityDTO.getSequence()) {
            exceptionService.actionNotPermittedException("message.activity.priority.sequence");
        }
        ActivityPriority activityPriority = activityPriorityMongoRepository.findBySequenceAndOrganizationId(activityPriorityDTO.getSequence(), organizationId);
        if (isNotNull(activityPriority)) {
            exceptionService.actionNotPermittedException("message.activity.priority.same.sequence");
        }
        activityPriority = ObjectMapperUtils.copyPropertiesByMapper(activityPriorityDTO, ActivityPriority.class);
        activityPriority.setOrganizationId(organizationId);
        activityPriorityMongoRepository.save(activityPriority);
        activityPriorityDTO.setId(activityPriority.getId());
        createActivityPriorityAtOrganizations(activityPriority, organizationId);
        return activityPriorityDTO;
    }

    public List<ActivityPriorityDTO> getActivityPriorityAtCountry(Long countryId) {
        return activityPriorityMongoRepository.findAllCountryId(countryId);
    }

    public List<ActivityPriorityDTO> getActivityPriorityAtOrganization(Long unitId) {
        return activityPriorityMongoRepository.findAllOrganizationId(unitId);
    }

    public ActivityPriorityDTO updateActivityPriorityAtCountry(Long countryId, ActivityPriorityDTO activityPriorityDTO) {
        boolean existByName = activityPriorityMongoRepository.existsByNameAndCountryIdAndNotEqualToId(activityPriorityDTO.getName(), activityPriorityDTO.getColorCode(), activityPriorityDTO.getId(), countryId);
        if (existByName) {
            exceptionService.actionNotPermittedException(ERROR_NAME_DUPLICATE, activityPriorityDTO.getName());
        }
        ActivityPriority activityPriority = activityPriorityMongoRepository.findOne(activityPriorityDTO.getId());
        if (activityPriority.getSequence() < activityPriorityDTO.getSequence()) {
            activityPriorityMongoRepository.updateSequenceOfActivityPriorityOnCountry(activityPriorityDTO.getSequence(), activityPriority.getSequence(), countryId);
        }
        activityPriority = ObjectMapperUtils.copyPropertiesByMapper(activityPriorityDTO, ActivityPriority.class);
        activityPriority.setCountryId(countryId);
        activityPriorityMongoRepository.save(activityPriority);
        return activityPriorityDTO;
    }

    public ActivityPriorityDTO updateActivityPriorityAtOrganization(Long unitId, ActivityPriorityDTO activityPriorityDTO) {
        boolean existByName = activityPriorityMongoRepository.existsByNameAndOrganizationIdAndNotEqualToId(activityPriorityDTO.getName(), activityPriorityDTO.getColorCode(), activityPriorityDTO.getId(), unitId);
        if (existByName) {
            exceptionService.actionNotPermittedException(ERROR_NAME_DUPLICATE, activityPriorityDTO.getName());
        }
        int activityPriorityCount = activityPriorityMongoRepository.getActivityPriorityCountAtOrganization(unitId);
        if (activityPriorityCount < activityPriorityDTO.getSequence()) {
            exceptionService.actionNotPermittedException("message.activity.priority.sequence");
        }
        ActivityPriority activityPriority = activityPriorityMongoRepository.findOne(activityPriorityDTO.getId());
        if (activityPriority.getSequence() != activityPriorityDTO.getSequence()) {
            activityPriorityMongoRepository.updateSequenceOfActivityPriorityOnOrganization(activityPriorityDTO.getSequence(), activityPriority.getSequence(), unitId);
        }
        activityPriority = ObjectMapperUtils.copyPropertiesByMapper(activityPriorityDTO, ActivityPriority.class);
        activityPriority.setOrganizationId(unitId);
        activityPriorityMongoRepository.save(activityPriority);
        activityPriorityDTO.setId(activityPriority.getId());
        return activityPriorityDTO;
    }

    public boolean deleteActivityPriorityFromCountry(BigInteger activityPriorityId, Long countryId) {
        boolean existsActivitiesByActivityPriorityIdAndCountryId = activityMongoRepository.existsActivitiesByActivityPriorityIdAndCountryId(countryId, activityPriorityId);
        if (existsActivitiesByActivityPriorityIdAndCountryId) {
            exceptionService.actionNotPermittedException("error.activity.alreadyuse.priority");
        }
        ActivityPriority activityPriority = activityPriorityMongoRepository.findOne(activityPriorityId);
        activityPriority.setDeleted(true);
        activityPriorityMongoRepository.save(activityPriority);
        List<ActivityPriority> activityPriorities = activityPriorityMongoRepository.findAllGreaterThenAndEqualSequenceAndCountryId(activityPriority.getSequence(), countryId, new Sort(Sort.Direction.ASC, "sequence"));
        updateSequence(activityPriority, activityPriorities);
        activityPriorityMongoRepository.saveAll(activityPriorities);
        return true;
    }

    private void updateSequence(ActivityPriority activityPriority, List<ActivityPriority> activityPriorities) {
        int sequence = activityPriority.getSequence();
        for (ActivityPriority priority : activityPriorities) {
            priority.setSequence(sequence);
            sequence++;
        }
    }

    public boolean deleteActivityPriorityFromOrganization(BigInteger activityPriorityId, Long organizationId) {
        boolean existsActivitiesByActivityPriorityIdAndUnitId = activityMongoRepository.existsActivitiesByActivityPriorityIdAndUnitId(organizationId, activityPriorityId);
        if (existsActivitiesByActivityPriorityIdAndUnitId) {
            exceptionService.actionNotPermittedException("error.activity.alreadyuse.priority");
        }
        ActivityPriority activityPriority = activityPriorityMongoRepository.findOne(activityPriorityId);
        activityPriority.setDeleted(true);
        activityPriorityMongoRepository.save(activityPriority);
        List<ActivityPriority> activityPriorities = activityPriorityMongoRepository.findAllGreaterThenAndEqualSequenceAndOrganizationId(activityPriority.getSequence(), organizationId, new Sort(Sort.Direction.ASC, "sequence"));
        updateSequence(activityPriority, activityPriorities);
        activityPriorityMongoRepository.saveAll(activityPriorities);
        return true;
    }


    public boolean updateActivityPriorityInActivity(BigInteger activityPriorityId, BigInteger activityId) {
        Activity activity = activityMongoRepository.findActivityByIdAndEnabled(activityId);
        if (isNull(activity)) {
            exceptionService.dataNotFoundByIdException(MESSAGE_ACTIVITY_ID, activityId);
        }
        ActivityPriority activityPriority = activityPriorityMongoRepository.findOne(activityPriorityId);
        if (isNull(activityPriority)) {
            exceptionService.dataNotFoundByIdException(MESSAGE_ACTIVITY_PRIORITY_ID, activityPriorityId);
        }
        activity.setActivityPriorityId(activityPriorityId);
        activityMongoRepository.save(activity);
        return true;
    }

    public ActivityPriority getActivityPriorityNameAndOrganizationId(String activityPriorityName, Long organizationId){
            return activityPriorityMongoRepository.findByNameAndOrganizationId(activityPriorityName, organizationId);
    }

    public ActivityPriority getActivityPriorityById(BigInteger activityPriorityId){
        return activityPriorityMongoRepository.findOne(activityPriorityId);
    }

}

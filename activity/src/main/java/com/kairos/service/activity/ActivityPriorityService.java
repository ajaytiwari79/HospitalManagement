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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.ObjectUtils.*;

@Service
public class ActivityPriorityService {

    @Inject private ActivityPriorityMongoRepository activityPriorityMongoRepository;
    @Inject private ExceptionService exceptionService;
    @Inject private ActivityMongoRepository activityMongoRepository;
    @Inject private UserIntegrationService userIntegrationService;


    public ActivityPriorityDTO createActivityPriorityAtCountry(Long countryId,ActivityPriorityDTO activityPriorityDTO){
        boolean existByName = activityPriorityMongoRepository.existsByNameAndCountryIdAndNotEqualToId(activityPriorityDTO.getName(),null,countryId);
        if(existByName){
            exceptionService.actionNotPermittedException("error.name.duplicate", activityPriorityDTO.getName());
        }
        ActivityPriority activityPriority = activityPriorityMongoRepository.findBySequenceAndCountryId(activityPriorityDTO.getSequence(),countryId);
        if(isNotNull(activityPriority)){
            exceptionService.actionNotPermittedException("message.activity.priority.same.sequence");
        }
        activityPriority = ObjectMapperUtils.copyPropertiesByMapper(activityPriorityDTO,ActivityPriority.class);
        activityPriority.setCountryId(countryId);
        activityPriorityMongoRepository.save(activityPriority);
        activityPriorityDTO.setId(activityPriority.getId());
        createActivityPriorityAtOrganizations(activityPriority,null);
        return activityPriorityDTO;
    }



    private boolean createActivityPriorityAtOrganizations(ActivityPriority activityPriority,Long unitId){
        List<Long> organizationIds = userIntegrationService.getAllOrganizationIds(unitId);
        List<ActivityPriority> activityPriorities = activityPriorityMongoRepository.findLastSeqenceByOrganizationIds(organizationIds);
        Map<Long,Integer> activityPrioritiesMap = activityPriorities.stream().collect(Collectors.toMap(k->k.getOrganizationId(),v->v.getSequence()));
        List<ActivityPriority> activityPrioritiesOfOrganization = new ArrayList<>(organizationIds.size());
        for (Long organizationId : organizationIds) {
            ActivityPriority organizationActivityPriority = ObjectMapperUtils.copyPropertiesByMapper(activityPriority,ActivityPriority.class);
            activityPriority.setId(null);
            activityPriority.setCountryId(null);
            activityPriority.setSequence(activityPrioritiesMap.getOrDefault(organizationId,0)+1);
            organizationActivityPriority.setOrganizationId(organizationId);
            activityPrioritiesOfOrganization.add(organizationActivityPriority);
        }
        if(isCollectionNotEmpty(activityPrioritiesOfOrganization)) {
            activityPriorityMongoRepository.saveEntities(activityPrioritiesOfOrganization);
        }
        return true;
    }

    public ActivityPriorityDTO createActivityPriorityAtOrganization(Long organizationId,ActivityPriorityDTO activityPriorityDTO){
        boolean existByName = activityPriorityMongoRepository.existsByNameAndCountryIdAndNotEqualToId(activityPriorityDTO.getName(),null,organizationId);
        if(existByName){
            exceptionService.actionNotPermittedException("error.name.duplicate", activityPriorityDTO.getName());
        }
        ActivityPriority activityPriority = activityPriorityMongoRepository.findBySequenceAndOrganizationId(activityPriorityDTO.getSequence(),organizationId);
        if(isNotNull(activityPriority)){
            exceptionService.actionNotPermittedException("message.activity.priority.same.sequence");
        }
        activityPriority = ObjectMapperUtils.copyPropertiesByMapper(activityPriorityDTO,ActivityPriority.class);
        activityPriority.setOrganizationId(organizationId);
        activityPriorityMongoRepository.save(activityPriority);
        activityPriorityDTO.setId(activityPriority.getId());
        createActivityPriorityAtOrganizations(activityPriority,organizationId);
        return activityPriorityDTO;
    }

    public List<ActivityPriorityDTO> getActivityPriorityAtCountry(Long countryId){
        return activityPriorityMongoRepository.findAllCountryId(countryId);
    }
    public List<ActivityPriorityDTO> getActivityPriorityAtOrganization(Long unitId){
        return activityPriorityMongoRepository.findAllOrganizationId(unitId);
    }

    public ActivityPriorityDTO updateActivityPriorityAtCountry(Long countryId,ActivityPriorityDTO activityPriorityDTO){
        boolean existByName = activityPriorityMongoRepository.existsByNameAndCountryIdAndNotEqualToId(activityPriorityDTO.getName(),activityPriorityDTO.getId(),countryId);
        if(existByName){
            exceptionService.actionNotPermittedException("error.name.duplicate", activityPriorityDTO.getName());
        }
        ActivityPriority activityPriority = activityPriorityMongoRepository.findOne(activityPriorityDTO.getId());
        if(activityPriority.getSequence()!=activityPriorityDTO.getSequence()){
            ActivityPriority currentlySequenceActivityPriority = activityPriorityMongoRepository.findBySequenceAndCountryId(activityPriorityDTO.getSequence(),countryId);
            List<ActivityPriority> activityPriorities = activityPriorityMongoRepository.findAllGreaterThenAndEqualSequenceAndCountryId(currentlySequenceActivityPriority.getSequence(),countryId,new Sort(Sort.Direction.ASC, "sequence"));
            int sequence = currentlySequenceActivityPriority.getSequence();
            for (ActivityPriority priority : activityPriorities) {
                sequence++;
                priority.setSequence(sequence);
            }
            activityPriorityMongoRepository.saveAll(activityPriorities);
        }
        activityPriority = ObjectMapperUtils.copyPropertiesByMapper(activityPriorityDTO,ActivityPriority.class);
        activityPriorityMongoRepository.save(activityPriority);
        activityPriorityDTO.setId(activityPriority.getId());
        return activityPriorityDTO;
    }

    public ActivityPriorityDTO updateActivityPriorityAtOrganization(Long unitId,ActivityPriorityDTO activityPriorityDTO){
        boolean existByName = activityPriorityMongoRepository.existsByNameAndOrganizationIdAndNotEqualToId(activityPriorityDTO.getName(),activityPriorityDTO.getId(),unitId);
        if(existByName){
            exceptionService.actionNotPermittedException("error.name.duplicate", activityPriorityDTO.getName());
        }
        ActivityPriority activityPriority = activityPriorityMongoRepository.findOne(activityPriorityDTO.getId());
        if(activityPriority.getSequence()!=activityPriorityDTO.getSequence()){
            ActivityPriority currentlySequenceActivityPriority = activityPriorityMongoRepository.findBySequenceAndOrganizationId(activityPriorityDTO.getSequence(),unitId);
            List<ActivityPriority> activityPriorities = activityPriorityMongoRepository.findAllGreaterThenAndEqualSequenceAndOrganizationId(currentlySequenceActivityPriority.getSequence(),unitId,new Sort(Sort.Direction.ASC, "sequence"));
            int sequence = currentlySequenceActivityPriority.getSequence();
            for (ActivityPriority priority : activityPriorities) {
                sequence++;
                priority.setSequence(sequence);
            }
            activityPriorityMongoRepository.saveAll(activityPriorities);
        }
        activityPriority = ObjectMapperUtils.copyPropertiesByMapper(activityPriorityDTO,ActivityPriority.class);
        activityPriorityMongoRepository.save(activityPriority);
        activityPriorityDTO.setId(activityPriority.getId());
        return activityPriorityDTO;
    }

    public boolean deleteActivityPriorityFromCountry(BigInteger activityPriorityId,Long countryId){
        boolean existsActivitiesByActivityPriorityIdAndCountryId = activityMongoRepository.existsActivitiesByActivityPriorityIdAndCountryId(countryId,activityPriorityId);
        if(existsActivitiesByActivityPriorityIdAndCountryId){
            exceptionService.actionNotPermittedException("error.activity.alreadyuse.priority");
        }
        ActivityPriority activityPriority = activityPriorityMongoRepository.findOne(activityPriorityId);
        activityPriority.setDeleted(true);
        activityPriorityMongoRepository.save(activityPriority);
        List<ActivityPriority> activityPriorities = activityPriorityMongoRepository.findAllGreaterThenAndEqualSequenceAndCountryId(activityPriority.getSequence(),countryId,new Sort(Sort.Direction.ASC, "sequence"));
        int sequence = activityPriority.getSequence();
        for (ActivityPriority priority : activityPriorities) {
            priority.setSequence(sequence);
            sequence++;
        }
        activityPriorityMongoRepository.saveAll(activityPriorities);
        return true;
    }

    public boolean deleteActivityPriorityFromOrganization(BigInteger activityPriorityId,Long organizationId){
        boolean existsActivitiesByActivityPriorityIdAndUnitId = activityMongoRepository.existsActivitiesByActivityPriorityIdAndUnitId(organizationId,activityPriorityId);
        if(existsActivitiesByActivityPriorityIdAndUnitId){
            exceptionService.actionNotPermittedException("error.activity.alreadyuse.priority");
        }
        ActivityPriority activityPriority = activityPriorityMongoRepository.findOne(activityPriorityId);
        activityPriority.setDeleted(true);
        activityPriorityMongoRepository.save(activityPriority);
        List<ActivityPriority> activityPriorities = activityPriorityMongoRepository.findAllGreaterThenAndEqualSequenceAndOrganizationId(activityPriority.getSequence(),organizationId,new Sort(Sort.Direction.ASC, "sequence"));
        int sequence = activityPriority.getSequence();
        for (ActivityPriority priority : activityPriorities) {
            priority.setSequence(sequence);
            sequence++;
        }
        activityPriorityMongoRepository.saveAll(activityPriorities);
        return true;
    }


    public boolean updateActivityPriorityInActvitiy(BigInteger activityPriorityId,BigInteger activityId){
        Activity activity = activityMongoRepository.findActivityByIdAndEnabled(activityId);
        if(isNull(activity)){
            exceptionService.dataNotFoundByIdException("message.activity.id",activityId);
        }
        ActivityPriority activityPriority = activityPriorityMongoRepository.findOne(activityPriorityId);
        if(isNull(activityPriority)){
            exceptionService.dataNotFoundByIdException("message.activity.priority.id",activityPriorityId);
        }
        activity.setActivityPriorityId(activityPriorityId);
        return true;
    }

}

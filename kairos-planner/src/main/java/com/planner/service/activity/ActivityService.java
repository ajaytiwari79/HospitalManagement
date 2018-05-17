package com.planner.service.activity;

import com.kairos.client.dto.activity.ActivityNoTabsDTO;
import com.planner.domain.activity.Activity;
import com.planner.repository.activity.ActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;

@Service
public class ActivityService  {
    @Autowired
    private ActivityRepository activityRepository;
    public void createActivity(Long unitId, ActivityNoTabsDTO activityDTO) {
        Activity activity= new Activity(activityDTO.getName(),activityDTO.getExpertises(),activityDTO.getDescription(),activityDTO.getCategoryId(),activityDTO.getActivitySkills(),activityDTO.getEmployementTypes(),activityDTO.getMinLength(),activityDTO.getMaxLength(),activityDTO.getMaxAllocations(),activityDTO.getId());
        activityRepository.save(activity);
    }

    public void updateActivity(Long unitId, BigInteger activityKariosId, ActivityNoTabsDTO activityDTO) {
        Activity activity=activityRepository.findByKairosId(activityKariosId).get();
        activity.setActivitySkills(activityDTO.getActivitySkills());
        activity.setEmployementTypes(activityDTO.getEmployementTypes());
        activity.setExpertises(activityDTO.getExpertises());
        activityRepository.save(activity);
    }
}

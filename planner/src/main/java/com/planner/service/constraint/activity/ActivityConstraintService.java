package com.planner.service.constraint.activity;

import com.kairos.commons.planning_setting.PlanningSetting;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.activity.ActivityConstraintDTO;
import com.kairos.enums.constraint.ConstraintLevel;
import com.planner.domain.constraint.activity.ActivityConstraint;
import com.planner.repository.constraint.ActivityConstraintRepository;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;
import static com.kairos.commons.utils.ObjectUtils.isNotNull;
import static com.kairos.enums.constraint.ConstraintLevel.HARD;
import static com.kairos.enums.constraint.ConstraintLevel.SOFT;
import static com.kairos.enums.constraint.ConstraintSubType.ACTIVITY_SHORTEST_DURATION_RELATIVE_TO_SHIFT_LENGTH;
import static com.kairos.enums.constraint.ConstraintSubType.ACTIVITY_VALID_DAYTYPE;

@Service
public class ActivityConstraintService {
    @Inject
    private ActivityConstraintRepository activityConstraintRepository;

    public ActivityConstraintDTO createActivityConstraint(ActivityConstraintDTO activityConstraintDTO){

          if(isNotNull(activityConstraintDTO)) {
              ActivityConstraint activityConstraint = ObjectMapperUtils.copyPropertiesByMapper(activityConstraintDTO, ActivityConstraint.class);
              activityConstraintRepository.saveEntity(activityConstraint);
          }
        return activityConstraintDTO;
    }



    public List<ActivityConstraintDTO> getAllActivityConstraintByActivityId(BigInteger activityId){
        List<ActivityConstraint> activityConstraintList = activityConstraintRepository.findAllByActivityIdAndDeletedFalse( activityId);
        if(isCollectionNotEmpty(activityConstraintList)){
            return ObjectMapperUtils.copyPropertiesOfCollectionByMapper(activityConstraintList, ActivityConstraintDTO.class);
        }else{
           return defaultActivityConstraintList(activityId);
        }


    }


    public List<ActivityConstraintDTO> defaultActivityConstraintList(BigInteger activityId){
        List<ActivityConstraintDTO> activityConstraintDTOS = new ArrayList<>();
        PlanningSetting planningSetting1 = new PlanningSetting(HARD,2);
        ActivityConstraintDTO activityConstraintDTO1 = new ActivityConstraintDTO(activityId,planningSetting1, ACTIVITY_SHORTEST_DURATION_RELATIVE_TO_SHIFT_LENGTH);
        PlanningSetting planningSetting2 = new PlanningSetting(SOFT,2);
        ActivityConstraintDTO activityConstraintDTO2 = new ActivityConstraintDTO(activityId,planningSetting2,ACTIVITY_VALID_DAYTYPE);
        activityConstraintDTOS.add(activityConstraintDTO1);
        activityConstraintDTOS.add(activityConstraintDTO2);
        return activityConstraintDTOS;
    }


    public ActivityConstraintDTO updateActivityConstraint(ActivityConstraintDTO activityConstraintDTO){

        ActivityConstraint activityConstraint = activityConstraintRepository.findByActivityIdAndConstraintSubTypeAndDeletedFalse(activityConstraintDTO.getActivityId(),activityConstraintDTO.getConstraintSubType());
           if (isNotNull(activityConstraint)) {
               activityConstraint.setPlanningSetting(activityConstraintDTO.getPlanningSetting());
           }else {
               activityConstraint = ObjectMapperUtils.copyPropertiesByMapper(activityConstraintDTO, ActivityConstraint.class);

           }
        activityConstraintRepository.saveEntity(activityConstraint);
        return activityConstraintDTO;
    }





}

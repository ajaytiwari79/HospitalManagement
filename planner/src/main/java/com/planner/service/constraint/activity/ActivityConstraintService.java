package com.planner.service.constraint.activity;

import com.kairos.commons.planning_setting.PlanningSetting;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.activity.ActivityConstraintDTO;
import com.kairos.enums.constraint.ConstraintLevel;
import com.kairos.enums.constraint.ConstraintSubType;
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

    public ActivityConstraintDTO createOrUpdateActivityConstraint(ActivityConstraintDTO activityConstraintDTO){
        ActivityConstraint activityConstraint = activityConstraintRepository.findByActivityIdAndConstraintSubTypeAndDeletedFalse(activityConstraintDTO.getActivityId(),activityConstraintDTO.getConstraintSubType());
        if (isNotNull(activityConstraint)) {
            if(!activityConstraint.getMandatory()){
                activityConstraint.setPlanningSetting(activityConstraintDTO.getPlanningSetting());
            }
        }else {
            activityConstraint = ObjectMapperUtils.copyPropertiesByMapper(activityConstraintDTO, ActivityConstraint.class);

        }
        activityConstraintRepository.saveEntity(activityConstraint);
        return activityConstraintDTO;
    }



    public List<ActivityConstraintDTO> getAllActivityConstraintByActivityId(BigInteger activityId){
        List<ActivityConstraint> activityConstraintList = activityConstraintRepository.findAllByActivityIdAndDeletedFalse( activityId);
        if(isCollectionNotEmpty(activityConstraintList)){
            return ObjectMapperUtils.copyPropertiesOfCollectionByMapper(activityConstraintList, ActivityConstraintDTO.class);
        }else{
           return getActivityConstraints();
        }


    }


    public List<ActivityConstraintDTO> getActivityConstraints(){
        List<ActivityConstraintDTO> activityConstraintDTOS = new ArrayList<>();

        for(int i = 0; i < ConstraintSubType.values().length; i++) {
            PlanningSetting planningSetting1 = new PlanningSetting(HARD,2);
            ActivityConstraintDTO activityConstraintDTO1 = new ActivityConstraintDTO(planningSetting1,ConstraintSubType.values()[i]);
            activityConstraintDTOS.add(activityConstraintDTO1);
        }

        return activityConstraintDTOS;
    }







}

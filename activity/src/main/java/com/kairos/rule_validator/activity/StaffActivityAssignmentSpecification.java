package com.kairos.rule_validator.activity;

import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.staff.personal_details.StaffDTO;
import com.kairos.rule_validator.AbstractSpecification;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;
import static com.kairos.commons.utils.ObjectUtils.isNotNull;
import static com.kairos.constants.ActivityMessagesConstants.EXPERTISE_ABSENT_ACTIVITY;
import static com.kairos.constants.ActivityMessagesConstants.STAFF_SKILL_DOES_NOT_MATCHED;

public class StaffActivityAssignmentSpecification extends AbstractSpecification<StaffDTO> {
    private Activity activity;


    public StaffActivityAssignmentSpecification(Activity activity) {
        this.activity = activity;
    }

    @Override
    public boolean isSatisfied(StaffDTO staffExpertiseSpecification) {
        return false;
    }

    @Override
    public void validateRules(StaffDTO staffDTO) {
        //Not in use
    }

    @Override
    public List<String> isSatisfiedString(StaffDTO staffDTO) {
        List<String> errorMessages = new ArrayList<>();
        if (isNotNull(staffDTO) && !CollectionUtils.containsAny(activity.getExpertises(), staffDTO.getExpertiseIds())) {
            errorMessages.add(EXPERTISE_ABSENT_ACTIVITY);
        }
        if(isCollectionNotEmpty(activity.getSkillActivityTab().getActivitySkillIds()) && !activity.getSkillActivityTab().getActivitySkillIds().stream().allMatch(skillId->staffDTO.getSkillIds().contains(skillId))){
            errorMessages.add(STAFF_SKILL_DOES_NOT_MATCHED);
        }
        return errorMessages;
    }
}

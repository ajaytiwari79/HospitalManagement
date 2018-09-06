package com.kairos.rule_validator.activity;

import com.kairos.rule_validator.AbstractSpecification;
import com.kairos.wrapper.shift.ShiftWithActivityDTO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by oodles on 28/11/17.
 */
public class StaffAndSkillSpecification extends AbstractSpecification<ShiftWithActivityDTO> {


    private List<Long> staffSkills;
    private List<Long> activitySkills = new ArrayList<>();

    public StaffAndSkillSpecification(List<Long> staffSkills) {
        this.staffSkills = staffSkills;
    }

    @Override
    public boolean isSatisfied(ShiftWithActivityDTO shift) {
        /*if (!shift.getActivity().getSkillActivityTab().getActivitySkills().isEmpty()) {
            shift.getActivity().getSkillActivityTab().getActivitySkills().forEach(
                    activityTypeSkill -> activitySkills.add(activityTypeSkill.getSkillId()));
            if( !activitySkills.containsAll(this.staffSkills)){
                exceptionService.actionNotPermittedException("message.activity.skills-match");
            }
        }*/
        return true;

    }

    @Override
    public void validateRules(ShiftWithActivityDTO shift) {

    }


    @Override
    public List<String> isSatisfiedString(ShiftWithActivityDTO shift) {
        return Collections.emptyList();
    }

}

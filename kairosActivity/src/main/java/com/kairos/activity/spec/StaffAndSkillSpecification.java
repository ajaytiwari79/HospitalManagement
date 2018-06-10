package com.kairos.activity.spec;

import com.kairos.activity.persistence.model.activity.Activity;
import com.kairos.activity.response.dto.ShiftWithActivityDTO;
import com.kairos.activity.service.exception.ExceptionService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
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
    public List<String> isSatisfiedString(Activity activity) {
        return null;
    }

}

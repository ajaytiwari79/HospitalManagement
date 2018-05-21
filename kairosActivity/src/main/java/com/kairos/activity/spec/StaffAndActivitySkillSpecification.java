package com.kairos.activity.spec;

import com.kairos.activity.custom_exception.ActionNotPermittedException;
import com.kairos.activity.persistence.model.activity.Activity;
import com.kairos.activity.persistence.model.activity.tabs.SkillActivityTab;
import com.kairos.activity.service.exception.ExceptionService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by oodles on 28/11/17.
 */
public class StaffAndActivitySkillSpecification extends AbstractActivitySpecification<Activity> {


    private List<Long> staffSkills;
    private List<Long> activitySkills = new ArrayList<>();

    public StaffAndActivitySkillSpecification(List<Long> staffSkills) {
        this.staffSkills = staffSkills;
    }
@Autowired
    ExceptionService exceptionService;

    @Override
    public boolean isSatisfied(Activity activity) {
        if (!activity.getSkillActivityTab().getActivitySkills().isEmpty()) {
            activity.getSkillActivityTab().getActivitySkills().forEach(
                    activityTypeSkill -> activitySkills.add(activityTypeSkill.getSkillId()));
            if( !activitySkills.containsAll(this.staffSkills)){
                exceptionService.actionNotPermittedException("message.activity.skills-match");
            }
        }
        return true;

    }
}

package com.kairos.rule_validator.activity;

import com.kairos.dto.activity.shift.ActivityRuleViolation;
import com.kairos.dto.activity.shift.ShiftActivityDTO;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.rule_validator.AbstractSpecification;
import com.kairos.service.exception.ExceptionService;
import com.kairos.utils.ShiftValidatorService;
import com.kairos.wrapper.shift.ShiftWithActivityDTO;
import com.kairos.wrapper.wta.RuleTemplateSpecificInfo;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by oodles on 28/11/17.
 */
public class StaffAndSkillSpecification extends AbstractSpecification<ShiftWithActivityDTO> {

    @Autowired
    ExceptionService exceptionService;

    private List<Long> staffSkills;
    private List<String> errorMessages = new ArrayList<>();
    private RuleTemplateSpecificInfo ruleTemplateSpecificInfo;

    public StaffAndSkillSpecification(List<Long> staffSkills, RuleTemplateSpecificInfo ruleTemplateSpecificInfo) {
        this.staffSkills = staffSkills;
        this.ruleTemplateSpecificInfo = ruleTemplateSpecificInfo;
    }

    @Override
    public boolean isSatisfied(ShiftWithActivityDTO shift) {
//        if (!shift.getActivity().getSkillActivityTab().getActivitySkills().isEmpty()) {
//            shift.getActivity().getSkillActivityTab().getActivitySkills().forEach(
//                    activityTypeSkill -> activitySkills.add(activityTypeSkill.getSkillId()));
//            if( !activitySkills.containsAll(this.staffSkills)){
//                exceptionService.actionNotPermittedException("message.activity.skills-match");
//            }
//        }
        return true;

    }

    @Override
    public void validateRules(ShiftWithActivityDTO shift) {
        for (ShiftActivityDTO shiftActivityDTO : shift.getActivities()) {
            if (!CollectionUtils.containsAny(shiftActivityDTO.getActivity().getSkillActivityTab().getActivitySkillIds(), staffSkills)) {
                errorMessages.add(exceptionService.convertMessage("message.activity.skill.match", shiftActivityDTO.getActivity().getName()));
            }
        }
        ActivityRuleViolation activityRuleViolation=ruleTemplateSpecificInfo.getViolatedRules().getActivities().stream().filter(k->k.getActivityId().equals(shiftActivityDTO.getActivity().getId())).findAny().orElse(null);
        if(activityRuleViolation==null){
            activityRuleViolation=new ActivityRuleViolation(shiftActivityDTO.getActivity().getId(),shiftActivityDTO.getActivity().getName(),0,errorMessages);
        }
    }


    @Override
    public List<String> isSatisfiedString(ShiftWithActivityDTO shift) {
        return Collections.emptyList();
    }

}

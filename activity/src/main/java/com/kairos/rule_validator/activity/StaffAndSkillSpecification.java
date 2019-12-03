package com.kairos.rule_validator.activity;

import com.kairos.dto.activity.activity.activity_tabs.ActivitySkill;
import com.kairos.dto.activity.activity.activity_tabs.SkillActivityDTO;
import com.kairos.dto.activity.shift.ActivityRuleViolation;
import com.kairos.dto.activity.shift.ShiftActivityDTO;
import com.kairos.dto.activity.shift.ShiftWithActivityDTO;
import com.kairos.dto.user.skill.SkillLevelDTO;
import com.kairos.enums.SkillLevel;
import com.kairos.rule_validator.AbstractSpecification;
import com.kairos.service.exception.ExceptionService;
import com.kairos.wrapper.wta.RuleTemplateSpecificInfo;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.kairos.constants.ActivityMessagesConstants.MESSAGE_ACTIVITY_SKILL_MATCH;

/**
 * Created by oodles on 28/11/17.
 */
public class StaffAndSkillSpecification extends AbstractSpecification<ShiftWithActivityDTO> {

    private List<SkillLevelDTO> skillLevelDTOS;
    private RuleTemplateSpecificInfo ruleTemplateSpecificInfo;
    private ExceptionService exceptionService;

    public StaffAndSkillSpecification(List<SkillLevelDTO> skillLevelDTOS, RuleTemplateSpecificInfo ruleTemplateSpecificInfo,ExceptionService exceptionService) {
        this.skillLevelDTOS = skillLevelDTOS;
        this.ruleTemplateSpecificInfo = ruleTemplateSpecificInfo;
        this.exceptionService=exceptionService;
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
        List<String> errorMessages = new ArrayList<>();
        for (ShiftActivityDTO shiftActivityDTO : shift.getActivities()) {
            for (ShiftActivityDTO childActivity : shiftActivityDTO.getChildActivities()) {
                validateStaffSkills(errorMessages, childActivity);
            }
            validateStaffSkills(errorMessages, shiftActivityDTO);
        }

    }

    private void validateStaffSkills(List<String> errorMessages, ShiftActivityDTO shiftActivityDTO) {
        ActivityRuleViolation activityRuleViolation;
        if (CollectionUtils.isNotEmpty(shiftActivityDTO.getActivity().getSkillActivityTab().getActivitySkillIds()) &&
                (CollectionUtils.isEmpty(skillLevelDTOS) || !isSkillSatisfied(shiftActivityDTO.getActivity().getSkillActivityTab()))) {
            errorMessages.add(exceptionService.convertMessage(MESSAGE_ACTIVITY_SKILL_MATCH, shiftActivityDTO.getActivity().getName()));
            activityRuleViolation=ruleTemplateSpecificInfo.getViolatedRules().getActivities().stream().filter(k->k.getActivityId().equals(shiftActivityDTO.getActivity().getId())).findAny().orElse(null);
            if(activityRuleViolation==null){
                activityRuleViolation=new ActivityRuleViolation(shiftActivityDTO.getActivity().getId(),shiftActivityDTO.getActivity().getName(),0,errorMessages);
                ruleTemplateSpecificInfo.getViolatedRules().getActivities().add(activityRuleViolation);
            }
            else {
                activityRuleViolation.getErrorMessages().addAll(errorMessages);
            }
        }
    }

    private boolean isSkillSatisfied(SkillActivityDTO skillActivityDTO) {
        for (ActivitySkill activitySkill : skillActivityDTO.getActivitySkills()){
            for (SkillLevelDTO skillLevelDTO : skillLevelDTOS) {
                if (activitySkill.getSkillId().equals(skillLevelDTO.getSkillId()) &&
                        (SkillLevel.BASIC.toString().equals(activitySkill.getLevel()) ||
                                (SkillLevel.ADVANCE.toString().equals(activitySkill.getLevel()) && !SkillLevel.BASIC.toString().equals(skillLevelDTO.getSkillLevel())) ||
                                activitySkill.getLevel().equals(skillLevelDTO.getSkillLevel()))) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public List<String> isSatisfiedString(ShiftWithActivityDTO shift) {
        return Collections.emptyList();
    }

}

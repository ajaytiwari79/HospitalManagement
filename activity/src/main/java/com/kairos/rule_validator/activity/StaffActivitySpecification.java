package com.kairos.rule_validator.activity;

import com.kairos.dto.activity.shift.ActivityRuleViolation;
import com.kairos.dto.activity.shift.ShiftActivityDTO;
import com.kairos.dto.activity.shift.ShiftWithActivityDTO;
import com.kairos.rule_validator.AbstractSpecification;
import com.kairos.wrapper.wta.RuleTemplateSpecificInfo;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.kairos.constants.ActivityMessagesConstants.MESSAGE_TEAM_ACTIVITY_NOT_ASSIGN;
import static com.kairos.service.shift.ShiftValidatorService.convertMessage;

public class StaffActivitySpecification extends AbstractSpecification<ShiftWithActivityDTO> {

    private Set<BigInteger> teamActivityIds;
    private RuleTemplateSpecificInfo ruleTemplateSpecificInfo;

    public StaffActivitySpecification( RuleTemplateSpecificInfo ruleTemplateSpecificInfo,Set<BigInteger> teamActivityIds) {
        this.teamActivityIds = teamActivityIds;
        this.ruleTemplateSpecificInfo = ruleTemplateSpecificInfo;
    }

    @Override
    public boolean isSatisfied(ShiftWithActivityDTO shiftWithActivityDTO) {
        return false;
    }

    @Override
    public void validateRules(ShiftWithActivityDTO shiftWithActivityDTO) {
        List<String> errorMessages = new ArrayList<>();
        for (ShiftActivityDTO shiftActivityDTO : shiftWithActivityDTO.getActivities()) {
            for (ShiftActivityDTO childActivity : shiftActivityDTO.getChildActivities()) {
                validateStaffActivity(errorMessages,childActivity);
            }
            validateStaffActivity(errorMessages,shiftActivityDTO);
        }
    }

    private void validateStaffActivity(List<String> errorMessages, ShiftActivityDTO activity) {
        ActivityRuleViolation activityRuleViolation;
        if (!teamActivityIds.contains(activity.getActivityId())) {
            errorMessages.add(convertMessage(MESSAGE_TEAM_ACTIVITY_NOT_ASSIGN, activity.getActivity().getName()));
            activityRuleViolation = ruleTemplateSpecificInfo.getViolatedRules().getActivities().stream().filter(k -> k.getActivityId().equals(activity.getActivity().getId())).findAny().orElse(null);
            if (activityRuleViolation == null) {
                activityRuleViolation = new ActivityRuleViolation(activity.getActivity().getId(), activity.getActivity().getName(), 0, errorMessages);
                ruleTemplateSpecificInfo.getViolatedRules().getActivities().add(activityRuleViolation);
            } else {
                activityRuleViolation.getErrorMessages().addAll(errorMessages);
            }
        }
    }

    @Override
    public List<String> isSatisfiedString(ShiftWithActivityDTO shiftWithActivityDTO) {
        return null;
    }
}

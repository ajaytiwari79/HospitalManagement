package com.kairos.rule_validator.activity;

import com.kairos.dto.activity.shift.ActivityRuleViolation;
import com.kairos.dto.activity.shift.Expertise;
import com.kairos.dto.activity.shift.ShiftActivityDTO;
import com.kairos.rule_validator.AbstractSpecification;
import com.kairos.dto.activity.shift.ShiftWithActivityDTO;
import com.kairos.wrapper.wta.RuleTemplateSpecificInfo;

import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.ObjectUtils.isNotNull;
import static com.kairos.service.shift.ShiftValidatorService.convertMessage;
import static com.kairos.service.shift.ShiftValidatorService.throwException;

/**
 * Created by vipul on 31/1/18.
 */
public class ExpertiseSpecification extends AbstractSpecification<ShiftWithActivityDTO> {

    private Set<Long> expertiseIds = new HashSet<>();
    private Expertise expertise;
    private RuleTemplateSpecificInfo ruleTemplateSpecificInfo;


    public ExpertiseSpecification(Expertise expertise, RuleTemplateSpecificInfo ruleTemplateSpecificInfo) {
        this.expertise = expertise;
        this.ruleTemplateSpecificInfo = ruleTemplateSpecificInfo;

    }



    @Override
    public boolean isSatisfied(ShiftWithActivityDTO shift) {
        expertiseIds.addAll(shift.getActivities().stream().flatMap(a -> a.getActivity().getExpertises().stream()).collect(Collectors.toList()));
        if (!expertiseIds.contains(expertise.getId())) {
            return false;
        }
        throwException("message.activity.expertise.match");
        return true;
    }

    @Override
    public void validateRules(ShiftWithActivityDTO shift) {
        List<String> errorMessages = new ArrayList<>();
        for (ShiftActivityDTO shiftActivityDTO : shift.getActivities()) {
            ActivityRuleViolation activityRuleViolation = null;
            if (isNotNull(shiftActivityDTO.getActivity().getExpertises()) && !shiftActivityDTO.getActivity().getExpertises().contains(expertise.getId())) {
                errorMessages.add(convertMessage("message.activity.expertise.match", shiftActivityDTO.getActivity().getName(), expertise.getName()));
                activityRuleViolation = ruleTemplateSpecificInfo.getViolatedRules().getActivities().stream().filter(k -> k.getActivityId().equals(shiftActivityDTO.getActivity().getId())).findAny().orElse(null);
                if (activityRuleViolation == null) {
                    activityRuleViolation = new ActivityRuleViolation(shiftActivityDTO.getActivity().getId(), shiftActivityDTO.getActivity().getName(), 0, errorMessages);
                    ruleTemplateSpecificInfo.getViolatedRules().getActivities().add(activityRuleViolation);
                } else {
                    activityRuleViolation.getErrorMessages().addAll(errorMessages);
                }
            }

        }
    }

    @Override
    public List<String> isSatisfiedString(ShiftWithActivityDTO shift) {
        expertiseIds.addAll(shift.getActivities().stream().flatMap(a -> a.getActivity().getExpertises().stream()).collect(Collectors.toList()));
        if (!expertiseIds.contains(expertise.getId())) {
            return Arrays.asList("message.activity.expertise.match");
        }
        return Collections.emptyList();
    }

}

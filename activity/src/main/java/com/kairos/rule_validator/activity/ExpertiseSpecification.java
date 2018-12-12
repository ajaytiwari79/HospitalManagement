package com.kairos.rule_validator.activity;

import com.kairos.dto.activity.shift.ActivityRuleViolation;
import com.kairos.dto.activity.shift.Expertise;
import com.kairos.dto.activity.shift.ShiftActivityDTO;
import com.kairos.rule_validator.AbstractSpecification;
import com.kairos.service.exception.ExceptionService;
import com.kairos.utils.ShiftValidatorService;
import com.kairos.wrapper.shift.ShiftWithActivityDTO;
import com.kairos.wrapper.wta.RuleTemplateSpecificInfo;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by vipul on 31/1/18.
 */
public class ExpertiseSpecification extends AbstractSpecification<ShiftWithActivityDTO> {

    private Set<Long> expertiseIds = new HashSet<>();
    private Expertise expertise;
    private List<String> errorMessages = new ArrayList<>();
    private RuleTemplateSpecificInfo ruleTemplateSpecificInfo;
    private ExceptionService exceptionService;

    public ExpertiseSpecification(Expertise expertise, RuleTemplateSpecificInfo ruleTemplateSpecificInfo,ExceptionService exceptionService) {
        this.expertise = expertise;
        this.ruleTemplateSpecificInfo = ruleTemplateSpecificInfo;
        this.exceptionService=exceptionService;
    }



    @Override
    public boolean isSatisfied(ShiftWithActivityDTO shift) {
        expertiseIds.addAll(shift.getActivities().stream().flatMap(a -> a.getActivity().getExpertises().stream()).collect(Collectors.toList()));
        if (!expertiseIds.contains(expertise.getId())) {
            return false;
        }
        exceptionService.invalidRequestException("message.activity.expertise.match");
        return true;
    }

    @Override
    public void validateRules(ShiftWithActivityDTO shift) {

        for (ShiftActivityDTO shiftActivityDTO : shift.getActivities()) {
            ActivityRuleViolation activityRuleViolation = null;
            if (!shiftActivityDTO.getActivity().getExpertises().contains(expertise.getId())) {
                errorMessages.add(exceptionService.convertMessage("message.activity.expertise.match", shiftActivityDTO.getActivity().getName(), expertise.getName()));
                activityRuleViolation = ruleTemplateSpecificInfo.getViolatedRules().getActivities().stream().filter(k -> k.getActivityId().equals(shiftActivityDTO.getActivity().getId())).findAny().orElse(null);
                if (activityRuleViolation == null) {
                    activityRuleViolation = new ActivityRuleViolation(shiftActivityDTO.getActivity().getId(), shiftActivityDTO.getActivity().getName(), 0, errorMessages);
                } else {
                    activityRuleViolation.getErrorMessages().addAll(errorMessages);
                }
            }
            ruleTemplateSpecificInfo.getViolatedRules().getActivities().add(activityRuleViolation);
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

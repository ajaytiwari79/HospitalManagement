package com.kairos.rule_validator.activity;

import com.kairos.dto.activity.shift.Expertise;
import com.kairos.rule_validator.AbstractSpecification;
import com.kairos.service.exception.ExceptionService;
import com.kairos.utils.ShiftValidatorService;
import com.kairos.wrapper.shift.ShiftWithActivityDTO;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by vipul on 31/1/18.
 */
public class ExpertiseSpecification extends AbstractSpecification<ShiftWithActivityDTO> {

    private Set<Long> expertiseIds = new HashSet<>();
    private Expertise expertise;

    public ExpertiseSpecification(Expertise expertise) {
        this.expertise = expertise;
    }

    @Autowired
    ExceptionService exceptionService;

    @Override
    public boolean isSatisfied(ShiftWithActivityDTO shift) {
            expertiseIds.addAll(shift.getActivities().stream().flatMap(a -> a.getActivity().getExpertises().stream()).collect(Collectors.toList()));
            if (!expertiseIds.contains(expertise.getId())) {
                return false;
            }
            //exceptionService.invalidRequestException("message.activity.expertise.match");
        return true;
    }

    @Override
    public void validateRules(ShiftWithActivityDTO shift) {
            expertiseIds.addAll(shift.getActivities().stream().filter(a->a.getActivity()!=null).flatMap(a -> a.getActivity().getExpertises().stream()).collect(Collectors.toList()));
            if (!expertiseIds.contains(expertise.getId())) {
                ShiftValidatorService.throwException("message.activity.expertise.match");
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

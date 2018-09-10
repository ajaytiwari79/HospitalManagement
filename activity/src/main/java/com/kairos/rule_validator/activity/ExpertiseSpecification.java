package com.kairos.rule_validator.activity;

import com.kairos.dto.activity.shift.Expertise;
import com.kairos.rule_validator.AbstractSpecification;
import com.kairos.service.exception.ExceptionService;
import com.kairos.utils.ShiftValidatorService;
import com.kairos.wrapper.shift.ShiftWithActivityDTO;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

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
        if (Optional.ofNullable(shift.getActivity().getExpertises()).isPresent() && !shift.getActivity().getExpertises().isEmpty()) {
            expertiseIds.addAll(shift.getActivity().getExpertises());
            if (expertiseIds.contains(expertise.getId())) {
                return true;
            }
            //exceptionService.invalidRequestException("message.activity.expertise.match");
        }
        return true;
    }

    @Override
    public void validateRules(ShiftWithActivityDTO shift) {
        if (Optional.ofNullable(shift.getActivity().getExpertises()).isPresent() && !shift.getActivity().getExpertises().isEmpty()) {
            expertiseIds.addAll(shift.getActivity().getExpertises());
            if (!expertiseIds.contains(expertise.getId())) {
                ShiftValidatorService.throwException("message.activity.expertise.match");
            }

        }
    }

    @Override
    public List<String> isSatisfiedString(ShiftWithActivityDTO shift) {
        if (Optional.ofNullable(shift.getActivity().getExpertises()).isPresent() && !shift.getActivity().getExpertises().isEmpty()) {
            expertiseIds.addAll(shift.getActivity().getExpertises());
            if (expertiseIds.contains(expertise.getId())) {
                return Collections.emptyList();
            }
            return Arrays.asList("message.activity.expertise.match");
        }
        return Collections.emptyList();
    }

}

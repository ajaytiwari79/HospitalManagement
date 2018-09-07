package com.kairos.rule_validator.activity;

import com.kairos.activity.shift.EmploymentType;
import com.kairos.rule_validator.AbstractSpecification;
import com.kairos.service.exception.ExceptionService;
import com.kairos.util.ShiftValidatorService;
import com.kairos.wrapper.shift.ShiftWithActivityDTO;

import java.util.*;


/**
 * Created by vipul on 30/1/18.
 */
public class EmploymentTypeSpecification extends AbstractSpecification<ShiftWithActivityDTO> {

    private Set<Long> employmentTypeIds = new HashSet<>();
    private EmploymentType employmentType;

    public EmploymentTypeSpecification(EmploymentType employmentType) {
        this.employmentType = employmentType;
    }

    @Override
    public List<String> isSatisfiedString(ShiftWithActivityDTO shift) {
        if (Optional.ofNullable(shift.getActivity().getEmploymentTypes()).isPresent() && !shift.getActivity().getEmploymentTypes().isEmpty()) {
            employmentTypeIds.addAll(shift.getActivity().getEmploymentTypes());
            if (employmentTypeIds.contains(employmentType.getId())) {
                return Collections.EMPTY_LIST;
            }
            return Arrays.asList("message.activity.employement-type-match");
        }
        return Collections.emptyList();

    }

    @Override
    public boolean isSatisfied(ShiftWithActivityDTO shift) {
        return false;
    }

    @Override
    public void validateRules(ShiftWithActivityDTO shift) {
        if (Optional.ofNullable(shift.getActivity().getEmploymentTypes()).isPresent() && !shift.getActivity().getEmploymentTypes().isEmpty()) {
            employmentTypeIds.addAll(shift.getActivity().getEmploymentTypes());
            if (!employmentTypeIds.contains(employmentType.getId())) {
                ShiftValidatorService.throwException("message.activity.employement-type-match");
            }
        }
    }
}

package com.kairos.rule_validator.activity;

import com.kairos.dto.activity.shift.EmploymentType;
import com.kairos.rule_validator.AbstractSpecification;
import com.kairos.utils.ShiftValidatorService;
import com.kairos.wrapper.shift.ShiftWithActivityDTO;

import java.util.*;
import java.util.stream.Collectors;


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
        employmentTypeIds.addAll(shift.getActivities().stream().filter(a->a.getActivity()!=null).flatMap(a -> a.getActivity().getEmploymentTypes().stream()).collect(Collectors.toList()));
        if (!employmentTypeIds.contains(employmentType.getId())) {
            return Arrays.asList("message.activity.employement-type-match");
        }
        return new ArrayList<>();

    }

    @Override
    public boolean isSatisfied(ShiftWithActivityDTO shift) {
        return false;
    }

    @Override
    public void validateRules(ShiftWithActivityDTO shift) {
        employmentTypeIds.addAll(shift.getActivities().get(0).getActivity().getEmploymentTypes());
        if (!employmentTypeIds.contains(employmentType.getId())) {
            ShiftValidatorService.throwException("message.activity.employement-type-match");
        }
    }
}

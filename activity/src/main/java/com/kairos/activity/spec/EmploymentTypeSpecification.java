package com.kairos.activity.spec;


import com.kairos.activity.response.dto.ShiftWithActivityDTO;
import com.kairos.activity.response.dto.shift.EmploymentType;
import com.kairos.activity.service.exception.ExceptionService;

import java.util.*;


/**
 * Created by vipul on 30/1/18.
 */
public class EmploymentTypeSpecification extends AbstractSpecification<ShiftWithActivityDTO> {

    private Set<Long> employmentTypeIds = new HashSet<>();
    private EmploymentType employmentType;
    ExceptionService exceptionService;

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
}

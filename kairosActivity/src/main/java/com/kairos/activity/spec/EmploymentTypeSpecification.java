package com.kairos.activity.spec;


import com.kairos.activity.persistence.model.activity.Activity;
import com.kairos.activity.response.dto.ShiftWithActivityDTO;
import com.kairos.activity.response.dto.shift.EmploymentType;
import com.kairos.activity.service.exception.ExceptionService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

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
    public boolean isSatisfied(ShiftWithActivityDTO shift) {
        if (Optional.ofNullable(shift.getActivity().getEmploymentTypes()).isPresent() && !shift.getActivity().getEmploymentTypes().isEmpty()) {
            employmentTypeIds.addAll(shift.getActivity().getEmploymentTypes());
            if (employmentTypeIds.contains(employmentType.getId())) {
                return true;
            }
            //exceptionService.invalidRequestException("message.activity.employement-type-match");
        }
        return true;

    }
}

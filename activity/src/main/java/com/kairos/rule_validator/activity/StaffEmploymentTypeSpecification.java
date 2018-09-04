package com.kairos.rule_validator.activity;

import com.kairos.persistence.model.activity.Activity;
import com.kairos.rule_validator.AbstractSpecification;
import com.kairos.user.staff.StaffDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StaffEmploymentTypeSpecification extends AbstractSpecification<StaffDTO> {
    private Activity activity;


    public StaffEmploymentTypeSpecification(Activity activity) {
        this.activity = activity;
    }

    @Override
    public boolean isSatisfied(StaffDTO staffDTO) {
        return false;
    }

    @Override
    public void validateRules(StaffDTO staffDTO) {

    }

    @Override
    public List<String> isSatisfiedString(StaffDTO staffDTO) {
        List<String> errorMessages = new ArrayList<>();
        if ((!Optional.ofNullable(activity.getEmploymentTypes()).isPresent()) || (!activity.getEmploymentTypes().contains(staffDTO.getEmploymentTypeId()))) {
            errorMessages.add("employment_type.absent.activity");
        }
        return errorMessages;
    }

}

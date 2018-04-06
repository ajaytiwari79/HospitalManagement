package com.kairos.activity.spec;


import com.kairos.activity.custom_exception.InvalidRequestException;
import com.kairos.activity.persistence.model.activity.Activity;
import com.kairos.activity.response.dto.shift.EmploymentType;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Created by vipul on 30/1/18.
 */
public class ActivityEmploymentTypeSpecification extends AbstractActivitySpecification<Activity> {

    private Set<Long> employmentTypeIds = new HashSet<>();
    private EmploymentType employmentType;

    public ActivityEmploymentTypeSpecification(EmploymentType employmentType) {
        this.employmentType = employmentType;
    }

    @Override
    public boolean isSatisfied(Activity activity) {
        if (Optional.ofNullable(activity.getEmploymentTypes()).isPresent() && !activity.getEmploymentTypes().isEmpty()) {
            employmentTypeIds.addAll(activity.getEmploymentTypes());
            if (employmentTypeIds.contains(employmentType.getId())) {
                return true;
            }
            throw new InvalidRequestException("Employment Type is not matched with this activity.");
        }
        return true;

    }
}

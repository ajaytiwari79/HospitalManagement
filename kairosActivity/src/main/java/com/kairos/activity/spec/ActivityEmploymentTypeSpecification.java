package com.kairos.activity.spec;


import com.kairos.activity.persistence.model.activity.Activity;
import com.kairos.activity.response.dto.shift.EmploymentType;
import com.kairos.activity.service.exception.ExceptionService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

import static com.kairos.activity.constants.AppConstants.SUCCESS;


/**
 * Created by vipul on 30/1/18.
 */
public class ActivityEmploymentTypeSpecification extends AbstractActivitySpecification<Activity> {

    private Set<Long> employmentTypeIds = new HashSet<>();
    private EmploymentType employmentType;
    @Autowired
    ExceptionService exceptionService;

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
            exceptionService.invalidRequestException("message.activity.employement-type-match");
        }
        return true;

    }

    @Override
    public List<String> isSatisfiedString(Activity activity) {
        if (Optional.ofNullable(activity.getEmploymentTypes()).isPresent() && !activity.getEmploymentTypes().isEmpty()) {
            employmentTypeIds.addAll(activity.getEmploymentTypes());
            if (employmentTypeIds.contains(employmentType.getId())) {
                return Collections.EMPTY_LIST;
            }
            return Arrays.asList("message.activity.employement-type-match");
        }
        return Collections.emptyList();

    }
}

package com.kairos.activity.spec;


import com.kairos.activity.custom_exception.InvalidRequestException;
import com.kairos.activity.persistence.model.activity.Activity;
import com.kairos.activity.response.dto.shift.Expertise;
import com.kairos.activity.service.exception.ExceptionService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * Created by vipul on 31/1/18.
 */
public class ActivityExpertiseSpecification extends AbstractActivitySpecification<Activity> {

    private Set<Long> expertiseIds = new HashSet<>();
    private Expertise expertise;

    public ActivityExpertiseSpecification(Expertise expertise) {
        this.expertise = expertise;
    }

    @Autowired
    ExceptionService exceptionService;

    @Override
    public boolean isSatisfied(Activity activity) {
        if (Optional.ofNullable(activity.getExpertises()).isPresent() && !activity.getExpertises().isEmpty()) {
            expertiseIds.addAll(activity.getExpertises());
            if (expertiseIds.contains(expertise.getId())) {
                return true;
            }
            exceptionService.invalidRequestException("message.activity.expertise.match");
        }
        return true;
    }

    @Override
    public List<String> isSatisfiedString(Activity activity) {
        if (Optional.ofNullable(activity.getExpertises()).isPresent() && !activity.getExpertises().isEmpty()) {
            expertiseIds.addAll(activity.getExpertises());
            if (expertiseIds.contains(expertise.getId())) {
                Collections.emptyList();
            }
            return Arrays.asList("message.activity.expertise.match");
        }

        return Collections.emptyList();

    }
}

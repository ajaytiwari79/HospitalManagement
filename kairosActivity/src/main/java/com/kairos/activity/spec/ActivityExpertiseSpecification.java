package com.kairos.activity.spec;


import com.kairos.activity.custom_exception.InvalidRequestException;
import com.kairos.activity.persistence.model.activity.Activity;
import com.kairos.activity.response.dto.shift.Expertise;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Created by vipul on 31/1/18.
 */
public class ActivityExpertiseSpecification extends AbstractActivitySpecification<Activity> {

    private Set<Long> expertiseIds = new HashSet<>();
    private Expertise expertise;

    public ActivityExpertiseSpecification(Expertise expertise) {
        this.expertise = expertise;
    }

    @Override
    public boolean isSatisfied(Activity activity) {
        if (Optional.ofNullable(activity.getExpertises()).isPresent() && !activity.getExpertises().isEmpty()) {
            expertiseIds.addAll(activity.getExpertises());
            if (expertiseIds.contains(expertise.getId())) {
                return true;
            }
            throw new InvalidRequestException("Expertise does not match with this activity.");
        }
        return true;
    }
}

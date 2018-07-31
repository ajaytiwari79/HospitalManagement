package com.kairos.rule_validator.activity;

import com.kairos.persistence.model.activity.Activity;
import com.kairos.rule_validator.AbstractSpecification;
import com.kairos.service.locale.LocaleService;
import com.kairos.service.locale.LocaleServiceImpl;
import com.kairos.user.staff.staff.StaffExpertiseWrapper;
import org.springframework.util.CollectionUtils;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class StaffExpertiseSpecification extends AbstractSpecification<StaffExpertiseWrapper> {
    private Activity activity;
    List<String> errorMessages = new ArrayList<>();

    public StaffExpertiseSpecification(Activity activity) {
        this.activity = activity;
    }

    @Override
    public boolean isSatisfied(StaffExpertiseWrapper staffExpertiseSpecification) {
        return false;
    }

    @Override
    public List<String> isSatisfiedString(StaffExpertiseWrapper staffExpertiseSpecification) {
        if (!CollectionUtils.containsAny(activity.getExpertises(), staffExpertiseSpecification.getExpertiseIds())) {
            errorMessages.add("expertise.absent.activity");
        }
        return errorMessages;
    }
}

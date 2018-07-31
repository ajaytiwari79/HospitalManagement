package com.kairos.rule_validator.activity;

import com.kairos.persistence.model.activity.Activity;
import com.kairos.rule_validator.AbstractSpecification;
import com.kairos.service.locale.LocaleService;
import com.kairos.user.staff.staff.StaffExpertiseWrapper;
import org.springframework.util.CollectionUtils;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StaffEmploymentTypeSpecification extends AbstractSpecification<StaffExpertiseWrapper> {
    private Activity activity;
    List<String> errorMessages = new ArrayList<>();

    public StaffEmploymentTypeSpecification(Activity activity) {
        this.activity = activity;
    }

    @Override
    public boolean isSatisfied(StaffExpertiseWrapper staffEmploymentTypeSpecification) {
        return false;
    }

    @Override
    public List<String> isSatisfiedString(StaffExpertiseWrapper staffEmploymentTypeSpecification) {
        if (!activity.getEmploymentTypes().contains(staffEmploymentTypeSpecification.getEmploymentTypeId())) {
            errorMessages.add("employment_type.absent.activity");
        }
        return errorMessages;
    }

}

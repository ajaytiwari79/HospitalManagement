package com.kairos.rule_validator.activity;

import com.kairos.persistence.model.activity.Activity;
import com.kairos.rule_validator.AbstractSpecification;
import com.kairos.user.staff.StaffDTO;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class StaffExpertiseSpecification extends AbstractSpecification<StaffDTO> {
    private Activity activity;


    public StaffExpertiseSpecification(Activity activity) {
        this.activity = activity;
    }

    @Override
    public boolean isSatisfied(StaffDTO staffExpertiseSpecification) {
        return false;
    }

    @Override
    public void validateRules(StaffDTO staffDTO) {

    }

    @Override
    public List<String> isSatisfiedString(StaffDTO staffDTO) {
        List<String> errorMessages = new ArrayList<>();
        if (!CollectionUtils.containsAny(activity.getExpertises(), staffDTO.getExpertiseIds())) {
            errorMessages.add("expertise.absent.activity");
        }
        return errorMessages;
    }
}

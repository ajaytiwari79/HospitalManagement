package com.kairos.rule_validator.activity;

import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.staff.personal_details.StaffPersonalDetail;
import com.kairos.rule_validator.AbstractSpecification;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

import static com.kairos.commons.utils.ObjectUtils.isNotNull;
import static com.kairos.constants.ActivityMessagesConstants.EXPERTISE_ABSENT_ACTIVITY;

public class StaffExpertiseSpecification extends AbstractSpecification<StaffPersonalDetail> {
    private Activity activity;


    public StaffExpertiseSpecification(Activity activity) {
        this.activity = activity;
    }

    @Override
    public boolean isSatisfied(StaffPersonalDetail staffExpertiseSpecification) {
        return false;
    }

    @Override
    public void validateRules(StaffPersonalDetail staffDTO) {
        //Not in use
    }

    @Override
    public List<String> isSatisfiedString(StaffPersonalDetail staffDTO) {
        List<String> errorMessages = new ArrayList<>();
        if (isNotNull(staffDTO) && !CollectionUtils.containsAny(activity.getExpertises(), staffDTO.getExpertiseIds())) {
            errorMessages.add(EXPERTISE_ABSENT_ACTIVITY);
        }
        return errorMessages;
    }
}

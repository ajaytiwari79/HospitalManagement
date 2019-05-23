package com.kairos.rule_validator.activity;

import com.kairos.dto.user.staff.StaffDTO;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.rule_validator.AbstractSpecification;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

import static com.kairos.commons.utils.ObjectUtils.isNotNull;
import static com.kairos.constants.ActivityMessagesConstants.EXPERTISE_ABSENT_ACTIVITY;

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
        //Not in use
    }

    @Override
    public List<String> isSatisfiedString(StaffDTO staffDTO) {
        List<String> errorMessages = new ArrayList<>();
        if (isNotNull(staffDTO) && !CollectionUtils.containsAny(activity.getExpertises(), staffDTO.getExpertiseIds())) {
            errorMessages.add(EXPERTISE_ABSENT_ACTIVITY);
        }
        return errorMessages;
    }
}

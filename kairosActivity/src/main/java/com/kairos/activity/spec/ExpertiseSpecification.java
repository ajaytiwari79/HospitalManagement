package com.kairos.activity.spec;


import com.kairos.activity.persistence.model.activity.Activity;
import com.kairos.activity.response.dto.ShiftWithActivityDTO;
import com.kairos.activity.response.dto.shift.Expertise;
import com.kairos.activity.service.exception.ExceptionService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Created by vipul on 31/1/18.
 */
public class ExpertiseSpecification extends AbstractSpecification<ShiftWithActivityDTO> {

    private Set<Long> expertiseIds = new HashSet<>();
    private Expertise expertise;

    public ExpertiseSpecification(Expertise expertise) {
        this.expertise = expertise;
    }

    @Override
    public boolean isSatisfied(ShiftWithActivityDTO shift) {
        if (Optional.ofNullable(shift.getActivity().getExpertises()).isPresent() && !shift.getActivity().getExpertises().isEmpty()) {
            expertiseIds.addAll(shift.getActivity().getExpertises());
            if (expertiseIds.contains(expertise.getId())) {
                return true;
            }
            //exceptionService.invalidRequestException("message.activity.expertise.match");
        }
        return true;
    }
}

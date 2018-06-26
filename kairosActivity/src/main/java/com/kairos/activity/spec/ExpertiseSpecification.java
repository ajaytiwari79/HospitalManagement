package com.kairos.activity.spec;


import com.kairos.activity.persistence.model.activity.Activity;
import com.kairos.activity.response.dto.ShiftWithActivityDTO;
import com.kairos.activity.response.dto.shift.Expertise;
import com.kairos.activity.service.exception.ExceptionService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * Created by vipul on 31/1/18.
 */
public class ExpertiseSpecification extends AbstractSpecification<ShiftWithActivityDTO> {

    private Set<Long> expertiseIds = new HashSet<>();
    private Expertise expertise;

    public ExpertiseSpecification(Expertise expertise) {
        this.expertise = expertise;
    }

    @Autowired
    ExceptionService exceptionService;

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

    @Override
    public List<String> isSatisfiedString(ShiftWithActivityDTO shift) {
        if (Optional.ofNullable(shift.getActivity().getExpertises()).isPresent() && !shift.getActivity().getExpertises().isEmpty()) {
            expertiseIds.addAll(shift.getActivity().getExpertises());
            if (expertiseIds.contains(expertise.getId())) {
                return Collections.emptyList();
            }
            return Arrays.asList("message.activity.expertise.match");
        }
        return Collections.emptyList();
    }

}

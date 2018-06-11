package com.kairos.activity.persistence.repository.priority_group;

import com.kairos.response.dto.web.open_shift.OpenShiftResponseDTO;

public interface CustomPriorityGroupRepository {

    public OpenShiftResponseDTO getOpenshiftByPriorityGroup();

}

package com.kairos.persistence.repository.priority_group;

import com.kairos.activity.open_shift.OpenShiftResponseDTO;

public interface CustomPriorityGroupRepository {

    public OpenShiftResponseDTO getOpenshiftByPriorityGroup();

}

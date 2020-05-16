package com.kairos.dto.activity.open_shift;

import com.kairos.dto.activity.open_shift.priority_group.PriorityGroupDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@NoArgsConstructor
public class OrderOpenshiftResponseDTO {

    OrderResponseDTO order;
    List<OpenShiftResponseDTO> openshifts;
    List<PriorityGroupDTO> priorityGroups;

    public OrderOpenshiftResponseDTO(List<OpenShiftResponseDTO> openshifts, List<PriorityGroupDTO> priorityGroups) {
        this.openshifts = openshifts;
        this.priorityGroups = priorityGroups;
    }

}

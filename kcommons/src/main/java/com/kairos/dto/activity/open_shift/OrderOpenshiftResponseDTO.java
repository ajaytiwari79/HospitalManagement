package com.kairos.dto.activity.open_shift;

import com.kairos.dto.activity.open_shift.priority_group.PriorityGroupDTO;

import java.util.List;

public class OrderOpenshiftResponseDTO {

    OrderResponseDTO order;
    List<OpenShiftResponseDTO> openshifts;
    List<PriorityGroupDTO> priorityGroups;

    public OrderOpenshiftResponseDTO() {
    }

    public OrderOpenshiftResponseDTO(List<OpenShiftResponseDTO> openshifts, List<PriorityGroupDTO> priorityGroups) {
        this.openshifts = openshifts;
        this.priorityGroups = priorityGroups;
    }

    public OrderResponseDTO getOrder() {
        return order;
    }

    public void setOrder(OrderResponseDTO order) {
        this.order = order;
    }

    public List<OpenShiftResponseDTO> getOpenshifts() {
        return openshifts;
    }

    public void setOpenshifts(List<OpenShiftResponseDTO> openshifts) {
        this.openshifts = openshifts;
    }

    public List<PriorityGroupDTO> getPriorityGroups() {
        return priorityGroups;
    }

    public void setPriorityGroups(List<PriorityGroupDTO> priorityGroups) {
        this.priorityGroups = priorityGroups;
    }
}

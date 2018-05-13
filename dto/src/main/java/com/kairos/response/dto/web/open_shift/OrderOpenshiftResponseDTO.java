package com.kairos.response.dto.web.open_shift;

import java.util.List;

public class OrderOpenshiftResponseDTO {

    OrderResponseDTO order;
    List<OpenShiftResponseDTO> openshifts;

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

}

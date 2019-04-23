package com.kairos.persistence.model.staff;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Created by oodles on 4/1/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class StaffClientMultipleData {

    private List<Long> removedStaffId;
    private Long clientId;
    private List<Long> selectedStaffId;


    public StaffClientMultipleData() {
        //Default Constructor
    }
    public List<Long> getRemovedStaffId() {
        return removedStaffId;
    }

    public void setRemovedStaffId(List<Long> removedStaffId) {
        this.removedStaffId = removedStaffId;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public List<Long> getSelectedStaffId() {
        return selectedStaffId;
    }

    public void setSelectedStaffId(List<Long> selectedStaffId) {
        this.selectedStaffId = selectedStaffId;
    }
}

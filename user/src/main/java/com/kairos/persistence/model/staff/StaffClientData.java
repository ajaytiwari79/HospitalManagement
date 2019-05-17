package com.kairos.persistence.model.staff;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Created by oodles on 4/1/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class StaffClientData {

    private List<Long> staffIds;
    private Long clientId;

    public StaffClientData() {
        //Default Constructor
    }
    public List<Long> getStaffIds() {
        return staffIds;
    }

    public void setStaffIds(List<Long> staffIds) {
        this.staffIds = staffIds;
    }


    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }


}

package com.kairos.dto.user.organization.skill;

import java.util.List;
import java.util.Map;

public class OrganizationClientWrapper {
    private List<Map<String, Object>> clientList;
    private Map<String, Object> timeSlotData;
    private Long staffId;

    public OrganizationClientWrapper() {
        //default
    }

    public OrganizationClientWrapper(List<Map<String, Object>> clientList, Map<String, Object> timeSlotData) {
        this.clientList = clientList;
        this.timeSlotData = timeSlotData;
    }

    public List<Map<String, Object>> getClientList() {
        return clientList;
    }

    public void setClientList(List<Map<String, Object>> clientList) {
        this.clientList = clientList;
    }

    public Map<String, Object> getTimeSlotData() {
        return timeSlotData;
    }

    public void setTimeSlotData(Map<String, Object> timeSlotData) {
        this.timeSlotData = timeSlotData;
    }

    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }

}

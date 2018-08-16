package com.kairos.user.staff.staff_settings;

import java.util.List;
import java.util.Set;

public class StaffAndActivitySettingWrapper {
    private Set<Long> staffIds;
    private List<StaffActivitySettingDTO> staffActivitySettings;

    public StaffAndActivitySettingWrapper() {
        //Default Constructor
    }

    public StaffAndActivitySettingWrapper(Set<Long> staffIds, List<StaffActivitySettingDTO> staffActivitySettings) {
        this.staffIds = staffIds;
        this.staffActivitySettings = staffActivitySettings;
    }

    public Set<Long> getStaffIds() {
        return staffIds;
    }

    public void setStaffIds(Set<Long> staffIds) {
        this.staffIds = staffIds;
    }

    public List<StaffActivitySettingDTO> getStaffActivitySettings() {
        return staffActivitySettings;
    }

    public void setStaffActivitySettings(List<StaffActivitySettingDTO> staffActivitySettings) {
        this.staffActivitySettings = staffActivitySettings;
    }
}

package com.kairos.activity.unit_settings;

public class TAndAGracePeriodSettingDTO {


    private int staffGracePeriodDays;
    private int managementGracePeriodDays;

    public TAndAGracePeriodSettingDTO() {
    }

    public TAndAGracePeriodSettingDTO(int staffGracePeriodDays, int managementGracePeriodDays) {
        this.staffGracePeriodDays = staffGracePeriodDays;
        this.managementGracePeriodDays = managementGracePeriodDays;
    }

    public int getStaffGracePeriodDays() {
        return staffGracePeriodDays;
    }

    public void setStaffGracePeriodDays(int staffGracePeriodDays) {
        this.staffGracePeriodDays = staffGracePeriodDays;
    }

    public int getManagementGracePeriodDays() {
        return managementGracePeriodDays;
    }

    public void setManagementGracePeriodDays(int managementGracePeriodDays) {
        this.managementGracePeriodDays = managementGracePeriodDays;
    }
}

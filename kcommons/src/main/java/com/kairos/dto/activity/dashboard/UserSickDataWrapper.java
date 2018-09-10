package com.kairos.dto.activity.dashboard;

import com.kairos.dto.activity.activity.ActivityDTO;
import com.kairos.dto.user.staff.staff.StaffResultDTO;

import java.util.List;

/**
 * CreatedBy vipulpandey on 30/8/18
 **/
public class UserSickDataWrapper {
    private List<StaffResultDTO>  staffOrganizations;
    private List<ActivityDTO> activities;

    public List<StaffResultDTO> getStaffOrganizations() {
        return staffOrganizations;
    }

    public void setStaffOrganizations(List<StaffResultDTO> staffOrganizations) {
        this.staffOrganizations = staffOrganizations;
    }

    public List<ActivityDTO> getActivities() {
        return activities;
    }

    public void setActivities(List<ActivityDTO> activities) {
        this.activities = activities;
    }
}

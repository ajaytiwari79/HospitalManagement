package com.kairos.dto.activity.break_settings;/*
 *Created By Pavan on 27/8/18
 *
 */


import com.kairos.dto.activity.activity.ActivityDTO;
import com.kairos.dto.activity.unit_settings.FlexibleTimeSettingDTO;

import java.util.List;

public class BreakSettingAndActivitiesWrapper {
    private List<BreakSettingsDTO> breakSettings;
    private List<ActivityDTO> paidActivities;
    private List<ActivityDTO> unpaidActivities;
    private FlexibleTimeSettingDTO flexibleTimeSettings;

    public BreakSettingAndActivitiesWrapper() {
        //Default Constructor
    }

    public BreakSettingAndActivitiesWrapper(List<BreakSettingsDTO> breakSettings, List<ActivityDTO> paidActivities, List<ActivityDTO> unpaidActivities, FlexibleTimeSettingDTO flexibleTimeSettings) {
        this.breakSettings = breakSettings;
        this.paidActivities = paidActivities;
        this.unpaidActivities = unpaidActivities;
        this.flexibleTimeSettings = flexibleTimeSettings;
    }

    public List<BreakSettingsDTO> getBreakSettings() {
        return breakSettings;
    }

    public void setBreakSettings(List<BreakSettingsDTO> breakSettings) {
        this.breakSettings = breakSettings;
    }

    public List<ActivityDTO> getPaidActivities() {
        return paidActivities;
    }

    public void setPaidActivities(List<ActivityDTO> paidActivities) {
        this.paidActivities = paidActivities;
    }

    public List<ActivityDTO> getUnpaidActivities() {
        return unpaidActivities;
    }

    public void setUnpaidActivities(List<ActivityDTO> unpaidActivities) {
        this.unpaidActivities = unpaidActivities;
    }

    public FlexibleTimeSettingDTO getFlexibleTimeSettings() {
        return flexibleTimeSettings;
    }

    public void setFlexibleTimeSettings(FlexibleTimeSettingDTO flexibleTimeSettings) {
        this.flexibleTimeSettings = flexibleTimeSettings;
    }
}

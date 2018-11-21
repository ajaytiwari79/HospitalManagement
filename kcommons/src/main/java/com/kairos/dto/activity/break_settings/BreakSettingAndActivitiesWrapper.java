package com.kairos.dto.activity.break_settings;/*
 *Created By Pavan on 27/8/18
 *
 */


import com.kairos.dto.activity.activity.ActivityDTO;
import com.kairos.dto.activity.unit_settings.FlexibleTimeSettingDTO;

import java.util.List;

public class BreakSettingAndActivitiesWrapper {
    private List<BreakSettingsDTO> breakSettings;
    private List<ActivityDTO> activities;
    private FlexibleTimeSettingDTO flexibleTimeSettings;

    public BreakSettingAndActivitiesWrapper() {
        //Default Constructor
    }
    public BreakSettingAndActivitiesWrapper(List<BreakSettingsDTO> breakSettings, List<ActivityDTO> activities) {
        this.breakSettings = breakSettings;
        this.activities=activities;

    }
    public BreakSettingAndActivitiesWrapper(List<BreakSettingsDTO> breakSettings, List<ActivityDTO> activities, FlexibleTimeSettingDTO flexibleTimeSettings) {
        this.breakSettings = breakSettings;
        this.activities=activities;
        this.flexibleTimeSettings = flexibleTimeSettings;
    }

    public List<BreakSettingsDTO> getBreakSettings() {
        return breakSettings;
    }

    public void setBreakSettings(List<BreakSettingsDTO> breakSettings) {
        this.breakSettings = breakSettings;
    }

    public List<ActivityDTO> getActivities() {
        return activities;
    }

    public void setActivities(List<ActivityDTO> activities) {
        this.activities = activities;
    }

    public FlexibleTimeSettingDTO getFlexibleTimeSettings() {
        return flexibleTimeSettings;
    }

    public void setFlexibleTimeSettings(FlexibleTimeSettingDTO flexibleTimeSettings) {
        this.flexibleTimeSettings = flexibleTimeSettings;
    }
}

package com.kairos.user.user.user_personalized_settings;

import com.kairos.enums.AbsenceViewSettings;

/**
 * Created by yatharth on 1/5/18.
 */
public class SelfRosteringViewDto {
    private AbsenceViewSettings absenceViewSettings;

    public SelfRosteringViewDto() {
        //Default Constructor
    }

    public SelfRosteringViewDto(AbsenceViewSettings absenceViewSettings) {
        this.absenceViewSettings = absenceViewSettings;

    }

    public AbsenceViewSettings getAbsenceViewSettings() {
        return absenceViewSettings;
    }

    public void setAbsenceViewSettings(AbsenceViewSettings absenceViewSettings) {
        this.absenceViewSettings = absenceViewSettings;
    }


}

package com.kairos.persistence.model.user.user_personalized_settings;

import com.kairos.persistence.model.enums.AbsenceViewSettings;

/**
 * Created by yatharth on 1/5/18.
 */
public class SelfRosteringViewDto {
    private AbsenceViewSettings absenceViewSettings;

    public AbsenceViewSettings getAbsenceViewSettings() {
        return absenceViewSettings;
    }

    public void setAbsenceViewSettings(AbsenceViewSettings absenceViewSettings) {
        this.absenceViewSettings = absenceViewSettings;
    }
    public SelfRosteringViewDto() {

    }

    public SelfRosteringViewDto(AbsenceViewSettings absenceViewSettings) {
        this.absenceViewSettings = absenceViewSettings;

    }

}

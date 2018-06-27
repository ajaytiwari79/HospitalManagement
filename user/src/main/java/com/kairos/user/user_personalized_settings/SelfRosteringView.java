package com.kairos.user.user_personalized_settings;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.enums.AbsenceViewSettings;
import org.neo4j.ogm.annotation.NodeEntity;

/**
 * Created by yatharth on 1/5/18.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@NodeEntity
public class SelfRosteringView extends UserBaseEntity {

    private AbsenceViewSettings absenceViewSettings;

    public AbsenceViewSettings getAbsenceViewSettings() {
        return absenceViewSettings;
    }

    public void setAbsenceViewSettings(AbsenceViewSettings absenceViewSettings) {
        this.absenceViewSettings = absenceViewSettings;
    }
    public SelfRosteringView() {

    }

    public SelfRosteringView(AbsenceViewSettings absenceViewSettings) {

        this.absenceViewSettings = absenceViewSettings;
    }

}

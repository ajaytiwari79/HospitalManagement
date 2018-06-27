package com.kairos.persistence.model.unit_settings;

import com.kairos.persistence.model.common.MongoBaseEntity;
import com.kairos.activity.unit_settings.activity_configuration.AbsencePlannedTime;
import com.kairos.activity.unit_settings.activity_configuration.PresencePlannedTime;

public class ActivityConfiguration extends MongoBaseEntity {
    private Long unitId;
    private PresencePlannedTime presencePlannedTime;
    private AbsencePlannedTime absencePlannedTime;

    public ActivityConfiguration() {
        // dc
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public PresencePlannedTime getPresencePlannedTime() {
        return presencePlannedTime;
    }

    public void setPresencePlannedTime(PresencePlannedTime presencePlannedTime) {
        this.presencePlannedTime = presencePlannedTime;
    }

    public AbsencePlannedTime getAbsencePlannedTime() {
        return absencePlannedTime;
    }


    public void setAbsencePlannedTime(AbsencePlannedTime absencePlannedTime) {

        this.absencePlannedTime = absencePlannedTime;
    }

    public ActivityConfiguration(Long unitId, PresencePlannedTime presencePlannedTime) {
        this.unitId = unitId;
        this.presencePlannedTime = presencePlannedTime;
    }
    public ActivityConfiguration(Long unitId, AbsencePlannedTime absencePlannedTime) {
        this.unitId = unitId;
        this.absencePlannedTime = absencePlannedTime;
    }

}

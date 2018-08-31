package com.kairos.persistence.model.unit_settings;

import com.kairos.persistence.model.common.MongoBaseEntity;
import com.kairos.activity.unit_settings.activity_configuration.AbsencePlannedTime;
import com.kairos.activity.unit_settings.activity_configuration.PresencePlannedTime;
import org.springframework.data.mongodb.core.index.Indexed;

public class ActivityConfiguration extends MongoBaseEntity {
    @Indexed
    private Long unitId;
    private PresencePlannedTime presencePlannedTime;
    private AbsencePlannedTime absencePlannedTime;
    @Indexed
    private Long countryId;

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

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public ActivityConfiguration(PresencePlannedTime presencePlannedTime, Long countryId) {
        this.presencePlannedTime = presencePlannedTime;
        this.countryId = countryId;
    }

    public ActivityConfiguration(AbsencePlannedTime absencePlannedTime, Long countryId) {
        this.absencePlannedTime = absencePlannedTime;
        this.countryId = countryId;
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

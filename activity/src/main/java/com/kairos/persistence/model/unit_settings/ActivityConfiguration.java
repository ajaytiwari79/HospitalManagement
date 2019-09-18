package com.kairos.persistence.model.unit_settings;

import com.kairos.dto.activity.unit_settings.activity_configuration.AbsencePlannedTime;
import com.kairos.dto.activity.unit_settings.activity_configuration.NonWorkingPlannedTime;
import com.kairos.dto.activity.unit_settings.activity_configuration.PresencePlannedTime;
import com.kairos.persistence.model.common.MongoBaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;
@Getter
@Setter
@NoArgsConstructor
public class ActivityConfiguration extends MongoBaseEntity {
    @Indexed
    private Long unitId;
    private PresencePlannedTime presencePlannedTime;
    private AbsencePlannedTime absencePlannedTime;
    private NonWorkingPlannedTime nonWorkingPlannedTime;
    @Indexed
    private Long countryId;

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

    public ActivityConfiguration(Long unitId, NonWorkingPlannedTime nonWorkingPlannedTime) {
        this.unitId = unitId;
        this.nonWorkingPlannedTime = nonWorkingPlannedTime;
    }

    public ActivityConfiguration(NonWorkingPlannedTime nonWorkingPlannedTime, Long countryId) {
        this.nonWorkingPlannedTime = nonWorkingPlannedTime;
        this.countryId = countryId;
    }
}

package com.kairos.activity.persistence.model.unit_settings;

import com.kairos.activity.persistence.model.common.MongoBaseEntity;
import com.kairos.response.dto.web.unit_settings.OpenShiftPhaseSetting;

public class UnitSetting extends MongoBaseEntity {
    private Integer minOpenShiftHours;
    private OpenShiftPhaseSetting openShiftPhaseSetting;
    private Long unitId;

    public UnitSetting() {
        //Default Constructor
    }

    public Integer getMinOpenShiftHours() {
        return minOpenShiftHours;
    }

    public void setMinOpenShiftHours(Integer minOpenShiftHours) {
        this.minOpenShiftHours = minOpenShiftHours;
    }

    public OpenShiftPhaseSetting getOpenShiftPhaseSetting() {
        return openShiftPhaseSetting;
    }

    public void setOpenShiftPhaseSetting(OpenShiftPhaseSetting openShiftPhaseSetting) {
        this.openShiftPhaseSetting = openShiftPhaseSetting;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }
}

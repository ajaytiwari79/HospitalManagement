package com.kairos.activity.persistence.model.unit_settings;


import com.kairos.activity.persistence.model.common.MongoBaseEntity;
import com.kairos.response.dto.web.unit_settings.OpenShiftPhaseSetting;

public class UnitSetting extends MongoBaseEntity {
    private OpenShiftPhaseSetting openShiftPhaseSetting;
    private Long unitId;

    public UnitSetting() {
        //Default Constructor
    }

    public UnitSetting(OpenShiftPhaseSetting openShiftPhaseSetting, Long unitId) {
        this.openShiftPhaseSetting = openShiftPhaseSetting;
        this.unitId = unitId;
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

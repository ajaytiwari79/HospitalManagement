package com.kairos.dto.activity.unit_settings;

import java.math.BigInteger;

public class UnitSettingDTO {
    private BigInteger id;
    private OpenShiftPhaseSetting openShiftPhaseSetting;

    public UnitSettingDTO() {
        //Default Constructor
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public OpenShiftPhaseSetting getOpenShiftPhaseSetting() {
        return openShiftPhaseSetting;
    }

    public void setOpenShiftPhaseSetting(OpenShiftPhaseSetting openShiftPhaseSetting) {
        this.openShiftPhaseSetting = openShiftPhaseSetting;
    }
}

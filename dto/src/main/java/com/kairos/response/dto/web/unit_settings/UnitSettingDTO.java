package com.kairos.response.dto.web.unit_settings;

import java.math.BigInteger;

public class UnitSettingDTO {
    private BigInteger id;
    private OpenShiftPhaseSetting openShiftPhaseSetting;
    private Integer minShiftHours;
    private Long unitId;

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

    public Integer getMinShiftHours() {
        return minShiftHours;
    }

    public void setMinShiftHours(Integer minShiftHours) {
        this.minShiftHours = minShiftHours;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }
}

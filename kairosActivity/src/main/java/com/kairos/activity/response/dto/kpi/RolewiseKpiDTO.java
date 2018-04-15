package com.kairos.activity.response.dto.kpi;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class RolewiseKpiDTO {
    private BigInteger roleId;
    private List<BigInteger> modulewiseKpiIds = new ArrayList<>();

    public BigInteger getRoleId() {
        return roleId;
    }

    public void setRoleId(BigInteger roleId) {
        this.roleId = roleId;
    }

    public List<BigInteger> getModulewiseKpiIds() {
        return modulewiseKpiIds;
    }

    public void setModulewiseKpiIds(List<BigInteger> modulewiseKpiIds) {
        this.modulewiseKpiIds = modulewiseKpiIds;
    }
}

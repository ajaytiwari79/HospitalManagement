package com.kairos.dto.activity.counter.distribution.access_group;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/*
 * @author: mohit.shakya@oodlestechnologies.com
 * @dated: Jun/26/2018
 */

public class RoleCounterDTO {
    private BigInteger roleId;
    private List<BigInteger> modulewiseCounterIds = new ArrayList<>();

    public BigInteger getRoleId() {
        return roleId;
    }

    public void setRoleId(BigInteger roleId) {
        this.roleId = roleId;
    }

    public List<BigInteger> getModuleCounterIds() {
        return modulewiseCounterIds;
    }

    public void setModulewiseCounterIds(List<BigInteger> modulewiseCounterIds) {
        this.modulewiseCounterIds = modulewiseCounterIds;
    }
}

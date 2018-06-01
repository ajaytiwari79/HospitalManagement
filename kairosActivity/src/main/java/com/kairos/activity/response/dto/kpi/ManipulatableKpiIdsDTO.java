package com.kairos.activity.response.dto.kpi;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class ManipulatableKpiIdsDTO {
    private List<BigInteger> kpiIdsToRemove = new ArrayList<BigInteger>();
    private List<BigInteger> kpiIdsToAdd = new ArrayList<BigInteger>();

    public List<BigInteger> getKpiIdsToRemove() {
        return kpiIdsToRemove;
    }

    public void setKpiIdsToRemove(List<BigInteger> kpiIdsToRemove) {
        this.kpiIdsToRemove = kpiIdsToRemove;
    }

    public List<BigInteger> getKpiIdsToAdd() {
        return kpiIdsToAdd;
    }

    public void setKpiIdsToAdd(List<BigInteger> kpiIdsToAdd) {
        this.kpiIdsToAdd = kpiIdsToAdd;
    }
}

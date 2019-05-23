package com.kairos.dto.activity.kpi;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigInteger;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class KPIResponseDTO {

    private BigInteger kpiId;

    private String kpiName;

    private Map<Long ,Double> staffKPIValue;

    public BigInteger getKpiId() {
        return kpiId;
    }

    public void setKpiId(BigInteger kpiId) {
        this.kpiId = kpiId;
    }

    public String getKpiName() {
        return kpiName;
    }

    public void setKpiName(String kpiName) {
        this.kpiName = kpiName;
    }

    public Map<Long, Double> getStaffKPIValue() {
        return staffKPIValue;
    }

    public void setStaffKPIValue(Map<Long, Double> staffKPIValue) {
        this.staffKPIValue = staffKPIValue;
    }
}

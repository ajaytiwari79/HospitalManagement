package com.kairos.dto.activity.kpi;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class KPISetResponseDTO {

    private BigInteger kpiId;

    private String kpiSetName;

    private String kpiName;

    private BigInteger kpiSetId;

    private Map<Long ,Double> staffKPIValue;

    private List<KPISetResponseDTO> kpiData;


    public BigInteger getKpiId() {
        return kpiId;
    }

    public void setKpiId(BigInteger kpiId) {
        this.kpiId = kpiId;
    }

    public String getKpiSetName() {
        return kpiSetName;
    }

    public void setKpiSetName(String kpiSetName) {
        this.kpiSetName = kpiSetName;
    }

    public Map<Long, Double> getStaffKPIValue() {
        return staffKPIValue;
    }

    public void setStaffKPIValue(Map<Long, Double> staffKPIValue) {
        this.staffKPIValue = staffKPIValue;
    }

    public List<KPISetResponseDTO> getKpiData() {
        return kpiData;
    }

    public void setKpiData(List<KPISetResponseDTO> kpiData) {
        this.kpiData = kpiData;
    }

    public KPISetResponseDTO(BigInteger kpiId, String kpiName, Map<Long, Double> staffKPIValue) {
        this.kpiId = kpiId;
        this.kpiName = kpiName;
        this.staffKPIValue = staffKPIValue;
    }

    public String getKpiName() {
        return kpiName;
    }

    public void setKpiName(String kpiName) {
        this.kpiName = kpiName;
    }

    public BigInteger getKpiSetId() {
        return kpiSetId;
    }

    public void setKpiSetId(BigInteger kpiSetId) {
        this.kpiSetId = kpiSetId;
    }

    public KPISetResponseDTO() {
    }
}

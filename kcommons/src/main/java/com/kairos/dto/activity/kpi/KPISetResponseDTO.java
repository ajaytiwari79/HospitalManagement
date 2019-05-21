package com.kairos.dto.activity.kpi;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KPISetResponseDTO {

    private BigInteger kpiId;

    private String kpiSetName;

    private List<Map<String ,Number> >data;

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

    public List<Map<String, Number>> getData() {
        return data;
    }

    public void setData(List<Map<String, Number>> data) {
        this.data = data;
    }

    public KPISetResponseDTO(BigInteger kpiId, String kpiSetName, List<Map<String, Number>> data) {
        this.kpiId = kpiId;
        this.kpiSetName = kpiSetName;
        this.data = data;
    }

    public KPISetResponseDTO() {
    }
}

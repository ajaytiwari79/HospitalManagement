package com.kairos.dto.activity.kpi;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class KPISetResponseDTO extends KPIResponseDTO {

    private String kpiSetName;

    private BigInteger kpiSetId;

    private List<KPIResponseDTO> kpiData;

    public String getKpiSetName() {
        return kpiSetName;
    }

    public void setKpiSetName(String kpiSetName) {
        this.kpiSetName = kpiSetName;
    }

    public BigInteger getKpiSetId() {
        return kpiSetId;
    }

    public void setKpiSetId(BigInteger kpiSetId) {
        this.kpiSetId = kpiSetId;
    }

    public List<KPIResponseDTO> getKpiData() {
        return kpiData;
    }

    public void setKpiData(List<KPIResponseDTO> kpiData) {
        this.kpiData = kpiData;
    }
}

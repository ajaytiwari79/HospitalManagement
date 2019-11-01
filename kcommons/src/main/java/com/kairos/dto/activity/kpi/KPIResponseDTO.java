package com.kairos.dto.activity.kpi;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class KPIResponseDTO {

    private BigInteger kpiId;

    private String kpiName;

    private Map<Long, Double> staffKPIValue;

    private Map<LocalDateTime, Double> kpiValue;

    public KPIResponseDTO() {
        //DC
    }

    public KPIResponseDTO(BigInteger kpiId, String kpiName, Map<Long, Double> staffKPIValue) {
        this.kpiId = kpiId;
        this.kpiName = kpiName;
        this.staffKPIValue = staffKPIValue;
    }
}

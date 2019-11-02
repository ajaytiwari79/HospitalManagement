package com.kairos.dto.activity.kpi;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.activity.counter.enums.KPISetType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class KPISetResponseDTO extends KPIResponseDTO {

    private String kpiSetName;

    private BigInteger kpiSetId;

    private KPISetType kpiSetType;

    private List<KPIResponseDTO> kpiData;

}

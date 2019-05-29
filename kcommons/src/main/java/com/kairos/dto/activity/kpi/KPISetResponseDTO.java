package com.kairos.dto.activity.kpi;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class KPISetResponseDTO extends KPIResponseDTO {

    private String kpiSetName;

    private BigInteger kpiSetId;

    private List<KPIResponseDTO> kpiData;

}

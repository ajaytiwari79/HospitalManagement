package com.kairos.dto.planner.solverconfig;

import com.kairos.enums.wta.WTATemplateType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.List;

@Deprecated
@Getter
@Setter
public class SolverConfigWTADTO {
    private BigInteger id;
    private List<WTATemplateType> templateTypes;
    private long terminationSeconds;

}

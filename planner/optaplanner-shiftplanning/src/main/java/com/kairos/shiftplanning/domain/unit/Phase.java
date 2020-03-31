package com.kairos.shiftplanning.domain.unit;

import com.kairos.enums.phase.PhaseDefaultName;
import com.kairos.enums.phase.PhaseType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
@NoArgsConstructor
public class Phase {
    private BigInteger id;
    private PhaseDefaultName phaseEnum;
    private PhaseType phaseType;
}

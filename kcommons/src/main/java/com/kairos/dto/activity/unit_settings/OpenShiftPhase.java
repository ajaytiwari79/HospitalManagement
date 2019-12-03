package com.kairos.dto.activity.unit_settings;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OpenShiftPhase {
    private BigInteger phaseId;
    private String phaseName;
    private boolean solveUnderStaffingOverStaffing;
    private int sequence;
}

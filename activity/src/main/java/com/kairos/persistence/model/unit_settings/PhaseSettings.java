package com.kairos.persistence.model.unit_settings;

import com.kairos.persistence.model.common.MongoBaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigInteger;
@Getter
@Setter
@NoArgsConstructor
public class PhaseSettings extends MongoBaseEntity implements Serializable {
    private static final long serialVersionUID = 2087037270008785656L;
    private BigInteger phaseId;
    private String name;
    private String description;
    private boolean staffEligibleForUnderStaffing;
    private boolean staffEligibleForOverStaffing;
    private boolean managementEligibleForUnderStaffing;
    private boolean managementEligibleForOverStaffing;
    private Long unitId;
    private int sequence;
    private short maxProblemAllowed; // storing it in minutes


    public PhaseSettings(BigInteger phaseId, String name, String description, boolean staffEligibleForUnderStaffing, boolean staffEligibleForOverStaffing,
                         boolean managementEligibleForUnderStaffing, boolean managementEligibleForOverStaffing, Long unitId, int sequence) {
        this.phaseId = phaseId;
        this.name = name;
        this.description = description;
        this.staffEligibleForUnderStaffing = staffEligibleForUnderStaffing;
        this.staffEligibleForOverStaffing = staffEligibleForOverStaffing;
        this.managementEligibleForUnderStaffing = managementEligibleForUnderStaffing;
        this.managementEligibleForOverStaffing = managementEligibleForOverStaffing;
        this.unitId = unitId;
        this.sequence = sequence;
    }
}

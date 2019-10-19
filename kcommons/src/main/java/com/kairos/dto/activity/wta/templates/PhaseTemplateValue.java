package com.kairos.dto.activity.wta.templates;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.util.Objects;

/**
 * Created by pavan on 18/1/18.
 */
@Getter
@Setter
@NoArgsConstructor
public class PhaseTemplateValue {
    private BigInteger phaseId;
    private String phaseName;
    private short staffValue;
    private short managementValue;
    private boolean disabled=true;
    private int sequence;
    private boolean staffCanIgnore;
    private boolean managementCanIgnore;


    public PhaseTemplateValue(BigInteger phaseId, String phaseName, short staffValue, short managementValue, boolean disabled, boolean staffCanIgnore, boolean managementCanIgnore, int sequence) {
        this.phaseId = phaseId;
        this.phaseName = phaseName;
        this.staffValue = staffValue;
        this.managementValue = managementValue;
        this.disabled = disabled;
        this.staffCanIgnore = staffCanIgnore;
        this.managementCanIgnore = managementCanIgnore;
        this.sequence = sequence;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PhaseTemplateValue)) return false;
        PhaseTemplateValue that = (PhaseTemplateValue) o;
        return phaseId == that.phaseId &&
                staffValue == that.staffValue &&
                managementValue == that.managementValue &&
                disabled == that.disabled &&
                sequence == that.sequence &&
                staffCanIgnore == that.staffCanIgnore &&
                managementCanIgnore == that.managementCanIgnore &&
                Objects.equals(phaseName, that.phaseName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(phaseId, phaseName, staffValue, managementValue, disabled, sequence, staffCanIgnore, managementCanIgnore);
    }

    @Override
    public String toString() {
        return "PhaseTemplateValue{" +
                "phaseName='" + phaseName + '\'' +
                ", staffValue=" + staffValue +
                ", managementValue=" + managementValue +
                ", disabled=" + disabled +
                '}';
    }
}

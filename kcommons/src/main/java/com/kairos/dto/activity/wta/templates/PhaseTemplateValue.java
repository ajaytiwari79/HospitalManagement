package com.kairos.dto.activity.wta.templates;


import lombok.*;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Objects;

/**
 * Created by pavan on 18/1/18.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PhaseTemplateValue implements Serializable {
    private static final long serialVersionUID = 213213212232l;
    private BigInteger phaseId;
    private String phaseName;
    private short staffValue;
    private short managementValue;
    private boolean disabled;
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
        return phaseId.equals(that.phaseId) &&
                staffValue == that.staffValue &&
                managementValue == that.managementValue &&
                disabled == that.disabled &&
                sequence == that.sequence &&
                staffCanIgnore == that.staffCanIgnore &&
                managementCanIgnore == that.managementCanIgnore &&
                Objects.equals(phaseName, that.phaseName);
    }

}

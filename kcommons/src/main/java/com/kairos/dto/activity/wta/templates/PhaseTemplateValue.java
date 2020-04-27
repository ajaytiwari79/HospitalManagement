package com.kairos.dto.activity.wta.templates;


import lombok.*;

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
public class PhaseTemplateValue {
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

}

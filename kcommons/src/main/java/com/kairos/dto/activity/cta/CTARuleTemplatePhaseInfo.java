package com.kairos.dto.activity.cta;

import lombok.*;

import java.math.BigInteger;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class CTARuleTemplatePhaseInfo{
    private BigInteger phaseId;
    private phaseType type;
    private int beforeStart;
    public enum  phaseType{
        DAYS,HOURS;
    }
}

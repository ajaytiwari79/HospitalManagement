package com.kairos.dto.activity.cta;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.util.Objects;
@Getter
@Setter
@NoArgsConstructor
public class CTARuleTemplatePhaseInfo{
    private BigInteger phaseId;
    private phaseType type;
    private int beforeStart;
    public enum  phaseType{
        DAYS,HOURS;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CTARuleTemplatePhaseInfo that = (CTARuleTemplatePhaseInfo) o;
        return beforeStart == that.beforeStart &&
                Objects.equals(phaseId, that.phaseId) &&
                type == that.type;
    }

    @Override
    public int hashCode() {

        return Objects.hash(phaseId, type, beforeStart);
    }
}

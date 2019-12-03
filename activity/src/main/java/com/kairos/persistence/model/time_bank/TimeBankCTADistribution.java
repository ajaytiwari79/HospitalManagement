package com.kairos.persistence.model.time_bank;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;

import java.math.BigInteger;
import java.time.LocalDate;
@Getter
@Setter
@NoArgsConstructor
public class TimeBankCTADistribution {

    private String ctaName;
    private int minutes;
    private BigInteger ctaRuleTemplateId;
    private LocalDate ctaDate;
    private transient float cost;

    public TimeBankCTADistribution(String ctaName, int minutes, BigInteger ctaRuleTemplateId) {
        this.ctaName = ctaName;
        this.minutes = minutes;
        this.ctaRuleTemplateId = ctaRuleTemplateId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        TimeBankCTADistribution that = (TimeBankCTADistribution) o;

        return new EqualsBuilder()
                .append(ctaName, that.ctaName)
                .append(ctaRuleTemplateId, that.ctaRuleTemplateId)
                .isEquals();
    }

    @Override
    public String toString() {
        return "TimeBankCTADistribution{" + "ctaName='" + ctaName + '\'' + ", minutes=" + minutes + ", ctaRuleTemplateId=" + ctaRuleTemplateId + '}';
    }
}

package com.kairos.dto.user.staff.unit_position;

import java.math.BigInteger;

/**
 * CreatedBy vipulpandey on 3/10/18
 **/
public class PositionLineChangeResultDTO {
    private boolean calculativeChanged;
    private boolean employmentTypeChanged;
    private BigInteger ctaId;
    private BigInteger oldctaId;
    private BigInteger wtaId;
    private BigInteger oldwtaId;
    private boolean functionsChanged;
    public PositionLineChangeResultDTO(boolean calculativeChanged) {
        this.calculativeChanged=calculativeChanged;
    }

    public boolean isCalculativeChanged() {
        return calculativeChanged;
    }

    public void setCalculativeChanged(boolean calculativeChanged) {
        this.calculativeChanged = calculativeChanged;
    }

    public BigInteger getCtaId() {
        return ctaId;
    }

    public void setCtaId(BigInteger ctaId) {
        this.ctaId = ctaId;
    }

    public BigInteger getOldctaId() {
        return oldctaId;
    }

    public void setOldctaId(BigInteger oldctaId) {
        this.oldctaId = oldctaId;
    }

    public BigInteger getWtaId() {
        return wtaId;
    }

    public void setWtaId(BigInteger wtaId) {
        this.wtaId = wtaId;
    }

    public BigInteger getOldwtaId() {
        return oldwtaId;
    }

    public void setOldwtaId(BigInteger oldwtaId) {
        this.oldwtaId = oldwtaId;
    }

    public boolean isEmploymentTypeChanged() {
        return employmentTypeChanged;
    }

    public void setEmploymentTypeChanged(boolean employmentTypeChanged) {
        this.employmentTypeChanged = employmentTypeChanged;
    }

    public boolean isFunctionsChanged() {
        return functionsChanged;
    }

    public void setFunctionsChanged(boolean functionsChanged) {
        this.functionsChanged = functionsChanged;
    }
}

package com.kairos.dto.user.staff.unit_position;

import java.math.BigInteger;

/**
 * CreatedBy vipulpandey on 3/10/18
 **/
public class PositionLineChangeResultDTO {
    boolean calculativeChanged;
    private BigInteger ctaId;
    private BigInteger oldctaId;
    private BigInteger wtaId;
    private BigInteger oldwtaId;

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
}

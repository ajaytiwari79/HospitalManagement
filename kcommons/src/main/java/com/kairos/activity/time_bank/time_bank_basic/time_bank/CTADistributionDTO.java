package com.kairos.activity.time_bank.time_bank_basic.time_bank;

import java.math.BigInteger;

/**
 * @author pradeep
 * @date - 23/7/18
 */

public class CTADistributionDTO {
    private BigInteger id;
    private String name;
    private int minutes;

    public CTADistributionDTO() {
    }

    public CTADistributionDTO(BigInteger id, String name, int minutes) {
        this.id = id;
        this.name = name;
        this.minutes = minutes;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }
}

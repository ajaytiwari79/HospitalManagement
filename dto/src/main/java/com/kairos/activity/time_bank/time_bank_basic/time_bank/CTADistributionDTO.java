package com.kairos.activity.time_bank.time_bank_basic.time_bank;

/**
 * @author pradeep
 * @date - 23/7/18
 */

public class CTADistributionDTO {
    private Long id;
    private String name;
    private int minutes;

    public CTADistributionDTO() {
    }

    public CTADistributionDTO(Long id, String name, int minutes) {
        this.id = id;
        this.name = name;
        this.minutes = minutes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

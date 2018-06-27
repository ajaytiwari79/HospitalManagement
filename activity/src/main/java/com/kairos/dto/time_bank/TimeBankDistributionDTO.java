package com.kairos.dto.time_bank;

public class TimeBankDistributionDTO {

    //cta ruletemplate based distributions
    private Long id;
    private String name;
    private int minutes;

    public TimeBankDistributionDTO() {
    }

    public TimeBankDistributionDTO(Long id, String name, int minutes) {
        this.id = id;
        this.name = name;
        this.minutes = minutes;
    }

    public TimeBankDistributionDTO(Long id, int minutes) {
        this.id = id;
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

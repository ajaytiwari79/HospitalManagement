package com.kairos.activity.time_bank;

import com.kairos.activity.time_bank.time_bank_basic.time_bank.CTADistributionDTO;

import java.util.List;

public class TimeBankCTADistributionDTO {

    //cta ruletemplate based distributions
    private List<CTADistributionDTO> children;
    private long minutes;

    public TimeBankCTADistributionDTO() {
    }

    public TimeBankCTADistributionDTO(List<CTADistributionDTO> children, long minutes) {
        this.children = children;
        this.minutes = minutes;
    }


    public long getMinutes() {
        return minutes;
    }

    public void setMinutes(long minutes) {
        this.minutes = minutes;
    }

    public List<CTADistributionDTO> getChildren() {
        return children;
    }

    public void setChildren(List<CTADistributionDTO> children) {
        this.children = children;
    }
}

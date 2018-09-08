package com.kairos.activity.pay_out;

import com.kairos.activity.time_bank.time_bank_basic.time_bank.CTADistributionDTO;

import java.util.List;

public class PayOutCTADistributionDTO {

    //cta ruletemplate based distributions
    private long minutes;
    private List<CTADistributionDTO> children;
    public PayOutCTADistributionDTO() {
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

    public PayOutCTADistributionDTO(long minutes, List<CTADistributionDTO> children) {
        this.minutes = minutes;
        this.children = children;
    }
}

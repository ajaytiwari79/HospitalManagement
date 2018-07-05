package com.kairos.activity.wta.version;

import com.kairos.activity.shift.Expertise;
import com.kairos.activity.wta.basic_details.WTABaseRuleTemplateDTO;


import java.math.BigInteger;
import java.util.Date;
import java.util.List;

public class WTAVersionDTO {

    private Date startDate;
    private Date endDate;
    private Long expiryDate;
    private String name;
    private BigInteger id;
    private Expertise expertise;
    private List<WTAVersionDTO> versions;
    private List<WTABaseRuleTemplateDTO> ruleTemplates;

    public WTAVersionDTO() {
        //dc
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Long getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Long expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public Expertise getExpertise() {
        return expertise;
    }

    public void setExpertise(Expertise expertise) {
        this.expertise = expertise;
    }

    public List<WTAVersionDTO> getVersions() {
        return versions;
    }

    public void setVersions(List<WTAVersionDTO> versions) {
        this.versions = versions;
    }

    public List<WTABaseRuleTemplateDTO> getRuleTemplates() {
        return ruleTemplates;
    }

    public void setRuleTemplates(List<WTABaseRuleTemplateDTO> ruleTemplates) {
        this.ruleTemplates = ruleTemplates;
    }
}

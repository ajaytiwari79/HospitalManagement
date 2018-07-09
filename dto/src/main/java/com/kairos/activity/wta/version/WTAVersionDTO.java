package com.kairos.activity.wta.version;

import com.kairos.activity.shift.Expertise;
import com.kairos.activity.wta.basic_details.WTABaseRuleTemplateDTO;
import com.kairos.user.organization.position_code.PositionCodeDTO;


import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class WTAVersionDTO {

    private Date startDate;
    private Date endDate;
    private Long expiryDate;
    private String name;
    private BigInteger id;
    private Expertise expertise;
    private Boolean disabled;
    private List<WTAVersionDTO> versions = new ArrayList<>();
    private List<WTABaseRuleTemplateDTO> ruleTemplates;
    private Long parentUnitId;
    private Long unitId;
    private Map<String, Object> unitInfo;
    private PositionCodeDTO positionCode;

    public WTAVersionDTO() {
        //dc
    }

    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
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

    public Long getParentUnitId() {
        return parentUnitId;
    }

    public void setParentUnitId(Long parentUnitId) {
        this.parentUnitId = parentUnitId;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public Map<String, Object> getUnitInfo() {
        return unitInfo;
    }

    public void setUnitInfo(Map<String, Object> unitInfo) {
        this.unitInfo = unitInfo;
    }

    public PositionCodeDTO getPositionCode() {
        return positionCode;
    }

    public void setPositionCode(PositionCodeDTO positionCode) {
        this.positionCode = positionCode;
    }
}

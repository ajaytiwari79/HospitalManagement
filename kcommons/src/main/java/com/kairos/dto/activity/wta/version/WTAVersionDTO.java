package com.kairos.dto.activity.wta.version;

import com.kairos.dto.activity.shift.Expertise;
import com.kairos.dto.activity.wta.basic_details.WTABaseRuleTemplateDTO;
import com.kairos.commons.utils.DateUtils;


import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WTAVersionDTO {

    private LocalDate startDate;
    private LocalDate endDate;
    // TODO fix need to make constant fields for date
    private Long startDateMillis;
    private Long endDateMillis;
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
    private Long unitPositionId;

    public WTAVersionDTO() {
        //dc
    }



    public Long getUnitPositionId() {
        return unitPositionId;
    }

    public void setUnitPositionId(Long unitPositionId) {
        this.unitPositionId = unitPositionId;
    }

    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
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

    public Long getStartDateMillis() {
        return DateUtils.asDate(startDate).getTime();
    }

    public void setStartDateMillis(Long startDateMillis) {
        this.startDateMillis = DateUtils.asDate(startDate).getTime();
    }

    public Long getEndDateMillis() {
        return (endDate!=null)?DateUtils.asDate(endDate).getTime():null;
    }

    public void setEndDateMillis(Long endDateMillis) {
        this.endDateMillis = (endDate!=null)?DateUtils.asDate(endDate).getTime():null;
    }
}

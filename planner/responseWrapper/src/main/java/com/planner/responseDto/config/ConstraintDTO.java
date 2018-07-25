package com.planner.responseDto.config;


import java.util.List;

public class ConstraintDTO {

    private String catagoryName;
    private String level;
    private Integer levelNo;
    private Integer levelValue;
    private List<Integer> staticRuleValues;
    private String ruleId;
    private List<Integer> dynamicRuleValues;
    private RuleDTO ruleDTO;

    public String getCatagoryName() {
        return catagoryName;
    }

    public void setCatagoryName(String catagoryName) {
        this.catagoryName = catagoryName;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public Integer getLevelNo() {
        return levelNo;
    }

    public void setLevelNo(Integer levelNo) {
        this.levelNo = levelNo;
    }

    public Integer getLevelValue() {
        return levelValue;
    }

    public void setLevelValue(Integer levelValue) {
        this.levelValue = levelValue;
    }

    public List<Integer> getStaticRuleValues() {
        return staticRuleValues;
    }

    public void setStaticRuleValues(List<Integer> staticRuleValues) {
        this.staticRuleValues = staticRuleValues;
    }

    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    public List<Integer> getDynamicRuleValues() {
        return dynamicRuleValues;
    }

    public void setDynamicRuleValues(List<Integer> dynamicRuleValues) {
        this.dynamicRuleValues = dynamicRuleValues;
    }

    public RuleDTO getRuleDTO() {
        return ruleDTO;
    }

    public void setRuleDTO(RuleDTO ruleDTO) {
        this.ruleDTO = ruleDTO;
    }
}

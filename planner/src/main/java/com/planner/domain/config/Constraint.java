package com.planner.domain.config;

import com.planner.domain.common.BaseEntity;

import java.util.List;

//import org.springframework.data.cassandra.core.mapping.Table;

//@Table
public class Constraint extends BaseEntity {

    private String categoryId;
    //it is used for current constraint belongs to which level like (Hard,Medium,soft).
    private String level;
    //it is used for current constraint belongs to which level no in bendableScore like (hard[0],soft[1]).
    private Integer levelNo;
    //it is used for current constraint break how much in no. (like -10,-20) if it is static.
    private Integer levelValue;
    //it is used for making rule conditions like (waiting time not more then 8 hours) It is used when planner doesnt' give
    private List<Integer> staticRuleValues;
    private String ruleId;
    //it is same as static value but it is used when planner give the value for this contraint.
    private List<Integer> dynamicRuleValue;
    private String solverConfigId;

    public Integer getLevelNo() {
        return levelNo;
    }

    public void setLevelNo(Integer levelNo) {
        this.levelNo = levelNo;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
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

    public List<Integer> getDynamicRuleValue() {
        return dynamicRuleValue;
    }

    public void setDynamicRuleValue(List<Integer> dynamicRuleValue) {
        this.dynamicRuleValue = dynamicRuleValue;
    }

    public String getSolverConfigId() {
        return solverConfigId;
    }

    public void setSolverConfigId(String solverConfigId) {
        this.solverConfigId = solverConfigId;
    }
}

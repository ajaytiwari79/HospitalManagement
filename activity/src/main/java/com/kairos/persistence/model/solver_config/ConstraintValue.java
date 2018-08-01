package com.kairos.persistence.model.solver_config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.enums.solver_config.ContraintLevel;

import java.math.BigInteger;
import java.util.List;

/**
 * @author pradeep
 * @date - 20/6/18
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConstraintValue {


    //constraint id
    private BigInteger id;

    //it is used for current constraint belongs to which level like (Hard,Medium,soft).
    private ContraintLevel level;
    //it is used for current constraint belongs to which level no in bendableScore like (hard[0],soft[1]).
    private Integer levelNo;
    //it is used for current constraint break how much in no. (like -10,-20) if it is static.
    private Integer levelValue;
    //it is used for making rule conditions like (waiting time not more then 8 hours) It is used when planner doesnt' give
    private List<Integer> staticRuleValues;
    //it is same as static constraintValue but it is used when planner give the constraintValue for this contraint.
    private List<Integer> dynamicRuleValue;

    private Integer constraintValue;
    private Integer penalityValue;

    public Integer getPenalityValue() {
        return penalityValue;
    }

    public void setPenalityValue(Integer penalityValue) {
        this.penalityValue = penalityValue;
    }

    public Integer getConstraintValue() {
        return constraintValue;
    }

    public void setConstraintValue(Integer constraintValue) {
        this.constraintValue = constraintValue;
    }

    public ConstraintValue() {
    }

    public ConstraintValue(BigInteger id,Integer penalityValue) {
        this.id = id;
        this.penalityValue = penalityValue;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public ContraintLevel getLevel() {
        return level;
    }

    public void setLevel(ContraintLevel level) {
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

    public List<Integer> getDynamicRuleValue() {
        return dynamicRuleValue;
    }

    public void setDynamicRuleValue(List<Integer> dynamicRuleValue) {
        this.dynamicRuleValue = dynamicRuleValue;
    }
}

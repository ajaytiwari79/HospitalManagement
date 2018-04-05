package com.kairos.persistence.model.user.unit_position;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.util.Set;

/**
 * Created by vipul on 5/4/18.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UnitPositionSeniorityLevelResponse {
    private Long id;
    private Long parentId;
    private Integer from;
    private Integer to;
    private Integer moreThan;
    private Set<Long> functions;
    private Long payGradeId;
    private BigDecimal pensionPercentage;
    private BigDecimal freeChoicePercentage;
    private BigDecimal freeChoiceToPension;

    public UnitPositionSeniorityLevelResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Integer getFrom() {
        return from;
    }

    public void setFrom(Integer from) {
        this.from = from;
    }

    public Integer getTo() {
        return to;
    }

    public void setTo(Integer to) {
        this.to = to;
    }

    public Integer getMoreThan() {
        return moreThan;
    }

    public void setMoreThan(Integer moreThan) {
        this.moreThan = moreThan;
    }

    public Set<Long> getFunctions() {
        return functions;
    }

    public void setFunctions(Set<Long> functions) {
        this.functions = functions;
    }

    public Long getPayGradeId() {
        return payGradeId;
    }

    public void setPayGradeId(Long payGradeId) {
        this.payGradeId = payGradeId;
    }

    public BigDecimal getPensionPercentage() {
        return pensionPercentage;
    }

    public void setPensionPercentage(BigDecimal pensionPercentage) {
        this.pensionPercentage = pensionPercentage;
    }

    public BigDecimal getFreeChoicePercentage() {
        return freeChoicePercentage;
    }

    public void setFreeChoicePercentage(BigDecimal freeChoicePercentage) {
        this.freeChoicePercentage = freeChoicePercentage;
    }

    public BigDecimal getFreeChoiceToPension() {
        return freeChoiceToPension;
    }

    public void setFreeChoiceToPension(BigDecimal freeChoiceToPension) {
        this.freeChoiceToPension = freeChoiceToPension;
    }
}

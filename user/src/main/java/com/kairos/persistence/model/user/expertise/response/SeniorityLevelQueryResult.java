package com.kairos.persistence.model.user.expertise.response;

import com.kairos.persistence.model.country.functions.FunctionDTO;
import com.kairos.persistence.model.pay_table.PayGrade;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by vipul on 29/3/18.
 */
@QueryResult
public class SeniorityLevelQueryResult {
    private PayGrade payGrade;
    private BigDecimal pensionPercentage;
    private BigDecimal freeChoicePercentage;
    private BigDecimal freeChoiceToPension;
    private Integer from;
    private Integer to;
    private Long id;
    private List<FunctionDTO> functions;

    public SeniorityLevelQueryResult() {
        //default
    }

    public PayGrade getPayGrade() {
        return payGrade;
    }

    public void setPayGrade(PayGrade payGrade) {
        this.payGrade = payGrade;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<FunctionDTO> getFunctions() {
        return functions;
    }

    public void setFunctions(List<FunctionDTO> functions) {
        this.functions = functions;
    }
}

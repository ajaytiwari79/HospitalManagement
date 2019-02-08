package com.kairos.persistence.model.pay_table;

import com.kairos.persistence.model.user.pay_group_area.PayGroupArea;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.StartNode;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.math.BigDecimal;

/*
 *Created By Pavan on 7/2/19
 *
 */
@QueryResult
public class PayGradePayGroupAreaRelationShipQueryResult {
    private Long id;
    private PayGrade payGrade;
    private PayGroupArea payGroupArea;
    private BigDecimal payGroupAreaAmount;

    public PayGradePayGroupAreaRelationShipQueryResult() {
        //Default Constructor
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PayGrade getPayGrade() {
        return payGrade;
    }

    public void setPayGrade(PayGrade payGrade) {
        this.payGrade = payGrade;
    }

    public PayGroupArea getPayGroupArea() {
        return payGroupArea;
    }

    public void setPayGroupArea(PayGroupArea payGroupArea) {
        this.payGroupArea = payGroupArea;
    }

    public BigDecimal getPayGroupAreaAmount() {
        return payGroupAreaAmount;
    }

    public void setPayGroupAreaAmount(BigDecimal payGroupAreaAmount) {
        this.payGroupAreaAmount = payGroupAreaAmount;
    }
}

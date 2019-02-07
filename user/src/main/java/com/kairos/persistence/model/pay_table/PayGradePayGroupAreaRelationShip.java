package com.kairos.persistence.model.pay_table;

import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.user.pay_group_area.PayGroupArea;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.math.BigDecimal;

import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_PAY_GROUP_AREA;

/**
 * Created by vipul on 15/3/18.
 */
@RelationshipEntity(type = HAS_PAY_GROUP_AREA)
public class PayGradePayGroupAreaRelationShip extends UserBaseEntity {
    @StartNode
    private PayGrade payGrade;
    @EndNode
    private PayGroupArea payGroupArea;


    private BigDecimal payGroupAreaAmount;

    public PayGradePayGroupAreaRelationShip() {
    }

    public PayGroupArea getPayGroupArea() {
        return payGroupArea;
    }

    public void setPayGroupArea(PayGroupArea payGroupArea) {
        this.payGroupArea = payGroupArea;
    }

    public PayGrade getPayGrade() {
        return payGrade;
    }

    public void setPayGrade(PayGrade payGrade) {
        this.payGrade = payGrade;
    }

    public BigDecimal getPayGroupAreaAmount() {
        return payGroupAreaAmount;
    }

    public void setPayGroupAreaAmount(BigDecimal payGroupAreaAmount) {
        this.payGroupAreaAmount = payGroupAreaAmount;
    }


    public PayGradePayGroupAreaRelationShip(PayGrade payGrade, PayGroupArea payGroupArea, BigDecimal payGroupAreaAmount) {
        this.payGrade = payGrade;
        this.payGroupArea = payGroupArea;
        this.payGroupAreaAmount = payGroupAreaAmount;
    }

}

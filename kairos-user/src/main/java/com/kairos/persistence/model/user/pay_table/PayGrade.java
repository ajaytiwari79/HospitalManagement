package com.kairos.persistence.model.user.pay_table;

import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.user.pay_group_area.PayGroupArea;
import org.neo4j.ogm.annotation.NodeEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by prabjot on 21/12/17.
 */
@NodeEntity
public class PayGrade extends UserBaseEntity {

    private Long payGradeLevel;

    public PayGrade() {
    }

    public PayGrade(Long payGradeLevel) {
        this.payGradeLevel = payGradeLevel;
    }

    public Long getPayGradeLevel() {
        return payGradeLevel;
    }

    public void setPayGradeLevel(Long payGradeLevel) {
        this.payGradeLevel = payGradeLevel;
    }
}

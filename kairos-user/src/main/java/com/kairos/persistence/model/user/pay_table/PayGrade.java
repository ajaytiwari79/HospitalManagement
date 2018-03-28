package com.kairos.persistence.model.user.pay_table;

import com.kairos.persistence.model.common.UserBaseEntity;
import org.neo4j.ogm.annotation.NodeEntity;

/**
 * Created by prabjot on 21/12/17.
 */
@NodeEntity
public class PayGrade extends UserBaseEntity {

    private Long payGradeLevel;
    private boolean published;

    public PayGrade() {
    }

    public PayGrade(Long payGradeLevel) {
        this.payGradeLevel = payGradeLevel;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public Long getPayGradeLevel() {
        return payGradeLevel;
    }

    public void setPayGradeLevel(Long payGradeLevel) {
        this.payGradeLevel = payGradeLevel;
    }

    public PayGrade(Long payGradeLevel, boolean published) {
        this.payGradeLevel = payGradeLevel;
        this.published = published;
    }
}

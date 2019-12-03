package com.kairos.persistence.model.pay_table;

import com.kairos.persistence.model.common.UserBaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.NodeEntity;

import java.util.Objects;

/**
 * Created by prabjot on 21/12/17.
 */
@NodeEntity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PayGrade extends UserBaseEntity {

    private Long payGradeLevel;
    private boolean published;

    public PayGrade(Long payGradeLevel) {
        this.payGradeLevel = payGradeLevel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PayGrade payGrade = (PayGrade) o;
        return Objects.equals(payGradeLevel, payGrade.payGradeLevel);
    }

    @Override
    public int hashCode() {

        return Objects.hash(payGradeLevel);
    }
}

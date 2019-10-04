package com.kairos.persistence.model.organization;

import com.kairos.persistence.model.common.UserBaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;
import org.neo4j.ogm.annotation.NodeEntity;

import java.time.DayOfWeek;

/**
 * Created by vipul on 12/4/18.
 */
@NodeEntity
@Getter
@Setter
@NoArgsConstructor
public class PaymentSettings extends UserBaseEntity {
    private DayOfWeek weeklyPayDay;
    private DayOfWeek fornightlyPayDay;
    private Long lastFornightlyPayDate;
    @Range(min = 1l, max = 31L)
    private Long monthlyPayDate;

}

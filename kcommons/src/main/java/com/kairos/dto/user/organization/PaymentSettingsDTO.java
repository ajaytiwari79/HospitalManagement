package com.kairos.dto.user.organization;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import java.time.DayOfWeek;

/**
 * Created by vipul on 12/4/18.
 */
@Getter
@Setter
public class PaymentSettingsDTO {
    private Long id;
    private DayOfWeek weeklyPayDay;
    private DayOfWeek fornightlyPayDay;
    private Long lastFornightlyPayDate;
    @Range(min = 1l, max = 31L)
    private Long monthlyPayDate;
}

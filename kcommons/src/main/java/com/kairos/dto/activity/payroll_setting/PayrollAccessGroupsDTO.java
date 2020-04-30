package com.kairos.dto.activity.payroll_setting;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PayrollAccessGroupsDTO {
    //access group id and name
    private Long id;
    private String name;
    //grace period of access group for pay roll
    private int gracePeriod;
    private int priority;

}

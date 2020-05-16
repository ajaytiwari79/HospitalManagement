package com.kairos.shiftplanning.domain.staff;

import lombok.Getter;
import lombok.Setter;

/**
 * @author pradeep
 * @date - 16/11/18
 */
@Getter
@Setter
public class CareDays {
    private Long id;
    private Integer from;
    private Integer to;
    private Integer leavesAllowed;

}

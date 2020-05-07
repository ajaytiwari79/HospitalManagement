package com.kairos.dto.activity.open_shift;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FibonacciCounter {

    private Long staffId;
    private Integer timeBank;
    private Integer assignedOpenShifts;
    private Integer fibonacciTimeBank;
    private Integer fibonacciAssignedOpenShifts;
    private Integer countersSum;

    public FibonacciCounter(Long staffId, Integer timebank, Integer assignedOpenShifts) {
        this.staffId = staffId;
        this.timeBank = timebank;
        this.assignedOpenShifts = assignedOpenShifts;
    }

}

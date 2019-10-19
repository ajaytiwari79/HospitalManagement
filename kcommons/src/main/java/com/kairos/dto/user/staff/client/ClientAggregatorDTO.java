package com.kairos.dto.user.staff.client;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.util.List;

/**
 * Created by Jasgeet on 4/9/17.
 */
@Getter
@Setter
@NoArgsConstructor
public class ClientAggregatorDTO {

    int longDrivingCount = 0;
    List<BigInteger> longDrivingTasks;
    int mostDrivenCount = 0;
    List<BigInteger> mostDrivenTasks;
    int escalationCount = 0;
    List<BigInteger> escalationTasks;
    int waitingCount = 0;
    int plannedStatusCount = 0;
    List<BigInteger> waitingTasks;
    List<BigInteger> plannedStatusTasks;
    List<BigInteger> totalPlannedProblemsTasks;


    public ClientAggregatorDTO(int longDrivingCount, List<BigInteger> longDrivingTasks, int mostDrivenCount, List<BigInteger> mostDrivenTasks, int escalationCount, List<BigInteger> escalationTasks, int waitingCount, int plannedStatusCount, List<BigInteger> waitingTasks, List<BigInteger> plannedStatusTasks, List<BigInteger> totalPlannedProblemsTasks) {
        this.longDrivingCount = longDrivingCount;
        this.longDrivingTasks = longDrivingTasks;
        this.mostDrivenCount = mostDrivenCount;
        this.mostDrivenTasks = mostDrivenTasks;
        this.escalationCount = escalationCount;
        this.escalationTasks = escalationTasks;
        this.waitingCount = waitingCount;
        this.plannedStatusCount = plannedStatusCount;
        this.waitingTasks = waitingTasks;
        this.plannedStatusTasks = plannedStatusTasks;
        this.totalPlannedProblemsTasks = totalPlannedProblemsTasks;
    }

}

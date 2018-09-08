package com.kairos.dto.user.staff.client;

import java.math.BigInteger;
import java.util.List;

/**
 * Created by Jasgeet on 4/9/17.
 */
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

    public ClientAggregatorDTO() {
    }

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

    public int getLongDrivingCount() {
        return longDrivingCount;
    }

    public void setLongDrivingCount(int longDrivingCount) {
        this.longDrivingCount = longDrivingCount;
    }

    public List<BigInteger> getLongDrivingTasks() {
        return longDrivingTasks;
    }

    public void setLongDrivingTasks(List<BigInteger> longDrivingTasks) {
        this.longDrivingTasks = longDrivingTasks;
    }

    public int getMostDrivenCount() {
        return mostDrivenCount;
    }

    public void setMostDrivenCount(int mostDrivenCount) {
        this.mostDrivenCount = mostDrivenCount;
    }

    public List<BigInteger> getMostDrivenTasks() {
        return mostDrivenTasks;
    }

    public void setMostDrivenTasks(List<BigInteger> mostDrivenTasks) {
        this.mostDrivenTasks = mostDrivenTasks;
    }

    public int getEscalationCount() {
        return escalationCount;
    }

    public void setEscalationCount(int escalationCount) {
        this.escalationCount = escalationCount;
    }

    public List<BigInteger> getEscalationTasks() {
        return escalationTasks;
    }

    public void setEscalationTasks(List<BigInteger> escalationTasks) {
        this.escalationTasks = escalationTasks;
    }

    public int getWaitingCount() {
        return waitingCount;
    }

    public void setWaitingCount(int waitingCount) {
        this.waitingCount = waitingCount;
    }

    public int getPlannedStatusCount() {
        return plannedStatusCount;
    }

    public void setPlannedStatusCount(int plannedStatusCount) {
        this.plannedStatusCount = plannedStatusCount;
    }

    public List<BigInteger> getWaitingTasks() {
        return waitingTasks;
    }

    public void setWaitingTasks(List<BigInteger> waitingTasks) {
        this.waitingTasks = waitingTasks;
    }

    public List<BigInteger> getPlannedStatusTasks() {
        return plannedStatusTasks;
    }

    public void setPlannedStatusTasks(List<BigInteger> plannedStatusTasks) {
        this.plannedStatusTasks = plannedStatusTasks;
    }

    public List<BigInteger> getTotalPlannedProblemsTasks() {
        return totalPlannedProblemsTasks;
    }

    public void setTotalPlannedProblemsTasks(List<BigInteger> totalPlannedProblemsTasks) {
        this.totalPlannedProblemsTasks = totalPlannedProblemsTasks;
    }
}

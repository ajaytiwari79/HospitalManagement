package com.kairos.activity.open_shift;


public class FibonacciCounter {

    private Long staffId;
    private Integer timeBank;
    private Integer assignedOpenShifts;
    private Integer fibonacciTimeBank;
    private Integer fibonacciAssignedOpenShifts;

    public Integer getCountersSum() {
        return countersSum;
    }

    public void setCountersSum(Integer countersSum) {
        this.countersSum = countersSum;
    }

    private Integer countersSum;

    public FibonacciCounter(Long staffId, Integer timebank, Integer assignedOpenShifts) {
        this.staffId = staffId;
        this.timeBank = timebank;
        this.assignedOpenShifts = assignedOpenShifts;
    }

    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }

    public Integer getTimeBank() {
        return timeBank;
    }

    public void setTimeBank(Integer timeBank) {
        this.timeBank = timeBank;
    }

    public Integer getAssignedOpenShifts() {
        return assignedOpenShifts;
    }

    public void setAssignedOpenShifts(Integer assignedOpenShifts) {
        this.assignedOpenShifts = assignedOpenShifts;
    }

    public Integer getFibonacciTimeBank() {
        return fibonacciTimeBank;
    }

    public void setFibonacciTimeBank(Integer fibonacciTimeBank) {
        this.fibonacciTimeBank = fibonacciTimeBank;
    }

    public Integer getFibonacciAssignedOpenShifts() {
        return fibonacciAssignedOpenShifts;
    }

    public void setFibonacciAssignedOpenShifts(Integer fibonacciAssignedOpenShifts) {
        this.fibonacciAssignedOpenShifts = fibonacciAssignedOpenShifts;
    }
}

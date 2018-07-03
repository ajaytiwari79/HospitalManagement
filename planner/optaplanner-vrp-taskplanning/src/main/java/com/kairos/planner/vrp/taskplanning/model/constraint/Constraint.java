package com.kairos.planner.vrp.taskplanning.model.constraint;

/**
 * @author pradeep
 * @date - 30/6/18
 */

public class Constraint {

    private StartInTimeWindow startInTimeWindow1;
    private StartsAsFirstTask startsAsFirstTask;
    private PlanInsideTimeWindow planInsideTimeWindow;
    private MustBePlanned mustBePlanned;
    private OptimizePlanBasedOnSkill optimizePlanBasedOnSkill;
    private MinimizeDrivingTime minimizeDrivingTime;
    private MaximizeFlexitimeUtilization maximizeFlexitimeUtilization;
    private TaskFromSameInstallationNumber taskFromSameInstallationNumber;
    private PlanTaskFromSameInstallationNumber planTaskFromSameInstallationNumber;
    private NumberOfTaskPerShift numberOfTaskPerShift;

    public StartInTimeWindow getStartInTimeWindow1() {
        return startInTimeWindow1;
    }

    public void setStartInTimeWindow1(StartInTimeWindow startInTimeWindow1) {
        this.startInTimeWindow1 = startInTimeWindow1;
    }

    public StartsAsFirstTask getStartsAsFirstTask() {
        return startsAsFirstTask;
    }

    public void setStartsAsFirstTask(StartsAsFirstTask startsAsFirstTask) {
        this.startsAsFirstTask = startsAsFirstTask;
    }

    public PlanInsideTimeWindow getPlanInsideTimeWindow() {
        return planInsideTimeWindow;
    }

    public void setPlanInsideTimeWindow(PlanInsideTimeWindow planInsideTimeWindow) {
        this.planInsideTimeWindow = planInsideTimeWindow;
    }

    public MustBePlanned getMustBePlanned() {
        return mustBePlanned;
    }

    public void setMustBePlanned(MustBePlanned mustBePlanned) {
        this.mustBePlanned = mustBePlanned;
    }

    public OptimizePlanBasedOnSkill getOptimizePlanBasedOnSkill() {
        return optimizePlanBasedOnSkill;
    }

    public void setOptimizePlanBasedOnSkill(OptimizePlanBasedOnSkill optimizePlanBasedOnSkill) {
        this.optimizePlanBasedOnSkill = optimizePlanBasedOnSkill;
    }

    public MinimizeDrivingTime getMinimizeDrivingTime() {
        return minimizeDrivingTime;
    }

    public void setMinimizeDrivingTime(MinimizeDrivingTime minimizeDrivingTime) {
        this.minimizeDrivingTime = minimizeDrivingTime;
    }

    public MaximizeFlexitimeUtilization getMaximizeFlexitimeUtilization() {
        return maximizeFlexitimeUtilization;
    }

    public void setMaximizeFlexitimeUtilization(MaximizeFlexitimeUtilization maximizeFlexitimeUtilization) {
        this.maximizeFlexitimeUtilization = maximizeFlexitimeUtilization;
    }

    public TaskFromSameInstallationNumber getTaskFromSameInstallationNumber() {
        return taskFromSameInstallationNumber;
    }

    public void setTaskFromSameInstallationNumber(TaskFromSameInstallationNumber taskFromSameInstallationNumber) {
        this.taskFromSameInstallationNumber = taskFromSameInstallationNumber;
    }

    public PlanTaskFromSameInstallationNumber getPlanTaskFromSameInstallationNumber() {
        return planTaskFromSameInstallationNumber;
    }

    public void setPlanTaskFromSameInstallationNumber(PlanTaskFromSameInstallationNumber planTaskFromSameInstallationNumber) {
        this.planTaskFromSameInstallationNumber = planTaskFromSameInstallationNumber;
    }

    public NumberOfTaskPerShift getNumberOfTaskPerShift() {
        return numberOfTaskPerShift;
    }

    public void setNumberOfTaskPerShift(NumberOfTaskPerShift numberOfTaskPerShift) {
        this.numberOfTaskPerShift = numberOfTaskPerShift;
    }
}

package com.kairos.persistence.model.task_type;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.enums.task_type.TaskTypeEnum;

import java.io.Serializable;

/**
 * Created by prabjot on 7/10/16.
 */
public class TaskTypeDefination implements Serializable {

    private TaskTypeEnum.TaskTypeStatus taskTypeStatus;

    @JsonProperty(value = "isTaskAvailable")
    private boolean isTaskAvailable;
    @JsonProperty(value = "isTaskUnavailable")
    private boolean isTaskUnavailable;
    @JsonProperty(value = "isEmergency")
    private boolean isEmergency;
    private boolean assignedToRegion;
    @JsonProperty(value = "isTaskShift")
    private boolean isTaskShift;
    @JsonProperty(value = "isTaskEminent")
    private boolean isTaskEminent;
    private boolean canInsertIntoOtherShift;
    @JsonProperty(value = "isTaskVoluntary")
    private boolean isTaskVoluntary;
    @JsonProperty(value = "isTaskOnCall")
    private boolean isTaskOnCall;
    @JsonProperty(value = "isTaskStandBy")
    private boolean isTaskStandBy;
    @JsonProperty(value = "isTaskInterruptable")
    private boolean isTaskInterruptable;
    @JsonProperty(value = "isFixed")
    private boolean isFixed;
    private boolean canIncludeOvertime;
    private boolean canIncludeExtraTime;
    private boolean canIncludeFlexiTime;
    private TaskTypeEnum.GenderRestrictions genderRestrictions;
    @JsonProperty(value = "isProject")
    private boolean isProject;
    private boolean hasMaxDeploymentRadius;
    private boolean hasMinDeploymentRadius;
    private boolean hasMaxDrivingDistance;
    private boolean canExpire;
    private boolean hasWarningDays;
    private boolean hasPlanningPoole;
    private TaskTypeEnum.PreplanStatus preplanStatus;
    private boolean recurring;
    private String scheduledType;
    private String type;
    private boolean hasTimeRules;
    private boolean hasFromAndEndAddress;
    private boolean hasFromAddress;
    private boolean hasToAddress;
    private boolean hasViaAddress;
    @JsonProperty(value = "isVirtual")
    private boolean isVirtual;
    private boolean hasOvernightStays;
    private boolean confirmBeforePlanning;
    @JsonProperty(value = "isOverBooked")
    private boolean isOverBooked;
    private boolean hasMultipleEmployees;
    @JsonProperty(value = "isMultiDayTask")
    private boolean isMultiDayTask;
    private String tuUnit;
    private String matrix;
    private String priority;
    private boolean hasVisitation;
    private boolean hasCapacity;
    private boolean hasAbsence;
    private boolean hasRequests;
    private boolean hasDraft;
    private boolean hasConstruction;
    @JsonProperty(value = "isFinal")
    private boolean isFinal;
    private boolean hasShortTeam;
    private boolean hasRealTime;
    private boolean hasPast;
    private boolean hasPayroll;
    private String reserveType;
    @JsonProperty(value = "isUsedBy")
    private boolean isUsedBy;
    @JsonProperty(value = "isTimeBank")
    private boolean isTimeBank;


    private boolean hasIndoor;

    private boolean hasPrimaryAddress;


    public void setReserveType(String reserveType) {
        this.reserveType = reserveType;
    }

    public String getReserveType() {

        return reserveType;
    }

    public void setTaskTypeStatus(TaskTypeEnum.TaskTypeStatus taskTypeStatus) {
        this.taskTypeStatus = taskTypeStatus;
    }

    public void setTaskAvailable(boolean taskAvailable) {
        isTaskAvailable = taskAvailable;
    }

    public void setTaskUnavailable(boolean taskUnavailable) {
        isTaskUnavailable = taskUnavailable;
    }

    public void setEmergency(boolean emergency) {
        isEmergency = emergency;
    }

    public void setAssignedToRegion(boolean assignedToRegion) {
        this.assignedToRegion = assignedToRegion;
    }

    public void setTaskShift(boolean taskShift) {
        isTaskShift = taskShift;
    }

    public void setTaskEminent(boolean taskEminent) {
        isTaskEminent = taskEminent;
    }

    public void setCanInsertIntoOtherShift(boolean canInsertIntoOtherShift) {
        this.canInsertIntoOtherShift = canInsertIntoOtherShift;
    }

    public void setTaskVoluntary(boolean taskVoluntary) {
        isTaskVoluntary = taskVoluntary;
    }

    public void setTaskOnCall(boolean taskOnCall) {
        isTaskOnCall = taskOnCall;
    }

    public void setTaskStandBy(boolean taskStandBy) {
        isTaskStandBy = taskStandBy;
    }

    public void setTaskInterruptable(boolean taskInterruptable) {
        isTaskInterruptable = taskInterruptable;
    }

    public void setFixed(boolean fixed) {
        isFixed = fixed;
    }

    public void setCanIncludeOvertime(boolean canIncludeOvertime) {
        this.canIncludeOvertime = canIncludeOvertime;
    }

    public void setCanIncludeExtraTime(boolean canIncludeExtraTime) {
        this.canIncludeExtraTime = canIncludeExtraTime;
    }

    public void setCanIncludeFlexiTime(boolean canIncludeFlexiTime) {
        this.canIncludeFlexiTime = canIncludeFlexiTime;
    }

    public void setGenderRestrictions(TaskTypeEnum.GenderRestrictions genderRestrictions) {
        this.genderRestrictions = genderRestrictions;
    }

    public void setProject(boolean project) {
        isProject = project;
    }

    public void setHasMaxDeploymentRadius(boolean hasMaxDeploymentRadius) {
        this.hasMaxDeploymentRadius = hasMaxDeploymentRadius;
    }

    public void setHasMinDeploymentRadius(boolean hasMinDeploymentRadius) {
        this.hasMinDeploymentRadius = hasMinDeploymentRadius;
    }

    public void setHasMaxDrivingDistance(boolean hasMaxDrivingDistance) {
        this.hasMaxDrivingDistance = hasMaxDrivingDistance;
    }

    public void setCanExpire(boolean canExpire) {
        this.canExpire = canExpire;
    }

    public void setHasWarningDays(boolean hasWarningDays) {
        this.hasWarningDays = hasWarningDays;
    }

    public void setHasPlanningPoole(boolean hasPlanningPoole) {
        this.hasPlanningPoole = hasPlanningPoole;
    }

    public void setPreplanStatus(TaskTypeEnum.PreplanStatus preplanStatus) {
        this.preplanStatus = preplanStatus;
    }

    public void setRecurring(boolean recurring) {
        this.recurring = recurring;
    }

    public void setScheduledType(String scheduledType) {
        this.scheduledType = scheduledType;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setHasTimeRules(boolean hasTimeRules) {
        this.hasTimeRules = hasTimeRules;
    }

    public void setHasFromAndEndAddress(boolean hasFromAndEndAddress) {
        this.hasFromAndEndAddress = hasFromAndEndAddress;
    }

    public void setHasToAddress(boolean hasToAddress) {
        this.hasToAddress = hasToAddress;
    }

    public void setHasViaAddress(boolean hasViaAddress) {
        this.hasViaAddress = hasViaAddress;
    }

    public void setVirtual(boolean virtual) {
        isVirtual = virtual;
    }

    public void setHasOvernightStays(boolean hasOvernightStays) {
        this.hasOvernightStays = hasOvernightStays;
    }

    public void setConfirmBeforePlanning(boolean confirmBeforePlanning) {
        this.confirmBeforePlanning = confirmBeforePlanning;
    }

    public void setOverBooked(boolean overBooked) {
        isOverBooked = overBooked;
    }

    public void setHasMultipleEmployees(boolean hasMultipleEmployees) {
        this.hasMultipleEmployees = hasMultipleEmployees;
    }

    public void setMultiDayTask(boolean multiDayTask) {
        isMultiDayTask = multiDayTask;
    }

    public void setTuUnit(String tuUnit) {
        this.tuUnit = tuUnit;
    }

    public void setMatrix(String matrix) {
        this.matrix = matrix;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public void setHasVisitation(boolean hasVisitation) {
        this.hasVisitation = hasVisitation;
    }

    public void setHasCapacity(boolean hasCapacity) {
        this.hasCapacity = hasCapacity;
    }

    public void setHasAbsence(boolean hasAbsence) {
        this.hasAbsence = hasAbsence;
    }

    public void setHasRequests(boolean hasRequests) {
        this.hasRequests = hasRequests;
    }

    public void setHasDraft(boolean hasDraft) {
        this.hasDraft = hasDraft;
    }

    public void setHasConstruction(boolean hasConstruction) {
        this.hasConstruction = hasConstruction;
    }

    public void setFinal(boolean aFinal) {
        isFinal = aFinal;
    }

    public void setHasShortTeam(boolean hasShortTeam) {
        this.hasShortTeam = hasShortTeam;
    }

    public void setHasRealTime(boolean hasRealTime) {
        this.hasRealTime = hasRealTime;
    }

    public void setHasPast(boolean hasPast) {
        this.hasPast = hasPast;
    }

    public void setHasPayroll(boolean hasPayroll) {
        this.hasPayroll = hasPayroll;
    }

    public TaskTypeEnum.TaskTypeStatus getTaskTypeStatus() {
        return taskTypeStatus;
    }

    public boolean isTaskAvailable() {
        return isTaskAvailable;
    }

    public boolean isTaskUnavailable() {
        return isTaskUnavailable;
    }

    public boolean isEmergency() {
        return isEmergency;
    }

    public boolean isAssignedToRegion() {
        return assignedToRegion;
    }

    public boolean isTaskShift() {
        return isTaskShift;
    }

    public boolean isTaskEminent() {
        return isTaskEminent;
    }

    public boolean isCanInsertIntoOtherShift() {
        return canInsertIntoOtherShift;
    }

    public boolean isTaskVoluntary() {
        return isTaskVoluntary;
    }

    public boolean isTaskOnCall() {
        return isTaskOnCall;
    }

    public boolean isTaskStandBy() {
        return isTaskStandBy;
    }

    public boolean isTaskInterruptable() {
        return isTaskInterruptable;
    }

    public boolean isFixed() {
        return isFixed;
    }

    public boolean isCanIncludeOvertime() {
        return canIncludeOvertime;
    }

    public boolean isCanIncludeExtraTime() {
        return canIncludeExtraTime;
    }

    public boolean isCanIncludeFlexiTime() {
        return canIncludeFlexiTime;
    }

    public TaskTypeEnum.GenderRestrictions getGenderRestrictions() {
        return genderRestrictions;
    }

    public boolean isProject() {
        return isProject;
    }

    public boolean isHasMaxDeploymentRadius() {
        return hasMaxDeploymentRadius;
    }

    public boolean isHasMinDeploymentRadius() {
        return hasMinDeploymentRadius;
    }

    public boolean isHasMaxDrivingDistance() {
        return hasMaxDrivingDistance;
    }

    public boolean isCanExpire() {
        return canExpire;
    }

    public boolean isHasWarningDays() {
        return hasWarningDays;
    }

    public boolean isHasPlanningPoole() {
        return hasPlanningPoole;
    }

    public TaskTypeEnum.PreplanStatus getPreplanStatus() {
        return preplanStatus;
    }

    public boolean isRecurring() {
        return recurring;
    }

    public String getScheduledType() {
        return scheduledType;
    }

    public String getType() {
        return type;
    }

    public boolean isHasTimeRules() {
        return hasTimeRules;
    }

    public boolean isHasFromAndEndAddress() {
        return hasFromAndEndAddress;
    }

    public boolean isHasToAddress() {
        return hasToAddress;
    }

    public boolean isHasViaAddress() {
        return hasViaAddress;
    }

    public boolean isVirtual() {
        return isVirtual;
    }

    public boolean isHasOvernightStays() {
        return hasOvernightStays;
    }

    public boolean isConfirmBeforePlanning() {
        return confirmBeforePlanning;
    }

    public boolean isOverBooked() {
        return isOverBooked;
    }

    public boolean isHasMultipleEmployees() {
        return hasMultipleEmployees;
    }

    public boolean isMultiDayTask() {
        return isMultiDayTask;
    }

    public String getTuUnit() {
        return tuUnit;
    }

    public String getMatrix() {
        return matrix;
    }

    public String getPriority() {
        return priority;
    }

    public boolean isHasVisitation() {
        return hasVisitation;
    }

    public boolean isHasCapacity() {
        return hasCapacity;
    }

    public boolean isHasAbsence() {
        return hasAbsence;
    }

    public boolean isHasRequests() {
        return hasRequests;
    }

    public boolean isHasDraft() {
        return hasDraft;
    }

    public boolean isHasConstruction() {
        return hasConstruction;
    }

    public boolean isFinal() {
        return isFinal;
    }

    public boolean isHasShortTeam() {
        return hasShortTeam;
    }

    public boolean isHasRealTime() {
        return hasRealTime;
    }

    public boolean isHasPast() {
        return hasPast;
    }

    public boolean isHasPayroll() {
        return hasPayroll;
    }

    public boolean isHasFromAddress() {
        return hasFromAddress;
    }

    public boolean isUsedBy() {
        return isUsedBy;
    }

    public void setUsedBy(boolean usedBy) {

        isUsedBy = usedBy;
    }

    public void setHasFromAddress(boolean hasFromAddress) {

        this.hasFromAddress = hasFromAddress;
    }

    public boolean isTimeBank() {
        return isTimeBank;
    }

    public void setTimeBank(boolean timeBank) {
        isTimeBank = timeBank;
    }

    public boolean isHasIndoor() {
        return hasIndoor;
    }

    public void setHasIndoor(boolean hasIndoor) {
        this.hasIndoor = hasIndoor;
    }

    public boolean isHasPrimaryAddress() {
        return hasPrimaryAddress;
    }

    public void setHasPrimaryAddress(boolean hasPrimaryAddress) {
        this.hasPrimaryAddress = hasPrimaryAddress;
    }

    protected TaskTypeDefination cloneObject(){
        return ObjectMapperUtils.copyPropertiesByMapper(this,TaskTypeDefination.class);
    }

}

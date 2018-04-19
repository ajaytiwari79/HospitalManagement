package com.planning.domain.taskPlanning;

import com.planning.domain.common.BaseEntity;
import com.planning.enums.ContraintType;
import org.springframework.data.cassandra.mapping.Table;

@Table
public class PlanningConfig extends BaseEntity{

    private ContraintType longestShiftType = ContraintType.MEDIUM;
    private ContraintType shortestWorkingDayType = ContraintType.MEDIUM;
    private ContraintType maxWorkingTimeType = ContraintType.MEDIUM;
    private ContraintType TeamPriorityType = ContraintType.SOFT;
    private ContraintType twoPersonsTaskType = ContraintType.HARD;
    private ContraintType carStatusType = ContraintType.HARD;
    private ContraintType emergencyVehicleType = ContraintType.SOFT;
    private ContraintType delayedTaskHigherPrioritType = ContraintType.SOFT;
    private ContraintType highPriorityTaskType = ContraintType.SOFT;
    private ContraintType threeTimeWindowsType = ContraintType.MEDIUM;
    private ContraintType staffAffinityType = ContraintType.SOFT;
    private ContraintType citizenAffintyType = ContraintType.SOFT;
    private ContraintType citizenPunctualityType = ContraintType.SOFT;
    private ContraintType citizenMoreVisitForStaffType = ContraintType.SOFT;
    private ContraintType citizentPresisionAffinityType = ContraintType.SOFT;
    private ContraintType vetodayConstraintType = ContraintType.MEDIUM;
    private ContraintType fullDayAbsenceType = ContraintType.HARD;
    private ContraintType StopBrickConstraintType = ContraintType.HARD;
    private ContraintType restingTimeConstraintType = ContraintType.HARD;
    private ContraintType meetingConstraintType = ContraintType.SOFT;
    private ContraintType breakConstraintsType = ContraintType.SOFT;
    private ContraintType restingTimeConstraintAsSoftType = ContraintType.SOFT;
    private ContraintType staffPointOptimizationType = ContraintType.SOFT;
    private ContraintType cheapestStaffType;
    private ContraintType cheapestVehicleType;
    private ContraintType swapsVehiclesType = ContraintType.SOFT;
    private ContraintType co2CEmissionType = ContraintType.SOFT;
    private ContraintType sameLocalAreaType = ContraintType.SOFT;
    private ContraintType specificVehicleSkillsType = ContraintType.MEDIUM;
    private ContraintType chargingTimeForEletricVehiclesType = ContraintType.SOFT;
    private ContraintType maxRangeOfElectricVehiclesType = ContraintType.SOFT;
    private ContraintType simultaionousCharingOnDepotType = ContraintType.HARD;
    private ContraintType staffTaskTypeExperienceType = ContraintType.SOFT;
    private ContraintType staffTaskTypeDissatisfactionType = ContraintType.SOFT;
    private ContraintType staffBringsPrivateCarType = ContraintType.HARD;
    private ContraintType staffPreferencesToLocalAreaType = ContraintType.SOFT;
    private ContraintType staffpreferredTransPortationType = ContraintType.SOFT;
    private ContraintType vehicleContinuityType = ContraintType.SOFT;
    private ContraintType staffContinuityType = ContraintType.SOFT;
    private ContraintType staffServicetypeContinuityType = ContraintType.SOFT;
    private ContraintType highLevelBuildingType;
    private ContraintType localAreaAffinityType = ContraintType.SOFT;
    private ContraintType vehicleCapacityType = ContraintType.HARD;
    private ContraintType otherUnitAtSameTimeConstraintsType = ContraintType.SOFT;
    private ContraintType householdOnSameTimeConstraintsType = ContraintType.SOFT;
    private ContraintType staffBicyclePreferenceType = ContraintType.SOFT;
    private ContraintType staffCarPreferenceType = ContraintType.SOFT;
    private ContraintType staffPreferenceForBreakLocationType = ContraintType.SOFT;
    private ContraintType staffExtraTimePrefrenceType;



    private int preExceedingShift;
    private int postExceedingShift;
    private int exceedingShiftLimit;
    private int staffAffinity;
    private int citizenAffinity;
    private int staffCost;
    private int taskWithinWindow;
    private int co2Emission;
    private int considerUnavailableEmployee;


    public int getPreExceedingShift() {
        return preExceedingShift;
    }

    public void setPreExceedingShift(int preExceedingShift) {
        this.preExceedingShift = preExceedingShift;
    }

    public int getPostExceedingShift() {
        return postExceedingShift;
    }

    public void setPostExceedingShift(int postExceedingShift) {
        this.postExceedingShift = postExceedingShift;
    }

    public int getExceedingShiftLimit() {
        return exceedingShiftLimit;
    }

    public void setExceedingShiftLimit(int exceedingShiftLimit) {
        this.exceedingShiftLimit = exceedingShiftLimit;
    }

    public int getStaffAffinity() {
        return staffAffinity;
    }

    public void setStaffAffinity(int staffAffinity) {
        this.staffAffinity = staffAffinity;
    }

    public int getCitizenAffinity() {
        return citizenAffinity;
    }

    public void setCitizenAffinity(int citizenAffinity) {
        this.citizenAffinity = citizenAffinity;
    }

    public int getStaffCost() {
        return staffCost;
    }

    public void setStaffCost(int staffCost) {
        this.staffCost = staffCost;
    }

    public int getTaskWithinWindow() {
        return taskWithinWindow;
    }

    public void setTaskWithinWindow(int taskWithinWindow) {
        this.taskWithinWindow = taskWithinWindow;
    }

    public int getCo2Emission() {
        return co2Emission;
    }

    public void setCo2Emission(int co2Emission) {
        this.co2Emission = co2Emission;
    }

    public int getConsiderUnavailableEmployee() {
        return considerUnavailableEmployee;
    }

    public void setConsiderUnavailableEmployee(int considerUnavailableEmployee) {
        this.considerUnavailableEmployee = considerUnavailableEmployee;
    }
}

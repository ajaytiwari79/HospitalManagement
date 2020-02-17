package com.kairos.wrappers;

/**
 * Created by oodles on 3/10/17.
 */
public class VisitatedHoursTasksCount {


    protected long unitId;

    protected long citizenId;

    private long visitatedHoursPerWeek;

    private long visitatedHoursPerMonth;

    private long visitatedMinutesPerWeek;

    private long visitatedMinutesPerMonth;

    private float visitatedTasksPerWeek;

    private float visitatedTasksPerMonth;

    public long getUnitId() {
        return unitId;
    }

    public void setUnitId(long unitId) {
        this.unitId = unitId;
    }

    public long getCitizenId() {
        return citizenId;
    }

    public void setCitizenId(long citizenId) {
        this.citizenId = citizenId;
    }

    public long getVisitatedHoursPerWeek() {
        return visitatedHoursPerWeek;
    }

    public void setVisitatedHoursPerWeek(long visitatedHoursPerWeek) {
        this.visitatedHoursPerWeek = visitatedHoursPerWeek;
    }

    public long getVisitatedHoursPerMonth() {
        return visitatedHoursPerMonth;
    }

    public void setVisitatedHoursPerMonth(long visitatedHoursPerMonth) {
        this.visitatedHoursPerMonth = visitatedHoursPerMonth;
    }

    public long getVisitatedMinutesPerWeek() {
        return visitatedMinutesPerWeek;
    }

    public void setVisitatedMinutesPerWeek(long visitatedMinutesPerWeek) {
        this.visitatedMinutesPerWeek = visitatedMinutesPerWeek;
    }

    public long getVisitatedMinutesPerMonth() {
        return visitatedMinutesPerMonth;
    }

    public void setVisitatedMinutesPerMonth(long visitatedMinutesPerMonth) {
        this.visitatedMinutesPerMonth = visitatedMinutesPerMonth;
    }

    public float getVisitatedTasksPerWeek() {
        return visitatedTasksPerWeek;
    }

    public void setVisitatedTasksPerWeek(float visitatedTasksPerWeek) {
        this.visitatedTasksPerWeek = visitatedTasksPerWeek;
    }

    public float getVisitatedTasksPerMonth() {
        return visitatedTasksPerMonth;
    }

    public void setVisitatedTasksPerMonth(float visitatedTasksPerMonth) {
        this.visitatedTasksPerMonth = visitatedTasksPerMonth;
    }
}

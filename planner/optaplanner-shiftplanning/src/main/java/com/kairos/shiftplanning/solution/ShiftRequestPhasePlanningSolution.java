package com.kairos.shiftplanning.solution;

import com.kairos.shiftplanning.domain.activity.Activity;
import com.kairos.shiftplanning.domain.activity.ActivityLineInterval;
import com.kairos.shiftplanning.domain.shift.ShiftImp;
import com.kairos.shiftplanning.domain.staff.Employee;
import com.kairos.shiftplanning.domain.staffing_level.SkillLineInterval;
import com.kairos.shiftplanning.domain.staffing_level.StaffingLevelMatrix;
import com.kairos.shiftplanning.domain.unit.Unit;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import org.joda.time.LocalDate;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningEntityProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.drools.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.solution.drools.ProblemFactProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScore;
import org.optaplanner.persistence.xstream.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScoreXStreamConverter;

import java.util.List;
import java.util.Map;

@PlanningSolution
@XStreamAlias("ShiftPlanningSolution")
public class ShiftRequestPhasePlanningSolution {
	private String id;
	private Long unitId;
	@ProblemFactCollectionProperty
    private List<Employee> employees;
    @ProblemFactCollectionProperty
    private List<LocalDate> weekDates;
    @PlanningEntityCollectionProperty
    @ValueRangeProvider(id = "shifts")
    private List<ShiftImp> shifts;
    //TODO it should have all activities per day
    @ProblemFactCollectionProperty
    private List<Activity> activities;
    @PlanningEntityCollectionProperty
    //@ValueRangeProvider(id = "activityLineIntervals")
    private List<ActivityLineInterval> activityLineIntervals;
    @PlanningEntityCollectionProperty
    private List<SkillLineInterval> skillLineIntervals;
    @ProblemFactProperty
    private Unit unit;

    //@PlanningEntityCollectionProperty
    //private List<DateTime> possibleStartDateTimes;
    private Map<LocalDate,List<Activity>> activitiesPerDay;
	@XStreamConverter(HardMediumSoftLongScoreXStreamConverter.class)
	@PlanningScore
    private HardMediumSoftLongScore score;
    @ProblemFactProperty
    private StaffingLevelMatrix staffingLevelMatrix;

    private Map<String,List<ActivityLineInterval>> activitiesIntervalsGroupedPerDay;


    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public List<ShiftImp> getShifts() {
        return shifts;
    }

    public void setShifts(List<ShiftImp> shifts) {
        this.shifts = shifts;
    }

    public List<Activity> getActivities() {
        return activities;
    }

    public void setActivities(List<Activity> activities) {
        this.activities = activities;
    }

    public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public List<Employee> getEmployees() {
		return employees;
	}

	public void setEmployees(List<Employee> employees) {
		this.employees = employees;
	}


    public HardMediumSoftLongScore getScore() {
		return score;
	}

	public void setScore(HardMediumSoftLongScore score) {
		this.score = score;
	}


    public List<ActivityLineInterval> getActivityLineIntervals() {
        return activityLineIntervals;
    }

    public void setActivityLineIntervals(List<ActivityLineInterval> activityLineIntervals) {
        this.activityLineIntervals = activityLineIntervals;
    }

    public List<SkillLineInterval> getSkillLineIntervals() {
        return skillLineIntervals;
    }

    public void setSkillLineIntervals(List<SkillLineInterval> skillLineIntervals) {
        this.skillLineIntervals = skillLineIntervals;
    }

    public List<LocalDate> getWeekDates() {
        return weekDates;
    }

    public void setWeekDates(List<LocalDate> weekDates) {
        this.weekDates = weekDates;
    }

    public Map<LocalDate, List<Activity>> getActivitiesPerDay() {
        return activitiesPerDay;
    }

    public void setActivitiesPerDay(Map<LocalDate, List<Activity>> activitiesPerDay) {
        this.activitiesPerDay = activitiesPerDay;
    }

    public StaffingLevelMatrix getStaffingLevelMatrix() {
        return staffingLevelMatrix;
    }

    public void setStaffingLevelMatrix(StaffingLevelMatrix staffingLevelMatrix) {
        this.staffingLevelMatrix = staffingLevelMatrix;
    }

    public Map<String, List<ActivityLineInterval>> getActivitiesIntervalsGroupedPerDay() {
        return activitiesIntervalsGroupedPerDay;
    }

    public void setActivitiesIntervalsGroupedPerDay(Map<String, List<ActivityLineInterval>> activitiesIntervalsGroupedPerDay) {
        this.activitiesIntervalsGroupedPerDay = activitiesIntervalsGroupedPerDay;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }
}

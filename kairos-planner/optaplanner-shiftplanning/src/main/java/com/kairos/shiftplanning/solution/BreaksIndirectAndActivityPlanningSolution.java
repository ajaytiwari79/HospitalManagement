package com.kairos.shiftplanning.solution;

import com.kairos.shiftplanning.domain.*;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.drools.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.solution.drools.ProblemFactProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScore;
import org.optaplanner.persistence.xstream.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScoreXStreamConverter;

import java.util.List;

@PlanningSolution
@XStreamAlias("BreaksIndirectAndActivityPlanningSolution")
public class BreaksIndirectAndActivityPlanningSolution {
	private String id;
	private Long unitId;
	@ProblemFactCollectionProperty
    private List<EmployeePlanningFact> employees;
    @ProblemFactCollectionProperty
    private List<LocalDate> weekDates;
    @PlanningEntityCollectionProperty
    private List<IndirectActivity> indirectActivities;
    @ProblemFactCollectionProperty
    //@ValueRangeProvider(id = "shifts")
    private List<ShiftRequestPhase> shifts;
    @ProblemFactCollectionProperty
    private List<ActivityPlannerEntity> activities;
    @ProblemFactCollectionProperty
    //@ValueRangeProvider(id = "activityLineIntervals")
    private List<ActivityLineInterval> activityLineIntervals;
    @ProblemFactCollectionProperty
    private List<SkillLineInterval> skillLineIntervals;
    @PlanningEntityCollectionProperty
    private List<ShiftBreak> shiftBreaks;
    @ProblemFactCollectionProperty
    @ValueRangeProvider(id = "possibleStartDateTimes")
    private List<DateTime> possibleStartDateTimes;
	@XStreamConverter(HardMediumSoftLongScoreXStreamConverter.class)
	@PlanningScore
    private HardMediumSoftLongScore score;
	//private Map<LocalDate, Object[]> staffingLevelMatrix;
    @ProblemFactProperty
    private StaffingLevelMatrix staffingLevelMatrix;


    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public List<ShiftRequestPhase> getShifts() {
        return shifts;
    }

    public void setShifts(List<ShiftRequestPhase> shifts) {
        this.shifts = shifts;
    }

    public List<ActivityPlannerEntity> getActivities() {
        return activities;
    }

    public void setActivities(List<ActivityPlannerEntity> activities) {
        this.activities = activities;
    }

    public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public List<EmployeePlanningFact> getEmployees() {
		return employees;
	}

	public void setEmployees(List<EmployeePlanningFact> employees) {
		this.employees = employees;
	}


    public HardMediumSoftLongScore getScore() {
		return score;
	}

	public void setScore(HardMediumSoftLongScore score) {
		this.score = score;
	}


    public List<IndirectActivity> getIndirectActivities() {
        return indirectActivities;
    }

    public void setIndirectActivities(List<IndirectActivity> indirectActivities) {
        this.indirectActivities = indirectActivities;
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

    public List<DateTime> getPossibleStartDateTimes() {
        return possibleStartDateTimes;
    }

    public void setPossibleStartDateTimes(List<DateTime> possibleStartDateTimes) {
        this.possibleStartDateTimes = possibleStartDateTimes;
    }

    public List<LocalDate> getWeekDates() {
        return weekDates;
    }

    public void setWeekDates(List<LocalDate> weekDates) {
        this.weekDates = weekDates;
    }

    public List<ShiftBreak> getShiftBreaks() {
        return shiftBreaks;
    }

    public void setShiftBreaks(List<ShiftBreak> shiftBreaks) {
        this.shiftBreaks = shiftBreaks;
    }

    public StaffingLevelMatrix getStaffingLevelMatrix() {
        return staffingLevelMatrix;
    }

    public void setStaffingLevelMatrix(StaffingLevelMatrix staffingLevelMatrix) {
        this.staffingLevelMatrix = staffingLevelMatrix;
    }
}

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
import lombok.Getter;
import lombok.Setter;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.drools.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.solution.drools.ProblemFactProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScore;
import org.optaplanner.persistence.xstream.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScoreXStreamConverter;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@PlanningSolution
@Getter
@Setter
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
    @ProblemFactCollectionProperty
    private List<Activity> activities;
    @PlanningEntityCollectionProperty
    private List<ActivityLineInterval> activityLineIntervals;
    @PlanningEntityCollectionProperty
    private List<SkillLineInterval> skillLineIntervals;
    @ProblemFactProperty
    private Unit unit;
    private Map<LocalDate,List<Activity>> activitiesPerDay;
	@XStreamConverter(HardMediumSoftLongScoreXStreamConverter.class)
	@PlanningScore
    private HardMediumSoftLongScore score;
    @ProblemFactProperty
    private StaffingLevelMatrix staffingLevelMatrix;

    private Map<String,List<ActivityLineInterval>> activitiesIntervalsGroupedPerDay;

}

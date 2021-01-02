package com.kairos.shiftplanningNewVersion.solution;

import com.kairos.shiftplanning.domain.activity.Activity;
import com.kairos.shiftplanning.domain.unit.Unit;
import com.kairos.shiftplanningNewVersion.entity.ALI;
import com.kairos.shiftplanningNewVersion.entity.Shift;
import com.kairos.shiftplanningNewVersion.entity.Staff;
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
@Getter
@Setter
@XStreamAlias("StaffingLevelSolution")
@PlanningSolution
public class StaffingLevelSolution {

    private String id;
    @ProblemFactCollectionProperty
    private List<Staff> staffs;
    @ProblemFactCollectionProperty
    private List<LocalDate> weekDates;
    @PlanningEntityCollectionProperty
    @ValueRangeProvider(id = "shifts")
    private List<Shift> shifts;
    @ProblemFactCollectionProperty
    private List<Activity> activities;
    @ProblemFactCollectionProperty
    private List<List<ALI>> aliPerActivities;
    @PlanningEntityCollectionProperty
    private List<ALI> activityLineIntervals;
    @ProblemFactProperty
    private Unit unit;
    private Map<LocalDate,List<Activity>> activitiesPerDay;
    @XStreamConverter(HardMediumSoftLongScoreXStreamConverter.class)
    @PlanningScore
    private HardMediumSoftLongScore score;

    public HardMediumSoftLongScore getScore() {
        return score;
    }

    public void setScore(HardMediumSoftLongScore score) {
        this.score = score;
    }
}

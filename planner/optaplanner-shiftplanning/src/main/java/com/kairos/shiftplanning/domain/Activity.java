package com.kairos.shiftplanning.domain;

import com.kairos.shiftplanning.domain.activityConstraint.*;
import com.kairos.shiftplanning.domain.wta.WorkingTimeConstraints;
import com.kairos.shiftplanning.executioner.ShiftPlanningGenerator;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.kie.api.runtime.rule.RuleContext;
import org.optaplanner.core.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScoreHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@XStreamAlias("Activity")
public class Activity {

    private static Logger log= LoggerFactory.getLogger(WorkingTimeConstraints.class);

    private String id;
    private List<Skill> skills;
    private int priority;
    private String name;
    private ActivityConstraints activityConstraints;
    private TimeType timeType;
    private int order;
    private int rank;
    private List<Long> expertises;

    public Activity(String id, List<Skill> skills, int priority, String name, TimeType timeType, int order, int rank, List<Long> expertises) {
        this.id = id;
        this.skills = skills;
        this.priority = priority;
        this.name = name;
        this.timeType=timeType;
        this.order = order;
        this.rank=rank;
        this.expertises = expertises;
    }
    public Activity() {
    }


    public TimeType getTimeType() {
        return timeType;
    }

    public void setTimeType(TimeType timeType) {
        this.timeType = timeType;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Skill> getSkills() {
        return skills;
    }

    public void setSkills(List<Skill> skills) {
        this.skills = skills;
    }


    public ActivityConstraints getActivityConstraints() {
        return activityConstraints;
    }

    public void setActivityConstraints(ActivityConstraints activityConstraints) {
        this.activityConstraints = activityConstraints;
    }
    public boolean isBlankActivity(){
        return this.name== ShiftPlanningGenerator.BLANK_ACTIVITY;
    }
    public boolean isTypePresence(){
        boolean presence=false;
        switch (timeType.getName()){
            case "presence":presence=true;
            break;
            default: presence=false;
        }
        return presence;
    }
    public boolean isTypeAbsence(){
        boolean absence=true;
        switch (timeType.getName()){
            //More to be added here
            case "presence":absence=false;
                break;
            default: absence=true;
        }
        return absence;
    }

    public int checkActivityConstraints(ShiftRequestPhase shift, int index) {
        if(shift.isLocked()) return 0;
            switch (index){
                /*case 1:return activityConstraints.getLongestDuration().checkConstraints(this,shift);
                case 2:return activityConstraints.getShortestDuration().checkConstraints(this,shift);*/
                case 3:return activityConstraints.getMaxAllocationPerShift().checkConstraints(this,shift);
                case 4:return activityConstraints.getMaxDiffrentActivity().checkConstraints(shift);
                //case 5:return skillsSatisFaction(shift);
                case 6:return activityConstraints.getMinimumLengthofActivity().checkConstraints(this,shift);
                case 7:return activityConstraints.getActivityDayType().checkConstraints(shift);

            }
        return 0;
    }
   /* public boolean checkActivityConstraints(ActivityLineInterval lineInterval, int index) {
        return activityConstraints.getContinousActivityPerShift().checkConstraints(lineInterval);
    }*/


    public void breakActivityContraints(ShiftRequestPhase shift, HardMediumSoftLongScoreHolder scoreHolder, RuleContext kContext, int constraintPenality, int index) {
        log.debug("breaking Activity constraint: {}",index);
        switch (index) {
            /*case 1:
                activityConstraints.getLongestDuration().breakLevelConstraints(scoreHolder, kContext,constraintPenality);
                break;
            case 2:
                activityConstraints.getShortestDuration().breakLevelConstraints(scoreHolder, kContext,constraintPenality);
                break;*/
            case 3:
                activityConstraints.getMaxAllocationPerShift().breakLevelConstraints(scoreHolder, kContext,constraintPenality);
                break;
            case 4:
                activityConstraints.getMaxDiffrentActivity().breakLevelConstraints(scoreHolder, kContext,constraintPenality);
                break;
            case 5:
                brokeActivitySkillConstraints(constraintPenality,scoreHolder, kContext);
                break;
            case 6:
                activityConstraints.getMinimumLengthofActivity().breakLevelConstraints(scoreHolder, kContext,constraintPenality);
                break;
            case 7:
                activityConstraints.getActivityDayType().breakLevelConstraints(scoreHolder, kContext,constraintPenality);
                break;
        }
    }

    public void broketaskPriorityConstraints(HardMediumSoftLongScoreHolder scoreHolder, RuleContext kContext){
        switch (priority){
            case 1:scoreHolder.addSoftConstraintMatch(kContext,-1);
            break;
            case 2:scoreHolder.addMediumConstraintMatch(kContext,-1);
            break;
            case 3:scoreHolder.addSoftConstraintMatch(kContext,-1);
            break;
        }
    }

    public int skillsSatisFaction(ShiftRequestPhase shift) {
        List<Skill> skills = (List<Skill>) CollectionUtils.subtract(this.skills, shift.getEmployee().getSkillSet());
        int weight = skills.stream().mapToInt(s -> s.getWeight()).sum();
        return weight;
    }

    public void brokeActivitySkillConstraints(int constraintPenality,HardMediumSoftLongScoreHolder scoreHolder, RuleContext kContext) {
        /*List<Skill> skills = (List<Skill>) CollectionUtils.subtract(this.skills, shiftRequestPhase.getEmployee().getSkillSet());
        int weight = skills.stream().mapToInt(s -> s.getWeight()).sum();*/
        scoreHolder.addSoftConstraintMatch(kContext, constraintPenality);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        //Activity activity = (Activity) o;
        return id.equals(((Activity) o).getId());

        /*return new EqualsBuilder()
                .append(priority, activity.priority)
                .append(activityCost, activity.activityCost)
                .append(id, activity.id)
                .append(skills, activity.skills)
                .append(name, activity.name)
                .append(activityConstraints, activity.activityConstraints)
                .isEquals();*/
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(skills)
                .append(priority)
                .append(name)
                .append(activityConstraints)
                .toHashCode();
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public List<Long> getExpertises() {
        return expertises;
    }

    public void setExpertises(List<Long> expertises) {
        this.expertises = expertises;
    }
}

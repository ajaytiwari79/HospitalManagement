package com.kairos.shiftplanning.domain.activity;

import com.kairos.enums.constraint.ConstraintSubType;
import com.kairos.shiftplanning.constraints.Constraint;
import com.kairos.shiftplanning.domain.shift.ShiftImp;
import com.kairos.shiftplanning.domain.skill.Skill;
import com.kairos.shiftplanning.domain.tag.Tag;
import com.kairos.shiftplanning.domain.timetype.TimeType;
import com.kairos.shiftplanning.domain.wta.WorkingTimeConstraints;
import com.kairos.shiftplanning.executioner.ShiftPlanningGenerator;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.kie.api.runtime.rule.RuleContext;
import org.optaplanner.core.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScoreHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@XStreamAlias("Activity")
public class Activity {

    private static Logger log= LoggerFactory.getLogger(WorkingTimeConstraints.class);

    private String id;
    private List<Skill> skills;
    private int priority;
    private String name;
    private Map<ConstraintSubType, Constraint> constraintMap;
    private TimeType timeType;
    private int order;
    private int rank;
    private List<Long> expertises;
    private List<Tag> tags;


    public Activity(String id, List<Skill> skills, int priority, String name, TimeType timeType, int order, int rank, List<Long> expertises, List<Tag> tags) {
        this.id = id;
        this.skills = skills;
        this.priority = priority;
        this.name = name;
        this.timeType=timeType;
        this.order = order;
        this.rank=rank;
        this.expertises = expertises;
        this.tags = tags;
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

    public int checkActivityConstraints(ShiftImp shift, ConstraintSubType constraintSubType) {
        if(shift.isLocked()) return 0;
        return constraintMap.get(constraintSubType).checkConstraints(this,shift);
    }


    public void breakActivityContraints(ShiftImp shift, HardMediumSoftLongScoreHolder scoreHolder, RuleContext kContext, int constraintPenality, ConstraintSubType constraintSubType) {
        log.debug("breaking Activity constraint: {}",constraintSubType);
        constraintMap.get(constraintSubType).breakLevelConstraints(scoreHolder,kContext,constraintPenality);
    }

    public void broketaskPriorityConstraints(HardMediumSoftLongScoreHolder scoreHolder, RuleContext kContext){
        switch (priority){
            case 1:scoreHolder.addSoftConstraintMatch(kContext,-1);
            break;
            case 2:scoreHolder.addMediumConstraintMatch(kContext,-1);
            break;
            case 3:scoreHolder.addSoftConstraintMatch(kContext,-1);
            break;
            default:
                break;
        }
    }

    public int skillsSatisFaction(ShiftImp shift) {
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
                .append(constraintMap)
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

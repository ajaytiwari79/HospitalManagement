package com.kairos.shiftplanning.domain.activity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kairos.dto.activity.activity.activity_tabs.CutOffIntervalUnit;
import com.kairos.enums.TimeCalaculationType;
import com.kairos.enums.TimeTypeEnum;
import com.kairos.enums.constraint.ConstraintSubType;
import com.kairos.shiftplanning.constraints.ConstraintHandler;
import com.kairos.shiftplanning.domain.shift.ShiftImp;
import com.kairos.shiftplanning.domain.skill.Skill;
import com.kairos.shiftplanning.domain.tag.Tag;
import com.kairos.shiftplanning.domain.timetype.TimeType;
import com.kairos.shiftplanning.executioner.ShiftPlanningGenerator;
import com.kairos.shiftplanningNewVersion.entity.Shift;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.kie.api.runtime.rule.RuleContext;
import org.optaplanner.core.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScoreHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@XStreamAlias("Activity")
public class Activity {

    public static final double DEFAULT_VALUE = 1d;
    private static Logger log= LoggerFactory.getLogger(Activity.class);

    private BigInteger id;
    private List<Skill> skills;
    private String name;
    @JsonIgnore
    private Map<ConstraintSubType, ConstraintHandler> constraints;
    private Set<BigInteger> validDayTypeIds;
    private TimeType timeType;
    private int order;
    private int ranking;
    private List<Long> expertises;
    private Set<Tag> tags;
    private Long teamId;
    private LocalDate cutOffStartFrom;
    private CutOffIntervalUnit cutOffIntervalUnit;
    private Integer cutOffdayValue;
    private boolean breakAllowed;
    private String methodForCalculatingTime;
    @Builder.Default
    private Double multiplyWithValue = DEFAULT_VALUE;
    private Long fixedTimeValue;
    private TimeCalaculationType fullDayCalculationType;
    private TimeCalaculationType fullWeekCalculationType;



    public Activity(BigInteger id, List<Skill> skills, String name, TimeType timeType, int order, int ranking, List<Long> expertises, Set<Tag> tags) {
        this.id = id;
        this.skills = skills;
        this.name = name;
        this.timeType=timeType;
        this.order = order;
        this.ranking = ranking;
        this.expertises = expertises;
        this.tags = tags;
        this.validDayTypeIds = new HashSet<>();
    }

    public boolean isBlankActivity(){
        return this.name.equals(ShiftPlanningGenerator.BLANK_ACTIVITY);
    }
    public boolean isTypePresence(){
        return TimeTypeEnum.PRESENCE.equals(timeType.getTimeTypeEnum());
    }
    public boolean isTypeAbsence(){
        return TimeTypeEnum.ABSENCE.equals(timeType.getTimeTypeEnum());
    }

    public int checkConstraints(ShiftImp shift, ConstraintSubType constraintSubType) {
        if(shift.isLocked() || !constraints.containsKey(constraintSubType)) return 0;
        return constraints.get(constraintSubType).checkConstraints(this,shift);
    }

    public int checkConstraints(List<ShiftImp> shifts, ConstraintSubType constraintSubType) {
        if(!constraints.containsKey(constraintSubType)) return 0;
        return constraints.get(constraintSubType).checkConstraints(shifts);
    }

    public int verifyConstraints(Shift shift, ConstraintSubType constraintSubType) {
        if(shift.isLocked() || !constraints.containsKey(constraintSubType)) return 0;
        return constraints.get(constraintSubType).verifyConstraints(this,shift);
    }

    public int verifyConstraints(List<Shift> shifts, ConstraintSubType constraintSubType) {
        if(!constraints.containsKey(constraintSubType)) return 0;
        return constraints.get(constraintSubType).verifyConstraints(shifts);
    }


    public void breakContraints( HardMediumSoftLongScoreHolder scoreHolder, RuleContext kContext, int constraintPenality, ConstraintSubType constraintSubType) {
        log.debug("breaking Activity constraint: {}",constraintSubType);
        constraints.get(constraintSubType).breakLevelConstraints(scoreHolder,kContext,constraintPenality);
    }

    public int skillsSatisFaction(ShiftImp shift) {
        List<Skill> skills = (List<Skill>) CollectionUtils.subtract(this.skills, shift.getEmployee().getSkillSet());
        int weight = skills.stream().mapToInt(s -> s.getWeight()).sum();
        return weight;
    }

    public void brokeActivitySkillConstraints(int constraintPenality,HardMediumSoftLongScoreHolder scoreHolder, RuleContext kContext) {
        scoreHolder.addSoftConstraintMatch(kContext, constraintPenality);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return id.equals(((Activity) o).getId());
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(skills)
                .append(name)
                .append(constraints)
                .toHashCode();
    }

}

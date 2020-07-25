package com.kairos.persistence.model.activity.tabs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.commons.planning_setting.ConstraintSetting;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.kairos.commons.utils.ObjectUtils.isNullOrElse;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.USE_DEFAULTS)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActivityOptaPlannerSetting {

    private int maxThisActivityPerShift;
    private int minLength;
    private boolean eligibleForMove;
    private ConstraintSetting maxThisActivityPerShiftConstraint;
    private ConstraintSetting activityCanHappenOnDaysConstraint;
    private ConstraintSetting minimumLengthOfActivityConstraint;
    private ConstraintSetting shortestDurationConstraint;
    private ConstraintSetting longestDurationConstraint;

    public ActivityOptaPlannerSetting(int maxThisActivityPerShift, int minLength, boolean eligibleForMove) {
        this.maxThisActivityPerShift = maxThisActivityPerShift;
        this.minLength = minLength;
        this.eligibleForMove = eligibleForMove;
    }

    public ConstraintSetting getMaxThisActivityPerShiftConstraint() {
        return isNullOrElse(maxThisActivityPerShiftConstraint,new ConstraintSetting());
    }

    public ConstraintSetting getActivityCanHappenOnDaysConstraint() {
        return isNullOrElse(activityCanHappenOnDaysConstraint,new ConstraintSetting());
    }

    public ConstraintSetting getMinimumLengthOfActivityConstraint() {
        return isNullOrElse(minimumLengthOfActivityConstraint,new ConstraintSetting());
    }

    public ConstraintSetting getShortestDurationConstraint() {
        return isNullOrElse(shortestDurationConstraint,new ConstraintSetting());
    }

    public ConstraintSetting getLongestDurationConstraint() {
        return isNullOrElse(longestDurationConstraint,new ConstraintSetting());
    }
}
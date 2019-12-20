package com.kairos.shiftplanning.constraints.activityConstraint;

import com.kairos.commons.planning_setting.PlanningSetting;
import com.kairos.shiftplanning.constraints.ScoreLevel;
import com.kairos.shiftplanning.domain.shift.Shift;
import com.kairos.shiftplanning.domain.tag.Tag;
import com.kairos.shiftplanning.domain.wta.ConstraintHandler;
import com.kairos.shiftplanning.domain.activity.Activity;

import java.math.BigInteger;
import java.util.List;

import static com.kairos.shiftplanning.utils.ShiftPlanningUtility.isValidForDayType;

public class ActivityRequiredTag implements ConstraintHandler {

    private Tag requiredTag;
    //private PlanningSetting planningSetting;
    private ScoreLevel level;
    private int weight;

    public ActivityRequiredTag(Tag requiredTag, ScoreLevel level, int weight) {
        this.requiredTag = requiredTag;
        this.level = level;
        this.weight = weight;
    }

    public ActivityRequiredTag() {
    }

    public Tag getRequiredTag() {
        return requiredTag;
    }

    public void setRequiredTag(Tag requiredTag) {
        this.requiredTag = requiredTag;
    }

   /*public PlanningSetting getPlanningSetting() {
        return planningSetting;
    }

    public void setPlanningSetting(PlanningSetting planningSetting) {
        this.planningSetting = planningSetting;
    }*/


    @Override
    public ScoreLevel getLevel() {
        return level;
    }

    public void setLevel(ScoreLevel level) {
        this.level = level;
    }

    @Override
    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int checkConstraints(Activity activity, Shift shift){
        List<Tag> tags = activity.getTags();
        for(Tag  tag:tags){
            if(tag.getMasterDataType().equals(this.requiredTag.getMasterDataType())){
                return 0;
            }else{
                continue;
            }
        }
        return 1;
    }

}

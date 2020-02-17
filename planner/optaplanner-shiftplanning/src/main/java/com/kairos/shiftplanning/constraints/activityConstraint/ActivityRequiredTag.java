package com.kairos.shiftplanning.constraints.activityConstraint;

import com.kairos.shiftplanning.constraints.Constraint;
import com.kairos.shiftplanning.constraints.ScoreLevel;
import com.kairos.shiftplanning.domain.activity.Activity;
import com.kairos.shiftplanning.domain.shift.Shift;
import com.kairos.shiftplanning.domain.shift.ShiftImp;
import com.kairos.shiftplanning.domain.tag.Tag;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ActivityRequiredTag implements Constraint {

    private Tag requiredTag;
    private ScoreLevel level;
    private int weight;

    public ActivityRequiredTag(Tag requiredTag, ScoreLevel level, int weight) {
        this.requiredTag = requiredTag;
        this.level = level;
        this.weight = weight;
    }

    @Override
    public int checkConstraints(Activity activity, ShiftImp shift){
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

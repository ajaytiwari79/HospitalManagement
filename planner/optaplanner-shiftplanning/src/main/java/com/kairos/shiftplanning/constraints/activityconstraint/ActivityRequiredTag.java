package com.kairos.shiftplanning.constraints.activityconstraint;

import com.kairos.enums.constraint.ScoreLevel;
import com.kairos.shiftplanning.constraints.Constraint;
import com.kairos.shiftplanning.domain.activity.Activity;
import com.kairos.shiftplanning.domain.shift.ShiftImp;
import com.kairos.shiftplanning.domain.tag.Tag;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
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
        Set<Tag> tags = activity.getTags();
        if(CollectionUtils.containsAny(tags,shift.getEmployee().getTags())){
            return 0;
        }
        return 1;
    }

    @Override
    public int checkConstraints(List<ShiftImp> shifts) {
        return 0;
    }

}

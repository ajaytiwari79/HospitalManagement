package com.kairos.shiftplanning.constraints.unitConstraint;

import com.kairos.shiftplanning.constraints.Constraint;
import com.kairos.shiftplanning.constraints.ScoreLevel;
import com.kairos.shiftplanning.domain.activity.Activity;
import com.kairos.shiftplanning.domain.shift.ShiftImp;
import com.kairos.shiftplanning.domain.tag.Tag;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Optional;

@Getter
@Setter
public class NoChangesToStaffWithCareBubble implements Constraint {
    private Long tagId;
    private ScoreLevel level;
    private int weight;

    @Override
    public int checkConstraints(Activity activity, ShiftImp shift) {
        return 0;
    }

    @Override
    public int checkConstraints(Activity activity, List<ShiftImp> shifts) {
        for (ShiftImp shift : shifts) {
            Optional<Tag> tagOptional = shift.getEmployee().getTags().stream().filter(tag -> tag.getId().equals(tagId)).findFirst();
            if(tagOptional.isPresent()){

            }
        }
        return 0;
    }
}

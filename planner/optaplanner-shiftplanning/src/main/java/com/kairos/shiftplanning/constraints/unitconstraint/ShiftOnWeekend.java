package com.kairos.shiftplanning.constraints.unitconstraint;

import com.kairos.enums.constraint.ScoreLevel;
import com.kairos.shiftplanning.constraints.ConstraintHandler;
import com.kairos.shiftplanning.domain.activity.Activity;
import com.kairos.shiftplanning.domain.shift.ShiftImp;
import com.kairos.shiftplanning.domain.unit.Unit;
import com.kairos.shiftplanningNewVersion.entity.Shift;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Set;

import static com.kairos.commons.utils.ObjectUtils.newHashSet;
@Setter
@Getter
@NoArgsConstructor
public class ShiftOnWeekend implements ConstraintHandler {

    private ScoreLevel level;
    private int weight;
    private Set<DayOfWeek> weekEndSet = newHashSet(DayOfWeek.SATURDAY,DayOfWeek.SUNDAY);

    public ShiftOnWeekend(ScoreLevel level, int weight, Set<DayOfWeek> weekEndSet) {
        this.level = level;
        this.weight = weight;
        this.weekEndSet = weekEndSet;
    }

    @Override
    public int checkConstraints(Activity activity, ShiftImp shift) {
        return 0;
    }

    @Override
    public int checkConstraints(List<ShiftImp> shifts) {
        return (int)shifts.stream().filter(shiftImp -> weekEndSet.contains(shiftImp.getStartDate().getDayOfWeek())).count();
    }

    @Override
    public int verifyConstraints(Unit unit, Shift shiftImp, List<Shift> shiftImps){return 0;};
}

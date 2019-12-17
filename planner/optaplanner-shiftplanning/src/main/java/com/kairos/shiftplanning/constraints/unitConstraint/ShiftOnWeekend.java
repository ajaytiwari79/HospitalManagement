package com.kairos.shiftplanning.constraints.unitConstraint;

import com.kairos.shiftplanning.constraints.ScoreLevel;
import com.kairos.shiftplanning.domain.shift.ShiftImp;
import com.kairos.shiftplanning.domain.wta.ConstraintHandler;
import org.kie.api.runtime.rule.RuleContext;
import org.optaplanner.core.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScoreHolder;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ShiftOnWeekend implements ConstraintHandler {

    private ScoreLevel level;
    private int weight;

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


    public  int checkConstraints(List<ShiftImp> shifts){
        List<ShiftImp> shiftImps= new ArrayList<>();
        for(ShiftImp shiftImp:shifts){
            if(shiftImp.getDate().dayOfWeek().equals(DayOfWeek.SATURDAY) || shiftImp.getDate().dayOfWeek().equals(DayOfWeek.SUNDAY)){
                shiftImps.add(shiftImp);
            }
        }
        return shiftImps.size();
    }


}

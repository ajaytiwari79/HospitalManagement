package com.kairos.shiftplanning.constraints.activityconstraint;

import com.kairos.shiftplanning.constraints.ScoreLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ContinousActivityPerShift {

    private int contActivityPerShift;
    private int weight;
    private ScoreLevel level;

    public boolean checkConstraints(){
        boolean isValid = false;
        return isValid;
    }
}

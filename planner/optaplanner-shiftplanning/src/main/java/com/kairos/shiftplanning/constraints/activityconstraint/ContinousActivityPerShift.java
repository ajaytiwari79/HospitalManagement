package com.kairos.shiftplanning.constraints.activityconstraint;

import com.kairos.enums.constraint.ScoreLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class ContinousActivityPerShift {

    private int contActivityPerShift;
    private int weight;
    private ScoreLevel level;

    public boolean checkConstraints(){
        boolean isValid = false;
        return isValid;
    }
}

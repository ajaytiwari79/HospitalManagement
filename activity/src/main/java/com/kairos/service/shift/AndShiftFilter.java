package com.kairos.service.shift;

import com.kairos.dto.activity.shift.ShiftDTO;

import java.util.List;

/**
 * Created by pradeep
 * Created at 30/6/19
 **/

public class AndShiftFilter implements ShiftFilter {

    private ShiftFilter firstCriteria;
    private ShiftFilter secondCriteria;

    public AndShiftFilter(ShiftFilter firstCriteria,ShiftFilter secondCriteria){
        this.firstCriteria = firstCriteria;
        this.secondCriteria = secondCriteria;
    }

    public ShiftFilter and(ShiftFilter shiftFilter){
        return new AndShiftFilter(this,shiftFilter);
    }

    @Override
    public <T extends ShiftDTO> List<T> meetCriteria(List<T> shiftDTOS) {
        shiftDTOS = firstCriteria.meetCriteria(shiftDTOS);
        return secondCriteria.meetCriteria(shiftDTOS);
    }
}

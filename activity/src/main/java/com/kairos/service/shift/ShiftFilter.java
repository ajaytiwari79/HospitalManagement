package com.kairos.service.shift;

import com.kairos.dto.activity.shift.ShiftDTO;

import java.util.List;

/**
 * Created by pradeep
 * Created at 30/6/19
 **/
public interface ShiftFilter {

    <T extends ShiftDTO> List<T> meetCriteria(List<T> shiftDTOS);

}

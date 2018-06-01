package com.kairos.activity.shift;

import com.kairos.response.dto.web.FunctionDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Created by oodles on 1/6/18.
 */
public class ShiftFunctionWrapper {


    List<ShiftQueryResult> shiftQueryResult;
    Map<LocalDate,FunctionDTO> appliedFunctions;


    //default constructor
    public ShiftFunctionWrapper() {
    }

    public ShiftFunctionWrapper(List<ShiftQueryResult> shiftQueryResult, Map<LocalDate, FunctionDTO> appliedFunctions) {
        this.shiftQueryResult = shiftQueryResult;
        this.appliedFunctions = appliedFunctions;
    }

    public List<ShiftQueryResult> getShiftQueryResult() {
        return shiftQueryResult;
    }

    public void setShiftQueryResult(List<ShiftQueryResult> shiftQueryResult) {
        this.shiftQueryResult = shiftQueryResult;
    }

    public Map<LocalDate, FunctionDTO> getAppliedFunctions() {
        return appliedFunctions;
    }

    public void setAppliedFunctions(Map<LocalDate, FunctionDTO> appliedFunctions) {
        this.appliedFunctions = appliedFunctions;
    }
}

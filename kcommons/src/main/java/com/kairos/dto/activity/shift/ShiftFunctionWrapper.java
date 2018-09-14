package com.kairos.dto.activity.shift;



import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Created by oodles on 1/6/18.
 */
public class ShiftFunctionWrapper {


    Map<LocalDate,List<ShiftDTO>> shifts;
    Map<LocalDate,FunctionDTO> assignedFunctionsObj;


    //default constructor
    public ShiftFunctionWrapper() {
    }

    public ShiftFunctionWrapper(Map<LocalDate,List<ShiftDTO>> shifts, Map<LocalDate, FunctionDTO> assignedFunctionsObj) {
        this.shifts = shifts;
        this.assignedFunctionsObj = assignedFunctionsObj;
    }

    public Map<LocalDate,List<ShiftDTO>> getShifts() {
        return shifts;
    }

    public void setShifts(Map<LocalDate,List<ShiftDTO>> shifts) {
        this.shifts = shifts;
    }

    public Map<LocalDate, FunctionDTO> getAssignedFunctionsObj() {
        return assignedFunctionsObj;
    }

    public void setAssignedFunctionsObj(Map<LocalDate, FunctionDTO> assignedFunctionsObj) {
        this.assignedFunctionsObj = assignedFunctionsObj;
    }
}

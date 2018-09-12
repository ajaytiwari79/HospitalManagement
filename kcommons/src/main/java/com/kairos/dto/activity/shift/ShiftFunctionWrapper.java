package com.kairos.dto.activity.shift;



import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Created by oodles on 1/6/18.
 */
public class ShiftFunctionWrapper {


    List<ShiftDTO> shiftList;
    Map<LocalDate,FunctionDTO> assignedFunctionsObj;


    //default constructor
    public ShiftFunctionWrapper() {
    }

    public ShiftFunctionWrapper(List<ShiftDTO> shiftList, Map<LocalDate, FunctionDTO> assignedFunctionsObj) {
        this.shiftList = shiftList;
        this.assignedFunctionsObj = assignedFunctionsObj;
    }

    public List<ShiftDTO> getShiftList() {
        return shiftList;
    }

    public void setShiftList(List<ShiftDTO> shiftList) {
        this.shiftList = shiftList;
    }

    public Map<LocalDate, FunctionDTO> getAssignedFunctionsObj() {
        return assignedFunctionsObj;
    }

    public void setAssignedFunctionsObj(Map<LocalDate, FunctionDTO> assignedFunctionsObj) {
        this.assignedFunctionsObj = assignedFunctionsObj;
    }
}

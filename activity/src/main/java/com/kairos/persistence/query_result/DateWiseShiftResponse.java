package com.kairos.persistence.query_result;


import com.kairos.persistence.model.activity.Shift;
import com.kairos.activity.shift.ShiftQueryResult;

import java.time.LocalDate;
import java.util.List;

public class DateWiseShiftResponse {


    private LocalDate currentDate;

    private List<Shift> shifts;
    public DateWiseShiftResponse() {
        //DC
    }

    public LocalDate getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(LocalDate currentDate) {
        this.currentDate = currentDate;
    }

    public List<Shift> getShifts() {
        return shifts;
    }

    public void setShifts(List<Shift> shifts) {
        this.shifts = shifts;
    }


}

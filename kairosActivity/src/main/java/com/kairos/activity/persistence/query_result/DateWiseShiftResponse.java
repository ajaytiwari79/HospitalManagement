package com.kairos.activity.persistence.query_result;


import com.kairos.activity.persistence.model.activity.Shift;
import com.kairos.activity.shift.ShiftQueryResult;

import java.time.LocalDate;
import java.util.List;

public class DateWiseShiftResponse {


    private LocalDate currentDate;

    private List<Shift> shifts;
    private String year;
    private String month;
    private String day;

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

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

}

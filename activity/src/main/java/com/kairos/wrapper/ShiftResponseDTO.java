package com.kairos.wrapper;


import com.kairos.persistence.model.shift.Shift;

import java.time.LocalDate;
import java.util.List;

public class ShiftResponseDTO {


    private LocalDate currentDate;
    private Long unitPositionId;
    private List<Shift> shifts;
    public ShiftResponseDTO() {
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

    public Long getUnitPositionId() {
        return unitPositionId;
    }

    public void setUnitPositionId(Long unitPositionId) {
        this.unitPositionId = unitPositionId;
    }
}

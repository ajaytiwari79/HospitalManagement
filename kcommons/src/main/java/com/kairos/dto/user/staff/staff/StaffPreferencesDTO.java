package com.kairos.dto.user.staff.staff;



import com.kairos.enums.ShiftBlockType;
import java.math.BigInteger;
import java.time.LocalDate;

public class StaffPreferencesDTO {
    private ShiftBlockType shiftBlockType;
    private BigInteger activityId;
    private LocalDate startDate;


    public StaffPreferencesDTO() {
        //Default Constructor
    }

    public ShiftBlockType getShiftBlockType() {
        return shiftBlockType;
    }

    public void setShiftBlockType(ShiftBlockType shiftBlockType) {
        this.shiftBlockType = shiftBlockType;
    }

    public BigInteger getActivityId() {
        return activityId;
    }

    public void setActivityId(BigInteger activityId) {
        this.activityId = activityId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

}

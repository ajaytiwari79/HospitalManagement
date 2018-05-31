package com.kairos.persistence.model.user.staff;



import com.kairos.persistence.model.enums.ShiftBlockType;
import java.math.BigInteger;
import java.time.LocalDate;

public class StaffPreferencesDTO {
    private ShiftBlockType shiftBlockType;
    private BigInteger activityId;
    private String startDate;


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

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

}

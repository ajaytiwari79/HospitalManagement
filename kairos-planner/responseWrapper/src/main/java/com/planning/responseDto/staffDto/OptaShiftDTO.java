package com.planning.responseDto.staffDto;


import com.planning.responseDto.commonDto.BaseDTO;
import org.joda.time.DateTime;

import java.util.Date;

public class OptaShiftDTO extends BaseDTO{

    private Long staffId;
    private Date startDateTime;
    private Date endDateTime;
    private String shiftType;

    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }

    public Date getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(Date startDateTime) {
        this.startDateTime = startDateTime;
    }

    public Date getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(Date endDateTime) {
        this.endDateTime = endDateTime;
    }


    public String getShiftType() {
        return shiftType;
    }

    public void setShiftType(String shiftType) {
        this.shiftType = shiftType;
    }


}

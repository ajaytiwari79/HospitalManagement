package com.planner.domain.vehicle;

import com.planner.domain.common.BaseEntity;
import com.planner.enums.ShiftType;

import java.util.Date;

////import org.springframework.data.cassandra.core.mapping.Table;

////@Table
public class VehicleAvailability extends BaseEntity{

    private Date fromDate;
    private Date toDate;
    private ShiftType shiftType;

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public ShiftType getShiftType() {
        return shiftType;
    }

    public void setShiftType(ShiftType shiftType) {
        this.shiftType = shiftType;
    }
}

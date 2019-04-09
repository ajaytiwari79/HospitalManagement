package com.planner.domain.shift_planning;


import com.kairos.dto.activity.shift.ShiftActivityDTO;
import com.kairos.shiftplanning.domain.staff.Employee;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

public class Shift {

    private BigInteger id;
    private Employee employee;
    private Date startDate;
    private Date endDate;
    private Long unitPositionId;
    private List<ShiftActivityDTO> activities;

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Long getUnitPositionId() {
        return unitPositionId;
    }

    public void setUnitPositionId(Long unitPositionId) {
        this.unitPositionId = unitPositionId;
    }

    public List<ShiftActivityDTO> getActivities() {
        return activities;
    }

    public void setActivities(List<ShiftActivityDTO> activities) {
        this.activities = activities;
    }
}

package com.kairos.response.dto.web.shift;

import java.math.BigInteger;
import java.time.LocalDate;

public class ShiftCreationPojoData {
    private BigInteger shiftTemplateId;
    private Long staffId;
    private Long unitPositionId;
    private LocalDate startDate;
    private LocalDate endDate;

    public ShiftCreationPojoData() {
        //Default Constructor
    }

    public BigInteger getShiftTemplateId() {
        return shiftTemplateId;
    }

    public void setShiftTemplateId(BigInteger shiftTemplateId) {
        this.shiftTemplateId = shiftTemplateId;
    }

    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }

    public Long getUnitPositionId() {
        return unitPositionId;
    }

    public void setUnitPositionId(Long unitPositionId) {
        this.unitPositionId = unitPositionId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}

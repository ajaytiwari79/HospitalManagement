package com.kairos.persistence.model.shift;

import com.kairos.dto.user.access_permission.AccessGroupRole;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Date;

@Document(collection = "shiftState")
public class ShiftState extends Shift {

    private BigInteger shiftId;
    private BigInteger shiftStatePhaseId;
    private AccessGroupRole accessGroupRole;
    private String actualPhaseState;
    private LocalDate validated;

    public ShiftState() {

    }
    public ShiftState(BigInteger shiftId,AccessGroupRole accessGroupRole,String actualPhaseState, LocalDate validated,Date startDate,Date endDate,Long unitId,Long staffId) {
        this.shiftId = shiftId;
        this.accessGroupRole = accessGroupRole;
        this.validated = validated;
        this.actualPhaseState = actualPhaseState;
        super.setStartDate(startDate) ;
        super.setEndDate(endDate);
        super.setUnitId(unitId);
        super.setStaffId(staffId);

    }
    public BigInteger getShiftId() {
        return shiftId;
    }

    public void setShiftId(BigInteger shiftId) {
        this.shiftId = shiftId;
    }

    public BigInteger getShiftStatePhaseId() {
        return shiftStatePhaseId;
    }

    public void setShiftStatePhaseId(BigInteger shiftStatePhaseId) {
        this.shiftStatePhaseId = shiftStatePhaseId;
    }

    public String getActualPhaseState() {
        return actualPhaseState;
    }

    public void setActualPhaseState(String actualPhaseState) {
        this.actualPhaseState = actualPhaseState;
    }


    public AccessGroupRole getAccessGroupRole() {
        return accessGroupRole;
    }

    public void setAccessGroupRole(AccessGroupRole accessGroupRole) {
        this.accessGroupRole = accessGroupRole;
    }

    public LocalDate getValidated() {
        return validated;
    }

    public void setValidated(LocalDate validated) {
        this.validated = validated;
    }
}

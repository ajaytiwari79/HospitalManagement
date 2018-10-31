package com.kairos.persistence.model.shift;

import com.kairos.dto.user.access_permission.AccessGroupRole;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;
import java.time.LocalDate;

@Document(collection = "shiftState")
public class ShiftState extends Shift {

    private BigInteger shiftId;
    private BigInteger shiftStatePhaseId;
    private AccessGroupRole accessGroupRole;
    private String actualPhaseState;
    private BigInteger attendanceSettingId;
    private LocalDate validated;

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

    public BigInteger getAttendanceSettingId() {
        return attendanceSettingId;
    }

    public void setAttendanceSettingId(BigInteger attendanceSettingId) {
        this.attendanceSettingId = attendanceSettingId;
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

package com.kairos.persistence.model.shift;

import com.kairos.dto.user.access_permission.AccessGroupRole;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;
import java.time.LocalDate;

@Document(collection = "shiftState")
public class ShiftState extends Shift {

    private BigInteger shiftId;
    private BigInteger shiftStatePhaseId;
    private AccessGroupRole TAndARole;
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

    public AccessGroupRole getTAndARole() {
        return TAndARole;
    }

    public void setTAndARole(AccessGroupRole TAndARole) {
        this.TAndARole = TAndARole;
    }

    public LocalDate getValidated() {
        return validated;
    }

    public void setValidated(LocalDate validated) {
        this.validated = validated;
    }
}

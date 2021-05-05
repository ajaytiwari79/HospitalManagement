package com.kairos.persistence.model.shift;

import com.kairos.dto.user.access_permission.AccessGroupRole;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;
import java.time.LocalDate;

@Getter
@Setter
@Document(collection = "shiftState")
@NoArgsConstructor
public class ShiftState extends Shift {

    private static final long serialVersionUID = 7363158576726436085L;
    private BigInteger shiftId;
    private BigInteger shiftStatePhaseId;

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

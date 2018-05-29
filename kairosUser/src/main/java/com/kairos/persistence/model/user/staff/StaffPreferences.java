package com.kairos.persistence.model.user.staff;

import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.enums.ShiftBlockReason;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class StaffPreferences extends UserBaseEntity {
    private ShiftBlockReason shiftBlockReason;

    public StaffPreferences() {
        //Default Constructor
    }

    public ShiftBlockReason getShiftBlockReason() {
        return shiftBlockReason;
    }

    public void setShiftBlockReason(ShiftBlockReason shiftBlockReason) {
        this.shiftBlockReason = shiftBlockReason;
    }
}

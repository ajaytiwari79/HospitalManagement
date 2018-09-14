package com.kairos.dto.user.employment;

public class UnitPositionIdDTO {

    Long oldUnitPositionID;
    Long newUnitPositionID;

    public UnitPositionIdDTO() {

    }
    public UnitPositionIdDTO(Long oldUnitPositionID,Long newUnitPositionID ) {

        this.oldUnitPositionID = oldUnitPositionID;
        this.newUnitPositionID = newUnitPositionID;
    }

    public Long getOldUnitPositionID() {
        return oldUnitPositionID;
    }

    public void setOldUnitPositionID(Long oldUnitPositionID) {
        this.oldUnitPositionID = oldUnitPositionID;
    }

    public Long getNewUnitPositionID() {
        return newUnitPositionID;
    }

    public void setNewUnitPositionID(Long newUnitPositionID) {
        this.newUnitPositionID = newUnitPositionID;
    }




}

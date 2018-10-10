package com.kairos.dto.user.employment;

public class UnitPositionIdDTO {

    private Long oldUnitPositionID;
    private Long newUnitPositionID;
    private Long positionLineId;
    public UnitPositionIdDTO() {

    }
    public UnitPositionIdDTO(Long oldUnitPositionID,Long newUnitPositionID ) {

        this.oldUnitPositionID = oldUnitPositionID;
        this.newUnitPositionID = newUnitPositionID;
    }

    public UnitPositionIdDTO(Long oldUnitPositionID, Long newUnitPositionID, Long positionLineId) {
        this.oldUnitPositionID = oldUnitPositionID;
        this.newUnitPositionID = newUnitPositionID;
        this.positionLineId = positionLineId;
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

    public Long getPositionLineId() {
        return positionLineId;
    }

    public void setPositionLineId(Long positionLineId) {
        this.positionLineId = positionLineId;
    }
}

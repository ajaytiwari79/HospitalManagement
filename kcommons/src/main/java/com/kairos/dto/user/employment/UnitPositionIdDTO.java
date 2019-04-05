package com.kairos.dto.user.employment;

public class UnitPositionIdDTO {

    private Long oldUnitPositionID;
    private Long newUnitPositionID;
    private Long employmentLineId;
    public UnitPositionIdDTO() {

    }
    public UnitPositionIdDTO(Long oldUnitPositionID,Long newUnitPositionID ) {

        this.oldUnitPositionID = oldUnitPositionID;
        this.newUnitPositionID = newUnitPositionID;
    }

    public UnitPositionIdDTO(Long oldUnitPositionID, Long newUnitPositionID, Long employmentLineId) {
        this.oldUnitPositionID = oldUnitPositionID;
        this.newUnitPositionID = newUnitPositionID;
        this.employmentLineId = employmentLineId;
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

    public Long getEmploymentLineId() {
        return employmentLineId;
    }

    public void setEmploymentLineId(Long employmentLineId) {
        this.employmentLineId = employmentLineId;
    }
}

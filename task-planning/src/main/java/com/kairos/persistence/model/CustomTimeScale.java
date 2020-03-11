package com.kairos.persistence.model;


import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by oodles on 17/4/17.
 * This domain will keep track of time scale representation
 * Planner may add columns for specific citizen and unit
 */
@Document(collection = "custom_time_scale")
public class CustomTimeScale extends MongoBaseEntity {

    private long unitId;
    private long citizenId;
    private long staffId; //Planner's Id, who is adding/removing columns
    private int numberOfAdditionalScales; //Columns to represent on gantt view.

    public boolean isShowExceptionDeleteConfirmation() {
        return showExceptionDeleteConfirmation;
    }

    public void setShowExceptionDeleteConfirmation(boolean showExceptionDeleteConfirmation) {
        this.showExceptionDeleteConfirmation = showExceptionDeleteConfirmation;
    }

    private boolean showExceptionDeleteConfirmation = true;

    public CustomTimeScale(long staffId, long citizenId, long unitId, int numberOfAdditionalScales) {

        this.staffId = staffId;
        this.citizenId = citizenId;
        this.unitId = unitId;
        this.numberOfAdditionalScales = numberOfAdditionalScales;
    }


    public long getUnitId() {
        return unitId;
    }

    public void setUnitId(long unitId) {
        this.unitId = unitId;
    }

    public long getCitizenId() {
        return citizenId;
    }

    public void setCitizenId(long citizenId) {
        this.citizenId = citizenId;
    }

    public long getStaffId() {
        return staffId;
    }

    public void setStaffId(long staffId) {
        this.staffId = staffId;
    }

    public int getNumberOfAdditionalScales() {
        return numberOfAdditionalScales;
    }

    public void setNumberOfAdditionalScales(int numberOfAdditionalScales) {
        this.numberOfAdditionalScales = numberOfAdditionalScales;
    }

}

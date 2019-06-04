package com.kairos.persistence.model.task;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.common.MongoBaseEntity;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;

/**
 * Created by oodles on 31/1/17.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Document
@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskReport extends MongoBaseEntity {
    // Related Details
    private  Long unitId;
    private String unitName;
    private BigInteger taskId;
    private  Long staffId;
    private String staffName;
    private String previousActivity;
    private String currentActivity;
    private String updateDate;
    private  String previousFrom;
    private String previousTo;
    private String previousDuration;
    private String currentFrom;
    private String currentTo;
    private String currentDuration;
    private String draft;


    public TaskReport(String staffName , String previousActivity, String currentActivity , String updateDate, String day, String previousFrom, String previousTo, String previousDuration, String currentFrom, String currentTo, String currentDuration, String draft) {
        this.staffName = staffName;
        this.previousActivity = previousActivity;
        this.currentActivity = currentActivity;
        this.updateDate = updateDate;
        this.previousFrom = previousFrom;
        this.previousTo = previousTo;
        this.previousDuration = previousDuration;

        this.currentFrom = currentFrom;
        this.currentTo =currentTo;
        this.currentDuration = currentDuration;

        this.draft = draft;

    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public BigInteger getTaskId() {
        return taskId;
    }

    public void setTaskId(BigInteger taskId) {
        this.taskId = taskId;
    }

    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public String getPreviousFrom() {
        return previousFrom;
    }

    public void setPreviousFrom(String previousFrom) {
        this.previousFrom = previousFrom;
    }

    public String getPreviousTo() {
        return previousTo;
    }

    public void setPreviousTo(String previousTo) {
        this.previousTo = previousTo;
    }

    public String getCurrentFrom() {
        return currentFrom;
    }

    public void setCurrentFrom(String currentFrom) {
        this.currentFrom = currentFrom;
    }

    public String getCurrentTo() {
        return currentTo;
    }

    public void setCurrentTo(String currentTo) {
        this.currentTo = currentTo;
    }

    public String getPreviousActivity() {
        return previousActivity;
    }

    public void setPreviousActivity(String previousActivity) {
        this.previousActivity = previousActivity;
    }

    public String getCurrentActivity() {
        return currentActivity;
    }

    public void setCurrentActivity(String currentActivity) {
        this.currentActivity = currentActivity;
    }

    public String getPreviousDuration() {
        return previousDuration;
    }

    public void setPreviousDuration(String previousDuration) {
        this.previousDuration = previousDuration;
    }

    public String getCurrentDuration() {
        return currentDuration;
    }

    public void setCurrentDuration(String currentDuration) {
        this.currentDuration = currentDuration;
    }

    public String getDraft() {
        return draft;
    }

    public void setDraft(String draft) {
        this.draft = draft;
    }


    public TaskReport() {
        //Not in use
    }
}

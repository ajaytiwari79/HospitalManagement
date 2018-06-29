package com.kairos.activity.task_type;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigInteger;

/**
 * @author pradeep
 * @date - 11/6/18
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskTypeSettingDTO {

    private BigInteger id;
    private Long staffId;
    private BigInteger taskTypeId;
    private int efficiency;
    private String taskTypeName;
    private Long clientId;
    private int duration;



    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public String getTaskTypeName() {
        return taskTypeName;
    }

    public void setTaskTypeName(String taskTypeName) {
        this.taskTypeName = taskTypeName;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }

    public BigInteger getTaskTypeId() {
        return taskTypeId;
    }

    public void setTaskTypeId(BigInteger taskTypeId) {
        this.taskTypeId = taskTypeId;
    }

    public int getEfficiency() {
        return efficiency;
    }

    public void setEfficiency(int efficiency) {
        this.efficiency = efficiency;
    }
}

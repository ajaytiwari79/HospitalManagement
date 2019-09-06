package com.kairos.persistence.model.task_type;

import com.kairos.persistence.model.common.MongoBaseEntity;

import java.math.BigInteger;

/**
 * @author pradeep
 * @date - 11/6/18
 */

/*
this Task TypeSetting common for Staff and Client
if staffId Exist so this is belongs to staff and
if clientId so belongs to client, efficiency field only for staff and
duration field only for client
*/

public class TaskTypeSetting extends MongoBaseEntity{

    private Long staffId;
    private BigInteger taskTypeId;
    private int efficiency;
    private Long clientId;
    private int duration;


    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public TaskTypeSetting() {
    }

    public TaskTypeSetting(Long staffId, BigInteger taskTypeId, int efficiency) {
        this.staffId = staffId;
        this.taskTypeId = taskTypeId;
        this.efficiency = efficiency;
    }

    public TaskTypeSetting(BigInteger taskTypeId, Long clientId) {
        this.taskTypeId = taskTypeId;
        this.clientId = clientId;
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

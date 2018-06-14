package com.kairos.activity.response.dto.task;

import com.kairos.activity.response.dto.TaskTypeDTO;
import com.kairos.client.dto.TaskAddress;

import java.math.BigInteger;

/**
 * @author pradeep
 * @date - 14/6/18
 */

public class VRPTaskDTO {
    private TaskAddress address;
    //Vrp settings
    private Integer installationNo;
    private Long citizenId;
    private String skill;
    private BigInteger taskTypeId;
    private String citizenName;
    private TaskTypeDTO taskType;


    public TaskTypeDTO getTaskType() {
        return taskType;
    }

    public void setTaskType(TaskTypeDTO taskType) {
        this.taskType = taskType;
    }

    public String getCitizenName() {
        return citizenName;
    }

    public void setCitizenName(String citizenName) {
        this.citizenName = citizenName;
    }

    public BigInteger getTaskTypeId() {
        return taskTypeId;
    }

    public void setTaskTypeId(BigInteger taskTypeId) {
        this.taskTypeId = taskTypeId;
    }

    public String getSkill() {
        return skill;
    }

    public void setSkill(String skill) {
        this.skill = skill;
    }

    public Long getCitizenId() {
        return citizenId;
    }

    public void setCitizenId(Long citizenId) {
        this.citizenId = citizenId;
    }

    public TaskAddress getAddress() {
        return address;
    }

    public void setAddress(TaskAddress address) {
        this.address = address;
    }

    public Integer getInstallationNo() {
        return installationNo;
    }

    public void setInstallationNo(Integer installationNo) {
        this.installationNo = installationNo;
    }
}

package com.kairos.dto;

import com.kairos.enums.scheduler.JobSubType;
import com.kairos.enums.scheduler.JobType;
import com.kairos.enums.scheduler.OperationType;

import java.math.BigInteger;

public class KairosSchedulerExecutorDTO {

    private JobType jobType;
    private JobSubType jobSubType;
    private BigInteger entityId;
    private OperationType operationType;
    private IntegrationConfigurationDTO integrationConfigurationDTO;

    public KairosSchedulerExecutorDTO(JobType jobType,JobSubType jobSubType, BigInteger entityId, OperationType operationType,
                                      IntegrationConfigurationDTO integrationConfigurationDTO) {

    }

    public JobType getJobType() {
        return jobType;
    }

    public void setJobType(JobType jobType) {
        this.jobType = jobType;
    }

    public JobSubType getJobSubType() {
        return jobSubType;
    }

    public void setJobSubType(JobSubType jobSubType) {
        this.jobSubType = jobSubType;
    }

    public BigInteger getEntityId() {
        return entityId;
    }

    public void setEntityId(BigInteger entityId) {
        this.entityId = entityId;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    public void setOperationType(OperationType operationType) {
        this.operationType = operationType;
    }




}

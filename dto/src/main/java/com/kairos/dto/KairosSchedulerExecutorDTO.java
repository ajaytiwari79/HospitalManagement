package com.kairos.dto;

import com.kairos.enums.scheduler.JobSubType;
import com.kairos.enums.scheduler.JobType;
import com.kairos.enums.scheduler.OperationType;

import java.math.BigInteger;
import java.time.LocalDateTime;

public class KairosSchedulerExecutorDTO {

    private BigInteger id;
    private Long unitId;
    private JobType jobType;
    private JobSubType jobSubType;
    private BigInteger entityId;
    private String filterId;
    private IntegrationConfigurationDTO integrationConfigurationDTO;
    private Long oneTimeTriggerDateMillis;

    public KairosSchedulerExecutorDTO() {

    }
    public KairosSchedulerExecutorDTO(BigInteger id,Long unitId,JobType jobType,JobSubType jobSubType, BigInteger entityId,
                                      IntegrationConfigurationDTO integrationConfigurationDTO,Long oneTimeTriggerDateMillis ) {

        this.id = id;
        this.unitId = unitId;
        this.jobType = jobType;
        this.jobSubType = jobSubType;
        this.entityId = entityId;
        this.integrationConfigurationDTO = integrationConfigurationDTO;
        this.oneTimeTriggerDateMillis = oneTimeTriggerDateMillis;

    }


    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }
    public String getFilterId() {
        return filterId;
    }

    public void setFilterId(String filterId) {
        this.filterId = filterId;
    }
    public IntegrationConfigurationDTO getIntegrationConfigurationDTO() {
        return integrationConfigurationDTO;
    }

    public void setIntegrationConfigurationDTO(IntegrationConfigurationDTO integrationConfigurationDTO) {
        this.integrationConfigurationDTO = integrationConfigurationDTO;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
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

    public Long getOneTimeTriggerDateMillis() {
        return oneTimeTriggerDateMillis;
    }

    public void setOneTimeTriggerDateMillis(Long oneTimeTriggerDateMillis) {
        this.oneTimeTriggerDateMillis = oneTimeTriggerDateMillis;
    }





}

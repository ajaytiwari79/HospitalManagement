package com.kairos.dto.scheduler.queue;

import com.kairos.dto.scheduler.IntegrationSettingsDTO;
import com.kairos.enums.scheduler.JobSubType;
import com.kairos.enums.scheduler.JobType;

import java.math.BigInteger;

public class KairosSchedulerExecutorDTO {

    private BigInteger id;
    private Long unitId;
    private JobType jobType;
    private JobSubType jobSubType;
    private BigInteger entityId;
    private String filterId;
    private IntegrationSettingsDTO integrationSettingsDTO;
    private Long oneTimeTriggerDateMillis;

    public KairosSchedulerExecutorDTO() {

    }
    public KairosSchedulerExecutorDTO(BigInteger id, Long unitId, JobType jobType, JobSubType jobSubType, BigInteger entityId,
                                      IntegrationSettingsDTO integrationSettingsDTO, Long oneTimeTriggerDateMillis ) {

        this.id = id;
        this.unitId = unitId;
        this.jobType = jobType;
        this.jobSubType = jobSubType;
        this.entityId = entityId;
        this.integrationSettingsDTO = integrationSettingsDTO;
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
    public IntegrationSettingsDTO getIntegrationSettingsDTO() {
        return integrationSettingsDTO;
    }

    public void setIntegrationSettingsDTO(IntegrationSettingsDTO integrationSettingsDTO) {
        this.integrationSettingsDTO = integrationSettingsDTO;
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

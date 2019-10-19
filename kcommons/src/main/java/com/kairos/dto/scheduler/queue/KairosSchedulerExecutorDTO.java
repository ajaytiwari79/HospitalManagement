package com.kairos.dto.scheduler.queue;

import com.kairos.dto.scheduler.IntegrationSettingsDTO;
import com.kairos.enums.scheduler.JobSubType;
import com.kairos.enums.scheduler.JobType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
@Getter
@Setter
@NoArgsConstructor
public class KairosSchedulerExecutorDTO {

    private BigInteger id;
    private Long unitId;
    private JobType jobType;
    private JobSubType jobSubType;
    private BigInteger entityId;
    private String filterId;
    private IntegrationSettingsDTO integrationSettingsDTO;
    private Long oneTimeTriggerDateMillis;

    public KairosSchedulerExecutorDTO(BigInteger id, Long unitId, JobType jobType, JobSubType jobSubType, BigInteger entityId,
                                      IntegrationSettingsDTO integrationSettingsDTO, Long oneTimeTriggerDateMillis,String filterId ) {

        this.id = id;
        this.unitId = unitId;
        this.jobType = jobType;
        this.jobSubType = jobSubType;
        this.entityId = entityId;
        this.integrationSettingsDTO = integrationSettingsDTO;
        this.oneTimeTriggerDateMillis = oneTimeTriggerDateMillis;
        this.filterId=filterId;

    }
}

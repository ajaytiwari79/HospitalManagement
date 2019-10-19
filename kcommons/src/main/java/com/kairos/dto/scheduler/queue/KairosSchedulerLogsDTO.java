package com.kairos.dto.scheduler.queue;

import com.kairos.enums.scheduler.JobSubType;
import com.kairos.enums.scheduler.Result;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
@Getter
@Setter
@NoArgsConstructor
public class KairosSchedulerLogsDTO {
    private Result result;
    private String log;
    private BigInteger schedulerPanelId;
    private Long unitId;
    private Long startedDate;
    private Long stoppedDate;
    private JobSubType jobSubType;

    public KairosSchedulerLogsDTO(Result result, String log, BigInteger schedulerPanelId, Long unitId, Long startedDate, Long stoppedDate, JobSubType jobSubType) {
        this.result = result;
        this.log = log;
        this.schedulerPanelId = schedulerPanelId;
        this.unitId = unitId;
        this.startedDate = startedDate;
        this.stoppedDate = stoppedDate;
        this.jobSubType = jobSubType;
    }
    public KairosSchedulerLogsDTO(Result result, String log, BigInteger schedulerPanelId, Long unitId, JobSubType jobSubType) {
        this.result = result;
        this.log = log;
        this.schedulerPanelId = schedulerPanelId;
        this.unitId = unitId;
        this.jobSubType = jobSubType;
    }

}

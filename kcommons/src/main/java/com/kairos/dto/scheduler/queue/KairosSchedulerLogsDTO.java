package com.kairos.dto.scheduler.queue;

import com.kairos.enums.scheduler.JobSubType;
import com.kairos.enums.scheduler.Result;
import java.math.BigInteger;

public class KairosSchedulerLogsDTO {


    private Result result;
    private String log;
    private BigInteger schedulerPanelId;
    private Long unitId;



    private Long startedDate;
    private Long stoppedDate;
    private JobSubType jobSubType;

    public KairosSchedulerLogsDTO() {

    }
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

    public Long getStartedDate() {
        return startedDate;
    }

    public void setStartedDate(Long startedDate) {
        this.startedDate = startedDate;
    }

    public Long getStoppedDate() {
        return stoppedDate;
    }

    public void setStoppedDate(Long stoppedDate) {
        this.stoppedDate = stoppedDate;
    }
    public BigInteger getSchedulerPanelId() {
        return schedulerPanelId;
    }

    public void setSchedulerPanelId(BigInteger schedulerPanelId) {
        this.schedulerPanelId = schedulerPanelId;
    }

    public JobSubType getJobSubType() {
        return jobSubType;
    }

    public void setJobSubType(JobSubType jobSubType) {
        this.jobSubType = jobSubType;
    }


    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }


    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

}

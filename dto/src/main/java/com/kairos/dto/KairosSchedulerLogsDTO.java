package com.kairos.dto;

import com.kairos.enums.scheduler.JobSubType;
import com.kairos.enums.scheduler.Result;
import java.math.BigInteger;
import java.time.LocalDateTime;

public class KairosSchedulerLogsDTO {


    private Result result;
    private String log;
    private BigInteger schedulerPanelId;
    private Long unitId;
    private LocalDateTime started;
    private LocalDateTime stopped;
    private JobSubType jobSubType;

    public KairosSchedulerLogsDTO() {

    }
    public KairosSchedulerLogsDTO(Result result,String log,BigInteger schedulerPanelId,Long unitId,LocalDateTime started, LocalDateTime stopped,JobSubType jobSubType) {
        this.result = result;
        this.log = log;
        this.schedulerPanelId = schedulerPanelId;
        this.unitId = unitId;
        this.started = started;
        this.stopped = stopped;
        this.jobSubType = jobSubType;
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

    public LocalDateTime getStarted() {
        return started;
    }

    public void setStarted(LocalDateTime started) {
        this.started = started;
    }

    public LocalDateTime getStopped() {
        return stopped;
    }

    public void setStopped(LocalDateTime stopped) {
        this.stopped = stopped;
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

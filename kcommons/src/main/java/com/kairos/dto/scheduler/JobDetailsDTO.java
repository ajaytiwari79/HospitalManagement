package com.kairos.dto.scheduler;

import com.kairos.enums.scheduler.JobSubType;
import com.kairos.enums.scheduler.Result;

import java.math.BigInteger;
import java.time.LocalDateTime;

public class JobDetailsDTO {
    private BigInteger schedulerPanelId;
    private LocalDateTime started;
    private LocalDateTime stopped;
    private Result result;
    private String processName;
    private String name;
    private String log;
    private Long unitId;
    private JobSubType jobSubType;

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

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

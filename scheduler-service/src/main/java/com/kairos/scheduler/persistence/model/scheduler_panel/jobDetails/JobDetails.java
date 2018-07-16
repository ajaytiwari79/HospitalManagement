package com.kairos.scheduler.persistence.model.scheduler_panel.jobDetails;
import com.kairos.enums.scheduler.Result;
import com.kairos.scheduler.persistence.model.common.MongoBaseEntity;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Date;
import java.math.BigInteger;

/**
 * Created by Jasgeet on 17/1/17.
 */
@Document
public class JobDetails extends MongoBaseEntity {


    private BigInteger schedulerPanelId;
    private LocalDateTime started;
    private LocalDateTime stopped;
    private Result result;
    private String processName;
    private String name;
    private String log;

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

    public BigInteger getSchedulerPanelId() {
        return schedulerPanelId;
    }

    public void setSchedulerPanelId(BigInteger schedulerPanelId) {
        this.schedulerPanelId = schedulerPanelId;
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

    @Override
    public String toString() {
        return "JobDetails{" +
                "schedulerPanelId=" + schedulerPanelId +
                ", started=" + started +
                ", stopped=" + stopped +
                ", result='" + result + '\'' +
                ", log='" + log + '\'' +
                '}';
    }
}

package com.kairos.persistence.model.user.control_panel.jobDetails;
import com.kairos.enums.scheduler.Result;
import com.kairos.persistence.model.common.UserBaseEntity;

import org.neo4j.ogm.annotation.NodeEntity;

import java.util.Date;

/**
 * Created by Jasgeet on 17/1/17.
 */
@NodeEntity
public class JobDetails extends UserBaseEntity {

    private Long controlPanelId;
    private Date started;
    private Date stopped;
    private Result result;
    private String processName;
    private String name;
    private String log;

    public Result getResult() {
        return result;
    }
    public void setResult(Result result) {
        this.result = result;
    }
    public Long getControlPanelId() {
        return controlPanelId;
    }

    public void setControlPanelId(Long controlPanelId) {
        this.controlPanelId = controlPanelId;
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

    public Date getStarted() {
        return started;
    }

    public void setStarted(Date started) {
        this.started = started;
    }

    public Date getStopped() {
        return stopped;
    }

    public void setStopped(Date stopped) {
        this.stopped = stopped;
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
                "controlPanelId=" + controlPanelId +
                ", started=" + started +
                ", stopped=" + stopped +
                ", result='" + result + '\'' +
                ", log='" + log + '\'' +
                '}';
    }
}

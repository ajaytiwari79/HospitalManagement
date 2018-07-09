package com.kairos.dto;

import com.kairos.enums.IntegrationOperation;
import com.kairos.enums.scheduler.JobSubType;
import com.kairos.enums.scheduler.JobType;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

public class QueueDTO {
    private String name;
    private String processType;
    private boolean active;
    private String cronExpression;
    private String interval; // to show the content selected e.g. Monday,Tuesday,Wednesday,Thursday,Friday. Every 60 minute
    private Date lastRunTime;
    private Date nextRunTime;
    private boolean isAlarmed;
    private Integer startMinute;
    private String repeat;
    private List<String> days;
    private String runOnce;
    private List<String> selectedHours;
    private Date startDate;
    private Date endDate;
    private Integer weeks;
    private Long unitId;
    private String filterId;
    private JobType jobType;
    private JobSubType jobSubType;
    private IntegrationOperation integrationOperation;



    public IntegrationOperation getIntegrationOperation() {
        return integrationOperation;
    }

    public void setIntegrationOperation(IntegrationOperation integrationOperation) {
        this.integrationOperation = integrationOperation;
    }


    public BigInteger getEntityId() {
        return entityId;
    }

    public void setEntityId(BigInteger entityId) {
        this.entityId = entityId;
    }

    private BigInteger entityId;




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



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProcessType() {
        return processType;
    }

    public void setProcessType(String processType) {
        this.processType = processType;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public String getInterval() {
        return interval;
    }

    public void setInterval(String interval) {
        this.interval = interval;
    }

    public Date getLastRunTime() {
        return lastRunTime;
    }

    public void setLastRunTime(Date lastRunTime) {
        this.lastRunTime = lastRunTime;
    }

    public Date getNextRunTime() {
        return nextRunTime;
    }

    public void setNextRunTime(Date nextRunTime) {
        this.nextRunTime = nextRunTime;
    }

    public boolean isAlarmed() {
        return isAlarmed;
    }

    public void setAlarmed(boolean alarmed) {
        isAlarmed = alarmed;
    }

    public Integer getStartMinute() {
        return startMinute;
    }

    public void setStartMinute(Integer startMinute) {
        this.startMinute = startMinute;
    }

    public String getRepeat() {
        return repeat;
    }

    public void setRepeat(String repeat) {
        this.repeat = repeat;
    }

    public List<String> getDays() {
        return days;
    }

    public void setDays(List<String> days) {
        this.days = days;
    }

    public String getRunOnce() {
        return runOnce;
    }

    public void setRunOnce(String runOnce) {
        this.runOnce = runOnce;
    }

    public List<String> getSelectedHours() {
        return selectedHours;
    }

    public void setSelectedHours(List<String> selectedHours) {
        this.selectedHours = selectedHours;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Integer getWeeks() {
        return weeks;
    }

    public void setWeeks(Integer weeks) {
        this.weeks = weeks;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public String getFilterId() {
        return filterId;
    }

    public void setFilterId(String filterId) {
        this.filterId = filterId;
    }



}

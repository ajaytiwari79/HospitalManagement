package com.kairos.scheduler.persistence.model.scheduler_panel;

import com.kairos.enums.scheduler.JobSubType;
import com.kairos.enums.scheduler.JobType;
import com.kairos.scheduler.persistence.model.common.MongoBaseEntity;

import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

/**
 * Created by Jasgeet on 29/12/16.
 */

@Document
public class SchedulerPanel extends MongoBaseEntity {


    private String name;
    private String processType;
    private boolean active;
    private String cronExpression;
    private String interval; // to show the content selected e.g. Monday,Tuesday,Wednesday,Thursday,Friday. Every 60 minute
    private Date lastRunTime;
    private Date nextRunTime;
    private boolean isAlarmed;
    private Integer startMinute;
    private Integer repeat;
    private List<DayOfWeek> days;
    private LocalTime runOnce;
    private List<String> selectedHours;
    private Date startDate;
    private Date endDate;
    private Integer weeks;
    private Long unitId;
    private String filterId;
    private BigInteger integrationConfigurationId;
    private JobType jobType;
    private JobSubType jobSubType;
    private boolean oneTimeTrigger;
    private LocalDateTime oneTimeTriggerDate;
    private BigInteger entityId;



    public Integer getRepeat() {
        return repeat;
    }

    public void setRepeat(Integer repeat) {
        this.repeat = repeat;
    }
    public BigInteger getEntityId() {
        return entityId;
    }

    public void setEntityId(BigInteger entityId) {
        this.entityId = entityId;
    }

    public boolean isOneTimeTrigger() {
        return oneTimeTrigger;
    }

    public void setOneTimeTrigger(boolean oneTimeTrigger) {
        this.oneTimeTrigger = oneTimeTrigger;
    }

    public LocalDateTime getOneTimeTriggerDate() {
        return oneTimeTriggerDate;
    }

    public void setOneTimeTriggerDate(LocalDateTime oneTimeTriggerDate) {
        this.oneTimeTriggerDate = oneTimeTriggerDate;
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


    public BigInteger getIntegrationConfigurationId() {
        return integrationConfigurationId;
    }

    public void setIntegrationConfigurationId(BigInteger integrationConfigurationId) {
        this.integrationConfigurationId = integrationConfigurationId;
    }


    public List<String> getSelectedHours() {
        return selectedHours;
    }

    public void setSelectedHours(List<String> selectedHours) {
        this.selectedHours = selectedHours;
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

    public String getInterval() {
        return interval;
    }

    public void setInterval(String interval) {
        this.interval = interval;
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

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public Integer getWeeks() {
        return weeks;
    }

    public void setWeeks(Integer weeks) {
        this.weeks = weeks;
    }

    public String getFilterId() {
        return filterId;
    }

    public void setFilterId(String filterId) {
        this.filterId = filterId;
    }
    public List<DayOfWeek> getDays() {
        return days;
    }

    public void setDays(List<DayOfWeek> days) {
        this.days = days;
    }

    public LocalTime getRunOnce() {
        return runOnce;
    }

    public void setRunOnce(LocalTime runOnce) {
        this.runOnce = runOnce;
    }
}

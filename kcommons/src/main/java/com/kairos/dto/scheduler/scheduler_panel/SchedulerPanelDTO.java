package com.kairos.dto.scheduler.scheduler_panel;

import com.kairos.dto.activity.activity.activity_tabs.communication_tab.ActivityReminderSettings;
import com.kairos.enums.IntegrationOperation;
import com.kairos.enums.scheduler.JobSubType;
import com.kairos.enums.scheduler.JobType;

import java.math.BigInteger;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

public class SchedulerPanelDTO {

    private BigInteger id;
    private String name;
    private String processType;
    private boolean active;
    private String cronExpression;
    private String interval; // to show the content selected e.g. Monday,Tuesday,Wednesday,Thursday,Friday. Every 60 minute
    private Date lastRunTime;
    private Date nextRunTime;
    private Integer startMinute;
    private Integer repeat;
    private List<DayOfWeek> days;
    private LocalTime runOnce;
    private List<String> selectedHours;
    private Date startDate;
    private Date endDate;
    private Long unitId;
    private String filterId;
    private BigInteger integrationConfigurationId;
    private JobType jobType;
    private JobSubType jobSubType;
    private boolean oneTimeTrigger;
    private LocalDateTime oneTimeTriggerDate;
    private BigInteger entityId;



    public SchedulerPanelDTO() {

    }

    public SchedulerPanelDTO(String name, boolean active, Integer startMinute, Integer repeat, List<DayOfWeek> days, LocalTime runOnce, List<String> selectedHours, Long unitId, BigInteger integrationConfigurationId, JobType jobType, JobSubType jobSubType, boolean oneTimeTrigger, LocalDateTime oneTimeTriggerDate, BigInteger entityId) {
        this.name = name;
        this.active = active;
        this.startMinute = startMinute;
        this.repeat = repeat;
        this.days = days;
        this.runOnce = runOnce;
        this.selectedHours = selectedHours;
        this.unitId = unitId;
        this.integrationConfigurationId = integrationConfigurationId;
        this.jobType = jobType;
        this.jobSubType = jobSubType;
        this.oneTimeTrigger = oneTimeTrigger;
        this.oneTimeTriggerDate = oneTimeTriggerDate;
        this.entityId = entityId;
    }

    public SchedulerPanelDTO(JobType jobType,JobSubType jobSubType, boolean oneTimeTrigger, LocalDateTime oneTimeTriggerDate, BigInteger entityId) {
       this.jobType=jobType;
        this.jobSubType = jobSubType;
        this.oneTimeTrigger = oneTimeTrigger;
        this.oneTimeTriggerDate = oneTimeTriggerDate;
        this.entityId = entityId;
    }
    public SchedulerPanelDTO(Long unitId, JobType jobType, JobSubType jobSubType, BigInteger entityId,
                             LocalDateTime oneTimeTriggerDate, boolean oneTimeTrigger,String filterId) {

        this.unitId = unitId;
        this.jobType = jobType;
        this.jobSubType = jobSubType;
        this.oneTimeTriggerDate = oneTimeTriggerDate;
        this.entityId = entityId;
        this.oneTimeTrigger = oneTimeTrigger;
        this.filterId=filterId;

    }
    public SchedulerPanelDTO(BigInteger id, LocalDateTime oneTimeTriggerDate) {
        this.id = id;
        this.oneTimeTriggerDate = oneTimeTriggerDate;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
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



    /* public String getRunOnce() {
        return runOnce;
    }

    public void setRunOnce(String runOnce) {
        this.runOnce = runOnce;
    }*/

   /* public List<String> getDays() {
        return days;
    }

    public void setDays(List<String> days) {
        this.days = days;
    }
*/


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

    public Integer getRepeat() {
        return repeat;
    }

    public void setRepeat(Integer repeat) {
        this.repeat = repeat;
    }


    public LocalTime getRunOnce() {
        return runOnce;
    }

    public void setRunOnce(LocalTime runOnce) {
        this.runOnce = runOnce;
    }

    public List<String> getSelectedHours() {
        return selectedHours;
    }

    public void setSelectedHours(List<String> selectedHours) {
        this.selectedHours = selectedHours;
    }


}

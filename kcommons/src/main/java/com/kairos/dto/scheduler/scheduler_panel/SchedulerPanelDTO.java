package com.kairos.dto.scheduler.scheduler_panel;

import com.kairos.commons.utils.DateUtils;
import com.kairos.enums.scheduler.JobFrequencyType;
import com.kairos.enums.scheduler.JobSubType;
import com.kairos.enums.scheduler.JobType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
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
    private LocalDateTime monthlyJobTriggerDate;
    private BigInteger entityId;
    private String timezone;
    private Long oneTimeTriggerDateMillis;
    private JobFrequencyType jobFrequencyType;

    public SchedulerPanelDTO(JobType jobType, JobSubType jobSubType, JobFrequencyType jobFrequencyType, LocalDateTime monthlyJobTriggerDate, boolean oneTimeTrigger ) {
        this.monthlyJobTriggerDate = monthlyJobTriggerDate;
        this.jobType = jobType;
        this.jobSubType = jobSubType;
        this.jobFrequencyType = jobFrequencyType;
        this.oneTimeTrigger=oneTimeTrigger;
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

    public SchedulerPanelDTO(List<DayOfWeek> days, LocalTime runOnce, JobType jobType, JobSubType jobSubType, String timezone) {
        this.days = days;
        this.runOnce = runOnce;
        this.jobType = jobType;
        this.jobSubType = jobSubType;
        this.timezone = timezone;
    }

    public SchedulerPanelDTO(BigInteger entityId,List<DayOfWeek> days, LocalTime runOnce, JobType jobType, JobSubType jobSubType, String timezone) {
        this.days = days;
        this.runOnce = runOnce;
        this.jobType = jobType;
        this.jobSubType = jobSubType;
        this.timezone = timezone;
        this.entityId=entityId;
    }

    public SchedulerPanelDTO(Long unitId, JobType jobType, JobSubType jobSubType, boolean oneTimeTrigger, LocalDateTime oneTimeTriggerDate, BigInteger entityId,String timezone) {
        this.unitId = unitId;
        this.jobType = jobType;
        this.jobSubType = jobSubType;
        this.oneTimeTrigger = oneTimeTrigger;
        this.oneTimeTriggerDate = oneTimeTriggerDate;
        this.entityId = entityId;
        this.timezone=timezone;
    }

    public SchedulerPanelDTO( JobType jobType, JobSubType jobSubType, boolean oneTimeTrigger, LocalDateTime oneTimeTriggerDate, BigInteger entityId,String timezone) {
        this.jobType = jobType;
        this.jobSubType = jobSubType;
        this.oneTimeTrigger = oneTimeTrigger;
        this.oneTimeTriggerDate = oneTimeTriggerDate;
        this.entityId = entityId;
        this.timezone=timezone;
    }

    public SchedulerPanelDTO(Long unitId, JobType jobType, JobSubType jobSubType, BigInteger entityId,
                             LocalDateTime oneTimeTriggerDate, boolean oneTimeTrigger, String filterId) {

        this.unitId = unitId;
        this.jobType = jobType;
        this.jobSubType = jobSubType;
        this.oneTimeTriggerDate = oneTimeTriggerDate;
        this.entityId = entityId;
        this.oneTimeTrigger = oneTimeTrigger;
        this.filterId = filterId;

    }

    public SchedulerPanelDTO(BigInteger id, LocalDateTime oneTimeTriggerDate) {
        this.id = id;
        this.oneTimeTriggerDate = oneTimeTriggerDate;
    }


    public void setOneTimeTriggerDateMillis(Long oneTimeTriggerDateMillis) {
        this.oneTimeTriggerDateMillis = oneTimeTriggerDateMillis;
        if (this.oneTimeTrigger && Optional.ofNullable(oneTimeTriggerDateMillis).isPresent()) {
            this.oneTimeTriggerDate = DateUtils.getLocalDateTimeFromMillis(oneTimeTriggerDateMillis);
        }
    }

}

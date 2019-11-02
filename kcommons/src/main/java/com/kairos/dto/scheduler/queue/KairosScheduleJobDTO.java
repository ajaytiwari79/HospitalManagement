package com.kairos.dto.scheduler.queue;


import com.kairos.enums.IntegrationOperation;
import com.kairos.enums.scheduler.JobSubType;
import com.kairos.enums.scheduler.JobType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class KairosScheduleJobDTO {
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
    private List<LocalTime> selectedHours;
    private Date startDate;
    private Date endDate;
    private Integer weeks;
    private Long unitId;
    private String filterId;
    private JobType jobType;
    private JobSubType jobSubType;
    private BigInteger entityId;
    private IntegrationOperation integrationOperation;
    private boolean oneTimeTrigger;
    private Long oneTimeTriggerDateMillis;

    public KairosScheduleJobDTO(Long unitId, JobType jobType, JobSubType jobSubType, BigInteger entityId,IntegrationOperation operation,
                                Long oneTimeTriggerDateMillis, boolean oneTimeTrigger) {

        this.unitId = unitId;
        this.jobType = jobType;
        this.jobSubType = jobSubType;
        this.entityId = entityId;
        this.integrationOperation = operation;
        this.oneTimeTriggerDateMillis = oneTimeTriggerDateMillis;
        this.oneTimeTrigger = oneTimeTrigger;
    }



}

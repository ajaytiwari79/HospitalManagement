package com.kairos.dto.activity.task_type;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.enums.task_type.TaskTypeEnum;

import javax.validation.constraints.NotNull;

/**
 * Created by prabjot on 12/12/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskTypeSlaConfigDTO {

    @NotNull
    private TaskTypeEnum.TaskTypeSlaDay taskTypeSlaDay;
    private short slaStartDuration;
    private short slaEndDuration;
    @NotNull(message = "Time slot id can't be null")
    private Long timeSlotId;

    public TaskTypeEnum.TaskTypeSlaDay getTaskTypeSlaDay() {
        return taskTypeSlaDay;
    }

    public void setTaskTypeSlaDay(TaskTypeEnum.TaskTypeSlaDay taskTypeSlaDay) {
        this.taskTypeSlaDay = taskTypeSlaDay;
    }

    public Long getTimeSlotId() {
        return timeSlotId;
    }

    public void setTimeSlotId(Long timeSlotId) {
        this.timeSlotId = timeSlotId;
    }

    public short getSlaStartDuration() {
        return slaStartDuration;
    }

    public void setSlaStartDuration(short slaStartDuration) {
        this.slaStartDuration = slaStartDuration;
    }

    public short getSlaEndDuration() {
        return slaEndDuration;
    }

    public void setSlaEndDuration(short slaEndDuration) {
        this.slaEndDuration = slaEndDuration;
    }
}

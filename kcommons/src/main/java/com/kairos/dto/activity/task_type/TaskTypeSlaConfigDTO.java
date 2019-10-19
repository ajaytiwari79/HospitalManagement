package com.kairos.dto.activity.task_type;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.enums.task_type.TaskTypeEnum;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * Created by prabjot on 12/12/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class TaskTypeSlaConfigDTO {

    @NotNull
    private TaskTypeEnum.TaskTypeSlaDay taskTypeSlaDay;
    private short slaStartDuration;
    private short slaEndDuration;
    @NotNull(message = "Time slot id can't be null")
    private Long timeSlotId;
}

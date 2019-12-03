package com.kairos.dto.activity.task_type;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

/**
 * @author pradeep
 * @date - 11/6/18
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class TaskTypeSettingDTO {

    private BigInteger id;
    private Long staffId;
    private BigInteger taskTypeId;
    private int efficiency;
    private String taskTypeName;
    private Long clientId;
    private int duration;
    private TaskTypeDTO taskType;

    public TaskTypeSettingDTO(BigInteger taskTypeId, String taskTypeName, Long clientId, int duration) {
        this.taskTypeId = taskTypeId;
        this.taskTypeName = taskTypeName;
        this.clientId = clientId;
        this.duration = duration;
    }
}

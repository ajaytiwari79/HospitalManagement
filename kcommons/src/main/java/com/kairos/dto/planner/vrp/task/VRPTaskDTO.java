package com.kairos.dto.planner.vrp.task;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.activity.task_type.TaskTypeDTO;
import com.kairos.dto.planner.vrp.TaskAddress;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

/**
 * @author pradeep
 * @date - 14/6/18
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class VRPTaskDTO {
    private BigInteger id;
    private TaskAddress address;
    //Vrp settings
    private Long installationNumber;
    private Long citizenId;
    private String skill;
    private BigInteger taskTypeId;
    private String citizenName;
    private TaskTypeDTO taskType;
    private Long unitId;
    private int duration;
}

package com.kairos.dto.activity.task;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by prabjot on 11/7/17.
 */
@Getter
@Setter
public class TaskRestrictionDto {

    private boolean allowedEngineers;
    private boolean notAllowedEngineers;
    private boolean team;
    private boolean skills;
    private boolean removeFix;
    private Integer slaTime;
    private boolean isReduction;
    private Integer priority;
    private Integer percentageDuration;
    private String delayPenalty;
    private Integer extraPenalty;
    private long citizenId;


    @JsonProperty(value = "isReduction")
    public void setReduction(boolean reduction) {
        isReduction = reduction;
    }

}

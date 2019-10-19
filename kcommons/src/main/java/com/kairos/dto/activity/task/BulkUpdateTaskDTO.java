package com.kairos.dto.activity.task;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.List;

/**
 * Created by prabjot on 27/6/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class BulkUpdateTaskDTO {

    private List<BigInteger> taskIds;
    private Integer slaDuration;
    private boolean isReduced;
    private String team;
    private String info1;
    private String info2;
    private List<Long> forbiddenStaff;
    private List<Long> prefferedStaff;
    private List<String> skillsList;
    private Boolean removeTeam;
    private Boolean removeNotAllowedStaff;
    private Boolean removeAllowedStaff;
    private Boolean removeSkills;
    private Integer priority;
    private Integer percentageDuration;

    @JsonProperty(value = "isReduced")
    public void setReduced(boolean reduced) {
        isReduced = reduced;
    }

}

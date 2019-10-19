package com.kairos.dto.scheduler;

import com.kairos.enums.scheduler.JobSubType;
import com.kairos.enums.scheduler.Result;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Getter
@Setter
public class JobDetailsDTO {
    private BigInteger schedulerPanelId;
    private LocalDateTime started;
    private LocalDateTime stopped;
    private Result result;
    private String processName;
    private String name;
    private String log;
    private Long unitId;
    private JobSubType jobSubType;

}

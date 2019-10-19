package com.kairos.dto.scheduler.scheduler_panel;

import com.kairos.enums.scheduler.JobSubType;
import com.kairos.enums.scheduler.JobType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@NoArgsConstructor
public class SchedulerPanelDefaultDataDto {

    private List<JobType> jobTypes;
    private List<JobSubType> jobSubTypes;

    public SchedulerPanelDefaultDataDto(List<JobSubType> jobSubTypes,List<JobType> jobTypes) {
        this.jobSubTypes = jobSubTypes;
        this.jobTypes = jobTypes;
    }




}

package com.kairos.dto.scheduler.scheduler_panel;

import com.kairos.enums.scheduler.JobSubType;
import com.kairos.enums.scheduler.JobType;
import java.util.List;

public class SchedulerPanelDefaultDataDto {

    private List<JobType> jobTypes;
    private List<JobSubType> jobSubTypes;

    public SchedulerPanelDefaultDataDto() {

    }
    public SchedulerPanelDefaultDataDto(List<JobSubType> jobSubTypes,List<JobType> jobTypes) {
        this.jobSubTypes = jobSubTypes;
        this.jobTypes = jobTypes;
    }
    public List<JobType> getJobTypes() {
        return jobTypes;
    }

    public void setJobTypes(List<JobType> jobTypes) {
        this.jobTypes = jobTypes;
    }

    public List<JobSubType> getJobsubTypes() {
        return jobSubTypes;
    }

    public void setJobsubTypes(List<JobSubType> jobsubTypes) {
        this.jobSubTypes = jobsubTypes;
    }



}

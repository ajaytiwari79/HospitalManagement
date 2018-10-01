package com.kairos.scheduler.persistence.repository.job_details;

import java.util.List;
import com.kairos.scheduler.persistence.model.scheduler_panel.jobDetails.JobDetails;

public interface CustomJobDetailsRepository {

    public List<JobDetails> findAllSchedulerPanelsByUnitIdAndOffset(Long unitId, int offset);
}

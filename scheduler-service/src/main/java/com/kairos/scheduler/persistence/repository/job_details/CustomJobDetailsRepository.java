package com.kairos.scheduler.persistence.repository.job_details;

import java.util.List;

import com.kairos.dto.scheduler.JobDetailsDTO;
import com.kairos.scheduler.persistence.model.scheduler_panel.jobDetails.JobDetails;

public interface CustomJobDetailsRepository {

    public List<JobDetailsDTO> findAllSchedulerPanelsByUnitIdAndOffset(Long unitId, int offset);
}

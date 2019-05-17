package com.kairos.scheduler.persistence.repository.job_details;

import com.kairos.dto.scheduler.JobDetailsDTO;

import java.util.List;

public interface CustomJobDetailsRepository {

    public List<JobDetailsDTO> findAllSchedulerPanelsByUnitIdAndOffset(Long unitId, int offset);
}

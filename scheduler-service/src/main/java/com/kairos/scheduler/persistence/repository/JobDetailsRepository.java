package com.kairos.scheduler.persistence.repository;

import java.util.List;
import com.kairos.scheduler.persistence.model.common.MongoBaseEntity;
import com.kairos.scheduler.persistence.model.scheduler_panel.jobDetails.JobDetails;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
@Repository
public interface JobDetailsRepository extends MongoRepository<JobDetails,BigInteger> {


    List<JobDetails> findAllBySchedulerPanelIdOrderByStartedDesc(BigInteger schedulerPanelId);
}

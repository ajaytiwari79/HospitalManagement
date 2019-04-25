package com.kairos.scheduler.persistence.repository.job_details;

import com.kairos.scheduler.persistence.model.scheduler_panel.jobDetails.JobDetails;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
@Repository
public interface JobDetailsRepository extends MongoRepository<JobDetails,BigInteger>,CustomJobDetailsRepository {


    List<JobDetails> findAllBySchedulerPanelIdOrderByStartedDesc(BigInteger schedulerPanelId);


}

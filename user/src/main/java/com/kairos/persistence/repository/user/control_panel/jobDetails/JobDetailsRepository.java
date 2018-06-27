package com.kairos.persistence.repository.user.control_panel.jobDetails;
import com.kairos.persistence.model.user.control_panel.jobDetails.JobDetails;
import org.springframework.data.domain.Sort;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

/**
 * Created by oodles on 23/1/17.
 */
@Repository
public interface JobDetailsRepository extends Neo4jBaseRepository<JobDetails,Long> {

    List<JobDetails> findByControlPanelId(Long controlPanelId, Sort sort);

}

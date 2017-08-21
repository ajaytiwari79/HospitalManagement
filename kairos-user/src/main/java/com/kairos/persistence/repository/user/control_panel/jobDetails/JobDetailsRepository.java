package com.kairos.persistence.repository.user.control_panel.jobDetails;
import com.kairos.persistence.model.user.control_panel.jobDetails.JobDetails;
import org.springframework.data.domain.Sort;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

/**
 * Created by oodles on 23/1/17.
 */
@Repository
public interface JobDetailsRepository extends GraphRepository<JobDetails> {

    List<JobDetails> findByControlPanelId(Long controlPanelId, Sort sort);

}

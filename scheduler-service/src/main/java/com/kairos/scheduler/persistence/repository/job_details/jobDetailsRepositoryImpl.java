package com.kairos.scheduler.persistence.repository.job_details;

import com.kairos.scheduler.persistence.model.scheduler_panel.jobDetails.JobDetails;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;

@Repository
public class jobDetailsRepositoryImpl implements CustomJobDetailsRepository {

    @Inject
    private MongoTemplate mongoTemplate;

    public List<JobDetails> findAllSchedulerPanelsByUnitIdAndOffset(Long unitId, int offset) {

        Query query = Query.query(Criteria.where("unitId").is(unitId).and("deleted").is(false));
        query.with(new Sort(Sort.Direction.DESC, "started"));

        query.skip(offset*50);
        query.limit(50);
        return mongoTemplate.find(query,JobDetails.class);
    }
}

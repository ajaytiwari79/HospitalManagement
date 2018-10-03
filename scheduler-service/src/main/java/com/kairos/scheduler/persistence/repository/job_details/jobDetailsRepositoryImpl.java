package com.kairos.scheduler.persistence.repository.job_details;

import com.kairos.dto.scheduler.JobDetailsDTO;
import com.kairos.scheduler.persistence.model.scheduler_panel.jobDetails.JobDetails;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Repository
public class jobDetailsRepositoryImpl implements CustomJobDetailsRepository {

    @Inject
    private MongoTemplate mongoTemplate;

    public List<JobDetailsDTO> findAllSchedulerPanelsByUnitIdAndOffset(Long unitId, int offset) {

        Query query = Query.query(Criteria.where("unitId").is(unitId).and("deleted").is(false));
        query.with(new Sort(Sort.Direction.DESC, "started"));

        query.skip(offset*50);
        query.limit(50);
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("unitId").is(unitId).and("deleted").is(false)),
                lookup("schedulerPanel","schedulerPanelId","_id","schedulerPanel"),
                        project("schedulerPanelId","unitId","started","stopped","result","log","processName").and("schedulerPanel").arrayElementAt(0).as("schedulerPanel").and("schedulerPanel.jobSubType").arrayElementAt(0).as("jobSubType"),
                sort(Sort.Direction.DESC,"started"),
                skip(offset*50L),
                limit(50)
        );
        AggregationResults<JobDetailsDTO> result = mongoTemplate.aggregate(aggregation, JobDetails.class, JobDetailsDTO.class);

        return result.getMappedResults();
    }
}

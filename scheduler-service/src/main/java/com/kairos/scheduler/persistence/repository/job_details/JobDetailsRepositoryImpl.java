package com.kairos.scheduler.persistence.repository.job_details;

import com.kairos.dto.scheduler.JobDetailsDTO;
import com.kairos.scheduler.persistence.model.scheduler_panel.jobDetails.JobDetails;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Repository
public class JobDetailsRepositoryImpl implements CustomJobDetailsRepository {

    public static final String SCHEDULER_PANEL = "schedulerPanel";
    @Inject
    private MongoTemplate mongoTemplate;

    public List<JobDetailsDTO> findAllSchedulerPanelsByUnitIdAndOffset(Long unitId, int offset) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("unitId").is(unitId).and("deleted").is(false)),
                lookup(SCHEDULER_PANEL,"schedulerPanelId","_id", SCHEDULER_PANEL),
                        project("schedulerPanelId","unitId","started","stopped","result","log","processName").and(SCHEDULER_PANEL).arrayElementAt(0).as(SCHEDULER_PANEL).and("schedulerPanel.jobSubType").arrayElementAt(0).as("jobSubType"),
                sort(Sort.Direction.DESC,"started"),
                skip(offset*50L),
                limit(50)
        );
        AggregationResults<JobDetailsDTO> result = mongoTemplate.aggregate(aggregation, JobDetails.class, JobDetailsDTO.class);

        return result.getMappedResults();
    }
}

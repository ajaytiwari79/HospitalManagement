package com.kairos.persistence.repository.data_inventory.Assessment;

import com.kairos.enums.gdpr.AssessmentStatus;
import com.kairos.persistence.model.data_inventory.assessment.Assessment;
import com.kairos.response.dto.common.AssessmentBasicResponseDTO;
import org.springframework.data.mongodb.core.MongoTemplate;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Collation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import javax.inject.Inject;

import java.util.ArrayList;
import java.util.List;

import static com.kairos.constants.AppConstant.DELETED;
import static com.kairos.constants.AppConstant.ORGANIZATION_ID;

public class AssessmentMongoRepositoryImpl implements CustomAssessmentRepository {


    @Inject
    private MongoTemplate mongoTemplate;


    @Override
    public Assessment findAssessmentByNameAndUnitId(Long unitId, String name) {
        Query query = new Query(Criteria.where(ORGANIZATION_ID).is(unitId).and(DELETED).is(false).and("name").is(name));
        query.collation(Collation.of("en").strength(Collation.ComparisonLevel.secondary()));
        return mongoTemplate.findOne(query, Assessment.class);
    }

    @Override
    public List<AssessmentBasicResponseDTO> getAllLaunchedAssessmentAssignToRespondent(Long unitId,Long loggedInUserId) {
        List<AssessmentStatus> assessmentStatusList = new ArrayList<>();
        assessmentStatusList.add(AssessmentStatus.NEW);
        assessmentStatusList.add(AssessmentStatus.IN_PROGRESS);
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(ORGANIZATION_ID).is(unitId).and(DELETED).is(false).and("assessmentStatus").in(assessmentStatusList).and("assignee._id").is(loggedInUserId))

        );
        AggregationResults<AssessmentBasicResponseDTO> result = mongoTemplate.aggregate(aggregation, Assessment.class, AssessmentBasicResponseDTO.class);
       return result.getMappedResults();
    }
}

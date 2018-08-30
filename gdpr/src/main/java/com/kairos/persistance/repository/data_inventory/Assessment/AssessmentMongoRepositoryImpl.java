package com.kairos.persistance.repository.data_inventory.Assessment;

import com.kairos.enums.AssessmentStatus;
import com.kairos.persistance.model.data_inventory.assessment.Assessment;
import com.kairos.response.dto.data_inventory.AssessmentResponseDTO;
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
    public List<AssessmentResponseDTO> getAllLaunchedAssessmentAssignToRespondent(Long unitId) {
        List<AssessmentStatus> assessmentStatusList = new ArrayList<>();
        assessmentStatusList.add(AssessmentStatus.NEW);
        assessmentStatusList.add(AssessmentStatus.INPROGRESS);
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(ORGANIZATION_ID).is(unitId).and(DELETED).is(false).and("assessmentStatus").in(assessmentStatusList))

        );
        AggregationResults<AssessmentResponseDTO> result = mongoTemplate.aggregate(aggregation, Assessment.class, AssessmentResponseDTO.class);
       return result.getMappedResults();
    }
}

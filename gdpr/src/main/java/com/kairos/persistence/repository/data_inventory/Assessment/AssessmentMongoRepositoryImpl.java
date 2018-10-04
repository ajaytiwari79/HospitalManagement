package com.kairos.persistence.repository.data_inventory.Assessment;

import com.kairos.enums.gdpr.AssessmentStatus;
import com.kairos.persistence.model.data_inventory.assessment.Assessment;
import com.kairos.persistence.repository.client_aggregator.CustomAggregationOperation;
import com.kairos.response.dto.common.AssessmentBasicResponseDTO;
import com.kairos.response.dto.common.AssessmentResponseDTO;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Collation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import javax.inject.Inject;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

import static com.kairos.constants.AppConstant.DELETED;
import static com.kairos.constants.AppConstant.ORGANIZATION_ID;

public class AssessmentMongoRepositoryImpl implements CustomAssessmentRepository {


    @Inject
    private MongoTemplate mongoTemplate;

    List<AssessmentStatus> assessmentStatusList = Arrays.asList(AssessmentStatus.NEW, AssessmentStatus.IN_PROGRESS);


    @Override
    public Assessment findAssessmentByNameAndUnitId(Long unitId, String name) {
        Query query = new Query(Criteria.where(ORGANIZATION_ID).is(unitId).and(DELETED).is(false).and("name").is(name));
        query.collation(Collation.of("en").strength(Collation.ComparisonLevel.secondary()));
        return mongoTemplate.findOne(query, Assessment.class);
    }

    @Override
    public List<AssessmentBasicResponseDTO> getAllLaunchedAssessmentAssignToRespondent(Long unitId, Long loggedInUserId) {

        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(ORGANIZATION_ID).is(unitId).and(DELETED).is(false).and("assessmentStatus").in(assessmentStatusList).and("assignee._id").is(loggedInUserId))

        );
        AggregationResults<AssessmentBasicResponseDTO> result = mongoTemplate.aggregate(aggregation, Assessment.class, AssessmentBasicResponseDTO.class);
        return result.getMappedResults();
    }

    @Override
    public List<AssessmentResponseDTO> getAllAssessmentByUnitId(Long unitId) {

        String projectionOpertaion = "{ '$project':{'asset':{$arrayElemAt:['$asset',0]},'processingActivity':{'$arrayElemAt':['$processingActivity',0]}," +
                "'_id':1,'name':1,'endDate':1,'completedDate':1,'comment':1,'assignee':1,'approver':1,'assessmentStatus':1}}";

        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(ORGANIZATION_ID).is(unitId).and(DELETED).is(false)),
                lookup("asset", "assetId", "_id", "asset"),
                lookup("processing_activity", "", "_id", "processingActivity"),
                new CustomAggregationOperation(Document.parse(projectionOpertaion))

        );
        AggregationResults<AssessmentResponseDTO> result = mongoTemplate.aggregate(aggregation, Assessment.class, AssessmentResponseDTO.class);
        return result.getMappedResults();
    }

    @Override
    public Assessment findPreviousLaunchedAssessmentOfAssetByUnitId(Long unitId, BigInteger assetId) {

        Query query = new Query(Criteria.where(ORGANIZATION_ID).is(unitId).and(DELETED).is(false).and("assetId").is(assetId).and("assessmentStatus").in(assessmentStatusList));
        return mongoTemplate.findOne(query, Assessment.class);
    }

    @Override
    public Assessment findPreviousLaunchedAssessmentOfProcessingActivityByUnitId(Long unitId, BigInteger processingActivityId) {
        Query query = new Query(Criteria.where(ORGANIZATION_ID).is(unitId).and(DELETED).is(false).and("processingActivityId").is(processingActivityId).and("assessmentStatus").in(assessmentStatusList));
        return mongoTemplate.findOne(query, Assessment.class);
    }
}

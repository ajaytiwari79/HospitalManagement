package com.kairos.persistence.repository.risk_management;

import com.kairos.persistence.model.risk_management.Risk;
import com.kairos.persistence.repository.client_aggregator.CustomAggregationOperation;
import com.kairos.response.dto.common.RiskResponseDTO;
import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import static com.kairos.constants.AppConstant.*;

import javax.inject.Inject;
import java.util.List;

public class RiskMongoRepositoryImpl implements CustomRiskRepository {


    @Inject
    private MongoTemplate mongoTemplate;


    private String projectionOperationForRisk = "{'$project':{'processingActivity':{'$arrayElemAt':['$processingActivity',0]},'assetType':{'$arrayElemAt':['$assetType',0]}," +
            "'riskLevel':1,'riskOwner':1,'daysToReminderBefore':1 ,'isReminderActive':1,'riskRecommendation':1,'dueDate':1,'description':1,'name':1 }}";

    @Override
    public List<RiskResponseDTO> getAllRiskByUnitId(Long unitId) {

        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(ORGANIZATION_ID).is(unitId).and(DELETED).is(false)),
                lookup("assetType", "assetType", "_id", "assetType"),
                lookup("processingActivity", "processingActivity", "_id", "processingActivity")
                , sort(Sort.Direction.DESC, "createdAt"),
                new CustomAggregationOperation(Document.parse(projectionOperationForRisk))

        );
        AggregationResults<RiskResponseDTO> results = mongoTemplate.aggregate(aggregation, Risk.class, RiskResponseDTO.class);
        return results.getMappedResults();
    }
}

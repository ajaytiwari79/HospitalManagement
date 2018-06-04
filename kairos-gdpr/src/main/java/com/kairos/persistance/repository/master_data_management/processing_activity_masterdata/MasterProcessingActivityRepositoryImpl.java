package com.kairos.persistance.repository.master_data_management.processing_activity_masterdata;

import com.kairos.persistance.model.master_data_management.processing_activity_masterdata.MasterProcessingActivity;
import com.kairos.response.dto.MasterProcessingActivityResponseDto;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;

import javax.inject.Inject;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

import java.math.BigInteger;
import java.util.List;

public class MasterProcessingActivityRepositoryImpl implements CustomMasterProcessingActivity {



    @Inject
    private MongoTemplate mongoTemplate;


    @Override
    public MasterProcessingActivityResponseDto getMasterProcessingActivityWithSubProcessingActivity(Long countryId, BigInteger id) {


        Aggregation aggregation=Aggregation.newAggregation(

                match(Criteria.where("countryId").is(countryId).and("_id").is(id).and("deleted").is(false).and("isSubProcess").is(false)),
                lookup("master_processing_activity","subProcessingActivityIds","_id","subProcessingActivities")

        );


        AggregationResults<MasterProcessingActivityResponseDto> result=mongoTemplate.aggregate(aggregation,MasterProcessingActivity.class,MasterProcessingActivityResponseDto.class);

        return result.getUniqueMappedResult();
    }

    @Override
    public List<MasterProcessingActivityResponseDto> getMasterProcessingActivityListWithSubProcessingActivity(Long countryId) {
        Aggregation aggregation=Aggregation.newAggregation(

                match(Criteria.where("countryId").is(countryId).and("deleted").is(false)),
                lookup("master_processing_activity","subProcessingActivityIds","_id","subProcessingActivities")

        );


        AggregationResults<MasterProcessingActivityResponseDto> result=mongoTemplate.aggregate(aggregation,MasterProcessingActivity.class,MasterProcessingActivityResponseDto.class);

        return result.getMappedResults();

    }
}

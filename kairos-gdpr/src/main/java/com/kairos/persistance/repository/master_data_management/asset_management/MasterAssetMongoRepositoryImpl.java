package com.kairos.persistance.repository.master_data_management.asset_management;

import com.kairos.persistance.model.master_data_management.asset_management.MasterAsset;
import com.kairos.response.dto.filter.FilterQueryResult;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;

import javax.inject.Inject;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

public class MasterAssetMongoRepositoryImpl implements CustomMasterAssetRepository {


    @Inject
    private MongoTemplate mongoTemplate;

    @Override
    public FilterQueryResult getMasterAssetFilter(Long countryId) {


        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("countryId").is(countryId).and("deleted").is(false)),
                unwind("organizationTypes"),
                unwind("organizationSubTypes"),
                unwind("organizationServices"),
                unwind("organizationSubServices"),
                group()
                        .addToSet("organizationSubTypes").as("organizationSubTypes")
                        .addToSet("organizationTypes").as("organizationTypes")
                        .addToSet("organizationServices").as("organizationServices")
                        .addToSet("organizationSubServices").as("organizationSubServices")

        );

        AggregationResults<FilterQueryResult> result = mongoTemplate.aggregate(aggregation, MasterAsset.class, FilterQueryResult.class);
        return  result.getUniqueMappedResult();


    }
}

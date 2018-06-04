package com.kairos.persistance.repository.master_data_management.asset_management;

import com.kairos.dto.FilterSelectionDto;
import com.kairos.persistance.model.clause.Clause;
import com.kairos.persistance.model.master_data_management.asset_management.MasterAsset;
import com.kairos.response.dto.filter.FilterQueryResult;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

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
        return result.getUniqueMappedResult();


    }

    @Override
    public List<MasterAsset> getMasterAssetListWithFilterData(Long countryId, FilterSelectionDto filterSelectionDto) {

        Query query = new Query(Criteria.where("countryId").is(countryId).and("deleted").is(false));
        if (Optional.ofNullable(filterSelectionDto.getOrganizationTypes()).isPresent()) {
            query.addCriteria(Criteria.where(filterSelectionDto.getOrganizationTypes().getName() + "._id").in(filterSelectionDto.getOrganizationTypes().getValues()));
        }
        if (Optional.ofNullable(filterSelectionDto.getOrganizationSubTypes()).isPresent()) {
            query.addCriteria(Criteria.where(filterSelectionDto.getOrganizationSubTypes().getName() + "._id").in(filterSelectionDto.getOrganizationSubTypes().getValues()));

        }
        if (Optional.ofNullable(filterSelectionDto.getOrganizationServices()).isPresent()) {
            query.addCriteria(Criteria.where(filterSelectionDto.getOrganizationServices().getName() + "._id").in(filterSelectionDto.getOrganizationServices().getValues()));

        }
        if (Optional.ofNullable(filterSelectionDto.getOrganizationSubServices()).isPresent()) {
            query.addCriteria(Criteria.where(filterSelectionDto.getOrganizationSubServices().getName() + "._id").in(filterSelectionDto.getOrganizationSubServices().getValues()));
        }

        List<MasterAsset> result =  (List<MasterAsset>) mongoTemplate.find(query, MasterAsset.class);
        return result;
    }
}

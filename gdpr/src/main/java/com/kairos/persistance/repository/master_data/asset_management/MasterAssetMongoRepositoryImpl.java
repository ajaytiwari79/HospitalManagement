package com.kairos.persistance.repository.master_data.asset_management;

import com.kairos.custom_exception.InvalidRequestException;
import com.kairos.dto.FilterSelection;
import com.kairos.dto.FilterSelectionDTO;
import com.kairos.enums.FilterType;
import com.kairos.persistance.model.master_data.asset_management.MasterAsset;
import com.kairos.response.dto.filter.FilterQueryResult;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Collation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import javax.inject.Inject;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import static com.kairos.constants.AppConstant.ID;
import static com.kairos.constants.AppConstant.COUNTRY_ID;
import static com.kairos.constants.AppConstant.ORGANIZATION_ID;
import static com.kairos.constants.AppConstant.DELETED;

public class MasterAssetMongoRepositoryImpl implements CustomMasterAssetRepository {


    @Inject
    private MongoTemplate mongoTemplate;

    @Override
    public MasterAsset findByName(Long countryId, Long organizationId, String name) {
        Query query = new Query();
        query.addCriteria(Criteria.where("countryId").is(countryId).and("deleted").is(false).and("name").is(name).and(ORGANIZATION_ID).is(organizationId));
        query.collation(Collation.of("en").
                strength(Collation.ComparisonLevel.secondary()));
        return mongoTemplate.findOne(query, MasterAsset.class);

    }

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
    public List<MasterAsset> getMasterAssetDataWithFilterSelection(Long countryId,Long organizationId,FilterSelectionDTO filterSelectionDto) {

        Query query = new Query(Criteria.where(COUNTRY_ID).is(countryId).and(DELETED).is(false).and(ORGANIZATION_ID).is(organizationId));

        filterSelectionDto.getFiltersData().forEach(filterSelection -> {
            if (filterSelection.getValue().size()!=0) {

                query.addCriteria(buildQuery(filterSelection, filterSelection.getName(), query));
            }
        });
        return mongoTemplate.find(query, MasterAsset.class);

    }


    @Override
    public Criteria buildQuery(FilterSelection filterSelection, FilterType filterType, Query query) {

        switch (filterType) {
            case ACCOUNT_TYPES:
                return Criteria.where(filterType.value + ID).in(filterSelection.getValue());
            case ORGANIZATION_TYPES:
                return Criteria.where(filterType.value + ID).in(filterSelection.getValue());

            case ORGANIZATION_SUB_TYPES:
                return Criteria.where(filterType.value + ID).in(filterSelection.getValue());
            case ORGANIZATION_SERVICES:
                return Criteria.where(filterType.value + ID).in(filterSelection.getValue());

            case ORGANIZATION_SUB_SERVICES:
                return Criteria.where(filterType.value + ID).in(filterSelection.getValue());
            default:
                throw new InvalidRequestException("data not found for Filtertype " + filterType);


        }


    }


}

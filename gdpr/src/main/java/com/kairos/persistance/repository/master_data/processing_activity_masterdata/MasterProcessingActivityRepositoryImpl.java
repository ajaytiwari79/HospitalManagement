package com.kairos.persistance.repository.master_data.processing_activity_masterdata;

import com.kairos.custom_exception.InvalidRequestException;
import com.kairos.dto.FilterSelection;
import com.kairos.dto.FilterSelectionDTO;
import com.kairos.enums.FilterType;
import com.kairos.persistance.model.master_data.default_proc_activity_setting.MasterProcessingActivity;
import com.kairos.persistance.repository.client_aggregator.CustomAggregationOperation;
import com.kairos.persistance.repository.common.CustomAggregationQuery;
import com.kairos.response.dto.master_data.MasterProcessingActivityResponseDTO;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Collation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import javax.inject.Inject;

import static com.kairos.constants.AppConstant.COUNTRY_ID;
import static com.kairos.constants.AppConstant.DELETED;
import static com.kairos.constants.AppConstant.ORGANIZATION_ID;
import static com.kairos.constants.AppConstant.ID;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

import java.math.BigInteger;
import java.util.List;


public class MasterProcessingActivityRepositoryImpl implements CustomMasterProcessingActivity {


    @Inject
    private MongoTemplate mongoTemplate;


    @Override
    public MasterProcessingActivity findByName(Long countryId, Long organizationId, String name) {
        Query query = new Query();
        query.addCriteria(Criteria.where("countryId").is(countryId).and("deleted").is(false).and("name").is(name).and(ORGANIZATION_ID).is(organizationId).and("isSubProcess").is(false));
        query.collation(Collation.of("en").
                strength(Collation.ComparisonLevel.secondary()));
        return mongoTemplate.findOne(query, MasterProcessingActivity.class);


    }

    @Override
    public MasterProcessingActivityResponseDTO getMasterProcessingActivityWithSubProcessingActivity(Long countryId,Long organizationId,BigInteger id) {

        Document projectionOperation = Document.parse(CustomAggregationQuery.processingActivityWithSubProcessingNonDeletedData());
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(COUNTRY_ID).is(countryId).and("_id").is(id).and(DELETED).is(false).and("isSubProcess").is(false).and(ORGANIZATION_ID).is(organizationId)),
                lookup("master_processing_activity", "subProcessingActivityIds", "_id", "subProcessingActivities")
                , new CustomAggregationOperation(projectionOperation)
        );
        AggregationResults<MasterProcessingActivityResponseDTO> result = mongoTemplate.aggregate(aggregation, MasterProcessingActivity.class, MasterProcessingActivityResponseDTO.class);
        return result.getUniqueMappedResult();
    }

    @Override
    public List<MasterProcessingActivityResponseDTO> getMasterProcessingActivityListWithSubProcessingActivity(Long countryId,Long organizationId) {
        Document projectionOperation = Document.parse(CustomAggregationQuery.processingActivityWithSubProcessingNonDeletedData());
        Aggregation aggregation = Aggregation.newAggregation(

                match(Criteria.where(COUNTRY_ID).is(countryId).and(DELETED).is(false).and("isSubProcess").is(false).and(ORGANIZATION_ID).is(organizationId)),
                lookup("master_processing_activity", "subProcessingActivityIds", "_id", "subProcessingActivities")
                , new CustomAggregationOperation(projectionOperation)
        );
        AggregationResults<MasterProcessingActivityResponseDTO> result = mongoTemplate.aggregate(aggregation, MasterProcessingActivity.class, MasterProcessingActivityResponseDTO.class);
        return result.getMappedResults();

    }

    @Override
    public List<MasterProcessingActivity> getMasterProcessingActivityWithFilterSelection(Long countryId,Long organizationId, FilterSelectionDTO filterSelectionDto) {
        Query query = new Query(Criteria.where(COUNTRY_ID).is(countryId).and(DELETED).is(false).and("isSubProcess").is(false).and(ORGANIZATION_ID).is(organizationId));
        filterSelectionDto.getFiltersData().forEach(filterSelection -> {
            if (filterSelection.getValue().size() != 0) {
              query.addCriteria(buildQuery(filterSelection, filterSelection.getName(), query));


            }
        });
       return mongoTemplate.find(query, MasterProcessingActivity.class);

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
                throw new InvalidRequestException("data not found for FilterType " + filterType);


        }
    }
}

package com.kairos.persistance.repository.master_data_management.processing_activity_masterdata;

import com.kairos.custome_exception.InvalidRequestException;
import com.kairos.dto.FilterSelection;
import com.kairos.dto.FilterSelectionDto;
import com.kairos.enums.FilterType;
import com.kairos.persistance.model.master_data_management.processing_activity_masterdata.MasterProcessingActivity;
import com.kairos.response.dto.MasterProcessingActivityResponseDto;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import javax.inject.Inject;
import static com.kairos.constant.AppConstant.COUNTRY_ID;
import static com.kairos.constant.AppConstant.DELETED;
import static com.kairos.constant.AppConstant.ID;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import java.math.BigInteger;
import java.util.List;


public class MasterProcessingActivityRepositoryImpl implements CustomMasterProcessingActivity {



    @Inject
    private MongoTemplate mongoTemplate;


    @Override
    public MasterProcessingActivityResponseDto getMasterProcessingActivityWithSubProcessingActivity(Long countryId, BigInteger id) {


        Aggregation aggregation=Aggregation.newAggregation(

                match(Criteria.where(COUNTRY_ID).is(countryId).and("_id").is(id).and(DELETED).is(false).and("isSubProcess").is(false)),
                lookup("master_processing_activity","subProcessingActivityIds","_id","subProcessingActivities")

        );


        AggregationResults<MasterProcessingActivityResponseDto> result=mongoTemplate.aggregate(aggregation,MasterProcessingActivity.class,MasterProcessingActivityResponseDto.class);

        return result.getUniqueMappedResult();
    }

    @Override
    public List<MasterProcessingActivityResponseDto> getMasterProcessingActivityListWithSubProcessingActivity(Long countryId) {
        Aggregation aggregation=Aggregation.newAggregation(

                match(Criteria.where(COUNTRY_ID).is(countryId).and(DELETED).is(false).and("isSubProcess").is(false)),
                lookup("master_processing_activity","subProcessingActivityIds","_id","subProcessingActivities")

        );


        AggregationResults<MasterProcessingActivityResponseDto> result=mongoTemplate.aggregate(aggregation,MasterProcessingActivity.class,MasterProcessingActivityResponseDto.class);

        return result.getMappedResults();

    }

    @Override
    public List<MasterProcessingActivity> getMasterProcessingActivityWithFilterSelection(Long countryId, FilterSelectionDto filterSelectionDto) {
        Query query = new Query(Criteria.where(COUNTRY_ID).is(countryId).and(DELETED).is(false).and("isSubProcess").is(false));
        filterSelectionDto.getFiltersData().forEach(filterSelection -> {

            if (filterSelection.getValue().size()!=0) {
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
                throw new InvalidRequestException("data not found for Filtertype " + filterType);


        }
    }
}

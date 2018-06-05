package com.kairos.persistance.repository.filter;

import com.kairos.custome_exception.DataNotFoundByIdException;
import com.kairos.custome_exception.InvalidRequestException;
import com.kairos.dto.ModuleIdDto;
import com.kairos.persistance.model.clause.Clause;
import com.kairos.persistance.model.enums.FilterType;
import com.kairos.persistance.model.filter.FilterGroup;
import com.kairos.persistance.model.master_data_management.asset_management.MasterAsset;
import com.kairos.persistance.model.master_data_management.processing_activity_masterdata.MasterProcessingActivity;
import com.kairos.persistance.model.processing_activity.ProcessingActivity;
import com.kairos.response.dto.filter.FilterQueryResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static com.kairos.constant.AppConstant.CLAUSE_MODULE_NAME;
import static com.kairos.constant.AppConstant.ASSET_MODULE_NAME;
import static com.kairos.constant.AppConstant.PROCESSING_ACTIVITY_NAME;


public class FilterMongoRepositoryImpl implements CustomeFilterMongoRepository {



    @Inject
    private MongoTemplate mongoTemplate;

    @Override
    public Map<String, AggregationOperation> getFilterCriterias(Long countryId, List<FilterType> filterTypes) {
        Map<String, AggregationOperation> aggregationOperations = new HashMap<>();
        aggregationOperations.put("match", match(Criteria.where("countryId").is(countryId).and("deleted").is(false)));
        filterTypes.forEach(filterType -> {
                    aggregationOperations.put(filterType.value, buildAggregationQuery(filterType));
                }

        );

        return aggregationOperations;
    }

    @Override
    public AggregationOperation buildAggregationQuery(FilterType filterType) {
        switch (filterType) {

            case ACCOUNT_TYPES:
                return Aggregation.unwind(filterType.value);
            case ORGANIZATION_SERVICES:
                return Aggregation.unwind(filterType.value);
            case ORGANIZATION_SUB_SERVICES:
                return Aggregation.unwind(filterType.value);
            case ORGANIZATION_TYPES:
                return Aggregation.unwind(filterType.value);
            case ORGANIZATION_SUB_TYPES:
                return Aggregation.unwind(filterType.value);
            default:
                throw new InvalidRequestException("invalid request");
        }


    }

    @Override
    public Aggregation createAggregationQueryForMasterAsset(Map<String, AggregationOperation> aggregationOperations) {
        GroupOperation groupOperation = group();
        List<AggregationOperation> operations = new ArrayList<>();
        operations.add(aggregationOperations.get("match"));
        for (Map.Entry<String, AggregationOperation> entry : aggregationOperations.entrySet())
            if (entry.getKey().equals("match")) {
                continue;
            } else {
                operations.add(entry.getValue());
                groupOperation = groupOperation.addToSet(entry.getKey()).as(entry.getKey());
            }
        operations.add(groupOperation);
        Aggregation aggregation = Aggregation.newAggregation(operations);
        return aggregation;

    }

    @Override
    public AggregationResults<FilterQueryResult> getFilterAggregationResult(Aggregation aggregation,FilterGroup filterGroup, String moduleId) {

        List<ModuleIdDto> moduleIdDto = filterGroup.getAccessModule();
        String domainName = new String();
        for (ModuleIdDto moduleIdDto1 : moduleIdDto) {
            if (moduleIdDto1.getModuleId().equalsIgnoreCase(moduleId)) {
                domainName = moduleIdDto1.getName();
                break;
            }
        }
        if (StringUtils.isBlank(domainName))
        {
            throw new InvalidRequestException("module name is null");
        }

        switch (domainName){

            case CLAUSE_MODULE_NAME:
                 return mongoTemplate.aggregate(aggregation,Clause.class,FilterQueryResult.class);
            case ASSET_MODULE_NAME:
                return mongoTemplate.aggregate(aggregation,MasterAsset.class,FilterQueryResult.class);
            case PROCESSING_ACTIVITY_NAME:
                return mongoTemplate.aggregate(aggregation,ProcessingActivity.class,FilterQueryResult.class);
                default:
                    throw new DataNotFoundByIdException("data not found by moduleId"+moduleId);

        }
       /* if (domainName.toLowerCase().contains("asset"))
        {
            return mongoTemplate.aggregate(aggregation,MasterAsset.class,FilterQueryResult.class);
        }
        if (domainName.toLowerCase().contains("clauses"))
        {
            return mongoTemplate.aggregate(aggregation,Clause.class,FilterQueryResult.class);
        } if (domainName.toLowerCase().contains("processing"))
        {
            return mongoTemplate.aggregate(aggregation,MasterProcessingActivity.class,FilterQueryResult.class);
        }
        else
            throw new DataNotFoundByIdException("data not found by moduleId"+moduleId);*/
    }
}

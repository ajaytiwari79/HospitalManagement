package com.kairos.persistance.repository.filter;

import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.InvalidRequestException;
import com.kairos.dto.master_data.ModuleIdDTO;
import com.kairos.persistance.model.clause.Clause;
import com.kairos.enums.FilterType;
import com.kairos.persistance.model.filter.FilterGroup;
import com.kairos.persistance.model.master_data.asset_management.MasterAsset;
import com.kairos.persistance.model.master_data.processing_activity_masterdata.MasterProcessingActivity;
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
import static com.kairos.constants.AppConstant.CLAUSE_MODULE_NAME;
import static com.kairos.constants.AppConstant.ASSET_MODULE_NAME;
import static com.kairos.constants.AppConstant.MASTER_PROCESSING_ACTIVITY_MODULE_NAME;
import static com.kairos.constants.AppConstant.COUNTRY_ID;
import static com.kairos.constants.AppConstant.DELETED;
import static com.kairos.constants.AppConstant.ORGANIZATION_ID;


public class FilterMongoRepositoryImpl implements CustomFilterMongoRepository {


    @Inject
    private MongoTemplate mongoTemplate;

    @Override
    public Map<String, AggregationOperation> getFilterCriterias(Long countryId,Long organizationId,List<FilterType> filterTypes) {
        Map<String, AggregationOperation> aggregationOperations = new HashMap<>();
        aggregationOperations.put("match", match(Criteria.where(COUNTRY_ID).is(countryId).and(DELETED).is(false).and(ORGANIZATION_ID).is(organizationId)));
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
    public AggregationResults<FilterQueryResult> getFilterAggregationResult(Aggregation aggregation, FilterGroup filterGroup, String moduleId) {

        List<ModuleIdDTO> moduleIdDto = filterGroup.getAccessModule();
        String domainName = new String();
        for (ModuleIdDTO moduleIdDto1 : moduleIdDto) {
            if (moduleIdDto1.getModuleId().equalsIgnoreCase(moduleId)) {
                domainName = moduleIdDto1.getName();
                break;
            }
        }
        if (StringUtils.isBlank(domainName)) {
            throw new InvalidRequestException("module name is null");
        }
        switch (domainName) {
            case CLAUSE_MODULE_NAME:
                return mongoTemplate.aggregate(aggregation, Clause.class, FilterQueryResult.class);
            case ASSET_MODULE_NAME:
                return mongoTemplate.aggregate(aggregation, MasterAsset.class, FilterQueryResult.class);
            case MASTER_PROCESSING_ACTIVITY_MODULE_NAME:
                return mongoTemplate.aggregate(aggregation, MasterProcessingActivity.class, FilterQueryResult.class);
            default:
                throw new DataNotFoundByIdException("data not found by moduleId" + moduleId);

        }
    }
}

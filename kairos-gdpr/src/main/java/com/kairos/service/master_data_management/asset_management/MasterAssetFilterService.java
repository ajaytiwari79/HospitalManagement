package com.kairos.service.master_data_management.asset_management;


import com.kairos.custome_exception.InvalidRequestException;
import com.kairos.dto.FilterSelectionDto;
import com.kairos.persistance.model.enums.FilterType;
import com.kairos.persistance.model.filter.FilterGroup;
import com.kairos.persistance.model.master_data_management.asset_management.MasterAsset;
import com.kairos.persistance.repository.filter.FilterGroupMongoRepository;
import com.kairos.persistance.repository.master_data_management.asset_management.MasterAssetMongoRepository;
import com.kairos.response.dto.filter.FilterQueryResult;
import com.kairos.service.MongoBaseService;
import com.kairos.service.filter.FilterService;
import org.bson.BSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;

import org.springframework.data.mongodb.core.aggregation.*;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.*;

@Service
public class MasterAssetFilterService extends MongoBaseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MasterAssetFilterService.class);


    @Inject
    private MasterAssetMongoRepository masterAssetMongoRepository;

    @Inject
    private FilterGroupMongoRepository filterGroupMongoRepository;

    @Inject
    private FilterService filterService;

    @Inject
    private MongoTemplate mongoTemplate;


    public FilterQueryResult getAllMasterAssetFilter(Long countryId) {
        return masterAssetMongoRepository.getMasterAssetFilter(countryId);

    }


    public FilterQueryResult masterAssetfilterQueryResult(Long countryId, String moduleId, Boolean active) {

        Map<String, AggregationOperation> filterCriteria = new HashMap<>();
        FilterGroup filterGroup = filterGroupMongoRepository.findFilterGroupByModuleId(moduleId, active);
        if (Optional.ofNullable(filterGroup).isPresent()) {
            List<FilterType> filterTypes = filterGroup.getFilterTypes();
            filterCriteria = filterService.getFilterCriterias(countryId, filterTypes);
            Aggregation aggregation = createAggregationQueryForMasterAsset(filterCriteria);
            AggregationResults<FilterQueryResult> result = mongoTemplate.aggregate(aggregation, MasterAsset.class, FilterQueryResult.class);
            return result.getUniqueMappedResult();

        } else
            throw new InvalidRequestException("invalide Request filter roup not exist for moduleId " + moduleId);


    }


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


    public List<MasterAsset> getMasterAssetDataWithFilter(Long countryId, String moduleId, FilterSelectionDto filterSelectionDto) {

        if (checkIfFilterGroupExistForMduleId(moduleId, true)) {
            return masterAssetMongoRepository.getMasterAssetListWithFilterData(countryId, filterSelectionDto);
        } else
            throw new InvalidRequestException("invalide Request filter group not exist for moduleId " + moduleId);
    }


    boolean checkIfFilterGroupExistForMduleId(String moduleId, Boolean active) {

        if (Optional.ofNullable(filterGroupMongoRepository.findFilterGroupByModuleId(moduleId, active)).isPresent()) {
            return true;
        }
        return false;
    }


}

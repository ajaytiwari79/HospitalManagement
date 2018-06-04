package com.kairos.service.master_data_management.asset_management;


import com.kairos.custome_exception.InvalidRequestException;
import com.kairos.dto.FilterSelectionDto;
import com.kairos.persistance.model.enums.FilterType;
import com.kairos.persistance.model.filter.FilterGroup;
import com.kairos.persistance.model.master_data_management.asset_management.MasterAsset;
import com.kairos.persistance.repository.filter.FilterGroupMongoRepository;
import com.kairos.persistance.repository.master_data_management.asset_management.MasterAssetMongoRepository;
import com.kairos.response.dto.filter.FilterAndFavouriteFilterDto;
import com.kairos.response.dto.filter.FilterAttributes;
import com.kairos.response.dto.filter.FilterQueryResult;
import com.kairos.response.dto.filter.FilterResponseDto;
import com.kairos.service.MongoBaseService;
import com.kairos.utils.userContext.UserContext;
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
import java.util.stream.Collectors;

@Service
public class MasterAssetFilterService extends MongoBaseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MasterAssetFilterService.class);


    @Inject
    private MasterAssetMongoRepository masterAssetMongoRepository;

    @Inject
    private FilterGroupMongoRepository filterGroupMongoRepository;

    @Inject
    private MongoTemplate mongoTemplate;


    public FilterQueryResult getAllMasterAssetFilter(Long countryId) {
        return masterAssetMongoRepository.getMasterAssetFilter(countryId);

    }


    public FilterAndFavouriteFilterDto masterAssetfilterQueryResult(String moduleId, Long countryId) {

        Map<String, AggregationOperation> filterCriteria = new HashMap<>();
        FilterGroup filterGroup = filterGroupMongoRepository.findFilterGroupByModuleId(moduleId, countryId);
        List<FilterResponseDto> filterResponseData = new ArrayList<>();
        FilterAndFavouriteFilterDto filterAndFavouriteFilterDto=new FilterAndFavouriteFilterDto();
        if (Optional.ofNullable(filterGroup).isPresent()) {
            List<FilterType> filterTypes = filterGroup.getFilterTypes();
            filterCriteria = filterGroupMongoRepository.getFilterCriterias(countryId, filterTypes);
            Aggregation aggregation = filterGroupMongoRepository.createAggregationQueryForMasterAsset(filterCriteria);
            AggregationResults<FilterQueryResult> result = mongoTemplate.aggregate(aggregation, MasterAsset.class, FilterQueryResult.class);
            FilterQueryResult filterQueryResult = result.getUniqueMappedResult();
            filterResponseData.add(new FilterResponseDto(FilterType.ACCOUNT_TYPES, FilterType.ACCOUNT_TYPES.value, filterQueryResult.getAccountTypes()));
            filterResponseData.add(new FilterResponseDto(FilterType.ORGANIZATION_TYPES, FilterType.ORGANIZATION_TYPES.value, filterQueryResult.getOrganizationTypes()));
            filterResponseData.add(new FilterResponseDto(FilterType.ORGANIZATION_SUB_TYPES, FilterType.ORGANIZATION_SUB_TYPES.value, filterQueryResult.getOrganizationSubTypes()));
            filterResponseData.add(new FilterResponseDto(FilterType.ORGANIZATION_SERVICES, FilterType.ORGANIZATION_SERVICES.value, filterQueryResult.getOrganizationServices()));
            filterResponseData.add(new FilterResponseDto(FilterType.ORGANIZATION_SUB_SERVICES, FilterType.ORGANIZATION_SUB_SERVICES.value, filterQueryResult.getOrganizationSubServices()));
            filterAndFavouriteFilterDto.setAllFilters(filterResponseData);
            filterAndFavouriteFilterDto.setFavouriteFilters(new ArrayList<>());
            return filterAndFavouriteFilterDto;

        } else
            throw new InvalidRequestException("invalide Request filter group not exist for moduleId " + moduleId);


    }



    public List<MasterAsset> getMasterAssetDataWithFilter(Long countryId, String moduleId, FilterSelectionDto filterSelectionDto) {

        if (checkIfFilterGroupExistForMduleId(moduleId, true)) {
            return masterAssetMongoRepository.getMasterAssetListWithFilterData(countryId, filterSelectionDto);
        } else
            throw new InvalidRequestException("invalide Request filter group not exist for moduleId " + moduleId);
    }


    boolean checkIfFilterGroupExistForMduleId(String moduleId, Boolean active) {

        if (Optional.ofNullable(filterGroupMongoRepository.findFilterGroupByModuleId(moduleId, UserContext.getCountryId())).isPresent()) {
            return true;
        }
        return false;
    }


}

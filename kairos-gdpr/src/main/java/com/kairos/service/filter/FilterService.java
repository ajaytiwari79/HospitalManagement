package com.kairos.service.filter;

import com.kairos.custome_exception.InvalidRequestException;
import com.kairos.dto.FilterSelectionDto;
import com.kairos.persistance.model.enums.FilterType;
import com.kairos.persistance.model.filter.FilterGroup;
import com.kairos.persistance.model.master_data_management.asset_management.MasterAsset;
import com.kairos.persistance.repository.filter.FilterGroupMongoRepository;
import com.kairos.persistance.repository.master_data_management.asset_management.MasterAssetMongoRepository;
import com.kairos.response.dto.filter.FilterAndFavouriteFilterDto;
import com.kairos.response.dto.filter.FilterQueryResult;
import com.kairos.response.dto.filter.FilterResponseDto;
import com.kairos.service.master_data_management.asset_management.MasterAssetFilterService;
import com.kairos.utils.userContext.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.*;

@Service
public class FilterService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MasterAssetFilterService.class);


    @Inject
    private FilterGroupMongoRepository filterGroupMongoRepository;

    public FilterAndFavouriteFilterDto metaDatafilters(String moduleId, Long countryId) {

        Map<String, AggregationOperation> filterCriteria = new HashMap<>();
        FilterGroup filterGroup = filterGroupMongoRepository.findFilterGroupByModuleId(moduleId, countryId);
        List<FilterResponseDto> filterResponseData = new ArrayList<>();
        FilterAndFavouriteFilterDto filterAndFavouriteFilterDto = new FilterAndFavouriteFilterDto();
        if (Optional.ofNullable(filterGroup).isPresent()) {
            List<FilterType> filterTypes = filterGroup.getFilterTypes();
            filterCriteria = filterGroupMongoRepository.getFilterCriterias(countryId, filterTypes);
            Aggregation aggregation = filterGroupMongoRepository.createAggregationQueryForMasterAsset(filterCriteria);
            AggregationResults<FilterQueryResult> result = filterGroupMongoRepository.getFilterAggregationResult(aggregation, filterGroup, moduleId);
            FilterQueryResult filterQueryResult = result.getUniqueMappedResult();
            filterTypes.forEach(filterType -> {
                filterResponseData.add(buildFilters(filterQueryResult, filterType));
            });
            filterAndFavouriteFilterDto.setAllFilters(filterResponseData);
            filterAndFavouriteFilterDto.setFavouriteFilters(new ArrayList<>());
            return filterAndFavouriteFilterDto;

        } else
            throw new InvalidRequestException("invalide Request filter group not exist for moduleId " + moduleId);


    }

    boolean checkIfFilterGroupExistForMduleId(String moduleId, Boolean active) {

        if (Optional.ofNullable(filterGroupMongoRepository.findFilterGroupByModuleId(moduleId, UserContext.getCountryId())).isPresent()) {
            return true;
        }
        return false;
    }


    public FilterResponseDto buildFilters(FilterQueryResult filterQueryResult, FilterType filterType) {
        switch (filterType) {
            case ACCOUNT_TYPES:
                return new FilterResponseDto(filterType, filterType.value, filterQueryResult.getAccountTypes());
            case ORGANIZATION_TYPES:
                return new FilterResponseDto(filterType, filterType.value, filterQueryResult.getOrganizationTypes());
            case ORGANIZATION_SUB_TYPES:
                return new FilterResponseDto(filterType, filterType.value, filterQueryResult.getOrganizationSubTypes());
            case ORGANIZATION_SERVICES:
                return new FilterResponseDto(filterType, filterType.value, filterQueryResult.getOrganizationServices());
            case ORGANIZATION_SUB_SERVICES:
                return new FilterResponseDto(filterType, filterType.value, filterQueryResult.getOrganizationSubServices());
            default:
                throw new InvalidRequestException("invalid request");

        }


    }


   /* public AggregationResults<FilterQueryResult> getFilterAggregationResult(FilterGroup filterGroup, String moduleId) {

        List<ModuleIdDto> moduleIdDto = filterGroup.getAccessModule();
        String domainName;
        for (ModuleIdDto moduleIdDto1 : moduleIdDto) {

            if (moduleIdDto1.getModuleId().equalsIgnoreCase(moduleId)) {
                domainName = moduleIdDto1.getName();
            }

        }

        switch (domainName.contains()) {


        }


    }
*/
}

package com.kairos.service.filter;

import com.kairos.commons.custom_exception.DataNotFoundByIdException;
import com.kairos.commons.custom_exception.InvalidRequestException;
import com.kairos.dto.gdpr.FilterSelectionDTO;
import com.kairos.dto.gdpr.master_data.ModuleIdDTO;
import com.kairos.enums.gdpr.FilterType;
import com.kairos.persistence.model.filter.FilterGroup;
import com.kairos.response.dto.filter.FilterAndFavouriteFilterDTO;
import com.kairos.response.dto.filter.FilterCategoryResult;
import com.kairos.response.dto.filter.FilterResponseDTO;
import com.kairos.utils.FilterResponseWithData;
import com.kairos.service.exception.ExceptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;


@Service
public class FilterService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FilterService.class);

    /*@Inject
    private FilterMongoRepository filterMongoRepository;*/

    @Inject
    private ExceptionService exceptionService;



    //get fields with distinct values on which filter is applicable
    public FilterAndFavouriteFilterDTO getFilterCategories(Long countryId,  String moduleId) {
//TODO
     /*   Map<String, AggregationOperation> filterCriteria = null;
        FilterGroup filterGroup = filterMongoRepository.findFilterGroupByModuleId(moduleId, countryId);
        List<FilterResponseDTO> filterResponseData = new ArrayList<>();
        FilterAndFavouriteFilterDTO filterAndFavouriteFilterDto = new FilterAndFavouriteFilterDTO();
        if (Optional.ofNullable(filterGroup).isPresent()) {
            List<FilterType> filterTypes = filterGroup.getFilterTypes();
            filterCriteria = filterMongoRepository.getDefaultFilters(countryId, filterTypes, filterGroup);
            Aggregation aggregation = filterMongoRepository.createAggregationQueryForFilterCategory(filterCriteria);
            AggregationResults<FilterCategoryResult> result = filterMongoRepository.getFilterAggregationResult(aggregation, filterGroup, moduleId);
            FilterCategoryResult filterQueryResult = result.getUniqueMappedResult();

            if (Optional.ofNullable(filterQueryResult).isPresent()) {
                filterTypes.forEach(filterType -> filterResponseData.add(buildFiltersCategoryResponse(filterQueryResult, filterType)));
                filterAndFavouriteFilterDto.setAllFilters(filterResponseData);
                filterAndFavouriteFilterDto.setFavouriteFilters(new ArrayList<>());
            } else {
                filterAndFavouriteFilterDto.setAllFilters(filterResponseData);
                filterAndFavouriteFilterDto.setFavouriteFilters(new ArrayList<>());

            }
            return filterAndFavouriteFilterDto;

        } else
            throw new InvalidRequestException("invalid Request filter group not exist for moduleId " + moduleId);*/

return  new FilterAndFavouriteFilterDTO();
    }


    //build filter Category For asset ,clause and processing activity (response is to give different values of filter criteria)
    public FilterResponseDTO buildFiltersCategoryResponse(FilterCategoryResult filterQueryResult, FilterType filterType) {
        switch (filterType) {
            case ACCOUNT_TYPES:
                return new FilterResponseDTO(filterType, filterType.value,  filterQueryResult.getAccountTypes());
            case ORGANIZATION_TYPES:
                return new FilterResponseDTO(filterType, filterType.value, filterQueryResult.getOrganizationTypes());
            case ORGANIZATION_SUB_TYPES:
                return new FilterResponseDTO(filterType,filterType.value, filterQueryResult.getOrganizationSubTypes());
            case ORGANIZATION_SERVICES:
                return new FilterResponseDTO(filterType,filterType.value, filterQueryResult.getOrganizationServices());
            case ORGANIZATION_SUB_SERVICES:
                return new FilterResponseDTO(filterType,filterType.value,  filterQueryResult.getOrganizationSubServices());
            default:
                throw new InvalidRequestException("invalid request");
        }
    }


    //get filter data on the bases of selection of data and get filter Group By moduleId
    public FilterResponseWithData getFilterDataWithFilterSelection(Long countryId, String moduleId, FilterSelectionDTO filterSelectionDto) {
        FilterGroup filterGroup = null;
       /* FilterGroup filterGroup = filterMongoRepository.findFilterGroupByModuleId(moduleId, countryId);
        if (!Optional.ofNullable(filterGroup).isPresent()) {
            exceptionService.invalidRequestException("filter group not exists for " + moduleId);
        }*/
        String domainName = null;
        if (filterGroup.getAccessModule().size() != 0) {
            for (ModuleIdDTO moduleIdDto1 : filterGroup.getAccessModule()) {
                if (moduleIdDto1.getModuleId().equalsIgnoreCase(moduleId)) {
                    domainName = moduleIdDto1.getName();
                    break;
                }
            }
        }
        return getFilterDataByModuleName(countryId, domainName, filterSelectionDto);

    }


    public FilterResponseWithData getFilterDataByModuleName(Long countryId,  String moduleName, FilterSelectionDTO filterSelectionDto) {

        switch (moduleName) {
            //TODO
      /*      case CLAUSE_MODULE_NAME:
                List<ClauseResponseDTO> clauses = clauseMongoRepository.getClauseDataWithFilterSelection(countryId, filterSelectionDto);
                FilterResponseWithData<List<ClauseResponseDTO>> clauseFilterData = new FilterResponseWithData<>();
                clauseFilterData.setData(clauses);
                return clauseFilterData;
            case ASSET_MODULE_NAME:
                List<MasterAssetResponseDTO> masterAssets = masterAssetMongoRepository.getMasterAssetDataWithFilterSelection(countryId, filterSelectionDto);
                FilterResponseWithData<List<MasterAssetResponseDTO>> assetFilterData = new FilterResponseWithData<>();
                assetFilterData.setData(masterAssets);
                return assetFilterData;
            case MASTER_PROCESSING_ACTIVITY_MODULE_NAME:
                List<MasterProcessingActivityResponseDTO> processingActivities = masterProcessingActivityRepository.getMasterProcessingActivityWithFilterSelection(countryId, filterSelectionDto);
                FilterResponseWithData<List<MasterProcessingActivityResponseDTO>> processingActivityFilterData = new FilterResponseWithData<>();
                processingActivityFilterData.setData(processingActivities);
                return processingActivityFilterData;*/
            default:
                throw new DataNotFoundByIdException("data not found by moduleName " + moduleName);
        }
    }


}


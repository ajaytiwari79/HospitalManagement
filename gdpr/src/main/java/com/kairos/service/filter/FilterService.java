package com.kairos.service.filter;

import com.kairos.util.ObjectMapperUtils;
import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.InvalidRequestException;
import com.kairos.dto.FilterSelectionDTO;
import com.kairos.dto.master_data.ModuleIdDTO;
import com.kairos.persistance.model.clause.Clause;
import com.kairos.enums.FilterType;
import com.kairos.persistance.model.filter.FilterGroup;
import com.kairos.persistance.model.master_data.default_asset_setting.MasterAsset;
import com.kairos.persistance.model.master_data.default_proc_activity_setting.MasterProcessingActivity;
import com.kairos.persistance.repository.clause.ClauseMongoRepository;
import com.kairos.persistance.repository.filter.FilterMongoRepository;
import com.kairos.persistance.repository.master_data.asset_management.MasterAssetMongoRepository;
import com.kairos.persistance.repository.master_data.processing_activity_masterdata.MasterProcessingActivityRepository;
import com.kairos.response.dto.clause.ClauseResponseDTO;
import com.kairos.response.dto.master_data.MasterAssetResponseDTO;
import com.kairos.response.dto.master_data.MasterProcessingActivityResponseDTO;
import com.kairos.response.dto.filter.FilterAndFavouriteFilterDTO;
import com.kairos.response.dto.filter.FilterQueryResult;
import com.kairos.response.dto.filter.FilterResponseDTO;
import com.kairos.utils.FilterResponseWithData;
import com.kairos.service.exception.ExceptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.*;

import static com.kairos.constants.AppConstant.CLAUSE_MODULE_NAME;
import static com.kairos.constants.AppConstant.ASSET_MODULE_NAME;
import static com.kairos.constants.AppConstant.MASTER_PROCESSING_ACTIVITY_MODULE_NAME;

@Service
public class FilterService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FilterService.class);

    @Inject
    private FilterMongoRepository filterMongoRepository;

    @Inject
    private ExceptionService exceptionService;


    @Inject
    private ClauseMongoRepository clauseMongoRepository;

    @Inject
    private MasterAssetMongoRepository masterAssetMongoRepository;

    @Inject
    private MasterProcessingActivityRepository masterProcessingActivityRepository;

    //get fields with distinct values on which filter is applicable
    public FilterAndFavouriteFilterDTO getFilterCategories(Long countryId, Long organizationId, String moduleId) {

        Map<String, AggregationOperation> filterCriteria = new HashMap<>();
        FilterGroup filterGroup = filterMongoRepository.findFilterGroupByModuleId(moduleId, countryId);
        List<FilterResponseDTO> filterResponseData = new ArrayList<>();
        FilterAndFavouriteFilterDTO filterAndFavouriteFilterDto = new FilterAndFavouriteFilterDTO();
        if (Optional.ofNullable(filterGroup).isPresent()) {
            List<FilterType> filterTypes = filterGroup.getFilterTypes();
            filterCriteria = filterMongoRepository.getFilterCriteria(countryId, organizationId, filterTypes);
            Aggregation aggregation = filterMongoRepository.createAggregationQueryForMasterAsset(filterCriteria);
            AggregationResults<FilterQueryResult> result = filterMongoRepository.getFilterAggregationResult(aggregation, filterGroup, moduleId);
            FilterQueryResult filterQueryResult = result.getUniqueMappedResult();

            if (Optional.ofNullable(filterQueryResult).isPresent()) {
                filterTypes.forEach(filterType -> {
                    filterResponseData.add(buildFiltersCategoryResponse(filterQueryResult, filterType));
                });
                filterAndFavouriteFilterDto.setAllFilters(filterResponseData);
                filterAndFavouriteFilterDto.setFavouriteFilters(new ArrayList<>());
            } else {
                filterAndFavouriteFilterDto.setAllFilters(filterResponseData);
                filterAndFavouriteFilterDto.setFavouriteFilters(new ArrayList<>());

            }
            return filterAndFavouriteFilterDto;

        } else
            throw new InvalidRequestException("invalid Request filter group not exist for moduleId " + moduleId);


    }


    //build filter Category For asset ,clause and processing activity (response is to give different values of filter criteria)
    public FilterResponseDTO buildFiltersCategoryResponse(FilterQueryResult filterQueryResult, FilterType filterType) {
        switch (filterType) {
            case ACCOUNT_TYPES:
                return new FilterResponseDTO(filterType, filterType.value, "Account Types", filterQueryResult.getAccountTypes());
            case ORGANIZATION_TYPES:
                return new FilterResponseDTO(filterType, filterType.value, "Organization Types", filterQueryResult.getOrganizationTypes());
            case ORGANIZATION_SUB_TYPES:
                return new FilterResponseDTO(filterType, filterType.value, "Organization Sub Types", filterQueryResult.getOrganizationSubTypes());
            case ORGANIZATION_SERVICES:
                return new FilterResponseDTO(filterType, filterType.value, "Service Types", filterQueryResult.getOrganizationServices());
            case ORGANIZATION_SUB_SERVICES:
                return new FilterResponseDTO(filterType, filterType.value, "Service Sub Types", filterQueryResult.getOrganizationSubServices());
            default:
                throw new InvalidRequestException("invalid request");
        }
    }


    //get filter data on the bases of selection of data and get filter Group By moduleId
    public FilterResponseWithData getFilterDataWithFilterSelection(Long countryId, Long organizationId, String moduleId, FilterSelectionDTO filterSelectionDto) {
        FilterGroup filterGroup = filterMongoRepository.findFilterGroupByModuleId(moduleId, countryId);
        if (!Optional.ofNullable(filterGroup).isPresent()) {
            exceptionService.invalidRequestException("filter group not exists for " + moduleId);
        }
        String domainName = null;
        if (filterGroup.getAccessModule().size() != 0) {
            for (ModuleIdDTO moduleIdDto1 : filterGroup.getAccessModule()) {
                if (moduleIdDto1.getModuleId().equalsIgnoreCase(moduleId)) {
                    domainName = moduleIdDto1.getName();
                    break;
                }
            }
        }
        return getFilterDataByModuleName(countryId, organizationId, domainName, filterSelectionDto);

    }


    //Wrap filter data response on the basic and module id and filter selection
    public FilterResponseWithData getFilterDataByModuleName(Long countryId, Long organizationId, String moduleName, FilterSelectionDTO filterSelectionDto) {

        switch (moduleName) {
            case CLAUSE_MODULE_NAME:
                List<Clause> clauses = clauseMongoRepository.getClauseDataWithFilterSelection(countryId, organizationId, filterSelectionDto);
                List<ClauseResponseDTO> clauseResponseDTOs = ObjectMapperUtils.copyPropertiesOfListByMapper(clauses, ClauseResponseDTO.class);
                FilterResponseWithData<List<ClauseResponseDTO>> clauseFilterData = new FilterResponseWithData<>();
                clauseFilterData.setData(clauseResponseDTOs);
                return clauseFilterData;
            case ASSET_MODULE_NAME:
                List<MasterAsset> masterAssets = masterAssetMongoRepository.getMasterAssetDataWithFilterSelection(countryId, organizationId, filterSelectionDto);
                List<MasterAssetResponseDTO> masterAssetResponseDTOs = ObjectMapperUtils.copyPropertiesOfListByMapper(masterAssets, MasterAssetResponseDTO.class);
                FilterResponseWithData<List<MasterAssetResponseDTO>> assetFilterData = new FilterResponseWithData<>();
                assetFilterData.setData(masterAssetResponseDTOs);
                return assetFilterData;
            case MASTER_PROCESSING_ACTIVITY_MODULE_NAME:
                List<MasterProcessingActivity> processingActivities = masterProcessingActivityRepository.getMasterProcessingActivityWithFilterSelection(countryId, organizationId, filterSelectionDto);
                List<MasterProcessingActivityResponseDTO> processingActivityResponseDTOs = ObjectMapperUtils.copyPropertiesOfListByMapper(processingActivities, MasterProcessingActivityResponseDTO.class);
                FilterResponseWithData<List<MasterProcessingActivityResponseDTO>> processingActivityFilterData = new FilterResponseWithData<>();
                processingActivityFilterData.setData(processingActivityResponseDTOs);
                return processingActivityFilterData;
            default:
                throw new DataNotFoundByIdException("data not found by moduleName " + moduleName);
        }
    }


}


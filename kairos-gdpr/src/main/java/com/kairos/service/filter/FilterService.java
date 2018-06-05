package com.kairos.service.filter;

import com.kairos.activity.util.ObjectMapperUtils;
import com.kairos.custome_exception.DataNotFoundByIdException;
import com.kairos.custome_exception.InvalidRequestException;
import com.kairos.dto.FilterSelectionDto;
import com.kairos.dto.MasterAssetDto;
import com.kairos.dto.ModuleIdDto;
import com.kairos.persistance.model.clause.Clause;
import com.kairos.persistance.model.enums.FilterType;
import com.kairos.persistance.model.filter.FilterGroup;
import com.kairos.persistance.model.master_data_management.asset_management.MasterAsset;
import com.kairos.persistance.model.master_data_management.processing_activity_masterdata.MasterProcessingActivity;
import com.kairos.persistance.model.processing_activity.ProcessingActivity;
import com.kairos.persistance.repository.clause.ClauseMongoRepository;
import com.kairos.persistance.repository.filter.FilterMongoRepository;
import com.kairos.persistance.repository.master_data_management.asset_management.MasterAssetMongoRepository;
import com.kairos.persistance.repository.master_data_management.processing_activity_masterdata.MasterProcessingActivityRepository;
import com.kairos.persistance.repository.processing_activity.ProcessingActivityMongoRepository;
import com.kairos.response.dto.ClauseResponseDto;
import com.kairos.response.dto.MasterAssetResponseDto;
import com.kairos.response.dto.MasterProcessingActivityResponseDto;
import com.kairos.response.dto.filter.FilterAndFavouriteFilterDto;
import com.kairos.response.dto.filter.FilterQueryResult;
import com.kairos.response.dto.filter.FilterResponseDto;
import com.kairos.response.dto.filter.FilterResponseWithData;
import com.kairos.service.exception.ExceptionService;
import com.kairos.utils.userContext.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.*;

import static com.kairos.constant.AppConstant.CLAUSE_MODULE_NAME;
import static com.kairos.constant.AppConstant.ASSET_MODULE_NAME;
import static com.kairos.constant.AppConstant.MASTER_PROCESSING_ACTIVITY_NAME;

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


    public FilterAndFavouriteFilterDto getFilterCategories(Long countryId, String moduleId) {

        Map<String, AggregationOperation> filterCriteria = new HashMap<>();
        FilterGroup filterGroup = filterMongoRepository.findFilterGroupByModuleId(moduleId, countryId);
        List<FilterResponseDto> filterResponseData = new ArrayList<>();
        FilterAndFavouriteFilterDto filterAndFavouriteFilterDto = new FilterAndFavouriteFilterDto();
        if (Optional.ofNullable(filterGroup).isPresent()) {
            List<FilterType> filterTypes = filterGroup.getFilterTypes();
            filterCriteria = filterMongoRepository.getFilterCriterias(countryId, filterTypes);
            Aggregation aggregation = filterMongoRepository.createAggregationQueryForMasterAsset(filterCriteria);
            AggregationResults<FilterQueryResult> result = filterMongoRepository.getFilterAggregationResult(aggregation, filterGroup, moduleId);
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


    boolean checkIfFilterGroupExistForMduleId(String moduleId, Boolean active) {

        if (Optional.ofNullable(filterMongoRepository.findFilterGroupByModuleId(moduleId, UserContext.getCountryId())).isPresent()) {
            return true;
        }
        return false;
    }


    public FilterResponseWithData getFilterDataWithFilterSelection(Long countryId, String moduleId, FilterSelectionDto filterSelectionDto) {
        FilterGroup filterGroup = filterMongoRepository.findFilterGroupByModuleId(moduleId, countryId);
        if (!Optional.ofNullable(filterGroup).isPresent()) {
            exceptionService.invalidRequestException("filter group not exists for " + moduleId);
        }
        String domainName = null;
        if (filterGroup.getAccessModule().size() != 0) {
            for (ModuleIdDto moduleIdDto1 : filterGroup.getAccessModule()) {
                if (moduleIdDto1.getModuleId().equalsIgnoreCase(moduleId)) {
                    domainName = moduleIdDto1.getName();
                    break;
                }
            }
        }
        return getFilterDataByModuleName(countryId, domainName, filterSelectionDto);

    }


    public FilterResponseWithData getFilterDataByModuleName(Long countryId, String moduleName, FilterSelectionDto filterSelectionDto) {

        switch (moduleName) {
            case CLAUSE_MODULE_NAME:
                List<Clause> clauses = clauseMongoRepository.getClauseDataWithFilterSelection(countryId, filterSelectionDto);
                List<ClauseResponseDto> clauseResponseDtos = ObjectMapperUtils.copyPropertiesOfListByMapper(clauses, ClauseResponseDto.class);
                FilterResponseWithData<List<ClauseResponseDto>> clauseFilterData = new FilterResponseWithData<>();
                clauseFilterData.setData(clauseResponseDtos);
                return clauseFilterData;
            case ASSET_MODULE_NAME:
                List<MasterAsset> masterAssets = masterAssetMongoRepository.getMasterAssetDataWithFilterSelection(countryId, filterSelectionDto);
                List<MasterAssetResponseDto> masterAssetResponseDtos = ObjectMapperUtils.copyPropertiesOfListByMapper(masterAssets, MasterAssetResponseDto.class);
                FilterResponseWithData<List<MasterAssetResponseDto>> assetfilterData = new FilterResponseWithData<>();
                assetfilterData.setData(masterAssetResponseDtos);
                return assetfilterData;
            case MASTER_PROCESSING_ACTIVITY_NAME:
                List<MasterProcessingActivity> processingActivities = masterProcessingActivityRepository.getMasterProcessingActivityWithFilterSelection(countryId, filterSelectionDto);
                List<MasterProcessingActivityResponseDto> processingActivityResponseDtos = ObjectMapperUtils.copyPropertiesOfListByMapper(processingActivities, MasterProcessingActivityResponseDto.class);
                FilterResponseWithData<List<MasterProcessingActivityResponseDto>> processingActivityFilterData = new FilterResponseWithData<>();
                processingActivityFilterData.setData(processingActivityResponseDtos);
                return processingActivityFilterData;
            default:
                throw new DataNotFoundByIdException("data not found by moduleName " + moduleName);
        }
    }


}


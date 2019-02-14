package com.kairos.service.master_data.processing_activity_masterdata;


import com.kairos.commons.custom_exception.DataNotFoundByIdException;
import com.kairos.commons.custom_exception.DuplicateDataException;
import com.kairos.enums.gdpr.SuggestedDataStatus;
import com.kairos.dto.gdpr.metadata.DataSourceDTO;
import com.kairos.persistence.model.master_data.default_proc_activity_setting.DataSource;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.data_source.DataSourceRepository;
import com.kairos.response.dto.common.DataSourceResponseDTO;
import com.kairos.service.exception.ExceptionService;
import com.kairos.utils.ComparisonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.constants.AppConstant.EXISTING_DATA_LIST;
import static com.kairos.constants.AppConstant.NEW_DATA_LIST;


@Service
public class DataSourceService{

    private static final Logger LOGGER = LoggerFactory.getLogger(DataSourceService.class);

    @Inject
    private ExceptionService exceptionService;

    @Inject
    private DataSourceRepository dataSourceRepository;


    /**
     * @param countryId
     * @param dataSources
     * @return return map which contain list of new DataSource and list of existing DataSource if DataSource already exist
     * @description this method create new DataSource if DataSource not exist with same name ,
     * and if exist then simply add  DataSource to existing list and return list ;
     * findMetaDataByNamesAndCountryId()  return list of existing DataSource using collation ,used for case insensitive result
     */
    public Map<String, List<DataSource>> createDataSource(Long countryId, List<DataSourceDTO> dataSources , boolean isSuggestion) {
        //TODO still need to optimize we can get name of list in string from here
        Map<String, List<DataSource>> result = new HashMap<>();
        Set<String> dataSourceNames = new HashSet<>();
            for (DataSourceDTO dataSource : dataSources) {
                dataSourceNames.add(dataSource.getName());
            }
            List<String> nameInLowerCase = dataSourceNames.stream().map(String::toLowerCase)
                    .collect(Collectors.toList());
            //TODO still need to update we can return name of list from here and can apply removeAll on list
            List<DataSource> existing = dataSourceRepository.findByCountryIdAndDeletedAndNameIn(countryId,  nameInLowerCase);
            dataSourceNames = ComparisonUtils.getNameListForMetadata(existing, dataSourceNames);

            List<DataSource> newDataSources = new ArrayList<>();
            if (!dataSourceNames.isEmpty()) {
                for (String name : dataSourceNames) {
                    DataSource newDataSource = new DataSource(name,countryId);
                if(isSuggestion){
                    newDataSource.setSuggestedDataStatus(SuggestedDataStatus.PENDING);
                    newDataSource.setSuggestedDate(LocalDate.now());
                }else{
                    newDataSource.setSuggestedDataStatus(SuggestedDataStatus.APPROVED);
                }
                    newDataSources.add(newDataSource);

                }
                newDataSources = dataSourceRepository.saveAll(newDataSources);
            }
            result.put(EXISTING_DATA_LIST, existing);
            result.put(NEW_DATA_LIST, newDataSources);
            return result;
    }

    /**
     * @param countryId
     * @return list of DataSource
     */
    public List<DataSourceResponseDTO> getAllDataSource(Long countryId) {
        return dataSourceRepository.findAllByCountryIdAndSortByCreatedDate(countryId);

    }

    /**
     * @param countryId
     * @return DataSource object fetch by given id
     * @throws DataNotFoundByIdException throw exception if DataSource not found for given id
     */
    public DataSource getDataSource(Long countryId, Long id) {
        DataSource exist = dataSourceRepository.findByIdAndCountryIdAndDeletedFalse(id, countryId);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("No data found");
        } else {
            return exist;

        }
    }


    public Boolean deleteDataSource(Long countryId, Long id) {
        Integer resultCount = dataSourceRepository.deleteByIdAndCountryId(id, countryId);
        if (resultCount > 0) {
            LOGGER.info("Data Source deleted successfully for id :: {}", id);
        }else{
            throw new DataNotFoundByIdException("No data found");
        }
        return true;
    }

    /***
     * @throws DuplicateDataException throw exception if DataSource data not exist for given id
     * @param countryId
     * @param id id of DataSource
     * @param dataSourceDTO
     * @return DataSource updated object
     */
    public DataSourceDTO updateDataSource(Long countryId, Long id, DataSourceDTO dataSourceDTO) {

        DataSource dataSource = dataSourceRepository.findByCountryIdAndName(countryId,  dataSourceDTO.getName());
        if (Optional.ofNullable(dataSource).isPresent()) {
            if (id.equals(dataSource.getId())) {
                return dataSourceDTO;
            }
            throw new DuplicateDataException("data  exist for  " + dataSourceDTO.getName());
        }
        Integer resultCount =  dataSourceRepository.updateMasterMetadataName(dataSourceDTO.getName(), id, countryId);
        if(resultCount <=0){
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Data Source", id);
        }else{
            LOGGER.info("Data updated successfully for id : {} and name updated name is : {}", id, dataSourceDTO.getName());
        }
        return dataSourceDTO;

    }


    /**
     * @description method save Data Source suggested by unit
     * @param countryId
     * @param dataSourceDTOS
     * @return
     */
    public List<DataSource> saveSuggestedDataSourcesFromUnit(Long countryId, List<DataSourceDTO> dataSourceDTOS) {

        Map<String, List<DataSource>> result = createDataSource(countryId, dataSourceDTOS, true);
        return result.get(NEW_DATA_LIST);
    }


    /**
     *
     * @param countryId
     * @param dataSourceIds
     * @param suggestedDataStatus
     * @return
     */
    public List<DataSource> updateSuggestedStatusOfDataSourceList(Long countryId, Set<Long> dataSourceIds, SuggestedDataStatus suggestedDataStatus) {

        Integer updateCount = dataSourceRepository.updateMetadataStatus(countryId, dataSourceIds,suggestedDataStatus);
        if(updateCount > 0){
            LOGGER.info("Data Sources are updated successfully with ids :: {}", dataSourceIds);
        }else{
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Data Source", dataSourceIds);
        }
        return dataSourceRepository.findAllByIds(dataSourceIds);
    }


}

    
    
    


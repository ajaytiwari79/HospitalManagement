package com.kairos.service.master_data.processing_activity_masterdata;


import com.kairos.commons.custom_exception.DataNotFoundByIdException;
import com.kairos.commons.custom_exception.DuplicateDataException;
import com.kairos.commons.custom_exception.InvalidRequestException;
import com.kairos.enums.gdpr.SuggestedDataStatus;
import com.kairos.dto.gdpr.metadata.DataSourceDTO;
import com.kairos.persistence.model.master_data.default_proc_activity_setting.DataSource;
import com.kairos.persistence.model.master_data.default_proc_activity_setting.DataSourceMD;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.data_source.DataSourceMongoRepository;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.data_source.DataSourceRepository;
import com.kairos.response.dto.common.DataSourceResponseDTO;
import com.kairos.service.common.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.utils.ComparisonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.constants.AppConstant.EXISTING_DATA_LIST;
import static com.kairos.constants.AppConstant.NEW_DATA_LIST;


@Service
public class DataSourceService extends MongoBaseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataSourceService.class);

    @Inject
    private DataSourceMongoRepository dataSourceMongoRepository;

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
    public Map<String, List<DataSourceMD>> createDataSource(Long countryId, List<DataSourceDTO> dataSources) {
        //TODO still need to optimize we can get name of list in string from here
        Map<String, List<DataSourceMD>> result = new HashMap<>();
        Set<String> dataSourceNames = new HashSet<>();
        if (!dataSources.isEmpty()) {
            for (DataSourceDTO dataSource : dataSources) {
                dataSourceNames.add(dataSource.getName());
            }
            List<String> nameInLowerCase = dataSourceNames.stream().map(String::toLowerCase)
                    .collect(Collectors.toList());
            //TODO still need to update we can return name of list from here and can apply removeAll on list
            List<DataSourceMD> existing = dataSourceRepository.findByCountryIdAndDeletedAndNameIn(countryId, false, nameInLowerCase);
            dataSourceNames = ComparisonUtils.getNameListForMetadata(existing, dataSourceNames);

            List<DataSourceMD> newDataSources = new ArrayList<>();
            if (!dataSourceNames.isEmpty()) {
                for (String name : dataSourceNames) {
                    DataSourceMD newDataSource = new DataSourceMD(name,countryId,SuggestedDataStatus.APPROVED);
                    newDataSources.add(newDataSource);

                }
                newDataSources = dataSourceRepository.saveAll(newDataSources);
            }
            result.put(EXISTING_DATA_LIST, existing);
            result.put(NEW_DATA_LIST, newDataSources);
            return result;
        } else
            throw new InvalidRequestException("list cannot be empty");


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
    public DataSourceMD getDataSource(Long countryId, Long id) {
        DataSourceMD exist = dataSourceRepository.findByIdAndCountryIdAndDeleted(id, countryId, false);
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

        DataSourceMD dataSource = dataSourceRepository.findByNameAndCountryId( dataSourceDTO.getName(), countryId);
        if (Optional.ofNullable(dataSource).isPresent()) {
            if (id.equals(dataSource.getId())) {
                return dataSourceDTO;
            }
            throw new DuplicateDataException("data  exist for  " + dataSourceDTO.getName());
        }
        Integer resultCount =  dataSourceRepository.updateDataSourceName(dataSourceDTO.getName(), id);
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

        Set<String> dataSourceNameList = new HashSet<>();
        for (DataSourceDTO DataSource : dataSourceDTOS) {
            dataSourceNameList.add(DataSource.getName());
        }
        List<DataSource> existingDataSources = findMetaDataByNamesAndCountryId(countryId, dataSourceNameList, DataSource.class);
        dataSourceNameList = ComparisonUtils.getNameListForMetadata(existingDataSources, dataSourceNameList);
        List<DataSource> dataSourceList = new ArrayList<>();
        if (!dataSourceNameList.isEmpty()) {
            for (String name : dataSourceNameList) {

                DataSource dataSource = new DataSource(name);
                dataSource.setCountryId(countryId);
                dataSource.setSuggestedDataStatus(SuggestedDataStatus.PENDING);
                dataSource.setSuggestedDate(LocalDate.now());
                dataSourceList.add(dataSource);
            }

            dataSourceMongoRepository.saveAll(getNextSequence(dataSourceList));
        }
        return dataSourceList;
    }


    /**
     *
     * @param countryId
     * @param dataSourceIds
     * @param suggestedDataStatus
     * @return
     */
    public List<DataSourceMD> updateSuggestedStatusOfDataSourceList(Long countryId, Set<Long> dataSourceIds, SuggestedDataStatus suggestedDataStatus) {

        Integer updateCount = dataSourceRepository.updateDataSourceStatus(countryId, dataSourceIds,suggestedDataStatus);
        if(updateCount > 0){
            LOGGER.info("Data Sources are updated successfully with ids :: {}", dataSourceIds);
        }else{
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Data Source", dataSourceIds);
        }
        return dataSourceRepository.findAllByIds(dataSourceIds);
    }


}

    
    
    


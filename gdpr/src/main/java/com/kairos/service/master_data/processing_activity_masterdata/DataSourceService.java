package com.kairos.service.master_data.processing_activity_masterdata;


import com.kairos.custom_exception.DataNotExists;
import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.custom_exception.InvalidRequestException;
import com.kairos.enums.SuggestedDataStatus;
import com.kairos.gdpr.metadata.DataSourceDTO;
import com.kairos.persistance.model.master_data.default_proc_activity_setting.DataSource;
import com.kairos.persistance.repository.master_data.processing_activity_masterdata.data_source.DataSourceMongoRepository;
import com.kairos.response.dto.common.DataSourceResponseDTO;
import com.kairos.service.common.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.utils.ComparisonUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;

import static com.kairos.constants.AppConstant.EXISTING_DATA_LIST;
import static com.kairos.constants.AppConstant.NEW_DATA_LIST;


@Service
public class DataSourceService extends MongoBaseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataSourceService.class);

    @Inject
    private DataSourceMongoRepository dataSourceMongoRepository;

    @Inject
    private ExceptionService exceptionService;


    /**
     * @param countryId
     * @param dataSources
     * @return return map which contain list of new DataSource and list of existing DataSource if DataSource already exist
     * @description this method create new DataSource if DataSource not exist with same name ,
     * and if exist then simply add  DataSource to existing list and return list ;
     * findMetaDataByNamesAndCountryId()  return list of existing DataSource using collation ,used for case insensitive result
     */
    public Map<String, List<DataSource>> createDataSource(Long countryId, List<DataSourceDTO> dataSources) {

        Map<String, List<DataSource>> result = new HashMap<>();
        Set<String> dataSourceNames = new HashSet<>();
        if (!dataSources.isEmpty()) {
            for (DataSourceDTO dataSource : dataSources) {
                dataSourceNames.add(dataSource.getName());
            }
            List<DataSource> existing = findMetaDataByNamesAndCountryId(countryId, dataSourceNames, DataSource.class);
            dataSourceNames = ComparisonUtils.getNameListForMetadata(existing, dataSourceNames);

            List<DataSource> newDataSources = new ArrayList<>();
            if (!dataSourceNames.isEmpty()) {
                for (String name : dataSourceNames) {

                    DataSource newDataSource = new DataSource(name,countryId,SuggestedDataStatus.APPROVED);
                    newDataSources.add(newDataSource);

                }

                newDataSources = dataSourceMongoRepository.saveAll(getNextSequence(newDataSources));
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
        return dataSourceMongoRepository.findAllDataSources(countryId);

    }

    /**
     * @param countryId
     * @return DataSource object fetch by given id
     * @throws DataNotFoundByIdException throw exception if DataSource not found for given id
     */
    public DataSource getDataSource(Long countryId, BigInteger id) {

        DataSource exist = dataSourceMongoRepository.findByIdAndNonDeleted(countryId, id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            return exist;

        }
    }


    public Boolean deleteDataSource(Long countryId, BigInteger id) {

        DataSource dataSource = dataSourceMongoRepository.findByIdAndNonDeleted(countryId, id);
        if (!Optional.ofNullable(dataSource).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        }
        delete(dataSource);
        return true;

    }

    /***
     * @throws DuplicateDataException throw exception if DataSource data not exist for given id
     * @param countryId
     * @param id id of DataSource
     * @param dataSourceDTO
     * @return DataSource updated object
     */
    public DataSourceDTO updateDataSource(Long countryId, BigInteger id, DataSourceDTO dataSourceDTO) {

        DataSource dataSource = dataSourceMongoRepository.findByNameAndCountryId(countryId, dataSourceDTO.getName());
        if (Optional.ofNullable(dataSource).isPresent()) {
            if (id.equals(dataSource.getId())) {
                return dataSourceDTO;
            }
            throw new DuplicateDataException("data  exist for  " + dataSourceDTO.getName());
        }
        dataSource = dataSourceMongoRepository.findByid(id);
        if (!Optional.ofNullable(dataSource).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Data Source", id);
        }
        dataSource.setName(dataSourceDTO.getName());
        dataSourceMongoRepository.save(dataSource);
        return dataSourceDTO;

    }

    /**
     * @param countryId
     * @param name      name of DataSource
     * @return DataSource object fetch on basis of  name
     * @throws DataNotExists throw exception if DataSource not exist for given name
     */
    public DataSource getDataSourceByName(Long countryId, String name) {


        if (!StringUtils.isBlank(name)) {
            DataSource exist = dataSourceMongoRepository.findByNameAndCountryId(countryId, name);
            if (!Optional.ofNullable(exist).isPresent()) {
                throw new DataNotExists("data not exist for name " + name);
            }
            return exist;
        } else
            throw new InvalidRequestException("request param cannot be empty  or null");

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
    public List<DataSource> updateSuggestedStatusOfDataSourceList(Long countryId, Set<BigInteger> dataSourceIds, SuggestedDataStatus suggestedDataStatus) {

        List<DataSource> dataSourceList = dataSourceMongoRepository.getDataSourceListByIds(countryId, dataSourceIds);
        dataSourceList.forEach(dataSource-> dataSource.setSuggestedDataStatus(suggestedDataStatus));
        dataSourceMongoRepository.saveAll(getNextSequence(dataSourceList));
        return dataSourceList;
    }


}

    
    
    


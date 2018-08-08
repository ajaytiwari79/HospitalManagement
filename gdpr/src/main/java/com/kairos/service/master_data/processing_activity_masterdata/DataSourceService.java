package com.kairos.service.master_data.processing_activity_masterdata;


import com.kairos.custom_exception.DataNotExists;
import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.custom_exception.InvalidRequestException;
import com.kairos.persistance.model.master_data.default_proc_activity_setting.DataSource;
import com.kairos.persistance.repository.master_data.processing_activity_masterdata.data_source.DataSourceMongoRepository;
import com.kairos.response.dto.common.DataSourceResponseDTO;
import com.kairos.service.common.MongoBaseService;
import com.kairos.utils.ComparisonUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;

import static com.kairos.constants.AppConstant.EXISTING_DATA_LIST;
import static com.kairos.constants.AppConstant.NEW_DATA_LIST;


@Service
public class DataSourceService extends MongoBaseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataSourceService.class);

    @Inject
    private DataSourceMongoRepository dataSourceMongoRepository;


    /**
     * @param countryId
     * @param dataSources
     * @return return map which contain list of new DataSource and list of existing DataSource if DataSource already exist
     * @description this method create new DataSource if DataSource not exist with same name ,
     * and if exist then simply add  DataSource to existing list and return list ;
     * findByNamesAndCountryId()  return list of existing DataSource using collation ,used for case insensitive result
     */
    public Map<String, List<DataSource>> createDataSource(Long countryId, List<DataSource> dataSources) {

        Map<String, List<DataSource>> result = new HashMap<>();
        Set<String> dataSourceNames = new HashSet<>();
        if (!dataSources.isEmpty()) {
            for (DataSource dataSource : dataSources) {
                if (!StringUtils.isBlank(dataSource.getName())) {
                    dataSourceNames.add(dataSource.getName());
                } else
                    throw new InvalidRequestException("name could not be empty or null");
            }
            List<DataSource> existing = findByNamesAndCountryId(countryId, dataSourceNames, DataSource.class);
            dataSourceNames = ComparisonUtils.getNameListForMetadata(existing, dataSourceNames);

            List<DataSource> newDataSources = new ArrayList<>();
            if (!dataSourceNames.isEmpty()) {
                for (String name : dataSourceNames) {

                    DataSource newDataSource = new DataSource(name);
                    newDataSource.setCountryId(countryId);
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

        DataSource exist = dataSourceMongoRepository.findByIdAndNonDeleted(countryId, id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            delete(exist);
            return true;

        }
    }

    /***
     * @throws DuplicateDataException throw exception if DataSource data not exist for given id
     * @param countryId
     * @param id id of DataSource
     * @param dataSource
     * @return DataSource updated object
     */
    public DataSource updateDataSource(Long countryId,  BigInteger id, DataSource dataSource) {

        DataSource exist = dataSourceMongoRepository.findByNameAndCountryId(countryId, dataSource.getName());
        if (Optional.ofNullable(exist).isPresent()) {
            if (id.equals(exist.getId())) {
                return exist;
            }
            throw new DuplicateDataException("data  exist for  " + dataSource.getName());
        } else {
            exist = dataSourceMongoRepository.findByid(id);
            exist.setName(dataSource.getName());
            return dataSourceMongoRepository.save(exist);

        }
    }

    /**
     * @param countryId
     * @param name           name of DataSource
     * @return DataSource object fetch on basis of  name
     * @throws DataNotExists throw exception if DataSource not exist for given name
     */
    public DataSource getDataSourceByName(Long countryId,  String name) {


        if (!StringUtils.isBlank(name)) {
            DataSource exist = dataSourceMongoRepository.findByNameAndCountryId(countryId,  name);
            if (!Optional.ofNullable(exist).isPresent()) {
                throw new DataNotExists("data not exist for name " + name);
            }
            return exist;
        } else
            throw new InvalidRequestException("request param cannot be empty  or null");

    }


    /**
     * @param countryId
     * @param organizationId - id of parent organization
     * @param unitId         - id of unit organization
     * @return method return list of organization Data Sources with Data Sources which were not inherited by organization  till now
     */
    public List<DataSourceResponseDTO> getAllNotInheritedDataSourceFromParentOrgAndUnitDataSource(Long countryId, Long organizationId, Long unitId) {
        return dataSourceMongoRepository.getAllNotInheritedDataSourceFromParentOrgAndUnitDataSource(countryId, organizationId, unitId);
    }


}

    
    
    


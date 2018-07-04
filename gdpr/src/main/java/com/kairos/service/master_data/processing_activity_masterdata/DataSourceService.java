package com.kairos.service.master_data.processing_activity_masterdata;


import com.kairos.custom_exception.DataNotExists;
import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.custom_exception.InvalidRequestException;
import com.kairos.persistance.model.master_data.processing_activity_masterdata.DataSource;
import com.kairos.persistance.repository.master_data.processing_activity_masterdata.DataSourceMongoRepository;
import com.kairos.service.common.MongoBaseService;
import com.kairos.utils.ComparisonUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;

@Service
public class DataSourceService extends MongoBaseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataSourceService.class);

    @Inject
    private DataSourceMongoRepository dataSourceMongoRepository;

    @Inject
    private ComparisonUtils comparisonUtils;


    public Map<String, List<DataSource>> createDataSource(Long countryId,Long organizationId,List<DataSource> dataSources) {

        Map<String, List<DataSource>> result = new HashMap<>();
        Set<String> dataSourceNames = new HashSet<>();
        if (dataSources.size() != 0) {
            for (DataSource dataSource : dataSources) {
                if (!StringUtils.isBlank(dataSource.getName())) {
                    dataSourceNames.add(dataSource.getName());
                } else
                    throw new InvalidRequestException("name could not be empty or null");
            }
            List<DataSource> existing = findByNamesList(countryId,organizationId,dataSourceNames,DataSource.class);
            dataSourceNames = comparisonUtils.getNameListForMetadata(existing, dataSourceNames);

            List<DataSource> newDataSources = new ArrayList<>();
            if (dataSourceNames.size()!=0) {
                for (String name : dataSourceNames) {

                    DataSource newDataSource = new DataSource();
                    newDataSource.setName(name);
                    newDataSource.setCountryId(countryId);
                    newDataSource.setOrganizationId(organizationId);
                    newDataSources.add(newDataSource);

                }

                newDataSources = dataSourceMongoRepository.saveAll(sequenceGenerator(newDataSources));
            }
            result.put("existing", existing);
            result.put("new", newDataSources);
            return result;
        } else
            throw new InvalidRequestException("list cannot be empty");


    }

    public List<DataSource> getAllDataSource(Long countryId,Long organizationId) {
        return dataSourceMongoRepository.findAllDataSources(countryId,organizationId);
    }


    public DataSource getDataSource(Long countryId,Long organizationId,BigInteger id) {

        DataSource exist = dataSourceMongoRepository.findByIdAndNonDeleted(countryId,organizationId, id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            return exist;

        }
    }


    public Boolean deleteDataSource(Long countryId,Long organizationId,BigInteger id) {

        DataSource exist = dataSourceMongoRepository.findByIdAndNonDeleted(countryId,organizationId,id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            delete(exist);
            return true;

        }
    }


    public DataSource updateDataSource(Long countryId,Long organizationId,BigInteger id, DataSource dataSource) {

        DataSource exist = dataSourceMongoRepository.findByNameAndCountryId(countryId,organizationId,dataSource.getName());
        if (Optional.ofNullable(exist).isPresent()) {
            if (id.equals(exist.getId())) {
                return exist;
            }
            throw new DuplicateDataException("data  exist for  "+dataSource.getName());
        } else {
            exist=dataSourceMongoRepository.findByid(id);
            exist.setName(dataSource.getName());
            return dataSourceMongoRepository.save(sequenceGenerator(exist));

        }
    }


    public DataSource getDataSourceByName(Long countryId,Long organizationId, String name) {


        if (!StringUtils.isBlank(name)) {
            DataSource exist = dataSourceMongoRepository.findByNameAndCountryId(countryId,organizationId, name);
            if (!Optional.ofNullable(exist).isPresent()) {
                throw new DataNotExists("data not exist for name " + name);
            }
            return exist;
        } else
            throw new InvalidRequestException("request param cannot be empty  or null");

    }


}

    
    
    


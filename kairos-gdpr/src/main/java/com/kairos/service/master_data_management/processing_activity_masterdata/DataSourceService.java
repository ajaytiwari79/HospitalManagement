package com.kairos.service.master_data_management.processing_activity_masterdata;


import com.kairos.custome_exception.DataNotExists;
import com.kairos.custome_exception.DataNotFoundByIdException;
import com.kairos.custome_exception.InvalidRequestException;
import com.kairos.persistance.model.master_data_management.processing_activity_masterdata.DataSource;
import com.kairos.persistance.repository.master_data_management.processing_activity_masterdata.DataSourceMongoRepository;
import com.kairos.service.MongoBaseService;
import com.kairos.utils.userContext.UserContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;

@Service
public class DataSourceService extends MongoBaseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataSourceService.class);

    @Inject
    private DataSourceMongoRepository dataSourceMongoRepository;


    public Map<String, List<DataSource>> createDataSource(Long countryId, List<DataSource> dataSources) {
        Map<String, List<DataSource>> result = new HashMap<>();
        List<DataSource> existing = new ArrayList<>();
        List<DataSource> newDataSources = new ArrayList<>();
        Set<String> names = new HashSet<>();
        if (dataSources.size() != 0) {
            for (DataSource dataSource : dataSources) {
                if (!StringUtils.isBlank(dataSource.getName())) {
                    names.add(dataSource.getName());
                } else
                    throw new InvalidRequestException("name could not be empty or null");
            }

            existing = dataSourceMongoRepository.findByCountryAndNameList(countryId, names);
            existing.forEach(item -> names.remove(item.getName()));

            if (names.size()!=0) {
                for (String name : names) {

                    DataSource newDataSource = new DataSource();
                    newDataSource.setName(name);
                    newDataSource.setCountryId(countryId);
                    newDataSources.add(newDataSource);

                }

                newDataSources = save(newDataSources);
            }
            result.put("existing", existing);
            result.put("new", newDataSources);
            return result;
        } else
            throw new InvalidRequestException("list cannot be empty");


    }

    public List<DataSource> getAllDataSource() {
        return dataSourceMongoRepository.findAllDataSources(UserContext.getCountryId());
    }


    public DataSource getDataSource(Long countryId, BigInteger id) {

        DataSource exist = dataSourceMongoRepository.findByIdAndNonDeleted(countryId, id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            return exist;

        }
    }


    public Boolean deleteDataSource(BigInteger id) {

        DataSource exist = dataSourceMongoRepository.findByid(id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            exist.setDeleted(true);
            save(exist);
            return true;

        }
    }


    public DataSource updateDataSource(BigInteger id, DataSource dataSource) {

        DataSource exist = dataSourceMongoRepository.findByName(UserContext.getCountryId(),dataSource.getName());
        if (Optional.ofNullable(exist).isPresent()) {
            throw new InvalidRequestException("data  exist for  "+dataSource.getName());
        } else {
            exist=dataSourceMongoRepository.findByid(id);
            exist.setName(dataSource.getName());
            return save(exist);

        }
    }


    public DataSource getDataSourceByName(Long countryId, String name) {


        if (!StringUtils.isBlank(name)) {
            DataSource exist = dataSourceMongoRepository.findByName(countryId, name);
            if (!Optional.ofNullable(exist).isPresent()) {
                throw new DataNotExists("data not exist for name " + name);
            }
            return exist;
        } else
            throw new InvalidRequestException("request param cannot be empty  or null");

    }


}

    
    
    


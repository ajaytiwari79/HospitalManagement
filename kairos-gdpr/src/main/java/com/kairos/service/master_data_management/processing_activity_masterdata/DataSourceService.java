package com.kairos.service.master_data_management.processing_activity_masterdata;


import com.kairos.custome_exception.DataNotExists;
import com.kairos.custome_exception.DataNotFoundByIdException;
import com.kairos.custome_exception.InvalidRequestException;
import com.kairos.persistance.model.master_data_management.processing_activity_masterdata.DataSource;
import com.kairos.persistance.repository.master_data_management.processing_activity_masterdata.DataSourceMongoRepository;
import com.kairos.service.MongoBaseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;

@Service
public class DataSourceService extends MongoBaseService {


    @Inject
    private DataSourceMongoRepository dataSourceMongoRepository;


    public Map<String, List<DataSource>> createDataSource(List<DataSource> dataSources) {
        Map<String, List<DataSource>> result = new HashMap<>();
        List<DataSource> existing = new ArrayList<>();
        List<DataSource> newDataSources = new ArrayList<>();
        if (dataSources.size() != 0) {
            for (DataSource dataSource : dataSources) {
                if (!StringUtils.isBlank(dataSource.getName())) {
                    DataSource exist = dataSourceMongoRepository.findByName(dataSource.getName());
                    if (Optional.ofNullable(exist).isPresent()) {
                        existing.add(exist);

                    } else {
                        DataSource newDataSource = new DataSource();
                        newDataSource.setName(dataSource.getName());
                        newDataSources.add(save(newDataSource));
                    }
                } else
                    throw new InvalidRequestException("name could not be empty or null");
            }

            result.put("existing", existing);
            result.put("new", newDataSources);
            return result;
        } else
            throw new InvalidRequestException("list cannot be empty");


    }

    public List<DataSource> getAllDataSource() {
       return dataSourceMongoRepository.findAllDataSources();
    }


    public DataSource getDataSource(BigInteger id) {

        DataSource exist = dataSourceMongoRepository.findByIdAndNonDeleted(id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            return exist;

        }
    }


    public Boolean deleteDataSource(BigInteger id) {

        DataSource exist = dataSourceMongoRepository.findByIdAndNonDeleted(id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            exist.setDeleted(true);
            save(exist);
            return true;

        }
    }


    public DataSource updateDataSource(BigInteger id, DataSource dataSource) {


        DataSource exist = dataSourceMongoRepository.findByIdAndNonDeleted(id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            exist.setName(dataSource.getName());

            return save(exist);

        }
    }


    public DataSource getDataSourceByName(String name) {


        if (!StringUtils.isBlank(name)) {
            DataSource exist = dataSourceMongoRepository.findByName(name);
            if (!Optional.ofNullable(exist).isPresent()) {
                throw new DataNotExists("data not exist for name " + name);
            }
            return exist;
        } else
            throw new InvalidRequestException("request param cannot be empty  or null");

    }


}

    
    
    


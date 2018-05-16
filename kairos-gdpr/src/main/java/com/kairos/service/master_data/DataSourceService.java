package com.kairos.service.master_data;


import com.kairos.custome_exception.DataNotExists;
import com.kairos.custome_exception.DataNotFoundByIdException;
import com.kairos.custome_exception.DuplicateDataException;
import com.kairos.custome_exception.RequestDataNull;
import com.kairos.persistance.model.master_data.DataSource;
import com.kairos.persistance.repository.master_data.DataSourceMongoRepository;

import com.kairos.service.MongoBaseService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Service
public class DataSourceService extends MongoBaseService {


    @Inject
    private DataSourceMongoRepository dataSourceMongoRepository;



    public DataSource createDataSource(String dataSource) {
        if (StringUtils.isEmpty(dataSource))
        {
            throw new RequestDataNull("requested dataSource  is null or empty");
        }
        DataSource exist = dataSourceMongoRepository.findByName(dataSource);
        if (Optional.ofNullable(exist).isPresent()) {
            throw new DuplicateDataException("data already exist for name " + dataSource);
        } else {
            DataSource newDataSource = new DataSource();
            newDataSource.setName(dataSource);
            return save(newDataSource);
        }
    }


    public List<DataSource> getAllDataSource() {
        List<DataSource> result = dataSourceMongoRepository.findAll();
        if (result.size()!=0) {
            return result;

        } else
            throw new DataNotExists("DataSource not exist please create purpose ");
    }



    public DataSource getDataSourceById(BigInteger id) {

        DataSource exist = dataSourceMongoRepository.findByid(id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id "+id);
        } else {
            return exist;

        }
    }



    public Boolean deleteDataSourceById(BigInteger id) {

        DataSource exist = dataSourceMongoRepository.findByid(id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id "+id);
        } else {
            dataSourceMongoRepository.delete(exist);
            return true;

        }
    }


    public DataSource updateDataSource(BigInteger id,String dataSource) {
        if (StringUtils.isEmpty(dataSource))
        {
            throw new RequestDataNull("requested dataSource  is null or empty");
        }

        DataSource exist = dataSourceMongoRepository.findByid(id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id "+id);
        } else {
            exist.setName(dataSource);
            return save(exist);

        }
    }


    public List<DataSource> dataSourceList(List<BigInteger> dataSourceids) {

        if (dataSourceids != null) {
            return dataSourceMongoRepository.dataSourceList(dataSourceids);

        } else
            throw new RequestDataNull("requested dataSourceList is null");
    }





}

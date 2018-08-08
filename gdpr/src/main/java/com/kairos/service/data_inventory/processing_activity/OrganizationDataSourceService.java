package com.kairos.service.data_inventory.processing_activity;

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
public class OrganizationDataSourceService extends MongoBaseService {


    private static final Logger LOGGER = LoggerFactory.getLogger(OrganizationDataSourceService.class);

    @Inject
    private DataSourceMongoRepository dataSourceMongoRepository;


    /**
     * @param organizationId
     * @param dataSources
     * @return return map which contain list of new DataSource and list of existing DataSource if DataSource already exist
     * @description this method create new DataSource if DataSource not exist with same name ,
     * and if exist then simply add  DataSource to existing list and return list ;
     * findByNamesAndCountryId()  return list of existing DataSource using collation ,used for case insensitive result
     */
    public Map<String, List<DataSource>> createDataSource( Long organizationId, List<DataSource> dataSources) {

        Map<String, List<DataSource>> result = new HashMap<>();
        Set<String> dataSourceNames = new HashSet<>();
        if (dataSources.size() != 0) {
            for (DataSource dataSource : dataSources) {
                if (!StringUtils.isBlank(dataSource.getName())) {
                    dataSourceNames.add(dataSource.getName());
                } else
                    throw new InvalidRequestException("name could not be empty or null");
            }
            List<DataSource> existing = findAllByNameAndOrganizationId( organizationId, dataSourceNames, DataSource.class);
            dataSourceNames = ComparisonUtils.getNameListForMetadata(existing, dataSourceNames);

            List<DataSource> newDataSources = new ArrayList<>();
            if (dataSourceNames.size() != 0) {
                for (String name : dataSourceNames) {
                    DataSource newDataSource = new DataSource(name);
                    newDataSource.setOrganizationId(organizationId);
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
     * @param organizationId
     * @return list of DataSource
     */
    public List<DataSourceResponseDTO> getAllDataSource(Long organizationId) {
        return dataSourceMongoRepository.findAllOrganizationDataSources( organizationId);
    }

    /**
     * @throws DataNotFoundByIdException throw exception if DataSource not found for given id
     * @param organizationId
     * @param id id of DataSource
     * @return DataSource object fetch by given id
     */
    public DataSource getDataSource( Long organizationId, BigInteger id) {

        DataSource exist = dataSourceMongoRepository.findByOrganizationIdAndId(organizationId, id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            return exist;

        }
    }


    public Boolean deleteDataSource( Long organizationId, BigInteger id) {

        DataSource exist = dataSourceMongoRepository.findByOrganizationIdAndId( organizationId, id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            delete(exist);
            return true;

        }
    }

    /***
     * @throws DuplicateDataException throw exception if DataSource data not exist for given id
     * @param organizationId
     * @param id id of DataSource
     * @param dataSource
     * @return DataSource updated object
     */
    public DataSource updateDataSource( Long organizationId, BigInteger id, DataSource dataSource) {

        DataSource exist = dataSourceMongoRepository.findByNameAndOrganizationId(organizationId, dataSource.getName());
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
     * @param organizationId
     * @param name           name of DataSource
     * @return DataSource object fetch on basis of  name
     * @throws DataNotExists throw exception if DataSource not exist for given name
     */
    public DataSource getDataSourceByName( Long organizationId, String name) {


        if (!StringUtils.isBlank(name)) {
            DataSource exist = dataSourceMongoRepository.findByNameAndOrganizationId( organizationId, name);
            if (!Optional.ofNullable(exist).isPresent()) {
                throw new DataNotExists("data not exist for name " + name);
            }
            return exist;
        } else
            throw new InvalidRequestException("request param cannot be empty  or null");

    }


}

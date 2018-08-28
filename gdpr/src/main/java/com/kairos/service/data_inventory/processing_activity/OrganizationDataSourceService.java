package com.kairos.service.data_inventory.processing_activity;

import com.kairos.custom_exception.DataNotExists;
import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.custom_exception.InvalidRequestException;
import com.kairos.gdpr.metadata.DataSourceDTO;
import com.kairos.persistance.model.master_data.default_proc_activity_setting.DataSource;
import com.kairos.persistance.repository.data_inventory.processing_activity.ProcessingActivityMongoRepository;
import com.kairos.persistance.repository.master_data.processing_activity_masterdata.data_source.DataSourceMongoRepository;
import com.kairos.response.dto.common.DataSourceResponseDTO;
import com.kairos.response.dto.data_inventory.ProcessingActivityBasicResponseDTO;
import com.kairos.service.common.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.master_data.processing_activity_masterdata.DataSourceService;
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
    private ExceptionService exceptionService;

    @Inject
    private DataSourceMongoRepository dataSourceMongoRepository;

    @Inject
    private DataSourceService dataSourceService;

    @Inject
    private ProcessingActivityMongoRepository processingActivityMongoRepository;


    /**
     * @param organizationId
     * @param dataSourceDTOS
     * @return return map which contain list of new DataSource and list of existing DataSource if DataSource already exist
     * @description this method create new DataSource if DataSource not exist with same name ,
     * and if exist then simply add  DataSource to existing list and return list ;
     * findMetaDataByNamesAndCountryId()  return list of existing DataSource using collation ,used for case insensitive result
     */
    public Map<String, List<DataSource>> createDataSource(Long organizationId, List<DataSourceDTO> dataSourceDTOS) {

        Map<String, List<DataSource>> result = new HashMap<>();
        Set<String> dataSourceNames = new HashSet<>();
        if (!dataSourceDTOS.isEmpty()) {
            for (DataSourceDTO dataSource : dataSourceDTOS) {

                dataSourceNames.add(dataSource.getName());
            }
            List<DataSource> existing = findMetaDataByNameAndUnitId(organizationId, dataSourceNames, DataSource.class);
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
        return dataSourceMongoRepository.findAllOrganizationDataSources(organizationId);
    }

    /**
     * @param organizationId
     * @param id             id of DataSource
     * @return DataSource object fetch by given id
     * @throws DataNotFoundByIdException throw exception if DataSource not found for given id
     */
    public DataSource getDataSource(Long organizationId, BigInteger id) {

        DataSource exist = dataSourceMongoRepository.findByOrganizationIdAndId(organizationId, id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        }
        return exist;
    }


    public Boolean deleteDataSource(Long unitId, BigInteger dataSourceId) {

        List<ProcessingActivityBasicResponseDTO>  processingActivitiesLinkedWithDataSource = processingActivityMongoRepository.findAllProcessingActivityLinkedWithDataSource(unitId, dataSourceId);
        if (!processingActivitiesLinkedWithDataSource.isEmpty()) {
            StringBuilder processingActivityNames=new StringBuilder();
            processingActivitiesLinkedWithDataSource.forEach(processingActivity->processingActivityNames.append(processingActivity.getName()+","));
            exceptionService.metaDataLinkedWithProcessingActivityException("message.metaData.linked.with.ProcessingActivity", "DataSource", processingActivityNames);
        }
        DataSource dataSource = dataSourceMongoRepository.findByOrganizationIdAndId(unitId, dataSourceId);
        if (!Optional.ofNullable(dataSource).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "DataSource", dataSourceId);
        }
        delete(dataSource);
        return true;
    }

    /***
     * @throws DuplicateDataException throw exception if DataSource data not exist for given id
     * @param organizationId
     * @param id id of DataSource
     * @param dataSourceDTO
     * @return DataSource updated object
     */
    public DataSourceDTO updateDataSource(Long organizationId, BigInteger id, DataSourceDTO dataSourceDTO) {

        DataSource dataSource = dataSourceMongoRepository.findByNameAndOrganizationId(organizationId, dataSourceDTO.getName());
        if (Optional.ofNullable(dataSource).isPresent()) {
            if (id.equals(dataSource.getId())) {
                return dataSourceDTO;
            }
            exceptionService.duplicateDataException("message.duplicate","DataSource",dataSource.getName());
        }
        dataSource = dataSourceMongoRepository.findByid(id);
        if (!Optional.ofNullable(dataSource).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "DataSource", id);
        }
        dataSource.setName(dataSourceDTO.getName());
        dataSourceMongoRepository.save(dataSource);
        return dataSourceDTO;


    }

    public Map<String, List<DataSource>> saveAndSuggestDataSources(Long countryId, Long organizationId, List<DataSourceDTO> DataSourceDTOS) {

        Map<String, List<DataSource>> result;
        result = createDataSource(organizationId, DataSourceDTOS);
        List<DataSource> masterDataSourceSuggestedByUnit = dataSourceService.saveSuggestedDataSourcesFromUnit(countryId, DataSourceDTOS);
        if (!masterDataSourceSuggestedByUnit.isEmpty()) {
            result.put("SuggestedData", masterDataSourceSuggestedByUnit);
        }
        return result;
    }


}

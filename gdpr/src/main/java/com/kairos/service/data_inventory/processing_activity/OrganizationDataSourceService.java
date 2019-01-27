package com.kairos.service.data_inventory.processing_activity;



import com.kairos.commons.custom_exception.DataNotFoundByIdException;
import com.kairos.commons.custom_exception.DuplicateDataException;
import com.kairos.commons.custom_exception.InvalidRequestException;
import com.kairos.dto.gdpr.metadata.DataSourceDTO;
import com.kairos.persistence.model.master_data.default_proc_activity_setting.DataSourceMD;
import com.kairos.persistence.repository.data_inventory.processing_activity.ProcessingActivityRepository;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.data_source.DataSourceRepository;
import com.kairos.response.dto.common.DataSourceResponseDTO;
import com.kairos.service.common.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.master_data.processing_activity_masterdata.DataSourceService;
import com.kairos.utils.ComparisonUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.constants.AppConstant.EXISTING_DATA_LIST;
import static com.kairos.constants.AppConstant.NEW_DATA_LIST;


@Service
public class OrganizationDataSourceService extends MongoBaseService {


    private static final Logger LOGGER = LoggerFactory.getLogger(OrganizationDataSourceService.class);

    @Inject
    private ExceptionService exceptionService;

    @Inject
    private DataSourceRepository dataSourceRepository;

    @Inject
    private DataSourceService dataSourceService;

    @Inject
    private ProcessingActivityRepository processingActivityRepository;


    /**
     * @param organizationId
     * @param dataSourceDTOS
     * @return return map which contain list of new DataSource and list of existing DataSource if DataSource already exist
     * @description this method create new DataSource if DataSource not exist with same name ,
     * and if exist then simply add  DataSource to existing list and return list ;
     * findMetaDataByNamesAndCountryId()  return list of existing DataSource using collation ,used for case insensitive result
     */
    public Map<String, List<DataSourceMD>> createDataSource(Long organizationId, List<DataSourceDTO> dataSourceDTOS) {

        Map<String, List<DataSourceMD>> result = new HashMap<>();
        Set<String> dataSourceNames = new HashSet<>();
        if (!dataSourceDTOS.isEmpty()) {
            for (DataSourceDTO dataSource : dataSourceDTOS) {
                dataSourceNames.add(dataSource.getName());
            }
            List<String> nameInLowerCase = dataSourceNames.stream().map(String::toLowerCase)
                    .collect(Collectors.toList());
            //TODO still need to update we can return name of list from here and can apply removeAll on list
            List<DataSourceMD> existing = dataSourceRepository.findByOrganizationIdAndDeletedAndNameIn(organizationId, false, nameInLowerCase);
            dataSourceNames = ComparisonUtils.getNameListForMetadata(existing, dataSourceNames);

            List<DataSourceMD> newDataSources = new ArrayList<>();
            if (dataSourceNames.size() != 0) {
                for (String name : dataSourceNames) {
                    DataSourceMD newDataSource = new DataSourceMD(name);
                    newDataSource.setOrganizationId(organizationId);
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
     * @param organizationId
     * @return list of DataSource
     */
    public List<DataSourceResponseDTO> getAllDataSource(Long organizationId) {
        return dataSourceRepository.findAllByOrganizationIdAndSortByCreatedDate(organizationId);
    }

    /**
     * @param organizationId
     * @param id             id of DataSource
     * @return DataSource object fetch by given id
     * @throws DataNotFoundByIdException throw exception if DataSource not found for given id
     */
    public DataSourceMD getDataSource(Long organizationId, Long id) {

        DataSourceMD exist = dataSourceRepository.findByIdAndOrganizationIdAndDeleted( id, organizationId, false);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        }
        return exist;
    }


    public Boolean deleteDataSource(Long unitId, Long dataSourceId) {

        List<String>  processingActivitiesLinkedWithDataSource = processingActivityRepository.findAllProcessingActivityLinkedWithDataSource(unitId, dataSourceId);
        if (!processingActivitiesLinkedWithDataSource.isEmpty()) {
            exceptionService.metaDataLinkedWithProcessingActivityException("message.metaData.linked.with.ProcessingActivity", "DataSource", StringUtils.join(processingActivitiesLinkedWithDataSource, ','));
        }
       dataSourceRepository.deleteByIdAndOrganizationId(dataSourceId, unitId);
        return true;
    }

    /***
     * @throws DuplicateDataException throw exception if DataSource data not exist for given id
     * @param organizationId
     * @param id id of DataSource
     * @param dataSourceDTO
     * @return DataSource updated object
     */
    public DataSourceDTO updateDataSource(Long organizationId, Long id, DataSourceDTO dataSourceDTO) {

        DataSourceMD dataSource = dataSourceRepository.findByOrganizationIdAndDeletedAndName(organizationId, false, dataSourceDTO.getName());
        if (Optional.ofNullable(dataSource).isPresent()) {
            if (id.equals(dataSource.getId())) {
                return dataSourceDTO;
            }
            exceptionService.duplicateDataException("message.duplicate","DataSource",dataSource.getName());
        }
        Integer resultCount =  dataSourceRepository.updateMetadataName(dataSourceDTO.getName(), id, organizationId);
        if(resultCount <=0){
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "DataSource", id);
        }else{
            LOGGER.info("Data updated successfully for id : {} and name updated name is : {}", id, dataSourceDTO.getName());
        }
        return dataSourceDTO;


    }

    public Map<String, List<DataSourceMD>> saveAndSuggestDataSources(Long countryId, Long organizationId, List<DataSourceDTO> dataSourceDTOS) {

        Map<String, List<DataSourceMD>> result = createDataSource(organizationId, dataSourceDTOS);
        List<DataSourceMD> masterDataSourceSuggestedByUnit = dataSourceService.saveSuggestedDataSourcesFromUnit(countryId, dataSourceDTOS);
        if (!masterDataSourceSuggestedByUnit.isEmpty()) {
            result.put("SuggestedData", masterDataSourceSuggestedByUnit);
        }
        return result;
    }


}

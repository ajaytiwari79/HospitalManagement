package com.kairos.service.data_inventory.data_category_element;


import com.kairos.gdpr.data_inventory.OrganizationDataSubjectDTO;
import com.kairos.gdpr.master_data.DataCategoryDTO;
import com.kairos.gdpr.data_inventory.OrganizationDataSubjectBasicDTO;
import com.kairos.persistance.model.master_data.data_category_element.DataCategory;
import com.kairos.persistance.model.master_data.data_category_element.DataElement;
import com.kairos.persistance.model.master_data.data_category_element.DataSubjectMapping;
import com.kairos.persistance.repository.master_data.data_category_element.DataCategoryMongoRepository;
import com.kairos.persistance.repository.master_data.data_category_element.DataElementMongoRepository;
import com.kairos.persistance.repository.master_data.data_category_element.DataSubjectMappingRepository;
import com.kairos.response.dto.master_data.data_mapping.DataSubjectMappingResponseDTO;
import com.kairos.service.common.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.mongodb.MongoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import static com.kairos.constants.AppConstant.DATA_ELEMENTS_LIST;
import static com.kairos.constants.AppConstant.DATA_CATEGORY_LIST;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;

@Service
public class OrganizationDataSubjectMappingService extends MongoBaseService {


    private Logger LOGGER = LoggerFactory.getLogger(OrganizationDataSubjectMappingService.class);

    @Inject
    private DataSubjectMappingRepository dataSubjectMappingRepository;

    @Inject
    private ExceptionService exceptionService;

    @Inject
    private OrganizationDataCategoryService organizationDataCategoryService;

    @Inject
    private DataCategoryMongoRepository dataCategoryMongoRepository;

    @Inject
    private DataElementMongoRepository dataElementMongoRepository;

    /**
     * @description  -method uses buildDataSubjectWithDataCategoriesAndDataElement to build Data Subject Object and Map Data Categories With Data Subject,
     * Method check for Duplicate name of Data Subject
     * @param unitId -organization id
     * @param dataSubjectDTOS Data Subject List contain Set<String> names of data Subject and list of  Data Category corresponding to Data Subject
     * @return method return list of Data Subject
     */

    public List<DataSubjectMapping> createDataSubjectWithDataCategoriesAndDataElements(Long unitId, List<OrganizationDataSubjectDTO> dataSubjectDTOS) {


        Set<String> dataSubjectNameList = new HashSet<>();
        List<DataCategoryDTO> dataCategoryDTOList = new ArrayList<>();
        for (OrganizationDataSubjectDTO dataSubjectDTO : dataSubjectDTOS) {
            dataSubjectNameList.addAll(dataSubjectDTO.getDataSubjectNames());
            dataCategoryDTOList.addAll(dataSubjectDTO.getDataCategories());
        }
        List<DataSubjectMapping> previousDataSubjectList = dataSubjectMappingRepository.findByNameListAndUnitId(unitId, dataSubjectNameList);
        if (!previousDataSubjectList.isEmpty()) {
            exceptionService.duplicateDataException("message.duplicate", "Data Subject ", previousDataSubjectList.get(0).getName());
        }
        return buildDataSubjectWithDataCategoriesAndDataElement(unitId, dataSubjectDTOS, dataCategoryDTOList);
    }


    /**
     * @descirption
     * @param unitId
     * @param dataSubjectDTOS
     * @param dataCategoryDTOS  list od Data Category dto which contain List of Data Element corresponding to Data Category.
     * @return method build data Subject object and Return list of Data Subjects
     */
    private List<DataSubjectMapping> buildDataSubjectWithDataCategoriesAndDataElement(Long unitId, List<OrganizationDataSubjectDTO> dataSubjectDTOS, List<DataCategoryDTO> dataCategoryDTOS) {


        Map<String, Object> dataCategoryAndDataElementListMap = organizationDataCategoryService.createDataCategoryWithDataElements(unitId, dataCategoryDTOS);
        List<DataCategory> dataCategoryList = (List<DataCategory>) dataCategoryAndDataElementListMap.get(DATA_CATEGORY_LIST);
        Map<String, BigInteger> dataCategoryIdCorrespondingToName = new HashMap<>();
        for (DataCategory dataCategory : dataCategoryList) {
            dataCategoryIdCorrespondingToName.put(dataCategory.getName(), dataCategory.getId());
        }
        List<DataSubjectMapping> dataSubjectMappingList = new ArrayList<>();
        for (OrganizationDataSubjectDTO dataSubjectDTO : dataSubjectDTOS) {
            Set<BigInteger> dataCategoryIdList = new HashSet<>();
            if (Optional.ofNullable(dataSubjectDTO.getDataCategories()).isPresent() && !dataSubjectDTO.getDataCategories().isEmpty()) {
                dataSubjectDTO.getDataCategories().forEach(dataCategoryDTO -> {
                    dataCategoryIdList.add(dataCategoryIdCorrespondingToName.get(dataCategoryDTO.getName()));
                });
            }
            Set<String> dataSubjectNameList = dataSubjectDTO.getDataSubjectNames();
            for (String dataSubjectName : dataSubjectNameList) {
                DataSubjectMapping dataSubjectMapping = new DataSubjectMapping(dataSubjectName);
                dataSubjectMapping.setDataCategories(dataCategoryIdList);
                dataSubjectMapping.setOrganizationId(unitId);
                dataSubjectMappingList.add(dataSubjectMapping);
            }
        }
        try {

            dataSubjectMappingList = dataSubjectMappingRepository.saveAll(getNextSequence(dataSubjectMappingList));
        } catch (MongoException e) {
            LOGGER.info("data Subject Mapping build", e.getMessage());
            dataCategoryMongoRepository.deleteAll(dataCategoryList);
            dataElementMongoRepository.deleteAll((List<DataElement>) dataCategoryAndDataElementListMap.get(DATA_ELEMENTS_LIST));
        }

        return dataSubjectMappingList;
    }


    /**
     *
     * @param unitId - organization id
     * @param dataSubjectId - Data Subject id
     * @return return true on success full deletion of data Subject
     */
    public Boolean deleteDataSubjectById(Long unitId, BigInteger dataSubjectId) {

        DataSubjectMapping dataSubjectMapping = dataSubjectMappingRepository.findByUnitIdAndId(unitId, dataSubjectId);
        if (!Optional.ofNullable(dataSubjectMapping).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", " Data Subject ", dataSubjectId);
        }
        delete(dataSubjectMapping);
        return true;
    }


    public List<DataSubjectMappingResponseDTO> getAllDataSubjectByUnitId(Long unitId) {
        return dataSubjectMappingRepository.getAllDataSubjectWithDataCategoryAndDataElementByUnitId(unitId);
    }


    public DataSubjectMappingResponseDTO getDataSubjectByUnitId(Long unitId, BigInteger dataSubjectId) {
        return dataSubjectMappingRepository.getDataSubjectWithDataCategoryAndDataElementByUnitId(unitId, dataSubjectId);
    }


    public OrganizationDataSubjectBasicDTO updateDataSubjectMappingById(Long unitId, BigInteger dataSubjectId, OrganizationDataSubjectBasicDTO dataSubjectMappingDTO) {

        DataSubjectMapping dataSubjectMapping = dataSubjectMappingRepository.findByNameAndUnitId(unitId, dataSubjectMappingDTO.getName());
        if (Optional.ofNullable(dataSubjectMapping).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.duplicate", "Data Subject ", dataSubjectMappingDTO.getName());
        }
        dataSubjectMapping = dataSubjectMappingRepository.findByUnitIdAndId(unitId, dataSubjectId);
        if (!Optional.ofNullable(dataSubjectMapping).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Data Subject ", dataSubjectId);
        }
        dataSubjectMapping.setName(dataSubjectMappingDTO.getName());
        dataSubjectMapping.setDataCategories(dataSubjectMappingDTO.getDataCategories());
        dataSubjectMappingRepository.save(dataSubjectMapping);
        dataSubjectMappingDTO.setId(dataSubjectMapping.getId());
        return dataSubjectMappingDTO;


    }


}

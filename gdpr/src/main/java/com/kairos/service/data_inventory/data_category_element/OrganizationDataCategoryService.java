package com.kairos.service.data_inventory.data_category_element;


import com.kairos.dto.gdpr.master_data.DataCategoryDTO;
import com.kairos.dto.gdpr.master_data.DataElementDTO;
import com.kairos.persistance.model.master_data.data_category_element.DataCategory;
import com.kairos.persistance.model.master_data.data_category_element.DataElement;
import com.kairos.persistance.repository.master_data.data_category_element.DataCategoryMongoRepository;
import com.kairos.persistance.repository.master_data.data_category_element.DataElementMongoRepository;
import com.kairos.persistance.repository.master_data.data_category_element.DataSubjectMappingRepository;
import com.kairos.response.dto.master_data.data_mapping.DataCategoryResponseDTO;
import com.kairos.response.dto.master_data.data_mapping.DataSubjectMappingBasicResponseDTO;
import com.kairos.service.common.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.mongodb.MongoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;

import static com.kairos.constants.AppConstant.*;

@Service
public class OrganizationDataCategoryService extends MongoBaseService {


    Logger LOGGER = LoggerFactory.getLogger(OrganizationDataCategoryService.class);

    @Inject
    private DataCategoryMongoRepository dataCategoryMongoRepository;


    @Inject
    private ExceptionService exceptionService;


    @Inject
    private OrganizationDataElementService dataElementService;


    @Inject
    private DataElementMongoRepository dataElementMongoRepository;

    @Inject
    private DataSubjectMappingRepository dataSubjectMappingRepository;


    /**
     * @param unitId - organization id to which data category belong
     * @param dataCategoryDTOS- list of dataCategory Dto contain list of Data Elements
     * @return method return list of Data element and list of Data category
     */
    public Map<String, Object> createDataCategoryWithDataElements(Long unitId, List<DataCategoryDTO> dataCategoryDTOS) {

        Set<String> datCategoryNameList = new HashSet<>();
        for (DataCategoryDTO dataCategoryDTO : dataCategoryDTOS) {
            datCategoryNameList.add(dataCategoryDTO.getName());
        }
        List<DataCategory> dataCategories = dataCategoryMongoRepository.findByNamesAndUnitId(unitId, datCategoryNameList);
        if (!dataCategories.isEmpty()) {
            exceptionService.duplicateDataException("message.duplicate", "Data Category", dataCategories.get(0).getName());
        }

        Map<String, Object> result = buildDataCategoryWithDataElements(unitId, dataCategoryDTOS);
        try {
            List<DataCategory> dataCategoryList = (List<DataCategory>) result.get(DATA_CATEGORY_LIST);
            dataCategoryMongoRepository.saveAll(getNextSequence(dataCategoryList));

        } catch (MongoException e) {
            dataElementMongoRepository.deleteAll((List<DataElement>) result.get(DATA_ELEMENTS_LIST));
            LOGGER.info("data category save method", e.getMessage());
        }

        return result;
    }


    /**
     * @description -  Map<DataCategory, List<DataElement>> contain list of data elements corresponding to data Category,and
     * save Data Elements  And Check Duplicate Entry method create Data Elements ,at the end we map data Category with data element and return list of Data Category with data Element
     * @param unitId
     * @param dataCategoryDTOS List og Data Category Dto which contain data element Dto list
     * @return method return list of Data Elements and data category
     */
    private Map<String, Object> buildDataCategoryWithDataElements(Long unitId, List<DataCategoryDTO> dataCategoryDTOS) {


        Map<DataCategory, List<DataElement>> dataCategorySetMapCorrespondingDataElementList = new HashMap<>();
        List<DataElement> dataElementList = new ArrayList<>();
        for (DataCategoryDTO dataCategoryDTO : dataCategoryDTOS) {
            DataCategory dataCategory = new DataCategory(dataCategoryDTO.getName());
            dataCategory.setOrganizationId(unitId);
            if (Optional.ofNullable(dataCategoryDTO.getDataElements()).isPresent() && !dataCategoryDTO.getDataElements().isEmpty()) {
                List<String> dataElementNameCorrespondingToDataCategory = new ArrayList<>();
                List<DataElement> dataElementListCorrespondingToDataCategory = new ArrayList<>();
                for (DataElementDTO dataElementDTO : dataCategoryDTO.getDataElements()) {
                    if (!dataElementNameCorrespondingToDataCategory.contains(dataElementDTO.getName())) {
                        DataElement dataElement = new DataElement(dataElementDTO.getName());
                        dataElement.setOrganizationId(unitId);
                        dataElementListCorrespondingToDataCategory.add(dataElement);
                    }
                }
                dataElementList.addAll(dataElementListCorrespondingToDataCategory);
                dataCategorySetMapCorrespondingDataElementList.put(dataCategory, dataElementListCorrespondingToDataCategory);

            }
        }
        dataElementList = dataElementService.saveDataElementsAndCheckDuplicateEntry(unitId, dataElementList);
        Map<String, Object> result = new HashMap<>();
        List<DataCategory> dataCategoryList = new ArrayList<>();
        for (Map.Entry<DataCategory, List<DataElement>> entrySet : dataCategorySetMapCorrespondingDataElementList.entrySet()) {
            DataCategory dataCategory = entrySet.getKey();
            if (!entrySet.getValue().isEmpty()) {
                List<BigInteger> dataElementIds = new ArrayList<>();
                entrySet.getValue().forEach(dataElement -> dataElementIds.add(dataElement.getId()));
                dataCategory.setDataElements(dataElementIds);
                dataCategoryList.add(dataCategory);
            }
        }
        result.put(DATA_ELEMENT_LIST, dataElementList);
        result.put(DATA_CATEGORY_LIST, dataCategoryList);
        return result;

    }


    /**
     *
     * @param unitId  - organization Id
     * @param dataCategoryId - data Category id
     * @return method  return map with status is Success true or false on successfull deletion of Data category if
     *         Data Category is Linked with Data Subjects then status is false nad we return list of Data Subjects to which Data Category is linked
     */
    public Map<String, Object> deleteDataCategoryAndDataElement(Long unitId, BigInteger dataCategoryId) {

        Map<String, Object> result = new HashMap<>();
        DataCategory dataCategory = dataCategoryMongoRepository.findByUnitIdAndId(unitId, dataCategoryId);
        if (Optional.ofNullable(dataCategory).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Data Category ", dataCategoryId);
        }
        List<DataSubjectMappingBasicResponseDTO> dataSubjectLinkedToDataCategory = dataSubjectMappingRepository.findByUnitDataSubjectLinkWithDataCategory(unitId, dataCategoryId);
        if (!dataSubjectLinkedToDataCategory.isEmpty()) {
            result.put(IS_SUCCESS, false);
            result.put(DATA_SUBJECT_LIST, dataSubjectLinkedToDataCategory);
        } else {
            List<DataElement> dataElementList = dataElementMongoRepository.findAllDataElementByUnitIdAndIds(unitId, dataCategory.getDataElements());
            if (!dataElementList.isEmpty()) {
                deleteAll(dataElementList);
            }
            delete(dataCategory);
            result.put(IS_SUCCESS, true);
        }
        return result;
    }


    /**
     * @param unitId
     * @return -return list of Data Category with Data elements
     */
    public List<DataCategoryResponseDTO> getAllDataCategoryWithDataElementByUnitId(Long unitId) {
        return dataCategoryMongoRepository.getAllDataCategoryWithDataElementByUnitId(unitId);

    }

    /**
     * @param unitId
     * @param dataCategoryId
     * @return return Data Category with Data Elements list
     */
    public DataCategoryResponseDTO getDataCategoryWithDataElementByUnitIdAndId(Long unitId, BigInteger dataCategoryId) {

        return dataCategoryMongoRepository.getDataCategoryWithDataElementByUnitIdAndId(unitId, dataCategoryId);
    }

}

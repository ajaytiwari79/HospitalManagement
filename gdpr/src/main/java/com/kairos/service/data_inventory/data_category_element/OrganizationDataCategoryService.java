package com.kairos.service.data_inventory.data_category_element;


import com.kairos.dto.master_data.DataCategoryDTO;
import com.kairos.dto.master_data.DataElementDTO;
import com.kairos.persistance.model.master_data.data_category_element.DataCategory;
import com.kairos.persistance.model.master_data.data_category_element.DataElement;
import com.kairos.persistance.repository.master_data.data_category_element.DataCategoryMongoRepository;
import com.kairos.persistance.repository.master_data.data_category_element.DataElementMognoRepository;
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
    private DataElementMognoRepository dataElementMognoRepository;


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
            dataElementMognoRepository.deleteAll((List<DataElement>) result.get(DATA_ELEMENTS_LIST));
            LOGGER.info("data category save method", e.getMessage());
        }

        return result;
    }


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
        dataElementList = dataElementService.saveDataElementsAndCheckDuplicateyEntry(unitId, dataElementList);
        Map<String, Object> result = new HashMap<>();
        List<DataCategory> dataCategoryList = new ArrayList<>();
        for (Map.Entry<DataCategory, List<DataElement>> entrySet : dataCategorySetMapCorrespondingDataElementList.entrySet()) {
            DataCategory dataCategory = entrySet.getKey();
            if (!entrySet.getValue().isEmpty()) {
                List<BigInteger> dataElementIds = new ArrayList<>();
                entrySet.getValue().forEach(dataElement -> {
                    dataElementIds.add(dataElement.getId());
                });
                dataCategory.setDataElements(dataElementIds);
                dataCategoryList.add(dataCategory);
            }
        }
        result.put(DATA_ELEMENT_LIST, dataElementList);
        result.put(DATA_CATEGORY_LIST, dataCategoryList);
        return result;

    }


    public Boolean deleteDataCategoryAndDataElement(Long unitId, BigInteger dataCategoryId) {

        DataCategory dataCategory = dataCategoryMongoRepository.findByUnitIdAndId(unitId, dataCategoryId);
        if (Optional.ofNullable(dataCategory).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Data Category ", dataCategoryId);
        }
        List<DataElement> dataElementList = dataElementMognoRepository.findAllDataElementByUnitIdAndIdss(unitId, dataCategory.getDataElements());
        if (!dataElementList.isEmpty()) {
            deleteAll(dataElementList);
        }
        delete(dataCategory);
        return true;
    }


}

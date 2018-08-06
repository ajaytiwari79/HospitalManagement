package com.kairos.service.data_inventory.data_category_element;


import com.kairos.dto.master_data.DataCategoryDTO;
import com.kairos.dto.master_data.DataElementDTO;
import com.kairos.persistance.model.master_data.data_category_element.DataCategory;
import com.kairos.persistance.repository.master_data.data_category_element.DataCategoryMongoRepository;
import com.kairos.service.common.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.*;

@Service
public class OrganizationDataCategoryService extends MongoBaseService {


    @Inject
    private DataCategoryMongoRepository dataCategoryMongoRepository;


    @Inject
    private ExceptionService exceptionService;


    public List<DataCategory> createDataCategoryWithDataElements(Long unitId, List<DataCategoryDTO> dataCategoryDTOS) {

        Set<String> datCategoryNameList = new HashSet<>();
        Map<String, List<DataElementDTO>> dataElementsCorrespondingToDataCategory = new HashMap<>();
        for (DataCategoryDTO dataCategoryDTO : dataCategoryDTOS) {
            datCategoryNameList.add(dataCategoryDTO.getName());
            dataElementsCorrespondingToDataCategory.put(dataCategoryDTO.getName(), dataCategoryDTO.getDataElements());
        }
        List<DataCategory> dataCategories = dataCategoryMongoRepository.findByNamesAndUnitId(unitId, datCategoryNameList);
        if (!dataCategories.isEmpty()) {
            exceptionService.duplicateDataException("message.duplicate", "Data Category", dataCategories.get(0).getName());
        }

        return null;
    }


    public void buildDataCategoryWithDataElements(Long unitId, Map<String, List<DataElementDTO>> dataElementsCorrespondingToDataCategory) {

        List<DataCategory> dataCategories = new ArrayList<>();
        List<DataElementDTO> dataElementDTOList = new ArrayList<>();
        for (Map.Entry<String, List<DataElementDTO>> dataCategoryEntrySet : dataElementsCorrespondingToDataCategory.entrySet()) {
            DataCategory dataCategory = new DataCategory(dataCategoryEntrySet.getKey());
            dataElementDTOList.addAll(dataCategoryEntrySet.getValue());

        }



    }


}

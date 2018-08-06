package com.kairos.service.data_inventory.data_category_element;


import com.kairos.dto.data_inventory.OrganizationDataSubjectDTO;
import com.kairos.dto.master_data.DataCategoryDTO;
import com.kairos.persistance.model.master_data.data_category_element.DataCategory;
import com.kairos.persistance.model.master_data.data_category_element.DataElement;
import com.kairos.persistance.model.master_data.data_category_element.DataSubjectMapping;
import com.kairos.persistance.repository.master_data.data_category_element.DataCategoryMongoRepository;
import com.kairos.persistance.repository.master_data.data_category_element.DataElementMognoRepository;
import com.kairos.persistance.repository.master_data.data_category_element.DataSubjectMappingRepository;
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


    Logger LOGGER = LoggerFactory.getLogger(OrganizationDataSubjectMappingService.class);

    @Inject
    private DataSubjectMappingRepository dataSubjectMappingRepository;

    @Inject
    private ExceptionService exceptionService;

    @Inject
    private OrganizationDataCategoryService organizationDataCategoryService;

    @Inject
    private DataCategoryMongoRepository dataCategoryMongoRepository;

    @Inject
    private DataElementMognoRepository dataElementMongoRepository;


    public List<DataSubjectMapping> createDataSubjectWithDataCategoriesAndDataElements(Long unitId, List<OrganizationDataSubjectDTO> dataSubjectDTOS) {


        Set<String> dataSubjectNameList = new HashSet<>();
        List<DataCategoryDTO> dataCategoryDTOList = new ArrayList<>();
        for (OrganizationDataSubjectDTO dataSubjectDTO : dataSubjectDTOS) {
            dataSubjectNameList.addAll(dataSubjectDTO.getDataSubjectNames());
            dataCategoryDTOList.addAll(dataSubjectDTO.getDataCategories());
        }
        List<DataSubjectMapping> dataSubjects = dataSubjectMappingRepository.findByNamesAndUnitId(unitId, dataSubjectNameList);
        if (!dataSubjects.isEmpty()) {
            exceptionService.duplicateDataException("message.duplicate", "Data Subject ", dataSubjects.get(0).getName());
        }
        return buildDataSubjectWithDataCategoriesAndDataElement(unitId, dataSubjectDTOS, dataCategoryDTOList);
    }


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
            if (!dataSubjectDTO.getDataCategories().isEmpty()) {
                dataSubjectDTO.getDataCategories().forEach(dataCategoryDTO -> {
                    dataCategoryIdList.add(dataCategoryIdCorrespondingToName.get(dataCategoryDTO.getName()));
                });
            }
            Set<String> dataSubjectNameList = dataSubjectDTO.getDataSubjectNames();
            for (String dataSubjectName : dataSubjectNameList) {
                DataSubjectMapping dataSubjectMapping = new DataSubjectMapping(dataSubjectName);
                dataSubjectMapping.setDataCategories(dataCategoryIdList);
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


    public Boolean deleteDataSubjectById(Long unitId, BigInteger dataSubjectId) {

        DataSubjectMapping dataSubjectMapping = dataSubjectMappingRepository.findByUnitIdAndId(unitId, dataSubjectId);
        if (Optional.ofNullable(dataSubjectMapping).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", " Data Subject ", dataSubjectId);
        }
        delete(dataSubjectMapping);
        return true;
    }


}

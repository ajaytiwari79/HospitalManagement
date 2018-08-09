package com.kairos.service.data_inventory.data_category_element;


import com.kairos.persistance.model.master_data.data_category_element.DataCategory;
import com.kairos.persistance.model.master_data.data_category_element.DataElement;
import com.kairos.persistance.repository.master_data.data_category_element.DataCategoryMongoRepository;
import com.kairos.persistance.repository.master_data.data_category_element.DataElementMongoRepository;
import com.kairos.response.dto.master_data.data_mapping.DataElementBasicResponseDTO;
import com.kairos.service.common.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;

@Service
public class OrganizationDataElementService extends MongoBaseService {


    @Inject
    private DataElementMongoRepository dataElementMongoRepository;

    @Inject
    private ExceptionService exceptionService;

    @Inject
    private DataCategoryMongoRepository dataCategoryMongoRepository;


    /**
     *
     * @param unitId
     * @param dataElementList  List of Data Elements
     * @return method return  list of  data Element and check of duplicate name in data Elements
     */
    public List<DataElement> saveDataElementsAndCheckDuplicateyEntry(Long unitId, List<DataElement> dataElementList) {

        Set<String> dataELementNameList = new HashSet<>();
        for (DataElement dataElement : dataElementList) {
            dataELementNameList.add(dataElement.getName());
        }
        List<DataElement> previousDataElementList = findAllByNameAndOrganizationId(unitId, dataELementNameList, DataElement.class);
        if (!previousDataElementList.isEmpty()) {
            exceptionService.duplicateDataException("message.duplicate", "Data element ", previousDataElementList.get(0).getName());
        }
        dataElementList = dataElementMongoRepository.saveAll(getNextSequence(dataElementList));
        return dataElementList;

    }


    /**
     *
     * @param unitId - organization id
     * @param dataCategoryId - Data Category Id to which Data element belong
     * @param dataElementId - Data Element id
     * @return method delete Data element and remove id of data Element from data Category.
     */
    public Boolean deleteDataElementById(Long unitId, BigInteger dataCategoryId, BigInteger dataElementId) {


        DataElement dataElement = dataElementMongoRepository.findByUnitIdAndId(unitId, dataElementId);
        if (!Optional.ofNullable(dataElement).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Data Element ", dataElement);
        }
        DataCategory dataCategory = dataCategoryMongoRepository.findByUnitIdAndId(unitId, dataCategoryId);
        List<BigInteger> dataElementIds = dataCategory.getDataElements();
        dataElementIds.remove(dataElementId);
        dataCategoryMongoRepository.save(dataCategory);
        dataElementMongoRepository.delete(dataElement);
        return true;

    }


    public List<DataElementBasicResponseDTO> getAllDataElementbyUnitId(Long unitId) {
        return dataElementMongoRepository.getAllDataElementByUnitId(unitId);
    }


    public DataElementBasicResponseDTO getDataElementbyUnitIdAndId(Long unitId, BigInteger id) {
        return dataElementMongoRepository.getDataElementByUnitIdAndId(unitId, id);
    }


}

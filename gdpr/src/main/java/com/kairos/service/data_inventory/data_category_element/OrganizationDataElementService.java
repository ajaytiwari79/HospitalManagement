package com.kairos.service.data_inventory.data_category_element;


import com.kairos.persistance.model.master_data.data_category_element.DataCategory;
import com.kairos.persistance.model.master_data.data_category_element.DataElement;
import com.kairos.persistance.repository.master_data.data_category_element.DataCategoryMongoRepository;
import com.kairos.persistance.repository.master_data.data_category_element.DataElementMognoRepository;
import com.kairos.service.common.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;

import static com.kairos.constants.AppConstant.*;

@Service
public class OrganizationDataElementService extends MongoBaseService {


    @Inject
    private DataElementMognoRepository dataElementMongoRepository;

    @Inject
    private ExceptionService exceptionService;

    @Inject
    private DataCategoryMongoRepository dataCategoryMongoRepository;


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


    public Boolean deleteDataElementById(Long unitId, BigInteger dataCategoryId, BigInteger dataElementId) {


        DataElement dataElement = dataElementMongoRepository.findByUnitIdAndId(unitId, dataElementId);
        if (!Optional.ofNullable(dataElement).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Data Element ", dataElement);
        }
        DataCategory dataCategory = dataCategoryMongoRepository.findByUnitIdAndId(unitId, dataCategoryId);
        List<BigInteger> dataElementIds = dataCategory.getDataElements();
        dataElementIds.remove(dataElementId);
        dataCategoryMongoRepository.save(getNextSequence(dataCategory));
        dataElementMongoRepository.delete(dataElement);

        return true;

    }


}

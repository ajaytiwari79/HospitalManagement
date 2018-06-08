package com.kairos.service.master_data_management.data_category_element;

import com.kairos.persistance.model.master_data_management.data_category_element.DataElement;
import com.kairos.persistance.repository.master_data_management.data_category_element.DataElementMognoRepository;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;

@Service
public class DataElementService extends MongoBaseService {


    @Inject
    private ExceptionService exceptionService;
    @Inject
    private DataElementMognoRepository dataElementMognoRepository;

    public Map<String, Object> createDataElements(Long countryId, List<DataElement> dataElements) {

        List<DataElement> dataElementList = new ArrayList<>();
        Set<String> dataElementNames = new HashSet<>();
        dataElements.forEach(dataElement -> {
            dataElementNames.add(dataElement.getName());
        });
        List<DataElement> existingDataElement = dataElementMognoRepository.findByCountryIdAndNames(countryId, dataElementNames);
        if (existingDataElement.size() != 0) {
            exceptionService.duplicateDataException("message.duplicate", "data element", existingDataElement.iterator().next().getName());
        }
        for (String name : dataElementNames) {
            DataElement newDataElement = new DataElement();
            newDataElement.setName(name);
            newDataElement.setCountryId(countryId);
            dataElementList.add(newDataElement);
            dataElementList = save(dataElementList);
        }
        List<BigInteger> dataElementids = new ArrayList<>();
        Map<String, Object> result = new HashMap<>();
        dataElementList.forEach(dataElement -> {
            dataElementids.add(dataElement.getId());
        });
        result.put("ids", dataElementids);
        result.put("elements", dataElementList);
        return result;

    }

    public DataElement getDataElement(Long countryId, BigInteger id) {
        DataElement exist = dataElementMognoRepository.findByIdAndNonDeleted(countryId, id);
        if (!Optional.ofNullable(exist).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "data element", id);
        }
        return exist;

    }

    public List<DataElement> getAllDataElements(Long countryId) {

        return dataElementMognoRepository.getAllDataElement(countryId);
    }


    public Boolean deleteDataElement(Long countryId, BigInteger id) {
        DataElement exist = dataElementMognoRepository.findByIdAndNonDeleted(countryId, id);
        if (!Optional.ofNullable(exist).isPresent()) {
            exceptionService.dataNotFoundByIdException("", id);
        }
        exist.setDeleted(true);
        return true;

    }


    public DataElement updateDataElement(BigInteger id, DataElement dataElement) {

        DataElement exist = dataElementMognoRepository.findByid(id);
        if (!Optional.ofNullable(exist).isPresent()) {

            exceptionService.dataNotFoundByIdException("message.dataNotFound", "data element", id);

        }
        exist.setName(dataElement.getName());
        return save(exist);
    }


}

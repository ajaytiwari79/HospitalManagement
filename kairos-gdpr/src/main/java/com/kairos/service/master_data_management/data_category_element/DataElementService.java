package com.kairos.service.master_data_management.data_category_element;

import com.kairos.dto.master_data.DataElementDto;
import com.kairos.persistance.model.master_data_management.data_category_element.DataElement;
import com.kairos.persistance.repository.master_data_management.data_category_element.DataElementMognoRepository;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.mongodb.MongoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;

import static com.kairos.constant.AppConstant.IDS_LIST;
import static com.kairos.constant.AppConstant.DATA_EMELENTS_LIST;


@Service
public class DataElementService extends MongoBaseService {


    Logger LOGGER = LoggerFactory.getLogger(DataElementService.class);

    @Inject
    private ExceptionService exceptionService;
    @Inject
    private DataElementMognoRepository dataElementMognoRepository;

    public Map<String, Object> createDataElements(Long countryId, List<DataElementDto> dataElements) {

        Set<String> dataElementNames = new HashSet<>();
        dataElements.forEach(dataElement -> {
            dataElementNames.add(dataElement.getName().trim());
        });
        List<DataElement> existingDataElement = dataElementMognoRepository.findByCountryIdAndNames(countryId, dataElementNames);
        if (existingDataElement.size() != 0) {
            exceptionService.duplicateDataException("message.duplicate", "data element", existingDataElement.iterator().next().getName());
        }
        List<DataElement> dataElementList = new ArrayList<>();
        Map<String, Object> result = new HashMap<>();
        List<BigInteger> dataElementids = new ArrayList<>();
        for (String name : dataElementNames) {
            DataElement newDataElement = new DataElement();
            newDataElement.setName(name);
            newDataElement.setCountryId(countryId);
            dataElementList.add(newDataElement);
        }
        try {
            dataElementList = save(dataElementList);
            dataElementList.forEach(dataElement -> {
                dataElementids.add(dataElement.getId());
            });
        } catch (Exception e) {
            LOGGER.info(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
        result.put(IDS_LIST, dataElementids);
        result.put(DATA_EMELENTS_LIST, dataElementList);
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
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "data element ", id);
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


    public Map<String, Object> updateDataElementAndCreateNewDataElement(Long countryId, List<DataElementDto> dataElementsDto) {

        List<DataElementDto> upadateDataElementsDto = new ArrayList<>();
        List<DataElementDto> createNewDataElementsDto = new ArrayList<>();
        dataElementsDto.forEach(dataElementDto -> {
            if (Optional.ofNullable(dataElementDto.getId()).isPresent()) {
                upadateDataElementsDto.add(dataElementDto);
            } else {
                createNewDataElementsDto.add(dataElementDto);
            }
        });

        Map<String, Object> newDataElements = new HashMap<>();
        Map<String, Object> updatedDataElements = new HashMap<>();
        List<BigInteger> dataElementsIds = new ArrayList<>();
        List<DataElement> dataElementList = new ArrayList<>();
        if (createNewDataElementsDto.size() != 0) {
            newDataElements = createDataElements(countryId, createNewDataElementsDto);
            dataElementsIds.addAll((List<BigInteger>) newDataElements.get(IDS_LIST));
            dataElementList.addAll((List<DataElement>) newDataElements.get(DATA_EMELENTS_LIST));
        }
        if (upadateDataElementsDto.size() != 0) {
            updatedDataElements = updateDataElementsList(countryId, upadateDataElementsDto);
            dataElementsIds.addAll((List<BigInteger>) updatedDataElements.get(IDS_LIST));
            dataElementList.addAll((List<DataElement>) updatedDataElements.get(DATA_EMELENTS_LIST));
        }
        updatedDataElements.put(IDS_LIST, dataElementsIds);
        updatedDataElements.put(DATA_EMELENTS_LIST, dataElementList);
        return updatedDataElements;

    }

    public Map<String, Object> updateDataElementsList(Long countryId, List<DataElementDto> dataElementsDto) {

        Map<BigInteger, DataElementDto> dataElementsDtoList = new HashMap<>();
        List<BigInteger> dataElementsIds = new ArrayList<>();
        dataElementsDto.forEach(dataElementDto -> {
            dataElementsDtoList.put(dataElementDto.getId(), dataElementDto);
            dataElementsIds.add(dataElementDto.getId());

        });

        List<DataElement> dataElementList = dataElementMognoRepository.getAllDataElementListByIds(countryId, dataElementsIds);
        dataElementList.forEach(dataElement -> {

            DataElementDto darElementDto = dataElementsDtoList.get(dataElement.getId());
            dataElement.setName(darElementDto.getName());

        });
        Map<String, Object> result = new HashMap<>();
        try {
            dataElementList = save(dataElementList);
        } catch (Exception e) {
            LOGGER.info(e.getMessage());
            throw new RuntimeException(e.getMessage());

        }

        result.put(IDS_LIST, dataElementsIds);
        result.put(DATA_EMELENTS_LIST, dataElementList);
        return result;
    }

}

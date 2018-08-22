package com.kairos.service.master_data.data_category_element;

import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.gdpr.master_data.DataElementDTO;
import com.kairos.persistance.model.master_data.data_category_element.DataElement;
import com.kairos.persistance.repository.master_data.data_category_element.DataElementMongoRepository;
import com.kairos.service.common.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;

import static com.kairos.constants.AppConstant.IDS_LIST;
import static com.kairos.constants.AppConstant.DATA_ELEMENTS_LIST;


@Service
public class DataElementService extends MongoBaseService {


    Logger LOGGER = LoggerFactory.getLogger(DataElementService.class);

    @Inject
    private ExceptionService exceptionService;

    @Inject
    private DataElementMongoRepository dataElementMongoRepository;



    /**@decription method create new Data Elements throw exception if data element already exist
     * @param countryId
     * @param organizationId
     * @param dataElementsDto request body for creating New Data Elements
     * @return map of Data Elements  List  and new Data Elements ids
     */
    public Map<String, Object> createDataElements(Long countryId, Long organizationId, List<DataElementDTO> dataElementsDto) {

        checkForDuplicacyInName(dataElementsDto);
        List<String> dataElementNames = new ArrayList<>();
        dataElementsDto.forEach(dataElement -> dataElementNames.add(dataElement.getName().trim()));
        List<DataElement> existingDataElement = dataElementMongoRepository.findByCountryIdAndNames(countryId, organizationId, dataElementNames);
        if (existingDataElement.size() != 0) {
            exceptionService.duplicateDataException("message.duplicate", "data element", existingDataElement.iterator().next().getName());
        }
        List<DataElement> dataElementList = new ArrayList<>();
        List<BigInteger> dataElementIdList = new ArrayList<>();
        for (String name : dataElementNames) {
            DataElement newDataElement = new DataElement(name,countryId);
            newDataElement.setOrganizationId(organizationId);
            dataElementList.add(newDataElement);
        }
        try {
            dataElementList = dataElementMongoRepository.saveAll(dataElementList);
            dataElementList.forEach(dataElement -> {
                dataElementIdList.add(dataElement.getId());
            });
        } catch (Exception e) {
            LOGGER.info(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
        Map<String, Object> result = new HashMap<>();
        result.put(IDS_LIST, dataElementIdList);
        result.put(DATA_ELEMENTS_LIST, dataElementList);
        return result;

    }

    public DataElement getDataElement(Long countryId, Long organizationId, BigInteger id) {
        DataElement dataElement = dataElementMongoRepository.findByIdAndNonDeleted(countryId, organizationId, id);
        if (!Optional.ofNullable(dataElement).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "data element", id);
        }
        return dataElement;

    }

    public List<DataElement> getAllDataElements(Long countryId, Long organizationId) {
        return dataElementMongoRepository.getAllDataElement(countryId, organizationId);
    }


    public Boolean deleteDataElement(Long countryId, Long organizationId, BigInteger id) {
        DataElement exist = dataElementMongoRepository.findByIdAndNonDeleted(countryId, organizationId, id);
        if (!Optional.ofNullable(exist).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "data element ", id);
        }
        delete(exist);
        return true;

    }


    public DataElementDTO updateDataElement(Long countryId, Long organizationId, BigInteger id, DataElementDTO dataElementDTO) {

        DataElement dataElement = dataElementMongoRepository.findByIdAndNonDeleted(countryId, organizationId, id);
        if (!Optional.ofNullable(dataElement).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "data element", id);
        }
        dataElement.setName(dataElementDTO.getName());
         dataElementMongoRepository.save(dataElement);
         return dataElementDTO;
    }

    /**
     * @desciption method create new data Data elements and update data Element if data element already exist.
     * @param countryId
     * @param organizationId
     * @param dataElementsDto request body contain list Of Existing Data Elements which needs to be Update and List of New Data Elements
     * @return map of Data Element ids and ,List of  updated and new Data Elements
     */
    public Map<String, Object> updateDataElementAndCreateNewDataElement(Long countryId, Long organizationId, List<DataElementDTO> dataElementsDto) {

        checkForDuplicacyInName(dataElementsDto);
        List<DataElementDTO> updateDataElementsDto = new ArrayList<>();
        List<DataElementDTO> createNewDataElementsDto = new ArrayList<>();
        dataElementsDto.forEach(dataElementDto -> {
            if (Optional.ofNullable(dataElementDto.getId()).isPresent()) {
                updateDataElementsDto.add(dataElementDto);
            } else {
                createNewDataElementsDto.add(dataElementDto);
            }
        });
        Map<String, Object> updatedDataElements = new HashMap<>();
        List<BigInteger> dataElementsIds = new ArrayList<>();
        List<DataElement> dataElementList = new ArrayList<>();
        if (createNewDataElementsDto.size() != 0) {
            Map<String, Object> newDataElements = createDataElements(countryId, organizationId, createNewDataElementsDto);
            dataElementsIds.addAll((List<BigInteger>) newDataElements.get(IDS_LIST));
            dataElementList.addAll((List<DataElement>) newDataElements.get(DATA_ELEMENTS_LIST));
        }
        if (updateDataElementsDto.size() != 0) {
            updatedDataElements = updateDataElementsList(countryId, organizationId, updateDataElementsDto);
            dataElementsIds.addAll((List<BigInteger>) updatedDataElements.get(IDS_LIST));
            dataElementList.addAll((List<DataElement>) updatedDataElements.get(DATA_ELEMENTS_LIST));
        }
        updatedDataElements.put(IDS_LIST, dataElementsIds);
        updatedDataElements.put(DATA_ELEMENTS_LIST, dataElementList);
        return updatedDataElements;

    }


    /**
     * @desciption update list of Existing Data elements,
     * dataElementsDtoMap   contain data element corresponding to its id
     * @param countryId
     * @param organizationId
     * @param dataElementsDto request body for updating Existing Data Elements List
     * @return  map of Data Elements contain ids List and updated  Data Elements List
     */
    public Map<String, Object> updateDataElementsList(Long countryId, Long organizationId, List<DataElementDTO> dataElementsDto) {

        Map<BigInteger, DataElementDTO> dataElementsDtoMap = new HashMap<>();
        List<BigInteger> dataElementsIds = new ArrayList<>();
        List<String> dataElementsNames = new ArrayList<>();
        dataElementsDto.forEach(dataElementDto -> {
            dataElementsDtoMap.put(dataElementDto.getId(), dataElementDto);
            dataElementsIds.add(dataElementDto.getId());
            dataElementsNames.add(dataElementDto.getName());
        });
        checkDuplicateInsertionOnUpdatingDataElements(countryId, organizationId, dataElementsDtoMap, dataElementsNames);
        List<DataElement> dataElementList = dataElementMongoRepository.getAllDataElementListByIds(countryId, organizationId, dataElementsIds);
        dataElementList.forEach(dataElement -> {
            DataElementDTO darElementDto = dataElementsDtoMap.get(dataElement.getId());
            dataElement.setName(darElementDto.getName());
        });
        Map<String, Object> result = new HashMap<>();
        try {
            dataElementList = dataElementMongoRepository.saveAll(dataElementList);
        } catch (Exception e) {
            LOGGER.info(e.getMessage());
            throw new RuntimeException(e.getMessage());

        }

        result.put(IDS_LIST, dataElementsIds);
        result.put(DATA_ELEMENTS_LIST, dataElementList);
        return result;
    }


    public void checkForDuplicacyInName(List<DataElementDTO> dataElementDTOs) {
        List<String> names = new ArrayList<>();
        dataElementDTOs.forEach(dataElementDTO -> {
            if (names.contains(dataElementDTO.getName())) {
                throw new DuplicateDataException("Duplicate Entry with name " + dataElementDTO.getName());
            }
            names.add(dataElementDTO.getName());
        });


    }

    /**@description check if data element already exist with same name ,and map not contain id then throw exception.
     * @param countryId
     * @param dataElementDtoMap map contain dataElement corresponding to id
     * @param dataElementNames  list of data elements names which we need to check if duplicate data present on updating existing Data elements
     */
    public void checkDuplicateInsertionOnUpdatingDataElements(Long countryId, Long orgId, Map<BigInteger, DataElementDTO> dataElementDtoMap, List<String> dataElementNames) {

        List<DataElement> dataElementList = dataElementMongoRepository.findByCountryIdAndNames(countryId, orgId, dataElementNames);
        dataElementList.forEach(dataElement -> {
            if (!dataElementDtoMap.containsKey(dataElement.getId())) {
                exceptionService.duplicateDataException("message.duplicate", "data element", dataElement.getName());
            } else {
                if (!dataElementDtoMap.get(dataElement.getId()).getName().equals(dataElement.getName())) {
                    exceptionService.duplicateDataException("message.duplicate", "data element", dataElement.getName());
                }
            }
        });
    }


}

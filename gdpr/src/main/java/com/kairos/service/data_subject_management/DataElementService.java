package com.kairos.service.data_subject_management;

import com.kairos.commons.custom_exception.DuplicateDataException;
import com.kairos.dto.gdpr.master_data.DataElementDTO;
import com.kairos.persistence.model.master_data.data_category_element.DataCategory;
import com.kairos.persistence.model.master_data.data_category_element.DataElement;
import com.kairos.persistence.repository.master_data.data_category_element.DataElementRepository;
import com.kairos.service.exception.ExceptionService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.*;


@Service
public class DataElementService{


    Logger LOGGER = LoggerFactory.getLogger(DataElementService.class);

    @Inject
    private ExceptionService exceptionService;

    @Inject
    private DataElementRepository dataElementRepository;


    /**
     * @param referenceId     reference id may be country id or unitId
     * @param dataElementsDto request body for creating New Data Elements
     * @return map of Data Elements  List  and new Data Elements ids
     * @decription method create new Data Elements throw exception if data element already exist
     */
    public List<DataElement> createDataElements(Long referenceId, boolean isUnitId, List<DataElementDTO> dataElementsDto, DataCategory dataCategory) {

        Set<String> dataElementNames = checkForDuplicacyInName(dataElementsDto);
        List<DataElement> existingDataElement = isUnitId ? dataElementRepository.findByUnitIdAndNames(referenceId, dataElementNames) : dataElementRepository.findByCountryIdAndNames(referenceId, dataElementNames);
        if (CollectionUtils.isNotEmpty(existingDataElement)) {
            exceptionService.duplicateDataException("message.duplicate", "data element", existingDataElement.iterator().next().getName());
        }
        List<DataElement> dataElementList = new ArrayList<>();
        for (String name : dataElementNames) {
            DataElement dataElement = new DataElement(name);
            if (isUnitId)
                dataElement.setOrganizationId(referenceId);
            else
                dataElement.setCountryId(referenceId);
            dataElementList.add(dataElement);

        }
        return dataElementList;
    }


    /**
     * @param referenceId     = unitId or countryId
     * @param dataElementsDto request body contain list Of Existing Data Elements which needs to be Update and List of New Data Elements
     * @return map of Data Element ids and ,List of  updated and new Data Elements
     * @desciption method create new data Data elements and update data Element if data element already exist.
     */

    //TODO need to refactor
    public List<DataElement> updateDataElementAndCreateNewDataElement(Long referenceId, boolean isUnitId, List<DataElementDTO> dataElementsDto) {

        Set<String> dataElementNames = checkForDuplicacyInName(dataElementsDto);
        Map<Long, DataElementDTO> dataElementDTOMap = new HashMap<>();
        List<DataElement> dataElements = new ArrayList<>();
        dataElementsDto.forEach(dataElementDto -> {
            if (Optional.ofNullable(dataElementDto.getId()).isPresent()) {
                dataElementDTOMap.put(dataElementDto.getId(), dataElementDto);
            } else {
                DataElement dataElement = new DataElement(dataElementDto.getName());
                if (isUnitId)
                    dataElement.setOrganizationId(referenceId);
                else
                    dataElement.setCountryId(referenceId);
                dataElements.add(dataElement);
            }
        });
        List<DataElement> previousDataElementList = isUnitId ? dataElementRepository.findByUnitIdAndNames(referenceId, dataElementNames) : dataElementRepository.findByCountryIdAndNames(referenceId, dataElementNames);
        previousDataElementList.forEach(dataElement -> {

            if (!dataElementDTOMap.containsKey(dataElement.getId())) {
                exceptionService.duplicateDataException("message.duplicate", "Data Element", dataElement.getName());
            }
        });
        previousDataElementList = isUnitId ? dataElementRepository.findByUnitIdAndIds(referenceId, dataElementDTOMap.keySet()) : dataElementRepository.findByCountryIdAndIds(referenceId, dataElementDTOMap.keySet());
        previousDataElementList.forEach(dataElement -> dataElement.setName(dataElementDTOMap.get(dataElement.getId()).getName()));
        dataElements.addAll(previousDataElementList);
        dataElementRepository.saveAll(dataElements);
        return dataElements;
    }



    public Set<String> checkForDuplicacyInName(List<DataElementDTO> dataElementDTOs) {

        Set<String> dataElementNames = new HashSet<>();
        List<String> dataElementNamesLowerCase = new ArrayList<>();
        dataElementDTOs.forEach(dataElementDTO -> {
            if (dataElementNamesLowerCase.contains(dataElementDTO.getName().toLowerCase())) {
                throw new DuplicateDataException("Duplicate Entry with name " + dataElementDTO.getName());
            }
            dataElementNames.add(dataElementDTO.getName());
            dataElementNamesLowerCase.add(dataElementDTO.getName().toLowerCase());
        });
        return dataElementNames;
    }



    /**
     * @param countryId
     * @return get country data elements
     */
   /* public List<DataElement> getAllDataElementByCountryId(Long countryId) {
        return dataElementMongoRepository.getAllDataElementByCountryId(countryId);
    }*/


    /**
     * @param unitId
     * @return get organizational data elements
     */
   /* public List<DataElement> getAllDataElementByUnitId(Long unitId) {
        return dataElementMongoRepository.getAllDataElementByUnitId(unitId);
    }*/


   /* public Boolean deleteDataElementById(BigInteger dataElementId) {
        dataElementMongoRepository.safeDeleteById(dataElementId);
        return true;

    }*/

    /*public DataElement getDataElementById(BigInteger dataElementId) {
        DataElement dataElement = dataElementMongoRepository.getByIdAndNonDeleted(dataElementId);
        if (!Optional.ofNullable(dataElement).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "data element", dataElementId);
        }
        return dataElement;

    }*/


}

package com.kairos.service.data_subject_management;

import com.kairos.dto.gdpr.master_data.DataCategoryDTO;
import com.kairos.persistence.model.master_data.data_category_element.DataCategory;
import com.kairos.persistence.model.master_data.data_category_element.DataElement;
import com.kairos.persistence.repository.master_data.data_category_element.DataCategoryMongoRepository;
import com.kairos.persistence.repository.master_data.data_category_element.DataElementMongoRepository;
import com.kairos.persistence.repository.master_data.data_category_element.DataSubjectMappingRepository;
import com.kairos.response.dto.master_data.data_mapping.DataCategoryResponseDTO;
import com.kairos.response.dto.master_data.data_mapping.DataSubjectMappingBasicResponseDTO;
import com.kairos.service.common.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class DataCategoryService extends MongoBaseService {

    private Logger LOGGER = LoggerFactory.getLogger(DataCategoryService.class);

    @Inject
    private DataCategoryMongoRepository dataCategoryMongoRepository;

    @Inject
    private ExceptionService exceptionService;

    @Inject
    private DataElementService dataElementService;

    @Inject
    private DataElementMongoRepository dataElementMongoRepository;

    @Inject
    private DataSubjectMappingRepository dataSubjectMappingRepository;


    /**
     * @param referenceId
     * @param dataCategoryDto contain data category name and list of data elements
     * @return return data category object.
     * @descitpion this method create new data category and add new data elements to data Category
     */
    public DataCategoryDTO saveDataCategoryAndDataElement(Long referenceId, boolean isUnitId, DataCategoryDTO dataCategoryDto) {

        DataCategory previousDataCategory = isUnitId ? dataCategoryMongoRepository.findByUnitIdAndName(referenceId, dataCategoryDto.getName()) : dataCategoryMongoRepository.findByCountryIdName(referenceId, dataCategoryDto.getName());
        if (Optional.ofNullable(previousDataCategory).isPresent()) {
            exceptionService.duplicateDataException("message.duplicate", "data category", dataCategoryDto.getName());
        }
        List<DataElement> dataElementList = dataElementService.createDataElements(referenceId, isUnitId, dataCategoryDto.getDataElements());
        DataCategory dataCategory = new DataCategory(dataCategoryDto.getName(), dataElementList.stream().map(DataElement::getId).collect(Collectors.toList()));
        if (isUnitId)
            dataCategory.setOrganizationId(referenceId);
        else
            dataCategory.setCountryId(referenceId);
        dataCategoryMongoRepository.save(dataCategory);
        dataCategoryDto.setId(dataCategory.getId());
        return dataCategoryDto;
    }


    /**
     * @param referenceId
     * @param dataCategoryId  id of Data Category
     * @param dataCategoryDto request body of Data Category contains List of Existing Data Elements and New Data Elements
     * @return Data category with updated data elements and new Data Elements
     * @description this method update data category , data elements and create new data elements if add to data category
     */
    public DataCategoryDTO updateDataCategoryAndDataElement(Long referenceId, boolean isUnitId, BigInteger dataCategoryId, DataCategoryDTO dataCategoryDto) {

        DataCategory dataCategory = isUnitId ? dataCategoryMongoRepository.findByUnitIdAndName(referenceId, dataCategoryDto.getName()) : dataCategoryMongoRepository.findByCountryIdName(referenceId, dataCategoryDto.getName());
        if (Optional.ofNullable(dataCategory).isPresent() && !dataCategoryId.equals(dataCategory.getId())) {
            exceptionService.duplicateDataException("message.duplicate", "data category", dataCategoryDto.getName());
        }
        dataCategory = dataCategoryMongoRepository.findOne(dataCategoryId);
        if (!Optional.ofNullable(dataCategory).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "data category", dataCategoryId);
        }
        List<DataElement> dataElements = dataElementService.updateDataElementAndCreateNewDataElement(referenceId, isUnitId, dataCategoryDto.getDataElements());
        dataCategory.setName(dataCategoryDto.getName());
        dataCategory.setDataElements(dataElements.stream().map(DataElement::getId).collect(Collectors.toList()));
        dataCategoryMongoRepository.save(dataCategory);

        return dataCategoryDto;

    }


    public boolean deleteDataCatgeoryById(Long refrenceId, boolean isUnitId, BigInteger dataCatgeoryId) {

        List<DataSubjectMappingBasicResponseDTO> dataSubjectLinkedWithDataCategory = isUnitId ? dataSubjectMappingRepository.findDataSubjectsLinkWithDataCategoryByUnitIdAndDataCategoryId(refrenceId, dataCatgeoryId)
                : dataSubjectMappingRepository.findDataSubjectsLinkWithDataCategoryByCountryIdAndDataCategoryId(refrenceId, dataCatgeoryId);
        if (CollectionUtils.isNotEmpty(dataSubjectLinkedWithDataCategory)) {
            exceptionService.invalidRequestException("message.cannot.delete.dataCategory", dataSubjectLinkedWithDataCategory.stream().map(DataSubjectMappingBasicResponseDTO::getName).collect(Collectors.joining(",")));
        }
        DataCategory dataCategory = dataCategoryMongoRepository.findOne(dataCatgeoryId);
        if (!Optional.ofNullable(dataCategory).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "data category", dataCatgeoryId);
        }
        dataCategoryMongoRepository.safeDeleteById(dataCatgeoryId);
        dataElementMongoRepository.safeDeleteByIds(new HashSet<>(dataCategory.getDataElements()));
        return true;

    }


    /**
     * @param countryId
     * @param dataCategoryId data category id
     * @return return data category with its data elements
     */
    public DataCategoryResponseDTO getDataCategoryWithDataElementByCountryIdAndId(Long countryId, BigInteger dataCategoryId) {
        DataCategoryResponseDTO dataCategory = dataCategoryMongoRepository.getDataCategoryWithDataElementById(countryId, dataCategoryId);
        if (!Optional.ofNullable(dataCategory).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "data category", dataCategoryId);
        }
        return dataCategory;

    }

    /**
     * @param unitId
     * @param dataCategoryId data category id
     * @return return data category with its data elements
     */
    public DataCategoryResponseDTO getDataCategoryWithDataElementByUnitIdAndId(Long unitId, BigInteger dataCategoryId) {
        DataCategoryResponseDTO dataCategory = dataCategoryMongoRepository.getDataCategoryWithDataElementByUnitIdAndId(unitId, dataCategoryId);
        if (!Optional.ofNullable(dataCategory).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "data category", dataCategoryId);
        }
        return dataCategory;

    }


    /**
     * @param countryId
     * @return return list of Data Category with data Elements
     */
    public List<DataCategoryResponseDTO> getAllDataCategoryWithDataElementByCountryId(Long countryId) {
        return dataCategoryMongoRepository.getAllDataCategoryWithDataElement(countryId);
    }


    /**
     * @param unitId
     * @return return list of Data Category with data Elements
     */
    public List<DataCategoryResponseDTO> getAllDataCategoryWithDataElementByUnitId(Long unitId) {
        return dataCategoryMongoRepository.getAllDataCategoryWithDataElementByUnitId(unitId);
    }


}

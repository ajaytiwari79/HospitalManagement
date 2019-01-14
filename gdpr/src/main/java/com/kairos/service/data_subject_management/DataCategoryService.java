package com.kairos.service.data_subject_management;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.gdpr.master_data.DataCategoryDTO;
import com.kairos.persistence.model.master_data.data_category_element.DataCategory;
import com.kairos.persistence.model.master_data.data_category_element.DataCategoryMD;
import com.kairos.persistence.model.master_data.data_category_element.DataElement;
import com.kairos.persistence.model.master_data.data_category_element.DataElementMD;
import com.kairos.persistence.repository.master_data.data_category_element.DataCategoryMongoRepository;
import com.kairos.persistence.repository.master_data.data_category_element.DataCategoryRepository;
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

    @Inject
    private DataCategoryRepository dataCategoryRepository;


    /**
     * @param referenceId
     * @param dataCategoryDto contain data category name and list of data elements
     * @return return data category object.
     * @descitpion this method create new data category and add new data elements to data Category
     */
    public DataCategoryDTO saveDataCategoryAndDataElement(Long referenceId, boolean isUnitId, DataCategoryDTO dataCategoryDto) {

        DataCategoryMD previousDataCategory = isUnitId ? dataCategoryRepository.findByUnitIdAndName(referenceId, dataCategoryDto.getName()) : dataCategoryRepository.findByCountryIdName(referenceId, dataCategoryDto.getName());
        if (Optional.ofNullable(previousDataCategory).isPresent()) {
            exceptionService.duplicateDataException("message.duplicate", "data category", dataCategoryDto.getName());
        }

        DataCategoryMD dataCategory = new DataCategoryMD(dataCategoryDto.getName());
        if (isUnitId)
            dataCategory.setOrganizationId(referenceId);
        else
            dataCategory.setCountryId(referenceId);
        List<DataElementMD> dataElementList = dataElementService.createDataElements(referenceId, isUnitId, dataCategoryDto.getDataElements(), dataCategory);
        dataCategory.setDataElements(dataElementList);
        dataCategoryRepository.save(dataCategory);
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
    public DataCategoryDTO updateDataCategoryAndDataElement(Long referenceId, boolean isUnitId, Long dataCategoryId, DataCategoryDTO dataCategoryDto) {

        DataCategoryMD dataCategory = isUnitId ? dataCategoryRepository.findByUnitIdAndName(referenceId, dataCategoryDto.getName()) : dataCategoryRepository.findByCountryIdName(referenceId, dataCategoryDto.getName());
        if (Optional.ofNullable(dataCategory).isPresent() && !dataCategoryId.equals(dataCategory.getId())) {
            exceptionService.duplicateDataException("message.duplicate", "data category", dataCategoryDto.getName());
        }
        dataCategory = dataCategoryRepository.getOne(dataCategoryId);
        if (!Optional.ofNullable(dataCategory).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "data category", dataCategoryId);
        }
        List<DataElementMD> dataElements = dataElementService.updateDataElementAndCreateNewDataElement(referenceId, isUnitId, dataCategoryDto.getDataElements());
        dataCategory.setName(dataCategoryDto.getName());
       // dataCategory.setDataElements(dataElements);
        dataCategoryRepository.save(dataCategory);

        return dataCategoryDto;

    }


    public boolean deleteDataCategoryById(Long refrenceId, boolean isUnitId, BigInteger dataCategoryId) {

        List<DataSubjectMappingBasicResponseDTO> dataSubjectLinkedWithDataCategory = isUnitId ? dataSubjectMappingRepository.findDataSubjectsLinkWithDataCategoryByUnitIdAndDataCategoryId(refrenceId, dataCategoryId)
                : dataSubjectMappingRepository.findDataSubjectsLinkWithDataCategoryByCountryIdAndDataCategoryId(refrenceId, dataCategoryId);
        if (CollectionUtils.isNotEmpty(dataSubjectLinkedWithDataCategory)) {
            exceptionService.invalidRequestException("message.cannot.delete.dataCategory", dataSubjectLinkedWithDataCategory.stream().map(DataSubjectMappingBasicResponseDTO::getName).collect(Collectors.joining(",")));
        }
        DataCategory dataCategory = dataCategoryMongoRepository.findOne(dataCategoryId);
        if (!Optional.ofNullable(dataCategory).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "data category", dataCategoryId);
        }
        dataCategoryMongoRepository.safeDeleteById(dataCategoryId);
        dataElementMongoRepository.safeDeleteByIds(new HashSet<>(dataCategory.getDataElements()));
        return true;

    }


    /**
     * @param countryId
     * @param dataCategoryId data category id
     * @return return data category with its data elements
     */
    public DataCategoryResponseDTO getDataCategoryWithDataElementByCountryIdAndId(Long countryId, Long dataCategoryId) {
        DataCategoryMD dataCategory = dataCategoryRepository.getDataCategoryByCountryIdAndId(countryId, dataCategoryId);
        if (!Optional.ofNullable(dataCategory).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "data category", dataCategoryId);
        }
        return ObjectMapperUtils.copyPropertiesByMapper(dataCategory , DataCategoryResponseDTO.class);

    }

    /**
     * @param unitId
     * @param dataCategoryId data category id
     * @return return data category with its data elements
     */
    public DataCategoryResponseDTO getDataCategoryWithDataElementByUnitIdAndId(Long unitId, Long dataCategoryId) {
        DataCategoryMD dataCategory = dataCategoryRepository.getDataCategoryByUnitIdAndId(unitId, dataCategoryId);
        if (!Optional.ofNullable(dataCategory).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "data category", dataCategoryId);
        }
        return ObjectMapperUtils.copyPropertiesByMapper(dataCategory , DataCategoryResponseDTO.class);

    }


    /**
     * @param countryId
     * @return return list of Data Category with data Elements
     */
    public List<DataCategoryResponseDTO> getAllDataCategoryWithDataElementByCountryId(Long countryId) {
        List<DataCategoryMD> dataCategories = dataCategoryRepository.getAllDataCategoriesByCountryId(countryId);
        return ObjectMapperUtils.copyPropertiesOfListByMapper(dataCategories, DataCategoryResponseDTO.class);
    }


    /**
     * @param unitId
     * @return return list of Data Category with data Elements
     */
    public List<DataCategoryResponseDTO> getAllDataCategoryWithDataElementByUnitId(Long unitId) {
        List<DataCategoryMD> dataCategories = dataCategoryRepository.getAllDataCategoriesByUnitId(unitId);
        return ObjectMapperUtils.copyPropertiesOfListByMapper(dataCategories, DataCategoryResponseDTO.class);
    }


    /**
     * @param ids LIst of data category ids
     * @return return list of Data Category with data Elements
     */
    public List<DataCategoryMD> getAllDataCategoriesByIds(Set<Long> ids) {
        List<DataCategoryMD> dataCategories = dataCategoryRepository.getAllDataCategoriesByIds(ids);
        return dataCategories;
    }

}

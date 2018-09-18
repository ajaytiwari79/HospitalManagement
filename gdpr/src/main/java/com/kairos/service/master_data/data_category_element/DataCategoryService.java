package com.kairos.service.master_data.data_category_element;

import com.kairos.dto.gdpr.master_data.DataCategoryDTO;
import com.kairos.persistence.model.master_data.data_category_element.DataCategory;
import com.kairos.persistence.model.master_data.data_category_element.DataElement;
import com.kairos.persistence.repository.master_data.data_category_element.DataCategoryMongoRepository;
import com.kairos.persistence.repository.master_data.data_category_element.DataElementMongoRepository;
import com.kairos.response.dto.master_data.data_mapping.DataCategoryResponseDTO;
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


    /**
     * @param countryId
     * @param dataCategoryDto contain data category name and list of data elements
     * @return return data category object.
     * @descitpion this method create new data category and add new data elements to data Category
     */
    public DataCategoryDTO addDataCategoryAndDataElement(Long countryId, DataCategoryDTO dataCategoryDto) {

        DataCategory previousDataCategory = dataCategoryMongoRepository.findByName(countryId, dataCategoryDto.getName());
        if (Optional.ofNullable(previousDataCategory).isPresent()) {
            exceptionService.duplicateDataException("message.duplicate", "data category", dataCategoryDto.getName());
        }
        Map<String, Object> dataElementList = dataElementService.createDataElements(countryId, dataCategoryDto.getDataElements());
        try {
            DataCategory dataCategory = new DataCategory(dataCategoryDto.getName(), (List<BigInteger>) dataElementList.get(IDS_LIST), countryId);
            dataCategoryMongoRepository.save(dataCategory);
            dataCategoryDto.setId(dataCategory.getId());
        } catch (Exception e) {
            LOGGER.warn(e.getMessage());
            dataElementMongoRepository.deleteAll((List<DataElement>) dataElementList.get(DATA_ELEMENTS_LIST));
        }
        return dataCategoryDto;
    }


    public Boolean deleteDataCategory(Long countryId, BigInteger dataCategoryId) {
        DataCategory dataCategory = dataCategoryMongoRepository.findByIdAndNonDeleted(countryId, dataCategoryId);
        if (!Optional.ofNullable(dataCategory).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "data category", dataCategoryId);
        }
        delete(dataCategory);
        return true;

    }


    /**
     * @param countryId
     * @param dataCategoryId             data category id
     * @return return data category with its data elements
     */
    public DataCategoryResponseDTO getDataCategoryWithDataElement(Long countryId, BigInteger dataCategoryId) {
        DataCategoryResponseDTO dataCategory = dataCategoryMongoRepository.getDataCategoryWithDataElementById(countryId, dataCategoryId);
        if (!Optional.ofNullable(dataCategory).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "data category", dataCategoryId);
        }
        return dataCategory;

    }

    /**
     * @param countryId
     * @return return list of Data Category with data Elements
     */
    public List<DataCategoryResponseDTO> getAllDataCategoryWithDataElement(Long countryId) {
        return dataCategoryMongoRepository.getAllDataCategoryWithDataElement(countryId);
    }

    /**
     * @param countryId
     * @param dataCategoryId             data category id
     * @return return data category with its data elements
     */
    public DataCategoryResponseDTO getDataCategoryWithDataElementOnLeftHierarchySelectionById(Long countryId, BigInteger dataCategoryId) {
        DataCategoryResponseDTO dataCategory = dataCategoryMongoRepository.getDataCategoryWithDataElementById(countryId, dataCategoryId);
        if (!Optional.ofNullable(dataCategory).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "data category", dataCategoryId);
        }
        return dataCategory;

    }


    /**
     * @param unitId
     * @return return list of Data Category with data Elements
     */
    public List<DataCategoryResponseDTO> getAllDataCategoryWithDataElementOnLeftHierarchySelection(Long unitId) {
        return dataCategoryMongoRepository.getAllDataCategoryWithDataElementByUnitId(unitId);
    }



    /**
     * @param countryId
     * @param ids            ids of Data category
     * @return return list of Data category
     */
    public List<DataCategory> getDataCategoryByIds(Long countryId, Set<BigInteger> ids) {
        List<DataCategory> dataCategories = dataCategoryMongoRepository.findDataCategoryByIds(countryId, ids);
        Set<BigInteger> dataCategoryIds = new HashSet<>();
        dataCategories.forEach(dataCategory -> dataCategoryIds.add(dataCategory.getId()));
        if (dataCategoryIds.size() != ids.size()) {
            ids.removeAll(dataCategoryIds);
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "data category", ids.iterator().next());
        }
        return dataCategories;

    }


    /**
     * @param countryId
     * @param id              id of Data Category
     * @param dataCategoryDto request body of Data Category contains List of Existing Data Elements and New Data Elements
     * @return Data category with updated data elements and new Data Elements
     * @description this method update data category , data elements and create new data elements if add to data category
     */
    public DataCategoryDTO updateDataCategoryAndDataElement(Long countryId, BigInteger id, DataCategoryDTO dataCategoryDto) {

        DataCategory dataCategory = dataCategoryMongoRepository.findByName(countryId, dataCategoryDto.getName());
        if (Optional.ofNullable(dataCategory).isPresent() && !id.equals(dataCategory.getId())) {
            exceptionService.duplicateDataException("message.duplicate", "data category", dataCategoryDto.getName());
        }
        dataCategory = dataCategoryMongoRepository.findByid(id);
        if (!Optional.ofNullable(dataCategory).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "data category", id);
        }
        Map<String, Object> dataElementListMap = dataElementService.updateDataElementAndCreateNewDataElement(countryId, dataCategoryDto.getDataElements());
        try {
            dataCategory.setName(dataCategoryDto.getName());
            dataCategory.setDataElements((List<BigInteger>) dataElementListMap.get(IDS_LIST));
            dataCategoryMongoRepository.save(dataCategory);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage());
            dataElementMongoRepository.deleteAll((List<DataElement>) dataElementListMap.get(DATA_ELEMENTS_LIST));
        }

        return dataCategoryDto;

    }


}

package com.kairos.service.master_data_management.data_category_element;

import com.kairos.dto.master_data.DataCategoryDTO;
import com.kairos.persistance.model.master_data_management.data_category_element.DataCategory;
import com.kairos.persistance.model.master_data_management.data_category_element.DataElement;
import com.kairos.persistance.repository.master_data_management.data_category_element.DataCategoryMongoRepository;
import com.kairos.persistance.repository.master_data_management.data_category_element.DataElementMognoRepository;
import com.kairos.response.dto.master_data.data_mapping.DataCategoryResponseDto;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;

import static com.kairos.constants.AppConstant.IDS_LIST;
import static com.kairos.constants.AppConstant.DATA_EMELENTS_LIST;


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
    private DataElementMognoRepository dataElementMognoRepository;


    /**
     *
     * @param countryId
     * @param dataCategoryDto
     * @return
     */
    public DataCategory addDataCategoryAndDataElement(Long countryId,Long organizationId,DataCategoryDTO dataCategoryDto) {

        DataCategory dataCategory = dataCategoryMongoRepository.findByName(countryId,organizationId,dataCategoryDto.getName());
        if (Optional.ofNullable(dataCategory).isPresent()) {
            exceptionService.duplicateDataException("message.duplicate", "data category", dataCategoryDto.getName());
        }
        Map<String, Object> dataElementList = dataElementService.createDataElements(countryId,organizationId,dataCategoryDto.getDataElements());
        try {
            DataCategory newDataCategory = new DataCategory();
            newDataCategory.setCountryId(countryId);
            newDataCategory.setName(dataCategoryDto.getName());
            newDataCategory.setOrganizationId(organizationId);
            newDataCategory.setDataElements((List<BigInteger>) dataElementList.get(IDS_LIST));
            dataCategory = save(newDataCategory);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage());
            dataElementMognoRepository.deleteAll((List<DataElement>) dataElementList.get(DATA_EMELENTS_LIST));
        }
        return dataCategory;
    }


    public Boolean deleteDataCategory(Long countryId,Long organizationId,BigInteger id) {
        DataCategory dataCategory = dataCategoryMongoRepository.findByIdAndNonDeleted(countryId,organizationId,id);
        if (!Optional.ofNullable(dataCategory).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "data category", id);
        }
        dataCategory.setDeleted(true);
        save(dataCategory);
        return true;

    }


    //get data  category with data element
    public DataCategoryResponseDto getDataCategoryWithDataElement(Long countryId,Long organizationId,BigInteger id) {
        DataCategoryResponseDto dataCategory = dataCategoryMongoRepository.getDataCategoryWithDataElementById(countryId,organizationId,id);
        if (!Optional.ofNullable(dataCategory).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "data category", id);
        }
        return dataCategory;

    }

    //get data  category with data element
    public List<DataCategoryResponseDto> getAllDataCategoryWithDataElement(Long countryId,Long organizationId) {
        return dataCategoryMongoRepository.getAllDataCategoryWithDataElement(countryId,organizationId);
    }


    //get data  category with data element
    public List<DataCategory> getDataCategoryByIds(Long countryId,Long organizationId,Set<BigInteger> ids) {
        List<DataCategory> dataCategories = dataCategoryMongoRepository.findDataCategoryByIds(countryId,organizationId,ids);
        Set<BigInteger> dataCategoryIds = new HashSet<>();
        dataCategories.forEach(dataCategory -> {
            dataCategoryIds.add(dataCategory.getId());
        });
        if (dataCategoryIds.size() != ids.size()) {
            ids.removeAll(dataCategoryIds);
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "data category", ids.iterator().next());
        }
        return dataCategories;

    }


    public DataCategory updateDataCategoryAndDataElement(Long countryId,Long organizationId,BigInteger id, DataCategoryDTO dataCategoryDto) {

        DataCategory dataCategory = dataCategoryMongoRepository.findByName(countryId,organizationId,dataCategoryDto.getName());
        if (Optional.ofNullable(dataCategory).isPresent() && !id.equals(dataCategory.getId())) {
            exceptionService.duplicateDataException("message.duplicate", "data category", dataCategoryDto.getName());
        }
        dataCategory = dataCategoryMongoRepository.findByid(id);
        if (!Optional.ofNullable(dataCategory).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "data category", id);
        }
        Map<String, Object> dataElementListMap = dataElementService.updateDataElementAndCreateNewDataElement(countryId,organizationId,dataCategoryDto.getDataElements());
        try {
            dataCategory.setName(dataCategoryDto.getName());
            dataCategory.setDataElements((List<BigInteger>) dataElementListMap.get(IDS_LIST));
            dataCategory = save(dataCategory);

        } catch (Exception e) {
            LOGGER.warn(e.getMessage());
            dataElementMognoRepository.deleteAll((List<DataElement>) dataElementListMap.get(DATA_EMELENTS_LIST));
        }

        return dataCategory;

    }


}

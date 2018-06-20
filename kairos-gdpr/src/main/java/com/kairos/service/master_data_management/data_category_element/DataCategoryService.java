package com.kairos.service.master_data_management.data_category_element;

import com.kairos.dto.master_data.DataCategoryDto;
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

import static com.kairos.constant.AppConstant.IDS_LIST;
import static com.kairos.constant.AppConstant.DATA_EMELENTS_LIST;


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


    /*add data category with multiple data element, create data element and get ids from data element service
     */
    public DataCategory addDataCategoryAndDataElement(Long countryId, DataCategoryDto dataCategoryDto) {

        DataCategory dataCategory = dataCategoryMongoRepository.findByCountryIdAndName(countryId, dataCategoryDto.getName());
        if (Optional.ofNullable(dataCategory).isPresent()) {
            exceptionService.duplicateDataException("message.duplicate", "data category", dataCategoryDto.getName());
        }
        Map<String, Object> dataElementList = new HashMap<>();
        dataElementList = dataElementService.createDataElements(countryId, dataCategoryDto.getDataElements());
        try {
            DataCategory newDataCategory = new DataCategory();
            newDataCategory.setCountryId(countryId);
            newDataCategory.setName(dataCategoryDto.getName());
            newDataCategory.setDataElements((List<BigInteger>) dataElementList.get(IDS_LIST));
            dataCategory = save(newDataCategory);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage());
            dataElementMognoRepository.deleteAll((List<DataElement>) dataElementList.get(DATA_EMELENTS_LIST));
        }
        return dataCategory;
    }


    public Boolean deleteDataCategory(Long countryId, BigInteger id) {
        DataCategory dataCategory = dataCategoryMongoRepository.findByIdAndNonDeleted(countryId, id);
        if (!Optional.ofNullable(dataCategory).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "data category", id);
        }
        dataCategory.setDeleted(true);
        save(dataCategory);
        return true;

    }


    //get data  category with data element
    public DataCategoryResponseDto getDataCategoryWithDataElement(Long countryId, BigInteger id) {
        DataCategoryResponseDto dataCategory = dataCategoryMongoRepository.getDataCategoryWithDataElementById(countryId, id);
        if (!Optional.ofNullable(dataCategory).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "data category", id);
        }
        return dataCategory;

    }

    //get data  category with data element
    public List<DataCategoryResponseDto> getAllDataCategoryWithDataElement(Long countryId) {
        return dataCategoryMongoRepository.getAllDataCategoryWithDataElement(countryId);
    }


    //get data  category with data element
    public List<DataCategory> getDataCategoryByIds(Long countryId, Set<BigInteger> ids) {
        List<DataCategory> dataCategories = dataCategoryMongoRepository.findDataCategoryByIds(countryId, ids);
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


    public DataCategory updateDataCategoryAndDataElement(Long countryId, BigInteger id, DataCategoryDto dataCategoryDto) {

        DataCategory dataCategory = dataCategoryMongoRepository.findByCountryIdAndName(countryId, dataCategoryDto.getName());
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
            dataCategory = save(dataCategory);
            throw new RuntimeException();

        } catch (Exception e) {
            LOGGER.warn(e.getMessage());
            dataElementMognoRepository.deleteAll((List<DataElement>) dataElementListMap.get(DATA_EMELENTS_LIST));
        }

        return dataCategory;

    }


}

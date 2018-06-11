package com.kairos.service.master_data_management.data_category_element;


import com.kairos.dto.master_data.DataSubjectMappingDto;
import com.kairos.persistance.model.master_data_management.data_category_element.DataCategory;
import com.kairos.persistance.model.master_data_management.data_category_element.DataSubjectMapping;
import com.kairos.persistance.repository.master_data_management.data_category_element.DataSubjectMappingRepository;
import com.kairos.persistance.repository.master_data_management.processing_activity_masterdata.DataSubjectMongoRepository;
import com.kairos.response.dto.master_data.DataSubjectMappingResponseDto;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Service
public class DataSubjectMappingService extends MongoBaseService {


    Logger LOGGER = LoggerFactory.getLogger(DataSubjectMappingService.class);

    @Inject
    private DataSubjectMappingRepository dataSubjectMappingRepository;

    @Inject
    private DataCategoryService dataCategoryService;

    @Inject
    private ExceptionService exceptionService;


    public DataSubjectMapping addDataSubjectAndMapping(Long countryId, DataSubjectMappingDto dataSubjectMappingDto) {

        DataSubjectMapping existing = dataSubjectMappingRepository.findByCountryIdAndName(countryId, dataSubjectMappingDto.getName());
        if (Optional.ofNullable(existing).isPresent()) {
            exceptionService.duplicateDataException("message.duplicate", "data subject", dataSubjectMappingDto.getName());
        }
        dataCategoryService.getDataCategoryByIds(countryId, dataSubjectMappingDto.getDataCategories());
        DataSubjectMapping dataSubjectMapping = new DataSubjectMapping();
        if (dataSubjectMappingDto.getOrganizationTypes().size() != 0) {
            dataSubjectMapping.setOrganizationTypes(dataSubjectMappingDto.getOrganizationTypes());
        }
        if (dataSubjectMappingDto.getOrganizationSubTypes().size() != 0) {
            dataSubjectMapping.setOrganizationSubTypes(dataSubjectMappingDto.getOrganizationSubTypes());
        }
        if (dataSubjectMappingDto.getOrganizationServices().size() != 0) {
            dataSubjectMapping.setOrganizationServices(dataSubjectMappingDto.getOrganizationServices());
        }
        if (dataSubjectMappingDto.getOrganizationSubServices().size() != 0) {
            dataSubjectMapping.setOrganizationSubServices(dataSubjectMappingDto.getOrganizationTypes());
        }
        dataSubjectMapping.setName(dataSubjectMappingDto.getName());
        dataSubjectMapping.setDescription(dataSubjectMappingDto.getDescription());
        dataSubjectMapping.setCountryId(countryId);
        dataSubjectMapping.setDataCategories(dataSubjectMappingDto.getDataCategories());
        save(dataSubjectMapping);
        return dataSubjectMapping;

    }


    public Boolean deleteDataSubjectAndMapping(Long countryId, BigInteger id) {
        DataSubjectMapping dataSubjectMapping = dataSubjectMappingRepository.findByIdAndNonDeleted(countryId, id);
        if (!Optional.ofNullable(dataSubjectMapping).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "data subject", id);
        }
        dataSubjectMapping.setDeleted(true);
        save(dataSubjectMapping);
        return true;


    }

    public DataSubjectMappingResponseDto getDataSubjectAndMappingWithData(Long countryId, BigInteger id) {
        DataSubjectMappingResponseDto dataSubjectMapping = dataSubjectMappingRepository.getDataSubjectAndMappingWithDataCategory(countryId, id);
        if (!Optional.ofNullable(dataSubjectMapping).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "data subject", id);
        }
        return dataSubjectMapping;


    }


    public List<DataSubjectMappingResponseDto> getAllDataSubjectAndMappingWithData(Long countryId) {
        return dataSubjectMappingRepository.getAllDataSubjectAndMappingWithDataCategory(countryId);
        }


    public DataSubjectMapping updateDataSubjectAndMapping(Long countryId, BigInteger id, DataSubjectMappingDto dataSubjectMappingDto) {
        DataSubjectMapping existing = dataSubjectMappingRepository.findByCountryIdAndName(countryId, dataSubjectMappingDto.getName());
        if (Optional.ofNullable(existing).isPresent()) {
            exceptionService.duplicateDataException("message.duplicate", "data subject", dataSubjectMappingDto.getName());
        }
        existing = dataSubjectMappingRepository.findByIdAndNonDeleted(countryId, id);
        if (!Optional.ofNullable(existing).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "data subject", id);
        }
        dataCategoryService.getDataCategoryByIds(countryId, dataSubjectMappingDto.getDataCategories());
        DataSubjectMapping dataSubjectMapping = new DataSubjectMapping(dataSubjectMappingDto.getName(), dataSubjectMappingDto.getDescription());
        dataSubjectMapping.setOrganizationTypes(dataSubjectMappingDto.getOrganizationTypes());
        dataSubjectMapping.setOrganizationSubTypes(dataSubjectMappingDto.getOrganizationSubTypes());
        dataSubjectMapping.setOrganizationServices(dataSubjectMappingDto.getOrganizationServices());
        dataSubjectMapping.setOrganizationSubServices(dataSubjectMappingDto.getOrganizationTypes());
        dataSubjectMapping.setCountryId(countryId);
        dataSubjectMapping.setDataCategories(dataSubjectMappingDto.getDataCategories());
        return save(dataSubjectMapping);
    }


}

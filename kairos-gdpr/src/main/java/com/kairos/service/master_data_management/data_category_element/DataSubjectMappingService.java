package com.kairos.service.master_data_management.data_category_element;


import com.kairos.dto.master_data.DataSubjectMappingDTO;
import com.kairos.persistance.model.master_data_management.data_category_element.DataSubjectMapping;
import com.kairos.persistance.repository.master_data_management.data_category_element.DataSubjectMappingRepository;
import com.kairos.response.dto.master_data.data_mapping.DataSubjectMappingResponseDto;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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


    public DataSubjectMapping addDataSubjectAndMapping(Long countryId, Long organizationId, DataSubjectMappingDTO dataSubjectMappingDto) {

        DataSubjectMapping existing = dataSubjectMappingRepository.findByCountryIdAndName(countryId, organizationId, dataSubjectMappingDto.getName());
        if (Optional.ofNullable(existing).isPresent()) {
            exceptionService.duplicateDataException("message.duplicate", "data subject", dataSubjectMappingDto.getName());
        }
        if (dataCategoryService.getDataCategoryByIds(countryId, organizationId, dataSubjectMappingDto.getDataCategories()).size() != dataSubjectMappingDto.getDataCategories().size()) {
            exceptionService.invalidRequestException("message.invalid.request", "");
        }
        DataSubjectMapping dataSubjectMapping = new DataSubjectMapping();
        dataSubjectMapping.setOrganizationTypes(dataSubjectMappingDto.getOrganizationTypes());
        dataSubjectMapping.setOrganizationSubTypes(dataSubjectMappingDto.getOrganizationSubTypes());
        dataSubjectMapping.setName(dataSubjectMappingDto.getName());
        dataSubjectMapping.setDescription(dataSubjectMappingDto.getDescription());
        dataSubjectMapping.setCountryId(countryId);
        dataSubjectMapping.setOrganizationId(organizationId);
        dataSubjectMapping.setDataCategories(dataSubjectMappingDto.getDataCategories());
        save(dataSubjectMapping);
        return dataSubjectMapping;

    }


    public Boolean deleteDataSubjectAndMapping(Long countryId, Long organizationId, BigInteger id) {
        DataSubjectMapping dataSubjectMapping = dataSubjectMappingRepository.findByIdAndNonDeleted(countryId, organizationId, id);
        if (!Optional.ofNullable(dataSubjectMapping).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "data subject", id);
        }
        dataSubjectMapping.setDeleted(true);
        save(dataSubjectMapping);
        return true;


    }

    public DataSubjectMappingResponseDto getDataSubjectAndMappingWithData(Long countryId, Long organizationId, BigInteger id) {
        DataSubjectMappingResponseDto dataSubjectMapping = dataSubjectMappingRepository.getDataSubjectAndMappingWithDataCategory(countryId, organizationId,id);
        if (!Optional.ofNullable(dataSubjectMapping).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "data subject", id);
        }
        return dataSubjectMapping;
    }


    public List<DataSubjectMappingResponseDto> getAllDataSubjectAndMappingWithData(Long countryId, Long organizationId) {
        return dataSubjectMappingRepository.getAllDataSubjectAndMappingWithDataCategory(countryId,organizationId);
    }


    public DataSubjectMapping updateDataSubjectAndMapping(Long countryId, Long organizationId, BigInteger id, DataSubjectMappingDTO dataSubjectMappingDto) {
        DataSubjectMapping existing = dataSubjectMappingRepository.findByCountryIdAndName(countryId, organizationId, dataSubjectMappingDto.getName());
        if (Optional.ofNullable(existing).isPresent() && id != existing.getId()) {
            exceptionService.duplicateDataException("message.duplicate", "data subject", dataSubjectMappingDto.getName());
        }
        existing = dataSubjectMappingRepository.findByIdAndNonDeleted(countryId, organizationId, id);
        if (!Optional.ofNullable(existing).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "data subject", id);
        }
        dataCategoryService.getDataCategoryByIds(countryId, organizationId, dataSubjectMappingDto.getDataCategories());
        DataSubjectMapping dataSubjectMapping = new DataSubjectMapping(dataSubjectMappingDto.getName(), dataSubjectMappingDto.getDescription());
        dataSubjectMapping.setOrganizationTypes(dataSubjectMappingDto.getOrganizationTypes());
        dataSubjectMapping.setOrganizationSubTypes(dataSubjectMappingDto.getOrganizationSubTypes());
        dataSubjectMapping.setCountryId(countryId);
        dataSubjectMapping.setDataCategories(dataSubjectMappingDto.getDataCategories());
        return save(dataSubjectMapping);
    }


}

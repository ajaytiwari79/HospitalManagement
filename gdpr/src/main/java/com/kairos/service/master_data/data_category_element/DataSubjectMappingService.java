package com.kairos.service.master_data.data_category_element;


import com.kairos.dto.master_data.DataSubjectMappingDTO;
import com.kairos.persistance.model.master_data.data_category_element.DataSubjectMapping;
import com.kairos.persistance.repository.master_data.data_category_element.DataSubjectMappingRepository;
import com.kairos.response.dto.master_data.data_mapping.DataSubjectMappingResponseDTO;
import com.kairos.service.common.MongoBaseService;
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

    /**
     * @param countryId
     * @param organizationId
     * @param dataSubjectMappingDto request body of Data Subject ANd Mapping
     * @return Data Subject which contain list of data category ids
     */
    public DataSubjectMappingDTO addDataSubjectAndMapping(Long countryId, Long organizationId, DataSubjectMappingDTO dataSubjectMappingDto) {

        DataSubjectMapping previousDataSubject = dataSubjectMappingRepository.findByName(countryId, organizationId, dataSubjectMappingDto.getName());
        if (Optional.ofNullable(previousDataSubject).isPresent()) {
            exceptionService.duplicateDataException("message.duplicate", "data subject", dataSubjectMappingDto.getName());
        }
        if (dataCategoryService.getDataCategoryByIds(countryId, organizationId, dataSubjectMappingDto.getDataCategories()).size() != dataSubjectMappingDto.getDataCategories().size()) {
            exceptionService.invalidRequestException("message.invalid.request", "");
        }
        DataSubjectMapping dataSubjectMapping = new DataSubjectMapping(dataSubjectMappingDto.getName(), dataSubjectMappingDto.getDescription(), dataSubjectMappingDto.getOrganizationTypes(), dataSubjectMappingDto.getOrganizationSubTypes()
                , dataSubjectMappingDto.getDataCategories());
        dataSubjectMapping.setCountryId(countryId);
        dataSubjectMapping.setOrganizationId(organizationId);
        dataSubjectMappingRepository.save(dataSubjectMapping);
        dataSubjectMappingDto.setId(dataSubjectMapping.getId());
        return dataSubjectMappingDto;

    }


    public Boolean deleteDataSubjectAndMapping(Long countryId, Long organizationId, BigInteger id) {
        DataSubjectMapping dataSubjectMapping = dataSubjectMappingRepository.findByIdAndNonDeleted(countryId, organizationId, id);
        if (!Optional.ofNullable(dataSubjectMapping).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "data subject", id);
        }
        delete(dataSubjectMapping);
        return true;


    }

    public DataSubjectMappingResponseDTO getDataSubjectAndMappingWithData(Long countryId, Long organizationId, BigInteger id) {
        DataSubjectMappingResponseDTO dataSubjectMapping = dataSubjectMappingRepository.getDataSubjectAndMappingWithDataCategory(countryId, organizationId, id);
        if (!Optional.ofNullable(dataSubjectMapping).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "data subject", id);
        }
        return dataSubjectMapping;
    }

    /**
     * @param countryId
     * @param organizationId
     * @return list of DataSubject With Data category List
     */
    public List<DataSubjectMappingResponseDTO> getAllDataSubjectAndMappingWithData(Long countryId, Long organizationId) {
        return dataSubjectMappingRepository.getAllDataSubjectAndMappingWithDataCategory(countryId, organizationId);
    }


    /**
     * @param countryId
     * @param organizationId
     * @param id                    id of data SubjectMapping model
     * @param dataSubjectMappingDto request body for updating Data Subject Mapping Object
     * @return updated Data SubjectMapping object
     */
    public DataSubjectMappingDTO updateDataSubjectAndMapping(Long countryId, Long organizationId, BigInteger id, DataSubjectMappingDTO dataSubjectMappingDto) {
        DataSubjectMapping dataSubject = dataSubjectMappingRepository.findByName(countryId, organizationId, dataSubjectMappingDto.getName());
        if (Optional.ofNullable(dataSubject).isPresent() && !id.equals(dataSubject.getId())) {
            exceptionService.duplicateDataException("message.duplicate", "data subject", dataSubjectMappingDto.getName());
        }
        dataSubject = dataSubjectMappingRepository.findByIdAndNonDeleted(countryId, organizationId, id);
        if (!Optional.ofNullable(dataSubject).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "data subject", id);
        }
        dataCategoryService.getDataCategoryByIds(countryId, organizationId, dataSubjectMappingDto.getDataCategories());
        dataSubject.setName(dataSubjectMappingDto.getName());
        dataSubject.setDescription(dataSubjectMappingDto.getDescription());
        dataSubject.setOrganizationTypes(dataSubjectMappingDto.getOrganizationTypes());
        dataSubject.setOrganizationSubTypes(dataSubjectMappingDto.getOrganizationSubTypes());
        dataSubject.setDataCategories(dataSubjectMappingDto.getDataCategories());
        dataSubjectMappingRepository.save(dataSubject);
        return dataSubjectMappingDto;
    }


}

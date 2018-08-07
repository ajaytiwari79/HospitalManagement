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
     * @return  Data Subject which contain list of data category ids
     */
    public DataSubjectMapping addDataSubjectAndMapping(Long countryId, Long organizationId, DataSubjectMappingDTO dataSubjectMappingDto) {

        DataSubjectMapping existing = dataSubjectMappingRepository.findByName(countryId, organizationId, dataSubjectMappingDto.getName());
        if (Optional.ofNullable(existing).isPresent()) {
            exceptionService.duplicateDataException("message.duplicate", "data subject", dataSubjectMappingDto.getName());
        }
        if (dataCategoryService.getDataCategoryByIds(countryId, organizationId, dataSubjectMappingDto.getDataCategories()).size() != dataSubjectMappingDto.getDataCategories().size()) {
            exceptionService.invalidRequestException("message.invalid.request", "");
        }
        DataSubjectMapping dataSubjectMapping = new DataSubjectMapping(dataSubjectMappingDto.getName(), dataSubjectMappingDto.getDescription(), dataSubjectMappingDto.getOrganizationTypes(), dataSubjectMappingDto.getOrganizationSubTypes()
                , dataSubjectMappingDto.getDataCategories());
        dataSubjectMapping.setCountryId(countryId);
        dataSubjectMapping.setOrganizationId(organizationId);
        return dataSubjectMappingRepository.save(getNextSequence(dataSubjectMapping));


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
     * @param id   id of data SubjectMapping model
     * @param dataSubjectMappingDto request body for updating Data Subject Mapping Object
     * @return updated Data SubjectMapping object
     */
    public DataSubjectMapping updateDataSubjectAndMapping(Long countryId, Long organizationId, BigInteger id, DataSubjectMappingDTO dataSubjectMappingDto) {
        DataSubjectMapping existing = dataSubjectMappingRepository.findByName(countryId, organizationId, dataSubjectMappingDto.getName());
        if (Optional.ofNullable(existing).isPresent() && !id.equals(existing.getId())) {
            exceptionService.duplicateDataException("message.duplicate", "data subject", dataSubjectMappingDto.getName());
        }
        existing = dataSubjectMappingRepository.findByIdAndNonDeleted(countryId, organizationId, id);
        if (!Optional.ofNullable(existing).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "data subject", id);
        }
        dataCategoryService.getDataCategoryByIds(countryId, organizationId, dataSubjectMappingDto.getDataCategories());
        existing.setName(dataSubjectMappingDto.getName());
        existing.setDescription(dataSubjectMappingDto.getDescription());
        existing.setOrganizationTypes(dataSubjectMappingDto.getOrganizationTypes());
        existing.setOrganizationSubTypes(dataSubjectMappingDto.getOrganizationSubTypes());
        existing.setDataCategories(dataSubjectMappingDto.getDataCategories());
        return dataSubjectMappingRepository.save(getNextSequence(existing));
    }


}

package com.kairos.service.data_subject_management;


import com.kairos.dto.gdpr.master_data.DataSubjectDTO;
import com.kairos.dto.gdpr.master_data.MasterDataSubjectDTO;
import com.kairos.persistence.model.master_data.data_category_element.DataSubjectMapping;
import com.kairos.persistence.repository.master_data.data_category_element.DataSubjectMappingRepository;
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
     * @param dataSubjectMappingDto request body of Data Subject ANd Mapping
     * @return Data Subject which contain list of data category ids
     */
    public MasterDataSubjectDTO addDataSubjectAndMapping(Long countryId, MasterDataSubjectDTO dataSubjectMappingDto) {

        DataSubjectMapping previousDataSubject = dataSubjectMappingRepository.findByName(countryId, dataSubjectMappingDto.getName());
        if (Optional.ofNullable(previousDataSubject).isPresent()) {
            exceptionService.duplicateDataException("message.duplicate", "data subject", dataSubjectMappingDto.getName());
        }
        DataSubjectMapping dataSubjectMapping = new DataSubjectMapping(dataSubjectMappingDto.getName(), dataSubjectMappingDto.getDescription(), dataSubjectMappingDto.getOrganizationTypes(), dataSubjectMappingDto.getOrganizationSubTypes()
                , dataSubjectMappingDto.getDataCategories());
        dataSubjectMapping.setCountryId(countryId);
        dataSubjectMappingRepository.save(dataSubjectMapping);
        dataSubjectMappingDto.setId(dataSubjectMapping.getId());
        return dataSubjectMappingDto;

    }

    public DataSubjectMappingResponseDTO getDataSubjectAndMappingWithDataByCountryIdAndId(Long countryId, BigInteger id) {
        DataSubjectMappingResponseDTO dataSubjectMapping = dataSubjectMappingRepository.getDataSubjectWithDataCategoryAndDataElementByCountryIdAndId(countryId, id);
        if (!Optional.ofNullable(dataSubjectMapping).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "data subject", id);
        }
        return dataSubjectMapping;
    }


    /**
     * @param countryId
     * @return list of DataSubject With Data category List
     */
    public List<DataSubjectMappingResponseDTO> getAllDataSubjectWithDataCategoryByCountryId(Long countryId) {
        return dataSubjectMappingRepository.getAllDataSubjectWithDataCategoryAndDataElementByCountryId(countryId);
    }


    /**
     * @param countryId
     * @param dataSubjectId         id of data SubjectMapping model
     * @param dataSubjectMappingDto request body for updating Data Subject Mapping Object
     * @return updated Data SubjectMapping object
     */
    public MasterDataSubjectDTO updateDataSubjectAndMapping(Long countryId, BigInteger dataSubjectId, MasterDataSubjectDTO dataSubjectMappingDto) {
        DataSubjectMapping dataSubject = dataSubjectMappingRepository.findByName(countryId, dataSubjectMappingDto.getName());
        if (Optional.ofNullable(dataSubject).isPresent() && !dataSubjectId.equals(dataSubject.getId())) {
            exceptionService.duplicateDataException("message.duplicate", "data subject", dataSubjectMappingDto.getName());
        }
        dataSubject = dataSubjectMappingRepository.findByIdAndNonDeleted(countryId, dataSubjectId);
        if (!Optional.ofNullable(dataSubject).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "data subject", dataSubjectId);
        }
        dataSubject.setName(dataSubjectMappingDto.getName());
        dataSubject.setDescription(dataSubjectMappingDto.getDescription());
        dataSubject.setOrganizationTypes(dataSubjectMappingDto.getOrganizationTypes());
        dataSubject.setOrganizationSubTypes(dataSubjectMappingDto.getOrganizationSubTypes());
        dataSubject.setDataCategories(dataSubjectMappingDto.getDataCategories());
        dataSubjectMappingRepository.save(dataSubject);
        return dataSubjectMappingDto;
    }


    /**
     * @param unitId
     * @param subjectDTO data subject Dto contain basic field ,name description and data category id
     * @return
     */
    public DataSubjectDTO saveOrganizationDataSubject(Long unitId, DataSubjectDTO subjectDTO) {


        DataSubjectMapping previousDataSubject = dataSubjectMappingRepository.findByNameAndUnitId(unitId, subjectDTO.getName());
        if (Optional.ofNullable(previousDataSubject).isPresent()) {
            exceptionService.duplicateDataException("message.duplicate", "Data Subject", subjectDTO.getName());
        }
        DataSubjectMapping dataSubject = new DataSubjectMapping(subjectDTO.getName(), subjectDTO.getDescription(), subjectDTO.getDataCategories());
        dataSubject.setOrganizationId(unitId);
        dataSubjectMappingRepository.save(dataSubject);
        subjectDTO.setId(dataSubject.getId());
        return subjectDTO;

    }

    public boolean deleteDataSubjectById(BigInteger dataSubjectId) {
        dataSubjectMappingRepository.safeDeleteById(dataSubjectId);
        return true;
    }


    public List<DataSubjectMappingResponseDTO> findOrganizationAllDataSubjectWithDataCategoryAndDataElements(Long unitId) {
        return dataSubjectMappingRepository.getAllDataSubjectWithDataCategoryAndDataElementByUnitId(unitId);
    }


    /**
     * @param unitId
     * @param dataSubjectId
     * @param dataSubjectDTO
     */
    public DataSubjectDTO updateOrganizationDataSubject(Long unitId, BigInteger dataSubjectId, DataSubjectDTO dataSubjectDTO) {

        DataSubjectMapping dataSubject = dataSubjectMappingRepository.findByNameAndUnitId(unitId, dataSubjectDTO.getName());
        if (Optional.ofNullable(dataSubject).isPresent() && !dataSubjectId.equals(dataSubject.getId())) {
            exceptionService.duplicateDataException("message.duplicate", "Data Subject", dataSubject.getName());
        }
        dataSubject = dataSubjectMappingRepository.findOne(dataSubjectId);
        dataSubject.setName(dataSubjectDTO.getName());
        dataSubject.setDataCategories(dataSubjectDTO.getDataCategories());
        dataSubject.setDescription(dataSubjectDTO.getDescription());
        dataSubjectMappingRepository.save(dataSubject);
        return dataSubjectDTO;

    }


    public DataSubjectMappingResponseDTO getDataSubjectWithDataCategoryAndElementByUnitIdAndId(Long unitId, BigInteger datSubjectId) {
        DataSubjectMappingResponseDTO dataSubjectMapping = dataSubjectMappingRepository.getDataSubjectWithDataCategoryAndDataElementByUnitIdAndId(unitId, datSubjectId);
        if (!Optional.ofNullable(dataSubjectMapping).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "data subject", datSubjectId);
        }
        return dataSubjectMapping;
    }


}

package com.kairos.service.data_subject_management;


import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.gdpr.OrganizationSubTypeDTO;
import com.kairos.dto.gdpr.OrganizationTypeDTO;
import com.kairos.dto.gdpr.master_data.DataSubjectDTO;
import com.kairos.dto.gdpr.master_data.MasterDataSubjectDTO;
import com.kairos.persistence.model.master_data.data_category_element.DataCategory;
import com.kairos.persistence.model.master_data.data_category_element.DataSubject;
import com.kairos.persistence.model.embeddables.OrganizationSubType;
import com.kairos.persistence.model.embeddables.OrganizationType;
import com.kairos.persistence.repository.master_data.data_category_element.DataSubjectRepository;
import com.kairos.response.dto.master_data.data_mapping.DataSubjectResponseDTO;
import com.kairos.service.exception.ExceptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DataSubjectService {


    Logger LOGGER = LoggerFactory.getLogger(DataSubjectService.class);

    @Inject
    private DataCategoryService dataCategoryService;

    @Inject
    private ExceptionService exceptionService;

    @Inject
    private DataSubjectRepository dataSubjectRepository;

    /**
     * @param countryId
     * @param dataSubjectMappingDto request body of Data Subject ANd Mapping
     * @return Data Subject which contain list of data category ids
     */
    public MasterDataSubjectDTO addDataSubjectAndMapping(Long countryId, MasterDataSubjectDTO dataSubjectMappingDto) {

        DataSubject previousDataSubject = dataSubjectRepository.findByCountryIdAndName(countryId, dataSubjectMappingDto.getName());
        if (Optional.ofNullable(previousDataSubject).isPresent()) {
            exceptionService.duplicateDataException("message.duplicate", "data subject", dataSubjectMappingDto.getName());
        }
        DataSubject dataSubject = new DataSubject(dataSubjectMappingDto.getName(), dataSubjectMappingDto.getDescription());
        dataSubject.setOrganizationTypes(ObjectMapperUtils.copyPropertiesOfListByMapper(dataSubjectMappingDto.getOrganizationTypes(), OrganizationType.class));
        dataSubject.setOrganizationSubTypes(ObjectMapperUtils.copyPropertiesOfListByMapper(dataSubjectMappingDto.getOrganizationSubTypes(), OrganizationSubType.class));
        dataSubject.setDataCategories(dataCategoryService.getAllDataCategoriesByIds(dataSubjectMappingDto.getDataCategories()));
        dataSubject.setCountryId(countryId);
        dataSubjectRepository.save(dataSubject);
        dataSubjectMappingDto.setId(dataSubject.getId());
        return dataSubjectMappingDto;

    }

    public DataSubjectResponseDTO getDataSubjectAndMappingWithDataByCountryIdAndId(Long countryId, Long id) {
        DataSubject dataSubject = dataSubjectRepository.getDataSubjectByCountryIdAndId(countryId, id);
        if (!Optional.ofNullable(dataSubject).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "data subject", id);
        }
        return prepareDataSubjectResponseDTO(dataSubject, true);
    }


    private DataSubjectResponseDTO prepareDataSubjectResponseDTO(DataSubject dataSubject, boolean isMasterData){
        DataSubjectResponseDTO dataSubjectMappingResponse = new DataSubjectResponseDTO();
        dataSubjectMappingResponse.setId(dataSubject.getId());
        dataSubjectMappingResponse.setName(dataSubject.getName());
        dataSubjectMappingResponse.setDescription(dataSubject.getDescription());
        if(isMasterData) {
            dataSubjectMappingResponse.setOrganizationTypes(ObjectMapperUtils.copyPropertiesOfListByMapper(dataSubject.getOrganizationTypes(), OrganizationTypeDTO.class));
            dataSubjectMappingResponse.setOrganizationSubTypes(ObjectMapperUtils.copyPropertiesOfListByMapper(dataSubject.getOrganizationSubTypes(), OrganizationSubTypeDTO.class));
        }
        dataSubjectMappingResponse.setDataCategories(ObjectMapperUtils.copyPropertiesOfListByMapper(dataSubject.getDataCategories(), DataCategory.class));
        return dataSubjectMappingResponse;
    }

    /**
     * @param countryId
     * @return list of DataSubject With Data category List
     */
    public List<DataSubjectResponseDTO> getAllDataSubjectWithDataCategoryByCountryId(Long countryId, boolean isMasterData) {
        List<DataSubjectResponseDTO> dataSubjectMappingResponseList = new ArrayList<>();
        List<DataSubject> dataSubjects =  dataSubjectRepository.getAllDataSubjectByCountryId(countryId);
        for(DataSubject dataSubject : dataSubjects){
            dataSubjectMappingResponseList.add(prepareDataSubjectResponseDTO(dataSubject, isMasterData));
        }
        return dataSubjectMappingResponseList;
    }


    /**
     * @param countryId
     * @param dataSubjectId         id of data SubjectMapping model
     * @param dataSubjectMappingDto request body for updating Data Subject Mapping Object
     * @return updated Data SubjectMapping object
     */
    public MasterDataSubjectDTO updateDataSubjectAndMapping(Long countryId, Long dataSubjectId, MasterDataSubjectDTO dataSubjectMappingDto) {
        DataSubject dataSubject = dataSubjectRepository.findByCountryIdAndName(countryId, dataSubjectMappingDto.getName());
        if (Optional.ofNullable(dataSubject).isPresent() && !dataSubjectId.equals(dataSubject.getId())) {
            exceptionService.duplicateDataException("message.duplicate", "data subject", dataSubjectMappingDto.getName());
        }
        dataSubject = dataSubjectRepository.getDataSubjectByCountryIdAndId(countryId, dataSubjectId);
        if (!Optional.ofNullable(dataSubject).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "data subject", dataSubjectId);
        }
        dataSubject.setName(dataSubjectMappingDto.getName());
        dataSubject.setDescription(dataSubjectMappingDto.getDescription());
        dataSubject.setOrganizationTypes(ObjectMapperUtils.copyPropertiesOfListByMapper(dataSubjectMappingDto.getOrganizationTypes(), OrganizationType.class));
        dataSubject.setOrganizationSubTypes(ObjectMapperUtils.copyPropertiesOfListByMapper(dataSubjectMappingDto.getOrganizationSubTypes(), OrganizationSubType.class));
        dataSubject.setDataCategories(dataCategoryService.getAllDataCategoriesByIds(dataSubjectMappingDto.getDataCategories()));
        dataSubjectRepository.save(dataSubject);
        return dataSubjectMappingDto;
    }


    /**
     * @param unitId
     * @param subjectDTO data subject Dto contain basic field ,name description and data category id
     * @return
     */
    public DataSubjectDTO saveOrganizationDataSubject(Long unitId, DataSubjectDTO subjectDTO) {


        DataSubject previousDataSubject = dataSubjectRepository.findByNameAndUnitId(unitId, subjectDTO.getName());
        if (Optional.ofNullable(previousDataSubject).isPresent()) {
            exceptionService.duplicateDataException("message.duplicate", "Data Subject", subjectDTO.getName());
        }
        DataSubject dataSubject = new DataSubject(subjectDTO.getName(), subjectDTO.getDescription(), dataCategoryService.getAllDataCategoriesByIds(subjectDTO.getDataCategories()));
        dataSubject.setOrganizationId(unitId);
        dataSubjectRepository.save(dataSubject);
        subjectDTO.setId(dataSubject.getId());
        return subjectDTO;

    }

    public boolean deleteDataSubjectById(Long dataSubjectId) {
        dataSubjectRepository.safeDeleteById(dataSubjectId);
        return true;
    }


    public List<DataSubjectResponseDTO> findOrganizationAllDataSubjectWithDataCategoryAndDataElements(Long unitId) {
        List<DataSubjectResponseDTO> dataSubjectResponseDTOList = new ArrayList<>();
        List<DataSubject> dataSubjects = dataSubjectRepository.getAllDataSubjectByUnitId(unitId);
        for(DataSubject dataSubject : dataSubjects){
            dataSubjectResponseDTOList.add(prepareDataSubjectResponseDTO(dataSubject, false));
        }
        return dataSubjectResponseDTOList;

    }


    /**
     * @param unitId
     * @param dataSubjectId
     * @param dataSubjectDTO
     */
    public DataSubjectDTO updateOrganizationDataSubject(Long unitId, Long dataSubjectId, DataSubjectDTO dataSubjectDTO) {

        DataSubject dataSubject = dataSubjectRepository.findByNameAndUnitId(unitId, dataSubjectDTO.getName());
        if (Optional.ofNullable(dataSubject).isPresent() && !dataSubjectId.equals(dataSubject.getId())) {
            exceptionService.duplicateDataException("message.duplicate", "Data Subject", dataSubject.getName());
        }
        dataSubject = dataSubjectRepository.getOne(dataSubjectId);
        dataSubject.setName(dataSubjectDTO.getName());
        dataSubject.setDataCategories(dataCategoryService.getAllDataCategoriesByIds(dataSubjectDTO.getDataCategories()));
        dataSubject.setDescription(dataSubjectDTO.getDescription());
        dataSubjectRepository.save(dataSubject);
        return dataSubjectDTO;

    }


    public DataSubjectResponseDTO getDataSubjectWithDataCategoryAndElementByUnitIdAndId(Long unitId, Long datSubjectId) {
        DataSubject dataSubject = dataSubjectRepository.getDataSubjectByUnitIdAndId(unitId, datSubjectId);
        if (!Optional.ofNullable(dataSubject).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "data subject", datSubjectId);
        }

        return prepareDataSubjectResponseDTO(dataSubject, false);
    }


}

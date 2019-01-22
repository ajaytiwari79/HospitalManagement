package com.kairos.service.data_subject_management;


import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.gdpr.OrganizationSubTypeDTO;
import com.kairos.dto.gdpr.OrganizationTypeDTO;
import com.kairos.dto.gdpr.master_data.DataSubjectDTO;
import com.kairos.dto.gdpr.master_data.MasterDataSubjectDTO;
import com.kairos.persistence.model.master_data.data_category_element.DataCategoryMD;
import com.kairos.persistence.model.master_data.data_category_element.DataSubjectMappingMD;
import com.kairos.persistence.model.embeddables.OrganizationSubType;
import com.kairos.persistence.model.embeddables.OrganizationType;
import com.kairos.persistence.repository.master_data.data_category_element.DataSubjectRepository;
import com.kairos.response.dto.master_data.data_mapping.DataSubjectMappingResponseDTO;
import com.kairos.service.exception.ExceptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DataSubjectMappingService {


    Logger LOGGER = LoggerFactory.getLogger(DataSubjectMappingService.class);

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

        DataSubjectMappingMD previousDataSubject = dataSubjectRepository.findByCountryIdAndName(countryId, dataSubjectMappingDto.getName());
        if (Optional.ofNullable(previousDataSubject).isPresent()) {
            exceptionService.duplicateDataException("message.duplicate", "data subject", dataSubjectMappingDto.getName());
        }
        DataSubjectMappingMD dataSubjectMapping = new DataSubjectMappingMD(dataSubjectMappingDto.getName(), dataSubjectMappingDto.getDescription());
        dataSubjectMapping.setOrganizationTypes(ObjectMapperUtils.copyPropertiesOfListByMapper(dataSubjectMappingDto.getOrganizationTypes(), OrganizationType.class));
        dataSubjectMapping.setOrganizationSubTypes(ObjectMapperUtils.copyPropertiesOfListByMapper(dataSubjectMappingDto.getOrganizationSubTypes(), OrganizationSubType.class));
        dataSubjectMapping.setDataCategories(dataCategoryService.getAllDataCategoriesByIds(dataSubjectMappingDto.getDataCategories()));
        dataSubjectMapping.setCountryId(countryId);
        dataSubjectRepository.save(dataSubjectMapping);
        dataSubjectMappingDto.setId(dataSubjectMapping.getId());
        return dataSubjectMappingDto;

    }

    public DataSubjectMappingResponseDTO getDataSubjectAndMappingWithDataByCountryIdAndId(Long countryId, Long id) {
        DataSubjectMappingMD dataSubjectMapping = dataSubjectRepository.getDataSubjectByCountryIdAndId(countryId, id);
        if (!Optional.ofNullable(dataSubjectMapping).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "data subject", id);
        }
        DataSubjectMappingResponseDTO  dataSubjectMappingResponse =  prepareDataSubjectMappingResponseDTO(dataSubjectMapping, true);
        return dataSubjectMappingResponse;
    }


    private DataSubjectMappingResponseDTO prepareDataSubjectMappingResponseDTO(DataSubjectMappingMD dataSubjectMapping, boolean isMasterData){
        DataSubjectMappingResponseDTO  dataSubjectMappingResponse = new DataSubjectMappingResponseDTO();
        dataSubjectMappingResponse.setId(dataSubjectMapping.getId());
        dataSubjectMappingResponse.setName(dataSubjectMapping.getName());
        dataSubjectMappingResponse.setDescription(dataSubjectMapping.getDescription());
        if(isMasterData) {
            dataSubjectMappingResponse.setOrganizationTypes(ObjectMapperUtils.copyPropertiesOfListByMapper(dataSubjectMapping.getOrganizationTypes(), OrganizationTypeDTO.class));
            dataSubjectMappingResponse.setOrganizationSubTypes(ObjectMapperUtils.copyPropertiesOfListByMapper(dataSubjectMapping.getOrganizationSubTypes(), OrganizationSubTypeDTO.class));
        }
        dataSubjectMappingResponse.setDataCategories(ObjectMapperUtils.copyPropertiesOfListByMapper(dataSubjectMapping.getDataCategories(), DataCategoryMD.class));
        return dataSubjectMappingResponse;
    }

    /**
     * @param countryId
     * @return list of DataSubject With Data category List
     */
    public List<DataSubjectMappingResponseDTO> getAllDataSubjectWithDataCategoryByCountryId(Long countryId) {
        List<DataSubjectMappingResponseDTO> dataSubjectMappingResponseList = new ArrayList<>();
        List<DataSubjectMappingMD> dataSubjectMappings =  dataSubjectRepository.getAllDataSubjectByCountryId(countryId);
        for(DataSubjectMappingMD dataSubjectMapping : dataSubjectMappings){
            dataSubjectMappingResponseList.add(prepareDataSubjectMappingResponseDTO(dataSubjectMapping , true));
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
        DataSubjectMappingMD dataSubject = dataSubjectRepository.findByCountryIdAndName(countryId, dataSubjectMappingDto.getName());
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


        DataSubjectMappingMD previousDataSubject = dataSubjectRepository.findByNameAndUnitId(unitId, subjectDTO.getName());
        if (Optional.ofNullable(previousDataSubject).isPresent()) {
            exceptionService.duplicateDataException("message.duplicate", "Data Subject", subjectDTO.getName());
        }
        DataSubjectMappingMD dataSubject = new DataSubjectMappingMD(subjectDTO.getName(), subjectDTO.getDescription(), dataCategoryService.getAllDataCategoriesByIds(subjectDTO.getDataCategories()));
        dataSubject.setOrganizationId(unitId);
        dataSubjectRepository.save(dataSubject);
        subjectDTO.setId(dataSubject.getId());
        return subjectDTO;

    }

    public boolean deleteDataSubjectById(Long dataSubjectId) {
        dataSubjectRepository.safeDeleteById(dataSubjectId);
        return true;
    }


    public List<DataSubjectMappingResponseDTO> findOrganizationAllDataSubjectWithDataCategoryAndDataElements(Long unitId) {
        List<DataSubjectMappingResponseDTO> dataSubjectMappingResponseDTOList = new ArrayList<>();
        List<DataSubjectMappingMD> dataSubjects = dataSubjectRepository.getAllDataSubjectByUnitId(unitId);
        for(DataSubjectMappingMD dataSubjectMapping : dataSubjects){
            dataSubjectMappingResponseDTOList.add(prepareDataSubjectMappingResponseDTO(dataSubjectMapping , false));
        }
        return dataSubjectMappingResponseDTOList;

    }


    /**
     * @param unitId
     * @param dataSubjectId
     * @param dataSubjectDTO
     */
    public DataSubjectDTO updateOrganizationDataSubject(Long unitId, Long dataSubjectId, DataSubjectDTO dataSubjectDTO) {

        DataSubjectMappingMD dataSubject = dataSubjectRepository.findByNameAndUnitId(unitId, dataSubjectDTO.getName());
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


    public DataSubjectMappingResponseDTO getDataSubjectWithDataCategoryAndElementByUnitIdAndId(Long unitId, Long datSubjectId) {
        DataSubjectMappingMD dataSubjectMapping = dataSubjectRepository.getDataSubjectByUnitIdAndId(unitId, datSubjectId);
        if (!Optional.ofNullable(dataSubjectMapping).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "data subject", datSubjectId);
        }

        return prepareDataSubjectMappingResponseDTO(dataSubjectMapping, false);
    }


}

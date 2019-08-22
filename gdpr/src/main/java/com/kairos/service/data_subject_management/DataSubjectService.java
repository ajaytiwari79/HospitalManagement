package com.kairos.service.data_subject_management;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.gdpr.OrganizationSubTypeDTO;
import com.kairos.dto.gdpr.OrganizationTypeDTO;
import com.kairos.dto.gdpr.master_data.DataSubjectDTO;
import com.kairos.dto.gdpr.master_data.MasterDataSubjectDTO;
import com.kairos.persistence.model.embeddables.OrganizationSubType;
import com.kairos.persistence.model.embeddables.OrganizationType;
import com.kairos.persistence.model.master_data.data_category_element.DataCategory;
import com.kairos.persistence.model.master_data.data_category_element.DataSubject;
import com.kairos.persistence.repository.master_data.data_category_element.DataSubjectRepository;
import com.kairos.response.dto.master_data.data_mapping.DataSubjectResponseDTO;
import com.kairos.service.exception.ExceptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.*;

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
     * @param dataSubjectDto request body of Data Subject ANd Mapping
     * @return Data Subject which contain list of data category ids
     */
    public MasterDataSubjectDTO addDataSubjectAndMapping(Long countryId, MasterDataSubjectDTO dataSubjectDto) {

        DataSubject previousDataSubject = dataSubjectRepository.findByCountryIdAndName(countryId, dataSubjectDto.getName());
        if (Optional.ofNullable(previousDataSubject).isPresent()) {
            exceptionService.duplicateDataException("message.duplicate", "message.dataSubject", dataSubjectDto.getName());
        }
        DataSubject dataSubject = new DataSubject(dataSubjectDto.getName(), dataSubjectDto.getDescription());
        dataSubject.setOrganizationTypes(ObjectMapperUtils.copyPropertiesOfListByMapper(dataSubjectDto.getOrganizationTypes(), OrganizationType.class));
        dataSubject.setOrganizationSubTypes(ObjectMapperUtils.copyPropertiesOfListByMapper(dataSubjectDto.getOrganizationSubTypes(), OrganizationSubType.class));
        dataSubject.setDataCategories(dataCategoryService.getAllDataCategoriesByIds(dataSubjectDto.getDataCategories()));
        dataSubject.setCountryId(countryId);
        dataSubjectRepository.save(dataSubject);
        dataSubjectDto.setId(dataSubject.getId());
        return dataSubjectDto;

    }

    public DataSubjectResponseDTO getDataSubjectAndMappingWithDataByCountryIdAndId(Long countryId, Long id) {
        DataSubject dataSubject = dataSubjectRepository.getDataSubjectByCountryIdAndId(countryId, id);
        if (!Optional.ofNullable(dataSubject).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "message.dataSubject", id);
        }
        return prepareDataSubjectResponseDTO(dataSubject, true);
    }


    private DataSubjectResponseDTO prepareDataSubjectResponseDTO(DataSubject dataSubject, boolean isMasterData){
        DataSubjectResponseDTO dataSubjectResponse = new DataSubjectResponseDTO();
        dataSubjectResponse.setId(dataSubject.getId());
        dataSubjectResponse.setName(dataSubject.getName());
        dataSubjectResponse.setDescription(dataSubject.getDescription());
        if(isMasterData) {
            dataSubjectResponse.setOrganizationTypes(ObjectMapperUtils.copyPropertiesOfListByMapper(dataSubject.getOrganizationTypes(), OrganizationTypeDTO.class));
            dataSubjectResponse.setOrganizationSubTypes(ObjectMapperUtils.copyPropertiesOfListByMapper(dataSubject.getOrganizationSubTypes(), OrganizationSubTypeDTO.class));
        }
        dataSubjectResponse.setDataCategories(ObjectMapperUtils.copyPropertiesOfListByMapper(dataSubject.getDataCategories(), DataCategory.class));
        return dataSubjectResponse;
    }

    /**
     * @param countryId
     * @return list of DataSubject With Data category List
     */
    public List<DataSubjectResponseDTO> getAllDataSubjectWithDataCategoryByCountryId(Long countryId, boolean isMasterData) {
        List<DataSubjectResponseDTO> dataSubjectResponseList = new ArrayList<>();
        List<DataSubject> dataSubjects =  dataSubjectRepository.getAllDataSubjectByCountryId(countryId);
        for(DataSubject dataSubject : dataSubjects){
            dataSubjectResponseList.add(prepareDataSubjectResponseDTO(dataSubject, isMasterData));
        }
        return dataSubjectResponseList;
    }


    /**
     * @param countryId
     * @param dataSubjectId         id of data SubjectMapping model
     * @param dataSubjectDto request body for updating Data Subject Mapping Object
     * @return updated Data SubjectMapping object
     */
    public MasterDataSubjectDTO updateDataSubjectAndMapping(Long countryId, Long dataSubjectId, MasterDataSubjectDTO dataSubjectDto) {
        DataSubject dataSubject = dataSubjectRepository.findByCountryIdAndName(countryId, dataSubjectDto.getName());
        if (Optional.ofNullable(dataSubject).isPresent() && !dataSubjectId.equals(dataSubject.getId())) {
            exceptionService.duplicateDataException("message.duplicate", "message.dataSubject", dataSubjectDto.getName());
        }
        dataSubject = dataSubjectRepository.getDataSubjectByCountryIdAndId(countryId, dataSubjectId);
        if (!Optional.ofNullable(dataSubject).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "message.dataSubject", dataSubjectId);
        }
        dataSubject.setName(dataSubjectDto.getName());
        dataSubject.setDescription(dataSubjectDto.getDescription());
        dataSubject.setOrganizationTypes(ObjectMapperUtils.copyPropertiesOfListByMapper(dataSubjectDto.getOrganizationTypes(), OrganizationType.class));
        dataSubject.setOrganizationSubTypes(ObjectMapperUtils.copyPropertiesOfListByMapper(dataSubjectDto.getOrganizationSubTypes(), OrganizationSubType.class));
        dataSubject.setDataCategories(dataCategoryService.getAllDataCategoriesByIds(dataSubjectDto.getDataCategories()));
        dataSubjectRepository.save(dataSubject);
        return dataSubjectDto;
    }


    /**
     * @param unitId
     * @param subjectDTO data subject Dto contain basic field ,name description and data category id
     * @return
     */
    public DataSubjectDTO saveOrganizationDataSubject(Long unitId, DataSubjectDTO subjectDTO) {


        DataSubject previousDataSubject = dataSubjectRepository.findByNameAndUnitId(unitId, subjectDTO.getName());
        if (Optional.ofNullable(previousDataSubject).isPresent()) {
            exceptionService.duplicateDataException("message.duplicate", "message.dataSubject", subjectDTO.getName());
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
            exceptionService.duplicateDataException("message.duplicate", "message.dataSubject", dataSubject.getName());
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
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "message.dataSubject", datSubjectId);
        }

        return prepareDataSubjectResponseDTO(dataSubject, false);
    }


}

package com.kairos.service.data_inventory.processing_activity;

import com.kairos.commons.custom_exception.DataNotFoundByIdException;
import com.kairos.commons.custom_exception.DuplicateDataException;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.gdpr.metadata.ResponsibilityTypeDTO;
import com.kairos.persistence.model.master_data.default_proc_activity_setting.ResponsibilityType;
import com.kairos.persistence.repository.data_inventory.processing_activity.ProcessingActivityRepository;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.responsibility_type.ResponsibilityTypeRepository;
import com.kairos.response.dto.common.ResponsibilityTypeResponseDTO;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.master_data.processing_activity_masterdata.ResponsibilityTypeService;
import com.kairos.utils.ComparisonUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class OrganizationResponsibilityTypeService{


    private static final Logger LOGGER = LoggerFactory.getLogger(OrganizationResponsibilityTypeService.class);

    @Inject
    private ResponsibilityTypeRepository responsibilityTypeRepository;

    @Inject
    private ExceptionService exceptionService;

    @Inject
    private ResponsibilityTypeService responsibilityTypeService;

    @Inject
    private ProcessingActivityRepository processingActivityRepository;


    /**
     * @param unitId
     * @param responsibilityTypeDTOS
     * @return return map which contain list of new ResponsibilityType and list of existing ResponsibilityType if ResponsibilityType already exist
     * @description this method create new ResponsibilityType if ResponsibilityType not exist with same name ,
     * and if exist then simply add  ResponsibilityType to existing list and return list ;
     * findMetaDataByNamesAndCountryId()  return list of existing ResponsibilityType using collation ,used for case insensitive result
     */
    public List<ResponsibilityTypeDTO> createResponsibilityType(Long unitId, List<ResponsibilityTypeDTO> responsibilityTypeDTOS) {
        Set<String> existingResponsibilityTypeNames = responsibilityTypeRepository.findNameByOrganizationIdAndDeleted(unitId);
        Set<String> responsibilityTypeNames = ComparisonUtils.getNewMetaDataNames(responsibilityTypeDTOS,existingResponsibilityTypeNames );
            List<ResponsibilityType> responsibilityTypes = new ArrayList<>();
            if (!responsibilityTypeNames.isEmpty()) {
                for (String name : responsibilityTypeNames) {
                    ResponsibilityType responsibilityType = new ResponsibilityType(name);
                    responsibilityType.setOrganizationId(unitId);
                    responsibilityTypes.add(responsibilityType);
                }
              responsibilityTypeRepository.saveAll(responsibilityTypes);

            }
          return ObjectMapperUtils.copyPropertiesOfCollectionByMapper(responsibilityTypes,ResponsibilityTypeDTO.class);

    }

    /**
     * * @param unitId
     *
     * @return list of ResponsibilityType
     */
    public List<ResponsibilityTypeResponseDTO> getAllResponsibilityType(Long unitId) {
        return responsibilityTypeRepository.findAllByOrganizationIdAndSortByCreatedDate(unitId);
    }

    /**
     * @param unitId
     * @param id             id of ResponsibilityType
     * @return ResponsibilityType object fetch by given id
     * @throws DataNotFoundByIdException throw exception if ResponsibilityType not found for given id
     */
    public ResponsibilityType getResponsibilityType(Long unitId, Long id) {

        ResponsibilityType exist = responsibilityTypeRepository.findByIdAndOrganizationIdAndDeletedFalse(id, unitId);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            return exist;

        }
    }


    public Boolean deleteResponsibilityType(Long unitId, Long responsibilityTypeId) {

        List<String> processingActivities = processingActivityRepository.findAllProcessingActivityLinkedWithResponsibilityType(unitId, responsibilityTypeId);
        if (!processingActivities.isEmpty()) {
                exceptionService.metaDataLinkedWithProcessingActivityException("message.metaData.linked.with.ProcessingActivity", "message.responsibilityType", StringUtils.join(processingActivities, ','));
        }
        responsibilityTypeRepository.deleteByIdAndOrganizationId(responsibilityTypeId, unitId);
        return true;
    }

    /***
     * @throws DuplicateDataException throw exception if ResponsibilityType data not exist for given id
     * @param unitId
     * @param id id of ResponsibilityType
     * @param responsibilityTypeDTO
     * @return ResponsibilityType updated object
     */
    public ResponsibilityTypeDTO updateResponsibilityType(Long unitId, Long id, ResponsibilityTypeDTO responsibilityTypeDTO) {


        ResponsibilityType responsibilityType = responsibilityTypeRepository.findByOrganizationIdAndDeletedAndName(unitId, responsibilityTypeDTO.getName());
        if (Optional.ofNullable(responsibilityType).isPresent()) {
            if (id.equals(responsibilityType.getId())) {
                return responsibilityTypeDTO;
            }
            exceptionService.duplicateDataException("message.duplicate", "message.responsibilityType", responsibilityType.getName());
        }
        Integer resultCount =  responsibilityTypeRepository.updateMetadataName(responsibilityTypeDTO.getName(), id, unitId);
        if(resultCount <=0){
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "message.responsibilityType", id);
        }else{
            LOGGER.info("Data updated successfully for id : {} and name updated name is : {}", id, responsibilityTypeDTO.getName());
        }
        return responsibilityTypeDTO;


    }


    public List<ResponsibilityTypeDTO> saveAndSuggestResponsibilityTypes(Long countryId, Long unitId, List<ResponsibilityTypeDTO> responsibilityTypeDTOS) {

        List<ResponsibilityTypeDTO> result = createResponsibilityType(unitId, responsibilityTypeDTOS);
        responsibilityTypeService.saveSuggestedResponsibilityTypesFromUnit(countryId, responsibilityTypeDTOS);
        return result;
    }

}

package com.kairos.service.master_data.processing_activity_masterdata;

import com.kairos.commons.custom_exception.DataNotFoundByIdException;
import com.kairos.commons.custom_exception.DuplicateDataException;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.gdpr.metadata.ResponsibilityTypeDTO;
import com.kairos.enums.gdpr.SuggestedDataStatus;
import com.kairos.persistence.model.master_data.default_proc_activity_setting.ResponsibilityType;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.responsibility_type.ResponsibilityTypeRepository;
import com.kairos.response.dto.common.ResponsibilityTypeResponseDTO;
import com.kairos.service.exception.ExceptionService;
import com.kairos.utils.ComparisonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;


@Service
public class ResponsibilityTypeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResponsibilityTypeService.class);

    @Inject
    private ExceptionService exceptionService;

    @Inject
    private ResponsibilityTypeRepository responsibilityTypeRepository;


    /**
     * @param countryId
     * @param
     * @param responsibilityTypeDTOS
     * @return return map which contain list of new ResponsibilityType and list of existing ResponsibilityType if ResponsibilityType already exist
     * @description this method create new ResponsibilityType if ResponsibilityType not exist with same name ,
     * and if exist then simply add  ResponsibilityType to existing list and return list ;
     * findByNamesList()  return list of existing ResponsibilityType using collation ,used for case insensitive result
     */
    public List<ResponsibilityTypeDTO> createResponsibilityType(Long countryId, List<ResponsibilityTypeDTO> responsibilityTypeDTOS, boolean isSuggestion) {
        Set<String> existingResponsibilityTypeNames = responsibilityTypeRepository.findNameByCountryIdAndDeleted(countryId);
        Set<String> responsibilityTypeNames = ComparisonUtils.getNewMetaDataNames(responsibilityTypeDTOS,existingResponsibilityTypeNames );
        List<ResponsibilityType> responsibilityTypes = new ArrayList<>();
        if (!responsibilityTypeNames.isEmpty()) {
            for (String name : responsibilityTypeNames) {
                ResponsibilityType responsibilityType = new ResponsibilityType(countryId, name);
                if (isSuggestion) {
                    responsibilityType.setSuggestedDataStatus(SuggestedDataStatus.PENDING);
                    responsibilityType.setSuggestedDate(LocalDate.now());
                } else {
                    responsibilityType.setSuggestedDataStatus(SuggestedDataStatus.APPROVED);
                }
                responsibilityTypes.add(responsibilityType);
            }
            responsibilityTypeRepository.saveAll(responsibilityTypes);

        }
        return ObjectMapperUtils.copyPropertiesOfListByMapper(responsibilityTypes, ResponsibilityTypeDTO.class);

    }

    /**
     * @param countryId
     * @param
     * @return list of ResponsibilityType
     */
    public List<ResponsibilityTypeResponseDTO> getAllResponsibilityType(Long countryId) {
        return responsibilityTypeRepository.findAllByCountryIdAndSortByCreatedDate(countryId);
    }

    /**
     * @param id id of ResponsibilityType
     * @return ResponsibilityType object fetch by given id
     * @throws DataNotFoundByIdException throw exception if ResponsibilityType not found for given id
     */
    public ResponsibilityType getResponsibilityType(Long countryId, Long id) {
        ResponsibilityType exist = responsibilityTypeRepository.findByIdAndCountryIdAndDeletedFalse(id, countryId);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("No data found");
        } else {
            return exist;

        }
    }


    public Boolean deleteResponsibilityType(Long countryId, Long id) {
        Integer resultCount = responsibilityTypeRepository.deleteByIdAndCountryId(id, countryId);
        if (resultCount > 0) {
            LOGGER.info("Responsibility Type deleted successfully for id :: {}", id);
        } else {
            throw new DataNotFoundByIdException("No data found");
        }
        return true;
    }


    /***
     * @throws DuplicateDataException throw exception if ResponsibilityType data not exist for given id
     * @param countryId
     * @param
     * @param id id of ResponsibilityType
     * @param responsibilityTypeDTO
     * @return ResponsibilityType updated object
     */

    public ResponsibilityTypeDTO updateResponsibilityType(Long countryId, Long id, ResponsibilityTypeDTO responsibilityTypeDTO) {


        ResponsibilityType responsibilityType = responsibilityTypeRepository.findByCountryIdAndName(countryId, responsibilityTypeDTO.getName());
        if (Optional.ofNullable(responsibilityType).isPresent()) {
            if (id.equals(responsibilityType.getId())) {
                return responsibilityTypeDTO;
            }
            throw new DuplicateDataException("data  exist for  " + responsibilityTypeDTO.getName());
        }
        Integer resultCount = responsibilityTypeRepository.updateMasterMetadataName(responsibilityTypeDTO.getName(), id, countryId);
        if (resultCount <= 0) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "message.resposibilityType", id);
        } else {
            LOGGER.info("Data updated successfully for id : {} and name updated name is : {}", id, responsibilityTypeDTO.getName());
        }
        return responsibilityTypeDTO;


    }


    /**
     * @param countryId
     * @param responsibilityTypeDTOS - Responsibility type suggested by unit
     * @return
     * @description method save ResponsibilityType suggested by unit
     */
    public void saveSuggestedResponsibilityTypesFromUnit(Long countryId, List<ResponsibilityTypeDTO> responsibilityTypeDTOS) {
         createResponsibilityType(countryId, responsibilityTypeDTOS, true);
    }


    /**
     * @param countryId
     * @param responsibilityTypeIds
     * @param suggestedDataStatus
     * @return
     */
    public List<ResponsibilityType> updateSuggestedStatusOfResponsibilityTypeList(Long countryId, Set<Long> responsibilityTypeIds, SuggestedDataStatus suggestedDataStatus) {

        Integer updateCount = responsibilityTypeRepository.updateMetadataStatus(countryId, responsibilityTypeIds, suggestedDataStatus);
        if (updateCount > 0) {
            LOGGER.info("Responsibility Types are updated successfully with ids :: {}", responsibilityTypeIds);
        } else {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "message.resposibilityType", responsibilityTypeIds);
        }
        return responsibilityTypeRepository.findAllByIds(responsibilityTypeIds);
    }

}

    
    
    


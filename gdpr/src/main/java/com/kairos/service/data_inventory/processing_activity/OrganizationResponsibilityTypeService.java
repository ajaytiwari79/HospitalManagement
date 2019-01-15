package com.kairos.service.data_inventory.processing_activity;


import com.kairos.commons.custom_exception.DataNotFoundByIdException;
import com.kairos.commons.custom_exception.DuplicateDataException;
import com.kairos.commons.custom_exception.InvalidRequestException;
import com.kairos.dto.gdpr.metadata.ResponsibilityTypeDTO;
import com.kairos.persistence.model.master_data.default_proc_activity_setting.ResponsibilityType;
import com.kairos.persistence.model.master_data.default_proc_activity_setting.ResponsibilityTypeMD;
import com.kairos.persistence.repository.data_inventory.processing_activity.ProcessingActivityMongoRepository;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.responsibility_type.ResponsibilityTypeRepository;
import com.kairos.response.dto.common.ResponsibilityTypeResponseDTO;
import com.kairos.response.dto.data_inventory.ProcessingActivityBasicDTO;
import com.kairos.service.common.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.master_data.processing_activity_masterdata.ResponsibilityTypeService;
import com.kairos.utils.ComparisonUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.constants.AppConstant.EXISTING_DATA_LIST;
import static com.kairos.constants.AppConstant.NEW_DATA_LIST;

@Service
public class OrganizationResponsibilityTypeService extends MongoBaseService {


    private static final Logger LOGGER = LoggerFactory.getLogger(OrganizationResponsibilityTypeService.class);

    @Inject
    private ResponsibilityTypeRepository responsibilityTypeRepository;

    @Inject
    private ExceptionService exceptionService;

    @Inject
    private ResponsibilityTypeService responsibilityTypeService;

    @Inject
    private ProcessingActivityMongoRepository processingActivityMongoRepository;


    /**
     * @param organizationId
     * @param responsibilityTypeDTOS
     * @return return map which contain list of new ResponsibilityType and list of existing ResponsibilityType if ResponsibilityType already exist
     * @description this method create new ResponsibilityType if ResponsibilityType not exist with same name ,
     * and if exist then simply add  ResponsibilityType to existing list and return list ;
     * findMetaDataByNamesAndCountryId()  return list of existing ResponsibilityType using collation ,used for case insensitive result
     */
    public Map<String, List<ResponsibilityTypeMD>> createResponsibilityType(Long organizationId, List<ResponsibilityTypeDTO> responsibilityTypeDTOS) {

        Map<String, List<ResponsibilityTypeMD>> result = new HashMap<>();
        Set<String> responsibilityTypeNames = new HashSet<>();
        if (!responsibilityTypeDTOS.isEmpty()) {
            for (ResponsibilityTypeDTO responsibilityType : responsibilityTypeDTOS) {
                if (!StringUtils.isBlank(responsibilityType.getName())) {
                    responsibilityTypeNames.add(responsibilityType.getName());
                } else
                    throw new InvalidRequestException("name could not be empty or null");

            }
            List<String> nameInLowerCase = responsibilityTypeNames.stream().map(String::toLowerCase)
                    .collect(Collectors.toList());
            //TODO still need to update we can return name of list from here and can apply removeAll on list
            List<ResponsibilityTypeMD> existing = responsibilityTypeRepository.findByOrganizationIdAndDeletedAndNameIn(organizationId, false, nameInLowerCase);
            responsibilityTypeNames = ComparisonUtils.getNameListForMetadata(existing, responsibilityTypeNames);

            List<ResponsibilityTypeMD> newResponsibilityTypes = new ArrayList<>();
            if (!responsibilityTypeNames.isEmpty()) {
                for (String name : responsibilityTypeNames) {
                    ResponsibilityTypeMD newResponsibilityType = new ResponsibilityTypeMD(name);
                    newResponsibilityType.setOrganizationId(organizationId);
                    newResponsibilityTypes.add(newResponsibilityType);
                }
                newResponsibilityTypes = responsibilityTypeRepository.saveAll(newResponsibilityTypes);

            }
            result.put(EXISTING_DATA_LIST, existing);
            result.put(NEW_DATA_LIST, newResponsibilityTypes);
            return result;
        } else
            throw new InvalidRequestException("list cannot be empty");


    }

    /**
     * * @param organizationId
     *
     * @return list of ResponsibilityType
     */
    public List<ResponsibilityTypeResponseDTO> getAllResponsibilityType(Long organizationId) {
        return responsibilityTypeRepository.findAllByOrganizationIdAndSortByCreatedDate(organizationId);
    }

    /**
     * @param organizationId
     * @param id             id of ResponsibilityType
     * @return ResponsibilityType object fetch by given id
     * @throws DataNotFoundByIdException throw exception if ResponsibilityType not found for given id
     */
    public ResponsibilityTypeMD getResponsibilityType(Long organizationId, Long id) {

        ResponsibilityTypeMD exist = responsibilityTypeRepository.findByIdAndOrganizationIdAndDeleted(id, organizationId, false);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            return exist;

        }
    }


    public Boolean deleteResponsibilityType(Long unitId, BigInteger responsibilityTypeId) {

        List<ProcessingActivityBasicDTO> processingActivities = processingActivityMongoRepository.findAllProcessingActivityLinkedWithResponsibilityType(unitId, responsibilityTypeId);
        if (!processingActivities.isEmpty()) {
                exceptionService.metaDataLinkedWithProcessingActivityException("message.metaData.linked.with.ProcessingActivity", "Responsibility Type", new StringBuilder(processingActivities.stream().map(ProcessingActivityBasicDTO::getName).map(String::toString).collect(Collectors.joining(","))));
        }
        //responsibilityTypeRepository.safeDeleteById(responsibilityTypeId) ;
        return true;
    }

    /***
     * @throws DuplicateDataException throw exception if ResponsibilityType data not exist for given id
     * @param organizationId
     * @param id id of ResponsibilityType
     * @param responsibilityTypeDTO
     * @return ResponsibilityType updated object
     */
    public ResponsibilityTypeDTO updateResponsibilityType(Long organizationId, Long id, ResponsibilityTypeDTO responsibilityTypeDTO) {


        ResponsibilityTypeMD responsibilityType = responsibilityTypeRepository.findByOrganizationIdAndDeletedAndName(organizationId, false,responsibilityTypeDTO.getName());
        if (Optional.ofNullable(responsibilityType).isPresent()) {
            if (id.equals(responsibilityType.getId())) {
                return responsibilityTypeDTO;
            }
            exceptionService.duplicateDataException("message.duplicate", "Responsibility Type", responsibilityType.getName());
        }
        Integer resultCount =  responsibilityTypeRepository.updateMetadataName(responsibilityTypeDTO.getName(), id, organizationId);
        if(resultCount <=0){
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Responsibility Typ", id);
        }else{
            LOGGER.info("Data updated successfully for id : {} and name updated name is : {}", id, responsibilityTypeDTO.getName());
        }
        return responsibilityTypeDTO;


    }


    public Map<String, List<ResponsibilityTypeMD>> saveAndSuggestResponsibilityTypes(Long countryId, Long organizationId, List<ResponsibilityTypeDTO> responsibilityTypeDTOS) {

        Map<String, List<ResponsibilityTypeMD>> result = createResponsibilityType(organizationId, responsibilityTypeDTOS);
        List<ResponsibilityTypeMD> masterResponsibilityTypeSuggestedByUnit = responsibilityTypeService.saveSuggestedResponsibilityTypesFromUnit(countryId, responsibilityTypeDTOS);
        if (!masterResponsibilityTypeSuggestedByUnit.isEmpty()) {
            result.put("SuggestedData", masterResponsibilityTypeSuggestedByUnit);
        }
        return result;
    }

}

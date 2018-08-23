package com.kairos.service.data_inventory.processing_activity;


import com.kairos.custom_exception.DataNotExists;
import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.custom_exception.InvalidRequestException;
import com.kairos.gdpr.metadata.ResponsibilityTypeDTO;
import com.kairos.persistance.model.master_data.default_proc_activity_setting.ResponsibilityType;
import com.kairos.persistance.repository.data_inventory.processing_activity.ProcessingActivityMongoRepository;
import com.kairos.persistance.repository.master_data.processing_activity_masterdata.responsibility_type.ResponsibilityTypeMongoRepository;
import com.kairos.response.dto.common.ResponsibilityTypeResponseDTO;
import com.kairos.service.common.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.master_data.processing_activity_masterdata.ResponsibilityTypeService;
import com.kairos.utils.ComparisonUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;

import static com.kairos.constants.AppConstant.EXISTING_DATA_LIST;
import static com.kairos.constants.AppConstant.NEW_DATA_LIST;

@Service
public class OrganizationResponsibilityTypeService extends MongoBaseService {


    private static final Logger LOGGER = LoggerFactory.getLogger(OrganizationResponsibilityTypeService.class);

    @Inject
    private ResponsibilityTypeMongoRepository responsibilityTypeMongoRepository;

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
    public Map<String, List<ResponsibilityType>> createResponsibilityType(Long organizationId, List<ResponsibilityTypeDTO> responsibilityTypeDTOS) {

        Map<String, List<ResponsibilityType>> result = new HashMap<>();
        Set<String> responsibilityTypeNames = new HashSet<>();
        if (!responsibilityTypeDTOS.isEmpty()) {
            for (ResponsibilityTypeDTO responsibilityType : responsibilityTypeDTOS) {
                if (!StringUtils.isBlank(responsibilityType.getName())) {
                    responsibilityTypeNames.add(responsibilityType.getName());
                } else
                    throw new InvalidRequestException("name could not be empty or null");

            }
            List<ResponsibilityType> existing = findMetaDataByNameAndUnitId(organizationId, responsibilityTypeNames, ResponsibilityType.class);
            responsibilityTypeNames = ComparisonUtils.getNameListForMetadata(existing, responsibilityTypeNames);

            List<ResponsibilityType> newResponsibilityTypes = new ArrayList<>();
            if (!responsibilityTypeNames.isEmpty()) {
                for (String name : responsibilityTypeNames) {

                    ResponsibilityType newResponsibilityType = new ResponsibilityType(name);
                    newResponsibilityType.setOrganizationId(organizationId);
                    newResponsibilityTypes.add(newResponsibilityType);

                }

                newResponsibilityTypes = responsibilityTypeMongoRepository.saveAll(getNextSequence(newResponsibilityTypes));

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
        return responsibilityTypeMongoRepository.findAllOrganizationResponsibilityTypes(organizationId);
    }

    /**
     * @param organizationId
     * @param id             id of ResponsibilityType
     * @return ResponsibilityType object fetch by given id
     * @throws DataNotFoundByIdException throw exception if ResponsibilityType not found for given id
     */
    public ResponsibilityType getResponsibilityType(Long organizationId, BigInteger id) {

        ResponsibilityType exist = responsibilityTypeMongoRepository.findByOrganizationIdAndId(organizationId, id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            return exist;

        }
    }


    public Boolean deleteResponsibilityType(Long unitId, BigInteger responsibilityTypeId) {

        List<String> processingActivities = processingActivityMongoRepository.findAllProcessingActivityLinkedWithResponsibilityType(unitId, responsibilityTypeId);
        if (!processingActivities.isEmpty()) {
            exceptionService.metaDataLinkedWithProcessingActivityException("message.metaData.linked.with.ProcessingActivity", "Responsibility Type", processingActivities.get(0));
        }
        ResponsibilityType responsibilityType = responsibilityTypeMongoRepository.findByOrganizationIdAndId(unitId, responsibilityTypeId);
        if (!Optional.ofNullable(responsibilityType).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Responsibility Type", responsibilityTypeId);
        }
        delete(responsibilityType);
        return true;
    }

    /***
     * @throws DuplicateDataException throw exception if ResponsibilityType data not exist for given id
     * @param organizationId
     * @param id id of ResponsibilityType
     * @param responsibilityTypeDTO
     * @return ResponsibilityType updated object
     */
    public ResponsibilityTypeDTO updateResponsibilityType(Long organizationId, BigInteger id, ResponsibilityTypeDTO responsibilityTypeDTO) {


        ResponsibilityType responsibilityType = responsibilityTypeMongoRepository.findByOrganizationIdAndName(organizationId, responsibilityTypeDTO.getName());
        if (Optional.ofNullable(responsibilityType).isPresent()) {
            if (id.equals(responsibilityType.getId())) {
                return responsibilityTypeDTO;
            }
            exceptionService.duplicateDataException("message.duplicate", "Responsibility Type", responsibilityType.getName());
        }
        responsibilityType = responsibilityTypeMongoRepository.findByid(id);
        if (!Optional.ofNullable(responsibilityType).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Responsibility Type", id);
        }
        responsibilityType.setName(responsibilityTypeDTO.getName());
        responsibilityTypeMongoRepository.save(responsibilityType);
        return responsibilityTypeDTO;


    }

    /**
     * @param organizationId
     * @param name           name of ResponsibilityType
     * @return ResponsibilityType object fetch on basis of  name
     * @throws DataNotExists throw exception if ResponsibilityType not exist for given name
     */
    public ResponsibilityType getResponsibilityTypeByName(Long organizationId, String name) {


        if (!StringUtils.isBlank(name)) {
            ResponsibilityType exist = responsibilityTypeMongoRepository.findByOrganizationIdAndName(organizationId, name);
            if (!Optional.ofNullable(exist).isPresent()) {
                throw new DataNotExists("data not exist for name " + name);
            }
            return exist;
        } else
            throw new InvalidRequestException("request param cannot be empty  or null");

    }


    public Map<String, List<ResponsibilityType>> saveAndSuggestResponsibilityTypes(Long countryId, Long organizationId, List<ResponsibilityTypeDTO> ResponsibilityTypeDTOS) {

        Map<String, List<ResponsibilityType>> result;
        result = createResponsibilityType(organizationId, ResponsibilityTypeDTOS);
        List<ResponsibilityType> masterResponsibilityTypeSuggestedByUnit = responsibilityTypeService.saveSuggestedResponsibilityTypesFromUnit(countryId, ResponsibilityTypeDTOS);
        if (!masterResponsibilityTypeSuggestedByUnit.isEmpty()) {
            result.put("SuggestedData", masterResponsibilityTypeSuggestedByUnit);
        }
        return result;
    }

}

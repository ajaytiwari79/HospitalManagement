package com.kairos.service.master_data.processing_activity_masterdata;


import com.kairos.custom_exception.DataNotExists;
import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.custom_exception.InvalidRequestException;
import com.kairos.persistance.model.master_data.default_proc_activity_setting.ResponsibilityType;
import com.kairos.persistance.repository.master_data.processing_activity_masterdata.responsibility_type.ResponsibilityTypeMongoRepository;
import com.kairos.response.dto.metadata.ResponsibilityTypeResponseDTO;
import com.kairos.service.common.MongoBaseService;
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
public class ResponsibilityTypeService extends MongoBaseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResponsibilityTypeService.class);

    @Inject
    private ResponsibilityTypeMongoRepository responsibilityTypeMongoRepository;

    @Inject
    private ComparisonUtils comparisonUtils;


    /**
     * @param countryId
     * @param organizationId
     * @param responsibilityTypes
     * @return return map which contain list of new ResponsibilityType and list of existing ResponsibilityType if ResponsibilityType already exist
     * @description this method create new ResponsibilityType if ResponsibilityType not exist with same name ,
     * and if exist then simply add  ResponsibilityType to existing list and return list ;
     * findByNamesList()  return list of existing ResponsibilityType using collation ,used for case insensitive result
     */
    public Map<String, List<ResponsibilityType>> createResponsibilityType(Long countryId, Long organizationId, List<ResponsibilityType> responsibilityTypes) {

        Map<String, List<ResponsibilityType>> result = new HashMap<>();
        Set<String> responsibilityTypeNames = new HashSet<>();
        if (responsibilityTypes.size() != 0) {
            for (ResponsibilityType responsibilityType : responsibilityTypes) {
                if (!StringUtils.isBlank(responsibilityType.getName())) {
                    responsibilityTypeNames.add(responsibilityType.getName());
                } else
                    throw new InvalidRequestException("name could not be empty or null");

            }
            List<ResponsibilityType> existing = findByNamesList(countryId, organizationId, responsibilityTypeNames, ResponsibilityType.class);
            responsibilityTypeNames = comparisonUtils.getNameListForMetadata(existing, responsibilityTypeNames);

            List<ResponsibilityType> newResponsibilityTypes = new ArrayList<>();
            if (responsibilityTypeNames.size() != 0) {
                for (String name : responsibilityTypeNames) {

                    ResponsibilityType newResponsibilityType = new ResponsibilityType(name, countryId);
                    newResponsibilityType.setOrganizationId(organizationId);
                    newResponsibilityTypes.add(newResponsibilityType);

                }

                newResponsibilityTypes = responsibilityTypeMongoRepository.saveAll(sequenceGenerator(newResponsibilityTypes));

            }
            result.put(EXISTING_DATA_LIST, existing);
            result.put(NEW_DATA_LIST, newResponsibilityTypes);
            return result;
        } else
            throw new InvalidRequestException("list cannot be empty");


    }

    /**
     * @param countryId
     * @param organizationId
     * @return list of ResponsibilityType
     */
    public List<ResponsibilityTypeResponseDTO> getAllResponsibilityType(Long countryId, Long organizationId) {
        return responsibilityTypeMongoRepository.findAllResponsibilityTypes(countryId, organizationId);
    }

    /**
     * @param countryId
     * @param organizationId
     * @param id             id of ResponsibilityType
     * @return ResponsibilityType object fetch by given id
     * @throws DataNotFoundByIdException throw exception if ResponsibilityType not found for given id
     */
    public ResponsibilityType getResponsibilityType(Long countryId, Long organizationId, BigInteger id) {

        ResponsibilityType exist = responsibilityTypeMongoRepository.findByIdAndNonDeleted(countryId, organizationId, id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            return exist;

        }
    }


    public Boolean deleteResponsibilityType(Long countryId, Long organizationId, BigInteger id) {

        ResponsibilityType exist = responsibilityTypeMongoRepository.findByIdAndNonDeleted(countryId, organizationId, id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            delete(exist);
            return true;

        }
    }

    /***
     * @throws DuplicateDataException throw exception if ResponsibilityType data not exist for given id
     * @param countryId
     * @param organizationId
     * @param id id of ResponsibilityType
     * @param responsibilityType
     * @return ResponsibilityType updated object
     */
    public ResponsibilityType updateResponsibilityType(Long countryId, Long organizationId, BigInteger id, ResponsibilityType responsibilityType) {


        ResponsibilityType exist = responsibilityTypeMongoRepository.findByName(countryId, organizationId, responsibilityType.getName());
        if (Optional.ofNullable(exist).isPresent()) {
            if (id.equals(exist.getId())) {
                return exist;
            }
            throw new DuplicateDataException("data  exist for  " + responsibilityType.getName());
        } else {
            exist = responsibilityTypeMongoRepository.findByid(id);
            exist.setName(responsibilityType.getName());
            return responsibilityTypeMongoRepository.save(sequenceGenerator(exist));

        }
    }

    /**
     * @param countryId
     * @param organizationId
     * @param name           name of ResponsibilityType
     * @return ResponsibilityType object fetch on basis of  name
     * @throws DataNotExists throw exception if ResponsibilityType not exist for given name
     */
    public ResponsibilityType getResponsibilityTypeByName(Long countryId, Long organizationId, String name) {


        if (!StringUtils.isBlank(name)) {
            ResponsibilityType exist = responsibilityTypeMongoRepository.findByName(countryId, organizationId, name);
            if (!Optional.ofNullable(exist).isPresent()) {
                throw new DataNotExists("data not exist for name " + name);
            }
            return exist;
        } else
            throw new InvalidRequestException("request param cannot be empty  or null");

    }


    /**
     * @param countryId
     * @param parentOrganizationId -id of parent organization
     * @param unitId               - id of cuurent organization
     * @return method return list of processingPurposes (organzation processing purpose and processing purposes which were not inherited by organization from parent till now )
     */
    public List<ResponsibilityTypeResponseDTO> getAllNotInheritedResponsibilityTypesFromParentOrgAndUnitResponsibilityType(Long countryId, Long parentOrganizationId, Long unitId) {

        return responsibilityTypeMongoRepository.getAllNotInheritedResponsibilityTypesFromParentOrgAndUnitResponsibilityType(countryId, parentOrganizationId, unitId);
    }


}

    
    
    


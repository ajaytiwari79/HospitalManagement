package com.kairos.service.master_data_management.processing_activity_masterdata;


import com.kairos.custom_exception.DataNotExists;
import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.custom_exception.InvalidRequestException;
import com.kairos.persistance.model.master_data_management.processing_activity_masterdata.ResponsibilityType;
import com.kairos.persistance.repository.master_data_management.processing_activity_masterdata.ResponsibilityTypeMongoRepository;
import com.kairos.service.MongoBaseService;
import com.kairos.utils.ComparisonUtils;
import com.kairos.utils.userContext.UserContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;

@Service
public class ResponsibilityTypeService extends MongoBaseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResponsibilityTypeService.class);

    @Inject
    private ResponsibilityTypeMongoRepository responsibilityTypeMongoRepository;

    @Inject
    private ComparisonUtils comparisonUtils;

    public Map<String, List<ResponsibilityType>> createResponsibilityType(Long countryId,Long organizationId,List<ResponsibilityType> rsponsibilityTypes) {

        Map<String, List<ResponsibilityType>> result = new HashMap<>();
        List<ResponsibilityType> newResponsibilityTypes = new ArrayList<>();
        Set<String> names = new HashSet<>();
        if (rsponsibilityTypes.size() != 0) {
            for (ResponsibilityType responsibilityType : rsponsibilityTypes) {
                if (!StringUtils.isBlank(responsibilityType.getName())) {
                    names.add(responsibilityType.getName());
                } else
                    throw new InvalidRequestException("name could not be empty or null");

            }
            List<ResponsibilityType> existing = responsibilityTypeMongoRepository.findByCountryAndNameList(countryId,organizationId, names);
            if (existing.size() != 0) {
                Set<String> existingNames = new HashSet<>();
                existing.forEach(responsibilityType -> {
                    existingNames.add(responsibilityType.getName());
                });
                names = comparisonUtils.checkForExistingObjectAndRemoveFromList(names, existingNames);
            }

            if (names.size() != 0) {
                for (String name : names) {

                    ResponsibilityType newResponsibilityType = new ResponsibilityType();
                    newResponsibilityType.setName(name);
                    newResponsibilityType.setCountryId(countryId);
                    newResponsibilityType.setOrganizationId(organizationId);
                    newResponsibilityTypes.add(newResponsibilityType);

                }

                newResponsibilityTypes = save(newResponsibilityTypes);

            }
            result.put("existing", existing);
            result.put("new", newResponsibilityTypes);
            return result;
        } else
            throw new InvalidRequestException("list cannot be empty");


    }

    public List<ResponsibilityType> getAllResponsibilityType(Long countryId,Long organizationId) {
        return responsibilityTypeMongoRepository.findAllResponsibilityTypes(countryId,organizationId);
    }


    public ResponsibilityType getResponsibilityType(Long countryId,Long organizationId,BigInteger id) {

        ResponsibilityType exist = responsibilityTypeMongoRepository.findByIdAndNonDeleted(countryId,organizationId,id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            return exist;

        }
    }


    public Boolean deleteResponsibilityType(Long countryId,Long organizationId,BigInteger id) {

        ResponsibilityType exist = responsibilityTypeMongoRepository.findByIdAndNonDeleted(countryId,organizationId,id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            exist.setDeleted(true);
            save(exist);
            return true;

        }
    }


    public ResponsibilityType updateResponsibilityType(Long countryId,Long organizationId,BigInteger id, ResponsibilityType responsibilityType) {


        ResponsibilityType exist = responsibilityTypeMongoRepository.findByName(countryId,organizationId,responsibilityType.getName());
        if (Optional.ofNullable(exist).isPresent() ) {
            if (id.equals(exist.getId())) {
                return exist;
            }
            throw new DuplicateDataException("data  exist for  "+responsibilityType.getName());
        } else {
            exist=responsibilityTypeMongoRepository.findByid(id);
            exist.setName(responsibilityType.getName());
            return save(exist);

        }
    }


    public ResponsibilityType getResponsibilityTypeByName(Long countryId,Long organizationId,String name) {


        if (!StringUtils.isBlank(name)) {
            ResponsibilityType exist = responsibilityTypeMongoRepository.findByName(countryId,organizationId,name);
            if (!Optional.ofNullable(exist).isPresent()) {
                throw new DataNotExists("data not exist for name " + name);
            }
            return exist;
        } else
            throw new InvalidRequestException("request param cannot be empty  or null");

    }


}

    
    
    


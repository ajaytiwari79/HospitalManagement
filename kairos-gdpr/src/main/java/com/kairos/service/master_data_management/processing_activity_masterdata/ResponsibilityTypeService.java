package com.kairos.service.master_data_management.processing_activity_masterdata;


import com.kairos.custom_exception.DataNotExists;
import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.custom_exception.InvalidRequestException;
import com.kairos.persistance.model.master_data_management.processing_activity_masterdata.ResponsibilityType;
import com.kairos.persistance.repository.master_data_management.processing_activity_masterdata.ResponsibilityTypeMongoRepository;
import com.kairos.service.MongoBaseService;
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


    public Map<String, List<ResponsibilityType>> createResponsibilityType(Long countryId, List<ResponsibilityType> rsponsibilityTypes) {
        Map<String, List<ResponsibilityType>> result = new HashMap<>();
        List<ResponsibilityType> existing = new ArrayList<>();
        List<ResponsibilityType> newResponsibilityTypes = new ArrayList<>();
        Set<String> names = new HashSet<>();
        if (rsponsibilityTypes.size() != 0) {
            for (ResponsibilityType responsibilityType : rsponsibilityTypes) {
                if (!StringUtils.isBlank(responsibilityType.getName())) {
                    names.add(responsibilityType.getName());
                } else
                    throw new InvalidRequestException("name could not be empty or null");

            }
            existing = responsibilityTypeMongoRepository.findByCountryAndNameList(countryId, names);
            existing.forEach(item -> names.remove(item.getName()));
            if (names.size() != 0) {
                for (String name : names) {

                    ResponsibilityType newResponsibilityType = new ResponsibilityType();
                    newResponsibilityType.setName(name);
                    newResponsibilityType.setCountryId(countryId);
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

    public List<ResponsibilityType> getAllResponsibilityType() {
        return responsibilityTypeMongoRepository.findAllResponsibilityTypes(UserContext.getCountryId());
    }


    public ResponsibilityType getResponsibilityType(Long countryId, BigInteger id) {

        ResponsibilityType exist = responsibilityTypeMongoRepository.findByIdAndNonDeleted(countryId, id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            return exist;

        }
    }


    public Boolean deleteResponsibilityType(BigInteger id) {

        ResponsibilityType exist = responsibilityTypeMongoRepository.findByid(id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            exist.setDeleted(true);
            save(exist);
            return true;

        }
    }


    public ResponsibilityType updateResponsibilityType(BigInteger id, ResponsibilityType responsibilityType) {


        ResponsibilityType exist = responsibilityTypeMongoRepository.findByName(UserContext.getCountryId(),responsibilityType.getName());
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


    public ResponsibilityType getResponsibilityTypeByName(Long countryId, String name) {


        if (!StringUtils.isBlank(name)) {
            ResponsibilityType exist = responsibilityTypeMongoRepository.findByName(countryId, name);
            if (!Optional.ofNullable(exist).isPresent()) {
                throw new DataNotExists("data not exist for name " + name);
            }
            return exist;
        } else
            throw new InvalidRequestException("request param cannot be empty  or null");

    }


}

    
    
    


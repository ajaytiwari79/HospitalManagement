package com.kairos.service.master_data_management.processing_activity_masterdata;


import com.kairos.custome_exception.DataNotExists;
import com.kairos.custome_exception.DataNotFoundByIdException;
import com.kairos.custome_exception.InvalidRequestException;
import com.kairos.persistance.model.master_data_management.processing_activity_masterdata.ResponsibilityType;
import com.kairos.persistance.repository.master_data_management.processing_activity_masterdata.ResponsibilityTypeMongoRepository;
import com.kairos.service.MongoBaseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;

@Service
public class ResponsibilityTypeService extends MongoBaseService {

    @Inject
    private ResponsibilityTypeMongoRepository responsibilityTypeMongoRepository;


    public Map<String, List<ResponsibilityType>> createResponsibilityType(List<ResponsibilityType> rsponsibilityTypes) {
        Map<String, List<ResponsibilityType>> result = new HashMap<>();
        List<ResponsibilityType> existing= new ArrayList<>();
        List<ResponsibilityType> newResponsibilityTypes= new ArrayList<>();
        if (rsponsibilityTypes.size() != 0) {
            for (ResponsibilityType responsibilityType : rsponsibilityTypes) {

                ResponsibilityType exist = responsibilityTypeMongoRepository.findByName(responsibilityType.getName());
                if (Optional.ofNullable(exist).isPresent()) {
                    existing.add(exist);

                } else {
                    ResponsibilityType newResponsibilityType = new ResponsibilityType();
                    newResponsibilityType.setName(responsibilityType.getName());
                    newResponsibilityTypes.add(save(newResponsibilityType));
                }
            }

            result.put("existing", existing);
            result.put("new", newResponsibilityTypes);
            return result;
        } else
            throw new InvalidRequestException("list cannot be empty");


    }

    public List<ResponsibilityType> getAllResponsibilityType() {
        List<ResponsibilityType> result = responsibilityTypeMongoRepository.findAllResponsibilityTypes();
        if (result.size() != 0) {
            return result;

        } else
            throw new DataNotExists("ResponsibilityType not exist please create purpose ");
    }


    public ResponsibilityType getResponsibilityType(BigInteger id) {

        ResponsibilityType exist = responsibilityTypeMongoRepository.findByIdAndNonDeleted(id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            return exist;

        }
    }


    public Boolean deleteResponsibilityType(BigInteger id) {

        ResponsibilityType exist = responsibilityTypeMongoRepository.findByIdAndNonDeleted(id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            exist.setDeleted(true);
            save(exist);
            return true;

        }
    }


    public ResponsibilityType updateResponsibilityType(BigInteger id, ResponsibilityType responsibilityType) {


        ResponsibilityType exist = responsibilityTypeMongoRepository.findByIdAndNonDeleted(id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            exist.setName(responsibilityType.getName());

            return save(exist);

        }
    }




    public ResponsibilityType getResponsibilityTypeByName(String name) {


        if (!StringUtils.isBlank(name)) {
            ResponsibilityType exist = responsibilityTypeMongoRepository.findByName(name);
            if (!Optional.ofNullable(exist).isPresent()) {
                throw new DataNotExists("data not exist for name " + name);
            }
            return exist;
        }
        else
            throw new InvalidRequestException("request param cannot be empty  or null");

    }








}

    
    
    


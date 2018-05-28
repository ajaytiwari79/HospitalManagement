package com.kairos.service.master_data_management.processing_activity_masterdata;


import com.kairos.custome_exception.DataNotExists;
import com.kairos.custome_exception.DataNotFoundByIdException;
import com.kairos.custome_exception.InvalidRequestException;
import com.kairos.persistance.model.master_data_management.processing_activity_masterdata.Destination;
import com.kairos.persistance.repository.master_data_management.processing_activity_masterdata.DestinationMongoRepository;
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
public class DestinationService extends MongoBaseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DestinationService.class);

    @Inject
    private DestinationMongoRepository destinationMongoRepository;


    public Map<String, List<Destination>> createDestination(Long countryId,List<Destination> destinations) {
        Map<String, List<Destination>> result = new HashMap<>();
        List<Destination> existing = new ArrayList<>();
        List<Destination> newDestinations = new ArrayList<>();
        if (destinations.size() != 0) {
            for (Destination destination : destinations) {
                if (!StringUtils.isBlank(destination.getName())) {
                    Destination exist = destinationMongoRepository.findByName(countryId,destination.getName());
                    if (Optional.ofNullable(exist).isPresent()) {
                        existing.add(exist);

                    } else {
                        Destination newDestination = new Destination();
                        newDestination.setName(destination.getName());
                        newDestination.setCountryId(countryId);
                        newDestinations.add(save(newDestination));
                    }
                } else
                    throw new InvalidRequestException("name could not be empty or null");

            }

            result.put("existing", existing);
            result.put("new", newDestinations);
            return result;
        } else
            throw new InvalidRequestException("list cannot be empty");


    }

    public List<Destination> getAllDestination() {
       return destinationMongoRepository.findAllDestinations(UserContext.getCountryId());
        }


    public Destination getDestination(Long countryId,BigInteger id) {

        Destination exist = destinationMongoRepository.findByIdAndNonDeleted(countryId,id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            return exist;

        }
    }


    public Boolean deleteDestination(BigInteger id) {

        Destination exist = destinationMongoRepository.findByid(id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            exist.setDeleted(true);
            save(exist);
            return true;

        }
    }


    public Destination updateDestination(BigInteger id, Destination destination) {


        Destination exist = destinationMongoRepository.findByid(id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            exist.setName(destination.getName());

            return save(exist);

        }
    }


    public Destination getDestinationByName(Long countryId,String name) {


        if (!StringUtils.isBlank(name)) {
            Destination exist = destinationMongoRepository.findByName(countryId,name);
            if (!Optional.ofNullable(exist).isPresent()) {
                throw new DataNotExists("data not exist for name " + name);
            }
            return exist;
        } else
            throw new InvalidRequestException("request param cannot be empty  or null");

    }


}

    
    
    


package com.kairos.service.master_data_management.processing_activity_masterdata;


import com.kairos.custome_exception.DataNotExists;
import com.kairos.custome_exception.DataNotFoundByIdException;
import com.kairos.custome_exception.InvalidRequestException;
import com.kairos.persistance.model.master_data_management.processing_activity_masterdata.Destination;
import com.kairos.persistance.repository.master_data_management.processing_activity_masterdata.DestinationMongoRepository;
import com.kairos.service.MongoBaseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;

@Service
public class DestinationService extends MongoBaseService {

    @Inject
    private DestinationMongoRepository destinationMongoRepository;


    public Map<String, List<Destination>> createDestination(List<Destination> destinations) {
        Map<String, List<Destination>> result = new HashMap<>();
        List<Destination> existing= new ArrayList<>();
        List<Destination> newDestinations= new ArrayList<>();
        if (destinations.size() != 0) {
            for (Destination destination : destinations) {

                Destination exist = destinationMongoRepository.findByName(destination.getName());
                if (Optional.ofNullable(exist).isPresent()) {
                    existing.add(exist);

                } else {
                    Destination newDestination = new Destination();
                    newDestination.setName(destination.getName());
                    newDestinations.add(save(newDestination));
                }
            }

            result.put("existing", existing);
            result.put("new", newDestinations);
            return result;
        } else
            throw new InvalidRequestException("list cannot be empty");


    }

    public List<Destination> getAllDestination() {
        List<Destination> result = destinationMongoRepository.findAllDestinations();
        if (result.size() != 0) {
            return result;

        } else
            throw new DataNotExists("Destination not exist please create purpose ");
    }


    public Destination getDestination(BigInteger id) {

        Destination exist = destinationMongoRepository.findByIdAndNonDeleted(id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            return exist;

        }
    }


    public Boolean deleteDestination(BigInteger id) {

        Destination exist = destinationMongoRepository.findByIdAndNonDeleted(id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            exist.setDeleted(true);
            save(exist);
            return true;

        }
    }


    public Destination updateDestination(BigInteger id, Destination destination) {


        Destination exist = destinationMongoRepository.findByIdAndNonDeleted(id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            exist.setName(destination.getName());

            return save(exist);

        }
    }


    public Destination getDestinationByName(String name) {


        if (!StringUtils.isBlank(name)) {
            Destination exist = destinationMongoRepository.findByName(name);
            if (!Optional.ofNullable(exist).isPresent()) {
                throw new DataNotExists("data not exist for name " + name);
            }
            return exist;
        }
        else
            throw new InvalidRequestException("request param cannot be empty  or null");

    }





}

    
    
    


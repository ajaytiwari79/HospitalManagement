package com.kairos.service.master_data.processing_activity_masterdata;


import com.kairos.custom_exception.DataNotExists;
import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.custom_exception.InvalidRequestException;
import com.kairos.persistance.model.master_data.processing_activity_masterdata.Destination;
import com.kairos.persistance.repository.master_data.processing_activity_masterdata.DestinationMongoRepository;
import com.kairos.service.common.MongoBaseService;
import com.kairos.utils.ComparisonUtils;
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


    @Inject
    private ComparisonUtils comparisonUtils;


    public Map<String, List<Destination>> createDestination(Long countryId, Long organizationId, List<Destination> destinations) {

        Map<String, List<Destination>> result = new HashMap<>();
        Set<String> destinationNames = new HashSet<>();
        if (destinations.size() != 0) {
            for (Destination destination : destinations) {
                if (!StringUtils.isBlank(destination.getName())) {
                    destinationNames.add(destination.getName());
                } else
                    throw new InvalidRequestException("name could not be empty or null");

            }
            List<Destination> existing = findByNamesList(countryId,organizationId,destinationNames,Destination.class);
            destinationNames = comparisonUtils.getNameListForMetadata(existing, destinationNames);

            List<Destination> newDestinations = new ArrayList<>();
            if (destinationNames.size() != 0) {
                for (String name : destinationNames) {

                    Destination newDestination = new Destination();
                    newDestination.setName(name);
                    newDestination.setCountryId(countryId);
                    newDestination.setOrganizationId(organizationId);
                    newDestinations.add(newDestination);

                }

                newDestinations = destinationMongoRepository.saveAll(sequenceGenerator(newDestinations));
            }

            result.put("existing", existing);
            result.put("new", newDestinations);
            return result;
        } else
            throw new InvalidRequestException("list cannot be empty");


    }

    public List<Destination> getAllDestination(Long countryId, Long organizationId) {
        return destinationMongoRepository.findAllDestinations(countryId, organizationId);
    }


    public Destination getDestination(Long countryId, Long organizationId, BigInteger id) {

        Destination exist = destinationMongoRepository.findByIdAndNonDeleted(countryId, organizationId, id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            return exist;

        }
    }


    public Boolean deleteDestination(Long countryId, Long organizationId, BigInteger id) {

        Destination exist = destinationMongoRepository.findByIdAndNonDeleted(countryId, organizationId, id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            delete(exist);
            return true;

        }
    }


    public Destination updateDestination(Long countryId, Long organizationId, BigInteger id, Destination destination) {


        Destination exist = destinationMongoRepository.findByName(countryId, organizationId, destination.getName());
        if (Optional.ofNullable(exist).isPresent()) {
            if (id.equals(exist.getId())) {
                return exist;
            }
            throw new DuplicateDataException("data  exist for  " + destination.getName());
        } else {
            exist = destinationMongoRepository.findByid(id);
            exist.setName(destination.getName());
            return destinationMongoRepository.save(sequenceGenerator(exist));

        }
    }


    public Destination getDestinationByName(Long countryId, Long organizationId, String name) {


        if (!StringUtils.isBlank(name)) {
            Destination exist = destinationMongoRepository.findByName(countryId, organizationId, name);
            if (!Optional.ofNullable(exist).isPresent()) {
                throw new DataNotExists("data not exist for name " + name);
            }
            return exist;
        } else
            throw new InvalidRequestException("request param cannot be empty  or null");

    }


}

    
    
    


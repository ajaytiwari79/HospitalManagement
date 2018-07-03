package com.kairos.service.processing_activity;


import com.kairos.custom_exception.DataNotExists;
import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.custom_exception.InvalidRequestException;
import com.kairos.persistance.model.processing_activity.HostingLocation;
import com.kairos.persistance.repository.processing_activity.HostingLocationMongoRepository;
import com.kairos.service.common.MongoBaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Service
public class HostingLocationService  extends MongoBaseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(HostingLocationService.class);

@Inject
    private HostingLocationMongoRepository hostingLocationMongoRepository;



    public HostingLocation createHostingLocation(String hostingLocation) {
        if (StringUtils.isEmpty(hostingLocation))
        {
            throw new InvalidRequestException("requested hostingLocation is null");

        }

        HostingLocation exist = hostingLocationMongoRepository.findByName(hostingLocation);
        if (Optional.ofNullable(exist).isPresent()) {
            throw new DuplicateDataException("data already exist for  " + hostingLocation);
        } else {
            HostingLocation newHostingLocation = new HostingLocation();
            newHostingLocation.setName(hostingLocation);
            return save(newHostingLocation);
        }
    }


    public List<HostingLocation> getAllHostingLocation() {
        List<HostingLocation> result = hostingLocationMongoRepository.findAllHostingLocations();
        if (result.size()!=0) {
            return result;

        } else
            throw new DataNotExists("HostingLocationController not exist please create purpose ");
    }



    public HostingLocation getHostingLocationById(BigInteger id) {

        HostingLocation exist = hostingLocationMongoRepository.findByIdAndNonDeleted(id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id "+id);
        } else {
            return exist;

        }
    }



    public Boolean deleteHostingLocationById(BigInteger id) {

        HostingLocation exist = hostingLocationMongoRepository.findByIdAndNonDeleted(id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id "+id);
        } else {
            exist.setDeleted(true);
            save(exist);
            return true;

        }
    }


    public HostingLocation updateHostingLocation(BigInteger id,String hostingLocation) {
        if (StringUtils.isEmpty(hostingLocation))
        {
            throw new InvalidRequestException("requested hostingLocation is null");

        }
        HostingLocation exist = hostingLocationMongoRepository.findByIdAndNonDeleted(id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id "+id);
        } else {
            exist.setName(hostingLocation);
            return save(exist);

        }
    }






}

package com.kairos.service.master_data;


import com.kairos.custome_exception.DataNotExists;
import com.kairos.custome_exception.DataNotFoundByIdException;
import com.kairos.custome_exception.DuplicateDataException;
import com.kairos.custome_exception.RequestDataNull;
import com.kairos.persistance.model.master_data.HostingLocation;
import com.kairos.persistance.repository.master_data.HostingLocationMongoRepository;
import com.kairos.service.MongoBaseService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Service
public class HostingLocationService  extends MongoBaseService {


@Inject
    private HostingLocationMongoRepository hostingLocationMongoRepository;



    public HostingLocation createHostingLocation(String hostingLocation) {
        if (StringUtils.isEmpty(hostingLocation))
        {
            throw new RequestDataNull("requested hostingLocation is null");

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
        List<HostingLocation> result = hostingLocationMongoRepository.findAll();
        if (result.size()!=0) {
            return result;

        } else
            throw new DataNotExists("HostingLocationController not exist please create purpose ");
    }



    public HostingLocation getHostingLocationById(BigInteger id) {

        HostingLocation exist = hostingLocationMongoRepository.findByid(id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            return exist;

        }
    }



    public Boolean deleteHostingLocationById(BigInteger id) {

        HostingLocation exist = hostingLocationMongoRepository.findByid(id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            hostingLocationMongoRepository.delete(exist);
            return true;

        }
    }


    public HostingLocation updateHostingLocation(BigInteger id,String hostingLocation) {
        if (StringUtils.isEmpty(hostingLocation))
        {
            throw new RequestDataNull("requested hostingLocation is null");

        }
        HostingLocation exist = hostingLocationMongoRepository.findByid(id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            exist.setName(hostingLocation);
            return save(exist);

        }
    }






}

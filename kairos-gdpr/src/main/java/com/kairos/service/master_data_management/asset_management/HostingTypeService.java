package com.kairos.service.master_data_management.asset_management;


import com.kairos.custome_exception.DataNotExists;
import com.kairos.custome_exception.DataNotFoundByIdException;
import com.kairos.custome_exception.InvalidRequestException;
import com.kairos.persistance.model.master_data_management.asset_management.HostingType;
import com.kairos.persistance.repository.master_data_management.asset_management.HostingTypeMongoRepository;
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
public class HostingTypeService extends MongoBaseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(HostingTypeService.class);

    @Inject
    private HostingTypeMongoRepository hostingTypeMongoRepository;


    public Map<String, List<HostingType>> createHostingType(Long countryId,List<HostingType> hostingTypes) {
        Map<String, List<HostingType>> result = new HashMap<>();
        List<HostingType> existing = new ArrayList<>();
        Set<String> names=new HashSet<>();
        List<HostingType> newHostingTypes = new ArrayList<>();
        if (hostingTypes.size() != 0) {
            for (HostingType hostingType : hostingTypes) {
                if (!StringUtils.isBlank(hostingType.getName())) {
                    names.add(hostingType.getName());
                } else
                    throw new InvalidRequestException("name could not be empty or null");
            }
            existing = hostingTypeMongoRepository.findByCountryAndNameList(countryId, names);
            existing.forEach(item -> names.remove(item.getName()));

            if (names.size()!=0) {
                for (String name : names) {

                    HostingType newHostingType = new HostingType();
                    newHostingType.setName(name);
                    newHostingType.setCountryId(countryId);
                    newHostingTypes.add(newHostingType);

                }


                newHostingTypes = save(newHostingTypes);
            }
            result.put("existing", existing);
            result.put("new", newHostingTypes);
            return result;
        } else
            throw new InvalidRequestException("list cannot be empty");


    }

    public List<HostingType> getAllHostingType() {
       return hostingTypeMongoRepository.findAllHostingTypes(UserContext.getCountryId());
          }


    public HostingType getHostingType(Long countryId,BigInteger id) {

        HostingType exist = hostingTypeMongoRepository.findByIdAndNonDeleted(countryId,id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            return exist;

        }
    }


    public Boolean deleteHostingType(BigInteger id) {

        HostingType exist = hostingTypeMongoRepository.findByid(id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            exist.setDeleted(true);
            save(exist);
            return true;

        }
    }


    public HostingType updateHostingType(BigInteger id, HostingType hostingType) {


        HostingType exist = hostingTypeMongoRepository.findByName(UserContext.getCountryId(),hostingType.getName());
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            exist=hostingTypeMongoRepository.findByid(id);
            exist.setName(hostingType.getName());
            return save(exist);

        }
    }

    public HostingType getHostingTypeByName(Long countryId,String name) {


        if (!StringUtils.isBlank(name)) {
            HostingType exist = hostingTypeMongoRepository.findByName(countryId,name);
            if (!Optional.ofNullable(exist).isPresent()) {
                throw new DataNotExists("data not exist for name " + name);
            }
            return exist;
        } else
            throw new InvalidRequestException("request param cannot be empty  or null");

    }


}

    
    
    


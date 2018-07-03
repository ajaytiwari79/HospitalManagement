package com.kairos.service.master_data_management.asset_management;


import com.kairos.custom_exception.DataNotExists;
import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.custom_exception.InvalidRequestException;
import com.kairos.persistance.model.master_data_management.asset_management.HostingType;
import com.kairos.persistance.repository.master_data_management.asset_management.HostingTypeMongoRepository;
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
public class HostingTypeService extends MongoBaseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(HostingTypeService.class);

    @Inject
    private HostingTypeMongoRepository hostingTypeMongoRepository;

    @Inject
    private ComparisonUtils comparisonUtils;

    public Map<String, List<HostingType>> createHostingType(Long countryId,Long organizationId,List<HostingType> hostingTypes) {

        Map<String, List<HostingType>> result = new HashMap<>();
        Set<String> hostingTypeNames=new HashSet<>();
        if (hostingTypes.size() != 0) {
            for (HostingType hostingType : hostingTypes) {
                if (!StringUtils.isBlank(hostingType.getName())) {
                    hostingTypeNames.add(hostingType.getName());
                } else
                    throw new InvalidRequestException("name could not be empty or null");
            }
            List<HostingType> existing =  findByNamesList(countryId,organizationId,hostingTypeNames,HostingType.class);
            hostingTypeNames = comparisonUtils.getNameListForMetadata(existing, hostingTypeNames);
            List<HostingType> newHostingTypes = new ArrayList<>();
            if (hostingTypeNames.size()!=0) {
                for (String name : hostingTypeNames) {

                    HostingType newHostingType = new HostingType();
                    newHostingType.setName(name);
                    newHostingType.setCountryId(countryId);
                    newHostingType.setOrganizationId(organizationId);
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

    public List<HostingType> getAllHostingType(Long countryId,Long organizationId) {
       return hostingTypeMongoRepository.findAllHostingTypes(countryId,organizationId);
          }


    public HostingType getHostingType(Long countryId,Long organizationId,BigInteger id) {

        HostingType exist = hostingTypeMongoRepository.findByIdAndNonDeleted(countryId,organizationId,id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            return exist;

        }
    }


    public Boolean deleteHostingType(Long countryId,Long organizationId,BigInteger id) {

        HostingType exist = hostingTypeMongoRepository.findByIdAndNonDeleted(countryId,organizationId,id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            exist.setDeleted(true);
            save(exist);
            return true;

        }
    }


    public HostingType updateHostingType(Long countryId,Long organizationId,BigInteger id, HostingType hostingType) {


        HostingType exist = hostingTypeMongoRepository.findByName(countryId,organizationId,hostingType.getName());
        if (Optional.ofNullable(exist).isPresent() ) {
            if (id.equals(exist.getId())) {
                return exist;
            }
            throw new DuplicateDataException("data  exist for  "+hostingType.getName());
        } else {
            exist=hostingTypeMongoRepository.findByid(id);
            exist.setName(hostingType.getName());
            return save(exist);

        }
    }

    public HostingType getHostingTypeByName(Long countryId,Long organizationId,String name) {


        if (!StringUtils.isBlank(name)) {
            HostingType exist = hostingTypeMongoRepository.findByName(countryId,organizationId,name);
            if (!Optional.ofNullable(exist).isPresent()) {
                throw new DataNotExists("data not exist for name " + name);
            }
            return exist;
        } else
            throw new InvalidRequestException("request param cannot be empty  or null");

    }


}

    
    
    


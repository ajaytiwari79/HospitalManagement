package com.kairos.service.master_data_management.processing_activity_masterdata;

import com.kairos.custome_exception.DataNotExists;
import com.kairos.custome_exception.DataNotFoundByIdException;
import com.kairos.custome_exception.InvalidRequestException;
import com.kairos.persistance.model.master_data_management.processing_activity_masterdata.AccessorParty;
import com.kairos.persistance.repository.master_data_management.processing_activity_masterdata.AccessorPartyMongoRepository;
import com.kairos.service.MongoBaseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;

@Service
public class AccessorPartyService extends MongoBaseService {


    @Inject
    private AccessorPartyMongoRepository accessorPartyMongoRepository;


    public Map<String, List<AccessorParty>> createAccessorParty(List<AccessorParty> accessorPartys) {
        Map<String, List<AccessorParty>> result = new HashMap<>();
        List<AccessorParty> existing = new ArrayList<>();
        List<AccessorParty> newAccessorPartys = new ArrayList<>();
        if (accessorPartys.size() != 0) {
            for (AccessorParty accessorParty : accessorPartys) {
                if (!StringUtils.isBlank(accessorParty.getName())) {
                    AccessorParty exist = accessorPartyMongoRepository.findByName(accessorParty.getName());
                    if (Optional.ofNullable(exist).isPresent()) {
                        existing.add(exist);

                    } else {
                        AccessorParty newAccessorParty = new AccessorParty();
                        newAccessorParty.setName(accessorParty.getName());
                        newAccessorPartys.add(save(newAccessorParty));
                    }
                } else
                    throw new InvalidRequestException("name could not be empty or null");
            }

            result.put("existing", existing);
            result.put("new", newAccessorPartys);
            return result;
        } else
            throw new InvalidRequestException("list cannot be empty");


    }

    public List<AccessorParty> getAllAccessorParty() {
     return accessorPartyMongoRepository.findAllAccessorParties();

    }


    public AccessorParty getAccessorParty(BigInteger id) {

        AccessorParty exist = accessorPartyMongoRepository.findByIdAndNonDeleted(id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            return exist;

        }
    }


    public Boolean deleteAccessorParty(BigInteger id) {

        AccessorParty exist = accessorPartyMongoRepository.findByIdAndNonDeleted(id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            exist.setDeleted(true);
            save(exist);
            return true;

        }
    }


    public AccessorParty updateAccessorParty(BigInteger id, AccessorParty accessorParty) {


        AccessorParty exist = accessorPartyMongoRepository.findByIdAndNonDeleted(id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            exist.setName(accessorParty.getName());

            return save(exist);

        }
    }


    public AccessorParty getAccessorPartyByName(String name) {


        if (!StringUtils.isBlank(name)) {
            AccessorParty exist = accessorPartyMongoRepository.findByName(name);
            if (!Optional.ofNullable(exist).isPresent()) {
                throw new DataNotExists("data not exist for name " + name);
            }
            return exist;
        } else
            throw new InvalidRequestException("request param cannot be empty  or null");

    }


}






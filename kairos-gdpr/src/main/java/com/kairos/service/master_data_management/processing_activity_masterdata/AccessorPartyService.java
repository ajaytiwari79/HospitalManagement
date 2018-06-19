package com.kairos.service.master_data_management.processing_activity_masterdata;

import com.kairos.custome_exception.DataNotExists;
import com.kairos.custome_exception.DataNotFoundByIdException;
import com.kairos.custome_exception.DuplicateDataException;
import com.kairos.custome_exception.InvalidRequestException;
import com.kairos.persistance.model.master_data_management.processing_activity_masterdata.AccessorParty;
import com.kairos.persistance.repository.master_data_management.processing_activity_masterdata.AccessorPartyMongoRepository;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.utils.userContext.UserContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;

@Service
public class AccessorPartyService extends MongoBaseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccessorPartyService.class);

    @Inject
    private AccessorPartyMongoRepository accessorPartyMongoRepository;

    @Inject
    private ExceptionService exceptionService;


    public Map<String, List<AccessorParty>> createAccessorParty(Long countryId, List<AccessorParty> accessorPartys) {
        Map<String, List<AccessorParty>> result = new HashMap<>();
        List<AccessorParty> existing = new ArrayList<>();
        List<AccessorParty> newAccessorPartys = new ArrayList<>();
        Set<String> names = new HashSet<>();
        if (accessorPartys.size() != 0) {
            for (AccessorParty accessorParty : accessorPartys) {
                if (!StringUtils.isBlank(accessorParty.getName())) {
                    names.add(accessorParty.getName());
                } else
                    throw new InvalidRequestException("name could not be empty or null");
            }
            existing = accessorPartyMongoRepository.findByCountryAndNameList(countryId, names);
            existing.forEach(item -> names.remove(item.getName()));

            if (names.size() != 0) {
                for (String name : names) {
                    AccessorParty newAccessorParty = new AccessorParty();
                    newAccessorParty.setName(name);
                    newAccessorParty.setCountryId(countryId);
                    newAccessorPartys.add(newAccessorParty);
                }
                newAccessorPartys = save(newAccessorPartys);
            }
            result.put("existing", existing);
            result.put("new", newAccessorPartys);
            return result;
        } else
            throw new InvalidRequestException("list cannot be empty");


    }

    public List<AccessorParty> getAllAccessorParty() {
        return accessorPartyMongoRepository.findAllAccessorPartys(UserContext.getCountryId());

    }


    public AccessorParty getAccessorParty(Long countryId, BigInteger id) {

        AccessorParty exist = accessorPartyMongoRepository.findByIdAndNonDeleted(countryId, id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            return exist;

        }
    }


    public Boolean deleteAccessorParty(BigInteger id) {

        AccessorParty exist = accessorPartyMongoRepository.findByid(id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            exist.setDeleted(true);
            save(exist);
            return true;

        }
    }


    public AccessorParty updateAccessorParty(BigInteger id, AccessorParty accessorParty) {


        AccessorParty exist = accessorPartyMongoRepository.findByName(UserContext.getCountryId(), accessorParty.getName());
        if (Optional.ofNullable(exist).isPresent() ) {
            if (id.equals(exist.getId())) {
                return exist;
            }
            throw new DuplicateDataException("Name Already Exist");
        } else {
            exist=accessorPartyMongoRepository.findByid(id);
            exist.setName(accessorParty.getName());
            return save(exist);

        }
    }


    public AccessorParty getAccessorPartyByName(Long countryId, String name) {


        if (!StringUtils.isBlank(name)) {
            AccessorParty exist = accessorPartyMongoRepository.findByName(countryId, name);
            if (!Optional.ofNullable(exist).isPresent()) {
                throw new DataNotExists("data not exist for name " + name);
            }
            return exist;
        } else
            throw new InvalidRequestException("request param cannot be empty  or null");

    }


}






package com.kairos.service.master_data.processing_activity_masterdata;

import com.kairos.custom_exception.DataNotExists;
import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.custom_exception.InvalidRequestException;
import com.kairos.persistance.model.master_data.processing_activity_masterdata.AccessorParty;
import com.kairos.persistance.repository.master_data.processing_activity_masterdata.AccessorPartyMongoRepository;
import com.kairos.service.common.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.utils.ComparisonUtils;
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

    @Inject
    private ComparisonUtils comparisonUtils;


    public Map<String, List<AccessorParty>> createAccessorParty(Long countryId, Long organizationId, List<AccessorParty> accessorPartys) {

        Map<String, List<AccessorParty>> result = new HashMap<>();
        Set<String> accessorPartyNames = new HashSet<>();
        if (accessorPartys.size() != 0) {
            for (AccessorParty accessorParty : accessorPartys) {
                if (!StringUtils.isBlank(accessorParty.getName())) {
                    accessorPartyNames.add(accessorParty.getName());
                } else
                    throw new InvalidRequestException("name could not be empty or null");
            }
            List<AccessorParty> existing = findByNamesList(countryId, organizationId, accessorPartyNames, AccessorParty.class);
            accessorPartyNames = comparisonUtils.getNameListForMetadata(existing, accessorPartyNames);

            List<AccessorParty> newAccessorPartys = new ArrayList<>();
            if (accessorPartyNames.size() != 0) {
                for (String name : accessorPartyNames) {
                    AccessorParty newAccessorParty = new AccessorParty();
                    newAccessorParty.setName(name);
                    newAccessorParty.setCountryId(countryId);
                    newAccessorParty.setOrganizationId(organizationId);
                    newAccessorPartys.add(newAccessorParty);
                }
                newAccessorPartys = accessorPartyMongoRepository.saveAll(sequenceGenerator(newAccessorPartys));
            }
            result.put("existing", existing);
            result.put("new", newAccessorPartys);
            return result;
        } else
            throw new InvalidRequestException("list cannot be empty");


    }

    public List<AccessorParty> getAllAccessorParty(Long countryId, Long organizationId) {
        return accessorPartyMongoRepository.findAllAccessorPartys(countryId, organizationId);
/*
         Set<String> strings=new HashSet<>();
         strings.add("qwert");
         strings.add("qwertyu");
         strings.add("qazxsw");
         return findByNamesList(countryId,organizationId,strings,AccessorParty.class);*/
    }


    public AccessorParty getAccessorParty(Long countryId, Long organizationId, BigInteger id) {

        AccessorParty exist = accessorPartyMongoRepository.findByIdAndNonDeleted(countryId, organizationId, id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            return exist;

        }
    }


    public Boolean deleteAccessorParty(Long countryId, Long organizationId, BigInteger id) {

        AccessorParty exist = accessorPartyMongoRepository.findByIdAndNonDeleted(countryId, organizationId, id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            delete(exist);
            return true;

        }
    }


    public AccessorParty updateAccessorParty(Long countryId, Long organizationId, BigInteger id, AccessorParty accessorParty) {

        AccessorParty exist = accessorPartyMongoRepository.findByName(countryId, organizationId, accessorParty.getName());
        if (Optional.ofNullable(exist).isPresent()) {
            if (id.equals(exist.getId())) {
                return exist;
            }
            throw new DuplicateDataException("Name Already Exist");
        } else {
            exist = accessorPartyMongoRepository.findByid(id);
            exist.setName(accessorParty.getName());
            return accessorPartyMongoRepository.save(sequenceGenerator(exist));

        }
    }


    public AccessorParty getAccessorPartyByName(Long countryId, Long organizationId, String name) {


        if (!StringUtils.isBlank(name)) {
            AccessorParty exist = accessorPartyMongoRepository.findByName(countryId, organizationId, name);
            if (!Optional.ofNullable(exist).isPresent()) {
                throw new DataNotExists("data not exist for name " + name);
            }
            return exist;
        } else
            throw new InvalidRequestException("request param cannot be empty  or null");

    }


}






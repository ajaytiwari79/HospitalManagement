package com.kairos.service.master_data.processing_activity_masterdata;

import com.kairos.custom_exception.DataNotExists;
import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.custom_exception.InvalidRequestException;
import com.kairos.dto.data_inventory.ProcessingActivityDTO;
import com.kairos.dto.metadata.AccessorPartyDTO;
import com.kairos.persistance.model.master_data.default_proc_activity_setting.AccessorParty;
import com.kairos.persistance.repository.master_data.processing_activity_masterdata.accessor_party.AccessorPartyMongoRepository;
import com.kairos.response.dto.common.AccessorPartyReponseDTO;
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

import static com.kairos.constants.AppConstant.EXISTING_DATA_LIST;
import static com.kairos.constants.AppConstant.NEW_DATA_LIST;


@Service
public class AccessorPartyService extends MongoBaseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccessorPartyService.class);

    @Inject
    private AccessorPartyMongoRepository accessorPartyMongoRepository;

    @Inject
    private ExceptionService exceptionService;

    @Inject
    private ComparisonUtils comparisonUtils;


    /**
     * @param countryId
     * @param organizationId
     * @param accessorParties
     * @return return map which contain list of new AccessorParty and list of existing AccessorParty if AccessorParty already exist
     * @description this method create new AccessorParty if AccessorParty not exist with same name ,
     * and if exist then simply add  AccessorParty to existing list and return list ;
     * findByNamesList()  return list of existing AccessorParty using collation ,used for case insensitive result
     */
    public Map<String, List<AccessorParty>> createAccessorParty(Long countryId, Long organizationId, List<AccessorParty> accessorParties) {

        Map<String, List<AccessorParty>> result = new HashMap<>();
        Set<String> accessorPartyNames = new HashSet<>();
        if (!accessorParties.isEmpty()) {
            for (AccessorParty accessorParty : accessorParties) {
                if (!StringUtils.isBlank(accessorParty.getName())) {
                    accessorPartyNames.add(accessorParty.getName());
                } else
                    throw new InvalidRequestException("name could not be empty or null");
            }
            List<AccessorParty> existing = findByNamesList(countryId, organizationId, accessorPartyNames, AccessorParty.class);
            accessorPartyNames = comparisonUtils.getNameListForMetadata(existing, accessorPartyNames);

            List<AccessorParty> newAccessorPartyList = new ArrayList<>();
            if (!accessorPartyNames.isEmpty()) {
                for (String name : accessorPartyNames) {
                    AccessorParty newAccessorParty = new AccessorParty();
                    newAccessorParty.setName(name);
                    newAccessorParty.setCountryId(countryId);
                    newAccessorParty.setOrganizationId(organizationId);
                    newAccessorPartyList.add(newAccessorParty);
                }
                newAccessorPartyList = accessorPartyMongoRepository.saveAll(getNextSequence(newAccessorPartyList));
            }
            result.put(EXISTING_DATA_LIST, existing);
            result.put(NEW_DATA_LIST, newAccessorPartyList);
            return result;
        } else
            throw new InvalidRequestException("list cannot be empty");


    }

    public List<AccessorPartyReponseDTO> getAllAccessorParty(Long countryId, Long organizationId) {
        return accessorPartyMongoRepository.findAllAccessorParty(countryId, organizationId);
    }

    /**
     * @param countryId
     * @param organizationId
     * @param id             id of AccessorParty
     * @return AccessorParty object fetch by given id
     * @throws DataNotFoundByIdException throw exception if AccessorParty not found for given id
     */
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

    /**
     * @param countryId
     * @param organizationId
     * @param id             id of AccessorParty
     * @param accessorParty
     * @return AccessorParty updated object
     * @throws DuplicateDataException throw exception if AccessorParty data not exist for given id
     */
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
            return accessorPartyMongoRepository.save(getNextSequence(exist));

        }
    }

    /**
     * @param countryId
     * @param organizationId
     * @param name           name of AccessorParty
     * @return AccessorParty object fetch on basis of  name
     * @throws DataNotExists throw exception if AccessorParty exist for given name
     */
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


    public List<BigInteger> createAccessorPartyForOrganizationOnInheritingFromParentOrganization(Long countryId, Long organizationId, ProcessingActivityDTO processingActivityDTO) {


        List<AccessorPartyDTO> accessorParties = processingActivityDTO.getAccessorParties();
        List<AccessorParty> newInheritAccessorPartiesFromCountry = new ArrayList<>();
        List<BigInteger> accessorPartyIds = new ArrayList<>();
        for (AccessorPartyDTO accessorPartyDTO : accessorParties) {
            if (!accessorPartyDTO.getOrganizationId().equals(organizationId)) {
                AccessorParty accessorParty = new AccessorParty(accessorPartyDTO.getName(), countryId);
                accessorParty.setOrganizationId(organizationId);
                newInheritAccessorPartiesFromCountry.add(accessorParty);
            } else {
                accessorPartyIds.add(accessorPartyDTO.getId());
            }
        }
        newInheritAccessorPartiesFromCountry = accessorPartyMongoRepository.saveAll(getNextSequence(newInheritAccessorPartiesFromCountry));
        newInheritAccessorPartiesFromCountry.forEach(accessorParty -> {
            accessorPartyIds.add(accessorParty.getId());
        });

        return accessorPartyIds;
    }

    /**
     *
     * @param countryId
     * @param organizationId - id of current organization
     * @return     method return list of Organization Accessor party with non Inherited Accessor party from  parent
     */
    public List<AccessorPartyReponseDTO> getAllNotInheritedAccesorPartyFromParentOrgAndUnitAccesorParty(Long countryId, Long parentOrgId, Long organizationId) {

      return accessorPartyMongoRepository.getAllNotInheritedAccesorPartyFromParentOrgAndUnitAccesorParty(countryId,parentOrgId,organizationId);
    }


}






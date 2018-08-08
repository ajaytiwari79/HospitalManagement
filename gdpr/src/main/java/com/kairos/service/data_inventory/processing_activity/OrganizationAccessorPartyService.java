package com.kairos.service.data_inventory.processing_activity;

import com.kairos.custom_exception.DataNotExists;
import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.custom_exception.InvalidRequestException;
import com.kairos.persistance.model.master_data.default_proc_activity_setting.AccessorParty;
import com.kairos.persistance.repository.master_data.processing_activity_masterdata.accessor_party.AccessorPartyMongoRepository;
import com.kairos.response.dto.common.AccessorPartyResponseDTO;
import com.kairos.service.common.MongoBaseService;
import com.kairos.utils.ComparisonUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;

import static com.kairos.constants.AppConstant.EXISTING_DATA_LIST;
import static com.kairos.constants.AppConstant.NEW_DATA_LIST;

@Service
public class OrganizationAccessorPartyService extends MongoBaseService {




    @Inject
    private AccessorPartyMongoRepository accessorPartyMongoRepository;

    /**
     * @description this method create new AccessorParty if AccessorParty not exist with same name ,
     * and if exist then simply add  AccessorParty to existing list and return list ;
     * findByNamesAndCountryId()  return list of existing AccessorParty using collation ,used for case insensitive result
     * @param organizationId
     * @param accessorParties
     * @return return map which contain list of new AccessorParty and list of existing AccessorParty if AccessorParty already exist
     *
     */
    public Map<String, List<AccessorParty>> createAccessorParty( Long organizationId, List<AccessorParty> accessorParties) {

        Map<String, List<AccessorParty>> result = new HashMap<>();
        Set<String> accessorPartyNames = new HashSet<>();
        if (!accessorParties.isEmpty()) {
            for (AccessorParty accessorParty : accessorParties) {
                if (!StringUtils.isBlank(accessorParty.getName())) {
                    accessorPartyNames.add(accessorParty.getName());
                } else
                    throw new InvalidRequestException("name could not be empty or null");
            }
            List<AccessorParty> existing = findAllByNameAndOrganizationId(organizationId, accessorPartyNames, AccessorParty.class);
            accessorPartyNames = ComparisonUtils.getNameListForMetadata(existing, accessorPartyNames);

            List<AccessorParty> newAccessorPartyList = new ArrayList<>();
            if (!accessorPartyNames.isEmpty()) {
                for (String name : accessorPartyNames) {
                    AccessorParty newAccessorParty = new AccessorParty(name);
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

    public List<AccessorPartyResponseDTO> getAllAccessorParty(Long organizationId) {
        return accessorPartyMongoRepository.findAllOrganizationAccessorParty( organizationId);
    }

    /**
     * @throws DataNotFoundByIdException throw exception if AccessorParty not found for given id
     * @param organizationId
     * @param id id of AccessorParty
     * @return AccessorParty object fetch by given id
     */
    public AccessorParty getAccessorPartyById( Long organizationId, BigInteger id) {

        AccessorParty exist = accessorPartyMongoRepository.findOrganizationIdAndIdAndNonDeleted(organizationId, id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            return exist;

        }
    }


    public Boolean deleteAccessorParty( Long organizationId, BigInteger id) {

        AccessorParty exist = accessorPartyMongoRepository.findOrganizationIdAndIdAndNonDeleted( organizationId, id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            delete(exist);
            return true;

        }
    }

    /**
     * @throws DuplicateDataException throw exception if AccessorParty data not exist for given id
     * @param organizationId
     * @param id id of AccessorParty
     * @param accessorParty
     * @return AccessorParty updated object
     */
    public AccessorParty updateAccessorParty( Long organizationId, BigInteger id, AccessorParty accessorParty) {


        AccessorParty exist = accessorPartyMongoRepository.findByNameAndOrganizationId(organizationId, accessorParty.getName());
        if (Optional.ofNullable(exist).isPresent()) {
            if (id.equals(exist.getId())) {
                return exist;
            }
            throw new DuplicateDataException("Name Already Exist");
        } else {
            exist = accessorPartyMongoRepository.findByid(id);
            exist.setName(accessorParty.getName());
            return accessorPartyMongoRepository.save(exist);

        }
    }

    /** @throws DataNotExists throw exception if AccessorParty exist for given name
     * @param organizationId
     * @param name name of AccessorParty
     * @return AccessorParty object fetch on basis of  name
     */
    public AccessorParty getAccessorPartyByName( Long organizationId, String name) {


        if (!StringUtils.isBlank(name)) {
            AccessorParty exist = accessorPartyMongoRepository.findByNameAndOrganizationId( organizationId, name);
            if (!Optional.ofNullable(exist).isPresent()) {
                throw new DataNotExists("data not exist for name " + name);
            }
            return exist;
        } else
            throw new InvalidRequestException("request param cannot be empty  or null");

    }


}

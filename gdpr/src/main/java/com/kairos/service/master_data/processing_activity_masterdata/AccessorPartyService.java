package com.kairos.service.master_data.processing_activity_masterdata;

import com.kairos.custom_exception.DataNotExists;
import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.custom_exception.InvalidRequestException;
import com.kairos.enums.SuggestedDataStatus;
import com.kairos.gdpr.metadata.AccessorPartyDTO;
import com.kairos.persistance.model.master_data.default_proc_activity_setting.AccessorParty;
import com.kairos.persistance.repository.master_data.processing_activity_masterdata.accessor_party.AccessorPartyMongoRepository;
import com.kairos.response.dto.common.AccessorPartyResponseDTO;
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


    /**
     * @param countryId
     * @param accessorParties
     * @return return map which contain list of new AccessorParty and list of existing AccessorParty if AccessorParty already exist
     * @description this method create new AccessorParty if AccessorParty not exist with same name ,
     * and if exist then simply add  AccessorParty to existing list and return list ;
     * findByNamesList()  return list of existing AccessorParty using collation ,used for case insensitive result
     */
    public Map<String, List<AccessorParty>> createAccessorParty(Long countryId, List<AccessorPartyDTO> accessorParties) {

        Map<String, List<AccessorParty>> result = new HashMap<>();
        Set<String> accessorPartyNames = new HashSet<>();
        if (!accessorParties.isEmpty()) {
            for (AccessorPartyDTO accessorParty : accessorParties) {
                accessorPartyNames.add(accessorParty.getName());
            }
            List<AccessorParty> existing = findMetaDataByNamesAndCountryId(countryId, accessorPartyNames, AccessorParty.class);
            accessorPartyNames = ComparisonUtils.getNameListForMetadata(existing, accessorPartyNames);

            List<AccessorParty> newAccessorPartyList = new ArrayList<>();
            if (!accessorPartyNames.isEmpty()) {
                for (String name : accessorPartyNames) {
                    AccessorParty newAccessorParty = new AccessorParty(name);
                    newAccessorParty.setCountryId(countryId);
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


    public List<AccessorPartyResponseDTO> getAllAccessorParty(Long countryId) {
        return accessorPartyMongoRepository.findAllAccessorParty(countryId);
    }

    /**
     * @param id id of AccessorParty
     * @return AccessorParty object fetch by given id
     * @throws DataNotFoundByIdException throw exception if AccessorParty not found for given id
     */
    public AccessorParty getAccessorParty(Long countryId, BigInteger id) {

        AccessorParty exist = accessorPartyMongoRepository.findByIdAndNonDeleted(countryId, id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            return exist;

        }
    }


    public Boolean deleteAccessorParty(Long countryId, BigInteger id) {

        AccessorParty exist = accessorPartyMongoRepository.findByIdAndNonDeleted(countryId, id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            delete(exist);
            return true;

        }
    }

    /**
     * @param countryId
     * @param id               id of AccessorParty
     * @param accessorPartyDTO
     * @return AccessorParty updated object
     * @throws DuplicateDataException throw exception if AccessorParty data not exist for given id
     */
    public AccessorPartyDTO updateAccessorParty(Long countryId, BigInteger id, AccessorPartyDTO accessorPartyDTO) {

        AccessorParty accessorParty = accessorPartyMongoRepository.findByName(countryId, accessorPartyDTO.getName());
        if (Optional.ofNullable(accessorParty).isPresent()) {
            if (id.equals(accessorParty.getId())) {
                return accessorPartyDTO;
            }
            throw new DuplicateDataException("Name Already Exist");
        }
        accessorParty = accessorPartyMongoRepository.findByid(id);
        if (!Optional.ofNullable(accessorParty).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Accessor party", id);
        }
        accessorParty.setName(accessorPartyDTO.getName());
        accessorPartyMongoRepository.save(accessorParty);
        return accessorPartyDTO;


    }

    /**
     * @param countryId
     * @param name      name of AccessorParty
     * @return AccessorParty object fetch on basis of  name
     * @throws DataNotExists throw exception if AccessorParty exist for given name
     */
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


    /**
     * @description method save Accessor Party suggested by unit
     * @param countryId
     * @param accessorPartyDTOS
     * @return
     */
    public List<AccessorParty> saveSuggestedAccessorPartiesFromUnit(Long countryId, List<AccessorPartyDTO> accessorPartyDTOS) {

        Set<String> accessorPartyNameList = new HashSet<>();
        for (AccessorPartyDTO AccessorParty : accessorPartyDTOS) {
            accessorPartyNameList.add(AccessorParty.getName());
        }
        List<AccessorParty> existingAccessorParties = findMetaDataByNamesAndCountryId(countryId, accessorPartyNameList, AccessorParty.class);
        accessorPartyNameList = ComparisonUtils.getNameListForMetadata(existingAccessorParties, accessorPartyNameList);
        List<AccessorParty> accessorPartyList = new ArrayList<>();
        if (!accessorPartyNameList.isEmpty()) {
            for (String name : accessorPartyNameList) {

                AccessorParty accessorParty = new AccessorParty(name);
                accessorParty.setCountryId(countryId);
                accessorParty.setSuggestedDataStatus(SuggestedDataStatus.APPROVAL_PENDING);
                accessorPartyList.add(accessorParty);
            }

           accessorPartyMongoRepository.saveAll(getNextSequence(accessorPartyList));
        }
        return accessorPartyList;
    }


}






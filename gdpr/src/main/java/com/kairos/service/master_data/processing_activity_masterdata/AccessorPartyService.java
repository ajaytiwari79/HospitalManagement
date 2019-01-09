package com.kairos.service.master_data.processing_activity_masterdata;


import com.kairos.commons.custom_exception.DataNotFoundByIdException;
import com.kairos.commons.custom_exception.DuplicateDataException;
import com.kairos.commons.custom_exception.InvalidRequestException;
import com.kairos.enums.gdpr.SuggestedDataStatus;
import com.kairos.dto.gdpr.metadata.AccessorPartyDTO;
import com.kairos.persistence.model.master_data.default_proc_activity_setting.AccessorParty;
import com.kairos.persistence.model.master_data.default_proc_activity_setting.AccessorPartyMD;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.accessor_party.AccessorPartyMDRepository;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.accessor_party.AccessorPartyMongoRepository;
import com.kairos.response.dto.common.AccessorPartyResponseDTO;
import com.kairos.service.common.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.utils.ComparisonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

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
    private AccessorPartyMDRepository accessorPartyMDRepository;


    /**
     * @param countryId
     * @param accessorParties
     * @return return map which contain list of new AccessorParty and list of existing AccessorParty if AccessorParty already exist
     * @description this method create new AccessorParty if AccessorParty not exist with same name ,
     * and if exist then simply add  AccessorParty to existing list and return list ;
     * findByNamesList()  return list of existing AccessorParty using collation ,used for case insensitive result
     */
    public Map<String, List<AccessorPartyMD>> createAccessorParty(Long countryId, List<AccessorPartyDTO> accessorParties) {
        //TODO still need to optimize we can get name of list in string from here
        Map<String, List<AccessorPartyMD>> result = new HashMap<>();
        Set<String> accessorPartyNames = new HashSet<>();
        if (!accessorParties.isEmpty()) {
            for (AccessorPartyDTO accessorParty : accessorParties) {
                accessorPartyNames.add(accessorParty.getName());
            }
            List<String> nameInLowerCase = accessorPartyNames.stream().map(String::toLowerCase)
                    .collect(Collectors.toList());
            //TODO still need to update we can return name of list from here and can apply removeAll on list
            List<AccessorPartyMD> existing = accessorPartyMDRepository.findByCountryIdAndDeletedAndNameIn(countryId, false, nameInLowerCase);
            accessorPartyNames = ComparisonUtils.getNameListForMetadata(existing, accessorPartyNames);

            List<AccessorPartyMD> newAccessorPartyList = new ArrayList<>();
            if (!accessorPartyNames.isEmpty()) {
                for (String name : accessorPartyNames) {
                    AccessorPartyMD newAccessorParty = new AccessorPartyMD(name,countryId,SuggestedDataStatus.APPROVED);
                    newAccessorPartyList.add(newAccessorParty);
                }
                newAccessorPartyList = accessorPartyMDRepository.saveAll(newAccessorPartyList);
            }
            result.put(EXISTING_DATA_LIST, existing);
            result.put(NEW_DATA_LIST, newAccessorPartyList);
            return result;
        } else
            throw new InvalidRequestException("list cannot be empty");


    }


    public List<AccessorPartyResponseDTO> getAllAccessorParty(Long countryId) {
        return accessorPartyMDRepository.findAllByCountryIdAndSortByCreatedDate(countryId);
    }

    /**
     * @param id id of AccessorParty
     * @return AccessorParty object fetch by given id
     * @throws DataNotFoundByIdException throw exception if AccessorParty not found for given id
     */
    public AccessorPartyMD getAccessorParty(Long countryId, Long id) {
        AccessorPartyMD exist = accessorPartyMDRepository.findByIdAndCountryIdAndDeleted(id, countryId, false);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("No data found");
        } else {
            return exist;

        }
    }


    public Boolean deleteAccessorParty(Long countryId, Long id) {

        Integer resultCount = accessorPartyMDRepository.deleteByIdAndCountryId(id, countryId);
        if (resultCount > 0) {
            LOGGER.info("Accessor Party deleted successfully for id :: {}", id);
        }else{
            throw new DataNotFoundByIdException("No data found");
        }
        return true;
    }

    /**
     * @param countryId
     * @param id               id of AccessorParty
     * @param accessorPartyDTO
     * @return AccessorParty updated object
     * @throws DuplicateDataException throw exception if AccessorParty data not exist for given id
     */
    public AccessorPartyDTO updateAccessorParty(Long countryId, Long id, AccessorPartyDTO accessorPartyDTO) {

        AccessorPartyMD accessorParty = accessorPartyMDRepository.findByNameAndCountryId(accessorPartyDTO.getName(), countryId);
        if (Optional.ofNullable(accessorParty).isPresent()) {
            if (id.equals(accessorParty.getId())) {
                return accessorPartyDTO;
            }
            throw new DuplicateDataException("Name Already Exist");
        }
        Integer resultCount =  accessorPartyMDRepository.updateAccessorPartyName(accessorPartyDTO.getName(), id);
        if(resultCount <=0){
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Accessor Party", id);
        }else{
            LOGGER.info("Data updated successfully for id : {} and name updated name is : {}", id, accessorPartyDTO.getName());
        }
        return accessorPartyDTO;


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
                accessorParty.setSuggestedDataStatus(SuggestedDataStatus.PENDING);
                accessorParty.setSuggestedDate(LocalDate.now());
                accessorPartyList.add(accessorParty);
            }

           accessorPartyMongoRepository.saveAll(getNextSequence(accessorPartyList));
        }
        return accessorPartyList;
    }


    /**
     *
     * @param countryId
     * @param accessorPartyIds
     * @param suggestedDataStatus
     * @return
     */
    public List<AccessorPartyMD> updateSuggestedStatusOfAccessorPartyList(Long countryId, Set<Long> accessorPartyIds, SuggestedDataStatus suggestedDataStatus) {

        Integer updateCount = accessorPartyMDRepository.updateAccessorPartiesStatus(countryId, accessorPartyIds);
        if(updateCount > 0){
            LOGGER.info("Accessor Parties are updated successfully with ids :: {}", accessorPartyIds);
        }else{
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Accessor Party", accessorPartyIds);
        }
        return accessorPartyMDRepository.findAllByIds(accessorPartyIds);
    }

}






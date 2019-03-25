package com.kairos.service.master_data.processing_activity_masterdata;


import com.kairos.commons.custom_exception.DataNotFoundByIdException;
import com.kairos.commons.custom_exception.DuplicateDataException;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.gdpr.metadata.AccessorPartyDTO;
import com.kairos.enums.gdpr.SuggestedDataStatus;
import com.kairos.persistence.model.master_data.default_proc_activity_setting.AccessorParty;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.accessor_party.AccessorPartyRepository;
import com.kairos.response.dto.common.AccessorPartyResponseDTO;
import com.kairos.service.exception.ExceptionService;
import com.kairos.utils.ComparisonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;


@Service
public class AccessorPartyService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccessorPartyService.class);

    @Inject
    private ExceptionService exceptionService;

    @Inject
    private AccessorPartyRepository accessorPartyRepository;


    /**
     * @param accessorPartyDTOS
     * @param countryId -countryId
     * @return return map which contain list of new AccessorParty and list of existing AccessorParty if AccessorParty already exist
     * @description this method create new AccessorParty if AccessorParty not exist with same name ,
     * and if exist then simply add  AccessorParty to existing list and return list ;
     * findByNamesList()  return list of existing AccessorParty using collation ,used for case insensitive result
     */
    public List<AccessorPartyDTO> createAccessorParty(Long countryId, List<AccessorPartyDTO> accessorPartyDTOS, boolean isSuggestion) {
        Set<String> existingAccessorPartyNames = accessorPartyRepository.findNameByCountryIdAndDeleted(countryId);
        Set<String> accessorPartyNames = ComparisonUtils.getNewMetaDataNames(accessorPartyDTOS,existingAccessorPartyNames );
        List<AccessorParty> accessorParties = new ArrayList<>();
        if (!accessorPartyNames.isEmpty()) {
            for (String name : accessorPartyNames) {
                AccessorParty accessorParty = new AccessorParty(countryId, name);
                if (isSuggestion) {
                    accessorParty.setSuggestedDataStatus(SuggestedDataStatus.PENDING);
                    accessorParty.setSuggestedDate(LocalDate.now());
                } else {
                    accessorParty.setSuggestedDataStatus(SuggestedDataStatus.APPROVED);
                }
                accessorParties.add(accessorParty);
            }
            accessorPartyRepository.saveAll(accessorParties);
        }
        return ObjectMapperUtils.copyPropertiesOfListByMapper(accessorParties, AccessorPartyDTO.class);
    }


    public List<AccessorPartyResponseDTO> getAllAccessorParty(Long countryId) {
        return accessorPartyRepository.findAllByCountryIdAndSortByCreatedDate(countryId);
    }

    /**
     * @param id id of AccessorParty
     * @return AccessorParty object fetch by given id
     * @throws DataNotFoundByIdException throw exception if AccessorParty not found for given id
     */
    public AccessorParty getAccessorParty(Long countryId, Long id) {
        AccessorParty exist = accessorPartyRepository.findByIdAndCountryIdAndDeletedFalse(id, countryId);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("No data found");
        } else {
            return exist;

        }
    }


    public Boolean deleteAccessorParty(Long countryId, Long id) {

        Integer resultCount = accessorPartyRepository.deleteByIdAndCountryId(id, countryId);
        if (resultCount > 0) {
            LOGGER.info("Accessor Party deleted successfully for id :: {}", id);
        } else {
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

        AccessorParty accessorParty = accessorPartyRepository.findByCountryIdAndName(countryId, accessorPartyDTO.getName());
        if (Optional.ofNullable(accessorParty).isPresent()) {
            if (id.equals(accessorParty.getId())) {
                return accessorPartyDTO;
            }
            throw new DuplicateDataException("Name Already Exist");
        }
        Integer resultCount = accessorPartyRepository.updateMasterMetadataName(accessorPartyDTO.getName(), id, countryId);
        if (resultCount <= 0) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "message.accessorParty", id);
        } else {
            LOGGER.info("Data updated successfully for id : {} and name updated name is : {}", id, accessorPartyDTO.getName());
        }
        return accessorPartyDTO;


    }


    /**
     * @param countryId
     * @param accessorPartyDTOS
     * @return
     * @description method save Accessor Party suggested by unit
     */
    public void saveSuggestedAccessorPartiesFromUnit(Long countryId, List<AccessorPartyDTO> accessorPartyDTOS) {
         createAccessorParty(countryId, accessorPartyDTOS, true);
    }


    /**
     * @param countryId
     * @param accessorPartyIds
     * @param suggestedDataStatus
     * @return
     */
    public List<AccessorParty> updateSuggestedStatusOfAccessorPartyList(Long countryId, Set<Long> accessorPartyIds, SuggestedDataStatus suggestedDataStatus) {

        Integer updateCount = accessorPartyRepository.updateMetadataStatus(countryId, accessorPartyIds, suggestedDataStatus);
        if (updateCount > 0) {
            LOGGER.info("Accessor Parties are updated successfully with ids :: {}", accessorPartyIds);
        } else {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "message.accessorParty", accessorPartyIds);
        }
        return accessorPartyRepository.findAllByIds(accessorPartyIds);
    }

}






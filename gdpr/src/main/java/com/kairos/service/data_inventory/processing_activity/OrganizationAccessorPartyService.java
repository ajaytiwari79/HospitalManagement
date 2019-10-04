package com.kairos.service.data_inventory.processing_activity;

import com.kairos.commons.custom_exception.DataNotFoundByIdException;
import com.kairos.commons.custom_exception.DuplicateDataException;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.gdpr.metadata.AccessorPartyDTO;
import com.kairos.persistence.model.master_data.default_proc_activity_setting.AccessorParty;
import com.kairos.persistence.repository.data_inventory.processing_activity.ProcessingActivityRepository;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.accessor_party.AccessorPartyRepository;
import com.kairos.response.dto.common.AccessorPartyResponseDTO;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.master_data.processing_activity_masterdata.AccessorPartyService;
import com.kairos.utils.ComparisonUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class OrganizationAccessorPartyService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrganizationAccessorPartyService.class);

    @Inject
    private ExceptionService exceptionService;

    @Inject
    private AccessorPartyService accessorPartyService;


    @Inject
    private ProcessingActivityRepository processingActivityRepository;

    @Inject
    private AccessorPartyRepository accessorPartyRepository;

    /**
     * @param unitId
     * @param accessorPartyDTOS
     * @return return map which contain list of new AccessorParty and list of existing AccessorParty if AccessorParty already exist
     * @description this method create new AccessorParty if AccessorParty not exist with same name ,
     * and if exist then simply add  AccessorParty to existing list and return list ;
     * findMetaDataByNamesAndCountryId()  return list of existing AccessorParty using collation ,used for case insensitive result
     */
    public List<AccessorPartyDTO> createAccessorParty(Long unitId, List<AccessorPartyDTO> accessorPartyDTOS) {
        Set<String> existingAccessorPartyNames = accessorPartyRepository.findNameByOrganizationIdAndDeleted(unitId);
        Set<String> accessorPartyNames = ComparisonUtils.getNewMetaDataNames(accessorPartyDTOS,existingAccessorPartyNames );
        List<AccessorParty> accessorParties = new ArrayList<>();
        if (!accessorPartyNames.isEmpty()) {
            for (String name : accessorPartyNames) {
                AccessorParty accessorParty = new AccessorParty(name);
                accessorParty.setOrganizationId(unitId);
                accessorParties.add(accessorParty);
            }
           accessorPartyRepository.saveAll(accessorParties);
        }
        return ObjectMapperUtils.copyPropertiesOfListByMapper(accessorParties, AccessorPartyDTO.class);
    }

    public List<AccessorPartyResponseDTO> getAllAccessorParty(Long unitId) {
        return accessorPartyRepository.findAllByOrganizationIdAndSortByCreatedDate(unitId);
    }

    /**
     * @param unitId
     * @param id             id of AccessorParty
     * @return AccessorParty object fetch by given id
     * @throws DataNotFoundByIdException throw exception if AccessorParty not found for given id
     */
    public AccessorParty getAccessorPartyById(Long unitId, Long id) {

        AccessorParty exist = accessorPartyRepository.findByIdAndOrganizationIdAndDeletedFalse(id, unitId);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            return exist;

        }
    }


    public Boolean deleteAccessorParty(Long unitId, Long accessorPartyId) {

        List<String> processingActivitiesLinkedWithAccessorParty = processingActivityRepository.findAllProcessingActivityLinkedWithAccessorParty(unitId, accessorPartyId);
        if (!processingActivitiesLinkedWithAccessorParty.isEmpty()) {
            exceptionService.metaDataLinkedWithProcessingActivityException("message.metaData.linked.with.ProcessingActivity", "message.accessorParty", StringUtils.join(processingActivitiesLinkedWithAccessorParty, ','));
        }
        accessorPartyRepository.deleteByIdAndOrganizationId(accessorPartyId, unitId);
        return true;
    }

    /**
     * @param unitId
     * @param id               id of AccessorParty
     * @param accessorPartyDTO
     * @return AccessorParty updated object
     * @throws DuplicateDataException throw exception if AccessorParty data not exist for given id
     */
    public AccessorPartyDTO updateAccessorParty(Long unitId, Long id, AccessorPartyDTO accessorPartyDTO) {


        AccessorParty accessorParty = accessorPartyRepository.findByOrganizationIdAndDeletedAndName(unitId, accessorPartyDTO.getName());
        if (Optional.ofNullable(accessorParty).isPresent()) {
            if (id.equals(accessorParty.getId())) {
                return accessorPartyDTO;
            }
            exceptionService.duplicateDataException("message.duplicate", "message.accessorParty", accessorParty.getName());
        }
        Integer resultCount = accessorPartyRepository.updateMetadataName(accessorPartyDTO.getName(), id, unitId);
        if (resultCount <= 0) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "message.accessorParty", id);
        } else {
            LOGGER.info("Data updated successfully for id : {} and name updated name is : {}", id, accessorPartyDTO.getName());
        }
        return accessorPartyDTO;


    }

    public List<AccessorPartyDTO> saveAndSuggestAccessorParties(Long countryId, Long unitId, List<AccessorPartyDTO> accessorPartyDTOS) {

        List<AccessorPartyDTO> result = createAccessorParty(unitId, accessorPartyDTOS);
        accessorPartyService.saveSuggestedAccessorPartiesFromUnit(countryId, accessorPartyDTOS);
        return result;
    }


}

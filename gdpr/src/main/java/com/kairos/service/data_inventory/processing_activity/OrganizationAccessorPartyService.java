package com.kairos.service.data_inventory.processing_activity;


import com.kairos.commons.custom_exception.DataNotFoundByIdException;
import com.kairos.commons.custom_exception.DuplicateDataException;
import com.kairos.commons.custom_exception.InvalidRequestException;
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
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.constants.AppConstant.EXISTING_DATA_LIST;
import static com.kairos.constants.AppConstant.NEW_DATA_LIST;

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
     * @param organizationId
     * @param accessorPartyDTOS
     * @return return map which contain list of new AccessorParty and list of existing AccessorParty if AccessorParty already exist
     * @description this method create new AccessorParty if AccessorParty not exist with same name ,
     * and if exist then simply add  AccessorParty to existing list and return list ;
     * findMetaDataByNamesAndCountryId()  return list of existing AccessorParty using collation ,used for case insensitive result
     */
    public List<AccessorPartyDTO> createAccessorParty(Long organizationId, List<AccessorPartyDTO> accessorPartyDTOS) {

        Set<String> accessorPartyNames = new HashSet<>();
        for (AccessorPartyDTO accessorParty : accessorPartyDTOS) {
            accessorPartyNames.add(accessorParty.getName());
        }
        List<String> nameInLowerCase = accessorPartyNames.stream().map(String::toLowerCase)
                .collect(Collectors.toList());
        //TODO still need to update we can return name of list from here and can apply removeAll on list
        List<AccessorParty> previousAccessorParties = accessorPartyRepository.findByOrganizationIdAndDeletedAndNameIn(organizationId, false, nameInLowerCase);
        accessorPartyNames = ComparisonUtils.getNameListForMetadata(previousAccessorParties, accessorPartyNames);

        List<AccessorParty> accessorParties = new ArrayList<>();
        if (!accessorPartyNames.isEmpty()) {
            for (String name : accessorPartyNames) {
                AccessorParty accessorParty = new AccessorParty(name);
                accessorParty.setOrganizationId(organizationId);
                accessorParties.add(accessorParty);
            }
           accessorPartyRepository.saveAll(accessorParties);
        }
        return ObjectMapperUtils.copyPropertiesOfListByMapper(accessorParties, AccessorPartyDTO.class);
    }

    public List<AccessorPartyResponseDTO> getAllAccessorParty(Long organizationId) {
        return accessorPartyRepository.findAllByOrganizationIdAndSortByCreatedDate(organizationId);
    }

    /**
     * @param organizationId
     * @param id             id of AccessorParty
     * @return AccessorParty object fetch by given id
     * @throws DataNotFoundByIdException throw exception if AccessorParty not found for given id
     */
    public AccessorParty getAccessorPartyById(Long organizationId, Long id) {

        AccessorParty exist = accessorPartyRepository.findByIdAndOrganizationIdAndDeletedFalse(id, organizationId);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            return exist;

        }
    }


    public Boolean deleteAccessorParty(Long unitId, Long accessorPartyId) {

        List<String> processingActivitiesLinkedWithAccessorParty = processingActivityRepository.findAllProcessingActivityLinkedWithAccessorParty(unitId, accessorPartyId);
        if (!processingActivitiesLinkedWithAccessorParty.isEmpty()) {
            exceptionService.metaDataLinkedWithProcessingActivityException("message.metaData.linked.with.ProcessingActivity", "Accessor Party", StringUtils.join(processingActivitiesLinkedWithAccessorParty, ','));
        }
        accessorPartyRepository.deleteByIdAndOrganizationId(accessorPartyId, unitId);
        return true;
    }

    /**
     * @param organizationId
     * @param id               id of AccessorParty
     * @param accessorPartyDTO
     * @return AccessorParty updated object
     * @throws DuplicateDataException throw exception if AccessorParty data not exist for given id
     */
    public AccessorPartyDTO updateAccessorParty(Long organizationId, Long id, AccessorPartyDTO accessorPartyDTO) {


        AccessorParty accessorParty = accessorPartyRepository.findByOrganizationIdAndDeletedAndName(organizationId, accessorPartyDTO.getName());
        if (Optional.ofNullable(accessorParty).isPresent()) {
            if (id.equals(accessorParty.getId())) {
                return accessorPartyDTO;
            }
            exceptionService.duplicateDataException("message.duplicate", "Accessor Party", accessorParty.getName());
        }
        Integer resultCount = accessorPartyRepository.updateMetadataName(accessorPartyDTO.getName(), id, organizationId);
        if (resultCount <= 0) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Accessor Party", id);
        } else {
            LOGGER.info("Data updated successfully for id : {} and name updated name is : {}", id, accessorPartyDTO.getName());
        }
        return accessorPartyDTO;


    }

    public List<AccessorPartyDTO> saveAndSuggestAccessorParties(Long countryId, Long organizationId, List<AccessorPartyDTO> accessorPartyDTOS) {

        List<AccessorPartyDTO> result = createAccessorParty(organizationId, accessorPartyDTOS);
        accessorPartyService.saveSuggestedAccessorPartiesFromUnit(countryId, accessorPartyDTOS);
        return result;
    }


}

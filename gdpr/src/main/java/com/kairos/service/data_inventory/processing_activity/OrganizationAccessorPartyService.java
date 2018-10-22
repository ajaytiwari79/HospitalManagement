package com.kairos.service.data_inventory.processing_activity;


import com.kairos.commons.custom_exception.DataNotFoundByIdException;
import com.kairos.commons.custom_exception.DuplicateDataException;
import com.kairos.commons.custom_exception.InvalidRequestException;
import com.kairos.dto.gdpr.metadata.AccessorPartyDTO;
import com.kairos.persistence.model.master_data.default_proc_activity_setting.AccessorParty;
import com.kairos.persistence.repository.data_inventory.processing_activity.ProcessingActivityMongoRepository;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.accessor_party.AccessorPartyMongoRepository;
import com.kairos.response.dto.common.AccessorPartyResponseDTO;
import com.kairos.response.dto.data_inventory.ProcessingActivityBasicDTO;
import com.kairos.response.dto.data_inventory.ProcessingActivityBasicResponseDTO;
import com.kairos.service.common.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.master_data.processing_activity_masterdata.AccessorPartyService;
import com.kairos.utils.ComparisonUtils;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.constants.AppConstant.EXISTING_DATA_LIST;
import static com.kairos.constants.AppConstant.NEW_DATA_LIST;

@Service
public class OrganizationAccessorPartyService extends MongoBaseService {


    @Inject
    private AccessorPartyMongoRepository accessorPartyMongoRepository;

    @Inject
    private ExceptionService exceptionService;

    @Inject
    private AccessorPartyService accessorPartyService;


    @Inject
    private ProcessingActivityMongoRepository processingActivityMongoRepository;

    /**
     * @param organizationId
     * @param accessorPartyDTOS
     * @return return map which contain list of new AccessorParty and list of existing AccessorParty if AccessorParty already exist
     * @description this method create new AccessorParty if AccessorParty not exist with same name ,
     * and if exist then simply add  AccessorParty to existing list and return list ;
     * findMetaDataByNamesAndCountryId()  return list of existing AccessorParty using collation ,used for case insensitive result
     */
    public Map<String, List<AccessorParty>> createAccessorParty(Long organizationId, List<AccessorPartyDTO> accessorPartyDTOS) {

        Map<String, List<AccessorParty>> result = new HashMap<>();
        Set<String> accessorPartyNames = new HashSet<>();
        if (!accessorPartyDTOS.isEmpty()) {
            for (AccessorPartyDTO accessorParty : accessorPartyDTOS) {
                accessorPartyNames.add(accessorParty.getName());
            }
            List<AccessorParty> existing = findMetaDataByNameAndUnitId(organizationId, accessorPartyNames, AccessorParty.class);
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
        return accessorPartyMongoRepository.findAllByUnitIdSortByCreatedDate(organizationId, new Sort(Sort.Direction.DESC, "createdAt"));
    }

    /**
     * @param organizationId
     * @param id             id of AccessorParty
     * @return AccessorParty object fetch by given id
     * @throws DataNotFoundByIdException throw exception if AccessorParty not found for given id
     */
    public AccessorParty getAccessorPartyById(Long organizationId, BigInteger id) {

        AccessorParty exist = accessorPartyMongoRepository.findOrganizationIdAndIdAndNonDeleted(organizationId, id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            return exist;

        }
    }


    public Boolean deleteAccessorParty(Long unitId, BigInteger accessorPartyId) {

        List<ProcessingActivityBasicDTO> processingActivitiesLinkedWithAccessorParty = processingActivityMongoRepository.findAllProcessingActivityLinkedWithAccessorParty(unitId, accessorPartyId);
        if (!processingActivitiesLinkedWithAccessorParty.isEmpty()) {
            exceptionService.metaDataLinkedWithProcessingActivityException("message.metaData.linked.with.ProcessingActivity", "Accessor Party", new StringBuilder(processingActivitiesLinkedWithAccessorParty.stream().map(ProcessingActivityBasicDTO::getName).map(String::toString).collect(Collectors.joining(","))));
        }
        accessorPartyMongoRepository.safeDelete(accessorPartyId);
        return true;
    }

    /**
     * @param organizationId
     * @param id               id of AccessorParty
     * @param accessorPartyDTO
     * @return AccessorParty updated object
     * @throws DuplicateDataException throw exception if AccessorParty data not exist for given id
     */
    public AccessorPartyDTO updateAccessorParty(Long organizationId, BigInteger id, AccessorPartyDTO accessorPartyDTO) {


        AccessorParty accessorParty = accessorPartyMongoRepository.findByNameAndUnitId(organizationId, accessorPartyDTO.getName());
        if (Optional.ofNullable(accessorParty).isPresent()) {
            if (id.equals(accessorParty.getId())) {
                return accessorPartyDTO;
            }
            exceptionService.duplicateDataException("message.duplicate", "Accessor Party", accessorParty.getName());
        }
        accessorParty = accessorPartyMongoRepository.findByid(id);
        if (!Optional.ofNullable(accessorParty).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Accessor Party", id);
        }
        accessorParty.setName(accessorPartyDTO.getName());
        accessorPartyMongoRepository.save(accessorParty);
        return accessorPartyDTO;


    }

    public Map<String, List<AccessorParty>> saveAndSuggestAccessorParties(Long countryId, Long organizationId, List<AccessorPartyDTO> accessorPartyDTOS) {

        Map<String, List<AccessorParty>> result;
        result = createAccessorParty(organizationId, accessorPartyDTOS);
        List<AccessorParty> masterAccessorPartySuggestedByUnit = accessorPartyService.saveSuggestedAccessorPartiesFromUnit(countryId, accessorPartyDTOS);
        if (!masterAccessorPartySuggestedByUnit.isEmpty()) {
            result.put("SuggestedData", masterAccessorPartySuggestedByUnit);
        }
        return result;
    }


}

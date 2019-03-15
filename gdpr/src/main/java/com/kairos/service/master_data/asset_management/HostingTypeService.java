package com.kairos.service.master_data.asset_management;


import com.kairos.commons.custom_exception.DataNotFoundByIdException;
import com.kairos.commons.custom_exception.DuplicateDataException;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.gdpr.metadata.HostingTypeDTO;
import com.kairos.enums.gdpr.SuggestedDataStatus;
import com.kairos.persistence.model.master_data.default_asset_setting.HostingType;
import com.kairos.persistence.repository.master_data.asset_management.hosting_type.HostingTypeRepository;
import com.kairos.response.dto.common.HostingTypeResponseDTO;
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
public class HostingTypeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(HostingTypeService.class);

    @Inject
    private ExceptionService exceptionService;

    @Inject
    private HostingTypeRepository hostingTypeRepository;


    /**
     * @param countryId
     * @param
     * @param hostingTypeDTOS
     * @return return map which contain list of new HostingType and list of existing HostingType if HostingType already exist
     * @description this method create new HostingType if HostingType not exist with same name ,
     * and if exist then simply add  HostingType to existing list and return list ;
     * findMetaDataByNamesAndCountryId()  return list of existing HostingType using collation ,used for case insensitive result
     */
    public List<HostingTypeDTO> createHostingType(Long countryId, List<HostingTypeDTO> hostingTypeDTOS, boolean isSuggestion) {
        Set<String> existingHostingTypeNames = hostingTypeRepository.findNameByCountryIdAndDeleted(countryId);
        Set<String> hostingTypeNames = ComparisonUtils.getNewMetaDataNames(hostingTypeDTOS,existingHostingTypeNames );
        List<HostingType> hostingTypes = new ArrayList<>();
        if (!hostingTypeNames.isEmpty()) {
            for (String name : hostingTypeNames) {
                HostingType hostingType = new HostingType(name, countryId);
                if (isSuggestion) {
                    hostingType.setSuggestedDataStatus(SuggestedDataStatus.PENDING);
                    hostingType.setSuggestedDate(LocalDate.now());
                } else {
                    hostingType.setSuggestedDataStatus(SuggestedDataStatus.APPROVED);
                }
                hostingTypes.add(hostingType);
            }
            hostingTypeRepository.saveAll(hostingTypes);
        }

        return ObjectMapperUtils.copyPropertiesOfListByMapper(hostingTypes, HostingTypeDTO.class);

    }


    /**
     * @param countryId
     * @param
     * @return list of HostingType
     */
    public List<HostingTypeResponseDTO> getAllHostingType(Long countryId) {
        return hostingTypeRepository.findAllByCountryIdAndSortByCreatedDate(countryId);
    }


    /**
     * @param countryId
     * @param
     * @param id        of HostingType
     * @return HostingType object fetch by given id
     * @throws DataNotFoundByIdException throw exception if HostingType not found for given id
     */
    public HostingType getHostingType(Long countryId, Long id) {

        HostingType exist = hostingTypeRepository.findByIdAndCountryIdAndDeletedFalse(id, countryId);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("No data found");
        } else {
            return exist;

        }
    }


    public Boolean deleteHostingType(Long countryId, Long id) {

        Integer resultCount = hostingTypeRepository.deleteByIdAndCountryId(id, countryId);
        if (resultCount > 0) {
            LOGGER.info("Hosting Type deleted successfully for id :: {}", id);
        } else {
            throw new DataNotFoundByIdException("No data found");
        }
        return true;
    }


    /**
     * @param countryId
     * @param
     * @param id             id of HostingType
     * @param hostingTypeDTO
     * @return HostingType updated object
     * @throws DuplicateDataException if HostingType already exist with same name
     */
    public HostingTypeDTO updateHostingType(Long countryId, Long id, HostingTypeDTO hostingTypeDTO) {


        HostingType hostingType = hostingTypeRepository.findByCountryIdAndName(countryId, hostingTypeDTO.getName());
        if (Optional.ofNullable(hostingType).isPresent()) {
            if (id.equals(hostingType.getId())) {
                return hostingTypeDTO;
            }
            throw new DuplicateDataException("data  exist for  " + hostingTypeDTO.getName());
        }
        Integer resultCount = hostingTypeRepository.updateMasterMetadataName(hostingTypeDTO.getName(), id, countryId);
        if (resultCount <= 0) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Hosting Type", id);
        } else {
            LOGGER.info("Data updated successfully for id : {} and name updated name is : {}", id, hostingTypeDTO.getName());
        }
        return hostingTypeDTO;


    }


    /**
     * @param countryId
     * @param hostingTypeDTOS
     * @return
     * @description method save Hosting type suggested by unit
     */
    public List<HostingTypeDTO> saveSuggestedHostingTypesFromUnit(Long countryId, List<HostingTypeDTO> hostingTypeDTOS) {
        return createHostingType(countryId, hostingTypeDTOS, true);
    }


    /**
     * @param countryId
     * @param hostingTypeIds
     * @param suggestedDataStatus
     * @return
     */
    public List<HostingType> updateSuggestedStatusOfHostingTypes(Long countryId, Set<Long> hostingTypeIds, SuggestedDataStatus suggestedDataStatus) {

        Integer updateCount = hostingTypeRepository.updateMetadataStatus(countryId, hostingTypeIds, suggestedDataStatus);
        if (updateCount > 0) {
            LOGGER.info("Hosting providers are updated successfully with ids :: {}", hostingTypeIds);
        } else {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Hosting Providers", hostingTypeIds);
        }
        return hostingTypeRepository.findAllByIds(hostingTypeIds);
    }


}

    
    
    


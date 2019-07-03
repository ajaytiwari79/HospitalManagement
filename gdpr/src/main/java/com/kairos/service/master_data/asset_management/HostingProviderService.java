package com.kairos.service.master_data.asset_management;

import com.kairos.commons.custom_exception.DataNotFoundByIdException;
import com.kairos.commons.custom_exception.DuplicateDataException;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.gdpr.metadata.HostingProviderDTO;
import com.kairos.enums.gdpr.SuggestedDataStatus;
import com.kairos.persistence.model.master_data.default_asset_setting.HostingProvider;
import com.kairos.persistence.repository.master_data.asset_management.hosting_provider.HostingProviderRepository;
import com.kairos.response.dto.common.HostingProviderResponseDTO;
import com.kairos.service.exception.ExceptionService;
import com.kairos.utils.ComparisonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.*;

@Service
public class HostingProviderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(HostingProviderService.class);

    @Inject
    private ExceptionService exceptionService;

    @Inject
    private HostingProviderRepository hostingProviderRepository;


    /**
     * @param countryId
     * @param hostingProviderDTOS
     * @return return map which contain list of new HostingProvider and list of existing HostingProvider if HostingProvider already exist
     * @description this method create new HostingProvider if HostingProvider not exist with same name ,
     * and if exist then simply add  HostingProvider to existing list and return list ;
     * findMetaDataByNamesAndCountryId()  return list of existing HostingProvider using collation ,used for case insensitive result
     */
    public List<HostingProviderDTO> createHostingProviders(Long countryId, List<HostingProviderDTO> hostingProviderDTOS, boolean isSuggestion) {
        Set<String> existingHostingProviderNames = hostingProviderRepository.findNameByCountryIdAndDeleted(countryId);
        Set<String> hostingProviderNames = ComparisonUtils.getNewMetaDataNames(hostingProviderDTOS,existingHostingProviderNames );
        List<HostingProvider> hostingProviders = new ArrayList<>();
        if (!hostingProviderNames.isEmpty()) {
            for (String name : hostingProviderNames) {
                HostingProvider hostingProvider = new HostingProvider(countryId, name);
                if (isSuggestion) {
                    hostingProvider.setSuggestedDataStatus(SuggestedDataStatus.PENDING);
                    hostingProvider.setSuggestedDate(LocalDate.now());
                } else {
                    hostingProvider.setSuggestedDataStatus(SuggestedDataStatus.APPROVED);
                }
                hostingProviders.add(hostingProvider);
            }

           hostingProviderRepository.saveAll(hostingProviders);
        }

        return ObjectMapperUtils.copyPropertiesOfListByMapper(hostingProviders, HostingProviderDTO.class);

    }


    /**
     * @param countryId
     * @param
     * @return list of HostingProvider
     */
    public List<HostingProviderResponseDTO> getAllHostingProvider(Long countryId) {
        return hostingProviderRepository.findAllByCountryIdAndSortByCreatedDate(countryId);
    }


    /**
     * @param countryId
     * @param
     * @param id
     * @return HostingProvider object fetch by id
     * @throws DataNotFoundByIdException if HostingProvider not exist for given id
     */
    public HostingProvider getHostingProviderById(Long countryId, Long id) {

        HostingProvider exist = hostingProviderRepository.findByIdAndCountryIdAndDeletedFalse(id, countryId);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("No data found");
        } else {
            return exist;

        }
    }


    public Boolean deleteHostingProvider(Long countryId, Long id) {

        Integer resultCount = hostingProviderRepository.deleteByIdAndCountryId(id, countryId);
        if (resultCount > 0) {
            LOGGER.info("Hosting Provider deleted successfully for id :: {}", id);
        } else {
            throw new DataNotFoundByIdException("No data found");
        }
        return true;
    }


    /**
     * @param countryId
     * @param
     * @param id                 id of HostingProvider
     * @param hostingProviderDTO
     * @return return updated HostingProvider object
     * @throws DuplicateDataException if HostingProvider exist with same name
     */
    public HostingProviderDTO updateHostingProvider(Long countryId, Long id, HostingProviderDTO hostingProviderDTO) {

        HostingProvider hostingProvider = hostingProviderRepository.findByCountryIdAndName(countryId, hostingProviderDTO.getName());
        if (Optional.ofNullable(hostingProvider).isPresent()) {
            if (id.equals(hostingProvider.getId())) {
                return hostingProviderDTO;
            }
            throw new DuplicateDataException("data  exist for  " + hostingProviderDTO.getName());
        }
        Integer resultCount = hostingProviderRepository.updateMasterMetadataName(hostingProviderDTO.getName(), id, countryId);
        if (resultCount <= 0) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "message.hostingProvider", id);
        } else {
            LOGGER.info("Data updated successfully for id : {} and name updated name is : {}", id, hostingProviderDTO.getName());
        }
        return hostingProviderDTO;

    }


    /**
     * *
     *
     * @param countryId
     * @param hostingProviderDTOS
     * @return
     * @description method save Hosting provider suggested by unit
     */
    public void saveSuggestedHostingProvidersFromUnit(Long countryId, List<HostingProviderDTO> hostingProviderDTOS) {
         createHostingProviders(countryId, hostingProviderDTOS, true);
    }


    /**
     * @param countryId
     * @param hostingProviderIds  - ids of hosting providers
     * @param suggestedDataStatus - status to update
     * @return
     */
    public List<HostingProvider> updateSuggestedStatusOfHostingProviders(Long countryId, Set<Long> hostingProviderIds, SuggestedDataStatus suggestedDataStatus) {

        Integer updateCount = hostingProviderRepository.updateMetadataStatus(countryId, hostingProviderIds, suggestedDataStatus);
        if (updateCount > 0) {
            LOGGER.info("Hosting providers are updated successfully with ids :: {}", hostingProviderIds);
        } else {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "message.hostingProvider", hostingProviderIds);
        }
        return hostingProviderRepository.findAllByIds(hostingProviderIds);
    }

}

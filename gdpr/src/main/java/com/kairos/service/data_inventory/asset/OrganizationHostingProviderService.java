package com.kairos.service.data_inventory.asset;

import com.kairos.commons.custom_exception.DataNotFoundByIdException;
import com.kairos.commons.custom_exception.DuplicateDataException;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.gdpr.metadata.HostingProviderDTO;
import com.kairos.persistence.model.master_data.default_asset_setting.HostingProvider;
import com.kairos.persistence.repository.data_inventory.asset.AssetRepository;
import com.kairos.persistence.repository.master_data.asset_management.hosting_provider.HostingProviderRepository;
import com.kairos.response.dto.common.HostingProviderResponseDTO;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.master_data.asset_management.HostingProviderService;
import com.kairos.utils.ComparisonUtils;
import org.apache.commons.collections.CollectionUtils;
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
public class OrganizationHostingProviderService {


    private static final Logger LOGGER = LoggerFactory.getLogger(OrganizationHostingProviderService.class);

    @Inject
    private HostingProviderRepository hostingProviderRepository;

    @Inject
    private ExceptionService exceptionService;

    @Inject
    private HostingProviderService hostingProviderService;

    @Inject
    private AssetRepository assetRepository;


    /**
     * @param unitId
     * @param hostingProviderDTOS
     * @return return map which contain list of new HostingProvider and list of existing HostingProvider if HostingProvider already exist
     * @description this method create new HostingProvider if HostingProvider not exist with same name ,
     * and if exist then simply add  HostingProvider to existing list and return list ;
     * findMetaDataByNamesAndCountryId()  return list of existing HostingProvider using collation ,used for case insensitive result
     */
    public List<HostingProviderDTO> createHostingProviders(Long unitId, List<HostingProviderDTO> hostingProviderDTOS) {
        Set<String> existingHostingProviderNames = hostingProviderRepository.findNameByOrganizationIdAndDeleted(unitId);
        Set<String> hostingProviderNames = ComparisonUtils.getNewMetaDataNames(hostingProviderDTOS,existingHostingProviderNames );
        List<HostingProvider> hostingProviderList = new ArrayList<>();
        if (!hostingProviderNames.isEmpty()) {
            for (String name : hostingProviderNames) {

                HostingProvider hostingProvider = new HostingProvider(name);
                hostingProvider.setOrganizationId(unitId);
                hostingProviderList.add(hostingProvider);
            }
           hostingProviderRepository.saveAll(hostingProviderList);
        }
        return ObjectMapperUtils.copyPropertiesOfListByMapper(hostingProviderList, HostingProviderDTO.class);

    }


    /**
     * @param
     * @param unitId
     * @return list of HostingProvider
     */
    public List<HostingProviderResponseDTO> getAllHostingProvider(Long unitId) {
        return hostingProviderRepository.findAllByOrganizationIdAndSortByCreatedDate(unitId);
    }


    /**
     * @param
     * @param unitId
     * @param id
     * @return HostingProvider object fetch by id
     * @throws DataNotFoundByIdException if HostingProvider not exist for given id
     */
    public HostingProvider getHostingProviderById(Long unitId, Long id) {

        HostingProvider exist = hostingProviderRepository.findByIdAndOrganizationIdAndDeletedFalse(id, unitId);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            return exist;

        }
    }


    public Boolean deleteHostingProvider(Long unitId, Long hostingProviderId) {
        List<String> assetNames = assetRepository.findAllAssetLinkedWithDataDisposal(unitId, hostingProviderId);
        if (CollectionUtils.isNotEmpty(assetNames)) {
            exceptionService.metaDataLinkedWithAssetException("message.metaData.linked.with.asset", "Data Disposal", StringUtils.join(assetNames, ','));
        }
        Integer resultCount = hostingProviderRepository.deleteByIdAndOrganizationId(hostingProviderId, unitId);
        if (resultCount > 0) {
            LOGGER.info("Hosting provider deleted successfully for id :: {}", hostingProviderId);
        } else {
            throw new DataNotFoundByIdException("No data found");
        }
        return true;

    }


    /**
     * @param unitId
     * @param id                 id of HostingProvider
     * @param hostingProviderDTO
     * @return return hostingProviderDTO HostingProvider object
     * @throws DuplicateDataException if HostingProvider exist with same name
     */
    public HostingProviderDTO updateHostingProvider(Long unitId, Long id, HostingProviderDTO hostingProviderDTO) {

        HostingProvider hostingProvider = hostingProviderRepository.findByOrganizationIdAndDeletedAndName(unitId, hostingProviderDTO.getName());
        if (Optional.ofNullable(hostingProvider).isPresent()) {
            if (id.equals(hostingProvider.getId())) {
                return hostingProviderDTO;
            }
            exceptionService.duplicateDataException("message.duplicate", "Hosting Provider", hostingProvider.getName());
        }
        Integer resultCount = hostingProviderRepository.updateMetadataName(hostingProviderDTO.getName(), id, unitId);
        if (resultCount <= 0) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Hosting provider", id);
        } else {
            LOGGER.info("Data updated successfully for id : {} and name updated name is : {}", id, hostingProviderDTO.getName());
        }
        return hostingProviderDTO;


    }


    public List<HostingProviderDTO> saveAndSuggestHostingProviders(Long countryId, Long unitId, List<HostingProviderDTO> hostingProviderDTOS) {
        List<HostingProviderDTO> hostingProviders = createHostingProviders(unitId, hostingProviderDTOS);
        hostingProviderService.saveSuggestedHostingProvidersFromUnit(countryId, hostingProviderDTOS);
        return hostingProviders;
    }


}

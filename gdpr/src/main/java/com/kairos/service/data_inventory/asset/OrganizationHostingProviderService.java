package com.kairos.service.data_inventory.asset;
import com.kairos.commons.custom_exception.DataNotFoundByIdException;
import com.kairos.commons.custom_exception.DuplicateDataException;
import com.kairos.commons.custom_exception.InvalidRequestException;
import com.kairos.dto.gdpr.metadata.HostingProviderDTO;
import com.kairos.persistence.model.master_data.default_asset_setting.HostingProviderMD;
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
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.constants.AppConstant.EXISTING_DATA_LIST;
import static com.kairos.constants.AppConstant.NEW_DATA_LIST;


@Service
public class OrganizationHostingProviderService{


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
     * @param organizationId
     * @param hostingProviderDTOS
     * @return return map which contain list of new HostingProvider and list of existing HostingProvider if HostingProvider already exist
     * @description this method create new HostingProvider if HostingProvider not exist with same name ,
     * and if exist then simply add  HostingProvider to existing list and return list ;
     * findMetaDataByNamesAndCountryId()  return list of existing HostingProvider using collation ,used for case insensitive result
     */
    public Map<String, List<HostingProviderMD>> createHostingProviders(Long organizationId, List<HostingProviderDTO> hostingProviderDTOS) {

        Map<String, List<HostingProviderMD>> result = new HashMap<>();
        Set<String> hostingProviderNames = new HashSet<>();
        if (!hostingProviderDTOS.isEmpty()) {
            for (HostingProviderDTO hostingProvider : hostingProviderDTOS) {
                hostingProviderNames.add(hostingProvider.getName());
            }
            List<String> nameInLowerCase = hostingProviderNames.stream().map(String::toLowerCase)
                    .collect(Collectors.toList());
            //TODO still need to update we can return name of list from here and can apply removeAll on list
            List<HostingProviderMD> existing = hostingProviderRepository.findByOrganizationIdAndDeletedAndNameIn(organizationId, false, nameInLowerCase);
            hostingProviderNames = ComparisonUtils.getNameListForMetadata(existing, hostingProviderNames);
            List<HostingProviderMD> newHostingProviders = new ArrayList<>();
            if (!hostingProviderNames.isEmpty()) {
                for (String name : hostingProviderNames) {

                    HostingProviderMD newHostingProvider = new HostingProviderMD(name);
                    newHostingProvider.setOrganizationId(organizationId);
                    newHostingProviders.add(newHostingProvider);
                }
                newHostingProviders = hostingProviderRepository.saveAll(newHostingProviders);
            }
            result.put(EXISTING_DATA_LIST, existing);
            result.put(NEW_DATA_LIST, newHostingProviders);
            return result;
        } else
            throw new InvalidRequestException("list cannot be empty");


    }


    /**
     * @param
     * @param organizationId
     * @return list of HostingProvider
     */
    public List<HostingProviderResponseDTO> getAllHostingProvider(Long organizationId) {
        return hostingProviderRepository.findAllByOrganizationIdAndSortByCreatedDate(organizationId);
    }


    /**
     * @param
     * @param organizationId
     * @param id
     * @return HostingProvider object fetch by id
     * @throws DataNotFoundByIdException if HostingProvider not exist for given id
     */
    public HostingProviderMD getHostingProviderById(Long organizationId, Long id) {

        HostingProviderMD exist = hostingProviderRepository.findByIdAndOrganizationIdAndDeleted(id, organizationId, false);
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
        }else{
            throw new DataNotFoundByIdException("No data found");
        }
        return true;

    }


    /**
     * @param organizationId
     * @param id                 id of HostingProvider
     * @param hostingProviderDTO
     * @return return hostingProviderDTO HostingProvider object
     * @throws DuplicateDataException if HostingProvider exist with same name
     */
    public HostingProviderDTO updateHostingProvider(Long organizationId, Long id, HostingProviderDTO hostingProviderDTO) {

        HostingProviderMD hostingProvider = hostingProviderRepository.findByOrganizationIdAndDeletedAndName( organizationId, false, hostingProviderDTO.getName());
        if (Optional.ofNullable(hostingProvider).isPresent()) {
            if (id.equals(hostingProvider.getId())) {
                return hostingProviderDTO;
            }
            exceptionService.duplicateDataException("message.duplicate", "Hosting Provider", hostingProvider.getName());
        }
        Integer resultCount =  hostingProviderRepository.updateMetadataName(hostingProviderDTO.getName(), id, organizationId);
        if(resultCount <=0){
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Hosting provider", id);
        }else{
            LOGGER.info("Data updated successfully for id : {} and name updated name is : {}", id, hostingProviderDTO.getName());
        }
        return hostingProviderDTO;


    }


    public Map<String, List<HostingProviderMD>> saveAndSuggestHostingProviders(Long countryId, Long organizationId, List<HostingProviderDTO> hostingProviderDTOS) {

        Map<String, List<HostingProviderMD>> result = createHostingProviders(organizationId, hostingProviderDTOS);
        List<HostingProviderMD> masterHostingProviderSuggestedByUnit = hostingProviderService.saveSuggestedHostingProvidersFromUnit(countryId, hostingProviderDTOS);
        if (!masterHostingProviderSuggestedByUnit.isEmpty()) {
            result.put("SuggestedData", masterHostingProviderSuggestedByUnit);
        }
        return result;
    }


}

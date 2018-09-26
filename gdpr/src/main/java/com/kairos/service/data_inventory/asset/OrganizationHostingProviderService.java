package com.kairos.service.data_inventory.asset;

import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.custom_exception.InvalidRequestException;
import com.kairos.dto.gdpr.metadata.HostingProviderDTO;
import com.kairos.persistence.model.master_data.default_asset_setting.HostingProvider;
import com.kairos.persistence.repository.data_inventory.asset.AssetMongoRepository;
import com.kairos.persistence.repository.master_data.asset_management.hosting_provider.HostingProviderMongoRepository;
import com.kairos.response.dto.common.HostingProviderResponseDTO;
import com.kairos.response.dto.data_inventory.AssetBasicResponseDTO;
import com.kairos.service.common.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.master_data.asset_management.HostingProviderService;
import com.kairos.utils.ComparisonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;

import static com.kairos.constants.AppConstant.EXISTING_DATA_LIST;
import static com.kairos.constants.AppConstant.NEW_DATA_LIST;


@Service
public class OrganizationHostingProviderService extends MongoBaseService {


    private static final Logger LOGGER = LoggerFactory.getLogger(OrganizationHostingProviderService.class);

    @Inject
    private HostingProviderMongoRepository hostingProviderMongoRepository;

    @Inject
    private ExceptionService exceptionService;

    @Inject
    private HostingProviderService hostingProviderService;

    @Inject
    private AssetMongoRepository assetMongoRepository;


    /**
     * @param organizationId
     * @param hostingProviderDTOS
     * @return return map which contain list of new HostingProvider and list of existing HostingProvider if HostingProvider already exist
     * @description this method create new HostingProvider if HostingProvider not exist with same name ,
     * and if exist then simply add  HostingProvider to existing list and return list ;
     * findMetaDataByNamesAndCountryId()  return list of existing HostingProvider using collation ,used for case insensitive result
     */
    public Map<String, List<HostingProvider>> createHostingProviders(Long organizationId, List<HostingProviderDTO> hostingProviderDTOS) {

        Map<String, List<HostingProvider>> result = new HashMap<>();
        Set<String> hostingProviderNames = new HashSet<>();
        if (!hostingProviderDTOS.isEmpty()) {
            for (HostingProviderDTO hostingProvider : hostingProviderDTOS) {
                hostingProviderNames.add(hostingProvider.getName());
            }
            List<HostingProvider> existing = findMetaDataByNameAndUnitId(organizationId, hostingProviderNames, HostingProvider.class);
            hostingProviderNames = ComparisonUtils.getNameListForMetadata(existing, hostingProviderNames);
            List<HostingProvider> newHostingProviders = new ArrayList<>();
            if (!hostingProviderNames.isEmpty()) {
                for (String name : hostingProviderNames) {

                    HostingProvider newHostingProvider = new HostingProvider(name);
                    newHostingProvider.setOrganizationId(organizationId);
                    newHostingProviders.add(newHostingProvider);

                }

                newHostingProviders = hostingProviderMongoRepository.saveAll(getNextSequence(newHostingProviders));
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
        return hostingProviderMongoRepository.findAllOrganizationHostingProviders(organizationId,new Sort(Sort.Direction.DESC, "createdAt"));
    }


    /**
     * @param
     * @param organizationId
     * @param id
     * @return HostingProvider object fetch by id
     * @throws DataNotFoundByIdException if HostingProvider not exist for given id
     */
    public HostingProvider getHostingProviderById(Long organizationId, BigInteger id) {

        HostingProvider exist = hostingProviderMongoRepository.findByOrganizationIdAndId(organizationId, id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            return exist;

        }
    }


    public Boolean deleteHostingProvider(Long unitId, BigInteger hostingProviderId) {

        List<AssetBasicResponseDTO> assetsLinkedWithHostingProvider = assetMongoRepository.findAllAssetLinkedWithHostingProvider(unitId, hostingProviderId);
        if (!assetsLinkedWithHostingProvider.isEmpty()) {
            StringBuilder assetNames=new StringBuilder();
            assetsLinkedWithHostingProvider.forEach(asset->assetNames.append(asset.getName()+","));
            exceptionService.metaDataLinkedWithAssetException("message.metaData.linked.with.asset", "Hosting Provider", assetNames);
        }
        HostingProvider hostingProvider = hostingProviderMongoRepository.findByOrganizationIdAndId(unitId, hostingProviderId);
        if (!Optional.ofNullable(hostingProvider).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Hosting Provider", hostingProviderId);
        }
        delete(hostingProvider);
        return true;
    }


    /**
     * @param organizationId
     * @param id                 id of HostingProvider
     * @param hostingProviderDTO
     * @return return hostingProviderDTO HostingProvider object
     * @throws DuplicateDataException if HostingProvider exist with same name
     */
    public HostingProviderDTO updateHostingProvider(Long organizationId, BigInteger id, HostingProviderDTO hostingProviderDTO) {

        HostingProvider hostingProvider = hostingProviderMongoRepository.findByOrganizationIdAndName(organizationId, hostingProviderDTO.getName());
        if (Optional.ofNullable(hostingProvider).isPresent()) {
            if (id.equals(hostingProvider.getId())) {
                return hostingProviderDTO;
            }
            exceptionService.duplicateDataException("message.duplicate", "Hosting Provider", hostingProvider.getName());
        }
        hostingProvider = hostingProviderMongoRepository.findByid(id);
        if (!Optional.ofNullable(hostingProvider).isPresent()) {

            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Hosting Provider", id);
        }
        hostingProvider.setName(hostingProviderDTO.getName());
        hostingProviderMongoRepository.save(hostingProvider);
        return hostingProviderDTO;


    }


    public Map<String, List<HostingProvider>> saveAndSuggestHostingProviders(Long countryId, Long organizationId, List<HostingProviderDTO> hostingProviderDTOS) {

        Map<String, List<HostingProvider>> result;
        result = createHostingProviders(organizationId, hostingProviderDTOS);
        List<HostingProvider> masterHostingProviderSuggestedByUnit = hostingProviderService.saveSuggestedHostingProvidersFromUnit(countryId, hostingProviderDTOS);
        if (!masterHostingProviderSuggestedByUnit.isEmpty()) {
            result.put("SuggestedData", masterHostingProviderSuggestedByUnit);
        }
        return result;
    }


}

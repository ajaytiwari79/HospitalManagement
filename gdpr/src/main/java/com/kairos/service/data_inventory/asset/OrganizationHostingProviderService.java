package com.kairos.service.data_inventory.asset;
import com.kairos.commons.custom_exception.DataNotFoundByIdException;
import com.kairos.commons.custom_exception.DuplicateDataException;
import com.kairos.commons.custom_exception.InvalidRequestException;
import com.kairos.dto.gdpr.metadata.HostingProviderDTO;
import com.kairos.persistence.model.master_data.default_asset_setting.HostingProvider;
import com.kairos.persistence.model.master_data.default_asset_setting.HostingProviderMD;
import com.kairos.persistence.repository.data_inventory.asset.AssetMongoRepository;
import com.kairos.persistence.repository.master_data.asset_management.hosting_provider.HostingProviderMDRepository;
import com.kairos.persistence.repository.master_data.asset_management.hosting_provider.HostingProviderMongoRepository;
import com.kairos.response.dto.common.HostingProviderResponseDTO;
import com.kairos.response.dto.data_inventory.AssetBasicResponseDTO;
import com.kairos.service.common.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.master_data.asset_management.HostingProviderService;
import com.kairos.utils.ComparisonUtils;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.constants.AppConstant.EXISTING_DATA_LIST;
import static com.kairos.constants.AppConstant.NEW_DATA_LIST;


@Service
public class OrganizationHostingProviderService extends MongoBaseService {


    private static final Logger LOGGER = LoggerFactory.getLogger(OrganizationHostingProviderService.class);

    @Inject
    private HostingProviderMDRepository hostingProviderMDRepository;

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
            List<HostingProviderMD> existing = hostingProviderMDRepository.findByUnitIdAndDeletedAndNameIn(organizationId, false, nameInLowerCase);
            hostingProviderNames = ComparisonUtils.getNameListForMetadata(existing, hostingProviderNames);
            List<HostingProviderMD> newHostingProviders = new ArrayList<>();
            if (!hostingProviderNames.isEmpty()) {
                for (String name : hostingProviderNames) {

                    HostingProviderMD newHostingProvider = new HostingProviderMD(name);
                    newHostingProvider.setOrganizationId(organizationId);
                    newHostingProviders.add(newHostingProvider);
                }
                newHostingProviders = hostingProviderMDRepository.saveAll(newHostingProviders);
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
        return hostingProviderMDRepository.findAllByOrganizationIdAndSortByCreatedDate(organizationId);
    }


    /**
     * @param
     * @param organizationId
     * @param id
     * @return HostingProvider object fetch by id
     * @throws DataNotFoundByIdException if HostingProvider not exist for given id
     */
    public HostingProviderMD getHostingProviderById(Long organizationId, Long id) {

        HostingProviderMD exist = hostingProviderMDRepository.findByIdAndOrganizationIdAndDeleted(id, organizationId, false);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            return exist;

        }
    }


    public Boolean deleteHostingProvider(Long unitId, BigInteger hostingProviderId) {

      /*  List<AssetBasicResponseDTO> assetsLinkedWithHostingProvider = assetMongoRepository.findAllAssetLinkedWithHostingProvider(unitId, hostingProviderId);
        if (CollectionUtils.isNotEmpty(assetsLinkedWithHostingProvider)) {
            exceptionService.metaDataLinkedWithAssetException("message.metaData.linked.with.asset", "Hosting Provider", new StringBuilder(assetsLinkedWithHostingProvider.stream().map(AssetBasicResponseDTO::getName).map(String::toString).collect(Collectors.joining(","))));
        }
        hostingProviderMongoRepository.safeDeleteById(hostingProviderId)*/;
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

        HostingProviderMD hostingProvider = hostingProviderMDRepository.findByOrganizationIdAndDeletedAndName( organizationId, false, hostingProviderDTO.getName());
        if (Optional.ofNullable(hostingProvider).isPresent()) {
            if (id.equals(hostingProvider.getId())) {
                return hostingProviderDTO;
            }
            exceptionService.duplicateDataException("message.duplicate", "Hosting Provider", hostingProvider.getName());
        }
        Integer resultCount =  hostingProviderMDRepository.updateHostingProviderName(hostingProviderDTO.getName(), id, organizationId);
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

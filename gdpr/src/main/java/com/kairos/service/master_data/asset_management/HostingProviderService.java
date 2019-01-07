package com.kairos.service.master_data.asset_management;



import com.kairos.commons.custom_exception.DataNotFoundByIdException;
import com.kairos.commons.custom_exception.DuplicateDataException;
import com.kairos.commons.custom_exception.InvalidRequestException;
import com.kairos.enums.gdpr.SuggestedDataStatus;
import com.kairos.dto.gdpr.metadata.HostingProviderDTO;
import com.kairos.persistence.model.master_data.default_asset_setting.HostingProvider;
import com.kairos.persistence.model.master_data.default_asset_setting.HostingProviderMD;
import com.kairos.persistence.repository.master_data.asset_management.hosting_provider.HostingProviderMDRepository;
import com.kairos.persistence.repository.master_data.asset_management.hosting_provider.HostingProviderMongoRepository;
import com.kairos.response.dto.common.HostingProviderResponseDTO;
import com.kairos.service.common.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.utils.ComparisonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.constants.AppConstant.EXISTING_DATA_LIST;
import static com.kairos.constants.AppConstant.NEW_DATA_LIST;

@Service
public class HostingProviderService extends MongoBaseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(HostingProviderService.class);

    @Inject
    private HostingProviderMongoRepository hostingProviderMongoRepository;

    @Inject
    private ExceptionService exceptionService;

    @Inject
    private HostingProviderMDRepository hostingProviderMDRepository;


    /**
     * @param countryId
     * @param hostingProviderDTOS
     * @return return map which contain list of new HostingProvider and list of existing HostingProvider if HostingProvider already exist
     * @description this method create new HostingProvider if HostingProvider not exist with same name ,
     * and if exist then simply add  HostingProvider to existing list and return list ;
     * findMetaDataByNamesAndCountryId()  return list of existing HostingProvider using collation ,used for case insensitive result
     */
    public Map<String, List<HostingProviderMD>> createHostingProviders(Long countryId, List<HostingProviderDTO> hostingProviderDTOS) {
    //TODO still need to optimize we can get name of list in string from here
        Map<String, List<HostingProviderMD>> result = new HashMap<>();
        Set<String> hostingProviderNames = new HashSet<>();
        if (!hostingProviderDTOS.isEmpty()) {
            for (HostingProviderDTO hostingProvider : hostingProviderDTOS) {
                hostingProviderNames.add(hostingProvider.getName());
            }
            List<String> nameInLowerCase = hostingProviderNames.stream().map(String::toLowerCase)
                    .collect(Collectors.toList());

            //TODO still need to update we can return name of list from here and can apply removeAll on list
            List<HostingProviderMD> existing = hostingProviderMDRepository.findByCountryIdAndDeletedAndNameIn(countryId, false, nameInLowerCase);
            hostingProviderNames = ComparisonUtils.getNameListForMetadata(existing, hostingProviderNames);
            List<HostingProviderMD> newHostingProviders = new ArrayList<>();
            if (!hostingProviderNames.isEmpty()) {
                for (String name : hostingProviderNames) {

                    HostingProviderMD newHostingProvider = new HostingProviderMD(name, countryId, SuggestedDataStatus.APPROVED);
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
     * @param countryId
     * @param
     * @return list of HostingProvider
     */
    public List<HostingProviderResponseDTO> getAllHostingProvider(Long countryId) {
        return hostingProviderMDRepository.findAllByCountryIdAndSortByCreatedDate(countryId);
    }


    /**
     * @param countryId
     * @param
     * @param id
     * @return HostingProvider object fetch by id
     * @throws DataNotFoundByIdException if HostingProvider not exist for given id
     */
    public HostingProviderMD getHostingProviderById(Long countryId, Long id) {

        HostingProviderMD exist = hostingProviderMDRepository.findByIdAndCountryIdAndDeleted(id, countryId, false);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("No data found");
        } else {
            return exist;

        }
    }


    public Boolean deleteHostingProvider(Long countryId, Long id) {

        Integer resultCount = hostingProviderMDRepository.deleteByIdAndCountryId(id, countryId);
        if (resultCount > 0) {
            LOGGER.info("Hosting Provider deleted successfully for id :: {}", id);
        }else{
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
        //TODO What actually this code is doing?
        HostingProvider hostingProvider = hostingProviderMongoRepository.findByName(countryId, hostingProviderDTO.getName());
        if (Optional.ofNullable(hostingProvider).isPresent()) {
            if (id.equals(hostingProvider.getId())) {
                return hostingProviderDTO;
            }
            throw new DuplicateDataException("data  exist for  " + hostingProviderDTO.getName());
        }
        Integer resultCount =  hostingProviderMDRepository.updateHostingProviderName(hostingProviderDTO.getName(), id);
        if(resultCount <=0){
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Hosting Provider", id);
        }else{
            LOGGER.info("Data updated successfully for id : {} and name updated name is : {}", id, hostingProviderDTO.getName());
        }
        return hostingProviderDTO;

    }


    /**
     **
     * @param countryId
     * @param hostingProviderDTOS
     * @return
     * @description method save Hosting provider suggested by unit
     */
    public List<HostingProvider> saveSuggestedHostingProvidersFromUnit(Long countryId, List<HostingProviderDTO> hostingProviderDTOS) {

        Set<String> hostingProvidersName = new HashSet<>();
        for (HostingProviderDTO hostingProvider : hostingProviderDTOS) {
            hostingProvidersName.add(hostingProvider.getName());
        }
        List<HostingProvider> existingHostingProviders = findMetaDataByNamesAndCountryId(countryId, hostingProvidersName, HostingProvider.class);
        hostingProvidersName = ComparisonUtils.getNameListForMetadata(existingHostingProviders, hostingProvidersName);
        List<HostingProvider> hostingProviderList = new ArrayList<>();
        if (!hostingProvidersName.isEmpty()) {
            for (String name : hostingProvidersName) {

                HostingProvider hostingProvider = new HostingProvider(name);
                hostingProvider.setCountryId(countryId);
                hostingProvider.setSuggestedDataStatus(SuggestedDataStatus.PENDING);
                hostingProvider.setSuggestedDate(LocalDate.now());
                hostingProviderList.add(hostingProvider);
            }

            hostingProviderList = hostingProviderMongoRepository.saveAll(getNextSequence(hostingProviderList));
        }
        return hostingProviderList;
    }


    /**
     * @param countryId
     * @param hostingProviderIds  - ids of hosting providers
     * @param suggestedDataStatus - status to update
     * @return
     */
    public List<HostingProvider> updateSuggestedStatusOfHostingProviders(Long countryId, Set<BigInteger> hostingProviderIds, SuggestedDataStatus suggestedDataStatus) {

        List<HostingProvider> hostingProviderList = hostingProviderMongoRepository.getHostingProviderListByIds(countryId, hostingProviderIds);
        hostingProviderList.forEach(hostingProvider -> hostingProvider.setSuggestedDataStatus(suggestedDataStatus));
        hostingProviderMongoRepository.saveAll(getNextSequence(hostingProviderList));
        return hostingProviderList;
    }

}

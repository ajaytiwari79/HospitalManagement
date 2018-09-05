package com.kairos.service.master_data.asset_management;


import com.kairos.custom_exception.DataNotExists;
import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.custom_exception.InvalidRequestException;
import com.kairos.enums.SuggestedDataStatus;
import com.kairos.gdpr.metadata.HostingProviderDTO;
import com.kairos.persistance.model.master_data.default_asset_setting.HostingProvider;
import com.kairos.persistance.repository.master_data.asset_management.hosting_provider.HostingProviderMongoRepository;
import com.kairos.response.dto.common.HostingProviderResponseDTO;
import com.kairos.service.common.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.utils.ComparisonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;

import static com.kairos.constants.AppConstant.EXISTING_DATA_LIST;
import static com.kairos.constants.AppConstant.NEW_DATA_LIST;

@Service
public class HostingProviderService extends MongoBaseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(HostingProviderService.class);

    @Inject
    private HostingProviderMongoRepository hostingProviderMongoRepository;

    @Inject
    private ExceptionService exceptionService;


    /**
     * @param countryId
     * @param hostingProviderDTOS
     * @return return map which contain list of new HostingProvider and list of existing HostingProvider if HostingProvider already exist
     * @description this method create new HostingProvider if HostingProvider not exist with same name ,
     * and if exist then simply add  HostingProvider to existing list and return list ;
     * findMetaDataByNamesAndCountryId()  return list of existing HostingProvider using collation ,used for case insensitive result
     */
    public Map<String, List<HostingProvider>> createHostingProviders(Long countryId, List<HostingProviderDTO> hostingProviderDTOS) {

        Map<String, List<HostingProvider>> result = new HashMap<>();
        Set<String> hostingProviderNames = new HashSet<>();
        if (!hostingProviderDTOS.isEmpty()) {
            for (HostingProviderDTO hostingProvider : hostingProviderDTOS) {
                hostingProviderNames.add(hostingProvider.getName());
            }
            List<HostingProvider> existing = findMetaDataByNamesAndCountryId(countryId, hostingProviderNames, HostingProvider.class);
            hostingProviderNames = ComparisonUtils.getNameListForMetadata(existing, hostingProviderNames);
            List<HostingProvider> newHostingProviders = new ArrayList<>();
            if (!hostingProviderNames.isEmpty()) {
                for (String name : hostingProviderNames) {

                    HostingProvider newHostingProvider = new HostingProvider();
                    newHostingProvider.setName(name);
                    newHostingProvider.setCountryId(countryId);
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
     * @param countryId
     * @param
     * @return list of HostingProvider
     */
    public List<HostingProviderResponseDTO> getAllHostingProvider(Long countryId) {
        return hostingProviderMongoRepository.findAllHostingProviders(countryId,SuggestedDataStatus.ACCEPTED.value);
    }


    /**
     * @param countryId
     * @param
     * @param id
     * @return HostingProvider object fetch by id
     * @throws DataNotFoundByIdException if HostingProvider not exist for given id
     */
    public HostingProvider getHostingProviderById(Long countryId, BigInteger id) {

        HostingProvider exist = hostingProviderMongoRepository.findByIdAndNonDeleted(countryId, id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            return exist;

        }
    }


    public Boolean deleteHostingProvider(Long countryId, BigInteger id) {

        HostingProvider hostingProvider = hostingProviderMongoRepository.findByIdAndNonDeleted(countryId, id);
        if (!Optional.ofNullable(hostingProvider).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            delete(hostingProvider);
            return true;

        }
    }


    /**
     * @param countryId
     * @param
     * @param id                 id of HostingProvider
     * @param hostingProviderDTO
     * @return return updated HostingProvider object
     * @throws DuplicateDataException if HostingProvider exist with same name
     */
    public HostingProviderDTO updateHostingProvider(Long countryId, BigInteger id, HostingProviderDTO hostingProviderDTO) {

        HostingProvider hostingProvider = hostingProviderMongoRepository.findByName(countryId, hostingProviderDTO.getName());
        if (Optional.ofNullable(hostingProvider).isPresent()) {
            if (id.equals(hostingProvider.getId())) {
                return hostingProviderDTO;
            }
            throw new DuplicateDataException("data  exist for  " + hostingProviderDTO.getName());
        }
        hostingProvider = hostingProviderMongoRepository.findByid(id);
        if (!Optional.ofNullable(hostingProvider).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound","Hosting Provider",id);
        }
        hostingProvider.setName(hostingProviderDTO.getName());
        hostingProviderMongoRepository.save(hostingProvider);
        return hostingProviderDTO;

    }


    /**
     * @param countryId
     * @param
     * @param name      name of hosting provider
     * @return return object of hosting provider
     * @throws DataNotExists if hosting provider not exist for given name
     */
    public HostingProvider getHostingProviderByName(Long countryId, String name) {


        if (!StringUtils.isBlank(name)) {
            HostingProvider exist = hostingProviderMongoRepository.findByName(countryId, name);
            if (!Optional.ofNullable(exist).isPresent()) {
                throw new DataNotExists("data not exist for name " + name);
            }
            return exist;
        } else
            throw new InvalidRequestException("request param cannot be empty  or null");

    }



    /**
     * @description method save Hosting provider suggested by unit
     * @param countryId
     * @param hostingProviderDTOS
     * @return
     */
    public List<HostingProvider> saveSuggestedHostingProvidersFromUnit(Long countryId, List<HostingProviderDTO> hostingProviderDTOS) {

        Set<String> hostingProvidersName = new HashSet<>();
        for (HostingProviderDTO hostingProvider : hostingProviderDTOS) {
            hostingProvidersName.add(hostingProvider.getName());
        }
        List<HostingProvider> existingHostingProviders = findMetaDataByNamesAndCountryId(countryId, hostingProvidersName, HostingProvider.class);
        hostingProvidersName = ComparisonUtils.getNameListForMetadata(existingHostingProviders, hostingProvidersName);
        List<HostingProvider> hostingProviderList = new ArrayList<>();
        if (hostingProvidersName.size() != 0) {
            for (String name : hostingProvidersName) {

                HostingProvider hostingProvider = new HostingProvider(name);
                hostingProvider.setCountryId(countryId);
                hostingProvider.setSuggestedDataStatus(SuggestedDataStatus.NEW.value);
                hostingProviderList.add(hostingProvider);
            }

            hostingProviderList = hostingProviderMongoRepository.saveAll(getNextSequence(hostingProviderList));
        }
        return hostingProviderList;
    }
}

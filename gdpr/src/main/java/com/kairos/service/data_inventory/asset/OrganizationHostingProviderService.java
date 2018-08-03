package com.kairos.service.data_inventory.asset;

import com.kairos.custom_exception.DataNotExists;
import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.custom_exception.InvalidRequestException;
import com.kairos.persistance.model.master_data.default_asset_setting.HostingProvider;
import com.kairos.persistance.repository.master_data.asset_management.HostingProviderMongoRepository;
import com.kairos.response.dto.common.HostingProviderResponseDTO;
import com.kairos.service.common.MongoBaseService;
import com.kairos.utils.ComparisonUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private ComparisonUtils comparisonUtils;

    /**
     * @param organizationId
     * @param hostingProviders
     * @return return map which contain list of new HostingProvider and list of existing HostingProvider if HostingProvider already exist
     * @description this method create new HostingProvider if HostingProvider not exist with same name ,
     * and if exist then simply add  HostingProvider to existing list and return list ;
     * findByNamesAndCountryId()  return list of existing HostingProvider using collation ,used for case insensitive result
     */
    public Map<String, List<HostingProvider>> createHostingProviders(Long organizationId, List<HostingProvider> hostingProviders) {

        Map<String, List<HostingProvider>> result = new HashMap<>();
        Set<String> hostingProviderNames = new HashSet<>();
        if (!hostingProviders.isEmpty()) {
            for (HostingProvider hostingProvider : hostingProviders) {
                if (!StringUtils.isBlank(hostingProvider.getName())) {
                    hostingProviderNames.add(hostingProvider.getName());
                } else
                    throw new InvalidRequestException("name could not be empty or null");
            }
            List<HostingProvider> existing = findAllByNameAndOrganizationId(organizationId, hostingProviderNames, HostingProvider.class);
            hostingProviderNames = comparisonUtils.getNameListForMetadata(existing, hostingProviderNames);
            List<HostingProvider> newHostingProviders = new ArrayList<>();
            if (!hostingProviderNames.isEmpty()) {
                for (String name : hostingProviderNames) {

                    HostingProvider newHostingProvider = new HostingProvider(name);
                    newHostingProvider.setOrganizationId(organizationId);
                    newHostingProviders.add(newHostingProvider);

                }

                newHostingProviders = hostingProviderMongoRepository.saveAll(sequenceGenerator(newHostingProviders));
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
        return hostingProviderMongoRepository.findAllOrganizationHostingProviders(organizationId);
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


    public Boolean deleteHostingProvider(Long organizationId, BigInteger id) {

        HostingProvider exist = hostingProviderMongoRepository.findByOrganizationIdAndId(organizationId, id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            delete(exist);
            return true;

        }
    }


    /**
     * @param organizationId
     * @param id              id of HostingProvider
     * @param hostingProvider
     * @return return updated HostingProvider object
     * @throws DuplicateDataException if HostingProvider exist with same name
     */
    public HostingProvider updateHostingProvider(Long organizationId, BigInteger id, HostingProvider hostingProvider) {

        HostingProvider exist = hostingProviderMongoRepository.findByOrganizationIdAndName(organizationId, hostingProvider.getName());
        if (Optional.ofNullable(exist).isPresent()) {
            if (id.equals(exist.getId())) {
                return exist;
            }
            throw new DuplicateDataException("data  exist for  " + hostingProvider.getName());
        } else {
            exist = hostingProviderMongoRepository.findByid(id);
            exist.setName(hostingProvider.getName());
            return hostingProviderMongoRepository.save(sequenceGenerator(exist));

        }
    }


    /**
     * @param organizationId
     * @param name           name of hsoting provider
     * @return return object of hosting provider
     * @throws DataNotExists if hosting provider not exist for given name
     */
    public HostingProvider getHostingProviderByName(Long organizationId, String name) {


        if (!StringUtils.isBlank(name)) {
            HostingProvider exist = hostingProviderMongoRepository.findByOrganizationIdAndName(organizationId, name);
            if (!Optional.ofNullable(exist).isPresent()) {
                throw new DataNotExists("data not exist for name " + name);
            }
            return exist;
        } else
            throw new InvalidRequestException("request param cannot be empty  or null");

    }


}

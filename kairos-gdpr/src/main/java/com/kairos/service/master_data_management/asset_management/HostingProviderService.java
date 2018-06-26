package com.kairos.service.master_data_management.asset_management;


import com.kairos.custom_exception.DataNotExists;
import com.kairos.custom_exception.DataNotFoundByIdException;

import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.custom_exception.InvalidRequestException;
import com.kairos.persistance.model.master_data_management.asset_management.HostingProvider;
import com.kairos.persistance.repository.master_data_management.asset_management.HostingProviderMongoRepository;
import com.kairos.service.MongoBaseService;
import com.kairos.utils.userContext.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;

@Service
public class HostingProviderService extends MongoBaseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(HostingProviderService.class);

    @Inject
    private HostingProviderMongoRepository hostingProviderMongoRepository;


    public Map<String, List<HostingProvider>> createHostingProviders(Long countryId,Long organizationId, List<HostingProvider> hostingProviders) {
        Map<String, List<HostingProvider>> result = new HashMap<>();

        List<HostingProvider> newhostingProviders = new ArrayList<>();
        Set<String> names = new HashSet<>();
        if (hostingProviders.size() != 0) {
            for (HostingProvider hostingProvider : hostingProviders) {
                if (!StringUtils.isBlank(hostingProvider.getName())) {
                    names.add(hostingProvider.getName());
                } else
                    throw new InvalidRequestException("name could not be empty or null");
            }
            List<HostingProvider> existing  = hostingProviderMongoRepository.findByCountryAndNameList(countryId,organizationId, names);
            existing.forEach(item -> names.remove(item.getName()));
            if (names.size()!=0) {
                for (String name : names) {

                    HostingProvider newHostingProvider = new HostingProvider();
                    newHostingProvider.setName(name);
                    newHostingProvider.setCountryId(countryId);
                    newHostingProvider.setOrganizationId(organizationId);
                    newhostingProviders.add(newHostingProvider);

                }

                newhostingProviders = save(newhostingProviders);
            }
            result.put("existing", existing);
            result.put("new", newhostingProviders);
            return result;
        } else
            throw new InvalidRequestException("list cannot be empty");


    }

    public List<HostingProvider> getAllHostingProvider(Long countryId,Long organizationId) {
        return hostingProviderMongoRepository.findAllHostingProviders(countryId,organizationId);
    }


    public HostingProvider getHostingProviderById(Long countryId,Long organizationId, BigInteger id) {

        HostingProvider exist = hostingProviderMongoRepository.findByIdAndNonDeleted(countryId,organizationId,id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            return exist;

        }
    }


    public Boolean deleteHostingProvider(Long countryId,Long organizationId,BigInteger id) {

        HostingProvider exist = hostingProviderMongoRepository.findByIdAndNonDeleted(countryId,organizationId,id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            exist.setDeleted(true);
            save(exist);
            return true;

        }
    }


    public HostingProvider updateHostingProvider(Long countryId,Long organizationId,BigInteger id, HostingProvider hostingProvider) {

        HostingProvider exist = hostingProviderMongoRepository.findByName(countryId,organizationId,hostingProvider.getName());
        if (Optional.ofNullable(exist).isPresent() ) {
            if (id.equals(exist.getId())) {
                return exist;
            }
            throw new DuplicateDataException("data  exist for  "+hostingProvider.getName());
        } else {
            exist=hostingProviderMongoRepository.findByid(id);
            exist.setName(hostingProvider.getName());
            return save(exist);

        }
    }


    public HostingProvider getHostingProviderByName(Long countryId, Long organizationId,String name) {


        if (!StringUtils.isBlank(name)) {
            HostingProvider exist = hostingProviderMongoRepository.findByName(countryId,organizationId,name);
            if (!Optional.ofNullable(exist).isPresent()) {
                throw new DataNotExists("data not exist for name " + name);
            }
            return exist;
        } else
            throw new InvalidRequestException("request param cannot be empty  or null");

    }


}

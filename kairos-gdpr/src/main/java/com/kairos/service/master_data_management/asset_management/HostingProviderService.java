package com.kairos.service.master_data_management.asset_management;


import com.kairos.custome_exception.DataNotExists;
import com.kairos.custome_exception.DataNotFoundByIdException;

import com.kairos.custome_exception.InvalidRequestException;
import com.kairos.persistance.model.master_data_management.asset_management.HostingProvider;
import com.kairos.persistance.repository.master_data_management.asset_management.HostingProviderMongoRepository;
import com.kairos.service.MongoBaseService;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;

@Service
public class HostingProviderService extends MongoBaseService {


    @Inject
    private HostingProviderMongoRepository hostingProviderMongoRepository;


    public Map<String, List<HostingProvider>> createHostingProviders(List<HostingProvider> hostingProviders) {
        Map<String, List<HostingProvider>> result = new HashMap<>();
        List<HostingProvider> existing = new ArrayList<>();
        List<HostingProvider> newhostingProviders = new ArrayList<>();
        if (hostingProviders.size() != 0) {
            for (HostingProvider hostingProvider : hostingProviders) {
                if (!StringUtils.isBlank(hostingProvider.getName())) {
                    HostingProvider exist = hostingProviderMongoRepository.findByName(hostingProvider.getName());
                    if (Optional.ofNullable(exist).isPresent()) {
                        existing.add(exist);

                    } else {
                        HostingProvider newHostingProvider = new HostingProvider();
                        newHostingProvider.setName(hostingProvider.getName());
                        newhostingProviders.add(save(newHostingProvider));
                    }
                } else
                    throw new InvalidRequestException("name could not be empty or null");
            }

            result.put("existing", existing);
            result.put("new", newhostingProviders);
            return result;
        } else
            throw new InvalidRequestException("list cannot be empty");


    }

    public List<HostingProvider> getAllHostingProvider() {
       return hostingProviderMongoRepository.findAllHostingProviders();
           }


    public HostingProvider getHostingProviderById(BigInteger id) {

        HostingProvider exist = hostingProviderMongoRepository.findByIdAndNonDeleted(id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            return exist;

        }
    }


    public Boolean deleteHostingProvider(BigInteger id) {

        HostingProvider exist = hostingProviderMongoRepository.findByIdAndNonDeleted(id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            exist.setDeleted(true);
            save(exist);
            return true;

        }
    }


    public HostingProvider updateHostingProvider(BigInteger id, HostingProvider hostingProvider) {

        HostingProvider exist = hostingProviderMongoRepository.findByIdAndNonDeleted(id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            exist.setName(hostingProvider.getName());

            return save(exist);

        }
    }


    public HostingProvider getHostingProviderByName(String name) {


        if (!StringUtils.isBlank(name)) {
            HostingProvider exist = hostingProviderMongoRepository.findByName(name);
            if (!Optional.ofNullable(exist).isPresent()) {
                throw new DataNotExists("data not exist for name " + name);
            }
            return exist;
        } else
            throw new InvalidRequestException("request param cannot be empty  or null");

    }


}

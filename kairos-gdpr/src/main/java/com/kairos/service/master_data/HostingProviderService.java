package com.kairos.service.master_data;


import com.kairos.custome_exception.DataNotExists;
import com.kairos.custome_exception.DataNotFoundByIdException;
import com.kairos.custome_exception.DuplicateDataException;

import com.kairos.custome_exception.InvalidRequestException;
import com.kairos.persistance.model.master_data.HostingProvider;
import com.kairos.persistance.repository.master_data.HostingProviderMongoRepository;
import com.kairos.service.MongoBaseService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Service
public class HostingProviderService extends MongoBaseService {


    @Inject
    private HostingProviderMongoRepository hostingProviderMongoRepository;


    public HostingProvider createHostingProvider(String hostingProvider) {
        if (StringUtils.isEmpty(hostingProvider)) {
            throw new InvalidRequestException("requested hostingProvider is null");

        }
        HostingProvider exist = hostingProviderMongoRepository.findByName(hostingProvider);
        if (Optional.ofNullable(exist).isPresent()) {
            throw new DuplicateDataException("data already exist for  " + hostingProvider);
        } else {
            HostingProvider newHostingProvider = new HostingProvider();
            newHostingProvider.setName(hostingProvider);
            return save(newHostingProvider);
        }
    }


    public List<HostingProvider> getAllHostingProvider() {
        List<HostingProvider> result = hostingProviderMongoRepository.findAll();
        if (result.size() != 0) {
            return result;

        } else
            throw new DataNotExists("HostingProvider not exist please create purpose ");
    }


    public HostingProvider getHostingProviderById(BigInteger id) {

        HostingProvider exist = hostingProviderMongoRepository.findByid(id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            return exist;

        }
    }


    public Boolean deleteHostingProviderById(BigInteger id) {

        HostingProvider exist = hostingProviderMongoRepository.findByid(id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            hostingProviderMongoRepository.delete(exist);
            return true;

        }
    }


    public HostingProvider updateHostingProvider(BigInteger id, String hostingProvider) {

        if (StringUtils.isEmpty(hostingProvider)) {
            throw new InvalidRequestException("requested hostingProvider is null");

        }
        HostingProvider exist = hostingProviderMongoRepository.findByid(id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            exist.setName(hostingProvider);
            return save(exist);

        }
    }


}

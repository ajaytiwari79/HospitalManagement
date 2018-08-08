package com.kairos.service.master_data.asset_management;


import com.kairos.custom_exception.DataNotExists;
import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.custom_exception.InvalidRequestException;
import com.kairos.persistance.model.master_data.default_asset_setting.HostingProvider;
import com.kairos.persistance.repository.master_data.asset_management.hosting_provider.HostingProviderMongoRepository;
import com.kairos.response.dto.common.HostingProviderResponseDTO;
import com.kairos.service.common.MongoBaseService;
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


    /**
     * @description this method create new HostingProvider if HostingProvider not exist with same name ,
     * and if exist then simply add  HostingProvider to existing list and return list ;
     * findByNamesAndCountryId()  return list of existing HostingProvider using collation ,used for case insensitive result
     * @param countryId
     * @param hostingProviders
     * @return return map which contain list of new HostingProvider and list of existing HostingProvider if HostingProvider already exist
     *
     */
    public Map<String, List<HostingProvider>> createHostingProviders(Long countryId, List<HostingProvider> hostingProviders) {

        Map<String, List<HostingProvider>> result = new HashMap<>();
        Set<String> hostingProviderNames = new HashSet<>();
        if (hostingProviders.size() != 0) {
            for (HostingProvider hostingProvider : hostingProviders) {
                if (!StringUtils.isBlank(hostingProvider.getName())) {
                    hostingProviderNames.add(hostingProvider.getName());
                } else
                    throw new InvalidRequestException("name could not be empty or null");
            }
            List<HostingProvider> existing  = findByNamesAndCountryId(countryId,hostingProviderNames,HostingProvider.class);
            hostingProviderNames = ComparisonUtils.getNameListForMetadata(existing, hostingProviderNames);
            List<HostingProvider> newHostingProviders = new ArrayList<>();
            if (hostingProviderNames.size()!=0) {
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
     *
     * @param countryId
     * @param 
     * @return list of HostingProvider
     */
    public List<HostingProvider> getAllHostingProvider(Long countryId) {
        return hostingProviderMongoRepository.findAllHostingProviders(countryId);
    }


    /**
     * @throws DataNotFoundByIdException if HostingProvider not exist for given id
     * @param countryId
     * @param 
     * @param id
     * @return HostingProvider object fetch by id
     */
    public HostingProvider getHostingProviderById(Long countryId, BigInteger id) {

        HostingProvider exist = hostingProviderMongoRepository.findByIdAndNonDeleted(countryId,id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            return exist;

        }
    }


    public Boolean deleteHostingProvider(Long countryId,BigInteger id) {

        HostingProvider exist = hostingProviderMongoRepository.findByIdAndNonDeleted(countryId,id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            delete(exist);
            return true;

        }
    }


    /**
     * @throws DuplicateDataException if HostingProvider exist with same name
     * @param countryId
     * @param 
     * @param id id of HostingProvider
     * @param hostingProvider
     * @return return updated HostingProvider object
     */
    public HostingProvider updateHostingProvider(Long countryId,BigInteger id, HostingProvider hostingProvider) {

        HostingProvider exist = hostingProviderMongoRepository.findByName(countryId,hostingProvider.getName());
        if (Optional.ofNullable(exist).isPresent() ) {
            if (id.equals(exist.getId())) {
                return exist;
            }
            throw new DuplicateDataException("data  exist for  "+hostingProvider.getName());
        } else {
            exist=hostingProviderMongoRepository.findByid(id);
            exist.setName(hostingProvider.getName());
            return hostingProviderMongoRepository.save(getNextSequence(exist));

        }
    }


    /**
     * @throws DataNotExists if hosting provider not exist for given name
     * @param countryId
     * @param 
     * @param name name of hosting provider
     * @return return object of hosting provider
     */
    public HostingProvider getHostingProviderByName(Long countryId,String name) {


        if (!StringUtils.isBlank(name)) {
            HostingProvider exist = hostingProviderMongoRepository.findByName(countryId,name);
            if (!Optional.ofNullable(exist).isPresent()) {
                throw new DataNotExists("data not exist for name " + name);
            }
            return exist;
        } else
            throw new InvalidRequestException("request param cannot be empty  or null");

    }


    public List<HostingProviderResponseDTO> getAllNotInheritedHostingProviderFromParentOrgAndUnitHostingProvider(Long countryId, Long parentOrganizationId, Long unitId){

        return hostingProviderMongoRepository.getAllNotInheritedHostingProviderFromParentOrgAndUnitHostingProvider(countryId,parentOrganizationId,unitId);
    }






}

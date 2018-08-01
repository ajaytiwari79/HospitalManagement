package com.kairos.service.master_data.asset_management;


import com.kairos.custom_exception.DataNotExists;
import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.custom_exception.InvalidRequestException;
import com.kairos.persistance.model.master_data.default_asset_setting.HostingType;
import com.kairos.persistance.repository.master_data.asset_management.hosting_type.HostingTypeMongoRepository;
import com.kairos.response.dto.metadata.HostingTypeResponseDTO;
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
public class HostingTypeService extends MongoBaseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(HostingTypeService.class);

    @Inject
    private HostingTypeMongoRepository hostingTypeMongoRepository;

    @Inject
    private ComparisonUtils comparisonUtils;


    /**
     * @description this method create new HostingType if HostingType not exist with same name ,
     * and if exist then simply add  HostingType to existing list and return list ;
     * findByNamesList()  return list of existing HostingType using collation ,used for case insensitive result
     * @param countryId
     * @param organizationId
     * @param hostingTypes
     * @return return map which contain list of new HostingType and list of existing HostingType if HostingType already exist
     *
     */
    public Map<String, List<HostingType>> createHostingType(Long countryId,Long organizationId,List<HostingType> hostingTypes) {

        Map<String, List<HostingType>> result = new HashMap<>();
        Set<String> hostingTypeNames=new HashSet<>();
        if (hostingTypes.size() != 0) {
            for (HostingType hostingType : hostingTypes) {
                if (!StringUtils.isBlank(hostingType.getName())) {
                    hostingTypeNames.add(hostingType.getName());
                } else
                    throw new InvalidRequestException("name could not be empty or null");
            }
            List<HostingType> existing =  findByNamesList(countryId,organizationId,hostingTypeNames,HostingType.class);
            hostingTypeNames = comparisonUtils.getNameListForMetadata(existing, hostingTypeNames);
            List<HostingType> newHostingTypes = new ArrayList<>();
            if (hostingTypeNames.size()!=0) {
                for (String name : hostingTypeNames) {
                    HostingType newHostingType = new HostingType();
                    newHostingType.setName(name);
                    newHostingType.setCountryId(countryId);
                    newHostingType.setOrganizationId(organizationId);
                    newHostingTypes.add(newHostingType);
                }
                newHostingTypes = hostingTypeMongoRepository.saveAll(getNextSequence(newHostingTypes));
            }
            result.put(EXISTING_DATA_LIST, existing);
            result.put(NEW_DATA_LIST, newHostingTypes);
            return result;
        } else
            throw new InvalidRequestException("list cannot be empty");


    }


    /**
     *
     * @param countryId
     * @param organizationId
     * @return list of HostingType
     */
    public List<HostingType> getAllHostingType(Long countryId,Long organizationId) {
       return hostingTypeMongoRepository.findAllHostingTypes(countryId,organizationId);
          }


    /**
     * @throws DataNotFoundByIdException throw exception if HostingType not found for given id
     * @param countryId
     * @param organizationId
     * @param id of HostingType
     * @return HostingType object fetch by given id
     */
    public HostingType getHostingType(Long countryId,Long organizationId,BigInteger id) {

        HostingType exist = hostingTypeMongoRepository.findByIdAndNonDeleted(countryId,organizationId,id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            return exist;

        }
    }


    public Boolean deleteHostingType(Long countryId,Long organizationId,BigInteger id) {

        HostingType exist = hostingTypeMongoRepository.findByIdAndNonDeleted(countryId,organizationId,id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            delete(exist);
            return true;

        }
    }


    /**
     * @throws DuplicateDataException  if HostingType already exist with same name
     * @param countryId
     * @param organizationId
     * @param id id of HostingType
     * @param hostingType
     * @return HostingType updated object
     */
    public HostingType updateHostingType(Long countryId,Long organizationId,BigInteger id, HostingType hostingType) {


        HostingType exist = hostingTypeMongoRepository.findByName(countryId,organizationId,hostingType.getName());
        if (Optional.ofNullable(exist).isPresent() ) {
            if (id.equals(exist.getId())) {
                return exist;
            }
            throw new DuplicateDataException("data  exist for  "+hostingType.getName());
        } else {
            exist=hostingTypeMongoRepository.findByid(id);
            exist.setName(hostingType.getName());
            return hostingTypeMongoRepository.save(getNextSequence(exist));

        }
    }


    /**
     * @throws DataNotExists if HostingType not exist for given name
     * @param countryId
     * @param organizationId
     * @param name  name of HostingType
     * @return HostingType object fetch on the basis of name
     */
    public HostingType getHostingTypeByName(Long countryId,Long organizationId,String name) {


        if (!StringUtils.isBlank(name)) {
            HostingType exist = hostingTypeMongoRepository.findByName(countryId,organizationId,name);
            if (!Optional.ofNullable(exist).isPresent()) {
                throw new DataNotExists("data not exist for name " + name);
            }
            return exist;
        } else
            throw new InvalidRequestException("request param cannot be empty  or null");

    }



    public List<HostingTypeResponseDTO> getAllNotInheritedHostingTypeFromParentOrgAndUnitHostingType(Long countryId, Long parentOrganizationId, Long unitId){

        return hostingTypeMongoRepository.getAllNotInheritedHostingTypeFromParentOrgAndUnitHostingType(countryId,parentOrganizationId,unitId);
    }


}

    
    
    


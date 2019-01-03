package com.kairos.service.master_data.asset_management;


import com.kairos.commons.custom_exception.DataNotFoundByIdException;
import com.kairos.commons.custom_exception.DuplicateDataException;
import com.kairos.commons.custom_exception.InvalidRequestException;
import com.kairos.enums.gdpr.SuggestedDataStatus;
import com.kairos.dto.gdpr.metadata.HostingTypeDTO;
import com.kairos.persistence.model.master_data.default_asset_setting.HostingType;
import com.kairos.persistence.model.master_data.default_asset_setting.HostingTypeMD;
import com.kairos.persistence.repository.master_data.asset_management.hosting_provider.HostingProviderMDRepository;
import com.kairos.persistence.repository.master_data.asset_management.hosting_type.HostingTypeMDRepository;
import com.kairos.persistence.repository.master_data.asset_management.hosting_type.HostingTypeMongoRepository;
import com.kairos.response.dto.common.HostingTypeResponseDTO;
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
public class HostingTypeService extends MongoBaseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(HostingTypeService.class);

    @Inject
    private HostingTypeMongoRepository hostingTypeMongoRepository;

    @Inject
    private ExceptionService exceptionService;

    @Inject
    private HostingTypeMDRepository hostingTypeMDRepository;


    /**
     * @param countryId
     * @param
     * @param hostingTypeDTOS
     * @return return map which contain list of new HostingType and list of existing HostingType if HostingType already exist
     * @description this method create new HostingType if HostingType not exist with same name ,
     * and if exist then simply add  HostingType to existing list and return list ;
     * findMetaDataByNamesAndCountryId()  return list of existing HostingType using collation ,used for case insensitive result
     */
    public Map<String, List<HostingTypeMD>> createHostingType(Long countryId, List<HostingTypeDTO> hostingTypeDTOS) {
    //TODO still need to optimize we can get name of list in string from here
        Map<String, List<HostingTypeMD>> result = new HashMap<>();
        Set<String> hostingTypeNames = new HashSet<>();
        if (!hostingTypeDTOS.isEmpty()) {
            for (HostingTypeDTO hostingType : hostingTypeDTOS) {
                hostingTypeNames.add(hostingType.getName());
            }
            List<String> nameInLowerCase = hostingTypeNames.stream().map(String::toLowerCase)
                    .collect(Collectors.toList());

            //TODO still need to update we can return name of list from here and can apply removeAll on list
            List<HostingTypeMD> existing = hostingTypeMDRepository.findByCountryIdAndDeletedAndNameIn(countryId, false, nameInLowerCase);
            hostingTypeNames = ComparisonUtils.getNameListForMetadata(existing, hostingTypeNames);
            List<HostingTypeMD> newHostingTypes = new ArrayList<>();
            if (!hostingTypeNames.isEmpty()) {
                for (String name : hostingTypeNames) {
                    HostingTypeMD newHostingType = new HostingTypeMD(name,countryId,SuggestedDataStatus.APPROVED);
                    newHostingTypes.add(newHostingType);
                }
                newHostingTypes = hostingTypeMDRepository.saveAll(newHostingTypes);
            }
            result.put(EXISTING_DATA_LIST, existing);
            result.put(NEW_DATA_LIST, newHostingTypes);
            return result;
        } else
            throw new InvalidRequestException("list cannot be empty");


    }


    /**
     * @param countryId
     * @param
     * @return list of HostingType
     */
    public List<HostingTypeResponseDTO> getAllHostingType(Long countryId) {
        return hostingTypeMDRepository.findAllByCountryIdAndSortByCreatedDate(countryId);
    }


    /**
     * @param countryId
     * @param
     * @param id        of HostingType
     * @return HostingType object fetch by given id
     * @throws DataNotFoundByIdException throw exception if HostingType not found for given id
     */
    public HostingTypeMD getHostingType(Long countryId, Integer id) {

        HostingTypeMD exist = hostingTypeMDRepository.findByIdAndCountryIdAndDeleted(id, countryId, false);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("No data found");
        } else {
            return exist;

        }
    }


    public Boolean deleteHostingType(Long countryId, Integer id) {

        Integer resultCount = hostingTypeMDRepository.deleteByIdAndCountryId(id, countryId);
        if (resultCount > 0) {
            LOGGER.info("Hosting Type deleted successfully for id :: {}", id);
        }else{
            throw new DataNotFoundByIdException("No data found");
        }
        return true;
    }


    /**
     * @param countryId
     * @param
     * @param id             id of HostingType
     * @param hostingTypeDTO
     * @return HostingType updated object
     * @throws DuplicateDataException if HostingType already exist with same name
     */
    public HostingTypeDTO updateHostingType(Long countryId, Integer id, HostingTypeDTO hostingTypeDTO) {

        //TODO What actually this code is doing?
        HostingType hostingType = hostingTypeMongoRepository.findByName(countryId, hostingTypeDTO.getName());
        if (Optional.ofNullable(hostingType).isPresent()) {
            if (id.equals(hostingType.getId())) {
                return hostingTypeDTO;
            }
            throw new DuplicateDataException("data  exist for  " + hostingTypeDTO.getName());
        }
        Integer resultCount =  hostingTypeMDRepository.updateHostingTypeName(hostingTypeDTO.getName(), id);
        if(resultCount <=0){
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Hosting Type", id);
        }else{
            LOGGER.info("Data updated successfully for id : {} and name updated name is : {}", id, hostingTypeDTO.getName());
        }
        return hostingTypeDTO;


    }


    /**
     * @param countryId
     * @param hostingTypeDTOS
     * @return
     * @description method save Hosting type suggested by unit
     */
    public List<HostingType> saveSuggestedHostingTypesFromUnit(Long countryId, List<HostingTypeDTO> hostingTypeDTOS) {

        Set<String> hostingTypeNameList = new HashSet<>();
        for (HostingTypeDTO HostingType : hostingTypeDTOS) {
            hostingTypeNameList.add(HostingType.getName());
        }
        List<HostingType> existingHostingTypes = findMetaDataByNamesAndCountryId(countryId, hostingTypeNameList, HostingType.class);
        hostingTypeNameList = ComparisonUtils.getNameListForMetadata(existingHostingTypes, hostingTypeNameList);
        List<HostingType> hostingTypeList = new ArrayList<>();
        if (!hostingTypeNameList.isEmpty()) {
            for (String name : hostingTypeNameList) {

                HostingType hostingType = new HostingType(name);
                hostingType.setCountryId(countryId);
                hostingType.setSuggestedDataStatus(SuggestedDataStatus.PENDING);
                hostingType.setSuggestedDate(LocalDate.now());
                hostingTypeList.add(hostingType);
            }

            hostingTypeMongoRepository.saveAll(getNextSequence(hostingTypeList));
        }
        return hostingTypeList;
    }


    /**
     *
     * @param countryId
     * @param hostingTypeIds
     * @param suggestedDataStatus
     * @return
     */
    public List<HostingType> updateSuggestedStatusOfHostingTypes(Long countryId, Set<BigInteger> hostingTypeIds, SuggestedDataStatus suggestedDataStatus) {

        List<HostingType> hostingTypeList = hostingTypeMongoRepository.getHostingTypeListByIds(countryId, hostingTypeIds);
        hostingTypeList.forEach(hostingType -> hostingType.setSuggestedDataStatus(suggestedDataStatus));
        hostingTypeMongoRepository.saveAll(getNextSequence(hostingTypeList));
        return hostingTypeList;
    }


}

    
    
    


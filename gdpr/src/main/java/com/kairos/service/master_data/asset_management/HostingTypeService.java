package com.kairos.service.master_data.asset_management;


import com.kairos.custom_exception.DataNotExists;
import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.custom_exception.InvalidRequestException;
import com.kairos.enums.SuggestedDataStatus;
import com.kairos.dto.gdpr.metadata.HostingTypeDTO;
import com.kairos.persistance.model.master_data.default_asset_setting.HostingType;
import com.kairos.persistance.repository.master_data.asset_management.hosting_type.HostingTypeMongoRepository;
import com.kairos.response.dto.common.HostingTypeResponseDTO;
import com.kairos.service.common.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.utils.ComparisonUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;

import static com.kairos.constants.AppConstant.EXISTING_DATA_LIST;
import static com.kairos.constants.AppConstant.NEW_DATA_LIST;

@Service
public class HostingTypeService extends MongoBaseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(HostingTypeService.class);

    @Inject
    private HostingTypeMongoRepository hostingTypeMongoRepository;

    @Inject
    private ExceptionService exceptionService;


    /**
     * @param countryId
     * @param
     * @param hostingTypeDTOS
     * @return return map which contain list of new HostingType and list of existing HostingType if HostingType already exist
     * @description this method create new HostingType if HostingType not exist with same name ,
     * and if exist then simply add  HostingType to existing list and return list ;
     * findMetaDataByNamesAndCountryId()  return list of existing HostingType using collation ,used for case insensitive result
     */
    public Map<String, List<HostingType>> createHostingType(Long countryId, List<HostingTypeDTO> hostingTypeDTOS) {

        Map<String, List<HostingType>> result = new HashMap<>();
        Set<String> hostingTypeNames = new HashSet<>();
        if (!hostingTypeDTOS.isEmpty()) {
            for (HostingTypeDTO hostingType : hostingTypeDTOS) {
                hostingTypeNames.add(hostingType.getName());
            }
            List<HostingType> existing = findMetaDataByNamesAndCountryId(countryId, hostingTypeNames, HostingType.class);
            hostingTypeNames = ComparisonUtils.getNameListForMetadata(existing, hostingTypeNames);
            List<HostingType> newHostingTypes = new ArrayList<>();
            if (!hostingTypeNames.isEmpty()) {
                for (String name : hostingTypeNames) {
                    HostingType newHostingType = new HostingType(name,countryId,SuggestedDataStatus.APPROVED);
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
     * @param countryId
     * @param
     * @return list of HostingType
     */
    public List<HostingTypeResponseDTO> getAllHostingType(Long countryId) {
        return hostingTypeMongoRepository.findAllHostingTypes(countryId);
    }


    /**
     * @param countryId
     * @param
     * @param id        of HostingType
     * @return HostingType object fetch by given id
     * @throws DataNotFoundByIdException throw exception if HostingType not found for given id
     */
    public HostingType getHostingType(Long countryId, BigInteger id) {

        HostingType exist = hostingTypeMongoRepository.findByIdAndNonDeleted(countryId, id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            return exist;

        }
    }


    public Boolean deleteHostingType(Long countryId, BigInteger id) {

        HostingType hostingType = hostingTypeMongoRepository.findByIdAndNonDeleted(countryId, id);
        if (!Optional.ofNullable(hostingType).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            delete(hostingType);
            return true;

        }
    }


    /**
     * @param countryId
     * @param
     * @param id             id of HostingType
     * @param hostingTypeDTO
     * @return HostingType updated object
     * @throws DuplicateDataException if HostingType already exist with same name
     */
    public HostingTypeDTO updateHostingType(Long countryId, BigInteger id, HostingTypeDTO hostingTypeDTO) {


        HostingType hostingType = hostingTypeMongoRepository.findByName(countryId, hostingTypeDTO.getName());
        if (Optional.ofNullable(hostingType).isPresent()) {
            if (id.equals(hostingType.getId())) {
                return hostingTypeDTO;
            }
            throw new DuplicateDataException("data  exist for  " + hostingTypeDTO.getName());
        }
        hostingType = hostingTypeMongoRepository.findByid(id);
        if (!Optional.ofNullable(hostingType).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Hosting type", id);
        }
        hostingType.setName(hostingTypeDTO.getName());
        hostingTypeMongoRepository.save(hostingType);
        return hostingTypeDTO;


    }


    /**
     * @param countryId
     * @param
     * @param name      name of HostingType
     * @return HostingType object fetch on the basis of name
     * @throws DataNotExists if HostingType not exist for given name
     */
    public HostingType getHostingTypeByName(Long countryId, String name) {


        if (!StringUtils.isBlank(name)) {
            HostingType exist = hostingTypeMongoRepository.findByName(countryId, name);
            if (!Optional.ofNullable(exist).isPresent()) {
                throw new DataNotExists("data not exist for name " + name);
            }
            return exist;
        } else
            throw new InvalidRequestException("request param cannot be empty  or null");

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

    
    
    


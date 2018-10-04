package com.kairos.service.master_data.processing_activity_masterdata;


import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.custom_exception.InvalidRequestException;
import com.kairos.enums.gdpr.SuggestedDataStatus;
import com.kairos.dto.gdpr.metadata.ResponsibilityTypeDTO;
import com.kairos.persistence.model.master_data.default_proc_activity_setting.ResponsibilityType;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.responsibility_type.ResponsibilityTypeMongoRepository;
import com.kairos.response.dto.common.ResponsibilityTypeResponseDTO;
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

import static com.kairos.constants.AppConstant.EXISTING_DATA_LIST;
import static com.kairos.constants.AppConstant.NEW_DATA_LIST;


@Service
public class ResponsibilityTypeService extends MongoBaseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResponsibilityTypeService.class);

    @Inject
    private ResponsibilityTypeMongoRepository responsibilityTypeMongoRepository;

    @Inject
    private ExceptionService exceptionService;


    /**
     * @param countryId
     * @param
     * @param responsibilityTypeDTOS
     * @return return map which contain list of new ResponsibilityType and list of existing ResponsibilityType if ResponsibilityType already exist
     * @description this method create new ResponsibilityType if ResponsibilityType not exist with same name ,
     * and if exist then simply add  ResponsibilityType to existing list and return list ;
     * findByNamesList()  return list of existing ResponsibilityType using collation ,used for case insensitive result
     */
    public Map<String, List<ResponsibilityType>> createResponsibilityType(Long countryId, List<ResponsibilityTypeDTO> responsibilityTypeDTOS) {

        Map<String, List<ResponsibilityType>> result = new HashMap<>();
        Set<String> responsibilityTypeNames = new HashSet<>();
        if (!responsibilityTypeDTOS.isEmpty()) {
            for (ResponsibilityTypeDTO responsibilityType : responsibilityTypeDTOS) {

                responsibilityTypeNames.add(responsibilityType.getName());
            }
            List<ResponsibilityType> existing = findMetaDataByNamesAndCountryId(countryId, responsibilityTypeNames, ResponsibilityType.class);
            responsibilityTypeNames = ComparisonUtils.getNameListForMetadata(existing, responsibilityTypeNames);

            List<ResponsibilityType> newResponsibilityTypes = new ArrayList<>();
            if (!responsibilityTypeNames.isEmpty()) {
                for (String name : responsibilityTypeNames) {

                    ResponsibilityType newResponsibilityType = new ResponsibilityType(name,countryId,SuggestedDataStatus.APPROVED);
                    newResponsibilityTypes.add(newResponsibilityType);

                }

                newResponsibilityTypes = responsibilityTypeMongoRepository.saveAll(getNextSequence(newResponsibilityTypes));

            }
            result.put(EXISTING_DATA_LIST, existing);
            result.put(NEW_DATA_LIST, newResponsibilityTypes);
            return result;
        } else
            throw new InvalidRequestException("list cannot be empty");


    }

    /**
     * @param countryId
     * @param
     * @return list of ResponsibilityType
     */
    public List<ResponsibilityTypeResponseDTO> getAllResponsibilityType(Long countryId) {
        return responsibilityTypeMongoRepository.findAllByCountryIdSortByCreatedDate(countryId,new Sort(Sort.Direction.DESC, "createdAt"));
    }

    /**
     * @param id id of ResponsibilityType
     * @return ResponsibilityType object fetch by given id
     * @throws DataNotFoundByIdException throw exception if ResponsibilityType not found for given id
     */
    public ResponsibilityType getResponsibilityType(Long countryId, BigInteger id) {

        ResponsibilityType exist = responsibilityTypeMongoRepository.findByIdAndNonDeleted(countryId, id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            return exist;

        }
    }


    public Boolean deleteResponsibilityType(Long countryId, BigInteger id) {
        ResponsibilityType responsibilityType = responsibilityTypeMongoRepository.findByIdAndNonDeleted(countryId, id);
        if (!Optional.ofNullable(responsibilityType).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        }
        delete(responsibilityType);
        return true;
    }


    /***
     * @throws DuplicateDataException throw exception if ResponsibilityType data not exist for given id
     * @param countryId
     * @param
     * @param id id of ResponsibilityType
     * @param responsibilityTypeDTO
     * @return ResponsibilityType updated object
     */

    public ResponsibilityTypeDTO updateResponsibilityType(Long countryId, BigInteger id, ResponsibilityTypeDTO responsibilityTypeDTO) {


        ResponsibilityType responsibilityType = responsibilityTypeMongoRepository.findByName(countryId, responsibilityTypeDTO.getName());
        if (Optional.ofNullable(responsibilityType).isPresent()) {
            if (id.equals(responsibilityType.getId())) {
                return responsibilityTypeDTO;
            }
            throw new DuplicateDataException("data  exist for  " + responsibilityTypeDTO.getName());
        }
        responsibilityType = responsibilityTypeMongoRepository.findByid(id);
        if (!Optional.ofNullable(responsibilityType).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Responsibility Type", id);

        }
        responsibilityType.setName(responsibilityTypeDTO.getName());
        responsibilityTypeMongoRepository.save(responsibilityType);
        return responsibilityTypeDTO;


    }


    /**
     * @description method save ResponsibilityType suggested by unit
     * @param countryId
     * @param responsibilityTypeDTOS - Responsibility type suggested by unit
     * @return
     */
    public List<ResponsibilityType> saveSuggestedResponsibilityTypesFromUnit(Long countryId, List<ResponsibilityTypeDTO> responsibilityTypeDTOS) {

        Set<String> responsibilityTypeNameList = new HashSet<>();
        for (ResponsibilityTypeDTO ResponsibilityType : responsibilityTypeDTOS) {
            responsibilityTypeNameList.add(ResponsibilityType.getName());
        }
        List<ResponsibilityType> existingResponsibilityTypes = findMetaDataByNamesAndCountryId(countryId, responsibilityTypeNameList, ResponsibilityType.class);
        responsibilityTypeNameList = ComparisonUtils.getNameListForMetadata(existingResponsibilityTypes, responsibilityTypeNameList);
        List<ResponsibilityType> responsibilityTypeList = new ArrayList<>();
        if (!responsibilityTypeNameList.isEmpty()) {
            for (String name : responsibilityTypeNameList) {

                ResponsibilityType responsibilityType = new ResponsibilityType(name);
                responsibilityType.setCountryId(countryId);
                responsibilityType.setSuggestedDataStatus(SuggestedDataStatus.PENDING);
                responsibilityType.setSuggestedDate(LocalDate.now());
                responsibilityTypeList.add(responsibilityType);
            }

            responsibilityTypeMongoRepository.saveAll(getNextSequence(responsibilityTypeList));
        }
        return responsibilityTypeList;
    }


    /**
     *
     * @param countryId
     * @param responsibilityTypeIds
     * @param suggestedDataStatus
     * @return
     */
    public List<ResponsibilityType> updateSuggestedStatusOfResponsibilityTypeList(Long countryId, Set<BigInteger> responsibilityTypeIds , SuggestedDataStatus suggestedDataStatus) {

        List<ResponsibilityType> responsibilityTypes = responsibilityTypeMongoRepository.getResponsibilityTypeListByIds(countryId, responsibilityTypeIds);
        responsibilityTypes.forEach(responsibilityType-> responsibilityType.setSuggestedDataStatus(suggestedDataStatus));
        responsibilityTypeMongoRepository.saveAll(getNextSequence(responsibilityTypes));
        return responsibilityTypes;
    }

}

    
    
    


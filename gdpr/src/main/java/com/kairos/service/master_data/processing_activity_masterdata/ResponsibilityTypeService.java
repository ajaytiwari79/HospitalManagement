package com.kairos.service.master_data.processing_activity_masterdata;

import com.kairos.commons.custom_exception.DataNotFoundByIdException;
import com.kairos.commons.custom_exception.DuplicateDataException;
import com.kairos.commons.custom_exception.InvalidRequestException;
import com.kairos.enums.gdpr.SuggestedDataStatus;
import com.kairos.dto.gdpr.metadata.ResponsibilityTypeDTO;
import com.kairos.persistence.model.master_data.default_proc_activity_setting.ResponsibilityType;
import com.kairos.persistence.model.master_data.default_proc_activity_setting.ResponsibilityTypeMD;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.responsibility_type.ResponsibilityTypeMongoRepository;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.responsibility_type.ResponsibilityTypeRepository;
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
import java.util.stream.Collectors;

import static com.kairos.constants.AppConstant.EXISTING_DATA_LIST;
import static com.kairos.constants.AppConstant.NEW_DATA_LIST;


@Service
public class ResponsibilityTypeService extends MongoBaseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResponsibilityTypeService.class);

    @Inject
    private ResponsibilityTypeMongoRepository responsibilityTypeMongoRepository;

    @Inject
    private ExceptionService exceptionService;

    @Inject
    private ResponsibilityTypeRepository responsibilityTypeRepository;


    /**
     * @param countryId
     * @param
     * @param responsibilityTypeDTOS
     * @return return map which contain list of new ResponsibilityType and list of existing ResponsibilityType if ResponsibilityType already exist
     * @description this method create new ResponsibilityType if ResponsibilityType not exist with same name ,
     * and if exist then simply add  ResponsibilityType to existing list and return list ;
     * findByNamesList()  return list of existing ResponsibilityType using collation ,used for case insensitive result
     */
    public Map<String, List<ResponsibilityTypeMD>> createResponsibilityType(Long countryId, List<ResponsibilityTypeDTO> responsibilityTypeDTOS) {
        //TODO still need to optimize we can get name of list in string from here
        Map<String, List<ResponsibilityTypeMD>> result = new HashMap<>();
        Set<String> responsibilityTypeNames = new HashSet<>();
        if (!responsibilityTypeDTOS.isEmpty()) {
            for (ResponsibilityTypeDTO responsibilityType : responsibilityTypeDTOS) {
                responsibilityTypeNames.add(responsibilityType.getName());
            }
            List<String> nameInLowerCase = responsibilityTypeNames.stream().map(String::toLowerCase)
                    .collect(Collectors.toList());
            //TODO still need to update we can return name of list from here and can apply removeAll on list
            List<ResponsibilityTypeMD> existing = responsibilityTypeRepository.findByCountryIdAndDeletedAndNameIn(countryId, false, nameInLowerCase);
            responsibilityTypeNames = ComparisonUtils.getNameListForMetadata(existing, responsibilityTypeNames);

            List<ResponsibilityTypeMD> newResponsibilityTypes = new ArrayList<>();
            if (!responsibilityTypeNames.isEmpty()) {
                for (String name : responsibilityTypeNames) {
                    ResponsibilityTypeMD newResponsibilityType = new ResponsibilityTypeMD(name,countryId,SuggestedDataStatus.APPROVED);
                    newResponsibilityTypes.add(newResponsibilityType);
                }
                newResponsibilityTypes = responsibilityTypeRepository.saveAll(newResponsibilityTypes);

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
        return responsibilityTypeRepository.findAllByCountryIdAndSortByCreatedDate(countryId);
    }

    /**
     * @param id id of ResponsibilityType
     * @return ResponsibilityType object fetch by given id
     * @throws DataNotFoundByIdException throw exception if ResponsibilityType not found for given id
     */
    public ResponsibilityTypeMD getResponsibilityType(Long countryId, Long id) {
        ResponsibilityTypeMD exist = responsibilityTypeRepository.findByIdAndCountryIdAndDeleted(id, countryId, false);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("No data found");
        } else {
            return exist;

        }
    }


    public Boolean deleteResponsibilityType(Long countryId, Long id) {
        Integer resultCount = responsibilityTypeRepository.deleteByIdAndCountryId(id, countryId);
        if (resultCount > 0) {
            LOGGER.info("Responsibility Type deleted successfully for id :: {}", id);
        }else{
            throw new DataNotFoundByIdException("No data found");
        }
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

    public ResponsibilityTypeDTO updateResponsibilityType(Long countryId, Long id, ResponsibilityTypeDTO responsibilityTypeDTO) {


        ResponsibilityTypeMD responsibilityType = responsibilityTypeRepository.findByNameAndCountryId(responsibilityTypeDTO.getName(), countryId);
        if (Optional.ofNullable(responsibilityType).isPresent()) {
            if (id.equals(responsibilityType.getId())) {
                return responsibilityTypeDTO;
            }
            throw new DuplicateDataException("data  exist for  " + responsibilityTypeDTO.getName());
        }
        Integer resultCount =  responsibilityTypeRepository.updateResponsibilityTypeName(responsibilityTypeDTO.getName(), id);
        if(resultCount <=0){
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Responsibility Type", id);
        }else{
            LOGGER.info("Data updated successfully for id : {} and name updated name is : {}", id, responsibilityTypeDTO.getName());
        }
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
    public List<ResponsibilityTypeMD> updateSuggestedStatusOfResponsibilityTypeList(Long countryId, Set<Long> responsibilityTypeIds , SuggestedDataStatus suggestedDataStatus) {

        Integer updateCount = responsibilityTypeRepository.updateResponsibilityTypeStatus(countryId, responsibilityTypeIds,suggestedDataStatus);
        if(updateCount > 0){
            LOGGER.info("Responsibility Types are updated successfully with ids :: {}", responsibilityTypeIds);
        }else{
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Responsibility Type", responsibilityTypeIds);
        }
        return responsibilityTypeRepository.findAllByIds(responsibilityTypeIds);
    }

}

    
    
    


package com.kairos.service.master_data.processing_activity_masterdata;



import com.kairos.commons.custom_exception.DataNotFoundByIdException;
import com.kairos.commons.custom_exception.DuplicateDataException;
import com.kairos.commons.custom_exception.InvalidRequestException;
import com.kairos.enums.gdpr.SuggestedDataStatus;
import com.kairos.dto.gdpr.metadata.TransferMethodDTO;
import com.kairos.persistence.model.master_data.default_proc_activity_setting.TransferMethod;
import com.kairos.persistence.model.master_data.default_proc_activity_setting.TransferMethodMD;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.transfer_method.TransferMethodMongoRepository;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.transfer_method.TransferMethodRepository;
import com.kairos.response.dto.common.TransferMethodResponseDTO;
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
public class TransferMethodService extends MongoBaseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransferMethodService.class);

    @Inject
    private TransferMethodMongoRepository transferMethodMongoRepository;

    @Inject
    private ExceptionService exceptionService;

    @Inject
    private TransferMethodRepository transferMethodRepository;


    /**
     * @param countryId
     * @param
     * @param transferMethodDTOS
     * @return return map which contain list of new TransferMethod and list of existing TransferMethod if TransferMethod already exist
     * @description this method create new TransferMethod if TransferMethod not exist with same name ,
     * and if exist then simply add  TransferMethod to existing list and return list ;
     * findMetaDataByNamesAndCountryId()  return list of existing TransferMethod using collation ,used for case insensitive result
     */
    public Map<String, List<TransferMethodMD>> createTransferMethod(Long countryId, List<TransferMethodDTO> transferMethodDTOS) {
        //TODO still need to optimize we can get name of list in string from here
        Map<String, List<TransferMethodMD>> result = new HashMap<>();
        Set<String> transferMethodNames = new HashSet<>();
        if (!transferMethodDTOS.isEmpty()) {
            for (TransferMethodDTO transferMethod : transferMethodDTOS) {
                transferMethodNames.add(transferMethod.getName());
            }
            List<String> nameInLowerCase = transferMethodNames.stream().map(String::toLowerCase)
                    .collect(Collectors.toList());
            //TODO still need to update we can return name of list from here and can apply removeAll on list
            List<TransferMethodMD> existing = transferMethodRepository.findByCountryIdAndDeletedAndNameIn(countryId, false, nameInLowerCase);
            transferMethodNames = ComparisonUtils.getNameListForMetadata(existing, transferMethodNames);

            List<TransferMethodMD> newTransferMethods = new ArrayList<>();
            if (!transferMethodNames.isEmpty()) {
                for (String name : transferMethodNames) {
                    TransferMethodMD newTransferMethod = new TransferMethodMD(name,countryId,SuggestedDataStatus.APPROVED);
                    newTransferMethods.add(newTransferMethod);
                }
                newTransferMethods = transferMethodRepository.saveAll(newTransferMethods);
            }
            result.put(EXISTING_DATA_LIST, existing);
            result.put(NEW_DATA_LIST, newTransferMethods);
            return result;
        } else
            throw new InvalidRequestException("list cannot be empty");


    }

    /**
     * @param countryId
     * @param
     * @return list of TransferMethod
     */
    public List<TransferMethodResponseDTO> getAllTransferMethod(Long countryId) {
        return transferMethodRepository.findAllByCountryIdAndSortByCreatedDate(countryId);
    }

    /**
     * @param countryId
     * @param
     * @param id        id of TransferMethod
     * @return TransferMethod object fetch by given id
     * @throws DataNotFoundByIdException throw exception if TransferMethod not found for given id
     */
    public TransferMethodMD getTransferMethod(Long countryId, Long id) {
        TransferMethodMD exist = transferMethodRepository.findByIdAndCountryIdAndDeleted(id, countryId, false);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("No data found");
        } else {
            return exist;

        }
    }


    public Boolean deleteTransferMethod(Long countryId, Long id) {

        Integer resultCount = transferMethodRepository.deleteByIdAndCountryId(id, countryId);
        if (resultCount > 0) {
            LOGGER.info("Transfer Method deleted successfully for id :: {}", id);
        }else{
            throw new DataNotFoundByIdException("No data found");
        }
        return true;
    }

    /***
     * @throws DuplicateDataException throw exception if TransferMethod data not exist for given id
     * @param countryId
     * @param
     * @param id id of TransferMethod
     * @param transferMethodDTO
     * @return TransferMethod updated object
     */
    public TransferMethodDTO updateTransferMethod(Long countryId, Long id, TransferMethodDTO transferMethodDTO) {

        TransferMethodMD transferMethod = transferMethodRepository.findByNameAndCountryId(transferMethodDTO.getName(), countryId);
        if (Optional.ofNullable(transferMethod).isPresent()) {
            if (id.equals(transferMethod.getId())) {
                return transferMethodDTO;
            }
            throw new DuplicateDataException("data  exist for  " + transferMethodDTO.getName());
        }
        Integer resultCount =  transferMethodRepository.updateTransferMethodName(transferMethodDTO.getName(), id);
        if(resultCount <=0){
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Transfer Method", id);
        }else{
            LOGGER.info("Data updated successfully for id : {} and name updated name is : {}", id, transferMethodDTO.getName());
        }
        return transferMethodDTO;

    }

    /**
     * @description method save TransferMethod suggested by unit
     * @param countryId
     * @param transferMethodDTOS - transfer method suggested by unit
     * @return
     */
    public List<TransferMethod> saveSuggestedTransferMethodsFromUnit(Long countryId, List<TransferMethodDTO> transferMethodDTOS) {

        Set<String> transferMethodNameList = new HashSet<>();
        for (TransferMethodDTO TransferMethod : transferMethodDTOS) {
            transferMethodNameList.add(TransferMethod.getName());
        }
        List<TransferMethod> existingTransferMethods = findMetaDataByNamesAndCountryId(countryId, transferMethodNameList, TransferMethod.class);
        transferMethodNameList = ComparisonUtils.getNameListForMetadata(existingTransferMethods, transferMethodNameList);
        List<TransferMethod> transferMethodList = new ArrayList<>();
        if (!transferMethodNameList.isEmpty()) {
            for (String name : transferMethodNameList) {

                TransferMethod transferMethod = new TransferMethod(name);
                transferMethod.setCountryId(countryId);
                transferMethod.setSuggestedDataStatus(SuggestedDataStatus.PENDING);
                transferMethod.setSuggestedDate(LocalDate.now());
                transferMethodList.add(transferMethod);
            }

             transferMethodMongoRepository.saveAll(getNextSequence(transferMethodList));
        }
        return transferMethodList;
    }


    /**
     *
     * @param countryId
     * @param transferMethodIds
     * @param suggestedDataStatus
     * @return
     */
    public List<TransferMethodMD> updateSuggestedStatusOfTransferMethodList(Long countryId, Set<Long> transferMethodIds , SuggestedDataStatus suggestedDataStatus) {

        Integer updateCount = transferMethodRepository.updateTransferMethodStatus(countryId, transferMethodIds);
        if(updateCount > 0){
            LOGGER.info("Transfer Methods are updated successfully with ids :: {}", transferMethodIds);
        }else{
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Transfer Method", transferMethodIds);
        }
        return transferMethodRepository.findAllByIds(transferMethodIds);
    }

}

    
    
    


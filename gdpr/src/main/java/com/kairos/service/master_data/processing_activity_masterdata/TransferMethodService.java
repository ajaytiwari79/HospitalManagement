package com.kairos.service.master_data.processing_activity_masterdata;


import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.custom_exception.InvalidRequestException;
import com.kairos.enums.gdpr.SuggestedDataStatus;
import com.kairos.dto.gdpr.metadata.TransferMethodDTO;
import com.kairos.persistence.model.master_data.default_proc_activity_setting.TransferMethod;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.transfer_method.TransferMethodMongoRepository;
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

import static com.kairos.constants.AppConstant.EXISTING_DATA_LIST;
import static com.kairos.constants.AppConstant.NEW_DATA_LIST;


@Service
public class TransferMethodService extends MongoBaseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransferMethodService.class);

    @Inject
    private TransferMethodMongoRepository transferMethodRepository;

    @Inject
    private ExceptionService exceptionService;


    /**
     * @param countryId
     * @param
     * @param transferMethodDTOS
     * @return return map which contain list of new TransferMethod and list of existing TransferMethod if TransferMethod already exist
     * @description this method create new TransferMethod if TransferMethod not exist with same name ,
     * and if exist then simply add  TransferMethod to existing list and return list ;
     * findMetaDataByNamesAndCountryId()  return list of existing TransferMethod using collation ,used for case insensitive result
     */
    public Map<String, List<TransferMethod>> createTransferMethod(Long countryId, List<TransferMethodDTO> transferMethodDTOS) {

        Map<String, List<TransferMethod>> result = new HashMap<>();
        Set<String> transferMethodNames = new HashSet<>();
        if (!transferMethodDTOS.isEmpty()) {
            for (TransferMethodDTO transferMethod : transferMethodDTOS) {

                transferMethodNames.add(transferMethod.getName());
            }
            List<TransferMethod> existing = findMetaDataByNamesAndCountryId(countryId, transferMethodNames, TransferMethod.class);
            transferMethodNames = ComparisonUtils.getNameListForMetadata(existing, transferMethodNames);

            List<TransferMethod> newTransferMethods = new ArrayList<>();
            if (!transferMethodNames.isEmpty()) {
                for (String name : transferMethodNames) {
                    TransferMethod newTransferMethod = new TransferMethod(name,countryId,SuggestedDataStatus.APPROVED);
                    newTransferMethods.add(newTransferMethod);
                }

                newTransferMethods = transferMethodRepository.saveAll(getNextSequence(newTransferMethods));
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
        return transferMethodRepository.findAllTransferMethods(countryId,new Sort(Sort.Direction.DESC, "createdAt"));
    }

    /**
     * @param countryId
     * @param
     * @param id        id of TransferMethod
     * @return TransferMethod object fetch by given id
     * @throws DataNotFoundByIdException throw exception if TransferMethod not found for given id
     */
    public TransferMethod getTransferMethod(Long countryId, BigInteger id) {

        TransferMethod exist = transferMethodRepository.findByIdAndNonDeleted(countryId, id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            return exist;
        }
    }


    public Boolean deleteTransferMethod(Long countryId, BigInteger id) {

        TransferMethod transferMethod = transferMethodRepository.findByIdAndNonDeleted(countryId, id);
        if (!Optional.ofNullable(transferMethod).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        }
        delete(transferMethod);
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
    public TransferMethodDTO updateTransferMethod(Long countryId, BigInteger id, TransferMethodDTO transferMethodDTO) {

        TransferMethod transferMethod = transferMethodRepository.findByName(countryId, transferMethodDTO.getName());
        if (Optional.ofNullable(transferMethod).isPresent()) {
            if (id.equals(transferMethod.getId())) {
                return transferMethodDTO;
            }
            throw new DuplicateDataException("data  exist for  " + transferMethodDTO.getName());
        }
        transferMethod = transferMethodRepository.findByid(id);
        if (!Optional.ofNullable(transferMethod).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Transfer Method", id);

        }
        transferMethod.setName(transferMethodDTO.getName());
        transferMethodRepository.save(transferMethod);
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

             transferMethodRepository.saveAll(getNextSequence(transferMethodList));
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
    public List<TransferMethod> updateSuggestedStatusOfTransferMethodList(Long countryId, Set<BigInteger> transferMethodIds , SuggestedDataStatus suggestedDataStatus) {

        List<TransferMethod> transferMethodList = transferMethodRepository.getTransferMethodListByIds(countryId, transferMethodIds);
        transferMethodList.forEach(transferMethod-> transferMethod.setSuggestedDataStatus(suggestedDataStatus));
        transferMethodRepository.saveAll(getNextSequence(transferMethodList));
        return transferMethodList;
    }

}

    
    
    


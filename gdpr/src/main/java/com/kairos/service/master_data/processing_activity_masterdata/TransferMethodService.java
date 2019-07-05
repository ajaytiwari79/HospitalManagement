package com.kairos.service.master_data.processing_activity_masterdata;

import com.kairos.commons.custom_exception.DataNotFoundByIdException;
import com.kairos.commons.custom_exception.DuplicateDataException;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.gdpr.metadata.TransferMethodDTO;
import com.kairos.enums.gdpr.SuggestedDataStatus;
import com.kairos.persistence.model.master_data.default_proc_activity_setting.TransferMethod;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.transfer_method.TransferMethodRepository;
import com.kairos.response.dto.common.TransferMethodResponseDTO;
import com.kairos.service.exception.ExceptionService;
import com.kairos.utils.ComparisonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.*;


@Service
public class TransferMethodService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransferMethodService.class);

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
    public List<TransferMethodDTO> createTransferMethod(Long countryId, List<TransferMethodDTO> transferMethodDTOS, boolean isSuggestion) {
        Set<String> existingTransferMethodNames = transferMethodRepository.findNameByCountryIdAndDeleted(countryId);
        Set<String> transferMethodNames = ComparisonUtils.getNewMetaDataNames(transferMethodDTOS,existingTransferMethodNames );
        List<TransferMethod> transferMethods = new ArrayList<>();
        if (!transferMethodNames.isEmpty()) {
            for (String name : transferMethodNames) {
                TransferMethod transferMethod = new TransferMethod(countryId, name);
                if (isSuggestion) {
                    transferMethod.setSuggestedDataStatus(SuggestedDataStatus.PENDING);
                    transferMethod.setSuggestedDate(LocalDate.now());
                } else {
                    transferMethod.setSuggestedDataStatus(SuggestedDataStatus.APPROVED);
                }
                transferMethods.add(transferMethod);
            }
            transferMethodRepository.saveAll(transferMethods);
        }
        return ObjectMapperUtils.copyPropertiesOfListByMapper(transferMethods, TransferMethodDTO.class);
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
    public TransferMethod getTransferMethod(Long countryId, Long id) {
        TransferMethod exist = transferMethodRepository.findByIdAndCountryIdAndDeletedFalse(id, countryId);
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
        } else {
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

        TransferMethod transferMethod = transferMethodRepository.findByCountryIdAndName(countryId, transferMethodDTO.getName());
        if (Optional.ofNullable(transferMethod).isPresent()) {
            if (id.equals(transferMethod.getId())) {
                return transferMethodDTO;
            }
            throw new DuplicateDataException("data  exist for  " + transferMethodDTO.getName());
        }
        Integer resultCount = transferMethodRepository.updateMasterMetadataName(transferMethodDTO.getName(), id, countryId);
        if (resultCount <= 0) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "message.transferMethod", id);
        } else {
            LOGGER.info("Data updated successfully for id : {} and name updated name is : {}", id, transferMethodDTO.getName());
        }
        return transferMethodDTO;

    }

    /**
     * @param countryId
     * @param transferMethodDTOS - transfer method suggested by unit
     * @return
     * @description method save TransferMethod suggested by unit
     */
    public void saveSuggestedTransferMethodsFromUnit(Long countryId, List<TransferMethodDTO> transferMethodDTOS) {
        createTransferMethod(countryId, transferMethodDTOS, true);
    }


    /**
     * @param countryId
     * @param transferMethodIds
     * @param suggestedDataStatus
     * @return
     */
    public List<TransferMethod> updateSuggestedStatusOfTransferMethodList(Long countryId, Set<Long> transferMethodIds, SuggestedDataStatus suggestedDataStatus) {

        Integer updateCount = transferMethodRepository.updateMetadataStatus(countryId, transferMethodIds, suggestedDataStatus);
        if (updateCount > 0) {
            LOGGER.info("Transfer Methods are updated successfully with ids :: {}", transferMethodIds);
        } else {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "message.transferMethod", transferMethodIds);
        }
        return transferMethodRepository.findAllByIds(transferMethodIds);
    }

}

    
    
    


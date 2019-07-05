package com.kairos.service.data_inventory.processing_activity;

import com.kairos.commons.custom_exception.DataNotFoundByIdException;
import com.kairos.commons.custom_exception.DuplicateDataException;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.gdpr.metadata.TransferMethodDTO;
import com.kairos.persistence.model.master_data.default_proc_activity_setting.TransferMethod;
import com.kairos.persistence.repository.data_inventory.processing_activity.ProcessingActivityRepository;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.transfer_method.TransferMethodRepository;
import com.kairos.response.dto.common.TransferMethodResponseDTO;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.master_data.processing_activity_masterdata.TransferMethodService;
import com.kairos.utils.ComparisonUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.*;

@Service
public class OrganizationTransferMethodService {


    private static final Logger LOGGER = LoggerFactory.getLogger(TransferMethodService.class);

    @Inject
    private TransferMethodRepository transferMethodRepository;

    @Inject
    private ExceptionService exceptionService;

    @Inject
    private TransferMethodService transferMethodService;

    @Inject
    private ProcessingActivityRepository processingActivityRepository;

    /**
     * @param unitId
     * @param transferMethodDTOS
     * @return return map which contain list of new TransferMethod and list of existing TransferMethod if TransferMethod already exist
     * @description this method create new TransferMethod if TransferMethod not exist with same name ,
     * and if exist then simply add  TransferMethod to existing list and return list ;
     * findMetaDataByNamesAndCountryId()  return list of existing TransferMethod using collation ,used for case insensitive result
     */
    public List<TransferMethodDTO> createTransferMethod(Long unitId, List<TransferMethodDTO> transferMethodDTOS) {
        Set<String> existingTransferMethodNames = transferMethodRepository.findNameByOrganizationIdAndDeleted(unitId);
        Set<String> transferMethodNames = ComparisonUtils.getNewMetaDataNames(transferMethodDTOS,existingTransferMethodNames );
        List<TransferMethod> transferMethods = new ArrayList<>();
        if (!transferMethodNames.isEmpty()) {
            for (String name : transferMethodNames) {
                TransferMethod transferMethod = new TransferMethod(name);
                transferMethod.setOrganizationId(unitId);
                transferMethods.add(transferMethod);
            }

            transferMethodRepository.saveAll(transferMethods);
        }
        return ObjectMapperUtils.copyPropertiesOfListByMapper(transferMethods, TransferMethodDTO.class);
    }

    /**
     * @param unitId
     * @return list of TransferMethod
     */
    public List<TransferMethodResponseDTO> getAllTransferMethod(Long unitId) {
        return transferMethodRepository.findAllByOrganizationIdAndSortByCreatedDate(unitId);
    }

    /**
     * @param unitId
     * @param id             id of TransferMethod
     * @return TransferMethod object fetch by given id
     * @throws DataNotFoundByIdException throw exception if TransferMethod not found for given id
     */
    public TransferMethod getTransferMethod(Long unitId, Long id) {

        TransferMethod exist = transferMethodRepository.findByIdAndOrganizationIdAndDeletedFalse(id, unitId);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            return exist;
        }
    }


    public Boolean deleteTransferMethod(Long unitId, Long transferMethodId) {

        List<String> processingActivitiesLinkedWithTransferMethod = processingActivityRepository.findAllProcessingActivityLinkedWithTransferMethod(unitId, transferMethodId);
        if (!processingActivitiesLinkedWithTransferMethod.isEmpty()) {
            exceptionService.metaDataLinkedWithProcessingActivityException("message.metaData.linked.with.ProcessingActivity", "message.transferMethod", StringUtils.join(processingActivitiesLinkedWithTransferMethod, ','));
        }
        transferMethodRepository.deleteByIdAndOrganizationId(transferMethodId, unitId);
        return true;
    }

    /***
     * @throws DuplicateDataException throw exception if TransferMethod data not exist for given id
     * @param unitId
     * @param id id of TransferMethod
     * @param transferMethodDTO
     * @return TransferMethod updated object
     */
    public TransferMethodDTO updateTransferMethod(Long unitId, Long id, TransferMethodDTO transferMethodDTO) {

        TransferMethod transferMethod = transferMethodRepository.findByOrganizationIdAndDeletedAndName(unitId, transferMethodDTO.getName());
        if (Optional.ofNullable(transferMethod).isPresent()) {
            if (id.equals(transferMethod.getId())) {
                return transferMethodDTO;
            }
            exceptionService.duplicateDataException("message.duplicate", "message.transferMethod", transferMethod.getName());
        }
        Integer resultCount = transferMethodRepository.updateMetadataName(transferMethodDTO.getName(), id, unitId);
        if (resultCount <= 0) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "message.transferMethod", id);
        } else {
            LOGGER.info("Data updated successfully for id : {} and name updated name is : {}", id, transferMethodDTO.getName());
        }
        return transferMethodDTO;


    }

    public List<TransferMethodDTO> saveAndSuggestTransferMethods(Long countryId, Long unitId, List<TransferMethodDTO> transferMethodDTOS) {

        List<TransferMethodDTO> result = createTransferMethod(unitId, transferMethodDTOS);
        transferMethodService.saveSuggestedTransferMethodsFromUnit(countryId, transferMethodDTOS);
        return result;
    }

}

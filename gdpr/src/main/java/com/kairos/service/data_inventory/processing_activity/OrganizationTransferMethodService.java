package com.kairos.service.data_inventory.processing_activity;


import com.kairos.commons.custom_exception.DataNotFoundByIdException;
import com.kairos.commons.custom_exception.DuplicateDataException;
import com.kairos.commons.custom_exception.InvalidRequestException;
import com.kairos.dto.gdpr.metadata.TransferMethodDTO;
import com.kairos.persistence.model.master_data.default_proc_activity_setting.TransferMethodMD;
import com.kairos.persistence.repository.data_inventory.processing_activity.ProcessingActivityRepository;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.transfer_method.TransferMethodRepository;
import com.kairos.response.dto.common.TransferMethodResponseDTO;
import com.kairos.service.common.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.master_data.processing_activity_masterdata.TransferMethodService;
import com.kairos.utils.ComparisonUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.constants.AppConstant.EXISTING_DATA_LIST;
import static com.kairos.constants.AppConstant.NEW_DATA_LIST;

@Service
public class OrganizationTransferMethodService extends MongoBaseService {


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
     * @param organizationId
     * @param transferMethodDTOS
     * @return return map which contain list of new TransferMethod and list of existing TransferMethod if TransferMethod already exist
     * @description this method create new TransferMethod if TransferMethod not exist with same name ,
     * and if exist then simply add  TransferMethod to existing list and return list ;
     * findMetaDataByNamesAndCountryId()  return list of existing TransferMethod using collation ,used for case insensitive result
     */
    public Map<String, List<TransferMethodMD>> createTransferMethod(Long organizationId, List<TransferMethodDTO> transferMethodDTOS) {

        Map<String, List<TransferMethodMD>> result = new HashMap<>();
        Set<String> transferMethodNames = new HashSet<>();
        if (!transferMethodDTOS.isEmpty()) {
            for (TransferMethodDTO transferMethod : transferMethodDTOS) {
                if (!StringUtils.isBlank(transferMethod.getName())) {
                    transferMethodNames.add(transferMethod.getName());
                } else
                    throw new InvalidRequestException("name could not be empty or null");

            }
            List<String> nameInLowerCase = transferMethodNames.stream().map(String::toLowerCase)
                    .collect(Collectors.toList());
            //TODO still need to update we can return name of list from here and can apply removeAll on list
            List<TransferMethodMD> existing = transferMethodRepository.findByOrganizationIdAndDeletedAndNameIn(organizationId, false, nameInLowerCase);
            transferMethodNames = ComparisonUtils.getNameListForMetadata(existing, transferMethodNames);

            List<TransferMethodMD> newTransferMethods = new ArrayList<>();
            if (transferMethodNames.size() != 0) {
                for (String name : transferMethodNames) {
                    TransferMethodMD newTransferMethod = new TransferMethodMD(name);
                    newTransferMethod.setOrganizationId(organizationId);
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
     * @param organizationId
     * @return list of TransferMethod
     */
    public List<TransferMethodResponseDTO> getAllTransferMethod(Long organizationId) {
        return transferMethodRepository.findAllByOrganizationIdAndSortByCreatedDate(organizationId);
    }

    /**
     * @param organizationId
     * @param id             id of TransferMethod
     * @return TransferMethod object fetch by given id
     * @throws DataNotFoundByIdException throw exception if TransferMethod not found for given id
     */
    public TransferMethodMD getTransferMethod(Long organizationId, Long id) {

        TransferMethodMD exist = transferMethodRepository.findByIdAndOrganizationIdAndDeleted(id, organizationId, false);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            return exist;
        }
    }


    public Boolean deleteTransferMethod(Long unitId, Long transferMethodId) {

        List<String> processingActivitiesLinkedWithTransferMethod = processingActivityRepository.findAllProcessingActivityLinkedWithTransferMethod(unitId, transferMethodId);
        if (!processingActivitiesLinkedWithTransferMethod.isEmpty()) {
            exceptionService.metaDataLinkedWithProcessingActivityException("message.metaData.linked.with.ProcessingActivity", "Transfer Method", StringUtils.join(processingActivitiesLinkedWithTransferMethod, ','));
        }
        transferMethodRepository.deleteByIdAndOrganizationId(transferMethodId, unitId);
        return true;
    }

    /***
     * @throws DuplicateDataException throw exception if TransferMethod data not exist for given id
     * @param organizationId
     * @param id id of TransferMethod
     * @param transferMethodDTO
     * @return TransferMethod updated object
     */
    public TransferMethodDTO updateTransferMethod(Long organizationId, Long id, TransferMethodDTO transferMethodDTO) {

        TransferMethodMD transferMethod = transferMethodRepository.findByOrganizationIdAndDeletedAndName(organizationId, false, transferMethodDTO.getName());
        if (Optional.ofNullable(transferMethod).isPresent()) {
            if (id.equals(transferMethod.getId())) {
                return transferMethodDTO;
            }
            exceptionService.duplicateDataException("message.duplicate", "Transfer Method", transferMethod.getName());
        }
        Integer resultCount =  transferMethodRepository.updateMetadataName(transferMethodDTO.getName(), id, organizationId);
        if(resultCount <=0){
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Transfer Method", id);
        }else{
            LOGGER.info("Data updated successfully for id : {} and name updated name is : {}", id, transferMethodDTO.getName());
        }
        return transferMethodDTO;


    }

    public Map<String, List<TransferMethodMD>> saveAndSuggestTransferMethods(Long countryId, Long organizationId, List<TransferMethodDTO> transferMethodDTOS) {

        Map<String, List<TransferMethodMD>> result = createTransferMethod(organizationId, transferMethodDTOS);
        List<TransferMethodMD> masterTransferMethodSuggestedByUnit = transferMethodService.saveSuggestedTransferMethodsFromUnit(countryId, transferMethodDTOS);
        if (!masterTransferMethodSuggestedByUnit.isEmpty()) {
            result.put("SuggestedData", masterTransferMethodSuggestedByUnit);
        }
        return result;
    }

}

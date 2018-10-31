package com.kairos.service.data_inventory.processing_activity;


import com.kairos.commons.custom_exception.DataNotFoundByIdException;
import com.kairos.commons.custom_exception.DuplicateDataException;
import com.kairos.commons.custom_exception.InvalidRequestException;
import com.kairos.dto.gdpr.metadata.TransferMethodDTO;
import com.kairos.persistence.model.master_data.default_proc_activity_setting.TransferMethod;
import com.kairos.persistence.repository.data_inventory.processing_activity.ProcessingActivityMongoRepository;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.transfer_method.TransferMethodMongoRepository;
import com.kairos.response.dto.common.TransferMethodResponseDTO;
import com.kairos.response.dto.data_inventory.ProcessingActivityBasicDTO;
import com.kairos.service.common.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.master_data.processing_activity_masterdata.TransferMethodService;
import com.kairos.utils.ComparisonUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.constants.AppConstant.EXISTING_DATA_LIST;
import static com.kairos.constants.AppConstant.NEW_DATA_LIST;

@Service
public class OrganizationTransferMethodService extends MongoBaseService {


    private static final Logger LOGGER = LoggerFactory.getLogger(TransferMethodService.class);

    @Inject
    private TransferMethodMongoRepository transferMethodRepository;

    @Inject
    private ExceptionService exceptionService;

    @Inject
    private TransferMethodService transferMethodService;

    @Inject
    private ProcessingActivityMongoRepository processingActivityMongoRepository;

    /**
     * @param organizationId
     * @param transferMethodDTOS
     * @return return map which contain list of new TransferMethod and list of existing TransferMethod if TransferMethod already exist
     * @description this method create new TransferMethod if TransferMethod not exist with same name ,
     * and if exist then simply add  TransferMethod to existing list and return list ;
     * findMetaDataByNamesAndCountryId()  return list of existing TransferMethod using collation ,used for case insensitive result
     */
    public Map<String, List<TransferMethod>> createTransferMethod(Long organizationId, List<TransferMethodDTO> transferMethodDTOS) {

        Map<String, List<TransferMethod>> result = new HashMap<>();
        Set<String> transferMethodNames = new HashSet<>();
        if (!transferMethodDTOS.isEmpty()) {
            for (TransferMethodDTO transferMethod : transferMethodDTOS) {
                if (!StringUtils.isBlank(transferMethod.getName())) {
                    transferMethodNames.add(transferMethod.getName());
                } else
                    throw new InvalidRequestException("name could not be empty or null");

            }
            List<TransferMethod> existing = findMetaDataByNameAndUnitId(organizationId, transferMethodNames, TransferMethod.class);
            transferMethodNames = ComparisonUtils.getNameListForMetadata(existing, transferMethodNames);

            List<TransferMethod> newTransferMethods = new ArrayList<>();
            if (transferMethodNames.size() != 0) {
                for (String name : transferMethodNames) {
                    TransferMethod newTransferMethod = new TransferMethod(name);
                    newTransferMethod.setOrganizationId(organizationId);
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
     * @param organizationId
     * @return list of TransferMethod
     */
    public List<TransferMethodResponseDTO> getAllTransferMethod(Long organizationId) {
        return transferMethodRepository.findAllByUnitIdSortByCreatedDate(organizationId, new Sort(Sort.Direction.DESC, "createdAt"));
    }

    /**
     * @param organizationId
     * @param id             id of TransferMethod
     * @return TransferMethod object fetch by given id
     * @throws DataNotFoundByIdException throw exception if TransferMethod not found for given id
     */
    public TransferMethod getTransferMethod(Long organizationId, BigInteger id) {

        TransferMethod exist = transferMethodRepository.findByUnitIdAndId(organizationId, id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            return exist;
        }
    }


    public Boolean deleteTransferMethod(Long unitId, BigInteger transferMethodId) {

        List<ProcessingActivityBasicDTO> processingActivitiesLinkedWithTransferMethod = processingActivityMongoRepository.findAllProcessingActivityLinkedWithTransferMethod(unitId, transferMethodId);
        if (!processingActivitiesLinkedWithTransferMethod.isEmpty()) {
            exceptionService.metaDataLinkedWithProcessingActivityException("message.metaData.linked.with.ProcessingActivity", "Transfer Method", new StringBuilder(processingActivitiesLinkedWithTransferMethod.stream().map(ProcessingActivityBasicDTO::getName).map(String::toString).collect(Collectors.joining(","))));
        }
        transferMethodRepository.safeDeleteById(transferMethodId);
        return true;
    }

    /***
     * @throws DuplicateDataException throw exception if TransferMethod data not exist for given id
     * @param organizationId
     * @param id id of TransferMethod
     * @param transferMethodDTO
     * @return TransferMethod updated object
     */
    public TransferMethodDTO updateTransferMethod(Long organizationId, BigInteger id, TransferMethodDTO transferMethodDTO) {

        TransferMethod transferMethod = transferMethodRepository.findByUnitIdAndName(organizationId, transferMethodDTO.getName());
        if (Optional.ofNullable(transferMethod).isPresent()) {
            if (id.equals(transferMethod.getId())) {
                return transferMethodDTO;
            }
            exceptionService.duplicateDataException("message.duplicate", "Transfer Method", transferMethod.getName());
        }
        transferMethod = transferMethodRepository.findByid(id);
        if (!Optional.ofNullable(transferMethod).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Transfer Method", id);
        }
        transferMethod.setName(transferMethodDTO.getName());
        transferMethodRepository.save(transferMethod);
        return transferMethodDTO;


    }

    public Map<String, List<TransferMethod>> saveAndSuggestTransferMethods(Long countryId, Long organizationId, List<TransferMethodDTO> transferMethodDTOS) {

        Map<String, List<TransferMethod>> result;
        result = createTransferMethod(organizationId, transferMethodDTOS);
        List<TransferMethod> masterTransferMethodSuggestedByUnit = transferMethodService.saveSuggestedTransferMethodsFromUnit(countryId, transferMethodDTOS);
        if (!masterTransferMethodSuggestedByUnit.isEmpty()) {
            result.put("SuggestedData", masterTransferMethodSuggestedByUnit);
        }
        return result;
    }

}

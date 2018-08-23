package com.kairos.service.data_inventory.processing_activity;


import com.kairos.custom_exception.DataNotExists;
import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.custom_exception.InvalidRequestException;
import com.kairos.gdpr.metadata.ProcessingPurposeDTO;
import com.kairos.persistance.model.master_data.default_proc_activity_setting.ProcessingPurpose;
import com.kairos.persistance.repository.data_inventory.processing_activity.ProcessingActivityMongoRepository;
import com.kairos.persistance.repository.master_data.processing_activity_masterdata.processing_purpose.ProcessingPurposeMongoRepository;
import com.kairos.response.dto.common.ProcessingPurposeResponseDTO;
import com.kairos.service.common.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.master_data.processing_activity_masterdata.ProcessingPurposeService;
import com.kairos.utils.ComparisonUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;

import static com.kairos.constants.AppConstant.EXISTING_DATA_LIST;
import static com.kairos.constants.AppConstant.NEW_DATA_LIST;

@Service
public class OrganizationProcessingPurposeService extends MongoBaseService {


    private static final Logger LOGGER = LoggerFactory.getLogger(OrganizationProcessingPurposeService.class);

    @Inject
    private ProcessingPurposeMongoRepository processingPurposeMongoRepository;

    @Inject
    private ExceptionService exceptionService;

    @Inject
    private ProcessingPurposeService processingPurposeService;

    @Inject
    private ProcessingActivityMongoRepository processingActivityMongoRepository;


    /**
     * @param organizationId
     * @param processingPurposeDTOS
     * @return return map which contain list of new ProcessingPurpose and list of existing ProcessingPurpose if ProcessingPurpose already exist
     * @description this method create new ProcessingPurpose if ProcessingPurpose not exist with same name ,
     * and if exist then simply add  ProcessingPurpose to existing list and return list ;
     * findMetaDataByNamesAndCountryId()  return list of existing ProcessingPurpose using collation ,used for case insensitive result
     */
    public Map<String, List<ProcessingPurpose>> createProcessingPurpose(Long organizationId, List<ProcessingPurposeDTO> processingPurposeDTOS) {

        Map<String, List<ProcessingPurpose>> result = new HashMap<>();
        Set<String> processingPurposesNames = new HashSet<>();
        if (!processingPurposeDTOS.isEmpty()) {
            for (ProcessingPurposeDTO processingPurpose : processingPurposeDTOS) {
                processingPurposesNames.add(processingPurpose.getName());
            }
            List<ProcessingPurpose> existing = findMetaDataByNameAndUnitId(organizationId, processingPurposesNames, ProcessingPurpose.class);
            processingPurposesNames = ComparisonUtils.getNameListForMetadata(existing, processingPurposesNames);

            List<ProcessingPurpose> newProcessingPurposes = new ArrayList<>();
            if (processingPurposesNames.size() != 0) {
                for (String name : processingPurposesNames) {

                    ProcessingPurpose newProcessingPurpose = new ProcessingPurpose(name);
                    newProcessingPurpose.setOrganizationId(organizationId);
                    newProcessingPurposes.add(newProcessingPurpose);

                }
                newProcessingPurposes = processingPurposeMongoRepository.saveAll(getNextSequence(newProcessingPurposes));
            }
            result.put(EXISTING_DATA_LIST, existing);
            result.put(NEW_DATA_LIST, newProcessingPurposes);
            return result;
        } else
            throw new InvalidRequestException("list cannot be empty");


    }


    /**
     * @param organizationId
     * @return list of ProcessingPurpose
     */
    public List<ProcessingPurposeResponseDTO> getAllProcessingPurpose(Long organizationId) {
        return processingPurposeMongoRepository.findAllOrganizationProcessingPurposes(organizationId);
    }

    /**
     * @param organizationId
     * @param id             id of ProcessingPurpose
     * @return ProcessingPurpose object fetch by given id
     * @throws DataNotFoundByIdException throw exception if ProcessingPurpose not found for given id
     */
    public ProcessingPurpose getProcessingPurpose(Long organizationId, BigInteger id) {

        ProcessingPurpose exist = processingPurposeMongoRepository.findByOrganizationIdAndId(organizationId, id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            return exist;

        }
    }


    public Boolean deleteProcessingPurpose(Long unitId, BigInteger processingPurposeId) {

        List<String> processingActivities = processingActivityMongoRepository.findAllProcessingActivityLinkedWithProcessingPurpose(unitId, processingPurposeId);
        if (!processingActivities.isEmpty()) {
            exceptionService.metaDataLinkedWithProcessingActivityException("message.metaData.linked.with.ProcessingActivity", "Processing Purpose", processingActivities.get(0));
        }
        ProcessingPurpose processingPurpose = processingPurposeMongoRepository.findByOrganizationIdAndId(unitId, processingPurposeId);
        if (!Optional.ofNullable(processingPurpose).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Processing Purpose", processingPurposeId);
        }
        delete(processingPurpose);
        return true;
    }

    /***
     * @throws DuplicateDataException throw exception if ProcessingPurpose data not exist for given id
     * @param organizationId
     * @param id id of ProcessingPurpose
     * @param processingPurposeDTO
     * @return ProcessingPurpose updated object
     */
    public ProcessingPurposeDTO updateProcessingPurpose(Long organizationId, BigInteger id, ProcessingPurposeDTO processingPurposeDTO) {

        ProcessingPurpose processingPurpose = processingPurposeMongoRepository.findByOrganizationIdAndName(organizationId, processingPurposeDTO.getName());
        if (Optional.ofNullable(processingPurpose).isPresent()) {
            if (id.equals(processingPurpose.getId())) {
                return processingPurposeDTO;
            }
            exceptionService.duplicateDataException("message.duplicate", "Processing Purpose", processingPurpose.getName());
        }
        processingPurpose = processingPurposeMongoRepository.findByid(id);
        if (!Optional.ofNullable(processingPurpose).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Processing Purpose", id);
        }
        processingPurpose.setName(processingPurposeDTO.getName());
        processingPurposeMongoRepository.save(processingPurpose);
        return processingPurposeDTO;

    }

    public Map<String, List<ProcessingPurpose>> saveAndSuggestProcessingPurposes(Long countryId, Long organizationId, List<ProcessingPurposeDTO> ProcessingPurposeDTOS) {

        Map<String, List<ProcessingPurpose>> result;
        result = createProcessingPurpose(organizationId, ProcessingPurposeDTOS);
        List<ProcessingPurpose> masterProcessingPurposeSuggestedByUnit = processingPurposeService.saveSuggestedProcessingPurposesFromUnit(countryId, ProcessingPurposeDTOS);
        if (!masterProcessingPurposeSuggestedByUnit.isEmpty()) {
            result.put("SuggestedData", masterProcessingPurposeSuggestedByUnit);
        }
        return result;
    }


}

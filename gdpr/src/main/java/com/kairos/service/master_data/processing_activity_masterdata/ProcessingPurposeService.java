package com.kairos.service.master_data.processing_activity_masterdata;


import com.kairos.custom_exception.DataNotExists;
import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.custom_exception.InvalidRequestException;
import com.kairos.enums.SuggestedDataStatus;
import com.kairos.gdpr.metadata.ProcessingPurposeDTO;
import com.kairos.persistance.model.master_data.default_proc_activity_setting.ProcessingPurpose;
import com.kairos.persistance.repository.master_data.processing_activity_masterdata.processing_purpose.ProcessingPurposeMongoRepository;
import com.kairos.response.dto.common.ProcessingPurposeResponseDTO;
import com.kairos.service.common.MongoBaseService;
import com.kairos.utils.ComparisonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;

import static com.kairos.constants.AppConstant.EXISTING_DATA_LIST;
import static com.kairos.constants.AppConstant.NEW_DATA_LIST;


@Service
public class ProcessingPurposeService extends MongoBaseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessingPurposeService.class);

    @Inject
    private ProcessingPurposeMongoRepository processingPurposeMongoRepository;

    /**
     * @param countryId
     * @param
     * @param processingPurposeDTOS
     * @return return map which contain list of new ProcessingPurpose and list of existing ProcessingPurpose if ProcessingPurpose already exist
     * @description this method create new ProcessingPurpose if ProcessingPurpose not exist with same name ,
     * and if exist then simply add  ProcessingPurpose to existing list and return list ;
     * findMetaDataByNamesAndCountryId()  return list of existing ProcessingPurpose using collation ,used for case insensitive result
     */
    public Map<String, List<ProcessingPurpose>> createProcessingPurpose(Long countryId, List<ProcessingPurposeDTO> processingPurposeDTOS) {

        Map<String, List<ProcessingPurpose>> result = new HashMap<>();
        Set<String> processingPurposesNames = new HashSet<>();
        if (!processingPurposeDTOS.isEmpty()) {
            for (ProcessingPurposeDTO processingPurpose : processingPurposeDTOS) {
                if (!StringUtils.isBlank(processingPurpose.getName())) {
                    processingPurposesNames.add(processingPurpose.getName());
                } else
                    throw new InvalidRequestException("name could not be empty or null");

            }
            List<ProcessingPurpose> existing = findMetaDataByNamesAndCountryId(countryId, processingPurposesNames, ProcessingPurpose.class);
            processingPurposesNames = ComparisonUtils.getNameListForMetadata(existing, processingPurposesNames);

            List<ProcessingPurpose> newProcessingPurposes = new ArrayList<>();
            if (!processingPurposesNames.isEmpty()) {
                for (String name : processingPurposesNames) {

                    ProcessingPurpose newProcessingPurpose = new ProcessingPurpose(name);
                    newProcessingPurpose.setCountryId(countryId);
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
     * @param countryId
     * @param
     * @return list of ProcessingPurpose
     */
    public List<ProcessingPurposeResponseDTO> getAllProcessingPurpose(Long countryId) {
        return processingPurposeMongoRepository.findAllProcessingPurposes(countryId,SuggestedDataStatus.ACCEPTED.value);
    }

    /**
     * @param countryId
     * @param
     * @param id        id of ProcessingPurpose
     * @return ProcessingPurpose object fetch by given id
     * @throws DataNotFoundByIdException throw exception if ProcessingPurpose not found for given id
     */
    public ProcessingPurpose getProcessingPurpose(Long countryId, BigInteger id) {

        ProcessingPurpose exist = processingPurposeMongoRepository.findByIdAndNonDeleted(countryId, id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            return exist;

        }
    }


    public Boolean deleteProcessingPurpose(Long countryId, BigInteger id) {

        ProcessingPurpose processingPurpose = processingPurposeMongoRepository.findByIdAndNonDeleted(countryId, id);
        if (!Optional.ofNullable(processingPurpose).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        }
            delete(processingPurpose);
            return true;

    }

    /***
     * @throws DuplicateDataException throw exception if ProcessingPurpose data not exist for given id
     * @param countryId
     * @param
     * @param id id of ProcessingPurpose
     * @param processingPurposeDTO
     * @return ProcessingPurpose updated object
     */
    public ProcessingPurposeDTO updateProcessingPurpose(Long countryId, BigInteger id, ProcessingPurposeDTO processingPurposeDTO) {


        ProcessingPurpose processingPurpose = processingPurposeMongoRepository.findByName(countryId, processingPurposeDTO.getName());
        if (Optional.ofNullable(processingPurpose).isPresent()) {
            if (id.equals(processingPurpose.getId())) {
                return processingPurposeDTO;
            }
            throw new DuplicateDataException("data  exist for  " + processingPurposeDTO.getName());
        } else {
            processingPurpose = processingPurposeMongoRepository.findByid(id);
            processingPurpose.setName(processingPurpose.getName());
             processingPurposeMongoRepository.save(processingPurpose);
             return processingPurposeDTO;

        }
    }


    /**
     * @param countryId
     * @param
     * @param name      name of ProcessingPurpose
     * @return ProcessingPurpose object fetch on basis of  name
     * @throws DataNotExists throw exception if ProcessingPurpose not exist for given name
     */
    public ProcessingPurpose getProcessingPurposeByName(Long countryId, String name) {


        if (!StringUtils.isBlank(name)) {
            ProcessingPurpose exist = processingPurposeMongoRepository.findByName(countryId, name);
            if (!Optional.ofNullable(exist).isPresent()) {
                throw new DataNotExists("data not exist for name " + name);
            }
            return exist;
        } else
            throw new InvalidRequestException("request param cannot be empty  or null");

    }


    public List<ProcessingPurpose> saveSuggestedProcessingPurposesFromUnit(Long countryId, List<ProcessingPurposeDTO> ProcessingPurposeDTOS) {

        Set<String> hostingProvoiderNames = new HashSet<>();
        for (ProcessingPurposeDTO ProcessingPurpose : ProcessingPurposeDTOS) {
            hostingProvoiderNames.add(ProcessingPurpose.getName());
        }
        List<ProcessingPurpose> existingProcessingPurposes = findMetaDataByNamesAndCountryId(countryId, hostingProvoiderNames, ProcessingPurpose.class);
        hostingProvoiderNames = ComparisonUtils.getNameListForMetadata(existingProcessingPurposes, hostingProvoiderNames);
        List<ProcessingPurpose> ProcessingPurposeList = new ArrayList<>();
        if (hostingProvoiderNames.size() != 0) {
            for (String name : hostingProvoiderNames) {

                ProcessingPurpose ProcessingPurpose = new ProcessingPurpose(name);
                ProcessingPurpose.setCountryId(countryId);
                ProcessingPurpose.setSuggestedDataStatus(SuggestedDataStatus.QUEUE.value);
                ProcessingPurposeList.add(ProcessingPurpose);
            }

            ProcessingPurposeList = processingPurposeMongoRepository.saveAll(getNextSequence(ProcessingPurposeList));
        }
        return ProcessingPurposeList;
    }

}

    
    
    


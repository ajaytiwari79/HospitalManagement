package com.kairos.service.master_data_management.processing_activity_masterdata;


import com.kairos.custom_exception.DataNotExists;
import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.custom_exception.InvalidRequestException;
import com.kairos.persistance.model.master_data_management.processing_activity_masterdata.ProcessingPurpose;
import com.kairos.persistance.repository.master_data_management.processing_activity_masterdata.ProcessingPurposeMongoRepository;
import com.kairos.service.MongoBaseService;
import com.kairos.utils.ComparisonUtils;
import com.kairos.utils.userContext.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;

@Service
public class ProcessingPurposeService extends MongoBaseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessingPurposeService.class);

    @Inject
    private ProcessingPurposeMongoRepository processingPurposeMongoRepository;


    @Inject
    private ComparisonUtils comparisonUtils;


    public Map<String, List<ProcessingPurpose>> createProcessingPurpose(Long countryId,Long organizationId,List<ProcessingPurpose> processingPurposes) {

        Map<String, List<ProcessingPurpose>> result = new HashMap<>();
        List<ProcessingPurpose> newProcessingPurposes = new ArrayList<>();
        Set<String> names = new HashSet<>();
        if (processingPurposes.size() != 0) {
            for (ProcessingPurpose processingPurpose : processingPurposes) {
                if (!StringUtils.isBlank(processingPurpose.getName())) {
                    names.add(processingPurpose.getName());
                } else
                    throw new InvalidRequestException("name could not be empty or null");

            }
            List<ProcessingPurpose> existing =  processingPurposeMongoRepository.findByCountryAndNameList(countryId,organizationId,names);
            if (existing.size() != 0) {
                Set<String> existingNames = new HashSet<>();
                existing.forEach(processingPurpose -> {
                    existingNames.add(processingPurpose.getName());
                });
                names = comparisonUtils.checkForExistingObjectAndRemoveFromList(names, existingNames);
            }
            if (names.size() != 0) {
                for (String name : names) {

                    ProcessingPurpose newProcessingPurpose = new ProcessingPurpose();
                    newProcessingPurpose.setName(name);
                    newProcessingPurpose.setCountryId(countryId);
                    newProcessingPurpose.setOrganizationId(organizationId);
                    newProcessingPurposes.add(newProcessingPurpose);

                }
                newProcessingPurposes = save(newProcessingPurposes);
            }
            result.put("existing", existing);
            result.put("new", newProcessingPurposes);
            return result;
        } else
            throw new InvalidRequestException("list cannot be empty");


    }

    public List<ProcessingPurpose> getAllProcessingPurpose(Long countryId,Long organizationId) {
        return processingPurposeMongoRepository.findAllProcessingPurposes(countryId,organizationId);
    }


    public ProcessingPurpose getProcessingPurpose(Long countryId,Long organizationId,BigInteger id) {

        ProcessingPurpose exist = processingPurposeMongoRepository.findByIdAndNonDeleted(countryId,organizationId,id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            return exist;

        }
    }


    public Boolean deleteProcessingPurpose(Long countryId,Long organizationId,BigInteger id) {

        ProcessingPurpose exist = processingPurposeMongoRepository.findByIdAndNonDeleted(countryId,organizationId,id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            exist.setDeleted(true);
            save(exist);
            return true;

        }
    }


    public ProcessingPurpose updateProcessingPurpose(Long countryId,Long organizationId,BigInteger id, ProcessingPurpose processingPurpose) {


        ProcessingPurpose exist = processingPurposeMongoRepository.findByName(countryId,organizationId,processingPurpose.getName());
        if (Optional.ofNullable(exist).isPresent() ) {
            if (id.equals(exist.getId())) {
                return exist;
            }
            throw new DuplicateDataException("data  exist for  "+processingPurpose.getName());
        } else {
            exist=processingPurposeMongoRepository.findByid(id);
            exist.setName(processingPurpose.getName());
            return save(exist);

        }
    }


    public ProcessingPurpose getProcessingPurposeByName(Long countryId,Long organizationId,String name) {


        if (!StringUtils.isBlank(name)) {
            ProcessingPurpose exist = processingPurposeMongoRepository.findByName(countryId,organizationId,name);
            if (!Optional.ofNullable(exist).isPresent()) {
                throw new DataNotExists("data not exist for name " + name);
            }
            return exist;
        } else
            throw new InvalidRequestException("request param cannot be empty  or null");

    }


    public List<ProcessingPurpose> geProcessingPurposeList(Long countryId,Long organizationId,List<BigInteger> ids) {
        return processingPurposeMongoRepository.getProcessingPurposeList(countryId,organizationId,ids);
    }


}

    
    
    


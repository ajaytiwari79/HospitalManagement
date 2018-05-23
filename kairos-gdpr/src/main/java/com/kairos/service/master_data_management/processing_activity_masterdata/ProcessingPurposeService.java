package com.kairos.service.master_data_management.processing_activity_masterdata;


import com.kairos.custome_exception.DataNotExists;
import com.kairos.custome_exception.DataNotFoundByIdException;
import com.kairos.custome_exception.DuplicateDataException;
import com.kairos.custome_exception.InvalidRequestException;
import com.kairos.persistance.model.master_data_management.processing_activity_masterdata.ProcessingPurpose;
import com.kairos.persistance.repository.master_data_management.processing_activity_masterdata.ProcessingPurposeMongoRepository;
import com.kairos.service.MongoBaseService;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;

@Service
public class ProcessingPurposeService extends MongoBaseService {


    @Inject
    private ProcessingPurposeMongoRepository processingPurposeMongoRepository;


    public Map<String, List<ProcessingPurpose>> createProcessingPurpose(List<ProcessingPurpose> processingPurposes) {
        Map<String, List<ProcessingPurpose>> result = new HashMap<>();
        List<ProcessingPurpose> existing= new ArrayList<>();
        List<ProcessingPurpose> newProcessingPurposes= new ArrayList<>();
        if (processingPurposes.size() != 0) {
            for (ProcessingPurpose processingPurpose : processingPurposes) {

                ProcessingPurpose exist = processingPurposeMongoRepository.findByName(processingPurpose.getName());
                if (Optional.ofNullable(exist).isPresent()) {
                    existing.add(exist);

                } else {
                    ProcessingPurpose newProcessingPurpose = new ProcessingPurpose();
                    newProcessingPurpose.setName(processingPurpose.getName());
                    newProcessingPurposes.add(save(newProcessingPurpose));
                }
            }

            result.put("existing", existing);
            result.put("new", newProcessingPurposes);
            return result;
        } else
            throw new InvalidRequestException("list cannot be empty");


    }

    public List<ProcessingPurpose> getAllProcessingPurpose() {
        List<ProcessingPurpose> result = processingPurposeMongoRepository.findAllProcessingPurposes();
        if (result.size() != 0) {
            return result;

        } else
            throw new DataNotExists("ProcessingPurpose not exist please create purpose ");
    }


    public ProcessingPurpose getProcessingPurpose(BigInteger id) {

        ProcessingPurpose exist = processingPurposeMongoRepository.findByIdAndNonDeleted(id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            return exist;

        }
    }


    public Boolean deleteProcessingPurpose(BigInteger id) {

        ProcessingPurpose exist = processingPurposeMongoRepository.findByIdAndNonDeleted(id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            exist.setDeleted(true);
            save(exist);
            return true;

        }
    }


    public ProcessingPurpose updateProcessingPurpose(BigInteger id, ProcessingPurpose processingPurpose) {


        ProcessingPurpose exist = processingPurposeMongoRepository.findByIdAndNonDeleted(id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            exist.setName(processingPurpose.getName());

            return save(exist);

        }
    }




    public ProcessingPurpose getProcessingPurposeByName(String name) {


        if (!StringUtils.isBlank(name)) {
            ProcessingPurpose exist = processingPurposeMongoRepository.findByName(name);
            if (!Optional.ofNullable(exist).isPresent()) {
                throw new DataNotExists("data not exist for name " + name);
            }
            return exist;
        }
        else
            throw new InvalidRequestException("request param cannot be empty  or null");

    }




    public List<ProcessingPurpose> geProcessingPurposeList(List<BigInteger> ids) {
        return processingPurposeMongoRepository.getProcessingPurposeList(ids);
    }



}

    
    
    


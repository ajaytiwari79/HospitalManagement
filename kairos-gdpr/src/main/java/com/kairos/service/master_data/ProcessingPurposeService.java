package com.kairos.service.master_data;


import com.kairos.custome_exception.DataNotExists;
import com.kairos.custome_exception.DataNotFoundByIdException;
import com.kairos.custome_exception.DuplicateDataException;
import com.kairos.custome_exception.RequestDataNull;
import com.kairos.persistance.model.master_data.ProcessingPurpose;
import com.kairos.persistance.repository.master_data.ProcessingPurposeMongoRepository;
import com.kairos.service.MongoBaseService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Service
public class ProcessingPurposeService extends MongoBaseService {


    @Inject
    private ProcessingPurposeMongoRepository processingPurposeMongoRepository;


    public ProcessingPurpose createProcessingPurpose(ProcessingPurpose processingPurpose) {
        String name = processingPurpose.getName();
        ProcessingPurpose exist = processingPurposeMongoRepository.findByName(name);
        if (Optional.ofNullable(exist).isPresent()) {
            throw new DuplicateDataException("data already exist for name " + name);
        } else {
            ProcessingPurpose newProcessingPurpose = new ProcessingPurpose();
            newProcessingPurpose.setName(name);
            return save(newProcessingPurpose);
        }
    }


    public List<ProcessingPurpose> getAllProcessingPurpose() {
        List<ProcessingPurpose> result = processingPurposeMongoRepository.findAll();
        if (result.size() != 0) {
            return result;

        } else
            throw new DataNotExists("processing purpose not exist please create purpose ");
    }


    public ProcessingPurpose getProcessingPurposeById(BigInteger id) {

        ProcessingPurpose exist = processingPurposeMongoRepository.findByid(id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            return exist;

        }
    }


    public Boolean deleteProcessingPurposeById(BigInteger id) {

        ProcessingPurpose exist = processingPurposeMongoRepository.findByid(id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            processingPurposeMongoRepository.delete(exist);
            return true;

        }
    }


    public ProcessingPurpose updateProcessingPurpose(BigInteger id, ProcessingPurpose processingPurpose) {
        ProcessingPurpose exist = processingPurposeMongoRepository.findByid(id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            exist.setName(processingPurpose.getName());
            return save(exist);

        }

    }


    public List<ProcessingPurpose> processingPurposeList(List<BigInteger> purposeids) {

        if (purposeids != null) {
            return processingPurposeMongoRepository.processingPurposeList(purposeids);

        } else
            throw new RequestDataNull("requested processingPurposeList is null");
    }



}

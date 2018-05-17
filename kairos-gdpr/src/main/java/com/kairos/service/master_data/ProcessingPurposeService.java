package com.kairos.service.master_data;


import com.kairos.custome_exception.DataNotExists;
import com.kairos.custome_exception.DataNotFoundByIdException;
import com.kairos.custome_exception.DuplicateDataException;
import com.kairos.custome_exception.RequestDataNull;
import com.kairos.persistance.model.master_data.ProcessingPurpose;
import com.kairos.persistance.repository.master_data.ProcessingPurposeMongoRepository;
import com.kairos.service.MongoBaseService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Service
public class ProcessingPurposeService extends MongoBaseService {


    @Inject
    private ProcessingPurposeMongoRepository processingPurposeMongoRepository;


    public ProcessingPurpose createProcessingPurpose(String processingPurpose) {
        if (StringUtils.isEmpty(processingPurpose))
        {
            throw new RequestDataNull("requested dataSource  is null or empty");
        }
        ProcessingPurpose exist = processingPurposeMongoRepository.findByName(processingPurpose);
        if (Optional.ofNullable(exist).isPresent()) {
            throw new DuplicateDataException("data already exist for name " + processingPurpose);
        } else {
            ProcessingPurpose newProcessingPurpose = new ProcessingPurpose();
            newProcessingPurpose.setName(processingPurpose);
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
            throw new DataNotFoundByIdException("data not exist for id "+id);
        } else {
            return exist;

        }
    }


    public Boolean deleteProcessingPurposeById(BigInteger id) {

        ProcessingPurpose exist = processingPurposeMongoRepository.findByid(id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id "+id);
        } else {
            processingPurposeMongoRepository.delete(exist);
            return true;

        }
    }


    public ProcessingPurpose updateProcessingPurpose(BigInteger id, String processingPurpose) {

        if (StringUtils.isEmpty(processingPurpose))
        {
            throw new RequestDataNull("requested dataSource  is null or empty");
        }
        ProcessingPurpose exist = processingPurposeMongoRepository.findByid(id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id "+id);
        } else {
            exist.setName(processingPurpose);
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

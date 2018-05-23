package com.kairos.service.master_data_management.processing_activity_masterdata;


import com.kairos.custome_exception.DataNotExists;
import com.kairos.custome_exception.DataNotFoundByIdException;
import com.kairos.custome_exception.InvalidRequestException;
import com.kairos.persistance.model.master_data_management.processing_activity_masterdata.ProcessingLegalBasis;
import com.kairos.persistance.repository.master_data_management.processing_activity_masterdata.ProcessingLegalBasisMongoRepository;
import com.kairos.service.MongoBaseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;

@Service
public class ProcessingLegalBasisService extends MongoBaseService {

    @Inject
    private ProcessingLegalBasisMongoRepository legalBasisMongoRepository;


    public Map<String, List<ProcessingLegalBasis>> createProcessingLegalBasis(List<ProcessingLegalBasis> legalBases) {
        Map<String, List<ProcessingLegalBasis>> result = new HashMap<>();
        List<ProcessingLegalBasis> existing = new ArrayList<>();
        List<ProcessingLegalBasis> newProcessingLegalBasiss = new ArrayList<>();
        if (legalBases.size() != 0) {
            for (ProcessingLegalBasis legalBasis : legalBases) {
                if (!StringUtils.isBlank(legalBasis.getName())) {

                    ProcessingLegalBasis exist = legalBasisMongoRepository.findByName(legalBasis.getName());
                    if (Optional.ofNullable(exist).isPresent()) {
                        existing.add(exist);

                    } else {
                        ProcessingLegalBasis newProcessingLegalBasis = new ProcessingLegalBasis();
                        newProcessingLegalBasis.setName(legalBasis.getName());
                        newProcessingLegalBasiss.add(save(newProcessingLegalBasis));
                    }
                } else
                    throw new InvalidRequestException("name could not be empty or null");

            }

            result.put("existing", existing);
            result.put("new", newProcessingLegalBasiss);
            return result;
        } else
            throw new InvalidRequestException("list cannot be empty");


    }

    public List<ProcessingLegalBasis> getAllProcessingLegalBasis() {
        List<ProcessingLegalBasis> result = legalBasisMongoRepository.findAllProcessingLegalBases();
        if (result.size() != 0) {
            return result;

        } else
            throw new DataNotExists("ProcessingLegalBasis not exist please create purpose ");
    }


    public ProcessingLegalBasis getProcessingLegalBasis(BigInteger id) {

        ProcessingLegalBasis exist = legalBasisMongoRepository.findByIdAndNonDeleted(id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            return exist;

        }
    }


    public Boolean deleteProcessingLegalBasis(BigInteger id) {

        ProcessingLegalBasis exist = legalBasisMongoRepository.findByIdAndNonDeleted(id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            exist.setDeleted(true);
            save(exist);
            return true;

        }
    }


    public ProcessingLegalBasis updateProcessingLegalBasis(BigInteger id, ProcessingLegalBasis legalBasis) {


        ProcessingLegalBasis exist = legalBasisMongoRepository.findByIdAndNonDeleted(id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            exist.setName(legalBasis.getName());

            return save(exist);

        }
    }


    public ProcessingLegalBasis getProcessingLegalBasisByName(String name) {


        if (!StringUtils.isBlank(name)) {
            ProcessingLegalBasis exist = legalBasisMongoRepository.findByName(name);
            if (!Optional.ofNullable(exist).isPresent()) {
                throw new DataNotExists("data not exist for name " + name);
            }
            return exist;
        } else
            throw new InvalidRequestException("request param cannot be empty  or null");

    }


}

    
    
    


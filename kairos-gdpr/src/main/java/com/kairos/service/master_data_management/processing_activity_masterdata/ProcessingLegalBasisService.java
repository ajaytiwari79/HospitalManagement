package com.kairos.service.master_data_management.processing_activity_masterdata;


import com.kairos.custome_exception.DataNotExists;
import com.kairos.custome_exception.DataNotFoundByIdException;
import com.kairos.custome_exception.DuplicateDataException;
import com.kairos.custome_exception.InvalidRequestException;
import com.kairos.persistance.model.master_data_management.processing_activity_masterdata.ProcessingLegalBasis;
import com.kairos.persistance.repository.master_data_management.processing_activity_masterdata.ProcessingLegalBasisMongoRepository;
import com.kairos.service.MongoBaseService;
import com.kairos.utils.userContext.UserContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;

@Service
public class ProcessingLegalBasisService extends MongoBaseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessingLegalBasisService.class);

    @Inject
    private ProcessingLegalBasisMongoRepository legalBasisMongoRepository;


    public Map<String, List<ProcessingLegalBasis>> createProcessingLegalBasis(Long countryId, List<ProcessingLegalBasis> legalBases) {
        Map<String, List<ProcessingLegalBasis>> result = new HashMap<>();
        List<ProcessingLegalBasis> existing = new ArrayList<>();
        Set<String> names = new HashSet<>();
        List<ProcessingLegalBasis> newProcessingLegalBasisList = new ArrayList<>();
        if (legalBases.size() != 0) {
            for (ProcessingLegalBasis legalBasis : legalBases) {
                if (!StringUtils.isBlank(legalBasis.getName())) {
                    names.add(legalBasis.getName());
                } else
                    throw new InvalidRequestException("name could not be empty or null");

            }
            existing = legalBasisMongoRepository.findByCountryAndNameList(countryId, names);
            existing.forEach(item -> names.remove(item.getName()));

            if (names.size() != 0) {
                for (String name : names) {

                    ProcessingLegalBasis newProcessingLegalBasis = new ProcessingLegalBasis();
                    newProcessingLegalBasis.setName(name);
                    newProcessingLegalBasis.setCountryId(countryId);
                    newProcessingLegalBasisList.add(newProcessingLegalBasis);

                }

                newProcessingLegalBasisList = save(newProcessingLegalBasisList);
            }
            result.put("existing", existing);
            result.put("new", newProcessingLegalBasisList);
            return result;
        } else
            throw new InvalidRequestException("list cannot be empty");


    }

    public List<ProcessingLegalBasis> getAllProcessingLegalBasis() {
        return legalBasisMongoRepository.findAllProcessingLegalBases(UserContext.getCountryId());
    }

    public ProcessingLegalBasis getProcessingLegalBasis(Long countryId, BigInteger id) {

        ProcessingLegalBasis exist = legalBasisMongoRepository.findByIdAndNonDeleted(countryId, id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            return exist;

        }
    }


    public Boolean deleteProcessingLegalBasis(BigInteger id) {

        ProcessingLegalBasis exist = legalBasisMongoRepository.findByid(id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            exist.setDeleted(true);
            save(exist);
            return true;

        }
    }


    public ProcessingLegalBasis updateProcessingLegalBasis(BigInteger id, ProcessingLegalBasis legalBasis) {


        ProcessingLegalBasis exist = legalBasisMongoRepository.findByName(UserContext.getCountryId(),legalBasis.getName());
        if (Optional.ofNullable(exist).isPresent() ) {
            if (id.equals(exist.getId())) {
                return exist;
            }
            throw new DuplicateDataException("data  exist for  "+legalBasis.getName());
        } else {
            exist=legalBasisMongoRepository.findByid(id);
            exist.setName(legalBasis.getName());
            return save(exist);

        }
    }


    public ProcessingLegalBasis getProcessingLegalBasisByName(Long countryId, String name) {


        if (!StringUtils.isBlank(name)) {
            ProcessingLegalBasis exist = legalBasisMongoRepository.findByName(countryId, name);
            if (!Optional.ofNullable(exist).isPresent()) {
                throw new DataNotExists("data not exist for name " + name);
            }
            return exist;
        } else
            throw new InvalidRequestException("request param cannot be empty  or null");

    }


}

    
    
    


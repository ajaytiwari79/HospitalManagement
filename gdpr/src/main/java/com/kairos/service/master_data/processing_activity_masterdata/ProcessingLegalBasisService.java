package com.kairos.service.master_data.processing_activity_masterdata;


import com.kairos.custom_exception.DataNotExists;
import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.custom_exception.InvalidRequestException;
import com.kairos.enums.SuggestedDataStatus;
import com.kairos.gdpr.metadata.ProcessingLegalBasisDTO;
import com.kairos.persistance.model.master_data.default_proc_activity_setting.ProcessingLegalBasis;
import com.kairos.persistance.repository.master_data.processing_activity_masterdata.legal_basis.ProcessingLegalBasisMongoRepository;
import com.kairos.response.dto.common.ProcessingLegalBasisResponseDTO;
import com.kairos.service.common.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
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
public class ProcessingLegalBasisService extends MongoBaseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessingLegalBasisService.class);

    @Inject
    private ProcessingLegalBasisMongoRepository legalBasisMongoRepository;

    @Inject
    private ExceptionService exceptionService;


    /**
     * @param countryId
     * @param
     * @param processingLegalBasisDTOS
     * @return return map which contain list of new ProcessingLegalBasis and list of existing ProcessingLegalBasis if ProcessingLegalBasis already exist
     * @description this method create new ProcessingLegalBasis if ProcessingLegalBasis not exist with same name ,
     * and if exist then simply add  ProcessingLegalBasis to existing list and return list ;
     * findByNamesList()  return list of existing ProcessingLegalBasis using collation ,used for case insensitive result
     */

    public Map<String, List<ProcessingLegalBasis>> createProcessingLegalBasis(Long countryId, List<ProcessingLegalBasisDTO> processingLegalBasisDTOS) {

        Map<String, List<ProcessingLegalBasis>> result = new HashMap<>();
        Set<String> legalBasisNames = new HashSet<>();
        if (!processingLegalBasisDTOS.isEmpty()) {
            for (ProcessingLegalBasisDTO legalBasis : processingLegalBasisDTOS) {
                legalBasisNames.add(legalBasis.getName());
            }

            List<ProcessingLegalBasis> existing = findMetaDataByNamesAndCountryId(countryId, legalBasisNames, ProcessingLegalBasis.class);
            legalBasisNames = ComparisonUtils.getNameListForMetadata(existing, legalBasisNames);

            List<ProcessingLegalBasis> newProcessingLegalBasisList = new ArrayList<>();
            if (!legalBasisNames.isEmpty()) {
                for (String name : legalBasisNames) {

                    ProcessingLegalBasis newProcessingLegalBasis = new ProcessingLegalBasis(name);
                    newProcessingLegalBasis.setCountryId(countryId);
                    newProcessingLegalBasisList.add(newProcessingLegalBasis);

                }

                newProcessingLegalBasisList = legalBasisMongoRepository.saveAll(getNextSequence(newProcessingLegalBasisList));
            }
            result.put(EXISTING_DATA_LIST, existing);
            result.put(NEW_DATA_LIST, newProcessingLegalBasisList);
            return result;
        } else
            throw new InvalidRequestException("list cannot be empty");


    }


    /**
     * @param countryId
     * @param
     * @return list of ProcessingLegalBasis
     */

    public List<ProcessingLegalBasisResponseDTO> getAllProcessingLegalBasis(Long countryId) {
        return legalBasisMongoRepository.findAllProcessingLegalBases(countryId);
    }

    /**
     * @param id id of ProcessingLegalBasis
     * @return ProcessingLegalBasis object fetch by given id
     * @throws DataNotFoundByIdException throw exception if ProcessingLegalBasis not found for given id
     */

    public ProcessingLegalBasis getProcessingLegalBasis(Long countryId, BigInteger id) {
        ProcessingLegalBasis exist = legalBasisMongoRepository.findByIdAndNonDeleted(countryId, id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            return exist;

        }
    }


    public Boolean deleteProcessingLegalBasis(Long countryId, BigInteger id) {

        ProcessingLegalBasis processingLegalBasis = legalBasisMongoRepository.findByIdAndNonDeleted(countryId, id);
        if (!Optional.ofNullable(processingLegalBasis).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        }
        delete(processingLegalBasis);
        return true;

    }

    /***
     * @throws DuplicateDataException throw exception if ProcessingLegalBasis data not exist for given id
     * @param countryId
     * @param
     * @param id id of ProcessingLegalBasis
     * @param processingLegalBasisDTO
     * @return ProcessingLegalBasis updated object
     */
    public ProcessingLegalBasisDTO updateProcessingLegalBasis(Long countryId, BigInteger id, ProcessingLegalBasisDTO processingLegalBasisDTO) {
        ProcessingLegalBasis processingLegalBasis = legalBasisMongoRepository.findByName(countryId, processingLegalBasisDTO.getName());
        if (Optional.ofNullable(processingLegalBasis).isPresent()) {
            if (id.equals(processingLegalBasis.getId())) {
                return processingLegalBasisDTO;
            }
            throw new DuplicateDataException("data  exist for  " + processingLegalBasisDTO.getName());
        }
        processingLegalBasis = legalBasisMongoRepository.findByid(id);
        if (!Optional.ofNullable(processingLegalBasis).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Legal Basis", id);
        }
        processingLegalBasis.setName(processingLegalBasisDTO.getName());
        legalBasisMongoRepository.save(processingLegalBasis);
        return processingLegalBasisDTO;


    }

    /**
     * @param countryId
     * @param
     * @param name      name of ProcessingLegalBasis
     * @return ProcessingLegalBasis object fetch on basis of  name
     * @throws DataNotExists throw exception if ProcessingLegalBasis not exist for given name
     */
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


    /**
     *
     * @param countryId  - country id
     * @param processingLegalBasisDTOS - processing legal basis suggested by Unit
     * @return
     */
    public List<ProcessingLegalBasis> saveSuggestedProcessingLegalBasissFromUnit(Long countryId, List<ProcessingLegalBasisDTO> processingLegalBasisDTOS) {

        Set<String> processingLegalBasisNameList = new HashSet<>();
        for (ProcessingLegalBasisDTO ProcessingLegalBasis : processingLegalBasisDTOS) {
            processingLegalBasisNameList.add(ProcessingLegalBasis.getName());
        }
        List<ProcessingLegalBasis> existingProcessingLegalBasiss = findMetaDataByNamesAndCountryId(countryId, processingLegalBasisNameList, ProcessingLegalBasis.class);
        processingLegalBasisNameList = ComparisonUtils.getNameListForMetadata(existingProcessingLegalBasiss, processingLegalBasisNameList);
        List<ProcessingLegalBasis> processingLegalBasisList = new ArrayList<>();
        if (!processingLegalBasisNameList.isEmpty()) {
            for (String name : processingLegalBasisNameList) {

                ProcessingLegalBasis processingLegalBasis = new ProcessingLegalBasis(name);
                processingLegalBasis.setCountryId(countryId);
                processingLegalBasis.setSuggestedDataStatus(SuggestedDataStatus.APPROVAL_PENDING);
                processingLegalBasisList.add(processingLegalBasis);
            }

             legalBasisMongoRepository.saveAll(getNextSequence(processingLegalBasisList));
        }
        return processingLegalBasisList;
    }

}

    
    
    


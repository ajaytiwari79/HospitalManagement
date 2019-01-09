package com.kairos.service.master_data.processing_activity_masterdata;


import com.kairos.commons.custom_exception.DataNotFoundByIdException;
import com.kairos.commons.custom_exception.DuplicateDataException;
import com.kairos.commons.custom_exception.InvalidRequestException;
import com.kairos.enums.gdpr.SuggestedDataStatus;
import com.kairos.dto.gdpr.metadata.ProcessingLegalBasisDTO;
import com.kairos.persistence.model.master_data.default_proc_activity_setting.ProcessingLegalBasis;
import com.kairos.persistence.model.master_data.default_proc_activity_setting.ProcessingLegalBasisMD;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.legal_basis.ProcessingLegalBasisMongoRepository;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.legal_basis.ProcessingLegalBasisRepository;
import com.kairos.response.dto.common.ProcessingLegalBasisResponseDTO;
import com.kairos.service.common.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.utils.ComparisonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.constants.AppConstant.EXISTING_DATA_LIST;
import static com.kairos.constants.AppConstant.NEW_DATA_LIST;


@Service
public class ProcessingLegalBasisService extends MongoBaseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessingLegalBasisService.class);

    @Inject
    private ProcessingLegalBasisMongoRepository legalBasisMongoRepository;

    @Inject
    private ExceptionService exceptionService;

    @Inject
    private ProcessingLegalBasisRepository processingLegalBasisRepository;


    /**
     * @param countryId
     * @param
     * @param processingLegalBasisDTOS
     * @return return map which contain list of new ProcessingLegalBasis and list of existing ProcessingLegalBasis if ProcessingLegalBasis already exist
     * @description this method create new ProcessingLegalBasis if ProcessingLegalBasis not exist with same name ,
     * and if exist then simply add  ProcessingLegalBasis to existing list and return list ;
     * findByNamesList()  return list of existing ProcessingLegalBasis using collation ,used for case insensitive result
     */

    public Map<String, List<ProcessingLegalBasisMD>> createProcessingLegalBasis(Long countryId, List<ProcessingLegalBasisDTO> processingLegalBasisDTOS) {
        //TODO still need to optimize we can get name of list in string from here
        Map<String, List<ProcessingLegalBasisMD>> result = new HashMap<>();
        Set<String> legalBasisNames = new HashSet<>();
        if (!processingLegalBasisDTOS.isEmpty()) {
            for (ProcessingLegalBasisDTO legalBasis : processingLegalBasisDTOS) {
                legalBasisNames.add(legalBasis.getName());
            }

            List<String> nameInLowerCase = legalBasisNames.stream().map(String::toLowerCase)
                    .collect(Collectors.toList());
            //TODO still need to update we can return name of list from here and can apply removeAll on list
            List<ProcessingLegalBasisMD> existing = processingLegalBasisRepository.findByCountryIdAndDeletedAndNameIn(countryId, false, nameInLowerCase);
            legalBasisNames = ComparisonUtils.getNameListForMetadata(existing, legalBasisNames);

            List<ProcessingLegalBasisMD> newProcessingLegalBasisList = new ArrayList<>();
            if (!legalBasisNames.isEmpty()) {
                for (String name : legalBasisNames) {
                    ProcessingLegalBasisMD newProcessingLegalBasis = new ProcessingLegalBasisMD(name,countryId,SuggestedDataStatus.APPROVED);
                    newProcessingLegalBasisList.add(newProcessingLegalBasis);

                }

                newProcessingLegalBasisList = processingLegalBasisRepository.saveAll(newProcessingLegalBasisList);
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
        return processingLegalBasisRepository.findAllByCountryIdAndSortByCreatedDate(countryId);
    }

    /**
     * @param id id of ProcessingLegalBasis
     * @return ProcessingLegalBasis object fetch by given id
     * @throws DataNotFoundByIdException throw exception if ProcessingLegalBasis not found for given id
     */

    public ProcessingLegalBasisMD getProcessingLegalBasis(Long countryId, Long id) {
        ProcessingLegalBasisMD exist = processingLegalBasisRepository.findByIdAndCountryIdAndDeleted(id, countryId, false);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("No data found");
        } else {
            return exist;

        }
    }


    public Boolean deleteProcessingLegalBasis(Long countryId, Long id) {

        Integer resultCount = processingLegalBasisRepository.deleteByIdAndCountryId(id, countryId);
        if (resultCount > 0) {
            LOGGER.info("Legal Basis deleted successfully for id :: {}", id);
        }else{
            throw new DataNotFoundByIdException("No data found");
        }
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
    public ProcessingLegalBasisDTO updateProcessingLegalBasis(Long countryId, Long id, ProcessingLegalBasisDTO processingLegalBasisDTO) {
        ProcessingLegalBasisMD processingLegalBasis = processingLegalBasisRepository.findByNameAndCountryId(processingLegalBasisDTO.getName(), countryId);
        if (Optional.ofNullable(processingLegalBasis).isPresent()) {
            if (id.equals(processingLegalBasis.getId())) {
                return processingLegalBasisDTO;
            }
            throw new DuplicateDataException("data  exist for  " + processingLegalBasisDTO.getName());
        }
        Integer resultCount =  processingLegalBasisRepository.updateLegalBasisName(processingLegalBasisDTO.getName(), id);
        if(resultCount <=0){
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Legal Basis", id);
        }else{
            LOGGER.info("Data updated successfully for id : {} and name updated name is : {}", id, processingLegalBasisDTO.getName());
        }
        return processingLegalBasisDTO;


    }




    /**
     *
     * @param countryId  - country id
     * @param processingLegalBasisDTOS - processing legal basis suggested by Unit
     * @return
     */
    public List<ProcessingLegalBasis> saveSuggestedProcessingLegalBasisFromUnit(Long countryId, List<ProcessingLegalBasisDTO> processingLegalBasisDTOS) {

        Set<String> processingLegalBasisNameList = new HashSet<>();
        for (ProcessingLegalBasisDTO ProcessingLegalBasis : processingLegalBasisDTOS) {
            processingLegalBasisNameList.add(ProcessingLegalBasis.getName());
        }
        List<ProcessingLegalBasis> existingProcessingLegalBasis = findMetaDataByNamesAndCountryId(countryId, processingLegalBasisNameList, ProcessingLegalBasis.class);
        processingLegalBasisNameList = ComparisonUtils.getNameListForMetadata(existingProcessingLegalBasis, processingLegalBasisNameList);
        List<ProcessingLegalBasis> processingLegalBasisList = new ArrayList<>();
        if (!processingLegalBasisNameList.isEmpty()) {
            for (String name : processingLegalBasisNameList) {

                ProcessingLegalBasis processingLegalBasis = new ProcessingLegalBasis(name);
                processingLegalBasis.setCountryId(countryId);
                processingLegalBasis.setSuggestedDataStatus(SuggestedDataStatus.PENDING);
                processingLegalBasis.setSuggestedDate(LocalDate.now());
                processingLegalBasisList.add(processingLegalBasis);
            }

             legalBasisMongoRepository.saveAll(getNextSequence(processingLegalBasisList));
        }
        return processingLegalBasisList;
    }


    /**
     *
     * @param countryId
     * @param processingLegalBasisIds
     * @param suggestedDataStatus
     * @return
     */
    public List<ProcessingLegalBasisMD> updateSuggestedStatusOfProcessingLegalBasisList(Long countryId, Set<Long> processingLegalBasisIds, SuggestedDataStatus suggestedDataStatus) {

        Integer updateCount = processingLegalBasisRepository.updateProcessingLegalBasisStatus(countryId, processingLegalBasisIds);
        if(updateCount > 0){
            LOGGER.info("Legal Basis are updated successfully with ids :: {}", processingLegalBasisIds);
        }else{
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Legal Basis", processingLegalBasisIds);
        }
        return processingLegalBasisRepository.findAllByIds(processingLegalBasisIds);
    }
}

    
    
    


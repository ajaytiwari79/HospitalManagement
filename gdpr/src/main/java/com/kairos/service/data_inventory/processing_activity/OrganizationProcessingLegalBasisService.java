package com.kairos.service.data_inventory.processing_activity;


import com.kairos.custom_exception.DataNotExists;
import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.custom_exception.InvalidRequestException;
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
public class OrganizationProcessingLegalBasisService extends MongoBaseService {


    private static final Logger LOGGER = LoggerFactory.getLogger(OrganizationProcessingLegalBasisService.class);

    @Inject
    private ProcessingLegalBasisMongoRepository legalBasisMongoRepository;

    @Inject
    private ExceptionService exceptionService;

    /**
     * @param organizationId
     * @param legalBasisDTOList
     * @return return map which contain list of new ProcessingLegalBasis and list of existing ProcessingLegalBasis if ProcessingLegalBasis already exist
     * @description this method create new ProcessingLegalBasis if ProcessingLegalBasis not exist with same name ,
     * and if exist then simply add  ProcessingLegalBasis to existing list and return list ;
     * findByNamesAndCountryId()  return list of existing ProcessingLegalBasis using collation ,used for case insensitive result
     */
    public Map<String, List<ProcessingLegalBasis>> createProcessingLegalBasis(Long organizationId, List<ProcessingLegalBasisDTO> legalBasisDTOList) {

        Map<String, List<ProcessingLegalBasis>> result = new HashMap<>();
        Set<String> legalBasisNames = new HashSet<>();
        if (!legalBasisDTOList.isEmpty()) {
            for (ProcessingLegalBasisDTO legalBasis : legalBasisDTOList) {

                legalBasisNames.add(legalBasis.getName());
            }
            List<ProcessingLegalBasis> existing = findAllByNameAndOrganizationId(organizationId, legalBasisNames, ProcessingLegalBasis.class);
            legalBasisNames = ComparisonUtils.getNameListForMetadata(existing, legalBasisNames);

            List<ProcessingLegalBasis> newProcessingLegalBasisList = new ArrayList<>();
            if (legalBasisNames.size() != 0) {
                for (String name : legalBasisNames) {

                    ProcessingLegalBasis newProcessingLegalBasis = new ProcessingLegalBasis(name);
                    newProcessingLegalBasis.setOrganizationId(organizationId);
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
     * @param organizationId
     * @return list of ProcessingLegalBasis
     */
    public List<ProcessingLegalBasisResponseDTO> getAllProcessingLegalBasis(Long organizationId) {
        return legalBasisMongoRepository.findAllOrganizationProcessingLegalBases(organizationId);
    }

    /**
     * @param organizationId
     * @param id             id of ProcessingLegalBasis
     * @return ProcessingLegalBasis object fetch by given id
     * @throws DataNotFoundByIdException throw exception if ProcessingLegalBasis not found for given id
     */
    public ProcessingLegalBasis getProcessingLegalBasis(Long organizationId, BigInteger id) {

        ProcessingLegalBasis exist = legalBasisMongoRepository.findByOrganizationIdAndId(organizationId, id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            return exist;

        }
    }


    public Boolean deleteProcessingLegalBasis(Long organizationId, BigInteger id) {

        ProcessingLegalBasis processingLegalBasis = legalBasisMongoRepository.findByOrganizationIdAndId(organizationId, id);
        if (!Optional.ofNullable(processingLegalBasis).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            delete(processingLegalBasis);
            return true;

        }
    }

    /***
     * @throws DuplicateDataException throw exception if ProcessingLegalBasis data not exist for given id
     * @param organizationId
     * @param id id of ProcessingLegalBasis
     * @param legalBasisDTO
     * @return ProcessingLegalBasis updated object
     */
    public ProcessingLegalBasisDTO updateProcessingLegalBasis(Long organizationId, BigInteger id, ProcessingLegalBasisDTO legalBasisDTO) {

        ProcessingLegalBasis processingLegalBasis = legalBasisMongoRepository.findByNameAndOrganizationId(organizationId, legalBasisDTO.getName());
        if (Optional.ofNullable(processingLegalBasis).isPresent()) {
            if (id.equals(processingLegalBasis.getId())) {
                return legalBasisDTO;
            }
            throw new DuplicateDataException("data  exist for  " + legalBasisDTO.getName());
        }
        processingLegalBasis = legalBasisMongoRepository.findByid(id);
        if (!Optional.ofNullable(processingLegalBasis).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Legal Basis", id);
        }
        processingLegalBasis.setName(legalBasisDTO.getName());
        legalBasisMongoRepository.save(processingLegalBasis);
        return legalBasisDTO;


    }

    /**
     * @param organizationId
     * @param name           name of ProcessingLegalBasis
     * @return ProcessingLegalBasis object fetch on basis of  name
     * @throws DataNotExists throw exception if ProcessingLegalBasis not exist for given name
     */
    public ProcessingLegalBasis getProcessingLegalBasisByName(Long organizationId, String name) {


        if (!StringUtils.isBlank(name)) {
            ProcessingLegalBasis exist = legalBasisMongoRepository.findByNameAndOrganizationId(organizationId, name);
            if (!Optional.ofNullable(exist).isPresent()) {
                throw new DataNotExists("data not exist for name " + name);
            }
            return exist;
        } else
            throw new InvalidRequestException("request param cannot be empty  or null");

    }


}

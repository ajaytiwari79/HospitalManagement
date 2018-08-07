package com.kairos.service.data_inventory.processing_activity;


import com.kairos.custom_exception.DataNotExists;
import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.custom_exception.InvalidRequestException;
import com.kairos.persistance.model.master_data.default_proc_activity_setting.ProcessingLegalBasis;
import com.kairos.persistance.repository.master_data.processing_activity_masterdata.legal_basis.ProcessingLegalBasisMongoRepository;
import com.kairos.response.dto.common.ProcessingLegalBasisResponseDTO;
import com.kairos.service.common.MongoBaseService;
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

    /**
     * @description this method create new ProcessingLegalBasis if ProcessingLegalBasis not exist with same name ,
     * and if exist then simply add  ProcessingLegalBasis to existing list and return list ;
     * findByNamesAndCountryId()  return list of existing ProcessingLegalBasis using collation ,used for case insensitive result
     * @param organizationId
     * @param legalBasisList
     * @return return map which contain list of new ProcessingLegalBasis and list of existing ProcessingLegalBasis if ProcessingLegalBasis already exist
     *
     */
    public Map<String, List<ProcessingLegalBasis>> createProcessingLegalBasis( Long organizationId, List<ProcessingLegalBasis> legalBasisList) {

        Map<String, List<ProcessingLegalBasis>> result = new HashMap<>();
        Set<String> legalBasisNames = new HashSet<>();
        if (legalBasisList.size() != 0) {
            for (ProcessingLegalBasis legalBasis : legalBasisList) {
                if (!StringUtils.isBlank(legalBasis.getName())) {
                    legalBasisNames.add(legalBasis.getName());
                } else
                    throw new InvalidRequestException("name could not be empty or null");

            }
            List<ProcessingLegalBasis> existing =  findAllByNameAndOrganizationId(organizationId,legalBasisNames,ProcessingLegalBasis.class);
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
     * @throws DataNotFoundByIdException throw exception if ProcessingLegalBasis not found for given id
     * @param organizationId
     * @param id id of ProcessingLegalBasis
     * @return ProcessingLegalBasis object fetch by given id
     */
    public ProcessingLegalBasis getProcessingLegalBasis(Long organizationId,BigInteger id) {

        ProcessingLegalBasis exist = legalBasisMongoRepository.findByOrganizationIdAndId(organizationId,id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            return exist;

        }
    }


    public Boolean deleteProcessingLegalBasis(Long organizationId,BigInteger id) {

        ProcessingLegalBasis exist = legalBasisMongoRepository.findByOrganizationIdAndId(organizationId,id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            delete(exist);
            return true;

        }
    }

    /***
     * @throws DuplicateDataException throw exception if ProcessingLegalBasis data not exist for given id
     * @param organizationId
     * @param id id of ProcessingLegalBasis
     * @param legalBasis
     * @return ProcessingLegalBasis updated object
     */
    public ProcessingLegalBasis updateProcessingLegalBasis(Long organizationId,BigInteger id, ProcessingLegalBasis legalBasis) {


        ProcessingLegalBasis exist = legalBasisMongoRepository.findByNameAndOrganizationId(organizationId,legalBasis.getName());
        if (Optional.ofNullable(exist).isPresent() ) {
            if (id.equals(exist.getId())) {
                return exist;
            }
            throw new DuplicateDataException("data  exist for  "+legalBasis.getName());
        } else {
            exist=legalBasisMongoRepository.findByid(id);
            exist.setName(legalBasis.getName());
            return legalBasisMongoRepository.save(getNextSequence(exist));

        }
    }

    /**
     * @throws DataNotExists throw exception if ProcessingLegalBasis not exist for given name
     * @param organizationId
     * @param name name of ProcessingLegalBasis
     * @return ProcessingLegalBasis object fetch on basis of  name
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

package com.kairos.service.data_inventory.processing_activity;


import com.kairos.custom_exception.DataNotExists;
import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.custom_exception.InvalidRequestException;
import com.kairos.persistance.model.master_data.default_proc_activity_setting.ProcessingPurpose;
import com.kairos.persistance.repository.master_data.processing_activity_masterdata.processing_purpose.ProcessingPurposeMongoRepository;
import com.kairos.response.dto.common.ProcessingPurposeResponseDTO;
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
public class OrganizationProcessingPurposeService extends MongoBaseService {



    private static final Logger LOGGER = LoggerFactory.getLogger(OrganizationProcessingPurposeService.class);

    @Inject
    private ProcessingPurposeMongoRepository processingPurposeMongoRepository;



    /**
     * @description this method create new ProcessingPurpose if ProcessingPurpose not exist with same name ,
     * and if exist then simply add  ProcessingPurpose to existing list and return list ;
     * findByNamesAndCountryId()  return list of existing ProcessingPurpose using collation ,used for case insensitive result
     * @param organizationId
     * @param processingPurposes
     * @return return map which contain list of new ProcessingPurpose and list of existing ProcessingPurpose if ProcessingPurpose already exist
     *
     */
    public Map<String, List<ProcessingPurpose>> createProcessingPurpose( Long organizationId, List<ProcessingPurpose> processingPurposes) {

        Map<String, List<ProcessingPurpose>> result = new HashMap<>();
        Set<String> processingPurposesNames = new HashSet<>();
        if (processingPurposes.size() != 0) {
            for (ProcessingPurpose processingPurpose : processingPurposes) {
                if (!StringUtils.isBlank(processingPurpose.getName())) {
                    processingPurposesNames.add(processingPurpose.getName());
                } else
                    throw new InvalidRequestException("name could not be empty or null");

            }
            List<ProcessingPurpose> existing =  findAllByNameAndOrganizationId(organizationId,processingPurposesNames,ProcessingPurpose.class);
            processingPurposesNames = ComparisonUtils.getNameListForMetadata(existing, processingPurposesNames);

            List<ProcessingPurpose> newProcessingPurposes = new ArrayList<>();
            if (processingPurposesNames.size() != 0) {
                for (String name : processingPurposesNames) {

                    ProcessingPurpose newProcessingPurpose = new ProcessingPurpose(name);
                    newProcessingPurpose.setOrganizationId(organizationId);
                    newProcessingPurposes.add(newProcessingPurpose);

                }
                newProcessingPurposes = processingPurposeMongoRepository.saveAll(getNextSequence(newProcessingPurposes));
            }
            result.put(EXISTING_DATA_LIST, existing);
            result.put(NEW_DATA_LIST, newProcessingPurposes);
            return result;
        } else
            throw new InvalidRequestException("list cannot be empty");


    }


    /**
     * @param organizationId
     * @return list of ProcessingPurpose
     */
    public List<ProcessingPurposeResponseDTO> getAllProcessingPurpose( Long organizationId) {
        return processingPurposeMongoRepository.findAllOrganizationProcessingPurposes(organizationId);
    }

    /**
     * @throws DataNotFoundByIdException throw exception if ProcessingPurpose not found for given id
     * @param organizationId
     * @param id id of ProcessingPurpose
     * @return ProcessingPurpose object fetch by given id
     */
    public ProcessingPurpose getProcessingPurpose(Long organizationId,BigInteger id) {

        ProcessingPurpose exist = processingPurposeMongoRepository.findByOrganizationIdAndId(organizationId,id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            return exist;

        }
    }


    public Boolean deleteProcessingPurpose(Long organizationId,BigInteger id) {

        ProcessingPurpose exist = processingPurposeMongoRepository.findByOrganizationIdAndId(organizationId,id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            delete(exist);
            return true;

        }
    }

    /***
     * @throws DuplicateDataException throw exception if ProcessingPurpose data not exist for given id
     * @param organizationId
     * @param id id of ProcessingPurpose
     * @param processingPurpose
     * @return ProcessingPurpose updated object
     */
    public ProcessingPurpose updateProcessingPurpose( Long organizationId, BigInteger id, ProcessingPurpose processingPurpose) {


        ProcessingPurpose exist = processingPurposeMongoRepository.findByOrganizationIdAndName(organizationId,processingPurpose.getName());
        if (Optional.ofNullable(exist).isPresent() ) {
            if (id.equals(exist.getId())) {
                return exist;
            }
            throw new DuplicateDataException("data  exist for  "+processingPurpose.getName());
        } else {
            exist=processingPurposeMongoRepository.findByid(id);
            exist.setName(processingPurpose.getName());
            return processingPurposeMongoRepository.save(exist);

        }
    }


    /**
     * @throws DataNotExists throw exception if ProcessingPurpose not exist for given name
     * @param organizationId
     * @param name name of ProcessingPurpose
     * @return ProcessingPurpose object fetch on basis of  name
     */
    public ProcessingPurpose getProcessingPurposeByName(Long organizationId,String name) {


        if (!StringUtils.isBlank(name)) {
            ProcessingPurpose exist = processingPurposeMongoRepository.findByOrganizationIdAndName(organizationId,name);
            if (!Optional.ofNullable(exist).isPresent()) {
                throw new DataNotExists("data not exist for name " + name);
            }
            return exist;
        } else
            throw new InvalidRequestException("request param cannot be empty  or null");

    }




}

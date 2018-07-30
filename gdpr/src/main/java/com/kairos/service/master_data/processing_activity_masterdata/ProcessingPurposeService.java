package com.kairos.service.master_data.processing_activity_masterdata;


import com.kairos.custom_exception.DataNotExists;
import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.custom_exception.InvalidRequestException;
import com.kairos.dto.data_inventory.ProcessingActivityDTO;
import com.kairos.dto.metadata.ProcessingPurposeDTO;
import com.kairos.persistance.model.master_data.default_proc_activity_setting.ProcessingPurpose;
import com.kairos.persistance.repository.master_data.processing_activity_masterdata.processing_purpose.ProcessingPurposeMongoRepository;
import com.kairos.response.dto.metadata.ProcessingPurposeResponseDTO;
import com.kairos.service.common.MongoBaseService;
import com.kairos.utils.ComparisonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.StringUtils;
import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;
import static com.kairos.constants.AppConstant.EXISTING_DATA_LIST;
import static com.kairos.constants.AppConstant.NEW_DATA_LIST;


@Service
public class ProcessingPurposeService extends MongoBaseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessingPurposeService.class);

    @Inject
    private ProcessingPurposeMongoRepository processingPurposeMongoRepository;


    @Inject
    private ComparisonUtils comparisonUtils;


    /**
     * @description this method create new ProcessingPurpose if ProcessingPurpose not exist with same name ,
     * and if exist then simply add  ProcessingPurpose to existing list and return list ;
     * findByNamesList()  return list of existing ProcessingPurpose using collation ,used for case insensitive result
     * @param countryId
     * @param organizationId
     * @param processingPurposes
     * @return return map which contain list of new ProcessingPurpose and list of existing ProcessingPurpose if ProcessingPurpose already exist
     *
     */
    public Map<String, List<ProcessingPurpose>> createProcessingPurpose(Long countryId,Long organizationId,List<ProcessingPurpose> processingPurposes) {

        Map<String, List<ProcessingPurpose>> result = new HashMap<>();
        Set<String> processingPurposesNames = new HashSet<>();
        if (processingPurposes.size() != 0) {
            for (ProcessingPurpose processingPurpose : processingPurposes) {
                if (!StringUtils.isBlank(processingPurpose.getName())) {
                    processingPurposesNames.add(processingPurpose.getName());
                } else
                    throw new InvalidRequestException("name could not be empty or null");

            }
            List<ProcessingPurpose> existing =  findByNamesList(countryId,organizationId,processingPurposesNames,ProcessingPurpose.class);
            processingPurposesNames = comparisonUtils.getNameListForMetadata(existing, processingPurposesNames);

            List<ProcessingPurpose> newProcessingPurposes = new ArrayList<>();
            if (processingPurposesNames.size() != 0) {
                for (String name : processingPurposesNames) {

                    ProcessingPurpose newProcessingPurpose = new ProcessingPurpose(name,countryId);
                    newProcessingPurpose.setOrganizationId(organizationId);
                    newProcessingPurposes.add(newProcessingPurpose);

                }
                newProcessingPurposes = processingPurposeMongoRepository.saveAll(sequenceGenerator(newProcessingPurposes));
            }
            result.put(EXISTING_DATA_LIST, existing);
            result.put(NEW_DATA_LIST, newProcessingPurposes);
            return result;
        } else
            throw new InvalidRequestException("list cannot be empty");


    }


    /**
     *
     * @param countryId
     * @param organizationId
     * @return list of ProcessingPurpose
     */
    public List<ProcessingPurposeResponseDTO> getAllProcessingPurpose(Long countryId,Long organizationId) {
        return processingPurposeMongoRepository.findAllProcessingPurposes(countryId,organizationId);
    }

    /**
     * @throws DataNotFoundByIdException throw exception if ProcessingPurpose not found for given id
     * @param countryId
     * @param organizationId
     * @param id id of ProcessingPurpose
     * @return ProcessingPurpose object fetch by given id
     */
    public ProcessingPurpose getProcessingPurpose(Long countryId,Long organizationId,BigInteger id) {

        ProcessingPurpose exist = processingPurposeMongoRepository.findByIdAndNonDeleted(countryId,organizationId,id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            return exist;

        }
    }


    public Boolean deleteProcessingPurpose(Long countryId,Long organizationId,BigInteger id) {

        ProcessingPurpose exist = processingPurposeMongoRepository.findByIdAndNonDeleted(countryId,organizationId,id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            delete(exist);
            return true;

        }
    }

    /***
     * @throws  DuplicateDataException throw exception if ProcessingPurpose data not exist for given id
     * @param countryId
     * @param organizationId
     * @param id id of ProcessingPurpose
     * @param processingPurpose
     * @return ProcessingPurpose updated object
     */
    public ProcessingPurpose updateProcessingPurpose(Long countryId,Long organizationId,BigInteger id, ProcessingPurpose processingPurpose) {


        ProcessingPurpose exist = processingPurposeMongoRepository.findByName(countryId,organizationId,processingPurpose.getName());
        if (Optional.ofNullable(exist).isPresent() ) {
            if (id.equals(exist.getId())) {
                return exist;
            }
            throw new DuplicateDataException("data  exist for  "+processingPurpose.getName());
        } else {
            exist=processingPurposeMongoRepository.findByid(id);
            exist.setName(processingPurpose.getName());
            return processingPurposeMongoRepository.save(sequenceGenerator(exist));

        }
    }


    /**
     * @throws DataNotExists throw exception if ProcessingPurpose not exist for given name
     * @param countryId
     * @param organizationId
     * @param name name of ProcessingPurpose
     * @return ProcessingPurpose object fetch on basis of  name
     */
    public ProcessingPurpose getProcessingPurposeByName(Long countryId,Long organizationId,String name) {


        if (!StringUtils.isBlank(name)) {
            ProcessingPurpose exist = processingPurposeMongoRepository.findByName(countryId,organizationId,name);
            if (!Optional.ofNullable(exist).isPresent()) {
                throw new DataNotExists("data not exist for name " + name);
            }
            return exist;
        } else
            throw new InvalidRequestException("request param cannot be empty  or null");

    }


    public List<ProcessingPurpose> geProcessingPurposeList(Long countryId,Long organizationId,List<BigInteger> ids) {
        return processingPurposeMongoRepository.getProcessingPurposeList(countryId,organizationId,ids);
    }




    public List<BigInteger> createProcessingPurposeForOrganizationOnInheritingFromParentOrganization(Long countryId, Long organizationId, ProcessingActivityDTO processingActivityDTO) {

        List<ProcessingPurposeDTO> processingPurposeDTOs = processingActivityDTO.getProcessingPurposes();
        List<ProcessingPurpose> newInheritProcessingPurposeFromCountry = new ArrayList<>();
        List<BigInteger> processingPurposeIds = new ArrayList<>();
        for (ProcessingPurposeDTO   processingPurposeDTO: processingPurposeDTOs) {
            if (!processingPurposeDTO.getOrganizationId().equals(organizationId)) {
                ProcessingPurpose processingPurpose = new ProcessingPurpose(processingPurposeDTO.getName(), countryId);
                processingPurpose.setOrganizationId(organizationId);
                newInheritProcessingPurposeFromCountry.add(processingPurpose);
            } else {
                processingPurposeIds.add(processingPurposeDTO.getId());
            }
        }
        newInheritProcessingPurposeFromCountry = processingPurposeMongoRepository.saveAll(sequenceGenerator(newInheritProcessingPurposeFromCountry));
        newInheritProcessingPurposeFromCountry.forEach(dataSource -> {
            processingPurposeIds.add(dataSource.getId());
        });
        return processingPurposeIds;
    }


    /**
     *
     * @param countryId
     * @param parentOrganizationId -id of parent organization
     * @param unitId - id of cuurent organization
     * @return method return list of processingPurposes (organzation processing purpose and processing purposes which were not inherited by organization from parent till now )
     */
    public List<ProcessingPurposeResponseDTO> getAllNotInheritedProcessingPurposesFromParentOrgAndUnitProcessingPurpose(Long countryId, Long parentOrganizationId, Long unitId) {

       return processingPurposeMongoRepository.getAllNotInheritedProcessingPurposesFromParentOrgAndUnitProcessingPurpose(countryId,parentOrganizationId,unitId);

    }


}

    
    
    


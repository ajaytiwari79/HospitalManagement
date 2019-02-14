package com.kairos.service.data_inventory.processing_activity;



import com.kairos.commons.custom_exception.DataNotFoundByIdException;
import com.kairos.commons.custom_exception.DuplicateDataException;
import com.kairos.commons.custom_exception.InvalidRequestException;
import com.kairos.dto.gdpr.metadata.ProcessingLegalBasisDTO;
import com.kairos.persistence.model.master_data.default_proc_activity_setting.ProcessingLegalBasis;
import com.kairos.persistence.repository.data_inventory.processing_activity.ProcessingActivityRepository;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.legal_basis.ProcessingLegalBasisRepository;
import com.kairos.response.dto.common.ProcessingLegalBasisResponseDTO;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.master_data.processing_activity_masterdata.ProcessingLegalBasisService;
import com.kairos.utils.ComparisonUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.constants.AppConstant.EXISTING_DATA_LIST;
import static com.kairos.constants.AppConstant.NEW_DATA_LIST;

@Service
public class OrganizationProcessingLegalBasisService{


    private static final Logger LOGGER = LoggerFactory.getLogger(OrganizationProcessingLegalBasisService.class);

    @Inject
    private ProcessingLegalBasisRepository processingLegalBasisRepository;

    @Inject
    private ProcessingLegalBasisService processingLegalBasisService;
    @Inject
    private ExceptionService exceptionService;

    @Inject
    private ProcessingActivityRepository processingActivityRepository;

    /**
     * @param organizationId
     * @param legalBasisDTOList
     * @return return map which contain list of new ProcessingLegalBasis and list of existing ProcessingLegalBasis if ProcessingLegalBasis already exist
     * @description this method create new ProcessingLegalBasis if ProcessingLegalBasis not exist with same name ,
     * and if exist then simply add  ProcessingLegalBasis to existing list and return list ;
     * findMetaDataByNamesAndCountryId()  return list of existing ProcessingLegalBasis using collation ,used for case insensitive result
     */
    public Map<String, List<ProcessingLegalBasis>> createProcessingLegalBasis(Long organizationId, List<ProcessingLegalBasisDTO> legalBasisDTOList) {

        Map<String, List<ProcessingLegalBasis>> result = new HashMap<>();
        Set<String> legalBasisNames = new HashSet<>();
        if (!legalBasisDTOList.isEmpty()) {
            for (ProcessingLegalBasisDTO legalBasis : legalBasisDTOList) {
                legalBasisNames.add(legalBasis.getName());
            }
            List<String> nameInLowerCase = legalBasisNames.stream().map(String::toLowerCase)
                    .collect(Collectors.toList());
            //TODO still need to update we can return name of list from here and can apply removeAll on list
            List<ProcessingLegalBasis> existing = processingLegalBasisRepository.findByOrganizationIdAndDeletedAndNameIn(organizationId, false, nameInLowerCase);
            legalBasisNames = ComparisonUtils.getNameListForMetadata(existing, legalBasisNames);

            List<ProcessingLegalBasis> newProcessingLegalBasisList = new ArrayList<>();
            if (legalBasisNames.size() != 0) {
                for (String name : legalBasisNames) {
                    ProcessingLegalBasis newProcessingLegalBasis = new ProcessingLegalBasis(name);
                    newProcessingLegalBasis.setOrganizationId(organizationId);
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
     * @param organizationId
     * @return list of ProcessingLegalBasis
     */
    public List<ProcessingLegalBasisResponseDTO> getAllProcessingLegalBasis(Long organizationId) {
        return processingLegalBasisRepository.findAllByOrganizationIdAndSortByCreatedDate(organizationId);
    }

    /**
     * @param organizationId
     * @param id             id of ProcessingLegalBasis
     * @return ProcessingLegalBasis object fetch by given id
     * @throws DataNotFoundByIdException throw exception if ProcessingLegalBasis not found for given id
     */
    public ProcessingLegalBasis getProcessingLegalBasis(Long organizationId, Long id) {

        ProcessingLegalBasis exist = processingLegalBasisRepository.findByIdAndOrganizationIdAndDeletedFalse(id, organizationId);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        }
        return exist;
    }


    public Boolean deleteProcessingLegalBasis(Long unitId, Long legalBasisId) {

        List<String> processingActivities = processingActivityRepository.findAllProcessingActivityLinkedWithProcessingLegalBasis(unitId, legalBasisId);
        if (!processingActivities.isEmpty()) {
            exceptionService.metaDataLinkedWithProcessingActivityException("message.metaData.linked.with.ProcessingActivity", "Processing Legal basis", StringUtils.join(processingActivities, ','));
        }
        processingLegalBasisRepository.deleteByIdAndOrganizationId(legalBasisId, unitId);
        return true;
    }

    /***
     * @throws DuplicateDataException throw exception if ProcessingLegalBasis data not exist for given id
     * @param organizationId
     * @param id id of ProcessingLegalBasis
     * @param legalBasisDTO
     * @return ProcessingLegalBasis updated object
     */
    public ProcessingLegalBasisDTO updateProcessingLegalBasis(Long organizationId, Long id, ProcessingLegalBasisDTO legalBasisDTO) {

        ProcessingLegalBasis processingLegalBasis = processingLegalBasisRepository.findByOrganizationIdAndDeletedAndName(organizationId,  legalBasisDTO.getName());
        if (Optional.ofNullable(processingLegalBasis).isPresent()) {
            if (id.equals(processingLegalBasis.getId())) {
                return legalBasisDTO;
            }
            exceptionService.duplicateDataException("message.duplicate", "Legal Basis", processingLegalBasis.getName());
        }
        Integer resultCount =  processingLegalBasisRepository.updateMetadataName(legalBasisDTO.getName(), id, organizationId);
        if(resultCount <=0){
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Legal Basis", id);
        }else{
            LOGGER.info("Data updated successfully for id : {} and name updated name is : {}", id, legalBasisDTO.getName());
        }
        return legalBasisDTO;


    }

    public Map<String, List<ProcessingLegalBasis>> saveAndSuggestProcessingLegalBasis(Long countryId, Long organizationId, List<ProcessingLegalBasisDTO> processingLegalBasisDTOS) {

        Map<String, List<ProcessingLegalBasis>> result = createProcessingLegalBasis(organizationId, processingLegalBasisDTOS);
        List<ProcessingLegalBasis> masterProcessingLegalBasisSuggestedByUnit = processingLegalBasisService.saveSuggestedProcessingLegalBasisFromUnit(countryId, processingLegalBasisDTOS);
        if (!masterProcessingLegalBasisSuggestedByUnit.isEmpty()) {
            result.put("SuggestedData", masterProcessingLegalBasisSuggestedByUnit);
        }
        return result;
    }

}

package com.kairos.service.data_inventory.processing_activity;



import com.kairos.commons.custom_exception.DataNotFoundByIdException;
import com.kairos.commons.custom_exception.DuplicateDataException;
import com.kairos.commons.utils.ObjectMapperUtils;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
    public List<ProcessingLegalBasisDTO> createProcessingLegalBasis(Long organizationId, List<ProcessingLegalBasisDTO> legalBasisDTOList) {
        Set<String> existingProcessingLegalBasisNames = processingLegalBasisRepository.findNameByOrganizationIdAndDeleted(organizationId);
        Set<String> legalBasisNames = ComparisonUtils.getNewMetaDataNames(legalBasisDTOList,existingProcessingLegalBasisNames );
            List<ProcessingLegalBasis> processingLegalBases = new ArrayList<>();
            if (!legalBasisNames.isEmpty()) {
                for (String name : legalBasisNames) {
                    ProcessingLegalBasis legalBasis = new ProcessingLegalBasis(name);
                    legalBasis.setOrganizationId(organizationId);
                    processingLegalBases.add(legalBasis);
                }

                 processingLegalBasisRepository.saveAll(processingLegalBases);
            }
           return ObjectMapperUtils.copyPropertiesOfListByMapper(processingLegalBases,ProcessingLegalBasisDTO.class);
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


    public Boolean deleteProcessingLegalBasis(Long organizationId, Long legalBasisId) {

        List<String> processingActivities = processingActivityRepository.findAllProcessingActivityLinkedWithProcessingLegalBasis(organizationId, legalBasisId);
        if (!processingActivities.isEmpty()) {
            exceptionService.metaDataLinkedWithProcessingActivityException("message.metaData.linked.with.ProcessingActivity", "Processing Legal basis", StringUtils.join(processingActivities, ','));
        }
        processingLegalBasisRepository.deleteByIdAndOrganizationId(legalBasisId, organizationId);
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

    public List<ProcessingLegalBasisDTO> saveAndSuggestProcessingLegalBasis(Long countryId, Long organizationId, List<ProcessingLegalBasisDTO> processingLegalBasisDTOS) {

        List<ProcessingLegalBasisDTO> result = createProcessingLegalBasis(organizationId, processingLegalBasisDTOS);
        processingLegalBasisService.saveSuggestedProcessingLegalBasisFromUnit(countryId, processingLegalBasisDTOS);
        return result;
    }

}

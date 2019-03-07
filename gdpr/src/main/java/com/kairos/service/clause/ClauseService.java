package com.kairos.service.clause;


import com.kairos.commons.custom_exception.DataNotFoundByIdException;
import com.kairos.commons.custom_exception.DuplicateDataException;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.gdpr.master_data.ClauseDTO;
import com.kairos.dto.gdpr.master_data.MasterClauseDTO;
import com.kairos.persistence.model.clause.Clause;
import com.kairos.persistence.model.clause.MasterClause;
import com.kairos.persistence.model.clause.OrganizationClause;
import com.kairos.persistence.model.clause_tag.ClauseTag;
import com.kairos.persistence.model.embeddables.*;
import com.kairos.persistence.model.template_type.TemplateType;
import com.kairos.persistence.repository.clause.ClauseRepository;
import com.kairos.persistence.repository.clause_tag.ClauseTagRepository;
import com.kairos.persistence.repository.template_type.TemplateTypeRepository;
import com.kairos.response.dto.clause.ClauseResponseDTO;
import com.kairos.response.dto.clause.UnitLevelClauseResponseDTO;
import com.kairos.service.agreement_template.PolicyAgreementTemplateService;
import com.kairos.service.clause_tag.ClauseTagService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.template_type.TemplateTypeService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.*;


@Service
public class ClauseService{

    private static final Logger LOGGER = LoggerFactory.getLogger(ClauseService.class);

    @Inject
    private ClauseTagService clauseTagService;

    @Inject
    private ExceptionService exceptionService;

    @Inject
    private TemplateTypeService templateTypeService;

    @Inject
    private PolicyAgreementTemplateService policyAgreementTemplateService;

    @Inject
    private ClauseRepository clauseRepository;

    @Inject
    private ClauseTagRepository clauseTagRepository;

    @Inject
    private TemplateTypeRepository templateTypeRepository;



    /**
     * @param referenceId country id or unit id
     * @param isOrganization    boolean to verify is reference id is unitId or not
     * @param clauseDto   contain data about clause and template type which belong to clause
     * @return clause  object , specific to organization type ,sub types ,Service Category and Sub Service Category
     * @throws DuplicateDataException : if clause already exist for id ,if account type is not selected}
     * @desciption this method create clause ,and add tags to clause if tag already exist then simply add tag and if not then create tag and then add to clause
     */
    public <E extends ClauseDTO> E createClause(Long referenceId, boolean isOrganization, E clauseDto) {

        Clause previousClause = isOrganization ? clauseRepository.findByUnitIdAndTitleAndDescription(referenceId, clauseDto.getTitle(), clauseDto.getDescription()) : clauseRepository.findByCountryIdAndTitleAndDescription(referenceId, clauseDto.getTitle(), clauseDto.getDescription());
        if (Optional.ofNullable(previousClause).isPresent()) {
            exceptionService.duplicateDataException("message.duplicate", "clause", clauseDto.getTitle().toLowerCase());
        }
        if(isOrganization) {
            previousClause = prepareOrganizationClauseData(referenceId, clauseDto, new OrganizationClause());
        }else{
            previousClause = prepareMasterClauseData(referenceId, clauseDto, new MasterClause());
        }
        clauseRepository.save(previousClause);
        clauseDto.setId(previousClause.getId());
        return clauseDto;
    }

    private <E extends ClauseDTO> Clause prepareOrganizationClauseData(Long referenceId, E clauseDto, OrganizationClause clause) {
        List<ClauseTag> clauseTags = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(clauseDto.getTags())) {
            clauseTags = clauseTagService.saveClauseTagList(referenceId, true, clauseDto.getTags());
        } else {
            clauseTags.add(clauseTagRepository.findDefaultTag());
        }
        List<TemplateType> templateTypes = templateTypeRepository.findAllById(clauseDto.getTemplateTypes());
        clause.setOrganizationId(referenceId);
        clause.setTemplateTypes(templateTypes);
        clause.setTitle(clauseDto.getTitle());
        clause.setDescription(clauseDto.getDescription());
        clause.setTags(clauseTags);
        return clause;
    }

    private <E extends ClauseDTO> Clause prepareMasterClauseData(Long referenceId, E clauseDto, MasterClause clause) {
        List<ClauseTag> clauseTags = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(clauseDto.getTags())) {
            clauseTags = clauseTagService.saveClauseTagList(referenceId, false, clauseDto.getTags());
        } else {
            clauseTags.add(clauseTagRepository.findDefaultTag());
        }
        List<TemplateType> templateTypes = templateTypeRepository.findAllById(clauseDto.getTemplateTypes());
        MasterClauseDTO masterClauseDTO = (MasterClauseDTO) clauseDto;
        setMetadataOfMasterClause(masterClauseDTO,  clause);
        clause.setCountryId(referenceId);
        clause.setTemplateTypes(templateTypes);
        clause.setTitle(clauseDto.getTitle());
        clause.setDescription(clauseDto.getDescription());
        clause.setTags(clauseTags);
        return clause;
    }

    /**
     *  This method is used to fetch all the metadata related to master clause from DTO like organisationType,
     *  organisationSubType, Service Category and Sub Service Category
     *
     * @param masterClauseDTO
     * @return
     */
    private void setMetadataOfMasterClause(MasterClauseDTO masterClauseDTO, MasterClause clause) {
            clause.setOrganizationTypes(ObjectMapperUtils.copyPropertiesOfListByMapper(masterClauseDTO.getOrganizationTypes(), OrganizationType.class));
            clause.setOrganizationSubTypes(ObjectMapperUtils.copyPropertiesOfListByMapper(masterClauseDTO.getOrganizationSubTypes(), OrganizationSubType.class));
            clause.setOrganizationServices(ObjectMapperUtils.copyPropertiesOfListByMapper(masterClauseDTO.getOrganizationServices(), ServiceCategory.class));
            clause.setOrganizationSubServices(ObjectMapperUtils.copyPropertiesOfListByMapper(masterClauseDTO.getOrganizationSubServices(), SubServiceCategory.class));
            clause.setAccountTypes(ObjectMapperUtils.copyPropertiesOfListByMapper(masterClauseDTO.getAccountTypes(), AccountType.class));

    }
    /**
     * @param referenceId country id or unit id
     * @param isOrganization    boolean to verify is reference id is unitId or not
     * @param clauseDto   contain update data for clause
     * @return updated clause object
     * @throws DataNotFoundByIdException: if clause not found for particular id, {@link DuplicateDataException if clause already exist with same name}
     * @description this method update clause ,and add tags to clause if tag already exist then simply add tag and if not then create tag and then add to clause
     */
    public <E extends ClauseDTO> E updateClause(Long referenceId, boolean isOrganization, Long clauseId, E clauseDto) {

        Clause clause = isOrganization ? clauseRepository.findByUnitIdAndTitleAndDescription(referenceId, clauseDto.getTitle(), clauseDto.getDescription()) : clauseRepository.findByCountryIdAndTitleAndDescription(referenceId, clauseDto.getTitle(), clauseDto.getDescription());
        if (Optional.ofNullable(clause).isPresent() && !clause.getId().equals(clauseId)) {
            exceptionService.duplicateDataException("message.duplicate", "message.clause", clauseDto.getTitle());
        }
        Optional<Clause> existingClause = clauseRepository.findById(clauseId);
        if(existingClause.isPresent()) {
            clause = existingClause.get();
            if (isOrganization) {
                prepareOrganizationClauseData(referenceId, clauseDto, (OrganizationClause) clause);
            } else {
                prepareMasterClauseData(referenceId, clauseDto, (MasterClause) clause);
            }
            clauseRepository.save(clause);
        }
        return clauseDto;

    }


    /**
     * @param countryId
     * @return return clause with account type basic response,org types ,sub types,service category ,sub service category and tags
     * @description
     */
    public List<ClauseResponseDTO> getAllClauseByCountryId(Long countryId) {
        List<Clause> clauses = clauseRepository.findAllClauseByCountryId(countryId);
        return ObjectMapperUtils.copyPropertiesOfListByMapper(clauses, ClauseResponseDTO.class);
    }

    public List<UnitLevelClauseResponseDTO> getAllClauseByUnitId(Long unitId) {
        List<Clause> clauses = clauseRepository.findAllClauseByUnitId(unitId);
       return ObjectMapperUtils.copyPropertiesOfListByMapper(clauses, ClauseResponseDTO.class);
    }


    public ClauseResponseDTO getClauseById(Long countryId, Long id) {
        Clause clause = clauseRepository.findByIdAndCountryId(id, countryId);
        if (!Optional.ofNullable(clause).isPresent()) {
            throw new DataNotFoundByIdException("message.clause.data.not.found.for " + id);
        }
        return ObjectMapperUtils.copyPropertiesByMapper(clause, ClauseResponseDTO.class);
    }


    /**
     * @param referenceId country id or unit id
     * @param isOrganization    boolean to verify is reference id is unitId or not
     * @return boolean true if data deleted successfully
     * @throws DataNotFoundByIdException; if clause not found for id
     */
    public Boolean deleteClauseById(Long referenceId, boolean isOrganization, Long clauseId) {

        //TODO refactor When done Policy Agreement template
       /* List<AgreementTemplateBasicResponseDTO> agreementTemplatesContainCurrentClause = policyAgreementTemplateRepository.findAllByReferenceIdAndClauseId(referenceId, isOrganization, clauseId);
        if (CollectionUtils.isNotEmpty(agreementTemplatesContainCurrentClause)) {
            exceptionService.invalidRequestException("message.clause.present.inPolicyAgreementTemplate.cannotbe.delete", new StringBuilder(agreementTemplatesContainCurrentClause.stream().map(AgreementTemplateBasicResponseDTO::getName).map(String::toString).collect(Collectors.joining(","))));
        }*/
        clauseRepository.safeDeleteById(clauseId);
        return true;
    }

    /**
     *
     * @param countryId
     * @return
     */
    //TODO try to use DTO instead of map
    public Map<String,Object> getClauseMetaDataByCountryId(Long countryId) {
        Map<String,Object> clauseMetaDataMap=new HashMap<>();

        // TODO call repository direct here instead of service method
        clauseMetaDataMap.put("clauseTagList", clauseTagService.getAllClauseTagByCountryId(countryId));
        clauseMetaDataMap.put("templateTypeList",templateTypeService.getAllTemplateType(countryId));
        return clauseMetaDataMap;
    }

    /**
     *
     * @param unitId
     * @return
     */

    //TODO try to use DTO instead of map
    public Map<String,Object> getClauseMetadataByOrganizationId(Long unitId) {
        Map<String,Object> clauseMetaDataMap=new HashMap<>();
        clauseMetaDataMap.put("clauseTagList",clauseTagService.getAllClauseTagByUnitId(unitId));
        clauseMetaDataMap.put("templateTypeList",policyAgreementTemplateService.getAllTemplateType(unitId));
        return clauseMetaDataMap;
    }
}

package com.kairos.service.clause;


import com.kairos.commons.custom_exception.DataNotFoundByIdException;
import com.kairos.commons.custom_exception.DuplicateDataException;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.gdpr.OrganizationSubTypeDTO;
import com.kairos.dto.gdpr.OrganizationTypeDTO;
import com.kairos.dto.gdpr.ServiceCategoryDTO;
import com.kairos.dto.gdpr.SubServiceCategoryDTO;
import com.kairos.dto.gdpr.master_data.AccountTypeVO;
import com.kairos.dto.gdpr.master_data.ClauseDTO;
import com.kairos.persistence.model.clause.Clause;
import com.kairos.dto.gdpr.master_data.MasterClauseDTO;
import com.kairos.persistence.model.clause.ClauseMD;
import com.kairos.persistence.model.clause_tag.ClauseTag;
import com.kairos.persistence.model.clause_tag.ClauseTagMD;
import com.kairos.persistence.model.master_data.default_asset_setting.*;
import com.kairos.persistence.model.master_data.default_proc_activity_setting.MasterProcessingActivityMD;
import com.kairos.persistence.model.template_type.TemplateTypeMD;
import com.kairos.persistence.repository.agreement_template.PolicyAgreementTemplateRepository;
import com.kairos.persistence.repository.clause.ClauseMongoRepository;
import com.kairos.persistence.repository.clause.ClauseRepository;
import com.kairos.persistence.repository.clause_tag.ClauseTagMongoRepository;
import com.kairos.persistence.repository.clause_tag.ClauseTagRepository;
import com.kairos.persistence.repository.template_type.TemplateTypeRepository;
import com.kairos.response.dto.clause.ClauseResponseDTO;
import com.kairos.response.dto.clause.UnitLevelClauseResponseDTO;
import com.kairos.response.dto.master_data.TemplateTypeResponseDTO;
import com.kairos.response.dto.policy_agreement.AgreementTemplateBasicResponseDTO;
import com.kairos.service.agreement_template.PolicyAgreementTemplateService;
import com.kairos.service.clause_tag.ClauseTagService;
import com.kairos.service.common.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.template_type.TemplateTypeService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class ClauseService extends MongoBaseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClauseService.class);

    @Inject
    private ClauseMongoRepository clauseMongoRepository;


    @Inject
    private ClauseTagService clauseTagService;

    @Inject
    private ExceptionService exceptionService;

    @Inject
    private ClauseTagMongoRepository clauseTagMongoRepository;

    @Inject
    private TemplateTypeService templateTypeService;

    @Inject
    private PolicyAgreementTemplateRepository policyAgreementTemplateRepository;
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
     * @param isUnitId    boolean to verify is reference id is unitId or not
     * @param clauseDto   contain data about clause and template type which belong to clause
     * @return clause  object , specific to organization type ,sub types ,Service Category and Sub Service Category
     * @throws DuplicateDataException : if clause already exist for id ,if account type is not selected}
     * @desciption this method create clause ,and add tags to clause if tag already exist then simply add tag and if not then create tag and then add to clause
     */
    public <E extends ClauseDTO> E createClause(Long referenceId, boolean isUnitId, E clauseDto) {

        ClauseMD previousClause = isUnitId ? clauseRepository.findByUnitIdAndTitleAndDescription(referenceId, clauseDto.getTitle(), clauseDto.getDescription()) : clauseRepository.findByCountryIdAndTitleAndDescription(referenceId, clauseDto.getTitle(), clauseDto.getDescription());
        if (Optional.ofNullable(previousClause).isPresent()) {
            exceptionService.duplicateDataException("message.duplicate", "clause", clauseDto.getTitle().toLowerCase());
        }
        ClauseMD clause = buildOrUpdateClause(referenceId, isUnitId, clauseDto, null);
        clauseRepository.save(clause);
        clauseDto.setId(clause.getId());
        return clauseDto;
    }


    private <E extends ClauseDTO> ClauseMD buildOrUpdateClause(Long referenceId, boolean isUnitId, E clauseDto, ClauseMD clause) {

        List<ClauseTagMD> clauseTags;
        if (CollectionUtils.isNotEmpty(clauseDto.getTags())) {
            clauseTags = clauseTagService.saveClauseTagList(referenceId, isUnitId, clauseDto.getTags());
        } else {
            clauseTags = Collections.singletonList(clauseTagRepository.findDefaultTag());
        }
        List<TemplateTypeMD> templateTypes = templateTypeRepository.findAllById(clauseDto.getTemplateTypes());
        if (Optional.ofNullable(clause).isPresent()) {
            if (isUnitId) {
                ObjectMapperUtils.copyProperties(clauseDto, clause);
            } else {
                MasterClauseDTO masterClauseDTO = (MasterClauseDTO) clauseDto;
                clause.setTitle(masterClauseDTO.getTitle());
                clause.setDescription(masterClauseDTO.getDescription());
                clause = getMetadataOfMasterClause(masterClauseDTO, clause);
                clause.setAccountTypes(ObjectMapperUtils.copyPropertiesOfListByMapper(masterClauseDTO.getAccountTypes(), AccountType.class));
                clause.setTemplateTypes(templateTypes);
            }
            clause.setTags(clauseTags);
        } else {
            clause = new ClauseMD(clauseDto.getTitle(), clauseDto.getDescription(), clauseTags,templateTypes);
            if (isUnitId) {
                clause.setOrganizationId(referenceId);
            } else {
                MasterClauseDTO masterClauseDTO = (MasterClauseDTO) clauseDto;
                clause = getMetadataOfMasterClause(masterClauseDTO, clause);
                clause.setAccountTypes(ObjectMapperUtils.copyPropertiesOfListByMapper(masterClauseDTO.getAccountTypes(), AccountType.class));
                clause.setCountryId(referenceId);
            }
        }
        return clause;
    }

    /**
     *  This method is used to fetch all the metadata related to master clause from DTO like organisationType,
     *  organisationSubType, Service Category and Sub Service Category
     *
     * @param masterClauseDTO
     * @return
     */
    //TODO need to make common method for asset and processing activity and others
    private ClauseMD getMetadataOfMasterClause(MasterClauseDTO masterClauseDTO, ClauseMD clause){
        clause.setOrganizationTypes(ObjectMapperUtils.copyPropertiesOfListByMapper(masterClauseDTO.getOrganizationTypes(), OrganizationType.class));
        clause.setOrganizationSubTypes(ObjectMapperUtils.copyPropertiesOfListByMapper(masterClauseDTO.getOrganizationSubServices(), OrganizationSubType.class));
        clause.setOrganizationServices(ObjectMapperUtils.copyPropertiesOfListByMapper(masterClauseDTO.getOrganizationServices(), ServiceCategory.class));
        clause.setOrganizationSubServices(ObjectMapperUtils.copyPropertiesOfListByMapper(masterClauseDTO.getOrganizationSubServices(), SubServiceCategory.class));
        return clause;
    }

    /**
     * @param referenceId country id or unit id
     * @param isUnitId    boolean to verify is reference id is unitId or not
     * @param clauseDto   contain update data for clause
     * @return updated clause object
     * @throws DataNotFoundByIdException: if clause not found for particular id, {@link DuplicateDataException if clause already exist with same name}
     * @description this method update clause ,and add tags to clause if tag already exist then simply add tag and if not then create tag and then add to clause
     */
    public <E extends ClauseDTO> E updateClause(Long referenceId, boolean isUnitId, Long clauseId, E clauseDto) {

        ClauseMD clause = isUnitId ? clauseRepository.findByUnitIdAndTitleAndDescription(referenceId, clauseDto.getTitle(), clauseDto.getDescription()) : clauseRepository.findByCountryIdAndTitleAndDescription(referenceId, clauseDto.getTitle(), clauseDto.getDescription());
        if (Optional.ofNullable(clause).isPresent() && !clause.getId().equals(clauseId)) {
            exceptionService.duplicateDataException("message.duplicate", "message.clause", clauseDto.getTitle());
        }
        clause = clauseRepository.getOne(clauseId);
        clause = buildOrUpdateClause(referenceId, isUnitId, clauseDto, clause);
        clauseRepository.save(clause);
        return clauseDto;

    }


    /**
     * @param countryId
     * @return return clause with account type basic response,org types ,sub types,service category ,sub service category and tags
     * @description
     */
    public List<ClauseResponseDTO> getAllClauseByCountryId(Long countryId) {
        List<ClauseResponseDTO> clauseResponseDTOS =  new ArrayList<>();
        List<ClauseMD> clauses = clauseRepository.findAllClauseByCountryId(countryId);
        for(ClauseMD clause : clauses){
            clauseResponseDTOS.add(prepareClauseResponseDTO(clause));
        }
        return clauseResponseDTOS;
    }

    public List<UnitLevelClauseResponseDTO> getAllClauseByUnitId(Long unitId) {
        return clauseMongoRepository.findAllClauseByUnitId(unitId);
    }


    private ClauseResponseDTO prepareClauseResponseDTO(ClauseMD clause){
        ClauseResponseDTO clauseResponseDTO =  new ClauseResponseDTO();
        clauseResponseDTO.setId(clause.getId());
        clauseResponseDTO.setTitle(clause.getTitle());
        clauseResponseDTO.setDescription(clause.getDescription());
        clauseResponseDTO.setTags(clause.getTags());
        clauseResponseDTO.setTemplateTypes(ObjectMapperUtils.copyPropertiesOfListByMapper(clause.getTemplateTypes(), TemplateTypeResponseDTO.class));
        clauseResponseDTO.setAccountTypes(ObjectMapperUtils.copyPropertiesOfListByMapper(clause.getAccountTypes(), AccountTypeVO.class));
        clauseResponseDTO.setOrganizationTypeDTOS(ObjectMapperUtils.copyPropertiesOfListByMapper(clause.getOrganizationTypes(), OrganizationTypeDTO.class));
        clauseResponseDTO.setOrganizationServices(ObjectMapperUtils.copyPropertiesOfListByMapper(clause.getOrganizationServices(), ServiceCategoryDTO.class));
        clauseResponseDTO.setOrganizationSubServices(ObjectMapperUtils.copyPropertiesOfListByMapper(clause.getOrganizationSubServices(), SubServiceCategoryDTO.class));
        clauseResponseDTO.setOrganizationSubTypeDTOS(ObjectMapperUtils.copyPropertiesOfListByMapper(clause.getOrganizationSubTypes(), OrganizationSubTypeDTO.class));
        return clauseResponseDTO;
    }

    public ClauseResponseDTO getClauseById(Long countryId, Long id) {
        ClauseMD clause = clauseRepository.findByIdAndCountryId(id, countryId);
        if (!Optional.ofNullable(clause).isPresent()) {
            throw new DataNotFoundByIdException("message.clause.data.not.found.for " + id);
        }
        return prepareClauseResponseDTO(clause);
    }


    /**
     * @param referenceId country id or unit id
     * @param isUnitId    boolean to verify is reference id is unitId or not
     * @return boolean true if data deleted successfully
     * @throws DataNotFoundByIdException; if clause not found for id
     */
    public Boolean deleteClauseById(Long referenceId, boolean isUnitId, Long clauseId) {

        //TODO refactor When done Policy Agreement template
       /* List<AgreementTemplateBasicResponseDTO> agreementTemplatesContainCurrentClause = policyAgreementTemplateRepository.findAllByReferenceIdAndClauseId(referenceId, isUnitId, clauseId);
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
    public Map<String,Object> getClauseMetaDataByCountryId(Long countryId) {
        Map<String,Object> clauseMetaDataMap=new HashMap<>();
        clauseMetaDataMap.put("clauseTagList", clauseTagService.getAllClauseTagByCountryId(countryId));
        clauseMetaDataMap.put("templateTypeList",templateTypeService.getAllTemplateType(countryId));
        return clauseMetaDataMap;
    }

    /**
     *
     * @param unitId
     * @return
     */
    public Map<String,Object> getClauseMetadataByOrganizationId(Long unitId) {
        Map<String,Object> clauseMetaDataMap=new HashMap<>();
        clauseMetaDataMap.put("clauseTagList",clauseTagService.getAllClauseTagByUnitId(unitId));
        clauseMetaDataMap.put("templateTypeList",policyAgreementTemplateService.getAllTemplateType(unitId));
        return clauseMetaDataMap;
    }
}

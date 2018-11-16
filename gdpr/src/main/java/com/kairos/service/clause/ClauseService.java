package com.kairos.service.clause;


import com.kairos.commons.custom_exception.DataNotFoundByIdException;
import com.kairos.commons.custom_exception.DuplicateDataException;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.gdpr.master_data.ClauseDTO;
import com.kairos.persistence.model.clause.Clause;
import com.kairos.dto.gdpr.master_data.MasterClauseDTO;
import com.kairos.persistence.model.clause_tag.ClauseTag;
import com.kairos.persistence.repository.agreement_template.PolicyAgreementTemplateRepository;
import com.kairos.persistence.repository.clause.ClauseMongoRepository;
import com.kairos.persistence.repository.clause_tag.ClauseTagMongoRepository;
import com.kairos.response.dto.clause.ClauseResponseDTO;
import com.kairos.response.dto.policy_agreement.AgreementTemplateBasicResponseDTO;
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


    /**
     * @param referenceId country id or unit id
     * @param isUnitId    boolean to verify is reference id is unitId or not
     * @param clauseDto   contain data about clause and template type which belong to clause
     * @return clause  object , specific to organization type ,sub types ,Service Category and Sub Service Category
     * @throws DuplicateDataException : if clause already exist for id ,if account type is not selected}
     * @desciption this method create clause ,and add tags to clause if tag already exist then simply add tag and if not then create tag and then add to clause
     */
    public <E extends ClauseDTO> E createClause(Long referenceId, boolean isUnitId, E clauseDto) {

        Clause previousClause = isUnitId ? clauseMongoRepository.findByUnitIdAndTitleAndDescription(referenceId, clauseDto.getTitle(), clauseDto.getDescription()) : clauseMongoRepository.findByCountryIdAndTitleAndDescription(referenceId, clauseDto.getTitle(), clauseDto.getDescription());
        if (Optional.ofNullable(previousClause).isPresent()) {
            exceptionService.duplicateDataException("message.duplicate", "clause", clauseDto.getTitle().toLowerCase());
        }
        Clause clause = buildOrUpdateClause(referenceId, isUnitId, clauseDto, null);
        clauseMongoRepository.save(clause);
        clauseDto.setId(clause.getId());
        return clauseDto;
    }


    private <E extends ClauseDTO> Clause buildOrUpdateClause(Long referenceId, boolean isUnitId, E clauseDto, Clause clause) {

        List<ClauseTag> clauseTags;
        if (CollectionUtils.isNotEmpty(clauseDto.getTags())) {
            clauseTags = clauseTagService.saveClauseTagList(referenceId, isUnitId, clauseDto.getTags());
        } else {
            clauseTags = isUnitId ? Collections.singletonList(clauseTagMongoRepository.findDefaultTagByUnitId(referenceId)) : Collections.singletonList(clauseTagMongoRepository.findDefaultTagByCountryId(referenceId));
        }
        if (Optional.ofNullable(clause).isPresent()) {
            if (isUnitId) {
                ObjectMapperUtils.copyProperties(clauseDto, clause);
            } else {
                MasterClauseDTO masterClauseDTO = (MasterClauseDTO) clauseDto;
                ObjectMapperUtils.copyProperties(masterClauseDTO, clause);
            }
        } else {
            clause = new Clause(clauseDto.getTitle(), clauseDto.getDescription(), clauseTags);
            if (isUnitId) {
                clause.setOrganizationId(referenceId);
            } else {
                MasterClauseDTO masterClauseDTO = (MasterClauseDTO) clauseDto;
                clause.setOrganizationTypes(masterClauseDTO.getOrganizationTypes());
                clause.setOrganizationSubTypes(masterClauseDTO.getOrganizationSubTypes());
                clause.setOrganizationServices(masterClauseDTO.getOrganizationServices());
                clause.setOrganizationSubServices(masterClauseDTO.getOrganizationSubServices());
                clause.setAccountTypes(masterClauseDTO.getAccountTypes());
                clause.setCountryId(referenceId);
                clause.setTemplateTypes(masterClauseDTO.getTemplateTypes());
            }
        }
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
    public <E extends ClauseDTO> E updateClause(Long referenceId, boolean isUnitId, BigInteger clauseId, E clauseDto) {

        Clause clause = isUnitId ? clauseMongoRepository.findByUnitIdAndTitleAndDescription(referenceId, clauseDto.getTitle(), clauseDto.getDescription()) : clauseMongoRepository.findByCountryIdAndTitleAndDescription(referenceId, clauseDto.getTitle(), clauseDto.getDescription());
        if (Optional.ofNullable(clause).isPresent() && !clause.getId().equals(clauseId)) {
            exceptionService.duplicateDataException("message.duplicate", "message.clause", clauseDto.getTitle());
        }
        clause = clauseMongoRepository.findOne(clauseId);
        clause = buildOrUpdateClause(referenceId, isUnitId, clauseDto, clause);
        clauseMongoRepository.save(clause);
        return clauseDto;

    }


    /**
     * @param countryId
     * @return return clause with account type basic response,org types ,sub types,service category ,sub service category and tags
     * @description
     */
    public List<ClauseResponseDTO> getAllClauseByCountryId(Long countryId) {
        return clauseMongoRepository.findAllClauseByCountryId(countryId);
    }

    public List<ClauseResponseDTO> getAllClauseByUnitId(Long unitId) {
        return clauseMongoRepository.findAllClauseByUnitId(unitId);
    }


    public ClauseResponseDTO getClauseById(Long countryId, BigInteger id) {
        ClauseResponseDTO clause = clauseMongoRepository.findClauseWithTemplateTypeById(countryId, id);
        if (!Optional.ofNullable(clause).isPresent()) {
            throw new DataNotFoundByIdException("message.clause.data.not.found.for " + id);
        }
        return clause;
    }


    /**
     * @param referenceId country id or unit id
     * @param isUnitId    boolean to verify is reference id is unitId or not
     * @return boolean true if data deleted successfully
     * @throws DataNotFoundByIdException; if clause not found for id
     */
    public Boolean deleteClauseById(Long referenceId, boolean isUnitId, BigInteger clauseId) {

        List<AgreementTemplateBasicResponseDTO> agreementTemplatesContainCurrentClause = policyAgreementTemplateRepository.findAgreementTemplateListByReferenceIdAndClauseId(referenceId, isUnitId, clauseId);
        if (CollectionUtils.isNotEmpty(agreementTemplatesContainCurrentClause)) {
            exceptionService.invalidRequestException("message.clause.present.inPolicyAgreementTemplate.cannotbe.delete", new StringBuilder(agreementTemplatesContainCurrentClause.stream().map(AgreementTemplateBasicResponseDTO::getName).map(String::toString).collect(Collectors.joining(","))));
        }
        clauseMongoRepository.safeDeleteById(clauseId);
        return true;
    }


}

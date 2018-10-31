package com.kairos.service.clause;


import com.kairos.commons.custom_exception.DataNotFoundByIdException;
import com.kairos.commons.custom_exception.DuplicateDataException;
import com.kairos.persistence.model.clause.Clause;
import com.kairos.dto.gdpr.master_data.ClauseDTO;
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
     * @param countryId
     * @param clauseDto contain data about clause and template type which belong to clause
     * @return clause  object , specific to organization type ,sub types ,Service Category and Sub Service Category
     * @throws DuplicateDataException : if clause already exist for id ,if account type is not selected}
     * @desciption this method create clause ,and add tags to clause if tag already exist then simply add tag and if not then create tag and then add to clause
     */
    public ClauseDTO createClause(Long countryId, ClauseDTO clauseDto) {

        Clause previousClause = clauseMongoRepository.findByTitle(countryId, clauseDto.getTitle());
        if (Optional.ofNullable(previousClause).isPresent()) {
            exceptionService.duplicateDataException("message.duplicate", "clause", clauseDto.getTitle().toLowerCase());
        }
        Clause newClause = new Clause(clauseDto.getTitle(), clauseDto.getDescription(), countryId, clauseDto.getOrganizationTypes(), clauseDto.getOrganizationSubTypes()
                , clauseDto.getOrganizationServices(), clauseDto.getOrganizationSubServices());
        newClause.setAccountTypes(clauseDto.getAccountTypes());
        newClause.setTemplateTypes(clauseDto.getTemplateTypes());
        newClause.setTags(clauseTagService.addClauseTagAndGetClauseTagList(countryId, clauseDto.getTags()));
        clauseMongoRepository.save(newClause);
        clauseDto.setId(newClause.getId());
        return clauseDto;


    }


    /**
     * @param countryId
     * @param clauseId  clause id
     * @param clauseDto contain update data for clause
     * @return updated clause object
     * @throws DataNotFoundByIdException: if clause not found for particular id, {@link DuplicateDataException if clause already exist with same name}
     * @description this method update clause ,and add tags to clause if tag already exist then simply add tag and if not then create tag and then add to clause
     */
    public ClauseDTO updateClause(Long countryId, BigInteger clauseId, ClauseDTO clauseDto) {

        Clause clause = clauseMongoRepository.findByTitle(countryId, clauseDto.getTitle());
        if (Optional.ofNullable(clause).isPresent() && !clause.getId().equals(clauseId)) {
            exceptionService.duplicateDataException("message.duplicate", "message.clause", clauseDto.getTitle());
        }
        clause = clauseMongoRepository.findOne(clauseId);
        clause.setAccountTypes(clauseDto.getAccountTypes());
        clause.setOrganizationTypes(clauseDto.getOrganizationTypes());
        clause.setOrganizationSubTypes(clauseDto.getOrganizationSubTypes());
        clause.setOrganizationServices(clauseDto.getOrganizationServices());
        clause.setOrganizationSubServices(clauseDto.getOrganizationSubServices());
        clause.setTitle(clauseDto.getTitle());
        clause.setDescription(clauseDto.getDescription());
        clause.setTags(clauseTagService.addClauseTagAndGetClauseTagList(countryId, clauseDto.getTags()));
        clause.setTemplateTypes(clauseDto.getTemplateTypes());
        clauseMongoRepository.save(clause);
        return clauseDto;

    }


    /**
     * @param countryId
     * @return return clause with account type basic response,org types ,sub types,service category ,sub service category and tags
     * @description
     */
    public List<ClauseResponseDTO> getAllClauses(Long countryId) {
        return clauseMongoRepository.findAllClauseWithTemplateType(countryId);
    }


    public ClauseResponseDTO getClauseById(Long countryId, BigInteger id) {
        ClauseResponseDTO clause = clauseMongoRepository.findClauseWithTemplateTypeById(countryId, id);
        if (!Optional.ofNullable(clause).isPresent()) {
            throw new DataNotFoundByIdException("message.clause.data.not.found.for " + id);
        }
        return clause;
    }


    /**
     * @param countryId
     * @param clauseId
     * @return boolean true if data deleted successfully
     * @throws DataNotFoundByIdException; if clause not found for id
     */
    public Boolean deleteClause(Long countryId, BigInteger clauseId) {

        List<AgreementTemplateBasicResponseDTO> agreementTemplatesContainCurrentClause = policyAgreementTemplateRepository.findAgreementTemplateListByCountryIdAndClauseId(countryId, clauseId);
        if (CollectionUtils.isNotEmpty(agreementTemplatesContainCurrentClause)) {
            exceptionService.invalidRequestException("message.clause.present.inPolicyAgreementTemplate.cannotbe.delete", new StringBuilder(agreementTemplatesContainCurrentClause.stream().map(AgreementTemplateBasicResponseDTO::getName).map(String::toString).collect(Collectors.joining(","))));
        }
        clauseMongoRepository.safeDeleteById(clauseId);
        return true;
    }


}

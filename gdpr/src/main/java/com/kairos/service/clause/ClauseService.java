package com.kairos.service.clause;

import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.persistance.repository.account_type.AccountTypeMongoRepository;
import com.kairos.persistance.model.clause.Clause;
import com.kairos.dto.master_data.ClauseDTO;
import com.kairos.persistance.model.clause_tag.ClauseTag;
import com.kairos.persistance.repository.clause.ClauseMongoRepository;
import com.kairos.persistance.repository.clause_tag.ClauseTagMongoRepository;
import com.kairos.response.dto.clause.ClauseResponseDTO;
import com.kairos.service.account_type.AccountTypeService;
import com.kairos.service.clause_tag.ClauseTagService;
import com.kairos.service.common.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.javers.JaversCommonService;
import com.kairos.service.template_type.TemplateTypeService;
import com.kairos.utils.ComparisonUtils;
import org.javers.spring.annotation.JaversAuditable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;
import java.util.List;


@Service
public class ClauseService extends MongoBaseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClauseService.class);

    @Autowired
    private ClauseMongoRepository clauseRepository;

    @Inject
    private AccountTypeService accountTypeService;
    @Inject
    private MongoTemplate mongoTemplate;


    @Inject
    private AccountTypeMongoRepository accountTypeMongoRepository;

    @Inject
    private ComparisonUtils comparisonUtils;


    @Inject
    private ClauseTagService clauseTagService;

    @Inject
    private ExceptionService exceptionService;

    @Inject
    private ClauseTagMongoRepository clauseTagMongoRepository;

    @Inject
    private TemplateTypeService templateTypeService;

    @Inject
    private JaversCommonService javersCommonService;


    public Clause createClause(Long countryId, Long organizationId, ClauseDTO clauseDto) {

        if (clauseRepository.findByTitle(countryId, organizationId, clauseDto.getTitle()) != null) {
            exceptionService.duplicateDataException("message.duplicate", "clause", clauseDto.getTitle().toLowerCase());
        }
        if (clauseDto.getAccountTypes().size() == 0) {
            exceptionService.invalidRequestException("message.invalid.request", "Select account Type");
        }
        List<ClauseTag> tagList = clauseTagService.addClauseTagAndGetClauseTagList(countryId, organizationId, clauseDto.getTags());
        templateTypeService.getTemplateByById(clauseDto.getTemplateType(), countryId);
        Clause newclause = new Clause(countryId, clauseDto.getTitle(), clauseDto.getDescription());
        newclause.setOrganizationTypes(clauseDto.getOrganizationTypes());
        newclause.setOrganizationSubTypes(clauseDto.getOrganizationSubTypes());
        newclause.setOrganizationServices(clauseDto.getOrganizationServices());
        newclause.setOrganizationSubServices(clauseDto.getOrganizationSubServices());
        newclause.setOrganizationId(organizationId);
        newclause.setAccountTypes(accountTypeService.getAccountTypeList(countryId, clauseDto.getAccountTypes()));
        newclause.setOrganizationList(clauseDto.getOrgannizationList());
        newclause.setTemplateType(clauseDto.getTemplateType());
        newclause.setTags(tagList);

        try {
            newclause = clauseRepository.save(save(newclause));
            return newclause;
        } catch (Exception e) {
            clauseTagMongoRepository.deleteAll(tagList);
            LOGGER.warn(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }

    }

    public Clause getClause(Long countryId, Long organizationId, BigInteger id) {
        Clause clause = clauseRepository.findByIdAndNonDeleted(countryId, organizationId, id);
        if (!Optional.ofNullable(clause).isPresent()) {
            throw new DataNotFoundByIdException("message.clause.data.not.found.for " + id);
        } else
            return clause;
    }


    public Clause updateClause(Long countryId, Long organizationId, BigInteger clauseId, ClauseDTO clauseDto) {

        Clause exists = clauseRepository.findByTitle(countryId, organizationId, clauseDto.getTitle());
        if (Optional.ofNullable(exists).isPresent() && !exists.getId().equals(clauseId)) {
            exceptionService.duplicateDataException("message.duplicate", "message.clause", clauseDto.getTitle());
        }
        exists = clauseRepository.findByIdAndNonDeleted(countryId, organizationId, clauseId);
        if (!Optional.ofNullable(exists).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "message.clause" + clauseId);
        }
        List<ClauseTag> tagList = clauseTagService.addClauseTagAndGetClauseTagList(countryId, organizationId, clauseDto.getTags());
        exists.setAccountTypes(accountTypeService.getAccountTypeList(countryId, clauseDto.getAccountTypes()));
        templateTypeService.getTemplateByById(clauseDto.getTemplateType(), countryId);
        try {
            exists.setOrganizationTypes(clauseDto.getOrganizationTypes());
            exists.setOrganizationSubTypes(clauseDto.getOrganizationSubTypes());
            exists.setOrganizationServices(clauseDto.getOrganizationServices());
            exists.setOrganizationSubServices(clauseDto.getOrganizationSubServices());
            exists.setTitle(clauseDto.getTitle());
            exists.setDescription(clauseDto.getDescription());
            exists.setTags(tagList);
            exists.setTemplateType(clauseDto.getTemplateType());
            exists.setOrganizationList(clauseDto.getOrgannizationList());
            exists = clauseRepository.save(save(exists));
        } catch (Exception e) {
            clauseTagMongoRepository.deleteAll(tagList);
            LOGGER.warn(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
        return exists;
    }


    public List<Clause> getClauseList(Long countryId, Long organizationId, Set<BigInteger> clausesId) {
        return clauseRepository.getClauseListByIds(countryId, organizationId, clausesId);
    }


    public List<ClauseResponseDTO> getAllClauses(Long countryId, Long organizationId) {
        return clauseRepository.findAllClause(countryId, organizationId);
    }


    public Boolean deleteClause(Long countryId, Long organizationId, BigInteger id) {

        Clause clause = clauseRepository.findByIdAndNonDeleted(countryId, organizationId, id);
        if (!Optional.ofNullable(clause).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "message.clause" + id);
        }
        delete(clause);
        return true;
    }


    public Page<Clause> getClausePagination(int page, int size) {
        //  return clauseRepository.findAll(new PageRequest(page, size));
        return null;
    }


}

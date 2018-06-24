package com.kairos.service.clause;

import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.InvalidRequestException;
import com.kairos.persistance.repository.account_type.AccountTypeMongoRepository;
import com.kairos.persistance.model.clause.Clause;
import com.kairos.dto.master_data.ClauseDTO;
import com.kairos.persistance.model.clause_tag.ClauseTag;
import com.kairos.persistance.repository.clause.ClauseMongoRepository;
import com.kairos.persistance.repository.clause_tag.ClauseTagMongoRepository;
import com.kairos.response.dto.clause.ClauseResponseDTO;
import com.kairos.service.MongoBaseService;
import com.kairos.service.account_type.AccountTypeService;
import com.kairos.service.clause_tag.ClauseTagService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.jackrabbit_service.JackrabbitService;
import com.kairos.utils.ComparisonUtils;
import com.kairos.utils.userContext.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.jcr.RepositoryException;
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
    private JackrabbitService jackrabbitService;

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


    public Clause createClause(Long countryId, Long organizationId, ClauseDTO clauseDto) throws RepositoryException {

        if (clauseRepository.findByTitle(countryId, organizationId, clauseDto.getTitle()) != null) {
            exceptionService.duplicateDataException("message.duplicate", "clause", clauseDto.getTitle().toLowerCase());
        }
        if (clauseDto.getAccountTypes().size() == 0) {
            exceptionService.invalidRequestException("message.invalid.request", "Select account Type");
        }
        List<ClauseTag> tagList = clauseTagService.addClauseTagAndGetClauseTagList(countryId,organizationId,clauseDto.getTags());
        Clause newclause = new Clause(countryId, clauseDto.getTitle(), clauseDto.getDescription());
        newclause.setOrganizationTypes(clauseDto.getOrganizationTypes());
        newclause.setOrganizationSubTypes(clauseDto.getOrganizationSubTypes());
        newclause.setOrganizationServices(clauseDto.getOrganizationServices());
        newclause.setOrganizationSubServices(clauseDto.getOrganizationSubServices());
        newclause.setOrganizationId(organizationId);
        newclause.setAccountTypes(accountTypeMongoRepository.getAccountTypeList(countryId, organizationId, clauseDto.getAccountTypes()));
        newclause.setTags(tagList);
        try {
            newclause = save(newclause);
            jackrabbitService.addClauseNodeToJackrabbit(newclause.getId(), newclause);
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


    public Clause updateClause(Long countryId, Long organizationId, BigInteger clauseId, ClauseDTO clauseDto) throws RepositoryException {

        Clause exists = clauseRepository.findByTitle(countryId, organizationId, clauseDto.getTitle());
        if (Optional.ofNullable(exists).isPresent() && !clauseId.equals(exists.getId())) {
            exceptionService.duplicateDataException("message.duplicate", "message.clause", clauseDto.getTitle());
        }
        exists = clauseRepository.findByIdAndNonDeleted(countryId, organizationId, clauseId);
        if (!Optional.ofNullable(exists).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "message.clause" + clauseId);
        }
        List<ClauseTag> tagList = clauseTagService.addClauseTagAndGetClauseTagList(countryId,organizationId,clauseDto.getTags());
        exists.setAccountTypes(accountTypeMongoRepository.getAccountTypeList(countryId, organizationId, clauseDto.getAccountTypes()));
        try {
            exists.setOrganizationTypes(clauseDto.getOrganizationTypes());
            exists.setOrganizationSubTypes(clauseDto.getOrganizationSubTypes());
            exists.setOrganizationServices(clauseDto.getOrganizationServices());
            exists.setOrganizationSubServices(clauseDto.getOrganizationSubServices());
            exists.setTitle(clauseDto.getTitle());
            exists.setDescription(clauseDto.getDescription());
            exists.setTags(tagList);
            jackrabbitService.clauseVersioning(clauseId, exists);
            exists = save(exists);
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


    public Boolean deleteClause(BigInteger id) {

        Clause clause = clauseRepository.findByid(id);
        if (Optional.ofNullable(clause).isPresent()) {
            clause.setDeleted(true);
            save(clause);
            return true;
        } else
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "message.clause" + id);
        return false;

    }


    public Page<Clause> getClausePagination(int page, int size) {
        return clauseRepository.findAll(new PageRequest(page, size));
    }


    public StringBuffer getClauseVersion(BigInteger id, String version) throws RepositoryException {
        return jackrabbitService.getClauseVersion(id, version);

    }

    public List<String> getAllClauseVersion(BigInteger id) throws RepositoryException {
        return jackrabbitService.getClauseVersions(id);

    }


}

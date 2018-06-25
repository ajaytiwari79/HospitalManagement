package com.kairos.service.clause;

import com.kairos.client.OrganizationTypeRestClient;
import com.kairos.custome_exception.DataNotFoundByIdException;
import com.kairos.persistance.model.account_type.AccountType;
import com.kairos.persistance.model.clause.Clause;
import com.kairos.dto.master_data.ClauseDto;
import com.kairos.persistance.model.clause_tag.ClauseTag;
import com.kairos.persistance.repository.clause.ClauseMongoRepository;
import com.kairos.persistance.repository.clause_tag.ClauseTagMongoRepository;
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
    private OrganizationTypeRestClient organizationTypeAndServiceRestClient;

    @Inject
    private ComparisonUtils comparisonUtils;


    @Inject
    private ClauseTagService clauseTagService;

    @Inject
    private ExceptionService exceptionService;

    @Inject
    private ClauseTagMongoRepository clauseTagMongoRepository;


    public Clause createClause(Long countryId, ClauseDto clauseDto) throws RepositoryException {
        List<ClauseTag> tagList = new ArrayList<>();
        if (clauseRepository.findByTitleAndCountry(countryId, clauseDto.getTitle()) != null) {
            exceptionService.duplicateDataException("message.duplicate", "clause", clauseDto.getTitle());
        }
        tagList = clauseTagService.addClauseTagAndGetClauseTagList(clauseDto.getTags());
        try {
            List<AccountType> accountTypes = accountTypeService.getAccountTypeList(countryId, clauseDto.getAccountType());
            Clause newclause = new Clause(countryId, clauseDto.getTitle(), clauseDto.getDescription());
            if (clauseDto.getOrganizationTypes() != null && clauseDto.getOrganizationTypes().size() != 0) {
                newclause.setOrganizationTypes(clauseDto.getOrganizationTypes());
            }
            if (clauseDto.getOrganizationSubTypes() != null && clauseDto.getOrganizationSubTypes().size() != 0) {
                newclause.setOrganizationSubTypes(clauseDto.getOrganizationSubTypes());
            }
            if (clauseDto.getOrganizationServices() != null && clauseDto.getOrganizationServices().size() != 0) {
                newclause.setOrganizationServices(clauseDto.getOrganizationServices());
            }
            if (clauseDto.getOrganizationSubServices() != null && clauseDto.getOrganizationSubServices().size() != 0) {
                newclause.setOrganizationSubServices(clauseDto.getOrganizationSubServices());
            }
            newclause.setAccountTypes(accountTypes);
            newclause.setTags(tagList);
            newclause = save(newclause);
            jackrabbitService.addClauseNodeToJackrabbit(newclause.getId(), newclause);
            return newclause;
        } catch (NullPointerException e) {
            clauseTagMongoRepository.deleteAll(tagList);
            LOGGER.warn(e.getMessage());
            e.printStackTrace();

        } catch (Exception e) {
            clauseTagMongoRepository.deleteAll(tagList);
            LOGGER.warn(e.getMessage());
            e.printStackTrace();

        }
        return null;
    }

    public Clause getClause(Long countryId, BigInteger id) {

        Clause clause = clauseRepository.findByIdAndNonDeleted(countryId, id);
        if (!Optional.ofNullable(clause).isPresent()) {
            throw new DataNotFoundByIdException("message.clause.data.not.found.for " + id);
        } else
            return clause;
    }


    public Clause updateClause(Long countryId, BigInteger clauseId, ClauseDto clauseDto) throws RepositoryException {

        List<ClauseTag> tagList = new ArrayList<>();
        Clause exists = clauseRepository.findByIdAndNonDeleted(countryId, clauseId);
        if (!Optional.ofNullable(exists).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "message.clause" + clauseId);
        } else if (clauseRepository.findClauseByNameAndCountryId(countryId, clauseDto.getTitle()) != null) {
            exceptionService.duplicateDataException("message.duplicate", "message.clause", clauseDto.getTitle());
        }
        try {

            if (clauseDto.getOrganizationTypes() != null && clauseDto.getOrganizationTypes().size() != 0) {
                exists.setOrganizationTypes(clauseDto.getOrganizationTypes());
            }
            if (clauseDto.getOrganizationSubTypes() != null && clauseDto.getOrganizationSubTypes().size() != 0) {
                exists.setOrganizationSubTypes(clauseDto.getOrganizationSubTypes());
            }
            if (clauseDto.getOrganizationServices() != null && clauseDto.getOrganizationServices().size() != 0) {
                exists.setOrganizationServices(clauseDto.getOrganizationServices());
            }
            if (clauseDto.getOrganizationSubServices() != null && clauseDto.getOrganizationSubServices().size() != 0) {
                exists.setOrganizationSubServices(clauseDto.getOrganizationSubServices());
            }
            List<AccountType> accountTypes = accountTypeService.getAccountTypeList(countryId, clauseDto.getAccountType());
            tagList = clauseTagService.addClauseTagAndGetClauseTagList(clauseDto.getTags());
            exists.setAccountTypes(accountTypes);
            exists.setTitle(clauseDto.getTitle());
            exists.setDescription(clauseDto.getDescription());
            exists.setTags(tagList);
            exists.setTitle(clauseDto.getTitle());
            jackrabbitService.clauseVersioning(clauseId, exists);
            exists = save(exists);
        } catch (NullPointerException e) {
            clauseTagMongoRepository.deleteAll(tagList);
            LOGGER.warn(e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            clauseTagMongoRepository.deleteAll(tagList);
            LOGGER.warn(e.getMessage());
            e.printStackTrace();

        }
        return exists;

    }


    public List<Clause> getClauseList(Long countryId, Set<BigInteger> clausesId) {

        return clauseRepository.getClauseListByIds(countryId, clausesId);
    }


    public List<Clause> getAllClauses() {

        return clauseRepository.findAllClause(UserContext.getCountryId());

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



/*  public List<Clause> getClauseByAccountType(String accountType) {
        List<Clause> clauses = clauseRepository.getClauseByAccountType(accountType);
        if (clauses != null)
            return clauses;
        else
            throw new DataNotExists("Clauses for AccountTYpe : " + accountType + "  Not Exists");
    }*/






/*

    public List<Clause> getClause(ClauseGetQueryDto clauseQueryDto) {
        Query query = new Query();
        String whereQuery = null;
        List<Clause> clauses;
        List<Long> organizationTypes, organizationSubTypes, organizationServices, organizationSubServices;
        organizationTypes = clauseQueryDto.getOrganizationTypes();
        organizationSubTypes = clauseQueryDto.getOrganizationSubTypes();
        organizationServices = clauseQueryDto.getOrganizationServices();
        organizationSubServices = clauseQueryDto.getOrganizationSubServices();
        Criteria criteria = new Criteria();
        if (!Optional.ofNullable(clauseQueryDto).isPresent()) {
            return null;
        } else {

            if (clauseQueryDto.getAccountTypes() != null) {
                whereQuery = "accountTypes._id";
                query.addCriteria((Criteria.where(whereQuery).in(clauseQueryDto.getAccountTypes())));
            }
            if (organizationServices != null) {
                whereQuery = "organizationServices";
                query.addCriteria((Criteria.where(whereQuery).in(organizationServices)));

            }
            if (organizationTypes != null) {
                whereQuery = "organizationTypes";
                query.addCriteria((Criteria.where(whereQuery).in(organizationTypes)));

            }
 if (organizationSubServices!=null) {
                whereQuery = "organizationServices";
                query.addCriteria(Criteria.where(whereQuery).in(organizationSubServices));

            }

            if (clauseQueryDto.getTags() != null) {
                whereQuery = "tags";
                query.addCriteria(Criteria.where(whereQuery).in(clauseQueryDto.getTags()));
            }
            clauses = mongoTemplate.find(query, Clause.class);
            if (clauses.size() != 0) {
                return clauses;
            }
            throw new DataNotExists("clause not exists");

        }

    }
*/


}

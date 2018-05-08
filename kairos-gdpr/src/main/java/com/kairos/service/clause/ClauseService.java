package com.kairos.service.clause;

import com.kairos.ExceptionHandler.DataNotFoundByIdException;
import com.kairos.ExceptionHandler.NotExists;

import com.kairos.ExceptionHandler.RequestDataNull;
import com.kairos.persistance.model.clause.AccountType;
import com.kairos.persistance.model.clause.Clause;
import com.kairos.persistance.model.clause.dto.ClauseDto;
import com.kairos.persistance.model.clause.dto.ClauseGetQueryDto;
import com.kairos.persistance.model.organization.OrganizationService;
import com.kairos.persistance.model.organization.OrganizationType;
import com.kairos.persistance.repository.clause.ClauseMongoRepository;
import com.kairos.service.MongoBaseService;
import com.kairos.service.organization.OrganizationServiceService;
import com.kairos.service.organization.OrganizationTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;

@Service
public class ClauseService extends MongoBaseService {

    @Autowired
    private ClauseMongoRepository clauseRepository;

    @Inject
    private OrganizationServiceService organizationServiceService;

    @Inject
    private OrganizationTypeService organizationTypeService;

    @Inject
    private AccountTypeService accountTypeService;
    @Inject
    private MongoTemplate mongoTemplate;

    public Clause createClause(ClauseDto clauseDto) {

        List<Long> orgServiceIds,orgTypeIds,accountTypeIds;
        orgServiceIds=clauseDto.getOrganizationServiceIds();
        orgTypeIds=clauseDto.getOrganizationTypeIds();
        accountTypeIds=clauseDto.getAccountType();
        List<AccountType> accountTypeList;
        List<OrganizationService> organizationServiceList=new ArrayList<>();
        List<OrganizationType> organizationTypeList=new ArrayList<>();
        if (!Optional.ofNullable(clauseDto).isPresent()) {
            throw new RequestDataNull("No Data Entered");
        } else {
            List<String> tags = new ArrayList<>();
            for (String tag : clauseDto.getTags()) {
                tags.add(tag);
            }
            if (accountTypeIds.size() < 0) {
                throw new RequestDataNull("Account type cannot be  empty or null");
            } else {
               accountTypeList = accountTypeService.getAccountList(accountTypeIds);
                if (!Optional.ofNullable(accountTypeList).isPresent()) {
                    throw new NotExists("Acoount type for Clause not Exist");
                }
            }
            if (orgServiceIds.size()>0)
            {
               organizationServiceList = organizationServiceService.getOrganizationServiceList(orgServiceIds);

            }
            if (orgTypeIds.size()>0)
            {
                organizationTypeList = organizationTypeService.getOrganizationTypeByList(clauseDto.getOrganizationTypeIds());

            }

            Clause clause=new Clause(clauseDto.getTitle(),clauseDto.getDescription(), tags,accountTypeList,organizationServiceList
                    ,organizationTypeList);

            return save(clause);
        }


    }


    public Map<String, Object> getClauseByOrganizationType(String orgTypeName) {

        Map<String, Object> result = new HashMap<>();
        Boolean isSuccess = false;
        List<Clause> clauses = clauseRepository.getClauseByOrganizationType(orgTypeName);

        if (clauses != null) {
            isSuccess = true;
            result.put("isSuccess", isSuccess);
            result.put("data", clauses);
            return result;
        } else
            throw new NotExists("Clauses not Exist for organizationType" + orgTypeName);
    }


    public Clause getClauseById(Long id) {
        Clause clause = (Clause) clauseRepository.findByid(id.toString());

        if (!Optional.ofNullable(clause).isPresent()) {
            throw new NotExists("clause Data Not Exist for given id" + id);

        } else
            return clause;


    }


    public List<Clause> getClauseByAccountType(String accountType) {
        List<Clause> clauses = clauseRepository.getClauseByAccountType(accountType);
        if (clauses != null)
            return clauses;
        else
            throw new NotExists("Clauses for AccountTYpe : " + accountType + "  Not Exists");
    }


    public Clause updateClause(Long clauseid, String description) {
        Clause clause = clauseRepository.findByid(clauseid.toString());
        if (!Optional.ofNullable(clause).isPresent()) {
            throw new NotExists("clause for given id " + clauseid + " not exist");
        }
        clause.setDescription(description);
        save(clause);
        return clause;
    }


    public List<Clause> getClause(ClauseGetQueryDto clauseQueryDto) {
        Query query = new Query();
        String whereQuery = null;
        List<String> value = null;
        List<Clause> clauses;
        Criteria criteria = new Criteria();
        if (!Optional.ofNullable(clauseQueryDto).isPresent()) {
            return null;
        } else {

            if (clauseQueryDto.getAccountTypes() != null) {
                whereQuery = "accountTypeList.typeOfAccount";
                value = clauseQueryDto.getAccountTypes();

                query.addCriteria((Criteria.where(whereQuery).in(value)));
            }
            if (clauseQueryDto.getOrganizationServices() != null) {
                whereQuery = "organizationServiceList.name";
                value = clauseQueryDto.getOrganizationServices();
                query.addCriteria((Criteria.where(whereQuery).in(value)));
            }
            if (clauseQueryDto.getOrganizationTypes() != null) {
                whereQuery = "organizationTypeList.name";
                value = clauseQueryDto.getOrganizationTypes();
                query.addCriteria((Criteria.where(whereQuery).in(value)));
            }
            if (clauseQueryDto.getOrganizationSubServices() != null) {
                whereQuery = "organizationServiceList.organizationSubService.name ";
                value = clauseQueryDto.getOrganizationSubServices();
                query.addCriteria(Criteria.where(whereQuery).in(value));
            }
            if (clauseQueryDto.getTags() != null) {
                whereQuery = "tags";
                value = clauseQueryDto.getTags();
                query.addCriteria(Criteria.where(whereQuery).in(value));
            }
            clauses = mongoTemplate.find(query, Clause.class);
            if (clauses.size() <= 0) {
                throw new NotExists("clause not exists");
            }
            return clauses;
        }

    }


    public List<Clause> getClausesByIds(List<BigInteger> clausesId) {

        List<Clause> clauses = new ArrayList<>();
        if (clausesId.size() > 0) {
            for (BigInteger id : clausesId) {

                Clause clause = clauseRepository.findByid(id.toString());
                if (clause != null) {
                    clauses.add(clause);
                } else {
                    throw new DataNotFoundByIdException("Clause for given id :" + id + " not exist");
                }
            }
            return clauses;
        } else
            throw new RequestDataNull("Requested Clauseid are Not null or empty");


    }


    public List<Clause> getAllClauses() {

        List<Clause> clauses = clauseRepository.findAll();
        if (clauses == null) {
            throw new NotExists("CLauses not Exist ");
        }
        return clauses;

    }


    public Boolean deleteClause(Long clauseId) {

        Clause clause = clauseRepository.findByid(clauseId.toString());
        if (Optional.ofNullable(clause).isPresent()) {
            clauseRepository.delete(clause);
            return true;
        } else
            throw new DataNotFoundByIdException("Data not found for given id " + clauseId);
    }


    public Page<Clause> getClausePagination(int page, int size) {
        return clauseRepository.findAll(new PageRequest(page, size));
    }


}

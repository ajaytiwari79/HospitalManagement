package com.kairos.service.clause;

import com.kairos.custome_exception.DataNotFoundByIdException;
import com.kairos.custome_exception.DuplicateDataException;
import com.kairos.custome_exception.DataNotExists;

import com.kairos.custome_exception.RequestDataNull;
import com.kairos.persistance.model.clause.AccountType;
import com.kairos.persistance.model.clause.Clause;
import com.kairos.persistance.model.clause.dto.ClauseDto;
import com.kairos.persistance.model.clause.dto.ClauseGetQueryDto;
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

        List<Long> orgServiceIds, orgTypeIds, orgSubServiceIds, orgSubTypeIds;
        orgServiceIds = clauseDto.getOrganizationServiceIds();
        orgTypeIds = clauseDto.getOrganizationTypeIds();
       List<BigInteger> accountTypeIds = clauseDto.getAccountType();
        orgSubServiceIds = clauseDto.getOrganizationServiceIds();
        orgSubTypeIds = clauseDto.getOrganizationSubTypeIds();
        List<AccountType> accountTypes;
        List<String> tags =new ArrayList<>();
       /* List<OrganizationService> organizationServices = new ArrayList<>();
        List<OrganizationType> organizationTypes = new ArrayList<>();
        List<OrganizationService> organizationSubServices = new ArrayList<>();
        List<OrganizationType> organizationSubTypes = new ArrayList<>();*/
        Clause clause = new Clause();
        if (clauseRepository.findByTitle(clauseDto.getTitle())!=null)
        {

            throw new DuplicateDataException("clause with name title "+clauseDto.getTitle()+" already Exist");
        }

        if (Optional.ofNullable(clauseDto.getTags()).isPresent())
        {
            tags = new ArrayList<>();
            for (String tag : clauseDto.getTags()) {
                tags.add(tag);
            }   }
            System.err.println("+++++++");

            if (accountTypeIds!=null&&!accountTypeIds.isEmpty()) {
                accountTypes = accountTypeService.getAccountList(accountTypeIds);
            } else {
                throw new RequestDataNull("Accounttype list cannot be  empty");
            }

            clause.setOrganizationServices(orgServiceIds);
            clause.setOrganizationSubServices(orgSubServiceIds);
            clause.setOrganizationTypes(orgTypeIds);
            clause.setOrganizationSubTypes(orgSubTypeIds);

            clause.setAccountTypes(accountTypes);
            clause.setTitle(clauseDto.getTitle());
            clause.setDescription(clauseDto.getDescription());
            clause.setTags(tags);
            return save(clause);



    }


    /*public Map<String, Object> getClauseByOrganizationType(String orgTypeName) {

        Map<String, Object> result = new HashMap<>();
        Boolean isSuccess = false;
        List<Clause> clauses = clauseRepository.getClauseByOrganizationType(orgTypeName);

        if (clauses != null) {
            isSuccess = true;
            result.put("isSuccess", isSuccess);
            result.put("data", clauses);
            return result;
        } else
            throw new DataNotExists("Clauses not Exist for organizationType" + orgTypeName);
    }
*/

    public Clause getClauseById(BigInteger id) {
        Clause clause = (Clause) clauseRepository.findByid(id);

        if (!Optional.ofNullable(clause).isPresent()) {
            throw new DataNotExists("clause Data Not Exist for given id" + id);

        } else
            return clause;
    }


    public List<Clause> getClauseByAccountType(String accountType) {
        List<Clause> clauses = clauseRepository.getClauseByAccountType(accountType);
        if (clauses != null)
            return clauses;
        else
            throw new DataNotExists("Clauses for AccountTYpe : " + accountType + "  Not Exists");
    }


    public Clause updateClause(BigInteger clauseid, ClauseDto clauseDto) {
        Clause exists = clauseRepository.findByid(clauseid);
        if (!Optional.ofNullable(exists).isPresent()) {
            throw new DataNotExists("clause for given id " + clauseid + " not exist");
        } else {
            List<Long> orgServiceIds, orgTypeIds, orgSubServiceIds, orgSubTypeIds;
            orgServiceIds = clauseDto.getOrganizationServiceIds();
            orgTypeIds = clauseDto.getOrganizationTypeIds();
            List<BigInteger>accountTypeIds = clauseDto.getAccountType();
            orgSubServiceIds = clauseDto.getOrganizationServiceIds();
            orgSubTypeIds = clauseDto.getOrganizationSubTypeIds();
            List<AccountType> accountTypes = new ArrayList<>();
            List<String> tags;
            if (!Optional.ofNullable(clauseDto).isPresent()) {
                throw new RequestDataNull("No Data Entered");
            } else {
                tags = new ArrayList<>();
                for (String tag : clauseDto.getTags()) {
                    tags.add(tag);
                }
                if (accountTypeIds!=null&&!accountTypeIds.isEmpty()) {
                    accountTypes = accountTypeService.getAccountList(accountTypeIds);
                } else {
                    throw new RequestDataNull("Accounttype list cannot be  empty");
                }
                exists.setOrganizationServices(orgServiceIds);
                exists.setOrganizationSubServices(orgSubServiceIds);
                exists.setOrganizationTypes(orgTypeIds);
                exists.setOrganizationSubTypes(orgSubTypeIds);

            }
            exists.setAccountTypes(accountTypes);
            exists.setDescription(clauseDto.getDescription());
            exists.setTags(tags);
            exists.setTitle(clauseDto.getTitle());
        }

        return save(exists);
    }


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

            if (clauseQueryDto.getAccountTypes()!=null) {
                whereQuery = "accountTypes._id";
                query.addCriteria((Criteria.where(whereQuery).in(clauseQueryDto.getAccountTypes())));
            }
            if (organizationServices!=null) {
                whereQuery = "organizationServices";
                query.addCriteria((Criteria.where(whereQuery).in(organizationServices)));

            }
            if (organizationTypes!=null) {
                whereQuery = "organizationTypes";
                query.addCriteria((Criteria.where(whereQuery).in(organizationTypes)));

            }
           /* if (organizationSubServices!=null) {
                whereQuery = "organizationServices";
                query.addCriteria(Criteria.where(whereQuery).in(organizationSubServices));

            }*/
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


    public List<Clause> getClausesByIds(List<BigInteger> clausesId) {

        List<Clause> clauses = new ArrayList<>();
        if (clausesId.size() != 0) {
            for (BigInteger id : clausesId) {

                Clause clause = clauseRepository.findByid(id);
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
            throw new DataNotExists("CLauses not Exist ");
        }
        return clauses;

    }


    public Boolean deleteClause(BigInteger clauseId) {

        Clause clause = clauseRepository.findByid(clauseId);
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
